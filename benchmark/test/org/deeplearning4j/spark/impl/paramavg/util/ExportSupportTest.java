/**
 * *****************************************************************************
 * Copyright (c) 2015-2018 Skymind, Inc.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 * ****************************************************************************
 */
package org.deeplearning4j.spark.impl.paramavg.util;


import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.spark.SparkConf;
import org.junit.Test;


/**
 *
 *
 * @author Ede Meijer
 */
public class ExportSupportTest {
    private static final String FS_CONF = "spark.hadoop.fs.defaultFS";

    @Test
    public void testLocalSupported() throws IOException {
        assertSupported(new SparkConf().setMaster("local").set(ExportSupportTest.FS_CONF, "file:///"));
        assertSupported(new SparkConf().setMaster("local[2]").set(ExportSupportTest.FS_CONF, "file:///"));
        assertSupported(new SparkConf().setMaster("local[64]").set(ExportSupportTest.FS_CONF, "file:///"));
        assertSupported(new SparkConf().setMaster("local[*]").set(ExportSupportTest.FS_CONF, "file:///"));
        assertSupported(new SparkConf().setMaster("local").set(ExportSupportTest.FS_CONF, "hdfs://localhost:9000"));
    }

    @Test
    public void testClusterWithRemoteFSSupported() throws IOException, URISyntaxException {
        assertSupported("spark://localhost:7077", FileSystem.get(new URI("hdfs://localhost:9000"), new Configuration()), true);
    }

    @Test
    public void testClusterWithLocalFSNotSupported() throws IOException, URISyntaxException {
        assertSupported("spark://localhost:7077", FileSystem.get(new URI("file:///home/test"), new Configuration()), false);
    }
}

