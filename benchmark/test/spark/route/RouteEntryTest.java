package spark.route;


import HttpMethod.after;
import HttpMethod.before;
import HttpMethod.get;
import SparkUtils.ALL_PATHS;
import org.junit.Assert;
import org.junit.Test;
import spark.utils.SparkUtils;

import static HttpMethod.after;
import static HttpMethod.before;
import static HttpMethod.get;
import static HttpMethod.post;


public class RouteEntryTest {
    @Test
    public void testMatches_BeforeAndAllPaths() {
        RouteEntry entry = new RouteEntry();
        entry.httpMethod = before;
        entry.path = SparkUtils.ALL_PATHS;
        Assert.assertTrue(("Should return true because HTTP method is \"Before\", the methods of route and match request match," + " and the path provided is same as ALL_PATHS (+/*paths)"), entry.matches(before, ALL_PATHS));
    }

    @Test
    public void testMatches_AfterAndAllPaths() {
        RouteEntry entry = new RouteEntry();
        entry.httpMethod = after;
        entry.path = SparkUtils.ALL_PATHS;
        Assert.assertTrue(("Should return true because HTTP method is \"After\", the methods of route and match request match," + " and the path provided is same as ALL_PATHS (+/*paths)"), entry.matches(after, ALL_PATHS));
    }

    @Test
    public void testMatches_NotAllPathsAndDidNotMatchHttpMethod() {
        RouteEntry entry = new RouteEntry();
        entry.httpMethod = post;
        entry.path = "/test";
        Assert.assertFalse("Should return false because path names did not match", entry.matches(get, "/path"));
    }

    @Test
    public void testMatches_RouteDoesNotEndWithSlash() {
        RouteEntry entry = new RouteEntry();
        entry.httpMethod = get;
        entry.path = "/test";
        Assert.assertFalse(("Should return false because route path does not end with a slash, does not end with " + "a wildcard, and the route pah supplied ends with a slash "), entry.matches(get, "/test/"));
    }

    @Test
    public void testMatches_PathDoesNotEndInSlash() {
        RouteEntry entry = new RouteEntry();
        entry.httpMethod = get;
        entry.path = "/test/";
        Assert.assertFalse(("Should return false because route path ends with a slash while path supplied as parameter does" + "not end with a slash"), entry.matches(get, "/test"));
    }

    @Test
    public void testMatches_MatchingPaths() {
        RouteEntry entry = new RouteEntry();
        entry.httpMethod = get;
        entry.path = "/test/";
        Assert.assertTrue("Should return true because route path and path is exactly the same", entry.matches(get, "/test/"));
    }

    @Test
    public void testMatches_WithWildcardOnEntryPath() {
        RouteEntry entry = new RouteEntry();
        entry.httpMethod = get;
        entry.path = "/test/*";
        Assert.assertTrue("Should return true because path specified is covered by the route path wildcard", entry.matches(get, "/test/me"));
    }

    @Test
    public void testMatches_PathsDoNotMatch() {
        RouteEntry entry = new RouteEntry();
        entry.httpMethod = get;
        entry.path = "/test/me";
        Assert.assertFalse("Should return false because path does not match route path", entry.matches(get, "/test/other"));
    }

    @Test
    public void testMatches_longRoutePathWildcard() {
        RouteEntry entry = new RouteEntry();
        entry.httpMethod = get;
        entry.path = "/test/this/resource/*";
        Assert.assertTrue("Should return true because path specified is covered by the route path wildcard", entry.matches(get, "/test/this/resource/child/id"));
    }
}

