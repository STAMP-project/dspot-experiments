package com.bumptech.glide.load.resource.bitmap;


import DefaultImageHeaderParser.EXIF_SEGMENT_TYPE;
import DefaultImageHeaderParser.JPEG_EXIF_SEGMENT_PREAMBLE_BYTES;
import DefaultImageHeaderParser.SEGMENT_START_ID;
import ImageHeaderParser.UNKNOWN_ORIENTATION;
import ImageType.GIF;
import ImageType.JPEG;
import ImageType.PNG;
import ImageType.PNG_A;
import ImageType.UNKNOWN;
import ImageType.WEBP;
import ImageType.WEBP_A;
import android.support.annotation.NonNull;
import com.bumptech.glide.load.engine.bitmap_recycle.ArrayPool;
import com.bumptech.glide.testutil.TestResourceUtil;
import java.io.ByteArrayInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.util.Util;

import static DefaultImageHeaderParser.EXIF_SEGMENT_TYPE;
import static DefaultImageHeaderParser.SEGMENT_START_ID;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, sdk = 18)
public class DefaultImageHeaderParserTest {
    private static final byte[] PNG_HEADER_WITH_IHDR_CHUNK = new byte[]{ ((byte) (137)), 80, 78, 71, 13, 10, 26, 10, 0, 0, 0, 13, 73, 72, 68, 82, 0, 0, 1, ((byte) (144)), 0, 0, 1, 44, 8, 6 };

    private ArrayPool byteArrayPool;

    @Test
    public void testCanParsePngType() throws IOException {
        // PNG magic number from: http://en.wikipedia.org/wiki/Portable_Network_Graphics.
        byte[] data = new byte[]{ ((byte) (137)), 80, 78, 71, 13, 10, 26, 10 };
        DefaultImageHeaderParserTest.runTest(data, new DefaultImageHeaderParserTest.ParserTestCase() {
            @Override
            public void run(DefaultImageHeaderParser parser, InputStream is, ArrayPool byteArrayPool) throws IOException {
                Assert.assertEquals(PNG, parser.getType(is));
            }

            @Override
            public void run(DefaultImageHeaderParser parser, ByteBuffer byteBuffer, ArrayPool byteArrayPool) throws IOException {
                Assert.assertEquals(PNG, parser.getType(byteBuffer));
            }
        });
    }

    @Test
    public void testCanParsePngWithAlpha() throws IOException {
        for (int i = 3; i <= 6; i++) {
            byte[] pngHeaderWithIhdrChunk = DefaultImageHeaderParserTest.generatePngHeaderWithIhdr(i);
            DefaultImageHeaderParserTest.runTest(pngHeaderWithIhdrChunk, new DefaultImageHeaderParserTest.ParserTestCase() {
                @Override
                public void run(DefaultImageHeaderParser parser, InputStream is, ArrayPool byteArrayPool) throws IOException {
                    Assert.assertEquals(PNG_A, parser.getType(is));
                }

                @Override
                public void run(DefaultImageHeaderParser parser, ByteBuffer byteBuffer, ArrayPool byteArrayPool) throws IOException {
                    Assert.assertEquals(PNG_A, parser.getType(byteBuffer));
                }
            });
        }
    }

    @Test
    public void testCanParsePngWithoutAlpha() throws IOException {
        for (int i = 0; i < 3; i++) {
            byte[] pngHeaderWithIhdrChunk = DefaultImageHeaderParserTest.generatePngHeaderWithIhdr(i);
            DefaultImageHeaderParserTest.runTest(pngHeaderWithIhdrChunk, new DefaultImageHeaderParserTest.ParserTestCase() {
                @Override
                public void run(DefaultImageHeaderParser parser, InputStream is, ArrayPool byteArrayPool) throws IOException {
                    Assert.assertEquals(PNG, parser.getType(is));
                }

                @Override
                public void run(DefaultImageHeaderParser parser, ByteBuffer byteBuffer, ArrayPool byteArrayPool) throws IOException {
                    Assert.assertEquals(PNG, parser.getType(byteBuffer));
                }
            });
        }
    }

