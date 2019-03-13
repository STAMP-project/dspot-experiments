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


import RuleType.AUTHORITY;
import RuleType.DEGRADE;
import RuleType.FLOW;
import RuleType.PARAM_FLOW;
import RuleType.SYSTEM;
import com.alibaba.csp.sentinel.slots.block.AbstractRule;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowRule;
import com.alibaba.csp.sentinel.slots.system.SystemRule;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author <a href="mailto:fangjian0423@gmail.com">Jim</a>
 */
public class RuleTypeTests {
    @Test
    public void testGetByName() {
        Assert.assertFalse("empty str rule name was not null", RuleType.getByName("").isPresent());
        Assert.assertFalse("test rule name was not null", RuleType.getByName("test").isPresent());
        Assert.assertFalse("param_flow rule name was not null", RuleType.getByName("param_flow").isPresent());
        Assert.assertFalse("param rule name was not null", RuleType.getByName("param").isPresent());
        Assert.assertFalse("FLOW rule name was not null", RuleType.getByName("FLOW").isPresent());
        Assert.assertTrue("flow rule name was null", RuleType.getByName("flow").isPresent());
        Assert.assertTrue("degrade rule name was null", RuleType.getByName("degrade").isPresent());
        Assert.assertTrue("param-flow rule name was null", RuleType.getByName("param-flow").isPresent());
        Assert.assertTrue("system rule name was null", RuleType.getByName("system").isPresent());
        Assert.assertTrue("authority rule name was null", RuleType.getByName("authority").isPresent());
        Assert.assertEquals("flow rule name was not equals RuleType.FLOW", FLOW, RuleType.getByName("flow").get());
        Assert.assertEquals("flow rule name was not equals RuleType.DEGRADE", DEGRADE, RuleType.getByName("degrade").get());
        Assert.assertEquals("flow rule name was not equals RuleType.PARAM_FLOW", PARAM_FLOW, RuleType.getByName("param-flow").get());
        Assert.assertEquals("flow rule name was not equals RuleType.SYSTEM", SYSTEM, RuleType.getByName("system").get());
        Assert.assertEquals("flow rule name was not equals RuleType.AUTHORITY", AUTHORITY, RuleType.getByName("authority").get());
    }

    @Test
    public void testGetByClass() {
        Assert.assertFalse("Object.class type type was not null", RuleType.getByClass(Object.class).isPresent());
        Assert.assertFalse("AbstractRule.class rule type was not null", RuleType.getByClass(AbstractRule.class).isPresent());
        Assert.assertTrue("FlowRule.class rule type was null", RuleType.getByClass(FlowRule.class).isPresent());
        Assert.assertTrue("DegradeRule.class rule type was null", RuleType.getByClass(DegradeRule.class).isPresent());
        Assert.assertTrue("ParamFlowRule.class rule type was null", RuleType.getByClass(ParamFlowRule.class).isPresent());
        Assert.assertTrue("SystemRule.class rule type was null", RuleType.getByClass(SystemRule.class).isPresent());
        Assert.assertTrue("AuthorityRule.class rule type was null", RuleType.getByClass(AuthorityRule.class).isPresent());
    }
}

