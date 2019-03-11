/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.jdbc.internal;


import java.sql.PreparedStatement;
import java.sql.Statement;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.engine.jdbc.batch.internal.BatchBuilderImpl;
import org.hibernate.engine.jdbc.batch.internal.BatchingBatch;
import org.hibernate.engine.jdbc.batch.internal.NonBatchingBatch;
import org.hibernate.engine.jdbc.batch.spi.Batch;
import org.hibernate.engine.jdbc.batch.spi.BatchBuilder;
import org.hibernate.engine.jdbc.batch.spi.BatchKey;
import org.hibernate.engine.jdbc.spi.JdbcCoordinator;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.jdbc.Expectations;
import org.hibernate.resource.jdbc.spi.LogicalConnectionImplementor;
import org.hibernate.test.common.JournalingBatchObserver;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 * @author Brett Meyer
 */
public class BatchingTest extends BaseCoreFunctionalTestCase implements BatchKey {
    @Test
    public void testNonBatchingUsage() throws Exception {
        Session session = openSession();
        SessionImplementor sessionImpl = ((SessionImplementor) (session));
        final JdbcCoordinator jdbcCoordinator = sessionImpl.getJdbcCoordinator();
        LogicalConnectionImplementor logicalConnection = jdbcCoordinator.getLogicalConnection();
        // set up some tables to use
        Statement statement = jdbcCoordinator.getStatementPreparer().createStatement();
        String dropSql = getDialect().getDropTableString("SANDBOX_JDBC_TST");
        try {
            jdbcCoordinator.getResultSetReturn().execute(statement, dropSql);
        } catch (Exception e) {
            // ignore if the DB doesn't support "if exists" and the table doesn't exist
        }
        jdbcCoordinator.getResultSetReturn().execute(statement, "create table SANDBOX_JDBC_TST ( ID integer, NAME varchar(100) )");
        Assert.assertTrue(jdbcCoordinator.getResourceRegistry().hasRegisteredResources());
        Assert.assertTrue(logicalConnection.isPhysicallyConnected());
        jdbcCoordinator.getResourceRegistry().release(statement);
        Assert.assertFalse(jdbcCoordinator.getResourceRegistry().hasRegisteredResources());
        Assert.assertTrue(logicalConnection.isPhysicallyConnected());// after_transaction specified

        // ok, now we can get down to it...
        Transaction txn = session.getTransaction();// same as Session#getTransaction

        txn.begin();
        final String insertSql = "insert into SANDBOX_JDBC_TST( ID, NAME ) values ( ?, ? )";
        final BatchBuilder batchBuilder = new BatchBuilderImpl((-1));
        final BatchKey batchKey = new org.hibernate.engine.jdbc.batch.internal.BasicBatchKey("this", Expectations.BASIC);
        final Batch insertBatch = batchBuilder.buildBatch(batchKey, jdbcCoordinator);
        final JournalingBatchObserver batchObserver = new JournalingBatchObserver();
        insertBatch.addObserver(batchObserver);
        Assert.assertTrue("unexpected Batch impl", NonBatchingBatch.class.isInstance(insertBatch));
        PreparedStatement insert = insertBatch.getBatchStatement(insertSql, false);
        insert.setLong(1, 1);
        insert.setString(2, "name");
        Assert.assertEquals(0, batchObserver.getExplicitExecutionCount());
        Assert.assertEquals(0, batchObserver.getImplicitExecutionCount());
        insertBatch.addToBatch();
        Assert.assertEquals(0, batchObserver.getExplicitExecutionCount());
        Assert.assertEquals(1, batchObserver.getImplicitExecutionCount());
        Assert.assertFalse(jdbcCoordinator.getResourceRegistry().hasRegisteredResources());
        insertBatch.execute();
        Assert.assertEquals(1, batchObserver.getExplicitExecutionCount());
        Assert.assertEquals(1, batchObserver.getImplicitExecutionCount());
        Assert.assertFalse(jdbcCoordinator.getResourceRegistry().hasRegisteredResources());
        insertBatch.release();
        txn.commit();
        session.close();
    }

