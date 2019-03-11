/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.beam.sdk.io.gcp.bigtable;


import BigtableOptions.Builder;
import ValueProvider.StaticValueProvider;
import com.google.cloud.bigtable.config.BigtableOptions;
import com.google.cloud.bigtable.config.BulkOptions;
import org.apache.beam.sdk.extensions.gcp.options.GcpOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.options.ValueProvider;
import org.apache.beam.sdk.transforms.SerializableFunction;
import org.apache.beam.sdk.transforms.display.DisplayData;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;


/**
 * Unit tests for {@link BigtableConfig}.
 */
@RunWith(JUnit4.class)
public class BigtableConfigTest {
    static final ValueProvider<String> NOT_ACCESSIBLE_VALUE = new ValueProvider<String>() {
        @Override
        public String get() {
            throw new IllegalStateException("Value is not accessible");
        }

        @Override
        public boolean isAccessible() {
            return false;
        }
    };

    static final ValueProvider<String> PROJECT_ID = StaticValueProvider.of("project_id");

    static final ValueProvider<String> INSTANCE_ID = StaticValueProvider.of("instance_id");

    static final ValueProvider<String> TABLE_ID = StaticValueProvider.of("table");

    static final SerializableFunction<BigtableOptions.Builder, BigtableOptions.Builder> CONFIGURATOR = ((SerializableFunction<BigtableOptions.Builder, BigtableOptions.Builder>) (( input) -> input));

    static final BigtableService SERVICE = Mockito.mock(BigtableService.class);

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private BigtableConfig config;

    @Test
    public void testWithProjectId() {
        Assert.assertEquals(BigtableConfigTest.PROJECT_ID.get(), config.withProjectId(BigtableConfigTest.PROJECT_ID).getProjectId().get());
        thrown.expect(IllegalArgumentException.class);
        config.withProjectId(null);
    }

    @Test
    public void testWithInstanceId() {
        Assert.assertEquals(BigtableConfigTest.INSTANCE_ID.get(), config.withInstanceId(BigtableConfigTest.INSTANCE_ID).getInstanceId().get());
        thrown.expect(IllegalArgumentException.class);
        config.withInstanceId(null);
    }

    @Test
    public void testWithTableId() {
        Assert.assertEquals(BigtableConfigTest.TABLE_ID.get(), config.withTableId(BigtableConfigTest.TABLE_ID).getTableId().get());
        thrown.expect(IllegalArgumentException.class);
        config.withTableId(null);
    }

    @Test
    public void testWithBigtableOptionsConfigurator() {
        Assert.assertEquals(BigtableConfigTest.CONFIGURATOR, config.withBigtableOptionsConfigurator(BigtableConfigTest.CONFIGURATOR).getBigtableOptionsConfigurator());
        thrown.expect(IllegalArgumentException.class);
        config.withBigtableOptionsConfigurator(null);
    }

    @Test
    public void testWithValidate() {
        Assert.assertEquals(true, config.withValidate(true).getValidate());
    }

    @Test
    public void testWithBigtableService() {
        Assert.assertEquals(BigtableConfigTest.SERVICE, config.withBigtableService(BigtableConfigTest.SERVICE).getBigtableService());
        thrown.expect(IllegalArgumentException.class);
        config.withBigtableService(null);
    }

    @Test
    public void testValidate() {
        config.withProjectId(BigtableConfigTest.PROJECT_ID).withInstanceId(BigtableConfigTest.INSTANCE_ID).withTableId(BigtableConfigTest.TABLE_ID).validate();
    }

    @Test
    public void testValidateFailsWithoutProjectId() {
        config.withInstanceId(BigtableConfigTest.INSTANCE_ID).withTableId(BigtableConfigTest.TABLE_ID);
        thrown.expect(IllegalArgumentException.class);
        config.validate();
    }

    @Test
    public void testValidateFailsWithoutInstanceId() {
        config.withProjectId(BigtableConfigTest.PROJECT_ID).withTableId(BigtableConfigTest.TABLE_ID);
        thrown.expect(IllegalArgumentException.class);
        config.validate();
    }

    @Test
    public void testValidateFailsWithoutTableId() {
        config.withProjectId(BigtableConfigTest.PROJECT_ID).withInstanceId(BigtableConfigTest.INSTANCE_ID);
        thrown.expect(IllegalArgumentException.class);
        config.validate();
    }

