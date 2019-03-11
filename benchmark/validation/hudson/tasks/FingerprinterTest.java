/**
 * The MIT License
 *
 *  Copyright 2011 Yahoo!, Inc.
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 */
package hudson.tasks;


import Fingerprinter.FingerprintAction;
import Result.SUCCESS;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import hudson.Functions;
import hudson.Launcher;
import hudson.Util;
import hudson.XmlFile;
import hudson.matrix.Axis;
import hudson.matrix.MatrixProject;
import hudson.util.RunList;
import hudson.util.StreamTaskListener;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import jenkins.model.Jenkins;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.Issue;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.recipes.LocalData;


/**
 *
 *
 * @author dty
 */
@SuppressWarnings("rawtypes")
public class FingerprinterTest {
    private static final String[] singleContents = new String[]{ "abcdef" };

    private static final String[] singleFiles = new String[]{ "test.txt" };

    private static final String[] singleContents2 = new String[]{ "ghijkl" };

    private static final String[] singleFiles2 = new String[]{ "test2.txt" };

    private static final String[] doubleContents = new String[]{ "abcdef", "ghijkl" };

    private static final String[] doubleFiles = new String[]{ "test.txt", "test2.txt" };

    private static final String renamedProject1 = "renamed project 1";

    private static final String renamedProject2 = "renamed project 2";

    @Rule
    public JenkinsRule j = new JenkinsRule();

    @Test
    public void fingerprintDependencies() throws Exception {
        FreeStyleProject upstream = createFreeStyleProjectWithFingerprints(FingerprinterTest.singleContents, FingerprinterTest.singleFiles);
        FreeStyleProject downstream = createFreeStyleProjectWithFingerprints(FingerprinterTest.singleContents, FingerprinterTest.singleFiles);
        j.assertBuildStatusSuccess(upstream.scheduleBuild2(0).get());
        j.assertBuildStatusSuccess(downstream.scheduleBuild2(0).get());
        j.jenkins.rebuildDependencyGraph();
        List<AbstractProject> downstreamProjects = upstream.getDownstreamProjects();
        List<AbstractProject> upstreamProjects = downstream.getUpstreamProjects();
        Assert.assertEquals(1, downstreamProjects.size());
        Assert.assertEquals(1, upstreamProjects.size());
        Assert.assertTrue(upstreamProjects.contains(upstream));
        Assert.assertTrue(downstreamProjects.contains(downstream));
    }

    private static class FingerprintAddingBuilder extends Builder {
        @Override
        public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws IOException, InterruptedException {
            build.addAction(new Fingerprinter.FingerprintAction(build, ImmutableMap.of(FingerprinterTest.singleFiles2[0], "fakefingerprint")));
            return true;
        }
    }

    @Test
    public void presentFingerprintActionIsReused() throws Exception {
        FreeStyleProject project = createFreeStyleProjectWithFingerprints(FingerprinterTest.singleContents, FingerprinterTest.singleFiles);
        project.getBuildersList().add(new FingerprinterTest.FingerprintAddingBuilder());
        FreeStyleBuild build = j.buildAndAssertSuccess(project);
        Assert.assertThat(build.getActions(FingerprintAction.class), Matchers.hasSize(1));
        Fingerprinter.FingerprintAction action = build.getAction(FingerprintAction.class);
        Assert.assertEquals(action.getRecords().keySet(), ImmutableSet.of(FingerprinterTest.singleFiles2[0], FingerprinterTest.singleFiles[0]));
    }

    @Test
    public void multipleUpstreamDependencies() throws Exception {
        FreeStyleProject upstream = createFreeStyleProjectWithFingerprints(FingerprinterTest.singleContents, FingerprinterTest.singleFiles);
        FreeStyleProject upstream2 = createFreeStyleProjectWithFingerprints(FingerprinterTest.singleContents2, FingerprinterTest.singleFiles2);
        FreeStyleProject downstream = createFreeStyleProjectWithFingerprints(FingerprinterTest.doubleContents, FingerprinterTest.doubleFiles);
        j.assertBuildStatusSuccess(upstream.scheduleBuild2(0).get());
        j.assertBuildStatusSuccess(upstream2.scheduleBuild2(0).get());
        j.assertBuildStatusSuccess(downstream.scheduleBuild2(0).get());
        j.jenkins.rebuildDependencyGraph();
        List<AbstractProject> downstreamProjects = upstream.getDownstreamProjects();
        List<AbstractProject> downstreamProjects2 = upstream2.getDownstreamProjects();
        List<AbstractProject> upstreamProjects = downstream.getUpstreamProjects();
        Assert.assertEquals(1, downstreamProjects.size());
        Assert.assertEquals(1, downstreamProjects2.size());
        Assert.assertEquals(2, upstreamProjects.size());
        Assert.assertTrue(upstreamProjects.contains(upstream));
        Assert.assertTrue(upstreamProjects.contains(upstream2));
        Assert.assertTrue(downstreamProjects.contains(downstream));
    }

