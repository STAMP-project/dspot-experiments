/**
 * The Alluxio Open Foundation licenses this work under the Apache License, version 2.0
 * (the "License"). You may not use this work except in compliance with the License, which is
 * available at www.apache.org/licenses/LICENSE-2.0
 *
 * This software is distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied, as more fully set forth in the License.
 *
 * See the NOTICE file distributed with this work for information regarding copyright ownership.
 */
package alluxio.client.block.stream;


import BlockInStream.BlockInStreamSource;
import alluxio.ClientContext;
import alluxio.ConfigurationTestUtils;
import alluxio.client.file.FileSystemContext;
import alluxio.client.file.options.InStreamOptions;
import alluxio.conf.InstancedConfiguration;
import alluxio.util.network.NettyUtils;
import alluxio.wire.BlockInfo;
import alluxio.wire.WorkerNetAddress;
import java.io.Closeable;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


/**
 * Tests the {@link BlockInStream} class's static methods.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ FileSystemContext.class, NettyUtils.class })
public class BlockInStreamTest {
    private FileSystemContext mMockContext;

    private BlockInfo mInfo;

    private InStreamOptions mOptions;

    private InstancedConfiguration mConf = ConfigurationTestUtils.defaults();

    @Test
    public void createShortCircuit() throws Exception {
        WorkerNetAddress dataSource = new WorkerNetAddress();
        BlockInStream.BlockInStreamSource dataSourceType = BlockInStreamSource.LOCAL;
        BlockInStream stream = BlockInStream.create(mMockContext, mInfo, dataSource, dataSourceType, mOptions);
        Assert.assertTrue(stream.isShortCircuit());
    }

    @Test
    public void createRemote() throws Exception {
        WorkerNetAddress dataSource = new WorkerNetAddress();
        BlockInStream.BlockInStreamSource dataSourceType = BlockInStreamSource.REMOTE;
        BlockInStream stream = BlockInStream.create(mMockContext, mInfo, dataSource, dataSourceType, mOptions);
        Assert.assertFalse(stream.isShortCircuit());
    }

    @Test
    public void createUfs() throws Exception {
        WorkerNetAddress dataSource = new WorkerNetAddress();
        BlockInStream.BlockInStreamSource dataSourceType = BlockInStreamSource.UFS;
        BlockInStream stream = BlockInStream.create(mMockContext, mInfo, dataSource, dataSourceType, mOptions);
        Assert.assertFalse(stream.isShortCircuit());
    }

    @Test
    public void createShortCircuitDisabled() throws Exception {
        try (Closeable c = toResource()) {
            WorkerNetAddress dataSource = new WorkerNetAddress();
            Mockito.when(mMockContext.getClientContext()).thenReturn(ClientContext.create(mConf));
            BlockInStream.BlockInStreamSource dataSourceType = BlockInStreamSource.LOCAL;
            BlockInStream stream = BlockInStream.create(mMockContext, mInfo, dataSource, dataSourceType, mOptions);
            Assert.assertFalse(stream.isShortCircuit());
        }
    }

    @Test
    public void createDomainSocketEnabled() throws Exception {
        PowerMockito.mockStatic(NettyUtils.class);
        PowerMockito.when(NettyUtils.isDomainSocketSupported(Matchers.any(WorkerNetAddress.class), Matchers.any(InstancedConfiguration.class))).thenReturn(true);
        WorkerNetAddress dataSource = new WorkerNetAddress();
        BlockInStream.BlockInStreamSource dataSourceType = BlockInStreamSource.LOCAL;
        BlockInStream stream = BlockInStream.create(mMockContext, mInfo, dataSource, dataSourceType, mOptions);
        Assert.assertFalse(stream.isShortCircuit());
    }
}

