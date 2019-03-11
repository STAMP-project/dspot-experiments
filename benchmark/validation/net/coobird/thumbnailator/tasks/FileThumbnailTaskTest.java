package net.coobird.thumbnailator.tasks;


import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import net.coobird.thumbnailator.ThumbnailParameter;
import net.coobird.thumbnailator.resizers.Resizers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class FileThumbnailTaskTest {
    @Test(expected = NullPointerException.class)
    public void nullParameter() throws IOException {
        // given
        File inputFile = new File("src/test/resources/Thumbnailator/grid.jpg");
        File outputFile = File.createTempFile("thumbnailator-testing-", ".png");
        outputFile.deleteOnExit();
        try {
            // when
            new FileThumbnailTask(null, inputFile, outputFile);
            Assert.fail();
        } catch (NullPointerException e) {
            // then
            Assert.assertEquals("The parameter is null.", e.getMessage());
            throw e;
        }
    }

    @Test
    public void testRead_CorrectUsage() throws IOException {
        ThumbnailParameter param = new ThumbnailParameter(new Dimension(50, 50), null, true, "jpg", ThumbnailParameter.DEFAULT_FORMAT_TYPE, ThumbnailParameter.DEFAULT_QUALITY, BufferedImage.TYPE_INT_ARGB, null, Resizers.PROGRESSIVE, true, true);
        File inputFile = new File("src/test/resources/Thumbnailator/grid.jpg");
        File outputFile = File.createTempFile("thumbnailator-testing-", ".png");
        outputFile.deleteOnExit();
        FileThumbnailTask task = new FileThumbnailTask(param, inputFile, outputFile);
        task.read();
    }

    @Test
    public void testGetParam() {
        ThumbnailParameter param = new ThumbnailParameter(new Dimension(50, 50), null, true, "jpg", ThumbnailParameter.DEFAULT_FORMAT_TYPE, ThumbnailParameter.DEFAULT_QUALITY, BufferedImage.TYPE_INT_ARGB, null, Resizers.PROGRESSIVE, true, true);
        InputStream is = Mockito.mock(InputStream.class);
        OutputStream os = Mockito.mock(OutputStream.class);
        StreamThumbnailTask task = new StreamThumbnailTask(param, is, os);
        Assert.assertEquals(param, task.getParam());
        Mockito.verifyZeroInteractions(is);
        Mockito.verifyZeroInteractions(os);
    }
}

