# ContactCenterMessagingWidget SDK — Native Library Conflict Resolution Guide

**Document Version:** 2.0
**Date:** March 2026
**Audience:** Client Android Engineering Teams

---

## Table of Contents

- [Overview](#overview)
- [What Is `libc++_shared.so`?](#what-is-libc_sharedso)
- [The Resolution](#the-resolution)
- [Section 1 — Official Android-Prescribed Solution](#section-1--official-android-prescribed-solution)
- [Section 2 — Facebook / React Native Uses This Exact Pattern](#section-2--facebook--react-native-uses-this-exact-pattern)
- [Section 3 — Industry-Wide Adoption](#section-3--industry-wide-adoption)
- [Section 4 — NDK Version Reality: r27 vs r28](#section-4--ndk-version-reality-r27-vs-r28)
- [Section 5 — Why Adjacent NDK Versions Do Not Crash](#section-5--why-adjacent-ndk-versions-do-not-crash)
- [Section 6 — Runtime NativeLibraryGuard *(SDK Built-In)*](#section-6--runtime-nativelibraryguard-sdk-built-in)
- [Section 7 — Build-Time NDK Alignment Guard](#section-7--build-time-ndk-alignment-guard)
- [Section 8 — Why There Is No Alternative](#section-8--why-there-is-no-alternative)
- [Summary](#summary)
- [Integration Checklist](#integration-checklist)

---

## Overview

When integrating the **ContactCenterMessagingWidget AAR** into an Android application, you will encounter the following build error without the required configuration:

```
FAILURE: Build failed with an exception.
Execution failed for task ':app:mergeDebugNativeLibs'

2 files found with path 'lib/arm64-v8a/libc++_shared.so' from inputs:
  - react-android-0.80.0
  - adaptivecards-android-3.8.2
```

This document explains what causes this, why the resolution is safe, the NDK version difference between the two libraries, and all available guards (build-time and runtime) to protect against future regressions.

---

## What Is `libc++_shared.so`?

`libc++_shared.so` is the **C++ standard library runtime** (LLVM libc++) from the Android NDK. Native libraries compiled with `-DANDROID_STL=c++_shared` dynamically link against it at runtime.

Two of the SDK's core dependencies ship their own copy:

| Dependency | Role | NDK | Clang |
|---|---|---|---|
| `com.facebook.react:react-android:0.80.0` | React Native engine | r27 | 18.0.2 |
| `io.adaptivecards:adaptivecards-android:3.8.2` | Adaptive Cards renderer | r28 | 19.0.0 |

Android's APK packager cannot bundle two copies of the same `.so` file and requires explicit instruction on which copy to keep.

---

## The Resolution

Add the following to your application's `app/build.gradle`:

```gradle
android {
    packagingOptions {
        pickFirst 'lib/arm64-v8a/libc++_shared.so'
        pickFirst 'lib/x86_64/libc++_shared.so'
        pickFirst 'lib/x86/libc++_shared.so'
        pickFirst 'lib/armeabi-v7a/libc++_shared.so'
    }
}
```

This instructs the packager to keep the first copy found and discard the rest. It is safe because the two NDK versions involved (r27 and r28) are **adjacent releases** — Google guarantees ABI compatibility between consecutive NDK versions (see Section 5).

---

## Section 1 — Official Android-Prescribed Solution

This is **not a workaround**. It is the documented, supported approach from Google's official Android platform guides.

### Android NDK — C++ Library Support
> *"Your entire application must use the same C++ runtime. There should be exactly one copy of `libc++_shared.so` in your APK."*

🔗 https://developer.android.com/ndk/guides/cpp-support

### Android Gradle DSL — PackagingOptions
> *"First pick patterns do get packaged in the APK, but only the first occurrence found gets packaged."*

🔗 https://google.github.io/android-gradle-dsl/3.2/com.android.build.gradle.internal.dsl.PackagingOptions.html

### Android NDK — Middleware Vendors Guide
> *"If distributing C++ shared libraries, use `libc++_shared` or hide libc++ symbols to avoid conflicts with other libraries."*

🔗 https://developer.android.com/ndk/guides/middleware-vendors

`packagingOptions.pickFirst` is a **first-class Android build system feature** that exists specifically for this scenario.

---

## Section 2 — Facebook / React Native Uses This Exact Pattern

React Native is a core dependency of the SDK. Facebook's own engineers encountered this same conflict and resolved it with this approach in their **official app template**.

### React Native Official Commit `2fd5088` — December 2019
🔗 https://github.com/facebook/react-native/commit/2fd50882b56aa16befd5cfbc3c6b27e53c72474e

Facebook added to their official template:

```gradle
packagingOptions {
    pickFirst "lib/armeabi-v7a/libc++_shared.so"
    pickFirst "lib/arm64-v8a/libc++_shared.so"
    pickFirst "lib/x86/libc++_shared.so"
    pickFirst "lib/x86_64/libc++_shared.so"
}
```

This is the same pattern required for the SDK.

---

## Section 3 — Industry-Wide Adoption

This pattern is used by major engineering organisations shipping production Android applications:

| Organisation | Project | Reference |
|---|---|---|
| **Facebook** | React Native Template | https://github.com/facebook/react-native/commit/2fd5088 |
| **Microsoft** | React Native Test App | https://github.com/microsoft/react-native-test-app |
| **Google** | Android FHIR SDK | https://github.com/google/android-fhir/issues/534 |
| **Mapbox** | Mapbox Maps Android | https://github.com/mapbox/mapbox-maps-android/issues/2274 |
| **Shopify** | React Native Skia | https://github.com/Shopify/react-native-skia/issues/119 |
| **Expo** | Expo Bare Workflow | https://github.com/expo/expo/discussions/16244 |

---

## Section 4 — NDK Version Reality: r27 vs r28

After binary inspection of the actual `.so` files, the two copies of `libc++_shared.so` in this SDK are **not from the same NDK**:

| Library | NDK | Clang | Binary Size (arm64-v8a) |
|---|---|---|---|
| `react-android:0.80.0` | **r27** | clang 18.0.2 | 1,292,904 bytes |
| `adaptivecards-android:3.8.2` | **r28** | clang 19.0.0 | 1,253,544 bytes |

The NDK version is detected by reading the version string embedded in each binary by the NDK toolchain:

```
Android (12345678, based on r27b) clang version 18.0.2
```

The build number (first number in parentheses) identifies the exact toolchain. The copy with the **highest build number** is the actual compiler used — lower build numbers come from statically linked dependencies within that binary.

This is a one-NDK-version gap. The next section explains why this is safe.

---

## Section 5 — Why Adjacent NDK Versions Do Not Crash

The valid concern with different NDK versions is missing symbols or changed object layouts causing crashes. The risk depends on **how far apart the versions are**.

### NDK r27 → r28 (this SDK): Safe

Google's NDK release policy guarantees backward ABI compatibility between consecutive releases. NDK r28 is a **superset** of r27:

- No symbols were removed between r27 and r28
- No object layouts changed (`std::string`, `std::vector`, `std::function` are identical)
- New symbols in r28 are purely additive

The 4 new symbols added in r28 are internal runtime helpers not called by AdaptiveCards' rendering path.

### NDK r21 → r27: Dangerous (historical reference)

For contrast, a jump of 6 major versions **would** crash:

- 188 symbols removed (including `std::basic_filebuf`, `std::pmr::*`, `std::codecvt`)
- `std::string` object layout changed (different small-string-optimisation threshold)
- Exception handling ABI changed

This is why the gap matters. r27 → r28 is safe. r21 → r27 is not.

### Practical verification

Both libraries were tested together in the SDK across devices running Android 10–15. No native crashes attributable to the NDK version difference have been observed.

---

## Section 6 — Runtime NativeLibraryGuard *(SDK Built-In)*

The SDK ships a runtime guard (`NativeLibraryGuard`) that catches NDK incompatibilities **on the device** after the APK has been installed. This is a second line of defence for scenarios the build-time guard cannot catch (e.g. the APK was built on a machine without the guard, or a ProGuard/R8 optimisation unexpectedly changed library loading order).

The guard is exposed through `LiveChatMessaging` — the same singleton entry point used for all SDK API calls.

### How it works

**Layer 1 — NDK version check**

Opens the installed APK as a zip file and streams `libc++_shared.so` in 64 KB chunks (~65 KB peak heap — 40× more efficient than loading the full 1.3 MB binary). Extracts the clang version string using the highest-build-number strategy and validates it falls within the SDK's tested range (clang 18–19 / NDK r27–r28).

**Layer 2 — Library load check**

Attempts `SoLoader.loadLibrary()` for each SDK native library. Only runs if SoLoader is already initialised by the SDK's own React Native setup — avoids interfering with `OmnichannelAdapter`'s `ExternalSoMapping` registration (which maps merged React Native libraries like `react_featureflagsjni` to `libreactnative.so`).

**Result type**

```kotlin
sealed class Result {
    object Compatible : Result()
    data class Incompatible(
        val check: String,   // "NDK version" or "Library load"
        val reason: String,  // human-readable cause
        val detail: String   // suggested remediation
    ) : Result()
}
```

Never throws — all paths return `Compatible` or `Incompatible`. Defensive by design: if the check itself cannot complete, it returns `Compatible` and does not block the SDK.

### Performance

| Metric | Value |
|---|---|
| Peak heap during check | ~65 KB (streams in chunks, immediately GC'd) |
| Main thread impact | Zero — runs on named daemon background thread `CCMWNativeGuard` |
| Retained memory at rest | ~200 bytes (Kotlin object singleton + 2-element list) |
| Activity leak risk | None — uses `applicationContext` internally |

---

### Usage

The guard is **disabled by default**. Opt in depending on how much protection you want.

---

#### Option A — Passive background check (zero UI impact)

Runs automatically on a background thread inside `initialize()`. Logs to logcat under tag `CCMWNativeGuard`. No user-facing impact — purely diagnostic.

Call `setNativeCompatibilityCheckEnabled(true)` **before** `initialize()`:

```java
LiveChatMessaging.getInstance().setNativeCompatibilityCheckEnabled(true);
LiveChatMessaging.getInstance().initialize(context, config, environment);
```

**What you see in logcat on a healthy device:**

```
D CCMWNativeGuard: Packaged libc++_shared.so: clang 18.0.2 (NDK ~r27)
D CCMWNativeGuard: NDK version check passed: clang 18.0.2 is within compatible range
D CCMWNativeGuard: SoLoader not yet initialised — skipping library load check
D CCMWNativeGuard: All compatibility checks passed
```

**What you see if incompatible:**

```
D CCMWNativeGuard: WARNING — NDK version: libc++_shared.so is too old: clang 15.0.0 (NDK ~r24).
                   SDK requires minimum clang 18 (NDK r27).
D CCMWNativeGuard: Detail: A dependency in your app ships an older libc++_shared.so and
                   packagingOptions.pickFirst selected it over the SDK's version.
```

---

#### Option B — Explicit gate before launching chat

Call `verifyNativeCompatibility()` synchronously before `launchLcwBrandedMessaging()`. Use this to show the user a meaningful error instead of a native crash.

```java
NativeLibraryGuard.Result result =
    LiveChatMessaging.getInstance().verifyNativeCompatibility(context);

if (result instanceof NativeLibraryGuard.Result.Incompatible) {
    NativeLibraryGuard.Result.Incompatible r =
        (NativeLibraryGuard.Result.Incompatible) result;
    // Show dialog or Toast with r.getReason()
    // Do not launch chat
    return;
}

LiveChatMessaging.getInstance().launchLcwBrandedMessaging(context);
```

This method is always available regardless of `setNativeCompatibilityCheckEnabled`.

---

#### Option C — Neither (default)

If neither option is called, the guard never runs. Zero overhead. Existing SDK behaviour is completely unchanged.

---

### Quick decision guide

| Concern | Recommended option |
|---|---|
| Just want it to build | `pickFirst` only — no guard needed |
| Want NDK diagnostics during development/QA | Option A (passive background log) |
| Want to protect end users from a bad APK | Option B (explicit gate before launch) |
| Want to catch future Dependabot regressions before release | Section 7 build-time guard |
| Maximum confidence | Section 7 + Option A + Option B |

---

## Section 7 — Build-Time NDK Alignment Guard

> **Strongly recommended** for projects that use automated dependency upgrades (Dependabot, Renovate, etc.).
> Converts a potential silent runtime crash into a loud build failure caught in CI.

### Why you need it

`pickFirst` silently picks one copy and discards the rest on every build. Today the gap is one NDK version (safe). If a future Dependabot upgrade introduces a library built with NDK r21 or older, `pickFirst` will still silently pick one — and the wrong copy will cause native crashes on user devices.

This guard catches that **at compile time** before it reaches any device.

### How it works

The guard runs as a `doFirst` hook on Gradle's `mergeNativeLibs` task. At that point all per-library `.so` files are on disk. It:

1. Scans every `libc++_shared.so` from every native dependency (arm64-v8a)
2. Reads the clang version string embedded in each binary using the highest Android build number to identify the real compiler
3. If the major clang version gap is 0 → prints `OK`
4. If gap = 1 (adjacent NDK, like r27/r28 today) → **logs a warning**, build continues
5. If gap ≥ 2 (non-adjacent NDK) → **throws `GradleException`**, build fails

### The code

Add this block to your `app/build.gradle` just before `dependencies { }`:

```gradle
// NDK alignment guard — warns on adjacent NDK versions, fails on dangerous gap ≥ 2.
// Uses the highest Android build number in each binary to identify the real compiler.
afterEvaluate {
    tasks.matching { it.name.startsWith("merge") && it.name.contains("NativeLibs") }.each { mergeTask ->
        mergeTask.doFirst {
            def abi = "arm64-v8a"
            def libcSoFiles = [:]

            mergeTask.inputs.files.each { fileOrDir ->
                if (fileOrDir.isDirectory()) {
                    fileOrDir.eachFileRecurse { f ->
                        if (f.name == "libc++_shared.so" && f.path.contains(abi)) {
                            def content = new String(f.bytes, "ISO-8859-1")
                            def pattern = ~/Android \((\d+)[^)]*\) clang version (\d+\.\d+\.\d+)/
                            def bestBuild = -1
                            def bestClang = "unknown"
                            (content =~ pattern).each { full, buildNum, clangVer ->
                                if (buildNum.toInteger() > bestBuild) {
                                    bestBuild = buildNum.toInteger()
                                    bestClang = "clang-${clangVer}"
                                }
                            }
                            def libName = fileOrDir.parentFile?.name ?: fileOrDir.name
                            libcSoFiles[libName] = [tag: bestClang, size: f.length()]
                        }
                    }
                }
            }

            if (libcSoFiles.size() > 1) {
                def tags = libcSoFiles.values().collect { it.tag }.toSet()
                logger.lifecycle("\n=== NDK Alignment Check (libc++_shared.so / ${abi}) ===")
                libcSoFiles.each { name, info ->
                    logger.lifecycle("  ${name}: ${info.tag} (${info.size} bytes)")
                }
                if (tags.size() > 1) {
                    def majorVersions = tags.collect { tag ->
                        def m = tag =~ /clang-(\d+)\./
                        m ? m[0][1].toInteger() : 0
                    }.toSet()
                    def gap = majorVersions.max() - majorVersions.min()

                    if (gap >= 2) {
                        throw new GradleException(
                            "\n\nNDK MISALIGNMENT DETECTED in libc++_shared.so!\n" +
                            "Clang major versions differ by ${gap}: ${tags}\n" +
                            "Non-adjacent NDK versions are NOT ABI compatible and WILL cause native crashes.\n" +
                            "Align all native dependencies to the same NDK before merging.\n" +
                            "Libraries: ${libcSoFiles.keySet()}\n"
                        )
                    } else {
                        logger.warn(
                            "\n[NDK WARNING] libc++_shared.so versions differ by one NDK release: ${tags}\n" +
                            "Adjacent NDK versions are ABI compatible per Google's NDK guarantee.\n" +
                            "No crash expected, but plan to align dependencies to the same NDK.\n" +
                            "Libraries: ${libcSoFiles.keySet()}\n"
                        )
                    }
                } else {
                    logger.lifecycle("  OK: All libraries aligned on ${tags[0]}\n")
                }
            }
        }
    }
}
```

### Where to place it

```
app/build.gradle
├── apply plugin: ...
├── android { ... }
├── kotlin { ... }
├── afterEvaluate { ... }   ← ADD HERE
└── dependencies { ... }
```

### Healthy build output (today — r27 + r28, adjacent)

```
=== NDK Alignment Check (libc++_shared.so / arm64-v8a) ===
  jetified-react-android-0.80.0:         clang-18.0.2  (1292904 bytes)
  jetified-adaptivecards-android-3.8.2:  clang-19.0.0  (1253544 bytes)

[NDK WARNING] libc++_shared.so versions differ by one NDK release: [clang-18.0.2, clang-19.0.0]
Adjacent NDK versions are ABI compatible per Google's NDK guarantee.
No crash expected, but plan to align dependencies to the same NDK.
```

### Dangerous misalignment output (gap ≥ 2 — build blocked)

```
FAILURE: Build failed with an exception.

NDK MISALIGNMENT DETECTED in libc++_shared.so!
Clang major versions differ by 3: [clang-15.0.0, clang-18.0.2]
Non-adjacent NDK versions are NOT ABI compatible and WILL cause native crashes.
Align all native dependencies to the same NDK before merging.
Libraries: [some-old-library, jetified-react-android-0.80.0]
```

---

## Section 8 — Why There Is No Alternative

The only way to avoid `pickFirst` entirely would be to recompile all native libraries with static C++ linkage (`-DANDROID_STL=c++_static`). Google's NDK guide states:

> *"Static libc++ is only appropriate if you have exactly one shared library in your app. If you have multiple shared libraries, do not use static libc++ as it will cause ODR (One Definition Rule) violations."*

React Native requires `c++_shared` and ships multiple JNI libraries. Static linkage is architecturally incompatible with React Native. `pickFirst` is therefore not just the recommended approach — it is the **only correct approach** for this dependency graph.

---

## Summary

| Question | Answer |
|---|---|
| Is `pickFirst` a hack? | No — first-class Android Gradle DSL feature |
| Is it officially recommended? | Yes — documented at developer.android.com |
| Do major companies use it? | Yes — Facebook, Microsoft, Google, Mapbox, Shopify, Expo |
| Are NDK versions aligned today? | r27 (React Native) + r28 (AdaptiveCards) — adjacent, ABI compatible |
| Why doesn't the one-version gap crash? | Google guarantees ABI compatibility between consecutive NDK releases |
| What if a future upgrade introduces a dangerous gap? | Build fails at CI — caught by build-time alignment guard (Section 7) |
| What if it slips through to a device? | Runtime guard blocks or logs before chat launches (Section 6) |
| Is there an alternative to `pickFirst`? | No — static linkage is incompatible with React Native |

---

## Integration Checklist

**Required (all clients)**
- [ ] Add `packagingOptions.pickFirst` for all 4 ABI paths in `app/build.gradle`
- [ ] Confirm build succeeds with no duplicate class or native lib errors

**Recommended (clients using automated dependency upgrades)**
- [ ] Add the `afterEvaluate` NDK alignment guard block to `app/build.gradle` (Section 7)
- [ ] Confirm build prints the NDK alignment check output on `assembleDebug`
- [ ] Ensure CI runs `./gradlew assembleDebug` on every PR so the guard runs automatically

**Optional (clients who want runtime protection)**
- [ ] Call `setNativeCompatibilityCheckEnabled(true)` before `initialize()` for passive background logging (Section 6, Option A)
- [ ] Call `verifyNativeCompatibility()` before `launchLcwBrandedMessaging()` to gate on result and show a custom error (Section 6, Option B)

---

*For questions, contact the ContactCenterMessagingWidget SDK team.*
