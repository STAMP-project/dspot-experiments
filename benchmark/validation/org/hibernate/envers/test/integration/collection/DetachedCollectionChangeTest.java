/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.envers.test.integration.collection;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
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
@TestForIssue(jiraKey = "HHH-13080")
public class DetachedCollectionChangeTest extends BaseEnversJPAFunctionalTestCase {
    @Audited
    @Entity(name = "Alert")
    public static class Alert {
        @Id
        @GeneratedValue
        private Integer id;

        @ManyToMany
        private List<DetachedCollectionChangeTest.RuleName> ruleNames = new ArrayList<>();

        @ElementCollection
        private List<String> names = new ArrayList<>();

        @ElementCollection
        private Set<DetachedCollectionChangeTest.CompositeName> composites = new HashSet<>();

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public List<DetachedCollectionChangeTest.RuleName> getRuleNames() {
            return ruleNames;
        }

        public void setRuleNames(List<DetachedCollectionChangeTest.RuleName> ruleNames) {
            this.ruleNames = ruleNames;
        }

        public List<String> getNames() {
            return names;
        }

        public void setNames(List<String> names) {
            this.names = names;
        }

        public Set<DetachedCollectionChangeTest.CompositeName> getComposites() {
            return composites;
        }

        public void setComposites(Set<DetachedCollectionChangeTest.CompositeName> composites) {
            this.composites = composites;
        }
    }

    @Audited
    @Entity(name = "RuleName")
    public static class RuleName {
        @Id
        @GeneratedValue
        private Integer id;

        private String name;

        public RuleName() {
        }

