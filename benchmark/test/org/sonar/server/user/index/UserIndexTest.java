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
package org.sonar.server.user.index;


import UserQuery.Builder;
import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.Collections;
import org.junit.Rule;
import org.junit.Test;
import org.sonar.api.utils.System2;
import org.sonar.server.es.EsTester;
import org.sonar.server.es.SearchOptions;


public class UserIndexTest {
    private static final String ORGANIZATION_UUID = "my-organization";

    private static final String USER1_LOGIN = "user1";

    private static final String USER2_LOGIN = "user2";

    @Rule
    public EsTester es = EsTester.create();

    private UserIndex underTest = new UserIndex(es.client(), System2.INSTANCE);

    private Builder userQuery = UserQuery.builder();

    @Test
    public void getAtMostThreeActiveUsersForScmAccount_returns_the_users_with_specified_scm_account() {
        UserDoc user1 = UserIndexTest.newUser("user1", Arrays.asList("user_1", "u1"));
        UserDoc user2 = UserIndexTest.newUser("user_with_same_email_as_user1", Arrays.asList("user_2")).setEmail(user1.email());
        UserDoc user3 = UserIndexTest.newUser("inactive_user_with_same_scm_as_user1", user1.scmAccounts()).setActive(false);
        es.putDocuments(UserIndexDefinition.INDEX_TYPE_USER, user1);
        es.putDocuments(UserIndexDefinition.INDEX_TYPE_USER, user2);
        es.putDocuments(UserIndexDefinition.INDEX_TYPE_USER, user3);
        assertThat(underTest.getAtMostThreeActiveUsersForScmAccount(user1.scmAccounts().get(0), UserIndexTest.ORGANIZATION_UUID)).extractingResultOf("login").containsOnly(user1.login());
        assertThat(underTest.getAtMostThreeActiveUsersForScmAccount(user1.login(), UserIndexTest.ORGANIZATION_UUID)).extractingResultOf("login").containsOnly(user1.login());
        // both users share the same email
        assertThat(underTest.getAtMostThreeActiveUsersForScmAccount(user1.email(), UserIndexTest.ORGANIZATION_UUID)).extracting(UserDoc::login).containsOnly(user1.login(), user2.login());
        assertThat(underTest.getAtMostThreeActiveUsersForScmAccount("", UserIndexTest.ORGANIZATION_UUID)).isEmpty();
        assertThat(underTest.getAtMostThreeActiveUsersForScmAccount("unknown", UserIndexTest.ORGANIZATION_UUID)).isEmpty();
    }

    @Test
    public void getAtMostThreeActiveUsersForScmAccount_ignores_inactive_user() {
        String scmAccount = "scmA";
        UserDoc user = UserIndexTest.newUser(UserIndexTest.USER1_LOGIN, Collections.singletonList(scmAccount)).setActive(false);
        es.putDocuments(UserIndexDefinition.INDEX_TYPE_USER, user);
        assertThat(underTest.getAtMostThreeActiveUsersForScmAccount(user.login(), UserIndexTest.ORGANIZATION_UUID)).isEmpty();
        assertThat(underTest.getAtMostThreeActiveUsersForScmAccount(scmAccount, UserIndexTest.ORGANIZATION_UUID)).isEmpty();
    }

    @Test
    public void getAtMostThreeActiveUsersForScmAccount_returns_maximum_three_users() {
        String email = "user@mail.com";
        UserDoc user1 = UserIndexTest.newUser("user1", Collections.emptyList()).setEmail(email);
        UserDoc user2 = UserIndexTest.newUser("user2", Collections.emptyList()).setEmail(email);
        UserDoc user3 = UserIndexTest.newUser("user3", Collections.emptyList()).setEmail(email);
        UserDoc user4 = UserIndexTest.newUser("user4", Collections.emptyList()).setEmail(email);
        es.putDocuments(UserIndexDefinition.INDEX_TYPE_USER, user1);
        es.putDocuments(UserIndexDefinition.INDEX_TYPE_USER, user2);
        es.putDocuments(UserIndexDefinition.INDEX_TYPE_USER, user3);
        es.putDocuments(UserIndexDefinition.INDEX_TYPE_USER, user4);
        // restrict results to 3 users
        assertThat(underTest.getAtMostThreeActiveUsersForScmAccount(email, UserIndexTest.ORGANIZATION_UUID)).hasSize(3);
    }

    @Test
    public void getAtMostThreeActiveUsersForScmAccount_is_case_sensitive_for_login() {
        UserDoc user = UserIndexTest.newUser("the_login", Collections.singletonList("John.Smith"));
        es.putDocuments(UserIndexDefinition.INDEX_TYPE_USER, user);
        assertThat(underTest.getAtMostThreeActiveUsersForScmAccount("the_login", UserIndexTest.ORGANIZATION_UUID)).hasSize(1);
        assertThat(underTest.getAtMostThreeActiveUsersForScmAccount("the_Login", UserIndexTest.ORGANIZATION_UUID)).isEmpty();
    }

    @Test
    public void getAtMostThreeActiveUsersForScmAccount_is_case_insensitive_for_email() {
        UserDoc user = UserIndexTest.newUser("the_login", "the_EMAIL@corp.com", Collections.singletonList("John.Smith"));
        es.putDocuments(UserIndexDefinition.INDEX_TYPE_USER, user);
        assertThat(underTest.getAtMostThreeActiveUsersForScmAccount("the_EMAIL@corp.com", UserIndexTest.ORGANIZATION_UUID)).hasSize(1);
        assertThat(underTest.getAtMostThreeActiveUsersForScmAccount("the_email@corp.com", UserIndexTest.ORGANIZATION_UUID)).hasSize(1);
        assertThat(underTest.getAtMostThreeActiveUsersForScmAccount("email", UserIndexTest.ORGANIZATION_UUID)).isEmpty();
    }

