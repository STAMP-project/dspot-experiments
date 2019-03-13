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
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.shardingsphere.shardingproxy.frontend.executor;


import TransactionType.BASE;
import TransactionType.LOCAL;
import TransactionType.XA;
import io.netty.channel.ChannelId;
import java.util.concurrent.ExecutorService;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public final class ExecutorGroupTest {
    @Test
    public void assertGetExecutorServiceWithLocal() {
        ChannelId channelId = Mockito.mock(ChannelId.class);
        Assert.assertThat(CommandExecutorSelector.getExecutor(false, LOCAL, channelId), CoreMatchers.instanceOf(ExecutorService.class));
    }

    @Test
    public void assertGetExecutorServiceWithOccupyThreadForPerConnection() {
        ChannelId channelId = Mockito.mock(ChannelId.class);
        ChannelThreadExecutorGroup.getInstance().register(channelId);
        Assert.assertThat(CommandExecutorSelector.getExecutor(true, LOCAL, channelId), CoreMatchers.instanceOf(ExecutorService.class));
    }

    @Test
    public void assertGetExecutorServiceWithXA() {
        ChannelId channelId = Mockito.mock(ChannelId.class);
        ChannelThreadExecutorGroup.getInstance().register(channelId);
        Assert.assertThat(CommandExecutorSelector.getExecutor(false, XA, channelId), CoreMatchers.instanceOf(ExecutorService.class));
    }

    @Test
    public void assertGetExecutorServiceWithBASE() {
        ChannelId channelId = Mockito.mock(ChannelId.class);
        ChannelThreadExecutorGroup.getInstance().register(channelId);
        Assert.assertThat(CommandExecutorSelector.getExecutor(false, BASE, channelId), CoreMatchers.instanceOf(ExecutorService.class));
    }
}

