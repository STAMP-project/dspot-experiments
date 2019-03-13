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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import org.hibernate.Hibernate;
import org.hibernate.Session;
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
 * @author Gail Badner
 */
public class EntityWithNonLazyCollectionResultSetProcessorTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testEntityWithSet() throws Exception {
        final EntityPersister entityPersister = sessionFactory().getEntityPersister(EntityWithNonLazyCollectionResultSetProcessorTest.Person.class.getName());
        // create some test data
        Session session = openSession();
        session.beginTransaction();
        EntityWithNonLazyCollectionResultSetProcessorTest.Person person = new EntityWithNonLazyCollectionResultSetProcessorTest.Person();
        person.id = 1;
        person.name = "John Doe";
        person.nickNames.add("Jack");
        person.nickNames.add("Johnny");
        session.save(person);
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
                    PreparedStatement ps = connection.prepareStatement(sql);
                    ps.setInt(1, 1);
                    ResultSet resultSet = ps.executeQuery();
                    results.addAll(resultSetProcessor.extractResults(resultSet, ((SessionImplementor) (workSession)), new QueryParameters(), Helper.parameterContext(), true, false, null, null));
                    resultSet.close();
                    ps.close();
                }
            });
            Assert.assertEquals(2, results.size());
            Object result1 = results.get(0);
            Assert.assertSame(result1, results.get(1));
            Assert.assertNotNull(result1);
            EntityWithNonLazyCollectionResultSetProcessorTest.Person workPerson = ExtraAssertions.assertTyping(EntityWithNonLazyCollectionResultSetProcessorTest.Person.class, result1);
            Assert.assertEquals(1, workPerson.id.intValue());
            Assert.assertEquals(person.name, workPerson.name);
            Assert.assertTrue(Hibernate.isInitialized(workPerson.nickNames));
            Assert.assertEquals(2, workPerson.nickNames.size());
            Assert.assertEquals(person.nickNames, workPerson.nickNames);
            workSession.getTransaction().commit();
            workSession.close();
        }
        // clean up test data
        session = openSession();
        session.beginTransaction();
        session.delete(person);
        session.getTransaction().commit();
        session.close();
    }

    @Entity(name = "Person")
    public static class Person {
        @Id
        private Integer id;

        private String name;

        @ElementCollection(fetch = FetchType.EAGER)
        @CollectionTable(name = "nick_names", joinColumns = @JoinColumn(name = "pid"))
        @Column(name = "nick")
        private Set<String> nickNames = new HashSet<String>();
    }
}

