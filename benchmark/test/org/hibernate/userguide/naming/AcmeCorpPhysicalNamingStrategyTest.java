/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.userguide.naming;


import org.hamcrest.CoreMatchers;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
public class AcmeCorpPhysicalNamingStrategyTest {
    private AcmeCorpPhysicalNamingStrategy strategy = new AcmeCorpPhysicalNamingStrategy();

    private StandardServiceRegistry serviceRegistry;

    @Test
    public void testTableNaming() {
        {
            Identifier in = Identifier.toIdentifier("accountNumber");
            Identifier out = strategy.toPhysicalTableName(in, serviceRegistry.getService(JdbcEnvironment.class));
            Assert.assertThat(out.getText(), CoreMatchers.equalTo("acct_num"));
        }
    }
}