    @Test
    public void multipleDownstreamDependencies() throws Exception {
        FreeStyleProject upstream = createFreeStyleProjectWithFingerprints(FingerprinterTest.doubleContents, FingerprinterTest.doubleFiles);
        FreeStyleProject downstream = createFreeStyleProjectWithFingerprints(FingerprinterTest.singleContents, FingerprinterTest.singleFiles);
        FreeStyleProject downstream2 = createFreeStyleProjectWithFingerprints(FingerprinterTest.singleContents2, FingerprinterTest.singleFiles2);
        j.assertBuildStatusSuccess(upstream.scheduleBuild2(0).get());
        j.assertBuildStatusSuccess(downstream.scheduleBuild2(0).get());
        j.assertBuildStatusSuccess(downstream2.scheduleBuild2(0).get());
        j.jenkins.rebuildDependencyGraph();
        List<AbstractProject> downstreamProjects = upstream.getDownstreamProjects();
        List<AbstractProject> upstreamProjects = downstream.getUpstreamProjects();
        List<AbstractProject> upstreamProjects2 = downstream2.getUpstreamProjects();
        Assert.assertEquals(2, downstreamProjects.size());
        Assert.assertEquals(1, upstreamProjects.size());
        Assert.assertEquals(1, upstreamProjects2.size());
        Assert.assertTrue(upstreamProjects.contains(upstream));
        Assert.assertTrue(upstreamProjects2.contains(upstream));
        Assert.assertTrue(downstreamProjects.contains(downstream));
        Assert.assertTrue(downstreamProjects.contains(downstream2));
    }

    @Test
    public void dependencyExclusion() throws Exception {
        FreeStyleProject upstream = createFreeStyleProjectWithFingerprints(FingerprinterTest.singleContents, FingerprinterTest.singleFiles);
        FreeStyleProject downstream = createFreeStyleProjectWithFingerprints(FingerprinterTest.singleContents, FingerprinterTest.singleFiles);
        FreeStyleBuild upstreamBuild = j.assertBuildStatusSuccess(upstream.scheduleBuild2(0).get());
        j.assertBuildStatusSuccess(downstream.scheduleBuild2(0).get());
        upstreamBuild.delete();
        Jenkins.getInstance().rebuildDependencyGraph();
        List<AbstractProject> upstreamProjects = downstream.getUpstreamProjects();
        List<AbstractProject> downstreamProjects = upstream.getDownstreamProjects();
        Assert.assertEquals(0, upstreamProjects.size());
        Assert.assertEquals(0, downstreamProjects.size());
    }

    @Test
    public void circularDependency() throws Exception {
        FreeStyleProject p = createFreeStyleProjectWithFingerprints(FingerprinterTest.singleContents, FingerprinterTest.singleFiles);
        j.assertBuildStatusSuccess(p.scheduleBuild2(0).get());
        j.assertBuildStatusSuccess(p.scheduleBuild2(0).get());
        Jenkins.getInstance().rebuildDependencyGraph();
        List<AbstractProject> upstreamProjects = p.getUpstreamProjects();
        List<AbstractProject> downstreamProjects = p.getDownstreamProjects();
        Assert.assertEquals(0, upstreamProjects.size());
        Assert.assertEquals(0, downstreamProjects.size());
    }

