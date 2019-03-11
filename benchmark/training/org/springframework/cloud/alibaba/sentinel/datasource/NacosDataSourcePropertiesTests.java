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


import RuleType.FLOW;
import RuleType.SYSTEM;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.cloud.alibaba.sentinel.datasource.config.NacosDataSourceProperties;
import org.springframework.cloud.alibaba.sentinel.datasource.factorybean.NacosDataSourceFactoryBean;


/**
 *
 *
 * @author <a href="mailto:fangjian0423@gmail.com">Jim</a>
 */
public class NacosDataSourcePropertiesTests {
    @Test
    public void testNacosWithAddr() {
        NacosDataSourceProperties nacosDataSourceProperties = new NacosDataSourceProperties();
        nacosDataSourceProperties.setServerAddr("127.0.0.1:8848");
        nacosDataSourceProperties.setRuleType(FLOW);
        nacosDataSourceProperties.setDataId("sentinel");
        nacosDataSourceProperties.setGroupId("custom-group");
        nacosDataSourceProperties.setDataType("xml");
        Assert.assertEquals("Nacos groupId was wrong", "custom-group", nacosDataSourceProperties.getGroupId());
        Assert.assertEquals("Nacos dataId was wrong", "sentinel", nacosDataSourceProperties.getDataId());
        Assert.assertEquals("Nacos default data type was wrong", "xml", nacosDataSourceProperties.getDataType());
        Assert.assertEquals("Nacos rule type was wrong", FLOW, nacosDataSourceProperties.getRuleType());
        Assert.assertEquals("Nacos default factory bean was wrong", NacosDataSourceFactoryBean.class.getName(), nacosDataSourceProperties.getFactoryBeanName());
    }

    @Test
    public void testNacosWithProperties() {
        NacosDataSourceProperties nacosDataSourceProperties = new NacosDataSourceProperties();
        nacosDataSourceProperties.setAccessKey("ak");
        nacosDataSourceProperties.setSecretKey("sk");
        nacosDataSourceProperties.setEndpoint("endpoint");
        nacosDataSourceProperties.setNamespace("namespace");
        nacosDataSourceProperties.setRuleType(SYSTEM);
        Assert.assertEquals("Nacos ak was wrong", "ak", nacosDataSourceProperties.getAccessKey());
        Assert.assertEquals("Nacos sk was wrong", "sk", nacosDataSourceProperties.getSecretKey());
        Assert.assertEquals("Nacos endpoint was wrong", "endpoint", nacosDataSourceProperties.getEndpoint());
        Assert.assertEquals("Nacos namespace was wrong", "namespace", nacosDataSourceProperties.getNamespace());
        Assert.assertEquals("Nacos rule type was wrong", SYSTEM, nacosDataSourceProperties.getRuleType());
    }
}

