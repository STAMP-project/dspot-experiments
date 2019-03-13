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
import javax.persistence.OneToOne;
import org.hibernate.annotations.JoinColumnOrFormula;
import org.hibernate.annotations.JoinFormula;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.cfg.AnnotationBinder;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.logger.LoggerInspectionRule;
import org.hibernate.testing.logger.Triggerable;
import org.hibernate.testing.transaction.TransactionUtil;
import org.jboss.logging.Logger;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


@TestForIssue(jiraKey = "HHH-12770")
public class JoinFormulaOneToOneNotIgnoreLazyFetchingTest extends BaseEntityManagerFunctionalTestCase {
    @Rule
    public LoggerInspectionRule logInspection = new LoggerInspectionRule(Logger.getMessageLogger(CoreMessageLogger.class, AnnotationBinder.class.getName()));

    private Triggerable triggerable = logInspection.watchForLogMessages("HHH000491");

    @Test
    public void testLazyLoading() {
        Assert.assertEquals("HHH000491: The [code] association in the [org.hibernate.test.annotations.formula.JoinFormulaOneToOneNotIgnoreLazyFetchingTest$Stock] entity uses both @NotFound(action = NotFoundAction.IGNORE) and FetchType.LAZY. The NotFoundAction.IGNORE @ManyToOne and @OneToOne associations are always fetched eagerly.", triggerable.triggerMessage());
        List<JoinFormulaOneToOneNotIgnoreLazyFetchingTest.Stock> stocks = TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            return entityManager.createQuery("SELECT s FROM Stock s", .class).getResultList();
        });
        Assert.assertEquals(2, stocks.size());
        Assert.assertEquals("ABC", stocks.get(0).getCode().getRefNumber());
        Assert.assertNull(stocks.get(1).getCode());
    }

    @Entity(name = "Stock")
    public static class Stock implements Serializable {
        @Id
        @Column(name = "ID")
        private Long id;

        @OneToOne(fetch = FetchType.LAZY)
        @NotFound(action = NotFoundAction.IGNORE)
        @JoinColumnOrFormula(column = @JoinColumn(name = "CODE_ID", referencedColumnName = "ID"))
        @JoinColumnOrFormula(formula = @JoinFormula(referencedColumnName = "TYPE", value = "'TYPE_A'"))
        private JoinFormulaOneToOneNotIgnoreLazyFetchingTest.StockCode code;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public JoinFormulaOneToOneNotIgnoreLazyFetchingTest.StockCode getCode() {
            return code;
        }

        public void setCode(JoinFormulaOneToOneNotIgnoreLazyFetchingTest.StockCode code) {
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
        private JoinFormulaOneToOneNotIgnoreLazyFetchingTest.CodeType copeType;

        private String refNumber;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public JoinFormulaOneToOneNotIgnoreLazyFetchingTest.CodeType getCopeType() {
            return copeType;
        }

        public void setCopeType(JoinFormulaOneToOneNotIgnoreLazyFetchingTest.CodeType copeType) {
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

