/**
 * Copyright (c) 2014-present, Facebook, Inc. All rights reserved.
 *
 * You are hereby granted a non-exclusive, worldwide, royalty-free license to use,
 * copy, modify, and distribute this software in source code or binary form for use
 * in connection with the web services and APIs provided by Facebook.
 *
 * As with any software that integrates with the Facebook platform, your use of
 * this software is subject to the Facebook Developer Principles and Policies
 * [http://developers.facebook.com/policy/]. This copyright notice shall be
 * included in all copies or substantial portions of the software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.facebook.applinks;


import AppLinkData.BUNDLE_APPLINK_ARGS_KEY;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import com.facebook.FacebookTestCase;
import org.junit.Assert;
import org.junit.Test;


public class AppLinkDataTest extends FacebookTestCase {
    private static final String TARGET_URI_STRING = "http://test.app/foo";

    private static final String FB_REF_KEY = "fb_ref";

    private static final String FB_REF_VALUE = "foobar";

    private static final String REFERER_DATA_KEY = "referer_data";

    private static final String EXTRA_ARGS_KEY = "extra_args";

    private static final String EXTRA_ARGS_VALUE = "extra_value";

    private static final String TARGET_URL_KEY = "target_url";

    private static final String USER_AGENT_KEY = "user_agent";

    private static final String USER_AGENT_VALUE = "foobarUserAgent";

    private static final String EXTRAS_KEY = "extras";

    private static final String DEEPLINK_CONTEXT_KEY = "deeplink_context";

    private static final String PROMO_CODE_KEY = "promo_code";

    private static final String PROMO_CODE = "PROMO1";

    private static final String JSON_DATA_REGULAR = ((((((((("{" + ((("\"version\":2," + "\"bridge_args\": {\"method\": \"applink\"},") + "\"method_args\": {") + "    \"ref\": \"")) + (AppLinkDataTest.FB_REF_VALUE)) + "\",") + "    \"") + (AppLinkDataTest.TARGET_URL_KEY)) + "\": \"") + (AppLinkDataTest.TARGET_URI_STRING)) + "\"") + "  }") + "}";

    private static final String JSON_DATA_REGULAR_WITH_NESTED_ARRAY = (((((((((("{" + ((("\"version\":2," + "\"bridge_args\": {\"method\": \"applink\"},") + "\"method_args\": {") + "    \"ref\": \"")) + (AppLinkDataTest.FB_REF_VALUE)) + "\",") + "    \"") + (AppLinkDataTest.TARGET_URL_KEY)) + "\": \"") + (AppLinkDataTest.TARGET_URI_STRING)) + "\",") + "    \"other\": [ [1, 2], [3, 4] ]") + "  }") + "}";

    private static final String JSON_DATA_WITH_REFERER_DATA = ((((((((((((((((("{" + (((("\"version\":2," + "\"bridge_args\": {\"method\": \"applink\"},") + "\"method_args\": {") + "    \"referer_data\" : {") + "      \"")) + (AppLinkDataTest.FB_REF_KEY)) + "\": \"") + (AppLinkDataTest.FB_REF_VALUE)) + "\",") + "      \"") + (AppLinkDataTest.EXTRA_ARGS_KEY)) + "\": \"") + (AppLinkDataTest.EXTRA_ARGS_VALUE)) + "\"") + "    },") + "    \"") + (AppLinkDataTest.TARGET_URL_KEY)) + "\": \"") + (AppLinkDataTest.TARGET_URI_STRING)) + "\"") + "  }") + "}";

    private static final String JSON_DATA_WITH_DEEPLINK_CONTEXT = (((((((((((((((((((((("{" + ((("\"version\":2," + "\"bridge_args\": {\"method\": \"applink\"},") + "\"method_args\": {") + "    \"ref\": \"")) + (AppLinkDataTest.FB_REF_VALUE)) + "\",") + "    \"") + (AppLinkDataTest.TARGET_URL_KEY)) + "\": \"") + (AppLinkDataTest.TARGET_URI_STRING)) + "\",") + "    \"") + (AppLinkDataTest.EXTRAS_KEY)) + "\": {") + "        \"") + (AppLinkDataTest.DEEPLINK_CONTEXT_KEY)) + "\": {") + "            \"") + (AppLinkDataTest.PROMO_CODE_KEY)) + "\": \"") + (AppLinkDataTest.PROMO_CODE)) + "\"") + "        }") + "    }") + "  }") + "}";

    private static class MockActivityWithAppLinkData extends Activity {
        public Intent getIntent() {
            Uri targetUri = Uri.parse(AppLinkDataTest.TARGET_URI_STRING);
            Intent intent = new Intent(Intent.ACTION_VIEW, targetUri);
            Bundle applinks = new Bundle();
            Bundle refererData = new Bundle();
            Bundle extras = new Bundle();
            String deeplinkContext = ((("{\"" + (AppLinkDataTest.PROMO_CODE_KEY)) + "\": \"") + (AppLinkDataTest.PROMO_CODE)) + "\"}";
            extras.putString(AppLinkDataTest.DEEPLINK_CONTEXT_KEY, deeplinkContext);
            refererData.putString(AppLinkDataTest.FB_REF_KEY, AppLinkDataTest.FB_REF_VALUE);
            refererData.putString(AppLinkDataTest.EXTRA_ARGS_KEY, AppLinkDataTest.EXTRA_ARGS_VALUE);
            applinks.putBundle(AppLinkDataTest.REFERER_DATA_KEY, refererData);
            applinks.putString(AppLinkDataTest.TARGET_URL_KEY, AppLinkDataTest.TARGET_URI_STRING);
            applinks.putString(AppLinkDataTest.USER_AGENT_KEY, AppLinkDataTest.USER_AGENT_VALUE);
            applinks.putBundle(AppLinkDataTest.EXTRAS_KEY, extras);
            intent.putExtra("al_applink_data", applinks);
            return intent;
        }
    }

    private static class MockActivityWithJsonData extends Activity {
        private String jsonString;

        public MockActivityWithJsonData(String jsonString) {
            this.jsonString = jsonString;
        }

        public Intent getIntent() {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.putExtra(BUNDLE_APPLINK_ARGS_KEY, jsonString);
            return intent;
        }
    }

    @Test
    public void testCreateFromAlApplinkData() {
        AppLinkData appLinkData = AppLinkData.createFromActivity(new AppLinkDataTest.MockActivityWithAppLinkData());
        Assert.assertNotNull("app link data not null", appLinkData);
        Assert.assertEquals("ref param", AppLinkDataTest.FB_REF_VALUE, appLinkData.getRef());
        Assert.assertEquals("target_url", AppLinkDataTest.TARGET_URI_STRING, appLinkData.getTargetUri().toString());
        Bundle args = appLinkData.getArgumentBundle();
        Assert.assertNotNull("app link args not null", args);
        Assert.assertEquals("user agent", AppLinkDataTest.USER_AGENT_VALUE, args.getString(AppLinkDataTest.USER_AGENT_KEY));
        Bundle refererData = appLinkData.getRefererData();
        Assert.assertNotNull("referer data not null", refererData);
        Assert.assertEquals("ref param in referer data", AppLinkDataTest.FB_REF_VALUE, refererData.getString(AppLinkDataTest.FB_REF_KEY));
        Assert.assertEquals("extra param", AppLinkDataTest.EXTRA_ARGS_VALUE, refererData.getString(AppLinkDataTest.EXTRA_ARGS_KEY));
        Assert.assertEquals("promo_code", AppLinkDataTest.PROMO_CODE, appLinkData.getPromotionCode());
    }

    @Test
    public void testCreateFromJson() {
        AppLinkData appLinkData = AppLinkData.createFromActivity(new AppLinkDataTest.MockActivityWithJsonData(AppLinkDataTest.JSON_DATA_REGULAR));
        Assert.assertNotNull("app link data not null", appLinkData);
        Assert.assertEquals("ref param", AppLinkDataTest.FB_REF_VALUE, appLinkData.getRef());
        Assert.assertEquals("target_url", AppLinkDataTest.TARGET_URI_STRING, appLinkData.getTargetUri().toString());
        Bundle args = appLinkData.getArgumentBundle();
        Assert.assertNotNull("app link args not null", args);
        Assert.assertNull("user agent", args.getString(AppLinkDataTest.USER_AGENT_KEY));
        Bundle refererData = appLinkData.getRefererData();
        Assert.assertNull("referer data", refererData);
    }

    @Test
    public void testCreateFromJsonWithNestedArray() {
        AppLinkData appLinkData = AppLinkData.createFromActivity(new AppLinkDataTest.MockActivityWithJsonData(AppLinkDataTest.JSON_DATA_REGULAR_WITH_NESTED_ARRAY));
        Assert.assertNull(appLinkData);
    }

    @Test
    public void testCreateFromJsonWithRefererData() {
        AppLinkData appLinkData = AppLinkData.createFromActivity(new AppLinkDataTest.MockActivityWithJsonData(AppLinkDataTest.JSON_DATA_WITH_REFERER_DATA));
        Assert.assertNotNull("app link data not null", appLinkData);
        Assert.assertEquals("ref param", AppLinkDataTest.FB_REF_VALUE, appLinkData.getRef());
        Assert.assertEquals("target_url", AppLinkDataTest.TARGET_URI_STRING, appLinkData.getTargetUri().toString());
        Bundle args = appLinkData.getArgumentBundle();
        Assert.assertNotNull("app link args not null", args);
        Assert.assertNull("user agent", args.getString(AppLinkDataTest.USER_AGENT_KEY));
        Bundle refererData = appLinkData.getRefererData();
        Assert.assertNotNull("referer data", refererData);
        Assert.assertEquals("ref param in referer data", AppLinkDataTest.FB_REF_VALUE, refererData.getString(AppLinkDataTest.FB_REF_KEY));
        Assert.assertEquals("extra param", AppLinkDataTest.EXTRA_ARGS_VALUE, refererData.getString(AppLinkDataTest.EXTRA_ARGS_KEY));
    }

    @Test
    public void testCreateFromJsonWithDeeplinkContext() {
        AppLinkData appLinkData = AppLinkData.createFromActivity(new AppLinkDataTest.MockActivityWithJsonData(AppLinkDataTest.JSON_DATA_WITH_DEEPLINK_CONTEXT));
        Assert.assertNotNull("app link data not null", appLinkData);
        Assert.assertEquals("ref param", AppLinkDataTest.FB_REF_VALUE, appLinkData.getRef());
        Assert.assertEquals("target_url", AppLinkDataTest.TARGET_URI_STRING, appLinkData.getTargetUri().toString());
        Assert.assertEquals("promo_code", AppLinkDataTest.PROMO_CODE, appLinkData.getPromotionCode());
        Bundle args = appLinkData.getArgumentBundle();
        Assert.assertNotNull("app link args not null", args);
        Assert.assertNull("user agent", args.getString(AppLinkDataTest.USER_AGENT_KEY));
        Bundle refererData = appLinkData.getRefererData();
        Assert.assertNull("referer data", refererData);
    }
}

