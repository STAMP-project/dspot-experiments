package com.orientechnologies.orient.core.sql.executor;


import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.record.impl.ODocument;
import java.util.HashSet;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Luigi Dell'Aquila (l.dellaquila-(at)-orientdb.com)
 */
public class OFindReferencesStatementExecutionTest {
    static ODatabaseDocument db;

    @Test
    public void testLink() {
        String name = "testLink1";
        String name2 = "testLink2";
        OFindReferencesStatementExecutionTest.db.getMetadata().getSchema().createClass(name);
        OFindReferencesStatementExecutionTest.db.getMetadata().getSchema().createClass(name2);
        ODocument linked = new ODocument(name);
        linked.field("foo", "bar");
        linked.save();
        Set<ORID> ridsToMatch = new HashSet<>();
        for (int i = 0; i < 10; i++) {
            ODocument doc = new ODocument(name2);
            doc.field("counter", i);
            if ((i % 2) == 0) {
                doc.field("link", linked);
            }
            doc.save();
            if ((i % 2) == 0) {
                ridsToMatch.add(doc.getIdentity());
            }
        }
        OResultSet result = OFindReferencesStatementExecutionTest.db.query(("find references " + (linked.getIdentity())));
        ExecutionPlanPrintUtils.printExecutionPlan(result);
        for (int i = 0; i < 5; i++) {
            Assert.assertTrue(result.hasNext());
            OResult next = result.next();
            ridsToMatch.remove(next.getProperty("referredBy"));
        }
        Assert.assertFalse(result.hasNext());
        Assert.assertTrue(ridsToMatch.isEmpty());
        result.close();
    }
}

