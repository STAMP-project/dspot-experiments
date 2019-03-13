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
package org.springframework.cloud.kubernetes.leader;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.integration.leader.Candidate;


/**
 *
 *
 * @author Gytis Trikleris
 */
@RunWith(MockitoJUnitRunner.class)
public class LeaderTest {
    private static final String ROLE = "test-role";

    private static final String ID = "test-id";

    @Mock
    private Candidate mockCandidate;

    private Leader leader;

    @Test
    public void shouldGetRole() {
        assertThat(this.leader.getRole()).isEqualTo(LeaderTest.ROLE);
    }

    @Test
    public void shouldGetId() {
        assertThat(this.leader.getId()).isEqualTo(LeaderTest.ID);
    }

    @Test
    public void shouldCheckWithNullCandidate() {
        assertThat(this.leader.isCandidate(null)).isEqualTo(false);
    }

    @Test
    public void shouldCheckCandidate() {
        BDDMockito.given(this.mockCandidate.getId()).willReturn(LeaderTest.ID);
        BDDMockito.given(this.mockCandidate.getRole()).willReturn(LeaderTest.ROLE);
        assertThat(this.leader.isCandidate(this.mockCandidate)).isTrue();
    }
}

