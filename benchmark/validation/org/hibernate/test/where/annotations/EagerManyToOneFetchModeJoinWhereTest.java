/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.where.annotations;


import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Where;
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
            org.hibernate.test.where.annotations.Product p = session.get(.class, product.id);
            assertNotNull(p);
            assertNotNull(p.category);
            assertNotNull(p.containedCategory.category);
            assertEquals(1, p.containedCategories.size());
            assertSame(p.category, p.containedCategory.category);
            assertSame(p.category, p.containedCategories.iterator().next().category);
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.test.where.annotations.Category c = session.get(.class, category.id);
            assertNotNull(c);
            c.inactive = 1;
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.test.where.annotations.Category c = session.get(.class, category.id);
            assertNull(c);
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            // Entity's where clause is ignored when to-one associations to that
            // association is loaded eagerly using FetchMode.JOIN, so the result
            // should be the same as before the Category was made inactive.
            org.hibernate.test.where.annotations.Product p = session.get(.class, product.id);
            assertNotNull(p);
            assertNull(p.category);
            assertNull(p.containedCategory.category);
            assertEquals(1, p.containedCategories.size());
            assertNull(p.containedCategories.iterator().next().category);
        });
    }

    @Entity(name = "Product")
    public static class Product {
        @Id
        @GeneratedValue
        private int id;

        @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
        @NotFound(action = NotFoundAction.IGNORE)
        @JoinColumn(name = "categoryId")
        @Fetch(FetchMode.JOIN)
        private EagerManyToOneFetchModeJoinWhereTest.Category category;

        private EagerManyToOneFetchModeJoinWhereTest.ContainedCategory containedCategory;

        @ElementCollection(fetch = FetchType.EAGER)
        private Set<EagerManyToOneFetchModeJoinWhereTest.ContainedCategory> containedCategories = new HashSet<>();
    }

    @Entity(name = "Category")
    @Table(name = "CATEGORY")
    @Where(clause = "inactive = 0")
    public static class Category {
        @Id
        @GeneratedValue
        private int id;

        private String name;

        private int inactive;
    }

    @Embeddable
    public static class ContainedCategory {
        @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
        @NotFound(action = NotFoundAction.IGNORE)
        @JoinColumn(name = "containedCategoryId")
        @Fetch(FetchMode.JOIN)
        private EagerManyToOneFetchModeJoinWhereTest.Category category;

        public ContainedCategory() {
        }

        public ContainedCategory(EagerManyToOneFetchModeJoinWhereTest.Category category) {
            this.category = category;
        }
    }
}

