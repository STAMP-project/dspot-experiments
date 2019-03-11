/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.component.empty;


import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import org.hibernate.annotations.Parent;
import org.hibernate.testing.FailureExpected;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


/**
 *
 *
 * @author Gail Badner
 */
public class EmptyCompositeEquivalentToNullTest extends BaseCoreFunctionalTestCase {
    @Test
    @TestForIssue(jiraKey = "HHH-11898")
    @FailureExpected(jiraKey = "HHH-11898")
    public void testPrimitive() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.test.component.empty.AnEntity anEntity = new org.hibernate.test.component.empty.AnEntity();
            session.persist(anEntity);
            session.flush();
            session.clear();
            anEntity = session.get(.class, anEntity.id);
            checkEmptyCompositeTypeEquivalentToNull(anEntity.embeddableWithPrimitive, "embeddableWithPrimitive", sessionFactory());
        });
    }

    @Test
    @TestForIssue(jiraKey = "HHH-11898")
    public void testParent() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.test.component.empty.AnEntity anEntity = new org.hibernate.test.component.empty.AnEntity();
            session.persist(anEntity);
            session.flush();
            session.clear();
            anEntity = session.get(.class, anEntity.id);
            checkEmptyCompositeTypeEquivalentToNull(anEntity.embeddableWithParent, "embeddableWithParent", sessionFactory());
        });
    }

    @Test
    @TestForIssue(jiraKey = "HHH-11898")
    public void testNoPrimitiveNoParent() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.test.component.empty.AnEntity anEntity = new org.hibernate.test.component.empty.AnEntity();
            session.persist(anEntity);
            session.flush();
            session.clear();
            anEntity = session.get(.class, anEntity.id);
            checkEmptyCompositeTypeEquivalentToNull(anEntity.embeddableWithNoPrimitiveNoParent, "embeddableWithNoPrimitiveNoParent", sessionFactory());
        });
    }

    @Entity(name = "AnEntity")
    public static class AnEntity {
        private int id;

        private EmptyCompositeEquivalentToNullTest.EmbeddableWithParent embeddableWithParent;

        private EmptyCompositeEquivalentToNullTest.EmbeddableWithPrimitive embeddableWithPrimitive;

        private EmptyCompositeEquivalentToNullTest.EmbeddableWithNoPrimitiveNoParent embeddableWithNoPrimitiveNoParent;

        @Id
        @GeneratedValue
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public EmptyCompositeEquivalentToNullTest.EmbeddableWithParent getEmbeddableWithParent() {
            return embeddableWithParent;
        }

        public void setEmbeddableWithParent(EmptyCompositeEquivalentToNullTest.EmbeddableWithParent embeddableWithParent) {
            this.embeddableWithParent = embeddableWithParent;
        }

        public EmptyCompositeEquivalentToNullTest.EmbeddableWithPrimitive getEmbeddableWithPrimitive() {
            return embeddableWithPrimitive;
        }

        public void setEmbeddableWithPrimitive(EmptyCompositeEquivalentToNullTest.EmbeddableWithPrimitive embeddableWithPrimitive) {
            this.embeddableWithPrimitive = embeddableWithPrimitive;
        }

        public EmptyCompositeEquivalentToNullTest.EmbeddableWithNoPrimitiveNoParent getEmbeddableWithNoPrimitiveNoParent() {
            return embeddableWithNoPrimitiveNoParent;
        }

        public void setEmbeddableWithNoPrimitiveNoParent(EmptyCompositeEquivalentToNullTest.EmbeddableWithNoPrimitiveNoParent embeddableWithNoPrimitiveNoParent) {
            this.embeddableWithNoPrimitiveNoParent = embeddableWithNoPrimitiveNoParent;
        }
    }

    @Embeddable
    public static class EmbeddableWithParent {
        private Object parent;

        private Long longObjectValue;

        @Parent
        public Object getParent() {
            return parent;
        }

        public void setParent(Object parent) {
            this.parent = parent;
        }

        public Long getLongObjectValue() {
            return longObjectValue;
        }

        public void setLongObjectValue(Long longObjectValue) {
            this.longObjectValue = longObjectValue;
        }
    }

    @Embeddable
    public static class EmbeddableWithPrimitive {
        private int intValue;

        private String stringValue;

        @Column(nullable = true)
        public int getIntValue() {
            return intValue;
        }

        public void setIntValue(int intValue) {
            this.intValue = intValue;
        }

        public String getStringValue() {
            return stringValue;
        }

        public void setStringValue(String stringValue) {
            this.stringValue = stringValue;
        }
    }

    @Embeddable
    public static class EmbeddableWithNoPrimitiveNoParent {
        private Integer intObjectValue;

        private String otherStringValue;

        public Integer getIntObjectValue() {
            return intObjectValue;
        }

        public void setIntObjectValue(Integer intObjectValue) {
            this.intObjectValue = intObjectValue;
        }

        public String getOtherStringValue() {
            return otherStringValue;
        }

        public void setOtherStringValue(String otherStringValue) {
            this.otherStringValue = otherStringValue;
        }
    }
}

