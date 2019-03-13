/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.flush;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TransactionRequiredException;
import org.hamcrest.core.IsNot;
import org.hamcrest.core.IsNull;
import org.hibernate.Session;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Andrea Boriero
 */
@TestForIssue(jiraKey = "HHH-10877")
public class NonTransactionalDataAccessTest extends BaseCoreFunctionalTestCase {
    private String allowUpdateOperationOutsideTransaction = "true";

    @Test
    public void testFlushAllowingOutOfTransactionUpdateOperations() throws Exception {
        allowUpdateOperationOutsideTransaction = "true";
        rebuildSessionFactory();
        prepareTest();
        try (Session s = openSession()) {
            final NonTransactionalDataAccessTest.MyEntity entity = ((NonTransactionalDataAccessTest.MyEntity) (s.createQuery("from MyEntity e where e.name = :n").setParameter("n", "entity").uniqueResult()));
            Assert.assertThat(entity, IsNot.not(IsNull.nullValue()));
            entity.setName("changed");
            session.flush();
        }
    }

    @Test
    public void testNativeQueryAllowingOutOfTransactionUpdateOperations() throws Exception {
        allowUpdateOperationOutsideTransaction = "true";
        rebuildSessionFactory();
        prepareTest();
        try (Session s = openSession()) {
            s.createSQLQuery("delete from MY_ENTITY").executeUpdate();
        }
    }

    @Test(expected = TransactionRequiredException.class)
    public void testNativeQueryDisallowingOutOfTransactionUpdateOperations() throws Exception {
        allowUpdateOperationOutsideTransaction = "false";
        rebuildSessionFactory();
        prepareTest();
        try (Session s = openSession()) {
            s.createSQLQuery("delete from MY_ENTITY").executeUpdate();
        }
    }

    @Test(expected = TransactionRequiredException.class)
    public void testFlushDisallowingOutOfTransactionUpdateOperations() throws Exception {
        allowUpdateOperationOutsideTransaction = "false";
        rebuildSessionFactory();
        prepareTest();
        try (Session s = openSession()) {
            final NonTransactionalDataAccessTest.MyEntity entity = ((NonTransactionalDataAccessTest.MyEntity) (s.createQuery("from MyEntity e where e.name = :n").setParameter("n", "entity").uniqueResult()));
            Assert.assertThat(entity, IsNot.not(IsNull.nullValue()));
            entity.setName("changed");
            session.flush();
        }
    }

    @Test(expected = TransactionRequiredException.class)
    public void testFlushOutOfTransaction() throws Exception {
        allowUpdateOperationOutsideTransaction = "";
        rebuildSessionFactory();
        prepareTest();
        try (Session s = openSession()) {
            final NonTransactionalDataAccessTest.MyEntity entity = ((NonTransactionalDataAccessTest.MyEntity) (s.createQuery("from MyEntity e where e.name = :n").setParameter("n", "entity").uniqueResult()));
            Assert.assertThat(entity, IsNot.not(IsNull.nullValue()));
            entity.setName("changed");
            session.flush();
        }
    }

    @Entity(name = "MyEntity")
    @Table(name = "MY_ENTITY")
    public static class MyEntity {
        @Id
        @GeneratedValue
        long id;

        String name;

        public MyEntity() {
        }

        public MyEntity(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}

