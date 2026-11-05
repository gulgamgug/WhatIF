package app.raihanlazuardi.whatif.data.model

data class OpenSourceLibrary(
    val name: String,
    val author: String,
    val version: String,
    val licenseName: String,
    val licenseUrl: String,
    val description: String,
    val licenseSummary: String
)

object OpenSourceLibraries {
    val APACHE_2_SUMMARY: String = """
        Licensed under the Apache License, Version 2.0 (the "License");
        you may not use this file except in compliance with the License.
        You may obtain a copy of the License at

            http://www.apache.org/licenses/LICENSE-2.0

        Unless required by applicable law or agreed to in writing, software
        distributed under the License is distributed on an "AS IS" BASIS,
        WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
        See the License for the specific language governing permissions and
        limitations under the License.
    """.trimIndent()

    val OFL_SUMMARY: String = """
        SIL OPEN FONT LICENSE Version 1.1 - 26 February 2007
        This Font Software is licensed under the SIL Open Font License, Version 1.1.
        Permission is hereby granted, free of charge, to any person obtaining a copy
        of the Font Software, to use, study, copy, merge, embed, modify, redistribute,
        and sell modified and unmodified copies of the Font Software.
    """.trimIndent()

    val EPL_SUMMARY: String = """
        Eclipse Public License - v 1.0
        THE ACCOMPANYING PROGRAM IS PROVIDED UNDER THE TERMS OF THIS ECLIPSE PUBLIC
        LICENSE ("AGREEMENT"). ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM
        CONSTITUTES RECIPIENT'S ACCEPTANCE OF THIS AGREEMENT.
    """.trimIndent()

    val list: List<OpenSourceLibrary> = listOf(
        OpenSourceLibrary(
            name = "AndroidX Jetpack Compose",
            author = "The Android Open Source Project",
            version = "2025.12.00 BOM",
            licenseName = "Apache 2.0",
            licenseUrl = "https://developer.android.com/jetpack/compose",
            description = "Android's modern toolkit for building native UI with reactive Kotlin declarative components.",
            licenseSummary = APACHE_2_SUMMARY
        ),
        OpenSourceLibrary(
            name = "Material 3 Expressive",
            author = "Google LLC & The Android Open Source Project",
            version = "androidx.compose.material3",
            licenseName = "Apache 2.0",
            licenseUrl = "https://m3.material.io",
            description = "Material Design 3 system components, expressive color palettes, dynamic theming, and shapes.",
            licenseSummary = APACHE_2_SUMMARY
        ),
        OpenSourceLibrary(
            name = "Material Icons Extended",
            author = "Google LLC & The Android Open Source Project",
            version = "androidx.compose.material:material-icons-extended",
            licenseName = "Apache 2.0",
            licenseUrl = "https://fonts.google.com/icons",
            description = "Comprehensive Material Design rounded and vector drawables for Jetpack Compose.",
            licenseSummary = APACHE_2_SUMMARY
        ),
        OpenSourceLibrary(
            name = "Material 3 Adaptive Navigation Suite",
            author = "The Android Open Source Project",
            version = "androidx.compose.material3.adaptive",
            licenseName = "Apache 2.0",
            licenseUrl = "https://developer.android.com/jetpack/androidx/releases/compose-material3-adaptive",
            description = "Adaptive layout and navigation scaffolds that adjust fluidly to diverse screen form factors.",
            licenseSummary = APACHE_2_SUMMARY
        ),
        OpenSourceLibrary(
            name = "AndroidX Core KTX",
            author = "The Android Open Source Project",
            version = "1.10.1",
            licenseName = "Apache 2.0",
            licenseUrl = "https://developer.android.com/kotlin/ktx",
            description = "Kotlin extensions for Android OS core APIs, system services, and framework compatibility.",
            licenseSummary = APACHE_2_SUMMARY
        ),
        OpenSourceLibrary(
            name = "AndroidX Lifecycle & ViewModel Compose",
            author = "The Android Open Source Project",
            version = "2.6.1",
            licenseName = "Apache 2.0",
            licenseUrl = "https://developer.android.com/jetpack/androidx/releases/lifecycle",
            description = "Architecture components for managing UI state, lifecycle awareness, and coroutines orchestration.",
            licenseSummary = APACHE_2_SUMMARY
        ),
        OpenSourceLibrary(
            name = "AndroidX Activity Compose",
            author = "The Android Open Source Project",
            version = "1.8.0",
            licenseName = "Apache 2.0",
            licenseUrl = "https://developer.android.com/jetpack/androidx/releases/activity",
            description = "Compose integration with Android Activity APIs, predictive back dispatching, and result contracts.",
            licenseSummary = APACHE_2_SUMMARY
        ),
        OpenSourceLibrary(
            name = "Kotlin Standard Library & Coroutines",
            author = "JetBrains s.r.o.",
            version = "2.2.10",
            licenseName = "Apache 2.0",
            licenseUrl = "https://kotlinlang.org",
            description = "Kotlin multiplatform runtime, reflection, and asynchronous coroutines flow primitives.",
            licenseSummary = APACHE_2_SUMMARY
        ),
        OpenSourceLibrary(
            name = "Google Sans Flex Variable Font",
            author = "Google LLC",
            version = "v2024",
            licenseName = "SIL Open Font License 1.1",
            licenseUrl = "https://fonts.google.com",
            description = "Expressive geometric variable typeface with continuous weight axes from Thin 100 to Black 900.",
            licenseSummary = OFL_SUMMARY
        ),
        OpenSourceLibrary(
            name = "JUnit 4",
            author = "JUnit.org",
            version = "4.13.2",
            licenseName = "Eclipse Public License 1.0",
            licenseUrl = "https://junit.org/junit4/",
            description = "Simple and extensible Java unit testing framework for regression and assertion tests.",
            licenseSummary = EPL_SUMMARY
        )
    )
}
