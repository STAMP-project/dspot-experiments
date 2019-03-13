/**
 * Copyright (C) 2015 Square, Inc.
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
package keywhiz.service.resources.admin;


import DbSeedCommand.defaultPassword;
import DbSeedCommand.defaultUser;
import KeywhizClient.NotFoundException;
import KeywhizClient.UnauthorizedException;
import java.io.IOException;
import keywhiz.IntegrationTestRule;
import keywhiz.client.KeywhizClient;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.RuleChain;


public class MembershipResourceIntegrationTest {
    KeywhizClient keywhizClient;

    @ClassRule
    public static final RuleChain chain = IntegrationTestRule.rule();

    @Test
    public void allowingSecretInGroup() throws IOException {
        keywhizClient.login(defaultUser, defaultPassword.toCharArray());
        assertThat(keywhizClient.secretDetailsForId(741).groups).doNotHave(MembershipResourceIntegrationTest.groupId(919));
        keywhizClient.grantSecretToGroupByIds(741, 919);
        assertThat(keywhizClient.secretDetailsForId(741).groups).haveExactly(1, MembershipResourceIntegrationTest.groupId(919));
    }

    @Test(expected = NotFoundException.class)
    public void allowingMissingSecretInGroup() throws IOException {
        keywhizClient.login(defaultUser, defaultPassword.toCharArray());
        keywhizClient.grantSecretToGroupByIds(4539475, 919);
    }

    @Test(expected = NotFoundException.class)
    public void allowingSecretInMissingGroup() throws IOException {
        keywhizClient.login(defaultUser, defaultPassword.toCharArray());
        keywhizClient.grantSecretToGroupByIds(741, 237694);
    }

    @Test(expected = UnauthorizedException.class)
    public void adminRejectsNonKeywhizUsers() throws IOException {
        keywhizClient.login("username", "password".toCharArray());
        keywhizClient.grantSecretToGroupByIds(741, 916);
    }

    @Test(expected = UnauthorizedException.class)
    public void adminRejectsWithoutCookie() throws IOException {
        keywhizClient.grantSecretToGroupByIds(741, 916);
    }

    @Test
    public void revokesSecretFromGroup() throws IOException {
        keywhizClient.login(defaultUser, defaultPassword.toCharArray());
        assertThat(keywhizClient.secretDetailsForId(737).groups).haveExactly(1, MembershipResourceIntegrationTest.groupId(918));
        keywhizClient.revokeSecretFromGroupByIds(737, 918);
        assertThat(keywhizClient.secretDetailsForId(737).groups).doNotHave(MembershipResourceIntegrationTest.groupId(918));
    }

    @Test(expected = NotFoundException.class)
    public void revokingMissingSecretFromGroup() throws IOException {
        keywhizClient.login(defaultUser, defaultPassword.toCharArray());
        keywhizClient.revokeSecretFromGroupByIds(4539475, 919);
    }

    @Test(expected = NotFoundException.class)
    public void revokingSecretInMissingGroup() throws IOException {
        keywhizClient.login(defaultUser, defaultPassword.toCharArray());
        keywhizClient.grantSecretToGroupByIds(741, 237694);
    }

    @Test
    public void enrollsClientInGroup() throws IOException {
        keywhizClient.login(defaultUser, defaultPassword.toCharArray());
        assertThat(keywhizClient.clientDetailsForId(770).groups).doNotHave(MembershipResourceIntegrationTest.groupId(918));
        keywhizClient.enrollClientInGroupByIds(770, 918);
        assertThat(keywhizClient.clientDetailsForId(770).groups).haveExactly(1, MembershipResourceIntegrationTest.groupId(918));
    }

    @Test(expected = NotFoundException.class)
    public void enrollingMissingClientInGroup() throws IOException {
        keywhizClient.login(defaultUser, defaultPassword.toCharArray());
        keywhizClient.enrollClientInGroupByIds(4539575, 919);
    }

    @Test(expected = NotFoundException.class)
    public void enrollingClientInMissingGroup() throws IOException {
        keywhizClient.login(defaultUser, defaultPassword.toCharArray());
        keywhizClient.enrollClientInGroupByIds(770, 237694);
    }

    @Test
    public void evictsClientFromGroup() throws IOException {
        keywhizClient.login(defaultUser, defaultPassword.toCharArray());
        assertThat(keywhizClient.clientDetailsForId(770).groups).haveExactly(1, MembershipResourceIntegrationTest.groupId(917));
        keywhizClient.evictClientFromGroupByIds(770, 917);
        assertThat(keywhizClient.clientDetailsForId(770).groups).doNotHave(MembershipResourceIntegrationTest.groupId(917));
    }

    @Test(expected = NotFoundException.class)
    public void evictingMissingClientFromGroup() throws IOException {
        keywhizClient.login(defaultUser, defaultPassword.toCharArray());
        keywhizClient.evictClientFromGroupByIds(4539475, 919);
    }

    @Test(expected = NotFoundException.class)
    public void evictingClientFromMissingGroup() throws IOException {
        keywhizClient.login(defaultUser, defaultPassword.toCharArray());
        keywhizClient.enrollClientInGroupByIds(770, 237694);
    }
}

