package com.kickstarter.libs.utils;


import com.kickstarter.KSRobolectricTestCase;
import com.kickstarter.mock.factories.UserFactory;
import com.kickstarter.models.User;
import junit.framework.TestCase;
import org.junit.Test;


public class UserUtilsTest extends KSRobolectricTestCase {
    @Test
    public void testUserHasChanged() {
        final User user15 = UserFactory.user().toBuilder().id(15).build();
        final User user21 = UserFactory.user().toBuilder().id(21).build();
        TestCase.assertTrue(UserUtils.userHasChanged(null, UserFactory.user()));
        TestCase.assertTrue(UserUtils.userHasChanged(UserFactory.user(), null));
        TestCase.assertTrue(UserUtils.userHasChanged(user15, user21));
        TestCase.assertFalse(UserUtils.userHasChanged(null, null));
        TestCase.assertFalse(UserUtils.userHasChanged(user15, user15));
    }
}

