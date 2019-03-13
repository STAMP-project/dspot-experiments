/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.graphs;


import java.util.HashSet;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
@TestForIssue(jiraKey = "HHH-12476")
public class EntityGraphNativeQueryTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void testQuery() {
        EntityGraphNativeQueryTest.Foo foo = TransactionUtil.doInJPA(this::entityManagerFactory, ( em) -> {
            EntityGraph<org.hibernate.jpa.test.graphs.Foo> fooGraph = em.createEntityGraph(.class);
            fooGraph.addAttributeNodes("bar", "baz");
            return em.createQuery("select f from Foo f", .class).setHint("javax.persistence.loadgraph", fooGraph).getSingleResult();
        });
        Assert.assertNotNull(foo.bar);
        Assert.assertNotNull(foo.baz);
    }

    @Test
    public void testNativeQueryLoadGraph() {
        try {
            TransactionUtil.doInJPA(this::entityManagerFactory, ( em) -> {
                EntityGraph<org.hibernate.jpa.test.graphs.Foo> fooGraph = em.createEntityGraph(.class);
                fooGraph.addAttributeNodes("bar", "baz");
                em.createNativeQuery(("select " + ((("	f.id as id, " + "	f.bar_id as bar_id, ") + "	f.baz_id as baz_id ") + "from Foo f")), .class).setHint(QueryHints.HINT_LOADGRAPH, fooGraph).getSingleResult();
                fail("Should throw exception");
            });
        } catch (Exception e) {
            Assert.assertEquals("A native SQL query cannot use EntityGraphs", e.getMessage());
        }
    }

    @Test
    public void testNativeQueryFetchGraph() {
        try {
            TransactionUtil.doInJPA(this::entityManagerFactory, ( em) -> {
                EntityGraph<org.hibernate.jpa.test.graphs.Foo> fooGraph = em.createEntityGraph(.class);
                fooGraph.addAttributeNodes("bar", "baz");
                em.createNativeQuery(("select " + ((("	f.id as id, " + "	f.bar_id as bar_id, ") + "	f.baz_id as baz_id ") + "from Foo f")), .class).setHint(QueryHints.HINT_FETCHGRAPH, fooGraph).getSingleResult();
                fail("Should throw exception");
            });
        } catch (Exception e) {
            Assert.assertEquals("A native SQL query cannot use EntityGraphs", e.getMessage());
        }
    }

    @Entity(name = "Foo")
    public static class Foo {
        @Id
        @GeneratedValue
        public Integer id;

        @ManyToOne(fetch = FetchType.LAZY)
        public EntityGraphNativeQueryTest.Bar bar;

        @ManyToOne(fetch = FetchType.LAZY)
        public EntityGraphNativeQueryTest.Baz baz;
    }

    @Entity(name = "Bar")
    public static class Bar {
        @Id
        @GeneratedValue
        public Integer id;

        @OneToMany(mappedBy = "bar")
        public Set<EntityGraphNativeQueryTest.Foo> foos = new HashSet<>();
    }

    @Entity(name = "Baz")
    public static class Baz {
        @Id
        @GeneratedValue
        public Integer id;

        @OneToMany(mappedBy = "baz")
        public Set<EntityGraphNativeQueryTest.Foo> foos = new HashSet<>();
    }
}

