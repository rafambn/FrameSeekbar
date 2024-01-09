rootProject.name = "Template-library-with-App"
include(":composeApp")
include(":FrameSeekBar")
includeBuild("convention-plugins")

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