    @Test
    public void testCanParseJpegType() throws IOException {
        byte[] data = new byte[]{ ((byte) (255)), ((byte) (216)) };
        DefaultImageHeaderParserTest.runTest(data, new DefaultImageHeaderParserTest.ParserTestCase() {
            @Override
            public void run(DefaultImageHeaderParser parser, InputStream is, ArrayPool byteArrayPool) throws IOException {
                Assert.assertEquals(JPEG, parser.getType(is));
            }

            @Override
            public void run(DefaultImageHeaderParser parser, ByteBuffer byteBuffer, ArrayPool byteArrayPool) throws IOException {
                Assert.assertEquals(JPEG, parser.getType(byteBuffer));
            }
        });
    }

    @Test
    public void testCanParseGifType() throws IOException {
        byte[] data = new byte[]{ 'G', 'I', 'F' };
        DefaultImageHeaderParserTest.runTest(data, new DefaultImageHeaderParserTest.ParserTestCase() {
            @Override
            public void run(DefaultImageHeaderParser parser, InputStream is, ArrayPool byteArrayPool) throws IOException {
                Assert.assertEquals(GIF, parser.getType(is));
            }

            @Override
            public void run(DefaultImageHeaderParser parser, ByteBuffer byteBuffer, ArrayPool byteArrayPool) throws IOException {
                Assert.assertEquals(GIF, parser.getType(byteBuffer));
            }
        });
    }

    @Test
    public void testCanParseWebpWithAlpha() throws IOException {
        byte[] data = new byte[]{ 82, 73, 70, 70, 60, 80, 0, 0, 87, 69, 66, 80, 86, 80, 56, 76, 48, 80, 0, 0, 47, ((byte) (239)), ((byte) (128)), 21, 16, ((byte) (141)), 48, 104, 27, ((byte) (201)), ((byte) (145)), ((byte) (178)) };
        DefaultImageHeaderParserTest.runTest(data, new DefaultImageHeaderParserTest.ParserTestCase() {
            @Override
            public void run(DefaultImageHeaderParser parser, InputStream is, ArrayPool byteArrayPool) throws IOException {
                Assert.assertEquals(WEBP_A, parser.getType(is));
            }

            @Override
            public void run(DefaultImageHeaderParser parser, ByteBuffer byteBuffer, ArrayPool byteArrayPool) throws IOException {
                Assert.assertEquals(WEBP_A, parser.getType(byteBuffer));
            }
        });
    }

    @Test
    public void testCanParseWebpWithoutAlpha() throws IOException {
        byte[] data = new byte[]{ 82, 73, 70, 70, 114, 28, 0, 0, 87, 69, 66, 80, 86, 80, 56, 32, 102, 28, 0, 0, 48, 60, 1, ((byte) (157)), 1, 42, 82, 2, ((byte) (148)), 3, 0, ((byte) (199)) };
        DefaultImageHeaderParserTest.runTest(data, new DefaultImageHeaderParserTest.ParserTestCase() {
            @Override
            public void run(DefaultImageHeaderParser parser, InputStream is, ArrayPool byteArrayPool) throws IOException {
                Assert.assertEquals(WEBP, parser.getType(is));
            }

            @Override
            public void run(DefaultImageHeaderParser parser, ByteBuffer byteBuffer, ArrayPool byteArrayPool) throws IOException {
                Assert.assertEquals(WEBP, parser.getType(byteBuffer));
            }
        });
    }

    @Test
    public void testReturnsUnknownTypeForUnknownImageHeaders() throws IOException {
        byte[] data = new byte[]{ 0, 0, 0, 0, 0, 0, 0, 0 };
        DefaultImageHeaderParserTest.runTest(data, new DefaultImageHeaderParserTest.ParserTestCase() {
            @Override
            public void run(DefaultImageHeaderParser parser, InputStream is, ArrayPool byteArrayPool) throws IOException {
                Assert.assertEquals(UNKNOWN, parser.getType(is));
            }

            @Override
            public void run(DefaultImageHeaderParser parser, ByteBuffer byteBuffer, ArrayPool byteArrayPool) throws IOException {
                Assert.assertEquals(UNKNOWN, parser.getType(byteBuffer));
            }
        });
    }