    @Test
    public void testBatchingUsage() throws Exception {
        Session session = openSession();
        SessionImplementor sessionImpl = ((SessionImplementor) (session));
        final JdbcCoordinator jdbcCoordinator = sessionImpl.getJdbcCoordinator();
        LogicalConnectionImplementor logicalConnection = jdbcCoordinator.getLogicalConnection();
        // set up some tables to use
        Statement statement = jdbcCoordinator.getStatementPreparer().createStatement();
        String dropSql = getDialect().getDropTableString("SANDBOX_JDBC_TST");
        try {
            jdbcCoordinator.getResultSetReturn().execute(statement, dropSql);
        } catch (Exception e) {
            // ignore if the DB doesn't support "if exists" and the table doesn't exist
        }
        jdbcCoordinator.getResultSetReturn().execute(statement, "create table SANDBOX_JDBC_TST ( ID integer, NAME varchar(100) )");
        Assert.assertTrue(jdbcCoordinator.getResourceRegistry().hasRegisteredResources());
        Assert.assertTrue(logicalConnection.isPhysicallyConnected());
        jdbcCoordinator.getResourceRegistry().release(statement);
        Assert.assertFalse(jdbcCoordinator.getResourceRegistry().hasRegisteredResources());
        Assert.assertTrue(logicalConnection.isPhysicallyConnected());// after_transaction specified

        // ok, now we can get down to it...
        Transaction txn = session.getTransaction();// same as Session#getTransaction

        txn.begin();
        final BatchBuilder batchBuilder = new BatchBuilderImpl(2);
        final BatchKey batchKey = new org.hibernate.engine.jdbc.batch.internal.BasicBatchKey("this", Expectations.BASIC);
        final Batch insertBatch = batchBuilder.buildBatch(batchKey, jdbcCoordinator);
        Assert.assertTrue("unexpected Batch impl", BatchingBatch.class.isInstance(insertBatch));
        final JournalingBatchObserver batchObserver = new JournalingBatchObserver();
        insertBatch.addObserver(batchObserver);
        final String insertSql = "insert into SANDBOX_JDBC_TST( ID, NAME ) values ( ?, ? )";
        PreparedStatement insert = insertBatch.getBatchStatement(insertSql, false);
        insert.setLong(1, 1);
        insert.setString(2, "name");
        Assert.assertEquals(0, batchObserver.getExplicitExecutionCount());
        Assert.assertEquals(0, batchObserver.getImplicitExecutionCount());
        insertBatch.addToBatch();
        Assert.assertEquals(0, batchObserver.getExplicitExecutionCount());
        Assert.assertEquals(0, batchObserver.getImplicitExecutionCount());
        Assert.assertTrue(jdbcCoordinator.getResourceRegistry().hasRegisteredResources());
        PreparedStatement insert2 = insertBatch.getBatchStatement(insertSql, false);
        Assert.assertSame(insert, insert2);
        insert = insert2;
        insert.setLong(1, 2);
        insert.setString(2, "another name");
        Assert.assertEquals(0, batchObserver.getExplicitExecutionCount());
        Assert.assertEquals(0, batchObserver.getImplicitExecutionCount());
        insertBatch.addToBatch();
        Assert.assertEquals(0, batchObserver.getExplicitExecutionCount());
        Assert.assertEquals(1, batchObserver.getImplicitExecutionCount());
        Assert.assertTrue(jdbcCoordinator.getResourceRegistry().hasRegisteredResources());
        insertBatch.execute();
        Assert.assertEquals(1, batchObserver.getExplicitExecutionCount());
        Assert.assertEquals(1, batchObserver.getImplicitExecutionCount());
        Assert.assertFalse(jdbcCoordinator.getResourceRegistry().hasRegisteredResources());
        insertBatch.release();
        txn.commit();
        session.close();
    }

