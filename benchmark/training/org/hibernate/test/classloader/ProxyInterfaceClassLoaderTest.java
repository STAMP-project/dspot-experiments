/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.classloader;


import javax.persistence.Entity;
import javax.persistence.Id;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.annotations.Proxy;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests if javassist instrumentation is done with the proper classloader for entities with proxy class. The classloader
 * of {@link HibernateProxy} will not see {@link IPerson}, since it is only accessible from this package. But: the
 * classloader of {@link IPerson} will see {@link HibernateProxy}, so instrumentation will only work if this classloader
 * is chosen for creating the instrumented proxy class. We need to check the class of a loaded object though, since
 * building the configuration will not fail, only log the error and fall back to using the entity class itself as a
 * proxy.
 *
 * @author lgathy
 */
public class ProxyInterfaceClassLoaderTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testProxyClassLoader() {
        Session s = openSession();
        Transaction t = s.beginTransaction();
        ProxyInterfaceClassLoaderTest.IPerson p = new ProxyInterfaceClassLoaderTest.Person();
        p.setId(1);
        s.persist(p);
        s.flush();
        s.clear();
        Object lp = s.load(ProxyInterfaceClassLoaderTest.Person.class, p.getId());
        Assert.assertTrue("Loaded entity is not an instance of the proxy interface", ProxyInterfaceClassLoaderTest.IPerson.class.isInstance(lp));
        Assert.assertFalse("Proxy class was not created", ProxyInterfaceClassLoaderTest.Person.class.isInstance(lp));
        s.delete(lp);
        t.commit();
        s.close();
    }

    public interface IPerson {
        int getId();

        void setId(int id);
    }

    @Entity(name = "Person")
    @Proxy(proxyClass = ProxyInterfaceClassLoaderTest.IPerson.class)
    static class Person implements ProxyInterfaceClassLoaderTest.IPerson {
        @Id
        private int id;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }
}

