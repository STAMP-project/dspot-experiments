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
package org.apache.nifi.hbase;


import HBase_1_1_2_ListLookupService.RETURN_TYPE;
import HBase_1_1_2_ListLookupService.VALUE_LIST;
import java.util.List;
import java.util.Optional;
import org.apache.nifi.util.TestRunner;
import org.junit.Assert;
import org.junit.Test;


public class TestHBase_1_1_2_ListLookupService {
    static final String TABLE_NAME = "guids";

    static final String ROW = "row1";

    static final String COLS = "cf1:cq1,cf2:cq2";

    private TestRunner runner;

    private HBase_1_1_2_ListLookupService lookupService;

    private MockHBaseClientService clientService;

    private TestRecordLookupProcessor testLookupProcessor;

    @Test
    public void testLookupKeyList() throws Exception {
        Optional<List> results = setupAndRun();
        Assert.assertTrue(results.isPresent());
        List result = results.get();
        Assert.assertTrue(((result.size()) == 2));
        Assert.assertTrue(result.contains("cq1"));
        Assert.assertTrue(result.contains("cq2"));
    }

    @Test
    public void testLookupValueList() throws Exception {
        runner.disableControllerService(lookupService);
        runner.setProperty(lookupService, RETURN_TYPE, VALUE_LIST);
        runner.enableControllerService(lookupService);
        Optional<List> results = setupAndRun();
        Assert.assertTrue(results.isPresent());
        List result = results.get();
        Assert.assertTrue(((result.size()) == 2));
        Assert.assertTrue(result.contains("v1"));
        Assert.assertTrue(result.contains("v2"));
    }
}

