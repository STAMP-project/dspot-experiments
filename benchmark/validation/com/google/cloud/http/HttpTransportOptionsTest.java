/**
 * Copyright 2016 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.cloud.http;


import com.google.api.gax.rpc.HeaderProvider;
import com.google.auth.http.HttpTransportFactory;
import com.google.cloud.ServiceOptions;
import com.google.cloud.http.HttpTransportOptions.DefaultHttpTransportFactory;
import java.util.regex.Pattern;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;


public class HttpTransportOptionsTest {
    private static final HttpTransportFactory MOCK_HTTP_TRANSPORT_FACTORY = EasyMock.createMock(HttpTransportFactory.class);

    private static final HttpTransportOptions OPTIONS = HttpTransportOptions.newBuilder().setConnectTimeout(1234).setHttpTransportFactory(HttpTransportOptionsTest.MOCK_HTTP_TRANSPORT_FACTORY).setReadTimeout(5678).build();

    private static final HttpTransportOptions DEFAULT_OPTIONS = HttpTransportOptions.newBuilder().build();

    private static final HttpTransportOptions OPTIONS_COPY = HttpTransportOptionsTest.OPTIONS.toBuilder().build();

    @Test
    public void testBuilder() {
        Assert.assertEquals(1234, HttpTransportOptionsTest.OPTIONS.getConnectTimeout());
        Assert.assertSame(HttpTransportOptionsTest.MOCK_HTTP_TRANSPORT_FACTORY, HttpTransportOptionsTest.OPTIONS.getHttpTransportFactory());
        Assert.assertEquals(5678, HttpTransportOptionsTest.OPTIONS.getReadTimeout());
        Assert.assertEquals((-1), HttpTransportOptionsTest.DEFAULT_OPTIONS.getConnectTimeout());
        Assert.assertTrue(((HttpTransportOptionsTest.DEFAULT_OPTIONS.getHttpTransportFactory()) instanceof DefaultHttpTransportFactory));
        Assert.assertEquals((-1), HttpTransportOptionsTest.DEFAULT_OPTIONS.getReadTimeout());
    }

    @Test
    public void testBaseEquals() {
        Assert.assertEquals(HttpTransportOptionsTest.OPTIONS, HttpTransportOptionsTest.OPTIONS_COPY);
        Assert.assertNotEquals(HttpTransportOptionsTest.DEFAULT_OPTIONS, HttpTransportOptionsTest.OPTIONS);
    }

    @Test
    public void testBaseHashCode() {
        Assert.assertEquals(HttpTransportOptionsTest.OPTIONS.hashCode(), HttpTransportOptionsTest.OPTIONS_COPY.hashCode());
        Assert.assertNotEquals(HttpTransportOptionsTest.DEFAULT_OPTIONS.hashCode(), HttpTransportOptionsTest.OPTIONS.hashCode());
    }

    @Test
    public void testHeader() {
        String expectedHeaderPattern = "^gl-java/.+ gccl/.* gax/.+";
        ServiceOptions<?, ?> serviceOptions = EasyMock.createMock(ServiceOptions.class);
        HeaderProvider headerProvider = HttpTransportOptionsTest.OPTIONS.getInternalHeaderProviderBuilder(serviceOptions).build();
        Assert.assertEquals(1, headerProvider.getHeaders().size());
        Assert.assertTrue(Pattern.compile(expectedHeaderPattern).matcher(headerProvider.getHeaders().values().iterator().next()).find());
    }
}

