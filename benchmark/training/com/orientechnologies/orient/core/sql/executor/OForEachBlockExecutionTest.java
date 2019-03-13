package com.orientechnologies.orient.core.sql.executor;


import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Luigi Dell'Aquila (l.dellaquila-(at)-orientdb.com)
 */
public class OForEachBlockExecutionTest {
    static ODatabaseDocument db;

    @Test
    public void testPlain() {
        String className = "testPlain";
        OForEachBlockExecutionTest.db.createClass(className);
        String script = "";
        script += "FOREACH ($val in [1,2,3]){\n";
        script += ("  insert into " + className) + " set value = $val;\n";
        script += "}";
        script += "SELECT FROM " + className;
        OResultSet results = OForEachBlockExecutionTest.db.execute("sql", script);
        int tot = 0;
        int sum = 0;
        while (results.hasNext()) {
            OResult item = results.next();
            sum += ((Integer) (item.getProperty("value")));
            tot++;
        } 
        Assert.assertEquals(3, tot);
        Assert.assertEquals(6, sum);
        results.close();
    }

    @Test
    public void testReturn() {
        String className = "testReturn";
        OForEachBlockExecutionTest.db.createClass(className);
        String script = "";
        script += "FOREACH ($val in [1,2,3]){\n";
        script += ("  insert into " + className) + " set value = $val;\n";
        script += "  if($val = 2){\n";
        script += "    RETURN;\n";
        script += "  }\n";
        script += "}";
        OResultSet results = OForEachBlockExecutionTest.db.execute("sql", script);
        results.close();
        results = OForEachBlockExecutionTest.db.query(("SELECT FROM " + className));
        int tot = 0;
        int sum = 0;
        while (results.hasNext()) {
            OResult item = results.next();
            sum += ((Integer) (item.getProperty("value")));
            tot++;
        } 
        Assert.assertEquals(2, tot);
        Assert.assertEquals(3, sum);
        results.close();
    }
}

