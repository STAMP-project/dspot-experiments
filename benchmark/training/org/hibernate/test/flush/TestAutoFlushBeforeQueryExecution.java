/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.flush;


import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.engine.spi.ActionQueue;
import org.hibernate.engine.spi.PersistenceContext;
import org.hibernate.event.spi.PreUpdateEvent;
import org.hibernate.event.spi.PreUpdateEventListener;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Gail Badner
 */
@TestForIssue(jiraKey = "HHH-6960")
public class TestAutoFlushBeforeQueryExecution extends BaseCoreFunctionalTestCase {
    @Test
    public void testAutoflushIsRequired() {
        Session s = openSession();
        Transaction txn = s.beginTransaction();
        Publisher publisher = new Publisher();
        publisher.setName("name");
        s.save(publisher);
        Assert.assertTrue("autoflush entity create", ((s.createQuery("from Publisher p").list().size()) == 1));
        publisher.setName("name");
        Assert.assertTrue("autoflush entity update", ((s.createQuery("from Publisher p where p.name='name'").list().size()) == 1));
        txn.commit();
        s.close();
        s = openSession();
        txn = s.beginTransaction();
        publisher = ((Publisher) (s.get(Publisher.class, publisher.getId())));
        Assert.assertTrue(publisher.getAuthors().isEmpty());
        final PersistenceContext persistenceContext = getPersistenceContext();
        final ActionQueue actionQueue = getActionQueue();
        Assert.assertEquals(1, persistenceContext.getCollectionEntries().size());
        Assert.assertEquals(1, persistenceContext.getCollectionsByKey().size());
        Assert.assertTrue(persistenceContext.getCollectionEntries().containsKey(publisher.getAuthors()));
        Assert.assertTrue(persistenceContext.getCollectionsByKey().values().contains(publisher.getAuthors()));
        Assert.assertEquals(0, actionQueue.numberOfCollectionRemovals());
        Author author1 = new Author();
        author1.setPublisher(publisher);
        publisher.getAuthors().add(author1);
        Assert.assertTrue("autoflush collection update", ((s.createQuery("select a from Publisher p join p.authors a").list().size()) == 1));
        Assert.assertEquals(2, persistenceContext.getCollectionEntries().size());
        Assert.assertEquals(2, persistenceContext.getCollectionsByKey().size());
        Assert.assertTrue(persistenceContext.getCollectionEntries().containsKey(publisher.getAuthors()));
        Assert.assertTrue(persistenceContext.getCollectionEntries().containsKey(author1.getBooks()));
        Assert.assertTrue(persistenceContext.getCollectionsByKey().values().contains(publisher.getAuthors()));
        Assert.assertTrue(persistenceContext.getCollectionsByKey().values().contains(author1.getBooks()));
        Assert.assertEquals(0, actionQueue.numberOfCollectionRemovals());
        author1.setPublisher(null);
        s.delete(author1);
        publisher.getAuthors().clear();
        Assert.assertEquals(0, actionQueue.numberOfCollectionRemovals());
        Assert.assertTrue("autoflush collection update", ((s.createQuery("select a from Publisher p join p.authors a").list().size()) == 0));
        Assert.assertEquals(1, persistenceContext.getCollectionEntries().size());
        Assert.assertEquals(1, persistenceContext.getCollectionsByKey().size());
        Assert.assertTrue(persistenceContext.getCollectionEntries().containsKey(publisher.getAuthors()));
        Assert.assertTrue(persistenceContext.getCollectionsByKey().values().contains(publisher.getAuthors()));
        Assert.assertEquals(0, actionQueue.numberOfCollectionRemovals());
        Set<Author> authorsOld = publisher.getAuthors();
        publisher.setAuthors(new HashSet<Author>());
        Author author2 = new Author();
        author2.setName("author2");
        author2.setPublisher(publisher);
        publisher.getAuthors().add(author2);
        List results = s.createQuery("select a from Publisher p join p.authors a").list();
        Assert.assertEquals(1, results.size());
        Assert.assertEquals(2, persistenceContext.getCollectionEntries().size());
        Assert.assertEquals(2, persistenceContext.getCollectionsByKey().size());
        Assert.assertTrue(persistenceContext.getCollectionEntries().containsKey(publisher.getAuthors()));
        Assert.assertTrue(persistenceContext.getCollectionEntries().containsKey(author2.getBooks()));
        Assert.assertTrue(persistenceContext.getCollectionsByKey().values().contains(publisher.getAuthors()));
        Assert.assertTrue(persistenceContext.getCollectionsByKey().values().contains(author2.getBooks()));
        Assert.assertEquals(0, actionQueue.numberOfCollectionRemovals());
        s.delete(publisher);
        Assert.assertTrue("autoflush delete", ((s.createQuery("from Publisher p").list().size()) == 0));
        txn.commit();
        s.close();
    }

