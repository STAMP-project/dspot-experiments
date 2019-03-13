package org.robolectric.shadows;


import android.webkit.MimeTypeMap;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Shadows;


@RunWith(AndroidJUnit4.class)
public class ShadowMimeTypeMapTest {
    private static final String IMAGE_EXTENSION = "jpg";

    private static final String VIDEO_EXTENSION = "mp4";

    private static final String VIDEO_MIMETYPE = "video/mp4";

    private static final String IMAGE_MIMETYPE = "image/jpeg";

    @Test
    public void shouldResetStaticStateBetweenTests() throws Exception {
        Assert.assertFalse(MimeTypeMap.getSingleton().hasExtension(ShadowMimeTypeMapTest.VIDEO_EXTENSION));
        Shadows.shadowOf(MimeTypeMap.getSingleton()).addExtensionMimeTypMapping(ShadowMimeTypeMapTest.VIDEO_EXTENSION, ShadowMimeTypeMapTest.VIDEO_MIMETYPE);
    }

    @Test
    public void shouldResetStaticStateBetweenTests_anotherTime() throws Exception {
        Assert.assertFalse(MimeTypeMap.getSingleton().hasExtension(ShadowMimeTypeMapTest.VIDEO_EXTENSION));
        Shadows.shadowOf(MimeTypeMap.getSingleton()).addExtensionMimeTypMapping(ShadowMimeTypeMapTest.VIDEO_EXTENSION, ShadowMimeTypeMapTest.VIDEO_MIMETYPE);
    }

    @Test
    public void getSingletonShouldAlwaysReturnSameInstance() {
        MimeTypeMap firstInstance = MimeTypeMap.getSingleton();
        MimeTypeMap secondInstance = MimeTypeMap.getSingleton();
        Assert.assertSame(firstInstance, secondInstance);
    }

    @Test
    public void byDefaultThereShouldBeNoMapping() {
        Assert.assertFalse(MimeTypeMap.getSingleton().hasExtension(ShadowMimeTypeMapTest.VIDEO_EXTENSION));
        Assert.assertFalse(MimeTypeMap.getSingleton().hasExtension(ShadowMimeTypeMapTest.IMAGE_EXTENSION));
    }

    @Test
    public void addingMappingShouldWorkCorrectly() {
        ShadowMimeTypeMap shadowMimeTypeMap = Shadows.shadowOf(MimeTypeMap.getSingleton());
        shadowMimeTypeMap.addExtensionMimeTypMapping(ShadowMimeTypeMapTest.VIDEO_EXTENSION, ShadowMimeTypeMapTest.VIDEO_MIMETYPE);
        shadowMimeTypeMap.addExtensionMimeTypMapping(ShadowMimeTypeMapTest.IMAGE_EXTENSION, ShadowMimeTypeMapTest.IMAGE_MIMETYPE);
        Assert.assertTrue(MimeTypeMap.getSingleton().hasExtension(ShadowMimeTypeMapTest.VIDEO_EXTENSION));
        Assert.assertTrue(MimeTypeMap.getSingleton().hasExtension(ShadowMimeTypeMapTest.IMAGE_EXTENSION));
        Assert.assertTrue(MimeTypeMap.getSingleton().hasMimeType(ShadowMimeTypeMapTest.VIDEO_MIMETYPE));
        Assert.assertTrue(MimeTypeMap.getSingleton().hasMimeType(ShadowMimeTypeMapTest.IMAGE_MIMETYPE));
        Assert.assertEquals(ShadowMimeTypeMapTest.IMAGE_EXTENSION, MimeTypeMap.getSingleton().getExtensionFromMimeType(ShadowMimeTypeMapTest.IMAGE_MIMETYPE));
        Assert.assertEquals(ShadowMimeTypeMapTest.VIDEO_EXTENSION, MimeTypeMap.getSingleton().getExtensionFromMimeType(ShadowMimeTypeMapTest.VIDEO_MIMETYPE));
        Assert.assertEquals(ShadowMimeTypeMapTest.IMAGE_MIMETYPE, MimeTypeMap.getSingleton().getMimeTypeFromExtension(ShadowMimeTypeMapTest.IMAGE_EXTENSION));
        Assert.assertEquals(ShadowMimeTypeMapTest.VIDEO_MIMETYPE, MimeTypeMap.getSingleton().getMimeTypeFromExtension(ShadowMimeTypeMapTest.VIDEO_EXTENSION));
    }

    @Test
    public void clearMappingsShouldRemoveAllMappings() {
        ShadowMimeTypeMap shadowMimeTypeMap = Shadows.shadowOf(MimeTypeMap.getSingleton());
        shadowMimeTypeMap.addExtensionMimeTypMapping(ShadowMimeTypeMapTest.VIDEO_EXTENSION, ShadowMimeTypeMapTest.VIDEO_MIMETYPE);
        shadowMimeTypeMap.addExtensionMimeTypMapping(ShadowMimeTypeMapTest.IMAGE_EXTENSION, ShadowMimeTypeMapTest.IMAGE_MIMETYPE);
        shadowMimeTypeMap.clearMappings();
        Assert.assertFalse(MimeTypeMap.getSingleton().hasExtension(ShadowMimeTypeMapTest.VIDEO_EXTENSION));
        Assert.assertFalse(MimeTypeMap.getSingleton().hasExtension(ShadowMimeTypeMapTest.IMAGE_EXTENSION));
        Assert.assertFalse(MimeTypeMap.getSingleton().hasMimeType(ShadowMimeTypeMapTest.VIDEO_MIMETYPE));
        Assert.assertFalse(MimeTypeMap.getSingleton().hasExtension(ShadowMimeTypeMapTest.IMAGE_MIMETYPE));
    }

    @Test
    public void unknownExtensionShouldProvideNothing() {
        ShadowMimeTypeMap shadowMimeTypeMap = Shadows.shadowOf(MimeTypeMap.getSingleton());
        shadowMimeTypeMap.addExtensionMimeTypMapping(ShadowMimeTypeMapTest.VIDEO_EXTENSION, ShadowMimeTypeMapTest.VIDEO_MIMETYPE);
        shadowMimeTypeMap.addExtensionMimeTypMapping(ShadowMimeTypeMapTest.IMAGE_EXTENSION, ShadowMimeTypeMapTest.IMAGE_MIMETYPE);
        Assert.assertFalse(MimeTypeMap.getSingleton().hasExtension("foo"));
        Assert.assertNull(MimeTypeMap.getSingleton().getMimeTypeFromExtension("foo"));
    }

    @Test
    public void unknownMimeTypeShouldProvideNothing() {
        ShadowMimeTypeMap shadowMimeTypeMap = Shadows.shadowOf(MimeTypeMap.getSingleton());
        shadowMimeTypeMap.addExtensionMimeTypMapping(ShadowMimeTypeMapTest.VIDEO_EXTENSION, ShadowMimeTypeMapTest.VIDEO_MIMETYPE);
        shadowMimeTypeMap.addExtensionMimeTypMapping(ShadowMimeTypeMapTest.IMAGE_EXTENSION, ShadowMimeTypeMapTest.IMAGE_MIMETYPE);
        Assert.assertFalse(MimeTypeMap.getSingleton().hasMimeType("foo/bar"));
        Assert.assertNull(MimeTypeMap.getSingleton().getExtensionFromMimeType("foo/bar"));
    }
}

