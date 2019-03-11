package com.orientechnologies.orient.core.sql.functions.sql;


import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.exception.OQueryParsingException;
import com.orientechnologies.orient.core.record.impl.ODocument;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class OCustomSQLFunctionsTest {
    private static ODatabaseDocumentTx db;

    @Test
    public void testRandom() {
        List<ODocument> result = OCustomSQLFunctionsTest.db.query(new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery<ODocument>("select math_random() as random"));
        Assert.assertTrue((((Double) (result.get(0).field("random"))) > 0));
    }

    @Test
    public void testLog10() {
        List<ODocument> result = OCustomSQLFunctionsTest.db.query(new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery<ODocument>("select math_log10(10000) as log10"));
        Assert.assertEquals(((Double) (result.get(0).field("log10"))), 4.0, 1.0E-4);
    }

    @Test
    public void testAbsInt() {
        List<ODocument> result = OCustomSQLFunctionsTest.db.query(new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery<ODocument>("select math_abs(-5) as abs"));
        Assert.assertTrue((((Integer) (result.get(0).field("abs"))) == 5));
    }

    @Test
    public void testAbsDouble() {
        List<ODocument> result = OCustomSQLFunctionsTest.db.query(new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery<ODocument>("select math_abs(-5.0d) as abs"));
        Assert.assertTrue((((Double) (result.get(0).field("abs"))) == 5.0));
    }

    @Test
    public void testAbsFloat() {
        List<ODocument> result = OCustomSQLFunctionsTest.db.query(new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery<ODocument>("select math_abs(-5.0f) as abs"));
        Assert.assertTrue((((Float) (result.get(0).field("abs"))) == 5.0));
    }

    @Test(expected = OQueryParsingException.class)
    public void testNonExistingFunction() {
        OCustomSQLFunctionsTest.db.query(new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery<ODocument>("select math_min('boom', 'boom') as boom"));
    }
}

