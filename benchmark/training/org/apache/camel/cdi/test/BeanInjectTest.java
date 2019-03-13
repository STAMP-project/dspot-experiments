/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.cdi.test;


import javax.inject.Inject;
import org.apache.camel.cdi.bean.BeanInjectBean;
import org.hamcrest.Matchers;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(Arquillian.class)
public class BeanInjectTest {
    @Inject
    private BeanInjectBean bean;

    @Test
    public void beanInjectField() {
        Assert.assertThat(bean.getInjectBeanField(), Matchers.is(Matchers.notNullValue()));
        Assert.assertThat(bean.getInjectBeanField().getProperty(), Matchers.is(Matchers.equalTo("value")));
    }

    @Test
    public void beanInjectMethod() {
        Assert.assertThat(bean.getInjectBeanMethod(), Matchers.is(Matchers.notNullValue()));
        Assert.assertThat(bean.getInjectBeanMethod().getProperty(), Matchers.is(Matchers.equalTo("value")));
    }

    @Test
    public void beanInjectNamed() {
        Assert.assertThat(bean.getInjectBeanNamed(), Matchers.is(Matchers.notNullValue()));
    }
}

