package org.nd4j.parameterserver.distributed.util;


import NetworkOrganizer.VirtualTree;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author raver119@gmail.com
 */
@Slf4j
public class NetworkOrganizerTest {
    @Test
    public void testSimpleSelection1() throws Exception {
        NetworkOrganizer organizer = new NetworkOrganizer("127.0.0.0/24");
        List<String> list = organizer.getSubset(1);
        Assert.assertEquals(1, list.size());
        Assert.assertEquals("127.0.0.1", list.get(0));
    }

    @Test
    public void testSimpleSelection2() throws Exception {
        NetworkOrganizer organizer = new NetworkOrganizer("127.0.0.0/24");
        String ip = organizer.getMatchingAddress();
        Assert.assertEquals("127.0.0.1", ip);
    }

    @Test
    public void testSelectionUniformNetworkC1() {
        List<NetworkInformation> collection = new ArrayList<>();
        for (int i = 1; i < 128; i++) {
            NetworkInformation information = new NetworkInformation();
            information.addIpAddress(("192.168.0." + i));
            information.addIpAddress(getRandomIp());
            collection.add(information);
        }
        NetworkOrganizer discoverer = new NetworkOrganizer(collection, "192.168.0.0/24");
        // check for primary subset (aka Shards)
        List<String> shards = discoverer.getSubset(10);
        Assert.assertEquals(10, shards.size());
        for (String ip : shards) {
            Assert.assertNotEquals(null, ip);
            Assert.assertTrue(ip.startsWith("192.168.0"));
        }
        // check for secondary subset (aka Backup)
        List<String> backup = discoverer.getSubset(10, shards);
        Assert.assertEquals(10, backup.size());
        for (String ip : backup) {
            Assert.assertNotEquals(null, ip);
            Assert.assertTrue(ip.startsWith("192.168.0"));
            Assert.assertFalse(shards.contains(ip));
        }
    }

    @Test
    public void testSelectionSingleBox1() throws Exception {
        List<NetworkInformation> collection = new ArrayList<>();
        NetworkInformation information = new NetworkInformation();
        information.addIpAddress("192.168.21.12");
        information.addIpAddress("10.0.27.19");
        collection.add(information);
        NetworkOrganizer organizer = new NetworkOrganizer(collection, "192.168.0.0/16");
        List<String> shards = organizer.getSubset(10);
        Assert.assertEquals(1, shards.size());
    }

    @Test
    public void testSelectionSingleBox2() throws Exception {
        List<NetworkInformation> collection = new ArrayList<>();
        NetworkInformation information = new NetworkInformation();
        information.addIpAddress("192.168.72.12");
        information.addIpAddress("10.2.88.19");
        collection.add(information);
        NetworkOrganizer organizer = new NetworkOrganizer(collection);
        List<String> shards = organizer.getSubset(10);
        Assert.assertEquals(1, shards.size());
    }

    @Test
    public void testSelectionDisjointNetworkC1() {
        List<NetworkInformation> collection = new ArrayList<>();
        for (int i = 1; i < 128; i++) {
            NetworkInformation information = new NetworkInformation();
            if (i < 20)
                information.addIpAddress(("172.12.0." + i));

            information.addIpAddress(getRandomIp());
            collection.add(information);
        }
        NetworkOrganizer discoverer = new NetworkOrganizer(collection, "172.12.0.0/24");
        // check for primary subset (aka Shards)
        List<String> shards = discoverer.getSubset(10);
        Assert.assertEquals(10, shards.size());
        List<String> backup = discoverer.getSubset(10, shards);
        // we expect 9 here, thus backups will be either incomplete or complex sharding will be used for them
        Assert.assertEquals(9, backup.size());
        for (String ip : backup) {
            Assert.assertNotEquals(null, ip);
            Assert.assertTrue(ip.startsWith("172.12.0"));
            Assert.assertFalse(shards.contains(ip));
        }
    }

