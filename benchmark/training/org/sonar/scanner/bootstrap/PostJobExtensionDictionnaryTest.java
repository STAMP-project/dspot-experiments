/**
 * SonarQube
 * Copyright (C) 2009-2019 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.scanner.bootstrap;


import Phase.Name;
import org.junit.Test;
import org.mockito.Mockito;
import org.sonar.api.batch.Phase;
import org.sonar.api.batch.postjob.PostJob;
import org.sonar.api.batch.postjob.PostJobContext;
import org.sonar.api.batch.postjob.PostJobDescriptor;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.scanner.postjob.PostJobOptimizer;


public class PostJobExtensionDictionnaryTest {
    private PostJobOptimizer postJobOptimizer = Mockito.mock(PostJobOptimizer.class);

    @Test
    public void dependsUponPhaseForPostJob() {
        PostJobExtensionDictionnaryTest.PrePostJob pre = new PostJobExtensionDictionnaryTest.PrePostJob();
        PostJobExtensionDictionnaryTest.NormalPostJob normal = new PostJobExtensionDictionnaryTest.NormalPostJob();
        PostJobExtensionDictionnary selector = newSelector(normal, pre);
        assertThat(selector.selectPostJobs()).extracting("wrappedPostJob").containsExactly(pre, normal);
    }

    interface Marker {}

    @Phase(name = Name.POST)
    class PostSensor implements Sensor {
        @Override
        public void describe(SensorDescriptor descriptor) {
        }

        @Override
        public void execute(SensorContext context) {
        }
    }

    class PostSensorSubclass extends PostJobExtensionDictionnaryTest.PostSensor {}

    class NormalPostJob implements PostJob {
        @Override
        public void describe(PostJobDescriptor descriptor) {
        }

        @Override
        public void execute(PostJobContext context) {
        }
    }

    @Phase(name = Name.PRE)
    class PrePostJob implements PostJob {
        @Override
        public void describe(PostJobDescriptor descriptor) {
        }

        @Override
        public void execute(PostJobContext context) {
        }
    }
}

