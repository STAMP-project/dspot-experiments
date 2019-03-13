/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.tool.schema;


import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.engine.jdbc.connections.internal.DriverManagerConnectionProviderImpl;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.service.spi.ServiceRegistryImplementor;
import org.hibernate.test.util.DdlTransactionIsolatorTestingImpl;
import org.hibernate.testing.RequiresDialect;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.hibernate.testing.logger.LoggerInspectionRule;
import org.hibernate.tool.schema.internal.DefaultSchemaFilter;
import org.hibernate.tool.schema.internal.HibernateSchemaManagementTool;
import org.hibernate.tool.schema.internal.exec.GenerationTargetToDatabase;
import org.hibernate.tool.schema.spi.ExecutionOptions;
import org.hibernate.tool.schema.spi.SchemaValidator;
import org.jboss.logging.Logger;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
@TestForIssue(jiraKey = "HHH-11864")
@RequiresDialect(H2Dialect.class)
public class IndividuallySchemaValidatorImplConnectionTest extends BaseUnitTestCase {
    @Rule
    public LoggerInspectionRule logInspection = new LoggerInspectionRule(Logger.getMessageLogger(CoreMessageLogger.class, IndividuallySchemaValidatorImplConnectionTest.class.getName()));

    private StandardServiceRegistry ssr;

    protected HibernateSchemaManagementTool tool;

    private Map configurationValues;

    private ExecutionOptions executionOptions;

    private DriverManagerConnectionProviderImpl connectionProvider;

    private Connection connection;

    @Test
    public void testMissingEntityContainsUnqualifiedEntityName() throws Exception {
        MetadataSources metadataSources = new MetadataSources(ssr);
        metadataSources.addAnnotatedClass(IndividuallySchemaValidatorImplConnectionTest.UnqualifiedMissingEntity.class);
        MetadataImplementor metadata = ((MetadataImplementor) (metadataSources.buildMetadata()));
        metadata.validate();
        Map<String, Object> settings = new HashMap<>();
        ServiceRegistryImplementor serviceRegistry = ((ServiceRegistryImplementor) (new StandardServiceRegistryBuilder().applySettings(settings).build()));
        DriverManagerConnectionProviderImpl connectionProvider = new DriverManagerConnectionProviderImpl();
        connectionProvider.configure(properties());
        final GenerationTargetToDatabase schemaGenerator = new GenerationTargetToDatabase(new DdlTransactionIsolatorTestingImpl(serviceRegistry, new org.hibernate.testing.boot.JdbcConnectionAccessImpl(connectionProvider)));
        try {
            new org.hibernate.tool.schema.internal.SchemaCreatorImpl(ssr).doCreation(metadata, serviceRegistry, settings, true, schemaGenerator);
            metadataSources = new MetadataSources(ssr);
            metadataSources.addAnnotatedClass(IndividuallySchemaValidatorImplConnectionTest.UnqualifiedMissingEntity.class);
            metadata = ((MetadataImplementor) (metadataSources.buildMetadata()));
            metadata.validate();
            SchemaValidator schemaValidator = new org.hibernate.tool.schema.internal.IndividuallySchemaValidatorImpl(tool, DefaultSchemaFilter.INSTANCE);
            Assert.assertFalse(connection.getAutoCommit());
            schemaValidator.doValidation(metadata, executionOptions);
            Assert.assertFalse(connection.getAutoCommit());
        } finally {
            new org.hibernate.tool.schema.internal.SchemaDropperImpl(serviceRegistry).doDrop(metadata, false, schemaGenerator);
            serviceRegistry.destroy();
            connectionProvider.stop();
        }
    }

    @Entity
    @Table(name = "UnqualifiedMissingEntity")
    public static class UnqualifiedMissingEntity {
        @Id
        private String id;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }
}

