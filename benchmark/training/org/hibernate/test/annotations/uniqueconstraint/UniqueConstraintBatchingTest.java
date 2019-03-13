/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.annotations.uniqueconstraint;


import javax.persistence.PersistenceException;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.RequiresDialect;
import org.hibernate.testing.TestForIssue;
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
@TestForIssue(jiraKey = "HHH-12688")
@RequiresDialect(H2Dialect.class)
public class UniqueConstraintBatchingTest extends BaseEntityManagerFunctionalTestCase {
    @Rule
    public LoggerInspectionRule logInspection = new LoggerInspectionRule(Logger.getMessageLogger(CoreMessageLogger.class, SqlExceptionHelper.class.getName()));

    private Triggerable triggerable;

    @Test
    public void testBatching() throws Exception {
        Room livingRoom = new Room();
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            livingRoom.setId(1L);
            livingRoom.setName("livingRoom");
            entityManager.persist(livingRoom);
        });
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            House house = new House();
            house.setId(1L);
            house.setCost(100);
            house.setHeight(1000L);
            house.setRoom(livingRoom);
            entityManager.persist(house);
        });
        try {
            TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
                House house2 = new House();
                house2.setId(2L);
                house2.setCost(100);
                house2.setHeight(1001L);
                house2.setRoom(livingRoom);
                entityManager.persist(house2);
            });
            Assert.fail("Should throw exception");
        } catch (PersistenceException e) {
            Assert.assertEquals(1, triggerable.triggerMessages().size());
            Assert.assertTrue(triggerable.triggerMessage().startsWith("Unique index or primary key violation"));
        }
    }
}

