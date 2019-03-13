/**
 * The MIT License
 *
 * Copyright (c) 2016 CloudBees, Inc.
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


import LocalPluginManager.CUSTOM_PLUGIN_MANAGER;
import java.io.File;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.servlet.ServletContext;
import jenkins.model.Jenkins;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.Issue;
import org.jvnet.hudson.test.JenkinsRecipe;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.recipes.WithPlugin;


/**
 * Tests for the use of a custom plugin manager in custom wars.
 */
public class CustomPluginManagerTest {
    @Rule
    public final JenkinsRule r = new JenkinsRule();

    // TODO: Move to jenkins-test-harness
    @JenkinsRecipe(CustomPluginManagerTest.WithCustomLocalPluginManager.RuleRunnerImpl.class)
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface WithCustomLocalPluginManager {
        Class<? extends LocalPluginManager> value();

        class RuleRunnerImpl extends JenkinsRecipe.Runner<CustomPluginManagerTest.WithCustomLocalPluginManager> {
            private String oldValue;

            @Override
            public void setup(JenkinsRule jenkinsRule, CustomPluginManagerTest.WithCustomLocalPluginManager recipe) throws Exception {
                jenkinsRule.useLocalPluginManager = true;
                oldValue = System.getProperty(CUSTOM_PLUGIN_MANAGER);
                System.setProperty(CUSTOM_PLUGIN_MANAGER, recipe.value().getName());
            }

            @Override
            public void tearDown(JenkinsRule jenkinsRule, CustomPluginManagerTest.WithCustomLocalPluginManager recipe) throws Exception {
                System.setProperty(CUSTOM_PLUGIN_MANAGER, oldValue);
            }
        }
    }

    // An interface not to override every constructor.
    interface CustomPluginManager {}

    @Issue("JENKINS-34681")
    @WithPlugin("tasks.jpi")
    @CustomPluginManagerTest.WithCustomLocalPluginManager(CustomPluginManagerTest.CustomPluginManager1.class)
    @Test
    public void customPluginManager1() {
        check(CustomPluginManagerTest.CustomPluginManager1.class);
    }

    public static class CustomPluginManager1 extends LocalPluginManager implements CustomPluginManagerTest.CustomPluginManager {
        public CustomPluginManager1(Jenkins jenkins) {
            super(jenkins);
        }
    }

    @Issue("JENKINS-34681")
    @WithPlugin("tasks.jpi")
    @CustomPluginManagerTest.WithCustomLocalPluginManager(CustomPluginManagerTest.CustomPluginManager2.class)
    @Test
    public void customPluginManager2() {
        check(CustomPluginManagerTest.CustomPluginManager2.class);
    }

    public static class CustomPluginManager2 extends LocalPluginManager implements CustomPluginManagerTest.CustomPluginManager {
        public CustomPluginManager2(ServletContext ctx, File root) {
            super(ctx, root);
        }
    }

    @Issue("JENKINS-34681")
    @WithPlugin("tasks.jpi")
    @CustomPluginManagerTest.WithCustomLocalPluginManager(CustomPluginManagerTest.CustomPluginManager3.class)
    @Test
    public void customPluginManager3() {
        check(CustomPluginManagerTest.CustomPluginManager3.class);
    }

    public static class CustomPluginManager3 extends LocalPluginManager implements CustomPluginManagerTest.CustomPluginManager {
        public CustomPluginManager3(File root) {
            super(root);
        }
    }

    @Issue("JENKINS-34681")
    @WithPlugin("tasks.jpi")
    @CustomPluginManagerTest.WithCustomLocalPluginManager(CustomPluginManagerTest.BadCustomPluginManager.class)
    @Test
    public void badCustomPluginManager() {
        Assert.assertFalse("Custom plugin manager not installed", ((r.getPluginManager()) instanceof CustomPluginManagerTest.CustomPluginManager));
    }

    public static class BadCustomPluginManager extends LocalPluginManager implements CustomPluginManagerTest.CustomPluginManager {
        public BadCustomPluginManager(File root, ServletContext ctx) {
            super(ctx, root);
        }
    }
}

