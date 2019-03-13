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
package org.datavec.spark;


import java.io.File;
import org.apache.spark.serializer.KryoSerializerInstance;
import org.apache.spark.serializer.SerializerInstance;
import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.datavec.api.split.FileSplit;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.io.ClassPathResource;


public class TestKryoSerialization extends BaseSparkTest {
    @Test
    public void testCsvRecordReader() throws Exception {
        SerializerInstance si = BaseSparkTest.sc.env().serializer().newInstance();
        Assert.assertTrue((si instanceof KryoSerializerInstance));
        RecordReader r1 = new CSVRecordReader(1, '\t');
        RecordReader r2 = serDe(r1, si);
        File f = new ClassPathResource("iris_tab_delim.txt").getFile();
        r1.initialize(new FileSplit(f));
        r2.initialize(new FileSplit(f));
        while (r1.hasNext()) {
            Assert.assertEquals(r1.next(), r2.next());
        } 
        Assert.assertFalse(r2.hasNext());
    }
}

