/**
 * Copyright 2018 The Data Transfer Project Authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.datatransferproject.cloud.google;


import Environment.QA;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.function.Executable;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class GoogleCloudModuleTest {
    @Test
    public void getProjectEnvironment() {
        assertThat(GoogleCloudExtensionModule.getProjectEnvironment("acme-qa")).isEqualTo(QA);
    }

    @Test
    public void getProjectEnvironment_missingEnvironment() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> GoogleCloudExtensionModule.getProjectEnvironment("acme"));
    }

    @Test
    public void getProjectEnvironment_invalidEnvironment() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> GoogleCloudExtensionModule.getProjectEnvironment("acme-notARealEnvironment"));
    }
}

