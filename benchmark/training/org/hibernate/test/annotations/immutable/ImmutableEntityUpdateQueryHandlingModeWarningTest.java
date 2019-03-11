/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.annotations.immutable;


import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.internal.SessionImpl;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
import org.hibernate.testing.logger.LoggerInspectionRule;
import org.hibernate.testing.logger.Triggerable;
import org.hibernate.testing.transaction.TransactionUtil;
import org.jboss.logging.Logger;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
@TestForIssue(jiraKey = "HHH-12387")
public class ImmutableEntityUpdateQueryHandlingModeWarningTest extends BaseNonConfigCoreFunctionalTestCase {
    @Rule
    public LoggerInspectionRule logInspection = new LoggerInspectionRule(Logger.getMessageLogger(CoreMessageLogger.class, SessionImpl.class.getName()));

    @Test
    public void testBulkUpdate() {
        Country _country = TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            Country country = new Country();
            country.setName("Germany");
            session.persist(country);
            return country;
        });
        Triggerable triggerable = logInspection.watchForLogMessages("HHH000487");
        triggerable.reset();
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            session.createQuery(("update Country " + "set name = :name")).setParameter("name", "N/A").executeUpdate();
        });
        Assert.assertEquals("HHH000487: The query: [update Country set name = :name] attempts to update an immutable entity: [Country]", triggerable.triggerMessage());
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            Country country = session.find(.class, _country.getId());
            assertEquals("N/A", country.getName());
        });
    }
}

