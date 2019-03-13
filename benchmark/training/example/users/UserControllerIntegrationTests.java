/**
 * Copyright 2016-2018 the original author or authors.
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
package example.users;


import MediaType.APPLICATION_JSON;
import MediaType.APPLICATION_XML;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcPrint;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;


/**
 * Integration tests for {@link UserController}.
 *
 * @author Oliver Gierke
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc(print = MockMvcPrint.NONE)
public class UserControllerIntegrationTests {
    @Autowired
    MockMvc mvc;

    @Test
    public void handlesJsonPayloadWithExactProperties() throws Exception {
        postAndExpect("{ \"firstname\" : \"Dave\", \"lastname\" : \"Matthews\" }", APPLICATION_JSON);
    }

    @Test
    public void handlesJsonPayloadWithNestedProperties() throws Exception {
        postAndExpect("{ \"user\" : { \"firstname\" : \"Dave\", \"lastname\" : \"Matthews\" } }", APPLICATION_JSON);
    }

    @Test
    public void handlesXmlPayLoadWithExactProperties() throws Exception {
        postAndExpect("<user><firstname>Dave</firstname><lastname>Matthews</lastname></user>", APPLICATION_XML);
    }
}

