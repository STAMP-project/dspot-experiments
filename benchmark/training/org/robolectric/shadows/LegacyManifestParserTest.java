package org.robolectric.shadows;


import PermissionInfo.PROTECTION_DANGEROUS;
import PermissionInfo.PROTECTION_NORMAL;
import android.content.pm.PermissionInfo;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.manifest.AndroidManifest;


/**
 * Unit test for {@link org.robolectric.shadows.LegacyManifestParser}.
 */
@RunWith(AndroidJUnit4.class)
public class LegacyManifestParserTest {
    private AndroidManifest androidManifest;

    @Test
    public void createPackage_signatureOrPrivileged_shouldParseCorrectFlags() {
        Package parsedPackage = LegacyManifestParser.createPackage(androidManifest);
        int protectionLevel = getPermissionInfo(parsedPackage.permissions, "signature_or_privileged_permission").protectionLevel;
        assertThat(protectionLevel).isEqualTo(((PermissionInfo.PROTECTION_SIGNATURE) | (PermissionInfo.PROTECTION_FLAG_PRIVILEGED)));
    }

    @Test
    public void createPackage_protectionLevelNotDeclated_shouldParseToNormal() {
        Package parsedPackage = LegacyManifestParser.createPackage(androidManifest);
        int protectionLevel = getPermissionInfo(parsedPackage.permissions, "permission_with_minimal_fields").protectionLevel;
        assertThat(protectionLevel).isEqualTo(PROTECTION_NORMAL);
    }

    @Test
    public void createPackage_protectionLevelVendorOrOem_shouldParseCorrectFlags() {
        Package parsedPackage = LegacyManifestParser.createPackage(androidManifest);
        int protectionLevel = getPermissionInfo(parsedPackage.permissions, "vendor_privileged_or_oem_permission").protectionLevel;
        assertThat(protectionLevel).isEqualTo(((PermissionInfo.PROTECTION_FLAG_VENDOR_PRIVILEGED) | (PermissionInfo.PROTECTION_FLAG_OEM)));
    }

    @Test
    public void createPackage_protectionLevelDangerous_shouldParseCorrectFlags() {
        Package parsedPackage = LegacyManifestParser.createPackage(androidManifest);
        int protectionLevel = getPermissionInfo(parsedPackage.permissions, "dangerous_permission").protectionLevel;
        assertThat(protectionLevel).isEqualTo(PROTECTION_DANGEROUS);
    }
}

