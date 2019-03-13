/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.enums;


import org.hibernate.criterion.Restrictions;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.testing.logger.LoggerInspectionRule;
import org.hibernate.testing.logger.Triggerable;
import org.hibernate.testing.transaction.TransactionUtil;
import org.hibernate.type.descriptor.sql.BasicBinder;
import org.hibernate.type.descriptor.sql.BasicExtractor;
import org.jboss.logging.Logger;
import org.junit.Rule;
import org.junit.Test;


/**
 *
 *
 * @author Brett Meyer
 */
public class EnumTypeTest extends BaseCoreFunctionalTestCase {
    @Rule
    public LoggerInspectionRule binderLogInspection = new LoggerInspectionRule(Logger.getMessageLogger(CoreMessageLogger.class, BasicBinder.class.getName()));

    @Rule
    public LoggerInspectionRule extractorLogInspection = new LoggerInspectionRule(Logger.getMessageLogger(CoreMessageLogger.class, BasicExtractor.class.getName()));

    private Person person;

    private Triggerable binderTriggerable;

    private Triggerable extractorTriggerable;

    @Test
    @TestForIssue(jiraKey = "HHH-8153")
    public void hbmEnumTypeTest() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( s) -> {
            assertEquals(s.createCriteria(.class).add(Restrictions.eq("gender", Gender.MALE)).list().size(), 2);
            assertEquals(s.createCriteria(.class).add(Restrictions.eq("gender", Gender.MALE)).add(Restrictions.eq("hairColor", HairColor.BROWN)).list().size(), 1);
            assertEquals(s.createCriteria(.class).add(Restrictions.eq("gender", Gender.FEMALE)).list().size(), 2);
            assertEquals(s.createCriteria(.class).add(Restrictions.eq("gender", Gender.FEMALE)).add(Restrictions.eq("hairColor", HairColor.BROWN)).list().size(), 1);
        });
    }

    @Test
    @TestForIssue(jiraKey = "HHH-12978")
    public void testEnumAsBindParameterAndExtract() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( s) -> {
            binderTriggerable.reset();
            extractorTriggerable.reset();
            s.createQuery("select p.id from Person p where p.id = :id", .class).setParameter("id", person.getId()).getSingleResult();
            assertTrue(binderTriggerable.wasTriggered());
            assertTrue(extractorTriggerable.wasTriggered());
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( s) -> {
            binderTriggerable.reset();
            extractorTriggerable.reset();
            s.createQuery("select p.gender from Person p where p.gender = :gender and p.hairColor = :hairColor", .class).setParameter("gender", Gender.MALE).setParameter("hairColor", HairColor.BROWN).getSingleResult();
            assertTrue(binderTriggerable.wasTriggered());
            assertTrue(extractorTriggerable.wasTriggered());
        });
    }
}

