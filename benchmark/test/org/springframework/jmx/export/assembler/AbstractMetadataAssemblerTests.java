/**
 * Copyright 2002-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.jmx.export.assembler;


import java.util.HashMap;
import java.util.Map;
import javax.management.Descriptor;
import javax.management.MBeanInfo;
import javax.management.MBeanParameterInfo;
import javax.management.modelmbean.ModelMBeanAttributeInfo;
import javax.management.modelmbean.ModelMBeanInfo;
import javax.management.modelmbean.ModelMBeanOperationInfo;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.jmx.IJmxTestBean;
import org.springframework.jmx.export.MBeanExporter;
import org.springframework.jmx.support.ObjectNameManager;
import org.springframework.tests.aop.interceptor.NopInterceptor;


/**
 *
 *
 * @author Rob Harrop
 * @author Chris Beams
 */
public abstract class AbstractMetadataAssemblerTests extends AbstractJmxAssemblerTests {
    protected static final String QUEUE_SIZE_METRIC = "QueueSize";

    protected static final String CACHE_ENTRIES_METRIC = "CacheEntries";

    @Test
    public void testDescription() throws Exception {
        ModelMBeanInfo info = getMBeanInfoFromAssembler();
        Assert.assertEquals("The descriptions are not the same", "My Managed Bean", info.getDescription());
    }

    @Test
    public void testAttributeDescriptionOnSetter() throws Exception {
        ModelMBeanInfo inf = getMBeanInfoFromAssembler();
        ModelMBeanAttributeInfo attr = inf.getAttribute(AbstractJmxAssemblerTests.AGE_ATTRIBUTE);
        Assert.assertEquals("The description for the age attribute is incorrect", "The Age Attribute", attr.getDescription());
    }

    @Test
    public void testAttributeDescriptionOnGetter() throws Exception {
        ModelMBeanInfo inf = getMBeanInfoFromAssembler();
        ModelMBeanAttributeInfo attr = inf.getAttribute(AbstractJmxAssemblerTests.NAME_ATTRIBUTE);
        Assert.assertEquals("The description for the name attribute is incorrect", "The Name Attribute", attr.getDescription());
    }

    /**
     * Tests the situation where the attribute is only defined on the getter.
     */
    @Test
    public void testReadOnlyAttribute() throws Exception {
        ModelMBeanInfo inf = getMBeanInfoFromAssembler();
        ModelMBeanAttributeInfo attr = inf.getAttribute(AbstractJmxAssemblerTests.AGE_ATTRIBUTE);
        Assert.assertFalse("The age attribute should not be writable", attr.isWritable());
    }

    @Test
    public void testReadWriteAttribute() throws Exception {
        ModelMBeanInfo inf = getMBeanInfoFromAssembler();
        ModelMBeanAttributeInfo attr = inf.getAttribute(AbstractJmxAssemblerTests.NAME_ATTRIBUTE);
        Assert.assertTrue("The name attribute should be writable", attr.isWritable());
        Assert.assertTrue("The name attribute should be readable", attr.isReadable());
    }

    /**
     * Tests the situation where the property only has a getter.
     */
    @Test
    public void testWithOnlySetter() throws Exception {
        ModelMBeanInfo inf = getMBeanInfoFromAssembler();
        ModelMBeanAttributeInfo attr = inf.getAttribute("NickName");
        Assert.assertNotNull("Attribute should not be null", attr);
    }

    /**
     * Tests the situation where the property only has a setter.
     */
    @Test
    public void testWithOnlyGetter() throws Exception {
        ModelMBeanInfo info = getMBeanInfoFromAssembler();
        ModelMBeanAttributeInfo attr = info.getAttribute("Superman");
        Assert.assertNotNull("Attribute should not be null", attr);
    }

    @Test
    public void testManagedResourceDescriptor() throws Exception {
        ModelMBeanInfo info = getMBeanInfoFromAssembler();
        Descriptor desc = info.getMBeanDescriptor();
        Assert.assertEquals("Logging should be set to true", "true", desc.getFieldValue("log"));
        Assert.assertEquals("Log file should be jmx.log", "jmx.log", desc.getFieldValue("logFile"));
        Assert.assertEquals("Currency Time Limit should be 15", "15", desc.getFieldValue("currencyTimeLimit"));
        Assert.assertEquals("Persist Policy should be OnUpdate", "OnUpdate", desc.getFieldValue("persistPolicy"));
        Assert.assertEquals("Persist Period should be 200", "200", desc.getFieldValue("persistPeriod"));
        Assert.assertEquals("Persist Location should be foo", "./foo", desc.getFieldValue("persistLocation"));
        Assert.assertEquals("Persist Name should be bar", "bar.jmx", desc.getFieldValue("persistName"));
    }

    @Test
    public void testAttributeDescriptor() throws Exception {
        ModelMBeanInfo info = getMBeanInfoFromAssembler();
        Descriptor desc = info.getAttribute(AbstractJmxAssemblerTests.NAME_ATTRIBUTE).getDescriptor();
        Assert.assertEquals("Default value should be foo", "foo", desc.getFieldValue("default"));
        Assert.assertEquals("Currency Time Limit should be 20", "20", desc.getFieldValue("currencyTimeLimit"));
        Assert.assertEquals("Persist Policy should be OnUpdate", "OnUpdate", desc.getFieldValue("persistPolicy"));
        Assert.assertEquals("Persist Period should be 300", "300", desc.getFieldValue("persistPeriod"));
    }

