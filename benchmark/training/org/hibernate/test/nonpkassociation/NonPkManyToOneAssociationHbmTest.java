/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.nonpkassociation;


import java.util.HashSet;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Test;


/**
 *
 *
 * @author pholvs
 */
public class NonPkManyToOneAssociationHbmTest extends BaseCoreFunctionalTestCase {
    private NonPkManyToOneAssociationHbmTest.Parent parent;

    @Test
    public void testHqlWithFetch() {
        inTransaction(( s) -> {
            org.hibernate.test.nonpkassociation.Parent dbParent = s.find(.class, this.parent.getId());
            Set<org.hibernate.test.nonpkassociation.Child> children = dbParent.getChildren();
            assertEquals(1, children.size());
        });
    }

    public static class Parent {
        private Long id;

        private Long collectionKey;

        private java.util.Set<NonPkManyToOneAssociationHbmTest.Child> children = new HashSet<>();

        public Parent(Long collectionKey) {
            setCollectionKey(collectionKey);
        }

        Parent() {
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Long getCollectionKey() {
            return collectionKey;
        }

        public void setCollectionKey(Long collectionKey) {
            this.collectionKey = collectionKey;
        }

        public java.util.Set<NonPkManyToOneAssociationHbmTest.Child> getChildren() {
            return children;
        }

        public void setChildren(java.util.Set<NonPkManyToOneAssociationHbmTest.Child> children) {
            this.children = children;
        }
    }

    public static class Child {
        private Long id;

        private NonPkManyToOneAssociationHbmTest.Parent parent;

        public Child(NonPkManyToOneAssociationHbmTest.Parent parent) {
            setParent(parent);
        }

        Child() {
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public NonPkManyToOneAssociationHbmTest.Parent getParent() {
            return parent;
        }

        public void setParent(NonPkManyToOneAssociationHbmTest.Parent parent) {
            this.parent = parent;
        }
    }
}

