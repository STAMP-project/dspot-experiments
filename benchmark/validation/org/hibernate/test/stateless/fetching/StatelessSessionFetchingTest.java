/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.stateless.fetching;


import java.util.Date;
import java.util.Locale;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.StatelessSession;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
public class StatelessSessionFetchingTest extends BaseCoreFunctionalTestCase {
    private class TestingNamingStrategy extends PhysicalNamingStrategyStandardImpl {
        private final String prefix = determineUniquePrefix();

        protected String applyPrefix(String baseTableName) {
            String prefixed = ((prefix) + '_') + baseTableName;
            log.debug(((("prefixed table name : " + baseTableName) + " -> ") + prefixed));
            return prefixed;
        }

        @Override
        public Identifier toPhysicalTableName(Identifier name, JdbcEnvironment jdbcEnvironment) {
            return jdbcEnvironment.getIdentifierHelper().toIdentifier(applyPrefix(name.getText()));
        }

        private String determineUniquePrefix() {
            return StringHelper.collapseQualifier(getClass().getName(), false).toUpperCase(Locale.ROOT);
        }
    }

    @Test
    public void testDynamicFetch() {
        Session s = openSession();
        s.beginTransaction();
        Date now = new Date();
        User me = new User("me");
        User you = new User("you");
        Resource yourClock = new Resource("clock", you);
        Task task = new Task(me, "clean", yourClock, now);// :)

        s.save(me);
        s.save(you);
        s.save(yourClock);
        s.save(task);
        s.getTransaction().commit();
        s.close();
        StatelessSession ss = sessionFactory().openStatelessSession();
        ss.beginTransaction();
        Task taskRef = ((Task) (ss.createQuery("from Task t join fetch t.resource join fetch t.user").uniqueResult()));
        Assert.assertTrue((taskRef != null));
        Assert.assertTrue(Hibernate.isInitialized(taskRef));
        Assert.assertTrue(Hibernate.isInitialized(taskRef.getUser()));
        Assert.assertTrue(Hibernate.isInitialized(taskRef.getResource()));
        Assert.assertFalse(Hibernate.isInitialized(taskRef.getResource().getOwner()));
        ss.getTransaction().commit();
        ss.close();
        cleanup();
    }
}

