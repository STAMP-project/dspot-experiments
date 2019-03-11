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
package alluxio.master.journal.raft;


import PropertyKey.JOB_MASTER_EMBEDDED_JOURNAL_ADDRESSES;
import PropertyKey.JOB_MASTER_EMBEDDED_JOURNAL_PORT;
import PropertyKey.JOB_MASTER_HOSTNAME;
import PropertyKey.MASTER_EMBEDDED_JOURNAL_ADDRESSES;
import PropertyKey.MASTER_EMBEDDED_JOURNAL_PORT;
import PropertyKey.MASTER_HOSTNAME;
import ServiceType.JOB_MASTER_RAFT;
import ServiceType.MASTER_RAFT;
import alluxio.conf.ServerConfiguration;
import com.google.common.collect.Sets;
import java.net.InetSocketAddress;
import org.junit.Assert;
import org.junit.Test;


/**
 * Units tests for {@link RaftJournalConfiguration}.
 */
public final class RaftJournalConfigurationTest {
    @Test
    public void defaultDefaults() {
        RaftJournalConfiguration conf = getConf(MASTER_RAFT);
        checkAddress("testhost", 19200, conf.getLocalAddress());
    }

    @Test
    public void port() {
        int testPort = 10000;
        ServerConfiguration.set(MASTER_EMBEDDED_JOURNAL_PORT, testPort);
        RaftJournalConfiguration conf = getConf(MASTER_RAFT);
        Assert.assertEquals(testPort, conf.getLocalAddress().getPort());
        Assert.assertEquals(testPort, conf.getClusterAddresses().get(0).getPort());
    }

    @Test
    public void derivedMasterHostname() {
        ServerConfiguration.set(MASTER_HOSTNAME, "test");
        RaftJournalConfiguration conf = getConf(MASTER_RAFT);
        checkAddress("test", 19200, conf.getLocalAddress());
    }

    @Test
    public void derivedJobMasterHostname() {
        ServerConfiguration.set(JOB_MASTER_HOSTNAME, "test");
        RaftJournalConfiguration conf = getConf(JOB_MASTER_RAFT);
        checkAddress("test", 20003, conf.getLocalAddress());
    }

    @Test
    public void derivedJobMasterHostnameFromMasterHostname() {
        ServerConfiguration.set(MASTER_HOSTNAME, "test");
        RaftJournalConfiguration conf = getConf(JOB_MASTER_RAFT);
        checkAddress("test", 20003, conf.getLocalAddress());
    }

    @Test
    public void derivedJobMasterAddressesFromMasterAddresses() {
        ServerConfiguration.set(MASTER_EMBEDDED_JOURNAL_ADDRESSES, "host1:10,host2:20,host3:10");
        ServerConfiguration.set(MASTER_HOSTNAME, "host1");
        ServerConfiguration.set(JOB_MASTER_EMBEDDED_JOURNAL_PORT, 5);
        RaftJournalConfiguration conf = getConf(JOB_MASTER_RAFT);
        Assert.assertEquals(Sets.newHashSet(new InetSocketAddress("host1", 5), new InetSocketAddress("host2", 5), new InetSocketAddress("host3", 5)), new java.util.HashSet(conf.getClusterAddresses()));
    }

    @Test
    public void derivedJobMasterAddressesFromJobMasterAddresses() {
        ServerConfiguration.set(JOB_MASTER_EMBEDDED_JOURNAL_ADDRESSES, "host1:10,host2:20,host3:10");
        ServerConfiguration.set(JOB_MASTER_HOSTNAME, "host1");
        ServerConfiguration.set(JOB_MASTER_EMBEDDED_JOURNAL_PORT, 10);
        RaftJournalConfiguration conf = getConf(JOB_MASTER_RAFT);
        Assert.assertEquals(Sets.newHashSet(new InetSocketAddress("host1", 10), new InetSocketAddress("host2", 20), new InetSocketAddress("host3", 10)), new java.util.HashSet(conf.getClusterAddresses()));
    }
}

