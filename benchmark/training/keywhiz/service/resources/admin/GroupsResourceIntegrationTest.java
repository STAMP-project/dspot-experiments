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
import KeywhizClient.MalformedRequestException;
import KeywhizClient.NotFoundException;
import KeywhizClient.UnauthorizedException;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.List;
import javax.annotation.Nullable;
import keywhiz.IntegrationTestRule;
import keywhiz.api.GroupDetailResponse;
import keywhiz.api.model.Group;
import keywhiz.client.KeywhizClient;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.RuleChain;


public class GroupsResourceIntegrationTest {
    KeywhizClient keywhizClient;

    @ClassRule
    public static final RuleChain chain = IntegrationTestRule.rule();

    @Test
    public void listsGroups() throws IOException {
        keywhizClient.login(defaultUser, defaultPassword.toCharArray());
        List<Group> groups = keywhizClient.allGroups();
        List<String> names = Lists.transform(groups, new Function<Group, String>() {
            @Override
            public String apply(@Nullable
            Group group) {
                return group == null ? null : group.getName();
            }
        });
        assertThat(names).contains("Security", "Web", "iOS");// BlackOps may be deleted.

    }

    @Test(expected = UnauthorizedException.class)
    public void adminRejectsNonKeywhizUsers() throws IOException {
        keywhizClient.login("username", "password".toCharArray());
        keywhizClient.allGroups();
    }

    @Test(expected = UnauthorizedException.class)
    public void adminRejectsWithoutCookie() throws IOException {
        keywhizClient.allGroups();
    }

    @Test(expected = MalformedRequestException.class)
    public void createFailsWhenGroupExists() throws IOException {
        keywhizClient.login(defaultUser, defaultPassword.toCharArray());
        keywhizClient.createGroup("Blackops", "should already exist", ImmutableMap.of("app", "Blackopps-app"));
    }

    @Test
    public void createsGroup() throws IOException {
        keywhizClient.login(defaultUser, defaultPassword.toCharArray());
        GroupDetailResponse groupDetails = keywhizClient.createGroup("NewGroup", "", ImmutableMap.of("app", "NewApp"));
        assertThat(groupDetails.getName()).isEqualTo("NewGroup");
        List<Group> groups = keywhizClient.allGroups();
        List<String> names = Lists.transform(groups, new Function<Group, String>() {
            @Override
            public String apply(@Nullable
            Group group) {
                return group == null ? null : group.getName();
            }
        });
        assertThat(names).contains("NewGroup");
    }

    @Test
    public void getGroupInfo() throws IOException {
        keywhizClient.login(defaultUser, defaultPassword.toCharArray());
        GroupDetailResponse groupDetail = keywhizClient.groupDetailsForId(916);
        assertThat(groupDetail.getName()).isEqualTo("Blackops");
    }

    @Test
    public void getGroupInfoByName() throws IOException {
        keywhizClient.login(defaultUser, defaultPassword.toCharArray());
        Group group = keywhizClient.getGroupByName("Blackops");
        assertThat(group.getId()).isEqualTo(916);
    }

    @Test(expected = NotFoundException.class)
    public void notFoundOnMissingId() throws IOException {
        keywhizClient.login(defaultUser, defaultPassword.toCharArray());
        keywhizClient.groupDetailsForId(900000);
    }

    @Test(expected = NotFoundException.class)
    public void notFoundOnMissingName() throws IOException {
        keywhizClient.login(defaultUser, defaultPassword.toCharArray());
        keywhizClient.getGroupByName("non-existent-group");
    }

    @Test
    public void deletesGroup() throws IOException {
        keywhizClient.login(defaultUser, defaultPassword.toCharArray());
        keywhizClient.deleteGroupWithId(920);
        try {
            keywhizClient.groupDetailsForId(920);
            failBecauseExceptionWasNotThrown(NotFoundException.class);
        } catch (KeywhizClient e) {
            // As expected, the group no longer exists.
        }
    }
}

