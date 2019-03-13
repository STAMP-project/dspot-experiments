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
package org.springframework.boot.autoconfigure.freemarker;


import freemarker.template.Configuration;
import java.io.File;
import java.io.StringWriter;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.testsupport.BuildOutput;
import org.springframework.boot.testsupport.rule.OutputCapture;


/**
 * Tests for {@link FreeMarkerAutoConfiguration}.
 *
 * @author Andy Wilkinson
 * @author Kazuki Shimizu
 */
public class FreeMarkerAutoConfigurationTests {
    private final BuildOutput buildOutput = new BuildOutput(getClass());

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner().withConfiguration(AutoConfigurations.of(FreeMarkerAutoConfiguration.class));

    @Rule
    public final OutputCapture output = new OutputCapture();

    @Test
    public void renderNonWebAppTemplate() {
        this.contextRunner.run(( context) -> {
            Configuration freemarker = context.getBean(.class);
            StringWriter writer = new StringWriter();
            freemarker.getTemplate("message.ftl").process(this, writer);
            assertThat(writer.toString()).contains("Hello World");
        });
    }

    @Test
    public void nonExistentTemplateLocation() {
        this.contextRunner.withPropertyValues(("spring.freemarker.templateLoaderPath:" + "classpath:/does-not-exist/,classpath:/also-does-not-exist")).run(( context) -> assertThat(this.output.toString()).contains("Cannot find template location"));
    }

    @Test
    public void emptyTemplateLocation() {
        File emptyDirectory = new File(this.buildOutput.getTestResourcesLocation(), "empty-templates/empty-directory");
        emptyDirectory.mkdirs();
        this.contextRunner.withPropertyValues(("spring.freemarker.templateLoaderPath:" + "classpath:/empty-templates/empty-directory/")).run(( context) -> assertThat(this.output.toString()).doesNotContain("Cannot find template location"));
    }

    @Test
    public void nonExistentLocationAndEmptyLocation() {
        new File(this.buildOutput.getTestResourcesLocation(), "empty-templates/empty-directory").mkdirs();
        this.contextRunner.withPropertyValues(("spring.freemarker.templateLoaderPath:" + "classpath:/does-not-exist/,classpath:/empty-templates/empty-directory/")).run(( context) -> assertThat(this.output.toString()).doesNotContain("Cannot find template location"));
    }
}

