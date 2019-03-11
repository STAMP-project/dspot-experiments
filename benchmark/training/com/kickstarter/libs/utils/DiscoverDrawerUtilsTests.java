package com.kickstarter.libs.utils;


import com.kickstarter.KSRobolectricTestCase;
import com.kickstarter.mock.factories.CategoryFactory;
import com.kickstarter.mock.factories.UserFactory;
import com.kickstarter.models.Category;
import com.kickstarter.services.DiscoveryParams;
import com.kickstarter.ui.adapters.data.NavigationDrawerData;
import java.util.Arrays;
import java.util.List;
import junit.framework.TestCase;
import org.junit.Test;


public class DiscoverDrawerUtilsTests extends KSRobolectricTestCase {
    static final List<Category> categories = Arrays.asList(CategoryFactory.artCategory(), CategoryFactory.ceramicsCategory(), CategoryFactory.textilesCategory(), CategoryFactory.photographyCategory(), CategoryFactory.musicCategory(), CategoryFactory.bluesCategory(), CategoryFactory.worldMusicCategory());

    @Test
    public void testDeriveNavigationDrawerData_LoggedOut_DefaultSelected() {
        final NavigationDrawerData data = DiscoveryDrawerUtils.deriveNavigationDrawerData(DiscoverDrawerUtilsTests.categories, DiscoveryParams.builder().build(), null, null);
        TestCase.assertEquals(5, data.sections().size());
        TestCase.assertEquals(1, data.sections().get(0).rows().size());
        TestCase.assertEquals(1, data.sections().get(1).rows().size());
        TestCase.assertEquals(1, data.sections().get(2).rows().size());
        TestCase.assertEquals(1, data.sections().get(3).rows().size());
        TestCase.assertEquals(1, data.sections().get(4).rows().size());
    }

    @Test
    public void testDeriveNavigationDrawerData_LoggedIn_DefaultSelected() {
        final NavigationDrawerData data = DiscoveryDrawerUtils.deriveNavigationDrawerData(DiscoverDrawerUtilsTests.categories, DiscoveryParams.builder().build(), null, UserFactory.user());
        TestCase.assertEquals(7, data.sections().size());
        TestCase.assertEquals(1, data.sections().get(0).rows().size());
        TestCase.assertEquals(1, data.sections().get(1).rows().size());
        TestCase.assertEquals(1, data.sections().get(2).rows().size());
        TestCase.assertEquals(1, data.sections().get(3).rows().size());
        TestCase.assertEquals(1, data.sections().get(4).rows().size());
        TestCase.assertEquals(1, data.sections().get(5).rows().size());
        TestCase.assertEquals(1, data.sections().get(6).rows().size());
    }

    @Test
    public void testDeriveNavigationDrawerData_LoggedIn_NoRecommendations_DefaultSelected() {
        final NavigationDrawerData data = DiscoveryDrawerUtils.deriveNavigationDrawerData(DiscoverDrawerUtilsTests.categories, DiscoveryParams.builder().build(), null, UserFactory.noRecommendations());
        TestCase.assertEquals(6, data.sections().size());
        TestCase.assertEquals(1, data.sections().get(0).rows().size());
        TestCase.assertEquals(1, data.sections().get(1).rows().size());
        TestCase.assertEquals(1, data.sections().get(2).rows().size());
        TestCase.assertEquals(1, data.sections().get(3).rows().size());
        TestCase.assertEquals(1, data.sections().get(4).rows().size());
        TestCase.assertEquals(1, data.sections().get(5).rows().size());
    }

    @Test
    public void testDeriveNavigationDrawerData_LoggedIn_Social_DefaultSelected() {
        final NavigationDrawerData data = DiscoveryDrawerUtils.deriveNavigationDrawerData(DiscoverDrawerUtilsTests.categories, DiscoveryParams.builder().build(), null, UserFactory.socialUser());
        TestCase.assertEquals(8, data.sections().size());
        TestCase.assertEquals(1, data.sections().get(0).rows().size());
        TestCase.assertEquals(1, data.sections().get(1).rows().size());
        TestCase.assertEquals(1, data.sections().get(2).rows().size());
        TestCase.assertEquals(1, data.sections().get(3).rows().size());
        TestCase.assertEquals(1, data.sections().get(4).rows().size());
        TestCase.assertEquals(1, data.sections().get(5).rows().size());
        TestCase.assertEquals(1, data.sections().get(6).rows().size());
        TestCase.assertEquals(1, data.sections().get(7).rows().size());
    }

    @Test
    public void testDeriveNavigationDrawerData_LoggedOut_ArtExpanded() {
        final NavigationDrawerData data = DiscoveryDrawerUtils.deriveNavigationDrawerData(DiscoverDrawerUtilsTests.categories, DiscoveryParams.builder().build(), CategoryFactory.artCategory(), null);
        TestCase.assertEquals(5, data.sections().size());
        TestCase.assertEquals(1, data.sections().get(0).rows().size());
        TestCase.assertEquals(1, data.sections().get(1).rows().size());
        TestCase.assertEquals(4, data.sections().get(2).rows().size());
        TestCase.assertEquals(1, data.sections().get(3).rows().size());
        TestCase.assertEquals(1, data.sections().get(4).rows().size());
    }
}

