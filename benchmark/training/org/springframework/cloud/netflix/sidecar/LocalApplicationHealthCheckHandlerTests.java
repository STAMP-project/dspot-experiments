/**
 * Copyright 2013-2019 the original author or authors.
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
package org.springframework.cloud.netflix.sidecar;


import InstanceStatus.DOWN;
import InstanceStatus.OUT_OF_SERVICE;
import InstanceStatus.UNKNOWN;
import InstanceStatus.UP;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;


/**
 *
 *
 * @author Spencer Gibb
 */
public class LocalApplicationHealthCheckHandlerTests {
    @Mock
    private HealthIndicator healthIndicator;

    @Test
    public void upMappingWorks() {
        assertStatus(UP, Health.up());
    }

    @Test
    public void downMappingWorks() {
        assertStatus(DOWN, Health.down());
    }

    @Test
    public void outOfServiceMappingWorks() {
        assertStatus(OUT_OF_SERVICE, Health.outOfService());
    }

    @Test
    public void unknownMappingWorks() {
        assertStatus(UNKNOWN, Health.unknown());
    }
}

