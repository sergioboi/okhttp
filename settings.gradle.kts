@file:Suppress("UnstableApiUsage")

pluginManagement {
  includeBuild("build-logic")
  repositories {
    mavenCentral()
    gradlePluginPortal()
    google()
  }
}

rootProject.name = "okhttp-parent"

dependencyResolutionManagement {
  repositories {
    mavenCentral()
    google()
  }
}

plugins {
  id("org.gradle.toolchains.foojay-resolver-convention") version("1.0.0")
}

include(":mockwebserver")
project(":mockwebserver").name = "mockwebserver3"
include(":mockwebserver-deprecated")
project(":mockwebserver-deprecated").name = "mockwebserver"
include(":mockwebserver-junit4")
project(":mockwebserver-junit4").name = "mockwebserver3-junit4"
include(":mockwebserver-junit5")
project(":mockwebserver-junit5").name = "mockwebserver3-junit5"

val androidBuild: String by settings
val graalBuild: String by settings
val loomBuild: String by settings

if (androidBuild.toBoolean()) {
  include(":regression-test")
}

if (graalBuild.toBoolean()) {
  include(":native-image-tests")
}

include(":okcurl")
include(":okhttp")
include(":okhttp-bom")
include(":okhttp-brotli")
include(":okhttp-coroutines")
include(":okhttp-dnsoverhttps")
include(":okhttp-hpacktests")
include(":okhttp-idna-mapping-table")
include(":okhttp-java-net-cookiejar")
include(":okhttp-logging-interceptor")
include(":okhttp-osgi-tests")
include(":okhttp-sse")
include(":okhttp-testing-support")
include(":okhttp-tls")
include(":okhttp-urlconnection")
include(":okhttp-zstd")
include(":samples:compare")
include(":samples:crawler")
include(":samples:guide")
include(":samples:simple-client")
include(":samples:slack")
include(":samples:static-server")
include(":samples:tlssurvey")
include(":samples:unixdomainsockets")
include(":container-tests")
val okhttpModuleTests: String by settings
if (okhttpModuleTests.toBoolean()) {
  include(":module-tests")
}

project(":okhttp-logging-interceptor").name = "logging-interceptor"

val androidHome = System.getenv("ANDROID_HOME")
val localProperties = java.util.Properties().apply {
  val file = rootProject.projectDir.resolve("local.properties")
  if (file.exists()) {
    load(file.inputStream())
  }
}
val sdkDir = localProperties.getProperty("sdk.dir")
if (androidHome != null || sdkDir != null) {
  include(":android-test")
  include(":android-test-app")
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
enableFeaturePreview("STABLE_CONFIGURATION_CACHE")

buildCache {
  local {
    // Disable local buildcache to maximize use of BuildFetch remote cache.
    isEnabled = false
  }

  remote<HttpBuildCache> {
    // On CI it's easiest to provide Env Vars
    // On local macOS it's easier to provide ~/.gradle/gradle.properties for consistency between Terminal & IDE
    val remoteUrl: String? = "OKHTTP_GRADLE_REMOTE_CACHE_URL"
      .let { System.getenv(it) ?: providers.gradleProperty(it).orNull }

    val user: String? = "OKHTTP_GRADLE_REMOTE_CACHE_USER"
      .let { System.getenv(it) ?: providers.gradleProperty(it).orNull }

    val token: String? = "OKHTTP_GRADLE_REMOTE_CACHE_TOKEN"
      .let { System.getenv(it) ?: providers.gradleProperty(it).orNull }

    if (remoteUrl != null && user != null && token != null) {
      isEnabled = true

      url = uri(remoteUrl.trim())

      credentials {
        username = user.trim()
        password = token.trim()
      }

      isPush = true
    } else {
      isEnabled = false
    }
  }
}