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
package org.apache.camel.swagger;


import RestSwaggerSupport.HEADER_X_FORWARDED_HOST;
import RestSwaggerSupport.HEADER_X_FORWARDED_PREFIX;
import RestSwaggerSupport.HEADER_X_FORWARDED_PROTO;
import Scheme.HTTP;
import Scheme.HTTPS;
import io.swagger.models.Swagger;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


public class RestSwaggerSupportTest {
    @Test
    public void shouldAdaptFromXForwardHeaders() {
        final Swagger swagger = Mockito.spy(new Swagger().basePath("/base"));
        final Map<String, Object> headers = new HashMap<>();
        headers.put(HEADER_X_FORWARDED_PREFIX, "/prefix");
        headers.put(HEADER_X_FORWARDED_HOST, "host");
        headers.put(HEADER_X_FORWARDED_PROTO, "http, HTTPS ");
        RestSwaggerSupport.setupXForwardedHeaders(swagger, headers);
        Mockito.verify(swagger).getBasePath();
        Mockito.verify(swagger).setBasePath("/prefix/base");
        Mockito.verify(swagger).setHost("host");
        Mockito.verify(swagger).addScheme(HTTP);
        Mockito.verify(swagger).addScheme(HTTPS);
        Mockito.verifyNoMoreInteractions(swagger);
    }

    @Test
    public void shouldNotAdaptFromXForwardHeadersWhenNoHeadersSpecified() {
        final Swagger swagger = Mockito.spy(new Swagger());
        RestSwaggerSupport.setupXForwardedHeaders(swagger, Collections.emptyMap());
        Mockito.verifyZeroInteractions(swagger);
    }
}

