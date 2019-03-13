/**
 * Copyright 2002-2016 the original author or authors.
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
package org.springframework.jdbc.object;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import javax.sql.DataSource;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.jdbc.JdbcUpdateAffectedIncorrectNumberOfRowsException;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;


/**
 *
 *
 * @author Trevor Cook
 * @author Thomas Risberg
 * @author Juergen Hoeller
 */
public class SqlUpdateTests {
    private static final String UPDATE = "update seat_status set booking_id = null";

    private static final String UPDATE_INT = "update seat_status set booking_id = null where performance_id = ?";

    private static final String UPDATE_INT_INT = "update seat_status set booking_id = null where performance_id = ? and price_band_id = ?";

    private static final String UPDATE_NAMED_PARAMETERS = "update seat_status set booking_id = null where performance_id = :perfId and price_band_id = :priceId";

    private static final String UPDATE_STRING = "update seat_status set booking_id = null where name = ?";

    private static final String UPDATE_OBJECTS = "update seat_status set booking_id = null where performance_id = ? and price_band_id = ? and name = ? and confirmed = ?";

    private static final String INSERT_GENERATE_KEYS = "insert into show (name) values(?)";

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private DataSource dataSource;

    private Connection connection;

    private PreparedStatement preparedStatement;

    private ResultSet resultSet;

    private ResultSetMetaData resultSetMetaData;

    @Test
    public void testUpdate() throws SQLException {
        BDDMockito.given(preparedStatement.executeUpdate()).willReturn(1);
        BDDMockito.given(connection.prepareStatement(SqlUpdateTests.UPDATE)).willReturn(preparedStatement);
        SqlUpdateTests.Updater pc = new SqlUpdateTests.Updater();
        int rowsAffected = pc.run();
        Assert.assertEquals(1, rowsAffected);
    }

    @Test
    public void testUpdateInt() throws SQLException {
        BDDMockito.given(preparedStatement.executeUpdate()).willReturn(1);
        BDDMockito.given(connection.prepareStatement(SqlUpdateTests.UPDATE_INT)).willReturn(preparedStatement);
        SqlUpdateTests.IntUpdater pc = new SqlUpdateTests.IntUpdater();
        int rowsAffected = pc.run(1);
        Assert.assertEquals(1, rowsAffected);
        Mockito.verify(preparedStatement).setObject(1, 1, Types.NUMERIC);
    }

    @Test
    public void testUpdateIntInt() throws SQLException {
        BDDMockito.given(preparedStatement.executeUpdate()).willReturn(1);
        BDDMockito.given(connection.prepareStatement(SqlUpdateTests.UPDATE_INT_INT)).willReturn(preparedStatement);
        SqlUpdateTests.IntIntUpdater pc = new SqlUpdateTests.IntIntUpdater();
        int rowsAffected = pc.run(1, 1);
        Assert.assertEquals(1, rowsAffected);
        Mockito.verify(preparedStatement).setObject(1, 1, Types.NUMERIC);
        Mockito.verify(preparedStatement).setObject(2, 1, Types.NUMERIC);
    }

    @Test
    public void testNamedParameterUpdateWithUnnamedDeclarations() throws SQLException {
        doTestNamedParameterUpdate(false);
    }

    @Test
    public void testNamedParameterUpdateWithNamedDeclarations() throws SQLException {
        doTestNamedParameterUpdate(true);
    }

    @Test
    public void testUpdateString() throws SQLException {
        BDDMockito.given(preparedStatement.executeUpdate()).willReturn(1);
        BDDMockito.given(connection.prepareStatement(SqlUpdateTests.UPDATE_STRING)).willReturn(preparedStatement);
        SqlUpdateTests.StringUpdater pc = new SqlUpdateTests.StringUpdater();
        int rowsAffected = pc.run("rod");
        Assert.assertEquals(1, rowsAffected);
        Mockito.verify(preparedStatement).setString(1, "rod");
    }

