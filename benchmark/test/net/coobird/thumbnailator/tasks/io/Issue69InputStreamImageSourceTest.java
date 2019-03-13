package net.coobird.thumbnailator.tasks.io;


import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
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
public class Issue69InputStreamImageSourceTest {
    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    public static byte[] sourceByteArray;

    public static int SIZE = 8000;

    static {
        BufferedImage img = new BufferedImage(Issue69InputStreamImageSourceTest.SIZE, Issue69InputStreamImageSourceTest.SIZE, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setPaint(new GradientPaint(0, 0, Color.blue, Issue69InputStreamImageSourceTest.SIZE, Issue69InputStreamImageSourceTest.SIZE, Color.red));
        g.dispose();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.createImageOutputStream(baos);
            ImageIO.write(img, "jpg", baos);
            baos.close();
            Issue69InputStreamImageSourceTest.sourceByteArray = baos.toByteArray();
        } catch (IOException e) {
            Assert.fail();
        }
    }

    @Test
    public void fromInputStreamBySizeWorkaroundDisabled() throws IOException {
        // given
        System.setProperty("thumbnailator.conserveMemoryWorkaround", "");
        ThumbnailParameter param = new ThumbnailParameterBuilder().size(200, 200).build();
        InputStreamImageSource source = new InputStreamImageSource(new ByteArrayInputStream(Issue69InputStreamImageSourceTest.sourceByteArray));
        source.setThumbnailParameter(param);
        // when
        BufferedImage img = source.read();
        // then
        Assert.assertEquals(Issue69InputStreamImageSourceTest.SIZE, img.getWidth());
        Assert.assertEquals(Issue69InputStreamImageSourceTest.SIZE, img.getHeight());
    }

    @Test
    public void fromInputStreamBySizeWorkaroundEnabled() throws IOException {
        // given
        System.setProperty("thumbnailator.conserveMemoryWorkaround", "true");
        ThumbnailParameter param = new ThumbnailParameterBuilder().size(200, 200).build();
        InputStreamImageSource source = new InputStreamImageSource(new ByteArrayInputStream(Issue69InputStreamImageSourceTest.sourceByteArray));
        source.setThumbnailParameter(param);
        // when
        BufferedImage img = source.read();
        // then
        Assert.assertTrue(((img.getWidth()) < (Issue69InputStreamImageSourceTest.SIZE)));
        Assert.assertTrue(((img.getWidth()) >= 600));
        Assert.assertTrue(((img.getHeight()) < (Issue69InputStreamImageSourceTest.SIZE)));
        Assert.assertTrue(((img.getHeight()) >= 600));
    }

    @Test
    public void fromInputStreamByScaleWorkaroundDisabled() throws IOException {
        // given
        System.setProperty("thumbnailator.conserveMemoryWorkaround", "");
        ThumbnailParameter param = new ThumbnailParameterBuilder().scale(0.1).build();
        InputStreamImageSource source = new InputStreamImageSource(new ByteArrayInputStream(Issue69InputStreamImageSourceTest.sourceByteArray));
        source.setThumbnailParameter(param);
        // when
        BufferedImage img = source.read();
        // then
        Assert.assertEquals(Issue69InputStreamImageSourceTest.SIZE, img.getWidth());
        Assert.assertEquals(Issue69InputStreamImageSourceTest.SIZE, img.getHeight());
    }

    @Test
    public void fromInputStreamByScaleWorkaroundEnabled() throws IOException {
        // given
        System.setProperty("thumbnailator.conserveMemoryWorkaround", "true");
        ThumbnailParameter param = new ThumbnailParameterBuilder().scale(0.1).build();
        InputStreamImageSource source = new InputStreamImageSource(new ByteArrayInputStream(Issue69InputStreamImageSourceTest.sourceByteArray));
        source.setThumbnailParameter(param);
        // when
        BufferedImage img = source.read();
        // then
        Assert.assertTrue(((img.getWidth()) < (Issue69InputStreamImageSourceTest.SIZE)));
        Assert.assertTrue(((img.getWidth()) >= 600));
        Assert.assertTrue(((img.getHeight()) < (Issue69InputStreamImageSourceTest.SIZE)));
        Assert.assertTrue(((img.getHeight()) >= 600));
    }
}

