/**
 * The MIT License
 *
 * Copyright (c) 2004-2009, Sun Microsystems, Inc., Kohsuke Kawaguchi
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package hudson.search;


import Jenkins.READ;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import hudson.model.FreeStyleProject;
import hudson.model.ListView;
import hudson.model.User;
import hudson.security.ACL;
import hudson.security.ACLContext;
import hudson.security.GlobalMatrixAuthorizationStrategy;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import jenkins.model.Jenkins;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.Issue;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.JenkinsRule.WebClient;
import org.jvnet.hudson.test.MockAuthorizationStrategy;
import org.jvnet.hudson.test.MockFolder;


/**
 *
 *
 * @author Kohsuke Kawaguchi
 */
public class SearchTest {
    @Rule
    public JenkinsRule j = new JenkinsRule();

    /**
     * No exact match should result in a failure status code.
     */
    @Test
    public void testFailure() throws Exception {
        WebClient wc = j.createWebClient().withThrowExceptionOnFailingStatusCode(false);
        HtmlPage resultPage = wc.search("no-such-thing");
        Assert.assertEquals(HttpURLConnection.HTTP_NOT_FOUND, resultPage.getWebResponse().getStatusCode());
    }

    /**
     * Makes sure the script doesn't execute.
     */
    @Issue("JENKINS-3415")
    @Test
    public void testXSS() throws Exception {
        WebClient wc = j.createWebClient().withThrowExceptionOnFailingStatusCode(false);
        wc.setAlertHandler(( page, message) -> {
            throw new AssertionError();
        });
        HtmlPage resultPage = wc.search("<script>alert('script');</script>");
        Assert.assertEquals(HttpURLConnection.HTTP_NOT_FOUND, resultPage.getWebResponse().getStatusCode());
    }

    @Test
    public void testSearchByProjectName() throws Exception {
        final String projectName = "testSearchByProjectName";
        j.createFreeStyleProject(projectName);
        Page result = j.search(projectName);
        Assert.assertNotNull(result);
        j.assertGoodStatus(result);
        // make sure we've fetched the testSearchByDisplayName project page
        String contents = result.getWebResponse().getContentAsString();
        Assert.assertTrue(contents.contains(String.format("<title>%s [Jenkins]</title>", projectName)));
    }

    @Issue("JENKINS-24433")
    @Test
    public void testSearchByProjectNameBehindAFolder() throws Exception {
        FreeStyleProject myFreeStyleProject = j.createFreeStyleProject("testSearchByProjectName");
        MockFolder myMockFolder = j.createFolder("my-folder-1");
        Page result = j.createWebClient().goTo((((myMockFolder.getUrl()) + "search?q=") + (myFreeStyleProject.getName())));
        Assert.assertNotNull(result);
        j.assertGoodStatus(result);
        URL resultUrl = result.getUrl();
        Assert.assertTrue(resultUrl.toString().equals(((j.getInstance().getRootUrl()) + (myFreeStyleProject.getUrl()))));
    }

    @Issue("JENKINS-24433")
    @Test
    public void testSearchByProjectNameInAFolder() throws Exception {
        MockFolder myMockFolder = j.createFolder("my-folder-1");
        FreeStyleProject myFreeStyleProject = myMockFolder.createProject(FreeStyleProject.class, "my-job-1");
        Page result = j.createWebClient().goTo((((myMockFolder.getUrl()) + "search?q=") + (myFreeStyleProject.getFullName())));
        Assert.assertNotNull(result);
        j.assertGoodStatus(result);
        URL resultUrl = result.getUrl();
        Assert.assertTrue(resultUrl.toString().equals(((j.getInstance().getRootUrl()) + (myFreeStyleProject.getUrl()))));
    }

    @Test
    public void testSearchByDisplayName() throws Exception {
        final String displayName = "displayName9999999";
        FreeStyleProject project = j.createFreeStyleProject("testSearchByDisplayName");
        project.setDisplayName(displayName);
        Page result = j.search(displayName);
        Assert.assertNotNull(result);
        j.assertGoodStatus(result);
        // make sure we've fetched the testSearchByDisplayName project page
        String contents = result.getWebResponse().getContentAsString();
        Assert.assertTrue(contents.contains(String.format("<title>%s [Jenkins]</title>", displayName)));
    }

