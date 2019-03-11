/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.envers.test.integration.hashcode;


import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import org.hibernate.envers.Audited;
import org.hibernate.envers.test.BaseEnversJPAFunctionalTestCase;
import org.hibernate.envers.test.Priority;
import org.hibernate.envers.test.tools.TestTools;
import org.hibernate.testing.TestForIssue;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Chris Cranford
 */
@TestForIssue(jiraKey = "HHH-11063")
public class ComponentCollectionHashcodeChangeTest extends BaseEnversJPAFunctionalTestCase {
    private Integer id;

    @Test
    @Priority(10)
    public void initData() {
        EntityManager entityManager = getEntityManager();
        try {
            // Revision 1 - Create entity with 2 components
            entityManager.getTransaction().begin();
            ComponentCollectionHashcodeChangeTest.Component component1 = new ComponentCollectionHashcodeChangeTest.Component();
            component1.setName("User1");
            component1.setData("Test1");
            ComponentCollectionHashcodeChangeTest.Component component2 = new ComponentCollectionHashcodeChangeTest.Component();
            component2.setName("User2");
            component2.setData("Test2");
            ComponentCollectionHashcodeChangeTest.ComponentEntity entity = new ComponentCollectionHashcodeChangeTest.ComponentEntity();
            entity.getComponents().add(component1);
            entity.getComponents().add(component2);
            entityManager.persist(entity);
            entityManager.getTransaction().commit();
            id = entity.getId();
            // Revision 2 - Change component name inline.
            // This effectively changes equality and hash of elment.
            entityManager.getTransaction().begin();
            component1.setName("User1-Inline");
            entityManager.merge(entity);
            entityManager.getTransaction().commit();
            // Revision 3 - Remove and add entity with same name.
            entityManager.getTransaction().begin();
            entity.getComponents().remove(component2);
            ComponentCollectionHashcodeChangeTest.Component component3 = new ComponentCollectionHashcodeChangeTest.Component();
            component3.setName("User2");
            component3.setData("Test3");
            entity.getComponents().add(component3);
            entityManager.merge(entity);
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
        } finally {
            entityManager.close();
        }
    }

    @Test
    public void testRevisionCounts() {
        Assert.assertEquals(Arrays.asList(1, 2, 3), getAuditReader().getRevisions(ComponentCollectionHashcodeChangeTest.ComponentEntity.class, id));
    }

    @Test
    public void testCollectionHistory() {
        final ComponentCollectionHashcodeChangeTest.ComponentEntity rev1 = getAuditReader().find(ComponentCollectionHashcodeChangeTest.ComponentEntity.class, id, 1);
        Assert.assertEquals(2, rev1.getComponents().size());
        Assert.assertEquals(TestTools.makeSet(new ComponentCollectionHashcodeChangeTest.Component("User1", "Test1"), new ComponentCollectionHashcodeChangeTest.Component("User2", "Test2")), rev1.getComponents());
        final ComponentCollectionHashcodeChangeTest.ComponentEntity rev2 = getAuditReader().find(ComponentCollectionHashcodeChangeTest.ComponentEntity.class, id, 2);
        Assert.assertEquals(2, rev2.getComponents().size());
        Assert.assertEquals(TestTools.makeSet(new ComponentCollectionHashcodeChangeTest.Component("User1-Inline", "Test1"), new ComponentCollectionHashcodeChangeTest.Component("User2", "Test2")), rev2.getComponents());
        final ComponentCollectionHashcodeChangeTest.ComponentEntity rev3 = getAuditReader().find(ComponentCollectionHashcodeChangeTest.ComponentEntity.class, id, 3);
        Assert.assertEquals(2, rev3.getComponents().size());
        Assert.assertEquals(TestTools.makeSet(new ComponentCollectionHashcodeChangeTest.Component("User1-Inline", "Test1"), new ComponentCollectionHashcodeChangeTest.Component("User2", "Test3")), rev3.getComponents());
    }

    @Entity(name = "ComponentEntity")
    @Audited
    public static class ComponentEntity {
        @Id
        @GeneratedValue
        private Integer id;

        @ElementCollection
        private Set<ComponentCollectionHashcodeChangeTest.Component> components = new HashSet<ComponentCollectionHashcodeChangeTest.Component>();

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public Set<ComponentCollectionHashcodeChangeTest.Component> getComponents() {
            return components;
        }

        public void setComponents(Set<ComponentCollectionHashcodeChangeTest.Component> components) {
            this.components = components;
        }
    }

    @Audited
    @Embeddable
    public static class Component {
        private String name;

        private String data;

        Component() {
        }

        Component(String name, String data) {
            this.name = name;
            this.data = data;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }

        @Override
        public int hashCode() {
            return (name) != null ? name.hashCode() : 0;
        }

        @Override
        public boolean equals(Object object) {
            if (object == (this)) {
                return true;
            }
            if ((object == null) || (!(object instanceof ComponentCollectionHashcodeChangeTest.Component))) {
                return false;
            }
            ComponentCollectionHashcodeChangeTest.Component that = ((ComponentCollectionHashcodeChangeTest.Component) (object));
            return !((name) != null ? !(name.equals(that.name)) : (that.name) != null);
        }
    }
}

