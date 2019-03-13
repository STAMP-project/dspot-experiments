/**
 * Copyright 2012-2017 the original author or authors.
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


/**
 * Tests for {@link CommonsDbcp2DataSourcePoolMetadata}.
 *
 * @author Stephane Nicoll
 */
public class CommonsDbcp2DataSourcePoolMetadataTests extends AbstractDataSourcePoolMetadataTests<CommonsDbcp2DataSourcePoolMetadata> {
    private CommonsDbcp2DataSourcePoolMetadata dataSourceMetadata;

    @Test
    public void getPoolUsageWithNoCurrent() {
        CommonsDbcp2DataSourcePoolMetadata dsm = new CommonsDbcp2DataSourcePoolMetadata(createDataSource()) {
            @Override
            public Integer getActive() {
                return null;
            }
        };
        assertThat(dsm.getUsage()).isNull();
    }

    @Test
    public void getPoolUsageWithNoMax() {
        CommonsDbcp2DataSourcePoolMetadata dsm = new CommonsDbcp2DataSourcePoolMetadata(createDataSource()) {
            @Override
            public Integer getMax() {
                return null;
            }
        };
        assertThat(dsm.getUsage()).isNull();
    }

    @Test
    public void getPoolUsageWithUnlimitedPool() {
        DataSourcePoolMetadata unlimitedDataSource = createDataSourceMetadata(0, (-1));
        assertThat(unlimitedDataSource.getUsage()).isEqualTo(Float.valueOf((-1.0F)));
    }
}

