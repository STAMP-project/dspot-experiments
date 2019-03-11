package net.coobird.thumbnailator.tasks.io;


import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import net.coobird.thumbnailator.ThumbnailParameter;
import net.coobird.thumbnailator.builders.ThumbnailParameterBuilder;
import net.coobird.thumbnailator.geometry.AbsoluteSize;
import net.coobird.thumbnailator.geometry.Coordinate;
import net.coobird.thumbnailator.geometry.Positions;
import net.coobird.thumbnailator.tasks.UnsupportedFormatException;
import net.coobird.thumbnailator.test.BufferedImageAssert;
import net.coobird.thumbnailator.test.BufferedImageComparer;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class InputStreamImageSourceTest {
    @Test(expected = NullPointerException.class)
    public void givenNullInputStream() throws IOException {
        try {
            // given
            // when
            new InputStreamImageSource(null);
        } catch (NullPointerException e) {
            // then
            Assert.assertEquals("InputStream cannot be null.", e.getMessage());
            throw e;
        }
    }

    @Test
    public void fileExists_Png() throws IOException {
        // given
        InputStreamImageSource source = new InputStreamImageSource(new FileInputStream("src/test/resources/Thumbnailator/grid.png"));
        // when
        BufferedImage img = source.read();
        // then
        Assert.assertEquals(100, img.getWidth());
        Assert.assertEquals(100, img.getHeight());
        Assert.assertEquals("png", source.getInputFormatName());
    }

    @Test
    public void fileExists_Jpeg() throws IOException {
        // given
        InputStreamImageSource source = new InputStreamImageSource(new FileInputStream("src/test/resources/Thumbnailator/grid.jpg"));
        // when
        BufferedImage img = source.read();
        // then
        Assert.assertEquals(100, img.getWidth());
        Assert.assertEquals(100, img.getHeight());
        Assert.assertEquals("JPEG", source.getInputFormatName());
    }

    @Test
    public void fileExists_Bmp() throws IOException {
        // given
        InputStreamImageSource source = new InputStreamImageSource(new FileInputStream("src/test/resources/Thumbnailator/grid.bmp"));
        // when
        BufferedImage img = source.read();
        // then
        Assert.assertEquals(100, img.getWidth());
        Assert.assertEquals(100, img.getHeight());
        Assert.assertEquals("bmp", source.getInputFormatName());
    }

    @Test(expected = UnsupportedFormatException.class)
    public void cannotDetermineImageFormat() throws IOException {
        // given
        InputStream is = Mockito.mock(InputStream.class);
        Mockito.when(is.read()).thenThrow(new IOException("Failed on read."));
        Mockito.when(is.read(ArgumentMatchers.any(byte[].class))).thenThrow(new IOException("Failed on read."));
        Mockito.when(is.read(ArgumentMatchers.any(byte[].class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt())).thenThrow(new IOException("Failed on read."));
        InputStreamImageSource source = new InputStreamImageSource(is);
        // when
        try {
            source.read();
            Assert.fail();
        } catch (UnsupportedFormatException e) {
            // then
            Assert.assertEquals("No suitable ImageReader found for source data.", e.getMessage());
            throw e;
        }
    }

    @Test(expected = IOException.class)
    public void badImage_Png() throws IOException {
        try {
            // given
            byte[] bytes = new byte[100];
            new FileInputStream("src/test/resources/Thumbnailator/grid.png").read(bytes);
            ByteArrayInputStream is = new ByteArrayInputStream(bytes);
            InputStreamImageSource source = new InputStreamImageSource(is);
            // when
            source.read();
        } catch (IOException e) {
            // then
            Assert.assertEquals("Error reading PNG image data", e.getMessage());
            throw e;
        }
        Assert.fail();
    }

    @Test(expected = IllegalStateException.class)
    public void fileExists_getInputFormatNameBeforeRead() throws IOException {
        // given
        InputStreamImageSource source = new InputStreamImageSource(new FileInputStream("src/test/resources/Thumbnailator/grid.png"));
        try {
            // when
            source.getInputFormatName();
        } catch (IllegalStateException e) {
            // then
            Assert.assertEquals("Input has not been read yet.", e.getMessage());
            throw e;
        }
    }

    /* +------+-----------+
        |XXXXXX|           |
        |XXXXXX|           |
        +------+           |
        |      region      |
        |                  |
        |                  |
        |                  |
        |                  |
        +------------------+
                           source
     */
    @Test
    public void appliesSourceRegion() throws IOException {
        // given
        File sourceFile = new File("src/test/resources/Thumbnailator/grid.png");
        BufferedImage sourceImage = ImageIO.read(sourceFile);
        InputStreamImageSource source = new InputStreamImageSource(new FileInputStream(sourceFile));
        source.setThumbnailParameter(new ThumbnailParameterBuilder().region(new net.coobird.thumbnailator.geometry.Region(Positions.TOP_LEFT, new AbsoluteSize(40, 40))).size(20, 20).build());
        // when
        BufferedImage img = source.read();
        // then
        BufferedImage expectedImg = sourceImage.getSubimage(0, 0, 40, 40);
        Assert.assertTrue(BufferedImageComparer.isRGBSimilar(expectedImg, img));
    }

    /* +------------------+ source
        |  +------------------+
        |  |XXXXXXXXXXXXXXX|  |
        |  |XXXXXXXXXXXXXXX|  |
        |  |XX  final  XXXX|  |
        |  |XX  region XXXX|  |
        |  |XXXXXXXXXXXXXXX|  |
        |  |XXXXXXXXXXXXXXX|  |
        |  |XXXXXXXXXXXXXXX|  |
        +--|---------------+  |
           +------------------+
                                region
     */
    @Test
    public void appliesSourceRegionTooBig() throws IOException {
        // given
        File sourceFile = new File("src/test/resources/Thumbnailator/grid.png");
        BufferedImage sourceImage = ImageIO.read(sourceFile);
        InputStreamImageSource source = new InputStreamImageSource(new FileInputStream(sourceFile));
        source.setThumbnailParameter(new ThumbnailParameterBuilder().region(new net.coobird.thumbnailator.geometry.Region(new Coordinate(20, 20), new AbsoluteSize(100, 100))).size(80, 80).build());
        // when
        BufferedImage img = source.read();
        // then
        BufferedImage expectedImg = sourceImage.getSubimage(20, 20, 80, 80);
        Assert.assertTrue(BufferedImageComparer.isRGBSimilar(expectedImg, img));
    }

    /* +-----------------+
      |                 |
      | +---------------|--+
      | |XXXXXXXXXXXXXXX|  |
      | |XXXXXXXXXXXXXXX|  |
      | |XXXX final XXXX|  |
      | |XXXX regionXXXX|  |
      | |XXXXXXXXXXXXXXX|  |
      | |XXXXXXXXXXXXXXX|  |
      +-----------------+  |
        |                region
        +------------------+
                           source
     */
    @Test
    public void appliesSourceRegionBeyondOrigin() throws IOException {
        // given
        File sourceFile = new File("src/test/resources/Thumbnailator/grid.png");
        BufferedImage sourceImage = ImageIO.read(sourceFile);
        InputStreamImageSource source = new InputStreamImageSource(new FileInputStream(sourceFile));
        source.setThumbnailParameter(new ThumbnailParameterBuilder().region(new net.coobird.thumbnailator.geometry.Region(new Coordinate((-20), (-20)), new AbsoluteSize(100, 100))).size(80, 80).build());
        // when
        BufferedImage img = source.read();
        // then
        BufferedImage expectedImg = sourceImage.getSubimage(0, 0, 80, 80);
        Assert.assertTrue(BufferedImageComparer.isRGBSimilar(expectedImg, img));
    }

    @Test
    public void appliesSourceRegionNotSpecified() throws IOException {
        // given
        File sourceFile = new File("src/test/resources/Thumbnailator/grid.png");
        BufferedImage sourceImage = ImageIO.read(sourceFile);
        InputStreamImageSource source = new InputStreamImageSource(new FileInputStream(sourceFile));
        source.setThumbnailParameter(new ThumbnailParameterBuilder().size(20, 20).build());
        // when
        BufferedImage img = source.read();
        // then
        Assert.assertTrue(BufferedImageComparer.isRGBSimilar(sourceImage, img));
    }

    @Test
    public void readImageUnaffectedForOrientation1() throws Exception {
        // given
        File sourceFile = new File("src/test/resources/Exif/source_1.jpg");
        BufferedImage sourceImage = ImageIO.read(sourceFile);
        InputStreamImageSource source = new InputStreamImageSource(new FileInputStream(sourceFile));
        ThumbnailParameter param = new ThumbnailParameterBuilder().size(20, 20).build();
        source.setThumbnailParameter(param);
        // when
        BufferedImage img = source.read();
        // then
        Assert.assertTrue(BufferedImageComparer.isRGBSimilar(sourceImage, img));
    }

    @Test
    public void readImageUnaffectedForOrientation2() throws Exception {
        // given
        File sourceFile = new File("src/test/resources/Exif/source_2.jpg");
        BufferedImage sourceImage = ImageIO.read(sourceFile);
        InputStreamImageSource source = new InputStreamImageSource(new FileInputStream(sourceFile));
        ThumbnailParameter param = new ThumbnailParameterBuilder().size(20, 20).build();
        source.setThumbnailParameter(param);
        // when
        BufferedImage img = source.read();
        // then
        Assert.assertTrue(BufferedImageComparer.isRGBSimilar(sourceImage, img));
    }

    @Test
    public void readImageUnaffectedForOrientation3() throws Exception {
        // given
        File sourceFile = new File("src/test/resources/Exif/source_3.jpg");
        BufferedImage sourceImage = ImageIO.read(sourceFile);
        InputStreamImageSource source = new InputStreamImageSource(new FileInputStream(sourceFile));
        ThumbnailParameter param = new ThumbnailParameterBuilder().size(20, 20).build();
        source.setThumbnailParameter(param);
        // when
        BufferedImage img = source.read();
        // then
        Assert.assertTrue(BufferedImageComparer.isRGBSimilar(sourceImage, img));
    }

    @Test
    public void readImageUnaffectedForOrientation4() throws Exception {
        // given
        File sourceFile = new File("src/test/resources/Exif/source_4.jpg");
        BufferedImage sourceImage = ImageIO.read(sourceFile);
        InputStreamImageSource source = new InputStreamImageSource(new FileInputStream(sourceFile));
        ThumbnailParameter param = new ThumbnailParameterBuilder().size(20, 20).build();
        source.setThumbnailParameter(param);
        // when
        BufferedImage img = source.read();
        // then
        Assert.assertTrue(BufferedImageComparer.isRGBSimilar(sourceImage, img));
    }

    @Test
    public void readImageUnaffectedForOrientation5() throws Exception {
        // given
        File sourceFile = new File("src/test/resources/Exif/source_5.jpg");
        BufferedImage sourceImage = ImageIO.read(sourceFile);
        InputStreamImageSource source = new InputStreamImageSource(new FileInputStream(sourceFile));
        ThumbnailParameter param = new ThumbnailParameterBuilder().size(20, 20).build();
        source.setThumbnailParameter(param);
        // when
        BufferedImage img = source.read();
        // then
        Assert.assertTrue(BufferedImageComparer.isRGBSimilar(sourceImage, img));
    }

    @Test
    public void readImageUnaffectedForOrientation6() throws Exception {
        // given
        File sourceFile = new File("src/test/resources/Exif/source_6.jpg");
        BufferedImage sourceImage = ImageIO.read(sourceFile);
        InputStreamImageSource source = new InputStreamImageSource(new FileInputStream(sourceFile));
        ThumbnailParameter param = new ThumbnailParameterBuilder().size(20, 20).build();
        source.setThumbnailParameter(param);
        // when
        BufferedImage img = source.read();
        // then
        Assert.assertTrue(BufferedImageComparer.isRGBSimilar(sourceImage, img));
    }

    @Test
    public void readImageUnaffectedForOrientation7() throws Exception {
        // given
        File sourceFile = new File("src/test/resources/Exif/source_7.jpg");
        BufferedImage sourceImage = ImageIO.read(sourceFile);
        InputStreamImageSource source = new InputStreamImageSource(new FileInputStream(sourceFile));
        ThumbnailParameter param = new ThumbnailParameterBuilder().size(20, 20).build();
        source.setThumbnailParameter(param);
        // when
        BufferedImage img = source.read();
        // then
        Assert.assertTrue(BufferedImageComparer.isRGBSimilar(sourceImage, img));
    }

    @Test
    public void readImageUnaffectedForOrientation8() throws Exception {
        // given
        File sourceFile = new File("src/test/resources/Exif/source_8.jpg");
        BufferedImage sourceImage = ImageIO.read(sourceFile);
        InputStreamImageSource source = new InputStreamImageSource(new FileInputStream(sourceFile));
        ThumbnailParameter param = new ThumbnailParameterBuilder().size(20, 20).build();
        source.setThumbnailParameter(param);
        // when
        BufferedImage img = source.read();
        // then
        Assert.assertTrue(BufferedImageComparer.isRGBSimilar(sourceImage, img));
    }

    @Test
    public void containsCorrectFilterForOrientation1() throws Exception {
        // given
        File sourceFile = new File("src/test/resources/Exif/source_1.jpg");
        InputStreamImageSource source = new InputStreamImageSource(new FileInputStream(sourceFile));
        ThumbnailParameter param = new ThumbnailParameterBuilder().size(20, 20).build();
        source.setThumbnailParameter(param);
        // when
        source.read();
        // then
        Assert.assertTrue(param.getImageFilters().isEmpty());
    }

    @Test
    public void containsCorrectFilterForOrientation2() throws Exception {
        // given
        File sourceFile = new File("src/test/resources/Exif/source_2.jpg");
        BufferedImage sourceImage = ImageIO.read(sourceFile);
        InputStreamImageSource source = new InputStreamImageSource(new FileInputStream(sourceFile));
        ThumbnailParameter param = new ThumbnailParameterBuilder().size(20, 20).build();
        source.setThumbnailParameter(param);
        // when
        source.read();
        // then
        BufferedImage result = param.getImageFilters().get(0).apply(sourceImage);
        BufferedImageAssert.assertMatches(result, new float[]{ 1, 1, 1, 1, 1, 1, 1, 0, 0 });
    }

    @Test
    public void containsCorrectFilterForOrientation3() throws Exception {
        // given
        File sourceFile = new File("src/test/resources/Exif/source_3.jpg");
        BufferedImage sourceImage = ImageIO.read(sourceFile);
        InputStreamImageSource source = new InputStreamImageSource(new FileInputStream(sourceFile));
        ThumbnailParameter param = new ThumbnailParameterBuilder().size(20, 20).build();
        source.setThumbnailParameter(param);
        // when
        source.read();
        // then
        BufferedImage result = param.getImageFilters().get(0).apply(sourceImage);
        BufferedImageAssert.assertMatches(result, new float[]{ 1, 1, 1, 1, 1, 1, 1, 0, 0 });
    }

    @Test
    public void containsCorrectFilterForOrientation4() throws Exception {
        // given
        File sourceFile = new File("src/test/resources/Exif/source_4.jpg");
        BufferedImage sourceImage = ImageIO.read(sourceFile);
        InputStreamImageSource source = new InputStreamImageSource(new FileInputStream(sourceFile));
        ThumbnailParameter param = new ThumbnailParameterBuilder().size(20, 20).build();
        source.setThumbnailParameter(param);
        // when
        source.read();
        // then
        BufferedImage result = param.getImageFilters().get(0).apply(sourceImage);
        BufferedImageAssert.assertMatches(result, new float[]{ 1, 1, 1, 1, 1, 1, 1, 0, 0 });
    }

    @Test
    public void containsCorrectFilterForOrientation5() throws Exception {
        // given
        File sourceFile = new File("src/test/resources/Exif/source_5.jpg");
        BufferedImage sourceImage = ImageIO.read(sourceFile);
        InputStreamImageSource source = new InputStreamImageSource(new FileInputStream(sourceFile));
        ThumbnailParameter param = new ThumbnailParameterBuilder().size(20, 20).build();
        source.setThumbnailParameter(param);
        // when
        source.read();
        // then
        BufferedImage result = param.getImageFilters().get(0).apply(sourceImage);
        BufferedImageAssert.assertMatches(result, new float[]{ 1, 1, 1, 1, 1, 1, 1, 0, 0 });
    }

    @Test
    public void containsCorrectFilterForOrientation6() throws Exception {
        // given
        File sourceFile = new File("src/test/resources/Exif/source_6.jpg");
        BufferedImage sourceImage = ImageIO.read(sourceFile);
        InputStreamImageSource source = new InputStreamImageSource(new FileInputStream(sourceFile));
        ThumbnailParameter param = new ThumbnailParameterBuilder().size(20, 20).build();
        source.setThumbnailParameter(param);
        // when
        source.read();
        // then
        BufferedImage result = param.getImageFilters().get(0).apply(sourceImage);
        BufferedImageAssert.assertMatches(result, new float[]{ 1, 1, 1, 1, 1, 1, 1, 0, 0 });
    }

    @Test
    public void containsCorrectFilterForOrientation7() throws Exception {
        // given
        File sourceFile = new File("src/test/resources/Exif/source_7.jpg");
        BufferedImage sourceImage = ImageIO.read(sourceFile);
        InputStreamImageSource source = new InputStreamImageSource(new FileInputStream(sourceFile));
        ThumbnailParameter param = new ThumbnailParameterBuilder().size(20, 20).build();
        source.setThumbnailParameter(param);
        // when
        source.read();
        // then
        BufferedImage result = param.getImageFilters().get(0).apply(sourceImage);
        BufferedImageAssert.assertMatches(result, new float[]{ 1, 1, 1, 1, 1, 1, 1, 0, 0 });
    }

    @Test
    public void containsCorrectFilterForOrientation8() throws Exception {
        // given
        File sourceFile = new File("src/test/resources/Exif/source_8.jpg");
        BufferedImage sourceImage = ImageIO.read(sourceFile);
        InputStreamImageSource source = new InputStreamImageSource(new FileInputStream(sourceFile));
        ThumbnailParameter param = new ThumbnailParameterBuilder().size(20, 20).build();
        source.setThumbnailParameter(param);
        // when
        source.read();
        // then
        BufferedImage result = param.getImageFilters().get(0).apply(sourceImage);
        BufferedImageAssert.assertMatches(result, new float[]{ 1, 1, 1, 1, 1, 1, 1, 0, 0 });
    }

    @Test
    public void useExifOrientationIsTrue_OrientationHonored() throws Exception {
        // given
        File sourceFile = new File("src/test/resources/Exif/source_2.jpg");
        BufferedImage sourceImage = ImageIO.read(sourceFile);
        InputStreamImageSource source = new InputStreamImageSource(new FileInputStream(sourceFile));
        ThumbnailParameter param = new ThumbnailParameterBuilder().size(20, 20).useExifOrientation(true).build();
        source.setThumbnailParameter(param);
        // when
        source.read();
        // then
        BufferedImage result = param.getImageFilters().get(0).apply(sourceImage);
        BufferedImageAssert.assertMatches(result, new float[]{ 1, 1, 1, 1, 1, 1, 1, 0, 0 });
    }

    @Test
    public void useExifOrientationIsFalse_OrientationIgnored() throws Exception {
        // given
        File sourceFile = new File("src/test/resources/Exif/source_2.jpg");
        InputStreamImageSource source = new InputStreamImageSource(new FileInputStream(sourceFile));
        ThumbnailParameter param = new ThumbnailParameterBuilder().size(20, 20).useExifOrientation(false).build();
        source.setThumbnailParameter(param);
        // when
        BufferedImage result = source.read();
        // then
        Assert.assertTrue(param.getImageFilters().isEmpty());
        BufferedImageAssert.assertMatches(result, new float[]{ 1, 1, 1, 1, 1, 1, 0, 0, 1 });
    }
}

