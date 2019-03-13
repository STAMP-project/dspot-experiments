/**
 * Copyright 2014-2016 CyberVision, Inc.
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
package org.kaaproject.kaa.client.configuration.base;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import org.kaaproject.kaa.client.KaaClientPlatformContext;
import org.kaaproject.kaa.client.persistence.PersistentStorage;
import org.mockito.Mockito;


public class SimpleConfigurationStorageTest {
    private static final String TEST_PATH = "configuration.data";

    @Test
    public void readTest() throws IOException {
        KaaClientPlatformContext context = Mockito.mock(KaaClientPlatformContext.class);
        PersistentStorage persistentStorage = Mockito.mock(PersistentStorage.class);
        Mockito.when(context.createPersistentStorage()).thenReturn(persistentStorage);
        Mockito.when(persistentStorage.exists(SimpleConfigurationStorageTest.TEST_PATH)).thenReturn(true);
        Mockito.when(persistentStorage.openForRead(SimpleConfigurationStorageTest.TEST_PATH)).thenReturn(new ByteArrayInputStream(createTestData()));
        SimpleConfigurationStorage storage = new SimpleConfigurationStorage(context, SimpleConfigurationStorageTest.TEST_PATH);
        ByteBuffer result = storage.loadConfiguration();
        Assert.assertTrue(Arrays.equals(result.array(), createTestData()));
    }

    @Test
    public void writeTest() throws IOException {
        KaaClientPlatformContext context = Mockito.mock(KaaClientPlatformContext.class);
        PersistentStorage persistentStorage = Mockito.mock(PersistentStorage.class);
        Mockito.when(context.createPersistentStorage()).thenReturn(persistentStorage);
        OutputStream osMock = Mockito.mock(OutputStream.class);
        Mockito.when(persistentStorage.openForWrite(SimpleConfigurationStorageTest.TEST_PATH)).thenReturn(osMock);
        SimpleConfigurationStorage storage = new SimpleConfigurationStorage(context, SimpleConfigurationStorageTest.TEST_PATH);
        storage.saveConfiguration(ByteBuffer.wrap(createTestData()));
    }
}

