/**
 * Copyright 2019-2019 the original author or authors.
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
package org.springframework.cloud.kubernetes.leader;


import Info.Builder;
import java.util.Map;
import java.util.Optional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.actuate.info.Info;
import org.springframework.integration.leader.Candidate;


@RunWith(MockitoJUnitRunner.class)
public class LeaderInfoContributorTest {
    @Mock
    private Candidate mockCandidate;

    @Mock
    private LeadershipController mockLeadershipController;

    @Mock
    private Leader mockLeader;

    private LeaderInfoContributor leaderInfoContributor;

    @SuppressWarnings("unchecked")
    @Test
    public void infoWithoutLeader() {
        Info.Builder builder = new Info.Builder();
        leaderInfoContributor.contribute(builder);
        Map<String, Object> details = ((Map<String, Object>) (builder.build().get("leaderElection")));
        assertThat(details).containsEntry("leaderId", "Unknown");
    }

    @SuppressWarnings("unchecked")
    @Test
    public void infoWhenLeader() {
        BDDMockito.given(this.mockLeadershipController.getLocalLeader()).willReturn(Optional.of(this.mockLeader));
        BDDMockito.given(this.mockLeader.isCandidate(this.mockCandidate)).willReturn(true);
        BDDMockito.given(this.mockLeader.getRole()).willReturn("testRole");
        BDDMockito.given(this.mockLeader.getId()).willReturn("id");
        Info.Builder builder = new Info.Builder();
        leaderInfoContributor.contribute(builder);
        Map<String, Object> details = ((Map<String, Object>) (builder.build().get("leaderElection")));
        assertThat(details).containsEntry("isLeader", true);
        assertThat(details).containsEntry("leaderId", "id");
        assertThat(details).containsEntry("role", "testRole");
    }

    @SuppressWarnings("unchecked")
    @Test
    public void infoWhenAnotherIsLeader() {
        BDDMockito.given(this.mockLeadershipController.getLocalLeader()).willReturn(Optional.of(this.mockLeader));
        BDDMockito.given(this.mockLeader.getRole()).willReturn("testRole");
        BDDMockito.given(this.mockLeader.getId()).willReturn("id");
        Info.Builder builder = new Info.Builder();
        leaderInfoContributor.contribute(builder);
        Map<String, Object> details = ((Map<String, Object>) (builder.build().get("leaderElection")));
        assertThat(details).containsEntry("isLeader", false);
        assertThat(details).containsEntry("leaderId", "id");
        assertThat(details).containsEntry("role", "testRole");
    }
}