    /**
     * In this test we'll check shards selection in "casual" AWS setup
     * By default AWS box has only one IP from 172.16.0.0/12 space + local loopback IP, which isn't exposed
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testSelectionWithoutMaskB1() throws Exception {
        List<NetworkInformation> collection = new ArrayList<>();
        // we imitiate 512 cluster nodes here
        for (int i = 0; i < 512; i++) {
            NetworkInformation information = new NetworkInformation();
            information.addIpAddress(getRandomAwsIp());
            collection.add(information);
        }
        NetworkOrganizer organizer = new NetworkOrganizer(collection);
        List<String> shards = organizer.getSubset(10);
        Assert.assertEquals(10, shards.size());
        List<String> backup = organizer.getSubset(10, shards);
        Assert.assertEquals(10, backup.size());
        for (String ip : backup) {
            Assert.assertNotEquals(null, ip);
            Assert.assertTrue(ip.startsWith("172."));
            Assert.assertFalse(shards.contains(ip));
        }
    }

    /**
     * In this test we check for environment which has AWS-like setup:
     *  1) Each box has IP address from 172.16.0.0/12 range
     *  2) Within original homogenous network, we have 3 separate networks:
     *      A) 192.168.0.X
     *      B) 10.0.12.X
     *      C) 10.172.12.X
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testSelectionWithoutMaskB2() throws Exception {
        List<NetworkInformation> collection = new ArrayList<>();
        // we imitiate 512 cluster nodes here
        for (int i = 0; i < 512; i++) {
            NetworkInformation information = new NetworkInformation();
            information.addIpAddress(getRandomAwsIp());
            if (i < 30) {
                information.addIpAddress(("192.168.0." + i));
            } else
                if (i < 95) {
                    information.addIpAddress(("10.0.12." + i));
                } else
                    if (i < 255) {
                        information.addIpAddress(("10.172.12." + i));
                    }


            collection.add(information);
        }
        NetworkOrganizer organizer = new NetworkOrganizer(collection);
        List<String> shards = organizer.getSubset(15);
        Assert.assertEquals(15, shards.size());
        for (String ip : shards) {
            Assert.assertNotEquals(null, ip);
            Assert.assertTrue(ip.startsWith("172."));
        }
        List<String> backup = organizer.getSubset(15, shards);
        for (String ip : backup) {
            Assert.assertNotEquals(null, ip);
            Assert.assertTrue(ip.startsWith("172."));
            Assert.assertFalse(shards.contains(ip));
        }
    }

    /**
     * Here we just check formatting for octets
     */
    @Test
    public void testFormat1() throws Exception {
        for (int i = 0; i < 256; i++) {
            String octet = NetworkOrganizer.toBinaryOctet(i);
            Assert.assertEquals(8, octet.length());
            log.trace("i: {}; Octet: {}", i, octet);
        }
    }

    @Test
    public void testFormat2() throws Exception {
        for (int i = 0; i < 1000; i++) {
            String octets = NetworkOrganizer.convertIpToOctets(getRandomIp());
            // we just expect 8 bits per bloc, 4 blocks in total, plus 3 dots between blocks
            Assert.assertEquals(35, octets.length());
        }
    }

    @Test
    public void testNetTree1() throws Exception {
        List<String> ips = Arrays.asList("192.168.0.1", "192.168.0.2");
        NetworkOrganizer.VirtualTree tree = new NetworkOrganizer.VirtualTree();
        for (String ip : ips)
            tree.map(NetworkOrganizer.convertIpToOctets(ip));

        Assert.assertEquals(2, tree.getUniqueBranches());
        Assert.assertEquals(2, tree.getTotalBranches());
        log.info("rewind: {}", tree.getHottestNetwork());
    }

