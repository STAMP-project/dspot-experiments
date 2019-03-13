/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.multitenancy.schema;


import javax.sql.DataSource;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsNull;
import org.hibernate.engine.jdbc.connections.internal.DatasourceConnectionProviderImpl;
import org.hibernate.engine.jdbc.connections.spi.AbstractDataSourceBasedMultiTenantConnectionProviderImpl;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.hibernate.testing.RequiresDialectFeature;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.env.ConnectionProviderBuilder;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
@RequiresDialectFeature(ConnectionProviderBuilder.class)
public class SchemaBasedDataSourceMultiTenancyTest extends AbstractSchemaBasedMultiTenancyTest<AbstractDataSourceBasedMultiTenantConnectionProviderImpl, DatasourceConnectionProviderImpl> {
    @Test
    @TestForIssue(jiraKey = "HHH-11651")
    public void testUnwrappingConnectionProvider() {
        final MultiTenantConnectionProvider multiTenantConnectionProvider = serviceRegistry.getService(MultiTenantConnectionProvider.class);
        final DataSource dataSource = multiTenantConnectionProvider.unwrap(DataSource.class);
        Assert.assertThat(dataSource, Is.is(IsNull.notNullValue()));
    }

    @Test
    @TestForIssue(jiraKey = "HHH-11651")
    public void testUnwrappingAbstractMultiTenantConnectionProvider() {
        final MultiTenantConnectionProvider multiTenantConnectionProvider = serviceRegistry.getService(MultiTenantConnectionProvider.class);
        final AbstractDataSourceBasedMultiTenantConnectionProviderImpl dataSourceBasedMultiTenantConnectionProvider = multiTenantConnectionProvider.unwrap(AbstractDataSourceBasedMultiTenantConnectionProviderImpl.class);
        Assert.assertThat(dataSourceBasedMultiTenantConnectionProvider, Is.is(IsNull.notNullValue()));
    }

    @Test
    @TestForIssue(jiraKey = "HHH-11651")
    public void testUnwrappingMultiTenantConnectionProvider() {
        final MultiTenantConnectionProvider multiTenantConnectionProvider = serviceRegistry.getService(MultiTenantConnectionProvider.class);
        final MultiTenantConnectionProvider connectionProvider = multiTenantConnectionProvider.unwrap(MultiTenantConnectionProvider.class);
        Assert.assertThat(connectionProvider, Is.is(IsNull.notNullValue()));
    }
}

