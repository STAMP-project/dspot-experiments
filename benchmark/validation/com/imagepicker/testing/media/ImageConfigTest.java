package com.imagepicker.testing.media;


import com.facebook.react.bridge.WritableMap;
import com.imagepicker.media.ImageConfig;
import java.io.File;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;


/**
 * Created by rusfearuth on 11.04.17.
 */
@RunWith(RobolectricTestRunner.class)
public class ImageConfigTest {
    @Test
    public void testOnImmutable() {
        ImageConfig original = new ImageConfig(new File("original.txt"), new File("resized.txt"), 0, 0, 0, 0, false);
        ImageConfig updated = original.withOriginalFile(null);
        Assert.assertNotNull("Original has got original file", original.original);
        Assert.assertNull("Updated hasn't got original file", updated.original);
        updated = original.withResizedFile(null);
        Assert.assertNotNull("Original has got resized file", original.resized);
        Assert.assertNull("Updated hasn't got resized file", updated.resized);
        updated = original.withMaxWidth(1);
        Assert.assertEquals("Original max width", 0, original.maxWidth);
        Assert.assertEquals("Updated max width", 1, updated.maxWidth);
        updated = original.withMaxHeight(2);
        Assert.assertEquals("Original max height", 0, original.maxHeight);
        Assert.assertEquals("Updated max height", 2, updated.maxHeight);
        updated = original.withQuality(29);
        Assert.assertEquals("Original quality", 0, original.quality);
        Assert.assertEquals("Updated quality", 29, updated.quality);
        updated = original.withRotation(135);
        Assert.assertEquals("Original rotation", 0, original.rotation);
        Assert.assertEquals("Updated rotation", 135, updated.rotation);
        updated = original.withSaveToCameraRoll(true);
        Assert.assertEquals("Original saveToCameraRoll", false, original.saveToCameraRoll);
        Assert.assertEquals("Updated saveToCameraRoll", true, updated.saveToCameraRoll);
    }

    @Test
    public void testParsingOptions() {
        WritableMap options = defaultOptions();
        ImageConfig config = new ImageConfig(null, null, 0, 0, 0, 0, false);
        config = config.updateFromOptions(options);
        Assert.assertEquals("maxWidth", 1000, config.maxWidth);
        Assert.assertEquals("maxHeight", 600, config.maxHeight);
        Assert.assertEquals("quality", 50, config.quality);
        Assert.assertEquals("rotation", 135, config.rotation);
        Assert.assertTrue("storageOptions.cameraRoll", config.saveToCameraRoll);
    }

    @Test
    public void testUseOriginal() {
        ImageConfig config = new ImageConfig(null, null, 800, 600, 100, 90, false);
        Assert.assertEquals("Image wont be resized", true, config.useOriginal(100, 100, 90));
        Assert.assertEquals("Image will be resized because of rotation", false, config.useOriginal(100, 100, 80));
        Assert.assertEquals("Image will be resized because of initial width", false, config.useOriginal(1000, 100, 80));
        Assert.assertEquals("Image will be resized because of initial height", false, config.useOriginal(100, 1000, 80));
        ImageConfig qualityIsLow = config.withQuality(90);
        Assert.assertEquals("Image will be resized because of quality is low", false, qualityIsLow.useOriginal(100, 100, 90));
    }

    @Test
    public void testGetActualFile() {
        ImageConfig originalConfig = new ImageConfig(new File("original.txt"), null, 0, 0, 0, 0, false);
        ImageConfig resizedConfig = originalConfig.withResizedFile(new File("resized.txt"));
        Assert.assertEquals("For config which has got only original file", "original.txt", originalConfig.getActualFile().getName());
        Assert.assertEquals("For config which has got resized file too", "resized.txt", resizedConfig.getActualFile().getName());
    }
}

