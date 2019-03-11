package net.coobird.thumbnailator.tasks.io;


import ThumbnailParameter.DEFAULT_FORMAT_TYPE;
import ThumbnailParameter.DEFAULT_QUALITY;
import ThumbnailParameter.ORIGINAL_FORMAT;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.spi.IIORegistry;
import javax.imageio.spi.ImageWriterSpi;
import net.coobird.thumbnailator.TestUtils;
import net.coobird.thumbnailator.ThumbnailParameter;
import net.coobird.thumbnailator.test.BufferedImageComparer;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class OutputStreamImageSinkTest {
    @Test
    public void validOutputStream() {
        // given
        OutputStream os = Mockito.mock(OutputStream.class);
        // when
        OutputStreamImageSink sink = new OutputStreamImageSink(os);
        // then
        Assert.assertEquals(os, sink.getSink());
    }

    @Test(expected = NullPointerException.class)
    public void nullOutputStream() {
        // given
        OutputStream f = null;
        try {
            // when
            new OutputStreamImageSink(f);
        } catch (NullPointerException e) {
            // then
            Assert.assertEquals("OutputStream cannot be null.", e.getMessage());
            throw e;
        }
    }

    @Test(expected = NullPointerException.class)
    public void write_NullImage() throws IOException {
        // given
        OutputStream os = Mockito.mock(OutputStream.class);
        BufferedImage img = null;
        OutputStreamImageSink sink = new OutputStreamImageSink(os);
        sink.setOutputFormatName("png");
        try {
            // when
            sink.write(img);
        } catch (NullPointerException e) {
            // then
            Assert.assertEquals("Cannot write a null image.", e.getMessage());
            throw e;
        }
    }

    @Test(expected = IllegalStateException.class)
    public void write_ValidImage_SetOutputFormat_NotSet() throws IOException {
        // given
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        BufferedImage imgToWrite = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
        OutputStreamImageSink sink = new OutputStreamImageSink(os);
        try {
            // when
            sink.write(imgToWrite);
        } catch (IllegalStateException e) {
            // then
            Assert.assertEquals("Output format has not been set.", e.getMessage());
            throw e;
        }
    }

    @Test(expected = IllegalStateException.class)
    public void write_ValidImage_SetOutputFormat_OriginalFormat() throws IOException {
        // given
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        BufferedImage imgToWrite = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
        OutputStreamImageSink sink = new OutputStreamImageSink(os);
        sink.setOutputFormatName(ORIGINAL_FORMAT);
        try {
            // when
            sink.write(imgToWrite);
        } catch (IllegalStateException e) {
            // then
            Assert.assertEquals("Output format has not been set.", e.getMessage());
            throw e;
        }
    }

    @Test
    public void write_ValidImage_SetOutputFormat() throws IOException {
        // given
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        BufferedImage imgToWrite = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
        OutputStreamImageSink sink = new OutputStreamImageSink(os);
        // when
        sink.setOutputFormatName("png");
        sink.write(imgToWrite);
        // then
        Assert.assertEquals(os, sink.getSink());
        byte[] imageData = os.toByteArray();
        BufferedImage writtenImg = ImageIO.read(new ByteArrayInputStream(imageData));
        Assert.assertTrue(BufferedImageComparer.isRGBSimilar(imgToWrite, writtenImg));
        String formatName = TestUtils.getFormatName(new ByteArrayInputStream(imageData));
        Assert.assertEquals("png", formatName);
    }

    @Test
    public void write_ValidImage_SetThumbnailParameter_BMP_QualityAndOutputFormatType_BothDefault() throws IOException {
        // given
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        BufferedImage imgToWrite = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
        ThumbnailParameter param = Mockito.mock(ThumbnailParameter.class);
        Mockito.when(param.getOutputQuality()).thenReturn(DEFAULT_QUALITY);
        Mockito.when(param.getOutputFormatType()).thenReturn(DEFAULT_FORMAT_TYPE);
        OutputStreamImageSink sink = new OutputStreamImageSink(os);
        sink.setThumbnailParameter(param);
        sink.setOutputFormatName("bmp");
        // when
        sink.write(imgToWrite);
        // then
        Assert.assertEquals(os, sink.getSink());
        byte[] imageData = os.toByteArray();
        BufferedImage writtenImg = ImageIO.read(new ByteArrayInputStream(imageData));
        Assert.assertTrue(BufferedImageComparer.isRGBSimilar(imgToWrite, writtenImg));
        String formatName = TestUtils.getFormatName(new ByteArrayInputStream(imageData));
        Assert.assertEquals("bmp", formatName);
        Mockito.verify(param, Mockito.atLeastOnce()).getOutputQuality();
        Mockito.verify(param, Mockito.atLeastOnce()).getOutputFormatType();
    }

    @Test
    public void write_ValidImage_SetThumbnailParameter_BMP_QualityAndOutputFormatType_BothNonDefault() throws IOException {
        // given
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        BufferedImage imgToWrite = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
        ThumbnailParameter param = Mockito.mock(ThumbnailParameter.class);
        Mockito.when(param.getOutputQuality()).thenReturn(0.5F);
        Mockito.when(param.getOutputFormatType()).thenReturn("BI_BITFIELDS");
        OutputStreamImageSink sink = new OutputStreamImageSink(os);
        sink.setThumbnailParameter(param);
        sink.setOutputFormatName("bmp");
        // when
        sink.write(imgToWrite);
        // then
        Assert.assertEquals(os, sink.getSink());
        byte[] imageData = os.toByteArray();
        BufferedImage writtenImg = ImageIO.read(new ByteArrayInputStream(imageData));
        Assert.assertTrue(BufferedImageComparer.isRGBSimilar(imgToWrite, writtenImg));
        String formatName = TestUtils.getFormatName(new ByteArrayInputStream(imageData));
        Assert.assertEquals("bmp", formatName);
        Mockito.verify(param, Mockito.atLeastOnce()).getOutputQuality();
        Mockito.verify(param, Mockito.atLeastOnce()).getOutputFormatType();
    }

    @Test
    public void write_ValidImage_SetThumbnailParameter_BMP_OutputFormatType() throws IOException {
        // given
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        BufferedImage imgToWrite = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
        ThumbnailParameter param = Mockito.mock(ThumbnailParameter.class);
        Mockito.when(param.getOutputQuality()).thenReturn(DEFAULT_QUALITY);
        Mockito.when(param.getOutputFormatType()).thenReturn("BI_BITFIELDS");
        OutputStreamImageSink sink = new OutputStreamImageSink(os);
        sink.setThumbnailParameter(param);
        sink.setOutputFormatName("bmp");
        // when
        sink.write(imgToWrite);
        // then
        Assert.assertEquals(os, sink.getSink());
        byte[] imageData = os.toByteArray();
        BufferedImage writtenImg = ImageIO.read(new ByteArrayInputStream(imageData));
        Assert.assertTrue(BufferedImageComparer.isRGBSimilar(imgToWrite, writtenImg));
        String formatName = TestUtils.getFormatName(new ByteArrayInputStream(imageData));
        Assert.assertEquals("bmp", formatName);
        Mockito.verify(param, Mockito.atLeastOnce()).getOutputQuality();
    }

    @Test
    public void write_ValidImage_WriterCantCompress() throws IOException {
        // given
        ImageWriteParam iwParam = Mockito.mock(ImageWriteParam.class);
        ImageWriter writer = Mockito.mock(ImageWriter.class);
        ImageWriterSpi spi = Mockito.mock(ImageWriterSpi.class);
        Mockito.when(iwParam.canWriteCompressed()).thenReturn(false);
        Mockito.when(writer.getDefaultWriteParam()).thenReturn(iwParam);
        Mockito.when(writer.getOriginatingProvider()).thenReturn(spi);
        Mockito.when(spi.getFormatNames()).thenReturn(new String[]{ "foo", "FOO" });
        Mockito.when(spi.getFileSuffixes()).thenReturn(new String[]{ "foo", "FOO" });
        Mockito.when(spi.createWriterInstance()).thenReturn(writer);
        Mockito.when(spi.createWriterInstance(ArgumentMatchers.anyObject())).thenReturn(writer);
        IIORegistry.getDefaultInstance().registerServiceProvider(spi);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        BufferedImage imgToWrite = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        ThumbnailParameter param = Mockito.mock(ThumbnailParameter.class);
        Mockito.when(param.getOutputQuality()).thenReturn(0.8F);
        Mockito.when(param.getOutputFormatType()).thenReturn(DEFAULT_FORMAT_TYPE);
        OutputStreamImageSink sink = new OutputStreamImageSink(os);
        sink.setThumbnailParameter(param);
        sink.setOutputFormatName("foo");
        // when
        sink.write(imgToWrite);
        // then
        Mockito.verify(iwParam, Mockito.never()).setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        Mockito.verify(iwParam, Mockito.never()).setCompressionType(ArgumentMatchers.anyString());
        Mockito.verify(iwParam, Mockito.never()).setCompressionQuality(ArgumentMatchers.anyFloat());
        // - check to see that parameters were not read, as this format doesn't
        // support compression.
        Mockito.verify(param, Mockito.never()).getOutputQuality();
        Mockito.verify(param, Mockito.never()).getOutputFormatType();
        // clean up
        IIORegistry.getDefaultInstance().deregisterServiceProvider(spi);
    }

    @Test
    public void write_ValidImage_WriterCanCompress_NoCompressionTypeFromWriter() throws IOException {
        // given
        ImageWriteParam iwParam = Mockito.mock(ImageWriteParam.class);
        ImageWriter writer = Mockito.mock(ImageWriter.class);
        ImageWriterSpi spi = Mockito.mock(ImageWriterSpi.class);
        Mockito.when(iwParam.canWriteCompressed()).thenReturn(true);
        Mockito.when(iwParam.getCompressionTypes()).thenReturn(null);
        Mockito.when(writer.getDefaultWriteParam()).thenReturn(iwParam);
        Mockito.when(writer.getOriginatingProvider()).thenReturn(spi);
        Mockito.when(spi.getFormatNames()).thenReturn(new String[]{ "foo", "FOO" });
        Mockito.when(spi.getFileSuffixes()).thenReturn(new String[]{ "foo", "FOO" });
        Mockito.when(spi.createWriterInstance()).thenReturn(writer);
        Mockito.when(spi.createWriterInstance(ArgumentMatchers.anyObject())).thenReturn(writer);
        IIORegistry.getDefaultInstance().registerServiceProvider(spi);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        BufferedImage imgToWrite = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        ThumbnailParameter param = Mockito.mock(ThumbnailParameter.class);
        Mockito.when(param.getOutputQuality()).thenReturn(0.8F);
        Mockito.when(param.getOutputFormatType()).thenReturn(DEFAULT_FORMAT_TYPE);
        OutputStreamImageSink sink = new OutputStreamImageSink(os);
        sink.setThumbnailParameter(param);
        sink.setOutputFormatName("foo");
        // when
        sink.write(imgToWrite);
        // then
        Mockito.verify(iwParam, Mockito.atLeastOnce()).setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        Mockito.verify(iwParam, Mockito.never()).setCompressionType(ArgumentMatchers.anyString());
        Mockito.verify(iwParam, Mockito.atLeastOnce()).setCompressionQuality(0.8F);
        // - check to see that parameters was read
        Mockito.verify(param, Mockito.atLeastOnce()).getOutputQuality();
        Mockito.verify(param, Mockito.atLeastOnce()).getOutputFormatType();
        // clean up
        IIORegistry.getDefaultInstance().deregisterServiceProvider(spi);
    }

    @Test
    public void write_ValidImage_WriterCanCompress_HasCompressionTypeFromWriter() throws IOException {
        // given
        ImageWriteParam iwParam = Mockito.mock(ImageWriteParam.class);
        ImageWriter writer = Mockito.mock(ImageWriter.class);
        ImageWriterSpi spi = Mockito.mock(ImageWriterSpi.class);
        Mockito.when(iwParam.canWriteCompressed()).thenReturn(true);
        Mockito.when(iwParam.getCompressionTypes()).thenReturn(new String[]{ "FOOBAR" });
        Mockito.when(writer.getDefaultWriteParam()).thenReturn(iwParam);
        Mockito.when(writer.getOriginatingProvider()).thenReturn(spi);
        Mockito.when(spi.getFormatNames()).thenReturn(new String[]{ "foo", "FOO" });
        Mockito.when(spi.getFileSuffixes()).thenReturn(new String[]{ "foo", "FOO" });
        Mockito.when(spi.createWriterInstance()).thenReturn(writer);
        Mockito.when(spi.createWriterInstance(ArgumentMatchers.anyObject())).thenReturn(writer);
        IIORegistry.getDefaultInstance().registerServiceProvider(spi);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        BufferedImage imgToWrite = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        ThumbnailParameter param = Mockito.mock(ThumbnailParameter.class);
        Mockito.when(param.getOutputQuality()).thenReturn(0.8F);
        Mockito.when(param.getOutputFormatType()).thenReturn(DEFAULT_FORMAT_TYPE);
        OutputStreamImageSink sink = new OutputStreamImageSink(os);
        sink.setThumbnailParameter(param);
        sink.setOutputFormatName("foo");
        // when
        sink.write(imgToWrite);
        // then
        Mockito.verify(iwParam, Mockito.atLeastOnce()).setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        Mockito.verify(iwParam, Mockito.atLeastOnce()).setCompressionType("FOOBAR");
        Mockito.verify(iwParam, Mockito.atLeastOnce()).setCompressionQuality(0.8F);
        // - check to see that parameters was read
        Mockito.verify(param, Mockito.atLeastOnce()).getOutputQuality();
        Mockito.verify(param, Mockito.atLeastOnce()).getOutputFormatType();
        // clean up
        IIORegistry.getDefaultInstance().deregisterServiceProvider(spi);
    }
}

