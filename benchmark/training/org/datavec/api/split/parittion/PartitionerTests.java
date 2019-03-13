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
package org.datavec.api.split.parittion;


import NumberOfRecordsPartitioner.RECORDS_PER_FILE_CONFIG;
import com.google.common.io.Files;
import java.io.File;
import java.io.OutputStream;
import junit.framework.TestCase;
import org.datavec.api.conf.Configuration;
import org.datavec.api.split.FileSplit;
import org.datavec.api.split.partition.NumberOfRecordsPartitioner;
import org.datavec.api.split.partition.PartitionMetaData;
import org.datavec.api.split.partition.Partitioner;
import org.junit.Assert;
import org.junit.Test;


public class PartitionerTests {
    @Test
    public void testRecordsPerFilePartition() {
        Partitioner partitioner = new NumberOfRecordsPartitioner();
        File tmpDir = Files.createTempDir();
        FileSplit fileSplit = new FileSplit(tmpDir);
        TestCase.assertTrue(fileSplit.needsBootstrapForWrite());
        fileSplit.bootStrapForWrite();
        partitioner.init(fileSplit);
        Assert.assertEquals(1, partitioner.numPartitions());
    }

    @Test
    public void testInputAddFile() throws Exception {
        Partitioner partitioner = new NumberOfRecordsPartitioner();
        File tmpDir = Files.createTempDir();
        FileSplit fileSplit = new FileSplit(tmpDir);
        TestCase.assertTrue(fileSplit.needsBootstrapForWrite());
        fileSplit.bootStrapForWrite();
        Configuration configuration = new Configuration();
        configuration.set(RECORDS_PER_FILE_CONFIG, String.valueOf(5));
        partitioner.init(configuration, fileSplit);
        partitioner.updatePartitionInfo(PartitionMetaData.builder().numRecordsUpdated(5).build());
        TestCase.assertTrue(partitioner.needsNewPartition());
        OutputStream os = partitioner.openNewStream();
        os.close();
        Assert.assertNotNull(os);
        // run more than once to ensure output stream creation works properly
        partitioner.updatePartitionInfo(PartitionMetaData.builder().numRecordsUpdated(5).build());
        os = partitioner.openNewStream();
        os.close();
        Assert.assertNotNull(os);
    }
}

