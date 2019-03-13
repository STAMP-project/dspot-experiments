package net.coobird.thumbnailator.tasks.io;


import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import net.coobird.thumbnailator.builders.ThumbnailParameterBuilder;
import net.coobird.thumbnailator.geometry.AbsoluteSize;
import net.coobird.thumbnailator.geometry.Coordinate;
import net.coobird.thumbnailator.geometry.Positions;
import net.coobird.thumbnailator.test.BufferedImageComparer;
import org.junit.Assert;
import org.junit.Test;


public class BufferedImageSourceTest {
    @Test(expected = NullPointerException.class)
    public void givenNullImage() throws IOException {
        try {
            // given
            // when
            new BufferedImageSource(null);
        } catch (NullPointerException e) {
            // then
            Assert.assertEquals("Image cannot be null.", e.getMessage());
            throw e;
        }
    }

    @Test
    public void givenValidImage() throws IOException {
        // given
        BufferedImage sourceImage = ImageIO.read(new File("src/test/resources/Thumbnailator/grid.png"));
        BufferedImageSource source = new BufferedImageSource(sourceImage);
        // when
        BufferedImage img = source.read();
        // then
        Assert.assertSame(sourceImage, img);
        Assert.assertEquals(100, img.getWidth());
        Assert.assertEquals(100, img.getHeight());
        Assert.assertEquals(null, source.getInputFormatName());
    }

    @Test(expected = IllegalStateException.class)
    public void givenValidImage_getInputFormatNameBeforeRead() throws IOException {
        // given
        BufferedImage sourceImage = ImageIO.read(new File("src/test/resources/Thumbnailator/grid.png"));
        BufferedImageSource source = new BufferedImageSource(sourceImage);
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
        BufferedImage sourceImage = ImageIO.read(new File("src/test/resources/Thumbnailator/grid.png"));
        BufferedImageSource source = new BufferedImageSource(sourceImage);
        source.setThumbnailParameter(new ThumbnailParameterBuilder().region(new net.coobird.thumbnailator.geometry.Region(Positions.TOP_LEFT, new AbsoluteSize(40, 40))).size(20, 20).build());
        // when
        BufferedImage img = source.read();
        // then
        BufferedImage expectedImg = sourceImage.getSubimage(0, 0, 40, 40);
        Assert.assertTrue(BufferedImageComparer.isSame(expectedImg, img));
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
        BufferedImage sourceImage = ImageIO.read(new File("src/test/resources/Thumbnailator/grid.png"));
        BufferedImageSource source = new BufferedImageSource(sourceImage);
        source.setThumbnailParameter(new ThumbnailParameterBuilder().region(new net.coobird.thumbnailator.geometry.Region(new Coordinate(20, 20), new AbsoluteSize(100, 100))).size(80, 80).build());
        // when
        BufferedImage img = source.read();
        // then
        BufferedImage expectedImg = sourceImage.getSubimage(20, 20, 80, 80);
        Assert.assertTrue(BufferedImageComparer.isSame(expectedImg, img));
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
        BufferedImage sourceImage = ImageIO.read(new File("src/test/resources/Thumbnailator/grid.png"));
        BufferedImageSource source = new BufferedImageSource(sourceImage);
        source.setThumbnailParameter(new ThumbnailParameterBuilder().region(new net.coobird.thumbnailator.geometry.Region(new Coordinate((-20), (-20)), new AbsoluteSize(100, 100))).size(80, 80).build());
        // when
        BufferedImage img = source.read();
        // then
        BufferedImage expectedImg = sourceImage.getSubimage(0, 0, 80, 80);
        Assert.assertTrue(BufferedImageComparer.isSame(expectedImg, img));
    }

    @Test
    public void appliesSourceRegionNotSpecified() throws IOException {
        // given
        BufferedImage sourceImage = ImageIO.read(new File("src/test/resources/Thumbnailator/grid.png"));
        BufferedImageSource source = new BufferedImageSource(sourceImage);
        source.setThumbnailParameter(new ThumbnailParameterBuilder().size(20, 20).build());
        // when
        BufferedImage img = source.read();
        // then
        Assert.assertEquals(sourceImage, img);
    }
}

