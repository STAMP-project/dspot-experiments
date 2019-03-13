package com.kickstarter.libs.utils;


import ProjectUtils.Metadata.BACKING;
import ProjectUtils.Metadata.CATEGORY_FEATURED;
import ProjectUtils.Metadata.SAVING;
import android.util.Pair;
import com.kickstarter.KSRobolectricTestCase;
import com.kickstarter.mock.factories.ProjectFactory;
import com.kickstarter.mock.factories.UserFactory;
import com.kickstarter.models.Project;
import com.kickstarter.services.DiscoveryParams;
import java.util.Collections;
import junit.framework.TestCase;
import org.joda.time.DateTime;
import org.junit.Test;


public final class ProjectUtilsTest extends KSRobolectricTestCase {
    @Test
    public void testCombineProjectsAndParams() {
        final Project project = ProjectFactory.project();
        final DiscoveryParams discoveryParams = DiscoveryParams.builder().build();
        TestCase.assertEquals(ProjectUtils.combineProjectsAndParams(Collections.singletonList(project), discoveryParams), Collections.singletonList(Pair.create(project, discoveryParams)));
    }

    @Test
    public void testIsCompleted() {
        TestCase.assertTrue(ProjectUtils.isCompleted(ProjectFactory.successfulProject()));
        TestCase.assertFalse(ProjectUtils.isCompleted(ProjectFactory.project()));
    }

    @Test
    public void testIsUsUserViewingNonUsProject() {
        TestCase.assertTrue(ProjectUtils.isUSUserViewingNonUSProject(UserFactory.user().location().country(), ProjectFactory.ukProject().country()));
        TestCase.assertFalse(ProjectUtils.isUSUserViewingNonUSProject(UserFactory.user().location().country(), ProjectFactory.project().country()));
        TestCase.assertFalse(ProjectUtils.isUSUserViewingNonUSProject(UserFactory.germanUser().location().country(), ProjectFactory.caProject().country()));
    }

    @Test
    public void testMetadataForProject() {
        TestCase.assertEquals(null, ProjectUtils.metadataForProject(ProjectFactory.project()));
        TestCase.assertEquals(BACKING, ProjectUtils.metadataForProject(ProjectFactory.backedProject()));
        TestCase.assertEquals(CATEGORY_FEATURED, ProjectUtils.metadataForProject(ProjectFactory.featured()));
        TestCase.assertEquals(SAVING, ProjectUtils.metadataForProject(ProjectFactory.saved()));
        final Project savedAndBacked = ProjectFactory.backedProject().toBuilder().isStarred(true).build();
        TestCase.assertEquals(BACKING, ProjectUtils.metadataForProject(savedAndBacked));
        final DateTime now = new DateTime();
        final Project savedAndFeatured = ProjectFactory.saved().toBuilder().featuredAt(now).build();
        TestCase.assertEquals(SAVING, ProjectUtils.metadataForProject(savedAndFeatured));
        final Project savedBackedFeatured = ProjectFactory.backedProject().toBuilder().featuredAt(now).isStarred(true).build();
        TestCase.assertEquals(BACKING, ProjectUtils.metadataForProject(savedBackedFeatured));
    }

    @Test
    public void testPhotoHeightFromWidthRatio() {
        TestCase.assertEquals(360, ProjectUtils.photoHeightFromWidthRatio(640));
        TestCase.assertEquals(576, ProjectUtils.photoHeightFromWidthRatio(1024));
    }
}

