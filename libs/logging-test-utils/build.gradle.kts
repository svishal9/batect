/*
    Copyright 2017-2021 Charles Korn.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
*/

val kotlinxSerializationVersion: String by project
val spekVersion: String by project
val hamkrestVersion: String by project
val mockitoKotlinVersion: String by project

plugins {
    id("batect-kotlin")
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinxSerializationVersion")
    implementation("org.spekframework.spek2:spek-dsl-jvm:$spekVersion")
    implementation("com.natpryce:hamkrest:$hamkrestVersion")
    implementation("org.mockito.kotlin:mockito-kotlin:$mockitoKotlinVersion")
    implementation(project(":libs:logging"))
    implementation(project(":libs:test-utils"))
}
