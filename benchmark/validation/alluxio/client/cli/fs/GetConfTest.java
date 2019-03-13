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
package alluxio.client.cli.fs;


import PropertyKey.WORKER_MEMORY_SIZE;
import PropertyKey.ZOOKEEPER_ENABLED;
import alluxio.ClientContext;
import alluxio.SystemOutRule;
import alluxio.cli.GetConf;
import alluxio.client.RetryHandlingMetaMasterConfigClient;
import alluxio.conf.PropertyKey;
import alluxio.conf.ServerConfiguration;
import alluxio.grpc.ConfigProperty;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.util.List;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * Tests for {@link GetConf}.
 */
public final class GetConfTest {
    private ByteArrayOutputStream mOutputStream = new ByteArrayOutputStream();

    @Rule
    public SystemOutRule mOutputStreamRule = new SystemOutRule(mOutputStream);

    @Test
    public void getConf() throws Exception {
        ServerConfiguration.set(WORKER_MEMORY_SIZE, "2048");
        ClientContext ctx = ClientContext.create(ServerConfiguration.global());
        Assert.assertEquals(0, GetConf.getConf(ctx, WORKER_MEMORY_SIZE.toString()));
        Assert.assertEquals("2048\n", mOutputStream.toString());
        mOutputStream.reset();
        ServerConfiguration.set(WORKER_MEMORY_SIZE, "2MB");
        ctx = ClientContext.create(ServerConfiguration.global());
        Assert.assertEquals(0, GetConf.getConf(ctx, WORKER_MEMORY_SIZE.toString()));
        Assert.assertEquals("2MB\n", mOutputStream.toString());
        mOutputStream.reset();
        ServerConfiguration.set(WORKER_MEMORY_SIZE, "Nonsense");
        ctx = ClientContext.create(ServerConfiguration.global());
        Assert.assertEquals(0, GetConf.getConf(ctx, WORKER_MEMORY_SIZE.toString()));
        Assert.assertEquals("Nonsense\n", mOutputStream.toString());
    }

    @Test
    public void getConfByAlias() {
        PropertyKey testProperty = new PropertyKey.Builder("alluxio.test.property").setAlias(new String[]{ "alluxio.test.property.alias" }).setDefaultValue("testValue").build();
        ClientContext ctx = ClientContext.create(ServerConfiguration.global());
        Assert.assertEquals(0, GetConf.getConf(ctx, "alluxio.test.property.alias"));
        Assert.assertEquals("testValue\n", mOutputStream.toString());
        mOutputStream.reset();
        Assert.assertEquals(0, GetConf.getConf(ctx, "alluxio.test.property"));
        Assert.assertEquals("testValue\n", mOutputStream.toString());
        PropertyKey.unregister(testProperty);
    }

    @Test
    public void getConfWithCorrectUnit() throws Exception {
        ServerConfiguration.set(WORKER_MEMORY_SIZE, "2048");
        ClientContext ctx = ClientContext.create(ServerConfiguration.global());
        Assert.assertEquals(0, GetConf.getConf(ctx, "--unit", "B", WORKER_MEMORY_SIZE.toString()));
        Assert.assertEquals("2048\n", mOutputStream.toString());
        mOutputStream.reset();
        ServerConfiguration.set(WORKER_MEMORY_SIZE, "2048");
        ctx = ClientContext.create(ServerConfiguration.global());
        Assert.assertEquals(0, GetConf.getConf(ctx, "--unit", "KB", WORKER_MEMORY_SIZE.toString()));
        Assert.assertEquals("2\n", mOutputStream.toString());
        mOutputStream.reset();
        ServerConfiguration.set(WORKER_MEMORY_SIZE, "2MB");
        ctx = ClientContext.create(ServerConfiguration.global());
        Assert.assertEquals(0, GetConf.getConf(ctx, "--unit", "KB", WORKER_MEMORY_SIZE.toString()));
        Assert.assertEquals("2048\n", mOutputStream.toString());
        mOutputStream.reset();
        ServerConfiguration.set(WORKER_MEMORY_SIZE, "2MB");
        ctx = ClientContext.create(ServerConfiguration.global());
        Assert.assertEquals(0, GetConf.getConf(ctx, "--unit", "MB", WORKER_MEMORY_SIZE.toString()));
        Assert.assertEquals("2\n", mOutputStream.toString());
    }

    @Test
    public void getConfWithWrongUnit() throws Exception {
        ServerConfiguration.set(WORKER_MEMORY_SIZE, "2048");
        Assert.assertEquals(1, GetConf.getConf(ClientContext.create(ServerConfiguration.global()), "--unit", "bad_unit", WORKER_MEMORY_SIZE.toString()));
    }

    @Test
    public void getConfWithInvalidConf() throws Exception {
        try (Closeable p = toResource()) {
            ServerConfiguration.reset();
            ClientContext ctx = ClientContext.create(ServerConfiguration.global());
            Assert.assertEquals(0, GetConf.getConf(ctx, ZOOKEEPER_ENABLED.toString()));
            Assert.assertEquals("true\n", mOutputStream.toString());
        } finally {
            ServerConfiguration.reset();
        }
    }

    @Test
    public void getConfFromMaster() throws Exception {
        // Prepare mock meta master client
        RetryHandlingMetaMasterConfigClient client = Mockito.mock(RetryHandlingMetaMasterConfigClient.class);
        List<ConfigProperty> configList = prepareConfigList();
        Mockito.when(client.getConfiguration()).thenReturn(configList);
        Assert.assertEquals(0, GetConf.getConfImpl(() -> client, ServerConfiguration.global(), "--master"));
        String expectedOutput = "alluxio.logger.type=MASTER_LOGGER\n" + (((("alluxio.master.audit.logger.type=MASTER_AUDIT_LOGGER\n" + "alluxio.master.hostname=localhost\n") + "alluxio.master.port=19998\n") + "alluxio.master.web.port=19999\n") + "alluxio.underfs.address=hdfs://localhost:9000\n");
        Assert.assertEquals(expectedOutput, mOutputStream.toString());
    }

    @Test
    public void getConfFromMasterWithSource() throws Exception {
        // Prepare mock meta master client
        RetryHandlingMetaMasterConfigClient client = Mockito.mock(RetryHandlingMetaMasterConfigClient.class);
        List<ConfigProperty> configList = prepareConfigList();
        Mockito.when(client.getConfiguration()).thenReturn(configList);
        Assert.assertEquals(0, GetConf.getConfImpl(() -> client, ServerConfiguration.global(), "--master", "--source"));
        // CHECKSTYLE.OFF: LineLengthExceed - Much more readable
        String expectedOutput = "alluxio.logger.type=MASTER_LOGGER (SYSTEM_PROPERTY)\n" + (((("alluxio.master.audit.logger.type=MASTER_AUDIT_LOGGER (SYSTEM_PROPERTY)\n" + "alluxio.master.hostname=localhost (SITE_PROPERTY (/alluxio/conf/alluxio-site.properties))\n") + "alluxio.master.port=19998 (DEFAULT)\n") + "alluxio.master.web.port=19999 (DEFAULT)\n") + "alluxio.underfs.address=hdfs://localhost:9000 (SITE_PROPERTY (/alluxio/conf/alluxio-site.properties))\n");
        // CHECKSTYLE.ON: LineLengthExceed
        Assert.assertEquals(expectedOutput, mOutputStream.toString());
    }
}

