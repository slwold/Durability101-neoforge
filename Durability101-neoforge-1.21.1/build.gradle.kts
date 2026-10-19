plugins {
    // NeoForge 的官方 Gradle 插件（moddev）。1.0.x 系列兼容 NeoForge 21.1.x（MC 1.21.1）。
    id("net.neoforged.moddev") version "1.0.24"
}

base {
    archivesName.set("durability101")
}

java {
    toolchain {
        // 1.21.x 需要 Java 21
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

neoForge {
    // NeoForge 版本（1.21.1 线 = 21.1.x）。如需更新，去下面的地址挑最新的 21.1.x：
    // https://maven.neoforged.net/releases/net/neoforged/neoforge/
    // 也可在命令行用：./gradlew build 时若报版本不存在，按提示改成存在的 21.1.x 即可。
    version = providers.gradleProperty("neoforge_version").get()
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}
