/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.envers.test.integration.manytomany;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import org.hamcrest.BaseMatcher;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Description;
import org.hibernate.envers.Audited;
import org.hibernate.envers.test.BaseEnversJPAFunctionalTestCase;
import org.hibernate.envers.test.Priority;
import org.hibernate.testing.TestForIssue;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Chris Cranford
 */
@TestForIssue(jiraKey = "HHH-12240")
public class MappedByEmbeddableAttributeTest extends BaseEnversJPAFunctionalTestCase {
    @Audited
    @Entity(name = "EntityA")
    public static class EntityA {
        @Id
        @GeneratedValue
        private Integer id;

        private String name;

        @Embedded
        private MappedByEmbeddableAttributeTest.Container container;

        EntityA() {
        }

        EntityA(String name) {
            this(name, new MappedByEmbeddableAttributeTest.Container());
        }

        EntityA(String name, MappedByEmbeddableAttributeTest.Container container) {
            this.name = name;
            this.container = container;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public MappedByEmbeddableAttributeTest.Container getContainer() {
            return container;
        }

        public void setContainer(MappedByEmbeddableAttributeTest.Container container) {
            this.container = container;
        }
    }

    @Embeddable
    public static class Container {
        @ManyToMany
        private List<MappedByEmbeddableAttributeTest.EntityB> bList = new ArrayList<>();

        public List<MappedByEmbeddableAttributeTest.EntityB> getbList() {
            return bList;
        }

        public void setbList(List<MappedByEmbeddableAttributeTest.EntityB> bList) {
            this.bList = bList;
        }
    }

    @Audited
    @Entity(name = "EntityB")
    public static class EntityB {
        @Id
        @GeneratedValue
        private Integer id;

        private String name;

        @ManyToMany(mappedBy = "container.bList")
        private List<MappedByEmbeddableAttributeTest.EntityA> aList = new ArrayList<>();

        EntityB() {
        }

        EntityB(String name, MappedByEmbeddableAttributeTest.EntityA... objects) {
            this.name = name;
            if ((objects.length) > 0) {
                for (MappedByEmbeddableAttributeTest.EntityA a : objects) {
                    this.aList.add(a);
                    a.getContainer().getbList().add(this);
                }
            }
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<MappedByEmbeddableAttributeTest.EntityA> getaList() {
            return aList;
        }

        public void setaList(List<MappedByEmbeddableAttributeTest.EntityA> aList) {
            this.aList = aList;
        }
    }

    private static class EntityBNameMatcher extends BaseMatcher<MappedByEmbeddableAttributeTest.EntityB> {
        private final String expectedValue;

        public EntityBNameMatcher(String name) {
            this.expectedValue = name;
        }

        @Override
        public boolean matches(Object item) {
            if (!(item instanceof MappedByEmbeddableAttributeTest.EntityB)) {
                return false;
            }
            MappedByEmbeddableAttributeTest.EntityB entityB = ((MappedByEmbeddableAttributeTest.EntityB) (item));
            return Objects.equals(entityB.getName(), this.expectedValue);
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("an instance of ").appendText(MappedByEmbeddableAttributeTest.EntityB.class.getName()).appendText(" named ").appendValue(this.expectedValue);
        }
    }

    private Integer aId;

    private Integer bId1;

    private Integer bId2;

    @Test
    @Priority(10)
    public void initData() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            final org.hibernate.envers.test.integration.manytomany.EntityA a = new org.hibernate.envers.test.integration.manytomany.EntityA("A");
            final org.hibernate.envers.test.integration.manytomany.EntityB b = new org.hibernate.envers.test.integration.manytomany.EntityB("B", a);
            entityManager.persist(a);
            entityManager.persist(b);
            this.aId = a.getId();
            this.bId1 = b.getId();
        });
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            final org.hibernate.envers.test.integration.manytomany.EntityA a = entityManager.find(.class, this.aId);
            for (org.hibernate.envers.test.integration.manytomany.EntityB b : a.getContainer().getbList()) {
                b.setName(((b.getName()) + "-Updated"));
                entityManager.merge(b);
            }
        });
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            final org.hibernate.envers.test.integration.manytomany.EntityA a = entityManager.find(.class, this.aId);
            final org.hibernate.envers.test.integration.manytomany.EntityB b = new org.hibernate.envers.test.integration.manytomany.EntityB("B2", a);
            entityManager.persist(b);
            entityManager.merge(a);
            this.bId2 = b.getId();
        });
    }

    @Test
    public void testRevisionHistoryEntityA() {
        List<Number> aRevisions = getAuditReader().getRevisions(MappedByEmbeddableAttributeTest.EntityA.class, this.aId);
        Assert.assertEquals(Arrays.asList(1, 3), aRevisions);
        MappedByEmbeddableAttributeTest.EntityA rev1 = getAuditReader().find(MappedByEmbeddableAttributeTest.EntityA.class, this.aId, 1);
        Assert.assertEquals(1, rev1.getContainer().getbList().size());
        Assert.assertEquals("B", rev1.getContainer().getbList().get(0).getName());
        MappedByEmbeddableAttributeTest.EntityA rev3 = getAuditReader().find(MappedByEmbeddableAttributeTest.EntityA.class, this.aId, 3);
        Assert.assertEquals(2, rev3.getContainer().getbList().size());
        Assert.assertThat(rev3.getContainer().getbList(), CoreMatchers.hasItem(new MappedByEmbeddableAttributeTest.EntityBNameMatcher("B-Updated")));
        Assert.assertThat(rev3.getContainer().getbList(), CoreMatchers.hasItem(new MappedByEmbeddableAttributeTest.EntityBNameMatcher("B2")));
    }

    @Test
    public void testRevisionHistoryEntityB() {
        List<Number> b1Revisions = getAuditReader().getRevisions(MappedByEmbeddableAttributeTest.EntityB.class, this.bId1);
        Assert.assertEquals(Arrays.asList(1, 2), b1Revisions);
        MappedByEmbeddableAttributeTest.EntityB b1Rev1 = getAuditReader().find(MappedByEmbeddableAttributeTest.EntityB.class, this.bId1, 1);
        Assert.assertEquals("B", b1Rev1.getName());
        Assert.assertEquals(1, b1Rev1.getaList().size());
        Assert.assertEquals(this.aId, b1Rev1.getaList().get(0).getId());
        MappedByEmbeddableAttributeTest.EntityB b1Rev2 = getAuditReader().find(MappedByEmbeddableAttributeTest.EntityB.class, this.bId1, 2);
        Assert.assertEquals("B-Updated", b1Rev2.getName());
        Assert.assertEquals(1, b1Rev1.getaList().size());
        Assert.assertEquals(this.aId, b1Rev1.getaList().get(0).getId());
        List<Number> b2Revisions = getAuditReader().getRevisions(MappedByEmbeddableAttributeTest.EntityB.class, this.bId2);
        Assert.assertEquals(Arrays.asList(3), b2Revisions);
        MappedByEmbeddableAttributeTest.EntityB b2Rev3 = getAuditReader().find(MappedByEmbeddableAttributeTest.EntityB.class, this.bId2, 3);
        Assert.assertEquals("B2", b2Rev3.getName());
        Assert.assertEquals(1, b2Rev3.getaList().size());
        Assert.assertEquals(this.aId, b2Rev3.getaList().get(0).getId());
    }
}

