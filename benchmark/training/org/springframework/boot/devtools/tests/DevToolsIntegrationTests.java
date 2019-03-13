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
package org.springframework.boot.devtools.tests;


import HttpStatus.NOT_FOUND;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.FixedValue;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testsupport.BuildOutput;

import static net.bytebuddy.description.annotation.AnnotationDescription.Builder.ofType;


/**
 * Integration tests for DevTools.
 *
 * @author Andy Wilkinson
 */
@RunWith(Parameterized.class)
public class DevToolsIntegrationTests {
    @ClassRule
    public static final TemporaryFolder temp = new TemporaryFolder();

    private static final BuildOutput buildOutput = new BuildOutput(DevToolsIntegrationTests.class);

    private LaunchedApplication launchedApplication;

    private final File serverPortFile;

    private final ApplicationLauncher applicationLauncher;

    @Rule
    public JvmLauncher javaLauncher = new JvmLauncher();

    public DevToolsIntegrationTests(ApplicationLauncher applicationLauncher) {
        this.applicationLauncher = applicationLauncher;
        this.serverPortFile = new File(DevToolsIntegrationTests.buildOutput.getRootLocation(), "server.port");
    }

    @Test
    public void addARequestMappingToAnExistingController() throws Exception {
        TestRestTemplate template = new TestRestTemplate();
        String urlBase = "http://localhost:" + (awaitServerPort());
        assertThat(template.getForObject((urlBase + "/one"), String.class)).isEqualTo("one");
        assertThat(template.getForEntity((urlBase + "/two"), String.class).getStatusCode()).isEqualTo(NOT_FOUND);
        controller("com.example.ControllerOne").withRequestMapping("one").withRequestMapping("two").build();
        urlBase = "http://localhost:" + (awaitServerPort());
        assertThat(template.getForObject((urlBase + "/one"), String.class)).isEqualTo("one");
        assertThat(template.getForObject((urlBase + "/two"), String.class)).isEqualTo("two");
    }

    @Test
    public void removeARequestMappingFromAnExistingController() throws Exception {
        TestRestTemplate template = new TestRestTemplate();
        String urlBase = "http://localhost:" + (awaitServerPort());
        assertThat(template.getForObject((urlBase + "/one"), String.class)).isEqualTo("one");
        controller("com.example.ControllerOne").build();
        urlBase = "http://localhost:" + (awaitServerPort());
        assertThat(template.getForEntity((urlBase + "/one"), String.class).getStatusCode()).isEqualTo(NOT_FOUND);
    }

    @Test
    public void createAController() throws Exception {
        TestRestTemplate template = new TestRestTemplate();
        String urlBase = "http://localhost:" + (awaitServerPort());
        assertThat(template.getForObject((urlBase + "/one"), String.class)).isEqualTo("one");
        assertThat(template.getForEntity((urlBase + "/two"), String.class).getStatusCode()).isEqualTo(NOT_FOUND);
        controller("com.example.ControllerTwo").withRequestMapping("two").build();
        urlBase = "http://localhost:" + (awaitServerPort());
        assertThat(template.getForObject((urlBase + "/one"), String.class)).isEqualTo("one");
        assertThat(template.getForObject((urlBase + "/two"), String.class)).isEqualTo("two");
    }

    @Test
    public void createAControllerAndThenAddARequestMapping() throws Exception {
        TestRestTemplate template = new TestRestTemplate();
        String urlBase = "http://localhost:" + (awaitServerPort());
        assertThat(template.getForObject((urlBase + "/one"), String.class)).isEqualTo("one");
        assertThat(template.getForEntity((urlBase + "/two"), String.class).getStatusCode()).isEqualTo(NOT_FOUND);
        controller("com.example.ControllerTwo").withRequestMapping("two").build();
        urlBase = "http://localhost:" + (awaitServerPort());
        assertThat(template.getForObject((urlBase + "/one"), String.class)).isEqualTo("one");
        assertThat(template.getForObject((urlBase + "/two"), String.class)).isEqualTo("two");
        controller("com.example.ControllerTwo").withRequestMapping("two").withRequestMapping("three").build();
        urlBase = "http://localhost:" + (awaitServerPort());
        assertThat(template.getForObject((urlBase + "/three"), String.class)).isEqualTo("three");
    }

