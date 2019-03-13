/**
 * The MIT License
 *
 * Copyright (c) 2015 CloudBees, Inc.
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


import Fingerprint.BuildPtr;
import Fingerprint.RangeSet;
import Item.DISCOVER;
import Jenkins.ADMINISTER;
import Jenkins.READ;
import hudson.XmlFile;
import hudson.security.ACL;
import hudson.security.ACLContext;
import hudson.security.AuthorizationMatrixProperty;
import hudson.security.ProjectMatrixAuthorizationStrategy;
import hudson.tasks.ArtifactArchiver;
import hudson.tasks.Fingerprinter;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.CreateFileBuilder;
import org.jvnet.hudson.test.Issue;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.SecuredMockFolder;


// TODO: Refactoring: Tests should be exchanged with FingerprinterTest somehow
/**
 * Tests for the {@link Fingerprint} class.
 *
 * @author Oleg Nenashev
 */
public class FingerprintTest {
    @Rule
    public JenkinsRule rule = new JenkinsRule();

    @Test
    public void shouldCreateFingerprintsForWorkspace() throws Exception {
        FreeStyleProject project = rule.createFreeStyleProject();
        project.getBuildersList().add(new CreateFileBuilder("test.txt", "Hello, world!"));
        project.getPublishersList().add(new Fingerprinter("test.txt", false));
        FreeStyleBuild build = rule.buildAndAssertSuccess(project);
        Fingerprint fp = getFingerprint(build, "test.txt");
    }

    @Test
    public void shouldCreateFingerprintsForArtifacts() throws Exception {
        FreeStyleProject project = rule.createFreeStyleProject();
        project.getBuildersList().add(new CreateFileBuilder("test.txt", "Hello, world!"));
        ArtifactArchiver archiver = new ArtifactArchiver("test.txt");
        archiver.setFingerprint(true);
        project.getPublishersList().add(archiver);
        FreeStyleBuild build = rule.buildAndAssertSuccess(project);
        Fingerprint fp = getFingerprint(build, "test.txt");
    }

    @Test
    public void shouldCreateUsageLinks() throws Exception {
        // Project 1
        FreeStyleProject project = createAndRunProjectWithPublisher("fpProducer", "test.txt");
        final FreeStyleBuild build = project.getLastBuild();
        // Project 2
        FreeStyleProject project2 = rule.createFreeStyleProject();
        project2.getBuildersList().add(new org.jvnet.hudson.test.WorkspaceCopyFileBuilder("test.txt", project.getName(), build.getNumber()));
        project2.getPublishersList().add(new Fingerprinter("test.txt"));
        FreeStyleBuild build2 = rule.buildAndAssertSuccess(project2);
        Fingerprint fp = getFingerprint(build, "test.txt");
        // Check references
        Fingerprint.BuildPtr original = fp.getOriginal();
        Assert.assertEquals("Original reference contains a wrong job name", project.getName(), original.getName());
        Assert.assertEquals("Original reference contains a wrong build number", build.getNumber(), original.getNumber());
        Hashtable<String, Fingerprint.RangeSet> usages = fp.getUsages();
        Assert.assertTrue(("Usages do not have a reference to " + project), usages.containsKey(project.getName()));
        Assert.assertTrue(("Usages do not have a reference to " + project2), usages.containsKey(project2.getName()));
    }

    @Test
    @Issue("JENKINS-51179")
    public void shouldThrowIOExceptionWhenFileIsInvalid() throws Exception {
        XmlFile f = new XmlFile(new File(rule.jenkins.getRootDir(), "foo.xml"));
        f.write("Hello, world!");
        try {
            Fingerprint.load(f.getFile());
        } catch (IOException ex) {
            Assert.assertThat(ex.getMessage(), Matchers.containsString("Unexpected Fingerprint type"));
            return;
        }
        Assert.fail("Expected IOException");
    }

