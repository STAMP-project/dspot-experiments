/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.envers.test.integration.collection;


import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.Audited;
import org.hibernate.envers.test.BaseEnversJPAFunctionalTestCase;
import org.hibernate.envers.test.Priority;
import org.hibernate.testing.TestForIssue;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Chris Cranford
 */
@TestForIssue(jiraKey = "HHH-12018")
public class EntitySameMapKeyMultiMapValueTest extends BaseEnversJPAFunctionalTestCase {
    private Integer otherEntityId;

    private Integer someEntityId;

    @Entity(name = "SomeEntity")
    @Audited
    public static class SomeEntity {
        @Id
        @GeneratedValue
        private Integer id;

        @ElementCollection
        private Map<EntitySameMapKeyMultiMapValueTest.OtherEntity, EntitySameMapKeyMultiMapValueTest.SomeEntity.Status> map = new HashMap<>();

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public Map<EntitySameMapKeyMultiMapValueTest.OtherEntity, EntitySameMapKeyMultiMapValueTest.SomeEntity.Status> getMap() {
            return map;
        }

        public void setMap(Map<EntitySameMapKeyMultiMapValueTest.OtherEntity, EntitySameMapKeyMultiMapValueTest.SomeEntity.Status> map) {
            this.map = map;
        }

        enum Status {

            A,
            B;}
    }

    @Entity(name = "OtherEntity")
    @Audited
    public static class OtherEntity {
        @Id
        @GeneratedValue
        private Integer id;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }
    }

    @Test
    @Priority(10)
    public void initData() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            final org.hibernate.envers.test.integration.collection.SomeEntity someEntity = new org.hibernate.envers.test.integration.collection.SomeEntity();
            final org.hibernate.envers.test.integration.collection.OtherEntity otherEntity = new org.hibernate.envers.test.integration.collection.OtherEntity();
            entityManager.persist(otherEntity);
            someEntity.getMap().put(otherEntity, SomeEntity.Status.A);
            entityManager.persist(someEntity);
            this.otherEntityId = otherEntity.getId();
            this.someEntityId = someEntity.getId();
        });
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            final org.hibernate.envers.test.integration.collection.SomeEntity someEntity = entityManager.find(.class, someEntityId);
            final org.hibernate.envers.test.integration.collection.OtherEntity otherEntity = entityManager.find(.class, otherEntityId);
            someEntity.getMap().put(otherEntity, SomeEntity.Status.B);
            entityManager.merge(someEntity);
        });
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            final org.hibernate.envers.test.integration.collection.SomeEntity someEntity = entityManager.find(.class, someEntityId);
            someEntity.getMap().clear();
            entityManager.merge(someEntity);
        });
    }

    @Test
    public void testRevisionCounts() {
        Assert.assertEquals(Arrays.asList(1), getAuditReader().getRevisions(EntitySameMapKeyMultiMapValueTest.OtherEntity.class, otherEntityId));
        Assert.assertEquals(Arrays.asList(1, 2, 3), getAuditReader().getRevisions(EntitySameMapKeyMultiMapValueTest.SomeEntity.class, someEntityId));
    }

    @Test
    public void blockTest() {
        final AuditReader reader = getAuditReader();
        System.out.println("Halt");
    }

    @Test
    public void testRevisionOne() {
        final EntitySameMapKeyMultiMapValueTest.SomeEntity someEntity = getAuditReader().find(EntitySameMapKeyMultiMapValueTest.SomeEntity.class, someEntityId, 1);
        Assert.assertNotNull(someEntity);
        Assert.assertFalse(someEntity.getMap().isEmpty());
        Assert.assertEquals(1, someEntity.getMap().size());
        final EntitySameMapKeyMultiMapValueTest.OtherEntity otherEntity = getAuditReader().find(EntitySameMapKeyMultiMapValueTest.OtherEntity.class, otherEntityId, 1);
        Assert.assertNotNull(otherEntity);
        final Map.Entry<EntitySameMapKeyMultiMapValueTest.OtherEntity, EntitySameMapKeyMultiMapValueTest.SomeEntity.Status> entry = someEntity.getMap().entrySet().iterator().next();
        Assert.assertEquals(otherEntity, entry.getKey());
        Assert.assertEquals(EntitySameMapKeyMultiMapValueTest.SomeEntity.Status.A, entry.getValue());
    }

    @Test
    public void testRevisionTwo() {
        final EntitySameMapKeyMultiMapValueTest.SomeEntity someEntity = getAuditReader().find(EntitySameMapKeyMultiMapValueTest.SomeEntity.class, someEntityId, 2);
        Assert.assertNotNull(someEntity);
        Assert.assertFalse(someEntity.getMap().isEmpty());
        Assert.assertEquals(1, someEntity.getMap().size());
        final EntitySameMapKeyMultiMapValueTest.OtherEntity otherEntity = getAuditReader().find(EntitySameMapKeyMultiMapValueTest.OtherEntity.class, otherEntityId, 2);
        Assert.assertNotNull(otherEntity);
        final Map.Entry<EntitySameMapKeyMultiMapValueTest.OtherEntity, EntitySameMapKeyMultiMapValueTest.SomeEntity.Status> entry = someEntity.getMap().entrySet().iterator().next();
        Assert.assertEquals(otherEntity, entry.getKey());
        Assert.assertEquals(EntitySameMapKeyMultiMapValueTest.SomeEntity.Status.B, entry.getValue());
    }

    @Test
    public void testRevisionThree() {
        final EntitySameMapKeyMultiMapValueTest.SomeEntity someEntity = getAuditReader().find(EntitySameMapKeyMultiMapValueTest.SomeEntity.class, someEntityId, 3);
        Assert.assertNotNull(someEntity);
        Assert.assertTrue(someEntity.getMap().isEmpty());
    }
}

