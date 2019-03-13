/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.insertordering;


import DialectChecks.SupportsJdbcDriverProxying;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToOne;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.RequiresDialectFeature;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


/**
 *
 *
 * @author Harikant Verma
 * @author Vlad Mihalcea
 */
@TestForIssue(jiraKey = "HHH-13068")
@RequiresDialectFeature(SupportsJdbcDriverProxying.class)
public class InsertOrderingSelfReferenceSingleTableInheritance extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void test1() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.test.insertordering.NodeLongValue longVal = new org.hibernate.test.insertordering.NodeLongValue();
            longVal.setLongValue(123L);
            org.hibernate.test.insertordering.NodeStringValue stringVal = new org.hibernate.test.insertordering.NodeStringValue();
            stringVal.setStringValue("Node 123");
            org.hibernate.test.insertordering.EntityTreeNode etn = new org.hibernate.test.insertordering.EntityTreeNode(null, null);
            org.hibernate.test.insertordering.ContentNode cn1 = new org.hibernate.test.insertordering.ContentNode(stringVal, null, null);
            org.hibernate.test.insertordering.ContentNode cn2 = new org.hibernate.test.insertordering.ContentNode(longVal, cn1, null);
            org.hibernate.test.insertordering.NodeLink nl = new org.hibernate.test.insertordering.NodeLink(cn2);
            org.hibernate.test.insertordering.ReferNode rn1 = new org.hibernate.test.insertordering.ReferNode(etn, null, nl);
            entityManager.persist(rn1);
        });
    }

    @Test
    public void test2() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.test.insertordering.NodeLongValue aparam = new org.hibernate.test.insertordering.NodeLongValue();
            aparam.setLongValue(123L);
            org.hibernate.test.insertordering.ContentNode xa = new org.hibernate.test.insertordering.ContentNode(aparam, null, null);
            org.hibernate.test.insertordering.ContentNode xb = new org.hibernate.test.insertordering.ContentNode(aparam, null, null);
            org.hibernate.test.insertordering.ContentNode xc = new org.hibernate.test.insertordering.ContentNode(aparam, xb, null);
            org.hibernate.test.insertordering.NodeLink nl = new org.hibernate.test.insertordering.NodeLink(xc);
            org.hibernate.test.insertordering.ReferNode ya = new org.hibernate.test.insertordering.ReferNode(xa, null, nl);
            entityManager.persist(ya);
        });
    }

    @Test
    public void test3() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.test.insertordering.NodeLongValue longVal = new org.hibernate.test.insertordering.NodeLongValue();
            longVal.setLongValue(123L);
            org.hibernate.test.insertordering.NodeStringValue stringVal = new org.hibernate.test.insertordering.NodeStringValue();
            stringVal.setStringValue("Node 123");
            org.hibernate.test.insertordering.EntityTreeNode etn = new org.hibernate.test.insertordering.EntityTreeNode(null, null);
            org.hibernate.test.insertordering.ContentNode cn1 = new org.hibernate.test.insertordering.ContentNode(stringVal, null, null);
            org.hibernate.test.insertordering.ContentNode cn2 = new org.hibernate.test.insertordering.ContentNode(longVal, cn1, null);
            org.hibernate.test.insertordering.ReferNode rn1 = new org.hibernate.test.insertordering.ReferNode(etn, cn2, null);
            entityManager.persist(rn1);
        });
    }

    @Test
    public void test4() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.test.insertordering.NodeLongValue longVal = new org.hibernate.test.insertordering.NodeLongValue();
            longVal.setLongValue(123L);
            org.hibernate.test.insertordering.NodeStringValue stringVal = new org.hibernate.test.insertordering.NodeStringValue();
            stringVal.setStringValue("Node 123");
            org.hibernate.test.insertordering.ContentNode cn1 = new org.hibernate.test.insertordering.ContentNode(stringVal, null, null);
            org.hibernate.test.insertordering.ContentNode cn2 = new org.hibernate.test.insertordering.ContentNode(longVal, cn1, null);
            org.hibernate.test.insertordering.ContentNode cn3 = new org.hibernate.test.insertordering.ContentNode(null, cn1, cn2);
            entityManager.persist(cn3);
        });
    }

    @Test
    public void test5() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.test.insertordering.NodeLongValue longVal = new org.hibernate.test.insertordering.NodeLongValue();
            longVal.setLongValue(123L);
            org.hibernate.test.insertordering.NodeStringValue stringVal = new org.hibernate.test.insertordering.NodeStringValue();
            stringVal.setStringValue("Node 123");
            org.hibernate.test.insertordering.EntityTreeNode etn = new org.hibernate.test.insertordering.EntityTreeNode(null, null);
            org.hibernate.test.insertordering.ContentNode cn1 = new org.hibernate.test.insertordering.ContentNode(stringVal, null, null);
            org.hibernate.test.insertordering.ContentNode cn2 = new org.hibernate.test.insertordering.ContentNode(longVal, cn1, null);
            org.hibernate.test.insertordering.ContentNode cn3 = new org.hibernate.test.insertordering.ContentNode(null, etn, cn2);
            entityManager.persist(cn3);
        });
    }

    @Test
    public void test6() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.test.insertordering.NodeLongValue longVal = new org.hibernate.test.insertordering.NodeLongValue();
            longVal.setLongValue(123L);
            org.hibernate.test.insertordering.NodeStringValue stringVal = new org.hibernate.test.insertordering.NodeStringValue();
            stringVal.setStringValue("Node 123");
            org.hibernate.test.insertordering.EntityTreeNode etn = new org.hibernate.test.insertordering.EntityTreeNode(null, null);
            org.hibernate.test.insertordering.ContentNode cn1 = new org.hibernate.test.insertordering.ContentNode(stringVal, null, null);
            org.hibernate.test.insertordering.ReferNode rn1 = new org.hibernate.test.insertordering.ReferNode(null, cn1, null);
            org.hibernate.test.insertordering.ContentNode cn3 = new org.hibernate.test.insertordering.ContentNode(longVal, etn, rn1);
            entityManager.persist(cn3);
        });
    }

    @Test
    public void test7() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.test.insertordering.NodeStringValue stringVal = new org.hibernate.test.insertordering.NodeStringValue();
            stringVal.setStringValue("Node 123");
            org.hibernate.test.insertordering.EntityTreeNode etn = new org.hibernate.test.insertordering.EntityTreeNode(null, null);
            org.hibernate.test.insertordering.ContentNode cn1 = new org.hibernate.test.insertordering.ContentNode(null, etn, null);
            org.hibernate.test.insertordering.ReferNode rn1 = new org.hibernate.test.insertordering.ReferNode(null, cn1, null);
            entityManager.persist(rn1);
        });
    }

    @Test
    public void test8() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.test.insertordering.NodeStringValue stringVal = new org.hibernate.test.insertordering.NodeStringValue();
            stringVal.setStringValue("Node 123");
            org.hibernate.test.insertordering.EntityTreeNode etn = new org.hibernate.test.insertordering.EntityTreeNode(null, null);
            org.hibernate.test.insertordering.ReferNode rn1 = new org.hibernate.test.insertordering.ReferNode(null, etn, null);
            org.hibernate.test.insertordering.ContentNode cn3 = new org.hibernate.test.insertordering.ContentNode(null, rn1, null);
            entityManager.persist(cn3);
        });
    }

    @Test
    public void test9() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.test.insertordering.NodeStringValue stringVal = new org.hibernate.test.insertordering.NodeStringValue();
            stringVal.setStringValue("Node 123");
            org.hibernate.test.insertordering.EntityTreeNode etn = new org.hibernate.test.insertordering.EntityTreeNode(null, null);
            org.hibernate.test.insertordering.ContentNode cn1 = new org.hibernate.test.insertordering.ContentNode(null, etn, null);
            org.hibernate.test.insertordering.ReferNode rn1 = new org.hibernate.test.insertordering.ReferNode(null, cn1, null);
            org.hibernate.test.insertordering.ContentNode cn3 = new org.hibernate.test.insertordering.ContentNode(null, rn1, null);
            entityManager.persist(cn3);
        });
    }

    @Entity(name = "EntityTreeNode")
    @DynamicUpdate
    @DynamicInsert
    @Inheritance(strategy = InheritanceType.SINGLE_TABLE)
    public static class EntityTreeNode {
        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE)
        private Long id;

        @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
        private InsertOrderingSelfReferenceSingleTableInheritance.EntityTreeNode leftNode;

        @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
        private InsertOrderingSelfReferenceSingleTableInheritance.EntityTreeNode rightNode;

        public EntityTreeNode(InsertOrderingSelfReferenceSingleTableInheritance.EntityTreeNode leftNode, InsertOrderingSelfReferenceSingleTableInheritance.EntityTreeNode rightNode) {
            super();
            this.leftNode = leftNode;
            this.rightNode = rightNode;
        }

        public EntityTreeNode() {
            super();
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public InsertOrderingSelfReferenceSingleTableInheritance.EntityTreeNode getLeftNode() {
            return leftNode;
        }

        public void setLeftNode(InsertOrderingSelfReferenceSingleTableInheritance.EntityTreeNode leftNode) {
            this.leftNode = leftNode;
        }

        public InsertOrderingSelfReferenceSingleTableInheritance.EntityTreeNode getRightNode() {
            return rightNode;
        }

        public void setRightNode(InsertOrderingSelfReferenceSingleTableInheritance.EntityTreeNode rightNode) {
            this.rightNode = rightNode;
        }
    }

    @Entity(name = "ContentNode")
    @DynamicUpdate
    @DynamicInsert
    @Inheritance(strategy = InheritanceType.SINGLE_TABLE)
    public static class ContentNode extends InsertOrderingSelfReferenceSingleTableInheritance.EntityTreeNode {
        @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
        private InsertOrderingSelfReferenceSingleTableInheritance.TreeNodeValue nodeValue;

        public InsertOrderingSelfReferenceSingleTableInheritance.TreeNodeValue getNodeValue() {
            return nodeValue;
        }

        public void setNodeValue(InsertOrderingSelfReferenceSingleTableInheritance.TreeNodeValue nodeValue) {
            this.nodeValue = nodeValue;
        }

        public ContentNode(InsertOrderingSelfReferenceSingleTableInheritance.TreeNodeValue nodeValue, InsertOrderingSelfReferenceSingleTableInheritance.EntityTreeNode leftNode, InsertOrderingSelfReferenceSingleTableInheritance.EntityTreeNode rightNode) {
            super(leftNode, rightNode);
            this.nodeValue = nodeValue;
        }

        public ContentNode() {
            super();
        }
    }

    @Entity(name = "NodeLink")
    @DynamicUpdate
    @DynamicInsert
    @Inheritance(strategy = InheritanceType.JOINED)
    public static class NodeLink {
        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE)
        private Long id;

        @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
        private InsertOrderingSelfReferenceSingleTableInheritance.ContentNode toNode;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public InsertOrderingSelfReferenceSingleTableInheritance.ContentNode getToNode() {
            return this.toNode;
        }

        public void setToNode(InsertOrderingSelfReferenceSingleTableInheritance.ContentNode toNode) {
            this.toNode = toNode;
        }

        public NodeLink(InsertOrderingSelfReferenceSingleTableInheritance.ContentNode toNode) {
            super();
            setToNode(toNode);
        }

        public NodeLink() {
            super();
        }
    }

    @Entity(name = "NodeLongValue")
    @DynamicUpdate
    @DynamicInsert
    public static class NodeLongValue extends InsertOrderingSelfReferenceSingleTableInheritance.TreeNodeValue {
        Long longValue;

        public Long getLongValue() {
            return longValue;
        }

        public void setLongValue(Long longValue) {
            this.longValue = longValue;
        }

        public NodeLongValue(String dataType, Long longValue) {
            super(dataType);
            this.longValue = longValue;
        }

        public NodeLongValue() {
            super();
        }
    }

    @Entity(name = "NodeStringValue")
    @DynamicUpdate
    @DynamicInsert
    public static class NodeStringValue extends InsertOrderingSelfReferenceSingleTableInheritance.TreeNodeValue {
        String stringValue;

        public String getStringValue() {
            return stringValue;
        }

        public void setStringValue(String stringValue) {
            this.stringValue = stringValue;
        }

        public NodeStringValue(String dataType, String stringValue) {
            super(dataType);
            this.stringValue = stringValue;
        }

        public NodeStringValue() {
            super();
        }
    }

    @Entity(name = "ReferNode")
    @DynamicUpdate
    @DynamicInsert
    @Inheritance(strategy = InheritanceType.SINGLE_TABLE)
    public static class ReferNode extends InsertOrderingSelfReferenceSingleTableInheritance.EntityTreeNode {
        @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
        private InsertOrderingSelfReferenceSingleTableInheritance.NodeLink nodeLink;

        public InsertOrderingSelfReferenceSingleTableInheritance.NodeLink getNodeLink() {
            return nodeLink;
        }

        public void setNodeLink(InsertOrderingSelfReferenceSingleTableInheritance.NodeLink nodeLink) {
            this.nodeLink = nodeLink;
        }

        public ReferNode(InsertOrderingSelfReferenceSingleTableInheritance.EntityTreeNode leftNode, InsertOrderingSelfReferenceSingleTableInheritance.EntityTreeNode rightNode, InsertOrderingSelfReferenceSingleTableInheritance.NodeLink nodeLink) {
            super(leftNode, rightNode);
            this.nodeLink = nodeLink;
        }

        public ReferNode() {
            super();
        }
    }

    @Entity(name = "TreeNodeValue")
    @DynamicUpdate
    @DynamicInsert
    @Inheritance(strategy = InheritanceType.SINGLE_TABLE)
    public static class TreeNodeValue {
        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE)
        private Long id;

        private String dataType;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getDataType() {
            return dataType;
        }

        public void setDataType(String dataType) {
            this.dataType = dataType;
        }

        public TreeNodeValue(String dataType) {
            super();
            this.dataType = dataType;
        }

        public TreeNodeValue() {
            super();
        }
    }
}

