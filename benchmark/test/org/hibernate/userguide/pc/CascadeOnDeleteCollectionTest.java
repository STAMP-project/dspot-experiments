package org.hibernate.userguide.pc;


import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
// end::pc-cascade-on-delete-collection-mapping-Phone-example[]
public class CascadeOnDeleteCollectionTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void test() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.userguide.pc.Person person = new org.hibernate.userguide.pc.Person();
            person.setId(1L);
            person.setName("John Doe");
            entityManager.persist(person);
            org.hibernate.userguide.pc.Phone phone1 = new org.hibernate.userguide.pc.Phone();
            phone1.setId(1L);
            phone1.setNumber("123-456-7890");
            phone1.setOwner(person);
            person.addPhone(phone1);
            org.hibernate.userguide.pc.Phone phone2 = new org.hibernate.userguide.pc.Phone();
            phone2.setId(2L);
            phone2.setNumber("101-010-1234");
            phone2.setOwner(person);
            person.addPhone(phone2);
        });
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            // tag::pc-cascade-on-delete-collection-example[]
            org.hibernate.userguide.pc.Person person = entityManager.find(.class, 1L);
            entityManager.remove(person);
            // end::pc-cascade-on-delete-collection-example[]
        });
    }

    // tag::pc-cascade-on-delete-collection-mapping-Person-example[]
    // tag::pc-cascade-on-delete-collection-mapping-Person-example[]
    @Entity(name = "Person")
    public static class Person {
        @Id
        private Long id;

        private String name;

        @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
        @OnDelete(action = OnDeleteAction.CASCADE)
        private List<CascadeOnDeleteCollectionTest.Phone> phones = new ArrayList<>();

        // Getters and setters are omitted for brevity
        // end::pc-cascade-on-delete-collection-mapping-Person-example[]
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

        public void addPhone(CascadeOnDeleteCollectionTest.Phone phone) {
            phone.setOwner(this);
            phones.add(phone);
        }
    }

    // end::pc-cascade-on-delete-collection-mapping-Person-example[]
    // tag::pc-cascade-on-delete-collection-mapping-Phone-example[]
    // tag::pc-cascade-on-delete-collection-mapping-Phone-example[]
    @Entity(name = "Phone")
    public static class Phone {
        @Id
        private Long id;

        @Column(name = "`number`")
        private String number;

        @ManyToOne(fetch = FetchType.LAZY)
        private CascadeOnDeleteCollectionTest.Person owner;

        // Getters and setters are omitted for brevity
        // end::pc-cascade-on-delete-collection-mapping-Phone-example[]
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

        public CascadeOnDeleteCollectionTest.Person getOwner() {
            return owner;
        }

        public void setOwner(CascadeOnDeleteCollectionTest.Person owner) {
            this.owner = owner;
        }
    }
}

