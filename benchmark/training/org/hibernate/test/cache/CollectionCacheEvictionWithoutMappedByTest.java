/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.cache;


import java.util.ArrayList;
import java.util.List;
import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.hibernate.Session;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Janario Oliveira
 */
public class CollectionCacheEvictionWithoutMappedByTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testCollectionCacheEvictionInsert() {
        CollectionCacheEvictionWithoutMappedByTest.People people = createPeople();
        people = initCache(people.id);
        Session session = openSession();
        session.beginTransaction();
        people = session.get(CollectionCacheEvictionWithoutMappedByTest.People.class, people.id);
        CollectionCacheEvictionWithoutMappedByTest.Person person = new CollectionCacheEvictionWithoutMappedByTest.Person();
        session.save(person);
        people.people.add(person);
        session.getTransaction().commit();
        session.close();
        session = openSession();
        people = session.get(CollectionCacheEvictionWithoutMappedByTest.People.class, people.id);
        Assert.assertEquals(3, people.people.size());
        session.close();
    }

    @Test
    public void testCollectionCacheEvictionRemove() {
        CollectionCacheEvictionWithoutMappedByTest.People people = createPeople();
        people = initCache(people.id);
        Session session = openSession();
        session.beginTransaction();
        people = session.get(CollectionCacheEvictionWithoutMappedByTest.People.class, people.id);
        CollectionCacheEvictionWithoutMappedByTest.Person person = people.people.remove(0);
        session.delete(person);
        session.getTransaction().commit();
        session.close();
        session = openSession();
        people = session.get(CollectionCacheEvictionWithoutMappedByTest.People.class, people.id);
        Assert.assertEquals(1, people.people.size());
        session.close();
    }

    @Test
    public void testCollectionCacheEvictionUpdate() {
        CollectionCacheEvictionWithoutMappedByTest.People people1 = createPeople();
        people1 = initCache(people1.id);
        CollectionCacheEvictionWithoutMappedByTest.People people2 = createPeople();
        people2 = initCache(people2.id);
        Session session = openSession();
        session.beginTransaction();
        people1 = session.get(CollectionCacheEvictionWithoutMappedByTest.People.class, people1.id);
        people2 = session.get(CollectionCacheEvictionWithoutMappedByTest.People.class, people2.id);
        CollectionCacheEvictionWithoutMappedByTest.Person person1 = people1.people.remove(0);
        CollectionCacheEvictionWithoutMappedByTest.Person person2 = people1.people.remove(0);
        CollectionCacheEvictionWithoutMappedByTest.Person person3 = people2.people.remove(0);
        session.flush();// avoid: Unique index or primary key violation

        people1.people.add(person3);
        people2.people.add(person2);
        people2.people.add(person1);
        session.getTransaction().commit();
        session.close();
        session = openSession();
        people1 = session.get(CollectionCacheEvictionWithoutMappedByTest.People.class, people1.id);
        people2 = session.get(CollectionCacheEvictionWithoutMappedByTest.People.class, people2.id);
        Assert.assertEquals(1, people1.people.size());
        Assert.assertEquals(3, people2.people.size());
        session.close();
    }

    @Entity
    @Table(name = "people_group")
    @Cacheable
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    public static class People {
        @Id
        @GeneratedValue
        private Integer id;

        @OneToMany(cascade = CascadeType.ALL)
        @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
        private List<CollectionCacheEvictionWithoutMappedByTest.Person> people = new ArrayList<CollectionCacheEvictionWithoutMappedByTest.Person>();
    }

    @Entity
    @Table(name = "person")
    @Cacheable
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    public static class Person {
        @Id
        @GeneratedValue
        private Integer id;

        protected Person() {
        }
    }
}

