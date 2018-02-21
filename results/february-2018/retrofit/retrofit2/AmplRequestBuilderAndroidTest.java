/**
 * Copyright (C) 2015 Square, Inc.
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
package retrofit2;


// Parameters inspected reflectively.
@org.junit.runner.RunWith(org.robolectric.RobolectricTestRunner.class)
@java.lang.SuppressWarnings({ "UnusedParameters" , "unused" })
public final class AmplRequestBuilderAndroidTest {
    @org.junit.Test
    public void getWithAndroidUriUrl() {
        class Example {
            @retrofit2.http.GET
            retrofit2.Call<okhttp3.ResponseBody> method(@retrofit2.http.Url
            android.net.Uri url) {
                return null;
            }
        }
        okhttp3.Request request = retrofit2.RequestBuilderTest.buildRequest(Example.class, android.net.Uri.parse("foo/bar/"));
        org.assertj.core.api.Assertions.assertThat(request.method()).isEqualTo("GET");
        org.assertj.core.api.Assertions.assertThat(request.headers().size()).isZero();
        org.assertj.core.api.Assertions.assertThat(request.url().toString()).isEqualTo("http://example.com/foo/bar/");
        org.assertj.core.api.Assertions.assertThat(request.body()).isNull();
    }

    @org.junit.Test
    public void getWithAndroidUriUrlAbsolute() {
        class Example {
            @retrofit2.http.GET
            retrofit2.Call<okhttp3.ResponseBody> method(@retrofit2.http.Url
            android.net.Uri url) {
                return null;
            }
        }
        okhttp3.Request request = retrofit2.RequestBuilderTest.buildRequest(Example.class, android.net.Uri.parse("https://example2.com/foo/bar/"));
        org.assertj.core.api.Assertions.assertThat(request.method()).isEqualTo("GET");
        org.assertj.core.api.Assertions.assertThat(request.headers().size()).isZero();
        org.assertj.core.api.Assertions.assertThat(request.url().toString()).isEqualTo("https://example2.com/foo/bar/");
        org.assertj.core.api.Assertions.assertThat(request.body()).isNull();
    }
}

