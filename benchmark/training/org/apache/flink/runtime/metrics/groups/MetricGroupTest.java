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
package org.apache.flink.runtime.metrics.groups;


import QueryScopeInfo.TaskQueryScopeInfo;
import org.apache.flink.api.common.JobID;
import org.apache.flink.configuration.ConfigConstants;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.metrics.CharacterFilter;
import org.apache.flink.metrics.Metric;
import org.apache.flink.metrics.MetricGroup;
import org.apache.flink.runtime.jobgraph.JobVertexID;
import org.apache.flink.runtime.metrics.MetricRegistry;
import org.apache.flink.runtime.metrics.MetricRegistryConfiguration;
import org.apache.flink.runtime.metrics.MetricRegistryImpl;
import org.apache.flink.runtime.metrics.NoOpMetricRegistry;
import org.apache.flink.runtime.metrics.dump.QueryScopeInfo;
import org.apache.flink.runtime.metrics.scope.ScopeFormat;
import org.apache.flink.runtime.metrics.util.DummyCharacterFilter;
import org.apache.flink.runtime.metrics.util.TestReporter;
import org.apache.flink.util.AbstractID;
import org.apache.flink.util.TestLogger;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for the {@link MetricGroup}.
 */
public class MetricGroupTest extends TestLogger {
    private static final MetricRegistryConfiguration defaultMetricRegistryConfiguration = MetricRegistryConfiguration.defaultMetricRegistryConfiguration();

    private MetricRegistryImpl registry;

    private final MetricRegistryImpl exceptionOnRegister = new MetricGroupTest.ExceptionOnRegisterRegistry();

    @Test
    public void sameGroupOnNameCollision() {
        GenericMetricGroup group = new GenericMetricGroup(registry, new MetricGroupTest.DummyAbstractMetricGroup(registry), "somegroup");
        String groupName = "sometestname";
        MetricGroup subgroup1 = group.addGroup(groupName);
        MetricGroup subgroup2 = group.addGroup(groupName);
        Assert.assertNotNull(subgroup1);
        Assert.assertNotNull(subgroup2);
        Assert.assertTrue((subgroup1 == subgroup2));
    }

    /**
     * Verifies the basic behavior when defining user-defined variables.
     */
    @Test
    public void testUserDefinedVariable() {
        MetricRegistry registry = NoOpMetricRegistry.INSTANCE;
        GenericMetricGroup root = new GenericMetricGroup(registry, new MetricGroupTest.DummyAbstractMetricGroup(registry), "root");
        String key = "key";
        String value = "value";
        MetricGroup group = root.addGroup(key, value);
        String variableValue = group.getAllVariables().get(ScopeFormat.asVariable("key"));
        Assert.assertEquals(value, variableValue);
        String identifier = group.getMetricIdentifier("metric");
        Assert.assertTrue("Key is missing from metric identifier.", identifier.contains("key"));
        Assert.assertTrue("Value is missing from metric identifier.", identifier.contains("value"));
        String logicalScope = getLogicalScope(new DummyCharacterFilter());
        Assert.assertTrue("Key is missing from logical scope.", logicalScope.contains(key));
        Assert.assertFalse("Value is present in logical scope.", logicalScope.contains(value));
    }

    /**
     * Verifies that calling {@link MetricGroup#addGroup(String, String)} on a {@link GenericKeyMetricGroup} goes
     * through the generic code path.
     */
    @Test
    public void testUserDefinedVariableOnKeyGroup() {
        MetricRegistry registry = NoOpMetricRegistry.INSTANCE;
        GenericMetricGroup root = new GenericMetricGroup(registry, new MetricGroupTest.DummyAbstractMetricGroup(registry), "root");
        String key1 = "key1";
        String value1 = "value1";
        root.addGroup(key1, value1);
        String key2 = "key2";
        String value2 = "value2";
        MetricGroup group = root.addGroup(key1).addGroup(key2, value2);
        String variableValue = group.getAllVariables().get("value2");
        Assert.assertNull(variableValue);
        String identifier = group.getMetricIdentifier("metric");
        Assert.assertTrue("Key1 is missing from metric identifier.", identifier.contains("key1"));
        Assert.assertTrue("Key2 is missing from metric identifier.", identifier.contains("key2"));
        Assert.assertTrue("Value2 is missing from metric identifier.", identifier.contains("value2"));
        String logicalScope = getLogicalScope(new DummyCharacterFilter());
        Assert.assertTrue("Key1 is missing from logical scope.", logicalScope.contains(key1));
        Assert.assertTrue("Key2 is missing from logical scope.", logicalScope.contains(key2));
        Assert.assertTrue("Value2 is missing from logical scope.", logicalScope.contains(value2));
    }

