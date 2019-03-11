/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.orphan.onetomany;


import java.util.LinkedHashSet;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import org.hibernate.Hibernate;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.FailureExpected;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Andrea Boriero
 */
public class DeleteSharedOneToManyOrphansTest extends BaseEntityManagerFunctionalTestCase {
    /* A value of BATCH_FETCH_SIZE > 1 along with the initialization of the Item#higherItemRelations
    collection causes the issue
     */
    private static final String BATCH_FETCH_SIZE = "2";

    @Test
    @TestForIssue(jiraKey = "HHH-11144")
    @FailureExpected(jiraKey = "HHH-11144")
    public void testInitializingSecondCollection() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.jpa.test.orphan.onetomany.Item item = entityManager.createQuery("select x from Item x where x.code = 'first'", .class).getSingleResult();
            Set<org.hibernate.jpa.test.orphan.onetomany.ItemRelation> lowerItemRelations = item.getLowerItemRelations();
            Hibernate.initialize(lowerItemRelations);
            Set<org.hibernate.jpa.test.orphan.onetomany.ItemRelation> higherItemRelations = item.getHigherItemRelations();
            Hibernate.initialize(higherItemRelations);
            Assert.assertEquals(1, lowerItemRelations.size());
            lowerItemRelations.clear();
        });
        checkLowerItemRelationsAreDeleted();
    }

    @Entity(name = "Item")
    public static class Item {
        @Id
        @GeneratedValue
        protected Long id;

        @Column
        protected String code;

        @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
        protected java.util.Set<DeleteSharedOneToManyOrphansTest.ItemRelation> lowerItemRelations = new LinkedHashSet<>();

        @OneToMany(mappedBy = "child", cascade = CascadeType.ALL, orphanRemoval = true)
        protected java.util.Set<DeleteSharedOneToManyOrphansTest.ItemRelation> higherItemRelations = new LinkedHashSet<>();

        public Item() {
        }

        public Item(String code) {
            this.code = code;
        }

        public java.util.Set<DeleteSharedOneToManyOrphansTest.ItemRelation> getLowerItemRelations() {
            return lowerItemRelations;
        }

        public java.util.Set<DeleteSharedOneToManyOrphansTest.ItemRelation> getHigherItemRelations() {
            return higherItemRelations;
        }

        public void addHigherItemRelations(DeleteSharedOneToManyOrphansTest.ItemRelation itemRelation) {
            higherItemRelations.add(itemRelation);
            itemRelation.setChild(this);
        }

        public void addLowerItemRelations(DeleteSharedOneToManyOrphansTest.ItemRelation itemRelation) {
            lowerItemRelations.add(itemRelation);
            itemRelation.setParent(this);
        }
    }

    @Entity(name = "ItemRelation")
    public static class ItemRelation {
        @Id
        @GeneratedValue
        protected Long id;

        @ManyToOne(optional = false)
        @JoinColumn(name = "PARENT_ID")
        private DeleteSharedOneToManyOrphansTest.Item parent;

        @ManyToOne(optional = false)
        @JoinColumn(name = "CHILD_ID")
        private DeleteSharedOneToManyOrphansTest.Item child;

        public DeleteSharedOneToManyOrphansTest.Item getParent() {
            return parent;
        }

        public void setParent(DeleteSharedOneToManyOrphansTest.Item parent) {
            this.parent = parent;
        }

        public DeleteSharedOneToManyOrphansTest.Item getChild() {
            return child;
        }

        public void setChild(DeleteSharedOneToManyOrphansTest.Item child) {
            this.child = child;
        }
    }
}

