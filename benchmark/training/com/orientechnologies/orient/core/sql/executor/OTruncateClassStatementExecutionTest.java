package com.orientechnologies.orient.core.sql.executor;


import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.index.OIndex;
import com.orientechnologies.orient.core.index.OIndexCursor;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Luigi Dell'Aquila (l.dellaquila-(at)-orientdb.com)
 */
public class OTruncateClassStatementExecutionTest {
    static ODatabaseDocument database;

    @SuppressWarnings("unchecked")
    @Test
    public void testTruncateClass() {
        OSchema schema = OTruncateClassStatementExecutionTest.database.getMetadata().getSchema();
        OClass testClass = getOrCreateClass(schema);
        final OIndex<?> index = getOrCreateIndex(testClass);
        OTruncateClassStatementExecutionTest.database.command("truncate class test_class");
        OTruncateClassStatementExecutionTest.database.save(field("name", "x").field("data", Arrays.asList(1, 2)));
        OTruncateClassStatementExecutionTest.database.save(field("name", "y").field("data", Arrays.asList(3, 0)));
        OTruncateClassStatementExecutionTest.database.command(new OCommandSQL("truncate class test_class")).execute();
        OTruncateClassStatementExecutionTest.database.save(field("name", "x").field("data", Arrays.asList(5, 6, 7)));
        OTruncateClassStatementExecutionTest.database.save(field("name", "y").field("data", Arrays.asList(8, 9, (-1))));
        OResultSet result = OTruncateClassStatementExecutionTest.database.query("select from test_class");
        // Assert.assertEquals(result.size(), 2);
        Set<Integer> set = new HashSet<Integer>();
        while (result.hasNext()) {
            set.addAll(((Collection<Integer>) (result.next().getProperty("data"))));
        } 
        result.close();
        Assert.assertTrue(set.containsAll(Arrays.asList(5, 6, 7, 8, 9, (-1))));
        Assert.assertEquals(index.getSize(), 6);
        OIndexCursor cursor = index.cursor();
        Map.Entry<Object, OIdentifiable> entry = cursor.nextEntry();
        while (entry != null) {
            Assert.assertTrue(set.contains(((Integer) (entry.getKey()))));
            entry = cursor.nextEntry();
        } 
        schema.dropClass("test_class");
    }

    @Test
    public void testTruncateVertexClass() {
        OTruncateClassStatementExecutionTest.database.command("create class TestTruncateVertexClass extends V");
        OTruncateClassStatementExecutionTest.database.command("create vertex TestTruncateVertexClass set name = 'foo'");
        try {
            OTruncateClassStatementExecutionTest.database.command("truncate class TestTruncateVertexClass");
            Assert.fail();
        } catch (Exception e) {
        }
        OResultSet result = OTruncateClassStatementExecutionTest.database.query("select from TestTruncateVertexClass");
        Assert.assertTrue(result.hasNext());
        result.close();
        OTruncateClassStatementExecutionTest.database.command("truncate class TestTruncateVertexClass unsafe");
        result = OTruncateClassStatementExecutionTest.database.query("select from TestTruncateVertexClass");
        Assert.assertFalse(result.hasNext());
        result.close();
    }

    @Test
    public void testTruncateVertexClassSubclasses() {
        OTruncateClassStatementExecutionTest.database.command("create class TestTruncateVertexClassSuperclass");
        OTruncateClassStatementExecutionTest.database.command("create class TestTruncateVertexClassSubclass extends TestTruncateVertexClassSuperclass");
        OTruncateClassStatementExecutionTest.database.command("insert into TestTruncateVertexClassSuperclass set name = 'foo'");
        OTruncateClassStatementExecutionTest.database.command("insert into TestTruncateVertexClassSubclass set name = 'bar'");
        OResultSet result = OTruncateClassStatementExecutionTest.database.query("select from TestTruncateVertexClassSuperclass");
        for (int i = 0; i < 2; i++) {
            Assert.assertTrue(result.hasNext());
            result.next();
        }
        Assert.assertFalse(result.hasNext());
        result.close();
        OTruncateClassStatementExecutionTest.database.command("truncate class TestTruncateVertexClassSuperclass ");
        result = OTruncateClassStatementExecutionTest.database.query("select from TestTruncateVertexClassSubclass");
        Assert.assertTrue(result.hasNext());
        result.next();
        Assert.assertFalse(result.hasNext());
        result.close();
        OTruncateClassStatementExecutionTest.database.command("truncate class TestTruncateVertexClassSuperclass polymorphic");
        result = OTruncateClassStatementExecutionTest.database.query("select from TestTruncateVertexClassSubclass");
        Assert.assertFalse(result.hasNext());
        result.close();
    }

    @Test
    public void testTruncateVertexClassSubclassesWithIndex() {
        OTruncateClassStatementExecutionTest.database.command("create class TestTruncateVertexClassSuperclassWithIndex");
        OTruncateClassStatementExecutionTest.database.command("create property TestTruncateVertexClassSuperclassWithIndex.name STRING");
        OTruncateClassStatementExecutionTest.database.command("create index TestTruncateVertexClassSuperclassWithIndex_index on TestTruncateVertexClassSuperclassWithIndex (name) NOTUNIQUE");
        OTruncateClassStatementExecutionTest.database.command("create class TestTruncateVertexClassSubclassWithIndex extends TestTruncateVertexClassSuperclassWithIndex");
        OTruncateClassStatementExecutionTest.database.command("insert into TestTruncateVertexClassSuperclassWithIndex set name = 'foo'");
        OTruncateClassStatementExecutionTest.database.command("insert into TestTruncateVertexClassSubclassWithIndex set name = 'bar'");
        OResultSet result = OTruncateClassStatementExecutionTest.database.query("select from index:TestTruncateVertexClassSuperclassWithIndex_index");
        Assert.assertEquals(toList(result).size(), 2);
        result.close();
        OTruncateClassStatementExecutionTest.database.command("truncate class TestTruncateVertexClassSubclassWithIndex");
        result = OTruncateClassStatementExecutionTest.database.query("select from index:TestTruncateVertexClassSuperclassWithIndex_index");
        Assert.assertEquals(toList(result).size(), 1);
        result.close();
        OTruncateClassStatementExecutionTest.database.command("truncate class TestTruncateVertexClassSuperclassWithIndex polymorphic");
        result = OTruncateClassStatementExecutionTest.database.query("select from index:TestTruncateVertexClassSuperclassWithIndex_index");
        Assert.assertEquals(toList(result).size(), 0);
        result.close();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testTruncateClassWithCommandCache() {
        OSchema schema = OTruncateClassStatementExecutionTest.database.getMetadata().getSchema();
        OClass testClass = getOrCreateClass(schema);
        boolean ccWasEnabled = getCommandCache().isEnabled();
        getCommandCache().enable();
        OTruncateClassStatementExecutionTest.database.command("truncate class test_class");
        OTruncateClassStatementExecutionTest.database.save(field("name", "x").field("data", Arrays.asList(1, 2)));
        OTruncateClassStatementExecutionTest.database.save(field("name", "y").field("data", Arrays.asList(3, 0)));
        OResultSet result = OTruncateClassStatementExecutionTest.database.query("select from test_class");
        Assert.assertEquals(toList(result).size(), 2);
        result.close();
        OTruncateClassStatementExecutionTest.database.command("truncate class test_class");
        result = OTruncateClassStatementExecutionTest.database.query("select from test_class");
        Assert.assertEquals(toList(result).size(), 0);
        result.close();
        schema.dropClass("test_class");
        if (!ccWasEnabled) {
            getCommandCache().disable();
        }
    }
}

