/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.inheritance.discriminator;


import java.util.List;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Christian Beikov
 */
public class SingleTableRelationsTest extends BaseCoreFunctionalTestCase {
    @Test
    @TestForIssue(jiraKey = "HHH-11375")
    public void testLazyInitialization() {
        createTestData();
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.test.inheritance.discriminator.Category category7 = session.find(.class, 7);
            // Must be empty because although Post and Category share the same column for their category relations,
            // the children must be based on entities that are of type Category
            Assert.assertTrue(category7.children.isEmpty());
        });
    }

    @Test
    @TestForIssue(jiraKey = "HHH-11375")
    public void testJoinFetch() {
        createTestData();
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.test.inheritance.discriminator.Category category7 = session.createQuery((("SELECT c FROM " + (.class.getName())) + " c LEFT JOIN FETCH c.children WHERE c.id = :id"), .class).setParameter("id", 7).getSingleResult();
            // Must be empty because although Post and Category share the same column for their category relations,
            // the children must be based on entities that are of type Category
            Assert.assertTrue(category7.children.isEmpty());
        });
    }

    @Entity
    @Table(name = "cp_post")
    @Inheritance(strategy = InheritanceType.SINGLE_TABLE)
    @DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.INTEGER)
    public static class PostTable {
        @Id
        protected Integer id;

        public PostTable() {
        }

        public PostTable(Integer id) {
            this.id = id;
        }
    }

    @Entity
    @DiscriminatorValue("1")
    public static class Category extends SingleTableRelationsTest.PostTable {
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn
        protected SingleTableRelationsTest.Category category;

        @OneToMany(fetch = FetchType.LAZY, mappedBy = "category")
        protected List<SingleTableRelationsTest.Category> children;

        public Category() {
        }

        public Category(Integer id) {
            super(id);
        }

        public Category(Integer id, SingleTableRelationsTest.Category category) {
            super(id);
            this.category = category;
        }
    }

    @Entity
    @DiscriminatorValue("2")
    public static class Post extends SingleTableRelationsTest.PostTable {
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn
        protected SingleTableRelationsTest.Category category;

        public Post() {
        }

        public Post(Integer id) {
            super(id);
        }

        public Post(Integer id, SingleTableRelationsTest.Category category) {
            super(id);
            this.category = category;
        }
    }
}

