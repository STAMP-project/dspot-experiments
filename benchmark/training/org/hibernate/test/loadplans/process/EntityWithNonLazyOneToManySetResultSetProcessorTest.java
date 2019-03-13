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
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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
public class EntityWithNonLazyOneToManySetResultSetProcessorTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testEntityWithSet() throws Exception {
        final EntityPersister entityPersister = sessionFactory().getEntityPersister(EntityWithNonLazyOneToManySetResultSetProcessorTest.Poster.class.getName());
        // create some test data
        Session session = openSession();
        session.beginTransaction();
        EntityWithNonLazyOneToManySetResultSetProcessorTest.Poster poster = new EntityWithNonLazyOneToManySetResultSetProcessorTest.Poster();
        poster.pid = 0;
        poster.name = "John Doe";
        EntityWithNonLazyOneToManySetResultSetProcessorTest.Message message1 = new EntityWithNonLazyOneToManySetResultSetProcessorTest.Message();
        message1.mid = 1;
        message1.msgTxt = "Howdy!";
        message1.poster = poster;
        poster.messages.add(message1);
        EntityWithNonLazyOneToManySetResultSetProcessorTest.Message message2 = new EntityWithNonLazyOneToManySetResultSetProcessorTest.Message();
        message2.mid = 2;
        message2.msgTxt = "Bye!";
        message2.poster = poster;
        poster.messages.add(message2);
        session.save(poster);
        session.getTransaction().commit();
        session.close();
        session = openSession();
        session.beginTransaction();
        EntityWithNonLazyOneToManySetResultSetProcessorTest.Poster posterGotten = ((EntityWithNonLazyOneToManySetResultSetProcessorTest.Poster) (session.get(EntityWithNonLazyOneToManySetResultSetProcessorTest.Poster.class, poster.pid)));
        Assert.assertEquals(0, posterGotten.pid.intValue());
        Assert.assertEquals(poster.name, posterGotten.name);
        Assert.assertTrue(Hibernate.isInitialized(posterGotten.messages));
        Assert.assertEquals(2, posterGotten.messages.size());
        for (EntityWithNonLazyOneToManySetResultSetProcessorTest.Message message : posterGotten.messages) {
            if ((message.mid) == 1) {
                Assert.assertEquals(message1.msgTxt, message.msgTxt);
            } else
                if ((message.mid) == 2) {
                    Assert.assertEquals(message2.msgTxt, message.msgTxt);
                } else {
                    Assert.fail("unexpected message id.");
                }

            Assert.assertSame(posterGotten, message.poster);
        }
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
            Assert.assertEquals(2, results.size());
            Object result1 = results.get(0);
            Assert.assertNotNull(result1);
            Assert.assertSame(result1, results.get(1));
            EntityWithNonLazyOneToManySetResultSetProcessorTest.Poster workPoster = ExtraAssertions.assertTyping(EntityWithNonLazyOneToManySetResultSetProcessorTest.Poster.class, result1);
            Assert.assertEquals(0, workPoster.pid.intValue());
            Assert.assertEquals(poster.name, workPoster.name);
            Assert.assertTrue(Hibernate.isInitialized(workPoster.messages));
            Assert.assertEquals(2, workPoster.messages.size());
            Assert.assertTrue(Hibernate.isInitialized(posterGotten.messages));
            Assert.assertEquals(2, workPoster.messages.size());
            for (EntityWithNonLazyOneToManySetResultSetProcessorTest.Message message : workPoster.messages) {
                if ((message.mid) == 1) {
                    Assert.assertEquals(message1.msgTxt, message.msgTxt);
                } else
                    if ((message.mid) == 2) {
                        Assert.assertEquals(message2.msgTxt, message.msgTxt);
                    } else {
                        Assert.fail("unexpected message id.");
                    }

                Assert.assertSame(workPoster, message.poster);
            }
            workSession.getTransaction().commit();
            workSession.close();
        }
        // clean up test data
        session = openSession();
        session.beginTransaction();
        session.delete(poster);
        session.getTransaction().commit();
        session.close();
    }

    @Entity(name = "Message")
    public static class Message {
        @Id
        private Integer mid;

        private String msgTxt;

        @ManyToOne
        @JoinColumn
        private EntityWithNonLazyOneToManySetResultSetProcessorTest.Poster poster;
    }

    @Entity(name = "Poster")
    public static class Poster {
        @Id
        private Integer pid;

        private String name;

        @OneToMany(mappedBy = "poster", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
        private Set<EntityWithNonLazyOneToManySetResultSetProcessorTest.Message> messages = new HashSet<EntityWithNonLazyOneToManySetResultSetProcessorTest.Message>();
    }
}

