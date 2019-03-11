package liquibase.resource;


import java.io.IOException;
import org.junit.Test;


public class UtfBomAwareReaderTest {
    UtfBomAwareReader reader;

    @Test
    public void testEmpty() throws IOException {
        prepare();
        assertEncoding("ISO-8859-1");
    }

    @Test
    public void testNoBom() throws IOException {
        prepare(97, 98, 99);
        assertEncoding("ISO-8859-1");
    }

    @Test
    public void testUtf8Empty() throws IOException {
        prepare(239, 187, 191);
        assertEncoding("UTF-8");
        assertEmpty();
    }

    @Test
    public void testEmptyUtf8WithFourBytesOnly() throws IOException {
        prepare(239, 187, 191, 97);
        assertEncoding("UTF-8");
    }

    @Test
    public void testUtf8() throws IOException {
        prepare(239, 187, 191, 97, 98, 99);
        assertEncoding("UTF-8");
        assertData();
    }

    @Test
    public void testUtf16BEEmpty() throws IOException {
        prepare(254, 255);
        assertEncoding("UTF-16BE");
        assertEmpty();
    }

    @Test
    public void testEmptyUtf16BEWithFourBytesOnly() throws IOException {
        prepare(254, 255, 0, 97);
        assertEncoding("UTF-16BE");
    }

    @Test
    public void testUtf16BE() throws IOException {
        prepare(254, 255, 0, 97, 0, 98, 0, 99);
        assertEncoding("UTF-16BE");
        assertData();
    }

    @Test
    public void testUtf16LEEmpty() throws IOException {
        prepare(255, 254);
        assertEncoding("UTF-16LE");
        assertEmpty();
    }

    @Test
    public void testEmptyUtf16LEWithFourBytesOnly() throws IOException {
        prepare(255, 254, 97, 0);
        assertEncoding("UTF-16LE");
    }

    @Test
    public void testUtf16LE() throws IOException {
        prepare(255, 254, 97, 0, 98, 0, 99, 0);
        assertEncoding("UTF-16LE");
        assertData();
    }

    @Test
    public void testUtf32LEEmpty() throws IOException {
        prepare(255, 254, 0, 0);
        assertEncoding("UTF-32LE");
    }

    @Test
    public void testUtf32LE() throws IOException {
        /*  */
        prepare(255, 254, 0, 0, 97, 0, 0, 0, 98, 0, 0, 0, 99, 0, 0, 0);
        assertEncoding("UTF-32LE");
        assertData();
    }

    @Test
    public void testUtf32BEEmpty() throws IOException {
        prepare(0, 0, 254, 255);
        assertEncoding("UTF-32BE");
        assertEmpty();
    }

    @Test
    public void testUtf32BE() throws IOException {
        prepare(0, 0, 254, 255, 0, 0, 0, 97, 0, 0, 0, 98, 0, 0, 0, 99);
        assertEncoding("UTF-32BE");
        assertData();
    }

    @Test
    public void testWithNoDefault() throws IOException {
        reader = new UtfBomAwareReader(prepareStream(0, 0, 254, 255, 0, 0, 0, 97, 0, 0, 0, 98, 0, 0, 0, 99));
        assertEncoding("UTF-32BE", "UTF-8");
        assertData();
    }
}

