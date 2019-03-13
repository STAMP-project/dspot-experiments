package com.orientechnologies.orient.core.sql.executor;


import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by tglman on 09/06/17.
 */
public class OSelectStatementExecutionTestIT {
    static ODatabaseDocument db;

    @Test
    public void stressTestNew() {
        String className = "stressTestNew";
        OSelectStatementExecutionTestIT.db.getMetadata().getSchema().createClass(className);
        for (int i = 0; i < 1000000; i++) {
            ODocument doc = OSelectStatementExecutionTestIT.db.newInstance(className);
            doc.setProperty("name", ("name" + i));
            doc.setProperty("surname", ("surname" + i));
            doc.save();
        }
        for (int run = 0; run < 5; run++) {
            long begin = System.nanoTime();
            OResultSet result = OSelectStatementExecutionTestIT.db.query((("select name from " + className) + " where name <> 'name1' "));
            for (int i = 0; i < 999999; i++) {
                // Assert.assertTrue(result.hasNext());
                OResult item = result.next();
                // Assert.assertNotNull(item);
                Object name = item.getProperty("name");
                Assert.assertFalse("name1".equals(name));
            }
            Assert.assertFalse(result.hasNext());
            result.close();
            long end = System.nanoTime();
            System.out.println(("new: " + ((end - begin) / 1000000)));
        }
    }
}

