plugins {
    java
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:2.20.0")
    implementation("redis.clients:jedis:6.2.0")
    implementation("org.slf4j:slf4j-nop:2.0.17")
}

tasks.withType<Test> {
    useJUnitPlatform()
}