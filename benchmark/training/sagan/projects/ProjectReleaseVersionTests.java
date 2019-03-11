/**
 * Copyright 2014-2015 the original author or authors.
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
package sagan.projects;


import org.hamcrest.MatcherAssert;
import org.junit.Test;


/**
 *
 *
 * @author Dave Syer
 */
public class ProjectReleaseVersionTests {
    @Test
    public void snapshotDetected() {
        ProjectRelease version = new ProjectReleaseBuilder().versionName("1.0.0.BUILD-SNAPSHOT").build();
        MatcherAssert.assertThat(version.isSnapshot(), equalTo(true));
        MatcherAssert.assertThat(version.getRepository().getUrl(), containsString("snapshot"));
    }

    @Test
    public void snapshotDetectedCiStyle() {
        ProjectRelease version = new ProjectReleaseBuilder().versionName("1.0.0.CI-SNAPSHOT").build();
        MatcherAssert.assertThat(version.isSnapshot(), equalTo(true));
        MatcherAssert.assertThat(version.getRepository().getUrl(), containsString("snapshot"));
    }

    @Test
    public void snapshotDetectedMavenStyle() {
        ProjectRelease version = new ProjectReleaseBuilder().versionName("1.0.0-SNAPSHOT").build();
        MatcherAssert.assertThat(version.isSnapshot(), equalTo(true));
        MatcherAssert.assertThat(version.getRepository().getUrl(), containsString("snapshot"));
    }

    @Test
    public void releaseTrainSnapshotDetected() {
        ProjectRelease version = new ProjectReleaseBuilder().versionName("Angel.BUILD-SNAPSHOT").build();
        MatcherAssert.assertThat(version.isSnapshot(), equalTo(true));
        MatcherAssert.assertThat(version.getRepository().getUrl(), containsString("snapshot"));
    }

    @Test
    public void prereleaseDetected() {
        ProjectRelease version = new ProjectReleaseBuilder().versionName("1.0.0.RC1").build();
        MatcherAssert.assertThat(version.isPreRelease(), equalTo(true));
        MatcherAssert.assertThat(version.getRepository().getUrl(), containsString("milestone"));
    }

    @Test
    public void releaseTrainPrereleaseDetected() {
        ProjectRelease version = new ProjectReleaseBuilder().versionName("Angel.RC1").build();
        MatcherAssert.assertThat(version.isPreRelease(), equalTo(true));
        MatcherAssert.assertThat(version.getRepository().getUrl(), containsString("milestone"));
    }

    @Test
    public void gaDetected() {
        ProjectRelease version = new ProjectReleaseBuilder().versionName("1.0.0.RELEASE").build();
        MatcherAssert.assertThat(version.isGeneralAvailability(), equalTo(true));
        MatcherAssert.assertThat(version.getRepository(), is(nullValue()));
    }

    @Test
    public void releaseTrainGaDetected() {
        ProjectRelease version = new ProjectReleaseBuilder().versionName("Angel.RELEASE").build();
        MatcherAssert.assertThat(version.isGeneralAvailability(), equalTo(true));
        MatcherAssert.assertThat(version.getRepository(), is(nullValue()));
    }

    @Test
    public void releaseServiceReleaseTrainGaDetected() {
        ProjectRelease version = new ProjectReleaseBuilder().versionName("Angel.SR1").build();
        MatcherAssert.assertThat(version.isGeneralAvailability(), equalTo(true));
        MatcherAssert.assertThat(version.getRepository(), is(nullValue()));
    }

    @Test
    public void releaseDashSeparatorDetected() {
        ProjectRelease version = new ProjectReleaseBuilder().versionName("Gosling-RC1").build();
        MatcherAssert.assertThat(version.isPreRelease(), equalTo(true));
        MatcherAssert.assertThat(version.getRepository().getUrl(), containsString("milestone"));
    }
}

