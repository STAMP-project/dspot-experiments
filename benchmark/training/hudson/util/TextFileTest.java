package hudson.util;


import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class TextFileTest {
    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();

    @Test
    public void head() throws Exception {
        File f = tmp.newFile();
        FileUtils.copyURLToFile(getClass().getResource("ascii.txt"), f);
        TextFile t = new TextFile(f);
        String first35 = "Lorem ipsum dolor sit amet, consect";
        Assert.assertEquals(35, first35.length());
        Assert.assertEquals(first35, t.head(35));
    }

    @Test
    public void shortHead() throws Exception {
        File f = tmp.newFile();
        FileUtils.write(f, "hello");
        TextFile t = new TextFile(f);
        Assert.assertEquals("hello", t.head(35));
    }

    @Test
    public void tail() throws Exception {
        File f = tmp.newFile();
        FileUtils.copyURLToFile(getClass().getResource("ascii.txt"), f);
        String whole = FileUtils.readFileToString(f);
        TextFile t = new TextFile(f);
        String tailStr = whole.substring(((whole.length()) - 34));
        Assert.assertEquals(tailStr, t.fastTail(tailStr.length()));
    }

    @Test
    public void shortTail() throws Exception {
        File f = tmp.newFile();
        FileUtils.write(f, "hello");
        TextFile t = new TextFile(f);
        Assert.assertEquals("hello", t.fastTail(35));
    }

    /**
     * Shift JIS is a multi-byte character encoding.
     *
     * In it, 0x82 0x83 is \u30e2, and 0x83 0x82 is \uFF43. So if aren't
     * careful, we'll parse the text incorrectly.
     */
    @Test
    public void tailShiftJIS() throws Exception {
        File f = tmp.newFile();
        TextFile t = new TextFile(f);
        try (OutputStream o = new FileOutputStream(f)) {
            for (int i = 0; i < 80; i++) {
                for (int j = 0; j < 40; j++) {
                    o.write(131);
                    o.write(130);
                }
                o.write(10);
            }
        }
        String tail = t.fastTail(35, Charset.forName("Shift_JIS"));
        Assert.assertEquals(((StringUtils.repeat("\u30e2", 34)) + "\n"), tail);
        Assert.assertEquals(35, tail.length());
        // add one more byte to force fastTail to read from one byte ahead
        // between this and the previous case, it should start parsing text incorrectly, until it hits NL
        // where it comes back in sync
        try (OutputStream o = new FileOutputStream(f, true)) {
            o.write(10);
        }
        tail = t.fastTail(35, Charset.forName("Shift_JIS"));
        Assert.assertEquals(((StringUtils.repeat("\u30e2", 33)) + "\n\n"), tail);
        Assert.assertEquals(35, tail.length());
    }
}

