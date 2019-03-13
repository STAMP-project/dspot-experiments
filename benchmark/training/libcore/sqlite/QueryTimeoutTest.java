/**
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package libcore.sqlite;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import junit.framework.TestCase;


/**
 * Test that statements honor their timeout.
 */
public final class QueryTimeoutTest extends TestCase {
    private static final String EXEC_QUERY = "insert into t_copy select a from t_orig where DELAY(2,1)=1";

    private static final String FETCH_QUERY = "select a from t_orig where DELAY(2,1)=1";

    private Connection connection;

    public void testPreparedStatementFetch() throws Exception {
        PreparedStatement statement = connection.prepareStatement(QueryTimeoutTest.FETCH_QUERY);
        statement.setQueryTimeout(1);
        ResultSet resultSet = null;
        try {
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
            } 
            TestCase.fail();
        } catch (SQLException expected) {
        } finally {
            statement.close();
            if (resultSet != null) {
                resultSet.close();
            }
        }
    }

    public void testPreparedStatementUpdate() throws Exception {
        PreparedStatement statement = connection.prepareStatement(QueryTimeoutTest.EXEC_QUERY);
        try {
            statement.setQueryTimeout(1);
            statement.execute();
            TestCase.fail();
        } catch (SQLException expected) {
        } finally {
            statement.close();
        }
    }

    public void testInvalidTimeout() throws Exception {
        connection.setAutoCommit(true);
        PreparedStatement statement = connection.prepareStatement("select 'hello'");
        try {
            statement.setQueryTimeout((-1));
            TestCase.fail();
        } catch (SQLException expected) {
        }
        ResultSet resultSet = statement.executeQuery();
        resultSet.close();
        statement.close();
    }

    public void testExecuteUpdate() throws Exception {
        Statement statement = connection.createStatement();
        try {
            statement.setQueryTimeout(1);
            statement.executeUpdate(QueryTimeoutTest.EXEC_QUERY);
            TestCase.fail();
        } catch (SQLException expected) {
        } finally {
            statement.close();
        }
    }

    public void testTimeoutAndStatementReuse() throws Exception {
        Statement statement = connection.createStatement();
        statement.setQueryTimeout(1);
        for (int i = 0; i < 3; i++) {
            try {
                ResultSet resultSet = statement.executeQuery(QueryTimeoutTest.FETCH_QUERY);
                while (resultSet.next()) {
                } 
                TestCase.fail();
            } catch (SQLException expected) {
            }
        }
        statement.close();
    }
}

