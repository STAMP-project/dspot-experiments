/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.cascade.multilevel;


import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.transaction.TransactionUtil;
import org.jboss.logging.Logger;
import org.junit.Test;


public class MultiLevelCascadeRegularIdBasedParentChildAssociationTest extends BaseEntityManagerFunctionalTestCase {
    private static final Logger log = Logger.getLogger(MultiLevelCascadeRegularIdBasedParentChildAssociationTest.class);

    @Test
    @TestForIssue(jiraKey = "HHH-12291")
    public void testHibernateDeleteEntityWithoutInitializingCollections() throws Exception {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.jpa.test.cascade.multilevel.Parent mainEntity = entityManager.find(.class, 1L);
            assertNotNull(mainEntity);
            assertFalse(mainEntity.getChildren().isEmpty());
            Optional<org.hibernate.jpa.test.cascade.multilevel.Child> childToRemove = mainEntity.getChildren().stream().filter(( child) -> Long.valueOf(1L).equals(child.id)).findFirst();
            childToRemove.ifPresent(mainEntity::removeChild);
        });
    }

    @Entity(name = "Parent")
    public static class Parent {
        @Id
        private Long id;

        @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
        private List<MultiLevelCascadeRegularIdBasedParentChildAssociationTest.Child> children = new ArrayList<>();

        public List<MultiLevelCascadeRegularIdBasedParentChildAssociationTest.Child> getChildren() {
            return children;
        }

        public void addChild(MultiLevelCascadeRegularIdBasedParentChildAssociationTest.Child child) {
            child.setParent(this);
            children.add(child);
        }

        public void removeChild(MultiLevelCascadeRegularIdBasedParentChildAssociationTest.Child child) {
            child.setParent(null);
            children.remove(child);
        }
    }

    @Entity(name = "Child")
    public static class Child {
        @Id
        private Long id;

        @ManyToOne(fetch = FetchType.LAZY)
        private MultiLevelCascadeRegularIdBasedParentChildAssociationTest.Parent parent;

        @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
        private List<MultiLevelCascadeRegularIdBasedParentChildAssociationTest.Hobby> hobbies = new ArrayList<>();

        @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
        private List<MultiLevelCascadeRegularIdBasedParentChildAssociationTest.Skill> skills = new ArrayList<>();

        public MultiLevelCascadeRegularIdBasedParentChildAssociationTest.Parent getParent() {
            return parent;
        }

        public void setParent(MultiLevelCascadeRegularIdBasedParentChildAssociationTest.Parent mainEntity) {
            this.parent = mainEntity;
        }

        public void addSkill(MultiLevelCascadeRegularIdBasedParentChildAssociationTest.Skill skill) {
            skill.owner = this;
            skills.add(skill);
        }
    }

    @Entity(name = "Skill")
    public static class Skill {
        @Id
        private Long id;

        @ManyToOne(fetch = FetchType.LAZY)
        private MultiLevelCascadeRegularIdBasedParentChildAssociationTest.Child owner;
    }

    @Entity(name = "Hobby")
    public static class Hobby {
        @Id
        private Long id;

        @ManyToOne(fetch = FetchType.LAZY)
        private MultiLevelCascadeRegularIdBasedParentChildAssociationTest.Child owner;
    }
}

