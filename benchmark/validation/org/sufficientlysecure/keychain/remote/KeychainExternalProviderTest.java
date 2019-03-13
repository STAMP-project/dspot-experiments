package org.sufficientlysecure.keychain.remote;


import AutocryptStatus.AUTOCRYPT_PEER_AVAILABLE;
import AutocryptStatus.AUTOCRYPT_PEER_DISABLED;
import AutocryptStatus.AUTOCRYPT_PEER_GOSSIP;
import AutocryptStatus.AUTOCRYPT_PEER_MUTUAL;
import AutocryptStatus.CONTENT_URI;
import GossipOrigin.GOSSIP_HEADER;
import KeychainExternalContract.KEY_STATUS_UNAVAILABLE;
import KeychainExternalContract.KEY_STATUS_UNVERIFIED;
import KeychainExternalContract.KEY_STATUS_VERIFIED;
import RuntimeEnvironment.application;
import android.content.ContentResolver;
import android.database.Cursor;
import java.security.AccessControlException;
import java.util.Date;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sufficientlysecure.keychain.KeychainTestRunner;
import org.sufficientlysecure.keychain.daos.ApiAppDao;
import org.sufficientlysecure.keychain.daos.AutocryptPeerDao;
import org.sufficientlysecure.keychain.daos.KeyWritableRepository;
import org.sufficientlysecure.keychain.model.ApiApp;
import org.sufficientlysecure.keychain.provider.KeychainExternalContract.AutocryptStatus;


@SuppressWarnings("WeakerAccess")
@RunWith(KeychainTestRunner.class)
public class KeychainExternalProviderTest {
    static final String PACKAGE_NAME = "test.package";

    static final byte[] PACKAGE_SIGNATURE = new byte[]{ 1, 2, 3 };

    static final String MAIL_ADDRESS_1 = "twi@openkeychain.org";

    static final String USER_ID_1 = "twi <twi@openkeychain.org>";

    static final long KEY_ID_SECRET = 6723210423119188527L;

    static final long KEY_ID_PUBLIC = -7338566240752589950L;

    public static final String AUTOCRYPT_PEER = "tid";

    public static final int PACKAGE_UID = 42;

    KeyWritableRepository databaseInteractor = KeyWritableRepository.create(application);

    ContentResolver contentResolver = application.getContentResolver();

    ApiPermissionHelper apiPermissionHelper;

    ApiAppDao apiAppDao;

    AutocryptPeerDao autocryptPeerDao;

    @Test(expected = AccessControlException.class)
    public void testPermission__withMissingPackage() throws Exception {
        apiAppDao.deleteApiApp(KeychainExternalProviderTest.PACKAGE_NAME);
        contentResolver.query(CONTENT_URI, new String[]{ AutocryptStatus.ADDRESS }, null, new String[]{  }, null);
    }

    @Test(expected = AccessControlException.class)
    public void testPermission__withWrongPackageCert() throws Exception {
        apiAppDao.deleteApiApp(KeychainExternalProviderTest.PACKAGE_NAME);
        apiAppDao.insertApiApp(ApiApp.create(KeychainExternalProviderTest.PACKAGE_NAME, new byte[]{ 1, 2, 4 }));
        contentResolver.query(CONTENT_URI, new String[]{ AutocryptStatus.ADDRESS }, null, new String[]{  }, null);
    }

