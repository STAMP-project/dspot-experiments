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
package org.sonar.db.user;


import UserQuery.ALL_ACTIVES;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import org.junit.Rule;
import org.junit.Test;
import org.sonar.api.user.UserQuery;
import org.sonar.api.utils.DateUtils;
import org.sonar.api.utils.internal.TestSystem2;
import org.sonar.db.DatabaseUtils;
import org.sonar.db.DbClient;
import org.sonar.db.DbSession;
import org.sonar.db.DbTester;
import org.sonar.db.component.ComponentDto;
import org.sonar.db.organization.OrganizationDto;


public class UserDaoTest {
    private static final long NOW = 1500000000000L;

    private TestSystem2 system2 = new TestSystem2().setNow(UserDaoTest.NOW);

    @Rule
    public DbTester db = DbTester.create(system2);

    private DbClient dbClient = db.getDbClient();

    private DbSession session = db.getSession();

    private UserDao underTest = db.getDbClient().userDao();

    @Test
    public void selectByUuid() {
        UserDto user1 = db.users().insertUser();
        UserDto user2 = db.users().insertUser(( user) -> user.setActive(false));
        assertThat(underTest.selectByUuid(session, user1.getUuid())).isNotNull();
        assertThat(underTest.selectByUuid(session, user2.getUuid())).isNotNull();
        assertThat(underTest.selectByUuid(session, "unknown")).isNull();
    }

    @Test
    public void selectUsersIds() {
        UserDto user1 = db.users().insertUser(( user) -> user.setLogin("user1"));
        UserDto user2 = db.users().insertUser(( user) -> user.setLogin("user2"));
        UserDto user3 = db.users().insertUser(( user) -> user.setLogin("inactive_user").setActive(false));
        assertThat(underTest.selectByIds(session, Arrays.asList(user1.getId(), user2.getId(), user3.getId(), 1000))).extracting("login").containsExactlyInAnyOrder("user1", "user2", "inactive_user");
        assertThat(underTest.selectByIds(session, Collections.emptyList())).isEmpty();
    }

    @Test
    public void selectUserByLogin_ignore_inactive() {
        db.users().insertUser(( user) -> user.setLogin("user1"));
        db.users().insertUser(( user) -> user.setLogin("user2"));
        db.users().insertUser(( user) -> user.setLogin("inactive_user").setActive(false));
        UserDto user = underTest.selectActiveUserByLogin(session, "inactive_user");
        assertThat(user).isNull();
    }

    @Test
    public void selectUserByLogin_not_found() {
        db.users().insertUser(( user) -> user.setLogin("user"));
        UserDto user = underTest.selectActiveUserByLogin(session, "not_found");
        assertThat(user).isNull();
    }

    @Test
    public void selectUsersByLogins() {
        db.users().insertUser(( user) -> user.setLogin("user1"));
        db.users().insertUser(( user) -> user.setLogin("user2"));
        db.users().insertUser(( user) -> user.setLogin("inactive_user").setActive(false));
        Collection<UserDto> users = underTest.selectByLogins(session, Arrays.asList("user1", "inactive_user", "other"));
        assertThat(users).extracting("login").containsExactlyInAnyOrder("user1", "inactive_user");
    }

    @Test
    public void selectUsersByUuids() {
        UserDto user1 = db.users().insertUser();
        UserDto user2 = db.users().insertUser();
        UserDto user3 = db.users().insertUser(( user) -> user.setActive(false));
        assertThat(((Collection<UserDto>) (underTest.selectByUuids(session, Arrays.asList(user1.getUuid(), user2.getUuid(), user3.getUuid()))))).hasSize(3);
        assertThat(((Collection<UserDto>) (underTest.selectByUuids(session, Arrays.asList(user1.getUuid(), "unknown"))))).hasSize(1);
        assertThat(((Collection<UserDto>) (underTest.selectByUuids(session, Collections.emptyList())))).isEmpty();
    }

    @Test
    public void selectUsersByLogins_empty_logins() {
        // no need to access db
        Collection<UserDto> users = underTest.selectByLogins(session, Collections.emptyList());
        assertThat(users).isEmpty();
    }

