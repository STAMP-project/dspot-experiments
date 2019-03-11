/**
 * Copyright 2002-2018 the original author or authors.
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
package org.springframework.security.provisioning;


import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.TestDataSource;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserCache;
import org.springframework.security.core.userdetails.UserDetails;


/**
 * Tests for {@link JdbcUserDetailsManager}
 *
 * @author Luke Taylor
 */
public class JdbcUserDetailsManagerTests {
    private static final String SELECT_JOE_SQL = "select * from users where username = 'joe'";

    private static final String SELECT_JOE_AUTHORITIES_SQL = "select * from authorities where username = 'joe'";

    private static final UserDetails joe = new User("joe", "password", true, true, true, true, AuthorityUtils.createAuthorityList("A", "C", "B"));

    private static TestDataSource dataSource;

    private JdbcUserDetailsManager manager;

    private JdbcUserDetailsManagerTests.MockUserCache cache;

    private JdbcTemplate template;

    @Test
    public void createUserInsertsCorrectData() {
        manager.createUser(JdbcUserDetailsManagerTests.joe);
        UserDetails joe2 = manager.loadUserByUsername("joe");
        assertThat(joe2).isEqualTo(JdbcUserDetailsManagerTests.joe);
    }

    @Test
    public void createUserInsertsCorrectDataWithLocking() {
        setUpAccLockingColumns();
        UserDetails user = new User("joe", "pass", true, false, true, false, AuthorityUtils.createAuthorityList("A", "B"));
        manager.createUser(user);
        UserDetails user2 = manager.loadUserByUsername(user.getUsername());
        assertThat(user2).isEqualToComparingFieldByField(user);
    }

    @Test
    public void deleteUserRemovesUserDataAndAuthoritiesAndClearsCache() {
        insertJoe();
        manager.deleteUser("joe");
        assertThat(template.queryForList(JdbcUserDetailsManagerTests.SELECT_JOE_SQL)).isEmpty();
        assertThat(template.queryForList(JdbcUserDetailsManagerTests.SELECT_JOE_AUTHORITIES_SQL)).isEmpty();
        assertThat(cache.getUserMap().containsKey("joe")).isFalse();
    }

    @Test
    public void updateUserChangesDataCorrectlyAndClearsCache() {
        insertJoe();
        User newJoe = new User("joe", "newpassword", false, true, true, true, AuthorityUtils.createAuthorityList(new String[]{ "D", "F", "E" }));
        manager.updateUser(newJoe);
        UserDetails joe = manager.loadUserByUsername("joe");
        assertThat(joe).isEqualTo(newJoe);
        assertThat(cache.getUserMap().containsKey("joe")).isFalse();
    }

    @Test
    public void updateUserChangesDataCorrectlyAndClearsCacheWithLocking() {
        setUpAccLockingColumns();
        insertJoe();
        User newJoe = new User("joe", "newpassword", false, false, false, true, AuthorityUtils.createAuthorityList("D", "F", "E"));
        manager.updateUser(newJoe);
        UserDetails joe = manager.loadUserByUsername(newJoe.getUsername());
        assertThat(joe).isEqualToComparingFieldByField(newJoe);
        assertThat(cache.getUserMap().containsKey(newJoe.getUsername())).isFalse();
    }

    @Test
    public void userExistsReturnsFalseForNonExistentUsername() {
        assertThat(manager.userExists("joe")).isFalse();
    }

    @Test
    public void userExistsReturnsTrueForExistingUsername() {
        insertJoe();
        assertThat(manager.userExists("joe")).isTrue();
        assertThat(cache.getUserMap().containsKey("joe")).isTrue();
    }

    @Test(expected = AccessDeniedException.class)
    public void changePasswordFailsForUnauthenticatedUser() {
        manager.changePassword("password", "newPassword");
    }

    @Test
    public void changePasswordSucceedsWithAuthenticatedUserAndNoAuthenticationManagerSet() {
        insertJoe();
        authenticateJoe();
        manager.changePassword("wrongpassword", "newPassword");
        UserDetails newJoe = manager.loadUserByUsername("joe");
        assertThat(newJoe.getPassword()).isEqualTo("newPassword");
        assertThat(cache.getUserMap().containsKey("joe")).isFalse();
    }

    @Test
    public void changePasswordSucceedsWithIfReAuthenticationSucceeds() {
        insertJoe();
        Authentication currentAuth = authenticateJoe();
        AuthenticationManager am = Mockito.mock(AuthenticationManager.class);
        Mockito.when(am.authenticate(currentAuth)).thenReturn(currentAuth);
        manager.setAuthenticationManager(am);
        manager.changePassword("password", "newPassword");
        UserDetails newJoe = manager.loadUserByUsername("joe");
        assertThat(newJoe.getPassword()).isEqualTo("newPassword");
        // The password in the context should also be altered
        Authentication newAuth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(newAuth.getName()).isEqualTo("joe");
        assertThat(newAuth.getDetails()).isEqualTo(currentAuth.getDetails());
        assertThat(newAuth.getCredentials()).isNull();
        assertThat(cache.getUserMap().containsKey("joe")).isFalse();
    }

    @Test
    public void changePasswordFailsIfReAuthenticationFails() {
        insertJoe();
        authenticateJoe();
        AuthenticationManager am = Mockito.mock(AuthenticationManager.class);
        Mockito.when(am.authenticate(ArgumentMatchers.any(Authentication.class))).thenThrow(new BadCredentialsException(""));
        manager.setAuthenticationManager(am);
        try {
            manager.changePassword("password", "newPassword");
            fail("Expected BadCredentialsException");
        } catch (BadCredentialsException expected) {
        }
        // Check password hasn't changed.
        UserDetails newJoe = manager.loadUserByUsername("joe");
        assertThat(newJoe.getPassword()).isEqualTo("password");
        assertThat(SecurityContextHolder.getContext().getAuthentication().getCredentials()).isEqualTo("password");
        assertThat(cache.getUserMap().containsKey("joe")).isTrue();
    }

