/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.mapping;


import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.FailureExpected;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


@TestForIssue(jiraKey = "HHH-13287")
public class BidirectionalOneToManyNotNullableColumnTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    @FailureExpected(jiraKey = "HHH-13287")
    public void test() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.jpa.test.mapping.ParentData parent = new org.hibernate.jpa.test.mapping.ParentData();
            parent.setId(1L);
            parent.addChildData(new org.hibernate.jpa.test.mapping.ChildData());
            parent.addChildData(new org.hibernate.jpa.test.mapping.ChildData());
            entityManager.persist(parent);
        });
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.jpa.test.mapping.ParentData parent = entityManager.find(.class, 1L);
            assertSame(2, parent.getChildren().size());
        });
    }

    @Entity(name = "ParentData")
    public static class ParentData {
        @Id
        long id;

        @OneToMany(mappedBy = "parentData", cascade = CascadeType.ALL, orphanRemoval = true)
        @OrderColumn(name = "listOrder", nullable = false)
        private List<BidirectionalOneToManyNotNullableColumnTest.ChildData> children = new ArrayList<>();

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public List<BidirectionalOneToManyNotNullableColumnTest.ChildData> getChildren() {
            return children;
        }

        public void addChildData(BidirectionalOneToManyNotNullableColumnTest.ChildData childData) {
            childData.setParentData(this);
            children.add(childData);
        }
    }

    @Entity(name = "ChildData")
    public static class ChildData {
        @Id
        @GeneratedValue
        long id;

        @ManyToOne
        private BidirectionalOneToManyNotNullableColumnTest.ParentData parentData;

        public ChildData() {
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public BidirectionalOneToManyNotNullableColumnTest.ParentData getParentData() {
            return parentData;
        }

        public void setParentData(BidirectionalOneToManyNotNullableColumnTest.ParentData parentData) {
            this.parentData = parentData;
        }
    }
}

