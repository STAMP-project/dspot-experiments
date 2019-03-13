/**
 * Copyright 2015-2019 the original author or authors.
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
package org.springframework.cloud.config.monitor;


import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.junit.Test;
import org.springframework.cloud.config.server.environment.AbstractScmEnvironmentRepository;
import org.springframework.cloud.config.server.environment.NativeEnvironmentRepository;
import org.springframework.test.util.ReflectionTestUtils;


/**
 *
 *
 * @author Gilles Robert
 */
public class FileMonitorConfigurationTest {
    private static final String SAMPLE_PATH = "resources/pathsamples";

    private FileMonitorConfiguration fileMonitorConfiguration = new FileMonitorConfiguration();

    private List<AbstractScmEnvironmentRepository> repositories = new ArrayList<>();

    @Test
    public void testStart_whenRepositoriesAreNull() {
        // given
        // when
        fileMonitorConfiguration.start();
        // then
        Set<Path> directory = getDirectory();
        assertThat(directory).isNull();
    }

    @Test
    public void testStart_withNativeEnvironmentRepository() {
        // given
        NativeEnvironmentRepository repository = createNativeEnvironmentRepository();
        ReflectionTestUtils.setField(fileMonitorConfiguration, "nativeEnvironmentRepository", repository);
        // when
        fileMonitorConfiguration.start();
        // then
        assertOnDirectory(1);
    }

    @Test
    public void testStart_withOneScmRepository() {
        // given
        AbstractScmEnvironmentRepository repository = createScmEnvironmentRepository(FileMonitorConfigurationTest.SAMPLE_PATH);
        addScmRepository(repository);
        // when
        fileMonitorConfiguration.start();
        // then
        assertOnDirectory(1);
    }

    @Test
    public void testStart_withTwoScmRepositories() {
        // given
        AbstractScmEnvironmentRepository repository = createScmEnvironmentRepository(FileMonitorConfigurationTest.SAMPLE_PATH);
        AbstractScmEnvironmentRepository secondRepository = createScmEnvironmentRepository("anotherPath");
        addScmRepository(repository);
        addScmRepository(secondRepository);
        // when
        fileMonitorConfiguration.start();
        // then
        assertOnDirectory(2);
    }
}

