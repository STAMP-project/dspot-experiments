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
package sample.integration.consumer;


import java.io.File;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import sample.integration.SampleIntegrationApplication;
import sample.integration.ServiceProperties;
import sample.integration.producer.ProducerApplication;


/**
 * Basic integration tests for service demo application.
 *
 * @author Dave Syer
 * @author Andy Wilkinson
 */
public class SampleIntegrationApplicationTests {
    @Rule
    public final TemporaryFolder temp = new TemporaryFolder();

    private ConfigurableApplicationContext context;

    @Test
    public void testVanillaExchange() throws Exception {
        File inputDir = new File(this.temp.getRoot(), "input");
        File outputDir = new File(this.temp.getRoot(), "output");
        this.context = SpringApplication.run(SampleIntegrationApplication.class, ("--service.input-dir=" + inputDir), ("--service.output-dir=" + outputDir));
        SpringApplication.run(ProducerApplication.class, "World", ("--service.input-dir=" + inputDir), ("--service.output-dir=" + outputDir));
        String output = getOutput(outputDir);
        assertThat(output).contains("Hello World");
    }

    @Test
    public void testMessageGateway() throws Exception {
        File inputDir = new File(this.temp.getRoot(), "input");
        File outputDir = new File(this.temp.getRoot(), "output");
        this.context = SpringApplication.run(SampleIntegrationApplication.class, "testviamg", ("--service.input-dir=" + inputDir), ("--service.output-dir=" + outputDir));
        String output = getOutput(this.context.getBean(ServiceProperties.class).getOutputDir());
        assertThat(output).contains("testviamg");
    }
}

