/**
 * Copyright 2002-2018 the original author or authors.
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
package org.springframework.jdbc.core;


import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.dao.TypeMismatchDataAccessException;


/**
 * Tests for {@link SingleColumnRowMapper}.
 *
 * @author Kazuki Shimizu
 * @since 5.0.4
 */
public class SingleColumnRowMapperTests {
    // SPR-16483
    @Test
    public void useDefaultConversionService() throws SQLException {
        Timestamp timestamp = new Timestamp(0);
        SingleColumnRowMapper<LocalDateTime> rowMapper = SingleColumnRowMapper.newInstance(LocalDateTime.class);
        ResultSet resultSet = Mockito.mock(ResultSet.class);
        ResultSetMetaData metaData = Mockito.mock(ResultSetMetaData.class);
        BDDMockito.given(metaData.getColumnCount()).willReturn(1);
        BDDMockito.given(resultSet.getMetaData()).willReturn(metaData);
        BDDMockito.given(resultSet.getObject(1, LocalDateTime.class)).willThrow(new SQLFeatureNotSupportedException());
        BDDMockito.given(resultSet.getTimestamp(1)).willReturn(timestamp);
        LocalDateTime actualLocalDateTime = rowMapper.mapRow(resultSet, 1);
        Assert.assertEquals(timestamp.toLocalDateTime(), actualLocalDateTime);
    }

    // SPR-16483
    @Test
    public void useCustomConversionService() throws SQLException {
        Timestamp timestamp = new Timestamp(0);
        DefaultConversionService myConversionService = new DefaultConversionService();
        myConversionService.addConverter(Timestamp.class, SingleColumnRowMapperTests.MyLocalDateTime.class, ( source) -> new org.springframework.jdbc.core.MyLocalDateTime(source.toLocalDateTime()));
        SingleColumnRowMapper<SingleColumnRowMapperTests.MyLocalDateTime> rowMapper = SingleColumnRowMapper.newInstance(SingleColumnRowMapperTests.MyLocalDateTime.class, myConversionService);
        ResultSet resultSet = Mockito.mock(ResultSet.class);
        ResultSetMetaData metaData = Mockito.mock(ResultSetMetaData.class);
        BDDMockito.given(metaData.getColumnCount()).willReturn(1);
        BDDMockito.given(resultSet.getMetaData()).willReturn(metaData);
        BDDMockito.given(resultSet.getObject(1, SingleColumnRowMapperTests.MyLocalDateTime.class)).willThrow(new SQLFeatureNotSupportedException());
        BDDMockito.given(resultSet.getObject(1)).willReturn(timestamp);
        SingleColumnRowMapperTests.MyLocalDateTime actualMyLocalDateTime = rowMapper.mapRow(resultSet, 1);
        Assert.assertNotNull(actualMyLocalDateTime);
        Assert.assertEquals(timestamp.toLocalDateTime(), actualMyLocalDateTime.value);
    }

    // SPR-16483
    @Test(expected = TypeMismatchDataAccessException.class)
    public void doesNotUseConversionService() throws SQLException {
        SingleColumnRowMapper<LocalDateTime> rowMapper = SingleColumnRowMapper.newInstance(LocalDateTime.class, null);
        ResultSet resultSet = Mockito.mock(ResultSet.class);
        ResultSetMetaData metaData = Mockito.mock(ResultSetMetaData.class);
        BDDMockito.given(metaData.getColumnCount()).willReturn(1);
        BDDMockito.given(resultSet.getMetaData()).willReturn(metaData);
        BDDMockito.given(resultSet.getObject(1, LocalDateTime.class)).willThrow(new SQLFeatureNotSupportedException());
        BDDMockito.given(resultSet.getTimestamp(1)).willReturn(new Timestamp(0));
        rowMapper.mapRow(resultSet, 1);
    }

    private static class MyLocalDateTime {
        private final LocalDateTime value;

        public MyLocalDateTime(LocalDateTime value) {
            this.value = value;
        }
    }
}

