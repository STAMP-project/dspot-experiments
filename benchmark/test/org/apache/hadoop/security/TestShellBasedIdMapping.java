/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.security;


import IdMappingConstant.STATIC_ID_MAPPING_FILE_KEY;
import IdMappingConstant.USERGROUPID_UPDATE_MILLIS_DEFAULT;
import IdMappingConstant.USERGROUPID_UPDATE_MILLIS_KEY;
import IdMappingConstant.USERGROUPID_UPDATE_MILLIS_MIN;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.ShellBasedIdMapping.StaticMapping;
import org.apache.hadoop.test.PlatformAssumptions;
import org.junit.Assert;
import org.junit.Test;

import static IdMappingConstant.USERGROUPID_UPDATE_MILLIS_DEFAULT;


public class TestShellBasedIdMapping {
    private static final Map<Integer, Integer> EMPTY_PASS_THROUGH_MAP = new org.apache.hadoop.security.ShellBasedIdMapping.PassThroughMap<Integer>();

    @Test
    public void testStaticMapParsing() throws IOException {
        File tempStaticMapFile = File.createTempFile("nfs-", ".map");
        final String staticMapFileContents = "uid 10 100\n" + ((((((((((("gid 10 200\n" + "uid 11 201 # comment at the end of a line\n") + "uid 12 301\n") + "# Comment at the beginning of a line\n") + "    # Comment that starts late in the line\n") + "uid 10000 10001# line without whitespace before comment\n") + "uid 13 302\n") + "gid\t11\t201\n")// Tabs instead of spaces.
         + "\n")// Entirely empty line.
         + "gid 12 202\n") + "uid 4294967294 123\n") + "gid 4294967295 321");
        createStaticMapFile(tempStaticMapFile, staticMapFileContents);
        StaticMapping parsedMap = ShellBasedIdMapping.parseStaticMap(tempStaticMapFile);
        Assert.assertEquals(10, ((int) (parsedMap.uidMapping.get(100))));
        Assert.assertEquals(11, ((int) (parsedMap.uidMapping.get(201))));
        Assert.assertEquals(12, ((int) (parsedMap.uidMapping.get(301))));
        Assert.assertEquals(13, ((int) (parsedMap.uidMapping.get(302))));
        Assert.assertEquals(10, ((int) (parsedMap.gidMapping.get(200))));
        Assert.assertEquals(11, ((int) (parsedMap.gidMapping.get(201))));
        Assert.assertEquals(12, ((int) (parsedMap.gidMapping.get(202))));
        Assert.assertEquals(10000, ((int) (parsedMap.uidMapping.get(10001))));
        // Ensure pass-through of unmapped IDs works.
        Assert.assertEquals(1000, ((int) (parsedMap.uidMapping.get(1000))));
        Assert.assertEquals((-2), ((int) (parsedMap.uidMapping.get(123))));
        Assert.assertEquals((-1), ((int) (parsedMap.gidMapping.get(321))));
    }

    @Test
    public void testStaticMapping() throws IOException {
        PlatformAssumptions.assumeNotWindows();
        Map<Integer, Integer> uidStaticMap = new org.apache.hadoop.security.ShellBasedIdMapping.PassThroughMap<Integer>();
        Map<Integer, Integer> gidStaticMap = new org.apache.hadoop.security.ShellBasedIdMapping.PassThroughMap<Integer>();
        uidStaticMap.put(11501, 10);
        gidStaticMap.put(497, 200);
        // Maps for id to name map
        BiMap<Integer, String> uMap = HashBiMap.create();
        BiMap<Integer, String> gMap = HashBiMap.create();
        String GET_ALL_USERS_CMD = "echo \"atm:x:1000:1000:Aaron T. Myers,,,:/home/atm:/bin/bash\n" + ("hdfs:x:11501:10787:Grid Distributed File System:/home/hdfs:/bin/bash\"" + " | cut -d: -f1,3");
        String GET_ALL_GROUPS_CMD = "echo \"hdfs:*:11501:hrt_hdfs\n" + (("mapred:x:497\n" + "mapred2:x:498\"") + " | cut -d: -f1,3");
        ShellBasedIdMapping.updateMapInternal(uMap, "user", GET_ALL_USERS_CMD, ":", uidStaticMap);
        ShellBasedIdMapping.updateMapInternal(gMap, "group", GET_ALL_GROUPS_CMD, ":", gidStaticMap);
        Assert.assertEquals("hdfs", uMap.get(10));
        Assert.assertEquals(10, ((int) (uMap.inverse().get("hdfs"))));
        Assert.assertEquals("atm", uMap.get(1000));
        Assert.assertEquals(1000, ((int) (uMap.inverse().get("atm"))));
        Assert.assertEquals("hdfs", gMap.get(11501));
        Assert.assertEquals(11501, ((int) (gMap.inverse().get("hdfs"))));
        Assert.assertEquals("mapred", gMap.get(200));
        Assert.assertEquals(200, ((int) (gMap.inverse().get("mapred"))));
        Assert.assertEquals("mapred2", gMap.get(498));
        Assert.assertEquals(498, ((int) (gMap.inverse().get("mapred2"))));
    }

