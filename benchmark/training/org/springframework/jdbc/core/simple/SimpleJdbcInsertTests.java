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
package org.springframework.jdbc.core.simple;


import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.HashMap;
import javax.sql.DataSource;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.dao.InvalidDataAccessApiUsageException;


/**
 * Mock object based tests for SimpleJdbcInsert.
 *
 * @author Thomas Risberg
 */
public class SimpleJdbcInsertTests {
    private Connection connection;

    private DatabaseMetaData databaseMetaData;

    private DataSource dataSource;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testNoSuchTable() throws Exception {
        ResultSet resultSet = Mockito.mock(ResultSet.class);
        BDDMockito.given(resultSet.next()).willReturn(false);
        BDDMockito.given(databaseMetaData.getDatabaseProductName()).willReturn("MyDB");
        BDDMockito.given(databaseMetaData.getDatabaseProductName()).willReturn("MyDB");
        BDDMockito.given(databaseMetaData.getDatabaseProductVersion()).willReturn("1.0");
        BDDMockito.given(databaseMetaData.getUserName()).willReturn("me");
        BDDMockito.given(databaseMetaData.storesLowerCaseIdentifiers()).willReturn(true);
        BDDMockito.given(databaseMetaData.getTables(null, null, "x", null)).willReturn(resultSet);
        SimpleJdbcInsert insert = new SimpleJdbcInsert(dataSource).withTableName("x");
        // Shouldn't succeed in inserting into table which doesn't exist
        thrown.expect(InvalidDataAccessApiUsageException.class);
        try {
            insert.execute(new HashMap());
        } finally {
            Mockito.verify(resultSet).close();
        }
    }
}