    @Test
    public void selectByOrderedLogins() {
        db.users().insertUser(( user) -> user.setLogin("U1"));
        db.users().insertUser(( user) -> user.setLogin("U2"));
        Iterable<UserDto> users = underTest.selectByOrderedLogins(session, Arrays.asList("U1", "U2", "U3"));
        assertThat(users).extracting("login").containsExactly("U1", "U2");
        users = underTest.selectByOrderedLogins(session, Arrays.asList("U2", "U3", "U1"));
        assertThat(users).extracting("login").containsExactly("U2", "U1");
        assertThat(underTest.selectByOrderedLogins(session, Collections.emptyList())).isEmpty();
    }

    @Test
    public void selectUsersByQuery_all() {
        db.users().insertUser(( user) -> user.setLogin("user").setName("User"));
        db.users().insertUser(( user) -> user.setLogin("inactive_user").setName("Disabled").setActive(false));
        List<UserDto> users = underTest.selectUsers(session, UserQuery.builder().includeDeactivated().build());
        assertThat(users).hasSize(2);
    }

    @Test
    public void selectUsersByQuery_only_actives() {
        db.users().insertUser(( user) -> user.setLogin("user").setName("User"));
        db.users().insertUser(( user) -> user.setLogin("inactive_user").setName("Disabled").setActive(false));
        List<UserDto> users = underTest.selectUsers(session, ALL_ACTIVES);
        assertThat(users).extracting(UserDto::getName).containsExactlyInAnyOrder("User");
    }

    @Test
    public void selectUsersByQuery_filter_by_login() {
        db.users().insertUser(( user) -> user.setLogin("user").setName("User"));
        db.users().insertUser(( user) -> user.setLogin("inactive_user").setName("Disabled").setActive(false));
        List<UserDto> users = underTest.selectUsers(session, UserQuery.builder().logins("user", "john").build());
        assertThat(users).extracting(UserDto::getName).containsExactlyInAnyOrder("User");
    }

    @Test
    public void selectUsersByQuery_search_by_login_text() {
        db.users().insertUser(( user) -> user.setLogin("user").setName("User"));
        db.users().insertUser(( user) -> user.setLogin("sbrandhof").setName("Simon Brandhof"));
        List<UserDto> users = underTest.selectUsers(session, UserQuery.builder().searchText("sbr").build());
        assertThat(users).extracting(UserDto::getLogin).containsExactlyInAnyOrder("sbrandhof");
    }

    @Test
    public void selectUsersByQuery_search_by_name_text() {
        db.users().insertUser(( user) -> user.setLogin("user").setName("User"));
        db.users().insertUser(( user) -> user.setLogin("sbrandhof").setName("Simon Brandhof"));
        List<UserDto> users = underTest.selectUsers(session, UserQuery.builder().searchText("Simon").build());
        assertThat(users).extracting(UserDto::getLogin).containsExactlyInAnyOrder("sbrandhof");
    }

    @Test
    public void selectUsersByQuery_escape_special_characters_in_like() {
        db.users().insertUser(( user) -> user.setLogin("user").setName("User"));
        db.users().insertUser(( user) -> user.setLogin("sbrandhof").setName("Simon Brandhof"));
        UserQuery query = UserQuery.builder().searchText("%s%").build();
        // we expect really a login or name containing the 3 characters "%s%"
        List<UserDto> users = underTest.selectUsers(session, query);
        assertThat(users).isEmpty();
    }

    @Test
    public void selectUsers_returns_both_only_root_or_only_non_root_depending_on_mustBeRoot_and_mustNotBeRoot_calls_on_query() {
        UserDto user1 = insertUser(true);
        UserDto root1 = insertRootUser(UserTesting.newUserDto());
        UserDto user2 = insertUser(true);
        UserDto root2 = insertRootUser(UserTesting.newUserDto());
        assertThat(underTest.selectUsers(session, UserQuery.builder().build())).extracting(UserDto::getLogin).containsOnly(user1.getLogin(), user2.getLogin(), root1.getLogin(), root2.getLogin());
        assertThat(underTest.selectUsers(session, UserQuery.builder().mustBeRoot().build())).extracting(UserDto::getLogin).containsOnly(root1.getLogin(), root2.getLogin());
        assertThat(underTest.selectUsers(session, UserQuery.builder().mustNotBeRoot().build())).extracting(UserDto::getLogin).containsOnly(user1.getLogin(), user2.getLogin());
    }