    // Test staticMap refreshing
    @Test
    public void testStaticMapUpdate() throws IOException {
        PlatformAssumptions.assumeNotWindows();
        File tempStaticMapFile = File.createTempFile("nfs-", ".map");
        tempStaticMapFile.delete();
        Configuration conf = new Configuration();
        conf.setLong(USERGROUPID_UPDATE_MILLIS_KEY, 1000);
        conf.set(STATIC_ID_MAPPING_FILE_KEY, tempStaticMapFile.getPath());
        ShellBasedIdMapping refIdMapping = new ShellBasedIdMapping(conf, true);
        ShellBasedIdMapping incrIdMapping = new ShellBasedIdMapping(conf);
        BiMap<Integer, String> uidNameMap = refIdMapping.getUidNameMap();
        BiMap<Integer, String> gidNameMap = refIdMapping.getGidNameMap();
        // Force empty map, to see effect of incremental map update of calling
        // getUid()
        incrIdMapping.clearNameMaps();
        uidNameMap = refIdMapping.getUidNameMap();
        for (Map.Entry<Integer, String> me : uidNameMap.entrySet()) {
            tempStaticMapFile.delete();
            incrIdMapping.clearNameMaps();
            Integer id = me.getKey();
            String name = me.getValue();
            // The static map is empty, so the id found for "name" would be
            // the same as "id"
            Integer nid = incrIdMapping.getUid(name);
            Assert.assertEquals(id, nid);
            // Clear map and update staticMap file
            incrIdMapping.clearNameMaps();
            Integer rid = id + 10000;
            String smapStr = (("uid " + rid) + " ") + id;
            createStaticMapFile(tempStaticMapFile, smapStr);
            // Now the id found for "name" should be the id specified by
            // the staticMap
            nid = incrIdMapping.getUid(name);
            Assert.assertEquals(rid, nid);
        }
        // Force empty map, to see effect of incremental map update of calling
        // getGid()
        incrIdMapping.clearNameMaps();
        gidNameMap = refIdMapping.getGidNameMap();
        for (Map.Entry<Integer, String> me : gidNameMap.entrySet()) {
            tempStaticMapFile.delete();
            incrIdMapping.clearNameMaps();
            Integer id = me.getKey();
            String name = me.getValue();
            // The static map is empty, so the id found for "name" would be
            // the same as "id"
            Integer nid = incrIdMapping.getGid(name);
            Assert.assertEquals(id, nid);
            // Clear map and update staticMap file
            incrIdMapping.clearNameMaps();
            Integer rid = id + 10000;
            String smapStr = (("gid " + rid) + " ") + id;
            // Sleep a bit to avoid that two changes have the same modification time
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                // Do nothing
            }
            createStaticMapFile(tempStaticMapFile, smapStr);
            // Now the id found for "name" should be the id specified by
            // the staticMap
            nid = incrIdMapping.getGid(name);
            Assert.assertEquals(rid, nid);
        }
    }

    @Test
    public void testDuplicates() throws IOException {
        PlatformAssumptions.assumeNotWindows();
        String GET_ALL_USERS_CMD = "echo \"root:x:0:0:root:/root:/bin/bash\n" + (((((((("hdfs:x:11501:10787:Grid Distributed File System:/home/hdfs:/bin/bash\n" + "hdfs:x:11502:10788:Grid Distributed File System:/home/hdfs:/bin/bash\n") + "hdfs1:x:11501:10787:Grid Distributed File System:/home/hdfs:/bin/bash\n") + "hdfs2:x:11502:10787:Grid Distributed File System:/home/hdfs:/bin/bash\n") + "bin:x:2:2:bin:/bin:/bin/sh\n") + "bin:x:1:1:bin:/bin:/sbin/nologin\n") + "daemon:x:1:1:daemon:/usr/sbin:/bin/sh\n") + "daemon:x:2:2:daemon:/sbin:/sbin/nologin\"") + " | cut -d: -f1,3");
        String GET_ALL_GROUPS_CMD = "echo \"hdfs:*:11501:hrt_hdfs\n" + (((("mapred:x:497\n" + "mapred2:x:497\n") + "mapred:x:498\n") + "mapred3:x:498\"") + " | cut -d: -f1,3");
        // Maps for id to name map
        BiMap<Integer, String> uMap = HashBiMap.create();
        BiMap<Integer, String> gMap = HashBiMap.create();
        ShellBasedIdMapping.updateMapInternal(uMap, "user", GET_ALL_USERS_CMD, ":", TestShellBasedIdMapping.EMPTY_PASS_THROUGH_MAP);
        Assert.assertEquals(5, uMap.size());
        Assert.assertEquals("root", uMap.get(0));
        Assert.assertEquals("hdfs", uMap.get(11501));
        Assert.assertEquals("hdfs2", uMap.get(11502));
        Assert.assertEquals("bin", uMap.get(2));
        Assert.assertEquals("daemon", uMap.get(1));
        ShellBasedIdMapping.updateMapInternal(gMap, "group", GET_ALL_GROUPS_CMD, ":", TestShellBasedIdMapping.EMPTY_PASS_THROUGH_MAP);
        Assert.assertTrue(((gMap.size()) == 3));
        Assert.assertEquals("hdfs", gMap.get(11501));
        Assert.assertEquals("mapred", gMap.get(497));
        Assert.assertEquals("mapred3", gMap.get(498));
    }

    @Test
    public void testIdOutOfIntegerRange() throws IOException {
        PlatformAssumptions.assumeNotWindows();
        String GET_ALL_USERS_CMD = "echo \"" + ((((((("nfsnobody:x:4294967294:4294967294:Anonymous NFS User:/var/lib/nfs:/sbin/nologin\n" + "nfsnobody1:x:4294967295:4294967295:Anonymous NFS User:/var/lib/nfs1:/sbin/nologin\n") + "maxint:x:2147483647:2147483647:Grid Distributed File System:/home/maxint:/bin/bash\n") + "minint:x:2147483648:2147483648:Grid Distributed File System:/home/minint:/bin/bash\n") + "archivebackup:*:1031:4294967294:Archive Backup:/home/users/archivebackup:/bin/sh\n") + "hdfs:x:11501:10787:Grid Distributed File System:/home/hdfs:/bin/bash\n") + "daemon:x:2:2:daemon:/sbin:/sbin/nologin\"") + " | cut -d: -f1,3");
        String GET_ALL_GROUPS_CMD = "echo \"" + ((((((("hdfs:*:11501:hrt_hdfs\n" + "rpcuser:*:29:\n") + "nfsnobody:*:4294967294:\n") + "nfsnobody1:*:4294967295:\n") + "maxint:*:2147483647:\n") + "minint:*:2147483648:\n") + "mapred3:x:498\"") + " | cut -d: -f1,3");
        // Maps for id to name map
        BiMap<Integer, String> uMap = HashBiMap.create();
        BiMap<Integer, String> gMap = HashBiMap.create();
        ShellBasedIdMapping.updateMapInternal(uMap, "user", GET_ALL_USERS_CMD, ":", TestShellBasedIdMapping.EMPTY_PASS_THROUGH_MAP);
        Assert.assertTrue(((uMap.size()) == 7));
        Assert.assertEquals("nfsnobody", uMap.get((-2)));
        Assert.assertEquals("nfsnobody1", uMap.get((-1)));
        Assert.assertEquals("maxint", uMap.get(2147483647));
        Assert.assertEquals("minint", uMap.get(-2147483648));
        Assert.assertEquals("archivebackup", uMap.get(1031));
        Assert.assertEquals("hdfs", uMap.get(11501));
        Assert.assertEquals("daemon", uMap.get(2));
        ShellBasedIdMapping.updateMapInternal(gMap, "group", GET_ALL_GROUPS_CMD, ":", TestShellBasedIdMapping.EMPTY_PASS_THROUGH_MAP);
        Assert.assertTrue(((gMap.size()) == 7));
        Assert.assertEquals("hdfs", gMap.get(11501));
        Assert.assertEquals("rpcuser", gMap.get(29));
        Assert.assertEquals("nfsnobody", gMap.get((-2)));
        Assert.assertEquals("nfsnobody1", gMap.get((-1)));
        Assert.assertEquals("maxint", gMap.get(2147483647));
        Assert.assertEquals("minint", gMap.get(-2147483648));
        Assert.assertEquals("mapred3", gMap.get(498));
    }

    @Test
    public void testUserUpdateSetting() throws IOException {
        ShellBasedIdMapping iug = new ShellBasedIdMapping(new Configuration());
        Assert.assertEquals(iug.getTimeout(), USERGROUPID_UPDATE_MILLIS_DEFAULT);
        Configuration conf = new Configuration();
        conf.setLong(USERGROUPID_UPDATE_MILLIS_KEY, 0);
        iug = new ShellBasedIdMapping(conf);
        Assert.assertEquals(iug.getTimeout(), USERGROUPID_UPDATE_MILLIS_MIN);
        conf.setLong(USERGROUPID_UPDATE_MILLIS_KEY, ((USERGROUPID_UPDATE_MILLIS_DEFAULT) * 2));
        iug = new ShellBasedIdMapping(conf);
        Assert.assertEquals(iug.getTimeout(), ((USERGROUPID_UPDATE_MILLIS_DEFAULT) * 2));
    }

    @Test
    public void testUpdateMapIncr() throws IOException {
        Configuration conf = new Configuration();
        conf.setLong(USERGROUPID_UPDATE_MILLIS_KEY, 600000);
        ShellBasedIdMapping refIdMapping = new ShellBasedIdMapping(conf, true);
        ShellBasedIdMapping incrIdMapping = new ShellBasedIdMapping(conf);
        // Command such as "getent passwd <userName>" will return empty string if
        // <username> is numerical, remove them from the map for testing purpose.
        BiMap<Integer, String> uidNameMap = refIdMapping.getUidNameMap();
        BiMap<Integer, String> gidNameMap = refIdMapping.getGidNameMap();
        // Force empty map, to see effect of incremental map update of calling
        // getUserName()
        incrIdMapping.clearNameMaps();
        uidNameMap = refIdMapping.getUidNameMap();
        for (Map.Entry<Integer, String> me : uidNameMap.entrySet()) {
            Integer id = me.getKey();
            String name = me.getValue();
            String tname = incrIdMapping.getUserName(id, null);
            Assert.assertEquals(name, tname);
        }
        Assert.assertEquals(uidNameMap.size(), incrIdMapping.getUidNameMap().size());
        // Force empty map, to see effect of incremental map update of calling
        // getUid()
        incrIdMapping.clearNameMaps();
        for (Map.Entry<Integer, String> me : uidNameMap.entrySet()) {
            Integer id = me.getKey();
            String name = me.getValue();
            Integer tid = incrIdMapping.getUid(name);
            Assert.assertEquals(id, tid);
        }
        Assert.assertEquals(uidNameMap.size(), incrIdMapping.getUidNameMap().size());
        // Force empty map, to see effect of incremental map update of calling
        // getGroupName()
        incrIdMapping.clearNameMaps();
        gidNameMap = refIdMapping.getGidNameMap();
        for (Map.Entry<Integer, String> me : gidNameMap.entrySet()) {
            Integer id = me.getKey();
            String name = me.getValue();
            String tname = incrIdMapping.getGroupName(id, null);
            Assert.assertEquals(name, tname);
        }
        Assert.assertEquals(gidNameMap.size(), incrIdMapping.getGidNameMap().size());
        // Force empty map, to see effect of incremental map update of calling
        // getGid()
        incrIdMapping.clearNameMaps();
        gidNameMap = refIdMapping.getGidNameMap();
        for (Map.Entry<Integer, String> me : gidNameMap.entrySet()) {
            Integer id = me.getKey();
            String name = me.getValue();
            Integer tid = incrIdMapping.getGid(name);
            Assert.assertEquals(id, tid);
        }
        Assert.assertEquals(gidNameMap.size(), incrIdMapping.getGidNameMap().size());
    }
}

