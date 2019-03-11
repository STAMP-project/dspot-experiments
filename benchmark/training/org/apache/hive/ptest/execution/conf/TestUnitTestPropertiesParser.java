/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.hive.ptest.execution.conf;


import UnitTestPropertiesParser.MODULE_NAME_TOP_LEVEL;
import UnitTestPropertiesParser.PROP_BATCH_SIZE;
import UnitTestPropertiesParser.PROP_DIRECTORIES;
import UnitTestPropertiesParser.PROP_EXCLUDE;
import UnitTestPropertiesParser.PROP_INCLUDE;
import UnitTestPropertiesParser.PROP_ISOLATE;
import UnitTestPropertiesParser.PROP_MODULE_LIST;
import UnitTestPropertiesParser.PROP_ONE_MODULE;
import UnitTestPropertiesParser.PROP_SKIP_BATCHING;
import com.google.common.collect.Sets;
import java.io.File;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static UnitTestPropertiesParser.PREFIX_TOP_LEVEL;


public class TestUnitTestPropertiesParser {
    private static final Logger LOG = LoggerFactory.getLogger(TestUnitTestPropertiesParser.class);

    private static final String MODULE1_NAME = "module1";

    private static final String MODULE1_TEST_NAME = "Module1";

    private static final String MODULE2_NAME = "module2";

    private static final String MODULE2_TEST_NAME = "Module2";

    private static final String TOP_LEVEL_TEST_NAME = "tl";

    private static final String TWO_LEVEL_MODULE1_NAME = "module2l.submodule1";

    private static final String TWO_LEVEL_TEST_NAME = "TwoLevel";

    private static final String THREE_LEVEL_MODULE1_NAME = "module3l.sub.submodule1";

    private static final String THREE_LEVEL_TEST_NAME = "ThreeLevel";

    private static final String MODULE3_REL_DIR = "TwoLevel/module-2.6";

    private static final String MODULE3_MODULE_NAME = "TwoLevel.module-2.6";

    private static final String MODULE3_TEST_NAME = "Module3";

    private static final int BATCH_SIZE_DEFAULT = 10;

    private static final String TEST_CASE_PROPERT_NAME = "test";

    @Test(timeout = 5000)
    public void testSimpleSetup() {
        File baseDir = TestUnitTestPropertiesParser.getFakeTestBaseDir();
        Context context = TestUnitTestPropertiesParser.getDefaultContext();
        FileListProvider flProvider = TestUnitTestPropertiesParser.getTestFileListProvider(baseDir, 5, 4);
        UnitTestPropertiesParser parser = new UnitTestPropertiesParser(context, new AtomicInteger(1), TestUnitTestPropertiesParser.TEST_CASE_PROPERT_NAME, baseDir, TestUnitTestPropertiesParser.LOG, flProvider, null, true);
        Collection<TestBatch> testBatchCollection = parser.generateTestBatches();
        verifyBatches(testBatchCollection, 2, new String[]{ TestUnitTestPropertiesParser.MODULE1_NAME, TestUnitTestPropertiesParser.MODULE2_NAME }, new int[]{ 5, 4 }, new boolean[]{ true, true });
    }

    @Test(timeout = 5000)
    public void testTopLevelExclude() {
        File baseDir = TestUnitTestPropertiesParser.getFakeTestBaseDir();
        Context context = TestUnitTestPropertiesParser.getDefaultContext();
        context.put(TestUnitTestPropertiesParser.getUtRootPropertyName(PROP_EXCLUDE), (("Test" + (TestUnitTestPropertiesParser.MODULE1_TEST_NAME)) + "1"));
        FileListProvider flProvider = TestUnitTestPropertiesParser.getTestFileListProvider(baseDir, 5, 4);
        UnitTestPropertiesParser parser = new UnitTestPropertiesParser(context, new AtomicInteger(1), TestUnitTestPropertiesParser.TEST_CASE_PROPERT_NAME, baseDir, TestUnitTestPropertiesParser.LOG, flProvider, null, true);
        Collection<TestBatch> testBatchCollection = parser.generateTestBatches();
        verifyBatches(testBatchCollection, 2, new String[]{ TestUnitTestPropertiesParser.MODULE1_NAME, TestUnitTestPropertiesParser.MODULE2_NAME }, new int[]{ 4, 4 }, new boolean[]{ true, true });
    }

