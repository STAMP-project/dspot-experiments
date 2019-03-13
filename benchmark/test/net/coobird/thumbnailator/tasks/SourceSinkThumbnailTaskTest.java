package net.coobird.thumbnailator.tasks;


import ThumbnailParameter.DETERMINE_FORMAT;
import ThumbnailParameter.ORIGINAL_FORMAT;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import net.coobird.thumbnailator.TestUtils;
import net.coobird.thumbnailator.ThumbnailParameter;
import net.coobird.thumbnailator.Thumbnailator;
import net.coobird.thumbnailator.builders.BufferedImageBuilder;
import net.coobird.thumbnailator.builders.ThumbnailParameterBuilder;
import net.coobird.thumbnailator.tasks.io.BufferedImageSink;
import net.coobird.thumbnailator.tasks.io.FileImageSource;
import net.coobird.thumbnailator.tasks.io.ImageSink;
import net.coobird.thumbnailator.tasks.io.ImageSource;
import net.coobird.thumbnailator.tasks.io.InputStreamImageSource;
import net.coobird.thumbnailator.tasks.io.OutputStreamImageSink;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class SourceSinkThumbnailTaskTest {
    @SuppressWarnings("unchecked")
    @Test
    public void task_UsesPreferredFromDestination() throws Exception {
        // given
        ThumbnailParameter param = new ThumbnailParameterBuilder().size(50, 50).format(DETERMINE_FORMAT).build();
        ImageSource source = Mockito.mock(ImageSource.class);
        Mockito.when(source.read()).thenReturn(new BufferedImageBuilder(100, 100).build());
        Mockito.when(source.getInputFormatName()).thenReturn("42a");
        ImageSink destination = Mockito.mock(ImageSink.class);
        Mockito.when(destination.preferredOutputFormatName()).thenReturn("42");
        // when
        Thumbnailator.createThumbnail(new SourceSinkThumbnailTask(param, source, destination));
        // then
        Mockito.verify(source).read();
        Mockito.verify(destination).preferredOutputFormatName();
        Mockito.verify(destination).setOutputFormatName("42");
        Mockito.verify(destination).write(ArgumentMatchers.any(BufferedImage.class));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void task_UsesOriginalFormat() throws Exception {
        // given
        ThumbnailParameter param = new ThumbnailParameterBuilder().size(50, 50).format(ORIGINAL_FORMAT).build();
        ImageSource source = Mockito.mock(ImageSource.class);
        Mockito.when(source.read()).thenReturn(new BufferedImageBuilder(100, 100).build());
        Mockito.when(source.getInputFormatName()).thenReturn("42");
        ImageSink destination = Mockito.mock(ImageSink.class);
        Mockito.when(destination.preferredOutputFormatName()).thenReturn("42a");
        // when
        Thumbnailator.createThumbnail(new SourceSinkThumbnailTask(param, source, destination));
        // then
        Mockito.verify(source).read();
        Mockito.verify(destination, Mockito.never()).preferredOutputFormatName();
        Mockito.verify(destination).setOutputFormatName("42");
        Mockito.verify(destination).write(ArgumentMatchers.any(BufferedImage.class));
    }

    @Test
    public void task_SizeOnly_InputStream_BufferedImage() throws IOException {
        // given
        ThumbnailParameter param = new ThumbnailParameterBuilder().size(50, 50).build();
        ByteArrayInputStream is = new ByteArrayInputStream(SourceSinkThumbnailTaskTest.makeImageData("png", 200, 200));
        InputStreamImageSource source = new InputStreamImageSource(is);
        BufferedImageSink destination = new BufferedImageSink();
        // when
        Thumbnailator.createThumbnail(new SourceSinkThumbnailTask<java.io.InputStream, BufferedImage>(param, source, destination));
        // then
        BufferedImage thumbnail = destination.getSink();
        Assert.assertEquals(50, thumbnail.getWidth());
        Assert.assertEquals(50, thumbnail.getHeight());
    }

    @Test
    public void task_SizeOnly_InputStream_OutputStream() throws IOException {
        // given
        ThumbnailParameter param = new ThumbnailParameterBuilder().size(50, 50).build();
        byte[] imageData = SourceSinkThumbnailTaskTest.makeImageData("png", 200, 200);
        ByteArrayInputStream is = new ByteArrayInputStream(imageData);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        InputStreamImageSource source = new InputStreamImageSource(is);
        OutputStreamImageSink destination = new OutputStreamImageSink(os);
        // when
        Thumbnailator.createThumbnail(new SourceSinkThumbnailTask<java.io.InputStream, java.io.OutputStream>(param, source, destination));
        // then
        ByteArrayInputStream destIs = new ByteArrayInputStream(os.toByteArray());
        BufferedImage thumbnail = ImageIO.read(destIs);
        Assert.assertEquals(50, thumbnail.getWidth());
        Assert.assertEquals(50, thumbnail.getHeight());
        destIs = new ByteArrayInputStream(os.toByteArray());
        String formatName = TestUtils.getFormatName(destIs);
        Assert.assertEquals("png", formatName);
    }

    @Test
    public void task_ChangeOutputFormat_InputStream_OutputStream() throws IOException {
        // given
        ThumbnailParameter param = new ThumbnailParameterBuilder().size(50, 50).format("jpg").build();
        byte[] imageData = SourceSinkThumbnailTaskTest.makeImageData("png", 200, 200);
        ByteArrayInputStream is = new ByteArrayInputStream(imageData);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        InputStreamImageSource source = new InputStreamImageSource(is);
        OutputStreamImageSink destination = new OutputStreamImageSink(os);
        // when
        Thumbnailator.createThumbnail(new SourceSinkThumbnailTask<java.io.InputStream, java.io.OutputStream>(param, source, destination));
        // then
        ByteArrayInputStream destIs = new ByteArrayInputStream(os.toByteArray());
        BufferedImage thumbnail = ImageIO.read(destIs);
        Assert.assertEquals(50, thumbnail.getWidth());
        Assert.assertEquals(50, thumbnail.getHeight());
        destIs = new ByteArrayInputStream(os.toByteArray());
        String formatName = TestUtils.getFormatName(destIs);
        Assert.assertEquals("JPEG", formatName);
    }

    @Test
    public void task_ChangeOutputFormat_File_OutputStream() throws IOException {
        // given
        ThumbnailParameter param = new ThumbnailParameterBuilder().size(50, 50).format("jpg").build();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        FileImageSource source = new FileImageSource("src/test/resources/Thumbnailator/grid.bmp");
        OutputStreamImageSink destination = new OutputStreamImageSink(os);
        // when
        Thumbnailator.createThumbnail(new SourceSinkThumbnailTask<java.io.File, java.io.OutputStream>(param, source, destination));
        // then
        ByteArrayInputStream destIs = new ByteArrayInputStream(os.toByteArray());
        BufferedImage thumbnail = ImageIO.read(destIs);
        Assert.assertEquals(50, thumbnail.getWidth());
        Assert.assertEquals(50, thumbnail.getHeight());
        destIs = new ByteArrayInputStream(os.toByteArray());
        String formatName = TestUtils.getFormatName(destIs);
        Assert.assertEquals("JPEG", formatName);
    }
}

