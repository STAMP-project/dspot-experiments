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
package org.springframework.boot.jdbc.metadata;


import org.junit.Test;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;


/**
 * Abstract base class for {@link DataSourcePoolMetadata} tests.
 *
 * @param <D>
 * 		the data source pool metadata type
 * @author Stephane Nicoll
 */
public abstract class AbstractDataSourcePoolMetadataTests<D extends AbstractDataSourcePoolMetadata<?>> {
    @Test
    public void getMaxPoolSize() {
        assertThat(getMax()).isEqualTo(Integer.valueOf(2));
    }

    @Test
    public void getMinPoolSize() {
        assertThat(getMin()).isEqualTo(Integer.valueOf(0));
    }

    @Test
    public void getPoolSizeNoConnection() {
        // Make sure the pool is initialized
        JdbcTemplate jdbcTemplate = new JdbcTemplate(getDataSource());
        jdbcTemplate.execute(((ConnectionCallback<Void>) (( connection) -> null)));
        assertThat(getActive()).isEqualTo(Integer.valueOf(0));
        assertThat(getUsage()).isEqualTo(Float.valueOf(0));
    }

    @Test
    public void getPoolSizeOneConnection() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(getDataSource());
        jdbcTemplate.execute(((ConnectionCallback<Void>) (( connection) -> {
            assertThat(getActive()).isEqualTo(Integer.valueOf(1));
            assertThat(getUsage()).isEqualTo(Float.valueOf(0.5F));
            return null;
        })));
    }

    @Test
    public void getPoolSizeTwoConnections() {
        final JdbcTemplate jdbcTemplate = new JdbcTemplate(getDataSource());
        jdbcTemplate.execute(((ConnectionCallback<Void>) (( connection) -> {
            jdbcTemplate.execute(((ConnectionCallback<Void>) (( connection1) -> {
                assertThat(getActive()).isEqualTo(2);
                assertThat(getUsage()).isEqualTo(1.0F);
                return null;
            })));
            return null;
        })));
    }
}

