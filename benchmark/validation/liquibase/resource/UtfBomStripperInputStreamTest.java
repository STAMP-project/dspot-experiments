package liquibase.resource;


import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Vity
 */
public class UtfBomStripperInputStreamTest {
    @Test
    public void testUtf8() throws IOException {
        final UtfBomStripperInputStream is = prepare(239, 187, 191, 97, 98, 99);
        Assert.assertEquals("UTF-8", is.getDetectedCharsetName());
        assertData(is);
        is.close();
    }

    @Test
    public void testNoBOM() throws IOException {
        final UtfBomStripperInputStream is = prepare(97, 98, 99);
        Assert.assertNull(is.getDetectedCharsetName());
        assertData(is);
        is.close();
    }
}

