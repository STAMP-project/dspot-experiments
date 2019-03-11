package com.baeldung.hibernate.converter;


import com.baeldung.hibernate.pojo.Person;
import com.baeldung.hibernate.pojo.PersonName;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.Assert;
import org.junit.Test;


public class PersonNameConverterUnitTest {
    private Session session;

    private Transaction transaction;

    @Test
    public void givenPersonName_WhenSaving_ThenNameAndSurnameConcat() {
        final String name = "name";
        final String surname = "surname";
        PersonName personName = new PersonName();
        personName.setName(name);
        personName.setSurname(surname);
        Person person = new Person();
        person.setPersonName(personName);
        Long id = ((Long) (session.save(person)));
        session.flush();
        session.clear();
        String dbPersonName = ((String) (session.createNativeQuery("select p.personName from PersonTable p where p.id = :id").setParameter("id", id).getSingleResult()));
        Assert.assertEquals(((surname + ", ") + name), dbPersonName);
        Person dbPerson = session.createNativeQuery("select * from PersonTable p where p.id = :id", Person.class).setParameter("id", id).getSingleResult();
        Assert.assertEquals(dbPerson.getPersonName().getName(), name);
        Assert.assertEquals(dbPerson.getPersonName().getSurname(), surname);
    }

    @Test
    public void givenPersonNameNull_WhenSaving_ThenNullStored() {
        final String name = null;
        final String surname = null;
        PersonName personName = new PersonName();
        personName.setName(name);
        personName.setSurname(surname);
        Person person = new Person();
        person.setPersonName(personName);
        Long id = ((Long) (session.save(person)));
        session.flush();
        session.clear();
        String dbPersonName = ((String) (session.createNativeQuery("select p.personName from PersonTable p where p.id = :id").setParameter("id", id).getSingleResult()));
        Assert.assertEquals("", dbPersonName);
        Person dbPerson = session.createNativeQuery("select * from PersonTable p where p.id = :id", Person.class).setParameter("id", id).getSingleResult();
        Assert.assertEquals(dbPerson.getPersonName(), null);
    }

    @Test
    public void givenPersonNameWithoutName_WhenSaving_ThenNotNameStored() {
        final String name = null;
        final String surname = "surname";
        PersonName personName = new PersonName();
        personName.setName(name);
        personName.setSurname(surname);
        Person person = new Person();
        person.setPersonName(personName);
        Long id = ((Long) (session.save(person)));
        session.flush();
        session.clear();
        String dbPersonName = ((String) (session.createNativeQuery("select p.personName from PersonTable p where p.id = :id").setParameter("id", id).getSingleResult()));
        Assert.assertEquals("surname, ", dbPersonName);
        Person dbPerson = session.createNativeQuery("select * from PersonTable p where p.id = :id", Person.class).setParameter("id", id).getSingleResult();
        Assert.assertEquals(dbPerson.getPersonName().getName(), name);
        Assert.assertEquals(dbPerson.getPersonName().getSurname(), surname);
    }

    @Test
    public void givenPersonNameWithoutSurName_WhenSaving_ThenNotSurNameStored() {
        final String name = "name";
        final String surname = null;
        PersonName personName = new PersonName();
        personName.setName(name);
        personName.setSurname(surname);
        Person person = new Person();
        person.setPersonName(personName);
        Long id = ((Long) (session.save(person)));
        session.flush();
        session.clear();
        String dbPersonName = ((String) (session.createNativeQuery("select p.personName from PersonTable p where p.id = :id").setParameter("id", id).getSingleResult()));
        Assert.assertEquals("name", dbPersonName);
        Person dbPerson = session.createNativeQuery("select * from PersonTable p where p.id = :id", Person.class).setParameter("id", id).getSingleResult();
        Assert.assertEquals(dbPerson.getPersonName().getName(), name);
        Assert.assertEquals(dbPerson.getPersonName().getSurname(), surname);
    }

    @Test
    public void givenPersonNameEmptyFields_WhenSaving_ThenFielsNotStored() {
        final String name = "";
        final String surname = "";
        PersonName personName = new PersonName();
        personName.setName(name);
        personName.setSurname(surname);
        Person person = new Person();
        person.setPersonName(personName);
        Long id = ((Long) (session.save(person)));
        session.flush();
        session.clear();
        String dbPersonName = ((String) (session.createNativeQuery("select p.personName from PersonTable p where p.id = :id").setParameter("id", id).getSingleResult()));
        Assert.assertEquals("", dbPersonName);
        Person dbPerson = session.createNativeQuery("select * from PersonTable p where p.id = :id", Person.class).setParameter("id", id).getSingleResult();
        Assert.assertEquals(dbPerson.getPersonName(), null);
    }
}

