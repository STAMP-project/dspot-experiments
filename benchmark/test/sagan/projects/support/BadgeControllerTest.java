/**
 * Copyright 2016-2017 the original author or authors.
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
package sagan.projects.support;


import HttpStatus.NOT_FOUND;
import HttpStatus.OK;
import java.util.ArrayList;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;
import sagan.projects.Project;
import sagan.projects.ProjectRelease;
import sagan.projects.ProjectRelease.ReleaseStatus;


/**
 *
 *
 * @author Mark Paluch
 */
@RunWith(MockitoJUnitRunner.class)
public class BadgeControllerTest {
    @Mock
    private ProjectMetadataService projectMetadataServiceMock;

    private VersionBadgeService versionBadgeService = new VersionBadgeService();

    private BadgeController controller;

    private Project project;

    private List<ProjectRelease> releases = new ArrayList<>();

    @Test
    public void badgeNotFound() throws Exception {
        ResponseEntity<byte[]> response = controller.releaseBadge("spring-data-redis");
        Assert.assertThat(response.getStatusCode(), CoreMatchers.is(CoreMatchers.equalTo(NOT_FOUND)));
    }

    @Test
    public void badgeShouldBeGenerated() throws Exception {
        releases.add(new ProjectRelease("1.0.RELEASE", ReleaseStatus.GENERAL_AVAILABILITY, true, "", "", "", ""));
        ResponseEntity<byte[]> response = controller.releaseBadge("spring-data-redis");
        Assert.assertThat(response.getStatusCode(), CoreMatchers.is(CoreMatchers.equalTo(OK)));
        Assert.assertThat(response.getHeaders().getETag(), CoreMatchers.is(CoreMatchers.equalTo("\"1.0.RELEASE\"")));
        Assert.assertThat(response.getHeaders().getCacheControl(), CoreMatchers.is(CoreMatchers.equalTo("max-age=3600")));
        String content = new String(response.getBody());
        Assert.assertThat(content, CoreMatchers.containsString("<svg"));
        Assert.assertThat(content, CoreMatchers.containsString("Spring Data Redis"));
        Assert.assertThat(content, CoreMatchers.containsString("1.0.RELEASE"));
    }

    @Test
    public void projecWithTwoReleasesShouldBeGenerated() throws Exception {
        releases.add(new ProjectRelease("1.0.RELEASE", ReleaseStatus.GENERAL_AVAILABILITY, false, "", "", "", ""));
        releases.add(new ProjectRelease("1.1.RELEASE", ReleaseStatus.GENERAL_AVAILABILITY, true, "", "", "", ""));
        ResponseEntity<byte[]> response = controller.releaseBadge("spring-data-redis");
        String content = new String(response.getBody());
        Assert.assertThat(content, CoreMatchers.containsString("1.1.RELEASE"));
    }

    @Test
    public void projecWithTwoReleasesWithoutCurrentFlagPicksHighestRelease() throws Exception {
        releases.add(new ProjectRelease("1.0.RELEASE", ReleaseStatus.GENERAL_AVAILABILITY, false, "", "", "", ""));
        releases.add(new ProjectRelease("1.1.RELEASE", ReleaseStatus.GENERAL_AVAILABILITY, false, "", "", "", ""));
        ResponseEntity<byte[]> response = controller.releaseBadge("spring-data-redis");
        String content = new String(response.getBody());
        Assert.assertThat(content, CoreMatchers.containsString("1.1.RELEASE"));
    }

    @Test
    public void projecWithTwoReleasesFlagPicksCurrentRelease() throws Exception {
        releases.add(new ProjectRelease("1.0.RELEASE", ReleaseStatus.GENERAL_AVAILABILITY, true, "", "", "", ""));
        releases.add(new ProjectRelease("1.1.RELEASE", ReleaseStatus.GENERAL_AVAILABILITY, false, "", "", "", ""));
        ResponseEntity<byte[]> response = controller.releaseBadge("spring-data-redis");
        String content = new String(response.getBody());
        Assert.assertThat(content, CoreMatchers.containsString("1.0.RELEASE"));
    }

    @Test
    public void projecWithTwoReleasesUsingSymbolicNamesWithNumbersWithoutCurrentFlagPicksMostRecentRelease() throws Exception {
        releases.add(new ProjectRelease("Angel-SR6", ReleaseStatus.GENERAL_AVAILABILITY, false, "", "", "", ""));
        releases.add(new ProjectRelease("Brixton-SR2", ReleaseStatus.GENERAL_AVAILABILITY, false, "", "", "", ""));
        ResponseEntity<byte[]> response = controller.releaseBadge("spring-data-redis");
        String content = new String(response.getBody());
        Assert.assertThat(content, CoreMatchers.containsString("Brixton-SR2"));
    }

    @Test
    public void projecWithTwoReleasesUsingSymbolicNamesWithoutCurrentFlagPicksFirstRelease() throws Exception {
        releases.add(new ProjectRelease("Angel-SR6", ReleaseStatus.GENERAL_AVAILABILITY, false, "", "", "", ""));
        releases.add(new ProjectRelease("Brixton-RELEASE", ReleaseStatus.GENERAL_AVAILABILITY, false, "", "", "", ""));
        ResponseEntity<byte[]> response = controller.releaseBadge("spring-data-redis");
        String content = new String(response.getBody());
        Assert.assertThat(content, CoreMatchers.containsString("Brixton-RELEASE"));
    }

    @Test
    public void projecWithTwoReleasesUsingSymbolicNamesFlagPicksCurrentRelease() throws Exception {
        releases.add(new ProjectRelease("Angel-SR1", ReleaseStatus.GENERAL_AVAILABILITY, false, "", "", "", ""));
        releases.add(new ProjectRelease("Brixton-RELEASE", ReleaseStatus.GENERAL_AVAILABILITY, true, "", "", "", ""));
        ResponseEntity<byte[]> response = controller.releaseBadge("spring-data-redis");
        String content = new String(response.getBody());
        Assert.assertThat(content, CoreMatchers.containsString("Brixton-RELEASE"));
    }
}

