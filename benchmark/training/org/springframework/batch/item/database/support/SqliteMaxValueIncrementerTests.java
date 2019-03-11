/**
 * Copyright 2014 the original author or authors.
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
package org.springframework.batch.item.database.support;


import org.junit.Assert;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;


/**
 *
 *
 * @author Luke Taylor
 */
public class SqliteMaxValueIncrementerTests {
    static String dbFile;

    static SimpleDriverDataSource dataSource;

    static JdbcTemplate template;

    @Test
    public void testNextKey() throws Exception {
        SqliteMaxValueIncrementer mvi = new SqliteMaxValueIncrementer(SqliteMaxValueIncrementerTests.dataSource, "max_value", "id");
        Assert.assertEquals(1, mvi.getNextKey());
        Assert.assertEquals(2, mvi.getNextKey());
        Assert.assertEquals(3, mvi.getNextKey());
        Assert.assertEquals(1, SqliteMaxValueIncrementerTests.template.queryForObject("select count(*) from max_value", Integer.class).intValue());
    }
}

