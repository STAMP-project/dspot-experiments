/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.dialect.functional;


import java.util.List;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.dialect.AbstractHANADialect;
import org.hibernate.query.Query;
import org.hibernate.testing.RequiresDialect;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


@RequiresDialect({ AbstractHANADialect.class })
public class HANACalcViewTest extends BaseCoreFunctionalTestCase {
    private static final String CALC_VIEW_NAME = "DUMMY_CV_VIEW";

    private static final String CALC_SCENARIO_NAME = "DUMMY_CV_SCEN";

    private static final String PROJECTION_NODE_NAME = "DUMMY_PROJ";

    @Test
    @TestForIssue(jiraKey = "HHH-12541")
    public void testCalcViewEntity() throws Exception {
        Session s = openSession();
        HANACalcViewTest.CVEntity cvEntity = s.find(HANACalcViewTest.CVEntity.class, "X");
        Assert.assertEquals("X", cvEntity.getDummy());
        Assert.assertEquals("XX", cvEntity.getDummydummy());
        Assert.assertEquals(2, cvEntity.getDummyint());
        Assert.assertEquals(1.5, cvEntity.getDummydouble(), 0.1);
    }

    @Test
    @TestForIssue(jiraKey = "HHH-12541")
    public void testCalcViewEntityQuery() throws Exception {
        Session s = openSession();
        Query<HANACalcViewTest.CVEntity> query = s.createQuery("select e from HANACalcViewTest$CVEntity e", HANACalcViewTest.CVEntity.class);
        List<HANACalcViewTest.CVEntity> list = query.list();
        Assert.assertEquals(1, list.size());
        HANACalcViewTest.CVEntity cvEntity = list.get(0);
        Assert.assertEquals("X", cvEntity.getDummy());
        Assert.assertEquals("XX", cvEntity.getDummydummy());
        Assert.assertEquals(2, cvEntity.getDummyint());
        Assert.assertEquals(1.5, cvEntity.getDummydouble(), 0.1);
    }

    @Test
    @TestForIssue(jiraKey = "HHH-12541")
    public void testCalcViewEntityCriteriaQuery() throws Exception {
        Session s = openSession();
        CriteriaBuilder criteriaBuilder = s.getCriteriaBuilder();
        CriteriaQuery<HANACalcViewTest.CVEntity> criteriaQuery = criteriaBuilder.createQuery(HANACalcViewTest.CVEntity.class);
        Root<HANACalcViewTest.CVEntity> from = criteriaQuery.from(HANACalcViewTest.CVEntity.class);
        criteriaQuery.where(criteriaBuilder.equal(from.get("dummydummy"), "XX"));
        Query<HANACalcViewTest.CVEntity> query = this.session.createQuery(criteriaQuery);
        List<HANACalcViewTest.CVEntity> list = query.list();
        Assert.assertEquals(1, list.size());
        HANACalcViewTest.CVEntity cvEntity = list.get(0);
        Assert.assertEquals("X", cvEntity.getDummy());
        Assert.assertEquals("XX", cvEntity.getDummydummy());
        Assert.assertEquals(2, cvEntity.getDummyint());
        Assert.assertEquals(1.5, cvEntity.getDummydouble(), 0.1);
    }

    @Test
    @TestForIssue(jiraKey = "HHH-12541")
    public void testCalcViewDTO() throws Exception {
        Session s = openSession();
        Query<HANACalcViewTest.CVEntityDTO> query = s.createQuery("select new org.hibernate.test.dialect.functional.HANACalcViewTest$CVEntityDTO(e.dummyint, e.dummy) from HANACalcViewTest$CVEntity e", HANACalcViewTest.CVEntityDTO.class);
        List<HANACalcViewTest.CVEntityDTO> list = query.list();
        Assert.assertEquals(1, list.size());
        HANACalcViewTest.CVEntityDTO cvEntity = list.get(0);
        Assert.assertEquals("X", cvEntity.getDummy());
        Assert.assertEquals(2, cvEntity.getDummyint());
    }

    @Entity
    @Table(name = HANACalcViewTest.CALC_VIEW_NAME)
    private static class CVEntity {
        private String dummydummy;

        private double dummydouble;

        private int dummyint;

        @Id
        private String dummy;

        public String getDummydummy() {
            return this.dummydummy;
        }

        public double getDummydouble() {
            return this.dummydouble;
        }

        public int getDummyint() {
            return this.dummyint;
        }

        public String getDummy() {
            return this.dummy;
        }
    }

    private static class CVEntityDTO {
        private int dummyint;

        private String dummy;

        @SuppressWarnings("unused")
        public CVEntityDTO(int dummyint, String dummy) {
            this.dummyint = dummyint;
            this.dummy = dummy;
        }

        public int getDummyint() {
            return this.dummyint;
        }

        public String getDummy() {
            return this.dummy;
        }
    }
}

