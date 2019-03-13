/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.envers.test.integration.components;


import java.util.Arrays;
import javax.persistence.EntityManager;
import org.hibernate.envers.test.BaseEnversJPAFunctionalTestCase;
import org.hibernate.envers.test.Priority;
import org.hibernate.envers.test.entities.components.DefaultValueComponent1;
import org.hibernate.envers.test.entities.components.DefaultValueComponent2;
import org.hibernate.envers.test.entities.components.DefaultValueComponentTestEntity;
import org.jboss.logging.Logger;
import org.junit.Test;


/**
 * Test class for components with default values.
 *
 * @author Erik-Berndt Scheper
 * @see <a
href="http://opensource.atlassian.com/projects/hibernate/browse/HHH-5288">
Hibernate JIRA </a>
 */
public class DefaultValueComponents extends BaseEnversJPAFunctionalTestCase {
    private static final Logger log = Logger.getLogger(DefaultValueComponents.class);

    private Integer id0;

    private Integer id1;

    private Integer id2;

    private Integer id3;

    private Integer id4;

    private Integer id5;

    private Integer id6;

    @Test
    @Priority(10)
    public void initData() {
        // Revision 1
        EntityManager em = getEntityManager();
        em.getTransaction().begin();
        DefaultValueComponentTestEntity cte0 = DefaultValueComponentTestEntity.of(null);
        DefaultValueComponentTestEntity cte1 = DefaultValueComponentTestEntity.of(DefaultValueComponent1.of("c1-str1", null));
        DefaultValueComponentTestEntity cte2 = DefaultValueComponentTestEntity.of(DefaultValueComponent1.of("c1-str1", DefaultValueComponent2.of("c2-str1", "c2-str2")));
        DefaultValueComponentTestEntity cte3 = DefaultValueComponentTestEntity.of(DefaultValueComponent1.of(null, DefaultValueComponent2.of("c2-str1", "c2-str2")));
        DefaultValueComponentTestEntity cte4 = DefaultValueComponentTestEntity.of(DefaultValueComponent1.of(null, DefaultValueComponent2.of(null, "c2-str2")));
        DefaultValueComponentTestEntity cte5 = DefaultValueComponentTestEntity.of(DefaultValueComponent1.of(null, DefaultValueComponent2.of("c2-str1", null)));
        DefaultValueComponentTestEntity cte6 = DefaultValueComponentTestEntity.of(DefaultValueComponent1.of(null, DefaultValueComponent2.of(null, null)));
        em.persist(cte0);
        em.persist(cte1);
        em.persist(cte2);
        em.persist(cte3);
        em.persist(cte4);
        em.persist(cte5);
        em.persist(cte6);
        em.getTransaction().commit();
        // Revision 2
        em = getEntityManager();
        em.getTransaction().begin();
        cte0 = em.find(DefaultValueComponentTestEntity.class, cte0.getId());
        cte1 = em.find(DefaultValueComponentTestEntity.class, cte1.getId());
        cte2 = em.find(DefaultValueComponentTestEntity.class, cte2.getId());
        cte3 = em.find(DefaultValueComponentTestEntity.class, cte3.getId());
        cte4 = em.find(DefaultValueComponentTestEntity.class, cte4.getId());
        cte5 = em.find(DefaultValueComponentTestEntity.class, cte5.getId());
        cte6 = em.find(DefaultValueComponentTestEntity.class, cte6.getId());
        cte0.setComp1(DefaultValueComponent1.of("upd-c1-str1", null));
        cte1.setComp1(DefaultValueComponent1.of(null, DefaultValueComponent2.of("upd-c2-str1", "upd-c2-str2")));
        cte2.getComp1().getComp2().setStr1("upd-c2-str1");
        cte3.getComp1().getComp2().setStr1("upd-c2-str1");
        cte4.getComp1().getComp2().setStr1("upd-c2-str1");
        cte5.getComp1().getComp2().setStr1("upd-c2-str1");
        cte6.getComp1().getComp2().setStr1("upd-c2-str1");
        em.getTransaction().commit();
        // afterwards
        id0 = cte0.getId();
        id1 = cte1.getId();
        id2 = cte2.getId();
        id3 = cte3.getId();
        id4 = cte4.getId();
        id5 = cte5.getId();
        id6 = cte6.getId();
    }

