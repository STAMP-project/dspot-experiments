/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ignite.internal.processors.hadoop.impl.igfs;


import org.apache.ignite.igfs.IgfsPath;
import org.apache.ignite.internal.processors.igfs.IgfsCommonAbstractTest;
import org.apache.ignite.spi.discovery.tcp.ipfinder.TcpDiscoveryIpFinder;
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder;
import org.junit.Test;


/**
 * Tests for IGFS file system handshake.
 */
public class IgniteHadoopFileSystemHandshakeSelfTest extends IgfsCommonAbstractTest {
    /**
     * IP finder.
     */
    private static final TcpDiscoveryIpFinder IP_FINDER = new TcpDiscoveryVmIpFinder(true);

    /**
     * Ignite instance name.
     */
    private static final String IGNITE_INSTANCE_NAME = "grid";

    /**
     * IGFS name.
     */
    private static final String IGFS_NAME = "igfs";

    /**
     * IGFS path.
     */
    private static final IgfsPath PATH = new IgfsPath("/path");

    /**
     * A host-port pair used for URI in embedded mode.
     */
    private static final String HOST_PORT_UNUSED = "somehost:65333";

    /**
     * Flag defines if to use TCP or embedded connection mode:
     */
    private boolean tcp = false;

    /**
     * Tests for Grid and IGFS having normal names.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testHandshake() throws Exception {
        startUp(false, false);
        tcp = true;
        checkValid(((((IgniteHadoopFileSystemHandshakeSelfTest.IGFS_NAME) + ":") + (IgniteHadoopFileSystemHandshakeSelfTest.IGNITE_INSTANCE_NAME)) + "@"));
        checkValid(((((IgniteHadoopFileSystemHandshakeSelfTest.IGFS_NAME) + ":") + (IgniteHadoopFileSystemHandshakeSelfTest.IGNITE_INSTANCE_NAME)) + "@127.0.0.1"));
        checkValid((((((IgniteHadoopFileSystemHandshakeSelfTest.IGFS_NAME) + ":") + (IgniteHadoopFileSystemHandshakeSelfTest.IGNITE_INSTANCE_NAME)) + "@127.0.0.1:") + (DFLT_IPC_PORT)));
        checkValid(((IgniteHadoopFileSystemHandshakeSelfTest.IGFS_NAME) + "@"));
        checkValid(((IgniteHadoopFileSystemHandshakeSelfTest.IGFS_NAME) + "@127.0.0.1"));
        checkValid((((IgniteHadoopFileSystemHandshakeSelfTest.IGFS_NAME) + "@127.0.0.1:") + (DFLT_IPC_PORT)));
        tcp = false;// Embedded mode:

        checkValid(((((IgniteHadoopFileSystemHandshakeSelfTest.IGFS_NAME) + ":") + (IgniteHadoopFileSystemHandshakeSelfTest.IGNITE_INSTANCE_NAME)) + "@"));
        checkValid((((((IgniteHadoopFileSystemHandshakeSelfTest.IGFS_NAME) + ":") + (IgniteHadoopFileSystemHandshakeSelfTest.IGNITE_INSTANCE_NAME)) + "@") + (IgniteHadoopFileSystemHandshakeSelfTest.HOST_PORT_UNUSED)));
        checkValid(((IgniteHadoopFileSystemHandshakeSelfTest.IGFS_NAME) + "@"));// Embedded mode fails, but remote tcp succeeds.

        checkValid((((IgniteHadoopFileSystemHandshakeSelfTest.IGFS_NAME) + "@") + (IgniteHadoopFileSystemHandshakeSelfTest.HOST_PORT_UNUSED)));
    }

    /**
     * Tests for Grid having {@code null} name and IGFS having normal name.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testHandshakeDefaultGrid() throws Exception {
        startUp(true, false);
        tcp = true;
        checkValid(((((IgniteHadoopFileSystemHandshakeSelfTest.IGFS_NAME) + ":") + (IgniteHadoopFileSystemHandshakeSelfTest.IGNITE_INSTANCE_NAME)) + "@"));
        checkValid(((((IgniteHadoopFileSystemHandshakeSelfTest.IGFS_NAME) + ":") + (IgniteHadoopFileSystemHandshakeSelfTest.IGNITE_INSTANCE_NAME)) + "@127.0.0.1"));
        checkValid((((((IgniteHadoopFileSystemHandshakeSelfTest.IGFS_NAME) + ":") + (IgniteHadoopFileSystemHandshakeSelfTest.IGNITE_INSTANCE_NAME)) + "@127.0.0.1:") + (DFLT_IPC_PORT)));
        checkValid(((IgniteHadoopFileSystemHandshakeSelfTest.IGFS_NAME) + "@"));
        checkValid(((IgniteHadoopFileSystemHandshakeSelfTest.IGFS_NAME) + "@127.0.0.1"));
        checkValid((((IgniteHadoopFileSystemHandshakeSelfTest.IGFS_NAME) + "@127.0.0.1:") + (DFLT_IPC_PORT)));
        tcp = false;// Embedded mode:

        checkValid(((((IgniteHadoopFileSystemHandshakeSelfTest.IGFS_NAME) + ":") + (IgniteHadoopFileSystemHandshakeSelfTest.IGNITE_INSTANCE_NAME)) + "@"));
        checkValid((((((IgniteHadoopFileSystemHandshakeSelfTest.IGFS_NAME) + ":") + (IgniteHadoopFileSystemHandshakeSelfTest.IGNITE_INSTANCE_NAME)) + "@") + (IgniteHadoopFileSystemHandshakeSelfTest.HOST_PORT_UNUSED)));
        checkValid(((IgniteHadoopFileSystemHandshakeSelfTest.IGFS_NAME) + "@"));
        checkValid((((IgniteHadoopFileSystemHandshakeSelfTest.IGFS_NAME) + "@") + (IgniteHadoopFileSystemHandshakeSelfTest.HOST_PORT_UNUSED)));
    }
}