    @Test
    public void testAutocryptStatus_autocryptPeer_withUnconfirmedKey() throws Exception {
        insertSecretKeyringFrom("/test-keys/testring.sec");
        insertPublicKeyringFrom("/test-keys/testring.pub");
        autocryptPeerDao.insertOrUpdateLastSeen(KeychainExternalProviderTest.PACKAGE_NAME, "tid", new Date());
        autocryptPeerDao.updateKey(KeychainExternalProviderTest.PACKAGE_NAME, KeychainExternalProviderTest.AUTOCRYPT_PEER, new Date(), KeychainExternalProviderTest.KEY_ID_PUBLIC, false);
        Cursor cursor = contentResolver.query(CONTENT_URI, new String[]{ AutocryptStatus.ADDRESS, AutocryptStatus.UID_KEY_STATUS, AutocryptStatus.UID_ADDRESS, AutocryptStatus.AUTOCRYPT_PEER_STATE, AutocryptStatus.AUTOCRYPT_KEY_STATUS, AutocryptStatus.AUTOCRYPT_MASTER_KEY_ID }, null, new String[]{ KeychainExternalProviderTest.AUTOCRYPT_PEER }, null);
        Assert.assertNotNull(cursor);
        Assert.assertTrue(cursor.moveToFirst());
        Assert.assertEquals("tid", cursor.getString(0));
        Assert.assertTrue(cursor.isNull(1));
        Assert.assertEquals(null, cursor.getString(2));
        Assert.assertEquals(AUTOCRYPT_PEER_AVAILABLE, cursor.getInt(3));
        Assert.assertEquals(KEY_STATUS_UNVERIFIED, cursor.getInt(4));
        Assert.assertEquals(KeychainExternalProviderTest.KEY_ID_PUBLIC, cursor.getLong(5));
        Assert.assertFalse(cursor.moveToNext());
    }

    @Test
    public void testAutocryptStatus_autocryptPeer_withMutualKey() throws Exception {
        insertSecretKeyringFrom("/test-keys/testring.sec");
        insertPublicKeyringFrom("/test-keys/testring.pub");
        autocryptPeerDao.insertOrUpdateLastSeen(KeychainExternalProviderTest.PACKAGE_NAME, "tid", new Date());
        autocryptPeerDao.updateKey(KeychainExternalProviderTest.PACKAGE_NAME, KeychainExternalProviderTest.AUTOCRYPT_PEER, new Date(), KeychainExternalProviderTest.KEY_ID_PUBLIC, true);
        Cursor cursor = contentResolver.query(CONTENT_URI, new String[]{ AutocryptStatus.ADDRESS, AutocryptStatus.UID_KEY_STATUS, AutocryptStatus.UID_ADDRESS, AutocryptStatus.AUTOCRYPT_PEER_STATE, AutocryptStatus.AUTOCRYPT_KEY_STATUS, AutocryptStatus.AUTOCRYPT_MASTER_KEY_ID }, null, new String[]{ KeychainExternalProviderTest.AUTOCRYPT_PEER }, null);
        Assert.assertNotNull(cursor);
        Assert.assertTrue(cursor.moveToFirst());
        Assert.assertEquals("tid", cursor.getString(0));
        Assert.assertTrue(cursor.isNull(1));
        Assert.assertEquals(null, cursor.getString(2));
        Assert.assertEquals(AUTOCRYPT_PEER_MUTUAL, cursor.getInt(3));
        Assert.assertEquals(KEY_STATUS_UNVERIFIED, cursor.getInt(4));
        Assert.assertEquals(KeychainExternalProviderTest.KEY_ID_PUBLIC, cursor.getLong(5));
        Assert.assertFalse(cursor.moveToNext());
    }

    @Test
    public void testAutocryptStatus_available_withConfirmedKey() throws Exception {
        insertSecretKeyringFrom("/test-keys/testring.sec");
        insertPublicKeyringFrom("/test-keys/testring.pub");
        autocryptPeerDao.insertOrUpdateLastSeen(KeychainExternalProviderTest.PACKAGE_NAME, "tid", new Date());
        autocryptPeerDao.updateKey(KeychainExternalProviderTest.PACKAGE_NAME, KeychainExternalProviderTest.AUTOCRYPT_PEER, new Date(), KeychainExternalProviderTest.KEY_ID_PUBLIC, false);
        certifyKey(KeychainExternalProviderTest.KEY_ID_SECRET, KeychainExternalProviderTest.KEY_ID_PUBLIC, KeychainExternalProviderTest.USER_ID_1);
        Cursor cursor = contentResolver.query(CONTENT_URI, new String[]{ AutocryptStatus.ADDRESS, AutocryptStatus.UID_KEY_STATUS, AutocryptStatus.UID_ADDRESS, AutocryptStatus.AUTOCRYPT_PEER_STATE, AutocryptStatus.AUTOCRYPT_KEY_STATUS }, null, new String[]{ KeychainExternalProviderTest.AUTOCRYPT_PEER }, null);
        Assert.assertNotNull(cursor);
        Assert.assertTrue(cursor.moveToFirst());
        Assert.assertEquals("tid", cursor.getString(0));
        Assert.assertEquals(KEY_STATUS_UNAVAILABLE, cursor.getInt(1));
        Assert.assertEquals(null, cursor.getString(2));
        Assert.assertEquals(AUTOCRYPT_PEER_AVAILABLE, cursor.getInt(3));
        Assert.assertEquals(KEY_STATUS_VERIFIED, cursor.getInt(4));
        Assert.assertFalse(cursor.moveToNext());
    }

