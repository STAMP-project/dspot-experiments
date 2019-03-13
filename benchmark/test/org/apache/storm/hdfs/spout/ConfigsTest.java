/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.  The ASF licenses this file to you under the Apache License, Version
 * 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */
package org.apache.storm.hdfs.spout;


import Configs.ARCHIVE_DIR;
import Configs.BAD_DIR;
import Configs.CLOCKS_INSYNC;
import Configs.COMMIT_FREQ_COUNT;
import Configs.COMMIT_FREQ_SEC;
import Configs.DEFAULT_HDFS_CONFIG_KEY;
import Configs.HDFS_URI;
import Configs.IGNORE_SUFFIX;
import Configs.LOCK_DIR;
import Configs.LOCK_TIMEOUT;
import Configs.MAX_OUTSTANDING;
import Configs.READER_TYPE;
import Configs.SEQ;
import Configs.SOURCE_DIR;
import Configs.TEXT;
import java.util.HashMap;
import java.util.Map;
import org.apache.storm.validation.ConfigValidation;
import org.junit.Test;


public class ConfigsTest {
    @SuppressWarnings("deprecation")
    @Test
    public void testGood() {
        Map<String, Object> conf = new HashMap<>();
        conf.put(READER_TYPE, TEXT);
        ConfigValidation.validateFields(conf);
        conf.put(READER_TYPE, SEQ);
        ConfigValidation.validateFields(conf);
        conf.put(READER_TYPE, TextFileReader.class.getName());
        ConfigValidation.validateFields(conf);
        conf.put(HDFS_URI, "hdfs://namenode/");
        ConfigValidation.validateFields(conf);
        conf.put(SOURCE_DIR, "/input/source");
        ConfigValidation.validateFields(conf);
        conf.put(ARCHIVE_DIR, "/input/done");
        ConfigValidation.validateFields(conf);
        conf.put(BAD_DIR, "/input/bad");
        ConfigValidation.validateFields(conf);
        conf.put(LOCK_DIR, "/topology/lock");
        ConfigValidation.validateFields(conf);
        conf.put(COMMIT_FREQ_COUNT, 0);
        ConfigValidation.validateFields(conf);
        conf.put(COMMIT_FREQ_COUNT, 100);
        ConfigValidation.validateFields(conf);
        conf.put(COMMIT_FREQ_SEC, 100);
        ConfigValidation.validateFields(conf);
        conf.put(MAX_OUTSTANDING, 500);
        ConfigValidation.validateFields(conf);
        conf.put(LOCK_TIMEOUT, 100);
        ConfigValidation.validateFields(conf);
        conf.put(CLOCKS_INSYNC, true);
        ConfigValidation.validateFields(conf);
        conf.put(IGNORE_SUFFIX, ".writing");
        ConfigValidation.validateFields(conf);
        Map<String, String> hdfsConf = new HashMap<>();
        hdfsConf.put("A", "B");
        conf.put(DEFAULT_HDFS_CONFIG_KEY, hdfsConf);
        ConfigValidation.validateFields(conf);
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testBad() {
        ConfigsTest.verifyBad(READER_TYPE, "SomeString");
        ConfigsTest.verifyBad(HDFS_URI, 100);
        ConfigsTest.verifyBad(COMMIT_FREQ_COUNT, (-10));
    }
}

