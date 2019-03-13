package org.hibernate.userguide.pc;


import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.junit.Test;


/**
 *
 *
 * @author F?bio Takeo Ueno
 */
public class CascadeMergeTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void mergeTest() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            Person person = new Person();
            person.setId(1L);
            person.setName("John Doe");
            Phone phone = new Phone();
            phone.setId(1L);
            phone.setNumber("123-456-7890");
            person.addPhone(phone);
            entityManager.persist(person);
        });
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            // tag::pc-cascade-merge-example[]
            Phone phone = entityManager.find(.class, 1L);
            Person person = phone.getOwner();
            person.setName("John Doe Jr.");
            phone.setNumber("987-654-3210");
            entityManager.clear();
            entityManager.merge(person);
            // end::pc-cascade-merge-example[]
        });
    }
}

