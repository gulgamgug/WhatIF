package app.raihanlazuardi.whatif

import app.raihanlazuardi.whatif.data.model.OpenSourceLibraries
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AboutAndLicenseTest {

    @Test
    fun testDeveloperAndHostConfig() {
        assertEquals("cachyos-x8664", BuildConfig.BUILD_HOST)
        assertEquals("raihanlazuardi", BuildConfig.DEVELOPER_NAME)
        assertEquals("1.0", BuildConfig.VERSION_NAME)
    }

    @Test
    fun testOpenSourceLibrariesListCompleteness() {
        val libs = OpenSourceLibraries.list
        assertTrue("Library list should contain items", libs.isNotEmpty())
        assertTrue("Library list should have at least 8 items", libs.size >= 8)

        // Verify key libraries exist
        val names = libs.map { it.name }
        assertTrue(names.any { it.contains("Jetpack Compose") })
        assertTrue(names.any { it.contains("Material 3") })
        assertTrue(names.any { it.contains("Kotlin") })
        assertTrue(names.any { it.contains("Google Sans Flex") })

        // Verify no empty fields
        libs.forEach { lib ->
            assertFalse("Name should not be blank for ${lib.name}", lib.name.isBlank())
            assertFalse("Author should not be blank for ${lib.name}", lib.author.isBlank())
            assertFalse("Version should not be blank for ${lib.name}", lib.version.isBlank())
            assertFalse("LicenseName should not be blank for ${lib.name}", lib.licenseName.isBlank())
            assertFalse("LicenseUrl should not be blank for ${lib.name}", lib.licenseUrl.isBlank())
            assertFalse("Description should not be blank for ${lib.name}", lib.description.isBlank())
            assertFalse("LicenseSummary should not be blank for ${lib.name}", lib.licenseSummary.isBlank())
        }
    }
}
