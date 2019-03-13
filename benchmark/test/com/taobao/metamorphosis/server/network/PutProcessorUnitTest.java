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


import Location.InvalidLocaltion;
import com.taobao.gecko.core.command.ResponseCommand;
import com.taobao.metamorphosis.network.BooleanCommand;
import com.taobao.metamorphosis.network.HttpStatus;
import com.taobao.metamorphosis.network.PutCommand;
import com.taobao.metamorphosis.server.assembly.BrokerCommandProcessor.StoreAppendCallback;
import com.taobao.metamorphosis.server.store.Location;
import com.taobao.metamorphosis.server.store.MessageStore;
import com.taobao.metamorphosis.utils.MessageFlagUtils;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;


public class PutProcessorUnitTest extends BaseProcessorUnitTest {
    private PutProcessor processor;

    final String topic = "PutProcessorUnitTest";

    @Test
    public void testProcessRequestNormal() throws Exception {
        final int partition = 1;
        final int opaque = -1;
        final long offset = 1024L;
        final byte[] data = new byte[1024];
        final int flag = MessageFlagUtils.getFlag(null);
        final PutCommand request = new PutCommand(this.topic, partition, data, null, flag, opaque);
        final long msgId = 100000L;
        final MessageStore store = this.mocksControl.createMock(MessageStore.class);
        EasyMock.expect(this.idWorker.nextId()).andReturn(msgId);
        EasyMock.expect(this.storeManager.getOrCreateMessageStore(this.topic, partition)).andReturn(store);
        final BooleanCommand expectResp = new BooleanCommand(HttpStatus.Success, ((((msgId + " ") + partition) + " ") + offset), opaque);
        final AtomicBoolean invoked = new AtomicBoolean(false);
        final PutCallback cb = new PutCallback() {
            @Override
            public void putComplete(final ResponseCommand resp) {
                invoked.set(true);
                if (!(expectResp.equals(resp))) {
                    throw new RuntimeException();
                }
            }
        };
        store.append(msgId, request, this.commandProcessor.new StoreAppendCallback(partition, (((this.metaConfig.getBrokerId()) + "-") + partition), request, msgId, cb));
        EasyMock.expectLastCall().andAnswer(new org.easymock.IAnswer<Object>() {
            @Override
            public Object answer() throws Throwable {
                ((StoreAppendCallback) (EasyMock.getCurrentArguments()[2])).appendComplete(Location.create(offset, 1024));
                return null;
            }
        });
        this.brokerZooKeeper.registerTopicInZk(this.topic, false);
        EasyMock.expectLastCall();
        this.mocksControl.replay();
        this.commandProcessor.processPutCommand(request, this.sessionContext, cb);
        this.mocksControl.verify();
        Assert.assertEquals(0, this.statsManager.getCmdPutFailed());
        Assert.assertTrue(invoked.get());
    }

    @Test
    public void testProcessRequestAppendFail() throws Exception {
        final int partition = 1;
        final int opaque = -1;
        final byte[] data = new byte[1024];
        final int flag = MessageFlagUtils.getFlag(null);
        final PutCommand request = new PutCommand(this.topic, partition, data, null, flag, opaque);
        final long msgId = 100000L;
        final MessageStore store = this.mocksControl.createMock(MessageStore.class);
        EasyMock.expect(this.idWorker.nextId()).andReturn(msgId);
        EasyMock.expect(this.storeManager.getOrCreateMessageStore(this.topic, partition)).andReturn(store);
        final AtomicBoolean invoked = new AtomicBoolean(false);
        final BooleanCommand expectResp = new BooleanCommand(HttpStatus.InternalServerError, "Put message to [broker 'meta://localhost:8123'] [partition 'PutProcessorUnitTest-1'] failed.", request.getOpaque());
        final PutCallback cb = new PutCallback() {
            @Override
            public void putComplete(final ResponseCommand resp) {
                invoked.set(true);
                System.out.println(getErrorMsg());
                if (!(expectResp.equals(resp))) {
                    throw new RuntimeException();
                }
            }
        };
        store.append(msgId, request, this.commandProcessor.new StoreAppendCallback(partition, (((this.metaConfig.getBrokerId()) + "-") + partition), request, msgId, cb));
        EasyMock.expectLastCall().andAnswer(new org.easymock.IAnswer<Object>() {
            @Override
            public Object answer() throws Throwable {
                ((StoreAppendCallback) (EasyMock.getCurrentArguments()[2])).appendComplete(InvalidLocaltion);
                return null;
            }
        });
        this.brokerZooKeeper.registerTopicInZk(this.topic, false);
        EasyMock.expectLastCall();
        EasyMock.expect(this.brokerZooKeeper.getBrokerString()).andReturn("meta://localhost:8123");
        this.mocksControl.replay();
        this.commandProcessor.processPutCommand(request, this.sessionContext, cb);
        this.mocksControl.verify();
        Assert.assertEquals(1, this.statsManager.getCmdPutFailed());
        Assert.assertTrue(invoked.get());
    }

    @Test
    public void testProcessRequestPartitionClosed() throws Exception {
        final int partition = 1;
        final int opaque = -1;
        final byte[] data = new byte[1024];
        final int flag = MessageFlagUtils.getFlag(null);
        final PutCommand request = new PutCommand(this.topic, partition, data, null, flag, opaque);
        this.metaConfig.setTopics(Arrays.asList(this.topic));
        this.metaConfig.closePartitions(this.topic, partition, partition);
        final BooleanCommand expectedResp = new BooleanCommand(HttpStatus.Forbidden, "Put message to [broker 'meta://localhost:8123'] [partition 'PutProcessorUnitTest-1'] failed.Detail:partition[0-1] has been closed", request.getOpaque());
        final AtomicBoolean invoked = new AtomicBoolean(false);
        final PutCallback cb = new PutCallback() {
            @Override
            public void putComplete(final ResponseCommand resp) {
                invoked.set(true);
                System.out.println(getErrorMsg());
                if (!(expectedResp.equals(resp))) {
                    throw new RuntimeException();
                }
            }
        };
        EasyMock.expect(this.brokerZooKeeper.getBrokerString()).andReturn("meta://localhost:8123");
        this.mocksControl.replay();
        this.commandProcessor.processPutCommand(request, this.sessionContext, cb);
        this.mocksControl.verify();
    }
}

