/**
 * This file is part of Graylog.
 *
 * Graylog is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Graylog is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Graylog.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.graylog2.plugin.inputs.transports;


import com.google.common.collect.ImmutableMap;
import io.netty.channel.nio.NioEventLoopGroup;
import java.io.File;
import java.io.IOException;
import org.graylog2.inputs.transports.NettyTransportConfiguration;
import org.graylog2.inputs.transports.netty.EventLoopGroupFactory;
import org.graylog2.plugin.LocalMetricRegistry;
import org.graylog2.plugin.configuration.Configuration;
import org.graylog2.plugin.inputs.MessageInput;
import org.graylog2.plugin.inputs.util.ThroughputCounter;
import org.junit.Assume;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.RestoreSystemProperties;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;


public class AbstractTcpTransportTest {
    @Rule
    public final MockitoRule mockitoRule = MockitoJUnit.rule();

    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Rule
    public final RestoreSystemProperties restoreSystemProperties = new RestoreSystemProperties();

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Mock
    private MessageInput input;

    private ThroughputCounter throughputCounter;

    private LocalMetricRegistry localRegistry;

    private NioEventLoopGroup eventLoopGroup;

    private EventLoopGroupFactory eventLoopGroupFactory;

    private final NettyTransportConfiguration nettyTransportConfiguration = new NettyTransportConfiguration("nio", "jdk", 2);

    @Test
    public void getChildChannelHandlersGeneratesSelfSignedCertificates() {
        final Configuration configuration = new Configuration(ImmutableMap.of("bind_address", "localhost", "port", 12345, "tls_enable", true));
        final AbstractTcpTransport transport = new AbstractTcpTransport(configuration, throughputCounter, localRegistry, eventLoopGroup, eventLoopGroupFactory, nettyTransportConfiguration) {};
        final MessageInput input = Mockito.mock(MessageInput.class);
        assertThat(transport.getChildChannelHandlers(input)).containsKey("tls");
    }

    @Test
    public void getChildChannelHandlersFailsIfTempDirDoesNotExist() throws IOException {
        final File tmpDir = temporaryFolder.newFolder();
        Assume.assumeTrue(tmpDir.delete());
        System.setProperty("java.io.tmpdir", tmpDir.getAbsolutePath());
        final Configuration configuration = new Configuration(ImmutableMap.of("bind_address", "localhost", "port", 12345, "tls_enable", true));
        final AbstractTcpTransport transport = new AbstractTcpTransport(configuration, throughputCounter, localRegistry, eventLoopGroup, eventLoopGroupFactory, nettyTransportConfiguration) {};
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage(("Couldn't write to temporary directory: " + (tmpDir.getAbsolutePath())));
        transport.getChildChannelHandlers(input);
    }

    @Test
    public void getChildChannelHandlersFailsIfTempDirIsNotWritable() throws IOException {
        final File tmpDir = temporaryFolder.newFolder();
        Assume.assumeTrue(tmpDir.setWritable(false));
        Assume.assumeFalse(tmpDir.canWrite());
        System.setProperty("java.io.tmpdir", tmpDir.getAbsolutePath());
        final Configuration configuration = new Configuration(ImmutableMap.of("bind_address", "localhost", "port", 12345, "tls_enable", true));
        final AbstractTcpTransport transport = new AbstractTcpTransport(configuration, throughputCounter, localRegistry, eventLoopGroup, eventLoopGroupFactory, nettyTransportConfiguration) {};
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage(("Couldn't write to temporary directory: " + (tmpDir.getAbsolutePath())));
        transport.getChildChannelHandlers(input);
    }

    @Test
    public void getChildChannelHandlersFailsIfTempDirIsNoDirectory() throws IOException {
        final File file = temporaryFolder.newFile();
        Assume.assumeTrue(file.isFile());
        System.setProperty("java.io.tmpdir", file.getAbsolutePath());
        final Configuration configuration = new Configuration(ImmutableMap.of("bind_address", "localhost", "port", 12345, "tls_enable", true));
        final AbstractTcpTransport transport = new AbstractTcpTransport(configuration, throughputCounter, localRegistry, eventLoopGroup, eventLoopGroupFactory, nettyTransportConfiguration) {};
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage(("Couldn't write to temporary directory: " + (file.getAbsolutePath())));
        transport.getChildChannelHandlers(input);
    }
}

