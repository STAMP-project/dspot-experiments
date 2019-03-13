/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.envers.test.integration.collection.embeddable;


import java.io.Serializable;
import java.util.Map;
import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.Id;
import org.hibernate.envers.Audited;
import org.hibernate.envers.strategy.ValidityAuditStrategy;
import org.hibernate.envers.test.BaseEnversJPAFunctionalTestCase;
import org.hibernate.envers.test.Priority;
import org.hibernate.testing.TestForIssue;
import org.junit.Assert;
import org.junit.Test;


/**
 * This test verifies that when a map-based {@link ElementCollection} of {@link Embeddable} objects
 * are audited that the same number of audit rows are generated regardless whether the embeddable
 * implements proper {@code equals} and {@code hashCode} methods.
 *
 * The {@link org.hibernate.envers.strategy.ValidityAuditStrategy} with equals/hashcode.
 *
 * +-----+---------+---------------+-----------+--------+--------+
 * | REV | REVTYPE | TESTENTITY_ID | EMBS1_KEY | REVEND | VALUE  |
 * +-----+---------+---------------+-----------+--------+--------+
 * | 1   | 0       | 1             | a         | 2      | value1 |
 * | 1   | 0       | 1             | b         | null   | value2 |
 * | 2   | 0       | 1             | a         | null   | value3 |
 * | 2   | 2       | 1             | a         | null   | value1 |
 * +-----+---------+---------------+-----------+--------+--------+
 *
 * The {@link org.hibernate.envers.strategy.DefaultAuditStrategy} with equals/hashcode.
 *
 * +-----+---------+---------------+-----------+--------+
 * | REV | REVTYPE | TESTENTITY_ID | EMBS1_KEY | VALUE  |
 * +-----+---------+---------------+-----------+--------+
 * | 1   | 0       | 1             | a         | value1 |
 * | 1   | 0       | 1             | b         | value2 |
 * | 2   | 0       | 1             | a         | value3 |
 * | 2   | 2       | 1             | a         | value1 |
 * +-----+---------+---------------+-----------+--------+
 *
 * @author Chris Cranford
 */
@TestForIssue(jiraKey = "HHH-12607")
public class MapNoEqualsHashCodeTest extends BaseEnversJPAFunctionalTestCase {
    @Test
    @Priority(10)
    public void initData() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.envers.test.integration.collection.embeddable.TestEntity e = new org.hibernate.envers.test.integration.collection.embeddable.TestEntity(1);
            e.setEmbs1(new HashMap<>());
            e.getEmbs1().put("a", new org.hibernate.envers.test.integration.collection.embeddable.Emb("value1"));
            e.getEmbs1().put("b", new org.hibernate.envers.test.integration.collection.embeddable.Emb("value2"));
            entityManager.persist(e);
        });
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.envers.test.integration.collection.embeddable.TestEntity e = entityManager.find(.class, 1);
            e.getEmbs1().put("a", new org.hibernate.envers.test.integration.collection.embeddable.Emb("value3"));
        });
    }

    @Test
    public void testAuditRowsForValidityAuditStrategy() {
        if (ValidityAuditStrategy.class.getName().equals(getAuditStrategy())) {
            doInJPA(this::entityManagerFactory, ( entityManager) -> {
                Long results = entityManager.createQuery("SELECT COUNT(1) FROM TestEntity_embs1_AUD WHERE REVEND IS NULL", .class).getSingleResult();
                assertNotNull(results);
                assertEquals(Long.valueOf(3), results);
            });
            doInJPA(this::entityManagerFactory, ( entityManager) -> {
                Long results = entityManager.createQuery("SELECT COUNT(1) FROM TestEntity_embs1_AUD", .class).getSingleResult();
                assertNotNull(results);
                assertEquals(Long.valueOf(4), results);
            });
        }
    }

    @Test
    public void testAuditRowsForDefaultAuditStrategy() {
        if (!(ValidityAuditStrategy.class.getName().equals(getAuditStrategy()))) {
            doInJPA(this::entityManagerFactory, ( entityManager) -> {
                Long results = entityManager.createQuery("SELECT COUNT(1) FROM TestEntity_embs1_AUD", .class).getSingleResult();
                assertNotNull(results);
                assertEquals(Long.valueOf(4), results);
            });
        }
    }

    @Test
    public void testRevisionHistory1() {
        MapNoEqualsHashCodeTest.TestEntity e = getAuditReader().find(MapNoEqualsHashCodeTest.TestEntity.class, 1, 1);
        Assert.assertEquals(2, e.getEmbs1().size());
        Assert.assertEquals("value1", e.getEmbs1().get("a").getValue());
        Assert.assertEquals("value2", e.getEmbs1().get("b").getValue());
    }

    @Test
    public void testRevisionHistory2() {
        MapNoEqualsHashCodeTest.TestEntity e = getAuditReader().find(MapNoEqualsHashCodeTest.TestEntity.class, 1, 2);
        Assert.assertEquals(2, e.getEmbs1().size());
        Assert.assertEquals("value3", e.getEmbs1().get("a").getValue());
        Assert.assertEquals("value2", e.getEmbs1().get("b").getValue());
    }

    @Entity(name = "TestEntity")
    @Audited
    public static class TestEntity {
        @Id
        private Integer id;

        @ElementCollection
        private Map<String, MapNoEqualsHashCodeTest.Emb> embs1;

        public TestEntity() {
        }

        public TestEntity(Integer id) {
            this.id = id;
        }

        public Map<String, MapNoEqualsHashCodeTest.Emb> getEmbs1() {
            return embs1;
        }

        public void setEmbs1(Map<String, MapNoEqualsHashCodeTest.Emb> embs1) {
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

