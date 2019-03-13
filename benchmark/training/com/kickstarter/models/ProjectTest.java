package com.kickstarter.models;


import Project.Urls;
import com.kickstarter.KSRobolectricTestCase;
import com.kickstarter.mock.factories.ProjectFactory;
import junit.framework.TestCase;
import org.joda.time.DateTime;
import org.junit.Test;


public class ProjectTest extends KSRobolectricTestCase {
    @Test
    public void testSecureWebProjectUrl() {
        final String projectUrl = "http://www.kickstarter.com/projects/foo/bar";
        final Project.Urls urls = Urls.builder().web(Project.Urls.Web.builder().project(projectUrl).rewards((projectUrl + "/rewards")).build()).build();
        final Project project = ProjectFactory.project().toBuilder().urls(urls).build();
        TestCase.assertEquals("https://www.kickstarter.com/projects/foo/bar", project.secureWebProjectUrl());
    }

    @Test
    public void testNewPledgeUrl() {
        TestCase.assertEquals("https://www.kickstarter.com/projects/foo/bar/pledge/new", projectWithSecureUrl().newPledgeUrl());
    }

    @Test
    public void testEditPledgeUrl() {
        TestCase.assertEquals("https://www.kickstarter.com/projects/foo/bar/pledge/edit", projectWithSecureUrl().editPledgeUrl());
    }

    @Test
    public void testPercentageFunded() {
        TestCase.assertEquals(50.0F, ProjectFactory.halfWayProject().percentageFunded());
        TestCase.assertEquals(100.0F, ProjectFactory.allTheWayProject().percentageFunded());
        TestCase.assertEquals(200.0F, ProjectFactory.doubledGoalProject().percentageFunded());
    }

    @Test
    public void testIsApproachingDeadline() {
        final Project projectApproachingDeadline = ProjectFactory.project().toBuilder().deadline(new DateTime().plusDays(1)).build();
        final Project projectNotApproachingDeadline = ProjectFactory.project().toBuilder().deadline(new DateTime().plusDays(3)).build();
        TestCase.assertTrue(projectApproachingDeadline.isApproachingDeadline());
        TestCase.assertFalse(projectNotApproachingDeadline.isApproachingDeadline());
    }
}

