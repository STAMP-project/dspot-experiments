/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.flume.interceptor;


import Interceptor.Builder;
import InterceptorType.REMOVE_HEADER;
import RemoveHeaderInterceptor.FROM_LIST;
import RemoveHeaderInterceptor.LIST_SEPARATOR;
import RemoveHeaderInterceptor.MATCH_REGEX;
import RemoveHeaderInterceptor.WITH_NAME;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.PatternSyntaxException;
import junit.framework.Assert;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.junit.Test;


public class RemoveHeaderInterceptorTest {
    private static final String HEADER1 = "my-header10";

    private static final String HEADER2 = "my-header11";

    private static final String HEADER3 = "my-header12";

    private static final String HEADER4 = "my-header20";

    private static final String HEADER5 = "my-header21";

    private static final String DEFAULT_SEPARATOR = ", ";

    private static final String MY_SEPARATOR = ";";

    @Test(expected = PatternSyntaxException.class)
    public void testBadConfig() throws Exception {
        new RemoveHeaderInterceptorTest.RemoveHeaderIntBuilder().fromList(RemoveHeaderInterceptorTest.HEADER1, "(").build();
    }

    @Test
    public void testWithName() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        final Interceptor removeHeaderInterceptor = new RemoveHeaderInterceptorTest.RemoveHeaderIntBuilder().withName(RemoveHeaderInterceptorTest.HEADER4).build();
        final Event event1 = buildEventWithHeader();
        Assert.assertEquals(RemoveHeaderInterceptorTest.HEADER1, event1.getHeaders().get(RemoveHeaderInterceptorTest.HEADER1));
        Assert.assertEquals(RemoveHeaderInterceptorTest.HEADER2, event1.getHeaders().get(RemoveHeaderInterceptorTest.HEADER2));
        Assert.assertEquals(RemoveHeaderInterceptorTest.HEADER3, event1.getHeaders().get(RemoveHeaderInterceptorTest.HEADER3));
        Assert.assertEquals(RemoveHeaderInterceptorTest.HEADER4, event1.getHeaders().get(RemoveHeaderInterceptorTest.HEADER4));
        Assert.assertEquals(RemoveHeaderInterceptorTest.HEADER5, event1.getHeaders().get(RemoveHeaderInterceptorTest.HEADER5));
        removeHeaderInterceptor.intercept(event1);
        Assert.assertEquals(RemoveHeaderInterceptorTest.HEADER1, event1.getHeaders().get(RemoveHeaderInterceptorTest.HEADER1));
        Assert.assertEquals(RemoveHeaderInterceptorTest.HEADER2, event1.getHeaders().get(RemoveHeaderInterceptorTest.HEADER2));
        Assert.assertEquals(RemoveHeaderInterceptorTest.HEADER3, event1.getHeaders().get(RemoveHeaderInterceptorTest.HEADER3));
        Assert.assertNull(event1.getHeaders().get(RemoveHeaderInterceptorTest.HEADER4));
        Assert.assertEquals(RemoveHeaderInterceptorTest.HEADER5, event1.getHeaders().get(RemoveHeaderInterceptorTest.HEADER5));
        final Event event2 = buildEventWithoutHeader();
        Assert.assertTrue(event2.getHeaders().isEmpty());
        removeHeaderInterceptor.intercept(event2);
        Assert.assertTrue(event2.getHeaders().isEmpty());
    }

    @Test
    public void testFromListWithDefaultSeparator1() throws Exception {
        final Interceptor removeHeaderInterceptor = new RemoveHeaderInterceptorTest.RemoveHeaderIntBuilder().fromList((((RemoveHeaderInterceptorTest.HEADER4) + (RemoveHeaderInterceptorTest.MY_SEPARATOR)) + (RemoveHeaderInterceptorTest.HEADER2))).build();
        final Event event1 = buildEventWithHeader();
        Assert.assertEquals(RemoveHeaderInterceptorTest.HEADER1, event1.getHeaders().get(RemoveHeaderInterceptorTest.HEADER1));
        Assert.assertEquals(RemoveHeaderInterceptorTest.HEADER2, event1.getHeaders().get(RemoveHeaderInterceptorTest.HEADER2));
        Assert.assertEquals(RemoveHeaderInterceptorTest.HEADER3, event1.getHeaders().get(RemoveHeaderInterceptorTest.HEADER3));
        Assert.assertEquals(RemoveHeaderInterceptorTest.HEADER4, event1.getHeaders().get(RemoveHeaderInterceptorTest.HEADER4));
        Assert.assertEquals(RemoveHeaderInterceptorTest.HEADER5, event1.getHeaders().get(RemoveHeaderInterceptorTest.HEADER5));
        removeHeaderInterceptor.intercept(event1);
        Assert.assertEquals(RemoveHeaderInterceptorTest.HEADER1, event1.getHeaders().get(RemoveHeaderInterceptorTest.HEADER1));
        Assert.assertEquals(RemoveHeaderInterceptorTest.HEADER2, event1.getHeaders().get(RemoveHeaderInterceptorTest.HEADER2));
        Assert.assertEquals(RemoveHeaderInterceptorTest.HEADER3, event1.getHeaders().get(RemoveHeaderInterceptorTest.HEADER3));
        Assert.assertEquals(RemoveHeaderInterceptorTest.HEADER4, event1.getHeaders().get(RemoveHeaderInterceptorTest.HEADER4));
        Assert.assertEquals(RemoveHeaderInterceptorTest.HEADER5, event1.getHeaders().get(RemoveHeaderInterceptorTest.HEADER5));
        final Event event2 = buildEventWithoutHeader();
        Assert.assertTrue(event2.getHeaders().isEmpty());
        removeHeaderInterceptor.intercept(event2);
        Assert.assertTrue(event2.getHeaders().isEmpty());
    }

    @Test
    public void testFromListWithDefaultSeparator2() throws Exception {
        final Interceptor removeHeaderInterceptor = new RemoveHeaderInterceptorTest.RemoveHeaderIntBuilder().fromList((((RemoveHeaderInterceptorTest.HEADER4) + (RemoveHeaderInterceptorTest.DEFAULT_SEPARATOR)) + (RemoveHeaderInterceptorTest.HEADER2))).build();
        final Event event1 = buildEventWithHeader();
        Assert.assertEquals(RemoveHeaderInterceptorTest.HEADER1, event1.getHeaders().get(RemoveHeaderInterceptorTest.HEADER1));
        Assert.assertEquals(RemoveHeaderInterceptorTest.HEADER2, event1.getHeaders().get(RemoveHeaderInterceptorTest.HEADER2));
        Assert.assertEquals(RemoveHeaderInterceptorTest.HEADER3, event1.getHeaders().get(RemoveHeaderInterceptorTest.HEADER3));
        Assert.assertEquals(RemoveHeaderInterceptorTest.HEADER4, event1.getHeaders().get(RemoveHeaderInterceptorTest.HEADER4));
        Assert.assertEquals(RemoveHeaderInterceptorTest.HEADER5, event1.getHeaders().get(RemoveHeaderInterceptorTest.HEADER5));
        removeHeaderInterceptor.intercept(event1);
        Assert.assertEquals(RemoveHeaderInterceptorTest.HEADER1, event1.getHeaders().get(RemoveHeaderInterceptorTest.HEADER1));
        Assert.assertNull(event1.getHeaders().get(RemoveHeaderInterceptorTest.HEADER2));
        Assert.assertEquals(RemoveHeaderInterceptorTest.HEADER3, event1.getHeaders().get(RemoveHeaderInterceptorTest.HEADER3));
        Assert.assertNull(event1.getHeaders().get(RemoveHeaderInterceptorTest.HEADER4));
        Assert.assertEquals(RemoveHeaderInterceptorTest.HEADER5, event1.getHeaders().get(RemoveHeaderInterceptorTest.HEADER5));
        final Event event2 = buildEventWithoutHeader();
        Assert.assertTrue(event2.getHeaders().isEmpty());
        removeHeaderInterceptor.intercept(event2);
        Assert.assertTrue(event2.getHeaders().isEmpty());
    }

    @Test
    public void testFromListWithCustomSeparator1() throws Exception {
        final Interceptor removeHeaderInterceptor = new RemoveHeaderInterceptorTest.RemoveHeaderIntBuilder().fromList((((RemoveHeaderInterceptorTest.HEADER4) + (RemoveHeaderInterceptorTest.MY_SEPARATOR)) + (RemoveHeaderInterceptorTest.HEADER2)), RemoveHeaderInterceptorTest.MY_SEPARATOR).build();
        final Event event1 = buildEventWithHeader();
        Assert.assertEquals(RemoveHeaderInterceptorTest.HEADER1, event1.getHeaders().get(RemoveHeaderInterceptorTest.HEADER1));
        Assert.assertEquals(RemoveHeaderInterceptorTest.HEADER2, event1.getHeaders().get(RemoveHeaderInterceptorTest.HEADER2));
        Assert.assertEquals(RemoveHeaderInterceptorTest.HEADER3, event1.getHeaders().get(RemoveHeaderInterceptorTest.HEADER3));
        Assert.assertEquals(RemoveHeaderInterceptorTest.HEADER4, event1.getHeaders().get(RemoveHeaderInterceptorTest.HEADER4));
        Assert.assertEquals(RemoveHeaderInterceptorTest.HEADER5, event1.getHeaders().get(RemoveHeaderInterceptorTest.HEADER5));
        removeHeaderInterceptor.intercept(event1);
        Assert.assertEquals(RemoveHeaderInterceptorTest.HEADER1, event1.getHeaders().get(RemoveHeaderInterceptorTest.HEADER1));
        Assert.assertNull(event1.getHeaders().get(RemoveHeaderInterceptorTest.HEADER2));
        Assert.assertEquals(RemoveHeaderInterceptorTest.HEADER3, event1.getHeaders().get(RemoveHeaderInterceptorTest.HEADER3));
        Assert.assertNull(event1.getHeaders().get(RemoveHeaderInterceptorTest.HEADER4));
        Assert.assertEquals(RemoveHeaderInterceptorTest.HEADER5, event1.getHeaders().get(RemoveHeaderInterceptorTest.HEADER5));
        final Event event2 = buildEventWithoutHeader();
        Assert.assertTrue(event2.getHeaders().isEmpty());
        removeHeaderInterceptor.intercept(event2);
        Assert.assertTrue(event2.getHeaders().isEmpty());
    }

    @Test
    public void testFromListWithCustomSeparator2() throws Exception {
        final Interceptor removeHeaderInterceptor = new RemoveHeaderInterceptorTest.RemoveHeaderIntBuilder().fromList((((RemoveHeaderInterceptorTest.HEADER4) + (RemoveHeaderInterceptorTest.DEFAULT_SEPARATOR)) + (RemoveHeaderInterceptorTest.HEADER2)), RemoveHeaderInterceptorTest.MY_SEPARATOR).build();
        final Event event1 = buildEventWithHeader();
        Assert.assertEquals(RemoveHeaderInterceptorTest.HEADER1, event1.getHeaders().get(RemoveHeaderInterceptorTest.HEADER1));
        Assert.assertEquals(RemoveHeaderInterceptorTest.HEADER2, event1.getHeaders().get(RemoveHeaderInterceptorTest.HEADER2));
        Assert.assertEquals(RemoveHeaderInterceptorTest.HEADER3, event1.getHeaders().get(RemoveHeaderInterceptorTest.HEADER3));
        Assert.assertEquals(RemoveHeaderInterceptorTest.HEADER4, event1.getHeaders().get(RemoveHeaderInterceptorTest.HEADER4));
        Assert.assertEquals(RemoveHeaderInterceptorTest.HEADER5, event1.getHeaders().get(RemoveHeaderInterceptorTest.HEADER5));
        removeHeaderInterceptor.intercept(event1);
        Assert.assertEquals(RemoveHeaderInterceptorTest.HEADER1, event1.getHeaders().get(RemoveHeaderInterceptorTest.HEADER1));
        Assert.assertEquals(RemoveHeaderInterceptorTest.HEADER2, event1.getHeaders().get(RemoveHeaderInterceptorTest.HEADER2));
        Assert.assertEquals(RemoveHeaderInterceptorTest.HEADER3, event1.getHeaders().get(RemoveHeaderInterceptorTest.HEADER3));
        Assert.assertEquals(RemoveHeaderInterceptorTest.HEADER4, event1.getHeaders().get(RemoveHeaderInterceptorTest.HEADER4));
        Assert.assertEquals(RemoveHeaderInterceptorTest.HEADER5, event1.getHeaders().get(RemoveHeaderInterceptorTest.HEADER5));
        final Event event2 = buildEventWithoutHeader();
        Assert.assertTrue(event2.getHeaders().isEmpty());
        removeHeaderInterceptor.intercept(event2);
        Assert.assertTrue(event2.getHeaders().isEmpty());
    }

    @Test
    public void testMatchRegex() throws Exception {
        final Interceptor removeHeaderInterceptor = new RemoveHeaderInterceptorTest.RemoveHeaderIntBuilder().matchRegex("my-header1.*").build();
        final Event event1 = buildEventWithHeader();
        Assert.assertEquals(RemoveHeaderInterceptorTest.HEADER1, event1.getHeaders().get(RemoveHeaderInterceptorTest.HEADER1));
        Assert.assertEquals(RemoveHeaderInterceptorTest.HEADER2, event1.getHeaders().get(RemoveHeaderInterceptorTest.HEADER2));
        Assert.assertEquals(RemoveHeaderInterceptorTest.HEADER3, event1.getHeaders().get(RemoveHeaderInterceptorTest.HEADER3));
        Assert.assertEquals(RemoveHeaderInterceptorTest.HEADER4, event1.getHeaders().get(RemoveHeaderInterceptorTest.HEADER4));
        Assert.assertEquals(RemoveHeaderInterceptorTest.HEADER5, event1.getHeaders().get(RemoveHeaderInterceptorTest.HEADER5));
        removeHeaderInterceptor.intercept(event1);
        Assert.assertNull(event1.getHeaders().get(RemoveHeaderInterceptorTest.HEADER1));
        Assert.assertNull(event1.getHeaders().get(RemoveHeaderInterceptorTest.HEADER2));
        Assert.assertNull(event1.getHeaders().get(RemoveHeaderInterceptorTest.HEADER3));
        Assert.assertEquals(RemoveHeaderInterceptorTest.HEADER4, event1.getHeaders().get(RemoveHeaderInterceptorTest.HEADER4));
        Assert.assertEquals(RemoveHeaderInterceptorTest.HEADER5, event1.getHeaders().get(RemoveHeaderInterceptorTest.HEADER5));
        final Event event2 = buildEventWithoutHeader();
        Assert.assertTrue(event2.getHeaders().isEmpty());
        removeHeaderInterceptor.intercept(event2);
        Assert.assertTrue(event2.getHeaders().isEmpty());
    }

    @Test
    public void testAll() throws Exception {
        final Interceptor removeHeaderInterceptor = new RemoveHeaderInterceptorTest.RemoveHeaderIntBuilder().matchRegex("my-header2.*").fromList((((RemoveHeaderInterceptorTest.HEADER1) + (RemoveHeaderInterceptorTest.MY_SEPARATOR)) + (RemoveHeaderInterceptorTest.HEADER3)), RemoveHeaderInterceptorTest.MY_SEPARATOR).withName(RemoveHeaderInterceptorTest.HEADER2).build();
        final Event event1 = buildEventWithHeader();
        Assert.assertEquals(RemoveHeaderInterceptorTest.HEADER1, event1.getHeaders().get(RemoveHeaderInterceptorTest.HEADER1));
        Assert.assertEquals(RemoveHeaderInterceptorTest.HEADER2, event1.getHeaders().get(RemoveHeaderInterceptorTest.HEADER2));
        Assert.assertEquals(RemoveHeaderInterceptorTest.HEADER3, event1.getHeaders().get(RemoveHeaderInterceptorTest.HEADER3));
        Assert.assertEquals(RemoveHeaderInterceptorTest.HEADER4, event1.getHeaders().get(RemoveHeaderInterceptorTest.HEADER4));
        Assert.assertEquals(RemoveHeaderInterceptorTest.HEADER5, event1.getHeaders().get(RemoveHeaderInterceptorTest.HEADER5));
        removeHeaderInterceptor.intercept(event1);
        Assert.assertTrue(event1.getHeaders().isEmpty());
        final Event event2 = buildEventWithoutHeader();
        Assert.assertTrue(event2.getHeaders().isEmpty());
        removeHeaderInterceptor.intercept(event2);
        Assert.assertTrue(event2.getHeaders().isEmpty());
    }

    private static class RemoveHeaderIntBuilder {
        final Map<String, String> contextMap = new HashMap<>();

        RemoveHeaderInterceptorTest.RemoveHeaderIntBuilder withName(final String str) {
            contextMap.put(WITH_NAME, str);
            return this;
        }

        RemoveHeaderInterceptorTest.RemoveHeaderIntBuilder fromList(final String str) {
            contextMap.put(FROM_LIST, str);
            return this;
        }

        RemoveHeaderInterceptorTest.RemoveHeaderIntBuilder fromList(final String str, final String separator) {
            fromList(str);
            contextMap.put(LIST_SEPARATOR, separator);
            return this;
        }

        RemoveHeaderInterceptorTest.RemoveHeaderIntBuilder matchRegex(final String str) {
            contextMap.put(MATCH_REGEX, str);
            return this;
        }

        public Interceptor build() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
            Interceptor.Builder builder = InterceptorBuilderFactory.newInstance(REMOVE_HEADER.toString());
            builder.configure(new Context(contextMap));
            return builder.build();
        }
    }
}

