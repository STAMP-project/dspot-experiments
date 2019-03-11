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


import com.hazelcast.core.Member;
import java.io.IOException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class DistributedAnswerTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private Member member = DistributedAnswerTest.newMember("member1");

    private DistributedAnswer underTest = new DistributedAnswer();

    @Test
    public void getMembers_return_all_members() {
        underTest.setAnswer(member, "foo");
        underTest.setTimedOut(DistributedAnswerTest.newMember("bar"));
        underTest.setFailed(DistributedAnswerTest.newMember("baz"), new IOException("BOOM"));
        assertThat(underTest.getMembers()).hasSize(3);
    }

    @Test
    public void test_call_with_unknown_member() {
        assertThat(underTest.getAnswer(member)).isEmpty();
        assertThat(underTest.hasTimedOut(member)).isFalse();
        assertThat(underTest.getFailed(member)).isEmpty();
    }

    @Test
    public void test_setAnswer() {
        underTest.setAnswer(member, "foo");
        assertThat(underTest.getAnswer(member)).hasValue("foo");
        assertThat(underTest.hasTimedOut(member)).isFalse();
        assertThat(underTest.getFailed(member)).isEmpty();
    }

    @Test
    public void test_setTimedOut() {
        underTest.setTimedOut(member);
        assertThat(underTest.getAnswer(member)).isEmpty();
        assertThat(underTest.hasTimedOut(member)).isTrue();
        assertThat(underTest.getFailed(member)).isEmpty();
    }

    @Test
    public void test_setFailed() {
        IOException e = new IOException();
        underTest.setFailed(member, e);
        assertThat(underTest.getAnswer(member)).isEmpty();
        assertThat(underTest.hasTimedOut(member)).isFalse();
        assertThat(underTest.getFailed(member)).hasValue(e);
    }

    @Test
    public void member_can_be_referenced_multiple_times() {
        underTest.setTimedOut(member);
        underTest.setAnswer(member, "foo");
        IOException exception = new IOException();
        underTest.setFailed(member, exception);
        assertThat(underTest.hasTimedOut(member)).isTrue();
        assertThat(underTest.getAnswer(member)).hasValue("foo");
        assertThat(underTest.getFailed(member)).hasValue(exception);
    }

    @Test
    public void propagateExceptions_does_nothing_if_no_members() {
        // no errors
        underTest.propagateExceptions();
    }

    @Test
    public void propagateExceptions_does_nothing_if_no_errors() {
        underTest.setAnswer(DistributedAnswerTest.newMember("foo"), "bar");
        // no errors
        underTest.propagateExceptions();
    }

    @Test
    public void propagateExceptions_throws_ISE_if_at_least_one_timeout() {
        underTest.setAnswer(DistributedAnswerTest.newMember("bar"), "baz");
        underTest.setTimedOut(DistributedAnswerTest.newMember("foo"));
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Distributed cluster action timed out in cluster nodes foo");
        underTest.propagateExceptions();
    }

    @Test
    public void propagateExceptions_throws_ISE_if_at_least_one_failure() {
        underTest.setAnswer(DistributedAnswerTest.newMember("bar"), "baz");
        underTest.setFailed(DistributedAnswerTest.newMember("foo"), new IOException("BOOM"));
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Distributed cluster action in cluster nodes foo (other nodes may have timed out)");
        underTest.propagateExceptions();
    }
}

