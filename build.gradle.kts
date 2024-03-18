val mod_id: String by extra
val mod_version: String by extra
val mc_version: String by extra
val sgui_version: String by extra
val polymer_version: String by extra

plugins {
    id("com.possible-triangle.gradle") version ("0.1.5")
}

withKotlin()

repositories {
    localMaven(project)

    maven {
        url = uri("https://maven.nucleoid.xyz")
        content {
            includeGroup("eu.pb4")
            includeGroup("xyz.nucleoid")
        }
    }
}

fabric {
    dataGen()

    includesMod("eu.pb4:sgui:${sgui_version}")
    includesMod("eu.pb4:polymer-core:${polymer_version}")
}

enablePublishing {
    githubPackages()
}

uploadToCurseforge()
uploadToModrinth {

}