/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.events;


import org.hibernate.IrrelevantEntity;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.event.spi.PostCommitDeleteEventListener;
import org.hibernate.event.spi.PostCommitInsertEventListener;
import org.hibernate.event.spi.PostCommitUpdateEventListener;
import org.hibernate.event.spi.PostDeleteEvent;
import org.hibernate.event.spi.PostDeleteEventListener;
import org.hibernate.event.spi.PostInsertEvent;
import org.hibernate.event.spi.PostInsertEventListener;
import org.hibernate.event.spi.PostUpdateEvent;
import org.hibernate.event.spi.PostUpdateEventListener;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test to ensure that the existing post commit behavior when using plain PostXEventListeners fire on both success and failure.
 *
 * @author ShawnClowater
 */
public class PostCommitListenerTest extends BaseCoreFunctionalTestCase {
    private PostInsertEventListener postCommitInsertEventListener = new PostCommitListenerTest.TestPostCommitInsertEventListener();

    private PostDeleteEventListener postCommitDeleteEventListener = new PostCommitListenerTest.TestPostCommitDeleteEventListener();

    private PostUpdateEventListener postCommitUpdateEventListener = new PostCommitListenerTest.TestPostCommitUpdateEventListener();

    @Test
    @TestForIssue(jiraKey = "HHH-1582")
    public void testPostCommitInsertListenerSuccess() {
        Session session = openSession();
        Transaction transaction = session.beginTransaction();
        IrrelevantEntity irrelevantEntity = new IrrelevantEntity();
        irrelevantEntity.setName("Irrelevant");
        session.save(irrelevantEntity);
        session.flush();
        transaction.commit();
        session.close();
        Assert.assertEquals(1, ((PostCommitListenerTest.TestPostCommitInsertEventListener) (postCommitInsertEventListener)).success);
        Assert.assertEquals(0, ((PostCommitListenerTest.TestPostCommitInsertEventListener) (postCommitInsertEventListener)).failed);
    }

    @Test
    @TestForIssue(jiraKey = "HHH-1582")
    public void testPostCommitInsertListenerRollback() {
        Session session = openSession();
        Transaction transaction = session.beginTransaction();
        IrrelevantEntity irrelevantEntity = new IrrelevantEntity();
        irrelevantEntity.setName("Irrelevant");
        session.save(irrelevantEntity);
        session.flush();
        transaction.rollback();
        session.close();
        Assert.assertEquals(0, ((PostCommitListenerTest.TestPostCommitInsertEventListener) (postCommitInsertEventListener)).success);
        Assert.assertEquals(1, ((PostCommitListenerTest.TestPostCommitInsertEventListener) (postCommitInsertEventListener)).failed);
    }

    @Test
    @TestForIssue(jiraKey = "HHH-1582")
    public void testPostCommitUpdateListenerSuccess() {
        Session session = openSession();
        Transaction transaction = session.beginTransaction();
        IrrelevantEntity irrelevantEntity = new IrrelevantEntity();
        irrelevantEntity.setName("Irrelevant");
        session.save(irrelevantEntity);
        session.flush();
        transaction.commit();
        session = openSession();
        transaction = session.beginTransaction();
        irrelevantEntity.setName("Irrelevant 2");
        session.update(irrelevantEntity);
        session.flush();
        transaction.commit();
        session.close();
        Assert.assertEquals(1, ((PostCommitListenerTest.TestPostCommitUpdateEventListener) (postCommitUpdateEventListener)).sucess);
        Assert.assertEquals(0, ((PostCommitListenerTest.TestPostCommitUpdateEventListener) (postCommitUpdateEventListener)).failed);
    }

    @Test
    @TestForIssue(jiraKey = "HHH-1582")
    public void testPostCommitUpdateListenerRollback() {
        Session session = openSession();
        Transaction transaction = session.beginTransaction();
        IrrelevantEntity irrelevantEntity = new IrrelevantEntity();
        irrelevantEntity.setName("Irrelevant");
        session.save(irrelevantEntity);
        session.flush();
        transaction.commit();
        session.close();
        session = openSession();
        transaction = session.beginTransaction();
        irrelevantEntity.setName("Irrelevant 2");
        session.update(irrelevantEntity);
        session.flush();
        transaction.rollback();
        session.close();
        Assert.assertEquals(0, ((PostCommitListenerTest.TestPostCommitUpdateEventListener) (postCommitUpdateEventListener)).sucess);
        Assert.assertEquals(1, ((PostCommitListenerTest.TestPostCommitUpdateEventListener) (postCommitUpdateEventListener)).failed);
    }

    @Test
    @TestForIssue(jiraKey = "HHH-1582")
    public void testPostCommitDeleteListenerSuccess() {
        Session session = openSession();
        Transaction transaction = session.beginTransaction();
        IrrelevantEntity irrelevantEntity = new IrrelevantEntity();
        irrelevantEntity.setName("Irrelevant");
        session.save(irrelevantEntity);
        session.flush();
        transaction.commit();
        session.close();
        session = openSession();
        transaction = session.beginTransaction();
        session.delete(irrelevantEntity);
        session.flush();
        transaction.commit();
        session.close();
        Assert.assertEquals(1, ((PostCommitListenerTest.TestPostCommitDeleteEventListener) (postCommitDeleteEventListener)).success);
        Assert.assertEquals(0, ((PostCommitListenerTest.TestPostCommitDeleteEventListener) (postCommitDeleteEventListener)).failed);
    }

    @Test
    @TestForIssue(jiraKey = "HHH-1582")
    public void testPostCommitDeleteListenerRollback() {
        Session session = openSession();
        Transaction transaction = session.beginTransaction();
        IrrelevantEntity irrelevantEntity = new IrrelevantEntity();
        irrelevantEntity.setName("Irrelevant");
        session.save(irrelevantEntity);
        session.flush();
        transaction.commit();
        session.close();
        session = openSession();
        transaction = session.beginTransaction();
        session.delete(irrelevantEntity);
        session.flush();
        transaction.rollback();
        session.close();
        Assert.assertEquals(0, ((PostCommitListenerTest.TestPostCommitDeleteEventListener) (postCommitDeleteEventListener)).success);
        Assert.assertEquals(1, ((PostCommitListenerTest.TestPostCommitDeleteEventListener) (postCommitDeleteEventListener)).failed);
    }

    private class TestPostCommitDeleteEventListener implements PostCommitDeleteEventListener {
        int success;

        int failed;

        @Override
        public void onPostDelete(PostDeleteEvent event) {
            (success)++;
        }

        @Override
        public void onPostDeleteCommitFailed(PostDeleteEvent event) {
            (failed)++;
        }

        @Override
        public boolean requiresPostCommitHanding(EntityPersister persister) {
            return true;
        }
    }

    private class TestPostCommitUpdateEventListener implements PostCommitUpdateEventListener {
        int sucess;

        int failed;

        @Override
        public void onPostUpdate(PostUpdateEvent event) {
            (sucess)++;
        }

        @Override
        public void onPostUpdateCommitFailed(PostUpdateEvent event) {
            (failed)++;
        }

        @Override
        public boolean requiresPostCommitHanding(EntityPersister persister) {
            return true;
        }
    }

    private class TestPostCommitInsertEventListener implements PostCommitInsertEventListener {
        int success;

        int failed;

        @Override
        public void onPostInsert(PostInsertEvent event) {
            (success)++;
        }

        @Override
        public void onPostInsertCommitFailed(PostInsertEvent event) {
            (failed)++;
        }

        @Override
        public boolean requiresPostCommitHanding(EntityPersister persister) {
            return true;
        }
    }
}

