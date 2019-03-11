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
package feign.auth;


import feign.RequestTemplate;
import feign.assertj.FeignAssertions;
import java.util.Arrays;
import org.junit.Test;


public class BasicAuthRequestInterceptorTest {
    @Test
    public void addsAuthorizationHeader() {
        RequestTemplate template = new RequestTemplate();
        BasicAuthRequestInterceptor interceptor = new BasicAuthRequestInterceptor("Aladdin", "open sesame");
        interceptor.apply(template);
        FeignAssertions.assertThat(template).hasHeaders(entry("Authorization", Arrays.asList("Basic QWxhZGRpbjpvcGVuIHNlc2FtZQ==")));
    }

    @Test
    public void addsAuthorizationHeader_longUserAndPassword() {
        RequestTemplate template = new RequestTemplate();
        BasicAuthRequestInterceptor interceptor = new BasicAuthRequestInterceptor("IOIOIOIOIOIOIOIOIOIOIOIOIOIOIOIOIOIOIO", "101010101010101010101010101010101010101010");
        interceptor.apply(template);
        FeignAssertions.assertThat(template).hasHeaders(entry("Authorization", Arrays.asList("Basic SU9JT0lPSU9JT0lPSU9JT0lPSU9JT0lPSU9JT0lPSU9JT0lPSU86MTAxMDEwMTAxMDEwMTAxMDEwMTAxMDEwMTAxMDEwMTAxMDEwMTAxMDEw")));
    }
}

