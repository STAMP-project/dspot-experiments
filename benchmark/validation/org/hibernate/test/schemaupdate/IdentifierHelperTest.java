/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.schemaupdate;


import AvailableSettings.KEYWORD_AUTO_QUOTING_ENABLED;
import java.util.Collections;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.testing.boot.ServiceRegistryTestingImpl;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
public class IdentifierHelperTest extends BaseUnitTestCase {
    @Test
    public void testAutoQuotingDisabled() {
        ServiceRegistry sr = ServiceRegistryTestingImpl.forUnitTesting(// true is the default, but to be sure...
        Collections.singletonMap(KEYWORD_AUTO_QUOTING_ENABLED, true));
        Identifier identifier = sr.getService(JdbcEnvironment.class).getIdentifierHelper().toIdentifier("select");
        Assert.assertTrue(identifier.isQuoted());
        StandardServiceRegistryBuilder.destroy(sr);
        sr = ServiceRegistryTestingImpl.forUnitTesting(Collections.singletonMap(KEYWORD_AUTO_QUOTING_ENABLED, false));
        identifier = sr.getService(JdbcEnvironment.class).getIdentifierHelper().toIdentifier("select");
        Assert.assertFalse(identifier.isQuoted());
        StandardServiceRegistryBuilder.destroy(sr);
    }
}

