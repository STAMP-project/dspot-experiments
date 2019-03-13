package com.orientechnologies.orient.core.sql.executor;


import OSequence.SEQUENCE_TYPE.ORDERED;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.exception.ODatabaseException;
import com.orientechnologies.orient.core.metadata.sequence.OSequence;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Luigi Dell'Aquila (l.dellaquila-(at)-orientdb.com)
 */
public class OAlterSequenceStatementExecutionTest {
    static ODatabaseDocument db;

    @Test
    public void testSetIncrement() {
        String sequenceName = "testSetStart";
        try {
            OAlterSequenceStatementExecutionTest.db.getMetadata().getSequenceLibrary().createSequence(sequenceName, ORDERED, new OSequence.CreateParams());
        } catch (ODatabaseException exc) {
            Assert.assertTrue("Failed to create sequence", false);
        }
        OResultSet result = OAlterSequenceStatementExecutionTest.db.command((("alter sequence " + sequenceName) + " increment 20"));
        Assert.assertNotNull(result);
        Assert.assertTrue(result.hasNext());
        OResult next = result.next();
        Assert.assertNotNull(next);
        Assert.assertEquals(((Object) (20)), next.getProperty("increment"));
        result.close();
        OSequence seq = OAlterSequenceStatementExecutionTest.db.getMetadata().getSequenceLibrary().getSequence(sequenceName);
        Assert.assertNotNull(seq);
        try {
            Assert.assertEquals(20, seq.next());
        } catch (ODatabaseException exc) {
            Assert.assertTrue("Failed to call next", false);
        }
    }
}

