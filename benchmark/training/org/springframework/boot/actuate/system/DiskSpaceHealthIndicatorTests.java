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
package org.springframework.boot.actuate.system;


import Status.DOWN;
import Status.UP;
import java.io.File;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.util.unit.DataSize;


/**
 * Tests for {@link DiskSpaceHealthIndicator}.
 *
 * @author Mattias Severson
 * @author Stephane Nicoll
 */
public class DiskSpaceHealthIndicatorTests {
    private static final DataSize THRESHOLD = DataSize.ofKilobytes(1);

    private static final DataSize TOTAL_SPACE = DataSize.ofKilobytes(10);

    @Mock
    private File fileMock;

    private HealthIndicator healthIndicator;

    @Test
    public void diskSpaceIsUp() {
        long freeSpace = (DiskSpaceHealthIndicatorTests.THRESHOLD.toBytes()) + 10;
        BDDMockito.given(this.fileMock.getUsableSpace()).willReturn(freeSpace);
        BDDMockito.given(this.fileMock.getTotalSpace()).willReturn(DiskSpaceHealthIndicatorTests.TOTAL_SPACE.toBytes());
        Health health = this.healthIndicator.health();
        assertThat(health.getStatus()).isEqualTo(UP);
        assertThat(health.getDetails().get("threshold")).isEqualTo(DiskSpaceHealthIndicatorTests.THRESHOLD.toBytes());
        assertThat(health.getDetails().get("free")).isEqualTo(freeSpace);
        assertThat(health.getDetails().get("total")).isEqualTo(DiskSpaceHealthIndicatorTests.TOTAL_SPACE.toBytes());
    }

    @Test
    public void diskSpaceIsDown() {
        long freeSpace = (DiskSpaceHealthIndicatorTests.THRESHOLD.toBytes()) - 10;
        BDDMockito.given(this.fileMock.getUsableSpace()).willReturn(freeSpace);
        BDDMockito.given(this.fileMock.getTotalSpace()).willReturn(DiskSpaceHealthIndicatorTests.TOTAL_SPACE.toBytes());
        Health health = this.healthIndicator.health();
        assertThat(health.getStatus()).isEqualTo(DOWN);
        assertThat(health.getDetails().get("threshold")).isEqualTo(DiskSpaceHealthIndicatorTests.THRESHOLD.toBytes());
        assertThat(health.getDetails().get("free")).isEqualTo(freeSpace);
        assertThat(health.getDetails().get("total")).isEqualTo(DiskSpaceHealthIndicatorTests.TOTAL_SPACE.toBytes());
    }
}