    @Test
    public void countRootUsersButLogin_returns_0_when_there_is_no_user_at_all() {
        assertThat(underTest.countRootUsersButLogin(session, "bla")).isEqualTo(0);
    }

    @Test
    public void countRootUsersButLogin_returns_0_when_there_is_no_root() {
        underTest.insert(session, UserTesting.newUserDto());
        session.commit();
        assertThat(underTest.countRootUsersButLogin(session, "bla")).isEqualTo(0);
    }

    @Test
    public void countRootUsersButLogin_returns_0_when_there_is_no_active_root() {
        insertNonRootUser(UserTesting.newUserDto());
        insertInactiveRootUser(UserTesting.newUserDto());
        session.commit();
        assertThat(underTest.countRootUsersButLogin(session, "bla")).isEqualTo(0);
    }

    @Test
    public void countRootUsersButLogin_returns_count_of_all_active_roots_when_there_specified_login_does_not_exist() {
        insertRootUser(UserTesting.newUserDto());
        insertNonRootUser(UserTesting.newUserDto());
        insertRootUser(UserTesting.newUserDto());
        insertRootUser(UserTesting.newUserDto());
        insertInactiveRootUser(UserTesting.newUserDto());
        insertInactiveRootUser(UserTesting.newUserDto());
        session.commit();
        assertThat(underTest.countRootUsersButLogin(session, "bla")).isEqualTo(3);
    }

    @Test
    public void countRootUsersButLogin_returns_count_of_all_active_roots_when_specified_login_is_not_root() {
        insertRootUser(UserTesting.newUserDto());
        String login = insertNonRootUser(UserTesting.newUserDto()).getLogin();
        insertRootUser(UserTesting.newUserDto());
        insertRootUser(UserTesting.newUserDto());
        insertInactiveRootUser(UserTesting.newUserDto());
        insertInactiveRootUser(UserTesting.newUserDto());
        session.commit();
        assertThat(underTest.countRootUsersButLogin(session, login)).isEqualTo(3);
    }

    @Test
    public void countRootUsersButLogin_returns_count_of_all_active_roots_when_specified_login_is_inactive_root() {
        insertRootUser(UserTesting.newUserDto());
        insertNonRootUser(UserTesting.newUserDto());
        insertRootUser(UserTesting.newUserDto());
        insertRootUser(UserTesting.newUserDto());
        String inactiveRootLogin = insertInactiveRootUser(UserTesting.newUserDto()).getLogin();
        insertInactiveRootUser(UserTesting.newUserDto());
        session.commit();
        assertThat(underTest.countRootUsersButLogin(session, inactiveRootLogin)).isEqualTo(3);
    }

    @Test
    public void countRootUsersButLogin_returns_count_of_all_active_roots_minus_one_when_specified_login_is_active_root() {
        insertRootUser(UserTesting.newUserDto());
        insertNonRootUser(UserTesting.newUserDto());
        insertRootUser(UserTesting.newUserDto());
        String rootLogin = insertRootUser(UserTesting.newUserDto()).getLogin();
        insertInactiveRootUser(UserTesting.newUserDto());
        insertInactiveRootUser(UserTesting.newUserDto());
        session.commit();
        assertThat(underTest.countRootUsersButLogin(session, rootLogin)).isEqualTo(2);
    }

