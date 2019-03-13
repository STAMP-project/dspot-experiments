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


import ImageConfiguration.SourceType;
import ImageConfiguration.Type.STORAGE;
import StorageImageConfiguration.ContainerType;
import org.junit.Assert;
import org.junit.Test;


public class StorageImageConfigurationTest {
    private static final String SOURCE = "source";

    private static final SourceType SOURCE_TYPE = SourceType.RAW;

    private static final ContainerType CONTAINER_TYPE = ContainerType.TAR;

    private static final Long ARCHIVE_SIZE_BYTES = 42L;

    private static final String SHA1 = "sha1";

    private static final StorageImageConfiguration CONFIGURATION = StorageImageConfiguration.newBuilder(StorageImageConfigurationTest.SOURCE).setSourceType(StorageImageConfigurationTest.SOURCE_TYPE).setContainerType(StorageImageConfigurationTest.CONTAINER_TYPE).setArchiveSizeBytes(StorageImageConfigurationTest.ARCHIVE_SIZE_BYTES).setSha1(StorageImageConfigurationTest.SHA1).build();

    @Test
    public void testToBuilder() {
        compareRawImageConfiguration(StorageImageConfigurationTest.CONFIGURATION, StorageImageConfigurationTest.CONFIGURATION.toBuilder().build());
        String newSource = "newSource";
        StorageImageConfiguration configuration = StorageImageConfigurationTest.CONFIGURATION.toBuilder().setSource(newSource).build();
        Assert.assertEquals(newSource, configuration.getSource());
        configuration = configuration.toBuilder().setSource(StorageImageConfigurationTest.SOURCE).build();
        compareRawImageConfiguration(StorageImageConfigurationTest.CONFIGURATION, configuration);
    }

    @Test
    public void testToBuilderIncomplete() {
        StorageImageConfiguration configuration = StorageImageConfiguration.of(StorageImageConfigurationTest.SOURCE);
        compareRawImageConfiguration(configuration, configuration.toBuilder().build());
    }

    @Test
    public void testBuilder() {
        Assert.assertEquals(StorageImageConfigurationTest.SOURCE_TYPE, StorageImageConfigurationTest.CONFIGURATION.getSourceType());
        Assert.assertEquals(StorageImageConfigurationTest.SOURCE, StorageImageConfigurationTest.CONFIGURATION.getSource());
        Assert.assertEquals(StorageImageConfigurationTest.CONTAINER_TYPE, StorageImageConfigurationTest.CONFIGURATION.getContainerType());
        Assert.assertEquals(StorageImageConfigurationTest.ARCHIVE_SIZE_BYTES, StorageImageConfigurationTest.CONFIGURATION.getArchiveSizeBytes());
        Assert.assertEquals(StorageImageConfigurationTest.SHA1, StorageImageConfigurationTest.CONFIGURATION.getSha1());
        Assert.assertEquals(STORAGE, StorageImageConfigurationTest.CONFIGURATION.getType());
    }

    @Test
    public void testToAndFromPb() {
        Assert.assertTrue(((ImageConfiguration.fromPb(StorageImageConfigurationTest.CONFIGURATION.toPb())) instanceof StorageImageConfiguration));
        compareRawImageConfiguration(StorageImageConfigurationTest.CONFIGURATION, ImageConfiguration.<StorageImageConfiguration>fromPb(StorageImageConfigurationTest.CONFIGURATION.toPb()));
        StorageImageConfiguration configuration = StorageImageConfiguration.of(StorageImageConfigurationTest.SOURCE);
        compareRawImageConfiguration(configuration, StorageImageConfiguration.fromPb(configuration.toPb()));
    }

    @Test
    public void testOf() {
        StorageImageConfiguration configuration = StorageImageConfiguration.of(StorageImageConfigurationTest.SOURCE);
        Assert.assertEquals(STORAGE, configuration.getType());
        Assert.assertNull(configuration.getSourceType());
        Assert.assertEquals(StorageImageConfigurationTest.SOURCE, configuration.getSource());
        Assert.assertNull(configuration.getContainerType());
        Assert.assertNull(configuration.getArchiveSizeBytes());
        Assert.assertNull(configuration.getSha1());
    }

    @Test
    public void testSetProjectId() {
        Assert.assertSame(StorageImageConfigurationTest.CONFIGURATION, StorageImageConfigurationTest.CONFIGURATION.setProjectId("project"));
    }
}