    @Test(timeout = 5000)
    public void testTopLevelInclude() {
        File baseDir = TestUnitTestPropertiesParser.getFakeTestBaseDir();
        Context context = TestUnitTestPropertiesParser.getDefaultContext();
        context.put(TestUnitTestPropertiesParser.getUtRootPropertyName(PROP_INCLUDE), (((((("Test" + (TestUnitTestPropertiesParser.MODULE1_TEST_NAME)) + "1") + " ") + "Test") + (TestUnitTestPropertiesParser.MODULE1_TEST_NAME)) + "2"));
        FileListProvider flProvider = TestUnitTestPropertiesParser.getTestFileListProvider(baseDir, 5, 4);
        UnitTestPropertiesParser parser = new UnitTestPropertiesParser(context, new AtomicInteger(1), TestUnitTestPropertiesParser.TEST_CASE_PROPERT_NAME, baseDir, TestUnitTestPropertiesParser.LOG, flProvider, null, true);
        Collection<TestBatch> testBatchCollection = parser.generateTestBatches();
        verifyBatches(testBatchCollection, 1, new String[]{ TestUnitTestPropertiesParser.MODULE1_NAME }, new int[]{ 2 }, new boolean[]{ true });
    }

    @Test(timeout = 5000)
    public void testTopLevelSkipBatching() {
        File baseDir = TestUnitTestPropertiesParser.getFakeTestBaseDir();
        Context context = TestUnitTestPropertiesParser.getDefaultContext();
        context.put(TestUnitTestPropertiesParser.getUtRootPropertyName(PROP_SKIP_BATCHING), (((((("Test" + (TestUnitTestPropertiesParser.MODULE1_TEST_NAME)) + "1") + " ") + "Test") + (TestUnitTestPropertiesParser.MODULE1_TEST_NAME)) + "2"));
        FileListProvider flProvider = TestUnitTestPropertiesParser.getTestFileListProvider(baseDir, 5, 4);
        UnitTestPropertiesParser parser = new UnitTestPropertiesParser(context, new AtomicInteger(1), TestUnitTestPropertiesParser.TEST_CASE_PROPERT_NAME, baseDir, TestUnitTestPropertiesParser.LOG, flProvider, null, true);
        Collection<TestBatch> testBatchCollection = parser.generateTestBatches();
        verifyBatches(testBatchCollection, 4, new String[]{ TestUnitTestPropertiesParser.MODULE1_NAME, TestUnitTestPropertiesParser.MODULE1_NAME, TestUnitTestPropertiesParser.MODULE1_NAME, TestUnitTestPropertiesParser.MODULE2_NAME }, new int[]{ 1, 1, 3, 4 }, new boolean[]{ true, true, true, true });
    }

    @Test(timeout = 5000)
    public void testTopLevelIsolate() {
        File baseDir = TestUnitTestPropertiesParser.getFakeTestBaseDir();
        Context context = TestUnitTestPropertiesParser.getDefaultContext();
        context.put(TestUnitTestPropertiesParser.getUtRootPropertyName(PROP_ISOLATE), (((((("Test" + (TestUnitTestPropertiesParser.MODULE1_TEST_NAME)) + "1") + " ") + "Test") + (TestUnitTestPropertiesParser.MODULE1_TEST_NAME)) + "2"));
        FileListProvider flProvider = TestUnitTestPropertiesParser.getTestFileListProvider(baseDir, 5, 4);
        UnitTestPropertiesParser parser = new UnitTestPropertiesParser(context, new AtomicInteger(1), TestUnitTestPropertiesParser.TEST_CASE_PROPERT_NAME, baseDir, TestUnitTestPropertiesParser.LOG, flProvider, null, true);
        Collection<TestBatch> testBatchCollection = parser.generateTestBatches();
        verifyBatches(testBatchCollection, 4, new String[]{ TestUnitTestPropertiesParser.MODULE1_NAME, TestUnitTestPropertiesParser.MODULE1_NAME, TestUnitTestPropertiesParser.MODULE1_NAME, TestUnitTestPropertiesParser.MODULE2_NAME }, new int[]{ 1, 1, 3, 4 }, new boolean[]{ false, false, true, true });
    }

