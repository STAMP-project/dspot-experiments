/**
 * Copyright 2016 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.cloud.compute.deprecated;


import Type.STANDARD;
import org.junit.Assert;
import org.junit.Test;


public class StandardDiskConfigurationTest {
    private static final Long SIZE = 42L;

    private static final DiskTypeId DISK_TYPE = DiskTypeId.of("project", "zone", "type");

    private static final StandardDiskConfiguration DISK_CONFIGURATION = StandardDiskConfiguration.newBuilder().setSizeGb(StandardDiskConfigurationTest.SIZE).setDiskType(StandardDiskConfigurationTest.DISK_TYPE).build();

    @Test
    public void testToBuilder() {
        compareStandardDiskConfiguration(StandardDiskConfigurationTest.DISK_CONFIGURATION, StandardDiskConfigurationTest.DISK_CONFIGURATION.toBuilder().build());
        StandardDiskConfiguration diskConfiguration = StandardDiskConfigurationTest.DISK_CONFIGURATION.toBuilder().setSizeGb(24L).build();
        Assert.assertEquals(24L, diskConfiguration.getSizeGb().longValue());
        diskConfiguration = diskConfiguration.toBuilder().setSizeGb(StandardDiskConfigurationTest.SIZE).build();
        compareStandardDiskConfiguration(StandardDiskConfigurationTest.DISK_CONFIGURATION, diskConfiguration);
    }

    @Test
    public void testToBuilderIncomplete() {
        StandardDiskConfiguration diskConfiguration = StandardDiskConfiguration.of(StandardDiskConfigurationTest.DISK_TYPE);
        compareStandardDiskConfiguration(diskConfiguration, diskConfiguration.toBuilder().build());
    }

    @Test
    public void testBuilder() {
        Assert.assertEquals(StandardDiskConfigurationTest.DISK_TYPE, StandardDiskConfigurationTest.DISK_CONFIGURATION.getDiskType());
        Assert.assertEquals(StandardDiskConfigurationTest.SIZE, StandardDiskConfigurationTest.DISK_CONFIGURATION.getSizeGb());
        Assert.assertEquals(STANDARD, StandardDiskConfigurationTest.DISK_CONFIGURATION.getType());
    }

    @Test
    public void testToAndFromPb() {
        Assert.assertTrue(((DiskConfiguration.fromPb(StandardDiskConfigurationTest.DISK_CONFIGURATION.toPb())) instanceof StandardDiskConfiguration));
        compareStandardDiskConfiguration(StandardDiskConfigurationTest.DISK_CONFIGURATION, DiskConfiguration.<StandardDiskConfiguration>fromPb(StandardDiskConfigurationTest.DISK_CONFIGURATION.toPb()));
    }

    @Test
    public void testOf() {
        StandardDiskConfiguration configuration = StandardDiskConfiguration.of(StandardDiskConfigurationTest.DISK_TYPE);
        Assert.assertEquals(StandardDiskConfigurationTest.DISK_TYPE, configuration.getDiskType());
        Assert.assertNull(configuration.getSizeGb());
        Assert.assertEquals(STANDARD, configuration.getType());
        configuration = StandardDiskConfiguration.of(StandardDiskConfigurationTest.DISK_TYPE, StandardDiskConfigurationTest.SIZE);
        Assert.assertEquals(StandardDiskConfigurationTest.DISK_TYPE, configuration.getDiskType());
        Assert.assertEquals(StandardDiskConfigurationTest.SIZE, configuration.getSizeGb());
        Assert.assertEquals(STANDARD, configuration.getType());
        configuration = StandardDiskConfiguration.of(StandardDiskConfigurationTest.SIZE);
        Assert.assertNull(configuration.getDiskType());
        Assert.assertEquals(StandardDiskConfigurationTest.SIZE, configuration.getSizeGb());
        Assert.assertEquals(STANDARD, configuration.getType());
    }

    @Test
    public void testSetProjectId() {
        StandardDiskConfiguration configuration = StandardDiskConfigurationTest.DISK_CONFIGURATION.toBuilder().setDiskType(DiskTypeId.of(StandardDiskConfigurationTest.DISK_TYPE.getZone(), StandardDiskConfigurationTest.DISK_TYPE.getType())).build();
        compareStandardDiskConfiguration(StandardDiskConfigurationTest.DISK_CONFIGURATION, configuration.setProjectId("project"));
    }
}