    @Test
    public void testUpdateMixed() throws SQLException {
        BDDMockito.given(preparedStatement.executeUpdate()).willReturn(1);
        BDDMockito.given(connection.prepareStatement(SqlUpdateTests.UPDATE_OBJECTS)).willReturn(preparedStatement);
        SqlUpdateTests.MixedUpdater pc = new SqlUpdateTests.MixedUpdater();
        int rowsAffected = pc.run(1, 1, "rod", true);
        Assert.assertEquals(1, rowsAffected);
        Mockito.verify(preparedStatement).setObject(1, 1, Types.NUMERIC);
        Mockito.verify(preparedStatement).setObject(2, 1, Types.NUMERIC, 2);
        Mockito.verify(preparedStatement).setString(3, "rod");
        Mockito.verify(preparedStatement).setBoolean(4, Boolean.TRUE);
    }

    @Test
    public void testUpdateAndGeneratedKeys() throws SQLException {
        BDDMockito.given(resultSetMetaData.getColumnCount()).willReturn(1);
        BDDMockito.given(resultSetMetaData.getColumnLabel(1)).willReturn("1");
        BDDMockito.given(resultSet.getMetaData()).willReturn(resultSetMetaData);
        BDDMockito.given(resultSet.next()).willReturn(true, false);
        BDDMockito.given(resultSet.getObject(1)).willReturn(11);
        BDDMockito.given(preparedStatement.executeUpdate()).willReturn(1);
        BDDMockito.given(preparedStatement.getGeneratedKeys()).willReturn(resultSet);
        BDDMockito.given(connection.prepareStatement(SqlUpdateTests.INSERT_GENERATE_KEYS, PreparedStatement.RETURN_GENERATED_KEYS)).willReturn(preparedStatement);
        SqlUpdateTests.GeneratedKeysUpdater pc = new SqlUpdateTests.GeneratedKeysUpdater();
        KeyHolder generatedKeyHolder = new GeneratedKeyHolder();
        int rowsAffected = pc.run("rod", generatedKeyHolder);
        Assert.assertEquals(1, rowsAffected);
        Assert.assertEquals(1, generatedKeyHolder.getKeyList().size());
        Assert.assertEquals(11, generatedKeyHolder.getKey().intValue());
        Mockito.verify(preparedStatement).setString(1, "rod");
        Mockito.verify(resultSet).close();
    }

    @Test
    public void testUpdateConstructor() throws SQLException {
        BDDMockito.given(preparedStatement.executeUpdate()).willReturn(1);
        BDDMockito.given(connection.prepareStatement(SqlUpdateTests.UPDATE_OBJECTS)).willReturn(preparedStatement);
        SqlUpdateTests.ConstructorUpdater pc = new SqlUpdateTests.ConstructorUpdater();
        int rowsAffected = pc.run(1, 1, "rod", true);
        Assert.assertEquals(1, rowsAffected);
        Mockito.verify(preparedStatement).setObject(1, 1, Types.NUMERIC);
        Mockito.verify(preparedStatement).setObject(2, 1, Types.NUMERIC);
        Mockito.verify(preparedStatement).setString(3, "rod");
        Mockito.verify(preparedStatement).setBoolean(4, Boolean.TRUE);
    }

    @Test
    public void testUnderMaxRows() throws SQLException {
        BDDMockito.given(preparedStatement.executeUpdate()).willReturn(3);
        BDDMockito.given(connection.prepareStatement(SqlUpdateTests.UPDATE)).willReturn(preparedStatement);
        SqlUpdateTests.MaxRowsUpdater pc = new SqlUpdateTests.MaxRowsUpdater();
        int rowsAffected = pc.run();
        Assert.assertEquals(3, rowsAffected);
    }

    @Test
    public void testMaxRows() throws SQLException {
        BDDMockito.given(preparedStatement.executeUpdate()).willReturn(5);
        BDDMockito.given(connection.prepareStatement(SqlUpdateTests.UPDATE)).willReturn(preparedStatement);
        SqlUpdateTests.MaxRowsUpdater pc = new SqlUpdateTests.MaxRowsUpdater();
        int rowsAffected = pc.run();
        Assert.assertEquals(5, rowsAffected);
    }

    @Test
    public void testOverMaxRows() throws SQLException {
        BDDMockito.given(preparedStatement.executeUpdate()).willReturn(8);
        BDDMockito.given(connection.prepareStatement(SqlUpdateTests.UPDATE)).willReturn(preparedStatement);
        SqlUpdateTests.MaxRowsUpdater pc = new SqlUpdateTests.MaxRowsUpdater();
        thrown.expect(JdbcUpdateAffectedIncorrectNumberOfRowsException.class);
        pc.run();
    }