        public RuleName(String name) {
            this.name = name;
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

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            DetachedCollectionChangeTest.RuleName ruleName = ((DetachedCollectionChangeTest.RuleName) (o));
            return (Objects.equals(id, ruleName.id)) && (Objects.equals(name, ruleName.name));
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, name);
        }
    }

    @Embeddable
    public static class CompositeName implements Serializable {
        private String value1;

        private String value2;

        public CompositeName() {
        }

        public CompositeName(String value1, String value2) {
            this.value1 = value1;
            this.value2 = value2;
        }

        public String getValue1() {
            return value1;
        }

        public void setValue1(String value1) {
            this.value1 = value1;
        }

        public String getValue2() {
            return value2;
        }

        public void setValue2(String value2) {
            this.value2 = value2;
        }
    }

    private Integer ruleName1Id;

    private Integer ruleName2Id;

    private Integer alertId;

    @Test
    @Priority(10)
    public void initData() {
        EntityManager em = getEntityManager();
        DetachedCollectionChangeTest.RuleName ruleName1 = new DetachedCollectionChangeTest.RuleName();
        DetachedCollectionChangeTest.RuleName ruleName2 = new DetachedCollectionChangeTest.RuleName();
        DetachedCollectionChangeTest.CompositeName compositeName1 = new DetachedCollectionChangeTest.CompositeName("First1", "Last1");
        DetachedCollectionChangeTest.CompositeName compositeName2 = new DetachedCollectionChangeTest.CompositeName("First2", "Last2");
        DetachedCollectionChangeTest.Alert alert = new DetachedCollectionChangeTest.Alert();
        alert.getRuleNames().add(ruleName1);
        alert.getRuleNames().add(ruleName2);
        alert.getNames().add("N1");
        alert.getNames().add("N2");
        alert.getComposites().add(compositeName1);
        alert.getComposites().add(compositeName2);
        // Revision 1
        em.getTransaction().begin();
        em.persist(ruleName1);
        em.persist(ruleName2);
        em.persist(alert);
        em.getTransaction().commit();
        alertId = alert.id;
        ruleName1Id = ruleName1.id;
        ruleName2Id = ruleName2.id;
    }

    @Test
    @Priority(9)
    public void testRevisionsCounts() {
        Assert.assertEquals(Arrays.asList(1), getAuditReader().getRevisions(DetachedCollectionChangeTest.Alert.class, alertId));
        Assert.assertEquals(Arrays.asList(1), getAuditReader().getRevisions(DetachedCollectionChangeTest.RuleName.class, ruleName1Id));
        Assert.assertEquals(Arrays.asList(1), getAuditReader().getRevisions(DetachedCollectionChangeTest.RuleName.class, ruleName2Id));
    }

    @Test
    @Priority(8)
    public void testClearAndAddWithinTransactionDoesNotChangeAnything() {
        EntityManager em = getEntityManager();
        em.getTransaction().begin();
        final DetachedCollectionChangeTest.Alert alert = em.find(DetachedCollectionChangeTest.Alert.class, alertId);
        List<DetachedCollectionChangeTest.RuleName> ruleNamesClone = new ArrayList<>(alert.getRuleNames());
        List<String> namesClone = new ArrayList<>(alert.getNames());
        List<DetachedCollectionChangeTest.CompositeName> compositeNamesClones = new ArrayList<>(alert.getComposites());
        alert.getRuleNames().clear();
        alert.getRuleNames().addAll(ruleNamesClone);
        alert.getNames().clear();
        alert.getNames().addAll(namesClone);
        alert.getComposites().clear();
        alert.getComposites().addAll(compositeNamesClones);
        em.persist(alert);
        em.getTransaction().commit();
        Assert.assertEquals(Arrays.asList(1), getAuditReader().getRevisions(DetachedCollectionChangeTest.Alert.class, alertId));
        Assert.assertEquals(Arrays.asList(1), getAuditReader().getRevisions(DetachedCollectionChangeTest.RuleName.class, ruleName1Id));
        Assert.assertEquals(Arrays.asList(1), getAuditReader().getRevisions(DetachedCollectionChangeTest.RuleName.class, ruleName2Id));
    }

    @Test
    @Priority(7)
    public void testClearAddDetachedOutsideTransaction() {
        EntityManager em = getEntityManager();
        em.getTransaction().begin();
        final DetachedCollectionChangeTest.RuleName ruleName1 = em.find(DetachedCollectionChangeTest.RuleName.class, ruleName1Id);
        final DetachedCollectionChangeTest.RuleName ruleName2 = em.find(DetachedCollectionChangeTest.RuleName.class, ruleName2Id);
        final DetachedCollectionChangeTest.CompositeName compositeName1 = new DetachedCollectionChangeTest.CompositeName("First1", "Last1");
        final DetachedCollectionChangeTest.CompositeName compositeName2 = new DetachedCollectionChangeTest.CompositeName("First2", "Last2");
        List<DetachedCollectionChangeTest.RuleName> ruleNamesClone = Arrays.asList(ruleName1, ruleName2);
        List<String> namesClone = Arrays.asList("N1", "N2");
        List<DetachedCollectionChangeTest.CompositeName> compositeNamesClone = Arrays.asList(compositeName1, compositeName2);
        em.getTransaction().rollback();
        em.getTransaction().begin();
        DetachedCollectionChangeTest.Alert alert = em.find(DetachedCollectionChangeTest.Alert.class, alertId);
        alert.getRuleNames().clear();
        alert.getRuleNames().addAll(ruleNamesClone);
        alert.getNames().clear();
        alert.getNames().addAll(namesClone);
        alert.getComposites().clear();
        alert.getComposites().addAll(compositeNamesClone);
        em.persist(alert);
        em.getTransaction().commit();
        Assert.assertEquals(Arrays.asList(1), getAuditReader().getRevisions(DetachedCollectionChangeTest.Alert.class, alertId));
        Assert.assertEquals(Arrays.asList(1), getAuditReader().getRevisions(DetachedCollectionChangeTest.RuleName.class, ruleName1Id));
        Assert.assertEquals(Arrays.asList(1), getAuditReader().getRevisions(DetachedCollectionChangeTest.RuleName.class, ruleName2Id));
    }

    @Test
    @Priority(6)
    public void testClearAddOneWithinTransaction() {
        EntityManager em = getEntityManager();
        em.getTransaction().begin();
        DetachedCollectionChangeTest.Alert alert = em.find(DetachedCollectionChangeTest.Alert.class, alertId);
        List<DetachedCollectionChangeTest.RuleName> ruleNamesClone = new ArrayList<>(alert.getRuleNames());
        List<String> namesClone = new ArrayList<>(alert.getNames());
        List<DetachedCollectionChangeTest.CompositeName> compositeNamesClones = new ArrayList<>(alert.getComposites());
        alert.getRuleNames().clear();
        alert.getRuleNames().add(ruleNamesClone.get(0));
        alert.getNames().clear();
        alert.getNames().add(namesClone.get(0));
        alert.getComposites().clear();
        alert.getComposites().add(compositeNamesClones.get(0));
        em.persist(alert);
        em.getTransaction().commit();
        Assert.assertEquals(Arrays.asList(1, 2), getAuditReader().getRevisions(DetachedCollectionChangeTest.Alert.class, alertId));
        Assert.assertEquals(Arrays.asList(1), getAuditReader().getRevisions(DetachedCollectionChangeTest.RuleName.class, ruleName1Id));
        Assert.assertEquals(Arrays.asList(1), getAuditReader().getRevisions(DetachedCollectionChangeTest.RuleName.class, ruleName2Id));
    }
}

