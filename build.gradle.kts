plugins {
    java
    application
}

application {
    mainClass = "Demo"
}

java {
    sourceCompatibility = JavaVersion.VERSION_18
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform("software.amazon.awssdk:bom:2.26.15"))
    implementation("software.amazon.awssdk:dynamodb-enhanced")
    implementation("org.slf4j:slf4j-api:1.7.36")
    implementation("ch.qos.logback:logback-classic:1.2.11")
}