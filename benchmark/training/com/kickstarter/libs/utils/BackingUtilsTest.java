package com.kickstarter.libs.utils;


import com.kickstarter.KSRobolectricTestCase;
import com.kickstarter.mock.factories.BackingFactory;
import com.kickstarter.mock.factories.ProjectFactory;
import com.kickstarter.mock.factories.RewardFactory;
import com.kickstarter.models.Backing;
import com.kickstarter.models.Project;
import junit.framework.TestCase;
import org.junit.Test;


public final class BackingUtilsTest extends KSRobolectricTestCase {
    @Test
    public void testIsBacked() {
        final Project backedProject = ProjectFactory.backedProject();
        TestCase.assertTrue(BackingUtils.isBacked(backedProject, backedProject.backing().reward()));
        TestCase.assertFalse(BackingUtils.isBacked(backedProject, RewardFactory.reward()));
    }

    @Test
    public void testIsShippable() {
        final Backing backingWithShipping = BackingFactory.backing().toBuilder().reward(RewardFactory.rewardWithShipping()).build();
        TestCase.assertTrue(BackingUtils.isShippable(backingWithShipping));
        TestCase.assertFalse(BackingUtils.isShippable(BackingFactory.backing()));
    }
}

