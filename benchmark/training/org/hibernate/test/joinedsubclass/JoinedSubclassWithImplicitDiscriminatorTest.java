/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.joinedsubclass;


import Ejb3DiscriminatorColumn.DEFAULT_DISCRIMINATOR_COLUMN_NAME;
import Ejb3DiscriminatorColumn.DEFAULT_DISCRIMINATOR_TYPE;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import org.hibernate.Session;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.persister.entity.JoinedSubclassEntityPersister;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.testing.junit4.ExtraAssertions;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
@TestForIssue(jiraKey = "HHH-6911")
public class JoinedSubclassWithImplicitDiscriminatorTest extends BaseCoreFunctionalTestCase {
    @Entity(name = "Animal")
    @Table(name = "animal")
    @Inheritance(strategy = InheritanceType.JOINED)
    public abstract static class Animal {
        @Id
        public Integer id;

        protected Animal() {
        }

        protected Animal(Integer id) {
            this.id = id;
        }
    }

    @Entity(name = "Cat")
    public static class Cat extends JoinedSubclassWithImplicitDiscriminatorTest.Animal {
        public Cat() {
            super();
        }

        public Cat(Integer id) {
            super(id);
        }
    }

    @Entity(name = "Dog")
    public static class Dog extends JoinedSubclassWithImplicitDiscriminatorTest.Animal {
        public Dog() {
            super();
        }

        public Dog(Integer id) {
            super(id);
        }
    }

    @Test
    public void metadataAssertions() {
        EntityPersister p = sessionFactory().getEntityPersister(JoinedSubclassWithImplicitDiscriminatorTest.Dog.class.getName());
        Assert.assertNotNull(p);
        final JoinedSubclassEntityPersister dogPersister = ExtraAssertions.assertTyping(JoinedSubclassEntityPersister.class, p);
        Assert.assertEquals(DEFAULT_DISCRIMINATOR_TYPE, dogPersister.getDiscriminatorType().getName());
        Assert.assertEquals(DEFAULT_DISCRIMINATOR_COLUMN_NAME, dogPersister.getDiscriminatorColumnName());
        Assert.assertEquals("Dog", dogPersister.getDiscriminatorValue());
        p = sessionFactory().getEntityPersister(JoinedSubclassWithImplicitDiscriminatorTest.Cat.class.getName());
        Assert.assertNotNull(p);
        final JoinedSubclassEntityPersister catPersister = ExtraAssertions.assertTyping(JoinedSubclassEntityPersister.class, p);
        Assert.assertEquals(DEFAULT_DISCRIMINATOR_TYPE, catPersister.getDiscriminatorType().getName());
        Assert.assertEquals(DEFAULT_DISCRIMINATOR_COLUMN_NAME, catPersister.getDiscriminatorColumnName());
        Assert.assertEquals("Cat", catPersister.getDiscriminatorValue());
    }

    @Test
    public void basicUsageTest() {
        Session session = openSession();
        session.beginTransaction();
        session.save(new JoinedSubclassWithImplicitDiscriminatorTest.Cat(1));
        session.save(new JoinedSubclassWithImplicitDiscriminatorTest.Dog(2));
        session.getTransaction().commit();
        session.close();
        session = openSession();
        session.beginTransaction();
        session.createQuery("from Animal").list();
        JoinedSubclassWithImplicitDiscriminatorTest.Cat cat = ((JoinedSubclassWithImplicitDiscriminatorTest.Cat) (session.get(JoinedSubclassWithImplicitDiscriminatorTest.Cat.class, 1)));
        Assert.assertNotNull(cat);
        session.delete(cat);
        JoinedSubclassWithImplicitDiscriminatorTest.Dog dog = ((JoinedSubclassWithImplicitDiscriminatorTest.Dog) (session.get(JoinedSubclassWithImplicitDiscriminatorTest.Dog.class, 2)));
        Assert.assertNotNull(dog);
        session.delete(dog);
        session.getTransaction().commit();
        session.close();
    }
}

