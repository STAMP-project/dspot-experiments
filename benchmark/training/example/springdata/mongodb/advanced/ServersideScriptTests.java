/**
 * Copyright 2015-2018 the original author or authors.
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
package example.springdata.mongodb.advanced;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.script.ExecutableMongoScript;
import org.springframework.test.context.junit4.SpringRunner;


/**
 *
 *
 * @author Christoph Strobl
 * @author Oliver Gierke
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ServersideScriptTests {
    @Autowired
    AdvancedRepository repository;

    @Autowired
    MongoOperations operations;

    /**
     * Store and call an arbitrary JavaScript function (in this case a simple echo script) via its name.
     */
    @Test
    public void saveAndCallScriptViaName() {
        operations.scriptOps().register(new org.springframework.data.mongodb.core.script.NamedMongoScript("echoScript", new ExecutableMongoScript("function(x) { return x; }")));
        assertThat(operations.scriptOps().call("echoScript", "Hello echo...!")).isEqualTo("Hello echo...!");
    }
}

