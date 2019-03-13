package org.opengrok.indexer.web;


import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import org.junit.Assert;
import org.junit.Test;
import org.opengrok.indexer.authorization.AuthorizationStack;
import org.opengrok.indexer.configuration.Group;
import org.opengrok.indexer.configuration.Project;
import org.opengrok.indexer.configuration.RuntimeEnvironment;


public class PageConfigRequestedProjectsTest {
    final RuntimeEnvironment env = RuntimeEnvironment.getInstance();

    private Map<String, Project> oldProjects;

    private Set<Group> oldGroups;

    private AuthorizationStack oldPluginStack;

    @Test
    public void testSingleProject() {
        final HttpServletRequest request = PageConfigRequestedProjectsTest.createRequest(new String[]{ "project-1" }, null);
        final PageConfig cfg = PageConfig.get(request);
        Assert.assertEquals(new HashSet(Arrays.asList("project-1")), cfg.getRequestedProjects());
    }

    @Test
    public void testMultipleProject() {
        final HttpServletRequest request = PageConfigRequestedProjectsTest.createRequest(new String[]{ "project-1", "project-3", "project-6" }, null);
        final PageConfig cfg = PageConfig.get(request);
        Assert.assertEquals(new HashSet(Arrays.asList("project-1", "project-3", "project-6")), cfg.getRequestedProjects());
    }

    @Test
    public void testNonIndexedProject() {
        env.getProjects().get("project-1").setIndexed(false);
        final HttpServletRequest request = PageConfigRequestedProjectsTest.createRequest(new String[]{ "project-1" }, null);
        final PageConfig cfg = PageConfig.get(request);
        Assert.assertEquals(new HashSet(), cfg.getRequestedProjects());
        env.getProjects().get("project-1").setIndexed(true);
    }

    @Test
    public void testMultipleWithNonIndexedProject() {
        env.getProjects().get("project-1").setIndexed(false);
        final HttpServletRequest request = PageConfigRequestedProjectsTest.createRequest(new String[]{ "project-1", "project-3", "project-6" }, null);
        final PageConfig cfg = PageConfig.get(request);
        Assert.assertEquals(new HashSet(Arrays.asList("project-3", "project-6")), cfg.getRequestedProjects());
        env.getProjects().get("project-1").setIndexed(true);
    }

    @Test
    public void testSingleGroup1() {
        final HttpServletRequest request = PageConfigRequestedProjectsTest.createRequest(null, new String[]{ "group-1-2-3" });
        final PageConfig cfg = PageConfig.get(request);
        Assert.assertEquals(new HashSet(Arrays.asList("project-1", "project-2", "project-3")), cfg.getRequestedProjects());
    }

    @Test
    public void testSingleGroup2() {
        final HttpServletRequest request = PageConfigRequestedProjectsTest.createRequest(null, new String[]{ "group-7-8-9" });
        final PageConfig cfg = PageConfig.get(request);
        Assert.assertEquals(new HashSet(Arrays.asList("project-7", "project-8", "project-9")), cfg.getRequestedProjects());
    }

    @Test
    public void testMultipleGroup() {
        final HttpServletRequest request = PageConfigRequestedProjectsTest.createRequest(null, new String[]{ "group-1-2-3", "group-7-8-9" });
        final PageConfig cfg = PageConfig.get(request);
        Assert.assertEquals(new HashSet(Arrays.asList("project-1", "project-2", "project-3", "project-7", "project-8", "project-9")), cfg.getRequestedProjects());
    }

    @Test
    public void testMixedGroupAndProjectAddingNewProjects() {
        final HttpServletRequest request = PageConfigRequestedProjectsTest.createRequest(new String[]{ "project-1", "project-6" }, new String[]{ "group-7-8-9" });
        final PageConfig cfg = PageConfig.get(request);
        Assert.assertEquals(new HashSet(Arrays.asList("project-1", "project-6", "project-7", "project-8", "project-9")), cfg.getRequestedProjects());
    }

    @Test
    public void testMixedGroupNonExistentGroupAndProjectAddingNewProjects() {
        final HttpServletRequest request = PageConfigRequestedProjectsTest.createRequest(new String[]{ "project-1", "project-6" }, new String[]{ "no-group", "group-7-8-9" });
        final PageConfig cfg = PageConfig.get(request);
        Assert.assertEquals(new HashSet(Arrays.asList("project-1", "project-6", "project-7", "project-8", "project-9")), cfg.getRequestedProjects());
    }

    @Test
    public void testMixedGroupAndProjectInclusion() {
        final HttpServletRequest request = PageConfigRequestedProjectsTest.createRequest(new String[]{ "project-1", "project-2" }, new String[]{ "group-1-2-3", "group-7-8-9" });
        final PageConfig cfg = PageConfig.get(request);
        Assert.assertEquals(new HashSet(Arrays.asList("project-1", "project-2", "project-3", "project-7", "project-8", "project-9")), cfg.getRequestedProjects());
    }

    @Test
    public void testNonIndexedInGroup() {
        env.getProjects().get("project-1").setIndexed(false);
        final HttpServletRequest request = PageConfigRequestedProjectsTest.createRequest(null, new String[]{ "group-1-2-3" });
        final PageConfig cfg = PageConfig.get(request);
        Assert.assertEquals(new HashSet(Arrays.asList("project-2", "project-3")), cfg.getRequestedProjects());
        env.getProjects().get("project-1").setIndexed(true);
    }

    /**
     * Assumes that there is no defaultProjects and no cookie set up.
     */
    @Test
    public void testNonExistentProject() {
        final HttpServletRequest request = PageConfigRequestedProjectsTest.createRequest(null, new String[]{ "no-project" });
        final PageConfig cfg = PageConfig.get(request);
        Assert.assertEquals(new HashSet(), cfg.getRequestedProjects());
    }

    /**
     * Assumes that there is no defaultProjects and no cookie set up.
     */
    @Test
    public void testNonExistentGroup() {
        final HttpServletRequest request = PageConfigRequestedProjectsTest.createRequest(null, new String[]{ "no-group" });
        final PageConfig cfg = PageConfig.get(request);
        Assert.assertEquals(new HashSet(), cfg.getRequestedProjects());
    }
}

