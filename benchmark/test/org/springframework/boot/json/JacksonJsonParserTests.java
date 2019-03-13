/**
 * Copyright 2012-2018 the original author or authors.
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
package org.springframework.boot.json;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Tests for {@link JacksonJsonParser}.
 *
 * @author Dave Syer
 * @author Stephane Nicoll
 */
public class JacksonJsonParserTests extends AbstractJsonParserTests {
    @Test
    public void instanceWithSpecificObjectMapper() throws IOException {
        ObjectMapper objectMapper = Mockito.spy(new ObjectMapper());
        new JacksonJsonParser(objectMapper).parseMap("{}");
        Mockito.verify(objectMapper).readValue(ArgumentMatchers.eq("{}"), ArgumentMatchers.any(TypeReference.class));
    }
}

