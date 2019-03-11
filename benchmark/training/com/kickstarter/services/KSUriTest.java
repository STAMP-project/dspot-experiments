package com.kickstarter.services;


import android.net.Uri;
import com.kickstarter.KSRobolectricTestCase;
import junit.framework.TestCase;
import org.junit.Test;


public final class KSUriTest extends KSRobolectricTestCase {
    private final Uri discoverCategoriesUri = Uri.parse("https://www.ksr.com/discover/categories/art");

    private final Uri discoverScopeUri = Uri.parse("https://www.kickstarter.com/discover/ending-soon");

    private final Uri discoverPlacesUri = Uri.parse("https://www.ksr.com/discover/places/newest");

    private final Uri newGuestCheckoutUri = Uri.parse("https://www.ksr.com/checkouts/1/guest/new");

    private final Uri projectUri = Uri.parse("https://www.ksr.com/projects/creator/project");

    private final Uri projectPreviewUri = Uri.parse("https://www.ksr.com/projects/creator/project?token=token");

    private final Uri projectSurveyUri = Uri.parse("https://www.ksr.com/projects/creator/project/surveys/survey-param");

    private final Uri updatesUri = Uri.parse("https://www.ksr.com/projects/creator/project/posts");

    private final Uri updateUri = Uri.parse("https://www.ksr.com/projects/creator/project/posts/id");

    private final Uri userSurveyUri = Uri.parse("https://www.ksr.com/users/user-param/surveys/survey-id");

    private final String webEndpoint = "https://www.ksr.com";

    @Test
    public void testKSUri_isDiscoverCategoriesPath() {
        TestCase.assertTrue(KSUri.isDiscoverCategoriesPath(this.discoverCategoriesUri.getPath()));
        TestCase.assertFalse(KSUri.isDiscoverCategoriesPath(this.discoverPlacesUri.getPath()));
    }

    @Test
    public void testKSUri_isDiscoverPlacesPath() {
        TestCase.assertTrue(KSUri.isDiscoverPlacesPath(this.discoverPlacesUri.getPath()));
        TestCase.assertFalse(KSUri.isDiscoverPlacesPath(this.discoverCategoriesUri.getPath()));
    }

    @Test
    public void testKSUri_isDiscoverScopePath() {
        TestCase.assertTrue(KSUri.isDiscoverScopePath(this.discoverScopeUri.getPath(), "ending-soon"));
    }

    @Test
    public void testKSUri_isKickstarterUri() {
        final Uri ksrUri = Uri.parse("https://www.ksr.com/discover");
        final Uri uri = Uri.parse("https://www.hello-world.org/goodbye");
        TestCase.assertTrue(KSUri.isKickstarterUri(ksrUri, this.webEndpoint));
        TestCase.assertFalse(KSUri.isKickstarterUri(uri, this.webEndpoint));
    }

    @Test
    public void testKSUri_isWebViewUri() {
        final Uri ksrUri = Uri.parse("https://www.ksr.com/project");
        final Uri uri = Uri.parse("https://www.hello-world.org/goodbye");
        final Uri ksrGraphUri = Uri.parse("https://www.ksr.com/graph");
        final Uri graphUri = Uri.parse("https://www.hello-world.org/graph");
        final Uri favIconUri = Uri.parse("https://www.ksr.com/favicon.ico");
        TestCase.assertTrue(KSUri.isWebViewUri(ksrUri, this.webEndpoint));
        TestCase.assertFalse(KSUri.isWebViewUri(uri, this.webEndpoint));
        TestCase.assertFalse(KSUri.isWebViewUri(ksrGraphUri, this.webEndpoint));
        TestCase.assertFalse(KSUri.isWebViewUri(graphUri, this.webEndpoint));
        TestCase.assertFalse(KSUri.isWebViewUri(favIconUri, this.webEndpoint));
    }

    @Test
    public void testKSUri_isKSFavIcon() {
        final Uri ksrUri = Uri.parse("https://www.ksr.com/favicon.ico");
        final Uri uri = Uri.parse("https://www.hello-world.org/goodbye");
        TestCase.assertTrue(KSUri.isKSFavIcon(ksrUri, this.webEndpoint));
        TestCase.assertFalse(KSUri.isKSFavIcon(uri, this.webEndpoint));
    }

    @Test
    public void testKSUri_isKSGraphQLUri() {
        final Uri ksrGraphUri = Uri.parse("https://www.ksr.com/graph");
        final Uri graphUri = Uri.parse("https://www.hello-world.org/graph");
        TestCase.assertTrue(KSUri.isKSGraphQLUri(ksrGraphUri, this.webEndpoint));
        TestCase.assertFalse(KSUri.isKSGraphQLUri(graphUri, this.webEndpoint));
    }

    @Test
    public void testKSUri_isModalUri() {
        final Uri modalUri = Uri.parse("https://www.ksr.com/project?modal=true");
        TestCase.assertTrue(KSUri.isModalUri(modalUri, this.webEndpoint));
        TestCase.assertFalse(KSUri.isModalUri(this.projectUri, this.webEndpoint));
    }

    @Test
    public void testKSUri_isNewGuestCheckoutUri() {
        TestCase.assertTrue(KSUri.isNewGuestCheckoutUri(this.newGuestCheckoutUri, this.webEndpoint));
    }

    @Test
    public void testKSUri_isProjectSurveyUri() {
        TestCase.assertTrue(KSUri.isProjectSurveyUri(this.projectSurveyUri, this.webEndpoint));
        TestCase.assertFalse(KSUri.isProjectSurveyUri(this.userSurveyUri, this.webEndpoint));
    }

    @Test
    public void testKSUri_isProjectUpdateCommentsUri() {
        final Uri updateCommentsUri = Uri.parse("https://www.ksr.com/projects/creator/project/posts/id/comments");
        TestCase.assertTrue(KSUri.isProjectUpdateCommentsUri(updateCommentsUri, this.webEndpoint));
        TestCase.assertFalse(KSUri.isProjectUpdateCommentsUri(this.updatesUri, this.webEndpoint));
    }

    @Test
    public void testKSUri_isProjectUpdateUri() {
        TestCase.assertTrue(KSUri.isProjectUpdateUri(this.updateUri, this.webEndpoint));
        TestCase.assertFalse(KSUri.isProjectUpdateUri(this.updatesUri, this.webEndpoint));
    }

    @Test
    public void testKSUri_isProjectUpdatesUri() {
        TestCase.assertTrue(KSUri.isProjectUpdatesUri(this.updatesUri, this.webEndpoint));
        TestCase.assertFalse(KSUri.isProjectUpdatesUri(this.updateUri, this.webEndpoint));
    }

    @Test
    public void testKSUri_isProjectUri() {
        TestCase.assertTrue(KSUri.isProjectUri(this.projectUri, this.webEndpoint));
        TestCase.assertTrue(KSUri.isProjectUri(this.projectPreviewUri, this.webEndpoint));
        TestCase.assertFalse(KSUri.isProjectUri(this.updateUri, this.webEndpoint));
    }

    @Test
    public void testKSUri_isProjectPreviewUri() {
        TestCase.assertTrue(KSUri.isProjectPreviewUri(this.projectPreviewUri, this.webEndpoint));
        TestCase.assertFalse(KSUri.isProjectPreviewUri(this.projectUri, this.webEndpoint));
    }

    @Test
    public void testKSuri_isUserSurveyUri() {
        TestCase.assertTrue(KSUri.isUserSurveyUri(this.userSurveyUri, this.webEndpoint));
        TestCase.assertFalse(KSUri.isUserSurveyUri(this.projectSurveyUri, this.webEndpoint));
    }
}

