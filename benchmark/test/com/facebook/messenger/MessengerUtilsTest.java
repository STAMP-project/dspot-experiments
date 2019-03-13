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
package com.facebook.messenger;


import Activity.RESULT_CANCELED;
import Activity.RESULT_OK;
import Intent.ACTION_SEND;
import Intent.ACTION_VIEW;
import Intent.EXTRA_STREAM;
import Intent.FLAG_GRANT_READ_URI_PERMISSION;
import MessengerThreadParams.Origin.REPLY_FLOW;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import com.facebook.FacebookPowerMockTestCase;
import com.facebook.internal.FacebookSignatureValidator;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


/**
 * Tests for {@link com.facebook.messenger.MessengerUtils}
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 18, manifest = Config.NONE)
@PrepareForTest({ FacebookSignatureValidator.class })
public class MessengerUtilsTest extends FacebookPowerMockTestCase {
    private Activity mMockActivity;

    private ContentResolver mMockContentResolver;

    @Test
    public void testMessengerIsInstalled() throws Exception {
        setupPackageManagerForMessenger(true);
        Assert.assertTrue(MessengerUtils.hasMessengerInstalled(mMockActivity));
    }

    @Test
    public void testMessengerNotInstalled() throws Exception {
        setupPackageManagerForMessenger(false);
        Assert.assertFalse(MessengerUtils.hasMessengerInstalled(mMockActivity));
    }

    @Test
    public void testShareToMessengerWith20150314Protocol() throws Exception {
        setupPackageManagerForMessenger(true);
        setupContentResolverForProtocolVersions(20150314);
        Uri uri = Uri.parse("file:///foo.jpeg");
        Uri externalUri = Uri.parse("http://example.com/foo.jpeg");
        ShareToMessengerParams params = ShareToMessengerParams.newBuilder(uri, "image/jpeg").setMetaData("{}").setExternalUri(externalUri).build();
        MessengerUtils.shareToMessenger(mMockActivity, 1, params);
        // Expect it to have launched messenger with the right intent.
        ArgumentCaptor<Intent> intentArgumentCaptor = ArgumentCaptor.forClass(Intent.class);
        Mockito.verify(mMockActivity).startActivityForResult(intentArgumentCaptor.capture(), ArgumentMatchers.eq(1));
        Intent intent = intentArgumentCaptor.getValue();
        Assert.assertEquals(ACTION_SEND, intent.getAction());
        Assert.assertEquals(FLAG_GRANT_READ_URI_PERMISSION, intent.getFlags());
        Assert.assertEquals("com.facebook.orca", intent.getPackage());
        Assert.assertEquals(uri, intent.getParcelableExtra(EXTRA_STREAM));
        Assert.assertEquals("image/jpeg", intent.getType());
        Assert.assertEquals("200", intent.getStringExtra("com.facebook.orca.extra.APPLICATION_ID"));
        Assert.assertEquals(20150314, intent.getIntExtra("com.facebook.orca.extra.PROTOCOL_VERSION", (-1)));
        Assert.assertEquals("{}", intent.getStringExtra("com.facebook.orca.extra.METADATA"));
        Assert.assertEquals(externalUri, intent.getParcelableExtra("com.facebook.orca.extra.EXTERNAL_URI"));
    }

    @Test
    public void testShareToMessengerWithNoProtocol() throws Exception {
        setupPackageManagerForMessenger(true);
        /* empty */
        setupContentResolverForProtocolVersions();
        Uri uri = Uri.parse("file:///foo.jpeg");
        Uri externalUri = Uri.parse("http://example.com/foo.jpeg");
        ShareToMessengerParams params = ShareToMessengerParams.newBuilder(uri, "image/jpeg").setMetaData("{}").setExternalUri(externalUri).build();
        MessengerUtils.shareToMessenger(mMockActivity, 1, params);
        // Expect it to have gone to the play store.
        ArgumentCaptor<Intent> intentArgumentCaptor = ArgumentCaptor.forClass(Intent.class);
        Mockito.verify(mMockActivity).startActivity(intentArgumentCaptor.capture());
        Intent intent = intentArgumentCaptor.getValue();
        Assert.assertEquals(ACTION_VIEW, intent.getAction());
        Assert.assertEquals(Uri.parse("market://details?id=com.facebook.orca"), intent.getData());
    }

    @Test
    public void testGetMessengerThreadParamsForIntentWith20150314Protocol() throws Exception {
        // Simulate an intent that Messenger would send.
        Intent intent = new Intent();
        intent.addCategory("com.facebook.orca.category.PLATFORM_THREAD_20150314");
        Bundle extrasBundle = setupIntentWithAppLinkExtrasBundle(intent);
        extrasBundle.putString("com.facebook.orca.extra.THREAD_TOKEN", "thread_token");
        extrasBundle.putString("com.facebook.orca.extra.METADATA", "{}");
        extrasBundle.putString("com.facebook.orca.extra.PARTICIPANTS", "100,400,500");
        extrasBundle.putBoolean("com.facebook.orca.extra.IS_REPLY", true);
        // Check the parsing logic.
        MessengerThreadParams params = MessengerUtils.getMessengerThreadParamsForIntent(intent);
        Assert.assertEquals(REPLY_FLOW, params.origin);
        Assert.assertEquals("thread_token", params.threadToken);
        Assert.assertEquals("{}", params.metadata);
        Assert.assertEquals(Arrays.asList("100", "400", "500"), params.participants);
    }

    @Test
    public void testGetMessengerThreadParamsForIntentWithUnrecognizedIntent() throws Exception {
        // Simulate an intent that Messenger would send.
        Intent intent = new Intent();
        Assert.assertNull(MessengerUtils.getMessengerThreadParamsForIntent(intent));
    }

    @Test
    public void testFinishShareToMessengerWith20150314Protocol() throws Exception {
        // Simulate an intent that Messenger would send.
        Intent originalIntent = new Intent();
        originalIntent.addCategory("com.facebook.orca.category.PLATFORM_THREAD_20150314");
        Bundle extrasBundle = setupIntentWithAppLinkExtrasBundle(originalIntent);
        extrasBundle.putString("com.facebook.orca.extra.THREAD_TOKEN", "thread_token");
        extrasBundle.putString("com.facebook.orca.extra.METADATA", "{}");
        extrasBundle.putString("com.facebook.orca.extra.PARTICIPANTS", "100,400,500");
        Mockito.when(mMockActivity.getIntent()).thenReturn(originalIntent);
        // Setup the data the app will send back to messenger.
        Uri uri = Uri.parse("file:///foo.jpeg");
        Uri externalUri = Uri.parse("http://example.com/foo.jpeg");
        ShareToMessengerParams params = ShareToMessengerParams.newBuilder(uri, "image/jpeg").setMetaData("{}").setExternalUri(externalUri).build();
        // Call finishShareToMessenger and verify the results.
        MessengerUtils.finishShareToMessenger(mMockActivity, params);
        ArgumentCaptor<Intent> intentArgumentCaptor = ArgumentCaptor.forClass(Intent.class);
        Mockito.verify(mMockActivity).setResult(ArgumentMatchers.eq(RESULT_OK), intentArgumentCaptor.capture());
        Mockito.verify(mMockActivity).finish();
        Intent intent = intentArgumentCaptor.getValue();
        Assert.assertNotNull(intent);
        Assert.assertEquals(FLAG_GRANT_READ_URI_PERMISSION, intent.getFlags());
        Assert.assertEquals(20150314, intent.getIntExtra("com.facebook.orca.extra.PROTOCOL_VERSION", (-1)));
        Assert.assertEquals("thread_token", intent.getStringExtra("com.facebook.orca.extra.THREAD_TOKEN"));
        Assert.assertEquals(uri, intent.getData());
        Assert.assertEquals("image/jpeg", intent.getType());
        Assert.assertEquals("200", intent.getStringExtra("com.facebook.orca.extra.APPLICATION_ID"));
        Assert.assertEquals("{}", intent.getStringExtra("com.facebook.orca.extra.METADATA"));
        Assert.assertEquals(externalUri, intent.getParcelableExtra("com.facebook.orca.extra.EXTERNAL_URI"));
    }

    @Test
    public void testFinishShareToMessengerWithUnexpectedIntent() throws Exception {
        // Simulate an intent that Messenger would send.
        Intent originalIntent = new Intent();
        Mockito.when(mMockActivity.getIntent()).thenReturn(originalIntent);
        // Setup the data the app will send back to messenger.
        Uri uri = Uri.parse("file:///foo.jpeg");
        Uri externalUri = Uri.parse("http://example.com/foo.jpeg");
        ShareToMessengerParams params = ShareToMessengerParams.newBuilder(uri, "image/jpeg").setMetaData("{}").setExternalUri(externalUri).build();
        // Call finishShareToMessenger and verify the results.
        MessengerUtils.finishShareToMessenger(mMockActivity, params);
        Mockito.verify(mMockActivity).setResult(RESULT_CANCELED, null);
        Mockito.verify(mMockActivity).finish();
    }
}

