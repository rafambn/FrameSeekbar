rootProject.name = "Template-library-with-App"
include(":composeApp")
include(":FrameSeekBar")

pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}
