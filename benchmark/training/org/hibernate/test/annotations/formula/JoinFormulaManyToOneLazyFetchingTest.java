/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.annotations.formula;


import java.io.Serializable;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import org.hibernate.LazyInitializationException;
import org.hibernate.annotations.JoinColumnOrFormula;
import org.hibernate.annotations.JoinFormula;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Assert;
import org.junit.Test;


@TestForIssue(jiraKey = "HHH-12770")
public class JoinFormulaManyToOneLazyFetchingTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void testLazyLoading() {
        List<JoinFormulaManyToOneLazyFetchingTest.Stock> stocks = TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            return entityManager.createQuery("SELECT s FROM Stock s", .class).getResultList();
        });
        Assert.assertEquals(2, stocks.size());
        try {
            Assert.assertEquals("ABC", stocks.get(0).getCode().getRefNumber());
            Assert.fail("Should have thrown LazyInitializationException");
        } catch (LazyInitializationException expected) {
        }
    }

    @Test
    public void testEagerLoading() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            List<org.hibernate.test.annotations.formula.Stock> stocks = entityManager.createQuery("SELECT s FROM Stock s", .class).getResultList();
            assertEquals(2, stocks.size());
            assertEquals("ABC", stocks.get(0).getCode().getRefNumber());
            try {
                stocks.get(1).getCode().getRefNumber();
                fail("Should have thrown EntityNotFoundException");
            } catch ( expected) {
            }
        });
    }

    @Entity(name = "Stock")
    public static class Stock implements Serializable {
        @Id
        @Column(name = "ID")
        private Long id;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumnOrFormula(column = @JoinColumn(name = "CODE_ID", referencedColumnName = "ID"))
        @JoinColumnOrFormula(formula = @JoinFormula(referencedColumnName = "TYPE", value = "'TYPE_A'"))
        private JoinFormulaManyToOneLazyFetchingTest.StockCode code;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public JoinFormulaManyToOneLazyFetchingTest.StockCode getCode() {
            return code;
        }

        public void setCode(JoinFormulaManyToOneLazyFetchingTest.StockCode code) {
            this.code = code;
        }
    }

    @Entity(name = "StockCode")
    public static class StockCode implements Serializable {
        @Id
        @Column(name = "ID")
        private Long id;

        @Id
        @Enumerated(EnumType.STRING)
        @Column(name = "TYPE")
        private JoinFormulaManyToOneLazyFetchingTest.CodeType copeType;

        private String refNumber;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public JoinFormulaManyToOneLazyFetchingTest.CodeType getCopeType() {
            return copeType;
        }

        public void setCopeType(JoinFormulaManyToOneLazyFetchingTest.CodeType copeType) {
            this.copeType = copeType;
        }

        public String getRefNumber() {
            return refNumber;
        }

        public void setRefNumber(String refNumber) {
            this.refNumber = refNumber;
        }
    }

    public enum CodeType {

        TYPE_A,
        TYPE_B;}
}

