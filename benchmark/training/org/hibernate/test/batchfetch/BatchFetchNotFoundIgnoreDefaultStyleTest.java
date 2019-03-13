/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.batchfetch;


import java.util.ArrayList;
import java.util.List;
import javax.persistence.ConstraintMode;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.resource.jdbc.spi.StatementInspector;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Gail Badner
 * @author Stephen Fikes
 */
public class BatchFetchNotFoundIgnoreDefaultStyleTest extends BaseCoreFunctionalTestCase {
    private static final BatchFetchNotFoundIgnoreDefaultStyleTest.AStatementInspector statementInspector = new BatchFetchNotFoundIgnoreDefaultStyleTest.AStatementInspector();

    private static final int NUMBER_OF_EMPLOYEES = 8;

    private List<BatchFetchNotFoundIgnoreDefaultStyleTest.Task> tasks = new ArrayList<>();

    @Test
    public void testSeveralNotFoundFromQuery() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            // delete 2nd and 8th Task so that the non-found Task entities will be queried
            // in 2 different batches.
            session.delete(tasks.get(1));
            session.delete(tasks.get(7));
        });
        BatchFetchNotFoundIgnoreDefaultStyleTest.statementInspector.clear();
        final List<BatchFetchNotFoundIgnoreDefaultStyleTest.Employee> employees = TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            List<org.hibernate.test.batchfetch.Employee> results = session.createQuery("from Employee e order by e.id", .class).getResultList();
            for (int i = 0; i < (tasks.size()); i++) {
                checkInBatchFetchQueue(tasks.get(i).id, session, false);
            }
            return results;
        });
        final List<Integer> paramterCounts = BatchFetchNotFoundIgnoreDefaultStyleTest.statementInspector.parameterCounts;
        // there should be 4 SQL statements executed
        Assert.assertEquals(4, paramterCounts.size());
        // query loading Employee entities shouldn't have any parameters
        Assert.assertEquals(0, paramterCounts.get(0).intValue());
        // query specifically for Task with ID == 0 will result in 1st batch;
        // query should have 5 parameters for [0,1,2,3,4];
        // Task with ID == 1 won't be found; the rest will be found.
        Assert.assertEquals(5, paramterCounts.get(1).intValue());
        // query specifically for Task with ID == 1 will result in 2nd batch;
        // query should have 4 parameters [1,5,6,7];
        // Task with IDs == [1,7] won't be found; the rest will be found.
        Assert.assertEquals(4, paramterCounts.get(2).intValue());
        // no extra queries required to load entities with IDs [2,3,4] because they
        // were already loaded from 1st batch
        // no extra queries required to load entities with IDs [5,6] because they
        // were already loaded from 2nd batch
        // query specifically for Task with ID == 7 will result in just querying
        // Task with ID == 7 (because the batch is empty).
        // query should have 1 parameter [7];
        // Task with ID == 7 won't be found.
        Assert.assertEquals(1, paramterCounts.get(3).intValue());
        Assert.assertEquals(BatchFetchNotFoundIgnoreDefaultStyleTest.NUMBER_OF_EMPLOYEES, employees.size());
        for (int i = 0; i < (BatchFetchNotFoundIgnoreDefaultStyleTest.NUMBER_OF_EMPLOYEES); i++) {
            if ((i == 1) || (i == 7)) {
                Assert.assertNull(employees.get(i).task);
            } else {
                Assert.assertEquals(tasks.get(i).id, employees.get(i).task.id);
            }
        }
    }

    @Test
    public void testMostNotFoundFromQuery() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            // delete all but last Task entity
            for (int i = 0; i < 7; i++) {
                session.delete(tasks.get(i));
            }
        });
        BatchFetchNotFoundIgnoreDefaultStyleTest.statementInspector.clear();
        final List<BatchFetchNotFoundIgnoreDefaultStyleTest.Employee> employees = TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            List<org.hibernate.test.batchfetch.Employee> results = session.createQuery("from Employee e order by e.id", .class).getResultList();
            for (int i = 0; i < (tasks.size()); i++) {
                checkInBatchFetchQueue(tasks.get(i).id, session, false);
            }
            return results;
        });
        final List<Integer> paramterCounts = BatchFetchNotFoundIgnoreDefaultStyleTest.statementInspector.parameterCounts;
        // there should be 8 SQL statements executed
        Assert.assertEquals(8, paramterCounts.size());
        // query loading Employee entities shouldn't have any parameters
        Assert.assertEquals(0, paramterCounts.get(0).intValue());
        // query specifically for Task with ID == 0 will result in 1st batch;
        // query should have 5 parameters for [0,1,2,3,4];
        // Task with IDs == [0,1,2,3,4] won't be found
        Assert.assertEquals(5, paramterCounts.get(1).intValue());
        // query specifically for Task with ID == 1 will result in 2nd batch;
        // query should have 4 parameters [1,5,6,7];
        // Task with IDs == [1,5,6] won't be found; Task with ID == 7 will be found.
        Assert.assertEquals(4, paramterCounts.get(2).intValue());
        // query specifically for Task with ID == 2 will result in just querying
        // Task with ID == 2 (because the batch is empty).
        // query should have 1 parameter [2];
        // Task with ID == 2 won't be found.
        Assert.assertEquals(1, paramterCounts.get(3).intValue());
        // query specifically for Task with ID == 3 will result in just querying
        // Task with ID == 3 (because the batch is empty).
        // query should have 1 parameter [3];
        // Task with ID == 3 won't be found.
        Assert.assertEquals(1, paramterCounts.get(4).intValue());
        // query specifically for Task with ID == 4 will result in just querying
        // Task with ID == 4 (because the batch is empty).
        // query should have 1 parameter [4];
        // Task with ID == 4 won't be found.
        Assert.assertEquals(1, paramterCounts.get(5).intValue());
        // query specifically for Task with ID == 5 will result in just querying
        // Task with ID == 5 (because the batch is empty).
        // query should have 1 parameter [5];
        // Task with ID == 5 won't be found.
        Assert.assertEquals(1, paramterCounts.get(6).intValue());
        // query specifically for Task with ID == 6 will result in just querying
        // Task with ID == 6 (because the batch is empty).
        // query should have 1 parameter [6];
        // Task with ID == 6 won't be found.
        Assert.assertEquals(1, paramterCounts.get(7).intValue());
        // no extra queries required to load entity with ID == 7 because it
        // was already loaded from 2nd batch
        Assert.assertEquals(BatchFetchNotFoundIgnoreDefaultStyleTest.NUMBER_OF_EMPLOYEES, employees.size());
        for (int i = 0; i < (BatchFetchNotFoundIgnoreDefaultStyleTest.NUMBER_OF_EMPLOYEES); i++) {
            if (i == 7) {
                Assert.assertEquals(tasks.get(i).id, employees.get(i).task.id);
            } else {
                Assert.assertNull(employees.get(i).task);
            }
        }
    }

    @Test
    public void testNotFoundFromGet() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            // delete task so it is not found later when getting the Employee.
            session.delete(tasks.get(0));
        });
        BatchFetchNotFoundIgnoreDefaultStyleTest.statementInspector.clear();
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.test.batchfetch.Employee employee = session.get(.class, "employee00");
            checkInBatchFetchQueue(tasks.get(0).id, session, false);
            assertNotNull(employee);
            assertNull(employee.task);
        });
        final List<Integer> paramterCounts = BatchFetchNotFoundIgnoreDefaultStyleTest.statementInspector.parameterCounts;
        // there should be 2 SQL statements executed
        // 1) query to load Employee entity by ID (associated Tasks is registered for batch loading)
        // 2) batch will only contain the ID for the associated Task (which will not be found)
        Assert.assertEquals(2, paramterCounts.size());
        // query loading Employee entities shouldn't have any parameters
        Assert.assertEquals(1, paramterCounts.get(0).intValue());
        // Will result in just querying a single Task (because the batch is empty).
        // query should have 1 parameter;
        // Task won't be found.
        Assert.assertEquals(1, paramterCounts.get(1).intValue());
    }

    @Entity(name = "Employee")
    public static class Employee {
        @Id
        private String name;

        @OneToOne(optional = true)
        @JoinColumn(foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
        @NotFound(action = NotFoundAction.IGNORE)
        private BatchFetchNotFoundIgnoreDefaultStyleTest.Task task;

        private Employee() {
        }

        private Employee(String name) {
            this.name = name;
        }
    }

    @Entity(name = "Task")
    @BatchSize(size = 5)
    public static class Task {
        @Id
        private long id;

        public Task() {
        }
    }

    public static class AStatementInspector implements StatementInspector {
        private List<Integer> parameterCounts = new ArrayList<>();

        public String inspect(String sql) {
            parameterCounts.add(countParameters(sql));
            return sql;
        }

        private void clear() {
            parameterCounts.clear();
        }

        private int countParameters(String sql) {
            int count = 0;
            int parameterIndex = sql.indexOf('?');
            while (parameterIndex >= 0) {
                count++;
                parameterIndex = sql.indexOf('?', (parameterIndex + 1));
            } 
            return count;
        }
    }
}