    @Test
    public void insert_user() {
        Long date = DateUtils.parseDate("2014-06-20").getTime();
        UserDto userDto = new UserDto().setId(1).setLogin("john").setName("John").setEmail("jo@hn.com").setScmAccounts(",jo.hn,john2,").setActive(true).setOnboarded(true).setSalt("1234").setCryptedPassword("abcd").setHashMethod("SHA1").setExternalLogin("johngithub").setExternalIdentityProvider("github").setExternalId("EXT_ID").setLocal(true).setHomepageType("project").setHomepageParameter("OB1").setOrganizationUuid("ORG_UUID").setCreatedAt(date).setUpdatedAt(date);
        underTest.insert(db.getSession(), userDto);
        db.getSession().commit();
        UserDto user = underTest.selectActiveUserByLogin(session, "john");
        assertThat(user).isNotNull();
        assertThat(user.getId()).isNotNull();
        assertThat(user.getLogin()).isEqualTo("john");
        assertThat(user.getName()).isEqualTo("John");
        assertThat(user.getEmail()).isEqualTo("jo@hn.com");
        assertThat(user.isActive()).isTrue();
        assertThat(user.isOnboarded()).isTrue();
        assertThat(user.getScmAccounts()).isEqualTo(",jo.hn,john2,");
        assertThat(user.getSalt()).isEqualTo("1234");
        assertThat(user.getCryptedPassword()).isEqualTo("abcd");
        assertThat(user.getHashMethod()).isEqualTo("SHA1");
        assertThat(user.getExternalLogin()).isEqualTo("johngithub");
        assertThat(user.getExternalIdentityProvider()).isEqualTo("github");
        assertThat(user.getExternalId()).isEqualTo("EXT_ID");
        assertThat(user.isLocal()).isTrue();
        assertThat(user.isRoot()).isFalse();
        assertThat(user.getHomepageType()).isEqualTo("project");
        assertThat(user.getHomepageParameter()).isEqualTo("OB1");
        assertThat(user.getOrganizationUuid()).isEqualTo("ORG_UUID");
    }

    @Test
    public void insert_user_does_not_set_last_connection_date() {
        UserDto user = UserTesting.newUserDto().setLastConnectionDate(10000000000L);
        underTest.insert(db.getSession(), user);
        db.getSession().commit();
        UserDto reloaded = underTest.selectByUuid(db.getSession(), user.getUuid());
        assertThat(reloaded.getLastConnectionDate()).isNull();
    }

    @Test
    public void update_user() {
        UserDto user = db.users().insertUser(( u) -> u.setLogin("john").setName("John").setEmail("jo@hn.com").setActive(true).setLocal(true).setOnboarded(false).setOrganizationUuid("OLD_ORG_UUID"));
        underTest.update(db.getSession(), UserTesting.newUserDto().setUuid(user.getUuid()).setLogin("johnDoo").setName("John Doo").setEmail("jodoo@hn.com").setScmAccounts(",jo.hn,john2,johndoo,").setActive(false).setOnboarded(true).setSalt("12345").setCryptedPassword("abcde").setHashMethod("BCRYPT").setExternalLogin("johngithub").setExternalIdentityProvider("github").setExternalId("EXT_ID").setLocal(false).setHomepageType("project").setHomepageParameter("OB1").setOrganizationUuid("ORG_UUID").setLastConnectionDate(10000000000L));
        UserDto reloaded = underTest.selectByUuid(db.getSession(), user.getUuid());
        assertThat(reloaded).isNotNull();
        assertThat(reloaded.getId()).isEqualTo(user.getId());
        assertThat(reloaded.getLogin()).isEqualTo("johnDoo");
        assertThat(reloaded.getName()).isEqualTo("John Doo");
        assertThat(reloaded.getEmail()).isEqualTo("jodoo@hn.com");
        assertThat(reloaded.isActive()).isFalse();
        assertThat(reloaded.isOnboarded()).isTrue();
        assertThat(reloaded.getScmAccounts()).isEqualTo(",jo.hn,john2,johndoo,");
        assertThat(reloaded.getSalt()).isEqualTo("12345");
        assertThat(reloaded.getCryptedPassword()).isEqualTo("abcde");
        assertThat(reloaded.getHashMethod()).isEqualTo("BCRYPT");
        assertThat(reloaded.getExternalLogin()).isEqualTo("johngithub");
        assertThat(reloaded.getExternalIdentityProvider()).isEqualTo("github");
        assertThat(reloaded.getExternalId()).isEqualTo("EXT_ID");
        assertThat(reloaded.isLocal()).isFalse();
        assertThat(reloaded.isRoot()).isFalse();
        assertThat(reloaded.getHomepageType()).isEqualTo("project");
        assertThat(reloaded.getHomepageParameter()).isEqualTo("OB1");
        assertThat(reloaded.getOrganizationUuid()).isEqualTo("ORG_UUID");
        assertThat(reloaded.getLastConnectionDate()).isEqualTo(10000000000L);
    }