    @Test
    public void testSearch2ProjectsWithSameDisplayName() throws Exception {
        // create 2 freestyle projects with the same display name
        final String projectName1 = "projectName1";
        final String projectName2 = "projectName2";
        final String projectName3 = "projectName3";
        final String displayName = "displayNameFoo";
        final String otherDisplayName = "otherDisplayName";
        FreeStyleProject project1 = j.createFreeStyleProject(projectName1);
        project1.setDisplayName(displayName);
        FreeStyleProject project2 = j.createFreeStyleProject(projectName2);
        project2.setDisplayName(displayName);
        FreeStyleProject project3 = j.createFreeStyleProject(projectName3);
        project3.setDisplayName(otherDisplayName);
        // make sure that on search we get back one of the projects, it doesn't
        // matter which one as long as the one that is returned has displayName
        // as the display name
        Page result = j.search(displayName);
        Assert.assertNotNull(result);
        j.assertGoodStatus(result);
        // make sure we've fetched the testSearchByDisplayName project page
        String contents = result.getWebResponse().getContentAsString();
        Assert.assertTrue(contents.contains(String.format("<title>%s [Jenkins]</title>", displayName)));
        Assert.assertFalse(contents.contains(otherDisplayName));
    }

    @Test
    public void testProjectNamePrecedesDisplayName() throws Exception {
        final String project1Name = "foo";
        final String project1DisplayName = "project1DisplayName";
        final String project2Name = "project2Name";
        final String project2DisplayName = project1Name;
        final String project3Name = "project3Name";
        final String project3DisplayName = "project3DisplayName";
        // create 1 freestyle project with the name foo
        FreeStyleProject project1 = j.createFreeStyleProject(project1Name);
        project1.setDisplayName(project1DisplayName);
        // create another with the display name foo
        FreeStyleProject project2 = j.createFreeStyleProject(project2Name);
        project2.setDisplayName(project2DisplayName);
        // create a third project and make sure it's not picked up by search
        FreeStyleProject project3 = j.createFreeStyleProject(project3Name);
        project3.setDisplayName(project3DisplayName);
        // search for foo
        Page result = j.search(project1Name);
        Assert.assertNotNull(result);
        j.assertGoodStatus(result);
        // make sure we get the project with the name foo
        String contents = result.getWebResponse().getContentAsString();
        Assert.assertTrue(contents.contains(String.format("<title>%s [Jenkins]</title>", project1DisplayName)));
        // make sure projects 2 and 3 were not picked up
        Assert.assertFalse(contents.contains(project2Name));
        Assert.assertFalse(contents.contains(project3Name));
        Assert.assertFalse(contents.contains(project3DisplayName));
    }

    @Test
    public void testGetSuggestionsHasBothNamesAndDisplayNames() throws Exception {
        final String projectName = "project name";
        final String displayName = "display name";
        FreeStyleProject project1 = j.createFreeStyleProject(projectName);
        project1.setDisplayName(displayName);
        WebClient wc = j.createWebClient();
        Page result = wc.goTo("search/suggest?query=name", "application/json");
        Assert.assertNotNull(result);
        j.assertGoodStatus(result);
        String content = result.getWebResponse().getContentAsString();
        System.out.println(content);
        JSONObject jsonContent = ((JSONObject) (JSONSerializer.toJSON(content)));
        Assert.assertNotNull(jsonContent);
        JSONArray jsonArray = jsonContent.getJSONArray("suggestions");
        Assert.assertNotNull(jsonArray);
        Assert.assertEquals(2, jsonArray.size());
        boolean foundProjectName = false;
        boolean foundDisplayName = false;
        for (Object suggestion : jsonArray) {
            JSONObject jsonSuggestion = ((JSONObject) (suggestion));
            String name = ((String) (jsonSuggestion.get("name")));
            if (projectName.equals(name)) {
                foundProjectName = true;
            } else
                if (displayName.equals(name)) {
                    foundDisplayName = true;
                }

        }
        Assert.assertTrue(foundProjectName);
        Assert.assertTrue(foundDisplayName);
    }