    @Test
    @Issue("SECURITY-153")
    public void shouldBeUnableToSeeJobsIfNoPermissions() throws Exception {
        // Project 1
        final FreeStyleProject project1 = createAndRunProjectWithPublisher("fpProducer", "test.txt");
        final FreeStyleBuild build = project1.getLastBuild();
        // Project 2
        final FreeStyleProject project2 = rule.createFreeStyleProject("project2");
        project2.getBuildersList().add(new org.jvnet.hudson.test.WorkspaceCopyFileBuilder("test.txt", project1.getName(), build.getNumber()));
        project2.getPublishersList().add(new Fingerprinter("test.txt"));
        final FreeStyleBuild build2 = rule.buildAndAssertSuccess(project2);
        // Get fingerprint
        final Fingerprint fp = getFingerprint(build, "test.txt");
        // Init Users
        User user1 = User.get("user1");// can access project1

        User user2 = User.get("user2");// can access project2

        User user3 = User.get("user3");// cannot access anything

        // Project permissions
        setupProjectMatrixAuthStrategy(READ);
        setJobPermissionsOnce(project1, "user1", Item.READ, DISCOVER);
        setJobPermissionsOnce(project2, "user2", Item.READ, DISCOVER);
        try (ACLContext acl = ACL.as(user1)) {
            Fingerprint.BuildPtr original = fp.getOriginal();
            Assert.assertThat("user1 should be able to see the origin", fp.getOriginal(), Matchers.notNullValue());
            Assert.assertEquals("user1 should be able to see the origin's project name", project1.getName(), original.getName());
            Assert.assertEquals("user1 should be able to see the origin's build number", build.getNumber(), original.getNumber());
            Assert.assertEquals("Only one usage should be visible to user1", 1, fp._getUsages().size());
            Assert.assertEquals("Only project1 should be visible to user1", project1.getFullName(), fp._getUsages().get(0).name);
        }
        try (ACLContext acl = ACL.as(user2)) {
            Assert.assertThat("user2 should be unable to see the origin", fp.getOriginal(), Matchers.nullValue());
            Assert.assertEquals("Only one usage should be visible to user2", 1, fp._getUsages().size());
            Assert.assertEquals("Only project2 should be visible to user2", project2.getFullName(), fp._getUsages().get(0).name);
        }
        try (ACLContext acl = ACL.as(user3)) {
            Fingerprint.BuildPtr original = fp.getOriginal();
            Assert.assertThat("user3 should be unable to see the origin", fp.getOriginal(), Matchers.nullValue());
            Assert.assertEquals("All usages should be invisible for user3", 0, fp._getUsages().size());
        }
    }

    @Test
    public void shouldBeAbleToSeeOriginalWithDiscoverPermissionOnly() throws Exception {
        // Setup the environment
        final FreeStyleProject project = createAndRunProjectWithPublisher("project", "test.txt");
        final FreeStyleBuild build = project.getLastBuild();
        final Fingerprint fingerprint = getFingerprint(build, "test.txt");
        // Init Users and security
        User user1 = User.get("user1");
        setupProjectMatrixAuthStrategy(READ, DISCOVER);
        try (ACLContext acl = ACL.as(user1)) {
            Fingerprint.BuildPtr original = fingerprint.getOriginal();
            Assert.assertThat("user1 should able to see the origin", fingerprint.getOriginal(), Matchers.notNullValue());
            Assert.assertEquals("user1 sees the wrong original name with Item.DISCOVER", project.getFullName(), original.getName());
            Assert.assertEquals("user1 sees the wrong original number with Item.DISCOVER", build.getNumber(), original.getNumber());
            Assert.assertEquals("Usage ref in fingerprint should be visible to user1", 1, fingerprint._getUsages().size());
        }
    }

    @Test
    public void shouldBeAbleToSeeFingerprintsInReadableFolder() throws Exception {
        final SecuredMockFolder folder = rule.jenkins.createProject(SecuredMockFolder.class, "folder");
        final FreeStyleProject project = createAndRunProjectWithPublisher(folder, "project", "test.txt");
        final FreeStyleBuild build = project.getLastBuild();
        final Fingerprint fingerprint = getFingerprint(build, "test.txt");
        // Init Users and security
        User user1 = User.get("user1");
        setupProjectMatrixAuthStrategy(false, READ, DISCOVER);
        setJobPermissionsOnce(project, "user1", DISCOVER);// Prevents the fallback to the folder ACL

        folder.setPermissions("user1", Item.READ);
        // Ensure we can read the original from user account
        try (ACLContext acl = ACL.as(user1)) {
            Assert.assertTrue("Test framework issue: User1 should be able to read the folder", folder.hasPermission(Item.READ));
            Fingerprint.BuildPtr original = fingerprint.getOriginal();
            Assert.assertThat("user1 should able to see the origin", fingerprint.getOriginal(), Matchers.notNullValue());
            Assert.assertEquals("user1 sees the wrong original name with Item.DISCOVER", project.getFullName(), original.getName());
            Assert.assertEquals("user1 sees the wrong original number with Item.DISCOVER", build.getNumber(), original.getNumber());
            Assert.assertEquals("user1 should be able to see the job", 1, fingerprint._getUsages().size());
            Assert.assertThat("User should be unable do retrieve the job due to the missing read", original.getJob(), Matchers.nullValue());
        }
    }