    @Test
    public void deactivate_user() {
        UserDto user = insertActiveUser();
        insertUserGroup(user);
        UserDto otherUser = insertActiveUser();
        underTest.update(db.getSession(), user.setLastConnectionDate(10000000000L));
        session.commit();
        underTest.deactivateUser(session, user);
        UserDto userReloaded = underTest.selectUserById(session, user.getId());
        assertThat(userReloaded.isActive()).isFalse();
        assertThat(userReloaded.getLogin()).isNotNull();
        assertThat(userReloaded.getExternalId()).isNotNull();
        assertThat(userReloaded.getExternalLogin()).isNotNull();
        assertThat(userReloaded.getExternalIdentityProvider()).isNotNull();
        assertThat(userReloaded.getEmail()).isNull();
        assertThat(userReloaded.getScmAccounts()).isNull();
        assertThat(userReloaded.getSalt()).isNull();
        assertThat(userReloaded.getCryptedPassword()).isNull();
        assertThat(userReloaded.isRoot()).isFalse();
        assertThat(userReloaded.getUpdatedAt()).isEqualTo(UserDaoTest.NOW);
        assertThat(userReloaded.getHomepageType()).isNull();
        assertThat(userReloaded.getHomepageParameter()).isNull();
        assertThat(userReloaded.getLastConnectionDate()).isNull();
        assertThat(underTest.selectUserById(session, otherUser.getId())).isNotNull();
    }

    @Test
    public void clean_users_homepage_when_deleting_organization() {
        UserDto userUnderTest = UserTesting.newUserDto().setHomepageType("ORGANIZATION").setHomepageParameter("dummy-organization-UUID");
        underTest.insert(session, userUnderTest);
        UserDto untouchedUser = UserTesting.newUserDto().setHomepageType("ORGANIZATION").setHomepageParameter("not-so-dummy-organization-UUID");
        underTest.insert(session, untouchedUser);
        session.commit();
        underTest.cleanHomepage(session, new OrganizationDto().setUuid("dummy-organization-UUID"));
        UserDto userWithAHomepageReloaded = underTest.selectUserById(session, userUnderTest.getId());
        assertThat(userWithAHomepageReloaded.getUpdatedAt()).isEqualTo(UserDaoTest.NOW);
        assertThat(userWithAHomepageReloaded.getHomepageType()).isNull();
        assertThat(userWithAHomepageReloaded.getHomepageParameter()).isNull();
        UserDto untouchedUserReloaded = underTest.selectUserById(session, untouchedUser.getId());
        assertThat(untouchedUserReloaded.getUpdatedAt()).isEqualTo(untouchedUser.getUpdatedAt());
        assertThat(untouchedUserReloaded.getHomepageType()).isEqualTo(untouchedUser.getHomepageType());
        assertThat(untouchedUserReloaded.getHomepageParameter()).isEqualTo(untouchedUser.getHomepageParameter());
    }

    @Test
    public void clean_users_homepage_when_deleting_project() {
        UserDto userUnderTest = UserTesting.newUserDto().setHomepageType("PROJECT").setHomepageParameter("dummy-project-UUID");
        underTest.insert(session, userUnderTest);
        UserDto untouchedUser = UserTesting.newUserDto().setHomepageType("PROJECT").setHomepageParameter("not-so-dummy-project-UUID");
        underTest.insert(session, untouchedUser);
        session.commit();
        underTest.cleanHomepage(session, new ComponentDto().setUuid("dummy-project-UUID"));
        UserDto userWithAHomepageReloaded = underTest.selectUserById(session, userUnderTest.getId());
        assertThat(userWithAHomepageReloaded.getUpdatedAt()).isEqualTo(UserDaoTest.NOW);
        assertThat(userWithAHomepageReloaded.getHomepageType()).isNull();
        assertThat(userWithAHomepageReloaded.getHomepageParameter()).isNull();
        UserDto untouchedUserReloaded = underTest.selectUserById(session, untouchedUser.getId());
        assertThat(untouchedUserReloaded.getUpdatedAt()).isEqualTo(untouchedUser.getUpdatedAt());
        assertThat(untouchedUserReloaded.getHomepageType()).isEqualTo(untouchedUser.getHomepageType());
        assertThat(untouchedUserReloaded.getHomepageParameter()).isEqualTo(untouchedUser.getHomepageParameter());
    }

