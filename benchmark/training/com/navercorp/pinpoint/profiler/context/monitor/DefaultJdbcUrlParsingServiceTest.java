/**
 * Copyright 2017 NAVER Corp.
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
package com.navercorp.pinpoint.profiler.context.monitor;


import ServiceType.TEST;
import ServiceType.UNKNOWN_DB;
import com.navercorp.pinpoint.bootstrap.context.DatabaseInfo;
import com.navercorp.pinpoint.bootstrap.plugin.jdbc.JdbcUrlParserV2;
import com.navercorp.pinpoint.bootstrap.plugin.jdbc.UnKnownDatabaseInfo;
import com.navercorp.pinpoint.common.trace.ServiceType;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Taejin Koo
 */
public class DefaultJdbcUrlParsingServiceTest {
    private static final String MYSQL_NORMALIZED_URL = "jdbc:mysql://ip_address:3306/database_name";

    private static final String MYSQL_JDBC_URL = (DefaultJdbcUrlParsingServiceTest.MYSQL_NORMALIZED_URL) + "?useUnicode=yes&amp;characterEncoding=UTF-8";

    private final JdbcUrlParserV2 jdbcUrlParser = new DefaultJdbcUrlParsingServiceTest.MockJdbcUrlParser();

    @Test
    public void cacheTest1() throws Exception {
        JdbcUrlParsingService jdbcUrlParsingService = new DefaultJdbcUrlParsingService(Arrays.asList(jdbcUrlParser));
        DatabaseInfo databaseInfo = jdbcUrlParsingService.getDatabaseInfo(DefaultJdbcUrlParsingServiceTest.MYSQL_JDBC_URL);
        Assert.assertNull(databaseInfo);
        DatabaseInfo parsingResult = jdbcUrlParsingService.parseJdbcUrl(TEST, DefaultJdbcUrlParsingServiceTest.MYSQL_JDBC_URL);
        Assert.assertTrue(parsingResult.isParsingComplete());
        DatabaseInfo cache1 = jdbcUrlParsingService.getDatabaseInfo(DefaultJdbcUrlParsingServiceTest.MYSQL_JDBC_URL);
        DatabaseInfo cache2 = jdbcUrlParsingService.getDatabaseInfo(DefaultJdbcUrlParsingServiceTest.MYSQL_JDBC_URL);
        Assert.assertTrue(((parsingResult == cache1) && (parsingResult == cache2)));
    }

    @Test
    public void cacheTest2() throws Exception {
        JdbcUrlParsingService jdbcUrlParsingService = new DefaultJdbcUrlParsingService(Arrays.asList(jdbcUrlParser));
        DatabaseInfo parsingResult = jdbcUrlParsingService.parseJdbcUrl(TEST, DefaultJdbcUrlParsingServiceTest.MYSQL_JDBC_URL);
        Assert.assertTrue(parsingResult.isParsingComplete());
        DatabaseInfo cache1 = jdbcUrlParsingService.getDatabaseInfo(TEST, DefaultJdbcUrlParsingServiceTest.MYSQL_JDBC_URL);
        DatabaseInfo cache2 = jdbcUrlParsingService.getDatabaseInfo(UNKNOWN_DB, DefaultJdbcUrlParsingServiceTest.MYSQL_JDBC_URL);
        Assert.assertNotEquals(cache1, cache2);
    }

    private static class MockJdbcUrlParser implements JdbcUrlParserV2 {
        @Override
        public DatabaseInfo parse(String url) {
            if (DefaultJdbcUrlParsingServiceTest.MYSQL_JDBC_URL.equals(url)) {
                DatabaseInfo dbInfo = new com.navercorp.pinpoint.bootstrap.plugin.jdbc.DefaultDatabaseInfo(ServiceType.UNKNOWN_DB, ServiceType.UNKNOWN_DB_EXECUTE_QUERY, DefaultJdbcUrlParsingServiceTest.MYSQL_JDBC_URL, DefaultJdbcUrlParsingServiceTest.MYSQL_NORMALIZED_URL, Arrays.asList("ip_address:3306"), "database_name");
                return dbInfo;
            }
            return UnKnownDatabaseInfo.createUnknownDataBase(url);
        }

        @Override
        public ServiceType getServiceType() {
            return ServiceType.TEST;
        }
    }
}

