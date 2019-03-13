/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.sorted;


import FetchMode.JOIN;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.annotations.SortNatural;
import org.hibernate.mapping.Collection;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
import org.hibernate.testing.junit4.ExtraAssertions;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Gavin King
 * @author Brett Meyer
 */
public class SortTest extends BaseNonConfigCoreFunctionalTestCase {
    @Test
    public void testSortedSetDefinitionInHbmXml() {
        final PersistentClass entityMapping = metadata().getEntityBinding(Search.class.getName());
        final Property sortedSetProperty = entityMapping.getProperty("searchResults");
        final Collection sortedSetMapping = ExtraAssertions.assertTyping(Collection.class, sortedSetProperty.getValue());
        Assert.assertTrue("SortedSet mapping not interpreted as sortable", sortedSetMapping.isSorted());
        final Property sortedMapProperty = entityMapping.getProperty("tokens");
        final Collection sortedMapMapping = ExtraAssertions.assertTyping(Collection.class, sortedMapProperty.getValue());
        Assert.assertTrue("SortedMap mapping not interpreted as sortable", sortedMapMapping.isSorted());
    }

    @Test
    @SuppressWarnings({ "unchecked" })
    public void testOrderBy() {
        Search s = new Search("Hibernate");
        s.getSearchResults().add("jboss.com");
        s.getSearchResults().add("hibernate.org");
        s.getSearchResults().add("HiA");
        Session sess = openSession();
        Transaction tx = sess.beginTransaction();
        sess.persist(s);
        sess.flush();
        sess.clear();
        s = ((Search) (sess.createCriteria(Search.class).uniqueResult()));
        Assert.assertFalse(Hibernate.isInitialized(s.getSearchResults()));
        Iterator iter = s.getSearchResults().iterator();
        Assert.assertEquals(iter.next(), "HiA");
        Assert.assertEquals(iter.next(), "hibernate.org");
        Assert.assertEquals(iter.next(), "jboss.com");
        Assert.assertFalse(iter.hasNext());
        sess.clear();
        s = ((Search) (sess.createCriteria(Search.class).setFetchMode("searchResults", JOIN).uniqueResult()));
        Assert.assertTrue(Hibernate.isInitialized(s.getSearchResults()));
        iter = s.getSearchResults().iterator();
        Assert.assertEquals(iter.next(), "HiA");
        Assert.assertEquals(iter.next(), "hibernate.org");
        Assert.assertEquals(iter.next(), "jboss.com");
        Assert.assertFalse(iter.hasNext());
        sess.clear();
        s = ((Search) (sess.createQuery("from Search s left join fetch s.searchResults").uniqueResult()));
        Assert.assertTrue(Hibernate.isInitialized(s.getSearchResults()));
        iter = s.getSearchResults().iterator();
        Assert.assertEquals(iter.next(), "HiA");
        Assert.assertEquals(iter.next(), "hibernate.org");
        Assert.assertEquals(iter.next(), "jboss.com");
        Assert.assertFalse(iter.hasNext());
        sess.delete(s);
        tx.commit();
        sess.close();
    }

    @Test
    @TestForIssue(jiraKey = "HHH-8827")
    public void testSortNatural() {
        Session s = openSession();
        s.beginTransaction();
        SortTest.Owner owner = new SortTest.Owner();
        SortTest.Cat cat1 = new SortTest.Cat();
        SortTest.Cat cat2 = new SortTest.Cat();
        cat1.owner = owner;
        cat1.name = "B";
        cat2.owner = owner;
        cat2.name = "A";
        owner.cats.add(cat1);
        owner.cats.add(cat2);
        s.persist(owner);
        s.getTransaction().commit();
        s.clear();
        s.beginTransaction();
        owner = ((SortTest.Owner) (s.get(SortTest.Owner.class, owner.id)));
        Assert.assertNotNull(owner.cats);
        Assert.assertEquals(owner.cats.size(), 2);
        Assert.assertEquals(owner.cats.first().name, "A");
        Assert.assertEquals(owner.cats.last().name, "B");
        s.getTransaction().commit();
        s.close();
    }

    @Entity
    @Table(name = "Owner")
    private static class Owner {
        @Id
        @GeneratedValue
        private long id;

        @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
        @SortNatural
        private SortedSet<SortTest.Cat> cats = new TreeSet<SortTest.Cat>();
    }

    @Entity
    @Table(name = "Cat")
    private static class Cat implements Comparable<SortTest.Cat> {
        @Id
        @GeneratedValue
        private long id;

        @ManyToOne
        private SortTest.Owner owner;

        private String name;

        @Override
        public int compareTo(SortTest.Cat cat) {
            return this.name.compareTo(cat.name);
        }
    }
}