    @Test
    public void testAutocryptStatus_noData() throws Exception {
        Cursor cursor = contentResolver.query(CONTENT_URI, new String[]{ AutocryptStatus.ADDRESS, AutocryptStatus.UID_KEY_STATUS, AutocryptStatus.UID_ADDRESS, AutocryptStatus.AUTOCRYPT_PEER_STATE, AutocryptStatus.AUTOCRYPT_KEY_STATUS }, null, new String[]{ KeychainExternalProviderTest.AUTOCRYPT_PEER }, null);
        Assert.assertNotNull(cursor);
        Assert.assertTrue(cursor.moveToFirst());
        Assert.assertEquals("tid", cursor.getString(0));
        Assert.assertEquals(KEY_STATUS_UNAVAILABLE, cursor.getInt(1));
        Assert.assertEquals(null, cursor.getString(2));
        Assert.assertEquals(AUTOCRYPT_PEER_DISABLED, cursor.getInt(3));
        Assert.assertTrue(cursor.isNull(4));
        Assert.assertFalse(cursor.moveToNext());
    }

    @Test
    public void testAutocryptStatus_afterDelete() throws Exception {
        insertSecretKeyringFrom("/test-keys/testring.sec");
        insertPublicKeyringFrom("/test-keys/testring.pub");
        autocryptPeerDao.insertOrUpdateLastSeen(KeychainExternalProviderTest.PACKAGE_NAME, "tid", new Date());
        autocryptPeerDao.updateKeyGossip(KeychainExternalProviderTest.PACKAGE_NAME, "tid", new Date(), KeychainExternalProviderTest.KEY_ID_PUBLIC, GOSSIP_HEADER);
        autocryptPeerDao.deleteByIdentifier(KeychainExternalProviderTest.PACKAGE_NAME, "tid");
        Cursor cursor = contentResolver.query(CONTENT_URI, new String[]{ AutocryptStatus.ADDRESS, AutocryptStatus.UID_KEY_STATUS, AutocryptStatus.UID_ADDRESS, AutocryptStatus.AUTOCRYPT_PEER_STATE, AutocryptStatus.AUTOCRYPT_KEY_STATUS }, null, new String[]{ KeychainExternalProviderTest.AUTOCRYPT_PEER }, null);
        Assert.assertNotNull(cursor);
        Assert.assertTrue(cursor.moveToFirst());
        Assert.assertEquals("tid", cursor.getString(0));
        Assert.assertEquals(KEY_STATUS_UNAVAILABLE, cursor.getInt(1));
        Assert.assertEquals(null, cursor.getString(2));
        Assert.assertEquals(AUTOCRYPT_PEER_DISABLED, cursor.getInt(3));
        Assert.assertTrue(cursor.isNull(4));
        Assert.assertFalse(cursor.moveToNext());
    }