    @Test
    public void shouldBeUnableToSeeFingerprintsInUnreadableFolder() throws Exception {
        final SecuredMockFolder folder = rule.jenkins.createProject(SecuredMockFolder.class, "folder");
        final FreeStyleProject project = createAndRunProjectWithPublisher(folder, "project", "test.txt");
        final FreeStyleBuild build = project.getLastBuild();
        final Fingerprint fingerprint = getFingerprint(build, "test.txt");
        // Init Users and security
        User user1 = User.get("user1");// can access project1

        setupProjectMatrixAuthStrategy(READ, DISCOVER);
        // Ensure we can read the original from user account
        try (ACLContext acl = ACL.as(user1)) {
            Assert.assertFalse("Test framework issue: User1 should be unable to read the folder", folder.hasPermission(Item.READ));
            Assert.assertThat("user1 should be unable to see the origin", fingerprint.getOriginal(), Matchers.nullValue());
            Assert.assertEquals("No jobs should be visible to user1", 0, fingerprint._getUsages().size());
        }
    }

    /**
     * A common non-admin user should not be able to see references to a
     * deleted job even if he used to have READ permissions before the deletion.
     *
     * @throws Exception
     * 		Test error
     */
    @Test
    @Issue("SECURITY-153")
    public void commonUserShouldBeUnableToSeeReferencesOfDeletedJobs() throws Exception {
        // Setup the environment
        FreeStyleProject project = createAndRunProjectWithPublisher("project", "test.txt");
        FreeStyleBuild build = project.getLastBuild();
        final Fingerprint fp = getFingerprint(build, "test.txt");
        // Init Users and security
        User user1 = User.get("user1");
        setupProjectMatrixAuthStrategy(READ, Item.READ, DISCOVER);
        project.delete();
        try (ACLContext acl = ACL.as(user1)) {
            Assert.assertThat("user1 should be unable to see the origin", fp.getOriginal(), Matchers.nullValue());
            Assert.assertEquals("No jobs should be visible to user1", 0, fp._getUsages().size());
        }
    }

    @Test
    public void adminShouldBeAbleToSeeReferencesOfDeletedJobs() throws Exception {
        // Setup the environment
        final FreeStyleProject project = createAndRunProjectWithPublisher("project", "test.txt");
        final FreeStyleBuild build = project.getLastBuild();
        final Fingerprint fingerprint = getFingerprint(build, "test.txt");
        // Init Users and security
        User user1 = User.get("user1");
        setupProjectMatrixAuthStrategy(ADMINISTER);
        project.delete();
        try (ACLContext acl = ACL.as(user1)) {
            Fingerprint.BuildPtr original = fingerprint.getOriginal();
            Assert.assertThat("user1 should able to see the origin", fingerprint.getOriginal(), Matchers.notNullValue());
            Assert.assertThat("Job has been deleted, so Job reference should return null", fingerprint.getOriginal().getJob(), Matchers.nullValue());
            Assert.assertEquals("user1 sees the wrong original name with Item.DISCOVER", project.getFullName(), original.getName());
            Assert.assertEquals("user1 sees the wrong original number with Item.DISCOVER", build.getNumber(), original.getNumber());
            Assert.assertEquals("user1 should be able to see the job in usages", 1, fingerprint._getUsages().size());
        }
    }

    /**
     * Security strategy, which prevents the permission inheritance from upper folders.
     */
    private static class NoInheritanceProjectMatrixAuthorizationStrategy extends ProjectMatrixAuthorizationStrategy {
        @Override
        public ACL getACL(Job<?, ?> project) {
            AuthorizationMatrixProperty amp = project.getProperty(AuthorizationMatrixProperty.class);
            if (amp != null) {
                return amp.getACL().newInheritingACL(getRootACL());
            } else {
                return getRootACL();
            }
        }
    }
}