    @Test(timeout = 5000)
    public void testTopLevelBatchSize() {
        File baseDir = TestUnitTestPropertiesParser.getFakeTestBaseDir();
        Context context = TestUnitTestPropertiesParser.getDefaultContext();
        context.put(TestUnitTestPropertiesParser.getUtRootPropertyName(PROP_BATCH_SIZE), Integer.toString(2));
        FileListProvider flProvider = TestUnitTestPropertiesParser.getTestFileListProvider(baseDir, 5, 4);
        UnitTestPropertiesParser parser = new UnitTestPropertiesParser(context, new AtomicInteger(1), TestUnitTestPropertiesParser.TEST_CASE_PROPERT_NAME, baseDir, TestUnitTestPropertiesParser.LOG, flProvider, null, true);
        Collection<TestBatch> testBatchCollection = parser.generateTestBatches();
        verifyBatches(testBatchCollection, 5, new String[]{ TestUnitTestPropertiesParser.MODULE1_NAME, TestUnitTestPropertiesParser.MODULE1_NAME, TestUnitTestPropertiesParser.MODULE1_NAME, TestUnitTestPropertiesParser.MODULE2_NAME, TestUnitTestPropertiesParser.MODULE2_NAME }, new int[]{ 2, 2, 1, 2, 2 }, new boolean[]{ true, true, true, true, true });
    }

    @Test(timeout = 5000)
    public void testModuleLevelExclude() {
        File baseDir = TestUnitTestPropertiesParser.getFakeTestBaseDir();
        Context context = TestUnitTestPropertiesParser.getDefaultContext();
        context.put(TestUnitTestPropertiesParser.getUtRootPropertyName(PROP_ONE_MODULE, TestUnitTestPropertiesParser.MODULE1_NAME), TestUnitTestPropertiesParser.MODULE1_NAME);
        context.put(TestUnitTestPropertiesParser.getUtSpecificPropertyName(TestUnitTestPropertiesParser.MODULE1_NAME, PROP_EXCLUDE), (("Test" + (TestUnitTestPropertiesParser.MODULE1_TEST_NAME)) + "1"));
        FileListProvider flProvider = TestUnitTestPropertiesParser.getTestFileListProvider(baseDir, 5, 4);
        UnitTestPropertiesParser parser = new UnitTestPropertiesParser(context, new AtomicInteger(1), TestUnitTestPropertiesParser.TEST_CASE_PROPERT_NAME, baseDir, TestUnitTestPropertiesParser.LOG, flProvider, null, true);
        Collection<TestBatch> testBatchCollection = parser.generateTestBatches();
        verifyBatches(testBatchCollection, 2, new String[]{ TestUnitTestPropertiesParser.MODULE1_NAME, TestUnitTestPropertiesParser.MODULE2_NAME }, new int[]{ 4, 4 }, new boolean[]{ true, true });
    }

    @Test(timeout = 5000)
    public void testModuleLevelInclude() {
        File baseDir = TestUnitTestPropertiesParser.getFakeTestBaseDir();
        Context context = TestUnitTestPropertiesParser.getDefaultContext();
        context.put(TestUnitTestPropertiesParser.getUtRootPropertyName(PROP_ONE_MODULE, TestUnitTestPropertiesParser.MODULE1_NAME), TestUnitTestPropertiesParser.MODULE1_NAME);
        context.put(TestUnitTestPropertiesParser.getUtSpecificPropertyName(TestUnitTestPropertiesParser.MODULE1_NAME, PROP_INCLUDE), (((((("Test" + (TestUnitTestPropertiesParser.MODULE1_TEST_NAME)) + "1") + " ") + "Test") + (TestUnitTestPropertiesParser.MODULE1_TEST_NAME)) + "2"));
        FileListProvider flProvider = TestUnitTestPropertiesParser.getTestFileListProvider(baseDir, 5, 4);
        UnitTestPropertiesParser parser = new UnitTestPropertiesParser(context, new AtomicInteger(1), TestUnitTestPropertiesParser.TEST_CASE_PROPERT_NAME, baseDir, TestUnitTestPropertiesParser.LOG, flProvider, null, true);
        Collection<TestBatch> testBatchCollection = parser.generateTestBatches();
        verifyBatches(testBatchCollection, 2, new String[]{ TestUnitTestPropertiesParser.MODULE1_NAME, TestUnitTestPropertiesParser.MODULE2_NAME }, new int[]{ 2, 4 }, new boolean[]{ true, true });
    }

