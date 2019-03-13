/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hbase.regionserver.compactions;


import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.regionserver.HStore;
import org.apache.hadoop.hbase.regionserver.HStoreFile;
import org.apache.hadoop.hbase.regionserver.StoreConfigInformation;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.testclassification.RegionServerTests;
import org.apache.hadoop.hbase.util.ReflectionUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@Category({ RegionServerTests.class, MediumTests.class })
@RunWith(Parameterized.class)
public class PerfTestCompactionPolicies extends MockStoreFileGenerator {
    private final RatioBasedCompactionPolicy cp;

    private final StoreFileListGenerator generator;

    private final HStore store;

    private Class<? extends StoreFileListGenerator> fileGenClass;

    private final int max;

    private final int min;

    private final float ratio;

    private long written = 0;

    /**
     * Test the perf of a CompactionPolicy with settings.
     *
     * @param cpClass
     * 		The compaction policy to test
     * @param inMmax
     * 		The maximum number of file to compact
     * @param inMin
     * 		The min number of files to compact
     * @param inRatio
     * 		The ratio that files must be under to be compacted.
     */
    public PerfTestCompactionPolicies(final Class<? extends CompactionPolicy> cpClass, final Class<? extends StoreFileListGenerator> fileGenClass, final int inMmax, final int inMin, final float inRatio) throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        super(PerfTestCompactionPolicies.class);
        this.fileGenClass = fileGenClass;
        this.max = inMmax;
        this.min = inMin;
        this.ratio = inRatio;
        // Hide lots of logging so the system out is usable as a tab delimited file.
        Logger.getLogger(CompactionConfiguration.class).setLevel(Level.ERROR);
        Logger.getLogger(RatioBasedCompactionPolicy.class).setLevel(Level.ERROR);
        Logger.getLogger(cpClass).setLevel(Level.ERROR);
        Configuration configuration = HBaseConfiguration.create();
        // Make sure that this doesn't include every file.
        configuration.setInt("hbase.hstore.compaction.max", max);
        configuration.setInt("hbase.hstore.compaction.min", min);
        configuration.setFloat("hbase.hstore.compaction.ratio", ratio);
        store = createMockStore();
        this.cp = ReflectionUtils.instantiateWithCustomCtor(cpClass.getName(), new Class[]{ Configuration.class, StoreConfigInformation.class }, new Object[]{ configuration, store });
        this.generator = fileGenClass.getDeclaredConstructor().newInstance();
        // Used for making paths
    }

    @Test
    public final void testSelection() throws Exception {
        long fileDiff = 0;
        for (List<HStoreFile> storeFileList : generator) {
            List<HStoreFile> currentFiles = new ArrayList<>(18);
            for (HStoreFile file : storeFileList) {
                currentFiles.add(file);
                currentFiles = runIteration(currentFiles);
            }
            fileDiff += (storeFileList.size()) - (currentFiles.size());
        }
        // print out tab delimited so that it can be used in excel/gdocs.
        System.out.println((((((((((((((cp.getClass().getSimpleName()) + "\t") + (fileGenClass.getSimpleName())) + "\t") + (max)) + "\t") + (min)) + "\t") + (ratio)) + "\t") + (written)) + "\t") + fileDiff));
    }
}

