/**
 * Copyright 2017 the original author or authors.
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


import java.util.Arrays;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.junit.Test;


public class ProjectVersionOrderTests {
    @Test
    public void getProjectReleases_ordersVersionsByNumber_major() throws Exception {
        Project project = getProject("10.0.0", "9.0.0", "11.0.0");
        MatcherAssert.assertThat(getProjectReleases(project), IsEqual.equalTo(Arrays.asList("11.0.0", "10.0.0", "9.0.0")));
    }

    @Test
    public void getProjectReleases_ordersVersionsByNumber_minor() throws Exception {
        Project project = getProject("0.10.0", "0.9.0", "0.11.0");
        MatcherAssert.assertThat(getProjectReleases(project), IsEqual.equalTo(Arrays.asList("0.11.0", "0.10.0", "0.9.0")));
    }

    @Test
    public void getProjectReleases_ordersVersionsByNumber_patch() throws Exception {
        Project project = getProject("0.0.10", "0.0.9", "0.0.11");
        MatcherAssert.assertThat(getProjectReleases(project), IsEqual.equalTo(Arrays.asList("0.0.11", "0.0.10", "0.0.9")));
    }

    @Test
    public void getProjectReleases_ordersVersionsByNumber_milestones() throws Exception {
        Project project = getProject("0.0.10.RELEASE", "0.0.9.BUILD-SNAPSHOT", "0.0.11.MILESTONE");
        MatcherAssert.assertThat(getProjectReleases(project), IsEqual.equalTo(Arrays.asList("0.0.11.MILESTONE", "0.0.10.RELEASE", "0.0.9.BUILD-SNAPSHOT")));
    }

    @Test
    public void getProjectReleases_ordersVersionsByNumber_milestonesWithVersions() throws Exception {
        Project project = getProject("0.1 M1", "0.1", "0.1 M2");
        MatcherAssert.assertThat(getProjectReleases(project), IsEqual.equalTo(Arrays.asList("0.1", "0.1 M2", "0.1 M1")));
    }

    @Test
    public void getProjectReleases_ordersMultipleStylesOfMilestones() throws Exception {
        Project project = getProject("Gosling-RC9", "Gosling.RC10");
        MatcherAssert.assertThat(getProjectReleases(project), IsEqual.equalTo(Arrays.asList("Gosling.RC10", "Gosling-RC9")));
    }

    @Test
    public void getProjectReleases_ordersElementsWithinAReleaseTrain() throws Exception {
        Project project = getProject("Camden.BUILD-SNAPSHOT", "Camden.M1", "Camden.RC1", "Camden.RELEASE", "Camden.SR5", "Camden.SR6");
        MatcherAssert.assertThat(getProjectReleases(project), IsEqual.equalTo(Arrays.asList("Camden.SR6", "Camden.SR5", "Camden.RELEASE", "Camden.RC1", "Camden.M1", "Camden.BUILD-SNAPSHOT")));
    }

    @Test
    public void getProjectReleases_ordersReleaseTrainsByName() throws Exception {
        Project project = getProject("Brixton.SR7", "Camden.SR5", "Dalston.RELEASE");
        MatcherAssert.assertThat(getProjectReleases(project), IsEqual.equalTo(Arrays.asList("Dalston.RELEASE", "Camden.SR5", "Brixton.SR7")));
    }

    @Test
    public void getProjectReleases_ordersVersionsByNumber_otherCharacters() throws Exception {
        Project project = getProject("Gosling-RC9", "Angel.RC10", "Gosling-RC10", "Angel.RC9");
        MatcherAssert.assertThat(getProjectReleases(project), IsEqual.equalTo(Arrays.asList("Gosling-RC10", "Gosling-RC9", "Angel.RC10", "Angel.RC9")));
    }
}

