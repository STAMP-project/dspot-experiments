/**
 * The MIT License
 *
 * Copyright (c) 2015, CloudBees, Inc.
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
package hudson;


import JenkinsRule.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlElementUtil;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.TestPluginManager;
import org.xml.sax.SAXException;


/**
 *
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class PluginManagerInstalledGUITest {
    @Rule
    public JenkinsRule jenkinsRule = new JenkinsRule() {
        @Override
        public PluginManager getPluginManager() {
            try {
                return new TestPluginManager() {
                    @Override
                    protected Collection<String> loadBundledPlugins() throws Exception {
                        try {
                            return super.loadBundledPlugins();
                        } finally {
                            copyBundledPlugin(PluginManagerInstalledGUITest.class.getResource("/WEB-INF/detached-plugins/cvs.hpi"), "cvs.jpi");// cannot use installDetachedPlugin at this point

                            copyBundledPlugin(PluginManagerInstalledGUITest.class.getResource("/plugins/tasks.jpi"), "tasks.jpi");
                        }
                    }
                };
            } catch (IOException e) {
                Assert.fail(e.getMessage());
                return null;
            }
        }
    };

    @Test
    public void test_enable_disable_uninstall() throws IOException, SAXException {
        PluginManagerInstalledGUITest.InstalledPlugins installedPlugins = new PluginManagerInstalledGUITest.InstalledPlugins();
        PluginManagerInstalledGUITest.InstalledPlugin tasksPlugin = installedPlugins.get("tasks");
        PluginManagerInstalledGUITest.InstalledPlugin cvsPlugin = installedPlugins.get("cvs");
        tasksPlugin.assertHasNoDependents();
        cvsPlugin.assertHasDependents();
        // Tasks plugin should be enabled and it should be possible to disable it
        // because no other plugins depend on it.
        tasksPlugin.assertEnabled();
        tasksPlugin.assertEnabledStateChangeable();
        tasksPlugin.assertUninstallable();
        // CVS plugin should be enabled, but it should not be possible to disable or uninstall it
        // because the tasks plugin depends on it.
        cvsPlugin.assertEnabled();
        cvsPlugin.assertEnabledStateNotChangeable();
        cvsPlugin.assertNotUninstallable();
        // Disable the tasks plugin
        tasksPlugin.clickEnabledWidget();
        // Now the tasks plugin should be disabled, but it should be possible to re-enable it
        // and it should still be uninstallable.
        tasksPlugin.assertNotEnabled();// this is different to earlier

        tasksPlugin.assertEnabledStateChangeable();
        tasksPlugin.assertUninstallable();
        // The CVS plugin should still be enabled, but it should now be possible to disable it because
        // the tasks plugin is no longer enabled. Should still not be possible to uninstall it.
        cvsPlugin.assertEnabled();
        cvsPlugin.assertEnabledStateChangeable();// this is different to earlier

        cvsPlugin.assertNotUninstallable();
        // Disable the cvs plugin
        cvsPlugin.clickEnabledWidget();
        // Now it should NOT be possible to change the enable state of the tasks plugin because one
        // of the plugins it depends on (the CVS plugin) is not enabled.
        tasksPlugin.assertNotEnabled();
        tasksPlugin.assertEnabledStateNotChangeable();// this is different to earlier

        tasksPlugin.assertUninstallable();
    }

    private class InstalledPlugins {
        private final List<PluginManagerInstalledGUITest.InstalledPlugin> installedPlugins;

        private InstalledPlugins() throws IOException, SAXException {
            JenkinsRule.WebClient webClient = jenkinsRule.createWebClient();
            HtmlPage installedPage = webClient.goTo("pluginManager/installed");
            // Note for debugging... simply print installedPage to get the JenkinsRule
            // Jenkins URL and then add a long Thread.sleep here. It's useful re being
            // able to see what the code is testing.
            DomElement pluginsTable = installedPage.getElementById("plugins");
            HtmlElement tbody = pluginsTable.getElementsByTagName("TBODY").get(0);
            installedPlugins = new ArrayList<>();
            for (DomElement htmlTableRow : tbody.getChildElements()) {
                installedPlugins.add(new PluginManagerInstalledGUITest.InstalledPlugin(((HtmlTableRow) (htmlTableRow))));
            }
        }

        public PluginManagerInstalledGUITest.InstalledPlugin get(String pluginId) {
            for (PluginManagerInstalledGUITest.InstalledPlugin plugin : installedPlugins) {
                if (plugin.isPlugin(pluginId)) {
                    return plugin;
                }
            }
            Assert.fail(("Now pluginManager/installed row for plugin " + pluginId));
            return null;
        }
    }

    private class InstalledPlugin {
        private final HtmlTableRow pluginRow;

        public InstalledPlugin(HtmlTableRow pluginRow) {
            this.pluginRow = pluginRow;
        }

        public String getId() {
            return pluginRow.getAttribute("data-plugin-id");
        }

        public boolean isPlugin(String pluginId) {
            return pluginId.equals(getId());
        }

        private HtmlInput getEnableWidget() {
            HtmlElement input = pluginRow.getCells().get(0).getElementsByTagName("input").get(0);
            return ((HtmlInput) (input));
        }

        public void assertEnabled() {
            HtmlInput enableWidget = getEnableWidget();
            Assert.assertTrue((("Plugin '" + (getId())) + "' is expected to be enabled."), enableWidget.isChecked());
        }

        public void assertNotEnabled() {
            HtmlInput enableWidget = getEnableWidget();
            Assert.assertFalse((("Plugin '" + (getId())) + "' is not expected to be enabled."), enableWidget.isChecked());
        }

        public void clickEnabledWidget() throws IOException {
            HtmlInput enableWidget = getEnableWidget();
            HtmlElementUtil.click(enableWidget);
        }

        public void assertEnabledStateChangeable() {
            if (((!(hasDependents())) && (!(hasDisabledDependency()))) && (!(allDependentsDisabled()))) {
                return;
            }
            if ((allDependentsDisabled()) && (!(hasDisabledDependency()))) {
                return;
            }
            Assert.fail((("The enable/disable state of plugin '" + (getId())) + "' cannot be changed."));
        }

        public void assertEnabledStateNotChangeable() {
            if (((hasDependents()) && (!(hasDisabledDependency()))) && (!(allDependentsDisabled()))) {
                return;
            }
            if ((!(hasDependents())) && (hasDisabledDependency())) {
                return;
            }
            Assert.fail((("The enable/disable state of plugin '" + (getId())) + "' cannot be changed."));
        }

        public void assertUninstallable() {
            Assert.assertFalse((("Plugin '" + (getId())) + "' cannot be uninstalled."), hasDependents());
        }

        public void assertNotUninstallable() {
            Assert.assertTrue((("Plugin '" + (getId())) + "' can be uninstalled."), hasDependents());
        }

        public void assertHasDependents() {
            Assert.assertTrue(hasDependents());
        }

        public void assertHasNoDependents() {
            Assert.assertFalse(hasDependents());
        }

        private boolean hasClassName(String className) {
            String classAttribute = pluginRow.getAttribute("class");
            Set<String> classes = new HashSet<>(Arrays.asList(classAttribute.split(" ")));
            return classes.contains(className);
        }

        private boolean hasDisabledDependency() {
            return hasClassName("has-disabled-dependency");
        }

        private boolean allDependentsDisabled() {
            return hasClassName("all-dependents-disabled");
        }

        private boolean hasDependents() {
            return hasClassName("has-dependents");
        }
    }
}