    @Test
    public void testRevisionsCounts() {
        DefaultValueComponents.log.error(getAuditReader().getRevisions(DefaultValueComponentTestEntity.class, id0).toString());
        DefaultValueComponents.log.error(getAuditReader().getRevisions(DefaultValueComponentTestEntity.class, id1).toString());
        DefaultValueComponents.log.error(getAuditReader().getRevisions(DefaultValueComponentTestEntity.class, id2).toString());
        DefaultValueComponents.log.error(getAuditReader().getRevisions(DefaultValueComponentTestEntity.class, id3).toString());
        DefaultValueComponents.log.error(getAuditReader().getRevisions(DefaultValueComponentTestEntity.class, id4).toString());
        DefaultValueComponents.log.error(getAuditReader().getRevisions(DefaultValueComponentTestEntity.class, id5).toString());
        DefaultValueComponents.log.error(getAuditReader().getRevisions(DefaultValueComponentTestEntity.class, id6).toString());
        assert Arrays.asList(1, 2).equals(getAuditReader().getRevisions(DefaultValueComponentTestEntity.class, id0));
        assert Arrays.asList(1, 2).equals(getAuditReader().getRevisions(DefaultValueComponentTestEntity.class, id1));
        assert Arrays.asList(1, 2).equals(getAuditReader().getRevisions(DefaultValueComponentTestEntity.class, id2));
        assert Arrays.asList(1, 2).equals(getAuditReader().getRevisions(DefaultValueComponentTestEntity.class, id3));
        assert Arrays.asList(1, 2).equals(getAuditReader().getRevisions(DefaultValueComponentTestEntity.class, id4));
        assert Arrays.asList(1, 2).equals(getAuditReader().getRevisions(DefaultValueComponentTestEntity.class, id5));
        assert Arrays.asList(1, 2).equals(getAuditReader().getRevisions(DefaultValueComponentTestEntity.class, id6));
    }

    @Test
    public void testHistoryOfId0() {
        DefaultValueComponentTestEntity ent1 = getAuditReader().find(DefaultValueComponentTestEntity.class, id0, 1);
        DefaultValueComponentTestEntity ent2 = getAuditReader().find(DefaultValueComponentTestEntity.class, id0, 2);
        DefaultValueComponents.log.error("------------ id0 -------------");
        DefaultValueComponents.log.error(ent1.toString());
        DefaultValueComponents.log.error(ent2.toString());
        checkCorrectlyPersisted(id0, null, null);
        DefaultValueComponentTestEntity expectedVer1 = DefaultValueComponentTestEntity.of(id0, DefaultValueComponent1.of(null, null));
        DefaultValueComponentTestEntity expectedVer2 = DefaultValueComponentTestEntity.of(id0, DefaultValueComponent1.of("upd-c1-str1", null));
        assert ent1.equals(expectedVer1);
        assert ent2.equals(expectedVer2);
    }

    @Test
    public void testHistoryOfId1() {
        DefaultValueComponentTestEntity ent1 = getAuditReader().find(DefaultValueComponentTestEntity.class, id1, 1);
        DefaultValueComponentTestEntity ent2 = getAuditReader().find(DefaultValueComponentTestEntity.class, id1, 2);
        DefaultValueComponents.log.error("------------ id1 -------------");
        DefaultValueComponents.log.error(ent1.toString());
        DefaultValueComponents.log.error(ent2.toString());
        checkCorrectlyPersisted(id1, null, "upd-c2-str1");
        DefaultValueComponentTestEntity expectedVer1 = DefaultValueComponentTestEntity.of(id1, DefaultValueComponent1.of("c1-str1", null));
        DefaultValueComponentTestEntity expectedVer2 = DefaultValueComponentTestEntity.of(id1, DefaultValueComponent1.of(null, DefaultValueComponent2.of("upd-c2-str1", "upd-c2-str2")));
        assert ent2.equals(expectedVer2);
        assert ent1.equals(expectedVer1);
    }

    @Test
    public void testHistoryOfId2() {
        DefaultValueComponentTestEntity ent1 = getAuditReader().find(DefaultValueComponentTestEntity.class, id2, 1);
        DefaultValueComponentTestEntity ent2 = getAuditReader().find(DefaultValueComponentTestEntity.class, id2, 2);
        DefaultValueComponents.log.error("------------ id2 -------------");
        DefaultValueComponents.log.error(ent1.toString());
        DefaultValueComponents.log.error(ent2.toString());
        DefaultValueComponentTestEntity expectedVer1 = DefaultValueComponentTestEntity.of(id2, DefaultValueComponent1.of("c1-str1", DefaultValueComponent2.of("c2-str1", "c2-str2")));
        DefaultValueComponentTestEntity expectedVer2 = DefaultValueComponentTestEntity.of(id2, DefaultValueComponent1.of("c1-str1", DefaultValueComponent2.of("upd-c2-str1", "c2-str2")));
        assert ent1.equals(expectedVer1);
        assert ent2.equals(expectedVer2);
    }

