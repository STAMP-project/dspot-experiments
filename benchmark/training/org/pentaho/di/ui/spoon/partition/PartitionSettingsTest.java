/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2017 by Hitachi Vantara : http://www.pentaho.com
 *
 * ******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ****************************************************************************
 */
package org.pentaho.di.ui.spoon.partition;


import StepPartitioningMeta.methodCodes;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.pentaho.di.core.plugins.PluginInterface;
import org.pentaho.di.partition.PartitionSchema;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepPartitioningMeta;
import org.pentaho.di.ui.spoon.PartitionSchemasProvider;


/**
 *
 *
 * @author Evgeniy_Lyakhov@epam.com
 */
public class PartitionSettingsTest {
    private TransMeta transMeta;

    private StepMeta stepMeta;

    private PartitionSchemasProvider partitionSchemasProvider;

    private int length;

    private PartitionSettings settings;

    private PluginInterface plugin;

    @Test
    public void codesArePickedUpFromPlugins() {
        PartitionSettings settings = new PartitionSettings(methodCodes.length, transMeta, stepMeta, partitionSchemasProvider);
        Assert.assertTrue(Arrays.equals(methodCodes, settings.getCodes()));
    }

    @Test
    public void pluginsCodesAreGathered() {
        settings.fillOptionsAndCodesByPlugins(Collections.singletonList(plugin));
        Assert.assertEquals("qwerty", settings.getCodes()[((length) - 1)]);
    }

    @Test
    public void codeIsFoundByDescription() {
        settings.fillOptionsAndCodesByPlugins(Collections.singletonList(plugin));
        Assert.assertEquals("qwerty", settings.getMethodByMethodDescription("asdfg"));
    }

    @Test
    public void codeOfNoneIsReturnedWhenNotFoundByDescription() {
        settings.fillOptionsAndCodesByPlugins(Collections.singletonList(plugin));
        Assert.assertEquals(StepPartitioningMeta.methodCodes[StepPartitioningMeta.PARTITIONING_METHOD_NONE], settings.getMethodByMethodDescription("qwerty"));
    }

    @Test
    public void defaultSelectedSchemaIndexIsFoundBySchemaName() throws Exception {
        PartitionSchema schema = new PartitionSchema("qwerty", Collections.<String>emptyList());
        StepPartitioningMeta meta = Mockito.mock(StepPartitioningMeta.class);
        Mockito.when(meta.getPartitionSchema()).thenReturn(schema);
        Mockito.when(stepMeta.getStepPartitioningMeta()).thenReturn(meta);
        List<String> schemas = Arrays.asList("1", plugin.getName(), "2");
        Mockito.when(partitionSchemasProvider.getPartitionSchemasNames(ArgumentMatchers.any(TransMeta.class))).thenReturn(schemas);
        Assert.assertEquals(1, settings.getDefaultSelectedSchemaIndex());
    }

    @Test
    public void defaultSelectedSchemaIndexWhenSchemaNameIsNotDefined() throws Exception {
        PartitionSchema schema = new PartitionSchema();
        StepPartitioningMeta meta = Mockito.mock(StepPartitioningMeta.class);
        Mockito.when(meta.getPartitionSchema()).thenReturn(schema);
        Mockito.when(stepMeta.getStepPartitioningMeta()).thenReturn(meta);
        List<String> schemas = Arrays.asList("test");
        Mockito.when(partitionSchemasProvider.getPartitionSchemasNames(ArgumentMatchers.any(TransMeta.class))).thenReturn(schemas);
        Assert.assertEquals(0, settings.getDefaultSelectedSchemaIndex());
    }

    @Test
    public void defaultSelectedSchemaIndexIsNilWhenNotFoundBySchemaName() throws Exception {
        PartitionSchema schema = new PartitionSchema("asdfg", Collections.<String>emptyList());
        StepPartitioningMeta meta = Mockito.mock(StepPartitioningMeta.class);
        Mockito.when(meta.getPartitionSchema()).thenReturn(schema);
        Mockito.when(stepMeta.getStepPartitioningMeta()).thenReturn(meta);
        List<String> schemas = Arrays.asList("1", plugin.getName(), "2");
        Mockito.when(partitionSchemasProvider.getPartitionSchemasNames(ArgumentMatchers.any(TransMeta.class))).thenReturn(schemas);
        Assert.assertEquals(0, settings.getDefaultSelectedSchemaIndex());
    }

    @Test
    public void metaIsUpdated() {
        PartitionSchema schema = new PartitionSchema("1", Collections.<String>emptyList());
        StepPartitioningMeta meta = Mockito.mock(StepPartitioningMeta.class);
        Mockito.when(stepMeta.getStepPartitioningMeta()).thenReturn(meta);
        settings.updateSchema(schema);
        Mockito.verify(meta).setPartitionSchema(schema);
    }

    @Test
    public void metaIsNotUpdatedWithNull() {
        StepPartitioningMeta meta = Mockito.mock(StepPartitioningMeta.class);
        Mockito.when(stepMeta.getStepPartitioningMeta()).thenReturn(meta);
        settings.updateSchema(null);
        Mockito.verify(meta, Mockito.never()).setPartitionSchema(ArgumentMatchers.any(PartitionSchema.class));
    }

    @Test
    public void metaIsNotUpdatedWithNameless() {
        PartitionSchema schema = new PartitionSchema(null, Collections.<String>emptyList());
        StepPartitioningMeta meta = Mockito.mock(StepPartitioningMeta.class);
        Mockito.when(stepMeta.getStepPartitioningMeta()).thenReturn(meta);
        settings.updateSchema(null);
        Mockito.verify(meta, Mockito.never()).setPartitionSchema(ArgumentMatchers.any(PartitionSchema.class));
    }
}

