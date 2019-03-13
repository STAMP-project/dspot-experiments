package com.kickstarter.libs.utils;


import Project.Urls.Web;
import android.content.SharedPreferences;
import com.kickstarter.KSRobolectricTestCase;
import com.kickstarter.libs.MockSharedPreferences;
import com.kickstarter.libs.RefTag;
import com.kickstarter.mock.factories.ProjectFactory;
import com.kickstarter.models.Project;
import java.net.CookieManager;
import java.net.CookieStore;
import java.net.HttpCookie;
import junit.framework.TestCase;
import org.junit.Test;


public final class RefTagUtilsTest extends KSRobolectricTestCase {
    static final SharedPreferences sharedPreferences = new MockSharedPreferences();

    @Test
    public void testCookieNameForProject() {
        final Project project = ProjectFactory.project();
        TestCase.assertEquals(("ref_" + (String.valueOf(project.id()))), RefTagUtils.cookieNameForProject(project));
    }

    @Test
    public void testCookieValueForRefTag() {
        final RefTag refTag = RefTag.from("test");
        TestCase.assertEquals(("test%3F" + (SystemUtils.secondsSinceEpoch())), RefTagUtils.cookieValueForRefTag(refTag));
    }

    @Test
    public void testStoredCookieRefTagForProject() {
        final CookieManager cookieManager = new CookieManager();
        final CookieStore cookieStore = cookieManager.getCookieStore();
        final Project project = ProjectFactory.project();
        final RefTag refTag = RefTag.recommended();
        // set the cookie and retrieve the ref tag
        cookieStore.add(null, new HttpCookie(("ref_" + (project.id())), (((refTag.tag()) + "%3F") + (SystemUtils.secondsSinceEpoch()))));
        final RefTag retrievedRefTag = RefTagUtils.storedCookieRefTagForProject(project, cookieManager, RefTagUtilsTest.sharedPreferences);
        TestCase.assertNotNull(retrievedRefTag);
        TestCase.assertEquals(refTag, retrievedRefTag);
    }

    @Test
    public void testBuildCookieForRefTagAndProject_WithWellFormedUrl() {
        final Project project = ProjectFactory.project();
        final RefTag refTag = RefTag.category();
        final HttpCookie cookie = RefTagUtils.buildCookieWithRefTagAndProject(refTag, project);
        TestCase.assertNotNull(cookie);
        TestCase.assertEquals(ProjectUtils.timeInSecondsUntilDeadline(project).longValue(), cookie.getMaxAge());
        TestCase.assertEquals("www.kickstarter.com", cookie.getDomain());
    }

    @Test
    public void testBuildCookieForRefTagAndProject_WithMalformedUrl() {
        final Project.Urls.Web webUrls = ProjectFactory.project().urls().web().toBuilder().project("such:\\bad^<data").build();
        final Project.Urls urls = ProjectFactory.project().urls().toBuilder().web(webUrls).build();
        final Project project = ProjectFactory.project().toBuilder().urls(urls).build();
        final RefTag refTag = RefTag.category();
        final HttpCookie cookie = RefTagUtils.buildCookieWithRefTagAndProject(refTag, project);
        TestCase.assertNull(cookie);
    }

    @Test
    public void testFindRefTagCookieForProject_WhenCookieExists() {
        final CookieManager cookieManager = new CookieManager();
        final CookieStore cookieStore = cookieManager.getCookieStore();
        final Project project = ProjectFactory.project();
        final RefTag refTag = RefTag.recommended();
        // set and retrieve the cookie
        cookieStore.add(null, new HttpCookie(("ref_" + (project.id())), (((refTag.tag()) + "%3F") + (SystemUtils.secondsSinceEpoch()))));
        final HttpCookie cookie = RefTagUtils.findRefTagCookieForProject(project, cookieManager, RefTagUtilsTest.sharedPreferences);
        TestCase.assertNotNull(cookie);
        TestCase.assertEquals(RefTagUtils.cookieNameForProject(project), cookie.getName());
        TestCase.assertEquals(RefTagUtils.cookieValueForRefTag(refTag), cookie.getValue());
    }

    @Test
    public void testFindRefTagCookieForProject_WhenCookieDoesNotExist() {
        final CookieManager cookieManager = new CookieManager();
        final Project project = ProjectFactory.project();
        // retrieve the cookie
        final HttpCookie cookie = RefTagUtils.findRefTagCookieForProject(project, cookieManager, RefTagUtilsTest.sharedPreferences);
        TestCase.assertNull(cookie);
    }
}

