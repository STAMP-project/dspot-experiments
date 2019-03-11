/**
 * Copyright 2012-2019 The Feign Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package feign.codec;


import feign.Response;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static java.util.Collections.emptyMap;


@RunWith(Parameterized.class)
public class DefaultErrorDecoderHttpErrorTest {
    @Parameterized.Parameter
    public int httpStatus;

    @Parameterized.Parameter(1)
    public Class expectedExceptionClass;

    private ErrorDecoder errorDecoder = new ErrorDecoder.Default();

    private Map<String, Collection<String>> headers = new LinkedHashMap<>();

    @Test
    public void testExceptionIsHttpSpecific() throws Throwable {
        Response response = Response.builder().status(httpStatus).reason("anything").request(feign.Request.create(HttpMethod.GET, "/api", emptyMap(), null, Util.UTF_8)).headers(headers).build();
        Exception exception = errorDecoder.decode("Service#foo()", response);
        assertThat(exception).isInstanceOf(expectedExceptionClass);
        assertThat(status()).isEqualTo(httpStatus);
    }
}

