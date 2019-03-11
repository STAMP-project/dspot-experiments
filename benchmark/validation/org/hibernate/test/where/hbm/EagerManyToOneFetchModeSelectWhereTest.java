/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.where.hbm;


import java.util.HashSet;
import java.util.Set;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


/**
 *
 *
 * @author Gail Badner
 */
public class EagerManyToOneFetchModeSelectWhereTest extends BaseNonConfigCoreFunctionalTestCase {
    @Test
    @TestForIssue(jiraKey = "HHH-12104")
    public void testAssociatedWhereClause() {
        EagerManyToOneFetchModeSelectWhereTest.Product product = new EagerManyToOneFetchModeSelectWhereTest.Product();
        EagerManyToOneFetchModeSelectWhereTest.Category category = new EagerManyToOneFetchModeSelectWhereTest.Category();
        category.name = "flowers";
        product.category = category;
        product.containedCategory = new EagerManyToOneFetchModeSelectWhereTest.ContainedCategory();
        product.containedCategory.category = category;
        product.containedCategories.add(new EagerManyToOneFetchModeSelectWhereTest.ContainedCategory(category));
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            session.persist(product);
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.test.where.hbm.Product p = session.get(.class, product.id);
            assertNotNull(p);
            assertNotNull(p.category);
            assertNotNull(p.containedCategory.category);
            assertEquals(1, p.containedCategories.size());
            assertSame(p.category, p.containedCategory.category);
            assertSame(p.category, p.containedCategories.iterator().next().category);
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.test.where.hbm.Category c = session.get(.class, category.id);
            assertNotNull(c);
            c.inactive = 1;
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.test.where.hbm.Category c = session.get(.class, category.id);
            assertNull(c);
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            // Entity's where clause is taken into account when to-one associations
            // to that entity is loaded eagerly using FetchMode.SELECT, so Category
            // associations will be null.
            org.hibernate.test.where.hbm.Product p = session.get(.class, product.id);
            assertNotNull(p);
            assertNull(p.category);
            assertNull(p.containedCategory.category);
            assertEquals(1, p.containedCategories.size());
            assertNull(p.containedCategories.iterator().next().category);
        });
    }

    public static class Product {
        private int id;

        private EagerManyToOneFetchModeSelectWhereTest.Category category;

        private EagerManyToOneFetchModeSelectWhereTest.ContainedCategory containedCategory;

        private Set<EagerManyToOneFetchModeSelectWhereTest.ContainedCategory> containedCategories = new HashSet<>();

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public EagerManyToOneFetchModeSelectWhereTest.Category getCategory() {
            return category;
        }

        public void setCategory(EagerManyToOneFetchModeSelectWhereTest.Category category) {
            this.category = category;
        }

        public EagerManyToOneFetchModeSelectWhereTest.ContainedCategory getContainedCategory() {
            return containedCategory;
        }

        public void setContainedCategory(EagerManyToOneFetchModeSelectWhereTest.ContainedCategory containedCategory) {
            this.containedCategory = containedCategory;
        }

        public Set<EagerManyToOneFetchModeSelectWhereTest.ContainedCategory> getContainedCategories() {
            return containedCategories;
        }

        public void setContainedCategories(Set<EagerManyToOneFetchModeSelectWhereTest.ContainedCategory> containedCategories) {
            this.containedCategories = containedCategories;
        }
    }

    public static class Category {
        private int id;

        private String name;

        private int inactive;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getInactive() {
            return inactive;
        }

        public void setInactive(int inactive) {
            this.inactive = inactive;
        }
    }

    public static class ContainedCategory {
        private EagerManyToOneFetchModeSelectWhereTest.Category category;

        public ContainedCategory() {
        }

        public ContainedCategory(EagerManyToOneFetchModeSelectWhereTest.Category category) {
            this.category = category;
        }

        public EagerManyToOneFetchModeSelectWhereTest.Category getCategory() {
            return category;
        }

        public void setCategory(EagerManyToOneFetchModeSelectWhereTest.Category category) {
            this.category = category;
        }
    }
}

