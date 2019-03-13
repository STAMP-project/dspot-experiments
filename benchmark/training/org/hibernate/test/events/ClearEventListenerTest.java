/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.events;


import org.hibernate.Session;
import org.hibernate.event.spi.ClearEvent;
import org.hibernate.event.spi.ClearEventListener;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
public class ClearEventListenerTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testExplicitClear() {
        listener.callCount = 0;
        Session s = openSession();
        s.clear();
        Assert.assertEquals(1, listener.callCount);
        s.close();
        Assert.assertEquals(1, listener.callCount);
    }

    @Test
    public void testAutoClear() {
        listener.callCount = 0;
        Session s = openSession();
        setAutoClear(true);
        s.beginTransaction();
        Assert.assertEquals(0, listener.callCount);
        s.getTransaction().commit();
        Assert.assertEquals(1, listener.callCount);
        s.close();
        Assert.assertEquals(1, listener.callCount);
    }

    private ClearEventListenerTest.TheListener listener = new ClearEventListenerTest.TheListener();

    private static class TheListener implements ClearEventListener {
        private int callCount;

        @Override
        public void onClear(ClearEvent event) {
            (callCount)++;
        }
    }
}

