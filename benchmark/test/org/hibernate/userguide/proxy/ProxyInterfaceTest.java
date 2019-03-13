/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.userguide.proxy;


import javax.persistence.Entity;
import javax.persistence.Id;
import org.hibernate.annotations.Proxy;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Test;


/**
 *
 *
 * @author lgathy
 */
// end::entity-proxy-interface-mapping[]
public class ProxyInterfaceTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testProxyClassLoader() {
        // tag::entity-proxy-persist-mapping[]
        doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.userguide.proxy.Book book = new org.hibernate.userguide.proxy.Book();
            book.setId(1L);
            book.setTitle("High-Performance Java Persistence");
            book.setAuthor("Vlad Mihalcea");
            session.persist(book);
        });
        doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.userguide.proxy.Identifiable book = session.getReference(.class, 1L);
            assertTrue("Loaded entity is not an instance of the proxy interface", (book instanceof org.hibernate.userguide.proxy.Identifiable));
            assertFalse("Proxy class was not created", (book instanceof org.hibernate.userguide.proxy.Book));
        });
        // end::entity-proxy-persist-mapping[]
    }

    // tag::entity-proxy-interface-mapping[]
    public interface Identifiable {
        Long getId();

        void setId(Long id);
    }

    // tag::entity-proxy-interface-mapping[]
    @Entity(name = "Book")
    @Proxy(proxyClass = ProxyInterfaceTest.Identifiable.class)
    public static final class Book implements ProxyInterfaceTest.Identifiable {
        @Id
        private Long id;

        private String title;

        private String author;

        @Override
        public Long getId() {
            return id;
        }

        @Override
        public void setId(Long id) {
            this.id = id;
        }

        // Other getters and setters omitted for brevity
        // end::entity-proxy-interface-mapping[]
        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }
    }
}

