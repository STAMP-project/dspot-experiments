package azkaban.viewer.hdfs;


import Capability.READ;
import ContentType.HTML;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Set;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.permission.AccessControlException;
import org.junit.Assert;
import org.junit.Test;


/**
 * <pre>
 * Test cases for HtmlFileViewer
 *
 * Validate accepted capabilities for the viewer and ensures that it
 * generates the correct content and content type.
 * </pre>
 */
public class HtmlFileViewerTest {
    private static final String EMPTY_HTM = "TestHtmEmptyFile.htm";

    private static final String VALID_HTML = "TestHtmlFile.html";

    FileSystem fs;

    HtmlFileViewer viewer;

    @Test
    public void testCapabilities() throws AccessControlException {
        Set<Capability> capabilities = this.viewer.getCapabilities(this.fs, getResourcePath(HtmlFileViewerTest.EMPTY_HTM));
        // READ should be the the one and only capability
        Assert.assertTrue(capabilities.contains(READ));
        Assert.assertEquals(capabilities.size(), 1);
        capabilities = this.viewer.getCapabilities(this.fs, getResourcePath(HtmlFileViewerTest.VALID_HTML));
        // READ should be the the one and only capability
        Assert.assertTrue(capabilities.contains(READ));
        Assert.assertEquals(capabilities.size(), 1);
    }

    @Test
    public void testContentType() {
        Assert.assertEquals(HTML, this.viewer.getContentType());
    }

    @Test
    public void testEmptyFile() throws IOException {
        final ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        this.viewer.displayFile(this.fs, getResourcePath(HtmlFileViewerTest.EMPTY_HTM), outStream, 1, 2);
        final String output = outStream.toString();
        Assert.assertTrue(output.isEmpty());
    }

    @Test
    @SuppressWarnings("DefaultCharset")
    public void testValidHtmlFile() throws IOException {
        final ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        this.viewer.displayFile(this.fs, getResourcePath(HtmlFileViewerTest.VALID_HTML), outStream, 1, 2);
        final String output = new String(outStream.toByteArray());
        Assert.assertEquals(output, "<p>file content</p>\n");
    }
}

