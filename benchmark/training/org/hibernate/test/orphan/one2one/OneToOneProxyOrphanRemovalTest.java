/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.orphan.one2one;


import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import org.hibernate.Hibernate;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


/**
 * A test that shows orphan-removal is triggered when an entity has a lazy one-to-one
 * mapping with property-based annotations and the getter method unwraps the proxy
 * inline during invocation leading to constraint violation due to attempted removal
 * of the associated entity.
 *
 * This test case documents old behavior so that it can be preserved but allowing
 * us to also maintain the fix for {@code HHH-9663}.
 *
 * @author Chris Cranford
 */
@TestForIssue(jiraKey = "HHH-11965")
public class OneToOneProxyOrphanRemovalTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void testUnproxyOneToOneWithCascade() {
        Integer pId = TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.test.orphan.one2one.Parent p = new org.hibernate.test.orphan.one2one.Parent();
            p.setChild(new org.hibernate.test.orphan.one2one.Child());
            entityManager.persist(p);
            return p.getId();
        });
        // This lambda fails because during flush the cascade of operations determine that the entity state
        // maintains the unwrapped proxy (from the getter) does not match the value maintained in the persistence
        // context (which is the proxy).
        // 
        // This results in a comparison that deems the values different and allows the orphan-removal to proceed,
        // leading to a constraint violation because the 'Child' entity continues to be referentially linked to
        // the 'Parent' entity.
        // 
        // In short, no cascade of orphan-removal should be invoked for this scenario, thus avoiding the raised
        // constraint violation exception.
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            assertNotNull(entityManager.find(.class, pId));
        });
    }

    @Entity(name = "Child")
    public static class Child {
        private Integer id;

        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }
    }

    @Entity(name = "Parent")
    public static class Parent {
        private Integer id;

        private OneToOneProxyOrphanRemovalTest.Child child;

        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
        public OneToOneProxyOrphanRemovalTest.Child getChild() {
            return ((OneToOneProxyOrphanRemovalTest.Child) (Hibernate.unproxy(child)));
        }

        public void setChild(OneToOneProxyOrphanRemovalTest.Child child) {
            this.child = child;
        }
    }
}

