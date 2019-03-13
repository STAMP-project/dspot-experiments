/**
 * -
 * #%L
 * rapidoid-integration-tests
 * %%
 * Copyright (C) 2014 - 2018 Nikolche Mihajlovski and contributors
 * %%
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
 * #L%
 */
package org.rapidoid.jdbc;


import Conf.ROOT;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.http.IsolatedIntegrationTest;
import org.rapidoid.u.U;


@Authors("Nikolche Mihajlovski")
@Since("4.1.0")
public class JDBCPoolC3P0Test extends IsolatedIntegrationTest {
    @Test
    public void testJDBCPoolC3P0() {
        JDBC.h2("test1");
        JdbcClient jdbc = JDBC.api().poolProvider("c3p0").init();
        ComboPooledDataSource c3p0 = ((ComboPooledDataSource) (jdbc.dataSource()));
        JDBC.execute("create table abc (id int, name varchar)");
        JDBC.execute("insert into abc values (?, ?)", 123, "xyz");
        final Map<String, ?> expected = U.map("id", 123, "name", "xyz");
        runBenchmark(expected);
    }

    @Test
    public void testJDBCWithTextConfig() {
        Conf.JDBC.set("poolProvider", "c3p0");
        Conf.JDBC.set("driver", "org.h2.Driver");
        Conf.JDBC.set("url", "jdbc:h2:mem:mydb");
        Conf.JDBC.set("username", "sa");
        ROOT.set("c3p0.minPoolSize", 5);
        ROOT.set("c3p0.maxPoolSize", "123");
        JdbcClient jdbc = JDBC.api();
        eq(jdbc.driver(), "org.h2.Driver");
        ComboPooledDataSource c3p0 = ((ComboPooledDataSource) (jdbc.init().dataSource()));
        eq(c3p0.getMinPoolSize(), 5);
        eq(c3p0.getMaxPoolSize(), 123);
        JDBC.execute("create table abc (id int, name varchar)");
        JDBC.execute("insert into abc values (?, ?)", 123, "xyz");
        final Map<String, ?> expected = U.map("id", 123, "name", "xyz");
        runBenchmark(expected);
    }
}

