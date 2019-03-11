package com.orientechnologies.orient.core.sql.executor;


import com.orientechnologies.orient.core.db.ODatabaseSession;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.db.viewmanager.ViewCreationListener;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.metadata.schema.OView;
import com.orientechnologies.orient.core.metadata.schema.OViewConfig;
import com.orientechnologies.orient.core.record.OElement;
import java.util.concurrent.CountDownLatch;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Luigi Dell'Aquila (l.dellaquila-(at)-orientdb.com)
 */
public class OCreateViewStatementExecutionTest {
    static ODatabaseDocument db;

    @Test
    public void testPlain() {
        String className = "testPlain";
        OResultSet result = OCreateViewStatementExecutionTest.db.command((("create view " + className) + "  FROM (SELECT FROM V)"));
        OSchema schema = OCreateViewStatementExecutionTest.db.getMetadata().getSchema();
        OView view = schema.getView(className);
        Assert.assertNotNull(view);
        Assert.assertEquals(className, view.getName());
        result.close();
    }

    @Test
    public void testOriginField() throws InterruptedException {
        String className = "testOriginFieldClass";
        String viewName = "testOriginFieldView";
        OCreateViewStatementExecutionTest.db.createClass(className);
        OElement elem = OCreateViewStatementExecutionTest.db.newElement(className);
        elem.setProperty("name", "foo");
        elem.save();
        OViewConfig cfg = new OViewConfig(viewName, ("SELECT FROM " + className));
        cfg.setOriginRidField("origin");
        CountDownLatch latch = new CountDownLatch(1);
        OCreateViewStatementExecutionTest.db.getMetadata().getSchema().createView(cfg, new ViewCreationListener() {
            @Override
            public void afterCreate(ODatabaseSession database, String viewName) {
                latch.countDown();
            }

            @Override
            public void onError(String viewName, Exception exception) {
                latch.countDown();
            }
        });
        latch.await();
        OResultSet rs = OCreateViewStatementExecutionTest.db.query(("SELECT FROM " + viewName));
        Assert.assertTrue(rs.hasNext());
        OResult item = rs.next();
        Assert.assertEquals(elem.getIdentity(), item.getProperty("origin"));
        Assert.assertEquals("foo", item.getProperty("name"));
        Assert.assertFalse(rs.hasNext());
        rs.close();
    }

    @Test
    public void testMetadata() throws InterruptedException {
        String className = "testMetadataClass";
        String viewName = "testMetadata";
        OCreateViewStatementExecutionTest.db.createClass(className);
        String statement = ((("CREATE VIEW " + viewName) + " FROM (SELECT FROM ") + className) + ") METADATA {";
        statement += "updatable:true, ";
        // statement+="indexes...";
        statement += ("updateStrategy: '" + (OViewConfig.UPDATE_STRATEGY_LIVE)) + "', ";
        statement += "watchClasses:['foo', 'bar'], ";
        statement += "nodes:['baz','xx'], ";
        statement += "updateIntervalSeconds:100, ";
        statement += "originRidField:'pp' ";
        statement += "}";
        OCreateViewStatementExecutionTest.db.command(statement);
        OView view = OCreateViewStatementExecutionTest.db.getMetadata().getSchema().getView(viewName);
        Assert.assertTrue(view.isUpdatable());
        // Assert.assertEquals(OViewConfig.UPDATE_STRATEGY_LIVE, view.get());
        Assert.assertTrue(view.getWatchClasses().contains("foo"));
        Assert.assertTrue(view.getWatchClasses().contains("bar"));
        Assert.assertEquals(2, view.getWatchClasses().size());
        Assert.assertTrue(view.getNodes().contains("baz"));
        Assert.assertTrue(view.getNodes().contains("xx"));
        Assert.assertEquals(2, view.getNodes().size());
        Assert.assertEquals(100, view.getUpdateIntervalSeconds());
        Assert.assertEquals("pp", view.getOriginRidField());
    }

    @Test
    public void testIndexes() throws InterruptedException {
        String className = "testIndexesClass";
        String viewName = "testIndexes";
        OCreateViewStatementExecutionTest.db.createClass(className);
        for (int i = 0; i < 10; i++) {
            OElement elem = OCreateViewStatementExecutionTest.db.newElement(className);
            elem.setProperty("name", ("name" + i));
            elem.setProperty("surname", ("surname" + i));
            elem.save();
        }
        String statement = ((("CREATE VIEW " + viewName) + " FROM (SELECT FROM ") + className) + ") METADATA {";
        statement += "indexes: [{type:'NOTUNIQUE', properties:{name:'STRING', surname:'STRING'}}]";
        statement += "}";
        OCreateViewStatementExecutionTest.db.command(statement);
        Thread.sleep(1000);
        OResultSet result = OCreateViewStatementExecutionTest.db.query((("SELECT FROM " + viewName) + " WHERE name = 'name4'"));
        result.getExecutionPlan().get().getSteps().stream().anyMatch(( x) -> x instanceof FetchFromIndexStep);
        Assert.assertTrue(result.hasNext());
        result.next();
        Assert.assertFalse(result.hasNext());
        result.close();
    }