    @Test
    public void testOperationDescriptor() throws Exception {
        ModelMBeanInfo info = getMBeanInfoFromAssembler();
        Descriptor desc = info.getOperation("myOperation").getDescriptor();
        Assert.assertEquals("Currency Time Limit should be 30", "30", desc.getFieldValue("currencyTimeLimit"));
        Assert.assertEquals("Role should be \"operation\"", "operation", desc.getFieldValue("role"));
    }

    @Test
    public void testOperationParameterMetadata() throws Exception {
        ModelMBeanInfo info = getMBeanInfoFromAssembler();
        ModelMBeanOperationInfo oper = info.getOperation("add");
        MBeanParameterInfo[] params = oper.getSignature();
        Assert.assertEquals("Invalid number of params", 2, params.length);
        Assert.assertEquals("Incorrect name for x param", "x", params[0].getName());
        Assert.assertEquals("Incorrect type for x param", int.class.getName(), params[0].getType());
        Assert.assertEquals("Incorrect name for y param", "y", params[1].getName());
        Assert.assertEquals("Incorrect type for y param", int.class.getName(), params[1].getType());
    }

    @Test
    public void testWithCglibProxy() throws Exception {
        IJmxTestBean tb = createJmxTestBean();
        ProxyFactory pf = new ProxyFactory();
        pf.setTarget(tb);
        pf.addAdvice(new NopInterceptor());
        Object proxy = pf.getProxy();
        MetadataMBeanInfoAssembler assembler = ((MetadataMBeanInfoAssembler) (getAssembler()));
        MBeanExporter exporter = new MBeanExporter();
        exporter.setBeanFactory(getContext());
        exporter.setAssembler(assembler);
        String objectName = "spring:bean=test,proxy=true";
        Map<String, Object> beans = new HashMap<>();
        beans.put(objectName, proxy);
        exporter.setBeans(beans);
        start(exporter);
        MBeanInfo inf = getServer().getMBeanInfo(ObjectNameManager.getInstance(objectName));
        Assert.assertEquals("Incorrect number of operations", getExpectedOperationCount(), inf.getOperations().length);
        Assert.assertEquals("Incorrect number of attributes", getExpectedAttributeCount(), inf.getAttributes().length);
        Assert.assertTrue("Not included in autodetection", assembler.includeBean(proxy.getClass(), "some bean name"));
    }

    @Test
    public void testMetricDescription() throws Exception {
        ModelMBeanInfo inf = getMBeanInfoFromAssembler();
        ModelMBeanAttributeInfo metric = inf.getAttribute(AbstractMetadataAssemblerTests.QUEUE_SIZE_METRIC);
        ModelMBeanOperationInfo operation = inf.getOperation("getQueueSize");
        Assert.assertEquals("The description for the queue size metric is incorrect", "The QueueSize metric", metric.getDescription());
        Assert.assertEquals("The description for the getter operation of the queue size metric is incorrect", "The QueueSize metric", operation.getDescription());
    }

    @Test
    public void testMetricDescriptor() throws Exception {
        ModelMBeanInfo info = getMBeanInfoFromAssembler();
        Descriptor desc = info.getAttribute(AbstractMetadataAssemblerTests.QUEUE_SIZE_METRIC).getDescriptor();
        Assert.assertEquals("Currency Time Limit should be 20", "20", desc.getFieldValue("currencyTimeLimit"));
        Assert.assertEquals("Persist Policy should be OnUpdate", "OnUpdate", desc.getFieldValue("persistPolicy"));
        Assert.assertEquals("Persist Period should be 300", "300", desc.getFieldValue("persistPeriod"));
        Assert.assertEquals("Unit should be messages", "messages", desc.getFieldValue("units"));
        Assert.assertEquals("Display Name should be Queue Size", "Queue Size", desc.getFieldValue("displayName"));
        Assert.assertEquals("Metric Type should be COUNTER", "COUNTER", desc.getFieldValue("metricType"));
        Assert.assertEquals("Metric Category should be utilization", "utilization", desc.getFieldValue("metricCategory"));
    }

    @Test
    public void testMetricDescriptorDefaults() throws Exception {
        ModelMBeanInfo info = getMBeanInfoFromAssembler();
        Descriptor desc = info.getAttribute(AbstractMetadataAssemblerTests.CACHE_ENTRIES_METRIC).getDescriptor();
        Assert.assertNull("Currency Time Limit should not be populated", desc.getFieldValue("currencyTimeLimit"));
        Assert.assertNull("Persist Policy should not be populated", desc.getFieldValue("persistPolicy"));
        Assert.assertNull("Persist Period should not be populated", desc.getFieldValue("persistPeriod"));
        Assert.assertNull("Unit should not be populated", desc.getFieldValue("units"));
        Assert.assertEquals("Display Name should be populated by default via JMX", AbstractMetadataAssemblerTests.CACHE_ENTRIES_METRIC, desc.getFieldValue("displayName"));
        Assert.assertEquals("Metric Type should be GAUGE", "GAUGE", desc.getFieldValue("metricType"));
        Assert.assertNull("Metric Category should not be populated", desc.getFieldValue("metricCategory"));
    }
}

