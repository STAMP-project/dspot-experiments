package net.coobird.thumbnailator;


import Rename.PREFIX_DOT_THUMBNAIL;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.imageio.ImageIO;
import net.coobird.thumbnailator.builders.BufferedImageBuilder;
import net.coobird.thumbnailator.name.Rename;
import net.coobird.thumbnailator.resizers.DefaultResizerFactory;
import net.coobird.thumbnailator.resizers.ResizerFactory;
import net.coobird.thumbnailator.tasks.UnsupportedFormatException;
import net.coobird.thumbnailator.tasks.io.BufferedImageSink;
import net.coobird.thumbnailator.tasks.io.BufferedImageSource;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Unit tests for the {@link Thumbnailator} class.
 *
 * @author coobird
 */
@SuppressWarnings("deprecation")
public class ThumbnailatorTest {
    /**
     * Test for
     * {@link Thumbnailator#createThumbnailCollection(Collection, Rename, int, int)}
     * where,
     *
     * 1) Width is negative.
     *
     * Expected outcome is,
     *
     * 1) Processing will stop with an IllegalArgumentException.
     *
     * @throws IOException
     * 		
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCreateThumbnailCollections_negativeWidth() throws IOException {
        /* The files to make thumbnails of. */
        List<File> files = Arrays.asList(new File("src/test/resources/Thumbnailator/grid.jpg"), new File("src/test/resources/Thumbnailator/grid.png"));
        Thumbnailator.createThumbnailsAsCollection(files, PREFIX_DOT_THUMBNAIL, (-42), 50);
        Assert.fail();
    }

    /**
     * Test for
     * {@link Thumbnailator#createThumbnailsAsCollection(Collection, Rename, int, int)}
     * where,
     *
     * 1) Height is negative.
     *
     * Expected outcome is,
     *
     * 1) Processing will stop with an IllegalArgumentException.
     *
     * @throws IOException
     * 		
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCreateThumbnailCollections_negativeHeight() throws IOException {
        /* The files to make thumbnails of. */
        List<File> files = Arrays.asList(new File("src/test/resources/Thumbnailator/grid.jpg"), new File("src/test/resources/Thumbnailator/grid.png"));
        Thumbnailator.createThumbnailsAsCollection(files, PREFIX_DOT_THUMBNAIL, 50, (-42));
        Assert.fail();
    }

    /**
     * Test for
     * {@link Thumbnailator#createThumbnailsAsCollection(Collection, Rename, int, int)}
     * where,
     *
     * 1) Width is negative.
     * 2) Height is negative.
     *
     * Expected outcome is,
     *
     * 1) Processing will stop with an IllegalArgumentException.
     *
     * @throws IOException
     * 		
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCreateThumbnailCollections_negativeWidthAndHeight() throws IOException {
        /* The files to make thumbnails of. */
        List<File> files = Arrays.asList(new File("src/test/resources/Thumbnailator/grid.jpg"), new File("src/test/resources/Thumbnailator/grid.png"));
        Thumbnailator.createThumbnailsAsCollection(files, PREFIX_DOT_THUMBNAIL, (-42), (-42));
        Assert.fail();
    }

    /**
     * Test for
     * {@link Thumbnailator#createThumbnailsAsCollection(Collection, Rename, int, int)}
     * where,
     *
     * 1) All parameters are correct, except
     *    a) The Collection is null
     *
     * Expected outcome is,
     *
     * 1) Processing will stop with an NullPointerException.
     *
     * @throws IOException
     * 		
     */
    @Test(expected = NullPointerException.class)
    public void testCreateThumbnailCollections_nullCollection() throws IOException {
        try {
            Thumbnailator.createThumbnailsAsCollection(null, PREFIX_DOT_THUMBNAIL, 50, 50);
            Assert.fail();
        } catch (NullPointerException e) {
            Assert.assertEquals("Collection of Files is null.", e.getMessage());
            throw e;
        }
    }

    /**
     * Test for
     * {@link Thumbnailator#createThumbnailsAsCollection(Collection, Rename, int, int)}
     * where,
     *
     * 1) All parameters are correct, except
     *    a) The Rename is null
     *
     * Expected outcome is,
     *
     * 1) Processing will stop with an NullPointerException.
     *
     * @throws IOException
     * 		
     */
    @Test(expected = NullPointerException.class)
    public void testCreateThumbnailCollections_nullRename() throws IOException {
        /* The files to make thumbnails of. */
        List<File> files = Arrays.asList(new File("src/test/resources/Thumbnailator/grid.jpg"), new File("src/test/resources/Thumbnailator/grid.png"), new File("src/test/resources/Thumbnailator/grid.bmp"));
        try {
            Thumbnailator.createThumbnailsAsCollection(files, null, 50, 50);
            Assert.fail();
        } catch (NullPointerException e) {
            Assert.assertEquals("Rename is null.", e.getMessage());
            throw e;
        }
    }

    /**
     * Test for
     * {@link Thumbnailator#createThumbnailsAsCollection(Collection, Rename, int, int)}
     * where,
     *
     * 1) All parameters are correct
     *    a) The Collection is an empty List.
     *
     * Expected outcome is,
     *
     * 1) Processing will complete successfully.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testCreateThumbnailCollections_NoErrors_EmptyList() throws IOException {
        /* The files to make thumbnails of -- nothing! */
        List<File> files = Collections.emptyList();
        Collection<File> resultingFiles = Thumbnailator.createThumbnailsAsCollection(files, PREFIX_DOT_THUMBNAIL, 50, 50);
        Assert.assertTrue(resultingFiles.isEmpty());
    }

    /**
     * Test for
     * {@link Thumbnailator#createThumbnailsAsCollection(Collection, Rename, int, int)}
     * where,
     *
     * 1) All parameters are correct
     *    a) The Collection is an empty Set.
     *
     * Expected outcome is,
     *
     * 1) Processing will complete successfully.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testCreateThumbnailCollections_NoErrors_EmptySet() throws IOException {
        /* The files to make thumbnails of -- nothing! */
        Set<File> files = Collections.emptySet();
        Thumbnailator.createThumbnailsAsCollection(files, PREFIX_DOT_THUMBNAIL, 50, 50);
    }

    /**
     * Test for
     * {@link Thumbnailator#createThumbnailsAsCollection(Collection, Rename, int, int)}
     * where,
     *
     * 1) All parameters are correct
     * 2) All data can be processed correctly.
     *
     * Expected outcome is,
     *
     * 1) Processing will complete successfully.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testCreateThumbnailCollections_NoErrors() throws IOException {
        /* The files to make thumbnails of. */
        List<File> files = Arrays.asList(new File("src/test/resources/Thumbnailator/grid.jpg"), new File("src/test/resources/Thumbnailator/grid.png"));
        /* Used to perform clean up. */
        for (File f : files) {
            String fileName = f.getName();
            String newFileName = PREFIX_DOT_THUMBNAIL.apply(fileName, null);
            new File(f.getParent(), newFileName).deleteOnExit();
        }
        Collection<File> resultingFiles = Thumbnailator.createThumbnailsAsCollection(files, PREFIX_DOT_THUMBNAIL, 50, 50);
        /* Perform post-execution checks. */
        Iterator<File> iter = resultingFiles.iterator();
        BufferedImage img0 = ImageIO.read(iter.next());
        Assert.assertEquals(50, img0.getWidth());
        Assert.assertEquals(50, img0.getHeight());
        BufferedImage img1 = ImageIO.read(iter.next());
        Assert.assertEquals(50, img1.getWidth());
        Assert.assertEquals(50, img1.getHeight());
        Assert.assertTrue((!(iter.hasNext())));
    }

    /**
     * Test for
     * {@link Thumbnailator#createThumbnailsAsCollection(Collection, Rename, int, int)}
     * where,
     *
     * 1) All parameters are correct
     * 2) A file that was specified does not exist
     *
     * Expected outcome is,
     *
     * 1) Processing will stop with an IOException.
     *
     * @throws IOException
     * 		
     */
    @Test(expected = IOException.class)
    public void testCreateThumbnailCollections_ErrorDuringProcessing_FileNotFound() throws IOException {
        /* The files to make thumbnails of. */
        List<File> files = Arrays.asList(new File("src/test/resources/Thumbnailator/grid.jpg"), new File("src/test/resources/Thumbnailator/grid.png"), new File("src/test/resources/Thumbnailator/grid.bmp"), new File("src/test/resources/Thumbnailator/filenotfound.gif"));
        /* Used to perform clean up. */
        for (File f : files) {
            String fileName = f.getName();
            String newFileName = PREFIX_DOT_THUMBNAIL.apply(fileName, null);
            new File(f.getParent(), newFileName).deleteOnExit();
        }
        Thumbnailator.createThumbnailsAsCollection(files, PREFIX_DOT_THUMBNAIL, 50, 50);
        Assert.fail();
    }

    /**
     * Test for
     * {@link Thumbnailator#createThumbnailsAsCollection(Collection, Rename, int, int)}
     * where,
     *
     * 1) All parameters are correct
     * 2) The thumbnail cannot be written. (unsupported format)
     *
     * Expected outcome is,
     *
     * 1) Processing will stop with an UnsupportedFormatException.
     *
     * @throws IOException
     * 		
     */
    @Test(expected = UnsupportedFormatException.class)
    public void testCreateThumbnailCollections_ErrorDuringProcessing_CantWriteThumbnail() throws IOException {
        /* The files to make thumbnails of. */
        List<File> files = Arrays.asList(new File("src/test/resources/Thumbnailator/grid.jpg"), new File("src/test/resources/Thumbnailator/grid.png"), new File("src/test/resources/Thumbnailator/grid.bmp"), new File("src/test/resources/Thumbnailator/grid.gif"));
        // This will force a UnsupportedFormatException when trying to output
        // a thumbnail whose source was a gif file.
        Rename brokenRenamer = new Rename() {
            @Override
            public String apply(String name, ThumbnailParameter param) {
                if (name.endsWith(".gif")) {
                    return ("thumbnail." + name) + ".foobar";
                }
                return "thumbnail." + name;
            }
        };
        /* Used to perform clean up. */
        for (File f : files) {
            String fileName = f.getName();
            String newFileName = brokenRenamer.apply(fileName, null);
            new File(f.getParent(), newFileName).deleteOnExit();
        }
        Thumbnailator.createThumbnailsAsCollection(files, brokenRenamer, 50, 50);
        Assert.fail();
    }

    /**
     * Test for
     * {@link Thumbnailator#createThumbnailsAsCollection(Collection, Rename, int, int)}
     * where,
     *
     * 1) All parameters are correct
     * 2) All data can be processed correctly.
     * 3) The Collection is a List of a class extending File.
     *
     * Expected outcome is,
     *
     * 1) Processing will complete successfully.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testCreateThumbnailCollections_NoErrors_CollectionExtendsFile() throws IOException {
        class File2 extends File {
            private static final long serialVersionUID = 1L;

            public File2(String pathname) {
                super(pathname);
            }
        }
        /* The files to make thumbnails of. */
        List<File2> files = Arrays.asList(new File2("src/test/resources/Thumbnailator/grid.jpg"), new File2("src/test/resources/Thumbnailator/grid.png"));
        /* Used to perform clean up. */
        for (File f : files) {
            String fileName = f.getName();
            String newFileName = PREFIX_DOT_THUMBNAIL.apply(fileName, null);
            new File(f.getParent(), newFileName).deleteOnExit();
        }
        Collection<File> resultingFiles = Thumbnailator.createThumbnailsAsCollection(files, PREFIX_DOT_THUMBNAIL, 50, 50);
        /* Perform post-execution checks. */
        Iterator<File> iter = resultingFiles.iterator();
        BufferedImage img0 = ImageIO.read(iter.next());
        Assert.assertEquals(50, img0.getWidth());
        Assert.assertEquals(50, img0.getHeight());
        BufferedImage img1 = ImageIO.read(iter.next());
        Assert.assertEquals(50, img1.getWidth());
        Assert.assertEquals(50, img1.getHeight());
        Assert.assertTrue((!(iter.hasNext())));
    }

    /**
     * Test for
     * {@link Thumbnailator#createThumbnails(Collection, Rename, int, int)}
     * where,
     *
     * 1) Width is negative.
     *
     * Expected outcome is,
     *
     * 1) Processing will stop with an IllegalArgumentException.
     *
     * @throws IOException
     * 		
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCreateThumbnails_negativeWidth() throws IOException {
        /* The files to make thumbnails of. */
        List<File> files = Arrays.asList(new File("src/test/resources/Thumbnailator/grid.jpg"), new File("src/test/resources/Thumbnailator/grid.png"));
        Thumbnailator.createThumbnails(files, PREFIX_DOT_THUMBNAIL, (-42), 50);
        Assert.fail();
    }

    /**
     * Test for
     * {@link Thumbnailator#createThumbnails(Collection, Rename, int, int)}
     * where,
     *
     * 1) Height is negative.
     *
     * Expected outcome is,
     *
     * 1) Processing will stop with an IllegalArgumentException.
     *
     * @throws IOException
     * 		
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCreateThumbnails_negativeHeight() throws IOException {
        /* The files to make thumbnails of. */
        List<File> files = Arrays.asList(new File("src/test/resources/Thumbnailator/grid.jpg"), new File("src/test/resources/Thumbnailator/grid.png"));
        Thumbnailator.createThumbnails(files, PREFIX_DOT_THUMBNAIL, 50, (-42));
        Assert.fail();
    }

    /**
     * Test for
     * {@link Thumbnailator#createThumbnails(Collection, Rename, int, int)}
     * where,
     *
     * 1) Width is negative.
     * 2) Height is negative.
     *
     * Expected outcome is,
     *
     * 1) Processing will stop with an IllegalArgumentException.
     *
     * @throws IOException
     * 		
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCreateThumbnails_negativeWidthAndHeight() throws IOException {
        /* The files to make thumbnails of. */
        List<File> files = Arrays.asList(new File("src/test/resources/Thumbnailator/grid.jpg"), new File("src/test/resources/Thumbnailator/grid.png"));
        Thumbnailator.createThumbnails(files, PREFIX_DOT_THUMBNAIL, (-42), (-42));
        Assert.fail();
    }

    /**
     * Test for
     * {@link Thumbnailator#createThumbnails(Collection, Rename, int, int)}
     * where,
     *
     * 1) All parameters are correct, except
     *    a) The Collection is null
     *
     * Expected outcome is,
     *
     * 1) Processing will stop with an NullPointerException.
     *
     * @throws IOException
     * 		
     */
    @Test(expected = NullPointerException.class)
    public void testCreateThumbnails_nullCollection() throws IOException {
        try {
            Thumbnailator.createThumbnails(null, PREFIX_DOT_THUMBNAIL, 50, 50);
            Assert.fail();
        } catch (NullPointerException e) {
            Assert.assertEquals("Collection of Files is null.", e.getMessage());
            throw e;
        }
    }

    /**
     * Test for
     * {@link Thumbnailator#createThumbnails(Collection, Rename, int, int)}
     * where,
     *
     * 1) All parameters are correct, except
     *    a) The Rename is null
     *
     * Expected outcome is,
     *
     * 1) Processing will stop with an NullPointerException.
     *
     * @throws IOException
     * 		
     */
    @Test(expected = NullPointerException.class)
    public void testCreateThumbnails_nullRename() throws IOException {
        /* The files to make thumbnails of. */
        List<File> files = Arrays.asList(new File("src/test/resources/Thumbnailator/grid.jpg"), new File("src/test/resources/Thumbnailator/grid.png"), new File("src/test/resources/Thumbnailator/grid.bmp"));
        try {
            Thumbnailator.createThumbnails(files, null, 50, 50);
            Assert.fail();
        } catch (NullPointerException e) {
            Assert.assertEquals("Rename is null.", e.getMessage());
            throw e;
        }
    }

    /**
     * Test for
     * {@link Thumbnailator#createThumbnails(Collection, Rename, int, int)}
     * where,
     *
     * 1) All parameters are correct
     *    a) The Collection is an empty List.
     *
     * Expected outcome is,
     *
     * 1) Processing will complete successfully.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testCreateThumbnails_NoErrors_EmptyList() throws IOException {
        /* The files to make thumbnails of -- nothing! */
        List<File> files = Collections.emptyList();
        Thumbnailator.createThumbnails(files, PREFIX_DOT_THUMBNAIL, 50, 50);
    }

    /**
     * Test for
     * {@link Thumbnailator#createThumbnails(Collection, Rename, int, int)}
     * where,
     *
     * 1) All parameters are correct
     *    a) The Collection is an empty Set.
     *
     * Expected outcome is,
     *
     * 1) Processing will complete successfully.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testCreateThumbnails_NoErrors_EmptySet() throws IOException {
        /* The files to make thumbnails of -- nothing! */
        Set<File> files = Collections.emptySet();
        Thumbnailator.createThumbnails(files, PREFIX_DOT_THUMBNAIL, 50, 50);
    }

    /**
     * Test for
     * {@link Thumbnailator#createThumbnails(Collection, Rename, int, int)}
     * where,
     *
     * 1) All parameters are correct
     * 2) All data can be processed correctly.
     *
     * Expected outcome is,
     *
     * 1) Processing will complete successfully.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testCreateThumbnails_NoErrors() throws IOException {
        /* The files to make thumbnails of. */
        List<File> files = Arrays.asList(new File("src/test/resources/Thumbnailator/grid.jpg"), new File("src/test/resources/Thumbnailator/grid.png"));
        /* Used to perform clean up. */
        for (File f : files) {
            String fileName = f.getName();
            String newFileName = PREFIX_DOT_THUMBNAIL.apply(fileName, null);
            new File(f.getParent(), newFileName).deleteOnExit();
        }
        Thumbnailator.createThumbnails(files, PREFIX_DOT_THUMBNAIL, 50, 50);
        /* Perform post-execution checks. */
        BufferedImage img0 = ImageIO.read(new File("src/test/resources/Thumbnailator/thumbnail.grid.jpg"));
        Assert.assertEquals(50, img0.getWidth());
        Assert.assertEquals(50, img0.getHeight());
        BufferedImage img1 = ImageIO.read(new File("src/test/resources/Thumbnailator/thumbnail.grid.png"));
        Assert.assertEquals(50, img1.getWidth());
        Assert.assertEquals(50, img1.getHeight());
    }

    /**
     * Test for
     * {@link Thumbnailator#createThumbnails(Collection, Rename, int, int)}
     * where,
     *
     * 1) All parameters are correct
     * 2) A file that was specified does not exist
     *
     * Expected outcome is,
     *
     * 1) Processing will stop with an IOException.
     *
     * @throws IOException
     * 		
     */
    @Test(expected = IOException.class)
    public void testCreateThumbnails_ErrorDuringProcessing_FileNotFound() throws IOException {
        /* The files to make thumbnails of. */
        List<File> files = Arrays.asList(new File("src/test/resources/Thumbnailator/grid.jpg"), new File("src/test/resources/Thumbnailator/grid.png"), new File("src/test/resources/Thumbnailator/grid.bmp"), new File("src/test/resources/Thumbnailator/filenotfound.gif"));
        /* Used to perform clean up. */
        for (File f : files) {
            String fileName = f.getName();
            String newFileName = PREFIX_DOT_THUMBNAIL.apply(fileName, null);
            new File(f.getParent(), newFileName).deleteOnExit();
        }
        Thumbnailator.createThumbnails(files, PREFIX_DOT_THUMBNAIL, 50, 50);
        Assert.fail();
    }

    /**
     * Test for
     * {@link Thumbnailator#createThumbnails(Collection, Rename, int, int)}
     * where,
     *
     * 1) All parameters are correct
     * 2) The thumbnail cannot be written. (unsupported format)
     *
     * Expected outcome is,
     *
     * 1) Processing will stop with an UnsupportedFormatException.
     *
     * @throws IOException
     * 		
     */
    @Test(expected = UnsupportedFormatException.class)
    public void testCreateThumbnails_ErrorDuringProcessing_CantWriteThumbnail() throws IOException {
        /* The files to make thumbnails of. */
        List<File> files = Arrays.asList(new File("src/test/resources/Thumbnailator/grid.jpg"), new File("src/test/resources/Thumbnailator/grid.png"), new File("src/test/resources/Thumbnailator/grid.bmp"), new File("src/test/resources/Thumbnailator/grid.gif"));
        // This will force a UnsupportedFormatException when trying to output
        // a thumbnail whose source was a gif file.
        Rename brokenRenamer = new Rename() {
            @Override
            public String apply(String name, ThumbnailParameter param) {
                if (name.endsWith(".gif")) {
                    return ("thumbnail." + name) + ".foobar";
                }
                return "thumbnail." + name;
            }
        };
        /* Used to perform clean up. */
        for (File f : files) {
            String fileName = f.getName();
            String newFileName = brokenRenamer.apply(fileName, null);
            new File(f.getParent(), newFileName).deleteOnExit();
        }
        Thumbnailator.createThumbnails(files, brokenRenamer, 50, 50);
        Assert.fail();
    }

    /**
     * Test for
     * {@link Thumbnailator#createThumbnail(InputStream, OutputStream, int, int)}
     * where,
     *
     * 1) InputStream is null
     *
     * Expected outcome is,
     *
     * 1) Processing will stop with an NullPointerException.
     *
     * @throws IOException
     * 		
     */
    @Test(expected = NullPointerException.class)
    public void testCreateThumbnail_IOII_nullIS() throws IOException {
        /* Actual test */
        InputStream is = null;
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Thumbnailator.createThumbnail(is, os, 50, 50);
        Assert.fail();
    }

    /**
     * Test for
     * {@link Thumbnailator#createThumbnail(InputStream, OutputStream, int, int)}
     * where,
     *
     * 1) OutputStream is null
     *
     * Expected outcome is,
     *
     * 1) Processing will stop with an NullPointerException.
     *
     * @throws IOException
     * 		
     */
    @Test(expected = NullPointerException.class)
    public void testCreateThumbnail_IOII_nullOS() throws IOException {
        /* Actual test */
        byte[] bytes = makeImageData("jpg", 200, 200);
        InputStream is = new ByteArrayInputStream(bytes);
        ByteArrayOutputStream os = null;
        Thumbnailator.createThumbnail(is, os, 50, 50);
        Assert.fail();
    }

    /**
     * Test for
     * {@link Thumbnailator#createThumbnail(InputStream, OutputStream, int, int)}
     * where,
     *
     * 1) InputStream is null
     * 2) OutputStream is null
     *
     * Expected outcome is,
     *
     * 1) Processing will stop with an NullPointerException.
     *
     * @throws IOException
     * 		
     */
    @Test(expected = NullPointerException.class)
    public void testCreateThumbnail_IOII_nullISnullOS() throws IOException {
        Thumbnailator.createThumbnail(((InputStream) (null)), null, 50, 50);
        Assert.fail();
    }

    /**
     * Test for
     * {@link Thumbnailator#createThumbnail(InputStream, OutputStream, int, int)}
     * where,
     *
     * 1) Width is negative.
     *
     * Expected outcome is,
     *
     * 1) Processing will stop with an IllegalArgumentException.
     *
     * @throws IOException
     * 		
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCreateThumbnail_IOII_negativeWidth() throws IOException {
        /* Actual test */
        byte[] bytes = makeImageData("jpg", 200, 200);
        InputStream is = new ByteArrayInputStream(bytes);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Thumbnailator.createThumbnail(is, os, (-42), 50);
        Assert.fail();
    }

    /**
     * Test for
     * {@link Thumbnailator#createThumbnail(InputStream, OutputStream, int, int)}
     * where,
     *
     * 1) Height is negative.
     *
     * Expected outcome is,
     *
     * 1) Processing will stop with an IllegalArgumentException.
     *
     * @throws IOException
     * 		
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCreateThumbnail_IOII_negativeHeight() throws IOException {
        /* Actual test */
        byte[] bytes = makeImageData("jpg", 200, 200);
        InputStream is = new ByteArrayInputStream(bytes);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Thumbnailator.createThumbnail(is, os, 50, (-42));
        Assert.fail();
    }

    /**
     * Test for
     * {@link Thumbnailator#createThumbnail(InputStream, OutputStream, int, int)}
     * where,
     *
     * 1) Width is negative.
     * 2) Height is negative.
     *
     * Expected outcome is,
     *
     * 1) Processing will stop with an IllegalArgumentException.
     *
     * @throws IOException
     * 		
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCreateThumbnail_IOII_negativeWidthAndHeight() throws IOException {
        /* Actual test */
        byte[] bytes = makeImageData("jpg", 200, 200);
        InputStream is = new ByteArrayInputStream(bytes);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Thumbnailator.createThumbnail(is, os, (-42), (-42));
        Assert.fail();
    }

    /**
     * Test for
     * {@link Thumbnailator#createThumbnail(InputStream, OutputStream, int, int)}
     * where,
     *
     * 1) Method arguments are correct
     * 2) Input data is a JPEG image
     *
     * Expected outcome is,
     *
     * 1) Processing will complete successfully.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testCreateThumbnail_IOII_Jpg() throws IOException {
        /* Actual test */
        byte[] bytes = makeImageData("jpg", 200, 200);
        InputStream is = new ByteArrayInputStream(bytes);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Thumbnailator.createThumbnail(is, os, 50, 50);
        /* Post-test checks */
        InputStream thumbIs = new ByteArrayInputStream(os.toByteArray());
        BufferedImage thumb = ImageIO.read(thumbIs);
        Assert.assertEquals(50, thumb.getWidth());
        Assert.assertEquals(50, thumb.getHeight());
    }

    /**
     * Test for
     * {@link Thumbnailator#createThumbnail(InputStream, OutputStream, int, int)}
     * where,
     *
     * 1) Method arguments are correct
     * 2) Input data is a PNG image
     *
     * Expected outcome is,
     *
     * 1) Processing will complete successfully.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testCreateThumbnail_IOII_Png() throws IOException {
        /* Actual test */
        byte[] bytes = makeImageData("png", 200, 200);
        InputStream is = new ByteArrayInputStream(bytes);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Thumbnailator.createThumbnail(is, os, 50, 50);
        /* Post-test checks */
        InputStream thumbIs = new ByteArrayInputStream(os.toByteArray());
        BufferedImage thumb = ImageIO.read(thumbIs);
        Assert.assertEquals(50, thumb.getWidth());
        Assert.assertEquals(50, thumb.getHeight());
    }

    /**
     * Test for
     * {@link Thumbnailator#createThumbnail(InputStream, OutputStream, int, int)}
     * where,
     *
     * 1) Method arguments are correct
     * 2) Input data is a BMP image
     *
     * Expected outcome is,
     *
     * 1) Processing will complete successfully.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testCreateThumbnail_IOII_Bmp() throws IOException {
        /* Actual test */
        byte[] bytes = new byte[40054];
        FileInputStream fis = new FileInputStream("src/test/resources/Thumbnailator/grid.bmp");
        fis.read(bytes);
        fis.close();
        InputStream is = new ByteArrayInputStream(bytes);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Thumbnailator.createThumbnail(is, os, 50, 50);
        /* Post-test checks */
        InputStream thumbIs = new ByteArrayInputStream(os.toByteArray());
        BufferedImage thumb = ImageIO.read(thumbIs);
        Assert.assertEquals(50, thumb.getWidth());
        Assert.assertEquals(50, thumb.getHeight());
    }

    /**
     * Test for
     * {@link Thumbnailator#createThumbnail(InputStream, OutputStream, int, int)}
     * where,
     *
     * 1) Method arguments are correct
     * 2) Input data is a BMP image
     *    -> writing to a BMP is not supported by default.
     *
     * Expected outcome is,
     *
     * 1) Processing will stop with an IOException.
     *    On Java 6 and later, this should pass, as it contains a GIF writer.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testCreateThumbnail_IOII_Gif() throws IOException {
        /* Actual test */
        byte[] bytes = new byte[492];
        FileInputStream fis = new FileInputStream("src/test/resources/Thumbnailator/grid.gif");
        fis.read(bytes);
        fis.close();
        InputStream is = new ByteArrayInputStream(bytes);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            Thumbnailator.createThumbnail(is, os, 50, 50);
            // This case should pass on Java 6 and later, as those JREs have a
            // GIF writer.
            if (System.getProperty("java.version").startsWith("1.5")) {
                Assert.fail();
            }
            /* Post-test checks */
            InputStream thumbIs = new ByteArrayInputStream(os.toByteArray());
            BufferedImage img = ImageIO.read(thumbIs);
            thumbIs.close();
            Assert.assertEquals("gif", ImageIO.getImageReaders(ImageIO.createImageInputStream(new ByteArrayInputStream(os.toByteArray()))).next().getFormatName());
            Assert.assertEquals(50, img.getWidth());
            Assert.assertEquals(50, img.getHeight());
        } catch (UnsupportedFormatException e) {
            // This case should pass on Java 6 and later.
            if (!(System.getProperty("java.version").startsWith("1.5"))) {
                Assert.fail();
            }
            Assert.assertEquals("No suitable ImageWriter found for gif.", e.getMessage());
            Assert.assertEquals("gif", e.getFormatName());
        }
    }

    /**
     * Test for
     * {@link Thumbnailator#createThumbnail(InputStream, OutputStream, int, int)}
     * where,
     *
     * 1) InputStream throws an IOException during read.
     *
     * Expected outcome is,
     *
     * 1) Processing will stop with an IOException.
     *
     * @throws IOException
     * 		
     */
    @Test(expected = IOException.class)
    public void testCreateThumbnail_IOII_IOExceptionFromIS() throws IOException {
        /* Actual test */
        InputStream is = Mockito.mock(InputStream.class);
        Mockito.doThrow(new IOException("read error!")).when(is).read();
        Mockito.doThrow(new IOException("read error!")).when(is).read(((byte[]) (ArgumentMatchers.any())));
        Mockito.doThrow(new IOException("read error!")).when(is).read(((byte[]) (ArgumentMatchers.any())), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Thumbnailator.createThumbnail(is, os, 50, 50);
        Assert.fail();
    }

    /**
     * Test for
     * {@link Thumbnailator#createThumbnail(InputStream, OutputStream, int, int)}
     * where,
     *
     * 1) OutputStream throws an IOException during read.
     *
     * Expected outcome is,
     *
     * 1) Processing will stop with an IOException.
     *
     * @throws IOException
     * 		
     */
    @Test(expected = IOException.class)
    public void testCreateThumbnail_IOII_IOExceptionFromOS() throws IOException {
        /* Actual test */
        byte[] bytes = makeImageData("png", 200, 200);
        InputStream is = new ByteArrayInputStream(bytes);
        OutputStream os = Mockito.mock(OutputStream.class);
        Mockito.doThrow(new IOException("write error!")).when(os).write(ArgumentMatchers.anyInt());
        Mockito.doThrow(new IOException("write error!")).when(os).write(((byte[]) (ArgumentMatchers.any())));
        Mockito.doThrow(new IOException("write error!")).when(os).write(((byte[]) (ArgumentMatchers.any())), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
        Thumbnailator.createThumbnail(is, os, 50, 50);
        Assert.fail();
    }

    /**
     * Test for
     * {@link Thumbnailator#createThumbnail(InputStream, OutputStream, String, int, int)}
     * where,
     *
     * 1) Method arguments are correct
     * 2) There is transcoding taking place:
     *   a) Input file is a JPEG image
     *   b) Output file is a PNG image
     *
     * Expected outcome is,
     *
     * 1) Processing will complete successfully.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testCreateThumbnail_IOSII_Transcoding_Jpeg_Png() throws IOException {
        /* Actual test */
        byte[] bytes = new byte[4602];
        FileInputStream fis = new FileInputStream("src/test/resources/Thumbnailator/grid.jpg");
        fis.read(bytes);
        fis.close();
        InputStream is = new ByteArrayInputStream(bytes);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Thumbnailator.createThumbnail(is, os, "png", 50, 50);
        /* Post-test checks */
        InputStream thumbIs = new ByteArrayInputStream(os.toByteArray());
        BufferedImage img = ImageIO.read(thumbIs);
        Assert.assertEquals("png", ImageIO.getImageReaders(ImageIO.createImageInputStream(new ByteArrayInputStream(os.toByteArray()))).next().getFormatName());
        Assert.assertEquals(50, img.getWidth());
        Assert.assertEquals(50, img.getHeight());
    }

    /**
     * Test for
     * {@link Thumbnailator#createThumbnail(InputStream, OutputStream, String, int, int)}
     * where,
     *
     * 1) Method arguments are correct
     * 2) There is transcoding taking place:
     *   a) Input file is a JPEG image
     *   b) Output file is a BMP image
     *
     * Expected outcome is,
     *
     * 1) Processing will complete successfully.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testCreateThumbnail_IOSII_Transcoding_Jpeg_Bmp() throws IOException {
        /* Actual test */
        byte[] bytes = new byte[4602];
        FileInputStream fis = new FileInputStream("src/test/resources/Thumbnailator/grid.jpg");
        fis.read(bytes);
        fis.close();
        InputStream is = new ByteArrayInputStream(bytes);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Thumbnailator.createThumbnail(is, os, "bmp", 50, 50);
        /* Post-test checks */
        InputStream thumbIs = new ByteArrayInputStream(os.toByteArray());
        BufferedImage img = ImageIO.read(thumbIs);
        Assert.assertEquals("bmp", ImageIO.getImageReaders(ImageIO.createImageInputStream(new ByteArrayInputStream(os.toByteArray()))).next().getFormatName());
        Assert.assertEquals(50, img.getWidth());
        Assert.assertEquals(50, img.getHeight());
    }

    /**
     * Test for
     * {@link Thumbnailator#createThumbnail(InputStream, OutputStream, String, int, int)}
     * where,
     *
     * 1) Method arguments are correct
     * 2) There is transcoding taking place:
     *   a) Input file is a JPEG image
     *   b) Output file is a GIF image
     *
     * Expected outcome is,
     *
     * 1) Processing will fail with an IllegalArgumentException due to not
     *    being able to write to a GIF.
     *    On Java 6 and later, this should pass, as it contains a GIF writer.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testCreateThumbnail_IOSII_Transcoding_Jpeg_Gif() throws IOException {
        /* Actual test */
        byte[] bytes = new byte[4602];
        FileInputStream fis = new FileInputStream("src/test/resources/Thumbnailator/grid.jpg");
        fis.read(bytes);
        fis.close();
        InputStream is = new ByteArrayInputStream(bytes);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            Thumbnailator.createThumbnail(is, os, "gif", 50, 50);
            // This case should pass on Java 6 and later, as those JREs have a
            // GIF writer.
            if (System.getProperty("java.version").startsWith("1.5")) {
                Assert.fail();
            }
            /* Post-test checks */
            InputStream thumbIs = new ByteArrayInputStream(os.toByteArray());
            BufferedImage img = ImageIO.read(thumbIs);
            thumbIs.close();
            Assert.assertEquals("gif", ImageIO.getImageReaders(ImageIO.createImageInputStream(new ByteArrayInputStream(os.toByteArray()))).next().getFormatName());
            Assert.assertEquals(50, img.getWidth());
            Assert.assertEquals(50, img.getHeight());
        } catch (IllegalArgumentException e) {
            // This case should pass on Java 6 and later.
            if (!(System.getProperty("java.version").startsWith("1.5"))) {
                Assert.fail();
            }
            Assert.assertTrue(e.getMessage().contains("gif"));
        }
    }

    /**
     * Test for
     * {@link Thumbnailator#createThumbnail(InputStream, OutputStream, String, int, int)}
     * where,
     *
     * 1) Method arguments are correct
     * 2) There is transcoding taking place:
     *   a) Input file is a PNG image
     *   b) Output file is a JPEG image
     *
     * Expected outcome is,
     *
     * 1) Processing will complete successfully.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testCreateThumbnail_IOSII_Transcoding_Png_Jpeg() throws IOException {
        /* Actual test */
        byte[] bytes = new byte[287];
        FileInputStream fis = new FileInputStream("src/test/resources/Thumbnailator/grid.png");
        fis.read(bytes);
        fis.close();
        InputStream is = new ByteArrayInputStream(bytes);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Thumbnailator.createThumbnail(is, os, "jpg", 50, 50);
        /* Post-test checks */
        InputStream thumbIs = new ByteArrayInputStream(os.toByteArray());
        BufferedImage img = ImageIO.read(thumbIs);
        Assert.assertEquals("JPEG", ImageIO.getImageReaders(ImageIO.createImageInputStream(new ByteArrayInputStream(os.toByteArray()))).next().getFormatName());
        Assert.assertEquals(50, img.getWidth());
        Assert.assertEquals(50, img.getHeight());
    }

    /**
     * Test for
     * {@link Thumbnailator#createThumbnail(InputStream, OutputStream, String, int, int)}
     * where,
     *
     * 1) Method arguments are correct
     * 2) There is transcoding taking place:
     *   a) Input file is a PNG image
     *   b) Output file is a BMP image
     *
     * Expected outcome is,
     *
     * 1) Processing will complete successfully.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testCreateThumbnail_IOSII_Transcoding_Png_Bmp() throws IOException {
        /* Actual test */
        byte[] bytes = new byte[287];
        FileInputStream fis = new FileInputStream("src/test/resources/Thumbnailator/grid.png");
        fis.read(bytes);
        fis.close();
        InputStream is = new ByteArrayInputStream(bytes);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Thumbnailator.createThumbnail(is, os, "bmp", 50, 50);
        /* Post-test checks */
        InputStream thumbIs = new ByteArrayInputStream(os.toByteArray());
        BufferedImage img = ImageIO.read(thumbIs);
        Assert.assertEquals("bmp", ImageIO.getImageReaders(ImageIO.createImageInputStream(new ByteArrayInputStream(os.toByteArray()))).next().getFormatName());
        Assert.assertEquals(50, img.getWidth());
        Assert.assertEquals(50, img.getHeight());
    }

    /**
     * Test for
     * {@link Thumbnailator#createThumbnail(InputStream, OutputStream, String, int, int)}
     * where,
     *
     * 1) Method arguments are correct
     * 2) There is transcoding taking place:
     *   a) Input file is a PNG image
     *   b) Output file is a GIF image
     *
     * Expected outcome is,
     *
     * 1) Processing will fail with an IllegalArgumentException due to not
     *    being able to write to a GIF.
     *    On Java 6 and later, this should pass, as it contains a GIF writer.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testCreateThumbnail_IOSII_Transcoding_Png_Gif() throws IOException {
        /* Actual test */
        byte[] bytes = new byte[287];
        FileInputStream fis = new FileInputStream("src/test/resources/Thumbnailator/grid.png");
        fis.read(bytes);
        fis.close();
        InputStream is = new ByteArrayInputStream(bytes);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            Thumbnailator.createThumbnail(is, os, "gif", 50, 50);
            // This case should pass on Java 6 and later, as those JREs have a
            // GIF writer.
            if (System.getProperty("java.version").startsWith("1.5")) {
                Assert.fail();
            }
            /* Post-test checks */
            InputStream thumbIs = new ByteArrayInputStream(os.toByteArray());
            BufferedImage img = ImageIO.read(thumbIs);
            thumbIs.close();
            Assert.assertEquals("gif", ImageIO.getImageReaders(ImageIO.createImageInputStream(new ByteArrayInputStream(os.toByteArray()))).next().getFormatName());
            Assert.assertEquals(50, img.getWidth());
            Assert.assertEquals(50, img.getHeight());
        } catch (IllegalArgumentException e) {
            // This case should pass on Java 6 and later.
            if (!(System.getProperty("java.version").startsWith("1.5"))) {
                Assert.fail();
            }
            Assert.assertTrue(e.getMessage().contains("gif"));
        }
    }

    /**
     * Test for
     * {@link Thumbnailator#createThumbnail(InputStream, OutputStream, String, int, int)}
     * where,
     *
     * 1) Method arguments are correct
     * 2) There is transcoding taking place:
     *   a) Input file is a BMP image
     *   b) Output file is a PNG image
     *
     * Expected outcome is,
     *
     * 1) Processing will complete successfully.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testCreateThumbnail_IOSII_Transcoding_Bmp_Png() throws IOException {
        /* Actual test */
        byte[] bytes = new byte[40054];
        FileInputStream fis = new FileInputStream("src/test/resources/Thumbnailator/grid.bmp");
        fis.read(bytes);
        fis.close();
        InputStream is = new ByteArrayInputStream(bytes);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Thumbnailator.createThumbnail(is, os, "png", 50, 50);
        /* Post-test checks */
        InputStream thumbIs = new ByteArrayInputStream(os.toByteArray());
        BufferedImage img = ImageIO.read(thumbIs);
        Assert.assertEquals("png", ImageIO.getImageReaders(ImageIO.createImageInputStream(new ByteArrayInputStream(os.toByteArray()))).next().getFormatName());
        Assert.assertEquals(50, img.getWidth());
        Assert.assertEquals(50, img.getHeight());
    }

    /**
     * Test for
     * {@link Thumbnailator#createThumbnail(InputStream, OutputStream, String, int, int)}
     * where,
     *
     * 1) Method arguments are correct
     * 2) There is transcoding taking place:
     *   a) Input file is a BMP image
     *   b) Output file is a JPEG image
     *
     * Expected outcome is,
     *
     * 1) Processing will complete successfully.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testCreateThumbnail_IOSII_Transcoding_Bmp_Jpeg() throws IOException {
        /* Actual test */
        byte[] bytes = new byte[40054];
        FileInputStream fis = new FileInputStream("src/test/resources/Thumbnailator/grid.bmp");
        fis.read(bytes);
        fis.close();
        InputStream is = new ByteArrayInputStream(bytes);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Thumbnailator.createThumbnail(is, os, "jpg", 50, 50);
        /* Post-test checks */
        InputStream thumbIs = new ByteArrayInputStream(os.toByteArray());
        BufferedImage img = ImageIO.read(thumbIs);
        Assert.assertEquals("JPEG", ImageIO.getImageReaders(ImageIO.createImageInputStream(new ByteArrayInputStream(os.toByteArray()))).next().getFormatName());
        Assert.assertEquals(50, img.getWidth());
        Assert.assertEquals(50, img.getHeight());
    }

    /**
     * Test for
     * {@link Thumbnailator#createThumbnail(InputStream, OutputStream, String, int, int)}
     * where,
     *
     * 1) Method arguments are correct
     * 2) There is transcoding taking place:
     *   a) Input file is a BMP image
     *   b) Output file is a GIF image
     *
     * Expected outcome is,
     *
     * 1) Processing will fail with an IllegalArgumentException due to not
     *    being able to write to a GIF.
     *    On Java 6 and later, this should pass, as it contains a GIF writer.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testCreateThumbnail_IOSII_Transcoding_Bmp_Gif() throws IOException {
        /* Actual test */
        byte[] bytes = new byte[40054];
        FileInputStream fis = new FileInputStream("src/test/resources/Thumbnailator/grid.bmp");
        fis.read(bytes);
        fis.close();
        InputStream is = new ByteArrayInputStream(bytes);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            Thumbnailator.createThumbnail(is, os, "gif", 50, 50);
            // This case should pass on Java 6 and later, as those JREs have a
            // GIF writer.
            if (System.getProperty("java.version").startsWith("1.5")) {
                Assert.fail();
            }
            /* Post-test checks */
            InputStream thumbIs = new ByteArrayInputStream(os.toByteArray());
            BufferedImage img = ImageIO.read(thumbIs);
            thumbIs.close();
            Assert.assertEquals("gif", ImageIO.getImageReaders(ImageIO.createImageInputStream(new ByteArrayInputStream(os.toByteArray()))).next().getFormatName());
            Assert.assertEquals(50, img.getWidth());
            Assert.assertEquals(50, img.getHeight());
        } catch (IllegalArgumentException e) {
            // This case should pass on Java 6 and later.
            if (!(System.getProperty("java.version").startsWith("1.5"))) {
                Assert.fail();
            }
            Assert.assertTrue(e.getMessage().contains("gif"));
        }
    }

    /**
     * Test for
     * {@link Thumbnailator#createThumbnail(InputStream, OutputStream, String, int, int)}
     * where,
     *
     * 1) Method arguments are correct
     * 2) There is transcoding taking place:
     *   a) Input file is a GIF image
     *   b) Output file is a PNG image
     *
     * Expected outcome is,
     *
     * 1) Processing will complete successfully.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testCreateThumbnail_IOSII_Transcoding_Gif_Png() throws IOException {
        /* Actual test */
        byte[] bytes = new byte[492];
        FileInputStream fis = new FileInputStream("src/test/resources/Thumbnailator/grid.gif");
        fis.read(bytes);
        fis.close();
        InputStream is = new ByteArrayInputStream(bytes);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Thumbnailator.createThumbnail(is, os, "png", 50, 50);
        /* Post-test checks */
        InputStream thumbIs = new ByteArrayInputStream(os.toByteArray());
        BufferedImage img = ImageIO.read(thumbIs);
        Assert.assertEquals("png", ImageIO.getImageReaders(ImageIO.createImageInputStream(new ByteArrayInputStream(os.toByteArray()))).next().getFormatName());
        Assert.assertEquals(50, img.getWidth());
        Assert.assertEquals(50, img.getHeight());
    }

    /**
     * Test for
     * {@link Thumbnailator#createThumbnail(InputStream, OutputStream, String, int, int)}
     * where,
     *
     * 1) Method arguments are correct
     * 2) There is transcoding taking place:
     *   a) Input file is a GIF image
     *   b) Output file is a JPEG image
     *
     * Expected outcome is,
     *
     * 1) Processing will complete successfully.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testCreateThumbnail_IOSII_Transcoding_Gif_Jpeg() throws IOException {
        /* Actual test */
        byte[] bytes = new byte[492];
        FileInputStream fis = new FileInputStream("src/test/resources/Thumbnailator/grid.gif");
        fis.read(bytes);
        fis.close();
        InputStream is = new ByteArrayInputStream(bytes);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Thumbnailator.createThumbnail(is, os, "jpg", 50, 50);
        /* Post-test checks */
        InputStream thumbIs = new ByteArrayInputStream(os.toByteArray());
        BufferedImage img = ImageIO.read(thumbIs);
        Assert.assertEquals("JPEG", ImageIO.getImageReaders(ImageIO.createImageInputStream(new ByteArrayInputStream(os.toByteArray()))).next().getFormatName());
        Assert.assertEquals(50, img.getWidth());
        Assert.assertEquals(50, img.getHeight());
    }

    /**
     * Test for
     * {@link Thumbnailator#createThumbnail(InputStream, OutputStream, String, int, int)}
     * where,
     *
     * 1) Method arguments are correct
     * 2) There is transcoding taking place:
     *   a) Input file is a GIF image
     *   b) Output file is a BMP image
     *
     * Expected outcome is,
     *
     * 1) Processing will complete successfully.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testCreateThumbnail_IOSII_Transcoding_Gif_Bmp() throws IOException {
        /* Actual test */
        byte[] bytes = new byte[492];
        FileInputStream fis = new FileInputStream("src/test/resources/Thumbnailator/grid.gif");
        fis.read(bytes);
        fis.close();
        InputStream is = new ByteArrayInputStream(bytes);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Thumbnailator.createThumbnail(is, os, "bmp", 50, 50);
        /* Post-test checks */
        InputStream thumbIs = new ByteArrayInputStream(os.toByteArray());
        BufferedImage img = ImageIO.read(thumbIs);
        Assert.assertEquals("bmp", ImageIO.getImageReaders(ImageIO.createImageInputStream(new ByteArrayInputStream(os.toByteArray()))).next().getFormatName());
        Assert.assertEquals(50, img.getWidth());
        Assert.assertEquals(50, img.getHeight());
    }

    /**
     * Test for
     * {@link Thumbnailator#createThumbnail(InputStream, OutputStream, String, int, int)}
     * where,
     *
     * 1) InputStream throws an IOException during read.
     *
     * Expected outcome is,
     *
     * 1) Processing will stop with an IOException.
     *
     * @throws IOException
     * 		
     */
    @Test(expected = IOException.class)
    public void testCreateThumbnail_IOSII_IOExceptionFromIS() throws IOException {
        /* Actual test */
        InputStream is = Mockito.mock(InputStream.class);
        Mockito.doThrow(new IOException("read error!")).when(is).read();
        Mockito.doThrow(new IOException("read error!")).when(is).read(((byte[]) (ArgumentMatchers.any())));
        Mockito.doThrow(new IOException("read error!")).when(is).read(((byte[]) (ArgumentMatchers.any())), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Thumbnailator.createThumbnail(is, os, "png", 50, 50);
        Assert.fail();
    }

    /**
     * Test for
     * {@link Thumbnailator#createThumbnail(InputStream, OutputStream, String, int, int)}
     * where,
     *
     * 1) OutputStream throws an IOException during read.
     *
     * Expected outcome is,
     *
     * 1) Processing will stop with an IOException.
     *
     * @throws IOException
     * 		
     */
    @Test(expected = IOException.class)
    public void testCreateThumbnail_IOSII_IOExceptionFromOS() throws IOException {
        /* Actual test */
        byte[] bytes = makeImageData("png", 200, 200);
        InputStream is = new ByteArrayInputStream(bytes);
        OutputStream os = Mockito.mock(OutputStream.class);
        Mockito.doThrow(new IOException("write error!")).when(os).write(ArgumentMatchers.anyInt());
        Mockito.doThrow(new IOException("write error!")).when(os).write(((byte[]) (ArgumentMatchers.any())));
        Mockito.doThrow(new IOException("write error!")).when(os).write(((byte[]) (ArgumentMatchers.any())), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
        Thumbnailator.createThumbnail(is, os, "png", 50, 50);
        Assert.fail();
    }

    /**
     * Test for
     * {@link Thumbnailator#createThumbnail(File, File, int, int)}
     * where,
     *
     * 1) Input File is null
     *
     * Expected outcome is,
     *
     * 1) Processing will stop with an NullPointerException.
     *
     * @throws IOException
     * 		
     */
    @Test(expected = NullPointerException.class)
    public void testCreateThumbnail_FFII_nullInputFile() throws IOException {
        /* Actual test */
        File inputFile = null;
        File outputFile = new File("bar.jpg");
        Thumbnailator.createThumbnail(inputFile, outputFile, 50, 50);
        Assert.fail();
    }

    /**
     * Test for
     * {@link Thumbnailator#createThumbnail(File, File, int, int)}
     * where,
     *
     * 1) Output File is null
     *
     * Expected outcome is,
     *
     * 1) Processing will stop with an NullPointerException.
     *
     * @throws IOException
     * 		
     */
    @Test(expected = NullPointerException.class)
    public void testCreateThumbnail_FFII_nullOutputFile() throws IOException {
        /* Actual test */
        File inputFile = new File("foo.jpg");
        File outputFile = null;
        Thumbnailator.createThumbnail(inputFile, outputFile, 50, 50);
        Assert.fail();
    }

    /**
     * Test for
     * {@link Thumbnailator#createThumbnail(File, File, int, int)}
     * where,
     *
     * 1) Input File is null
     * 2) Output File is null
     *
     * Expected outcome is,
     *
     * 1) Processing will stop with an NullPointerException.
     *
     * @throws IOException
     * 		
     */
    @Test(expected = NullPointerException.class)
    public void testCreateThumbnail_FFII_nullInputAndOutputFiles() throws IOException {
        Thumbnailator.createThumbnail(((File) (null)), null, 50, 50);
        Assert.fail();
    }

    /**
     * Test for
     * {@link Thumbnailator#createThumbnail(File, File, int, int)}
     * where,
     *
     * 1) Width is negative.
     *
     * Expected outcome is,
     *
     * 1) Processing will stop with an IllegalArgumentException.
     *
     * @throws IOException
     * 		
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCreateThumbnail_FFII_negativeWidth() throws IOException {
        /* Actual test */
        File inputFile = new File("foo.jpg");
        File outputFile = new File("bar.jpg");
        Thumbnailator.createThumbnail(inputFile, outputFile, (-42), 50);
        Assert.fail();
    }

    /**
     * Test for
     * {@link Thumbnailator#createThumbnail(File, File, int, int)}
     * where,
     *
     * 1) Height is negative.
     *
     * Expected outcome is,
     *
     * 1) Processing will stop with an IllegalArgumentException.
     *
     * @throws IOException
     * 		
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCreateThumbnail_FFII_negativeHeight() throws IOException {
        /* Actual test */
        File inputFile = new File("foo.jpg");
        File outputFile = new File("bar.jpg");
        Thumbnailator.createThumbnail(inputFile, outputFile, 50, (-42));
        Assert.fail();
    }

    /**
     * Test for
     * {@link Thumbnailator#createThumbnail(File, File, int, int)}
     * where,
     *
     * 1) Width is negative.
     * 2) Height is negative.
     *
     * Expected outcome is,
     *
     * 1) Processing will stop with an IllegalArgumentException.
     *
     * @throws IOException
     * 		
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCreateThumbnail_FFII_negativeWidthAndHeight() throws IOException {
        /* Actual test */
        File inputFile = new File("foo.jpg");
        File outputFile = new File("bar.jpg");
        Thumbnailator.createThumbnail(inputFile, outputFile, (-42), (-42));
        Assert.fail();
    }

    /**
     * Test for
     * {@link Thumbnailator#createThumbnail(File, File, int, int)}
     * where,
     *
     * 1) Method arguments are correct
     * 2) Input file is a JPEG image
     *
     * Expected outcome is,
     *
     * 1) Processing will complete successfully.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testCreateThumbnail_FFII_Jpg() throws IOException {
        /* Actual test */
        File inputFile = new File("src/test/resources/Thumbnailator/grid.jpg");
        File outputFile = new File("src/test/resources/Thumbnailator/tmp.jpg");
        outputFile.deleteOnExit();
        Thumbnailator.createThumbnail(inputFile, outputFile, 50, 50);
        Assert.assertTrue(outputFile.exists());
        BufferedImage img = ImageIO.read(outputFile);
        Assert.assertEquals(50, img.getWidth());
        Assert.assertEquals(50, img.getHeight());
    }

    /**
     * Test for
     * {@link Thumbnailator#createThumbnail(File, File, int, int)}
     * where,
     *
     * 1) Method arguments are correct
     * 2) Input file is a PNG image
     *
     * Expected outcome is,
     *
     * 1) Processing will complete successfully.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testCreateThumbnail_FFII_Png() throws IOException {
        /* Actual test */
        File inputFile = new File("src/test/resources/Thumbnailator/grid.png");
        File outputFile = new File("src/test/resources/Thumbnailator/tmp.png");
        outputFile.deleteOnExit();
        Thumbnailator.createThumbnail(inputFile, outputFile, 50, 50);
        Assert.assertTrue(outputFile.exists());
        BufferedImage img = ImageIO.read(outputFile);
        Assert.assertEquals(50, img.getWidth());
        Assert.assertEquals(50, img.getHeight());
    }

    /**
     * Test for
     * {@link Thumbnailator#createThumbnail(File, File, int, int)}
     * where,
     *
     * 1) Method arguments are correct
     * 2) Input data is a BMP image
     *
     * Expected outcome is,
     *
     * 1) Processing will complete successfully.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testCreateThumbnail_FFII_Bmp() throws IOException {
        /* Actual test */
        File inputFile = new File("src/test/resources/Thumbnailator/grid.bmp");
        File outputFile = new File("src/test/resources/Thumbnailator/tmp.bmp");
        outputFile.deleteOnExit();
        Thumbnailator.createThumbnail(inputFile, outputFile, 50, 50);
        Assert.assertTrue(outputFile.exists());
        BufferedImage img = ImageIO.read(outputFile);
        Assert.assertEquals(50, img.getWidth());
        Assert.assertEquals(50, img.getHeight());
    }

    /**
     * Test for
     * {@link Thumbnailator#createThumbnail(File, File, int, int)}
     * where,
     *
     * 1) Method arguments are correct
     * 2) Input data is a GIF image
     *
     * Expected outcome is,
     *
     * 1) Processing will stop with an IOException.
     *    On Java 6 and later, this should pass, as it contains a GIF writer.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testCreateThumbnail_FFII_Gif() throws IOException {
        /* Actual test */
        File inputFile = new File("src/test/resources/Thumbnailator/grid.gif");
        File outputFile = new File("src/test/resources/Thumbnailator/tmp.gif");
        outputFile.deleteOnExit();
        try {
            Thumbnailator.createThumbnail(inputFile, outputFile, 50, 50);
            // This case should pass on Java 6 and later, as those JREs have a
            // GIF writer.
            if (System.getProperty("java.version").startsWith("1.5")) {
                Assert.fail();
            }
            /* Post-test checks */
            BufferedImage img = ImageIO.read(outputFile);
            Assert.assertEquals("gif", ImageIO.getImageReaders(ImageIO.createImageInputStream(outputFile)).next().getFormatName());
            Assert.assertEquals(50, img.getWidth());
            Assert.assertEquals(50, img.getHeight());
        } catch (UnsupportedFormatException e) {
            // This case should pass on Java 6 and later.
            if (!(System.getProperty("java.version").startsWith("1.5"))) {
                Assert.fail();
            }
            Assert.assertEquals("No suitable ImageWriter found for gif.", e.getMessage());
            Assert.assertEquals("gif", e.getFormatName());
        }
    }

    /**
     * Test for
     * {@link Thumbnailator#createThumbnail(File, File, int, int)}
     * where,
     *
     * 1) Method arguments are correct
     * 2) There is transcoding taking place:
     *   a) Input file is a JPEG image
     *   b) Output file is a PNG image
     *
     * Expected outcome is,
     *
     * 1) Processing will complete successfully.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testCreateThumbnail_FFII_Transcoding_Jpeg_Png() throws IOException {
        /* Actual test */
        File inputFile = new File("src/test/resources/Thumbnailator/grid.jpg");
        File outputFile = File.createTempFile("thumbnailator-testing-", ".png");
        outputFile.deleteOnExit();
        Thumbnailator.createThumbnail(inputFile, outputFile, 50, 50);
        Assert.assertTrue(outputFile.exists());
        BufferedImage img = ImageIO.read(outputFile);
        Assert.assertEquals("png", ImageIO.getImageReaders(ImageIO.createImageInputStream(outputFile)).next().getFormatName());
        Assert.assertEquals(50, img.getWidth());
        Assert.assertEquals(50, img.getHeight());
    }

    /**
     * Test for
     * {@link Thumbnailator#createThumbnail(File, File, int, int)}
     * where,
     *
     * 1) Method arguments are correct
     * 2) There is transcoding taking place:
     *   a) Input file is a JPEG image
     *   b) Output file is a BMP image
     *
     * Expected outcome is,
     *
     * 1) Processing will complete successfully.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testCreateThumbnail_FFII_Transcoding_Jpeg_Bmp() throws IOException {
        /* Actual test */
        File inputFile = new File("src/test/resources/Thumbnailator/grid.jpg");
        File outputFile = File.createTempFile("thumbnailator-testing-", ".bmp");
        outputFile.deleteOnExit();
        Thumbnailator.createThumbnail(inputFile, outputFile, 50, 50);
        Assert.assertTrue(outputFile.exists());
        BufferedImage img = ImageIO.read(outputFile);
        Assert.assertEquals("bmp", ImageIO.getImageReaders(ImageIO.createImageInputStream(outputFile)).next().getFormatName());
        Assert.assertEquals(50, img.getWidth());
        Assert.assertEquals(50, img.getHeight());
    }

    /**
     * Test for
     * {@link Thumbnailator#createThumbnail(File, File, int, int)}
     * where,
     *
     * 1) Method arguments are correct
     * 2) There is transcoding taking place:
     *   a) Input file is a JPEG image
     *   b) Output file is a GIF image
     *
     * Expected outcome is,
     *
     * 1) Processing will stop with an IOException.
     *    On Java 6 and later, this should pass, as it contains a GIF writer.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testCreateThumbnail_FFII_Transcoding_Jpeg_Gif() throws IOException {
        /* Actual test */
        File inputFile = new File("src/test/resources/Thumbnailator/grid.jpg");
        File outputFile = File.createTempFile("thumbnailator-testing-", ".gif");
        outputFile.deleteOnExit();
        try {
            Thumbnailator.createThumbnail(inputFile, outputFile, 50, 50);
            // This case should pass on Java 6 and later, as those JREs have a
            // GIF writer.
            if (System.getProperty("java.version").startsWith("1.5")) {
                Assert.fail();
            }
            /* Post-test checks */
            BufferedImage img = ImageIO.read(outputFile);
            Assert.assertEquals("gif", ImageIO.getImageReaders(ImageIO.createImageInputStream(outputFile)).next().getFormatName());
            Assert.assertEquals(50, img.getWidth());
            Assert.assertEquals(50, img.getHeight());
        } catch (UnsupportedFormatException e) {
            // This case should pass on Java 6 and later.
            if (!(System.getProperty("java.version").startsWith("1.5"))) {
                Assert.fail();
            }
            Assert.assertEquals("No suitable ImageWriter found for gif.", e.getMessage());
            Assert.assertEquals("gif", e.getFormatName());
        }
    }

    /**
     * Test for
     * {@link Thumbnailator#createThumbnail(File, File, int, int)}
     * where,
     *
     * 1) Method arguments are correct
     * 2) There is transcoding taking place:
     *   a) Input file is a PNG image
     *   b) Output file is a JPEG image
     *
     * Expected outcome is,
     *
     * 1) Processing will complete successfully.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testCreateThumbnail_FFII_Transcoding_Png_Jpeg() throws IOException {
        /* Actual test */
        File inputFile = new File("src/test/resources/Thumbnailator/grid.png");
        File outputFile = File.createTempFile("thumbnailator-testing-", ".jpg");
        outputFile.deleteOnExit();
        Thumbnailator.createThumbnail(inputFile, outputFile, 50, 50);
        Assert.assertTrue(outputFile.exists());
        BufferedImage img = ImageIO.read(outputFile);
        Assert.assertEquals("JPEG", ImageIO.getImageReaders(ImageIO.createImageInputStream(outputFile)).next().getFormatName());
        Assert.assertEquals(50, img.getWidth());
        Assert.assertEquals(50, img.getHeight());
    }

    /**
     * Test for
     * {@link Thumbnailator#createThumbnail(File, File, int, int)}
     * where,
     *
     * 1) Method arguments are correct
     * 2) There is transcoding taking place:
     *   a) Input file is a PNG image
     *   b) Output file is a BMP image
     *
     * Expected outcome is,
     *
     * 1) Processing will complete successfully.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testCreateThumbnail_FFII_Transcoding_Png_Bmp() throws IOException {
        /* Actual test */
        File inputFile = new File("src/test/resources/Thumbnailator/grid.png");
        File outputFile = File.createTempFile("thumbnailator-testing-", ".bmp");
        outputFile.deleteOnExit();
        Thumbnailator.createThumbnail(inputFile, outputFile, 50, 50);
        Assert.assertTrue(outputFile.exists());
        BufferedImage img = ImageIO.read(outputFile);
        Assert.assertEquals("bmp", ImageIO.getImageReaders(ImageIO.createImageInputStream(outputFile)).next().getFormatName());
        Assert.assertEquals(50, img.getWidth());
        Assert.assertEquals(50, img.getHeight());
    }

    /**
     * Test for
     * {@link Thumbnailator#createThumbnail(File, File, int, int)}
     * where,
     *
     * 1) Method arguments are correct
     * 2) There is transcoding taking place:
     *   a) Input file is a PNG image
     *   b) Output file is a GIF image
     *
     * Expected outcome is,
     *
     * 1) Processing will stop with an IOException.
     *    On Java 6 and later, this should pass, as it contains a GIF writer.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testCreateThumbnail_FFII_Transcoding_Png_Gif() throws IOException {
        /* Actual test */
        File inputFile = new File("src/test/resources/Thumbnailator/grid.png");
        File outputFile = File.createTempFile("thumbnailator-testing-", ".gif");
        outputFile.deleteOnExit();
        try {
            Thumbnailator.createThumbnail(inputFile, outputFile, 50, 50);
            // This case should pass on Java 6 and later, as those JREs have a
            // GIF writer.
            if (System.getProperty("java.version").startsWith("1.5")) {
                Assert.fail();
            }
            /* Post-test checks */
            BufferedImage img = ImageIO.read(outputFile);
            Assert.assertEquals("gif", ImageIO.getImageReaders(ImageIO.createImageInputStream(outputFile)).next().getFormatName());
            Assert.assertEquals(50, img.getWidth());
            Assert.assertEquals(50, img.getHeight());
        } catch (UnsupportedFormatException e) {
            // This case should pass on Java 6 and later.
            if (!(System.getProperty("java.version").startsWith("1.5"))) {
                Assert.fail();
            }
            Assert.assertEquals("No suitable ImageWriter found for gif.", e.getMessage());
            Assert.assertEquals("gif", e.getFormatName());
        }
    }

    /**
     * Test for
     * {@link Thumbnailator#createThumbnail(File, File, int, int)}
     * where,
     *
     * 1) Method arguments are correct
     * 2) There is transcoding taking place:
     *   a) Input file is a BMP image
     *   b) Output file is a PNG image
     *
     * Expected outcome is,
     *
     * 1) Processing will complete successfully.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testCreateThumbnail_FFII_Transcoding_Bmp_Png() throws IOException {
        /* Actual test */
        File inputFile = new File("src/test/resources/Thumbnailator/grid.bmp");
        File outputFile = File.createTempFile("thumbnailator-testing-", ".png");
        outputFile.deleteOnExit();
        Thumbnailator.createThumbnail(inputFile, outputFile, 50, 50);
        Assert.assertTrue(outputFile.exists());
        BufferedImage img = ImageIO.read(outputFile);
        Assert.assertEquals("png", ImageIO.getImageReaders(ImageIO.createImageInputStream(outputFile)).next().getFormatName());
        Assert.assertEquals(50, img.getWidth());
        Assert.assertEquals(50, img.getHeight());
    }

    /**
     * Test for
     * {@link Thumbnailator#createThumbnail(File, File, int, int)}
     * where,
     *
     * 1) Method arguments are correct
     * 2) There is transcoding taking place:
     *   a) Input file is a BMP image
     *   b) Output file is a JPEG image
     *
     * Expected outcome is,
     *
     * 1) Processing will complete successfully.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testCreateThumbnail_FFII_Transcoding_Bmp_Jpeg() throws IOException {
        /* Actual test */
        File inputFile = new File("src/test/resources/Thumbnailator/grid.bmp");
        File outputFile = File.createTempFile("thumbnailator-testing-", ".jpg");
        outputFile.deleteOnExit();
        Thumbnailator.createThumbnail(inputFile, outputFile, 50, 50);
        Assert.assertTrue(outputFile.exists());
        BufferedImage img = ImageIO.read(outputFile);
        Assert.assertEquals("JPEG", ImageIO.getImageReaders(ImageIO.createImageInputStream(outputFile)).next().getFormatName());
        Assert.assertEquals(50, img.getWidth());
        Assert.assertEquals(50, img.getHeight());
    }

    /**
     * Test for
     * {@link Thumbnailator#createThumbnail(File, File, int, int)}
     * where,
     *
     * 1) Method arguments are correct
     * 2) There is transcoding taking place:
     *   a) Input file is a BMP image
     *   b) Output file is a GIF image
     *
     * Expected outcome is,
     *
     * 1) Processing will stop with an IOException.
     *    On Java 6 and later, this should pass, as it contains a GIF writer.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testCreateThumbnail_FFII_Transcoding_Bmp_Gif() throws IOException {
        /* Actual test */
        File inputFile = new File("src/test/resources/Thumbnailator/grid.bmp");
        File outputFile = File.createTempFile("thumbnailator-testing-", ".gif");
        outputFile.deleteOnExit();
        try {
            Thumbnailator.createThumbnail(inputFile, outputFile, 50, 50);
            // This case should pass on Java 6 and later, as those JREs have a
            // GIF writer.
            if (System.getProperty("java.version").startsWith("1.5")) {
                Assert.fail();
            }
            /* Post-test checks */
            BufferedImage img = ImageIO.read(outputFile);
            Assert.assertEquals("gif", ImageIO.getImageReaders(ImageIO.createImageInputStream(outputFile)).next().getFormatName());
            Assert.assertEquals(50, img.getWidth());
            Assert.assertEquals(50, img.getHeight());
        } catch (UnsupportedFormatException e) {
            // This case should pass on Java 6 and later.
            if (!(System.getProperty("java.version").startsWith("1.5"))) {
                Assert.fail();
            }
            Assert.assertEquals("No suitable ImageWriter found for gif.", e.getMessage());
            Assert.assertEquals("gif", e.getFormatName());
        }
    }

    /**
     * Test for
     * {@link Thumbnailator#createThumbnail(File, File, int, int)}
     * where,
     *
     * 1) Method arguments are correct
     * 2) There is transcoding taking place:
     *   a) Input file is a GIF image
     *   b) Output file is a PNG image
     *
     * Expected outcome is,
     *
     * 1) Processing will complete successfully.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testCreateThumbnail_FFII_Transcoding_Gif_Png() throws IOException {
        /* Actual test */
        File inputFile = new File("src/test/resources/Thumbnailator/grid.gif");
        File outputFile = File.createTempFile("thumbnailator-testing-", ".png");
        outputFile.deleteOnExit();
        Thumbnailator.createThumbnail(inputFile, outputFile, 50, 50);
        Assert.assertTrue(outputFile.exists());
        BufferedImage img = ImageIO.read(outputFile);
        Assert.assertEquals("png", ImageIO.getImageReaders(ImageIO.createImageInputStream(outputFile)).next().getFormatName());
        Assert.assertEquals(50, img.getWidth());
        Assert.assertEquals(50, img.getHeight());
    }

    /**
     * Test for
     * {@link Thumbnailator#createThumbnail(File, File, int, int)}
     * where,
     *
     * 1) Method arguments are correct
     * 2) There is transcoding taking place:
     *   a) Input file is a GIF image
     *   b) Output file is a JPEG image
     *
     * Expected outcome is,
     *
     * 1) Processing will complete successfully.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testCreateThumbnail_FFII_Transcoding_Gif_Jpeg() throws IOException {
        /* Actual test */
        File inputFile = new File("src/test/resources/Thumbnailator/grid.gif");
        File outputFile = File.createTempFile("thumbnailator-testing-", ".jpg");
        outputFile.deleteOnExit();
        Thumbnailator.createThumbnail(inputFile, outputFile, 50, 50);
        Assert.assertTrue(outputFile.exists());
        BufferedImage img = ImageIO.read(outputFile);
        Assert.assertEquals("JPEG", ImageIO.getImageReaders(ImageIO.createImageInputStream(outputFile)).next().getFormatName());
        Assert.assertEquals(50, img.getWidth());
        Assert.assertEquals(50, img.getHeight());
    }

    /**
     * Test for
     * {@link Thumbnailator#createThumbnail(File, File, int, int)}
     * where,
     *
     * 1) Method arguments are correct
     * 2) There is transcoding taking place:
     *   a) Input file is a GIF image
     *   b) Output file is a BMP image
     *
     * Expected outcome is,
     *
     * 1) Processing will complete successfully.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testCreateThumbnail_FFII_Transcoding_Gif_Bmp() throws IOException {
        /* Actual test */
        File inputFile = new File("src/test/resources/Thumbnailator/grid.gif");
        File outputFile = File.createTempFile("thumbnailator-testing-", ".bmp");
        outputFile.deleteOnExit();
        Thumbnailator.createThumbnail(inputFile, outputFile, 50, 50);
        Assert.assertTrue(outputFile.exists());
        BufferedImage img = ImageIO.read(outputFile);
        Assert.assertEquals("bmp", ImageIO.getImageReaders(ImageIO.createImageInputStream(outputFile)).next().getFormatName());
        Assert.assertEquals(50, img.getWidth());
        Assert.assertEquals(50, img.getHeight());
    }

    /**
     * Test for
     * {@link Thumbnailator#createThumbnail(File, File, int, int)}
     * where,
     *
     * 1) Input File does not exist.
     *
     * Expected outcome is,
     *
     * 1) Processing will stop with an IOException.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testCreateThumbnail_FFII_nonExistentInputFile() throws IOException {
        /* Actual test */
        File inputFile = new File("foo.jpg");
        File outputFile = new File("bar.jpg");
        try {
            Thumbnailator.createThumbnail(inputFile, outputFile, 50, 50);
            Assert.fail();
        } catch (IOException e) {
            Assert.assertEquals("Input file does not exist.", e.getMessage());
        }
    }

    /**
     * Test for
     * {@link Thumbnailator#createThumbnail(File, File, int, int)}
     * where,
     *
     * 1) A filename that is invalid
     *
     * Expected outcome is,
     *
     * 1) Processing will stop with an IOException.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testCreateThumbnail_FFII_invalidOutputFile() throws IOException {
        /* Actual test */
        File inputFile = new File("src/test/resources/Thumbnailator/grid.jpg");
        File outputFile = new File("@\\#?/^%*&/|!!$:#");
        try {
            Thumbnailator.createThumbnail(inputFile, outputFile, 50, 50);
            Assert.fail();
        } catch (IOException e) {
            // An IOException is expected. Likely a FileNotFoundException.
        }
    }

    /**
     * Test for
     * {@link Thumbnailator#createThumbnail(File, int, int)}
     * where,
     *
     * 1) Input File is null
     *
     * Expected outcome is,
     *
     * 1) Processing will stop with an NullPointerException.
     *
     * @throws IOException
     * 		
     */
    @Test(expected = NullPointerException.class)
    public void testCreateThumbnail_FII_nullInputFile() throws IOException {
        Thumbnailator.createThumbnail(((File) (null)), 50, 50);
        Assert.fail();
    }

    /**
     * Test for
     * {@link Thumbnailator#createThumbnail(File, int, int)}
     * where,
     *
     * 1) Width is negative.
     *
     * Expected outcome is,
     *
     * 1) Processing will stop with an IllegalArgumentException.
     *
     * @throws IOException
     * 		
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCreateThumbnail_FII_negativeWidth() throws IOException {
        /* Actual test */
        File inputFile = new File("foo.jpg");
        Thumbnailator.createThumbnail(inputFile, (-42), 50);
        Assert.fail();
    }

    /**
     * Test for
     * {@link Thumbnailator#createThumbnail(File, int, int)}
     * where,
     *
     * 1) Height is negative.
     *
     * Expected outcome is,
     *
     * 1) Processing will stop with an IllegalArgumentException.
     *
     * @throws IOException
     * 		
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCreateThumbnail_FII_negativeHeight() throws IOException {
        /* Actual test */
        File inputFile = new File("foo.jpg");
        Thumbnailator.createThumbnail(inputFile, 50, (-42));
        Assert.fail();
    }

    /**
     * Test for
     * {@link Thumbnailator#createThumbnail(File, int, int)}
     * where,
     *
     * 1) Width is negative.
     * 2) Height is negative.
     *
     * Expected outcome is,
     *
     * 1) Processing will stop with an IllegalArgumentException.
     *
     * @throws IOException
     * 		
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCreateThumbnail_FII_negativeWidthAndHeight() throws IOException {
        /* Actual test */
        File inputFile = new File("foo.jpg");
        Thumbnailator.createThumbnail(inputFile, (-42), (-42));
        Assert.fail();
    }

    /**
     * Test for
     * {@link Thumbnailator#createThumbnail(File, int, int)}
     * where,
     *
     * 1) Method arguments are correct
     * 2) Input file is a JPEG image
     *
     * Expected outcome is,
     *
     * 1) Processing will complete successfully.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testCreateThumbnail_FII_Jpg() throws IOException {
        /* Actual test */
        File inputFile = new File("src/test/resources/Thumbnailator/grid.jpg");
        BufferedImage img = Thumbnailator.createThumbnail(inputFile, 50, 50);
        Assert.assertEquals(50, img.getWidth());
        Assert.assertEquals(50, img.getHeight());
    }

    /**
     * Test for
     * {@link Thumbnailator#createThumbnail(File, int, int)}
     * where,
     *
     * 1) Method arguments are correct
     * 2) Input file is a PNG image
     *
     * Expected outcome is,
     *
     * 1) Processing will complete successfully.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testCreateThumbnail_FII_Png() throws IOException {
        /* Actual test */
        File inputFile = new File("src/test/resources/Thumbnailator/grid.png");
        BufferedImage img = Thumbnailator.createThumbnail(inputFile, 50, 50);
        Assert.assertEquals(50, img.getWidth());
        Assert.assertEquals(50, img.getHeight());
    }

    /**
     * Test for
     * {@link Thumbnailator#createThumbnail(File, int, int)}
     * where,
     *
     * 1) Method arguments are correct
     * 2) Input data is a BMP image
     *
     * Expected outcome is,
     *
     * 1) Processing will complete successfully.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testCreateThumbnail_FII_Bmp() throws IOException {
        /* Actual test */
        File inputFile = new File("src/test/resources/Thumbnailator/grid.bmp");
        BufferedImage img = Thumbnailator.createThumbnail(inputFile, 50, 50);
        Assert.assertEquals(50, img.getWidth());
        Assert.assertEquals(50, img.getHeight());
    }

    /**
     * Test for
     * {@link Thumbnailator#createThumbnail(File, int, int)}
     * where,
     *
     * 1) Method arguments are correct
     * 2) Input data is a GIF image
     *
     * Expected outcome is,
     *
     * 1) Processing will complete successfully.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testCreateThumbnail_FII_Gif() throws IOException {
        /* Actual test */
        File inputFile = new File("src/test/resources/Thumbnailator/grid.gif");
        BufferedImage img = Thumbnailator.createThumbnail(inputFile, 50, 50);
        Assert.assertEquals(50, img.getWidth());
        Assert.assertEquals(50, img.getHeight());
    }

    /**
     * Test for
     * {@link Thumbnailator#createThumbnail(BufferedImage, int, int)}
     * where,
     *
     * 1) Width is negative.
     *
     * Expected outcome is,
     *
     * 1) Processing will stop with an IllegalArgumentException.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCreateThumbnail_BII_negativeWidth() {
        /* Actual test */
        BufferedImage img = new BufferedImageBuilder(200, 200).build();
        Thumbnailator.createThumbnail(img, (-42), 50);
        Assert.fail();
    }

    /**
     * Test for
     * {@link Thumbnailator#createThumbnail(BufferedImage, int, int)}
     * where,
     *
     * 1) Height is negative.
     *
     * Expected outcome is,
     *
     * 1) Processing will stop with an IllegalArgumentException.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCreateThumbnail_BII_negativeHeight() {
        /* Actual test */
        BufferedImage img = new BufferedImageBuilder(200, 200).build();
        Thumbnailator.createThumbnail(img, 50, (-42));
        Assert.fail();
    }

    /**
     * Test for
     * {@link Thumbnailator#createThumbnail(BufferedImage, int, int)}
     * where,
     *
     * 1) Width is negative.
     * 2) Height is negative.
     *
     * Expected outcome is,
     *
     * 1) Processing will stop with an IllegalArgumentException.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCreateThumbnail_BII_negativeWidthAndHeight() {
        /* Actual test */
        BufferedImage img = new BufferedImageBuilder(200, 200).build();
        Thumbnailator.createThumbnail(img, (-42), (-42));
        Assert.fail();
    }

    /**
     * Test for
     * {@link Thumbnailator#createThumbnail(BufferedImage, int, int)}
     * where,
     *
     * 1) Method arguments are correct
     *
     * Expected outcome is,
     *
     * 1) Processing will complete successfully.
     */
    @Test
    public void testCreateThumbnail_BII_CorrectUsage() {
        /* Actual test */
        BufferedImage img = new BufferedImageBuilder(200, 200, BufferedImage.TYPE_INT_ARGB).build();
        BufferedImage thumbnail = Thumbnailator.createThumbnail(img, 50, 50);
        Assert.assertEquals(50, thumbnail.getWidth());
        Assert.assertEquals(50, thumbnail.getHeight());
        Assert.assertEquals(BufferedImage.TYPE_INT_ARGB, thumbnail.getType());
    }

    /**
     * Test for
     * {@link Thumbnailator#createThumbnail(Image, int, int)}
     * where,
     *
     * 1) Width is negative.
     *
     * Expected outcome is,
     *
     * 1) Processing will stop with an IllegalArgumentException.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCreateThumbnail_III_negativeWidth() {
        /* Actual test */
        BufferedImage img = new BufferedImageBuilder(200, 200).build();
        Thumbnailator.createThumbnail(((Image) (img)), (-42), 50);
        Assert.fail();
    }

    /**
     * Test for
     * {@link Thumbnailator#createThumbnail(Image, int, int)}
     * where,
     *
     * 1) Height is negative.
     *
     * Expected outcome is,
     *
     * 1) Processing will stop with an IllegalArgumentException.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCreateThumbnail_III_negativeHeight() {
        /* Actual test */
        BufferedImage img = new BufferedImageBuilder(200, 200).build();
        Thumbnailator.createThumbnail(((Image) (img)), 50, (-42));
        Assert.fail();
    }

    /**
     * Test for
     * {@link Thumbnailator#createThumbnail(Image, int, int)}
     * where,
     *
     * 1) Width is negative.
     * 2) Height is negative.
     *
     * Expected outcome is,
     *
     * 1) Processing will stop with an IllegalArgumentException.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCreateThumbnail_III_negativeWidthAndHeight() {
        /* Actual test */
        BufferedImage img = new BufferedImageBuilder(200, 200).build();
        Thumbnailator.createThumbnail(((Image) (img)), (-42), (-42));
        Assert.fail();
    }

    /**
     * Test for
     * {@link Thumbnailator#createThumbnail(Image, int, int)}
     * where,
     *
     * 1) Method arguments are correct
     *
     * Expected outcome is,
     *
     * 1) Processing will complete successfully.
     */
    @Test
    public void testCreateThumbnail_III_CorrectUsage() {
        /* Actual test */
        BufferedImage img = new BufferedImageBuilder(200, 200, BufferedImage.TYPE_INT_ARGB).build();
        Image thumbnail = Thumbnailator.createThumbnail(((Image) (img)), 50, 50);
        Assert.assertEquals(50, thumbnail.getWidth(null));
        Assert.assertEquals(50, thumbnail.getHeight(null));
    }

    /**
     * Test for
     * {@link Thumbnailator#createThumbnail(net.coobird.thumbnailator.tasks.ThumbnailTask)}
     * where,
     *
     * 1) The correct parameters are given.
     * 2) The size is specified for the ThumbnailParameter.
     *
     * Expected outcome is,
     *
     * 1) The ResizerFactory is being used.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testCreateThumbnail_ThumbnailTask_ResizerFactoryBeingUsed_UsingSize() throws IOException {
        // given
        BufferedImageSource source = new BufferedImageSource(new BufferedImageBuilder(200, 200, BufferedImage.TYPE_INT_ARGB).build());
        BufferedImageSink sink = new BufferedImageSink();
        ResizerFactory resizerFactory = Mockito.spy(DefaultResizerFactory.getInstance());
        ThumbnailParameter param = new net.coobird.thumbnailator.builders.ThumbnailParameterBuilder().size(100, 100).resizerFactory(resizerFactory).build();
        // when
        Thumbnailator.createThumbnail(new net.coobird.thumbnailator.tasks.SourceSinkThumbnailTask<BufferedImage, BufferedImage>(param, source, sink));
        // then
        Mockito.verify(resizerFactory).getResizer(new Dimension(200, 200), new Dimension(100, 100));
    }

    /**
     * Test for
     * {@link Thumbnailator#createThumbnail(net.coobird.thumbnailator.tasks.ThumbnailTask)}
     * where,
     *
     * 1) The correct parameters are given.
     * 2) The scale is specified for the ThumbnailParameter.
     *
     * Expected outcome is,
     *
     * 1) The ResizerFactory is being used.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testCreateThumbnail_ThumbnailTask_ResizerFactoryBeingUsed_UsingScale() throws IOException {
        // given
        BufferedImageSource source = new BufferedImageSource(new BufferedImageBuilder(200, 200, BufferedImage.TYPE_INT_ARGB).build());
        BufferedImageSink sink = new BufferedImageSink();
        ResizerFactory resizerFactory = Mockito.spy(DefaultResizerFactory.getInstance());
        ThumbnailParameter param = new net.coobird.thumbnailator.builders.ThumbnailParameterBuilder().scale(0.5).resizerFactory(resizerFactory).build();
        // when
        Thumbnailator.createThumbnail(new net.coobird.thumbnailator.tasks.SourceSinkThumbnailTask<BufferedImage, BufferedImage>(param, source, sink));
        // then
        Mockito.verify(resizerFactory).getResizer(new Dimension(200, 200), new Dimension(100, 100));
    }

    @Test
    public void renameGivenThumbnailParameter_createThumbnails() throws IOException {
        // given
        Rename rename = Mockito.mock(Rename.class);
        Mockito.when(rename.apply(ArgumentMatchers.anyString(), ArgumentMatchers.any(ThumbnailParameter.class))).thenReturn("thumbnail.grid.png");
        File f = new File("src/test/resources/Thumbnailator/grid.png");
        // when
        Thumbnailator.createThumbnails(Arrays.asList(f), rename, 50, 50);
        // then
        ArgumentCaptor<ThumbnailParameter> ac = ArgumentCaptor.forClass(ThumbnailParameter.class);
        Mockito.verify(rename).apply(ArgumentMatchers.eq(f.getName()), ac.capture());
        Assert.assertEquals(new Dimension(50, 50), ac.getValue().getSize());
        // clean up
        new File("src/test/resources/Thumbnailator/thumbnail.grid.png").deleteOnExit();
    }

    @Test
    public void renameGivenThumbnailParameter_createThumbnailsAsCollection() throws IOException {
        // given
        Rename rename = Mockito.mock(Rename.class);
        Mockito.when(rename.apply(ArgumentMatchers.anyString(), ArgumentMatchers.any(ThumbnailParameter.class))).thenReturn("thumbnail.grid.png");
        File f = new File("src/test/resources/Thumbnailator/grid.png");
        // when
        Thumbnailator.createThumbnailsAsCollection(Arrays.asList(f), rename, 50, 50);
        // then
        ArgumentCaptor<ThumbnailParameter> ac = ArgumentCaptor.forClass(ThumbnailParameter.class);
        Mockito.verify(rename).apply(ArgumentMatchers.eq(f.getName()), ac.capture());
        Assert.assertEquals(new Dimension(50, 50), ac.getValue().getSize());
        // clean up
        new File("src/test/resources/Thumbnailator/thumbnail.grid.png").deleteOnExit();
    }
}

