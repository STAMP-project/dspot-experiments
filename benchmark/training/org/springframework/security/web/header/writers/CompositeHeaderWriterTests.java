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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.security.web.header.HeaderWriter;


/**
 * Tests for class {@link CompositeHeaderWriter}/.
 *
 * @author Ankur Pathak
 * @since 5.2
 */
public class CompositeHeaderWriterTests {
    @Test
    public void writeHeadersWhenConfiguredWithDelegatesThenInvokesEach() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        HeaderWriter one = Mockito.mock(HeaderWriter.class);
        HeaderWriter two = Mockito.mock(HeaderWriter.class);
        CompositeHeaderWriter headerWriter = new CompositeHeaderWriter(Arrays.asList(one, two));
        headerWriter.writeHeaders(request, response);
        Mockito.verify(one).writeHeaders(request, response);
        Mockito.verify(two).writeHeaders(request, response);
    }

    @Test
    public void constructorWhenPassingEmptyListThenThrowsException() {
        assertThatCode(() -> new CompositeHeaderWriter(Collections.emptyList())).isInstanceOf(IllegalArgumentException.class);
    }
}

