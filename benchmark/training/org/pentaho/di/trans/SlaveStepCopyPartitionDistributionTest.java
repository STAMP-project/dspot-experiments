/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2018 by Hitachi Vantara : http://www.pentaho.com
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
package org.pentaho.di.trans;


import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.pentaho.di.partition.PartitionSchema;


public class SlaveStepCopyPartitionDistributionTest {
    private SlaveStepCopyPartitionDistribution slaveStep;

    @Test
    public void equalsNullTest() {
        Assert.assertFalse(slaveStep.equals(null));
    }

    @Test
    public void equalsDifferentClassesTest() {
        Assert.assertFalse(slaveStep.equals(Integer.valueOf(5)));
    }

    @Test
    public void equalsSameInstanceTest() {
        Assert.assertTrue(slaveStep.equals(slaveStep));
    }

    @Test
    public void equalsDifferentStepsTest() {
        SlaveStepCopyPartitionDistribution other = new SlaveStepCopyPartitionDistribution();
        List<PartitionSchema> schemas = new ArrayList<>();
        schemas.add(new PartitionSchema());
        other.setOriginalPartitionSchemas(schemas);
        Assert.assertFalse(slaveStep.equals(other));
    }

    @Test
    public void equalsTest() {
        Assert.assertTrue(slaveStep.equals(new SlaveStepCopyPartitionDistribution()));
    }

    @Test
    public void hashCodeEqualsTest() {
        SlaveStepCopyPartitionDistribution other = new SlaveStepCopyPartitionDistribution();
        Assert.assertEquals(slaveStep.hashCode(), other.hashCode());
    }

    @Test
    public void hashCodeDifferentTest() {
        SlaveStepCopyPartitionDistribution other = new SlaveStepCopyPartitionDistribution();
        List<PartitionSchema> schemas = new ArrayList<>();
        PartitionSchema schema = new PartitionSchema();
        schema.setName("Test");
        schemas.add(schema);
        other.setOriginalPartitionSchemas(schemas);
        Assert.assertNotEquals(slaveStep.hashCode(), other.hashCode());
    }
}

