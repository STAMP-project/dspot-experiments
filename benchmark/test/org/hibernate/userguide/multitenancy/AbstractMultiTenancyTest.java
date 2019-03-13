/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.userguide.multitenancy;


import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import javax.persistence.Entity;
import javax.persistence.Id;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
public abstract class AbstractMultiTenancyTest extends BaseUnitTestCase {
    protected static final String FRONT_END_TENANT = "front_end";

    protected static final String BACK_END_TENANT = "back_end";

    private Map<String, ConnectionProvider> connectionProviderMap = new HashMap<>();

    private SessionFactory sessionFactory;

    public AbstractMultiTenancyTest() {
        init();
    }

    // end::multitenacy-hibernate-MultiTenantConnectionProvider-example[]
    @Test
    public void testBasicExpectedBehavior() {
        // tag::multitenacy-multitenacy-hibernate-same-entity-example[]
        doInSession(AbstractMultiTenancyTest.FRONT_END_TENANT, ( session) -> {
            AbstractMultiTenancyTest.Person person = new AbstractMultiTenancyTest.Person();
            person.setId(1L);
            person.setName("John Doe");
            session.persist(person);
        });
        doInSession(AbstractMultiTenancyTest.BACK_END_TENANT, ( session) -> {
            AbstractMultiTenancyTest.Person person = new AbstractMultiTenancyTest.Person();
            person.setId(1L);
            person.setName("John Doe");
            session.persist(person);
        });
        // end::multitenacy-multitenacy-hibernate-same-entity-example[]
    }

    // end::multitenacy-hibernate-session-example[]
    @Entity(name = "Person")
    public static class Person {
        @Id
        private Long id;

        private String name;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}