    @Test
    public void clean_user_homepage() {
        UserDto user = UserTesting.newUserDto().setHomepageType("RANDOM").setHomepageParameter("any-string");
        underTest.insert(session, user);
        session.commit();
        underTest.cleanHomepage(session, user);
        UserDto reloaded = underTest.selectUserById(session, user.getId());
        assertThat(reloaded.getUpdatedAt()).isEqualTo(UserDaoTest.NOW);
        assertThat(reloaded.getHomepageType()).isNull();
        assertThat(reloaded.getHomepageParameter()).isNull();
    }

    @Test
    public void does_not_fail_to_deactivate_missing_user() {
        underTest.deactivateUser(session, UserTesting.newUserDto());
    }

    @Test
    public void select_by_login() {
        UserDto user1 = db.users().insertUser(( user) -> user.setLogin("marius").setName("Marius").setEmail("marius@lesbronzes.fr").setActive(true).setScmAccounts("\nma\nmarius33\n").setSalt("79bd6a8e79fb8c76ac8b121cc7e8e11ad1af8365").setCryptedPassword("650d2261c98361e2f67f90ce5c65a95e7d8ea2fg").setHomepageType("project").setHomepageParameter("OB1"));
        UserDto user2 = db.users().insertUser();
        underTest.setRoot(session, user2.getLogin(), true);
        UserDto dto = underTest.selectByLogin(session, user1.getLogin());
        assertThat(dto.getId()).isEqualTo(user1.getId());
        assertThat(dto.getLogin()).isEqualTo("marius");
        assertThat(dto.getName()).isEqualTo("Marius");
        assertThat(dto.getEmail()).isEqualTo("marius@lesbronzes.fr");
        assertThat(dto.isActive()).isTrue();
        assertThat(dto.getScmAccountsAsList()).containsOnly("ma", "marius33");
        assertThat(dto.getSalt()).isEqualTo("79bd6a8e79fb8c76ac8b121cc7e8e11ad1af8365");
        assertThat(dto.getCryptedPassword()).isEqualTo("650d2261c98361e2f67f90ce5c65a95e7d8ea2fg");
        assertThat(dto.isRoot()).isFalse();
        assertThat(dto.getCreatedAt()).isEqualTo(user1.getCreatedAt());
        assertThat(dto.getUpdatedAt()).isEqualTo(user1.getUpdatedAt());
        assertThat(dto.getHomepageType()).isEqualTo("project");
        assertThat(dto.getHomepageParameter()).isEqualTo("OB1");
        dto = underTest.selectByLogin(session, user2.getLogin());
        assertThat(dto.isRoot()).isTrue();
    }

    @Test
    public void select_nullable_by_scm_account() {
        db.users().insertUser(( user) -> user.setLogin("marius").setName("Marius").setEmail("marius@lesbronzes.fr").setScmAccounts(Arrays.asList("ma", "marius33")));
        db.users().insertUser(( user) -> user.setLogin("sbrandhof").setName("Simon Brandhof").setEmail("sbrandhof@lesbronzes.fr").setScmAccounts(((String) (null))));
        assertThat(underTest.selectByScmAccountOrLoginOrEmail(session, "ma")).extracting(UserDto::getLogin).containsExactly("marius");
        assertThat(underTest.selectByScmAccountOrLoginOrEmail(session, "marius")).extracting(UserDto::getLogin).containsExactly("marius");
        assertThat(underTest.selectByScmAccountOrLoginOrEmail(session, "marius@lesbronzes.fr")).extracting(UserDto::getLogin).containsExactly("marius");
        assertThat(underTest.selectByScmAccountOrLoginOrEmail(session, "m")).isEmpty();
        assertThat(underTest.selectByScmAccountOrLoginOrEmail(session, "unknown")).isEmpty();
    }

    @Test
    public void select_nullable_by_scm_account_return_many_results_when_same_email_is_used_by_many_users() {
        db.users().insertUser(( user) -> user.setLogin("marius").setName("Marius").setEmail("marius@lesbronzes.fr").setScmAccounts(Arrays.asList("ma", "marius33")));
        db.users().insertUser(( user) -> user.setLogin("sbrandhof").setName("Simon Brandhof").setEmail("marius@lesbronzes.fr").setScmAccounts(((String) (null))));
        List<UserDto> results = underTest.selectByScmAccountOrLoginOrEmail(session, "marius@lesbronzes.fr");
        assertThat(results).hasSize(2);
    }

