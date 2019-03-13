/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.annotations.formula;


import DialectChecks.SupportPartitionBy;
import java.io.Serializable;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.annotations.Formula;
import org.hibernate.testing.RequiresDialectFeature;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
@RequiresDialectFeature(jiraKey = "HHH-10754", value = SupportPartitionBy.class)
public class FormulaWithPartitionByTest extends BaseCoreFunctionalTestCase {
    @Test
    @TestForIssue(jiraKey = "HHH-10754")
    public void testFormulaAnnotationWithPartitionBy() {
        Session session = openSession();
        Transaction transaction = session.beginTransaction();
        FormulaWithPartitionByTest.DisplayItem displayItem20_1 = new FormulaWithPartitionByTest.DisplayItem();
        displayItem20_1.setId(1);
        displayItem20_1.setDiscountCode("20");
        displayItem20_1.setDiscountValue(12.34);
        FormulaWithPartitionByTest.DisplayItem displayItem20_2 = new FormulaWithPartitionByTest.DisplayItem();
        displayItem20_2.setId(2);
        displayItem20_2.setDiscountCode("20");
        displayItem20_2.setDiscountValue(15.89);
        FormulaWithPartitionByTest.DisplayItem displayItem100 = new FormulaWithPartitionByTest.DisplayItem();
        displayItem100.setId(3);
        displayItem100.setDiscountCode("100");
        displayItem100.setDiscountValue(12.5);
        session.persist(displayItem20_1);
        session.persist(displayItem20_2);
        session.persist(displayItem100);
        transaction.commit();
        session.close();
        session = openSession();
        transaction = session.beginTransaction();
        List<FormulaWithPartitionByTest.DisplayItem> displayItems = session.createQuery("select di from DisplayItem di order by di.id", FormulaWithPartitionByTest.DisplayItem.class).getResultList();
        Assert.assertNotNull(displayItems);
        Assert.assertEquals(displayItems.size(), 3);
        Assert.assertEquals(1, displayItems.get(0).getItemsByCode().intValue());
        Assert.assertEquals(2, displayItems.get(1).getItemsByCode().intValue());
        Assert.assertEquals(1, displayItems.get(2).getItemsByCode().intValue());
        transaction.commit();
        session.close();
    }

    @Entity(name = "DisplayItem")
    public static class DisplayItem implements Serializable {
        @Id
        private Integer id;

        @Column(name = "DISCOUNT_CODE")
        private String discountCode;

        @Column(name = "DISCOUNT_VALUE")
        private Double discountValue;

        @Formula("ROW_NUMBER() OVER( PARTITION BY DISCOUNT_CODE ORDER BY SIGN(DISCOUNT_VALUE) DESC )")
        private Integer itemsByCode;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getDiscountCode() {
            return discountCode;
        }

        public void setDiscountCode(String discountCode) {
            this.discountCode = discountCode;
        }

        public Integer getItemsByCode() {
            return itemsByCode;
        }

        public void setItemsByCode(Integer itemsByCode) {
            this.itemsByCode = itemsByCode;
        }

        public Double getDiscountValue() {
            return discountValue;
        }

        public void setDiscountValue(Double discountValue) {
            this.discountValue = discountValue;
        }
    }
}

