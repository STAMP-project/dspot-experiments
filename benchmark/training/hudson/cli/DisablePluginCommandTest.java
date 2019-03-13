/**
 * The MIT License
 *
 * Copyright (c) 2018 CloudBees, Inc.
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
package hudson.cli;


import CLICommandInvoker.Result;
import PluginWrapper.PluginDisableStatus.ALREADY_DISABLED;
import PluginWrapper.PluginDisableStatus.DISABLED;
import PluginWrapper.PluginDisableStatus.NOT_DISABLED_DEPENDANTS;
import PluginWrapper.PluginDisableStatus.NO_SUCH_PLUGIN;
import java.io.IOException;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.Issue;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.recipes.WithPlugin;

import static Matcher.failedWith;
import static Matcher.succeeded;


public class DisablePluginCommandTest {
    @Rule
    public JenkinsRule j = new JenkinsRule();

    /**
     * Can disable a plugin with an optional dependent plugin.
     * With strategy none.
     */
    @Test
    @Issue("JENKINS-27177")
    @WithPlugin({ "depender-0.0.2.hpi", "dependee-0.0.2.hpi" })
    public void canDisablePluginWithOptionalDependerStrategyNone() {
        Assert.assertThat(disablePluginsCLiCommand("-strategy", "NONE", "dependee"), succeeded());
        assertPluginDisabled("dependee");
    }

    @Test
    @Issue("JENKINS-27177")
    @WithPlugin({ "depender-0.0.2.hpi", "dependee-0.0.2.hpi", "mandatory-depender-0.0.2.hpi" })
    public void canDisablePluginWithDependentsDisabledStrategyNone() throws IOException {
        disablePlugin("mandatory-depender");
        CLICommandInvoker.Result result = disablePluginsCLiCommand("-strategy", "NONE", "dependee");
        Assert.assertThat(result, succeeded());
        Assert.assertEquals("Disabling only dependee", 1, StringUtils.countMatches(result.stdout(), "Disabling"));
        assertPluginDisabled("dependee");
    }

    /**
     * Can't disable a plugin with a mandatory dependent plugin.
     * With default strategy (none).
     */
    @Test
    @Issue("JENKINS-27177")
    @WithPlugin({ "mandatory-depender-0.0.2.hpi", "dependee-0.0.2.hpi" })
    public void cannotDisablePluginWithMandatoryDependerStrategyNone() {
        Assert.assertThat(disablePluginsCLiCommand("dependee"), failedWith(DisablePluginCommand.RETURN_CODE_NOT_DISABLED_DEPENDANTS));
        assertPluginEnabled("dependee");
    }

    /**
     * Can't disable a plugin with a mandatory dependent plugin before its dependent plugin.
     * With default strategy (none).
     */
    @Test
    @Issue("JENKINS-27177")
    @WithPlugin({ "mandatory-depender-0.0.2.hpi", "dependee-0.0.2.hpi" })
    public void cannotDisableDependentPluginWrongOrderStrategyNone() {
        Assert.assertThat(disablePluginsCLiCommand("dependee", "mandatory-depender"), failedWith(DisablePluginCommand.RETURN_CODE_NOT_DISABLED_DEPENDANTS));
        assertPluginDisabled("mandatory-depender");
        assertPluginEnabled("dependee");
    }

    /**
     * Can disable a plugin with a mandatory dependent plugin before its dependent plugin with <i><all/i> strategy
     */
    @Test
    @Issue("JENKINS-27177")
    @WithPlugin({ "mandatory-depender-0.0.2.hpi", "dependee-0.0.2.hpi" })
    public void canDisableDependentPluginWrongOrderStrategyAll() {
        Assert.assertThat(disablePluginsCLiCommand("dependee", "mandatory-depender", "-strategy", "all"), succeeded());
        assertPluginDisabled("mandatory-depender");
        assertPluginDisabled("dependee");
    }

    /**
     * Can disable a plugin with a mandatory dependent plugin after being disabled the mandatory dependent plugin. With
     * default strategy (none).
     */
    @Test
    @Issue("JENKINS-27177")
    @WithPlugin({ "mandatory-depender-0.0.2.hpi", "dependee-0.0.2.hpi" })
    public void canDisableDependentPluginsRightOrderStrategyNone() {
        Assert.assertThat(disablePluginsCLiCommand("mandatory-depender", "dependee"), succeeded());
        assertPluginDisabled("dependee");
        assertPluginDisabled("mandatory-depender");
    }

    /**
     * Can disable a plugin without dependents plugins and Jenkins doesn't restart after it if -restart is not passed.
     */
    @Test
    @Issue("JENKINS-27177")
    @WithPlugin("dependee-0.0.2.hpi")
    public void notRestartAfterDisablePluginWithoutArgumentRestart() {
        Assert.assertThat(disablePluginsCLiCommand("dependee"), succeeded());
        assertPluginDisabled("dependee");
        assertJenkinsNotInQuietMode();
    }

    /**
     * A non-existing plugin returns with a {@link DisablePluginCommand#RETURN_CODE_NO_SUCH_PLUGIN} status code.
     */
    @Test
    @Issue("JENKINS-27177")
    @WithPlugin("dependee-0.0.2.hpi")
    public void returnCodeDisableInvalidPlugin() {
        Assert.assertThat(disablePluginsCLiCommand("wrongname"), failedWith(DisablePluginCommand.RETURN_CODE_NO_SUCH_PLUGIN));
    }

    /**
     * A plugin already disabled returns 0 and jenkins doesn't restart even though you passed the -restart argument.
     *
     * @throws IOException
     * 		See {@link PluginWrapper#disable()}.
     */
    @Test
    @Issue("JENKINS-27177")
    @WithPlugin("dependee-0.0.2.hpi")
    public void disableAlreadyDisabledPluginNotRestart() throws IOException {
        // Disable before the command call
        disablePlugin("dependee");
        assertPluginDisabled("dependee");
        Assert.assertThat(disablePluginsCLiCommand("-restart", "dependee"), succeeded());
        assertPluginDisabled("dependee");
        assertJenkinsNotInQuietMode();
    }

    /**
     * All the dependent plugins, mandatory or optional, are disabled using <i>-strategy all</i>.
     */
    @Test
    @Issue("JENKINS-27177")
    @WithPlugin({ "variant.hpi", "depender-0.0.2.hpi", "mandatory-depender-0.0.2.hpi", "plugin-first.hpi", "dependee-0.0.2.hpi" })
    public void disablePluginsStrategyAll() {
        assertPluginEnabled("dependee");
        assertPluginEnabled("depender");
        assertPluginEnabled("mandatory-depender");
        Assert.assertThat(disablePluginsCLiCommand("-strategy", "all", "variant", "dependee", "plugin-first"), succeeded());
        assertPluginDisabled("variant");
        assertPluginDisabled("dependee");
        assertPluginDisabled("depender");
        assertPluginDisabled("plugin-first");
        assertPluginDisabled("mandatory-depender");
    }

    /**
     * Only the mandatory dependent plugins are disabled using <i>-strategy mandatory</i>.
     */
    @Test
    @Issue("JENKINS-27177")
    @WithPlugin({ "variant.hpi", "depender-0.0.2.hpi", "mandatory-depender-0.0.2.hpi", "plugin-first.hpi", "dependee-0.0.2.hpi" })
    public void disablePluginsStrategyMandatory() {
        Assert.assertThat(disablePluginsCLiCommand("-strategy", "mandatory", "variant", "dependee", "plugin-first"), succeeded());
        assertPluginDisabled("variant");
        assertPluginDisabled("dependee");
        assertPluginEnabled("depender");
        assertPluginDisabled("plugin-first");
        assertPluginDisabled("mandatory-depender");
    }

    /**
     * A plugin already disabled because it's a dependent plugin of one previously disabled appear two times in the log
     * with different messages.
     */
    @Test
    @Issue("JENKINS-27177")
    @WithPlugin({ "depender-0.0.2.hpi", "dependee-0.0.2.hpi" })
    public void disablePluginsMessageAlreadyDisabled() {
        CLICommandInvoker.Result result = disablePluginsCLiCommand("-strategy", "all", "dependee", "depender");
        Assert.assertThat(result, succeeded());
        assertPluginDisabled("dependee");
        assertPluginDisabled("depender");
        Assert.assertTrue("An occurrence of the depender plugin in the log says it was successfully disabled", checkResultWith(result, StringUtils::contains, "depender", DISABLED));
        Assert.assertTrue("An occurrence of the depender plugin in the log says it was already disabled", checkResultWith(result, StringUtils::contains, "depender", ALREADY_DISABLED));
    }

    /**
     * The return code is the first error distinct of 0 found during the process. In this case dependent plugins not
     * disabled.
     */
    @Test
    @Issue("JENKINS-27177")
    @WithPlugin({ "dependee-0.0.2.hpi", "mandatory-depender-0.0.2.hpi" })
    public void returnCodeFirstErrorIsDependents() {
        CLICommandInvoker.Result result = disablePluginsCLiCommand("dependee", "badplugin");
        Assert.assertThat(result, failedWith(DisablePluginCommand.RETURN_CODE_NOT_DISABLED_DEPENDANTS));
        assertPluginEnabled("dependee");
    }

    /**
     * The return code is the first error distinct of 0 found during the process. In this case no such plugin.
     */
    @Test
    @Issue("JENKINS-27177")
    @WithPlugin({ "dependee-0.0.2.hpi", "mandatory-depender-0.0.2.hpi" })
    public void returnCodeFirstErrorIsNoSuchPlugin() {
        CLICommandInvoker.Result result = disablePluginsCLiCommand("badplugin", "dependee");
        Assert.assertThat(result, failedWith(DisablePluginCommand.RETURN_CODE_NO_SUCH_PLUGIN));
        assertPluginEnabled("dependee");
    }

    /**
     * In quiet mode, no message is printed if all plugins are disabled or were already disabled.
     */
    @Test
    @Issue("JENKINS-27177")
    @WithPlugin({ "depender-0.0.2.hpi", "dependee-0.0.2.hpi", "mandatory-depender-0.0.2.hpi" })
    public void quietModeEmptyOutputSucceed() {
        CLICommandInvoker.Result result = disablePluginsCLiCommand("-strategy", "all", "-quiet", "dependee");
        Assert.assertThat(result, succeeded());
        assertPluginDisabled("dependee");
        assertPluginDisabled("depender");
        assertPluginDisabled("mandatory-depender");
        Assert.assertTrue("No log in quiet mode if all plugins disabled", StringUtils.isEmpty(result.stdout()));
    }

    /**
     * In quiet mode, only the errors (no such plugin) are printed.
     */
    @Test
    @Issue("JENKINS-27177")
    @WithPlugin({ "depender-0.0.2.hpi", "dependee-0.0.2.hpi", "mandatory-depender-0.0.2.hpi" })
    public void quietModeWithErrorNoSuch() {
        CLICommandInvoker.Result result = disablePluginsCLiCommand("-quiet", "-strategy", "all", "dependee", "badplugin");
        Assert.assertThat(result, failedWith(DisablePluginCommand.RETURN_CODE_NO_SUCH_PLUGIN));
        assertPluginDisabled("dependee");
        assertPluginDisabled("depender");
        assertPluginDisabled("mandatory-depender");
        Assert.assertTrue("Only error NO_SUCH_PLUGIN in quiet mode", checkResultWith(result, StringUtils::startsWith, "badplugin", NO_SUCH_PLUGIN));
    }

    /**
     * In quiet mode, only the errors (dependents plugins) are printed.
     */
    @Test
    @Issue("JENKINS-27177")
    @WithPlugin({ "depender-0.0.2.hpi", "dependee-0.0.2.hpi", "mandatory-depender-0.0.2.hpi" })
    public void quietModeWithErrorDependents() {
        CLICommandInvoker.Result result = disablePluginsCLiCommand("-quiet", "-strategy", "none", "dependee");
        Assert.assertThat(result, failedWith(DisablePluginCommand.RETURN_CODE_NOT_DISABLED_DEPENDANTS));
        assertPluginEnabled("dependee");
        assertPluginEnabled("depender");
        assertPluginEnabled("mandatory-depender");
        Assert.assertTrue("Only error NOT_DISABLED_DEPENDANTS in quiet mode", checkResultWith(result, StringUtils::startsWith, "dependee", NOT_DISABLED_DEPENDANTS));
    }
}

