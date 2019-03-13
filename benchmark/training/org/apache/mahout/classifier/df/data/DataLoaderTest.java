/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.mahout.classifier.df.data;


import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.Random;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.mahout.classifier.df.data.Dataset.Attribute;
import org.apache.mahout.common.MahoutTestCase;
import org.junit.Test;


@Deprecated
public final class DataLoaderTest extends MahoutTestCase {
    private Random rng;

    @Test
    public void testLoadDataWithDescriptor() throws Exception {
        int nbAttributes = 10;
        int datasize = 100;
        // prepare the descriptors
        String descriptor = Utils.randomDescriptor(rng, nbAttributes);
        Attribute[] attrs = DescriptorUtils.parseDescriptor(descriptor);
        // prepare the data
        double[][] data = Utils.randomDoubles(rng, descriptor, false, datasize);
        Collection<Integer> missings = Lists.newArrayList();
        String[] sData = prepareData(data, attrs, missings);
        Dataset dataset = DataLoader.generateDataset(descriptor, false, sData);
        Data loaded = DataLoader.loadData(dataset, sData);
        DataLoaderTest.testLoadedData(data, attrs, missings, loaded);
        DataLoaderTest.testLoadedDataset(data, attrs, missings, loaded);
        // regression
        data = Utils.randomDoubles(rng, descriptor, true, datasize);
        missings = Lists.newArrayList();
        sData = prepareData(data, attrs, missings);
        dataset = DataLoader.generateDataset(descriptor, true, sData);
        loaded = DataLoader.loadData(dataset, sData);
        DataLoaderTest.testLoadedData(data, attrs, missings, loaded);
        DataLoaderTest.testLoadedDataset(data, attrs, missings, loaded);
    }

    /**
     * Test method for
     * {@link DataLoader#generateDataset(CharSequence, boolean, String[])}
     */
    @Test
    public void testGenerateDataset() throws Exception {
        int nbAttributes = 10;
        int datasize = 100;
        // prepare the descriptors
        String descriptor = Utils.randomDescriptor(rng, nbAttributes);
        Attribute[] attrs = DescriptorUtils.parseDescriptor(descriptor);
        // prepare the data
        double[][] data = Utils.randomDoubles(rng, descriptor, false, datasize);
        Collection<Integer> missings = Lists.newArrayList();
        String[] sData = prepareData(data, attrs, missings);
        Dataset expected = DataLoader.generateDataset(descriptor, false, sData);
        Dataset dataset = DataLoader.generateDataset(descriptor, false, sData);
        assertEquals(expected, dataset);
        // regression
        data = Utils.randomDoubles(rng, descriptor, true, datasize);
        missings = Lists.newArrayList();
        sData = prepareData(data, attrs, missings);
        expected = DataLoader.generateDataset(descriptor, true, sData);
        dataset = DataLoader.generateDataset(descriptor, true, sData);
        assertEquals(expected, dataset);
    }

    @Test
    public void testLoadDataFromFile() throws Exception {
        int nbAttributes = 10;
        int datasize = 100;
        // prepare the descriptors
        String descriptor = Utils.randomDescriptor(rng, nbAttributes);
        Attribute[] attrs = DescriptorUtils.parseDescriptor(descriptor);
        // prepare the data
        double[][] source = Utils.randomDoubles(rng, descriptor, false, datasize);
        Collection<Integer> missings = Lists.newArrayList();
        String[] sData = prepareData(source, attrs, missings);
        Dataset dataset = DataLoader.generateDataset(descriptor, false, sData);
        Path dataPath = Utils.writeDataToTestFile(sData);
        FileSystem fs = dataPath.getFileSystem(getConfiguration());
        Data loaded = DataLoader.loadData(dataset, fs, dataPath);
        DataLoaderTest.testLoadedData(source, attrs, missings, loaded);
        // regression
        source = Utils.randomDoubles(rng, descriptor, true, datasize);
        missings = Lists.newArrayList();
        sData = prepareData(source, attrs, missings);
        dataset = DataLoader.generateDataset(descriptor, true, sData);
        dataPath = Utils.writeDataToTestFile(sData);
        fs = dataPath.getFileSystem(getConfiguration());
        loaded = DataLoader.loadData(dataset, fs, dataPath);
        DataLoaderTest.testLoadedData(source, attrs, missings, loaded);
    }

    /**
     * Test method for
     * {@link DataLoader#generateDataset(CharSequence, boolean, FileSystem, Path)}
     */
    @Test
    public void testGenerateDatasetFromFile() throws Exception {
        int nbAttributes = 10;
        int datasize = 100;
        // prepare the descriptors
        String descriptor = Utils.randomDescriptor(rng, nbAttributes);
        Attribute[] attrs = DescriptorUtils.parseDescriptor(descriptor);
        // prepare the data
        double[][] source = Utils.randomDoubles(rng, descriptor, false, datasize);
        Collection<Integer> missings = Lists.newArrayList();
        String[] sData = prepareData(source, attrs, missings);
        Dataset expected = DataLoader.generateDataset(descriptor, false, sData);
        Path path = Utils.writeDataToTestFile(sData);
        FileSystem fs = path.getFileSystem(getConfiguration());
        Dataset dataset = DataLoader.generateDataset(descriptor, false, fs, path);
        assertEquals(expected, dataset);
        // regression
        source = Utils.randomDoubles(rng, descriptor, false, datasize);
        missings = Lists.newArrayList();
        sData = prepareData(source, attrs, missings);
        expected = DataLoader.generateDataset(descriptor, false, sData);
        path = Utils.writeDataToTestFile(sData);
        fs = path.getFileSystem(getConfiguration());
        dataset = DataLoader.generateDataset(descriptor, false, fs, path);
        assertEquals(expected, dataset);
    }
}

