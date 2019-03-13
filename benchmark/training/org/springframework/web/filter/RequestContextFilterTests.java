/**
 * Copyright 2002-2018 the original author or authors.
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
package org.springframework.web.filter;


import javax.servlet.ServletException;
import org.junit.Test;


/**
 *
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 */
public class RequestContextFilterTests {
    @Test
    public void happyPath() throws Exception {
        testFilterInvocation(null);
    }

    @Test
    public void withException() throws Exception {
        testFilterInvocation(new ServletException());
    }
}

