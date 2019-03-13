/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.annotations.formula;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import org.hibernate.LazyInitializationException;
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
public class JoinFormulaOneToManyNotIgnoreLazyFetchingTest extends BaseEntityManagerFunctionalTestCase {
    @Rule
    public LoggerInspectionRule logInspection = new LoggerInspectionRule(Logger.getMessageLogger(CoreMessageLogger.class, AnnotationBinder.class.getName()));

    private Triggerable triggerable = logInspection.watchForLogMessages("HHH000491");

    @Test
    public void testLazyLoading() {
        Assert.assertFalse(triggerable.wasTriggered());
        List<JoinFormulaOneToManyNotIgnoreLazyFetchingTest.Stock> stocks = TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            return entityManager.createQuery("SELECT s FROM Stock s", .class).getResultList();
        });
        Assert.assertEquals(2, stocks.size());
        try {
            Assert.assertEquals("ABC", stocks.get(0).getCodes().get(0).getRefNumber());
            Assert.fail("Should have thrown LazyInitializationException");
        } catch (LazyInitializationException expected) {
        }
    }

    @Entity(name = "Stock")
    public static class Stock implements Serializable {
        @Id
        @Column(name = "ID")
        private Long id;

        @OneToMany
        @NotFound(action = NotFoundAction.IGNORE)
        @JoinColumn(name = "CODE_ID", referencedColumnName = "ID")
        private List<JoinFormulaOneToManyNotIgnoreLazyFetchingTest.StockCode> codes = new ArrayList<>();

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public List<JoinFormulaOneToManyNotIgnoreLazyFetchingTest.StockCode> getCodes() {
            return codes;
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
        private JoinFormulaOneToManyNotIgnoreLazyFetchingTest.CodeType copeType;

        private String refNumber;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public JoinFormulaOneToManyNotIgnoreLazyFetchingTest.CodeType getCopeType() {
            return copeType;
        }

        public void setCopeType(JoinFormulaOneToManyNotIgnoreLazyFetchingTest.CodeType copeType) {
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

