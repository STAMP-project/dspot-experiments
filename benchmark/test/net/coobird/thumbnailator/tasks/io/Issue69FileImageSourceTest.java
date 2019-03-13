package net.coobird.thumbnailator.tasks.io;


import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import net.coobird.thumbnailator.ThumbnailParameter;
import net.coobird.thumbnailator.builders.ThumbnailParameterBuilder;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


/**
 * Tests to see whether memory conservation code is triggered when the
 * {@code thumbnailator.conserveMemoryWorkaround} system property is set.
 * <p>
 * These tests will not necessarily be successful, as the workaround is only
 * triggered under conditions where the JVM memory is deemed to be low, which
 * will depend upon the environment in which the tests are run.
 */
public class Issue69FileImageSourceTest {
    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    public File tempFile;

    public static int SIZE = 8000;

    @Test
    public void fromFileBySizeWorkaroundDisabled() throws IOException {
        // given
        ThumbnailParameter param = new ThumbnailParameterBuilder().size(200, 200).build();
        FileImageSource source = new FileImageSource(tempFile);
        source.setThumbnailParameter(param);
        // when
        BufferedImage img = source.read();
        // then
        Assert.assertEquals(Issue69FileImageSourceTest.SIZE, img.getWidth());
        Assert.assertEquals(Issue69FileImageSourceTest.SIZE, img.getHeight());
    }

    @Test
    public void fromFileBySizeWorkaroundEnabled() throws IOException {
        // given
        System.setProperty("thumbnailator.conserveMemoryWorkaround", "true");
        ThumbnailParameter param = new ThumbnailParameterBuilder().size(200, 200).build();
        FileImageSource source = new FileImageSource(tempFile);
        source.setThumbnailParameter(param);
        // when
        BufferedImage img = source.read();
        // then
        Assert.assertTrue(((img.getWidth()) < (Issue69FileImageSourceTest.SIZE)));
        Assert.assertTrue(((img.getWidth()) >= 600));
        Assert.assertTrue(((img.getHeight()) < (Issue69FileImageSourceTest.SIZE)));
        Assert.assertTrue(((img.getHeight()) >= 600));
    }

    @Test
    public void fromFileByScaleWorkaroundDisabled() throws IOException {
        // given
        ThumbnailParameter param = new ThumbnailParameterBuilder().scale(0.1).build();
        FileImageSource source = new FileImageSource(tempFile);
        source.setThumbnailParameter(param);
        // when
        BufferedImage img = source.read();
        // then
        Assert.assertEquals(Issue69FileImageSourceTest.SIZE, img.getWidth());
        Assert.assertEquals(Issue69FileImageSourceTest.SIZE, img.getHeight());
    }

    @Test
    public void fromFileByScaleWorkaroundEnabled() throws IOException {
        // given
        System.setProperty("thumbnailator.conserveMemoryWorkaround", "true");
        ThumbnailParameter param = new ThumbnailParameterBuilder().scale(0.1).build();
        FileImageSource source = new FileImageSource(tempFile);
        source.setThumbnailParameter(param);
        // when
        BufferedImage img = source.read();
        // then
        Assert.assertTrue(((img.getWidth()) < (Issue69FileImageSourceTest.SIZE)));
        Assert.assertTrue(((img.getWidth()) >= 600));
        Assert.assertTrue(((img.getHeight()) < (Issue69FileImageSourceTest.SIZE)));
        Assert.assertTrue(((img.getHeight()) >= 600));
    }
}

