package com.ctrip.framework.apollo.common.conditional;


import com.ctrip.framework.apollo.common.condition.ConditionalOnMissingProfile;
import com.ctrip.framework.apollo.common.condition.ConditionalOnProfile;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 *
 *
 * @author Jason Song(song_s@ctrip.com)
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ConditionalOnProfileTest.TestConfiguration.class)
@ActiveProfiles({ ConditionalOnProfileTest.SOME_PROFILE, ConditionalOnProfileTest.ANOTHER_PROFILE })
public class ConditionalOnProfileTest {
    static final String SOME_PROFILE = "someProfile";

    static final String ANOTHER_PROFILE = "anotherProfile";

    static final String YET_ANOTHER_PROFILE = "yetAnotherProfile";

    static boolean someConfigurationEnabled = false;

    static boolean anotherConfigurationEnabled = false;

    static boolean yetAnotherConfigurationEnabled = false;

    static boolean combinedConfigurationEnabled = false;

    static boolean anotherCombinedConfigurationEnabled = false;

    @Test
    public void test() throws Exception {
        Assert.assertTrue(ConditionalOnProfileTest.someConfigurationEnabled);
        Assert.assertFalse(ConditionalOnProfileTest.anotherConfigurationEnabled);
        Assert.assertTrue(ConditionalOnProfileTest.yetAnotherConfigurationEnabled);
        Assert.assertTrue(ConditionalOnProfileTest.combinedConfigurationEnabled);
        Assert.assertFalse(ConditionalOnProfileTest.anotherCombinedConfigurationEnabled);
    }

    @Configuration
    static class TestConfiguration {
        @Configuration
        @ConditionalOnProfile(ConditionalOnProfileTest.SOME_PROFILE)
        static class SomeConfiguration {
            {
                ConditionalOnProfileTest.someConfigurationEnabled = true;
            }
        }

        @Configuration
        @ConditionalOnMissingProfile(ConditionalOnProfileTest.ANOTHER_PROFILE)
        static class AnotherConfiguration {
            {
                ConditionalOnProfileTest.anotherConfigurationEnabled = true;
            }
        }

        @Configuration
        @ConditionalOnMissingProfile(ConditionalOnProfileTest.YET_ANOTHER_PROFILE)
        static class YetAnotherConfiguration {
            {
                ConditionalOnProfileTest.yetAnotherConfigurationEnabled = true;
            }
        }

        @Configuration
        @ConditionalOnProfile({ ConditionalOnProfileTest.SOME_PROFILE, ConditionalOnProfileTest.ANOTHER_PROFILE })
        @ConditionalOnMissingProfile(ConditionalOnProfileTest.YET_ANOTHER_PROFILE)
        static class CombinedConfiguration {
            {
                ConditionalOnProfileTest.combinedConfigurationEnabled = true;
            }
        }

        @Configuration
        @ConditionalOnProfile(ConditionalOnProfileTest.SOME_PROFILE)
        @ConditionalOnMissingProfile({ ConditionalOnProfileTest.YET_ANOTHER_PROFILE, ConditionalOnProfileTest.ANOTHER_PROFILE })
        static class AnotherCombinedConfiguration {
            {
                ConditionalOnProfileTest.anotherCombinedConfigurationEnabled = true;
            }
        }
    }
}

