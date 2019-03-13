/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.events;


import java.util.Set;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.SessionFactoryObserver;
import org.hibernate.event.spi.DeleteEvent;
import org.hibernate.event.spi.DeleteEventListener;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 * CallbackTest implementation
 *
 * @author Steve Ebersole
 */
public class CallbackTest extends BaseCoreFunctionalTestCase {
    private CallbackTest.TestingObserver observer = new CallbackTest.TestingObserver();

    private CallbackTest.TestingListener listener = new CallbackTest.TestingListener();

    @Test
    public void testCallbacks() {
        // test pre-assertions
        assert (observer.closingCount) == 0;
        assert (observer.closedCount) == 0;
        Assert.assertEquals("observer not notified of creation", 1, observer.creationCount);
        Assert.assertEquals("listener not notified of creation", 1, listener.initCount);
        sessionFactory().close();
        Assert.assertEquals("observer not notified of closing", 1, observer.closingCount);
        Assert.assertEquals("observer not notified of close", 1, observer.closedCount);
        Assert.assertEquals("listener not notified of close", 1, listener.destoryCount);
    }

    private static class TestingObserver implements SessionFactoryObserver {
        private int creationCount = 0;

        private int closedCount = 0;

        private int closingCount = 0;

        public void sessionFactoryCreated(SessionFactory factory) {
            (creationCount)++;
        }

        @Override
        public void sessionFactoryClosing(SessionFactory factory) {
            (closingCount)++;
        }

        public void sessionFactoryClosed(SessionFactory factory) {
            (closedCount)++;
        }
    }

    private static class TestingListener implements DeleteEventListener {
        private int initCount = 0;

        private int destoryCount = 0;

        public void initialize() {
            (initCount)++;
        }

        public void cleanup() {
            (destoryCount)++;
        }

        public void onDelete(DeleteEvent event) throws HibernateException {
        }

        public void onDelete(DeleteEvent event, Set transientEntities) throws HibernateException {
        }
    }
}