    @Test
    public void matrixDependency() throws Exception {
        MatrixProject matrixProject = j.jenkins.createProject(MatrixProject.class, "p");
        matrixProject.setAxes(new hudson.matrix.AxisList(new Axis("foo", "a", "b")));
        FreeStyleProject freestyleProject = createFreeStyleProjectWithFingerprints(FingerprinterTest.singleContents, FingerprinterTest.singleFiles);
        addFingerprinterToProject(matrixProject, FingerprinterTest.singleContents, FingerprinterTest.singleFiles);
        j.jenkins.rebuildDependencyGraph();
        j.buildAndAssertSuccess(matrixProject);
        j.buildAndAssertSuccess(freestyleProject);
        j.waitUntilNoActivity();
        j.jenkins.rebuildDependencyGraph();
        RunList<FreeStyleBuild> builds = freestyleProject.getBuilds();
        Assert.assertEquals("There should only be one FreestyleBuild", 1, builds.size());
        FreeStyleBuild build = builds.iterator().next();
        Assert.assertEquals(SUCCESS, build.getResult());
        List<AbstractProject> downstream = j.jenkins.getDependencyGraph().getDownstream(matrixProject);
        Assert.assertTrue(downstream.contains(freestyleProject));
        List<AbstractProject> upstream = j.jenkins.getDependencyGraph().getUpstream(freestyleProject);
        Assert.assertTrue(upstream.contains(matrixProject));
    }

    @Test
    public void projectRename() throws Exception {
        FreeStyleProject upstream = createFreeStyleProjectWithFingerprints(FingerprinterTest.singleContents, FingerprinterTest.singleFiles);
        FreeStyleProject downstream = createFreeStyleProjectWithFingerprints(FingerprinterTest.singleContents, FingerprinterTest.singleFiles);
        FreeStyleBuild upstreamBuild = j.assertBuildStatusSuccess(upstream.scheduleBuild2(0).get());
        FreeStyleBuild downstreamBuild = j.assertBuildStatusSuccess(downstream.scheduleBuild2(0).get());
        String oldUpstreamName = upstream.getName();
        String oldDownstreamName = downstream.getName();
        // Verify that owner entry in fingerprint record is changed
        // after source project is renamed
        upstream.renameTo(FingerprinterTest.renamedProject1);
        Fingerprinter.FingerprintAction action = upstreamBuild.getAction(FingerprintAction.class);
        Assert.assertNotNull(action);
        Collection<Fingerprint> fingerprints = action.getFingerprints().values();
        for (Fingerprint f : fingerprints) {
            Assert.assertTrue(f.getOriginal().is(upstream));
            Assert.assertTrue(f.getOriginal().getName().equals(FingerprinterTest.renamedProject1));
            Assert.assertFalse(f.getOriginal().getName().equals(oldUpstreamName));
        }
        action = downstreamBuild.getAction(FingerprintAction.class);
        Assert.assertNotNull(action);
        fingerprints = action.getFingerprints().values();
        for (Fingerprint f : fingerprints) {
            Assert.assertTrue(f.getOriginal().is(upstream));
            Assert.assertTrue(f.getOriginal().getName().equals(FingerprinterTest.renamedProject1));
            Assert.assertFalse(f.getOriginal().getName().equals(oldUpstreamName));
        }
        // Verify that usage entry in fingerprint record is changed after
        // sink project is renamed
        downstream.renameTo(FingerprinterTest.renamedProject2);
        upstream.renameTo(FingerprinterTest.renamedProject1);
        action = upstreamBuild.getAction(FingerprintAction.class);
        Assert.assertNotNull(action);
        fingerprints = action.getFingerprints().values();
        for (Fingerprint f : fingerprints) {
            List<String> jobs = f.getJobs();
            Assert.assertTrue(jobs.contains(FingerprinterTest.renamedProject2));
            Assert.assertFalse(jobs.contains(oldDownstreamName));
        }
        action = downstreamBuild.getAction(FingerprintAction.class);
        Assert.assertNotNull(action);
        fingerprints = action.getFingerprints().values();
        for (Fingerprint f : fingerprints) {
            List<String> jobs = f.getJobs();
            Assert.assertTrue(jobs.contains(FingerprinterTest.renamedProject2));
            Assert.assertFalse(jobs.contains(oldDownstreamName));
        }
    }

