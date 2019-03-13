/**
 * Copyright 2002-2019 the original author or authors.
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
package org.springframework.security.web.header.writers;


import java.util.Arrays;
import java.util.Collections;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.web.header.Header;


/**
 * Test for the {@code StaticHeadersWriter}
 *
 * @author Marten Deinum
 * @author Rob Winch
 * @author Ankur Pathak
 * @since 3.2
 */
public class StaticHeaderWriterTests {
    private MockHttpServletRequest request;

    private MockHttpServletResponse response;

    @Test(expected = IllegalArgumentException.class)
    public void constructorNullHeaders() {
        new StaticHeadersWriter(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorEmptyHeaders() {
        new StaticHeadersWriter(Collections.<Header>emptyList());
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorNullHeaderName() {
        new StaticHeadersWriter(null, "value1");
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorNullHeaderValues() {
        new StaticHeadersWriter("name", ((String[]) (null)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorContainsNullHeaderValue() {
        new StaticHeadersWriter("name", "value1", null);
    }

    @Test
    public void sameHeaderShouldBeReturned() {
        String headerName = "X-header";
        String headerValue = "foo";
        StaticHeadersWriter factory = new StaticHeadersWriter(headerName, headerValue);
        factory.writeHeaders(request, response);
        assertThat(response.getHeaderValues(headerName)).isEqualTo(Arrays.asList(headerValue));
    }

    @Test
    public void writeHeadersMulti() {
        Header pragma = new Header("Pragma", "no-cache");
        Header cacheControl = new Header("Cache-Control", "no-cache", "no-store", "must-revalidate");
        StaticHeadersWriter factory = new StaticHeadersWriter(Arrays.asList(pragma, cacheControl));
        factory.writeHeaders(request, response);
        assertThat(response.getHeaderNames()).hasSize(2);
        assertThat(response.getHeaderValues(pragma.getName())).isEqualTo(pragma.getValues());
        assertThat(response.getHeaderValues(cacheControl.getName())).isEqualTo(cacheControl.getValues());
    }

    @Test
    public void writeHeaderWhenNotPresent() {
        String pragmaValue = new String("pragmaValue");
        String cacheControlValue = new String("cacheControlValue");
        this.response.setHeader("Pragma", pragmaValue);
        this.response.setHeader("Cache-Control", cacheControlValue);
        Header pragma = new Header("Pragma", "no-cache");
        Header cacheControl = new Header("Cache-Control", "no-cache", "no-store", "must-revalidate");
        StaticHeadersWriter factory = new StaticHeadersWriter(Arrays.asList(pragma, cacheControl));
        factory.writeHeaders(this.request, this.response);
        assertThat(this.response.getHeaderNames()).hasSize(2);
        assertThat(this.response.getHeader("Pragma")).isSameAs(pragmaValue);
        assertThat(this.response.getHeader("Cache-Control")).isSameAs(cacheControlValue);
    }
}