    @Test
    public void testAutocryptStatus_stateGossip() throws Exception {
        insertSecretKeyringFrom("/test-keys/testring.sec");
        insertPublicKeyringFrom("/test-keys/testring.pub");
        autocryptPeerDao.insertOrUpdateLastSeen(KeychainExternalProviderTest.PACKAGE_NAME, "tid", new Date());
        autocryptPeerDao.updateKeyGossip(KeychainExternalProviderTest.PACKAGE_NAME, KeychainExternalProviderTest.AUTOCRYPT_PEER, new Date(), KeychainExternalProviderTest.KEY_ID_PUBLIC, GOSSIP_HEADER);
        certifyKey(KeychainExternalProviderTest.KEY_ID_SECRET, KeychainExternalProviderTest.KEY_ID_PUBLIC, KeychainExternalProviderTest.USER_ID_1);
        Cursor cursor = contentResolver.query(CONTENT_URI, new String[]{ AutocryptStatus.ADDRESS, AutocryptStatus.UID_KEY_STATUS, AutocryptStatus.UID_ADDRESS, AutocryptStatus.AUTOCRYPT_PEER_STATE, AutocryptStatus.AUTOCRYPT_KEY_STATUS }, null, new String[]{ KeychainExternalProviderTest.AUTOCRYPT_PEER }, null);
        Assert.assertNotNull(cursor);
        Assert.assertTrue(cursor.moveToFirst());
        Assert.assertEquals("tid", cursor.getString(0));
        Assert.assertEquals(KEY_STATUS_UNAVAILABLE, cursor.getInt(1));
        Assert.assertEquals(null, cursor.getString(2));
        Assert.assertEquals(AUTOCRYPT_PEER_GOSSIP, cursor.getInt(3));
        Assert.assertEquals(KEY_STATUS_VERIFIED, cursor.getInt(4));
        Assert.assertFalse(cursor.moveToNext());
    }

    /* @Test
    public void testAutocryptStatus_stateSelected() throws Exception {
    insertSecretKeyringFrom("/test-keys/testring.sec");
    insertPublicKeyringFrom("/test-keys/testring.pub");

    autocryptPeerDao.updateToSelectedState("tid", KEY_ID_PUBLIC);
    certifyKey(KEY_ID_SECRET, KEY_ID_PUBLIC, USER_ID_1);

    Cursor cursor = contentResolver.query(
    AutocryptStatus.CONTENT_URI, new String[] {
    AutocryptStatus.ADDRESS, AutocryptStatus.UID_KEY_STATUS, AutocryptStatus.UID_ADDRESS,
    AutocryptStatus.AUTOCRYPT_PEER_STATE, AutocryptStatus.AUTOCRYPT_KEY_STATUS },
    null, new String [] { AUTOCRYPT_PEER }, null
    );

    assertNotNull(cursor);
    assertTrue(cursor.moveToFirst());
    assertEquals("tid", cursor.getString(0));
    assertEquals(KeychainExternalContract.KEY_STATUS_UNAVAILABLE, cursor.getInt(1));
    assertEquals(null, cursor.getString(2));
    assertEquals(AutocryptStatus.AUTOCRYPT_PEER_SELECTED, cursor.getInt(3));
    assertEquals(KeychainExternalContract.KEY_STATUS_VERIFIED, cursor.getInt(4));
    assertFalse(cursor.moveToNext());
    }
     */
    @Test
    public void testAutocryptStatus_withConfirmedKey() throws Exception {
        insertSecretKeyringFrom("/test-keys/testring.sec");
        insertPublicKeyringFrom("/test-keys/testring.pub");
        certifyKey(KeychainExternalProviderTest.KEY_ID_SECRET, KeychainExternalProviderTest.KEY_ID_PUBLIC, KeychainExternalProviderTest.USER_ID_1);
        Cursor cursor = contentResolver.query(CONTENT_URI, new String[]{ AutocryptStatus.ADDRESS, AutocryptStatus.UID_KEY_STATUS, AutocryptStatus.UID_ADDRESS, AutocryptStatus.AUTOCRYPT_PEER_STATE }, null, new String[]{ KeychainExternalProviderTest.MAIL_ADDRESS_1 }, null);
        Assert.assertNotNull(cursor);
        Assert.assertTrue(cursor.moveToFirst());
        Assert.assertEquals(KeychainExternalProviderTest.MAIL_ADDRESS_1, cursor.getString(0));
        Assert.assertEquals(KEY_STATUS_VERIFIED, cursor.getInt(1));
        Assert.assertEquals(KeychainExternalProviderTest.USER_ID_1, cursor.getString(2));
        Assert.assertEquals(AUTOCRYPT_PEER_DISABLED, cursor.getInt(3));
        Assert.assertFalse(cursor.moveToNext());
    }

    @Test(expected = AccessControlException.class)
    public void testPermission__withExplicitPackage() throws Exception {
        contentResolver.query(CONTENT_URI.buildUpon().appendPath("fake_pkg").build(), new String[]{ AutocryptStatus.ADDRESS }, null, new String[]{  }, null);
    }
}

