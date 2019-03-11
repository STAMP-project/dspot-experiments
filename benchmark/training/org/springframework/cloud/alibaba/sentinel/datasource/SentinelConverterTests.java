/**
 * Copyright (C) 2018 the original author or authors.
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
package org.springframework.cloud.alibaba.sentinel.datasource;


import RuleConstant.CONTROL_BEHAVIOR_DEFAULT;
import RuleConstant.FLOW_GRADE_QPS;
import RuleConstant.STRATEGY_DIRECT;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.cloud.alibaba.sentinel.datasource.converter.JsonConverter;
import org.springframework.cloud.alibaba.sentinel.datasource.converter.XmlConverter;


/**
 *
 *
 * @author <a href="mailto:fangjian0423@gmail.com">Jim</a>
 */
public class SentinelConverterTests {
    private ObjectMapper objectMapper = new ObjectMapper();

    private XmlMapper xmlMapper = new XmlMapper();

    @Test
    public void testJsonConverter() {
        JsonConverter jsonConverter = new JsonConverter(objectMapper, FlowRule.class);
        List<FlowRule> flowRules = jsonConverter.convert(readFileContent("classpath: flowrule.json"));
        Assert.assertEquals("json converter flow rule size was wrong", 1, flowRules.size());
        Assert.assertEquals("json converter flow rule resource name was wrong", "resource", flowRules.get(0).getResource());
        Assert.assertEquals("json converter flow rule limit app was wrong", "default", flowRules.get(0).getLimitApp());
        Assert.assertEquals("json converter flow rule count was wrong", "1.0", String.valueOf(flowRules.get(0).getCount()));
        Assert.assertEquals("json converter flow rule control behavior was wrong", CONTROL_BEHAVIOR_DEFAULT, flowRules.get(0).getControlBehavior());
        Assert.assertEquals("json converter flow rule strategy was wrong", STRATEGY_DIRECT, flowRules.get(0).getStrategy());
        Assert.assertEquals("json converter flow rule grade was wrong", FLOW_GRADE_QPS, flowRules.get(0).getGrade());
    }

    @Test
    public void testConverterEmptyContent() {
        JsonConverter jsonConverter = new JsonConverter(objectMapper, FlowRule.class);
        List<FlowRule> flowRules = jsonConverter.convert("");
        Assert.assertEquals("json converter flow rule size was not empty", 0, flowRules.size());
    }

    @Test(expected = RuntimeException.class)
    public void testConverterErrorFormat() {
        JsonConverter jsonConverter = new JsonConverter(objectMapper, FlowRule.class);
        jsonConverter.convert(readFileContent("classpath: flowrule-errorformat.json"));
    }

    @Test(expected = RuntimeException.class)
    public void testConverterErrorContent() {
        JsonConverter jsonConverter = new JsonConverter(objectMapper, FlowRule.class);
        jsonConverter.convert(readFileContent("classpath: flowrule-errorcontent.json"));
    }

    @Test
    public void testXmlConverter() {
        XmlConverter jsonConverter = new XmlConverter(xmlMapper, FlowRule.class);
        List<FlowRule> flowRules = jsonConverter.convert(readFileContent("classpath: flowrule.xml"));
        Assert.assertEquals("xml converter flow rule size was wrong", 2, flowRules.size());
        Assert.assertEquals("xml converter flow rule1 resource name was wrong", "resource", flowRules.get(0).getResource());
        Assert.assertEquals("xml converter flow rule2 limit app was wrong", "default", flowRules.get(0).getLimitApp());
        Assert.assertEquals("xml converter flow rule1 count was wrong", "1.0", String.valueOf(flowRules.get(0).getCount()));
        Assert.assertEquals("xml converter flow rule1 control behavior was wrong", CONTROL_BEHAVIOR_DEFAULT, flowRules.get(0).getControlBehavior());
        Assert.assertEquals("xml converter flow rule1 strategy was wrong", STRATEGY_DIRECT, flowRules.get(0).getStrategy());
        Assert.assertEquals("xml converter flow rule1 grade was wrong", FLOW_GRADE_QPS, flowRules.get(0).getGrade());
        Assert.assertEquals("xml converter flow rule2 resource name was wrong", "test", flowRules.get(1).getResource());
        Assert.assertEquals("xml converter flow rule2 limit app was wrong", "default", flowRules.get(1).getLimitApp());
        Assert.assertEquals("xml converter flow rule2 count was wrong", "1.0", String.valueOf(flowRules.get(1).getCount()));
        Assert.assertEquals("xml converter flow rule2 control behavior was wrong", CONTROL_BEHAVIOR_DEFAULT, flowRules.get(1).getControlBehavior());
        Assert.assertEquals("xml converter flow rule2 strategy was wrong", STRATEGY_DIRECT, flowRules.get(1).getStrategy());
        Assert.assertEquals("xml converter flow rule2 grade was wrong", FLOW_GRADE_QPS, flowRules.get(1).getGrade());
    }
}