    /**
     * Verifies that calling {@link MetricGroup#addGroup(String, String)} if a generic group with the key name already
     * exists goes through the generic code path.
     */
    @Test
    public void testNameCollisionForKeyAfterGenericGroup() {
        MetricRegistry registry = NoOpMetricRegistry.INSTANCE;
        GenericMetricGroup root = new GenericMetricGroup(registry, new MetricGroupTest.DummyAbstractMetricGroup(registry), "root");
        String key = "key";
        String value = "value";
        root.addGroup(key);
        MetricGroup group = root.addGroup(key, value);
        String variableValue = group.getAllVariables().get(ScopeFormat.asVariable("key"));
        Assert.assertNull(variableValue);
        String identifier = group.getMetricIdentifier("metric");
        Assert.assertTrue("Key is missing from metric identifier.", identifier.contains("key"));
        Assert.assertTrue("Value is missing from metric identifier.", identifier.contains("value"));
        String logicalScope = getLogicalScope(new DummyCharacterFilter());
        Assert.assertTrue("Key is missing from logical scope.", logicalScope.contains(key));
        Assert.assertTrue("Value is missing from logical scope.", logicalScope.contains(value));
    }

    /**
     * Verifies that calling {@link MetricGroup#addGroup(String, String)} if a generic group with the key and value name
     * already exists goes through the generic code path.
     */
    @Test
    public void testNameCollisionForKeyAndValueAfterGenericGroup() {
        MetricRegistry registry = NoOpMetricRegistry.INSTANCE;
        GenericMetricGroup root = new GenericMetricGroup(registry, new MetricGroupTest.DummyAbstractMetricGroup(registry), "root");
        String key = "key";
        String value = "value";
        root.addGroup(key).addGroup(value);
        MetricGroup group = root.addGroup(key, value);
        String variableValue = group.getAllVariables().get(ScopeFormat.asVariable("key"));
        Assert.assertNull(variableValue);
        String identifier = group.getMetricIdentifier("metric");
        Assert.assertTrue("Key is missing from metric identifier.", identifier.contains("key"));
        Assert.assertTrue("Value is missing from metric identifier.", identifier.contains("value"));
        String logicalScope = getLogicalScope(new DummyCharacterFilter());
        Assert.assertTrue("Key is missing from logical scope.", logicalScope.contains(key));
        Assert.assertTrue("Value is missing from logical scope.", logicalScope.contains(value));
    }

    /**
     * Verifies that existing key/value groups are returned when calling {@link MetricGroup#addGroup(String)}.
     */
    @Test
    public void testNameCollisionAfterKeyValueGroup() {
        MetricRegistry registry = NoOpMetricRegistry.INSTANCE;
        GenericMetricGroup root = new GenericMetricGroup(registry, new MetricGroupTest.DummyAbstractMetricGroup(registry), "root");
        String key = "key";
        String value = "value";
        root.addGroup(key, value);
        MetricGroup group = root.addGroup(key).addGroup(value);
        String variableValue = group.getAllVariables().get(ScopeFormat.asVariable("key"));
        Assert.assertEquals(value, variableValue);
        String identifier = group.getMetricIdentifier("metric");
        Assert.assertTrue("Key is missing from metric identifier.", identifier.contains("key"));
        Assert.assertTrue("Value is missing from metric identifier.", identifier.contains("value"));
        String logicalScope = getLogicalScope(new DummyCharacterFilter());
        Assert.assertTrue("Key is missing from logical scope.", logicalScope.contains(key));
        Assert.assertFalse("Value is present in logical scope.", logicalScope.contains(value));
    }

    /**
     * Verifies that calling {@link AbstractMetricGroup#getLogicalScope(CharacterFilter, char, int)} on {@link GenericValueMetricGroup}
     * should ignore value as well.
     */
    @Test
    public void testLogicalScopeShouldIgnoreValueGroupName() throws Exception {
        Configuration config = new Configuration();
        config.setString((((ConfigConstants.METRICS_REPORTER_PREFIX) + "test.") + (ConfigConstants.METRICS_REPORTER_CLASS_SUFFIX)), TestReporter.class.getName());
        MetricRegistryImpl registry = new MetricRegistryImpl(MetricRegistryConfiguration.fromConfiguration(config));
        try {
            GenericMetricGroup root = new GenericMetricGroup(registry, new MetricGroupTest.DummyAbstractMetricGroup(registry), "root");
            String key = "key";
            String value = "value";
            MetricGroup group = root.addGroup(key, value);
            String logicalScope = ((AbstractMetricGroup) (group)).getLogicalScope(new DummyCharacterFilter(), registry.getDelimiter(), 0);
            Assert.assertThat("Key is missing from logical scope.", logicalScope, Matchers.containsString(key));
            Assert.assertThat("Value is present in logical scope.", logicalScope, Matchers.not(Matchers.containsString(value)));
        } finally {
            registry.shutdown().get();
        }
    }

