/**
 *
 */
package marytts.tools.analysis;


import marytts.signalproc.analysis.Labels;
import marytts.util.dom.DomUtils;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;


/**
 *
 *
 * @author marc
 */
public class CopySynthesisTest {
    @Test
    public void imposeSegments() throws Exception {
        Labels source = getReferenceLabels();
        Document target = getTestDocument();
        CopySynthesis cs = new CopySynthesis(getAllophoneSet());
        cs.imposeSegments(source, target);
        Assert.assertArrayEquals(source.getLabelSymbols(), new Labels(target).getLabelSymbols());
    }

    @Test
    public void imposeDurations() throws Exception {
        Labels source = getReferenceLabels();
        Document target = getTestDocument();
        System.out.println("Document before imposing durations:");
        System.out.println(DomUtils.document2String(target));
        CopySynthesis cs = new CopySynthesis(getAllophoneSet());
        cs.imposeDurations(source, target);
        System.out.println("\n\n\nDocument after imposing durations:");
        System.out.println(DomUtils.document2String(target));
        assertSimilarDurations(source, new Labels(target));
    }
}

