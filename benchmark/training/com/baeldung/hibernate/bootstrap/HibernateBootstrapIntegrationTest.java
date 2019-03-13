package com.baeldung.hibernate.bootstrap;


import com.baeldung.hibernate.bootstrap.model.TestEntity;
import junit.framework.TestCase;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.transaction.annotation.Transactional;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { HibernateConf.class })
@Transactional
public class HibernateBootstrapIntegrationTest {
    @Autowired
    private SessionFactory sessionFactory;

    @Test
    public void whenBootstrapHibernateSession_thenNoException() {
        Session session = sessionFactory.getCurrentSession();
        TestEntity newEntity = new TestEntity();
        newEntity.setId(1);
        session.save(newEntity);
        TestEntity searchEntity = session.find(TestEntity.class, 1);
        Assert.assertNotNull(searchEntity);
    }

    @Test
    public void whenProgrammaticTransactionCommit_thenEntityIsInDatabase() {
        TestCase.assertTrue(TestTransaction.isActive());
        // Save an entity and commit.
        Session session = sessionFactory.getCurrentSession();
        TestEntity newEntity = new TestEntity();
        newEntity.setId(1);
        session.save(newEntity);
        TestEntity searchEntity = session.find(TestEntity.class, 1);
        Assert.assertNotNull(searchEntity);
        TestCase.assertTrue(TestTransaction.isFlaggedForRollback());
        TestTransaction.flagForCommit();
        TestTransaction.end();
        TestCase.assertFalse(TestTransaction.isFlaggedForRollback());
        TestCase.assertFalse(TestTransaction.isActive());
        // Check that the entity is still there in a new transaction,
        // then delete it, but don't commit.
        TestTransaction.start();
        TestCase.assertTrue(TestTransaction.isFlaggedForRollback());
        TestCase.assertTrue(TestTransaction.isActive());
        session = sessionFactory.getCurrentSession();
        searchEntity = session.find(TestEntity.class, 1);
        Assert.assertNotNull(searchEntity);
        session.delete(searchEntity);
        session.flush();
        TestTransaction.end();
        TestCase.assertFalse(TestTransaction.isActive());
        // Check that the entity is still there in a new transaction,
        // then delete it and commit.
        TestTransaction.start();
        session = sessionFactory.getCurrentSession();
        searchEntity = session.find(TestEntity.class, 1);
        Assert.assertNotNull(searchEntity);
        session.delete(searchEntity);
        session.flush();
        TestCase.assertTrue(TestTransaction.isActive());
        TestTransaction.flagForCommit();
        TestTransaction.end();
        TestCase.assertFalse(TestTransaction.isActive());
        // Check that the entity is no longer there in a new transaction.
        TestTransaction.start();
        TestCase.assertTrue(TestTransaction.isActive());
        session = sessionFactory.getCurrentSession();
        searchEntity = session.find(TestEntity.class, 1);
        Assert.assertNull(searchEntity);
    }

    @Test
    @Commit
    public void givenTransactionCommitDefault_whenProgrammaticTransactionCommit_thenEntityIsInDatabase() {
        TestCase.assertTrue(TestTransaction.isActive());
        // Save an entity and commit.
        Session session = sessionFactory.getCurrentSession();
        TestEntity newEntity = new TestEntity();
        newEntity.setId(1);
        session.save(newEntity);
        TestEntity searchEntity = session.find(TestEntity.class, 1);
        Assert.assertNotNull(searchEntity);
        TestCase.assertFalse(TestTransaction.isFlaggedForRollback());
        TestTransaction.end();
        TestCase.assertFalse(TestTransaction.isFlaggedForRollback());
        TestCase.assertFalse(TestTransaction.isActive());
        // Check that the entity is still there in a new transaction,
        // then delete it, but don't commit.
        TestTransaction.start();
        TestCase.assertFalse(TestTransaction.isFlaggedForRollback());
        TestCase.assertTrue(TestTransaction.isActive());
        session = sessionFactory.getCurrentSession();
        searchEntity = session.find(TestEntity.class, 1);
        Assert.assertNotNull(searchEntity);
        session.delete(searchEntity);
        session.flush();
        TestTransaction.flagForRollback();
        TestTransaction.end();
        TestCase.assertFalse(TestTransaction.isActive());
        // Check that the entity is still there in a new transaction,
        // then delete it and commit.
        TestTransaction.start();
        session = sessionFactory.getCurrentSession();
        searchEntity = session.find(TestEntity.class, 1);
        Assert.assertNotNull(searchEntity);
        session.delete(searchEntity);
        session.flush();
        TestCase.assertTrue(TestTransaction.isActive());
        TestTransaction.end();
        TestCase.assertFalse(TestTransaction.isActive());
        // Check that the entity is no longer there in a new transaction.
        TestTransaction.start();
        TestCase.assertTrue(TestTransaction.isActive());
        session = sessionFactory.getCurrentSession();
        searchEntity = session.find(TestEntity.class, 1);
        Assert.assertNull(searchEntity);
    }
}

