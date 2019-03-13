/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.envers.test.integration.query;


import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OrderBy;
import org.hibernate.envers.Audited;
import org.hibernate.envers.test.BaseEnversJPAFunctionalTestCase;
import org.hibernate.testing.TestForIssue;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test the use of the {@link OrderBy} annotation on a map-based element-collection
 * that uses entities for the key and value.
 *
 * This mapping and association invokes the use of the ThreeEntityQueryGenerator which
 * we want to verify orders the collection results properly.
 *
 * It's worth noting that a mapping like this orders the collection based on the value
 * and not the key.
 *
 * @author Chris Cranford
 */
@TestForIssue(jiraKey = "HHH-12992")
public class OrderByThreeEntityTest extends BaseEnversJPAFunctionalTestCase {
    @Entity(name = "Container")
    @Audited
    public static class Container {
        @Id
        @GeneratedValue
        private Integer id;

        @ElementCollection
        @OrderBy("value desc")
        private Map<OrderByThreeEntityTest.Key, OrderByThreeEntityTest.Item> data = new HashMap<>();

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public Map<OrderByThreeEntityTest.Key, OrderByThreeEntityTest.Item> getData() {
            return data;
        }

        public void setData(Map<OrderByThreeEntityTest.Key, OrderByThreeEntityTest.Item> data) {
            this.data = data;
        }
    }

    @Entity(name = "MapKey")
    @Audited
    public static class Key {
        @Id
        private Integer id;

        private String value;

        public Key() {
        }

        public Key(Integer id, String value) {
            this.id = id;
            this.value = value;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            OrderByThreeEntityTest.Key key = ((OrderByThreeEntityTest.Key) (o));
            return (Objects.equals(id, key.id)) && (Objects.equals(value, key.value));
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, value);
        }

