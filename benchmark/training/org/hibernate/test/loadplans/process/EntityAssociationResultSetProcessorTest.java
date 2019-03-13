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
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
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
public class EntityAssociationResultSetProcessorTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testManyToOneEntityProcessing() throws Exception {
        final EntityPersister entityPersister = sessionFactory().getEntityPersister(EntityAssociationResultSetProcessorTest.Message.class.getName());
        // create some test data
        Session session = openSession();
        session.beginTransaction();
        EntityAssociationResultSetProcessorTest.Message message = new EntityAssociationResultSetProcessorTest.Message(1, "the message");
        EntityAssociationResultSetProcessorTest.Poster poster = new EntityAssociationResultSetProcessorTest.Poster(2, "the poster");
        session.save(message);
        session.save(poster);
        message.poster = poster;
        poster.messages.add(message);
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
            Assert.assertEquals(1, results.size());
            Object result = results.get(0);
            Assert.assertNotNull(result);
            EntityAssociationResultSetProcessorTest.Message workMessage = ExtraAssertions.assertTyping(EntityAssociationResultSetProcessorTest.Message.class, result);
            Assert.assertEquals(1, workMessage.mid.intValue());
            Assert.assertEquals("the message", workMessage.msgTxt);
            Assert.assertTrue(Hibernate.isInitialized(workMessage.poster));
            EntityAssociationResultSetProcessorTest.Poster workPoster = workMessage.poster;
            Assert.assertEquals(2, workPoster.pid.intValue());
            Assert.assertEquals("the poster", workPoster.name);
            Assert.assertFalse(Hibernate.isInitialized(workPoster.messages));
            workSession.getTransaction().commit();
            workSession.close();
        }
        // clean up test data
        session = openSession();
        session.beginTransaction();
        session.createQuery("delete Message").executeUpdate();
        session.createQuery("delete Poster").executeUpdate();
        session.getTransaction().commit();
        session.close();
    }

    @Test
    public void testNestedManyToOneEntityProcessing() throws Exception {
        final EntityPersister entityPersister = sessionFactory().getEntityPersister(EntityAssociationResultSetProcessorTest.ReportedMessage.class.getName());
        // create some test data
        Session session = openSession();
        session.beginTransaction();
        EntityAssociationResultSetProcessorTest.Message message = new EntityAssociationResultSetProcessorTest.Message(1, "the message");
        EntityAssociationResultSetProcessorTest.Poster poster = new EntityAssociationResultSetProcessorTest.Poster(2, "the poster");
        session.save(message);
        session.save(poster);
        message.poster = poster;
        poster.messages.add(message);
        EntityAssociationResultSetProcessorTest.ReportedMessage reportedMessage = new EntityAssociationResultSetProcessorTest.ReportedMessage(0, "inappropriate", message);
        session.save(reportedMessage);
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
                    ps.setInt(1, 0);
                    ResultSet resultSet = ps.executeQuery();
                    results.addAll(resultSetProcessor.extractResults(resultSet, ((SessionImplementor) (workSession)), new QueryParameters(), Helper.parameterContext(), true, false, null, null));
                    resultSet.close();
                    ps.close();
                }
            });
            Assert.assertEquals(1, results.size());
            Object result = results.get(0);
            Assert.assertNotNull(result);
            EntityAssociationResultSetProcessorTest.ReportedMessage workReportedMessage = ExtraAssertions.assertTyping(EntityAssociationResultSetProcessorTest.ReportedMessage.class, result);
            Assert.assertEquals(0, workReportedMessage.id.intValue());
            Assert.assertEquals("inappropriate", workReportedMessage.reason);
            EntityAssociationResultSetProcessorTest.Message workMessage = workReportedMessage.message;
            Assert.assertNotNull(workMessage);
            Assert.assertTrue(Hibernate.isInitialized(workMessage));
            Assert.assertEquals(1, workMessage.mid.intValue());
            Assert.assertEquals("the message", workMessage.msgTxt);
            Assert.assertTrue(Hibernate.isInitialized(workMessage.poster));
            EntityAssociationResultSetProcessorTest.Poster workPoster = workMessage.poster;
            Assert.assertEquals(2, workPoster.pid.intValue());
            Assert.assertEquals("the poster", workPoster.name);
            Assert.assertFalse(Hibernate.isInitialized(workPoster.messages));
            workSession.getTransaction().commit();
            workSession.close();
        }
        // clean up test data
        session = openSession();
        session.beginTransaction();
        session.createQuery("delete ReportedMessage").executeUpdate();
        session.createQuery("delete Message").executeUpdate();
        session.createQuery("delete Poster").executeUpdate();
        session.getTransaction().commit();
        session.close();
    }

    @Entity(name = "ReportedMessage")
    public static class ReportedMessage {
        @Id
        private Integer id;

        private String reason;

        @ManyToOne
        @JoinColumn
        private EntityAssociationResultSetProcessorTest.Message message;

        public ReportedMessage() {
        }

        public ReportedMessage(Integer id, String reason, EntityAssociationResultSetProcessorTest.Message message) {
            this.id = id;
            this.reason = reason;
            this.message = message;
        }
    }

    @Entity(name = "Message")
    public static class Message {
        @Id
        private Integer mid;

        private String msgTxt;

        @ManyToOne(cascade = CascadeType.MERGE)
        @JoinColumn
        private EntityAssociationResultSetProcessorTest.Poster poster;

        public Message() {
        }

        public Message(Integer mid, String msgTxt) {
            this.mid = mid;
            this.msgTxt = msgTxt;
        }
    }

    @Entity(name = "Poster")
    public static class Poster {
        @Id
        private Integer pid;

        private String name;

        @OneToMany(mappedBy = "poster")
        private List<EntityAssociationResultSetProcessorTest.Message> messages = new ArrayList<EntityAssociationResultSetProcessorTest.Message>();

        public Poster() {
        }

        public Poster(Integer pid, String name) {
            this.pid = pid;
            this.name = name;
        }
    }
}

