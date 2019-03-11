/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.loadplans.process;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.Id;
import org.hibernate.Session;
import org.hibernate.engine.jdbc.spi.JdbcServices;
import org.hibernate.engine.spi.QueryParameters;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.jdbc.Work;
import org.hibernate.loader.plan.exec.process.spi.ResultSetProcessor;
import org.hibernate.loader.plan.exec.spi.LoadQueryDetails;
import org.hibernate.loader.plan.spi.LoadPlan;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.testing.junit4.ExtraAssertions;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
public class SimpleResultSetProcessorTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testSimpleEntityProcessing() throws Exception {
        final EntityPersister entityPersister = sessionFactory().getEntityPersister(SimpleResultSetProcessorTest.SimpleEntity.class.getName());
        // create some test data
        Session session = openSession();
        session.beginTransaction();
        session.save(new SimpleResultSetProcessorTest.SimpleEntity(1, "the only"));
        session.getTransaction().commit();
        session.close();
        {
            final LoadPlan plan = Helper.INSTANCE.buildLoadPlan(sessionFactory(), entityPersister);
            final LoadQueryDetails queryDetails = Helper.INSTANCE.buildLoadQueryDetails(plan, sessionFactory());
            final String sql = queryDetails.getSqlStatement();
            final ResultSetProcessor resultSetProcessor = queryDetails.getResultSetProcessor();
            final List results = new ArrayList();
            final Session workSession = openSession();
            workSession.beginTransaction();
            workSession.doWork(new Work() {
                @Override
                public void execute(Connection connection) throws SQLException {
                    getFactory().getServiceRegistry().getService(JdbcServices.class).getSqlStatementLogger().logStatement(sql);
                    PreparedStatement ps = connection.prepareStatement(sql);
                    ps.setInt(1, 1);
                    ResultSet resultSet = ps.executeQuery();
                    results.addAll(resultSetProcessor.extractResults(resultSet, ((SessionImplementor) (workSession)), new QueryParameters(), Helper.parameterContext(), true, false, null, null));
                    resultSet.close();
                    ps.close();
                }
            });
            Assert.assertEquals(1, results.size());
            Object result = results.get(0);
            Assert.assertNotNull(result);
            SimpleResultSetProcessorTest.SimpleEntity workEntity = ExtraAssertions.assertTyping(SimpleResultSetProcessorTest.SimpleEntity.class, result);
            Assert.assertEquals(1, workEntity.id.intValue());
            Assert.assertEquals("the only", workEntity.name);
            workSession.getTransaction().commit();
            workSession.close();
        }
        // clean up test data
        session = openSession();
        session.beginTransaction();
        session.createQuery("delete SimpleEntity").executeUpdate();
        session.getTransaction().commit();
        session.close();
    }

    @Entity(name = "SimpleEntity")
    public static class SimpleEntity {
        @Id
        public Integer id;

        public String name;

        public SimpleEntity() {
        }

        public SimpleEntity(Integer id, String name) {
            this.id = id;
            this.name = name;
        }
    }
}

