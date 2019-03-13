/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.envers.test.integration.components;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import org.hibernate.envers.AuditMappedBy;
import org.hibernate.envers.Audited;
import org.hibernate.envers.test.BaseEnversJPAFunctionalTestCase;
import org.hibernate.envers.test.Priority;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Chris Cranford
 */
@TestForIssue(jiraKey = "HHH-9108")
public class EmbeddableWithCollectionTest extends BaseEnversJPAFunctionalTestCase {
    private Long headerId;

    @Test
    @Priority(10)
    public void initData() {
        // Revision 1
        headerId = TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.envers.test.integration.components.Header h1 = new org.hibernate.envers.test.integration.components.Header("h1");
            h1.addItem(new org.hibernate.envers.test.integration.components.Item("h1-item0", h1));
            h1.addItem(new org.hibernate.envers.test.integration.components.Item("h1-item1", h1));
            entityManager.persist(h1);
            return h1.getId();
        });
        // Revision 2
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            final org.hibernate.envers.test.integration.components.Header header = entityManager.find(.class, headerId);
            header.addItem(new org.hibernate.envers.test.integration.components.Item("h1-item2", header));
            entityManager.merge(header);
        });
        // Revision 3
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            final org.hibernate.envers.test.integration.components.Header header = entityManager.find(.class, headerId);
            header.removeItem(header.getEmbeddableWithCollection().getItems().get(0));
            entityManager.merge(header);
        });
        // Revision 4
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            final org.hibernate.envers.test.integration.components.Header header = entityManager.find(.class, headerId);
            header.setEmbeddableWithCollection(null);
            entityManager.merge(header);
        });
    }

    @Test
    public void testRevisionCounts() {
        Assert.assertEquals(Arrays.asList(1, 2, 3, 4), getAuditReader().getRevisions(EmbeddableWithCollectionTest.Header.class, headerId));
    }

    @Test
    public void testRevisionHistory() {
        final EmbeddableWithCollectionTest.Header rev1 = getAuditReader().find(EmbeddableWithCollectionTest.Header.class, headerId, 1);
        Assert.assertEquals(2, rev1.getEmbeddableWithCollection().getItems().size());
        Assert.assertEquals("h1-item0", rev1.getEmbeddableWithCollection().getItems().get(0).getName());
        Assert.assertEquals("h1-item1", rev1.getEmbeddableWithCollection().getItems().get(1).getName());
        final EmbeddableWithCollectionTest.Header rev2 = getAuditReader().find(EmbeddableWithCollectionTest.Header.class, headerId, 2);
        Assert.assertEquals(3, rev2.getEmbeddableWithCollection().getItems().size());
        Assert.assertEquals("h1-item0", rev2.getEmbeddableWithCollection().getItems().get(0).getName());
        Assert.assertEquals("h1-item1", rev2.getEmbeddableWithCollection().getItems().get(1).getName());
        Assert.assertEquals("h1-item2", rev2.getEmbeddableWithCollection().getItems().get(2).getName());
        final EmbeddableWithCollectionTest.Header rev3 = getAuditReader().find(EmbeddableWithCollectionTest.Header.class, headerId, 3);
        Assert.assertEquals(2, rev3.getEmbeddableWithCollection().getItems().size());
        Assert.assertEquals("h1-item1", rev3.getEmbeddableWithCollection().getItems().get(0).getName());
        Assert.assertEquals("h1-item2", rev3.getEmbeddableWithCollection().getItems().get(1).getName());
        final EmbeddableWithCollectionTest.Header rev4 = getAuditReader().find(EmbeddableWithCollectionTest.Header.class, headerId, 4);
        Assert.assertEquals(0, rev4.getEmbeddableWithCollection().getItems().size());
    }

    @Entity(name = "Header")
    @Table(name = "ENVERS_HEADER")
    @Audited
    public static class Header {
        private Long id;

        private String name;

        private EmbeddableWithCollectionTest.EmbeddableWithCollection embeddableWithCollection;

        Header() {
        }

        Header(String name) {
            this.name = name;
        }

        Header(Long id, String name) {
            this(name);
            this.id = id;
        }

        @Id
        @GeneratedValue
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

        @Embedded
        public EmbeddableWithCollectionTest.EmbeddableWithCollection getEmbeddableWithCollection() {
            return embeddableWithCollection;
        }

        public void setEmbeddableWithCollection(EmbeddableWithCollectionTest.EmbeddableWithCollection embeddableWithCollection) {
            if (embeddableWithCollection != null) {
                this.embeddableWithCollection = embeddableWithCollection;
            } else
                if ((this.embeddableWithCollection) != null) {
                    this.embeddableWithCollection.getItems().clear();
                }

        }

        public void addItem(EmbeddableWithCollectionTest.Item item) {
            if ((this.embeddableWithCollection) == null) {
                this.embeddableWithCollection = new EmbeddableWithCollectionTest.EmbeddableWithCollection();
            }
            this.embeddableWithCollection.addItem(item);
        }

        public void removeItem(EmbeddableWithCollectionTest.Item item) {
            if ((this.embeddableWithCollection) != null) {
                this.embeddableWithCollection.removeItem(item);
            }
        }
    }

    @Entity(name = "Item")
    @Table(name = "ENVERS_ITEM")
    @Audited
    public static class Item {
        private Long id;

        private String name;

        private EmbeddableWithCollectionTest.Header header;

        private Integer position;

        Item() {
        }

        Item(String name, EmbeddableWithCollectionTest.Header header) {
            this.name = name;
            this.header = header;
        }

        Item(Long id, String name, EmbeddableWithCollectionTest.Header header) {
            this(name, header);
            this.id = id;
        }

        @Id
        @GeneratedValue
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

        @ManyToOne
        @JoinColumn(name = "C_HDR", insertable = false, updatable = false, nullable = false)
        public EmbeddableWithCollectionTest.Header getHeader() {
            return header;
        }

        public void setHeader(EmbeddableWithCollectionTest.Header header) {
            this.header = header;
        }

        @Column(name = "C_POSITION", insertable = false, updatable = false)
        public Integer getPosition() {
            return position;
        }

        public void setPosition(Integer position) {
            this.position = position;
        }
    }

    @Embeddable
    @Audited
    public static class EmbeddableWithCollection {
        private List<EmbeddableWithCollectionTest.Item> items;

        @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
        @JoinColumn(name = "C_HDR", foreignKey = @ForeignKey(name = "ENVERS_ITEM_FK_ENVERS_HEADER"), nullable = false)
        @OrderColumn(name = "C_POSITION", nullable = false)
        @AuditMappedBy(mappedBy = "header", positionMappedBy = "position")
        public List<EmbeddableWithCollectionTest.Item> getItems() {
            return items;
        }

        public void setItems(List<EmbeddableWithCollectionTest.Item> items) {
            this.items = items;
        }

        public void addItem(EmbeddableWithCollectionTest.Item item) {
            if ((this.items) == null) {
                this.items = new ArrayList<EmbeddableWithCollectionTest.Item>();
            }
            this.items.add(item);
        }

        public void removeItem(EmbeddableWithCollectionTest.Item item) {
            if ((this.items) != null) {
                this.items.remove(item);
            }
        }
    }
}