    @Test(timeout = 5000)
    public void testModuleLevelSkipBatching() {
        File baseDir = TestUnitTestPropertiesParser.getFakeTestBaseDir();
        Context context = TestUnitTestPropertiesParser.getDefaultContext();
        context.put(TestUnitTestPropertiesParser.getUtRootPropertyName(PROP_ONE_MODULE, TestUnitTestPropertiesParser.MODULE1_NAME), TestUnitTestPropertiesParser.MODULE1_NAME);
        context.put(TestUnitTestPropertiesParser.getUtSpecificPropertyName(TestUnitTestPropertiesParser.MODULE1_NAME, PROP_SKIP_BATCHING), (((((("Test" + (TestUnitTestPropertiesParser.MODULE1_TEST_NAME)) + "1") + " ") + "Test") + (TestUnitTestPropertiesParser.MODULE1_TEST_NAME)) + "2"));
        FileListProvider flProvider = TestUnitTestPropertiesParser.getTestFileListProvider(baseDir, 5, 4);
        UnitTestPropertiesParser parser = new UnitTestPropertiesParser(context, new AtomicInteger(1), TestUnitTestPropertiesParser.TEST_CASE_PROPERT_NAME, baseDir, TestUnitTestPropertiesParser.LOG, flProvider, null, true);
        Collection<TestBatch> testBatchCollection = parser.generateTestBatches();
        verifyBatches(testBatchCollection, 4, new String[]{ TestUnitTestPropertiesParser.MODULE1_NAME, TestUnitTestPropertiesParser.MODULE1_NAME, TestUnitTestPropertiesParser.MODULE1_NAME, TestUnitTestPropertiesParser.MODULE2_NAME }, new int[]{ 1, 1, 3, 4 }, new boolean[]{ true, true, true, true });
    }

    @Test(timeout = 5000)
    public void testModuleLevelIsolate() {
        File baseDir = TestUnitTestPropertiesParser.getFakeTestBaseDir();
        Context context = TestUnitTestPropertiesParser.getDefaultContext();
        context.put(TestUnitTestPropertiesParser.getUtRootPropertyName(PROP_ONE_MODULE, TestUnitTestPropertiesParser.MODULE1_NAME), TestUnitTestPropertiesParser.MODULE1_NAME);
        context.put(TestUnitTestPropertiesParser.getUtSpecificPropertyName(TestUnitTestPropertiesParser.MODULE1_NAME, PROP_ISOLATE), (((((("Test" + (TestUnitTestPropertiesParser.MODULE1_TEST_NAME)) + "1") + " ") + "Test") + (TestUnitTestPropertiesParser.MODULE1_TEST_NAME)) + "2"));
        FileListProvider flProvider = TestUnitTestPropertiesParser.getTestFileListProvider(baseDir, 5, 4);
        UnitTestPropertiesParser parser = new UnitTestPropertiesParser(context, new AtomicInteger(1), TestUnitTestPropertiesParser.TEST_CASE_PROPERT_NAME, baseDir, TestUnitTestPropertiesParser.LOG, flProvider, null, true);
        Collection<TestBatch> testBatchCollection = parser.generateTestBatches();
        verifyBatches(testBatchCollection, 4, new String[]{ TestUnitTestPropertiesParser.MODULE1_NAME, TestUnitTestPropertiesParser.MODULE1_NAME, TestUnitTestPropertiesParser.MODULE1_NAME, TestUnitTestPropertiesParser.MODULE2_NAME }, new int[]{ 1, 1, 3, 4 }, new boolean[]{ false, false, true, true });
    }