    @Test
    public void testSessionBatchingUsage() throws Exception {
        Session session = openSession();
        session.setJdbcBatchSize(3);
        SessionImplementor sessionImpl = ((SessionImplementor) (session));
        final JdbcCoordinator jdbcCoordinator = sessionImpl.getJdbcCoordinator();
        LogicalConnectionImplementor logicalConnection = jdbcCoordinator.getLogicalConnection();
        // set up some tables to use
        Statement statement = jdbcCoordinator.getStatementPreparer().createStatement();
        String dropSql = getDialect().getDropTableString("SANDBOX_JDBC_TST");
        try {
            jdbcCoordinator.getResultSetReturn().execute(statement, dropSql);
        } catch (Exception e) {
            // ignore if the DB doesn't support "if exists" and the table doesn't exist
        }
        jdbcCoordinator.getResultSetReturn().execute(statement, "create table SANDBOX_JDBC_TST ( ID integer, NAME varchar(100) )");
        Assert.assertTrue(jdbcCoordinator.getResourceRegistry().hasRegisteredResources());
        Assert.assertTrue(logicalConnection.isPhysicallyConnected());
        jdbcCoordinator.getResourceRegistry().release(statement);
        Assert.assertFalse(jdbcCoordinator.getResourceRegistry().hasRegisteredResources());
        Assert.assertTrue(logicalConnection.isPhysicallyConnected());// after_transaction specified

        // ok, now we can get down to it...
        Transaction txn = session.getTransaction();// same as Session#getTransaction

        txn.begin();
        final BatchBuilder batchBuilder = new BatchBuilderImpl(2);
        final BatchKey batchKey = new org.hibernate.engine.jdbc.batch.internal.BasicBatchKey("this", Expectations.BASIC);
        final Batch insertBatch = batchBuilder.buildBatch(batchKey, jdbcCoordinator);
        Assert.assertTrue("unexpected Batch impl", BatchingBatch.class.isInstance(insertBatch));
        final JournalingBatchObserver batchObserver = new JournalingBatchObserver();
        insertBatch.addObserver(batchObserver);
        final String insertSql = "insert into SANDBOX_JDBC_TST( ID, NAME ) values ( ?, ? )";
        PreparedStatement insert = insertBatch.getBatchStatement(insertSql, false);
        insert.setLong(1, 1);
        insert.setString(2, "name");
        Assert.assertEquals(0, batchObserver.getExplicitExecutionCount());
        Assert.assertEquals(0, batchObserver.getImplicitExecutionCount());
        insertBatch.addToBatch();
        Assert.assertEquals(0, batchObserver.getExplicitExecutionCount());
        Assert.assertEquals(0, batchObserver.getImplicitExecutionCount());
        Assert.assertTrue(jdbcCoordinator.getResourceRegistry().hasRegisteredResources());
        PreparedStatement insert2 = insertBatch.getBatchStatement(insertSql, false);
        Assert.assertSame(insert, insert2);
        insert = insert2;
        insert.setLong(1, 2);
        insert.setString(2, "another name");
        Assert.assertEquals(0, batchObserver.getExplicitExecutionCount());
        Assert.assertEquals(0, batchObserver.getImplicitExecutionCount());
        insertBatch.addToBatch();
        Assert.assertEquals(0, batchObserver.getExplicitExecutionCount());
        Assert.assertEquals(0, batchObserver.getImplicitExecutionCount());
        Assert.assertTrue(jdbcCoordinator.getResourceRegistry().hasRegisteredResources());
        PreparedStatement insert3 = insertBatch.getBatchStatement(insertSql, false);
        Assert.assertSame(insert, insert3);
        insert = insert3;
        insert.setLong(1, 3);
        insert.setString(2, "yet another name");
        Assert.assertEquals(0, batchObserver.getExplicitExecutionCount());
        Assert.assertEquals(0, batchObserver.getImplicitExecutionCount());
        insertBatch.addToBatch();
        Assert.assertEquals(0, batchObserver.getExplicitExecutionCount());
        Assert.assertEquals(1, batchObserver.getImplicitExecutionCount());
        Assert.assertTrue(jdbcCoordinator.getResourceRegistry().hasRegisteredResources());
        insertBatch.execute();
        Assert.assertEquals(1, batchObserver.getExplicitExecutionCount());
        Assert.assertEquals(1, batchObserver.getImplicitExecutionCount());
        Assert.assertFalse(jdbcCoordinator.getResourceRegistry().hasRegisteredResources());
        insertBatch.release();
        txn.commit();
        session.close();
    }
}