    @Test
    public void testPopulateDisplayData() {
        DisplayData displayData = DisplayData.from(config.withProjectId(BigtableConfigTest.PROJECT_ID).withInstanceId(BigtableConfigTest.INSTANCE_ID).withTableId(BigtableConfigTest.TABLE_ID)::populateDisplayData);
        Assert.assertThat(displayData, hasDisplayItem(Matchers.allOf(hasKey("projectId"), hasLabel("Bigtable Project Id"), hasValue(BigtableConfigTest.PROJECT_ID.get()))));
        Assert.assertThat(displayData, hasDisplayItem(Matchers.allOf(hasKey("instanceId"), hasLabel("Bigtable Instance Id"), hasValue(BigtableConfigTest.INSTANCE_ID.get()))));
        Assert.assertThat(displayData, hasDisplayItem(Matchers.allOf(hasKey("tableId"), hasLabel("Bigtable Table Id"), hasValue(BigtableConfigTest.TABLE_ID.get()))));
    }

    @Test
    public void testGetBigtableServiceWithDefaultService() {
        Assert.assertEquals(BigtableConfigTest.SERVICE, config.withBigtableService(BigtableConfigTest.SERVICE).getBigtableService());
    }

    @Test
    public void testGetBigtableServiceWithConfigurator() {
        SerializableFunction<BigtableOptions.Builder, BigtableOptions.Builder> configurator = ((SerializableFunction<BigtableOptions.Builder, BigtableOptions.Builder>) (( input) -> input.setInstanceId(((INSTANCE_ID.get()) + (INSTANCE_ID.get()))).setProjectId(((PROJECT_ID.get()) + (PROJECT_ID.get()))).setBulkOptions(new BulkOptions.Builder().setUseBulkApi(true).build())));
        BigtableService service = config.withProjectId(BigtableConfigTest.PROJECT_ID).withInstanceId(BigtableConfigTest.INSTANCE_ID).withBigtableOptionsConfigurator(configurator).getBigtableService(PipelineOptionsFactory.as(GcpOptions.class));
        Assert.assertEquals(BigtableConfigTest.PROJECT_ID.get(), service.getBigtableOptions().getProjectId());
        Assert.assertEquals(BigtableConfigTest.INSTANCE_ID.get(), service.getBigtableOptions().getInstanceId());
        Assert.assertEquals(true, service.getBigtableOptions().getBulkOptions().useBulkApi());
    }

    @Test
    public void testIsDataAccessible() {
        Assert.assertTrue(config.withTableId(BigtableConfigTest.TABLE_ID).withProjectId(BigtableConfigTest.PROJECT_ID).withInstanceId(BigtableConfigTest.INSTANCE_ID).isDataAccessible());
        Assert.assertTrue(config.withTableId(BigtableConfigTest.TABLE_ID).withProjectId(BigtableConfigTest.PROJECT_ID).withBigtableOptions(new BigtableOptions.Builder().setInstanceId("instance_id").build()).isDataAccessible());
        Assert.assertTrue(config.withTableId(BigtableConfigTest.TABLE_ID).withInstanceId(BigtableConfigTest.INSTANCE_ID).withBigtableOptions(new BigtableOptions.Builder().setProjectId("project_id").build()).isDataAccessible());
        Assert.assertTrue(config.withTableId(BigtableConfigTest.TABLE_ID).withBigtableOptions(new BigtableOptions.Builder().setProjectId("project_id").setInstanceId("instance_id").build()).isDataAccessible());
        Assert.assertFalse(config.withTableId(BigtableConfigTest.NOT_ACCESSIBLE_VALUE).withProjectId(BigtableConfigTest.PROJECT_ID).withInstanceId(BigtableConfigTest.INSTANCE_ID).isDataAccessible());
        Assert.assertFalse(config.withTableId(BigtableConfigTest.TABLE_ID).withProjectId(BigtableConfigTest.NOT_ACCESSIBLE_VALUE).withInstanceId(BigtableConfigTest.INSTANCE_ID).isDataAccessible());
        Assert.assertFalse(config.withTableId(BigtableConfigTest.TABLE_ID).withProjectId(BigtableConfigTest.PROJECT_ID).withInstanceId(BigtableConfigTest.NOT_ACCESSIBLE_VALUE).isDataAccessible());
    }
}