    @Test
    public void getAtMostThreeActiveUsersForScmAccount_is_case_insensitive_for_scm_account() {
        UserDoc user = UserIndexTest.newUser("the_login", Collections.singletonList("John.Smith"));
        es.putDocuments(UserIndexDefinition.INDEX_TYPE_USER, user);
        assertThat(underTest.getAtMostThreeActiveUsersForScmAccount("John.Smith", UserIndexTest.ORGANIZATION_UUID)).hasSize(1);
        assertThat(underTest.getAtMostThreeActiveUsersForScmAccount("JOHN.SMIth", UserIndexTest.ORGANIZATION_UUID)).hasSize(1);
        assertThat(underTest.getAtMostThreeActiveUsersForScmAccount("JOHN.SMITH", UserIndexTest.ORGANIZATION_UUID)).hasSize(1);
        assertThat(underTest.getAtMostThreeActiveUsersForScmAccount("JOHN", UserIndexTest.ORGANIZATION_UUID)).isEmpty();
    }

    @Test
    public void getAtMostThreeActiveUsersForScmAccount_search_only_user_within_given_organization() {
        UserDoc user1 = UserIndexTest.newUser("user1", Collections.singletonList("same_scm")).setOrganizationUuids(Collections.singletonList(UserIndexTest.ORGANIZATION_UUID));
        UserDoc user2 = UserIndexTest.newUser("user2", Collections.singletonList("same_scm")).setOrganizationUuids(Collections.singletonList("another_organization"));
        es.putDocuments(UserIndexDefinition.INDEX_TYPE_USER, user1);
        es.putDocuments(UserIndexDefinition.INDEX_TYPE_USER, user2);
        assertThat(underTest.getAtMostThreeActiveUsersForScmAccount("same_scm", UserIndexTest.ORGANIZATION_UUID)).extractingResultOf("login").containsOnly(user1.login());
    }

    @Test
    public void searchUsers() {
        es.putDocuments(UserIndexDefinition.INDEX_TYPE_USER, UserIndexTest.newUser(UserIndexTest.USER1_LOGIN, Arrays.asList("user_1", "u1")).setEmail("email1"));
        es.putDocuments(UserIndexDefinition.INDEX_TYPE_USER, UserIndexTest.newUser(UserIndexTest.USER2_LOGIN, Collections.emptyList()).setEmail("email2"));
        assertThat(underTest.search(userQuery.build(), new SearchOptions()).getDocs()).hasSize(2);
        assertThat(underTest.search(userQuery.setTextQuery("user").build(), new SearchOptions()).getDocs()).hasSize(2);
        assertThat(underTest.search(userQuery.setTextQuery("ser").build(), new SearchOptions()).getDocs()).hasSize(2);
        assertThat(underTest.search(userQuery.setTextQuery(UserIndexTest.USER1_LOGIN).build(), new SearchOptions()).getDocs()).hasSize(1);
        assertThat(underTest.search(userQuery.setTextQuery(UserIndexTest.USER2_LOGIN).build(), new SearchOptions()).getDocs()).hasSize(1);
        assertThat(underTest.search(userQuery.setTextQuery("mail").build(), new SearchOptions()).getDocs()).hasSize(2);
        assertThat(underTest.search(userQuery.setTextQuery("EMAIL1").build(), new SearchOptions()).getDocs()).hasSize(1);
    }

    @Test
    public void search_users_filter_by_organization_uuid() {
        es.putDocuments(UserIndexDefinition.INDEX_TYPE_USER, UserIndexTest.newUser(UserIndexTest.USER1_LOGIN, Arrays.asList("user_1", "u1")).setEmail("email1").setOrganizationUuids(Lists.newArrayList("O1", "O2")));
        es.putDocuments(UserIndexDefinition.INDEX_TYPE_USER, UserIndexTest.newUser(UserIndexTest.USER2_LOGIN, Collections.emptyList()).setEmail("email2").setOrganizationUuids(Lists.newArrayList("O2")));
        assertThat(underTest.search(userQuery.setOrganizationUuid("O42").build(), new SearchOptions()).getDocs()).isEmpty();
        assertThat(underTest.search(userQuery.setOrganizationUuid("O2").build(), new SearchOptions()).getDocs()).extracting(UserDoc::login).containsOnly(UserIndexTest.USER1_LOGIN, UserIndexTest.USER2_LOGIN);
        assertThat(underTest.search(userQuery.setOrganizationUuid("O1").build(), new SearchOptions()).getDocs()).extracting(UserDoc::login).containsOnly(UserIndexTest.USER1_LOGIN);
    }

    @Test
    public void search_users_filter_by_excluded_organization_uuid() {
        es.putDocuments(UserIndexDefinition.INDEX_TYPE_USER, UserIndexTest.newUser(UserIndexTest.USER1_LOGIN, Arrays.asList("user_1", "u1")).setEmail("email1").setOrganizationUuids(Lists.newArrayList("O1", "O2")));
        es.putDocuments(UserIndexDefinition.INDEX_TYPE_USER, UserIndexTest.newUser(UserIndexTest.USER2_LOGIN, Collections.emptyList()).setEmail("email2").setOrganizationUuids(Lists.newArrayList("O2")));
        assertThat(underTest.search(userQuery.setExcludedOrganizationUuid("O42").build(), new SearchOptions()).getDocs()).hasSize(2);
        assertThat(underTest.search(userQuery.setExcludedOrganizationUuid("O2").build(), new SearchOptions()).getDocs()).isEmpty();
        assertThat(underTest.search(userQuery.setExcludedOrganizationUuid("O1").build(), new SearchOptions()).getDocs()).hasSize(1);
    }
}