    @Test(timeout = 5000)
    public void testModuleLevelBatchSize() {
        File baseDir = TestUnitTestPropertiesParser.getFakeTestBaseDir();
        Context context = TestUnitTestPropertiesParser.getDefaultContext();
        context.put(TestUnitTestPropertiesParser.getUtRootPropertyName(PROP_ONE_MODULE, TestUnitTestPropertiesParser.MODULE1_NAME), TestUnitTestPropertiesParser.MODULE1_NAME);
        context.put(TestUnitTestPropertiesParser.getUtSpecificPropertyName(TestUnitTestPropertiesParser.MODULE1_NAME, PROP_BATCH_SIZE), Integer.toString(2));
        FileListProvider flProvider = TestUnitTestPropertiesParser.getTestFileListProvider(baseDir, 5, 4);
        UnitTestPropertiesParser parser = new UnitTestPropertiesParser(context, new AtomicInteger(1), TestUnitTestPropertiesParser.TEST_CASE_PROPERT_NAME, baseDir, TestUnitTestPropertiesParser.LOG, flProvider, null, true);
        Collection<TestBatch> testBatchCollection = parser.generateTestBatches();
        verifyBatches(testBatchCollection, 4, new String[]{ TestUnitTestPropertiesParser.MODULE1_NAME, TestUnitTestPropertiesParser.MODULE1_NAME, TestUnitTestPropertiesParser.MODULE1_NAME, TestUnitTestPropertiesParser.MODULE2_NAME }, new int[]{ 2, 2, 1, 4 }, new boolean[]{ true, true, true, true });
    }

    @Test(timeout = 5000)
    public void testProvidedExclude() {
        File baseDir = TestUnitTestPropertiesParser.getFakeTestBaseDir();
        Context context = TestUnitTestPropertiesParser.getDefaultContext();
        FileListProvider flProvider = TestUnitTestPropertiesParser.getTestFileListProvider(baseDir, 5, 4);
        Set<String> excludedProvided = Sets.newHashSet((("Test" + (TestUnitTestPropertiesParser.MODULE1_TEST_NAME)) + "1"));
        UnitTestPropertiesParser parser = new UnitTestPropertiesParser(context, new AtomicInteger(1), TestUnitTestPropertiesParser.TEST_CASE_PROPERT_NAME, baseDir, TestUnitTestPropertiesParser.LOG, flProvider, excludedProvided, true);
        Collection<TestBatch> testBatchCollection = parser.generateTestBatches();
        verifyBatches(testBatchCollection, 2, new String[]{ TestUnitTestPropertiesParser.MODULE1_NAME, TestUnitTestPropertiesParser.MODULE2_NAME }, new int[]{ 4, 4 }, new boolean[]{ true, true });
    }

    @Test(timeout = 5000)
    public void testTopLevelBatchSizeIncludeAll() {
        File baseDir = TestUnitTestPropertiesParser.getFakeTestBaseDir();
        Context context = TestUnitTestPropertiesParser.getDefaultContext();
        FileListProvider flProvider = TestUnitTestPropertiesParser.getTestFileListProvider(baseDir, 120, 60);
        context.put(TestUnitTestPropertiesParser.getUtRootPropertyName(PROP_BATCH_SIZE), Integer.toString(0));
        UnitTestPropertiesParser parser = new UnitTestPropertiesParser(context, new AtomicInteger(1), TestUnitTestPropertiesParser.TEST_CASE_PROPERT_NAME, baseDir, TestUnitTestPropertiesParser.LOG, flProvider, null, true);
        Collection<TestBatch> testBatchCollection = parser.generateTestBatches();
        verifyBatches(testBatchCollection, 2, new String[]{ TestUnitTestPropertiesParser.MODULE1_NAME, TestUnitTestPropertiesParser.MODULE2_NAME }, new int[]{ 120, 60 }, new boolean[]{ true, true });
    }

