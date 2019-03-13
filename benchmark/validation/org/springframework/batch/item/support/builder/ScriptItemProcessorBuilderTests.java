/**
 * Copyright 2017 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.springframework.batch.item.support.builder;


import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.batch.item.support.ScriptItemProcessor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;


/**
 *
 *
 * @author Glenn Renfro
 */
public class ScriptItemProcessorBuilderTests {
    private static List<String> availableLanguages = new ArrayList<>();

    @Test
    public void testScriptSource() throws Exception {
        ScriptItemProcessor<String, Object> scriptItemProcessor = new ScriptItemProcessorBuilder<String, Object>().scriptSource("item.toUpperCase();").language("javascript").build();
        scriptItemProcessor.afterPropertiesSet();
        Assert.assertEquals("Incorrect transformed value", "AA", scriptItemProcessor.process("aa"));
    }

    @Test
    public void testItemBinding() throws Exception {
        ScriptItemProcessor<String, Object> scriptItemProcessor = new ScriptItemProcessorBuilder<String, Object>().scriptSource("foo.contains('World');").language("javascript").itemBindingVariableName("foo").build();
        scriptItemProcessor.afterPropertiesSet();
        Assert.assertEquals("Incorrect transformed value", true, scriptItemProcessor.process("Hello World"));
    }

    @Test
    public void testScriptResource() throws Exception {
        Resource resource = new ClassPathResource("org/springframework/batch/item/support/processor-test-simple.js");
        ScriptItemProcessor<String, Object> scriptItemProcessor = new ScriptItemProcessorBuilder<String, Object>().scriptResource(resource).build();
        scriptItemProcessor.afterPropertiesSet();
        Assert.assertEquals("Incorrect transformed value", "BB", scriptItemProcessor.process("bb"));
    }

    @Test
    public void testNoScriptSourceNorResource() throws Exception {
        validateExceptionMessage(new ScriptItemProcessorBuilder(), "scriptResource or scriptSource is required.");
    }

    @Test
    public void testNoScriptSourceLanguage() throws Exception {
        validateExceptionMessage(new ScriptItemProcessorBuilder<String, Object>().scriptSource("foo.contains('World');"), "language is required when using scriptSource.");
    }
}

