/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to you under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.calcite.adapter.os;


import Hook.STANDARD_STREAMS;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.function.Consumer;
import org.apache.calcite.runtime.Hook;
import org.apache.calcite.util.Holder;
import org.apache.calcite.util.TestUtil;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;


/**
 * Unit tests for the OS (operating system) adapter.
 *
 * <p>Also please run the following tests manually, from your shell:
 *
 * <ul>
 *   <li>./sqlsh select \* from du
 *   <li>./sqlsh select \* from files
 *   <li>./sqlsh select \* from git_commits
 *   <li>./sqlsh select \* from ps
 *   <li>(echo cats; echo and dogs) | ./sqlsh select \* from stdin
 *   <li>./sqlsh select \* from vmstat
 * </ul>
 */
public class OsAdapterTest {
    @Test
    public void testDu() {
        Assume.assumeFalse("Skip: the 'du' table does not work on Windows", OsAdapterTest.isWindows());
        OsAdapterTest.assumeToolExists("du");
        OsAdapterTest.sql("select * from du").returns(( r) -> {
            try {
                assertThat(r.next(), is(true));
                assertThat(r.getInt(1), notNullValue());
                assertThat(r.getString(2), CoreMatchers.startsWith("./"));
                assertThat(r.wasNull(), is(false));
            } catch ( e) {
                throw TestUtil.rethrow(e);
            }
        });
    }

    @Test
    public void testDuFilterSortLimit() {
        Assume.assumeFalse("Skip: the 'du' table does not work on Windows", OsAdapterTest.isWindows());
        OsAdapterTest.assumeToolExists("du");
        OsAdapterTest.sql(("select * from du where path like \'%/src/test/java/%\'\n" + "order by 1 limit 2")).returns(( r) -> {
            try {
                assertThat(r.next(), is(true));
                assertThat(r.getInt(1), notNullValue());
                assertThat(r.getString(2), CoreMatchers.startsWith("./"));
                assertThat(r.wasNull(), is(false));
                assertThat(r.next(), is(true));
                assertThat(r.next(), is(false));// because of "limit 2"

            } catch ( e) {
                throw TestUtil.rethrow(e);
            }
        });
    }

    @Test
    public void testFiles() {
        Assume.assumeFalse("Skip: the 'files' table does not work on Windows", OsAdapterTest.isWindows());
        OsAdapterTest.sql("select distinct type from files").returnsUnordered("type=d", "type=f");
    }

    @Test
    public void testPs() {
        Assume.assumeFalse("Skip: the 'ps' table does not work on Windows", OsAdapterTest.isWindows());
        OsAdapterTest.assumeToolExists("ps");
        OsAdapterTest.sql("select * from ps").returns(( r) -> {
            try {
                assertThat(r.next(), is(true));
                final StringBuilder b = new StringBuilder();
                final int c = r.getMetaData().getColumnCount();
                for (int i = 0; i < c; i++) {
                    b.append(r.getString((i + 1))).append(';');
                    assertThat(r.wasNull(), is(false));
                }
                assertThat(b.toString(), notNullValue());
            } catch ( e) {
                throw TestUtil.rethrow(e);
            }
        });
    }

    @Test
    public void testPsDistinct() {
        Assume.assumeFalse("Skip: the 'ps' table does not work on Windows", OsAdapterTest.isWindows());
        OsAdapterTest.assumeToolExists("ps");
        OsAdapterTest.sql("select distinct `user` from ps").returns(( r) -> {
            try {
                assertThat(r.next(), is(true));
                assertThat(r.getString(1), notNullValue());
                assertThat(r.wasNull(), is(false));
            } catch ( e) {
                throw TestUtil.rethrow(e);
            }
        });
    }

    @Test
    public void testGitCommits() {
        Assume.assumeTrue("no git", OsAdapterTest.hasGit());
        OsAdapterTest.sql("select count(*) from git_commits").returns(( r) -> {
            try {
                assertThat(r.next(), is(true));
                assertThat(r.getString(1), notNullValue());
                assertThat(r.wasNull(), is(false));
            } catch ( e) {
                throw TestUtil.rethrow(e);
            }
        });
    }

    @Test
    public void testGitCommitsTop() {
        Assume.assumeTrue("no git", OsAdapterTest.hasGit());
        final String q = "select author from git_commits\n" + "group by 1 order by count(*) desc limit 2";
        OsAdapterTest.sql(q).returnsUnordered("author=Julian Hyde <julianhyde@gmail.com>", "author=Julian Hyde <jhyde@apache.org>");
    }

    @Test
    public void testVmstat() {
        Assume.assumeFalse("Skip: the 'files' table does not work on Windows", OsAdapterTest.isWindows());
        OsAdapterTest.assumeToolExists("vmstat");
        OsAdapterTest.sql("select * from vmstat").returns(( r) -> {
            try {
                assertThat(r.next(), is(true));
                final int c = r.getMetaData().getColumnCount();
                for (int i = 0; i < c; i++) {
                    assertThat(r.getLong((i + 1)), notNullValue());
                    assertThat(r.wasNull(), is(false));
                }
            } catch ( e) {
                throw TestUtil.rethrow(e);
            }
        });
    }

