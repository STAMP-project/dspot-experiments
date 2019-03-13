/**
 * *
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements. See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership. The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License. You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * /
 */
package org.apache.hadoop.yarn.server.nodemanager;


import YarnConfiguration.NM_NETWORK_TAG_MAPPING_FILE_PATH;
import java.io.File;
import java.util.List;
import java.util.Map;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.linux.resources.NetworkTagMappingJsonManager;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.linux.resources.NetworkTagMappingJsonManager.Group;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.linux.resources.NetworkTagMappingJsonManager.NetworkTagMapping;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.linux.resources.NetworkTagMappingJsonManager.User;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test NetworkTagMapping Json Manager.
 */
public class TestNetworkTagMappingJsonManager {
    private Path jsonDirDirPath = new Path("target/json");

    private Configuration conf = new YarnConfiguration();

    private FileSystem fs;

    @Test(timeout = 10000)
    public void testNetworkMappingJsonManager() throws Exception {
        Path jsonFilePath = new Path(jsonDirDirPath, "test.json");
        File jsonFile = new File(jsonFilePath.toString());
        NetworkTagMappingJsonManager manager = new NetworkTagMappingJsonManager();
        JSONObject json = new JSONObject();
        JSONArray userArray = new JSONArray();
        // add users
        Map<String, String> createdUsers = createUserNetworkTagIDMapping();
        for (Map.Entry<String, String> user : createdUsers.entrySet()) {
            JSONObject userJson = new JSONObject();
            userJson.put("name", user.getKey());
            userJson.put("network-tag-id", user.getValue());
            userArray.put(userJson);
        }
        // add duplicate user1
        JSONObject duplicateUser1 = new JSONObject();
        duplicateUser1.put("name", "user1");
        duplicateUser1.put("network-tag-id", "0x88888888");
        userArray.put(duplicateUser1);
        json.put("users", userArray);
        JSONArray groupArray = new JSONArray();
        // add groups
        Map<String, String> createdGroups = createGroupNetworkTagIDMapping();
        for (Map.Entry<String, String> group : createdGroups.entrySet()) {
            JSONObject groupJson = new JSONObject();
            groupJson.put("name", group.getKey());
            groupJson.put("network-tag-id", group.getValue());
            groupArray.put(groupJson);
        }
        // add duplicate group1
        JSONObject duplicateGroup1 = new JSONObject();
        duplicateGroup1.put("name", "team1");
        duplicateGroup1.put("network-tag-id", "0x20002003");
        groupArray.put(duplicateGroup1);
        json.put("groups", groupArray);
        writeJson(jsonFile, json.toString());
        conf.set(NM_NETWORK_TAG_MAPPING_FILE_PATH, jsonFile.getAbsolutePath());
        try {
            manager.initialize(conf);
            Assert.fail(("Should get an exception. Becase we did not " + "set default-network-tag-id"));
        } catch (Exception ex) {
            // Do Nothing
        }
        // add default-network-tag-id
        json.put("default-network-tag-id", "0x99999999");
        // remove previous json file
        if (fs.exists(jsonFilePath)) {
            fs.delete(jsonFilePath, false);
        }
        Assert.assertFalse(fs.exists(jsonFilePath));
        writeJson(jsonFile, json.toString());
        manager.initialize(conf);
        NetworkTagMapping networkTagMapping = manager.getNetworkTagMapping();
        // Verify the default-network-tag-id
        Assert.assertTrue((networkTagMapping != null));
        Assert.assertTrue("0x99999999".equals(networkTagMapping.getDefaultNetworkTagID()));
        // Verify the users
        List<User> users = networkTagMapping.getUsers();
        // The number of users should be 4 which is user1, user2, user3 and user4.
        Assert.assertTrue(((users.size()) == 4));
        for (int index = 0; index < (users.size()); index++) {
            String userName = users.get(index).getUserName();
            String classId = users.get(index).getNetworkTagID();
            Assert.assertTrue(createdUsers.containsValue(classId));
            String createdUserName = getUserName(createdUsers, classId);
            Assert.assertTrue(createdUserName.contains(userName));
        }
        // Verify the groups
        List<Group> groups = networkTagMapping.getGroups();
        // The number of groups should be 2 which is team1 and team2.
        Assert.assertTrue(((groups.size()) == 2));
        for (int index = 0; index < (groups.size()); index++) {
            String groupName = groups.get(index).getGroupName();
            String classId = groups.get(index).getNetworkTagID();
            Assert.assertTrue(createdGroups.containsKey(groupName));
            Assert.assertTrue(classId.equals(createdGroups.get(groupName)));
        }
    }

