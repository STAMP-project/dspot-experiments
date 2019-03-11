/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.userguide.mapping.identifier;


import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
// end::identifiers-basic-idclass-mapping-example[]
public class IdClassTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void test() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.userguide.mapping.identifier.SystemUser systemUser = entityManager.find(.class, new org.hibernate.userguide.mapping.identifier.PK("Hibernate Forum", "vlad"));
            assertEquals("Vlad Mihalcea", systemUser.getName());
        });
    }

    // tag::identifiers-basic-idclass-mapping-example[]
    // tag::identifiers-basic-idclass-mapping-example[]
    @Entity(name = "SystemUser")
    @IdClass(IdClassTest.PK.class)
    public static class SystemUser {
        @Id
        private String subsystem;

        @Id
        private String username;

        private String name;

        public IdClassTest.PK getId() {
            return new IdClassTest.PK(subsystem, username);
        }

        public void setId(IdClassTest.PK id) {
            this.subsystem = id.getSubsystem();
            this.username = id.getUsername();
        }

        // Getters and setters are omitted for brevity
        // end::identifiers-basic-idclass-mapping-example[]
        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class PK implements Serializable {
        private String subsystem;

        private String username;

        public PK(String subsystem, String username) {
            this.subsystem = subsystem;
            this.username = username;
        }

        private PK() {
        }

        // Getters and setters are omitted for brevity
        // end::identifiers-basic-idclass-mapping-example[]
        public String getSubsystem() {
            return subsystem;
        }

        public void setSubsystem(String subsystem) {
            this.subsystem = subsystem;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        // tag::identifiers-basic-idclass-mapping-example[]
        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            IdClassTest.PK pk = ((IdClassTest.PK) (o));
            return (Objects.equals(subsystem, pk.subsystem)) && (Objects.equals(username, pk.username));
        }

        @Override
        public int hashCode() {
            return Objects.hash(subsystem, username);
        }
    }
}