    @Test
    public void testStdin() throws SQLException {
        try (Hook.Closeable ignore = STANDARD_STREAMS.addThread(((Consumer<Holder<Object[]>>) (( o) -> {
            final Object[] values = o.get();
            final InputStream in = ((InputStream) (values[0]));
            final String s = "First line\n" + "Second line";
            final ByteArrayInputStream in2 = new ByteArrayInputStream(s.getBytes(StandardCharsets.UTF_8));
            final OutputStream out = ((OutputStream) (values[1]));
            final OutputStream err = ((OutputStream) (values[2]));
            o.set(new Object[]{ in2, out, err });
        })))) {
            Assert.assertThat(foo("select count(*) as c from stdin"), CoreMatchers.is("2\n"));
        }
    }

    @Test
    public void testStdinExplain() {
        // Can't execute stdin, because junit's stdin never ends;
        // so just run explain
        final String explain = "PLAN=" + ((("EnumerableAggregate(group=[{}], c=[COUNT()])\n" + "  EnumerableTableFunctionScan(invocation=[stdin(true)], ") + "rowType=[RecordType(INTEGER ordinal, VARCHAR line)], ") + "elementType=[class [Ljava.lang.Object;])");
        OsAdapterTest.sql("select count(*) as c from stdin").explainContains(explain);
    }

    @Test
    public void testSqlShellFormat() throws SQLException {
        final String q = "select * from (values (-1, true, 'a')," + (" (2, false, 'b, c')," + " (3, unknown, cast(null as char(1)))) as t(x, y, z)");
        final String empty = q + " where false";
        final String spacedOut = "-1 true a   \n" + ("2 false b, c\n" + "3 null null\n");
        Assert.assertThat(foo("-o", "spaced", q), CoreMatchers.is(spacedOut));
        Assert.assertThat(foo("-o", "spaced", empty), CoreMatchers.is(""));
        // default is 'spaced'
        Assert.assertThat(foo(q), CoreMatchers.is(spacedOut));
        final String headersOut = "x y z\n" + spacedOut;
        Assert.assertThat(foo("-o", "headers", q), CoreMatchers.is(headersOut));
        final String headersEmptyOut = "x y z\n";
        Assert.assertThat(foo("-o", "headers", empty), CoreMatchers.is(headersEmptyOut));
        final String jsonOut = "[\n" + ((((((((((((((("{\n" + "  \"x\": -1,\n") + "  \"y\": true,\n") + "  \"z\": \"a   \"\n") + "},\n") + "{\n") + "  \"x\": 2,\n") + "  \"y\": false,\n") + "  \"z\": \"b, c\"\n") + "},\n") + "{\n") + "  \"x\": 3,\n") + "  \"y\": null,\n") + "  \"z\": null\n") + "}\n") + "]\n");
        Assert.assertThat(foo("-o", "json", q), CoreMatchers.is(jsonOut));
        final String jsonEmptyOut = "[\n" + "]\n";
        Assert.assertThat(foo("-o", "json", empty), CoreMatchers.is(jsonEmptyOut));
        final String csvEmptyOut = "[\n" + "]\n";
        Assert.assertThat(foo("-o", "json", empty), CoreMatchers.is(csvEmptyOut));
        final String csvOut = "x,y,z\n" + (("-1,true,a   \n" + "2,false,\"b, c\"\n") + "3,,");
        Assert.assertThat(foo("-o", "csv", q), CoreMatchers.is(csvOut));
        final String mysqlOut = "" + (((((((("+----+-------+------+\n" + "|  x | y     | z    |\n") + "+----+-------+------+\n") + "| -1 | true  | a    |\n") + "|  2 | false | b, c |\n") + "|  3 |       |      |\n") + "+----+-------+------+\n") + "(3 rows)\n") + "\n");
        Assert.assertThat(foo("-o", "mysql", q), CoreMatchers.is(mysqlOut));
        final String mysqlEmptyOut = "" + ((((("+---+---+---+\n" + "| x | y | z |\n") + "+---+---+---+\n") + "+---+---+---+\n") + "(0 rows)\n") + "\n");
        Assert.assertThat(foo("-o", "mysql", empty), CoreMatchers.is(mysqlEmptyOut));
    }

    @Test
    public void testSqlShellHelp() throws SQLException {
        final String help = "Usage: sqlsh [OPTION]... SQL\n" + (((((("Execute a SQL command\n" + "\n") + "Options:\n") + "  -o FORMAT  Print output in FORMAT; options are 'spaced' (the ") + "default), \'csv\',\n") + "             \'headers\', \'json\', \'mysql\'\n") + "  -h --help  Print this help\n");
        final String q = "select 1";
        Assert.assertThat(foo("--help", q), CoreMatchers.is(help));
        Assert.assertThat(foo("-h", q), CoreMatchers.is(help));
        try {
            final String s = foo("-o", "bad", q);
            Assert.fail(("expected exception, got " + s));
        } catch (RuntimeException e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.is("unknown format: bad"));
        }
    }
}

/**
 * End OsAdapterTest.java
 */
