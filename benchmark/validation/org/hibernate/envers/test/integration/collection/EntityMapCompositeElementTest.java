/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.envers.test.integration.collection;


import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.Audited;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.query.AuditQuery;
import org.hibernate.envers.test.BaseEnversJPAFunctionalTestCase;
import org.hibernate.envers.test.Priority;
import org.hibernate.testing.FailureExpected;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Chris Cranford
 */
@TestForIssue(jiraKey = "HHH-11841")
public class EntityMapCompositeElementTest extends BaseEnversJPAFunctionalTestCase {
    private EntityMapCompositeElementTest.Category category;

    private EntityMapCompositeElementTest.Item item;

    @Test
    @Priority(10)
    public void initData() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            final org.hibernate.envers.test.integration.collection.Item item = new org.hibernate.envers.test.integration.collection.Item("The Item");
            entityManager.persist(item);
            final org.hibernate.envers.test.integration.collection.Category category = new org.hibernate.envers.test.integration.collection.Category("The Category");
            category.setDescription("The description");
            category.setValue(item, new org.hibernate.envers.test.integration.collection.Value("The Value", 4711L));
            category.setText(item, "The text");
            entityManager.persist(category);
            this.category = category;
            this.item = item;
        });
    }

    @Test
    @FailureExpected(jiraKey = "HHH-11841", message = "Reverted fix in HHH-12018 and will be fixed in HHH-12043")
    public void testRevisionHistory() {
        final AuditReader reader = getAuditReader();
        AuditQuery categoryQuery = reader.createQuery().forRevisionsOfEntity(EntityMapCompositeElementTest.Category.class, false, true).addOrder(AuditEntity.revisionProperty("timestamp").asc()).add(AuditEntity.id().eq(category.getId()));
        @SuppressWarnings("unchecked")
        List<Object[]> history = ((List<Object[]>) (categoryQuery.getResultList()));
        Assert.assertNotNull(history);
        Assert.assertEquals(1, history.size());
        final EntityMapCompositeElementTest.Category category = ((EntityMapCompositeElementTest.Category) (reader.createQuery().forEntitiesAtRevision(EntityMapCompositeElementTest.Category.class, 1).add(AuditEntity.property("id").eq(this.category.getId())).setMaxResults(1).getSingleResult()));
        Assert.assertEquals(this.category.getName(), category.getName());
        Assert.assertEquals(this.category.getDescription(), category.getDescription());
        Assert.assertEquals("The text", category.getText(this.item));
        final EntityMapCompositeElementTest.Value value = category.getValue(this.item);
        Assert.assertEquals("The Value", value.getText());
        Assert.assertEquals(Long.valueOf(4711L), value.getNumberValue());
    }

    @Audited
    public static class Category {
        private Long id;

        private String name;

        private String description;

        private Map<EntityMapCompositeElementTest.Item, String> textItem = new HashMap<>();

        private Map<EntityMapCompositeElementTest.Item, EntityMapCompositeElementTest.Value> categoryItem = new HashMap<>();

        Category() {
        }

        Category(String name) {
            this.name = name;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public Map<EntityMapCompositeElementTest.Item, String> getTextItem() {
            return textItem;
        }

        public void setTextItem(Map<EntityMapCompositeElementTest.Item, String> textItem) {
            this.textItem = textItem;
        }

        public Map<EntityMapCompositeElementTest.Item, EntityMapCompositeElementTest.Value> getCategoryItem() {
            return categoryItem;
        }

        public void setCategoryItem(Map<EntityMapCompositeElementTest.Item, EntityMapCompositeElementTest.Value> categoryItem) {
            this.categoryItem = categoryItem;
        }

        public void setValue(EntityMapCompositeElementTest.Item key, EntityMapCompositeElementTest.Value value) {
            this.categoryItem.put(key, value);
        }

        public EntityMapCompositeElementTest.Value getValue(EntityMapCompositeElementTest.Item key) {
            return this.categoryItem.get(key);
        }

        public void setText(EntityMapCompositeElementTest.Item key, String value) {
            this.textItem.put(key, value);
        }

        public String getText(EntityMapCompositeElementTest.Item key) {
            return this.textItem.get(key);
        }
    }

    @Audited
    public static class Item {
        private Long id;

        private String name;

        Item() {
        }

        Item(String name) {
            this.name = name;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            EntityMapCompositeElementTest.Item item = ((EntityMapCompositeElementTest.Item) (o));
            return (id) != null ? id.equals(item.id) : (item.id) == null;
        }

        @Override
        public int hashCode() {
            return (id) != null ? id.hashCode() : 0;
        }
    }

    public static class Value implements Serializable {
        private String text;

        private Long numberValue;

        Value() {
        }

        Value(String text, Long numberValue) {
            this.text = text;
            this.numberValue = numberValue;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public Long getNumberValue() {
            return numberValue;
        }

        public void setNumberValue(Long numberValue) {
            this.numberValue = numberValue;
        }
    }
}

