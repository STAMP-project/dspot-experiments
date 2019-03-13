package com.baeldung.persistence.save;


import com.baeldung.persistence.model.Person;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Assert;
import org.junit.Test;


/**
 * Testing specific implementation details for different methods:
 * persist, save, merge, update, saveOrUpdate.
 */
public class SaveMethodsIntegrationTest {
    private static SessionFactory sessionFactory;

    private Session session;

    @Test
    public void whenPersistTransient_thenSavedToDatabaseOnCommit() {
        Person person = new Person();
        person.setName("John");
        session.persist(person);
        session.getTransaction().commit();
        session.close();
        session = SaveMethodsIntegrationTest.sessionFactory.openSession();
        session.beginTransaction();
        Assert.assertNotNull(session.get(Person.class, person.getId()));
    }

    @Test
    public void whenPersistPersistent_thenNothingHappens() {
        Person person = new Person();
        person.setName("John");
        session.persist(person);
        Long id1 = person.getId();
        session.persist(person);
        Long id2 = person.getId();
        Assert.assertEquals(id1, id2);
    }

    @Test(expected = HibernateException.class)
    public void whenPersistDetached_thenThrowsException() {
        Person person = new Person();
        person.setName("John");
        session.persist(person);
        session.evict(person);
        session.persist(person);
    }

    @Test
    public void whenSaveTransient_thenIdGeneratedImmediately() {
        Person person = new Person();
        person.setName("John");
        Assert.assertNull(person.getId());
        Long id = ((Long) (session.save(person)));
        Assert.assertNotNull(id);
        session.getTransaction().commit();
        session.close();
        Assert.assertEquals(id, person.getId());
        session = SaveMethodsIntegrationTest.sessionFactory.openSession();
        session.beginTransaction();
        Assert.assertNotNull(session.get(Person.class, person.getId()));
    }

    @Test
    public void whenSavePersistent_thenNothingHappens() {
        Person person = new Person();
        person.setName("John");
        Long id1 = ((Long) (session.save(person)));
        Long id2 = ((Long) (session.save(person)));
        Assert.assertEquals(id1, id2);
    }

    @Test
    public void whenSaveDetached_thenNewInstancePersisted() {
        Person person = new Person();
        person.setName("John");
        Long id1 = ((Long) (session.save(person)));
        session.evict(person);
        Long id2 = ((Long) (session.save(person)));
        Assert.assertNotEquals(id1, id2);
    }

    @Test
    public void whenMergeDetached_thenEntityUpdatedFromDatabase() {
        Person person = new Person();
        person.setName("John");
        session.save(person);
        session.evict(person);
        person.setName("Mary");
        Person mergedPerson = ((Person) (session.merge(person)));
        Assert.assertNotSame(person, mergedPerson);
        Assert.assertEquals("Mary", mergedPerson.getName());
    }

    @Test
    public void whenMergeTransient_thenNewEntitySavedToDatabase() {
        Person person = new Person();
        person.setName("John");
        Person mergedPerson = ((Person) (session.merge(person)));
        session.getTransaction().commit();
        session.beginTransaction();
        Assert.assertNull(person.getId());
        Assert.assertNotNull(mergedPerson.getId());
    }

    @Test
    public void whenMergePersistent_thenReturnsSameObject() {
        Person person = new Person();
        person.setName("John");
        session.save(person);
        Person mergedPerson = ((Person) (session.merge(person)));
        Assert.assertSame(person, mergedPerson);
    }

    @Test
    public void whenUpdateDetached_thenEntityUpdatedFromDatabase() {
        Person person = new Person();
        person.setName("John");
        session.save(person);
        session.evict(person);
        person.setName("Mary");
        session.update(person);
        Assert.assertEquals("Mary", person.getName());
    }

    @Test(expected = HibernateException.class)
    public void whenUpdateTransient_thenThrowsException() {
        Person person = new Person();
        person.setName("John");
        session.update(person);
    }

    @Test
    public void whenUpdatePersistent_thenNothingHappens() {
        Person person = new Person();
        person.setName("John");
        session.save(person);
        session.update(person);
    }

    @Test
    public void whenSaveOrUpdateDetached_thenEntityUpdatedFromDatabase() {
        Person person = new Person();
        person.setName("John");
        session.save(person);
        session.evict(person);
        person.setName("Mary");
        session.saveOrUpdate(person);
        Assert.assertEquals("Mary", person.getName());
    }

    @Test
    public void whenSaveOrUpdateTransient_thenSavedToDatabaseOnCommit() {
        Person person = new Person();
        person.setName("John");
        session.saveOrUpdate(person);
        session.getTransaction().commit();
        session.close();
        session = SaveMethodsIntegrationTest.sessionFactory.openSession();
        session.beginTransaction();
        Assert.assertNotNull(session.get(Person.class, person.getId()));
    }

    @Test
    public void whenSaveOrUpdatePersistent_thenNothingHappens() {
        Person person = new Person();
        person.setName("John");
        session.save(person);
        session.saveOrUpdate(person);
    }
}

