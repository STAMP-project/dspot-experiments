/**
 * Copyright 2014 the original author or authors.
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
package org.springframework.batch.item.support;


import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;


/**
 * <p>
 * Test cases around {@link org.springframework.batch.item.support.ScriptItemProcessor}.
 * </p>
 *
 * @author Chris Schaefer
 * @since 3.1
 */
public class ScriptItemProcessorTests {
    private static List<String> availableLanguages = new ArrayList<>();

    @Test
    public void testJavascriptScriptSourceSimple() throws Exception {
        Assume.assumeTrue(languageExists("javascript"));
        ScriptItemProcessor<String, Object> scriptItemProcessor = new ScriptItemProcessor();
        scriptItemProcessor.setScriptSource("item.toUpperCase();", "javascript");
        scriptItemProcessor.afterPropertiesSet();
        Assert.assertEquals("Incorrect transformed value", "SS", scriptItemProcessor.process("ss"));
    }

    @Test
    public void testJavascriptScriptSourceFunction() throws Exception {
        Assume.assumeTrue(languageExists("javascript"));
        ScriptItemProcessor<String, Object> scriptItemProcessor = new ScriptItemProcessor();
        scriptItemProcessor.setScriptSource("function process(item) { return item.toUpperCase(); } process(item);", "javascript");
        scriptItemProcessor.afterPropertiesSet();
        Assert.assertEquals("Incorrect transformed value", "SS", scriptItemProcessor.process("ss"));
    }

    @Test
    public void testJRubyScriptSourceSimple() throws Exception {
        Assume.assumeTrue(languageExists("jruby"));
        ScriptItemProcessor<String, Object> scriptItemProcessor = new ScriptItemProcessor();
        scriptItemProcessor.setScriptSource("$item.upcase", "jruby");
        scriptItemProcessor.afterPropertiesSet();
        Assert.assertEquals("Incorrect transformed value", "SS", scriptItemProcessor.process("ss"));
    }

    @Test
    public void testJRubyScriptSourceMethod() throws Exception {
        Assume.assumeTrue(languageExists("jruby"));
        ScriptItemProcessor<String, Object> scriptItemProcessor = new ScriptItemProcessor();
        scriptItemProcessor.setScriptSource("def process(item) $item.upcase end \n process($item)", "jruby");
        scriptItemProcessor.afterPropertiesSet();
        Assert.assertEquals("Incorrect transformed value", "SS", scriptItemProcessor.process("ss"));
    }

    @Test
    public void testBeanShellScriptSourceSimple() throws Exception {
        Assume.assumeTrue(languageExists("bsh"));
        ScriptItemProcessor<String, Object> scriptItemProcessor = new ScriptItemProcessor();
        scriptItemProcessor.setScriptSource("item.toUpperCase();", "bsh");
        scriptItemProcessor.afterPropertiesSet();
        Assert.assertEquals("Incorrect transformed value", "SS", scriptItemProcessor.process("ss"));
    }

    @Test
    public void testBeanShellScriptSourceFunction() throws Exception {
        Assume.assumeTrue(languageExists("bsh"));
        ScriptItemProcessor<String, Object> scriptItemProcessor = new ScriptItemProcessor();
        scriptItemProcessor.setScriptSource("String process(String item) { return item.toUpperCase(); } process(item);", "bsh");
        scriptItemProcessor.afterPropertiesSet();
        Assert.assertEquals("Incorrect transformed value", "SS", scriptItemProcessor.process("ss"));
    }

    @Test
    public void testGroovyScriptSourceSimple() throws Exception {
        Assume.assumeTrue(languageExists("groovy"));
        ScriptItemProcessor<String, Object> scriptItemProcessor = new ScriptItemProcessor();
        scriptItemProcessor.setScriptSource("item.toUpperCase();", "groovy");
        scriptItemProcessor.afterPropertiesSet();
        Assert.assertEquals("Incorrect transformed value", "SS", scriptItemProcessor.process("ss"));
    }

    @Test
    public void testGroovyScriptSourceMethod() throws Exception {
        Assume.assumeTrue(languageExists("groovy"));
        ScriptItemProcessor<String, Object> scriptItemProcessor = new ScriptItemProcessor();
        scriptItemProcessor.setScriptSource("def process(item) { return item.toUpperCase() } \n process(item)", "groovy");
        scriptItemProcessor.afterPropertiesSet();
        Assert.assertEquals("Incorrect transformed value", "SS", scriptItemProcessor.process("ss"));
    }

    @Test
    public void testJavascriptScriptSimple() throws Exception {
        Assume.assumeTrue(languageExists("javascript"));
        Resource resource = new ClassPathResource("org/springframework/batch/item/support/processor-test-simple.js");
        ScriptItemProcessor<String, Object> scriptItemProcessor = new ScriptItemProcessor();
        scriptItemProcessor.setScript(resource);
        scriptItemProcessor.afterPropertiesSet();
        Assert.assertEquals("Incorrect transformed value", "SS", scriptItemProcessor.process("ss"));
    }

    @Test
    public void testItemBinding() throws Exception {
        ScriptItemProcessor<String, Object> scriptItemProcessor = new ScriptItemProcessor();
        scriptItemProcessor.setScriptSource("foo.contains('World');", "javascript");
        scriptItemProcessor.setItemBindingVariableName("foo");
        scriptItemProcessor.afterPropertiesSet();
        Assert.assertEquals("Incorrect transformed value", true, scriptItemProcessor.process("Hello World"));
    }

    @Test(expected = IllegalStateException.class)
    public void testNoScriptSet() throws Exception {
        ScriptItemProcessor<String, Object> scriptItemProcessor = new ScriptItemProcessor();
        scriptItemProcessor.afterPropertiesSet();
    }

    @Test(expected = IllegalStateException.class)
    public void testScriptSourceAndScriptResourceSet() throws Exception {
        ScriptItemProcessor<String, Object> scriptItemProcessor = new ScriptItemProcessor();
        scriptItemProcessor.setScriptSource("blah", "blah");
        scriptItemProcessor.setScript(new ClassPathResource("blah"));
        scriptItemProcessor.afterPropertiesSet();
    }

    @Test(expected = IllegalStateException.class)
    public void testNoScriptSetWithoutInitBean() throws Exception {
        ScriptItemProcessor<String, Object> scriptItemProcessor = new ScriptItemProcessor();
        scriptItemProcessor.process("blah");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testScriptSourceWithNoLanguage() throws Exception {
        ScriptItemProcessor<String, Object> scriptItemProcessor = new ScriptItemProcessor();
        scriptItemProcessor.setScriptSource("function process(item) { return item.toUpperCase(); } process(item);", null);
        scriptItemProcessor.afterPropertiesSet();
    }

    @Test
    public void testItemBindingNameChange() throws Exception {
        Assume.assumeTrue(languageExists("javascript"));
        ScriptItemProcessor<String, Object> scriptItemProcessor = new ScriptItemProcessor();
        scriptItemProcessor.setItemBindingVariableName("someOtherVarName");
        scriptItemProcessor.setScriptSource("function process(param) { return param.toUpperCase(); } process(someOtherVarName);", "javascript");
        scriptItemProcessor.afterPropertiesSet();
        Assert.assertEquals("Incorrect transformed value", "SS", scriptItemProcessor.process("ss"));
    }
}

