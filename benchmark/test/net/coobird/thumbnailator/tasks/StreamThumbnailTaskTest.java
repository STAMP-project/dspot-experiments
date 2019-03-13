package net.coobird.thumbnailator.tasks;


import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.imageio.ImageIO;
import net.coobird.thumbnailator.ThumbnailParameter;
import net.coobird.thumbnailator.builders.BufferedImageBuilder;
import net.coobird.thumbnailator.resizers.Resizers;
import net.coobird.thumbnailator.test.BufferedImageComparer;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class StreamThumbnailTaskTest {
    @Test(expected = NullPointerException.class)
    public void nullParameter() throws IOException {
        // given
        InputStream is = Mockito.mock(InputStream.class);
        OutputStream os = Mockito.mock(OutputStream.class);
        try {
            // when
            new StreamThumbnailTask(null, is, os);
            Assert.fail();
        } catch (NullPointerException e) {
            // then
            Assert.assertEquals("The parameter is null.", e.getMessage());
            Mockito.verifyZeroInteractions(is);
            Mockito.verifyZeroInteractions(os);
            throw e;
        }
    }

    @Test
    public void testRead_CorrectUsage() throws IOException {
        ThumbnailParameter param = new ThumbnailParameter(new Dimension(50, 50), null, true, "png", ThumbnailParameter.DEFAULT_FORMAT_TYPE, ThumbnailParameter.DEFAULT_QUALITY, BufferedImage.TYPE_INT_ARGB, null, Resizers.PROGRESSIVE, true, true);
        File inputFile = new File("src/test/resources/Thumbnailator/grid.jpg");
        File outputFile = File.createTempFile("thumbnailator-testing-", ".png");
        outputFile.deleteOnExit();
        InputStream spyIs = Mockito.spy(new FileInputStream(inputFile));
        OutputStream spyOs = Mockito.spy(new FileOutputStream(outputFile));
        StreamThumbnailTask task = new StreamThumbnailTask(param, spyIs, spyOs);
        BufferedImage img = task.read();
        Assert.assertTrue(BufferedImageComparer.isSame(img, ImageIO.read(inputFile)));
        Mockito.verify(spyIs, Mockito.never()).close();
        Mockito.verifyZeroInteractions(spyOs);
    }

    @Test
    public void testWrite_CorrectUsage() throws IOException {
        ThumbnailParameter param = new ThumbnailParameter(new Dimension(50, 50), null, true, "png", ThumbnailParameter.DEFAULT_FORMAT_TYPE, ThumbnailParameter.DEFAULT_QUALITY, BufferedImage.TYPE_INT_ARGB, null, Resizers.PROGRESSIVE, true, true);
        File inputFile = new File("src/test/resources/Thumbnailator/grid.jpg");
        File outputFile = File.createTempFile("thumbnailator-testing-", ".png");
        outputFile.deleteOnExit();
        InputStream spyIs = Mockito.spy(new FileInputStream(inputFile));
        OutputStream spyOs = Mockito.spy(new FileOutputStream(outputFile));
        StreamThumbnailTask task = new StreamThumbnailTask(param, spyIs, spyOs);
        BufferedImage img = new BufferedImageBuilder(50, 50).build();
        task.write(img);
        Mockito.verifyZeroInteractions(spyIs);
        Mockito.verify(spyOs, Mockito.never()).close();
        BufferedImage outputImage = ImageIO.read(outputFile);
        Assert.assertTrue(BufferedImageComparer.isRGBSimilar(img, outputImage));
    }

    @Test
    public void testGetParam() {
        ThumbnailParameter param = new ThumbnailParameter(new Dimension(50, 50), null, true, "png", ThumbnailParameter.DEFAULT_FORMAT_TYPE, ThumbnailParameter.DEFAULT_QUALITY, BufferedImage.TYPE_INT_ARGB, null, Resizers.PROGRESSIVE, true, true);
        InputStream is = Mockito.mock(InputStream.class);
        OutputStream os = Mockito.mock(OutputStream.class);
        StreamThumbnailTask task = new StreamThumbnailTask(param, is, os);
        Assert.assertEquals(param, task.getParam());
        Mockito.verifyZeroInteractions(is);
        Mockito.verifyZeroInteractions(os);
    }
}