    @Test
    public void testHistoryOfId3() {
        DefaultValueComponentTestEntity ent1 = getAuditReader().find(DefaultValueComponentTestEntity.class, id3, 1);
        DefaultValueComponentTestEntity ent2 = getAuditReader().find(DefaultValueComponentTestEntity.class, id3, 2);
        DefaultValueComponents.log.error("------------ id3 -------------");
        DefaultValueComponents.log.error(ent1.toString());
        DefaultValueComponents.log.error(ent2.toString());
        DefaultValueComponentTestEntity expectedVer1 = DefaultValueComponentTestEntity.of(id3, DefaultValueComponent1.of(null, DefaultValueComponent2.of("c2-str1", "c2-str2")));
        DefaultValueComponentTestEntity expectedVer2 = DefaultValueComponentTestEntity.of(id3, DefaultValueComponent1.of(null, DefaultValueComponent2.of("upd-c2-str1", "c2-str2")));
        assert ent1.equals(expectedVer1);
        assert ent2.equals(expectedVer2);
    }

    @Test
    public void testHistoryOfId4() {
        DefaultValueComponentTestEntity ent1 = getAuditReader().find(DefaultValueComponentTestEntity.class, id4, 1);
        DefaultValueComponentTestEntity ent2 = getAuditReader().find(DefaultValueComponentTestEntity.class, id4, 2);
        DefaultValueComponents.log.error("------------ id4 -------------");
        DefaultValueComponents.log.error(ent1.toString());
        DefaultValueComponents.log.error(ent2.toString());
        DefaultValueComponentTestEntity expectedVer1 = DefaultValueComponentTestEntity.of(id4, DefaultValueComponent1.of(null, DefaultValueComponent2.of(null, "c2-str2")));
        DefaultValueComponentTestEntity expectedVer2 = DefaultValueComponentTestEntity.of(id4, DefaultValueComponent1.of(null, DefaultValueComponent2.of("upd-c2-str1", "c2-str2")));
        assert ent1.equals(expectedVer1);
        assert ent2.equals(expectedVer2);
    }

    @Test
    public void testHistoryOfId5() {
        DefaultValueComponentTestEntity ent1 = getAuditReader().find(DefaultValueComponentTestEntity.class, id5, 1);
        DefaultValueComponentTestEntity ent2 = getAuditReader().find(DefaultValueComponentTestEntity.class, id5, 2);
        DefaultValueComponents.log.error("------------ id5 -------------");
        DefaultValueComponents.log.error(ent1.toString());
        DefaultValueComponents.log.error(ent2.toString());
        DefaultValueComponentTestEntity expectedVer1 = DefaultValueComponentTestEntity.of(id5, DefaultValueComponent1.of(null, DefaultValueComponent2.of("c2-str1", null)));
        DefaultValueComponentTestEntity expectedVer2 = DefaultValueComponentTestEntity.of(id5, DefaultValueComponent1.of(null, DefaultValueComponent2.of("upd-c2-str1", null)));
        assert ent1.equals(expectedVer1);
        assert ent2.equals(expectedVer2);
    }

    @Test
    public void testHistoryOfId6() {
        DefaultValueComponentTestEntity ent1 = getAuditReader().find(DefaultValueComponentTestEntity.class, id6, 1);
        DefaultValueComponentTestEntity ent2 = getAuditReader().find(DefaultValueComponentTestEntity.class, id6, 2);
        DefaultValueComponents.log.error("------------ id6 -------------");
        DefaultValueComponents.log.error(ent1.toString());
        DefaultValueComponents.log.error(ent2.toString());
        DefaultValueComponentTestEntity expectedVer1 = DefaultValueComponentTestEntity.of(id6, DefaultValueComponent1.of(null, null));
        DefaultValueComponentTestEntity expectedVer2 = DefaultValueComponentTestEntity.of(id6, DefaultValueComponent1.of(null, DefaultValueComponent2.of("upd-c2-str1", null)));
        assert ent2.equals(expectedVer2);
        assert ent1.equals(expectedVer1);
    }
}

