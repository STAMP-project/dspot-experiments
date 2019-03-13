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
import com.alibaba.csp.sentinel.datasource.apollo.ApolloDataSource;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.cloud.alibaba.sentinel.datasource.converter.JsonConverter;
import org.springframework.cloud.alibaba.sentinel.datasource.factorybean.ApolloDataSourceFactoryBean;


/**
 *
 *
 * @author <a href="mailto:fangjian0423@gmail.com">Jim</a>
 */
public class ApolloDataSourceFactoryBeanTests {
    private String flowRuleKey = "sentinel";

    private String namespace = "namespace";

    private String defaultFlowValue = "{}";

    @Test
    public void testApolloFactoryBean() throws Exception {
        ApolloDataSourceFactoryBean factoryBean = Mockito.spy(new ApolloDataSourceFactoryBean());
        Converter converter = Mockito.mock(JsonConverter.class);
        factoryBean.setDefaultFlowRuleValue(defaultFlowValue);
        factoryBean.setFlowRulesKey(flowRuleKey);
        factoryBean.setNamespaceName(namespace);
        factoryBean.setConverter(converter);
        ApolloDataSource apolloDataSource = Mockito.mock(ApolloDataSource.class);
        Mockito.when(apolloDataSource.readSource()).thenReturn("{}");
        Mockito.doReturn(apolloDataSource).when(factoryBean).getObject();
        Assert.assertEquals("ApolloDataSourceFactoryBean getObject error", apolloDataSource, factoryBean.getObject());
        Assert.assertEquals("ApolloDataSource read source value was wrong", "{}", factoryBean.getObject().readSource());
        Assert.assertEquals("ApolloDataSource converter was wrong", converter, factoryBean.getConverter());
        Assert.assertEquals("ApolloDataSourceFactoryBean flowRuleKey was wrong", flowRuleKey, factoryBean.getFlowRulesKey());
        Assert.assertEquals("ApolloDataSourceFactoryBean namespace was wrong", namespace, factoryBean.getNamespaceName());
        Assert.assertEquals("ApolloDataSourceFactoryBean defaultFlowValue was wrong", defaultFlowValue, factoryBean.getDefaultFlowRuleValue());
    }
}