    @Test
    public void testNetTree2() throws Exception {
        List<String> ips = Arrays.asList("192.168.12.2", "192.168.0.2", "192.168.0.2", "192.168.62.92");
        NetworkOrganizer.VirtualTree tree = new NetworkOrganizer.VirtualTree();
        for (String ip : ips)
            tree.map(NetworkOrganizer.convertIpToOctets(ip));

        Assert.assertEquals(3, tree.getUniqueBranches());
        Assert.assertEquals(4, tree.getTotalBranches());
    }

    /**
     * This test is just a naive test for counters
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testNetTree3() throws Exception {
        List<String> ips = new ArrayList<>();
        NetworkOrganizer.VirtualTree tree = new NetworkOrganizer.VirtualTree();
        for (int i = 0; i < 3000; i++)
            ips.add(getRandomIp());

        for (int i = 0; i < 20; i++)
            ips.add(("192.168.12." + i));

        Collections.shuffle(ips);
        Set<String> uniqueIps = new HashSet<>(ips);
        for (String ip : uniqueIps)
            tree.map(NetworkOrganizer.convertIpToOctets(ip));

        Assert.assertEquals(uniqueIps.size(), tree.getTotalBranches());
        Assert.assertEquals(uniqueIps.size(), tree.getUniqueBranches());
    }

    @Test
    public void testNetTree4() throws Exception {
        List<String> ips = Arrays.asList("192.168.12.2", "192.168.0.2", "192.168.0.2", "192.168.62.92", "5.3.4.5");
        NetworkOrganizer.VirtualTree tree = new NetworkOrganizer.VirtualTree();
        for (String ip : ips)
            tree.map(NetworkOrganizer.convertIpToOctets(ip));

        Assert.assertEquals(4, tree.getUniqueBranches());
        Assert.assertEquals(5, tree.getTotalBranches());
    }

    @Test
    public void testNetTree5() throws Exception {
        List<String> ips = new ArrayList<>();
        NetworkOrganizer.VirtualTree tree = new NetworkOrganizer.VirtualTree();
        for (int i = 0; i < 254; i++)
            ips.add(getRandomIp());

        for (int i = 1; i < 255; i++)
            ips.add(("192.168.12." + i));

        Collections.shuffle(ips);
        Set<String> uniqueIps = new HashSet<>(ips);
        for (String ip : uniqueIps)
            tree.map(NetworkOrganizer.convertIpToOctets(ip));

        Assert.assertEquals(508, uniqueIps.size());
        Assert.assertEquals(uniqueIps.size(), tree.getTotalBranches());
        Assert.assertEquals(uniqueIps.size(), tree.getUniqueBranches());
        /**
         * Now the most important part here. we should get 192.168.12. as the most "popular" branch
         */
        String networkA = tree.getHottestNetworkA();
        Assert.assertEquals("11000000", networkA);
        String networkAB = tree.getHottestNetworkAB();
        // assertEquals("11000000.10101000", networkAB);
    }

    @Test
    public void testNetTree6() throws Exception {
        List<String> ips = new ArrayList<>();
        NetworkOrganizer.VirtualTree tree = new NetworkOrganizer.VirtualTree();
        for (int i = 0; i < 254; i++)
            ips.add(getRandomIp());

        for (int i = 1; i < 255; i++)
            ips.add(getRandomAwsIp());

        Collections.shuffle(ips);
        Set<String> uniqueIps = new HashSet<>(ips);
        for (String ip : uniqueIps)
            tree.map(NetworkOrganizer.convertIpToOctets(ip));

        Assert.assertEquals(508, uniqueIps.size());
        Assert.assertEquals(uniqueIps.size(), tree.getTotalBranches());
        Assert.assertEquals(uniqueIps.size(), tree.getUniqueBranches());
        /**
         * Now the most important part here. we should get 192.168.12. as the most "popular" branch
         */
        String networkA = tree.getHottestNetworkA();
        Assert.assertEquals("10101100", networkA);
        String networkAB = tree.getHottestNetworkAB();
        // assertEquals("10101100.00010000", networkAB);
    }
}

