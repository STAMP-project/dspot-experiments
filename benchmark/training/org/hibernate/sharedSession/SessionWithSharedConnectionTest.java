/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.sharedSession;


import EventType.POST_COMMIT_INSERT;
import java.lang.reflect.Field;
import java.util.Collection;
import org.hibernate.IrrelevantEntity;
import org.hibernate.Session;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.PostInsertEvent;
import org.hibernate.event.spi.PostInsertEventListener;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
public class SessionWithSharedConnectionTest extends BaseCoreFunctionalTestCase {
    @Test
    @TestForIssue(jiraKey = "HHH-7090")
    public void testSharedTransactionContextSessionClosing() {
        Session session = openSession();
        session.getTransaction().begin();
        Session secondSession = openSession();
        secondSession.createCriteria(IrrelevantEntity.class).list();
        // the list should have registered and then released a JDBC resource
        Assert.assertFalse(getJdbcCoordinator().getResourceRegistry().hasRegisteredResources());
        Assert.assertTrue(session.isOpen());
        Assert.assertTrue(secondSession.isOpen());
        Assert.assertSame(session.getTransaction(), secondSession.getTransaction());
        session.getTransaction().commit();
        Assert.assertTrue(session.isOpen());
        Assert.assertTrue(secondSession.isOpen());
        secondSession.close();
        Assert.assertTrue(session.isOpen());
        Assert.assertFalse(secondSession.isOpen());
        session.close();
        Assert.assertFalse(session.isOpen());
        Assert.assertFalse(secondSession.isOpen());
    }

    @Test
    @TestForIssue(jiraKey = "HHH-7090")
    public void testSharedTransactionContextAutoClosing() {
        Session session = openSession();
        session.getTransaction().begin();
        // COMMIT ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        Session secondSession = openSession();
        // directly assert state of the second session
        Assert.assertTrue(isAutoCloseSessionEnabled());
        Assert.assertTrue(shouldAutoClose());
        // now commit the transaction and make sure that does not close the sessions
        session.getTransaction().commit();
        Assert.assertFalse(isClosed());
        Assert.assertTrue(isClosed());
        session.close();
        Assert.assertTrue(isClosed());
        Assert.assertTrue(isClosed());
        // ROLLBACK ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        session = sessionFactory().openSession();
        session.getTransaction().begin();
        secondSession = session.sessionWithOptions().transactionContext().autoClose(true).openSession();
        // directly assert state of the second session
        Assert.assertTrue(isAutoCloseSessionEnabled());
        Assert.assertTrue(shouldAutoClose());
        // now rollback the transaction and make sure that does not close the sessions
        session.getTransaction().rollback();
        Assert.assertFalse(isClosed());
        Assert.assertTrue(isClosed());
        session.close();
        Assert.assertTrue(isClosed());
        Assert.assertTrue(isClosed());
    }

    // @Test
    // @TestForIssue( jiraKey = "HHH-7090" )
    // public void testSharedTransactionContextAutoJoining() {
    // Session session = sessionFactory().openSession();
    // session.getTransaction().begin();
    // 
    // Session secondSession = session.sessionWithOptions()
    // .transactionContext()
    // .autoJoinTransactions( true )
    // .openSession();
    // 
    // // directly assert state of the second session
    // assertFalse( ((SessionImplementor) secondSession).shouldAutoJoinTransaction() );
    // 
    // secondSession.close();
    // session.close();
    // }
    @Test
    @TestForIssue(jiraKey = "HHH-7090")
    public void testSharedTransactionContextFlushBeforeCompletion() {
        Session session = openSession();
        session.getTransaction().begin();
        Session secondSession = openSession();
        // directly assert state of the second session
        // assertTrue( ((SessionImplementor) secondSession).isFlushBeforeCompletionEnabled() );
        // now try it out
        Integer id = ((Integer) (secondSession.save(new IrrelevantEntity())));
        session.getTransaction().commit();
        Assert.assertFalse(isClosed());
        Assert.assertTrue(isClosed());
        session.close();
        Assert.assertTrue(isClosed());
        Assert.assertTrue(isClosed());
        session = sessionFactory().openSession();
        session.getTransaction().begin();
        IrrelevantEntity it = ((IrrelevantEntity) (session.byId(IrrelevantEntity.class).load(id)));
        Assert.assertNotNull(it);
        session.delete(it);
        session.getTransaction().commit();
        session.close();
    }

    @Test
    @TestForIssue(jiraKey = "HHH-7239")
    public void testChildSessionCallsAfterTransactionAction() throws Exception {
        Session session = openSession();
        final String postCommitMessage = "post commit was called";
        EventListenerRegistry eventListenerRegistry = sessionFactory().getServiceRegistry().getService(EventListenerRegistry.class);
        // register a post commit listener
        eventListenerRegistry.appendListeners(POST_COMMIT_INSERT, new PostInsertEventListener() {
            @Override
            public void onPostInsert(PostInsertEvent event) {
                ((IrrelevantEntity) (event.getEntity())).setName(postCommitMessage);
            }

            @Override
            public boolean requiresPostCommitHanding(EntityPersister persister) {
                return true;
            }
        });
        session.getTransaction().begin();
        IrrelevantEntity irrelevantEntityMainSession = new IrrelevantEntity();
        irrelevantEntityMainSession.setName("main session");
        session.save(irrelevantEntityMainSession);
        // open secondary session to also insert an entity
        Session secondSession = openSession();
        IrrelevantEntity irrelevantEntitySecondarySession = new IrrelevantEntity();
        irrelevantEntitySecondarySession.setName("secondary session");
        secondSession.save(irrelevantEntitySecondarySession);
        session.getTransaction().commit();
        // both entities should have their names updated to the postCommitMessage value
        Assert.assertEquals(postCommitMessage, irrelevantEntityMainSession.getName());
        Assert.assertEquals(postCommitMessage, irrelevantEntitySecondarySession.getName());
    }

    @Test
    @TestForIssue(jiraKey = "HHH-7239")
    public void testChildSessionTwoTransactions() throws Exception {
        Session session = openSession();
        session.getTransaction().begin();
        // open secondary session with managed options
        Session secondarySession = openSession();
        // the secondary session should be automatically closed after the commit
        session.getTransaction().commit();
        Assert.assertFalse(secondarySession.isOpen());
        // should be able to create a new transaction and carry on using the original session
        session.getTransaction().begin();
        session.getTransaction().commit();
    }

    @Test
    @TestForIssue(jiraKey = "HHH-11830")
    public void testSharedSessionTransactionObserver() throws Exception {
        Session session = openSession();
        session.getTransaction().begin();
        Field field = null;
        Class<?> clazz = getTransactionCoordinator().getClass();
        while (clazz != null) {
            try {
                field = clazz.getDeclaredField("observers");
                field.setAccessible(true);
                break;
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        } 
        Assert.assertNotNull("Observers field was not found", field);
        Assert.assertEquals(0, ((Collection) (field.get(getTransactionCoordinator()))).size());
        // open secondary sessions with managed options and immediately close
        Session secondarySession;
        for (int i = 0; i < 10; i++) {
            secondarySession = session.sessionWithOptions().connection().flushMode(FlushMode.COMMIT).autoClose(true).openSession();
            // when the shared session is opened it should register an observer
            Assert.assertEquals(1, ((Collection) (field.get(getTransactionCoordinator()))).size());
            // observer should be released
            secondarySession.close();
            Assert.assertEquals(0, ((Collection) (field.get(getTransactionCoordinator()))).size());
        }
    }
}

