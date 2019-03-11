/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.joinedsubclass;


import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
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
public class JoinedSubclassWithIgnoredExplicitDiscriminatorTest extends BaseCoreFunctionalTestCase {
    @Entity(name = "Animal")
    @Table(name = "animal")
    @Inheritance(strategy = InheritanceType.JOINED)
    @DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
    @DiscriminatorValue("???animal???")
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
    @DiscriminatorValue("cat")
    public static class Cat extends JoinedSubclassWithIgnoredExplicitDiscriminatorTest.Animal {
        public Cat() {
            super();
        }

        public Cat(Integer id) {
            super(id);
        }
    }

    @Entity(name = "Dog")
    @DiscriminatorValue("dog")
    public static class Dog extends JoinedSubclassWithIgnoredExplicitDiscriminatorTest.Animal {
        public Dog() {
            super();
        }

        public Dog(Integer id) {
            super(id);
        }
    }

    @Test
    public void metadataAssertions() {
        EntityPersister p = sessionFactory().getEntityPersister(JoinedSubclassWithIgnoredExplicitDiscriminatorTest.Dog.class.getName());
        Assert.assertNotNull(p);
        final JoinedSubclassEntityPersister dogPersister = ExtraAssertions.assertTyping(JoinedSubclassEntityPersister.class, p);
        Assert.assertEquals("integer", dogPersister.getDiscriminatorType().getName());
        Assert.assertEquals("clazz_", dogPersister.getDiscriminatorColumnName());
        Assert.assertTrue(Integer.class.isInstance(dogPersister.getDiscriminatorValue()));
        p = sessionFactory().getEntityPersister(JoinedSubclassWithIgnoredExplicitDiscriminatorTest.Cat.class.getName());
        Assert.assertNotNull(p);
        final JoinedSubclassEntityPersister catPersister = ExtraAssertions.assertTyping(JoinedSubclassEntityPersister.class, p);
        Assert.assertEquals("integer", catPersister.getDiscriminatorType().getName());
        Assert.assertEquals("clazz_", catPersister.getDiscriminatorColumnName());
        Assert.assertTrue(Integer.class.isInstance(catPersister.getDiscriminatorValue()));
    }

    @Test
    public void basicUsageTest() {
        Session session = openSession();
        session.beginTransaction();
        session.save(new JoinedSubclassWithIgnoredExplicitDiscriminatorTest.Cat(1));
        session.save(new JoinedSubclassWithIgnoredExplicitDiscriminatorTest.Dog(2));
        session.getTransaction().commit();
        session.close();
        session = openSession();
        session.beginTransaction();
        session.createQuery("from Animal").list();
        JoinedSubclassWithIgnoredExplicitDiscriminatorTest.Cat cat = ((JoinedSubclassWithIgnoredExplicitDiscriminatorTest.Cat) (session.get(JoinedSubclassWithIgnoredExplicitDiscriminatorTest.Cat.class, 1)));
        Assert.assertNotNull(cat);
        session.delete(cat);
        JoinedSubclassWithIgnoredExplicitDiscriminatorTest.Dog dog = ((JoinedSubclassWithIgnoredExplicitDiscriminatorTest.Dog) (session.get(JoinedSubclassWithIgnoredExplicitDiscriminatorTest.Dog.class, 2)));
        Assert.assertNotNull(dog);
        session.delete(dog);
        session.getTransaction().commit();
        session.close();
    }
}

