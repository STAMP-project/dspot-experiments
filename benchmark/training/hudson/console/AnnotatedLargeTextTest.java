/**
 * The MIT License
 *
 * Copyright 2016 CloudBees, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package hudson.console;


import hudson.MarkupText;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringWriter;
import java.util.logging.Level;
import org.apache.commons.io.Charsets;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.For;
import org.jvnet.hudson.test.Issue;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.LoggerRule;
import org.kohsuke.stapler.framework.io.ByteBuffer;

import static ConsoleNote.INSECURE;
import static ConsoleNote.POSTAMBLE_STR;
import static ConsoleNote.PREAMBLE_STR;


@For({ AnnotatedLargeText.class, ConsoleNote.class, ConsoleAnnotationOutputStream.class, PlainTextConsoleOutputStream.class })
public class AnnotatedLargeTextTest {
    @ClassRule
    public static JenkinsRule r = new JenkinsRule();

    @Rule
    public LoggerRule logging = new LoggerRule().record(ConsoleAnnotationOutputStream.class, Level.FINE).capture(100);

    @Test
    public void smokes() throws Exception {
        ByteBuffer buf = new ByteBuffer();
        PrintStream ps = new PrintStream(buf, true);
        ps.print("Some text.\n");
        ps.print((("Go back to " + (AnnotatedLargeTextTest.TestNote.encodeTo("/root", "your home"))) + ".\n"));
        ps.print("More text.\n");
        AnnotatedLargeText<Void> text = new AnnotatedLargeText(buf, Charsets.UTF_8, true, null);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        text.writeLogTo(0, baos);
        Assert.assertEquals("Some text.\nGo back to your home.\nMore text.\n", baos.toString());
        StringWriter w = new StringWriter();
        text.writeHtmlTo(0, w);
        Assert.assertEquals("Some text.\nGo back to <a href=\'/root\'>your home</a>.\nMore text.\n", w.toString());
    }

    @Issue("SECURITY-382")
    @Test
    public void oldDeserialization() throws Exception {
        ByteBuffer buf = new ByteBuffer();
        buf.write((((("hello" + (PREAMBLE_STR)) + "AAAAwR+LCAAAAAAAAP9dzLEOwVAUxvHThtiNprYxsGiMQhiwNSIhMR/tSZXr3Lr3oJPwPt7FM5hM3gFh8i3/5Bt+1yeUrYH6ap9Yza1Ys9WKWuMiR05wqWhEgpmyEy306Jxvwb19ccGNoBJjLplmgWq0xgOGCjkNZ2IyTrsRlFayVTs4gVMYqP3pw28/JnznuABF/rYWyIyeJfLQe1vxZiDQ7NnYZLn0UZGRRjA9MiV+0OyFv3+utadQyH8B+aJxVM4AAAA=") + (POSTAMBLE_STR)) + "there\n").getBytes());
        AnnotatedLargeText<Void> text = new AnnotatedLargeText(buf, Charsets.UTF_8, true, null);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        text.writeLogTo(0, baos);
        Assert.assertEquals("hellothere\n", baos.toString());
        StringWriter w = new StringWriter();
        text.writeHtmlTo(0, w);
        Assert.assertEquals("hellothere\n", w.toString());
        Assert.assertThat(logging.getMessages(), CoreMatchers.hasItem("Failed to resurrect annotation"));// TODO assert that this is IOException: Refusing to deserialize unsigned note from an old log.

        INSECURE = true;
        try {
            w = new StringWriter();
            text.writeHtmlTo(0, w);
            Assert.assertThat(w.toString(), CoreMatchers.containsString("<script>"));
        } finally {
            INSECURE = false;
        }
    }

    @Issue("SECURITY-382")
    @Test
    public void badMac() throws Exception {
        ByteBuffer buf = new ByteBuffer();
        buf.write((((("Go back to " + (PREAMBLE_STR)) + "////4ByIhqPpAc43AbrEtyDUDc1/UEOXsoY6LeoHSeSlb1d7AAAAlR+LCAAAAAAAAP9b85aBtbiIQS+jNKU4P08vOT+vOD8nVc8xLy+/JLEkNcUnsSg9NSS1oiQktbhEBUT45ZekCpys9xWo8J3KxMDkycCWk5qXXpLhw8BcWpRTwiDkk5VYlqifk5iXrh9cUpSZl25dUcQghWaBM4QGGcYAAYxMDAwVBUAGZwkDq35Rfn4JABmN28qcAAAA") + (POSTAMBLE_STR)) + "your home.\n").getBytes());
        AnnotatedLargeText<Void> text = new AnnotatedLargeText(buf, Charsets.UTF_8, true, null);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        text.writeLogTo(0, baos);
        Assert.assertEquals("Go back to your home.\n", baos.toString());
        StringWriter w = new StringWriter();
        text.writeHtmlTo(0, w);
        Assert.assertEquals("Go back to your home.\n", w.toString());
        Assert.assertThat(logging.getMessages(), CoreMatchers.hasItem("Failed to resurrect annotation"));// TODO assert that this is IOException: MAC mismatch

    }

    /**
     * Simplified version of {@link HyperlinkNote}.
     */
    static class TestNote extends ConsoleNote<Void> {
        private final String url;

        private final int length;

        TestNote(String url, int length) {
            this.url = url;
            this.length = length;
        }

        @Override
        public ConsoleAnnotator<?> annotate(Void context, MarkupText text, int charPos) {
            text.addMarkup(charPos, (charPos + (length)), ((("<a href='" + (url)) + "'") + ">"), "</a>");
            return null;
        }

        static String encodeTo(String url, String text) throws IOException {
            return (encode()) + text;
        }
    }
}