    @Test
    public void findAllGroupsReturnsExpectedGroupNames() {
        List<String> groups = manager.findAllGroups();
        assertThat(groups).hasSize(4);
        Collections.sort(groups);
        assertThat(groups.get(0)).isEqualTo("GROUP_0");
        assertThat(groups.get(1)).isEqualTo("GROUP_1");
        assertThat(groups.get(2)).isEqualTo("GROUP_2");
        assertThat(groups.get(3)).isEqualTo("GROUP_3");
    }

    @Test
    public void findGroupMembersReturnsCorrectData() {
        List<String> groupMembers = manager.findUsersInGroup("GROUP_0");
        assertThat(groupMembers).hasSize(1);
        assertThat(groupMembers.get(0)).isEqualTo("jerry");
        groupMembers = manager.findUsersInGroup("GROUP_1");
        assertThat(groupMembers).hasSize(2);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void createGroupInsertsCorrectData() {
        manager.createGroup("TEST_GROUP", AuthorityUtils.createAuthorityList("ROLE_X", "ROLE_Y"));
        List roles = template.queryForList(("select ga.authority from groups g, group_authorities ga " + ("where ga.group_id = g.id " + "and g.group_name = 'TEST_GROUP'")));
        assertThat(roles).hasSize(2);
    }

    @Test
    public void deleteGroupRemovesData() throws Exception {
        manager.deleteGroup("GROUP_0");
        manager.deleteGroup("GROUP_1");
        manager.deleteGroup("GROUP_2");
        manager.deleteGroup("GROUP_3");
        assertThat(template.queryForList("select * from group_authorities")).isEmpty();
        assertThat(template.queryForList("select * from group_members")).isEmpty();
        assertThat(template.queryForList("select id from groups")).isEmpty();
    }

    @Test
    public void renameGroupIsSuccessful() throws Exception {
        manager.renameGroup("GROUP_0", "GROUP_X");
        assertThat(template.queryForObject("select id from groups where group_name = 'GROUP_X'", Integer.class)).isZero();
    }

    @Test
    public void addingGroupUserSetsCorrectData() throws Exception {
        manager.addUserToGroup("tom", "GROUP_0");
        assertThat(template.queryForList("select username from group_members where group_id = 0")).hasSize(2);
    }

    @Test
    public void removeUserFromGroupDeletesGroupMemberRow() throws Exception {
        manager.removeUserFromGroup("jerry", "GROUP_1");
        assertThat(template.queryForList("select group_id from group_members where username = 'jerry'")).hasSize(1);
    }

    @Test
    public void findGroupAuthoritiesReturnsCorrectAuthorities() throws Exception {
        assertThat(AuthorityUtils.createAuthorityList("ROLE_A")).isEqualTo(manager.findGroupAuthorities("GROUP_0"));
    }

    @Test
    public void addGroupAuthorityInsertsCorrectGroupAuthorityRow() throws Exception {
        GrantedAuthority auth = new SimpleGrantedAuthority("ROLE_X");
        manager.addGroupAuthority("GROUP_0", auth);
        template.queryForObject("select authority from group_authorities where authority = 'ROLE_X' and group_id = 0", String.class);
    }

    @Test
    public void deleteGroupAuthorityRemovesCorrectRows() throws Exception {
        GrantedAuthority auth = new SimpleGrantedAuthority("ROLE_A");
        manager.removeGroupAuthority("GROUP_0", auth);
        assertThat(template.queryForList("select authority from group_authorities where group_id = 0")).isEmpty();
        manager.removeGroupAuthority("GROUP_2", auth);
        assertThat(template.queryForList("select authority from group_authorities where group_id = 2")).hasSize(2);
    }

    // SEC-1156
    @Test
    public void createUserDoesNotSaveAuthoritiesIfEnableAuthoritiesIsFalse() throws Exception {
        manager.setEnableAuthorities(false);
        manager.createUser(JdbcUserDetailsManagerTests.joe);
        assertThat(template.queryForList(JdbcUserDetailsManagerTests.SELECT_JOE_AUTHORITIES_SQL)).isEmpty();
    }

    // SEC-1156
    @Test
    public void updateUserDoesNotSaveAuthoritiesIfEnableAuthoritiesIsFalse() throws Exception {
        manager.setEnableAuthorities(false);
        insertJoe();
        template.execute("delete from authorities where username='joe'");
        manager.updateUser(JdbcUserDetailsManagerTests.joe);
        assertThat(template.queryForList(JdbcUserDetailsManagerTests.SELECT_JOE_AUTHORITIES_SQL)).isEmpty();
    }

    // SEC-2166
    @Test
    public void createNewAuthenticationUsesNullPasswordToKeepPassordsSave() {
        insertJoe();
        UsernamePasswordAuthenticationToken currentAuth = new UsernamePasswordAuthenticationToken("joe", null, AuthorityUtils.createAuthorityList("ROLE_USER"));
        Authentication updatedAuth = manager.createNewAuthentication(currentAuth, "new");
        assertThat(updatedAuth.getCredentials()).isNull();
    }

    private class MockUserCache implements UserCache {
        private Map<String, UserDetails> cache = new HashMap<>();

        public UserDetails getUserFromCache(String username) {
            return ((User) (cache.get(username)));
        }

        public void putUserInCache(UserDetails user) {
            cache.put(user.getUsername(), user);
        }

        public void removeUserFromCache(String username) {
            cache.remove(username);
        }

        Map<String, UserDetails> getUserMap() {
            return cache;
        }
    }
}

