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
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
// end::identifiers-basic-embeddedid-manytoone-mapping-example[]
public class EmbeddedIdManyToOneTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void test() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.userguide.mapping.identifier.Subsystem subsystem = entityManager.find(.class, "Hibernate Forum");
            org.hibernate.userguide.mapping.identifier.SystemUser systemUser = entityManager.find(.class, new org.hibernate.userguide.mapping.identifier.PK(subsystem, "vlad"));
            assertEquals("Vlad Mihalcea", systemUser.getName());
        });
    }

    // tag::identifiers-basic-embeddedid-manytoone-mapping-example[]
    // tag::identifiers-basic-embeddedid-manytoone-mapping-example[]
    @Entity(name = "SystemUser")
    public static class SystemUser {
        @EmbeddedId
        private EmbeddedIdManyToOneTest.PK pk;

        private String name;

        // Getters and setters are omitted for brevity
        // end::identifiers-basic-embeddedid-manytoone-mapping-example[]
        public EmbeddedIdManyToOneTest.PK getPk() {
            return pk;
        }

        public void setPk(EmbeddedIdManyToOneTest.PK pk) {
            this.pk = pk;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    // tag::identifiers-basic-embeddedid-manytoone-mapping-example[]
    @Entity(name = "Subsystem")
    public static class Subsystem {
        @Id
        private String id;

        private String description;

        // Getters and setters are omitted for brevity
        // end::identifiers-basic-embeddedid-manytoone-mapping-example[]
        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }

    @Embeddable
    public static class PK implements Serializable {
        @ManyToOne(fetch = FetchType.LAZY)
        private EmbeddedIdManyToOneTest.Subsystem subsystem;

        private String username;

        public PK(EmbeddedIdManyToOneTest.Subsystem subsystem, String username) {
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
            EmbeddedIdManyToOneTest.PK pk = ((EmbeddedIdManyToOneTest.PK) (o));
            return (Objects.equals(subsystem, pk.subsystem)) && (Objects.equals(username, pk.username));
        }

        @Override
        public int hashCode() {
            return Objects.hash(subsystem, username);
        }
    }
}

