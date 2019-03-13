/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.flush;


import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.event.spi.PreUpdateEvent;
import org.hibernate.event.spi.PreUpdateEventListener;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
@TestForIssue(jiraKey = "HHH-2763")
public class TestCollectionInitializingDuringFlush extends BaseCoreFunctionalTestCase {
    @Test
    public void testInitializationDuringFlush() {
        Assert.assertFalse(TestCollectionInitializingDuringFlush.InitializingPreUpdateEventListener.INSTANCE.executed);
        Assert.assertFalse(TestCollectionInitializingDuringFlush.InitializingPreUpdateEventListener.INSTANCE.foundAny);
        Session s = openSession();
        s.beginTransaction();
        Publisher publisher = new Publisher("acme");
        Author author = new Author("john");
        author.setPublisher(publisher);
        publisher.getAuthors().add(author);
        author.getBooks().add(new Book("Reflections on a Wimpy Kid", author));
        s.save(author);
        s.getTransaction().commit();
        s.clear();
        s = openSession();
        s.beginTransaction();
        publisher = ((Publisher) (s.get(Publisher.class, publisher.getId())));
        publisher.setName("random nally");
        s.flush();
        s.getTransaction().commit();
        s.clear();
        s = openSession();
        s.beginTransaction();
        s.delete(author);
        s.getTransaction().commit();
        s.clear();
        Assert.assertTrue(TestCollectionInitializingDuringFlush.InitializingPreUpdateEventListener.INSTANCE.executed);
        Assert.assertTrue(TestCollectionInitializingDuringFlush.InitializingPreUpdateEventListener.INSTANCE.foundAny);
    }

    public static class InitializingPreUpdateEventListener implements PreUpdateEventListener {
        public static final TestCollectionInitializingDuringFlush.InitializingPreUpdateEventListener INSTANCE = new TestCollectionInitializingDuringFlush.InitializingPreUpdateEventListener();

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

