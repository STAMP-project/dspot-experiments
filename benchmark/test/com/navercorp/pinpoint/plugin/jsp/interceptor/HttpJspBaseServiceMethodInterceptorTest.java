/**
 * Copyright 2017 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.navercorp.pinpoint.plugin.jsp.interceptor;


import com.navercorp.pinpoint.bootstrap.context.MethodDescriptor;
import com.navercorp.pinpoint.bootstrap.context.TraceContext;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 *
 *
 * @author jaehong.kim
 */
public class HttpJspBaseServiceMethodInterceptorTest {
    @Test
    public void parseJspName() throws Exception {
        TraceContext traceContext = Mockito.mock(TraceContext.class);
        MethodDescriptor descriptor = Mockito.mock(MethodDescriptor.class);
        HttpJspBaseServiceMethodInterceptor interceptor = new HttpJspBaseServiceMethodInterceptor(traceContext, descriptor);
        Assert.assertEquals("WEB-INF/views/docs.jsp", interceptor.parseJspName("org.apache.jsp.WEB_002dINF.views.docs_jsp"));
        Assert.assertEquals("WEB-INF", interceptor.parseJspName("WEB_002dINF"));
        Assert.assertEquals("foo_.jsp", interceptor.parseJspName("WEB_002dINF.foo__jsp"));
        Assert.assertEquals("docs.jsp", interceptor.parseJspName("org.apache.jsp.docs_jsp"));
        Assert.assertEquals("bar", interceptor.parseJspName(".bar"));
        Assert.assertEquals("bar", interceptor.parseJspName("foo.bar"));
        Assert.assertEquals("docs.jsp", interceptor.parseJspName("com.navercorp.jsp.docs_jsp"));
        Assert.assertEquals("unknown", interceptor.parseJspName("unknown"));
        Assert.assertEquals("unknown_foo-bar", interceptor.parseJspName("unknown_foo-bar"));
        Assert.assertEquals("", interceptor.parseJspName("unknown."));
        Assert.assertEquals("", interceptor.parseJspName(""));
        Assert.assertEquals("", interceptor.parseJspName("."));
        Assert.assertEquals("", interceptor.parseJspName(".."));
        Assert.assertEquals(null, interceptor.parseJspName(null));
    }
}

