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
package org.sonar.process.cluster.hz;


import MemberSelectors.DATA_MEMBER_SELECTOR;
import java.net.InetAddress;
import java.util.List;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.DisableOnDebug;
import org.junit.rules.ExpectedException;
import org.junit.rules.TestRule;
import org.junit.rules.Timeout;


public class HazelcastMemberImplTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public TestRule safeguardTimeout = new DisableOnDebug(Timeout.seconds(60));

    // use loopback for support of offline builds
    private static InetAddress loopback = InetAddress.getLoopbackAddress();

    private static HazelcastMember member1;

    private static HazelcastMember member2;

    private static HazelcastMember member3;

    @Test
    public void call_executes_query_on_members() throws Exception {
        SuccessfulDistributedCall.COUNTER.set(0L);
        DistributedCall<Long> call = new SuccessfulDistributedCall();
        DistributedAnswer<Long> answer = HazelcastMemberImplTest.member1.call(call, DATA_MEMBER_SELECTOR, 30000L);
        assertThat(answer.getMembers()).extracting(Member::getUuid).containsOnlyOnce(HazelcastMemberImplTest.member1.getUuid(), HazelcastMemberImplTest.member2.getUuid(), HazelcastMemberImplTest.member3.getUuid());
        assertThat(HazelcastMemberImplTest.extractAnswers(answer)).containsOnlyOnce(0L, 1L, 2L);
    }

    @Test
    public void timed_out_calls_do_not_break_other_answers() throws InterruptedException {
        // member 1 and 3 success, member 2 times-out
        TimedOutDistributedCall.COUNTER.set(0L);
        DistributedCall call = new TimedOutDistributedCall();
        DistributedAnswer<Long> answer = HazelcastMemberImplTest.member1.call(call, DATA_MEMBER_SELECTOR, 2000L);
        assertThat(HazelcastMemberImplTest.extractAnswers(answer)).containsOnlyOnce(0L, 2L);
        assertThat(HazelcastMemberImplTest.extractTimeOuts(answer)).containsExactlyInAnyOrder(false, false, true);
    }

    @Test
    public void failed_calls_do_not_break_other_answers() throws InterruptedException {
        // member 1 and 3 success, member 2 fails
        FailedDistributedCall.COUNTER.set(0L);
        DistributedCall call = new FailedDistributedCall();
        DistributedAnswer<Long> answer = HazelcastMemberImplTest.member1.call(call, DATA_MEMBER_SELECTOR, 2000L);
        // 2 successful answers
        assertThat(HazelcastMemberImplTest.extractAnswers(answer)).containsOnlyOnce(0L, 2L);
        // 1 failure
        List<Exception> failures = HazelcastMemberImplTest.extractFailures(answer);
        assertThat(failures).hasSize(1);
        assertThat(failures.get(0)).hasMessageContaining("BOOM");
    }
}