    @Test(timeout = 10000)
    public void testNetworkTagIDMatchPattern() throws Exception {
        Path jsonFilePath = new Path(jsonDirDirPath, "test.json");
        File jsonFile = new File(jsonFilePath.toString());
        NetworkTagMappingJsonManager manager = new NetworkTagMappingJsonManager();
        JSONObject json = new JSONObject();
        JSONArray userArray = new JSONArray();
        JSONObject user1 = new JSONObject();
        user1.put("name", "user1");
        user1.put("network-tag-id", "1x88888888");
        userArray.put(user1);
        json.put("users", userArray);
        writeJson(jsonFile, json.toString());
        conf.set(NM_NETWORK_TAG_MAPPING_FILE_PATH, jsonFile.getAbsolutePath());
        try {
            manager.initialize(conf);
            Assert.fail(("Should get an exception. " + "Becase we did not set network-tag-id for user1 correctly"));
        } catch (Exception ex) {
            // should catch exception here
            Assert.assertTrue(ex.getMessage().contains("User-network-tag-id mapping configuraton error."));
        }
        json.remove("users");
        userArray = new JSONArray();
        user1 = new JSONObject();
        user1.put("name", "user1");
        user1.put("network-tag-id", "0x88888888");
        userArray.put(user1);
        json.put("users", userArray);
        JSONArray groupArray = new JSONArray();
        JSONObject group1 = new JSONObject();
        group1.put("name", "team1");
        group1.put("network-tag-id", "0x2000003");
        groupArray.put(group1);
        json.put("groups", groupArray);
        // remove previous json file
        if (fs.exists(jsonFilePath)) {
            fs.delete(jsonFilePath, false);
        }
        Assert.assertFalse(fs.exists(jsonFilePath));
        writeJson(jsonFile, json.toString());
        try {
            manager.initialize(conf);
            Assert.fail(("Should get an exception. " + "Becase we did not set network-tag-id for group1 correctly"));
        } catch (Exception ex) {
            // should catch exception here
            Assert.assertTrue(ex.getMessage().contains("Group-network-tag-id mapping configuraton error."));
        }
        json.remove("groups");
        groupArray = new JSONArray();
        group1 = new JSONObject();
        group1.put("name", "team1");
        group1.put("network-tag-id", "0x20002003");
        groupArray.put(group1);
        json.put("groups", groupArray);
        json.put("default-network-tag-id", "0x99");
        // remove previous json file
        if (fs.exists(jsonFilePath)) {
            fs.delete(jsonFilePath, false);
        }
        Assert.assertFalse(fs.exists(jsonFilePath));
        writeJson(jsonFile, json.toString());
        try {
            manager.initialize(conf);
            Assert.fail(("Should get an exception. " + "Becase we did not set default-network-tag-id correctly"));
        } catch (Exception ex) {
            // should catch exception here
            Assert.assertTrue(ex.getMessage().contains("Configuration error on default-network-tag-id."));
        }
        json.remove("default-network-tag-id");
        json.put("default-network-tag-id", "0x99999999");
        // remove previous json file
        if (fs.exists(jsonFilePath)) {
            fs.delete(jsonFilePath, false);
        }
        Assert.assertFalse(fs.exists(jsonFilePath));
        writeJson(jsonFile, json.toString());
        manager.initialize(conf);
    }
}

