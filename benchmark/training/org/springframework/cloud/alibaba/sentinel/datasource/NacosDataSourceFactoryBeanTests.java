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


import com.alibaba.csp.sentinel.datasource.Converter;
import com.alibaba.csp.sentinel.datasource.nacos.NacosDataSource;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.cloud.alibaba.sentinel.datasource.converter.SentinelConverter;
import org.springframework.cloud.alibaba.sentinel.datasource.factorybean.NacosDataSourceFactoryBean;


/**
 *
 *
 * @author <a href="mailto:fangjian0423@gmail.com">Jim</a>
 */
public class NacosDataSourceFactoryBeanTests {
    private String dataId = "sentinel";

    private String groupId = "DEFAULT_GROUP";

    private String serverAddr = "localhost:8848";

    private String accessKey = "ak";

    private String secretKey = "sk";

    private String endpoint = "endpoint";

    private String namespace = "namespace";

    @Test
    public void testNacosFactoryBeanServerAddr() throws Exception {
        NacosDataSourceFactoryBean factoryBean = Mockito.spy(new NacosDataSourceFactoryBean());
        Converter converter = Mockito.mock(SentinelConverter.class);
        factoryBean.setDataId(dataId);
        factoryBean.setGroupId(groupId);
        factoryBean.setServerAddr(serverAddr);
        factoryBean.setConverter(converter);
        NacosDataSource nacosDataSource = Mockito.mock(NacosDataSource.class);
        Mockito.doReturn(nacosDataSource).when(factoryBean).getObject();
        Mockito.when(nacosDataSource.readSource()).thenReturn("{}");
        Assert.assertEquals("NacosDataSourceFactoryBean getObject was wrong", nacosDataSource, factoryBean.getObject());
        Assert.assertEquals("NacosDataSource read source value was wrong", "{}", factoryBean.getObject().readSource());
        Assert.assertEquals("NacosDataSource converter was wrong", converter, factoryBean.getConverter());
        Assert.assertEquals("NacosDataSourceFactoryBean dataId was wrong", dataId, factoryBean.getDataId());
        Assert.assertEquals("NacosDataSourceFactoryBean groupId was wrong", groupId, factoryBean.getGroupId());
        Assert.assertEquals("NacosDataSourceFactoryBean serverAddr was wrong", serverAddr, factoryBean.getServerAddr());
    }

    @Test
    public void testNacosFactoryBeanProperties() throws Exception {
        NacosDataSourceFactoryBean factoryBean = Mockito.spy(new NacosDataSourceFactoryBean());
        Converter converter = Mockito.mock(SentinelConverter.class);
        factoryBean.setDataId(dataId);
        factoryBean.setGroupId(groupId);
        factoryBean.setAccessKey(accessKey);
        factoryBean.setSecretKey(secretKey);
        factoryBean.setEndpoint(endpoint);
        factoryBean.setNamespace(namespace);
        factoryBean.setConverter(converter);
        NacosDataSource nacosDataSource = Mockito.mock(NacosDataSource.class);
        Mockito.doReturn(nacosDataSource).when(factoryBean).getObject();
        Mockito.when(nacosDataSource.readSource()).thenReturn("{}");
        Assert.assertEquals("NacosDataSourceFactoryBean getObject was wrong", nacosDataSource, factoryBean.getObject());
        Assert.assertEquals("NacosDataSource read source value was wrong", "{}", factoryBean.getObject().readSource());
        Assert.assertEquals("NacosDataSource converter was wrong", converter, factoryBean.getConverter());
        Assert.assertEquals("NacosDataSourceFactoryBean dataId was wrong", dataId, factoryBean.getDataId());
        Assert.assertEquals("NacosDataSourceFactoryBean groupId was wrong", groupId, factoryBean.getGroupId());
        Assert.assertEquals("NacosDataSourceFactoryBean namespace was wrong", namespace, factoryBean.getNamespace());
        Assert.assertEquals("NacosDataSourceFactoryBean endpoint was wrong", endpoint, factoryBean.getEndpoint());
        Assert.assertEquals("NacosDataSourceFactoryBean ak was wrong", accessKey, factoryBean.getAccessKey());
        Assert.assertEquals("NacosDataSourceFactoryBean sk was wrong", secretKey, factoryBean.getSecretKey());
    }
}

