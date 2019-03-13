/**
 * The MIT License
 *
 * Copyright 2013 Jesse Glick.
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
package jenkins.model;


import hudson.model.AbstractProject;
import hudson.model.Action;
import hudson.model.FreeStyleProject;
import hudson.model.TransientProjectActionFactory;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.Callable;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.For;
import org.jvnet.hudson.test.Issue;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.TestExtension;
import org.kohsuke.stapler.Stapler;


@For(ContextMenu.class)
public class ContextMenuTest {
    @Rule
    public JenkinsRule j = new JenkinsRule();

    @Issue("JENKINS-19173")
    @Test
    public void contextMenuVisibility() throws Exception {
        final FreeStyleProject p = j.createFreeStyleProject("p");
        Callable<ContextMenu> doContextMenu = new Callable<ContextMenu>() {
            @Override
            public ContextMenu call() throws Exception {
                return p.doContextMenu(Stapler.getCurrentRequest(), Stapler.getCurrentResponse());
            }
        };
        ContextMenuTest.ActionFactory f = j.jenkins.getExtensionList(TransientProjectActionFactory.class).get(ContextMenuTest.ActionFactory.class);
        f.visible = true;
        ContextMenu menu = j.executeOnServer(doContextMenu);
        Map<String, String> parsed = ContextMenuTest.parse(menu);
        Assert.assertEquals(parsed.toString(), "Hello", parsed.get("testing"));
        f.visible = false;
        menu = j.executeOnServer(doContextMenu);
        parsed = ContextMenuTest.parse(menu);
        Assert.assertEquals(parsed.toString(), null, parsed.get("testing"));
    }

    @TestExtension
    public static class ActionFactory extends TransientProjectActionFactory {
        boolean visible;

        @SuppressWarnings("rawtypes")
        @Override
        public Collection<? extends Action> createFor(AbstractProject target) {
            return Collections.singleton(new ContextMenuVisibility() {
                @Override
                public boolean isVisible() {
                    return visible;
                }

                @Override
                public String getIconFileName() {
                    return "whatever";
                }

                @Override
                public String getDisplayName() {
                    return "Hello";
                }

                @Override
                public String getUrlName() {
                    return "testing";
                }
            });
        }
    }
}

