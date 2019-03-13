/**
 * The MIT License
 *
 * Copyright 2012 Jesse Glick.
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
package hudson.model;


import UpdateSite.Plugin;
import hudson.model.UpdateSite.Data;
import hudson.util.FormValidation;
import hudson.util.PersistedList;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import jenkins.security.UpdateSiteWarningsConfiguration;
import jenkins.security.UpdateSiteWarningsMonitor;
import org.eclipse.jetty.server.Server;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.Issue;
import org.jvnet.hudson.test.JenkinsRule;

import static UpdateCenter.ID_DEFAULT;
import static UpdateCenter.PREDEFINED_UPDATE_SITE_ID;


public class UpdateSiteTest {
    @Rule
    public JenkinsRule j = new JenkinsRule();

    private final String RELATIVE_BASE = "/_relative/";

    private Server server;

    private URL baseUrl;

    @Test
    public void relativeURLs() throws Exception {
        PersistedList<UpdateSite> sites = j.jenkins.getUpdateCenter().getSites();
        sites.clear();
        URL url = new URL(baseUrl, "/plugins/tasks-update-center.json");
        UpdateSite site = new UpdateSite(ID_DEFAULT, url.toString());
        sites.add(site);
        Assert.assertEquals(FormValidation.ok(), site.updateDirectly(false).get());
        Data data = site.getData();
        Assert.assertNotNull(data);
        Assert.assertEquals(new URL(url, "jenkins.war").toString(), data.core.url);
        Assert.assertEquals(new HashSet<String>(Arrays.asList("tasks", "dummy")), data.plugins.keySet());
        Assert.assertEquals(new URL(url, "tasks.jpi").toString(), data.plugins.get("tasks").url);
        Assert.assertEquals("http://nowhere.net/dummy.hpi", data.plugins.get("dummy").url);
        UpdateSite.Plugin tasksPlugin = data.plugins.get("tasks");
        Assert.assertEquals("Wrong name of plugin found", "Task Scanner Plug-in", tasksPlugin.getDisplayName());
    }

    @Test
    public void updateDirectlyWithJson() throws Exception {
        UpdateSite us = new UpdateSite("default", new URL(baseUrl, "update-center.json").toExternalForm());
        Assert.assertNull(us.getPlugin("AdaptivePlugin"));
        Assert.assertEquals(FormValidation.ok(), /* TODO the certificate is now expired, and downloading a fresh copy did not seem to help */
        us.updateDirectly(false).get());
        Assert.assertNotNull(us.getPlugin("AdaptivePlugin"));
    }

    @Test
    public void lackOfDataDoesNotFailWarningsCode() throws Exception {
        Assert.assertNull("plugin data is not present", j.jenkins.getUpdateCenter().getSite("default").getData());
        // nothing breaking?
        j.jenkins.getExtensionList(UpdateSiteWarningsMonitor.class).get(0).getActivePluginWarningsByPlugin();
        j.jenkins.getExtensionList(UpdateSiteWarningsMonitor.class).get(0).getActiveCoreWarnings();
        j.jenkins.getExtensionList(UpdateSiteWarningsConfiguration.class).get(0).getAllWarnings();
    }

    @Test
    public void incompleteWarningsJson() throws Exception {
        PersistedList<UpdateSite> sites = j.jenkins.getUpdateCenter().getSites();
        sites.clear();
        URL url = new URL(baseUrl, "/plugins/warnings-update-center-malformed.json");
        UpdateSite site = new UpdateSite(ID_DEFAULT, url.toString());
        sites.add(site);
        Assert.assertEquals(FormValidation.ok(), site.updateDirectly(false).get());
        Assert.assertEquals("number of warnings", 7, site.getData().getWarnings().size());
        Assert.assertNotEquals("plugin data is present", Collections.emptyMap(), site.getData().plugins);
    }

    @Issue("JENKINS-55048")
    @Test
    public void minimumJavaVersion() throws Exception {
        // TODO: factor out the sites init
        PersistedList<UpdateSite> sites = j.jenkins.getUpdateCenter().getSites();
        sites.clear();
        URL url = new URL(baseUrl, "/plugins/minJavaVersion-update-center.json");
        UpdateSite site = new UpdateSite(ID_DEFAULT, url.toString());
        sites.add(site);
        Assert.assertEquals(FormValidation.ok(), site.updateDirectly(false).get());
        // END TODO
        final UpdateSite.Plugin tasksPlugin = site.getPlugin("tasks");
        Assert.assertNotNull(tasksPlugin);
        Assert.assertFalse(tasksPlugin.isNeededDependenciesForNewerJava());
        Assert.assertFalse(tasksPlugin.isForNewerJava());
        final UpdateSite.Plugin pluginCompiledForTooRecentJava = site.getPlugin("java-too-recent");
        Assert.assertFalse(pluginCompiledForTooRecentJava.isNeededDependenciesForNewerJava());
        Assert.assertTrue(pluginCompiledForTooRecentJava.isForNewerJava());
        final UpdateSite.Plugin pluginDependingOnPluginCompiledForTooRecentJava = site.getPlugin("depending-on-too-recent-java");
        Assert.assertTrue(pluginDependingOnPluginCompiledForTooRecentJava.isNeededDependenciesForNewerJava());
        Assert.assertFalse(pluginDependingOnPluginCompiledForTooRecentJava.isForNewerJava());
    }

    @Issue("JENKINS-31448")
    @Test
    public void isLegacyDefault() throws Exception {
        Assert.assertFalse("isLegacyDefault should be false with null id", new UpdateSite(null, "url").isLegacyDefault());
        Assert.assertFalse("isLegacyDefault should be false when id is not default and url is http://hudson-ci.org/", new UpdateSite("dummy", "http://hudson-ci.org/").isLegacyDefault());
        Assert.assertTrue("isLegacyDefault should be true when id is default and url is http://hudson-ci.org/", new UpdateSite(PREDEFINED_UPDATE_SITE_ID, "http://hudson-ci.org/").isLegacyDefault());
        Assert.assertTrue("isLegacyDefault should be true when url is http://updates.hudson-labs.org/", new UpdateSite("dummy", "http://updates.hudson-labs.org/").isLegacyDefault());
        Assert.assertFalse("isLegacyDefault should be false with null url", new UpdateSite(null, null).isLegacyDefault());
    }
}

