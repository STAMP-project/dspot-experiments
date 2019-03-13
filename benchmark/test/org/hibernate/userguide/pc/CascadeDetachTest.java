package org.hibernate.userguide.pc;


import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.junit.Test;


/**
 *
 *
 * @author F?bio Takeo Ueno
 */
public class CascadeDetachTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void detachTest() {
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
            // tag::pc-cascade-detach-example[]
            Person person = entityManager.find(.class, 1L);
            assertEquals(1, person.getPhones().size());
            Phone phone = person.getPhones().get(0);
            assertTrue(entityManager.contains(person));
            assertTrue(entityManager.contains(phone));
            entityManager.detach(person);
            assertFalse(entityManager.contains(person));
            assertFalse(entityManager.contains(phone));
            // end::pc-cascade-detach-example[]
        });
    }
}