    @Test
    public void select_nullable_by_login() {
        db.users().insertUser(( user) -> user.setLogin("marius"));
        db.users().insertUser(( user) -> user.setLogin("sbrandhof"));
        assertThat(underTest.selectByLogin(session, "marius")).isNotNull();
        assertThat(underTest.selectByLogin(session, "unknown")).isNull();
    }

    @Test
    public void select_by_email() {
        UserDto activeUser1 = db.users().insertUser(( u) -> u.setEmail("user1@email.com"));
        UserDto activeUser2 = db.users().insertUser(( u) -> u.setEmail("user1@email.com"));
        UserDto disableUser = db.users().insertUser(( u) -> u.setActive(false));
        assertThat(underTest.selectByEmail(session, "user1@email.com")).hasSize(2);
        assertThat(underTest.selectByEmail(session, disableUser.getEmail())).isEmpty();
        assertThat(underTest.selectByEmail(session, "unknown")).isEmpty();
    }

    @Test
    public void select_by_external_id_and_identity_provider() {
        UserDto activeUser = db.users().insertUser();
        UserDto disableUser = db.users().insertUser(( u) -> u.setActive(false));
        assertThat(underTest.selectByExternalIdAndIdentityProvider(session, activeUser.getExternalId(), activeUser.getExternalIdentityProvider())).isNotNull();
        assertThat(underTest.selectByExternalIdAndIdentityProvider(session, disableUser.getExternalId(), disableUser.getExternalIdentityProvider())).isNotNull();
        assertThat(underTest.selectByExternalIdAndIdentityProvider(session, "unknown", "unknown")).isNull();
    }

    @Test
    public void select_by_external_ids_and_identity_provider() {
        UserDto user1 = db.users().insertUser(( u) -> u.setExternalIdentityProvider("github"));
        UserDto user2 = db.users().insertUser(( u) -> u.setExternalIdentityProvider("github"));
        UserDto user3 = db.users().insertUser(( u) -> u.setExternalIdentityProvider("bitbucket"));
        UserDto disableUser = db.users().insertDisabledUser(( u) -> u.setExternalIdentityProvider("github"));
        assertThat(underTest.selectByExternalIdsAndIdentityProvider(session, Collections.singletonList(user1.getExternalId()), "github")).extracting(UserDto::getUuid).containsExactlyInAnyOrder(user1.getUuid());
        assertThat(underTest.selectByExternalIdsAndIdentityProvider(session, Arrays.asList(user1.getExternalId(), user2.getExternalId(), user3.getExternalId(), disableUser.getExternalId()), "github")).extracting(UserDto::getUuid).containsExactlyInAnyOrder(user1.getUuid(), user2.getUuid(), disableUser.getUuid());
        assertThat(underTest.selectByExternalIdsAndIdentityProvider(session, Collections.singletonList("unknown"), "github")).isEmpty();
        assertThat(underTest.selectByExternalIdsAndIdentityProvider(session, Collections.singletonList(user1.getExternalId()), "unknown")).isEmpty();
    }

    @Test
    public void select_by_external_login_and_identity_provider() {
        UserDto activeUser = db.users().insertUser();
        UserDto disableUser = db.users().insertUser(( u) -> u.setActive(false));
        assertThat(underTest.selectByExternalLoginAndIdentityProvider(session, activeUser.getExternalLogin(), activeUser.getExternalIdentityProvider())).isNotNull();
        assertThat(underTest.selectByExternalLoginAndIdentityProvider(session, disableUser.getExternalLogin(), disableUser.getExternalIdentityProvider())).isNotNull();
        assertThat(underTest.selectByExternalLoginAndIdentityProvider(session, "unknown", "unknown")).isNull();
    }

    @Test
    public void setRoot_does_not_fail_on_non_existing_login() {
        underTest.setRoot(session, "unkown", true);
        underTest.setRoot(session, "unkown", false);
    }

