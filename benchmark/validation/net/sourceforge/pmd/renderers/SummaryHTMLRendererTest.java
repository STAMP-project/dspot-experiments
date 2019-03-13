/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.renderers;


import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.ReportTest;
import org.junit.Assert;
import org.junit.Test;


public class SummaryHTMLRendererTest extends AbstractRendererTst {
    @Test
    public void testShowSuppressions() throws Exception {
        Report rep = createEmptyReportWithSuppression();
        Renderer renderer = getRenderer();
        renderer.setShowSuppressedViolations(true);
        String actual = ReportTest.render(renderer, rep);
        Assert.assertEquals(((((((((((((((((((((((((((((((((((("<html><head><title>PMD</title></head><body>" + (PMD.EOL)) + "<center><h2>Summary</h2></center>") + (PMD.EOL)) + "<table align=\"center\" cellspacing=\"0\" cellpadding=\"3\">") + (PMD.EOL)) + "<tr><th>Rule name</th><th>Number of violations</th></tr>") + (PMD.EOL)) + "</table>") + (PMD.EOL)) + "<center><h2>Detail</h2></center><table align=\"center\" cellspacing=\"0\" cellpadding=\"3\"><tr>") + (PMD.EOL)) + "<center><h3>PMD report</h3></center><center><h3>Problems found</h3></center><table align=\"center\" cellspacing=\"0\" cellpadding=\"3\"><tr>") + (PMD.EOL)) + "<th>#</th><th>File</th><th>Line</th><th>Problem</th></tr>") + (PMD.EOL)) + "</table><hr/><center><h3>Suppressed warnings</h3></center><table align=\"center\" cellspacing=\"0\" cellpadding=\"3\"><tr>") + (PMD.EOL)) + "<th>File</th><th>Line</th><th>Rule</th><th>NOPMD or Annotation</th><th>Reason</th></tr>") + (PMD.EOL)) + "<tr bgcolor=\"lightgrey\"> ") + (PMD.EOL)) + "<td align=\"left\"></td>") + (PMD.EOL)) + "<td align=\"center\">1</td>") + (PMD.EOL)) + "<td align=\"center\">Foo</td>") + (PMD.EOL)) + "<td align=\"center\">NOPMD</td>") + (PMD.EOL)) + "<td align=\"center\">test</td>") + (PMD.EOL)) + "</tr>") + (PMD.EOL)) + "</table></tr></table></body></html>") + (PMD.EOL)), actual);
    }

    @Test
    public void testHideSuppressions() throws Exception {
        Report rep = createEmptyReportWithSuppression();
        Renderer renderer = getRenderer();
        renderer.setShowSuppressedViolations(false);
        String actual = ReportTest.render(renderer, rep);
        Assert.assertEquals(getExpectedEmpty(), actual);
    }
}