    @Test
    public void closedGroupDoesNotRegisterMetrics() {
        GenericMetricGroup group = new GenericMetricGroup(exceptionOnRegister, new MetricGroupTest.DummyAbstractMetricGroup(exceptionOnRegister), "testgroup");
        Assert.assertFalse(group.isClosed());
        group.close();
        Assert.assertTrue(group.isClosed());
        // these will fail is the registration is propagated
        group.counter("testcounter");
        group.gauge("testgauge", new org.apache.flink.metrics.Gauge<Object>() {
            @Override
            public Object getValue() {
                return null;
            }
        });
    }

    @Test
    public void closedGroupCreatesClosedGroups() {
        GenericMetricGroup group = new GenericMetricGroup(exceptionOnRegister, new MetricGroupTest.DummyAbstractMetricGroup(exceptionOnRegister), "testgroup");
        Assert.assertFalse(group.isClosed());
        group.close();
        Assert.assertTrue(group.isClosed());
        AbstractMetricGroup subgroup = ((AbstractMetricGroup) (group.addGroup("test subgroup")));
        Assert.assertTrue(subgroup.isClosed());
    }

    @Test
    public void tolerateMetricNameCollisions() {
        final String name = "abctestname";
        GenericMetricGroup group = new GenericMetricGroup(registry, new MetricGroupTest.DummyAbstractMetricGroup(registry), "testgroup");
        Assert.assertNotNull(group.counter(name));
        Assert.assertNotNull(group.counter(name));
    }

    @Test
    public void tolerateMetricAndGroupNameCollisions() {
        final String name = "abctestname";
        GenericMetricGroup group = new GenericMetricGroup(registry, new MetricGroupTest.DummyAbstractMetricGroup(registry), "testgroup");
        Assert.assertNotNull(group.addGroup(name));
        Assert.assertNotNull(group.counter(name));
    }

    @Test
    public void testCreateQueryServiceMetricInfo() {
        JobID jid = new JobID();
        JobVertexID vid = new JobVertexID();
        AbstractID eid = new AbstractID();
        MetricRegistryImpl registry = new MetricRegistryImpl(MetricGroupTest.defaultMetricRegistryConfiguration);
        TaskManagerMetricGroup tm = new TaskManagerMetricGroup(registry, "host", "id");
        TaskManagerJobMetricGroup job = new TaskManagerJobMetricGroup(registry, tm, jid, "jobname");
        TaskMetricGroup task = new TaskMetricGroup(registry, job, vid, eid, "taskName", 4, 5);
        GenericMetricGroup userGroup1 = new GenericMetricGroup(registry, task, "hello");
        GenericMetricGroup userGroup2 = new GenericMetricGroup(registry, userGroup1, "world");
        QueryScopeInfo.TaskQueryScopeInfo info1 = ((QueryScopeInfo.TaskQueryScopeInfo) (userGroup1.createQueryServiceMetricInfo(new DummyCharacterFilter())));
        Assert.assertEquals("hello", info1.scope);
        Assert.assertEquals(jid.toString(), info1.jobID);
        Assert.assertEquals(vid.toString(), info1.vertexID);
        Assert.assertEquals(4, info1.subtaskIndex);
        QueryScopeInfo.TaskQueryScopeInfo info2 = ((QueryScopeInfo.TaskQueryScopeInfo) (userGroup2.createQueryServiceMetricInfo(new DummyCharacterFilter())));
        Assert.assertEquals("hello.world", info2.scope);
        Assert.assertEquals(jid.toString(), info2.jobID);
        Assert.assertEquals(vid.toString(), info2.vertexID);
        Assert.assertEquals(4, info2.subtaskIndex);
    }

    // ------------------------------------------------------------------------
    private static class ExceptionOnRegisterRegistry extends MetricRegistryImpl {
        public ExceptionOnRegisterRegistry() {
            super(MetricGroupTest.defaultMetricRegistryConfiguration);
        }

        @Override
        public void register(Metric metric, String name, AbstractMetricGroup parent) {
            Assert.fail("Metric should never be registered");
        }

        @Override
        public void unregister(Metric metric, String name, AbstractMetricGroup parent) {
            Assert.fail("Metric should never be un-registered");
        }
    }

    // ------------------------------------------------------------------------
    /**
     * A dummy {@link AbstractMetricGroup} to be used when a group is required as an argument but not actually used.
     */
    public static class DummyAbstractMetricGroup extends AbstractMetricGroup {
        public DummyAbstractMetricGroup(MetricRegistry registry) {
            super(registry, new String[0], null);
        }

        @Override
        protected QueryScopeInfo createQueryServiceMetricInfo(CharacterFilter filter) {
            return null;
        }

        @Override
        protected String getGroupName(CharacterFilter filter) {
            return "";
        }

        @Override
        protected void addMetric(String name, Metric metric) {
        }

        @Override
        public MetricGroup addGroup(String name) {
            return new MetricGroupTest.DummyAbstractMetricGroup(MetricGroupTest.this.registry);
        }
    }
}