    @Issue("JENKINS-17125")
    @LocalData
    @Test
    public void actionSerialization() throws Exception {
        FreeStyleProject job = j.jenkins.getItemByFullName((Functions.isWindows() ? "j-windows" : "j"), hudson.model.FreeStyleProject.class);
        Assert.assertNotNull(job);
        FreeStyleBuild build = job.getBuildByNumber(2);
        Assert.assertNotNull(build);
        Fingerprinter.FingerprintAction action = build.getAction(FingerprintAction.class);
        Assert.assertNotNull(action);
        Assert.assertEquals(build, action.getBuild());
        if (Functions.isWindows()) {
            Assert.assertEquals("{a=603bc9e16cc05bdbc5e595969f42e3b8}", action.getRecords().toString());
        } else {
            Assert.assertEquals("{a=2d5fac981a2e865baf0e15db655c7d63}", action.getRecords().toString());
        }
        j.assertBuildStatusSuccess(job.scheduleBuild2(0));
        job._getRuns().purgeCache();// force build records to be reloaded

        build = job.getBuildByNumber(3);
        Assert.assertNotNull(build);
        System.out.println(new XmlFile(new File(build.getRootDir(), "build.xml")).asString());
        action = build.getAction(FingerprintAction.class);
        Assert.assertNotNull(action);
        Assert.assertEquals(build, action.getBuild());
        if (Functions.isWindows()) {
            Assert.assertEquals("{a=a97a39fb51de0eee9fd908174dccc304}", action.getRecords().toString());
        } else {
            Assert.assertEquals("{a=f31efcf9afe30617d6c46b919e702822}", action.getRecords().toString());
        }
    }

    // TODO randomly fails: for p3.upstreamProjects expected:<[hudson.model.FreeStyleProject@590e5b8[test0]]> but was:<[]>
    @SuppressWarnings("unchecked")
    @Issue("JENKINS-18417")
    @Test
    public void fingerprintCleanup() throws Exception {
        // file names shouldn't matter
        FreeStyleProject p1 = createFreeStyleProjectWithFingerprints(FingerprinterTest.singleContents, FingerprinterTest.singleFiles);
        FreeStyleProject p2 = createFreeStyleProjectWithFingerprints(FingerprinterTest.singleContents, FingerprinterTest.singleFiles2);
        FreeStyleProject p3 = createFreeStyleProjectWithFingerprints(FingerprinterTest.singleContents, FingerprinterTest.singleFiles);
        j.assertBuildStatusSuccess(p1.scheduleBuild2(0));
        j.assertBuildStatusSuccess(p2.scheduleBuild2(0));
        j.assertBuildStatusSuccess(p3.scheduleBuild2(0));
        Fingerprint f = j.jenkins._getFingerprint(Util.getDigestOf(((FingerprinterTest.singleContents[0]) + (System.lineSeparator()))));
        Assert.assertNotNull(f);
        Assert.assertEquals(3, f.getUsages().size());
        j.jenkins.rebuildDependencyGraph();
        Assert.assertEquals(Arrays.asList(p1), p2.getUpstreamProjects());
        Assert.assertEquals(Arrays.asList(p1), p3.getUpstreamProjects());
        Assert.assertEquals(new HashSet(Arrays.asList(p2, p3)), new HashSet(p1.getDownstreamProjects()));
        // discard the p3 records
        p3.delete();
        new FingerprintCleanupThread().execute(StreamTaskListener.fromStdout());
        j.jenkins.rebuildDependencyGraph();
        // records for p3 should have been deleted now
        Assert.assertEquals(2, f.getUsages().size());
        Assert.assertEquals(Arrays.asList(p1), p2.getUpstreamProjects());
        Assert.assertEquals(Arrays.asList(p2), p1.getDownstreamProjects());
        // do a new build in p2 #2 that points to a separate fingerprints
        p2.getBuildersList().clear();
        p2.getPublishersList().clear();
        addFingerprinterToProject(p2, FingerprinterTest.singleContents2, FingerprinterTest.singleFiles2);
        j.assertBuildStatusSuccess(p2.scheduleBuild2(0));
        // another garbage collection that gets rid of p2 records from the fingerprint
        p2.getBuildByNumber(1).delete();
        new FingerprintCleanupThread().execute(StreamTaskListener.fromStdout());
        Assert.assertEquals(1, f.getUsages().size());
    }
}