    @Issue("JENKINS-24433")
    @Test
    public void testProjectNameBehindAFolderDisplayName() throws Exception {
        final String projectName1 = "job-1";
        final String displayName1 = "job-1 display";
        final String projectName2 = "job-2";
        final String displayName2 = "job-2 display";
        FreeStyleProject project1 = j.createFreeStyleProject(projectName1);
        project1.setDisplayName(displayName1);
        MockFolder myMockFolder = j.createFolder("my-folder-1");
        FreeStyleProject project2 = myMockFolder.createProject(FreeStyleProject.class, projectName2);
        project2.setDisplayName(displayName2);
        WebClient wc = j.createWebClient();
        Page result = wc.goTo((((myMockFolder.getUrl()) + "search/suggest?query=") + projectName1), "application/json");
        Assert.assertNotNull(result);
        j.assertGoodStatus(result);
        String content = result.getWebResponse().getContentAsString();
        JSONObject jsonContent = ((JSONObject) (JSONSerializer.toJSON(content)));
        Assert.assertNotNull(jsonContent);
        JSONArray jsonArray = jsonContent.getJSONArray("suggestions");
        Assert.assertNotNull(jsonArray);
        Assert.assertEquals(2, jsonArray.size());
        boolean foundDisplayName = false;
        for (Object suggestion : jsonArray) {
            JSONObject jsonSuggestion = ((JSONObject) (suggestion));
            String name = ((String) (jsonSuggestion.get("name")));
            if (projectName1.equals(name)) {
                foundDisplayName = true;
            }
        }
        Assert.assertTrue(foundDisplayName);
    }

    @Issue("JENKINS-24433")
    @Test
    public void testProjectNameInAFolderDisplayName() throws Exception {
        final String projectName1 = "job-1";
        final String displayName1 = "job-1 display";
        final String projectName2 = "job-2";
        final String displayName2 = "my-folder-1 job-2";
        FreeStyleProject project1 = j.createFreeStyleProject(projectName1);
        project1.setDisplayName(displayName1);
        MockFolder myMockFolder = j.createFolder("my-folder-1");
        FreeStyleProject project2 = myMockFolder.createProject(FreeStyleProject.class, projectName2);
        project2.setDisplayName(displayName2);
        WebClient wc = j.createWebClient();
        Page result = wc.goTo((((myMockFolder.getUrl()) + "search/suggest?query=") + projectName2), "application/json");
        Assert.assertNotNull(result);
        j.assertGoodStatus(result);
        String content = result.getWebResponse().getContentAsString();
        JSONObject jsonContent = ((JSONObject) (JSONSerializer.toJSON(content)));
        Assert.assertNotNull(jsonContent);
        JSONArray jsonArray = jsonContent.getJSONArray("suggestions");
        Assert.assertNotNull(jsonArray);
        Assert.assertEquals(1, jsonArray.size());
        boolean foundDisplayName = false;
        for (Object suggestion : jsonArray) {
            JSONObject jsonSuggestion = ((JSONObject) (suggestion));
            String name = ((String) (jsonSuggestion.get("name")));
            if (displayName2.equals(name)) {
                foundDisplayName = true;
            }
        }
        Assert.assertTrue(foundDisplayName);
    }

    /**
     * Disable/enable status shouldn't affect the search
     */
    @Issue("JENKINS-13148")
    @Test
    public void testDisabledJobShouldBeSearchable() throws Exception {
        FreeStyleProject p = j.createFreeStyleProject("foo-bar");
        Assert.assertTrue(suggest(j.jenkins.getSearchIndex(), "foo").contains(p));
        p.disable();
        Assert.assertTrue(suggest(j.jenkins.getSearchIndex(), "foo").contains(p));
    }

    /**
     * All top-level jobs should be searchable, not just jobs in the current view.
     */
    @Issue("JENKINS-13148")
    @Test
    public void testCompletionOutsideView() throws Exception {
        FreeStyleProject p = j.createFreeStyleProject("foo-bar");
        ListView v = new ListView("empty1", j.jenkins);
        ListView w = new ListView("empty2", j.jenkins);
        j.jenkins.addView(v);
        j.jenkins.addView(w);
        j.jenkins.setPrimaryView(w);
        // new view should be empty
        Assert.assertFalse(v.contains(p));
        Assert.assertFalse(w.contains(p));
        Assert.assertFalse(j.jenkins.getPrimaryView().contains(p));
        Assert.assertTrue(suggest(j.jenkins.getSearchIndex(), "foo").contains(p));
    }