    // Test for #286.
    @Test
    public void testHandlesParsingOrientationWithMinimalExifSegment() throws IOException {
        byte[] data = Util.readBytes(TestResourceUtil.openResource(getClass(), "short_exif_sample.jpg"));
        DefaultImageHeaderParserTest.runTest(data, new DefaultImageHeaderParserTest.ParserTestCase() {
            @Override
            public void run(DefaultImageHeaderParser parser, InputStream is, ArrayPool byteArrayPool) throws IOException {
                Assert.assertEquals((-1), parser.getOrientation(is, byteArrayPool));
            }

            @Override
            public void run(DefaultImageHeaderParser parser, ByteBuffer byteBuffer, ArrayPool byteArrayPool) throws IOException {
                Assert.assertEquals((-1), parser.getOrientation(byteBuffer, byteArrayPool));
            }
        });
    }

    @Test
    public void testReturnsUnknownForEmptyData() throws IOException {
        DefaultImageHeaderParserTest.runTest(new byte[0], new DefaultImageHeaderParserTest.ParserTestCase() {
            @Override
            public void run(DefaultImageHeaderParser parser, InputStream is, ArrayPool byteArrayPool) throws IOException {
                Assert.assertEquals(UNKNOWN, parser.getType(is));
            }

            @Override
            public void run(DefaultImageHeaderParser parser, ByteBuffer byteBuffer, ArrayPool byteArrayPool) throws IOException {
                Assert.assertEquals(UNKNOWN, parser.getType(byteBuffer));
            }
        });
    }

    // Test for #387.
    @Test
    public void testHandlesPartialReads() throws IOException {
        InputStream is = TestResourceUtil.openResource(getClass(), "issue387_rotated_jpeg.jpg");
        DefaultImageHeaderParser parser = new DefaultImageHeaderParser();
        assertThat(parser.getOrientation(new DefaultImageHeaderParserTest.PartialReadInputStream(is), byteArrayPool)).isEqualTo(6);
    }

    // Test for #387.
    @Test
    public void testHandlesPartialSkips() throws IOException {
        InputStream is = TestResourceUtil.openResource(getClass(), "issue387_rotated_jpeg.jpg");
        DefaultImageHeaderParser parser = new DefaultImageHeaderParser();
        assertThat(parser.getOrientation(new DefaultImageHeaderParserTest.PartialSkipInputStream(is), byteArrayPool)).isEqualTo(6);
    }

    @Test
    public void testHandlesSometimesZeroSkips() throws IOException {
        InputStream is = new ByteArrayInputStream(new byte[]{ ((byte) (137)), 80, 78, 71, 13, 10, 26, 10 });
        DefaultImageHeaderParser parser = new DefaultImageHeaderParser();
        Assert.assertEquals(PNG, parser.getType(new DefaultImageHeaderParserTest.SometimesZeroSkipInputStream(is)));
    }

    @Test
    public void getOrientation_withExifSegmentLessThanLength_returnsUnknown() throws IOException {
        ByteBuffer jpegHeaderBytes = DefaultImageHeaderParserTest.getExifMagicNumber();
        byte[] data = new byte[]{ jpegHeaderBytes.get(0), jpegHeaderBytes.get(1), ((byte) (SEGMENT_START_ID)), ((byte) (EXIF_SEGMENT_TYPE)), // SEGMENT_LENGTH
        ((byte) (255)), ((byte) (255)) };
        ByteBuffer byteBuffer = ByteBuffer.wrap(data);
        DefaultImageHeaderParser parser = new DefaultImageHeaderParser();
        Assert.assertEquals(UNKNOWN_ORIENTATION, parser.getOrientation(byteBuffer, byteArrayPool));
    }

    @Test
    public void getOrientation_withNonExifSegmentLessThanLength_returnsUnknown() throws IOException {
        ByteBuffer jpegHeaderBytes = DefaultImageHeaderParserTest.getExifMagicNumber();
        byte[] data = new byte[]{ jpegHeaderBytes.get(0), jpegHeaderBytes.get(1), ((byte) (SEGMENT_START_ID)), // SEGMENT_TYPE (NOT EXIF_SEGMENT_TYPE)
        ((byte) (229)), // SEGMENT_LENGTH
        ((byte) (255)), ((byte) (255)) };
        ByteBuffer byteBuffer = ByteBuffer.wrap(data);
        DefaultImageHeaderParser parser = new DefaultImageHeaderParser();
        Assert.assertEquals(UNKNOWN_ORIENTATION, parser.getOrientation(byteBuffer, byteArrayPool));
    }

