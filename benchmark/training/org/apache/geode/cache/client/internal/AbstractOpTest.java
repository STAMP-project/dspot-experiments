/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.cache.client.internal;


import org.apache.geode.internal.cache.tier.sockets.Message;
import org.apache.geode.test.junit.categories.ClientServerTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


@Category({ ClientServerTest.class })
public class AbstractOpTest {
    @Test
    public void shouldBeMockable() throws Exception {
        AbstractOp mockAbstractOp = Mockito.mock(AbstractOp.class);
        Object mockObject = Mockito.mock(Object.class);
        Mockito.when(mockAbstractOp.processObjResponse(ArgumentMatchers.any(), ArgumentMatchers.anyString())).thenReturn(mockObject);
        assertThat(mockAbstractOp.processObjResponse(Mockito.mock(Message.class), "string")).isEqualTo(mockObject);
    }
}

