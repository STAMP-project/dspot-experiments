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
package org.apache.hadoop.hbase.http.lib;


import CommonConfigurationKeys.HADOOP_HTTP_STATIC_USER;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.http.lib.StaticUserWebFilter.StaticUserFilter;
import org.apache.hadoop.hbase.testclassification.MiscTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


@Category({ MiscTests.class, SmallTests.class })
public class TestStaticUserWebFilter {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestStaticUserWebFilter.class);

    @Test
    public void testFilter() throws Exception {
        FilterConfig config = mockConfig("myuser");
        StaticUserFilter suf = new StaticUserFilter();
        suf.init(config);
        ArgumentCaptor<HttpServletRequestWrapper> wrapperArg = ArgumentCaptor.forClass(HttpServletRequestWrapper.class);
        FilterChain chain = Mockito.mock(FilterChain.class);
        suf.doFilter(Mockito.mock(HttpServletRequest.class), Mockito.mock(ServletResponse.class), chain);
        Mockito.verify(chain).doFilter(wrapperArg.capture(), Mockito.<ServletResponse>anyObject());
        HttpServletRequestWrapper wrapper = wrapperArg.getValue();
        Assert.assertEquals("myuser", wrapper.getUserPrincipal().getName());
        Assert.assertEquals("myuser", wrapper.getRemoteUser());
        suf.destroy();
    }

    @Test
    public void testOldStyleConfiguration() {
        Configuration conf = new Configuration();
        conf.set("dfs.web.ugi", "joe,group1,group2");
        Assert.assertEquals("joe", StaticUserWebFilter.getUsernameFromConf(conf));
    }

    @Test
    public void testConfiguration() {
        Configuration conf = new Configuration();
        conf.set(HADOOP_HTTP_STATIC_USER, "dr.stack");
        Assert.assertEquals("dr.stack", StaticUserWebFilter.getUsernameFromConf(conf));
    }
}

