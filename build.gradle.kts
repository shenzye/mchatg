plugins {
    kotlin("jvm") version "1.5.20"
}

group = "com.github"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven {
        name = "spigotmc-repo"
        url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    }
}

dependencies {
    //kotlin基本库
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    //添加协程库，https://github.com/Kotlin/kotlinx.coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.1")
    compileOnly("org.spigotmc:spigot-api:1.16.5-R0.1-SNAPSHOT")

    //添加反射库
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.5.0")
    //gson解析器，https://github.com/google/gson
    implementation("com.google.code.gson:gson:2.8.7")


    //yaml解析库，https://bitbucket.org/asomov/snakeyaml/wiki/Installation
//    implementation("org.yaml:snakeyaml:1.27")
    //jackson解析器，https://github.com/FasterXML/jackson
//    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.11.4")

    //导入sqlite依赖，https://github.com/xerial/sqlite-jdbc
    implementation("org.xerial:sqlite-jdbc:3.36.0.1")
    //添加TelegramBots依赖，https://github.com/rubenlagus/TelegramBots
    implementation("org.telegram:telegrambots:5.3.0")
    //emoji处理包，https://github.com/vdurmont/emoji-java
    implementation("com.vdurmont:emoji-java:5.1.1")


    //javalin框架，https://github.com/tipsy/javalin
//    implementation("io.javalin:javalin:3.13.8")
    //orm框架exposed，https://github.com/JetBrains/Exposed
    implementation("org.jetbrains.exposed:exposed-core:0.31.1")
    implementation("org.jetbrains.exposed:exposed-dao:0.31.1")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.31.1")
}



kotlin {
    sourceSets.main {
        languageSettings.apiVersion = "1.5"
        languageSettings.languageVersion = "1.5"
        kotlin.srcDir("src/main")
        resources.srcDir("src/resources")
    }
    sourceSets.test {
        languageSettings.apiVersion = "1.5"
        languageSettings.languageVersion = "1.5"
        kotlin.srcDir("src/test")
    }


}

