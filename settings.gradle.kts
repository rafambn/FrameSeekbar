rootProject.name = "FrameBar"
include(":FrameBarComposeApp")
include(":FrameBarXMLApp")
include(":FrameBarCompose")
include(":FrameBarXML")

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