    @Test
    public void testLiveUpdate() throws InterruptedException {
        String className = "testLiveUpdateClass";
        String viewName = "testLiveUpdate";
        OCreateViewStatementExecutionTest.db.createClass(className);
        for (int i = 0; i < 10; i++) {
            OElement elem = OCreateViewStatementExecutionTest.db.newElement(className);
            elem.setProperty("name", ("name" + i));
            elem.setProperty("surname", ("surname" + i));
            elem.save();
        }
        String statement = ((("CREATE VIEW " + viewName) + " FROM (SELECT FROM ") + className) + ") METADATA {";
        statement += "updateStrategy:\"live\",";
        statement += "originRidField:\"origin\"";
        statement += "}";
        OCreateViewStatementExecutionTest.db.command(statement);
        Thread.sleep(1000);
        OCreateViewStatementExecutionTest.db.command((("UPDATE " + className) + " SET surname = 'changed' WHERE name = 'name3'"));
        Thread.sleep(1000);
        OResultSet result = OCreateViewStatementExecutionTest.db.query(("SELECT FROM " + viewName));
        for (int i = 0; i < 10; i++) {
            Assert.assertTrue(result.hasNext());
            OResult item = result.next();
            if (item.getProperty("name").equals("name3")) {
                Assert.assertEquals("changed", item.getProperty("surname"));
            } else {
                Assert.assertEquals(("sur" + (item.getProperty("name"))), item.getProperty("surname"));
            }
        }
        result.close();
    }

    @Test
    public void testLiveUpdateInsert() throws InterruptedException {
        String className = "testLiveUpdateInsertClass";
        String viewName = "testLiveUpdateInsert";
        OCreateViewStatementExecutionTest.db.createClass(className);
        for (int i = 0; i < 10; i++) {
            OElement elem = OCreateViewStatementExecutionTest.db.newElement(className);
            elem.setProperty("name", ("name" + i));
            elem.setProperty("surname", ("surname" + i));
            elem.save();
        }
        String statement = ((("CREATE VIEW " + viewName) + " FROM (SELECT FROM ") + className) + ") METADATA {";
        statement += "updateStrategy:\"live\"";
        statement += "}";
        OCreateViewStatementExecutionTest.db.command(statement);
        Thread.sleep(1000);
        OResultSet result = OCreateViewStatementExecutionTest.db.query(("SELECT FROM " + viewName));
        Assert.assertEquals(10, result.stream().count());
        result.close();
        OCreateViewStatementExecutionTest.db.command((("insert into " + className) + " set name = 'name10', surname = 'surname10'"));
        Thread.sleep(1000);
        result = OCreateViewStatementExecutionTest.db.query(("SELECT FROM " + viewName));
        Assert.assertEquals(11, result.stream().count());
        result.close();
    }

    @Test
    public void testLiveUpdateDelete() throws InterruptedException {
        String className = "testLiveUpdateDeleteClass";
        String viewName = "testLiveUpdateDelete";
        OCreateViewStatementExecutionTest.db.createClass(className);
        for (int i = 0; i < 10; i++) {
            OElement elem = OCreateViewStatementExecutionTest.db.newElement(className);
            elem.setProperty("name", ("name" + i));
            elem.setProperty("surname", ("surname" + i));
            elem.save();
        }
        String statement = ((("CREATE VIEW " + viewName) + " FROM (SELECT FROM ") + className) + ") METADATA {";
        statement += "updateStrategy:\"live\",";
        statement += "originRidField:\"origin\"";
        statement += "}";
        OCreateViewStatementExecutionTest.db.command(statement);
        Thread.sleep(1000);
        OResultSet result = OCreateViewStatementExecutionTest.db.query(("SELECT FROM " + viewName));
        Assert.assertEquals(10, result.stream().count());
        result.close();
        OCreateViewStatementExecutionTest.db.command((("DELETE FROM " + className) + " WHERE name = 'name3'"));
        Thread.sleep(1000);
        result = OCreateViewStatementExecutionTest.db.query(("SELECT FROM " + viewName));
        for (int i = 0; i < 9; i++) {
            Assert.assertTrue(result.hasNext());
            OResult item = result.next();
            Assert.assertNotEquals("name3", item.getProperty("name"));
        }
        result.close();
    }
}

