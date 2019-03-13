package org.sufficientlysecure.keychain.remote;


import BuildConfig.APPLICATION_ID;
import KeyIdResultStatus.DUPLICATE;
import KeyIdResultStatus.MISSING;
import KeyIdResultStatus.NO_KEYS;
import KeyIdResultStatus.NO_KEYS_ERROR;
import KeyIdResultStatus.OK;
import OpenPgpApi.EXTRA_KEY_IDS;
import OpenPgpApi.EXTRA_KEY_IDS_SELECTED;
import OpenPgpApi.EXTRA_USER_IDS;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Intent;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.sufficientlysecure.keychain.KeychainTestRunner;
import org.sufficientlysecure.keychain.remote.OpenPgpServiceKeyIdExtractor.KeyIdResult;


@SuppressWarnings("unchecked")
@RunWith(KeychainTestRunner.class)
public class OpenPgpServiceKeyIdExtractorTest {
    private static final long[] KEY_IDS = new long[]{ 123L, 234L };

    private static final String[] USER_IDS = new String[]{ "user1@example.org", "User 2 <user2@example.org>" };

    private OpenPgpServiceKeyIdExtractor openPgpServiceKeyIdExtractor;

    private ContentResolver contentResolver;

    private ApiPendingIntentFactory apiPendingIntentFactory;