    @Test
    public void getOrientation_withExifSegmentAndPreambleButLessThanLength_returnsUnknown() throws IOException {
        ByteBuffer jpegHeaderBytes = DefaultImageHeaderParserTest.getExifMagicNumber();
        ByteBuffer exifSegmentPreamble = ByteBuffer.wrap(JPEG_EXIF_SEGMENT_PREAMBLE_BYTES);
        ByteBuffer data = ByteBuffer.allocate(((((2 + 1) + 1) + 2) + (exifSegmentPreamble.capacity())));
        // SEGMENT_LENGTH, add two because length includes the segment length short, and one to go
        // beyond the preamble bytes length for the test.
        data.put(jpegHeaderBytes).put(((byte) (SEGMENT_START_ID))).put(((byte) (EXIF_SEGMENT_TYPE))).putShort(((short) (((JPEG_EXIF_SEGMENT_PREAMBLE_BYTES.length) + 2) + 1))).put(JPEG_EXIF_SEGMENT_PREAMBLE_BYTES);
        data.position(0);
        DefaultImageHeaderParser parser = new DefaultImageHeaderParser();
        Assert.assertEquals(UNKNOWN_ORIENTATION, parser.getOrientation(data, byteArrayPool));
    }

    @Test
    public void getOrientation_withExifSegmentAndPreambleBetweenLengthAndExpected_returnsUnknown() throws IOException {
        ByteBuffer jpegHeaderBytes = DefaultImageHeaderParserTest.getExifMagicNumber();
        ByteBuffer exifSegmentPreamble = ByteBuffer.wrap(JPEG_EXIF_SEGMENT_PREAMBLE_BYTES);
        ByteBuffer data = ByteBuffer.allocate(((((((2 + 1) + 1) + 2) + (exifSegmentPreamble.capacity())) + 2) + 1));
        // SEGMENT_LENGTH, add two because length includes the segment length short, and one to go
        // beyond the preamble bytes length for the test.
        data.put(jpegHeaderBytes).put(((byte) (SEGMENT_START_ID))).put(((byte) (EXIF_SEGMENT_TYPE))).putShort(((short) (((JPEG_EXIF_SEGMENT_PREAMBLE_BYTES.length) + 2) + 1))).put(JPEG_EXIF_SEGMENT_PREAMBLE_BYTES);
        data.position(0);
        DefaultImageHeaderParser parser = new DefaultImageHeaderParser();
        Assert.assertEquals(UNKNOWN_ORIENTATION, parser.getOrientation(data, byteArrayPool));
    }

    private interface ParserTestCase {
        void run(DefaultImageHeaderParser parser, InputStream is, ArrayPool byteArrayPool) throws IOException;

        void run(DefaultImageHeaderParser parser, ByteBuffer byteBuffer, ArrayPool byteArrayPool) throws IOException;
    }

    private static class SometimesZeroSkipInputStream extends FilterInputStream {
        boolean returnZeroFlag = true;

        SometimesZeroSkipInputStream(InputStream in) {
            super(in);
        }

        @Override
        public long skip(long byteCount) throws IOException {
            final long result;
            if (returnZeroFlag) {
                result = 0;
            } else {
                result = super.skip(byteCount);
            }
            returnZeroFlag = !(returnZeroFlag);
            return result;
        }
    }

    private static class PartialSkipInputStream extends FilterInputStream {
        PartialSkipInputStream(InputStream in) {
            super(in);
        }

        @Override
        public long skip(long byteCount) throws IOException {
            long toActuallySkip = byteCount / 2;
            if (byteCount == 1) {
                toActuallySkip = 1;
            }
            return super.skip(toActuallySkip);
        }
    }

    private static class PartialReadInputStream extends FilterInputStream {
        PartialReadInputStream(InputStream in) {
            super(in);
        }

        @Override
        public int read(@NonNull
        byte[] buffer, int byteOffset, int byteCount) throws IOException {
            int toActuallyRead = byteCount / 2;
            if (byteCount == 1) {
                toActuallyRead = 1;
            }
            return super.read(buffer, byteOffset, toActuallyRead);
        }
    }
}

