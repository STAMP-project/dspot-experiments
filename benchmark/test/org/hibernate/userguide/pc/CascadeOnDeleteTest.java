package org.hibernate.userguide.pc;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
// end::pc-cascade-on-delete-mapping-Phone-example[]
public class CascadeOnDeleteTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void test() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.userguide.pc.Person person = new org.hibernate.userguide.pc.Person();
            person.setId(1L);
            person.setName("John Doe");
            entityManager.persist(person);
            org.hibernate.userguide.pc.Phone phone = new org.hibernate.userguide.pc.Phone();
            phone.setId(1L);
            phone.setNumber("123-456-7890");
            phone.setOwner(person);
            entityManager.persist(phone);
        });
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            // tag::pc-cascade-on-delete-example[]
            org.hibernate.userguide.pc.Person person = entityManager.find(.class, 1L);
            entityManager.remove(person);
            // end::pc-cascade-on-delete-example[]
        });
    }

    // tag::pc-cascade-on-delete-mapping-Person-example[]
    // tag::pc-cascade-on-delete-mapping-Person-example[]
    @Entity(name = "Person")
    public static class Person {
        @Id
        private Long id;

        private String name;

        // Getters and setters are omitted for brevity
        // end::pc-cascade-on-delete-mapping-Person-example[]
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

    // end::pc-cascade-on-delete-mapping-Person-example[]
    // tag::pc-cascade-on-delete-mapping-Phone-example[]
    // tag::pc-cascade-on-delete-mapping-Phone-example[]
    @Entity(name = "Phone")
    public static class Phone {
        @Id
        private Long id;

        @Column(name = "`number`")
        private String number;

        @ManyToOne(fetch = FetchType.LAZY)
        @OnDelete(action = OnDeleteAction.CASCADE)
        private CascadeOnDeleteTest.Person owner;

        // Getters and setters are omitted for brevity
        // end::pc-cascade-on-delete-mapping-Phone-example[]
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }

        public CascadeOnDeleteTest.Person getOwner() {
            return owner;
        }

        public void setOwner(CascadeOnDeleteTest.Person owner) {
            this.owner = owner;
        }
    }
}

