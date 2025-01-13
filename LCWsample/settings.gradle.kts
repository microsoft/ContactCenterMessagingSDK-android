import java.net.URI
import org.gradle.api.provider.Provider

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
        maven(url = "$rootDir/node_modules")
        maven(url = "$rootDir/node_modules/react-native/android")
        maven(url = "$rootDir/node_modules/jsc-android/dist")
        maven(url = "https://maven.google.com")
        google()
    }
}

rootProject.name = "LCWSamplApp1"
include(":app")


include(":randombytes")
project(":randombytes").projectDir = file("./node_modules/react-native-randombytes/android")

include(":randomvalues")
project(":randomvalues").projectDir = file("./node_modules/react-native-get-random-values/android")

