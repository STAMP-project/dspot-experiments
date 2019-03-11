/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.component.empty;


import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Version;
import org.hibernate.EmptyInterceptor;
import org.hibernate.annotations.SelectBeforeUpdate;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.type.Type;
import org.junit.Test;


/**
 *
 *
 * @author Chris Cranford
 */
public class SelectBeforeUpdateEmbeddedTest extends BaseCoreFunctionalTestCase {
    private final SelectBeforeUpdateEmbeddedTest.OnFlushDirtyInterceptor i = new SelectBeforeUpdateEmbeddedTest.OnFlushDirtyInterceptor();

    @Test
    @TestForIssue(jiraKey = "HHH-11237")
    public void testSelectBeforeUpdateUsingEmptyComposites() {
        // Opt-in behavior 5.1+
        rebuildSessionFactory(( c) -> c.setProperty(AvailableSettings.CREATE_EMPTY_COMPOSITES_ENABLED, "true"));
        testSelectBeforeUpdate();
    }

    @Test
    @TestForIssue(jiraKey = "HHH-11237")
    public void testSelectBeforeUpdateUsingNullComposites() {
        // Legacy behavior test
        rebuildSessionFactory(( c) -> c.setProperty(AvailableSettings.CREATE_EMPTY_COMPOSITES_ENABLED, "false"));
        testSelectBeforeUpdate();
    }

    @Entity(name = "Person")
    @SelectBeforeUpdate
    public static class Person {
        @Id
        private Integer id;

        private String name;

        @Embedded
        private SelectBeforeUpdateEmbeddedTest.Address address;

        @Version
        private Integer version;

        Person() {
        }

        Person(Integer id, String name, SelectBeforeUpdateEmbeddedTest.Address address) {
            this.id = id;
            this.name = name;
            this.address = address;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public SelectBeforeUpdateEmbeddedTest.Address getAddress() {
            return address;
        }

        public void setAddress(SelectBeforeUpdateEmbeddedTest.Address address) {
            this.address = address;
        }

        public Integer getVersion() {
            return version;
        }

        public void setVersion(Integer version) {
            this.version = version;
        }
    }

    @Embeddable
    public static class Address implements Serializable {
        private String postalCode;

        private String state;

        private String address;

        Address() {
        }

        Address(String postalCode, String state, String address) {
            this.postalCode = postalCode;
            this.state = state;
            this.address = address;
        }

        public String getPostalCode() {
            return postalCode;
        }

        public void setPostalCode(String postalCode) {
            this.postalCode = postalCode;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            SelectBeforeUpdateEmbeddedTest.Address address1 = ((SelectBeforeUpdateEmbeddedTest.Address) (o));
            if ((getPostalCode()) != null ? !(getPostalCode().equals(address1.getPostalCode())) : (address1.getPostalCode()) != null) {
                return false;
            }
            if ((getState()) != null ? !(getState().equals(address1.getState())) : (address1.getState()) != null) {
                return false;
            }
            return (getAddress()) != null ? getAddress().equals(address1.getAddress()) : (address1.getAddress()) == null;
        }

        @Override
        public int hashCode() {
            int result = ((getPostalCode()) != null) ? getPostalCode().hashCode() : 0;
            result = (31 * result) + ((getState()) != null ? getState().hashCode() : 0);
            result = (31 * result) + ((getAddress()) != null ? getAddress().hashCode() : 0);
            return result;
        }
    }

    public static class OnFlushDirtyInterceptor extends EmptyInterceptor {
        private AtomicInteger calls = new AtomicInteger();

        @Override
        public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState, String[] propertyNames, Type[] types) {
            calls.incrementAndGet();
            return false;
        }

        public int getCalls() {
            return calls.get();
        }

        public void reset() {
            calls.set(0);
        }
    }
}

