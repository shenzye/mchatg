import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.10"
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
    testImplementation(kotlin("test"))
    compileOnly("org.spigotmc:spigot-api:1.19-R0.1-SNAPSHOT")

    //gson解析器，https://github.com/google/gson
    implementation("com.google.code.gson:gson:2.9.0")
    //添加TelegramBots依赖，https://github.com/rubenlagus/TelegramBots
    implementation("org.telegram:telegrambots:6.1.0")
    //emoji处理包，https://github.com/vdurmont/emoji-java
    implementation("com.vdurmont:emoji-java:5.1.1")
    //kotlin基本库
    //implementation("org.jetbrains.kotlin:kotlin-stdlib")
    //添加协程库，https://github.com/Kotlin/kotlinx.coroutines
    //implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    //导入sqlite依赖，https://github.com/xerial/sqlite-jdbc
    //implementation("org.xerial:sqlite-jdbc:3.36.0.3")
    //orm框架exposed，https://github.com/JetBrains/Exposed
    //implementation("org.jetbrains.exposed:exposed-core:0.38.2")
    //implementation("org.jetbrains.exposed:exposed-dao:0.38.2")
    //implementation("org.jetbrains.exposed:exposed-jdbc:0.38.2")
    //添加反射库
    //implementation("org.jetbrains.kotlin:kotlin-reflect:1.5.0")
    //yaml解析库，https://bitbucket.org/asomov/snakeyaml/wiki/Installation
    //implementation("org.yaml:snakeyaml:1.27")
    //jackson解析器，https://github.com/FasterXML/jackson
    //implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.11.4")
    //javalin框架，https://github.com/tipsy/javalin
    //implementation("io.javalin:javalin:3.13.8")


}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}