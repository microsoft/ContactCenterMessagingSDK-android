pluginManagement {
    repositories {
        pluginManagement {
            repositories {
                mavenCentral()
                google {
                    content {
                        includeGroupByRegex("com\\.android.*")
                        includeGroupByRegex("com\\.google.*")
                        includeGroupByRegex("androidx.*")
                    }
                }
                gradlePluginPortal()
            }
        }
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
    repositories {
        mavenCentral()
        google()
        maven { url = uri("https://jitpack.io") }
    }
}

rootProject.name = "LCWsample"

include(":app")
/*
include(":randombytes")
project(":randombytes").projectDir = file("./node_modules/react-native-randombytes/android")

include(":randomvalues")
project(":randomvalues").projectDir = file("./node_modules/react-native-get-random-values/android")

include(":ReactAndroid")
project(":ReactAndroid").projectDir = file("$rootDir/node_modules/react-native/ReactAndroid")
*/

