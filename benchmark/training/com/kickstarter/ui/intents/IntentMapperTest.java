package com.kickstarter.ui.intents;


import android.content.Intent;
import android.net.Uri;
import com.kickstarter.KSRobolectricTestCase;
import com.kickstarter.ui.intentmappers.IntentMapper;
import junit.framework.TestCase;
import org.junit.Test;


public class IntentMapperTest extends KSRobolectricTestCase {
    @Test
    public void testIntentMapper_EmitsFromAppBanner() {
        final Uri uri = Uri.parse("https://www.kickstarter.com/discover");
        final Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        final Uri appBannerUri = Uri.parse("https://www.kickstarter.com/?app_banner=1");
        final Intent appBannerIntent = new Intent(Intent.ACTION_VIEW, appBannerUri);
        TestCase.assertFalse(IntentMapper.appBannerIsSet(intent));
        TestCase.assertTrue(IntentMapper.appBannerIsSet(appBannerIntent));
    }
}

