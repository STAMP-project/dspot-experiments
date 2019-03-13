/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.quote;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.HibernateException;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.test.util.DdlTransactionIsolatorTestingImpl;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.hibernate.tool.hbm2ddl.SchemaValidator;
import org.hibernate.tool.schema.internal.exec.GenerationTarget;
import org.hibernate.tool.schema.internal.exec.GenerationTargetToDatabase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
public class TableGeneratorQuotingTest extends BaseUnitTestCase {
    private StandardServiceRegistry serviceRegistry;

    @Test
    @TestForIssue(jiraKey = "HHH-7927")
    public void testTableGeneratorQuoting() {
        final Metadata metadata = addAnnotatedClass(TableGeneratorQuotingTest.TestEntity.class).buildMetadata();
        final ConnectionProvider connectionProvider = serviceRegistry.getService(ConnectionProvider.class);
        final GenerationTarget target = new GenerationTargetToDatabase(new DdlTransactionIsolatorTestingImpl(serviceRegistry, new org.hibernate.engine.jdbc.env.internal.JdbcEnvironmentInitiator.ConnectionProviderJdbcConnectionAccess(connectionProvider)));
        new org.hibernate.tool.schema.internal.SchemaCreatorImpl(serviceRegistry).doCreation(metadata, false, target);
        try {
            new SchemaValidator().validate(metadata);
        } catch (HibernateException e) {
            Assert.fail(("The identifier generator table should have validated.  " + (e.getMessage())));
        } finally {
            new org.hibernate.tool.schema.internal.SchemaDropperImpl(serviceRegistry).doDrop(metadata, false, target);
        }
    }

    @Entity
    @Table(name = "test_entity")
    private static class TestEntity {
        @Id
        @GeneratedValue(strategy = GenerationType.TABLE)
        private int id;
    }
}

