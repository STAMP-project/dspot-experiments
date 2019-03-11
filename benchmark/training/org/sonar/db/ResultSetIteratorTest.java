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


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.NoSuchElementException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


public class ResultSetIteratorTest {
    @Rule
    public CoreDbTester dbTester = CoreDbTester.createForSchema(ResultSetIteratorTest.class, "schema.sql");

    @Test
    public void create_iterator_from_statement() throws Exception {
        dbTester.prepareDbUnit(getClass(), "feed.xml");
        try (Connection connection = dbTester.openConnection()) {
            PreparedStatement stmt = connection.prepareStatement("select * from issues order by id");
            ResultSetIteratorTest.FirstIntColumnIterator iterator = new ResultSetIteratorTest.FirstIntColumnIterator(stmt);
            assertThat(hasNext()).isTrue();
            // calling multiple times hasNext() is ok
            assertThat(hasNext()).isTrue();
            assertThat(next()).isEqualTo(10);
            assertThat(hasNext()).isTrue();
            assertThat(next()).isEqualTo(20);
            // call next() without calling hasNext()
            assertThat(next()).isEqualTo(30);
            assertThat(hasNext()).isFalse();
            try {
                next();
                Assert.fail();
            } catch (NoSuchElementException e) {
                // ok
            }
            close();
            // statement is closed by ResultSetIterator
            assertThat(stmt.isClosed()).isTrue();
        }
    }

    @Test
    public void iterate_empty_list() throws Exception {
        dbTester.prepareDbUnit(getClass(), "feed.xml");
        try (Connection connection = dbTester.openConnection()) {
            PreparedStatement stmt = connection.prepareStatement("select * from issues where id < 0");
            ResultSetIteratorTest.FirstIntColumnIterator iterator = new ResultSetIteratorTest.FirstIntColumnIterator(stmt);
            assertThat(hasNext()).isFalse();
        }
    }

    @Test
    public void create_iterator_from_result_set() throws Exception {
        dbTester.prepareDbUnit(getClass(), "feed.xml");
        try (Connection connection = dbTester.openConnection()) {
            PreparedStatement stmt = connection.prepareStatement("select * from issues order by id");
            ResultSet rs = stmt.executeQuery();
            ResultSetIteratorTest.FirstIntColumnIterator iterator = new ResultSetIteratorTest.FirstIntColumnIterator(rs);
            assertThat(next()).isEqualTo(10);
            assertThat(next()).isEqualTo(20);
            assertThat(next()).isEqualTo(30);
            close();
            assertThat(rs.isClosed()).isTrue();
            stmt.close();
        }
    }

    @Test
    public void remove_row_is_not_supported() throws Exception {
        try (Connection connection = dbTester.openConnection()) {
            PreparedStatement stmt = connection.prepareStatement("select * from issues order by id");
            ResultSetIteratorTest.FirstIntColumnIterator iterator = new ResultSetIteratorTest.FirstIntColumnIterator(stmt);
            try {
                remove();
                Assert.fail();
            } catch (UnsupportedOperationException ok) {
                // ok
            }
            close();
        }
    }

    @Test
    public void fail_to_read_row() throws Exception {
        dbTester.prepareDbUnit(getClass(), "feed.xml");
        try (Connection connection = dbTester.openConnection()) {
            PreparedStatement stmt = connection.prepareStatement("select * from issues order by id");
            ResultSetIteratorTest.FailIterator iterator = new ResultSetIteratorTest.FailIterator(stmt);
            assertThat(hasNext()).isTrue();
            try {
                next();
                Assert.fail();
            } catch (IllegalStateException e) {
                assertThat(e.getCause()).isInstanceOf(SQLException.class);
            }
            close();
        }
    }

    private static class FirstIntColumnIterator extends ResultSetIterator<Integer> {
        public FirstIntColumnIterator(PreparedStatement stmt) throws SQLException {
            super(stmt);
        }

        public FirstIntColumnIterator(ResultSet rs) {
            super(rs);
        }

        @Override
        protected Integer read(ResultSet rs) throws SQLException {
            return rs.getInt(1);
        }
    }

    private static class FailIterator extends ResultSetIterator<Integer> {
        public FailIterator(PreparedStatement stmt) throws SQLException {
            super(stmt);
        }

        @Override
        protected Integer read(ResultSet rs) throws SQLException {
            // column does not exist
            return rs.getInt(1234);
        }
    }
}

