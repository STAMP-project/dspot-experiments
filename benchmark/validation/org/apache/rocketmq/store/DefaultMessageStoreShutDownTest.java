/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.rocketmq.store;


import java.io.File;
import org.apache.rocketmq.store.config.StorePathConfigHelper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class DefaultMessageStoreShutDownTest {
    private DefaultMessageStore messageStore;

    @Test
    public void testDispatchBehindWhenShutdown() {
        messageStore.shutdown();
        Assert.assertTrue((!(messageStore.shutDownNormal)));
        File file = new File(StorePathConfigHelper.getAbortFile(messageStore.getMessageStoreConfig().getStorePathRootDir()));
        Assert.assertTrue(file.exists());
    }
}

