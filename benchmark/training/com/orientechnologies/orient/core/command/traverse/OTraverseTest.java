package com.orientechnologies.orient.core.command.traverse;


import OTraverse.STRATEGY.BREADTH_FIRST;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.record.impl.ODocument;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;


/**
 *
 *
 * @author Artem Orobets (enisher-at-gmail.com)
 */
public class OTraverseTest {
    private ODatabaseDocument db;

    private ODocument rootDocument;

    private OTraverse traverse;

    @Test
    public void testDepthTraverse() throws Exception {
        final ODocument aa = new ODocument();
        final ODocument ab = new ODocument();
        final ODocument ba = new ODocument();
        final ODocument bb = new ODocument();
        final ODocument a = new ODocument();
        a.field("aa", aa);
        a.field("ab", ab);
        final ODocument b = new ODocument();
        b.field("ba", ba);
        b.field("bb", bb);
        rootDocument.field("a", a);
        rootDocument.field("b", b);
        final ODocument c1 = new ODocument();
        final ODocument c1a = new ODocument();
        c1.field("c1a", c1a);
        final ODocument c1b = new ODocument();
        c1.field("c1b", c1b);
        final ODocument c2 = new ODocument();
        final ODocument c2a = new ODocument();
        c2.field("c2a", c2a);
        final ODocument c2b = new ODocument();
        c2.field("c2b", c2b);
        final ODocument c3 = new ODocument();
        final ODocument c3a = new ODocument();
        c3.field("c3a", c3a);
        final ODocument c3b = new ODocument();
        c3.field("c3b", c3b);
        rootDocument.field("c", new ArrayList<ODocument>(Arrays.asList(c1, c2, c3)));
        rootDocument.save(db.getClusterNameById(db.getDefaultClusterId()));
        final List<ODocument> expectedResult = Arrays.asList(rootDocument, a, aa, ab, b, ba, bb, c1, c1a, c1b, c2, c2a, c2b, c3, c3a, c3b);
        final List<OIdentifiable> results = traverse.execute();
        compareTraverseResults(expectedResult, results);
    }

    @Test
    public void testBreadthTraverse() throws Exception {
        traverse.setStrategy(BREADTH_FIRST);
        final ODocument aa = new ODocument();
        final ODocument ab = new ODocument();
        final ODocument ba = new ODocument();
        final ODocument bb = new ODocument();
        final ODocument a = new ODocument();
        a.field("aa", aa);
        a.field("ab", ab);
        final ODocument b = new ODocument();
        b.field("ba", ba);
        b.field("bb", bb);
        rootDocument.field("a", a);
        rootDocument.field("b", b);
        final ODocument c1 = new ODocument();
        final ODocument c1a = new ODocument();
        c1.field("c1a", c1a);
        final ODocument c1b = new ODocument();
        c1.field("c1b", c1b);
        final ODocument c2 = new ODocument();
        final ODocument c2a = new ODocument();
        c2.field("c2a", c2a);
        final ODocument c2b = new ODocument();
        c2.field("c2b", c2b);
        final ODocument c3 = new ODocument();
        final ODocument c3a = new ODocument();
        c3.field("c3a", c3a);
        final ODocument c3b = new ODocument();
        c3.field("c3b", c3b);
        rootDocument.field("c", new ArrayList<ODocument>(Arrays.asList(c1, c2, c3)));
        rootDocument.save(db.getClusterNameById(db.getDefaultClusterId()));
        final List<ODocument> expectedResult = Arrays.asList(rootDocument, a, b, aa, ab, ba, bb, c1, c2, c3, c1a, c1b, c2a, c2b, c3a, c3b);
        final List<OIdentifiable> results = traverse.execute();
        compareTraverseResults(expectedResult, results);
    }
}