    @Test(timeout = 5000)
    public void testModuleLevelBatchSizeIncludeAll() {
        File baseDir = TestUnitTestPropertiesParser.getFakeTestBaseDir();
        Context context = TestUnitTestPropertiesParser.getDefaultContext();
        FileListProvider flProvider = TestUnitTestPropertiesParser.getTestFileListProvider(baseDir, 50, 4);
        context.put(TestUnitTestPropertiesParser.getUtRootPropertyName(PROP_BATCH_SIZE), Integer.toString(2));
        context.put(TestUnitTestPropertiesParser.getUtRootPropertyName(PROP_ONE_MODULE, TestUnitTestPropertiesParser.MODULE1_NAME), TestUnitTestPropertiesParser.MODULE1_NAME);
        context.put(TestUnitTestPropertiesParser.getUtSpecificPropertyName(TestUnitTestPropertiesParser.MODULE1_NAME, PROP_BATCH_SIZE), Integer.toString(0));
        UnitTestPropertiesParser parser = new UnitTestPropertiesParser(context, new AtomicInteger(1), TestUnitTestPropertiesParser.TEST_CASE_PROPERT_NAME, baseDir, TestUnitTestPropertiesParser.LOG, flProvider, null, true);
        Collection<TestBatch> testBatchCollection = parser.generateTestBatches();
        verifyBatches(testBatchCollection, 3, new String[]{ TestUnitTestPropertiesParser.MODULE1_NAME, TestUnitTestPropertiesParser.MODULE2_NAME, TestUnitTestPropertiesParser.MODULE2_NAME }, new int[]{ 50, 2, 2 }, new boolean[]{ true, true, true });
    }

    @Test(timeout = 5000)
    public void testMultiLevelModules() {
        File baseDir = TestUnitTestPropertiesParser.getFakeTestBaseDir();
        Context context = TestUnitTestPropertiesParser.getDefaultContext();
        FileListProvider flProvider = TestUnitTestPropertiesParser.getTestFileListProviderMultiLevel(baseDir, 4, 30, 6, 9);
        context.put(TestUnitTestPropertiesParser.getUtRootPropertyName(PROP_BATCH_SIZE), Integer.toString(4));
        context.put(TestUnitTestPropertiesParser.getUtRootPropertyName(PROP_ONE_MODULE, TestUnitTestPropertiesParser.MODULE1_NAME), TestUnitTestPropertiesParser.MODULE1_NAME);
        context.put(TestUnitTestPropertiesParser.getUtSpecificPropertyName(TestUnitTestPropertiesParser.MODULE1_NAME, PROP_BATCH_SIZE), Integer.toString(0));
        context.put(TestUnitTestPropertiesParser.getUtRootPropertyName(PROP_ONE_MODULE, TestUnitTestPropertiesParser.THREE_LEVEL_MODULE1_NAME), TestUnitTestPropertiesParser.THREE_LEVEL_MODULE1_NAME);
        context.put(TestUnitTestPropertiesParser.getUtSpecificPropertyName(TestUnitTestPropertiesParser.THREE_LEVEL_MODULE1_NAME, PROP_BATCH_SIZE), Integer.toString(0));
        UnitTestPropertiesParser parser = new UnitTestPropertiesParser(context, new AtomicInteger(1), TestUnitTestPropertiesParser.TEST_CASE_PROPERT_NAME, baseDir, TestUnitTestPropertiesParser.LOG, flProvider, null, true);
        Collection<TestBatch> testBatchCollection = parser.generateTestBatches();
        verifyBatches(testBatchCollection, 5, new String[]{ PREFIX_TOP_LEVEL, TestUnitTestPropertiesParser.MODULE1_NAME, TestUnitTestPropertiesParser.TWO_LEVEL_MODULE1_NAME, TestUnitTestPropertiesParser.TWO_LEVEL_MODULE1_NAME, TestUnitTestPropertiesParser.THREE_LEVEL_MODULE1_NAME }, new int[]{ 4, 30, 4, 2, 9 }, new boolean[]{ true, true, true, true, true });
    }

