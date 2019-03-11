package com.kickstarter.services;


import DiscoveryParams.Sort.ENDING_SOON;
import DiscoveryParams.Sort.NEWEST;
import DiscoveryParams.Sort.POPULAR;
import DiscoveryParams.State.SUCCESSFUL;
import android.net.Uri;
import com.kickstarter.KSRobolectricTestCase;
import com.kickstarter.mock.factories.CategoryFactory;
import com.kickstarter.models.Category;
import junit.framework.TestCase;
import org.junit.Test;


public final class DiscoveryParamsTest extends KSRobolectricTestCase {
    @Test
    public void testFromUri_discoverRoot() {
        final Uri uri = Uri.parse("https://www.kickstarter.com/discover");
        TestCase.assertEquals(DiscoveryParams.builder().build(), DiscoveryParams.fromUri(uri));
    }

    @Test
    public void testFromUri_backed() {
        final Uri uri = Uri.parse("https://www.kickstarter.com/discover/advanced?backed=1");
        TestCase.assertEquals(DiscoveryParams.builder().backed(1).build(), DiscoveryParams.fromUri(uri));
    }

    @Test
    public void testFromUri_categories() {
        final DiscoveryParams params = DiscoveryParams.builder().categoryParam("music").build();
        final Uri categoryUri = Uri.parse("https://www.kickstarter.com/discover/categories/music");
        TestCase.assertEquals(params, DiscoveryParams.fromUri(categoryUri));
        final Uri advancedUri = Uri.parse("https://www.kickstarter.com/discover/advanced?category_id=music");
        TestCase.assertEquals(params, DiscoveryParams.fromUri(advancedUri));
    }

    @Test
    public void testFromUri_filters() {
        final DiscoveryParams allParams = DiscoveryParams.builder().recommended(true).social(1).staffPicks(true).starred(1).build();
        final Uri allParamsUri = Uri.parse("https://www.kickstarter.com/discover/advanced?recommended=true&social=1&staff_picks=true&starred=1");
        TestCase.assertEquals(allParams, DiscoveryParams.fromUri(allParamsUri));
        final DiscoveryParams recommendedParams = DiscoveryParams.builder().recommended(true).build();
        final Uri recommendedUri = Uri.parse("https://www.kickstarter.com/discover/advanced?recommended=true");
        TestCase.assertEquals(recommendedParams, DiscoveryParams.fromUri(recommendedUri));
        final DiscoveryParams socialParams = DiscoveryParams.builder().social(1).build();
        final Uri socialUri = Uri.parse("https://www.kickstarter.com/discover/advanced?social=1");
        TestCase.assertEquals(socialParams, DiscoveryParams.fromUri(socialUri));
        final DiscoveryParams staffPickParams = DiscoveryParams.builder().staffPicks(true).build();
        final Uri staffPicksUri = Uri.parse("https://www.kickstarter.com/discover/advanced?staff_picks=true");
        TestCase.assertEquals(staffPickParams, DiscoveryParams.fromUri(staffPicksUri));
        final DiscoveryParams starredParams = DiscoveryParams.builder().starred(1).build();
        final Uri starredUri = Uri.parse("https://www.kickstarter.com/discover/advanced?starred=1");
        TestCase.assertEquals(starredParams, DiscoveryParams.fromUri(starredUri));
    }

    @Test
    public void testFromUri_locations() {
        final DiscoveryParams params = DiscoveryParams.builder().locationParam("sydney-au").build();
        final Uri placesUri = Uri.parse("https://www.kickstarter.com/discover/places/sydney-au");
        TestCase.assertEquals(params, DiscoveryParams.fromUri(placesUri));
        final Uri advancedUri = Uri.parse("https://www.kickstarter.com/discover/advanced?woe_id=sydney-au");
        TestCase.assertEquals(params, DiscoveryParams.fromUri(advancedUri));
    }

