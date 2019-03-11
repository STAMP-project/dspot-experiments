/**
 * Copyright 2016 Netflix, Inc.
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
package io.reactivex.netty.examples.http.perf;


import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.HttpResponse;
import io.reactivex.netty.protocol.http.internal.HttpMessageFormatter;
import java.lang.reflect.InvocationTargetException;
import org.junit.Test;
import rx.Observable;


public class PerfTest {
    @Test(timeout = 60000)
    public void testPerf() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Observable.range(1, 10).toBlocking().forEach(( interval) -> {
            final Queue<String> output = runClientInMockedEnvironment(.class);
            HttpResponse expectedHeader = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
            expectedHeader.headers().add(HttpHeaderNames.CONTENT_LENGTH, 9);
            String expectedHeaderString = HttpMessageFormatter.formatResponse(expectedHeader.protocolVersion(), expectedHeader.status(), expectedHeader.headers().iteratorCharSequence());
            assertThat("Unexpected number of messages echoed", output, hasSize(2));
            assertThat("Unexpected response.", output, contains(expectedHeaderString, "Welcome!!"));
            output.clear();
        });
    }
}

