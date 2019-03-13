/**
 * Copyright 2012-2018 the original author or authors.
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
package org.springframework.boot.actuate.cassandra;


import Status.UP;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.querybuilder.Select;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.boot.actuate.health.Health;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.data.cassandra.core.cql.CqlOperations;


/**
 * Tests for {@link CassandraHealthIndicator}.
 *
 * @author Oleksii Bondar
 */
public class CassandraHealthIndicatorTests {
    @Test
    public void createWhenCassandraOperationsIsNullShouldThrowException() {
        assertThatIllegalArgumentException().isThrownBy(() -> new CassandraHealthIndicator(null));
    }

    @Test
    public void verifyHealthStatusWhenExhausted() {
        CassandraOperations cassandraOperations = Mockito.mock(CassandraOperations.class);
        CqlOperations cqlOperations = Mockito.mock(CqlOperations.class);
        ResultSet resultSet = Mockito.mock(ResultSet.class);
        CassandraHealthIndicator healthIndicator = new CassandraHealthIndicator(cassandraOperations);
        BDDMockito.given(cassandraOperations.getCqlOperations()).willReturn(cqlOperations);
        BDDMockito.given(cqlOperations.queryForResultSet(ArgumentMatchers.any(Select.class))).willReturn(resultSet);
        BDDMockito.given(resultSet.isExhausted()).willReturn(true);
        Health health = healthIndicator.health();
        assertThat(health.getStatus()).isEqualTo(UP);
    }

    @Test
    public void verifyHealthStatusWithVersion() {
        CassandraOperations cassandraOperations = Mockito.mock(CassandraOperations.class);
        CqlOperations cqlOperations = Mockito.mock(CqlOperations.class);
        ResultSet resultSet = Mockito.mock(ResultSet.class);
        Row row = Mockito.mock(Row.class);
        CassandraHealthIndicator healthIndicator = new CassandraHealthIndicator(cassandraOperations);
        BDDMockito.given(cassandraOperations.getCqlOperations()).willReturn(cqlOperations);
        BDDMockito.given(cqlOperations.queryForResultSet(ArgumentMatchers.any(Select.class))).willReturn(resultSet);
        BDDMockito.given(resultSet.isExhausted()).willReturn(false);
        BDDMockito.given(resultSet.one()).willReturn(row);
        String expectedVersion = "1.0.0";
        BDDMockito.given(row.getString(0)).willReturn(expectedVersion);
        Health health = healthIndicator.health();
        assertThat(health.getStatus()).isEqualTo(UP);
        assertThat(health.getDetails().get("version")).isEqualTo(expectedVersion);
    }
}

