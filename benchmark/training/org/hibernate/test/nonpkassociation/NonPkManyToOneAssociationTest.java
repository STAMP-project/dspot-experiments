/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.nonpkassociation;


import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Test;


/**
 *
 *
 * @author pholvs
 */
public class NonPkManyToOneAssociationTest extends BaseCoreFunctionalTestCase {
    private NonPkManyToOneAssociationTest.Parent parent;

    @Test
    public void testHqlWithFetch() {
        inTransaction(( s) -> {
            org.hibernate.test.nonpkassociation.Parent parent = s.find(.class, this.parent.getId());
            assertEquals(1, parent.getChildren().size());
        });
    }

    @Entity(name = "Parent")
    public static class Parent implements Serializable {
        @Id
        @GeneratedValue
        private Long id;

        private Long collectionKey;

        @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
        private Set<NonPkManyToOneAssociationTest.Child> children = new HashSet<>();

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

        public Set<NonPkManyToOneAssociationTest.Child> getChildren() {
            return children;
        }

        public void setChildren(Set<NonPkManyToOneAssociationTest.Child> children) {
            this.children = children;
        }
    }

    @Entity(name = "Child")
    public static class Child {
        @Id
        @GeneratedValue
        private Long id;

        @ManyToOne
        @JoinColumn(name = "parentVal", referencedColumnName = "collectionKey")
        private NonPkManyToOneAssociationTest.Parent parent;

        public Child(NonPkManyToOneAssociationTest.Parent parent) {
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

        public NonPkManyToOneAssociationTest.Parent getParent() {
            return parent;
        }

        public void setParent(NonPkManyToOneAssociationTest.Parent parent) {
            this.parent = parent;
        }
    }
}

