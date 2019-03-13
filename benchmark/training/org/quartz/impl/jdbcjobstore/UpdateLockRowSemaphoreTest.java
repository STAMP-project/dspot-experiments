/**
 * All content copyright Terracotta, Inc., unless otherwise indicated. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.quartz.impl.jdbcjobstore;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 *
 *
 * @author cdennis
 */
public class UpdateLockRowSemaphoreTest {
    private static final PreparedStatement GOOD_STATEMENT = Mockito.mock(PreparedStatement.class);

    private static final PreparedStatement FAIL_STATEMENT = Mockito.mock(PreparedStatement.class);

    private static final PreparedStatement BAD_STATEMENT = Mockito.mock(PreparedStatement.class);

    static {
        try {
            Mockito.when(UpdateLockRowSemaphoreTest.GOOD_STATEMENT.executeUpdate()).thenReturn(1);
            Mockito.when(UpdateLockRowSemaphoreTest.FAIL_STATEMENT.executeUpdate()).thenReturn(0);
            Mockito.when(UpdateLockRowSemaphoreTest.BAD_STATEMENT.executeUpdate()).thenThrow(SQLException.class);
        } catch (SQLException e) {
            throw new AssertionError(e);
        }
    }

    @Test
    public void testSingleSuccessUsingUpdate() throws SQLException, LockException {
        UpdateLockRowSemaphore semaphore = new UpdateLockRowSemaphore();
        semaphore.setSchedName("test");
        Connection mockConnection = Mockito.mock(Connection.class);
        Mockito.when(mockConnection.prepareStatement(ArgumentMatchers.startsWith("UPDATE"))).thenReturn(UpdateLockRowSemaphoreTest.GOOD_STATEMENT).thenThrow(AssertionError.class);
        Assert.assertTrue(semaphore.obtainLock(mockConnection, "test"));
    }

    @Test
    public void testSingleFailureFollowedBySuccessUsingUpdate() throws SQLException, LockException {
        UpdateLockRowSemaphore semaphore = new UpdateLockRowSemaphore();
        semaphore.setSchedName("test");
        Connection mockConnection = Mockito.mock(Connection.class);
        Mockito.when(mockConnection.prepareStatement(ArgumentMatchers.startsWith("UPDATE"))).thenReturn(UpdateLockRowSemaphoreTest.BAD_STATEMENT).thenReturn(UpdateLockRowSemaphoreTest.GOOD_STATEMENT).thenThrow(AssertionError.class);
        Assert.assertTrue(semaphore.obtainLock(mockConnection, "test"));
    }

    @Test
    public void testDoubleFailureFollowedBySuccessUsingUpdate() throws SQLException, LockException {
        UpdateLockRowSemaphore semaphore = new UpdateLockRowSemaphore();
        semaphore.setSchedName("test");
        Connection mockConnection = Mockito.mock(Connection.class);
        Mockito.when(mockConnection.prepareStatement(ArgumentMatchers.startsWith("UPDATE"))).thenReturn(UpdateLockRowSemaphoreTest.BAD_STATEMENT, UpdateLockRowSemaphoreTest.BAD_STATEMENT).thenThrow(AssertionError.class);
        try {
            semaphore.obtainLock(mockConnection, "test");
            Assert.fail();
        } catch (LockException e) {
            // expected
        }
    }

    @Test
    public void testFallThroughToInsert() throws SQLException, LockException {
        UpdateLockRowSemaphore semaphore = new UpdateLockRowSemaphore();
        semaphore.setSchedName("test");
        Connection mockConnection = Mockito.mock(Connection.class);
        Mockito.when(mockConnection.prepareStatement(ArgumentMatchers.startsWith("UPDATE"))).thenReturn(UpdateLockRowSemaphoreTest.FAIL_STATEMENT).thenThrow(AssertionError.class);
        Mockito.when(mockConnection.prepareStatement(ArgumentMatchers.startsWith("INSERT"))).thenReturn(UpdateLockRowSemaphoreTest.GOOD_STATEMENT).thenThrow(AssertionError.class);
        Assert.assertTrue(semaphore.obtainLock(mockConnection, "test"));
    }
}

