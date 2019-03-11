/**
 * The MIT License
 *
 * Copyright (c) 2004-2009, Sun Microsystems, Inc.
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


import DumbSlave.DescriptorImpl;
import JenkinsRule.WebClient;
import com.gargoylesoftware.htmlunit.Page;
import groovy.util.XmlSlurper;
import hudson.DescriptorExtensionList;
import hudson.ExtensionList;
import hudson.slaves.ComputerLauncher;
import hudson.slaves.DumbSlave;
import hudson.slaves.JNLPLauncher;
import hudson.slaves.NodeProperty;
import hudson.slaves.NodePropertyDescriptor;
import hudson.slaves.RetentionStrategy;
import hudson.util.FormValidation;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import org.apache.commons.io.IOUtils;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.Issue;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.TestExtension;


public class SlaveTest {
    @Rule
    public JenkinsRule j = new JenkinsRule();

    /**
     * Makes sure that a form validation method gets inherited.
     */
    @Test
    public void formValidation() throws Exception {
        j.executeOnServer(() -> {
            assertNotNull(j.jenkins.getDescriptor(.class).getCheckUrl("remoteFS"));
            return null;
        });
    }

    /**
     * Programmatic config.xml submission.
     */
    @Test
    public void slaveConfigDotXml() throws Exception {
        DumbSlave s = j.createSlave();
        JenkinsRule.WebClient wc = j.createWebClient();
        Page p = wc.goTo((("computer/" + (s.getNodeName())) + "/config.xml"), "application/xml");
        String xml = p.getWebResponse().getContentAsString();
        new XmlSlurper().parseText(xml);// verify that it is XML

        // make sure it survives the roundtrip
        post((("computer/" + (s.getNodeName())) + "/config.xml"), xml);
        Assert.assertNotNull(j.jenkins.getNode(s.getNodeName()));
        xml = IOUtils.toString(getClass().getResource("SlaveTest/slave.xml").openStream());
        xml = xml.replace("NAME", s.getNodeName());
        post((("computer/" + (s.getNodeName())) + "/config.xml"), xml);
        s = ((DumbSlave) (j.jenkins.getNode(s.getNodeName())));
        Assert.assertNotNull(s);
        Assert.assertEquals("some text", s.getNodeDescription());
        Assert.assertEquals(JNLPLauncher.class, s.getLauncher().getClass());
    }

    @Test
    public void remoteFsCheck() throws Exception {
        DumbSlave.DescriptorImpl d = j.jenkins.getDescriptorByType(DescriptorImpl.class);
        Assert.assertEquals(FormValidation.ok(), d.doCheckRemoteFS("c:\\"));
        Assert.assertEquals(FormValidation.ok(), d.doCheckRemoteFS("/tmp"));
        Assert.assertEquals(Kind.WARNING, d.doCheckRemoteFS("relative/path").kind);
        Assert.assertEquals(Kind.WARNING, d.doCheckRemoteFS("/net/foo/bar/zot").kind);
        Assert.assertEquals(Kind.WARNING, d.doCheckRemoteFS("\\\\machine\\folder\\foo").kind);
    }

    @Test
    @Issue("SECURITY-195")
    public void shouldNotEscapeJnlpSlavesResources() throws Exception {
        Slave slave = j.createSlave();
        // Spot-check correct requests
        assertJnlpJarUrlIsAllowed(slave, "agent.jar");
        assertJnlpJarUrlIsAllowed(slave, "slave.jar");
        assertJnlpJarUrlIsAllowed(slave, "remoting.jar");
        assertJnlpJarUrlIsAllowed(slave, "jenkins-cli.jar");
        assertJnlpJarUrlIsAllowed(slave, "hudson-cli.jar");
        // Check that requests to other WEB-INF contents fail
        assertJnlpJarUrlFails(slave, "web.xml");
        assertJnlpJarUrlFails(slave, "web.xml");
        assertJnlpJarUrlFails(slave, "classes/bundled-plugins.txt");
        assertJnlpJarUrlFails(slave, "classes/dependencies.txt");
        assertJnlpJarUrlFails(slave, "plugins/ant.hpi");
        assertJnlpJarUrlFails(slave, "nonexistentfolder/something.txt");
        // Try various kinds of folder escaping (SECURITY-195)
        assertJnlpJarUrlFails(slave, "../");
        assertJnlpJarUrlFails(slave, "..");
        assertJnlpJarUrlFails(slave, "..\\");
        assertJnlpJarUrlFails(slave, "../foo/bar");
        assertJnlpJarUrlFails(slave, "..\\foo\\bar");
        assertJnlpJarUrlFails(slave, "foo/../../bar");
        assertJnlpJarUrlFails(slave, "./../foo/bar");
    }

    @Test
    @Issue("JENKINS-36280")
    public void launcherFiltering() throws Exception {
        DumbSlave.DescriptorImpl descriptor = j.getInstance().getDescriptorByType(DescriptorImpl.class);
        DescriptorExtensionList<ComputerLauncher, Descriptor<ComputerLauncher>> descriptors = j.getInstance().getDescriptorList(ComputerLauncher.class);
        Assume.assumeThat("we need at least two launchers to test this", descriptors.size(), Matchers.not(Matchers.anyOf(Matchers.is(0), Matchers.is(1))));
        Assert.assertThat(descriptor.computerLauncherDescriptors(null), Matchers.containsInAnyOrder(descriptors.toArray(new Descriptor[descriptors.size()])));
        Descriptor<ComputerLauncher> victim = descriptors.iterator().next();
        Assert.assertThat(descriptor.computerLauncherDescriptors(null), Matchers.hasItem(victim));
        SlaveTest.DynamicFilter.descriptors().add(victim);
        Assert.assertThat(descriptor.computerLauncherDescriptors(null), Matchers.not(Matchers.hasItem(victim)));
        SlaveTest.DynamicFilter.descriptors().remove(victim);
        Assert.assertThat(descriptor.computerLauncherDescriptors(null), Matchers.hasItem(victim));
    }

    @Test
    @Issue("JENKINS-36280")
    public void retentionFiltering() throws Exception {
        DumbSlave.DescriptorImpl descriptor = j.getInstance().getDescriptorByType(DescriptorImpl.class);
        DescriptorExtensionList<RetentionStrategy<?>, Descriptor<RetentionStrategy<?>>> descriptors = RetentionStrategy.all();
        Assume.assumeThat("we need at least two retention strategies to test this", descriptors.size(), Matchers.not(Matchers.anyOf(Matchers.is(0), Matchers.is(1))));
        Assert.assertThat(descriptor.retentionStrategyDescriptors(null), Matchers.containsInAnyOrder(descriptors.toArray(new Descriptor[descriptors.size()])));
        Descriptor<RetentionStrategy<?>> victim = descriptors.iterator().next();
        Assert.assertThat(descriptor.retentionStrategyDescriptors(null), Matchers.hasItem(victim));
        SlaveTest.DynamicFilter.descriptors().add(victim);
        Assert.assertThat(descriptor.retentionStrategyDescriptors(null), Matchers.not(Matchers.hasItem(victim)));
        SlaveTest.DynamicFilter.descriptors().remove(victim);
        Assert.assertThat(descriptor.retentionStrategyDescriptors(null), Matchers.hasItem(victim));
    }

    @Test
    @Issue("JENKINS-36280")
    public void propertyFiltering() throws Exception {
        DumbSlave.DescriptorImpl descriptor = j.getInstance().getDescriptorByType(DescriptorImpl.class);
        DescriptorExtensionList<NodeProperty<?>, NodePropertyDescriptor> descriptors = NodeProperty.all();
        Assume.assumeThat("we need at least two node properties to test this", descriptors.size(), Matchers.not(Matchers.anyOf(Matchers.is(0), Matchers.is(1))));
        Assert.assertThat(descriptor.nodePropertyDescriptors(null), Matchers.containsInAnyOrder(descriptors.toArray(new Descriptor[descriptors.size()])));
        NodePropertyDescriptor victim = descriptors.iterator().next();
        Assert.assertThat(descriptor.nodePropertyDescriptors(null), Matchers.hasItem(victim));
        SlaveTest.DynamicFilter.descriptors().add(victim);
        Assert.assertThat(descriptor.nodePropertyDescriptors(null), Matchers.not(Matchers.hasItem(victim)));
        SlaveTest.DynamicFilter.descriptors().remove(victim);
        Assert.assertThat(descriptor.nodePropertyDescriptors(null), Matchers.hasItem(victim));
    }

    @TestExtension
    public static class DynamicFilter extends DescriptorVisibilityFilter {
        private final Set<Descriptor> descriptors = new HashSet<>();

        public static Set<Descriptor> descriptors() {
            return ExtensionList.lookup(DescriptorVisibilityFilter.class).get(SlaveTest.DynamicFilter.class).descriptors;
        }

        @Override
        public boolean filterType(@Nonnull
        Class<?> contextClass, @Nonnull
        Descriptor descriptor) {
            return !(descriptors.contains(descriptor));
        }

        @Override
        public boolean filter(@CheckForNull
        Object context, @Nonnull
        Descriptor descriptor) {
            return !(descriptors.contains(descriptor));
        }
    }
}