    @Issue("SECURITY-385")
    @Test
    public void testInaccessibleViews() throws IOException {
        j.jenkins.setSecurityRealm(j.createDummySecurityRealm());
        GlobalMatrixAuthorizationStrategy strategy = new GlobalMatrixAuthorizationStrategy();
        strategy.add(READ, "alice");
        j.jenkins.setAuthorizationStrategy(strategy);
        j.jenkins.addView(new ListView("foo", j.jenkins));
        // SYSTEM can see all the views
        Assert.assertEquals("two views exist", 2, Jenkins.getInstance().getViews().size());
        List<SearchItem> results = new ArrayList<>();
        j.jenkins.getSearchIndex().suggest("foo", results);
        Assert.assertEquals("nonempty results list", 1, results.size());
        // Alice can't
        Assert.assertFalse("no permission", j.jenkins.getView("foo").hasPermission(User.get("alice").impersonate(), View.READ));
        ACL.impersonate(User.get("alice").impersonate(), new Runnable() {
            @Override
            public void run() {
                Assert.assertEquals("no visible views", 0, Jenkins.getInstance().getViews().size());
                List<SearchItem> results = new ArrayList<>();
                j.jenkins.getSearchIndex().suggest("foo", results);
                Assert.assertEquals("empty results list", Collections.emptyList(), results);
            }
        });
    }

    @Test
    public void testSearchWithinFolders() throws Exception {
        MockFolder folder1 = j.createFolder("folder1");
        FreeStyleProject p1 = folder1.createProject(FreeStyleProject.class, "myjob");
        MockFolder folder2 = j.createFolder("folder2");
        FreeStyleProject p2 = folder2.createProject(FreeStyleProject.class, "myjob");
        List<SearchItem> suggest = suggest(j.jenkins.getSearchIndex(), "myjob");
        Assert.assertTrue(suggest.contains(p1));
        Assert.assertTrue(suggest.contains(p2));
    }

    @Test
    @Issue("JENKINS-7874")
    public void adminOnlyLinksNotShownToRegularUser() {
        j.jenkins.setSecurityRealm(j.createDummySecurityRealm());
        MockAuthorizationStrategy mas = new MockAuthorizationStrategy();
        mas.grant(READ).onRoot().toEveryone();
        j.jenkins.setAuthorizationStrategy(mas);
        try (ACLContext acl = ACL.as(User.get("alice"))) {
            List<SearchItem> results = new ArrayList<>();
            j.jenkins.getSearchIndex().find("config", results);
            j.jenkins.getSearchIndex().find("manage", results);
            j.jenkins.getSearchIndex().find("log", results);
            Assert.assertEquals("empty results list", 0, results.size());
        }
    }

    @Issue("JENKINS-35459")
    @Test
    public void testProjectNameInAListView() throws Exception {
        MockFolder myMockFolder = j.createFolder("folder");
        FreeStyleProject freeStyleProject = myMockFolder.createProject(FreeStyleProject.class, "myJob");
        ListView listView = new ListView("ListView", j.jenkins);
        listView.setRecurse(true);
        listView.add(myMockFolder);
        listView.add(freeStyleProject);
        j.jenkins.addView(listView);
        j.jenkins.setPrimaryView(listView);
        Assert.assertEquals(2, j.jenkins.getPrimaryView().getAllItems().size());
        WebClient wc = j.createWebClient();
        Page result = wc.goTo(("search/suggest?query=" + (freeStyleProject.getName())), "application/json");
        Assert.assertNotNull(result);
        j.assertGoodStatus(result);
        String content = result.getWebResponse().getContentAsString();
        JSONObject jsonContent = ((JSONObject) (JSONSerializer.toJSON(content)));
        Assert.assertNotNull(jsonContent);
        JSONArray jsonArray = jsonContent.getJSONArray("suggestions");
        Assert.assertNotNull(jsonArray);
        Assert.assertEquals(2, jsonArray.size());
        Page searchResult = wc.goTo(((("search?q=" + (myMockFolder.getName())) + "%2F") + (freeStyleProject.getName())));
        Assert.assertNotNull(searchResult);
        j.assertGoodStatus(searchResult);
        URL resultUrl = searchResult.getUrl();
        Assert.assertTrue(resultUrl.toString().equals(((j.getInstance().getRootUrl()) + (freeStyleProject.getUrl()))));
    }
}