        @Override
        public String toString() {
            return ((((("Key{" + "id=") + (id)) + ", value='") + (value)) + '\'') + '}';
        }
    }

    @Entity(name = "Item")
    @Audited
    public static class Item {
        @Id
        private Integer id;

        private String value;

        public Item() {
        }

        public Item(Integer id, String value) {
            this.id = id;
            this.value = value;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            OrderByThreeEntityTest.Item item = ((OrderByThreeEntityTest.Item) (o));
            return (Objects.equals(id, item.id)) && (Objects.equals(value, item.value));
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, value);
        }

        @Override
        public String toString() {
            return ((((("Item{" + "id=") + (id)) + ", value='") + (value)) + '\'') + '}';
        }
    }

    private Integer containerId;

    @Test
    public void initData() {
        // Rev 1
        this.containerId = doInJPA(this::entityManagerFactory, ( entityManager) -> {
            final org.hibernate.envers.test.integration.query.Container container = new org.hibernate.envers.test.integration.query.Container();
            final org.hibernate.envers.test.integration.query.Key key1 = new org.hibernate.envers.test.integration.query.Key(1, "A");
            final org.hibernate.envers.test.integration.query.Key key2 = new org.hibernate.envers.test.integration.query.Key(2, "B");
            final org.hibernate.envers.test.integration.query.Item item1 = new org.hibernate.envers.test.integration.query.Item(1, "I1");
            final org.hibernate.envers.test.integration.query.Item item2 = new org.hibernate.envers.test.integration.query.Item(2, "I2");
            entityManager.persist(item1);
            entityManager.persist(item2);
            entityManager.persist(key1);
            entityManager.persist(key2);
            container.getData().put(key1, item2);
            container.getData().put(key2, item1);
            entityManager.persist(container);
            return container.getId();
        });
        // Rev 2
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            final org.hibernate.envers.test.integration.query.Container container = entityManager.find(.class, containerId);
            final org.hibernate.envers.test.integration.query.Key key = new org.hibernate.envers.test.integration.query.Key(3, "C");
            final org.hibernate.envers.test.integration.query.Item item = new org.hibernate.envers.test.integration.query.Item(3, "I3");
            entityManager.persist(key);
            entityManager.persist(item);
            container.getData().put(key, item);
            entityManager.merge(container);
        });
        // Rev 3
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            final org.hibernate.envers.test.integration.query.Container container = entityManager.find(.class, containerId);
            container.getData().keySet().forEach(( key) -> {
                if ("B".equals(key.getValue())) {
                    final org.hibernate.envers.test.integration.query.Item item = container.getData().get(key);
                    container.getData().remove(key);
                    entityManager.remove(key);
                    entityManager.remove(item);
                }
            });
            entityManager.merge(container);
        });
        // Rev 4
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            final org.hibernate.envers.test.integration.query.Container container = entityManager.find(.class, containerId);
            container.getData().entrySet().forEach(( entry) -> {
                entityManager.remove(entry.getKey());
                entityManager.remove(entry.getValue());
            });
            container.getData().clear();
            entityManager.merge(container);
        });
    }

    @Test
    public void testRevisionCounts() {
        Assert.assertEquals(Arrays.asList(1, 2, 3, 4), getAuditReader().getRevisions(OrderByThreeEntityTest.Container.class, this.containerId));
        Assert.assertEquals(Arrays.asList(1, 4), getAuditReader().getRevisions(OrderByThreeEntityTest.Key.class, 1));
        Assert.assertEquals(Arrays.asList(1, 3), getAuditReader().getRevisions(OrderByThreeEntityTest.Key.class, 2));
        Assert.assertEquals(Arrays.asList(2, 4), getAuditReader().getRevisions(OrderByThreeEntityTest.Key.class, 3));
        Assert.assertEquals(Arrays.asList(1, 3), getAuditReader().getRevisions(OrderByThreeEntityTest.Item.class, 1));
        Assert.assertEquals(Arrays.asList(1, 4), getAuditReader().getRevisions(OrderByThreeEntityTest.Item.class, 2));
        Assert.assertEquals(Arrays.asList(2, 4), getAuditReader().getRevisions(OrderByThreeEntityTest.Item.class, 3));
    }

    @Test
    public void testRevision1History() {
        final OrderByThreeEntityTest.Container container = getAuditReader().find(OrderByThreeEntityTest.Container.class, this.containerId, 1);
        Assert.assertNotNull(container);
        Assert.assertTrue((!(container.getData().isEmpty())));
        Assert.assertEquals(2, container.getData().size());
        final Iterator<Map.Entry<OrderByThreeEntityTest.Key, OrderByThreeEntityTest.Item>> iterator = container.getData().entrySet().iterator();
        final Map.Entry<OrderByThreeEntityTest.Key, OrderByThreeEntityTest.Item> first = iterator.next();
        Assert.assertEquals(new OrderByThreeEntityTest.Key(1, "A"), first.getKey());
        Assert.assertEquals(new OrderByThreeEntityTest.Item(2, "I2"), first.getValue());
        final Map.Entry<OrderByThreeEntityTest.Key, OrderByThreeEntityTest.Item> second = iterator.next();
        Assert.assertEquals(new OrderByThreeEntityTest.Key(2, "B"), second.getKey());
        Assert.assertEquals(new OrderByThreeEntityTest.Item(1, "I1"), second.getValue());
    }

    @Test
    public void testRevision2History() {
        final OrderByThreeEntityTest.Container container = getAuditReader().find(OrderByThreeEntityTest.Container.class, this.containerId, 2);
        Assert.assertNotNull(container);
        Assert.assertTrue((!(container.getData().isEmpty())));
        Assert.assertEquals(3, container.getData().size());
        final Iterator<Map.Entry<OrderByThreeEntityTest.Key, OrderByThreeEntityTest.Item>> iterator = container.getData().entrySet().iterator();
        final Map.Entry<OrderByThreeEntityTest.Key, OrderByThreeEntityTest.Item> first = iterator.next();
        Assert.assertEquals(new OrderByThreeEntityTest.Key(3, "C"), first.getKey());
        Assert.assertEquals(new OrderByThreeEntityTest.Item(3, "I3"), first.getValue());
        final Map.Entry<OrderByThreeEntityTest.Key, OrderByThreeEntityTest.Item> second = iterator.next();
        Assert.assertEquals(new OrderByThreeEntityTest.Key(1, "A"), second.getKey());
        Assert.assertEquals(new OrderByThreeEntityTest.Item(2, "I2"), second.getValue());
        final Map.Entry<OrderByThreeEntityTest.Key, OrderByThreeEntityTest.Item> third = iterator.next();
        Assert.assertEquals(new OrderByThreeEntityTest.Key(2, "B"), third.getKey());
        Assert.assertEquals(new OrderByThreeEntityTest.Item(1, "I1"), third.getValue());
    }

    @Test
    public void testRevision3History() {
        final OrderByThreeEntityTest.Container container = getAuditReader().find(OrderByThreeEntityTest.Container.class, this.containerId, 3);
        Assert.assertNotNull(container);
        Assert.assertTrue((!(container.getData().isEmpty())));
        Assert.assertEquals(2, container.getData().size());
        final Iterator<Map.Entry<OrderByThreeEntityTest.Key, OrderByThreeEntityTest.Item>> iterator = container.getData().entrySet().iterator();
        final Map.Entry<OrderByThreeEntityTest.Key, OrderByThreeEntityTest.Item> first = iterator.next();
        Assert.assertEquals(new OrderByThreeEntityTest.Key(3, "C"), first.getKey());
        Assert.assertEquals(new OrderByThreeEntityTest.Item(3, "I3"), first.getValue());
        final Map.Entry<OrderByThreeEntityTest.Key, OrderByThreeEntityTest.Item> second = iterator.next();
        Assert.assertEquals(new OrderByThreeEntityTest.Key(1, "A"), second.getKey());
        Assert.assertEquals(new OrderByThreeEntityTest.Item(2, "I2"), second.getValue());
    }

    @Test
    public void testRevision4History() {
        final OrderByThreeEntityTest.Container container = getAuditReader().find(OrderByThreeEntityTest.Container.class, this.containerId, 4);
        Assert.assertNotNull(container);
        Assert.assertTrue(container.getData().isEmpty());
    }
}

