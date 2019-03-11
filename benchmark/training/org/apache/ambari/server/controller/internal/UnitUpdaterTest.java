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
package org.apache.ambari.server.controller.internal;


import java.util.HashMap;
import java.util.Map;
import org.apache.ambari.server.testutils.TestCollectionUtils;
import org.apache.ambari.server.topology.Blueprint;
import org.apache.ambari.server.topology.ClusterTopology;
import org.apache.ambari.server.topology.Configuration;
import org.easymock.EasyMockRule;
import org.easymock.EasyMockSupport;
import org.easymock.Mock;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


public class UnitUpdaterTest extends EasyMockSupport {
    public static final String HEAPSIZE = "oozie_heapsize";

    @Rule
    public EasyMockRule mocks = new EasyMockRule(this);

    public static final String OOZIE = "OOZIE";

    public static final String OOZIE_ENV = "oozie-env";

    private Map<String, Stack.ConfigProperty> stackConfigWithMetadata = new HashMap<>();

    private UnitUpdater unitUpdater;

    @Mock
    private ClusterTopology clusterTopology;

    @Mock
    private Blueprint blueprint;

    @Mock
    private Stack stack;

    @Test
    public void testStackUnitIsAppendedWhereUnitIsNotDefined() throws Exception {
        stackUnitIs(UnitUpdaterTest.HEAPSIZE, "GB");
        Assert.assertEquals("1g", updateUnit(UnitUpdaterTest.OOZIE, UnitUpdaterTest.OOZIE_ENV, UnitUpdaterTest.HEAPSIZE, "1"));
    }

    @Test
    public void testDefaultMbStackUnitIsAppendedWhereUnitIsNotDefined() throws Exception {
        Assert.assertEquals("4096m", updateUnit(UnitUpdaterTest.OOZIE, UnitUpdaterTest.OOZIE_ENV, UnitUpdaterTest.HEAPSIZE, "4096"));
    }

    @Test
    public void testNoUnitIsAppendedWhenPropertyAlreadyHasTheStackUnit() throws Exception {
        stackUnitIs(UnitUpdaterTest.HEAPSIZE, "MB");
        Assert.assertEquals("128m", updateUnit(UnitUpdaterTest.OOZIE, UnitUpdaterTest.OOZIE_ENV, UnitUpdaterTest.HEAPSIZE, "128m"));
    }

    @Test
    public void testNoUnitIsAppendedIfStackUnitIsInBytes() throws Exception {
        stackUnitIs(UnitUpdaterTest.HEAPSIZE, "Bytes");
        Assert.assertEquals("128", updateUnit(UnitUpdaterTest.OOZIE, UnitUpdaterTest.OOZIE_ENV, UnitUpdaterTest.HEAPSIZE, "128"));
    }

    @Test
    public void testUnitSuffixIsCaseInsenitiveAndWhiteSpaceTolerant() throws Exception {
        stackUnitIs(UnitUpdaterTest.HEAPSIZE, "GB");
        Assert.assertEquals("1g", updateUnit(UnitUpdaterTest.OOZIE, UnitUpdaterTest.OOZIE_ENV, UnitUpdaterTest.HEAPSIZE, " 1G "));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRejectValuesWhereStackUnitDoesNotMatchToGiveUnit() throws Exception {
        stackUnitIs(UnitUpdaterTest.HEAPSIZE, "MB");
        updateUnit(UnitUpdaterTest.OOZIE, UnitUpdaterTest.OOZIE_ENV, UnitUpdaterTest.HEAPSIZE, "2g");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRejectEmptyPropertyValue() throws Exception {
        updateUnit(UnitUpdaterTest.OOZIE, UnitUpdaterTest.OOZIE_ENV, UnitUpdaterTest.HEAPSIZE, "");
    }

    @Test
    public void updateUnits() {
        stackUnitIs(UnitUpdaterTest.HEAPSIZE, "MB");
        setUpStack(UnitUpdaterTest.OOZIE, UnitUpdaterTest.OOZIE_ENV);
        Map<String, Map<String, String>> properties = TestCollectionUtils.map(UnitUpdaterTest.OOZIE_ENV, TestCollectionUtils.map(UnitUpdaterTest.HEAPSIZE, "1024"), "core-site", TestCollectionUtils.map("fs.trash.interval", "360"));
        Configuration configuration = new Configuration(properties, new HashMap());
        UnitUpdater.updateUnits(configuration, stack);
        Map<String, Map<String, String>> expected = TestCollectionUtils.map(UnitUpdaterTest.OOZIE_ENV, TestCollectionUtils.map(UnitUpdaterTest.HEAPSIZE, "1024m"), "core-site", TestCollectionUtils.map("fs.trash.interval", "360"));
        Assert.assertEquals(expected, configuration.getProperties());
    }

    @Test
    public void removeUnits() {
        stackUnitIs(UnitUpdaterTest.HEAPSIZE, "MB");
        setUpStack(UnitUpdaterTest.OOZIE, UnitUpdaterTest.OOZIE_ENV);
        Map<String, Map<String, String>> properties = TestCollectionUtils.map(UnitUpdaterTest.OOZIE_ENV, TestCollectionUtils.map(UnitUpdaterTest.HEAPSIZE, "1024m"), "core-site", TestCollectionUtils.map("fs.trash.interval", "360"));
        Configuration configuration = new Configuration(properties, new HashMap());
        UnitUpdater.removeUnits(configuration, stack);
        Map<String, Map<String, String>> expected = TestCollectionUtils.map(UnitUpdaterTest.OOZIE_ENV, TestCollectionUtils.map(UnitUpdaterTest.HEAPSIZE, "1024"), "core-site", TestCollectionUtils.map("fs.trash.interval", "360"));
        Assert.assertEquals(expected, configuration.getProperties());
    }
}