    @Test(timeout = 5000)
    public void testTopLevelModuleConfig() {
        File baseDir = TestUnitTestPropertiesParser.getFakeTestBaseDir();
        Context context = TestUnitTestPropertiesParser.getDefaultContext();
        FileListProvider flProvider = TestUnitTestPropertiesParser.getTestFileListProviderMultiLevel(baseDir, 9, 0, 0, 0);
        context.put(TestUnitTestPropertiesParser.getUtRootPropertyName(PROP_BATCH_SIZE), Integer.toString(4));
        context.put(TestUnitTestPropertiesParser.getUtRootPropertyName(PROP_ONE_MODULE, MODULE_NAME_TOP_LEVEL), MODULE_NAME_TOP_LEVEL);
        context.put(TestUnitTestPropertiesParser.getUtSpecificPropertyName(MODULE_NAME_TOP_LEVEL, PROP_BATCH_SIZE), Integer.toString(0));
        UnitTestPropertiesParser parser = new UnitTestPropertiesParser(context, new AtomicInteger(1), TestUnitTestPropertiesParser.TEST_CASE_PROPERT_NAME, baseDir, TestUnitTestPropertiesParser.LOG, flProvider, null, true);
        Collection<TestBatch> testBatchCollection = parser.generateTestBatches();
        verifyBatches(testBatchCollection, 1, new String[]{ PREFIX_TOP_LEVEL }, new int[]{ 9 }, new boolean[]{ true });
    }

    @Test(timeout = 5000)
    public void testScanMultipleDirectoriesNested() {
        File baseDir = TestUnitTestPropertiesParser.getFakeTestBaseDir();
        Context context = TestUnitTestPropertiesParser.getDefaultContext();
        FileListProvider flProvider = TestUnitTestPropertiesParser.getTestFileListProviderMultiLevel(baseDir, 13, 5, 0, 0);
        context.put(TestUnitTestPropertiesParser.getUtRootPropertyName(PROP_DIRECTORIES), ("./ ./" + (TestUnitTestPropertiesParser.MODULE1_NAME)));
        UnitTestPropertiesParser parser = new UnitTestPropertiesParser(context, new AtomicInteger(1), TestUnitTestPropertiesParser.TEST_CASE_PROPERT_NAME, baseDir, TestUnitTestPropertiesParser.LOG, flProvider, null, true);
        Collection<TestBatch> testBatchCollection = parser.generateTestBatches();
        verifyBatches(testBatchCollection, 3, new String[]{ PREFIX_TOP_LEVEL, PREFIX_TOP_LEVEL, TestUnitTestPropertiesParser.MODULE1_NAME }, new int[]{ 10, 3, 5 }, new boolean[]{ true, true, true });
    }

    @Test(timeout = 5000)
    public void testScanMultipleDirectoriesNonNested() {
        File baseDir = TestUnitTestPropertiesParser.getFakeTestBaseDir();
        Context context = TestUnitTestPropertiesParser.getDefaultContext();
        FileListProvider flProvider = TestUnitTestPropertiesParser.getTestFileListProvider(baseDir, 13, 8);
        context.put(TestUnitTestPropertiesParser.getUtRootPropertyName(PROP_DIRECTORIES), (((("./" + (TestUnitTestPropertiesParser.MODULE1_NAME)) + " ") + "./") + (TestUnitTestPropertiesParser.MODULE2_NAME)));
        UnitTestPropertiesParser parser = new UnitTestPropertiesParser(context, new AtomicInteger(1), TestUnitTestPropertiesParser.TEST_CASE_PROPERT_NAME, baseDir, TestUnitTestPropertiesParser.LOG, flProvider, null, true);
        Collection<TestBatch> testBatchCollection = parser.generateTestBatches();
        verifyBatches(testBatchCollection, 3, new String[]{ TestUnitTestPropertiesParser.MODULE1_NAME, TestUnitTestPropertiesParser.MODULE1_NAME, TestUnitTestPropertiesParser.MODULE2_NAME }, new int[]{ 10, 3, 8 }, new boolean[]{ true, true, true });
    }

