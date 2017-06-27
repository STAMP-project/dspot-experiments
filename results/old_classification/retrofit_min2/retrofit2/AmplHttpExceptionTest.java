/**
 * Copyright (C) 2016 Square, Inc.
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


public final class AmplHttpExceptionTest {
    @org.junit.Test
    public void response() {
        retrofit2.Response<java.lang.String> response = retrofit2.Response.success("Hi");
        retrofit2.HttpException exception = new retrofit2.HttpException(response);
        org.assertj.core.api.Assertions.assertThat(exception.code()).isEqualTo(200);
        org.assertj.core.api.Assertions.assertThat(exception.message()).isEqualTo("OK");
        org.assertj.core.api.Assertions.assertThat(exception.response()).isSameAs(response);
    }

    @org.junit.Test
    public void nullResponseThrows() {
        try {
            new retrofit2.HttpException(null);
            org.junit.Assert.fail();
        } catch (java.lang.NullPointerException e) {
            org.assertj.core.api.Assertions.assertThat(e).hasMessage("response == null");
        }
    }
}

