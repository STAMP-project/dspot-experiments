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
package org.springframework.boot.jackson;


import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;


/**
 * Tests for {@link JsonComponentModule}.
 *
 * @author Phillip Webb
 * @author Vladimir Tsanev
 */
public class JsonComponentModuleTests {
    private AnnotationConfigApplicationContext context;

    @Test
    public void moduleShouldRegisterSerializers() throws Exception {
        load(JsonComponentModuleTests.OnlySerializer.class);
        JsonComponentModule module = this.context.getBean(JsonComponentModule.class);
        assertSerialize(module);
    }

    @Test
    public void moduleShouldRegisterDeserializers() throws Exception {
        load(JsonComponentModuleTests.OnlyDeserializer.class);
        JsonComponentModule module = this.context.getBean(JsonComponentModule.class);
        assertDeserialize(module);
    }

    @Test
    public void moduleShouldRegisterInnerClasses() throws Exception {
        load(NameAndAgeJsonComponent.class);
        JsonComponentModule module = this.context.getBean(JsonComponentModule.class);
        assertSerialize(module);
        assertDeserialize(module);
    }

    @Test
    public void moduleShouldAllowInnerAbstractClasses() throws Exception {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(JsonComponentModule.class, JsonComponentModuleTests.ComponentWithInnerAbstractClass.class);
        JsonComponentModule module = context.getBean(JsonComponentModule.class);
        assertSerialize(module);
        context.close();
    }

    @JsonComponent
    static class OnlySerializer extends NameAndAgeJsonComponent.Serializer {}

    @JsonComponent
    static class OnlyDeserializer extends NameAndAgeJsonComponent.Deserializer {}

    @JsonComponent
    static class ComponentWithInnerAbstractClass {
        private abstract static class AbstractSerializer extends NameAndAgeJsonComponent.Serializer {}

        static class ConcreteSerializer extends JsonComponentModuleTests.ComponentWithInnerAbstractClass.AbstractSerializer {}
    }
}

