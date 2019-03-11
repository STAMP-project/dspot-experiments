/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.renderers;


import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.ReportTest;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;


public class YAHTMLRendererTest extends AbstractRendererTst {
    private String outputDir;

    @Test
    public void testReportMultipleViolations() throws Exception {
        Report report = new Report();
        report.addRuleViolation(newRuleViolation(1, "net.sf.pmd.test", "YAHTMLSampleClass1"));
        report.addRuleViolation(newRuleViolation(2, "net.sf.pmd.test", "YAHTMLSampleClass1"));
        report.addRuleViolation(newRuleViolation(1, "net.sf.pmd.other", "YAHTMLSampleClass2"));
        String actual = ReportTest.render(getRenderer(), report);
        Assert.assertEquals(filter(getExpected()), filter(actual));
        String[] htmlFiles = new File(outputDir).list();
        Assert.assertEquals(3, htmlFiles.length);
        Arrays.sort(htmlFiles);
        Assert.assertEquals("YAHTMLSampleClass1.html", htmlFiles[0]);
        Assert.assertEquals("YAHTMLSampleClass2.html", htmlFiles[1]);
        Assert.assertEquals("index.html", htmlFiles[2]);
        for (String file : htmlFiles) {
            try (FileInputStream in = new FileInputStream(new File(outputDir, file));InputStream expectedIn = YAHTMLRendererTest.class.getResourceAsStream(("yahtml/" + file))) {
                String data = IOUtils.toString(in, StandardCharsets.UTF_8);
                String expected = YAHTMLRendererTest.normalizeLineSeparators(IOUtils.toString(expectedIn, StandardCharsets.UTF_8));
                Assert.assertEquals((("File " + file) + " is different"), expected, data);
            }
        }
    }
}

