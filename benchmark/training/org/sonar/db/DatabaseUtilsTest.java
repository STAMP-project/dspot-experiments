/**
 * SonarQube
 * Copyright (C) 2009-2019 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.db;


import LoggerLevel.ERROR;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.sonar.api.utils.log.LogTester;
import org.sonar.api.utils.log.Loggers;
import org.sonar.core.util.stream.MoreCollectors;


public class DatabaseUtilsTest {
    @Rule
    public CoreDbTester dbTester = CoreDbTester.createForSchema(DatabaseUtilsTest.class, "just_one_table.sql");

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public LogTester logTester = new LogTester();

    @Test
    public void should_close_connection() throws Exception {
        Connection connection = dbTester.openConnection();
        assertThat(isClosed(connection)).isFalse();
        DatabaseUtils.closeQuietly(connection);
        assertThat(isClosed(connection)).isTrue();
    }

    @Test
    public void should_support_null_connection() {
        DatabaseUtils.closeQuietly(((Connection) (null)));
        // no failure
    }

    @Test
    public void should_close_statement_and_resultset() throws Exception {
        Connection connection = dbTester.openConnection();
        try {
            PreparedStatement statement = connection.prepareStatement(selectDual());
            ResultSet rs = statement.executeQuery();
            DatabaseUtils.closeQuietly(rs);
            DatabaseUtils.closeQuietly(statement);
            assertThat(isClosed(statement)).isTrue();
            assertThat(isClosed(rs)).isTrue();
        } finally {
            DatabaseUtils.closeQuietly(connection);
        }
    }

    @Test
    public void should_not_fail_on_connection_errors() throws SQLException {
        Connection connection = Mockito.mock(Connection.class);
        Mockito.doThrow(new SQLException()).when(connection).close();
        DatabaseUtils.closeQuietly(connection);
        // no failure
        Mockito.verify(connection).close();// just to be sure

    }

    @Test
    public void should_not_fail_on_statement_errors() throws SQLException {
        Statement statement = Mockito.mock(Statement.class);
        Mockito.doThrow(new SQLException()).when(statement).close();
        DatabaseUtils.closeQuietly(statement);
        // no failure
        Mockito.verify(statement).close();// just to be sure

    }

    @Test
    public void should_not_fail_on_resulset_errors() throws SQLException {
        ResultSet rs = Mockito.mock(ResultSet.class);
        Mockito.doThrow(new SQLException()).when(rs).close();
        DatabaseUtils.closeQuietly(rs);
        // no failure
        Mockito.verify(rs).close();// just to be sure

    }

    @Test
    public void toUniqueAndSortedList_throws_NPE_if_arg_is_null() {
        expectedException.expect(NullPointerException.class);
        DatabaseUtils.toUniqueAndSortedList(null);
    }

    @Test
    public void toUniqueAndSortedList_throws_NPE_if_arg_contains_a_null() {
        expectedException.expect(NullPointerException.class);
        DatabaseUtils.toUniqueAndSortedList(Arrays.asList("A", null, "C"));
    }

    @Test
    public void toUniqueAndSortedList_throws_NPE_if_arg_is_a_set_containing_a_null() {
        expectedException.expect(NullPointerException.class);
        DatabaseUtils.toUniqueAndSortedList(new HashSet(Arrays.asList("A", ((String) (null)), "C")));
    }

    @Test
    public void toUniqueAndSortedList_enforces_natural_order() {
        assertThat(DatabaseUtils.toUniqueAndSortedList(Arrays.asList("A", "B", "C"))).containsExactly("A", "B", "C");
        assertThat(DatabaseUtils.toUniqueAndSortedList(Arrays.asList("B", "A", "C"))).containsExactly("A", "B", "C");
        assertThat(DatabaseUtils.toUniqueAndSortedList(Arrays.asList("B", "C", "A"))).containsExactly("A", "B", "C");
    }

    @Test
    public void toUniqueAndSortedList_removes_duplicates() {
        assertThat(DatabaseUtils.toUniqueAndSortedList(Arrays.asList("A", "A", "A"))).containsExactly("A");
        assertThat(DatabaseUtils.toUniqueAndSortedList(Arrays.asList("A", "C", "A"))).containsExactly("A", "C");
        assertThat(DatabaseUtils.toUniqueAndSortedList(Arrays.asList("C", "C", "B", "B", "A", "N", "C", "A"))).containsExactly("A", "B", "C", "N");
    }

    @Test
    public void toUniqueAndSortedList_removes_duplicates_and_apply_natural_order_of_any_Comparable() {
        assertThat(DatabaseUtils.toUniqueAndSortedList(Arrays.asList(DatabaseUtilsTest.myComparable(2), DatabaseUtilsTest.myComparable(5), DatabaseUtilsTest.myComparable(2), DatabaseUtilsTest.myComparable(4), DatabaseUtilsTest.myComparable((-1)), DatabaseUtilsTest.myComparable(10)))).containsExactly(DatabaseUtilsTest.myComparable((-1)), DatabaseUtilsTest.myComparable(2), DatabaseUtilsTest.myComparable(4), DatabaseUtilsTest.myComparable(5), DatabaseUtilsTest.myComparable(10));
    }

    private static final class MyComparable implements Comparable<DatabaseUtilsTest.MyComparable> {
        private final int ordinal;

        private MyComparable(int ordinal) {
            this.ordinal = ordinal;
        }

        @Override
        public int compareTo(DatabaseUtilsTest.MyComparable o) {
            return (ordinal) - (o.ordinal);
        }

        @Override
        public boolean equals(@Nullable
        Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            DatabaseUtilsTest.MyComparable that = ((DatabaseUtilsTest.MyComparable) (o));
            return (ordinal) == (that.ordinal);
        }

        @Override
        public int hashCode() {
            return Objects.hash(ordinal);
        }
    }

    @Test
    public void executeLargeInputs() {
        List<Integer> inputs = Lists.newArrayList();
        List<String> expectedOutputs = Lists.newArrayList();
        for (int i = 0; i < 2010; i++) {
            inputs.add(i);
            expectedOutputs.add(Integer.toString(i));
        }
        List<String> outputs = DatabaseUtils.executeLargeInputs(inputs, ( input) -> {
            // Check that each partition is only done on 1000 elements max
            assertThat(input.size()).isLessThanOrEqualTo(1000);
            return input.stream().map(String::valueOf).collect(MoreCollectors.toList());
        });
        assertThat(outputs).isEqualTo(expectedOutputs);
    }

    @Test
    public void executeLargeInputs_on_empty_list() {
        List<String> outputs = DatabaseUtils.executeLargeInputs(Collections.emptyList(), new Function<List<Integer>, List<String>>() {
            @Override
            public List<String> apply(List<Integer> input) {
                fail("No partition should be made on empty list");
                return Collections.emptyList();
            }
        });
        assertThat(outputs).isEmpty();
    }

    @Test
    public void executeLargeInputs_uses_specified_partition_size_manipulations() {
        List<List<Integer>> partitions = new ArrayList<>();
        List<Integer> outputs = DatabaseUtils.executeLargeInputs(Arrays.asList(1, 2, 3), ( partition) -> {
            partitions.add(partition);
            return partition;
        }, ( i) -> i / 500);
        assertThat(outputs).containsExactly(1, 2, 3);
        assertThat(partitions).containsExactly(Arrays.asList(1, 2), Arrays.asList(3));
    }

    @Test
    public void executeLargeUpdates() {
        List<Integer> inputs = Lists.newArrayList();
        for (int i = 0; i < 2010; i++) {
            inputs.add(i);
        }
        List<Integer> processed = Lists.newArrayList();
        DatabaseUtils.executeLargeUpdates(inputs, ( input) -> {
            assertThat(input.size()).isLessThanOrEqualTo(1000);
            processed.addAll(input);
        });
        assertThat(processed).containsExactlyElementsOf(inputs);
    }

    @Test
    public void executeLargeUpdates_on_empty_list() {
        DatabaseUtils.executeLargeUpdates(Collections.<Integer>emptyList(), ( input) -> {
            fail("No partition should be made on empty list");
        });
    }

    @Test
    public void log_all_sql_exceptions() {
        SQLException root = new SQLException("this is root", "123");
        SQLException next = new SQLException("this is next", "456");
        root.setNextException(next);
        DatabaseUtils.log(Loggers.get(getClass()), root);
        assertThat(logTester.logs(ERROR)).contains("SQL error: 456. Message: this is next");
    }

    @Test
    public void tableExists_returns_true_if_table_is_referenced_in_db_metadata() throws Exception {
        try (Connection connection = dbTester.openConnection()) {
            assertThat(DatabaseUtils.tableExists("SCHEMA_MIGRATIONS", connection)).isTrue();
            assertThat(DatabaseUtils.tableExists("schema_migrations", connection)).isTrue();
            assertThat(DatabaseUtils.tableExists("schema_MIGRATIONS", connection)).isTrue();
            assertThat(DatabaseUtils.tableExists("foo", connection)).isFalse();
        }
    }

    @Test
    public void tableExists_is_resilient_on_getSchema() throws Exception {
        try (Connection connection = Mockito.spy(dbTester.openConnection())) {
            Mockito.doThrow(AbstractMethodError.class).when(connection).getSchema();
            assertThat(DatabaseUtils.tableExists("SCHEMA_MIGRATIONS", connection)).isTrue();
            assertThat(DatabaseUtils.tableExists("schema_migrations", connection)).isTrue();
            assertThat(DatabaseUtils.tableExists("schema_MIGRATIONS", connection)).isTrue();
            assertThat(DatabaseUtils.tableExists("foo", connection)).isFalse();
        }
    }

    @Test
    public void tableExists_is_using_getSchema_when_not_using_h2() throws Exception {
        try (Connection connection = Mockito.spy(dbTester.openConnection())) {
            // DatabaseMetaData mock
            DatabaseMetaData metaData = Mockito.mock(DatabaseMetaData.class);
            Mockito.doReturn("xxx").when(metaData).getDriverName();
            // ResultSet mock
            ResultSet resultSet = Mockito.mock(ResultSet.class);
            Mockito.doReturn(true, false).when(resultSet).next();
            Mockito.doReturn("SCHEMA_MIGRATIONS").when(resultSet).getString(ArgumentMatchers.eq("TABLE_NAME"));
            Mockito.doReturn(resultSet).when(metaData).getTables(ArgumentMatchers.any(), ArgumentMatchers.eq("yyyy"), ArgumentMatchers.any(), ArgumentMatchers.any());
            // Connection mock
            Mockito.doReturn("yyyy").when(connection).getSchema();
            Mockito.doReturn(metaData).when(connection).getMetaData();
            assertThat(DatabaseUtils.tableExists("SCHEMA_MIGRATIONS", connection)).isTrue();
        }
    }

    @Test
    public void checkThatNotTooManyConditions_does_not_fail_if_less_than_1000_conditions() {
        DatabaseUtils.checkThatNotTooManyConditions(null, "unused");
        DatabaseUtils.checkThatNotTooManyConditions(Collections.emptySet(), "unused");
        DatabaseUtils.checkThatNotTooManyConditions(Collections.nCopies(10, "foo"), "unused");
        DatabaseUtils.checkThatNotTooManyConditions(Collections.nCopies(1000, "foo"), "unused");
    }

    @Test
    public void checkThatNotTooManyConditions_throws_IAE_if_strictly_more_than_1000_conditions() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("the message");
        DatabaseUtils.checkThatNotTooManyConditions(Collections.nCopies(1001, "foo"), "the message");
    }
}

