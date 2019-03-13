/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.event;


import DialectChecks.SupportsIdentityColumns;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import org.hibernate.action.internal.EntityActionVetoException;
import org.hibernate.testing.RequiresDialectFeature;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Chris Cranford
 */
@RequiresDialectFeature(SupportsIdentityColumns.class)
@TestForIssue(jiraKey = "HHH-11721")
public class PreInsertEventListenerVetoUnidirectionalTest extends BaseCoreFunctionalTestCase {
    @Test(expected = EntityActionVetoException.class)
    public void testVeto() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.event.Parent parent = new org.hibernate.event.Parent();
            parent.setField1("f1");
            parent.setfield2("f2");
            org.hibernate.event.Child child = new org.hibernate.event.Child();
            child.setParent(parent);
            session.save(child);
        });
        Assert.fail("Should have thrown EntityActionVetoException!");
    }

    @Entity(name = "Child")
    public static class Child {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Integer id;

        @OneToOne(cascade = CascadeType.ALL)
        private PreInsertEventListenerVetoUnidirectionalTest.Parent parent;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public PreInsertEventListenerVetoUnidirectionalTest.Parent getParent() {
            return parent;
        }

        public void setParent(PreInsertEventListenerVetoUnidirectionalTest.Parent parent) {
            this.parent = parent;
        }
    }

    @Entity(name = "Parent")
    public static class Parent {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Integer id;

        private String field1;

        private String field2;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getField1() {
            return field1;
        }

        public void setField1(String field1) {
            this.field1 = field1;
        }

        public String getField2() {
            return field2;
        }

        public void setfield2(String field2) {
            this.field2 = field2;
        }
    }
}

