package aima.test.core.unit.nlp.rank;


import aima.core.nlp.ranking.HITS;
import aima.core.nlp.ranking.Page;
import java.util.ArrayList;
import org.junit.Assert;
import org.junit.Test;


public class HITSTest {
    HITS hits;

    @Test
    public void testMatches() {
        String query = "purple horse";
        String queryTwo = "puurple horse";
        String queryThree = "green";
        String text = "This text contains the words 'purple horse' and the word 'green'";
        Assert.assertTrue(hits.matches(query, text));
        Assert.assertFalse(hits.matches(queryTwo, text));
        Assert.assertTrue(hits.matches(queryThree, text));
    }

    @Test
    public void testNormalize() {
        ArrayList<Page> pages = new ArrayList<Page>();
        Page p1 = new Page("");
        Page p2 = new Page("");
        Page p3 = new Page("");
        Page p4 = new Page("");
        p1.hub = 3;
        p1.authority = 2;
        p2.hub = 2;
        p2.authority = 3;
        p3.hub = 1;
        p1.authority = 4;
        p4.hub = 0;
        p4.authority = 10;
        pages.add(p1);
        pages.add(p2);
        pages.add(p3);
        pages.add(p4);
        // hub total will be 9 + 4 + 1 + 0 = 14
        // authority total will 4 + 9 + 16 + 100 = 129
        double p1HubNorm = 0.214285;
        double p2HubNorm = 0.142857;
        hits.normalize(pages);
        Assert.assertEquals("Out of tolerance", p1HubNorm, pages.get(0).hub, 0.02);
        Assert.assertEquals("Out of tolerance", pages.get(1).hub, p2HubNorm, 0.02);
    }

    @Test
    public void testGetAveDelta() {
        double[] one = new double[]{ 0, 1, 2, 3, 4, 5 };
        double[] two = new double[]{ 0.5, 1.5, 2.5, 3.5, 4.5, 5.5 };
        double aveDelta = hits.getAveDelta(one, two);
        Assert.assertEquals(aveDelta, 0.5, 0);
    }
}

