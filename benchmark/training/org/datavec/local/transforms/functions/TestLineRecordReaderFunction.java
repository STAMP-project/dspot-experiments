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
package org.datavec.local.transforms.functions;


import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.commons.io.FileUtils;
import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.datavec.api.split.FileSplit;
import org.datavec.api.writable.Writable;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.io.ClassPathResource;


/**
 * Created by Alex on 21/05/2016.
 */
public class TestLineRecordReaderFunction {
    @Test
    public void testLineRecordReader() throws Exception {
        File dataFile = new ClassPathResource("iris.dat").getFile();
        List<String> lines = FileUtils.readLines(dataFile);
        List<String> linesRdd = lines;
        CSVRecordReader rr = new CSVRecordReader(0, ',');
        List<List<Writable>> out = linesRdd.stream().map(( input) -> apply(input)).collect(Collectors.toList());
        List<List<Writable>> outList = out;
        CSVRecordReader rr2 = new CSVRecordReader(0, ',');
        rr2.initialize(new FileSplit(dataFile));
        Set<List<Writable>> expectedSet = new HashSet<>();
        int totalCount = 0;
        while (rr2.hasNext()) {
            expectedSet.add(rr2.next());
            totalCount++;
        } 
        Assert.assertEquals(totalCount, outList.size());
        for (List<Writable> line : outList) {
            Assert.assertTrue(expectedSet.contains(line));
        }
    }
}