    @Test(timeout = 5000)
    public void testModuleInclude() {
        File baseDir = TestUnitTestPropertiesParser.getFakeTestBaseDir();
        Context context = TestUnitTestPropertiesParser.getDefaultContext();
        FileListProvider flProvider = TestUnitTestPropertiesParser.getTestFileListProvider(baseDir, 13, 8);
        context.put(TestUnitTestPropertiesParser.getUtRootPropertyName(PROP_MODULE_LIST, PROP_INCLUDE), TestUnitTestPropertiesParser.MODULE1_NAME);
        UnitTestPropertiesParser parser = new UnitTestPropertiesParser(context, new AtomicInteger(1), TestUnitTestPropertiesParser.TEST_CASE_PROPERT_NAME, baseDir, TestUnitTestPropertiesParser.LOG, flProvider, null, true);
        Collection<TestBatch> testBatchCollection = parser.generateTestBatches();
        verifyBatches(testBatchCollection, 2, new String[]{ TestUnitTestPropertiesParser.MODULE1_NAME, TestUnitTestPropertiesParser.MODULE1_NAME }, new int[]{ 10, 3 }, new boolean[]{ true, true });
    }

    @Test(timeout = 5000)
    public void testModuleExclude() {
        File baseDir = TestUnitTestPropertiesParser.getFakeTestBaseDir();
        Context context = TestUnitTestPropertiesParser.getDefaultContext();
        FileListProvider flProvider = TestUnitTestPropertiesParser.getTestFileListProvider(baseDir, 13, 8);
        context.put(TestUnitTestPropertiesParser.getUtRootPropertyName(PROP_MODULE_LIST, PROP_EXCLUDE), TestUnitTestPropertiesParser.MODULE1_NAME);
        UnitTestPropertiesParser parser = new UnitTestPropertiesParser(context, new AtomicInteger(1), TestUnitTestPropertiesParser.TEST_CASE_PROPERT_NAME, baseDir, TestUnitTestPropertiesParser.LOG, flProvider, null, true);
        Collection<TestBatch> testBatchCollection = parser.generateTestBatches();
        verifyBatches(testBatchCollection, 1, new String[]{ TestUnitTestPropertiesParser.MODULE2_NAME }, new int[]{ 8 }, new boolean[]{ true });
    }

    @Test(timeout = 5000)
    public void testModuleWithPeriodInDirName() {
        File baseDir = TestUnitTestPropertiesParser.getFakeTestBaseDir();
        Context context = TestUnitTestPropertiesParser.getDefaultContext();
        FileListProvider flProvider = TestUnitTestPropertiesParser.getTestFileListProviderSingleModule(baseDir, TestUnitTestPropertiesParser.MODULE3_REL_DIR, TestUnitTestPropertiesParser.MODULE3_TEST_NAME, 13);
        context.put(TestUnitTestPropertiesParser.getUtRootPropertyName(PROP_ONE_MODULE, TestUnitTestPropertiesParser.MODULE3_MODULE_NAME), TestUnitTestPropertiesParser.MODULE3_MODULE_NAME);
        context.put(TestUnitTestPropertiesParser.getUtSpecificPropertyName(TestUnitTestPropertiesParser.MODULE3_MODULE_NAME, PROP_BATCH_SIZE), Integer.toString(5));
        UnitTestPropertiesParser parser = new UnitTestPropertiesParser(context, new AtomicInteger(1), TestUnitTestPropertiesParser.TEST_CASE_PROPERT_NAME, baseDir, TestUnitTestPropertiesParser.LOG, flProvider, null, true);
        Collection<TestBatch> testBatchCollection = parser.generateTestBatches();
        verifyBatches(testBatchCollection, 3, new String[]{ TestUnitTestPropertiesParser.MODULE3_MODULE_NAME, TestUnitTestPropertiesParser.MODULE3_MODULE_NAME, TestUnitTestPropertiesParser.MODULE3_MODULE_NAME }, new int[]{ 5, 5, 3 }, new boolean[]{ true, true, true });
    }
}