    @Test
    public void testFromUri_customScopes() {
        final DiscoveryParams endingSoonParams = DiscoveryParams.builder().sort(ENDING_SOON).build();
        final Uri endingSoonUri = Uri.parse("https://www.kickstarter.com/discover/ending-soon");
        TestCase.assertEquals(endingSoonParams, DiscoveryParams.fromUri(endingSoonUri));
        final DiscoveryParams newestParams = DiscoveryParams.builder().sort(NEWEST).staffPicks(true).build();
        final Uri newestUri = Uri.parse("https://www.kickstarter.com/discover/newest");
        TestCase.assertEquals(newestParams, DiscoveryParams.fromUri(newestUri));
        final DiscoveryParams popularParams = DiscoveryParams.builder().sort(POPULAR).build();
        final Uri popularUri = Uri.parse("https://www.kickstarter.com/discover/popular");
        TestCase.assertEquals(popularParams, DiscoveryParams.fromUri(popularUri));
        final DiscoveryParams recentlyLaunchedParams = DiscoveryParams.builder().sort(NEWEST).build();
        final Uri recentlyLaunchedUri = Uri.parse("https://www.kickstarter.com/discover/recently-launched");
        TestCase.assertEquals(recentlyLaunchedParams, DiscoveryParams.fromUri(recentlyLaunchedUri));
        final DiscoveryParams smallProjectsParams = DiscoveryParams.builder().pledged(0).build();
        final Uri smallProjectsUri = Uri.parse("https://www.kickstarter.com/discover/small-projects");
        TestCase.assertEquals(smallProjectsParams, DiscoveryParams.fromUri(smallProjectsUri));
        final DiscoveryParams socialParams = DiscoveryParams.builder().social(0).build();
        final Uri socialUri = Uri.parse("https://www.kickstarter.com/discover/social");
        TestCase.assertEquals(socialParams, DiscoveryParams.fromUri(socialUri));
        final DiscoveryParams successfulParams = DiscoveryParams.builder().sort(ENDING_SOON).state(SUCCESSFUL).build();
        final Uri successfulUri = Uri.parse("https://www.kickstarter.com/discover/successful");
        TestCase.assertEquals(successfulParams, DiscoveryParams.fromUri(successfulUri));
    }

    @Test
    public void testFromUri_pagination() {
        final DiscoveryParams params = DiscoveryParams.builder().page(5).perPage(21).build();
        final Uri uri = Uri.parse("https://www.kickstarter.com/discover/advanced?page=5&per_page=21");
        TestCase.assertEquals(params, DiscoveryParams.fromUri(uri));
    }

    @Test
    public void testFromUri_sort() {
        final DiscoveryParams params = DiscoveryParams.builder().sort(POPULAR).build();
        final Uri uri = Uri.parse("https://www.kickstarter.com/discover/advanced?sort=popularity");
        TestCase.assertEquals(params, DiscoveryParams.fromUri(uri));
    }

    @Test
    public void testFromUri_term() {
        final DiscoveryParams params = DiscoveryParams.builder().term("skull graphic tee").build();
        final Uri advancedUri = Uri.parse("https://www.kickstarter.com/discover/advanced?term=skull+graphic+tee");
        TestCase.assertEquals(params, DiscoveryParams.fromUri(advancedUri));
        final Uri searchUri = Uri.parse("https://www.kickstarter.com/projects/search?term=skull+graphic+tee");
        TestCase.assertEquals(params, DiscoveryParams.fromUri(searchUri));
    }

    @Test
    public void testShouldIncludeFeatured() {
        final Category nonRootCategory = CategoryFactory.bluesCategory();
        final DiscoveryParams nonRootParams = DiscoveryParams.builder().category(nonRootCategory).build();
        TestCase.assertEquals(false, nonRootParams.shouldIncludeFeatured());
        final Category rootCategory = CategoryFactory.gamesCategory();
        final DiscoveryParams rootParams = DiscoveryParams.builder().category(rootCategory).build();
        TestCase.assertEquals(true, rootParams.shouldIncludeFeatured());
    }
}

