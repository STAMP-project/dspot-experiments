/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.where.annotations;


import java.util.HashSet;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.hibernate.annotations.Where;
import org.hibernate.annotations.WhereJoinTable;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


/**
 * Tests association collections with AvailableSettings.USE_ENTITY_WHERE_CLAUSE_FOR_COLLECTIONS = false
 *
 * @author Gail Badner
 */
public class EagerToManyWhereDontUseClassWhereTest extends BaseNonConfigCoreFunctionalTestCase {
    @Test
    @TestForIssue(jiraKey = "HHH-13011")
    public void testAssociatedWhereClause() {
        EagerToManyWhereDontUseClassWhereTest.Product product = new EagerToManyWhereDontUseClassWhereTest.Product();
        EagerToManyWhereDontUseClassWhereTest.Category flowers = new EagerToManyWhereDontUseClassWhereTest.Category();
        flowers.id = 1;
        flowers.name = "flowers";
        flowers.description = "FLOWERS";
        product.categoriesOneToMany.add(flowers);
        product.categoriesWithDescOneToMany.add(flowers);
        product.categoriesManyToMany.add(flowers);
        product.categoriesWithDescManyToMany.add(flowers);
        product.categoriesWithDescIdLt4ManyToMany.add(flowers);
        EagerToManyWhereDontUseClassWhereTest.Category vegetables = new EagerToManyWhereDontUseClassWhereTest.Category();
        vegetables.id = 2;
        vegetables.name = "vegetables";
        vegetables.description = "VEGETABLES";
        product.categoriesOneToMany.add(vegetables);
        product.categoriesWithDescOneToMany.add(vegetables);
        product.categoriesManyToMany.add(vegetables);
        product.categoriesWithDescManyToMany.add(vegetables);
        product.categoriesWithDescIdLt4ManyToMany.add(vegetables);
        EagerToManyWhereDontUseClassWhereTest.Category dogs = new EagerToManyWhereDontUseClassWhereTest.Category();
        dogs.id = 3;
        dogs.name = "dogs";
        dogs.description = null;
        product.categoriesOneToMany.add(dogs);
        product.categoriesWithDescOneToMany.add(dogs);
        product.categoriesManyToMany.add(dogs);
        product.categoriesWithDescManyToMany.add(dogs);
        product.categoriesWithDescIdLt4ManyToMany.add(dogs);
        EagerToManyWhereDontUseClassWhereTest.Category building = new EagerToManyWhereDontUseClassWhereTest.Category();
        building.id = 4;
        building.name = "building";
        building.description = "BUILDING";
        product.categoriesOneToMany.add(building);
        product.categoriesWithDescOneToMany.add(building);
        product.categoriesManyToMany.add(building);
        product.categoriesWithDescManyToMany.add(building);
        product.categoriesWithDescIdLt4ManyToMany.add(building);
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            session.persist(flowers);
            session.persist(vegetables);
            session.persist(dogs);
            session.persist(building);
            session.persist(product);
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.test.where.annotations.Product p = session.get(.class, product.id);
            assertNotNull(p);
            assertEquals(4, p.categoriesOneToMany.size());
            checkIds(p.categoriesOneToMany, new Integer[]{ 1, 2, 3, 4 });
            assertEquals(3, p.categoriesWithDescOneToMany.size());
            checkIds(p.categoriesWithDescOneToMany, new Integer[]{ 1, 2, 4 });
            assertEquals(4, p.categoriesManyToMany.size());
            checkIds(p.categoriesManyToMany, new Integer[]{ 1, 2, 3, 4 });
            assertEquals(3, p.categoriesWithDescManyToMany.size());
            checkIds(p.categoriesWithDescManyToMany, new Integer[]{ 1, 2, 4 });
            assertEquals(2, p.categoriesWithDescIdLt4ManyToMany.size());
            checkIds(p.categoriesWithDescIdLt4ManyToMany, new Integer[]{ 1, 2 });
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.test.where.annotations.Category c = session.get(.class, flowers.id);
            assertNotNull(c);
            c.inactive = 1;
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.test.where.annotations.Category c = session.get(.class, flowers.id);
            assertNull(c);
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.test.where.annotations.Product p = session.get(.class, product.id);
            assertNotNull(p);
            assertEquals(4, p.categoriesOneToMany.size());
            checkIds(p.categoriesOneToMany, new Integer[]{ 1, 2, 3, 4 });
            assertEquals(3, p.categoriesWithDescOneToMany.size());
            checkIds(p.categoriesWithDescOneToMany, new Integer[]{ 1, 2, 4 });
            assertEquals(4, p.categoriesManyToMany.size());
            checkIds(p.categoriesManyToMany, new Integer[]{ 1, 2, 3, 4 });
            assertEquals(3, p.categoriesWithDescManyToMany.size());
            checkIds(p.categoriesWithDescManyToMany, new Integer[]{ 1, 2, 4 });
            assertEquals(2, p.categoriesWithDescIdLt4ManyToMany.size());
            checkIds(p.categoriesWithDescIdLt4ManyToMany, new Integer[]{ 1, 2 });
        });
    }

    @Entity(name = "Product")
    public static class Product {
        @Id
        @GeneratedValue
        private int id;

        @OneToMany(fetch = FetchType.EAGER)
        @JoinColumn
        private Set<EagerToManyWhereDontUseClassWhereTest.Category> categoriesOneToMany = new HashSet<>();

        @OneToMany(fetch = FetchType.EAGER)
        @JoinColumn
        @Where(clause = "description is not null")
        private Set<EagerToManyWhereDontUseClassWhereTest.Category> categoriesWithDescOneToMany = new HashSet<>();

        @ManyToMany(fetch = FetchType.EAGER)
        @JoinTable(name = "categoriesManyToMany")
        private Set<EagerToManyWhereDontUseClassWhereTest.Category> categoriesManyToMany = new HashSet<>();

        @ManyToMany(fetch = FetchType.EAGER)
        @JoinTable(name = "categoriesWithDescManyToMany", inverseJoinColumns = { @JoinColumn(name = "categoryId") })
        @Where(clause = "description is not null")
        private Set<EagerToManyWhereDontUseClassWhereTest.Category> categoriesWithDescManyToMany = new HashSet<>();

        @ManyToMany(fetch = FetchType.EAGER)
        @JoinTable(name = "categoriesWithDescIdLt4MToM", inverseJoinColumns = { @JoinColumn(name = "categoryId") })
        @Where(clause = "description is not null")
        @WhereJoinTable(clause = "categoryId < 4")
        private Set<EagerToManyWhereDontUseClassWhereTest.Category> categoriesWithDescIdLt4ManyToMany = new HashSet<>();
    }

    @Entity(name = "Category")
    @Table(name = "CATEGORY")
    @Where(clause = "inactive = 0")
    public static class Category {
        @Id
        private int id;

        private String name;

        private String description;

        private int inactive;
    }
}