    @Test
    public void setRoot_set_root_flag_of_specified_user_to_specified_value_and_updates_udpateAt() {
        String login = insertActiveUser().getLogin();
        UserDto otherUser = insertActiveUser();
        assertThat(underTest.selectByLogin(session, login).isRoot()).isEqualTo(false);
        assertThat(underTest.selectByLogin(session, otherUser.getLogin()).isRoot()).isEqualTo(false);
        // does not fail when changing to same value
        system2.setNow(15000L);
        commit(() -> underTest.setRoot(session, login, false));
        verifyRootAndUpdatedAt(login, false, 15000L);
        verifyRootAndUpdatedAt(otherUser.getLogin(), false, otherUser.getUpdatedAt());
        // change value
        system2.setNow(26000L);
        commit(() -> underTest.setRoot(session, login, true));
        verifyRootAndUpdatedAt(login, true, 26000L);
        verifyRootAndUpdatedAt(otherUser.getLogin(), false, otherUser.getUpdatedAt());
        // does not fail when changing to same value
        system2.setNow(37000L);
        commit(() -> underTest.setRoot(session, login, true));
        verifyRootAndUpdatedAt(login, true, 37000L);
        verifyRootAndUpdatedAt(otherUser.getLogin(), false, otherUser.getUpdatedAt());
        // change value back
        system2.setNow(48000L);
        commit(() -> underTest.setRoot(session, login, false));
        verifyRootAndUpdatedAt(login, false, 48000L);
        verifyRootAndUpdatedAt(otherUser.getLogin(), false, otherUser.getUpdatedAt());
    }

    @Test
    public void setRoot_has_no_effect_on_root_flag_of_inactive_user() {
        String nonRootInactiveUser = insertUser(false).getLogin();
        commit(() -> underTest.setRoot(session, nonRootInactiveUser, true));
        assertThat(underTest.selectByLogin(session, nonRootInactiveUser).isRoot()).isFalse();
        // create inactive root user
        UserDto rootUser = insertActiveUser();
        commit(() -> underTest.setRoot(session, rootUser.getLogin(), true));
        rootUser.setActive(false);
        commit(() -> underTest.update(session, rootUser));
        UserDto inactiveRootUser = underTest.selectByLogin(session, rootUser.getLogin());
        assertThat(inactiveRootUser.isRoot()).isTrue();
        assertThat(inactiveRootUser.isActive()).isFalse();
        commit(() -> underTest.setRoot(session, inactiveRootUser.getLogin(), false));
        assertThat(underTest.selectByLogin(session, inactiveRootUser.getLogin()).isRoot()).isTrue();
    }

    @Test
    public void scrollByLUuids() {
        UserDto u1 = insertUser(true);
        UserDto u2 = insertUser(false);
        UserDto u3 = insertUser(false);
        List<UserDto> result = new ArrayList<>();
        underTest.scrollByUuids(db.getSession(), Arrays.asList(u2.getUuid(), u3.getUuid(), "does not exist"), result::add);
        assertThat(result).extracting(UserDto::getUuid, UserDto::getName).containsExactlyInAnyOrder(tuple(u2.getUuid(), u2.getName()), tuple(u3.getUuid(), u3.getName()));
    }

    @Test
    public void scrollByUuids_scrolls_by_pages_of_1000_uuids() {
        List<String> uuids = new ArrayList<>();
        for (int i = 0; i < ((DatabaseUtils.PARTITION_SIZE_FOR_ORACLE) + 10); i++) {
            uuids.add(insertUser(true).getUuid());
        }
        List<UserDto> result = new ArrayList<>();
        underTest.scrollByUuids(db.getSession(), uuids, result::add);
        assertThat(result).extracting(UserDto::getUuid).containsExactlyInAnyOrder(uuids.toArray(new String[0]));
    }

    @Test
    public void scrollAll() {
        UserDto u1 = insertUser(true);
        UserDto u2 = insertUser(false);
        List<UserDto> result = new ArrayList<>();
        underTest.scrollAll(db.getSession(), result::add);
        assertThat(result).extracting(UserDto::getLogin, UserDto::getName).containsExactlyInAnyOrder(tuple(u1.getLogin(), u1.getName()), tuple(u2.getLogin(), u2.getName()));
    }
}

