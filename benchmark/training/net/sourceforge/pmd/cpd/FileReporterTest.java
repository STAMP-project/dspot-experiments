/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.cpd;


import java.io.File;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Philippe T'Seyen
 */
public class FileReporterTest {
    @Test
    public void testCreation() {
        new FileReporter(((String) (null)));
        new FileReporter(((File) (null)));
    }

    @Test
    public void testEmptyReport() throws ReportException {
        File reportFile = new File("report.tmp");
        FileReporter fileReporter = new FileReporter(reportFile);
        fileReporter.report("");
        Assert.assertTrue(reportFile.exists());
        Assert.assertEquals(0L, reportFile.length());
        Assert.assertTrue(reportFile.delete());
    }

    @Test
    public void testReport() throws IOException, ReportException {
        String testString = "first line\nsecond line";
        File reportFile = new File("report.tmp");
        FileReporter fileReporter = new FileReporter(reportFile);
        fileReporter.report(testString);
        Assert.assertEquals(testString, readFile(reportFile));
        Assert.assertTrue(reportFile.delete());
    }

    @Test(expected = ReportException.class)
    public void testInvalidFile() throws ReportException {
        File reportFile = new File("/invalid_folder/report.tmp");
        FileReporter fileReporter = new FileReporter(reportFile);
        fileReporter.report("");
    }
}

