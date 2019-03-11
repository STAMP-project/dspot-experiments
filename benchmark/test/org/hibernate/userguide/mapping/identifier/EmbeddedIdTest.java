/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.userguide.mapping.identifier;


import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
// end::identifiers-basic-embeddedid-mapping-example[]
public class EmbeddedIdTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void test() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.userguide.mapping.identifier.SystemUser systemUser = entityManager.find(.class, new org.hibernate.userguide.mapping.identifier.PK("Hibernate Forum", "vlad"));
            assertEquals("Vlad Mihalcea", systemUser.getName());
        });
    }

    // tag::identifiers-basic-embeddedid-mapping-example[]
    // tag::identifiers-basic-embeddedid-mapping-example[]
    @Entity(name = "SystemUser")
    public static class SystemUser {
        @EmbeddedId
        private EmbeddedIdTest.PK pk;

        private String name;

        // Getters and setters are omitted for brevity
        // end::identifiers-basic-embeddedid-mapping-example[]
        public EmbeddedIdTest.PK getPk() {
            return pk;
        }

        public void setPk(EmbeddedIdTest.PK pk) {
            this.pk = pk;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    @Embeddable
    public static class PK implements Serializable {
        private String subsystem;

        private String username;

        public PK(String subsystem, String username) {
            this.subsystem = subsystem;
            this.username = username;
        }

        private PK() {
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            EmbeddedIdTest.PK pk = ((EmbeddedIdTest.PK) (o));
            return (Objects.equals(subsystem, pk.subsystem)) && (Objects.equals(username, pk.username));
        }

        @Override
        public int hashCode() {
            return Objects.hash(subsystem, username);
        }
    }
}

