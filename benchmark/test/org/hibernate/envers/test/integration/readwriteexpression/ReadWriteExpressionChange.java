/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.envers.test.integration.readwriteexpression;


import java.math.BigDecimal;
import java.util.List;
import javax.persistence.EntityManager;
import org.hibernate.dialect.Oracle8iDialect;
import org.hibernate.envers.test.BaseEnversJPAFunctionalTestCase;
import org.hibernate.envers.test.Priority;
import org.junit.Assert;
import org.junit.Test;


public class ReadWriteExpressionChange extends BaseEnversJPAFunctionalTestCase {
    private static final Double HEIGHT_INCHES = 73.0;

    private static final Double HEIGHT_CENTIMETERS = (ReadWriteExpressionChange.HEIGHT_INCHES) * 2.54;

    private Integer id;

    @Test
    @Priority(10)
    public void initData() {
        EntityManager em = getEntityManager();
        em.getTransaction().begin();
        Staff staff = new Staff(ReadWriteExpressionChange.HEIGHT_INCHES, 1);
        em.persist(staff);
        em.getTransaction().commit();
        id = staff.getId();
    }

    @Test
    public void shouldRespectWriteExpression() {
        EntityManager em = getEntityManager();
        em.getTransaction().begin();
        List resultList = em.createNativeQuery(("select size_in_cm from t_staff_AUD where id =" + (id))).getResultList();
        Assert.assertEquals(1, resultList.size());
        Double sizeInCm = null;
        if ((getDialect()) instanceof Oracle8iDialect) {
            sizeInCm = ((BigDecimal) (resultList.get(0))).doubleValue();
        } else {
            sizeInCm = ((Double) (resultList.get(0)));
        }
        em.getTransaction().commit();
        Assert.assertEquals(ReadWriteExpressionChange.HEIGHT_CENTIMETERS, sizeInCm.doubleValue(), 1.0E-8);
    }

    @Test
    public void shouldRespectReadExpression() {
        List<Number> revisions = getAuditReader().getRevisions(Staff.class, id);
        Assert.assertEquals(1, revisions.size());
        Number number = revisions.get(0);
        Staff staffRev = getAuditReader().find(Staff.class, id, number);
        Assert.assertEquals(ReadWriteExpressionChange.HEIGHT_INCHES, staffRev.getSizeInInches(), 1.0E-8);
    }
}

