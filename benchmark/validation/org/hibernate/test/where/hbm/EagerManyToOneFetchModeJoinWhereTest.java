/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.where.hbm;


import java.util.HashSet;
import java.util.Set;
import org.hibernate.testing.FailureExpected;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


/**
 *
 *
 * @author Gail Badner
 */
public class EagerManyToOneFetchModeJoinWhereTest extends BaseNonConfigCoreFunctionalTestCase {
    @Test
    @TestForIssue(jiraKey = "HHH-12104")
    @FailureExpected(jiraKey = "HHH-12104")
    public void testAssociatedWhereClause() {
        EagerManyToOneFetchModeJoinWhereTest.Product product = new EagerManyToOneFetchModeJoinWhereTest.Product();
        EagerManyToOneFetchModeJoinWhereTest.Category category = new EagerManyToOneFetchModeJoinWhereTest.Category();
        category.name = "flowers";
        product.category = category;
        product.containedCategory = new EagerManyToOneFetchModeJoinWhereTest.ContainedCategory();
        product.containedCategory.category = category;
        product.containedCategories.add(new EagerManyToOneFetchModeJoinWhereTest.ContainedCategory(category));
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
            // Entity's where clause is ignored when to-one associations to that
            // association is loaded eagerly using FetchMode.JOIN, so the result
            // should be the same as before the Category was made inactive.
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

        private EagerManyToOneFetchModeJoinWhereTest.Category category;

        private EagerManyToOneFetchModeJoinWhereTest.ContainedCategory containedCategory;

        private Set<EagerManyToOneFetchModeJoinWhereTest.ContainedCategory> containedCategories = new HashSet<>();

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public EagerManyToOneFetchModeJoinWhereTest.Category getCategory() {
            return category;
        }

        public void setCategory(EagerManyToOneFetchModeJoinWhereTest.Category category) {
            this.category = category;
        }

        public EagerManyToOneFetchModeJoinWhereTest.ContainedCategory getContainedCategory() {
            return containedCategory;
        }

        public void setContainedCategory(EagerManyToOneFetchModeJoinWhereTest.ContainedCategory containedCategory) {
            this.containedCategory = containedCategory;
        }

        public Set<EagerManyToOneFetchModeJoinWhereTest.ContainedCategory> getContainedCategories() {
            return containedCategories;
        }

        public void setContainedCategories(Set<EagerManyToOneFetchModeJoinWhereTest.ContainedCategory> containedCategories) {
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
        private EagerManyToOneFetchModeJoinWhereTest.Category category;

        public ContainedCategory() {
        }

        public ContainedCategory(EagerManyToOneFetchModeJoinWhereTest.Category category) {
            this.category = category;
        }

        public EagerManyToOneFetchModeJoinWhereTest.Category getCategory() {
            return category;
        }

        public void setCategory(EagerManyToOneFetchModeJoinWhereTest.Category category) {
            this.category = category;
        }
    }
}

