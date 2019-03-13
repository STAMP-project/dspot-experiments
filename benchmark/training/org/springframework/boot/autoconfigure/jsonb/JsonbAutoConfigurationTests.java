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
package org.springframework.boot.autoconfigure.jsonb;


import javax.json.bind.Jsonb;
import org.junit.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;


/**
 * Tests for {@link JsonbAutoConfiguration}.
 *
 * @author Edd? Mel?ndez
 */
public class JsonbAutoConfigurationTests {
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner().withConfiguration(AutoConfigurations.of(JsonbAutoConfiguration.class));

    @Test
    public void jsonbRegistration() {
        this.contextRunner.run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            Jsonb jsonb = context.getBean(.class);
            assertThat(jsonb.toJson(new org.springframework.boot.autoconfigure.jsonb.DataObject())).isEqualTo("{\"data\":\"hello\"}");
        });
    }

    public class DataObject {
        private String data = "hello";

        public String getData() {
            return this.data;
        }

        public void setData(String data) {
            this.data = data;
        }
    }
}

