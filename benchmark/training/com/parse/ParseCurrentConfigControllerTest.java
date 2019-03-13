/**
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse;


import bolts.Task;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


// endregion
public class ParseCurrentConfigControllerTest {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    // region testConstructor
    @Test
    public void testConstructor() {
        File configFile = new File(temporaryFolder.getRoot(), "config");
        ParseCurrentConfigController currentConfigController = new ParseCurrentConfigController(configFile);
        Assert.assertNull(currentConfigController.currentConfig);
    }

    // endregion
    // region testSaveToDisk
    @Test
    public void testSaveToDiskSuccess() throws Exception {
        // Construct sample ParseConfig
        final Date date = new Date();
        final ParseFile file = new ParseFile(new ParseFile.State.Builder().name("image.png").url("http://yarr.com/image.png").build());
        final ParseGeoPoint geoPoint = new ParseGeoPoint(44.484, 26.029);
        final List<Object> list = new ArrayList<Object>() {
            {
                add("foo");
                add("bar");
                add("baz");
            }
        };
        final Map<String, Object> map = new HashMap<String, Object>() {
            {
                put("first", "foo");
                put("second", "bar");
                put("third", "baz");
            }
        };
        final Map<String, Object> sampleConfigParameters = new HashMap<String, Object>() {
            {
                put("string", "value");
                put("int", 42);
                put("double", 0.2778);
                put("trueBool", true);
                put("falseBool", false);
                put("date", date);
                put("file", file);
                put("geoPoint", geoPoint);
                put("array", list);
                put("object", map);
            }
        };
        JSONObject sampleConfigJson = new JSONObject() {
            {
                put("params", NoObjectsEncoder.get().encode(sampleConfigParameters));
            }
        };
        ParseConfig config = ParseConfig.decode(sampleConfigJson, ParseDecoder.get());
        // Save to disk
        File configFile = new File(temporaryFolder.getRoot(), "config");
        ParseCurrentConfigController currentConfigController = new ParseCurrentConfigController(configFile);
        currentConfigController.saveToDisk(config);
        // Verify file on disk
        JSONObject diskConfigJson = new JSONObject(ParseFileUtils.readFileToString(configFile, "UTF-8"));
        Map<String, Object> decodedDiskConfigObject = ((Map<String, Object>) (ParseDecoder.get().decode(diskConfigJson)));
        Map<String, Object> decodedDiskConfigParameters = ((Map<String, Object>) (decodedDiskConfigObject.get("params")));
        Assert.assertEquals(10, decodedDiskConfigParameters.size());
        Assert.assertEquals("value", decodedDiskConfigParameters.get("string"));
        Assert.assertEquals(42, decodedDiskConfigParameters.get("int"));
        Assert.assertEquals(0.2778, decodedDiskConfigParameters.get("double"));
        Assert.assertTrue(((Boolean) (decodedDiskConfigParameters.get("trueBool"))));
        Assert.assertFalse(((Boolean) (decodedDiskConfigParameters.get("falseBool"))));
        Assert.assertEquals(date, decodedDiskConfigParameters.get("date"));
        ParseFile fileAgain = ((ParseFile) (decodedDiskConfigParameters.get("file")));
        Assert.assertEquals(file.getUrl(), fileAgain.getUrl());
        Assert.assertEquals(file.getName(), fileAgain.getName());
        ParseGeoPoint geoPointAgain = ((ParseGeoPoint) (decodedDiskConfigParameters.get("geoPoint")));
        Assert.assertEquals(geoPoint.getLatitude(), geoPointAgain.getLatitude(), 1.0E-7);
        Assert.assertEquals(geoPoint.getLongitude(), geoPointAgain.getLongitude(), 1.0E-7);
        List<Object> listAgain = ((List<Object>) (decodedDiskConfigParameters.get("array")));
        Assert.assertArrayEquals(list.toArray(), listAgain.toArray());
        Map<String, Object> mapAgain = ((Map<String, Object>) (decodedDiskConfigParameters.get("object")));
        Assert.assertEquals(map.size(), mapAgain.size());
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            Assert.assertEquals(entry.getValue(), mapAgain.get(entry.getKey()));
        }
    }

    // TODO(mengyan) Add testSaveToDiskFailIOException when we have a way to handle IOException
    // endregion
    // region testGetFromDisk
    @Test
    public void testGetFromDiskSuccess() throws Exception {
        // Construct sample ParseConfig json
        final Date date = new Date();
        final ParseFile file = new ParseFile(new ParseFile.State.Builder().name("image.png").url("http://yarr.com/image.png").build());
        final ParseGeoPoint geoPoint = new ParseGeoPoint(44.484, 26.029);
        final List<Object> list = new ArrayList<Object>() {
            {
                add("foo");
                add("bar");
                add("baz");
            }
        };
        final Map<String, Object> map = new HashMap<String, Object>() {
            {
                put("first", "foo");
                put("second", "bar");
                put("third", "baz");
            }
        };
        final Map<String, Object> sampleConfigParameters = new HashMap<String, Object>() {
            {
                put("string", "value");
                put("int", 42);
                put("double", 0.2778);
                put("trueBool", true);
                put("falseBool", false);
                put("date", date);
                put("file", file);
                put("geoPoint", geoPoint);
                put("array", list);
                put("object", map);
            }
        };
        JSONObject sampleConfigJson = new JSONObject() {
            {
                put("params", NoObjectsEncoder.get().encode(sampleConfigParameters));
            }
        };
        ParseConfig config = ParseConfig.decode(sampleConfigJson, ParseDecoder.get());
        // Save to disk
        File configFile = new File(temporaryFolder.getRoot(), "config");
        ParseCurrentConfigController currentConfigController = new ParseCurrentConfigController(configFile);
        currentConfigController.saveToDisk(config);
        // Verify ParseConfig we get from getFromDisk
        ParseConfig configAgain = currentConfigController.getFromDisk();
        Map<String, Object> paramsAgain = configAgain.getParams();
        Assert.assertEquals(10, paramsAgain.size());
        Assert.assertEquals("value", paramsAgain.get("string"));
        Assert.assertEquals(42, paramsAgain.get("int"));
        Assert.assertEquals(0.2778, paramsAgain.get("double"));
        Assert.assertTrue(((Boolean) (paramsAgain.get("trueBool"))));
        Assert.assertFalse(((Boolean) (paramsAgain.get("falseBool"))));
        Assert.assertEquals(date, paramsAgain.get("date"));
        ParseFile fileAgain = ((ParseFile) (paramsAgain.get("file")));
        Assert.assertEquals(file.getUrl(), fileAgain.getUrl());
        Assert.assertEquals(file.getName(), fileAgain.getName());
        ParseGeoPoint geoPointAgain = ((ParseGeoPoint) (paramsAgain.get("geoPoint")));
        Assert.assertEquals(geoPoint.getLatitude(), geoPointAgain.getLatitude(), 1.0E-7);
        Assert.assertEquals(geoPoint.getLongitude(), geoPointAgain.getLongitude(), 1.0E-7);
        List<Object> listAgain = ((List<Object>) (paramsAgain.get("array")));
        Assert.assertArrayEquals(list.toArray(), listAgain.toArray());
        Map<String, Object> mapAgain = ((Map<String, Object>) (paramsAgain.get("object")));
        Assert.assertEquals(map.size(), mapAgain.size());
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            Assert.assertEquals(entry.getValue(), mapAgain.get(entry.getKey()));
        }
    }

    @Test
    public void testGetFromDiskConfigSuccessFileIOException() {
        File configFile = new File("errorConfigFile");
        ParseCurrentConfigController currentConfigController = new ParseCurrentConfigController(configFile);
        ParseConfig config = currentConfigController.getFromDisk();
        Assert.assertNull(config);
    }

    @Test
    public void testGetFromDiskSuccessConfigFileNotJsonFile() throws Exception {
        File configFile = new File(temporaryFolder.getRoot(), "config");
        ParseFileUtils.writeStringToFile(configFile, "notJson", "UTF-8");
        ParseCurrentConfigController currentConfigController = new ParseCurrentConfigController(configFile);
        ParseConfig config = currentConfigController.getFromDisk();
        Assert.assertNull(config);
    }

    // endregion
    // region testSetCurrentConfigAsync
    @Test
    public void testSetCurrentConfigAsyncSuccess() throws Exception {
        File configFile = new File(temporaryFolder.getRoot(), "config");
        ParseCurrentConfigController currentConfigController = new ParseCurrentConfigController(configFile);
        // Verify before set, file is empty and in memory config is null
        Assert.assertFalse(configFile.exists());
        Assert.assertNull(currentConfigController.currentConfig);
        ParseConfig config = new ParseConfig();
        ParseTaskUtils.wait(currentConfigController.setCurrentConfigAsync(config));
        // Verify after set, file exists(saveToDisk is called) and in memory config is set
        Assert.assertTrue(configFile.exists());
        Assert.assertSame(config, currentConfigController.currentConfig);
    }

    // endregion
    // region testGetCurrentConfigAsync
    @Test
    public void testGetCurrentConfigAsyncSuccessCurrentConfigAlreadySet() throws Exception {
        File configFile = new File(temporaryFolder.getRoot(), "config");
        ParseCurrentConfigController currentConfigController = new ParseCurrentConfigController(configFile);
        ParseConfig config = new ParseConfig();
        currentConfigController.currentConfig = config;
        Task<ParseConfig> getTask = currentConfigController.getCurrentConfigAsync();
        ParseTaskUtils.wait(getTask);
        ParseConfig configAgain = getTask.getResult();
        // Verify we get the same ParseConfig when currentConfig is set
        Assert.assertSame(config, configAgain);
    }

    @Test
    public void testGetCurrentConfigAsyncSuccessCurrentConfigNotSetDiskConfigExist() throws Exception {
        File configFile = new File(temporaryFolder.getRoot(), "config");
        ParseCurrentConfigController currentConfigController = new ParseCurrentConfigController(configFile);
        // Save sample ParseConfig to disk
        final Map<String, Object> sampleConfigParameters = new HashMap<String, Object>() {
            {
                put("string", "value");
            }
        };
        JSONObject sampleConfigJson = new JSONObject() {
            {
                put("params", NoObjectsEncoder.get().encode(sampleConfigParameters));
            }
        };
        ParseConfig diskConfig = ParseConfig.decode(sampleConfigJson, ParseDecoder.get());
        currentConfigController.saveToDisk(diskConfig);
        // Verify before set, disk config exist and in memory config is null
        Assert.assertTrue(configFile.exists());
        Assert.assertNull(currentConfigController.currentConfig);
        Task<ParseConfig> getTask = currentConfigController.getCurrentConfigAsync();
        ParseTaskUtils.wait(getTask);
        ParseConfig config = getTask.getResult();
        // Verify after set, in memory config is set and value is correct
        Assert.assertSame(config, currentConfigController.currentConfig);
        Assert.assertEquals("value", config.get("string"));
        Assert.assertEquals(1, config.getParams().size());
    }

    @Test
    public void testGetCurrentConfigAsyncSuccessCurrentConfigNotSetDiskConfigNotExist() throws Exception {
        File configFile = new File(temporaryFolder.getRoot(), "config");
        ParseCurrentConfigController currentConfigController = new ParseCurrentConfigController(configFile);
        // Verify before set, disk config does not exist and in memory config is null
        Assert.assertFalse(configFile.exists());
        Assert.assertNull(currentConfigController.currentConfig);
        Task<ParseConfig> getTask = currentConfigController.getCurrentConfigAsync();
        ParseTaskUtils.wait(getTask);
        ParseConfig config = getTask.getResult();
        // Verify after set, in memory config is set and the config we get is empty
        Assert.assertSame(config, currentConfigController.currentConfig);
        Assert.assertEquals(0, config.getParams().size());
    }

    // endregion
    // region testClearCurrentConfigForTesting
    @Test
    public void testClearCurrentConfigForTestingSuccess() {
        File configFile = new File(temporaryFolder.getRoot(), "config");
        ParseCurrentConfigController currentConfigController = new ParseCurrentConfigController(configFile);
        currentConfigController.currentConfig = new ParseConfig();
        // Verify before set, in memory config is not null
        Assert.assertNotNull(currentConfigController.currentConfig);
        currentConfigController.clearCurrentConfigForTesting();
        // Verify after set, in memory config is null
        Assert.assertNull(currentConfigController.currentConfig);
    }
}

