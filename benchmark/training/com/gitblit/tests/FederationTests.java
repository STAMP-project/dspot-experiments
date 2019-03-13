/**
 * Copyright 2011 gitblit.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.gitblit.tests;


import FederationProposalResult.NO_PROPOSALS;
import FederationRequest.PULL_REPOSITORIES;
import FederationRequest.PULL_TEAMS;
import FederationRequest.PULL_USERS;
import com.gitblit.Constants.AccessRestrictionType;
import com.gitblit.Constants.FederationToken;
import com.gitblit.models.FederationProposal;
import com.gitblit.models.RepositoryModel;
import com.gitblit.models.TeamModel;
import com.gitblit.models.UserModel;
import com.gitblit.utils.FederationUtils;
import com.gitblit.utils.JsonUtils;
import com.gitblit.utils.RpcUtils;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Assert;
import org.junit.Test;


public class FederationTests extends GitblitUnitTest {
    String url = GitBlitSuite.url;

    String account = GitBlitSuite.account;

    String password = GitBlitSuite.password;

    String token = "d7cc58921a80b37e0329a4dae2f9af38bf61ef5c";

    private static final AtomicBoolean started = new AtomicBoolean(false);

    @Test
    public void testProposal() throws Exception {
        // create dummy repository data
        Map<String, RepositoryModel> repositories = new HashMap<String, RepositoryModel>();
        for (int i = 0; i < 5; i++) {
            RepositoryModel model = new RepositoryModel();
            model.accessRestriction = AccessRestrictionType.VIEW;
            model.description = "cloneable repository " + i;
            model.lastChange = new Date();
            model.addOwner("adminuser");
            model.name = ("repo" + i) + ".git";
            model.size = "5 MB";
            model.hasCommits = true;
            repositories.put(model.name, model);
        }
        FederationProposal proposal = new FederationProposal("http://testurl", FederationToken.ALL, "testtoken", repositories);
        // propose federation
        Assert.assertEquals("proposal refused", FederationUtils.propose(url, proposal), NO_PROPOSALS);
    }

    @Test
    public void testJsonRepositories() throws Exception {
        String requrl = FederationUtils.asLink(url, token, PULL_REPOSITORIES);
        String json = JsonUtils.retrieveJsonString(requrl, null, null);
        Assert.assertNotNull(json);
    }

    @Test
    public void testJsonUsers() throws Exception {
        String requrl = FederationUtils.asLink(url, token, PULL_USERS);
        String json = JsonUtils.retrieveJsonString(requrl, null, null);
        Assert.assertNotNull(json);
    }

    @Test
    public void testJsonTeams() throws Exception {
        String requrl = FederationUtils.asLink(url, token, PULL_TEAMS);
        String json = JsonUtils.retrieveJsonString(requrl, null, null);
        Assert.assertNotNull(json);
    }

    @Test
    public void testPullRepositories() throws Exception {
        Map<String, RepositoryModel> repos = FederationUtils.getRepositories(getRegistration(), false);
        Assert.assertNotNull(repos);
        Assert.assertTrue(((repos.size()) > 0));
    }

    @Test
    public void testPullUsers() throws Exception {
        List<UserModel> users = FederationUtils.getUsers(getRegistration());
        Assert.assertNotNull(users);
        // admin is excluded
        Assert.assertEquals(0, users.size());
        UserModel newUser = new UserModel("test");
        newUser.password = "whocares";
        Assert.assertTrue(RpcUtils.createUser(newUser, url, account, password.toCharArray()));
        TeamModel team = new TeamModel("testteam");
        team.addUser("test");
        team.addRepositoryPermission("helloworld.git");
        Assert.assertTrue(RpcUtils.createTeam(team, url, account, password.toCharArray()));
        users = FederationUtils.getUsers(getRegistration());
        Assert.assertNotNull(users);
        Assert.assertEquals(1, users.size());
        newUser = users.get(0);
        Assert.assertTrue(newUser.isTeamMember("testteam"));
        Assert.assertTrue(RpcUtils.deleteUser(newUser, url, account, password.toCharArray()));
        Assert.assertTrue(RpcUtils.deleteTeam(team, url, account, password.toCharArray()));
    }

    @Test
    public void testPullTeams() throws Exception {
        TeamModel team = new TeamModel("testteam");
        team.addUser("test");
        team.addRepositoryPermission("helloworld.git");
        Assert.assertTrue(RpcUtils.createTeam(team, url, account, password.toCharArray()));
        List<TeamModel> teams = FederationUtils.getTeams(getRegistration());
        Assert.assertNotNull(teams);
        Assert.assertTrue(((teams.size()) > 0));
        Assert.assertTrue(RpcUtils.deleteTeam(team, url, account, password.toCharArray()));
    }

    @Test
    public void testPullScripts() throws Exception {
        Map<String, String> scripts = FederationUtils.getScripts(getRegistration());
        Assert.assertNotNull(scripts);
        Assert.assertTrue(scripts.keySet().contains("sendmail"));
    }
}