    @Test
    public void testAutoflushIsNotRequiredWithUnrelatedCollectionChange() {
        Session s = openSession();
        Transaction txn = s.beginTransaction();
        Publisher publisher = new Publisher();
        publisher.setName("name");
        s.save(publisher);
        Assert.assertTrue("autoflush entity create", ((s.createQuery("from Publisher p").list().size()) == 1));
        publisher.setName("name");
        Assert.assertTrue("autoflush entity update", ((s.createQuery("from Publisher p where p.name='name'").list().size()) == 1));
        UnrelatedEntity unrelatedEntity = new UnrelatedEntity();
        s.save(unrelatedEntity);
        txn.commit();
        s.close();
        s = openSession();
        txn = s.beginTransaction();
        unrelatedEntity = ((UnrelatedEntity) (s.get(UnrelatedEntity.class, unrelatedEntity.getId())));
        publisher = ((Publisher) (s.get(Publisher.class, publisher.getId())));
        Assert.assertTrue(publisher.getAuthors().isEmpty());
        final PersistenceContext persistenceContext = getPersistenceContext();
        final ActionQueue actionQueue = getActionQueue();
        Assert.assertEquals(1, persistenceContext.getCollectionEntries().size());
        Assert.assertEquals(1, persistenceContext.getCollectionsByKey().size());
        Assert.assertTrue(persistenceContext.getCollectionEntries().containsKey(publisher.getAuthors()));
        Assert.assertTrue(persistenceContext.getCollectionsByKey().values().contains(publisher.getAuthors()));
        Assert.assertEquals(0, actionQueue.numberOfCollectionRemovals());
        Author author1 = new Author();
        author1.setPublisher(publisher);
        publisher.getAuthors().add(author1);
        Assert.assertTrue(((s.createQuery("from UnrelatedEntity").list().size()) == 1));
        Assert.assertEquals(2, persistenceContext.getCollectionEntries().size());
        Assert.assertEquals(1, persistenceContext.getCollectionsByKey().size());
        Assert.assertTrue(persistenceContext.getCollectionEntries().containsKey(publisher.getAuthors()));
        Assert.assertTrue(persistenceContext.getCollectionEntries().containsKey(author1.getBooks()));
        Assert.assertTrue(persistenceContext.getCollectionsByKey().values().contains(publisher.getAuthors()));
        Assert.assertEquals(0, actionQueue.numberOfCollectionRemovals());
        author1.setPublisher(null);
        s.delete(author1);
        publisher.getAuthors().clear();
        Assert.assertEquals(0, actionQueue.numberOfCollectionRemovals());
        Assert.assertTrue(((s.createQuery("from UnrelatedEntity").list().size()) == 1));
        Assert.assertEquals(2, persistenceContext.getCollectionEntries().size());
        Assert.assertEquals(1, persistenceContext.getCollectionsByKey().size());
        Assert.assertTrue(persistenceContext.getCollectionEntries().containsKey(publisher.getAuthors()));
        Assert.assertTrue(persistenceContext.getCollectionEntries().containsKey(author1.getBooks()));
        Assert.assertTrue(persistenceContext.getCollectionsByKey().values().contains(publisher.getAuthors()));
        Assert.assertEquals(0, actionQueue.numberOfCollectionRemovals());
        Set<Author> authorsOld = publisher.getAuthors();
        publisher.setAuthors(new HashSet<Author>());
        Author author2 = new Author();
        author2.setName("author2");
        author2.setPublisher(publisher);
        publisher.getAuthors().add(author2);
        List results = s.createQuery("from UnrelatedEntity").list();
        Assert.assertEquals(1, results.size());
        Assert.assertEquals(4, persistenceContext.getCollectionEntries().size());
        Assert.assertEquals(1, persistenceContext.getCollectionsByKey().size());
        Assert.assertTrue(persistenceContext.getCollectionEntries().containsKey(publisher.getAuthors()));
        Assert.assertTrue(persistenceContext.getCollectionEntries().containsKey(author2.getBooks()));
        Assert.assertTrue(persistenceContext.getCollectionEntries().containsKey(authorsOld));
        Assert.assertTrue(persistenceContext.getCollectionEntries().containsKey(author1.getBooks()));
        Assert.assertTrue(persistenceContext.getCollectionsByKey().values().contains(authorsOld));
        Assert.assertEquals(0, actionQueue.numberOfCollectionRemovals());
        s.flush();
        Assert.assertEquals(2, persistenceContext.getCollectionEntries().size());
        Assert.assertEquals(2, persistenceContext.getCollectionsByKey().size());
        Assert.assertTrue(persistenceContext.getCollectionEntries().containsKey(publisher.getAuthors()));
        Assert.assertTrue(persistenceContext.getCollectionEntries().containsKey(author2.getBooks()));
        Assert.assertTrue(persistenceContext.getCollectionsByKey().values().contains(publisher.getAuthors()));
        Assert.assertTrue(persistenceContext.getCollectionsByKey().values().contains(author2.getBooks()));
        Assert.assertEquals(0, actionQueue.numberOfCollectionRemovals());
        s.delete(publisher);
        Assert.assertTrue("autoflush delete", ((s.createQuery("from UnrelatedEntity").list().size()) == 1));
        s.delete(unrelatedEntity);
        txn.commit();
        s.close();
    }

    public static class InitializingPreUpdateEventListener implements PreUpdateEventListener {
        public static final TestAutoFlushBeforeQueryExecution.InitializingPreUpdateEventListener INSTANCE = new TestAutoFlushBeforeQueryExecution.InitializingPreUpdateEventListener();

        private boolean executed = false;

        private boolean foundAny = false;

        @Override
        public boolean onPreUpdate(PreUpdateEvent event) {
            executed = true;
            final Object[] oldValues = event.getOldState();
            final String[] properties = event.getPersister().getPropertyNames();
            // Iterate through all fields of the updated object
            for (int i = 0; i < (properties.length); i++) {
                if ((oldValues != null) && ((oldValues[i]) != null)) {
                    if (!(Hibernate.isInitialized(oldValues[i]))) {
                        // force any proxies and/or collections to initialize to illustrate HHH-2763
                        foundAny = true;
                        Hibernate.initialize(oldValues);
                    }
                }
            }
            return true;
        }
    }
}

