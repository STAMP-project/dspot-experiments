/**
 * (C) 2007-2012 Alibaba Group Holding Limited.
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
 * Authors:
 *   wuhua <wq163@163.com> , boyan <killme2008@gmail.com>
 */
package com.taobao.metamorphosis.server.network;


import com.taobao.metamorphosis.network.HttpStatus;
import com.taobao.metamorphosis.network.OffsetCommand;
import com.taobao.metamorphosis.server.store.MessageStore;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;


public class OffsetProcessorUnitTest extends BaseProcessorUnitTest {
    private OffsetProcessor offsetProcessor;

    private final String topic = "OffsetProcessorUnitTest";

    private final String group = "boyan-test";

    @Test
    public void testHandleRequestNullStore() throws Exception {
        final int partition = 1;
        final int opaque = 0;
        final long offset = 1024;
        EasyMock.expect(this.storeManager.getMessageStore(this.topic, partition)).andReturn(null);
        this.conn.response(new com.taobao.metamorphosis.network.BooleanCommand(HttpStatus.NotFound, (((("The topic `" + (this.topic)) + "` in partition `") + partition) + "` is not exists"), opaque));
        this.mocksControl.replay();
        this.offsetProcessor.handleRequest(new OffsetCommand(this.topic, this.group, partition, offset, opaque), this.conn);
        this.mocksControl.verify();
        Assert.assertEquals(1, this.statsManager.getCmdOffsets());
    }

    @Test
    public void testHandleRequestNormal() throws Exception {
        final int partition = 1;
        final int opaque = 0;
        final long offset = 1024;
        final long resultOffset = 1536;
        final MessageStore store = this.mocksControl.createMock(MessageStore.class);
        EasyMock.expect(this.storeManager.getMessageStore(this.topic, partition)).andReturn(store);
        EasyMock.expect(store.getNearestOffset(offset)).andReturn(resultOffset);
        this.conn.response(new com.taobao.metamorphosis.network.BooleanCommand(HttpStatus.Success, String.valueOf(resultOffset), opaque));
        this.mocksControl.replay();
        this.offsetProcessor.handleRequest(new OffsetCommand(this.topic, this.group, partition, offset, opaque), this.conn);
        this.mocksControl.verify();
        Assert.assertEquals(1, this.statsManager.getCmdOffsets());
    }
}

