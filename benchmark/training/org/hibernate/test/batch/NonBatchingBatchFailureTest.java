/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.batch;


import java.lang.reflect.Field;
import java.util.Map;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.Session;
import org.hibernate.engine.jdbc.batch.internal.AbstractBatchImpl;
import org.hibernate.engine.jdbc.batch.internal.NonBatchingBatch;
import org.hibernate.engine.jdbc.batch.spi.Batch;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Shawn Clowater
 * @author Steve Ebersole
 */
@TestForIssue(jiraKey = "HHH-7689")
public class NonBatchingBatchFailureTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testBasicInsertion() {
        Session session = openSession();
        session.getTransaction().begin();
        try {
            session.persist(new NonBatchingBatchFailureTest.User(1, "ok"));
            session.persist(new NonBatchingBatchFailureTest.User(2, null));
            session.persist(new NonBatchingBatchFailureTest.User(3, "ok"));
            // the flush should fail
            session.flush();
            Assert.fail("Expecting failed flush");
        } catch (Exception expected) {
            System.out.println(("Caught expected exception : " + expected));
            expected.printStackTrace(System.out);
            try {
                // at this point the transaction is still active but the batch should have been aborted (have to use reflection to get at the field)
                SessionImplementor sessionImplementor = ((SessionImplementor) (session));
                Field field = sessionImplementor.getJdbcCoordinator().getClass().getDeclaredField("currentBatch");
                field.setAccessible(true);
                Batch batch = ((Batch) (field.get(sessionImplementor.getJdbcCoordinator())));
                if (batch != null) {
                    // make sure it's actually a batching impl
                    Assert.assertEquals(NonBatchingBatch.class, batch.getClass());
                    field = AbstractBatchImpl.class.getDeclaredField("statements");
                    field.setAccessible(true);
                    // check to see that there aren't any statements queued up (this can be an issue if using SavePoints)
                    Assert.assertEquals(0, ((Map) (field.get(batch))).size());
                }
            } catch (Exception fieldException) {
                Assert.fail(("Couldn't inspect field " + (fieldException.getMessage())));
            }
        } finally {
            session.getTransaction().rollback();
            session.close();
        }
    }

    @Entity(name = "User")
    @Table(name = "`USER`")
    public static class User {
        private Integer id;

        private String name;

        public User() {
        }

        public User(Integer id, String name) {
            this.id = id;
            this.name = name;
        }

        @Id
        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        @Column(nullable = false)
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}

