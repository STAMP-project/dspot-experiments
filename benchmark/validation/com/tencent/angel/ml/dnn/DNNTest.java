/**
 * Tencent is pleased to support the open source community by making Angel available.
 *
 * Copyright (C) 2017-2018 THL A29 Limited, a Tencent company. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 * https://opensource.org/licenses/Apache-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.tencent.angel.ml.dnn;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.log4j.PropertyConfigurator;
import org.junit.Test;


/**
 * Gradient descent LR UT.
 */
public class DNNTest {
    private Configuration conf = new Configuration();

    private static final Log LOG = LogFactory.getLog(DNNTest.class);

    private static String LOCAL_FS = FileSystem.DEFAULT_FS;

    private static String CLASSBASE = "com.tencent.angel.ml.core.graphsubmit.";

    private static String TMP_PATH = System.getProperty("java.io.tmpdir", "/tmp");

    static {
        PropertyConfigurator.configure("../conf/log4j.properties");
    }

    @Test
    public void testDNN() throws Exception {
        setSystemConf();
        trainTest();
        predictTest();
    }
}

