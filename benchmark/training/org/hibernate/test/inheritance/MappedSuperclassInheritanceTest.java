/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.inheritance;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.MappedSuperclass;
import org.hibernate.cfg.AnnotationBinder;
import org.hibernate.hql.internal.ast.QuerySyntaxException;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.logger.LoggerInspectionRule;
import org.hibernate.testing.transaction.TransactionUtil;
import org.hibernate.testing.util.ExceptionUtil;
import org.jboss.logging.Logger;
import org.junit.Rule;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
@TestForIssue(jiraKey = "HHH-12653")
public class MappedSuperclassInheritanceTest extends BaseEntityManagerFunctionalTestCase {
    @Rule
    public LoggerInspectionRule logInspection = new LoggerInspectionRule(Logger.getMessageLogger(CoreMessageLogger.class, AnnotationBinder.class.getName()));

    @Test
    public void test() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            entityManager.createQuery("from Manager").getResultList();
            entityManager.createQuery("from Developer").getResultList();
            try {
                // Check the @Inheritance annotation was ignored
                entityManager.createQuery("from Employee").getResultList();
                fail();
            } catch ( expected) {
                QuerySyntaxException rootException = ((QuerySyntaxException) (ExceptionUtil.rootCause(expected)));
                assertEquals("Employee is not mapped", rootException.getMessage());
            }
        });
    }

    @Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
    @MappedSuperclass
    public static class Employee {
        @Id
        @GeneratedValue
        private Long id;

        private String jobType;

        private String firstName;

        private String lastName;
    }

    @Entity(name = "Manager")
    public static class Manager extends MappedSuperclassInheritanceTest.Employee {}

    @Entity(name = "Developer")
    public static class Developer extends MappedSuperclassInheritanceTest.Employee {}
}