    @Test
    public void createAControllerAndThenAddARequestMappingToAnExistingController() throws Exception {
        TestRestTemplate template = new TestRestTemplate();
        String urlBase = "http://localhost:" + (awaitServerPort());
        assertThat(template.getForObject((urlBase + "/one"), String.class)).isEqualTo("one");
        assertThat(template.getForEntity((urlBase + "/two"), String.class).getStatusCode()).isEqualTo(NOT_FOUND);
        controller("com.example.ControllerTwo").withRequestMapping("two").build();
        urlBase = "http://localhost:" + (awaitServerPort());
        assertThat(template.getForObject((urlBase + "/one"), String.class)).isEqualTo("one");
        assertThat(template.getForObject((urlBase + "/two"), String.class)).isEqualTo("two");
        controller("com.example.ControllerOne").withRequestMapping("one").withRequestMapping("three").build();
        urlBase = "http://localhost:" + (awaitServerPort());
        assertThat(template.getForObject((urlBase + "/one"), String.class)).isEqualTo("one");
        assertThat(template.getForObject((urlBase + "/two"), String.class)).isEqualTo("two");
        assertThat(template.getForObject((urlBase + "/three"), String.class)).isEqualTo("three");
    }

    @Test
    public void deleteAController() throws Exception {
        TestRestTemplate template = new TestRestTemplate();
        String urlBase = "http://localhost:" + (awaitServerPort());
        assertThat(template.getForObject((urlBase + "/one"), String.class)).isEqualTo("one");
        assertThat(new File(this.launchedApplication.getClassesDirectory(), "com/example/ControllerOne.class").delete()).isTrue();
        urlBase = "http://localhost:" + (awaitServerPort());
        assertThat(template.getForEntity((urlBase + "/one"), String.class).getStatusCode()).isEqualTo(NOT_FOUND);
    }

    @Test
    public void createAControllerAndThenDeleteIt() throws Exception {
        TestRestTemplate template = new TestRestTemplate();
        String urlBase = "http://localhost:" + (awaitServerPort());
        assertThat(template.getForObject((urlBase + "/one"), String.class)).isEqualTo("one");
        assertThat(template.getForEntity((urlBase + "/two"), String.class).getStatusCode()).isEqualTo(NOT_FOUND);
        controller("com.example.ControllerTwo").withRequestMapping("two").build();
        urlBase = "http://localhost:" + (awaitServerPort());
        assertThat(template.getForObject((urlBase + "/one"), String.class)).isEqualTo("one");
        assertThat(template.getForObject((urlBase + "/two"), String.class)).isEqualTo("two");
        assertThat(new File(this.launchedApplication.getClassesDirectory(), "com/example/ControllerTwo.class").delete()).isTrue();
        urlBase = "http://localhost:" + (awaitServerPort());
        assertThat(template.getForEntity((urlBase + "/two"), String.class).getStatusCode()).isEqualTo(NOT_FOUND);
    }

    private static final class ControllerBuilder {
        private final List<String> mappings = new ArrayList<>();

        private final String name;

        private final File classesDirectory;

        private ControllerBuilder(String name, File classesDirectory) {
            this.name = name;
            this.classesDirectory = classesDirectory;
        }

        public DevToolsIntegrationTests.ControllerBuilder withRequestMapping(String mapping) {
            this.mappings.add(mapping);
            return this;
        }

        public void build() throws Exception {
            DynamicType.Builder<Object> builder = new ByteBuddy().subclass(Object.class).name(this.name).annotateType(ofType(org.springframework.web.bind.annotation.RestController.class).build());
            for (String mapping : this.mappings) {
                builder = builder.defineMethod(mapping, String.class, Visibility.PUBLIC).intercept(FixedValue.value(mapping)).annotateMethod(ofType(org.springframework.web.bind.annotation.RequestMapping.class).defineArray("value", mapping).build());
            }
            builder.make().saveIn(this.classesDirectory);
        }
    }
}

