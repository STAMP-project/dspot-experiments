/**
 * Copyright 2002-2015 the original author or authors.
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
package org.springframework.jdbc.support;


import org.junit.Assert;
import org.junit.Test;


/**
 * Unit tests for {@link JdbcUtils}.
 *
 * @author Thomas Risberg
 */
public class JdbcUtilsTests {
    @Test
    public void commonDatabaseName() {
        Assert.assertEquals("Oracle", JdbcUtils.commonDatabaseName("Oracle"));
        Assert.assertEquals("DB2", JdbcUtils.commonDatabaseName("DB2-for-Spring"));
        Assert.assertEquals("Sybase", JdbcUtils.commonDatabaseName("Sybase SQL Server"));
        Assert.assertEquals("Sybase", JdbcUtils.commonDatabaseName("Adaptive Server Enterprise"));
        Assert.assertEquals("MySQL", JdbcUtils.commonDatabaseName("MySQL"));
    }

    @Test
    public void convertUnderscoreNameToPropertyName() {
        Assert.assertEquals("myName", JdbcUtils.convertUnderscoreNameToPropertyName("MY_NAME"));
        Assert.assertEquals("yourName", JdbcUtils.convertUnderscoreNameToPropertyName("yOUR_nAME"));
        Assert.assertEquals("AName", JdbcUtils.convertUnderscoreNameToPropertyName("a_name"));
        Assert.assertEquals("someoneElsesName", JdbcUtils.convertUnderscoreNameToPropertyName("someone_elses_name"));
    }
}

