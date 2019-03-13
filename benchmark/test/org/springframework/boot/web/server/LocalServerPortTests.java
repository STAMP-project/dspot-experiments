/**
 * Copyright 2012-2017 the original author or authors.
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
package org.springframework.boot.web.server;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;


/**
 * Tests for {@link LocalServerPort}.
 *
 * @author Anand Shah
 * @author Phillip Webb
 */
@RunWith(SpringRunner.class)
@TestPropertySource(properties = "local.server.port=8181")
public class LocalServerPortTests {
    @Value("${local.server.port}")
    private String fromValue;

    @LocalServerPort
    private String fromAnnotation;

    @Test
    public void testLocalServerPortAnnotation() {
        assertThat(this.fromAnnotation).isNotNull().isEqualTo(this.fromValue);
    }

    @Configuration
    static class Config {}
}

