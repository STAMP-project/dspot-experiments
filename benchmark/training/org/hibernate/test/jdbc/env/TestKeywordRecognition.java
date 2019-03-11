/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.jdbc.env;


import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
public class TestKeywordRecognition extends BaseUnitTestCase {
    private StandardServiceRegistry serviceRegistry;

    @Test
    @TestForIssue(jiraKey = "HHH_9768")
    public void testAnsiSqlKeyword() {
        // END is ANSI SQL keyword
        JdbcEnvironment jdbcEnvironment = serviceRegistry.getService(JdbcEnvironment.class);
        Assert.assertTrue(jdbcEnvironment.getIdentifierHelper().isReservedWord("end"));
        Assert.assertTrue(jdbcEnvironment.getIdentifierHelper().isReservedWord("END"));
        Identifier identifier = jdbcEnvironment.getIdentifierHelper().toIdentifier("end");
        Assert.assertTrue(identifier.isQuoted());
    }
}

