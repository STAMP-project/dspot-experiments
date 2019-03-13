/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.envers.test.integration.collection.embeddable;


import java.io.Serializable;
import java.util.List;
import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OrderColumn;
import org.hibernate.envers.Audited;
import org.hibernate.envers.strategy.ValidityAuditStrategy;
import org.hibernate.envers.test.BaseEnversJPAFunctionalTestCase;
import org.hibernate.envers.test.Priority;
import org.hibernate.testing.TestForIssue;
import org.junit.Assert;
import org.junit.Test;


/**
 * This test verifies that when a list-based {@link ElementCollection} of {@link Embeddable} objects
 * are audited that the same number of audit rows are generated regardless whether the embeddable
 * implements proper {@code equals} and {@code hashCode} methods.
 *
 * @author Chris Cranford
 */
@TestForIssue(jiraKey = "HHH-12607")
public class ListNoEqualsHashCodeTest extends BaseEnversJPAFunctionalTestCase {
    @Test
    @Priority(10)
    public void initData() {
        final ListNoEqualsHashCodeTest.Emb emb1 = new ListNoEqualsHashCodeTest.Emb("value1");
        final ListNoEqualsHashCodeTest.Emb emb2 = new ListNoEqualsHashCodeTest.Emb("value2");
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.envers.test.integration.collection.embeddable.TestEntity e = new org.hibernate.envers.test.integration.collection.embeddable.TestEntity(1);
            e.setEmbs1(new ArrayList<>());
            e.getEmbs1().add(emb1);
            e.getEmbs1().add(emb2);
            entityManager.persist(e);
        });
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.envers.test.integration.collection.embeddable.TestEntity e = entityManager.find(.class, 1);
            for (org.hibernate.envers.test.integration.collection.embeddable.Emb emb : e.getEmbs1()) {
                if (emb.getValue().equals("value1")) {
                    e.getEmbs1().remove(emb);
                    break;
                }
            }
            e.getEmbs1().add(new org.hibernate.envers.test.integration.collection.embeddable.Emb("value3"));
        });
    }

    @Test
    public void testAuditRowsForValidityAuditStrategy() {
        if (ValidityAuditStrategy.class.getName().equals(getAuditStrategy())) {
            doInJPA(this::entityManagerFactory, ( entityManager) -> {
                Long results = entityManager.createQuery("SELECT COUNT(1) FROM TestEntity_embs1_AUD WHERE REVEND IS NULL", .class).getSingleResult();
                assertNotNull(results);
                assertEquals(Long.valueOf(4), results);
            });
            doInJPA(this::entityManagerFactory, ( entityManager) -> {
                Long results = entityManager.createQuery("SELECT COUNT(1) FROM TestEntity_embs1_AUD", .class).getSingleResult();
                assertNotNull(results);
                assertEquals(Long.valueOf(6), results);
            });
        }
    }

    @Test
    public void testAuditRowsForDefaultAuditStrategy() {
        if (!(ValidityAuditStrategy.class.getName().equals(getAuditStrategy()))) {
            doInJPA(this::entityManagerFactory, ( entityManager) -> {
                Long results = entityManager.createQuery("SELECT COUNT(1) FROM TestEntity_embs1_AUD", .class).getSingleResult();
                assertNotNull(results);
                assertEquals(Long.valueOf(6), results);
            });
        }
    }

    @Test
    public void testRevisionHistory1() {
        ListNoEqualsHashCodeTest.TestEntity e = getAuditReader().find(ListNoEqualsHashCodeTest.TestEntity.class, 1, 1);
        Assert.assertEquals(2, e.getEmbs1().size());
        ListNoEqualsHashCodeTest.assertHasEmbeddableWithValue(e, "value1");
        ListNoEqualsHashCodeTest.assertHasEmbeddableWithValue(e, "value2");
    }

    @Test
    public void testRevisionHistory2() {
        ListNoEqualsHashCodeTest.TestEntity e = getAuditReader().find(ListNoEqualsHashCodeTest.TestEntity.class, 1, 2);
        Assert.assertEquals(2, e.getEmbs1().size());
        ListNoEqualsHashCodeTest.assertHasEmbeddableWithValue(e, "value3");
        ListNoEqualsHashCodeTest.assertHasEmbeddableWithValue(e, "value2");
    }

    @Entity(name = "TestEntity")
    @Audited
    public static class TestEntity {
        @Id
        private Integer id;

        @ElementCollection
        @OrderColumn
        private List<ListNoEqualsHashCodeTest.Emb> embs1;

        public TestEntity() {
        }

        public TestEntity(Integer id) {
            this.id = id;
        }

        public List<ListNoEqualsHashCodeTest.Emb> getEmbs1() {
            return embs1;
        }

        public void setEmbs1(List<ListNoEqualsHashCodeTest.Emb> embs1) {
            this.embs1 = embs1;
        }
    }

    @Embeddable
    public static class Emb implements Serializable {
        private String value;

        public Emb() {
        }

        public Emb(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}

