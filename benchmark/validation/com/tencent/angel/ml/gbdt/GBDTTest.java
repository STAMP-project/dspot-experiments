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
package com.tencent.angel.ml.gbdt;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.LocalFileSystem;
import org.apache.log4j.PropertyConfigurator;
import org.junit.Test;


/**
 * GBDT UT.
 */
public class GBDTTest {
    private static final Log LOG = LogFactory.getLog(GBDTTest.class);

    private Configuration conf = new Configuration();

    private static final String LOCAL_FS = LocalFileSystem.DEFAULT_FS;

    private static final String TMP_PATH = System.getProperty("java.io.tmpdir", "/tmp");

    private static final String TrainInputPath = "../../data/agaricus/agaricus_127d_train.libsvm";

    private static final String PredictInputPath = "../../data/agaricus/agaricus_127d_test.libsvm";

    static {
        PropertyConfigurator.configure("../conf/log4j.properties");
    }

    @Test
    public void testGBDT() throws Exception {
        setConf();
        trainTest();
        predictTest();
    }
}