    @Test
    public void returnKeyIdsFromIntent__withKeyIdsExtra() throws Exception {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_KEY_IDS, OpenPgpServiceKeyIdExtractorTest.KEY_IDS);
        KeyIdResult keyIdResult = openPgpServiceKeyIdExtractor.returnKeyIdsFromIntent(intent, false, APPLICATION_ID);
        Assert.assertEquals(NO_KEYS_ERROR, keyIdResult.getStatus());
        Assert.assertFalse(keyIdResult.hasKeySelectionPendingIntent());
        OpenPgpServiceKeyIdExtractorTest.assertArrayEqualsSorted(OpenPgpServiceKeyIdExtractorTest.KEY_IDS, keyIdResult.getKeyIds());
    }

    @Test
    public void returnKeyIdsFromIntent__withKeyIdsSelectedExtra() throws Exception {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_KEY_IDS_SELECTED, OpenPgpServiceKeyIdExtractorTest.KEY_IDS);
        intent.putExtra(EXTRA_USER_IDS, OpenPgpServiceKeyIdExtractorTest.USER_IDS);// should be ignored

        KeyIdResult keyIdResult = openPgpServiceKeyIdExtractor.returnKeyIdsFromIntent(intent, false, APPLICATION_ID);
        Assert.assertEquals(OK, keyIdResult.getStatus());
        Assert.assertFalse(keyIdResult.hasKeySelectionPendingIntent());
        OpenPgpServiceKeyIdExtractorTest.assertArrayEqualsSorted(OpenPgpServiceKeyIdExtractorTest.KEY_IDS, keyIdResult.getKeyIds());
    }

    @Test(expected = IllegalStateException.class)
    public void returnKeyIdsFromIntent__withUserIds__withEmptyQueryResult() throws Exception {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_USER_IDS, OpenPgpServiceKeyIdExtractorTest.USER_IDS);
        setupContentResolverResult();
        PendingIntent pendingIntent = Mockito.mock(PendingIntent.class);
        setupSelectPubkeyPendingIntentFactoryResult(pendingIntent);
        openPgpServiceKeyIdExtractor.returnKeyIdsFromIntent(intent, false, APPLICATION_ID);
    }

    @Test
    public void returnKeyIdsFromIntent__withNoData() throws Exception {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_USER_IDS, new String[]{  });
        PendingIntent pendingIntent = Mockito.mock(PendingIntent.class);
        setupSelectPubkeyPendingIntentFactoryResult(pendingIntent);
        KeyIdResult keyIdResult = openPgpServiceKeyIdExtractor.returnKeyIdsFromIntent(intent, false, APPLICATION_ID);
        Assert.assertEquals(NO_KEYS, keyIdResult.getStatus());
        Assert.assertTrue(keyIdResult.hasKeySelectionPendingIntent());
        Assert.assertSame(pendingIntent, keyIdResult.getKeySelectionPendingIntent());
    }

    @Test
    public void returnKeyIdsFromIntent__withEmptyUserId() throws Exception {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_USER_IDS, new String[0]);
        PendingIntent pendingIntent = Mockito.mock(PendingIntent.class);
        setupSelectPubkeyPendingIntentFactoryResult(pendingIntent);
        KeyIdResult keyIdResult = openPgpServiceKeyIdExtractor.returnKeyIdsFromIntent(intent, false, APPLICATION_ID);
        Assert.assertEquals(NO_KEYS, keyIdResult.getStatus());
        Assert.assertTrue(keyIdResult.hasKeySelectionPendingIntent());
        Assert.assertSame(pendingIntent, keyIdResult.getKeySelectionPendingIntent());
    }

    @Test
    public void returnKeyIdsFromIntent__withNoData__askIfNoData() throws Exception {
        Intent intent = new Intent();
        setupContentResolverResult();
        PendingIntent pendingIntent = Mockito.mock(PendingIntent.class);
        setupSelectPubkeyPendingIntentFactoryResult(pendingIntent);
        KeyIdResult keyIdResult = openPgpServiceKeyIdExtractor.returnKeyIdsFromIntent(intent, true, APPLICATION_ID);
        Assert.assertEquals(NO_KEYS, keyIdResult.getStatus());
        Assert.assertTrue(keyIdResult.hasKeySelectionPendingIntent());
        Assert.assertSame(pendingIntent, keyIdResult.getKeySelectionPendingIntent());
    }

    @Test
    public void returnKeyIdsFromIntent__withUserIds() throws Exception {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_USER_IDS, OpenPgpServiceKeyIdExtractorTest.USER_IDS);
        setupContentResolverResult(OpenPgpServiceKeyIdExtractorTest.USER_IDS, new Long[]{ 123L, 234L }, new int[]{ 0, 0 }, new int[]{ 1, 1, 1 });
        KeyIdResult keyIdResult = openPgpServiceKeyIdExtractor.returnKeyIdsFromIntent(intent, false, APPLICATION_ID);
        Assert.assertEquals(OK, keyIdResult.getStatus());
        Assert.assertFalse(keyIdResult.hasKeySelectionPendingIntent());
        OpenPgpServiceKeyIdExtractorTest.assertArrayEqualsSorted(OpenPgpServiceKeyIdExtractorTest.KEY_IDS, keyIdResult.getKeyIds());
    }

    @Test
    public void returnKeyIdsFromIntent__withUserIds__withDuplicate() throws Exception {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_USER_IDS, OpenPgpServiceKeyIdExtractorTest.USER_IDS);
        setupContentResolverResult(new String[]{ OpenPgpServiceKeyIdExtractorTest.USER_IDS[0], OpenPgpServiceKeyIdExtractorTest.USER_IDS[1] }, new Long[]{ 123L, 234L }, new int[]{ 0, 0 }, new int[]{ 2, 1 });
        PendingIntent pendingIntent = Mockito.mock(PendingIntent.class);
        setupDeduplicatePendingIntentFactoryResult(pendingIntent);
        KeyIdResult keyIdResult = openPgpServiceKeyIdExtractor.returnKeyIdsFromIntent(intent, false, APPLICATION_ID);
        Assert.assertEquals(DUPLICATE, keyIdResult.getStatus());
        Assert.assertTrue(keyIdResult.hasKeySelectionPendingIntent());
    }

    @Test
    public void returnKeyIdsFromIntent__withUserIds__withMissing() throws Exception {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_USER_IDS, OpenPgpServiceKeyIdExtractorTest.USER_IDS);
        setupContentResolverResult(OpenPgpServiceKeyIdExtractorTest.USER_IDS, new Long[]{ null, 234L }, new int[]{ 0, 0 }, new int[]{ 0, 1 });
        PendingIntent pendingIntent = Mockito.mock(PendingIntent.class);
        setupSelectPubkeyPendingIntentFactoryResult(pendingIntent);
        KeyIdResult keyIdResult = openPgpServiceKeyIdExtractor.returnKeyIdsFromIntent(intent, false, APPLICATION_ID);
        Assert.assertEquals(MISSING, keyIdResult.getStatus());
        Assert.assertTrue(keyIdResult.hasKeySelectionPendingIntent());
    }
}