    @Test
    public void testRequiredRows() throws SQLException {
        BDDMockito.given(preparedStatement.executeUpdate()).willReturn(3);
        BDDMockito.given(connection.prepareStatement(SqlUpdateTests.UPDATE)).willReturn(preparedStatement);
        SqlUpdateTests.RequiredRowsUpdater pc = new SqlUpdateTests.RequiredRowsUpdater();
        int rowsAffected = pc.run();
        Assert.assertEquals(3, rowsAffected);
    }

    @Test
    public void testNotRequiredRows() throws SQLException {
        BDDMockito.given(preparedStatement.executeUpdate()).willReturn(2);
        BDDMockito.given(connection.prepareStatement(SqlUpdateTests.UPDATE)).willReturn(preparedStatement);
        thrown.expect(JdbcUpdateAffectedIncorrectNumberOfRowsException.class);
        SqlUpdateTests.RequiredRowsUpdater pc = new SqlUpdateTests.RequiredRowsUpdater();
        pc.run();
    }

    private class Updater extends SqlUpdate {
        public Updater() {
            setSql(SqlUpdateTests.UPDATE);
            setDataSource(dataSource);
            compile();
        }

        public int run() {
            return update();
        }
    }

    private class IntUpdater extends SqlUpdate {
        public IntUpdater() {
            setSql(SqlUpdateTests.UPDATE_INT);
            setDataSource(dataSource);
            declareParameter(new SqlParameter(Types.NUMERIC));
            compile();
        }

        public int run(int performanceId) {
            return update(performanceId);
        }
    }

    private class IntIntUpdater extends SqlUpdate {
        public IntIntUpdater() {
            setSql(SqlUpdateTests.UPDATE_INT_INT);
            setDataSource(dataSource);
            declareParameter(new SqlParameter(Types.NUMERIC));
            declareParameter(new SqlParameter(Types.NUMERIC));
            compile();
        }

        public int run(int performanceId, int type) {
            return update(performanceId, type);
        }
    }

    private class StringUpdater extends SqlUpdate {
        public StringUpdater() {
            setSql(SqlUpdateTests.UPDATE_STRING);
            setDataSource(dataSource);
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        }

        public int run(String name) {
            return update(name);
        }
    }

    private class MixedUpdater extends SqlUpdate {
        public MixedUpdater() {
            setSql(SqlUpdateTests.UPDATE_OBJECTS);
            setDataSource(dataSource);
            declareParameter(new SqlParameter(Types.NUMERIC));
            declareParameter(new SqlParameter(Types.NUMERIC, 2));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.BOOLEAN));
            compile();
        }

        public int run(int performanceId, int type, String name, boolean confirmed) {
            return update(performanceId, type, name, confirmed);
        }
    }

    private class GeneratedKeysUpdater extends SqlUpdate {
        public GeneratedKeysUpdater() {
            setSql(SqlUpdateTests.INSERT_GENERATE_KEYS);
            setDataSource(dataSource);
            declareParameter(new SqlParameter(Types.VARCHAR));
            setReturnGeneratedKeys(true);
            compile();
        }

        public int run(String name, KeyHolder generatedKeyHolder) {
            return update(new Object[]{ name }, generatedKeyHolder);
        }
    }

    private class ConstructorUpdater extends SqlUpdate {
        public ConstructorUpdater() {
            super(dataSource, SqlUpdateTests.UPDATE_OBJECTS, new int[]{ Types.NUMERIC, Types.NUMERIC, Types.VARCHAR, Types.BOOLEAN });
            compile();
        }

        public int run(int performanceId, int type, String name, boolean confirmed) {
            return update(performanceId, type, name, confirmed);
        }
    }

    private class MaxRowsUpdater extends SqlUpdate {
        public MaxRowsUpdater() {
            setSql(SqlUpdateTests.UPDATE);
            setDataSource(dataSource);
            setMaxRowsAffected(5);
            compile();
        }

        public int run() {
            return update();
        }
    }

    private class RequiredRowsUpdater extends SqlUpdate {
        public RequiredRowsUpdater() {
            setSql(SqlUpdateTests.UPDATE);
            setDataSource(dataSource);
            setRequiredRowsAffected(3);
            compile();
        }

        public int run() {
            return update();
        }
    }
}

