package com.gitblit.tests;


import AccessPermission.CLONE;
import Keys.filestore.maxUploadSize;
import Status.AuthenticationRequired;
import Status.Available;
import Status.Error_Exceeds_Size_Limit;
import Status.Error_Hash_Mismatch;
import Status.Error_Invalid_Oid;
import Status.Error_Invalid_Size;
import Status.Error_Size_Mismatch;
import Status.Error_Unauthorized;
import Status.Unavailable;
import Status.Upload_Pending;
import com.gitblit.Constants.AccessRestrictionType;
import com.gitblit.Constants.AuthorizationControl;
import com.gitblit.models.RepositoryModel;
import com.gitblit.models.UserModel;
import com.gitblit.utils.FileUtils;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test of the filestore manager and confirming filesystem updated
 *
 * @author Paul Martin
 */
public class FilestoreManagerTest extends GitblitUnitTest {
    private static final AtomicBoolean started = new AtomicBoolean(false);

    private static final BlobInfo blob_zero = new BlobInfo(0);

    private static final BlobInfo blob_512KB = new BlobInfo((512 * (FileUtils.KB)));

    private static final BlobInfo blob_6MB = new BlobInfo((6 * (FileUtils.MB)));

    private static int download_limit_default = -1;

    private static int download_limit_test = 5 * (FileUtils.MB);

    private static final String invalid_hash_empty = "";

    private static final String invalid_hash_major = "INVALID_HASH";

    private static final String invalid_hash_regex_attack = FilestoreManagerTest.blob_512KB.hash.replace('a', '*');

    private static final String invalid_hash_one_long = FilestoreManagerTest.blob_512KB.hash.concat("a");

    private static final String invalid_hash_one_short = FilestoreManagerTest.blob_512KB.hash.substring(1);

    @Test
    public void testAdminAccess() throws Exception {
        FileUtils.delete(GitblitUnitTest.filestore().getStorageFolder());
        GitblitUnitTest.filestore().clearFilestoreCache();
        RepositoryModel r = new RepositoryModel("myrepo.git", null, null, new Date());
        ByteArrayOutputStream streamOut = new ByteArrayOutputStream();
        UserModel u = new UserModel("admin");
        u.canAdmin = true;
        // No upload limit
        GitblitUnitTest.settings().overrideSetting(maxUploadSize, FilestoreManagerTest.download_limit_default);
        // Invalid hash tests
        Assert.assertEquals(Error_Invalid_Oid, GitblitUnitTest.filestore().downloadBlob(FilestoreManagerTest.invalid_hash_major, u, r, streamOut));
        Assert.assertEquals(Error_Invalid_Oid, GitblitUnitTest.filestore().downloadBlob(FilestoreManagerTest.invalid_hash_regex_attack, u, r, streamOut));
        Assert.assertEquals(Error_Invalid_Oid, GitblitUnitTest.filestore().downloadBlob(FilestoreManagerTest.invalid_hash_one_long, u, r, streamOut));
        Assert.assertEquals(Error_Invalid_Oid, GitblitUnitTest.filestore().downloadBlob(FilestoreManagerTest.invalid_hash_one_short, u, r, streamOut));
        // Download prior to upload
        streamOut.reset();
        Assert.assertEquals(Unavailable, GitblitUnitTest.filestore().downloadBlob(FilestoreManagerTest.blob_512KB.hash, u, r, streamOut));
        streamOut.reset();
        Assert.assertEquals(Unavailable, GitblitUnitTest.filestore().downloadBlob(FilestoreManagerTest.blob_512KB.hash, u, r, streamOut));
        // Bad input is rejected with no upload taking place
        Assert.assertEquals(Error_Invalid_Oid, GitblitUnitTest.filestore().uploadBlob(FilestoreManagerTest.invalid_hash_empty, FilestoreManagerTest.blob_512KB.length, u, r, new ByteArrayInputStream(FilestoreManagerTest.blob_512KB.blob)));
        Assert.assertEquals(Error_Invalid_Oid, GitblitUnitTest.filestore().uploadBlob(FilestoreManagerTest.invalid_hash_major, FilestoreManagerTest.blob_512KB.length, u, r, new ByteArrayInputStream(FilestoreManagerTest.blob_512KB.blob)));
        Assert.assertEquals(Error_Invalid_Oid, GitblitUnitTest.filestore().uploadBlob(FilestoreManagerTest.invalid_hash_regex_attack, FilestoreManagerTest.blob_512KB.length, u, r, new ByteArrayInputStream(FilestoreManagerTest.blob_512KB.blob)));
        Assert.assertEquals(Error_Invalid_Oid, GitblitUnitTest.filestore().uploadBlob(FilestoreManagerTest.invalid_hash_one_long, FilestoreManagerTest.blob_512KB.length, u, r, new ByteArrayInputStream(FilestoreManagerTest.blob_512KB.blob)));
        Assert.assertEquals(Error_Invalid_Oid, GitblitUnitTest.filestore().uploadBlob(FilestoreManagerTest.invalid_hash_one_short, FilestoreManagerTest.blob_512KB.length, u, r, new ByteArrayInputStream(FilestoreManagerTest.blob_512KB.blob)));
        Assert.assertEquals(Error_Invalid_Size, GitblitUnitTest.filestore().uploadBlob(FilestoreManagerTest.blob_512KB.hash, (-1), u, r, new ByteArrayInputStream(FilestoreManagerTest.blob_512KB.blob)));
        Assert.assertEquals(Error_Size_Mismatch, GitblitUnitTest.filestore().uploadBlob(FilestoreManagerTest.blob_512KB.hash, 0, u, r, new ByteArrayInputStream(FilestoreManagerTest.blob_512KB.blob)));
        Assert.assertEquals(Error_Size_Mismatch, GitblitUnitTest.filestore().uploadBlob(FilestoreManagerTest.blob_512KB.hash, ((FilestoreManagerTest.blob_512KB.length) - 1), u, r, new ByteArrayInputStream(FilestoreManagerTest.blob_512KB.blob)));
        Assert.assertEquals(Error_Size_Mismatch, GitblitUnitTest.filestore().uploadBlob(FilestoreManagerTest.blob_512KB.hash, ((FilestoreManagerTest.blob_512KB.length) + 1), u, r, new ByteArrayInputStream(FilestoreManagerTest.blob_512KB.blob)));
        Assert.assertEquals(Error_Hash_Mismatch, GitblitUnitTest.filestore().uploadBlob(FilestoreManagerTest.blob_zero.hash, FilestoreManagerTest.blob_512KB.length, u, r, new ByteArrayInputStream(FilestoreManagerTest.blob_512KB.blob)));
        // Confirm no upload with bad input
        streamOut.reset();
        Assert.assertEquals(Unavailable, GitblitUnitTest.filestore().downloadBlob(FilestoreManagerTest.blob_512KB.hash, u, r, streamOut));
        // Good input will accept the upload
        Assert.assertEquals(Available, GitblitUnitTest.filestore().uploadBlob(FilestoreManagerTest.blob_512KB.hash, FilestoreManagerTest.blob_512KB.length, u, r, new ByteArrayInputStream(FilestoreManagerTest.blob_512KB.blob)));
        streamOut.reset();
        Assert.assertEquals(Available, GitblitUnitTest.filestore().downloadBlob(FilestoreManagerTest.blob_512KB.hash, u, r, streamOut));
        Assert.assertArrayEquals(FilestoreManagerTest.blob_512KB.blob, streamOut.toByteArray());
        // Subsequent failed uploads do not affect file
        Assert.assertEquals(Error_Size_Mismatch, GitblitUnitTest.filestore().uploadBlob(FilestoreManagerTest.blob_512KB.hash, ((FilestoreManagerTest.blob_512KB.length) - 1), u, r, new ByteArrayInputStream(FilestoreManagerTest.blob_512KB.blob)));
        Assert.assertEquals(Error_Size_Mismatch, GitblitUnitTest.filestore().uploadBlob(FilestoreManagerTest.blob_512KB.hash, ((FilestoreManagerTest.blob_512KB.length) + 1), u, r, new ByteArrayInputStream(FilestoreManagerTest.blob_512KB.blob)));
        streamOut.reset();
        Assert.assertEquals(Available, GitblitUnitTest.filestore().downloadBlob(FilestoreManagerTest.blob_512KB.hash, u, r, streamOut));
        Assert.assertArrayEquals(FilestoreManagerTest.blob_512KB.blob, streamOut.toByteArray());
        // Zero length upload is valid
        Assert.assertEquals(Available, GitblitUnitTest.filestore().uploadBlob(FilestoreManagerTest.blob_zero.hash, FilestoreManagerTest.blob_zero.length, u, r, new ByteArrayInputStream(FilestoreManagerTest.blob_zero.blob)));
        streamOut.reset();
        Assert.assertEquals(Available, GitblitUnitTest.filestore().downloadBlob(FilestoreManagerTest.blob_zero.hash, u, r, streamOut));
        Assert.assertArrayEquals(FilestoreManagerTest.blob_zero.blob, streamOut.toByteArray());
        // Pre-informed upload identifies identical errors as immediate upload
        Assert.assertEquals(Upload_Pending, GitblitUnitTest.filestore().addObject(FilestoreManagerTest.blob_6MB.hash, FilestoreManagerTest.blob_6MB.length, u, r));
        streamOut.reset();
        Assert.assertEquals(Unavailable, GitblitUnitTest.filestore().downloadBlob(FilestoreManagerTest.blob_6MB.hash, u, r, streamOut));
        Assert.assertEquals(Error_Invalid_Oid, GitblitUnitTest.filestore().uploadBlob(FilestoreManagerTest.invalid_hash_empty, FilestoreManagerTest.blob_6MB.length, u, r, new ByteArrayInputStream(FilestoreManagerTest.blob_6MB.blob)));
        Assert.assertEquals(Error_Invalid_Oid, GitblitUnitTest.filestore().uploadBlob(FilestoreManagerTest.invalid_hash_major, FilestoreManagerTest.blob_6MB.length, u, r, new ByteArrayInputStream(FilestoreManagerTest.blob_6MB.blob)));
        Assert.assertEquals(Error_Invalid_Oid, GitblitUnitTest.filestore().uploadBlob(FilestoreManagerTest.invalid_hash_regex_attack, FilestoreManagerTest.blob_6MB.length, u, r, new ByteArrayInputStream(FilestoreManagerTest.blob_6MB.blob)));
        Assert.assertEquals(Error_Invalid_Oid, GitblitUnitTest.filestore().uploadBlob(FilestoreManagerTest.invalid_hash_one_long, FilestoreManagerTest.blob_6MB.length, u, r, new ByteArrayInputStream(FilestoreManagerTest.blob_6MB.blob)));
        Assert.assertEquals(Error_Invalid_Oid, GitblitUnitTest.filestore().uploadBlob(FilestoreManagerTest.invalid_hash_one_short, FilestoreManagerTest.blob_6MB.length, u, r, new ByteArrayInputStream(FilestoreManagerTest.blob_6MB.blob)));
        Assert.assertEquals(Error_Size_Mismatch, GitblitUnitTest.filestore().uploadBlob(FilestoreManagerTest.blob_6MB.hash, 0, u, r, new ByteArrayInputStream(FilestoreManagerTest.blob_6MB.blob)));
        Assert.assertEquals(Error_Size_Mismatch, GitblitUnitTest.filestore().uploadBlob(FilestoreManagerTest.blob_6MB.hash, ((FilestoreManagerTest.blob_6MB.length) - 1), u, r, new ByteArrayInputStream(FilestoreManagerTest.blob_6MB.blob)));
        Assert.assertEquals(Error_Size_Mismatch, GitblitUnitTest.filestore().uploadBlob(FilestoreManagerTest.blob_6MB.hash, ((FilestoreManagerTest.blob_6MB.length) + 1), u, r, new ByteArrayInputStream(FilestoreManagerTest.blob_6MB.blob)));
        streamOut.reset();
        Assert.assertEquals(Unavailable, GitblitUnitTest.filestore().downloadBlob(FilestoreManagerTest.blob_6MB.hash, u, r, streamOut));
        // Good input will accept the upload
        Assert.assertEquals(Available, GitblitUnitTest.filestore().uploadBlob(FilestoreManagerTest.blob_6MB.hash, FilestoreManagerTest.blob_6MB.length, u, r, new ByteArrayInputStream(FilestoreManagerTest.blob_6MB.blob)));
        streamOut.reset();
        Assert.assertEquals(Available, GitblitUnitTest.filestore().downloadBlob(FilestoreManagerTest.blob_6MB.hash, u, r, streamOut));
        Assert.assertArrayEquals(FilestoreManagerTest.blob_6MB.blob, streamOut.toByteArray());
        // Confirm the relevant files exist
        Assert.assertTrue("Admin did not save zero length file!", GitblitUnitTest.filestore().getStoragePath(FilestoreManagerTest.blob_zero.hash).exists());
        Assert.assertTrue("Admin did not save 512KB file!", GitblitUnitTest.filestore().getStoragePath(FilestoreManagerTest.blob_512KB.hash).exists());
        Assert.assertTrue("Admin did not save 6MB file!", GitblitUnitTest.filestore().getStoragePath(FilestoreManagerTest.blob_6MB.hash).exists());
        // Clear the files and cache to test upload limit property
        FileUtils.delete(GitblitUnitTest.filestore().getStorageFolder());
        GitblitUnitTest.filestore().clearFilestoreCache();
        GitblitUnitTest.settings().overrideSetting(maxUploadSize, FilestoreManagerTest.download_limit_test);
        Assert.assertEquals(Available, GitblitUnitTest.filestore().uploadBlob(FilestoreManagerTest.blob_512KB.hash, FilestoreManagerTest.blob_512KB.length, u, r, new ByteArrayInputStream(FilestoreManagerTest.blob_512KB.blob)));
        streamOut.reset();
        Assert.assertEquals(Available, GitblitUnitTest.filestore().downloadBlob(FilestoreManagerTest.blob_512KB.hash, u, r, streamOut));
        Assert.assertArrayEquals(FilestoreManagerTest.blob_512KB.blob, streamOut.toByteArray());
        Assert.assertEquals(Error_Exceeds_Size_Limit, GitblitUnitTest.filestore().uploadBlob(FilestoreManagerTest.blob_6MB.hash, FilestoreManagerTest.blob_6MB.length, u, r, new ByteArrayInputStream(FilestoreManagerTest.blob_6MB.blob)));
        streamOut.reset();
        Assert.assertEquals(Unavailable, GitblitUnitTest.filestore().downloadBlob(FilestoreManagerTest.blob_6MB.hash, u, r, streamOut));
        Assert.assertTrue("Admin did not save 512KB file!", GitblitUnitTest.filestore().getStoragePath(FilestoreManagerTest.blob_512KB.hash).exists());
        Assert.assertFalse("Admin saved 6MB file despite (over filesize limit)!", GitblitUnitTest.filestore().getStoragePath(FilestoreManagerTest.blob_6MB.hash).exists());
    }

    @Test
    public void testAuthenticatedAccess() throws Exception {
        FileUtils.delete(GitblitUnitTest.filestore().getStorageFolder());
        GitblitUnitTest.filestore().clearFilestoreCache();
        RepositoryModel r = new RepositoryModel("myrepo.git", null, null, new Date());
        r.authorizationControl = AuthorizationControl.AUTHENTICATED;
        r.accessRestriction = AccessRestrictionType.VIEW;
        ByteArrayOutputStream streamOut = new ByteArrayOutputStream();
        UserModel u = new UserModel("test");
        // No upload limit
        GitblitUnitTest.settings().overrideSetting(maxUploadSize, FilestoreManagerTest.download_limit_default);
        // Invalid hash tests
        Assert.assertEquals(Error_Invalid_Oid, GitblitUnitTest.filestore().downloadBlob(FilestoreManagerTest.invalid_hash_major, u, r, streamOut));
        Assert.assertEquals(Error_Invalid_Oid, GitblitUnitTest.filestore().downloadBlob(FilestoreManagerTest.invalid_hash_regex_attack, u, r, streamOut));
        Assert.assertEquals(Error_Invalid_Oid, GitblitUnitTest.filestore().downloadBlob(FilestoreManagerTest.invalid_hash_one_long, u, r, streamOut));
        Assert.assertEquals(Error_Invalid_Oid, GitblitUnitTest.filestore().downloadBlob(FilestoreManagerTest.invalid_hash_one_short, u, r, streamOut));
        // Download prior to upload
        streamOut.reset();
        Assert.assertEquals(Unavailable, GitblitUnitTest.filestore().downloadBlob(FilestoreManagerTest.blob_512KB.hash, u, r, streamOut));
        streamOut.reset();
        Assert.assertEquals(Unavailable, GitblitUnitTest.filestore().downloadBlob(FilestoreManagerTest.blob_512KB.hash, u, r, streamOut));
        // Bad input is rejected with no upload taking place
        Assert.assertEquals(Error_Invalid_Oid, GitblitUnitTest.filestore().uploadBlob(FilestoreManagerTest.invalid_hash_empty, FilestoreManagerTest.blob_512KB.length, u, r, new ByteArrayInputStream(FilestoreManagerTest.blob_512KB.blob)));
        Assert.assertEquals(Error_Invalid_Oid, GitblitUnitTest.filestore().uploadBlob(FilestoreManagerTest.invalid_hash_major, FilestoreManagerTest.blob_512KB.length, u, r, new ByteArrayInputStream(FilestoreManagerTest.blob_512KB.blob)));
        Assert.assertEquals(Error_Invalid_Oid, GitblitUnitTest.filestore().uploadBlob(FilestoreManagerTest.invalid_hash_regex_attack, FilestoreManagerTest.blob_512KB.length, u, r, new ByteArrayInputStream(FilestoreManagerTest.blob_512KB.blob)));
        Assert.assertEquals(Error_Invalid_Oid, GitblitUnitTest.filestore().uploadBlob(FilestoreManagerTest.invalid_hash_one_long, FilestoreManagerTest.blob_512KB.length, u, r, new ByteArrayInputStream(FilestoreManagerTest.blob_512KB.blob)));
        Assert.assertEquals(Error_Invalid_Oid, GitblitUnitTest.filestore().uploadBlob(FilestoreManagerTest.invalid_hash_one_short, FilestoreManagerTest.blob_512KB.length, u, r, new ByteArrayInputStream(FilestoreManagerTest.blob_512KB.blob)));
        Assert.assertEquals(Error_Invalid_Size, GitblitUnitTest.filestore().uploadBlob(FilestoreManagerTest.blob_512KB.hash, (-1), u, r, new ByteArrayInputStream(FilestoreManagerTest.blob_512KB.blob)));
        Assert.assertEquals(Error_Size_Mismatch, GitblitUnitTest.filestore().uploadBlob(FilestoreManagerTest.blob_512KB.hash, 0, u, r, new ByteArrayInputStream(FilestoreManagerTest.blob_512KB.blob)));
        Assert.assertEquals(Error_Size_Mismatch, GitblitUnitTest.filestore().uploadBlob(FilestoreManagerTest.blob_512KB.hash, ((FilestoreManagerTest.blob_512KB.length) - 1), u, r, new ByteArrayInputStream(FilestoreManagerTest.blob_512KB.blob)));
        Assert.assertEquals(Error_Size_Mismatch, GitblitUnitTest.filestore().uploadBlob(FilestoreManagerTest.blob_512KB.hash, ((FilestoreManagerTest.blob_512KB.length) + 1), u, r, new ByteArrayInputStream(FilestoreManagerTest.blob_512KB.blob)));
        Assert.assertEquals(Error_Hash_Mismatch, GitblitUnitTest.filestore().uploadBlob(FilestoreManagerTest.blob_zero.hash, FilestoreManagerTest.blob_512KB.length, u, r, new ByteArrayInputStream(FilestoreManagerTest.blob_512KB.blob)));
        // Confirm no upload with bad input
        streamOut.reset();
        Assert.assertEquals(Unavailable, GitblitUnitTest.filestore().downloadBlob(FilestoreManagerTest.blob_512KB.hash, u, r, streamOut));
        // Good input will accept the upload
        Assert.assertEquals(Available, GitblitUnitTest.filestore().uploadBlob(FilestoreManagerTest.blob_512KB.hash, FilestoreManagerTest.blob_512KB.length, u, r, new ByteArrayInputStream(FilestoreManagerTest.blob_512KB.blob)));
        streamOut.reset();
        Assert.assertEquals(Available, GitblitUnitTest.filestore().downloadBlob(FilestoreManagerTest.blob_512KB.hash, u, r, streamOut));
        Assert.assertArrayEquals(FilestoreManagerTest.blob_512KB.blob, streamOut.toByteArray());
        // Subsequent failed uploads do not affect file
        Assert.assertEquals(Error_Size_Mismatch, GitblitUnitTest.filestore().uploadBlob(FilestoreManagerTest.blob_512KB.hash, ((FilestoreManagerTest.blob_512KB.length) - 1), u, r, new ByteArrayInputStream(FilestoreManagerTest.blob_512KB.blob)));
        Assert.assertEquals(Error_Size_Mismatch, GitblitUnitTest.filestore().uploadBlob(FilestoreManagerTest.blob_512KB.hash, ((FilestoreManagerTest.blob_512KB.length) + 1), u, r, new ByteArrayInputStream(FilestoreManagerTest.blob_512KB.blob)));
        streamOut.reset();
        Assert.assertEquals(Available, GitblitUnitTest.filestore().downloadBlob(FilestoreManagerTest.blob_512KB.hash, u, r, streamOut));
        Assert.assertArrayEquals(FilestoreManagerTest.blob_512KB.blob, streamOut.toByteArray());
        // Zero length upload is valid
        Assert.assertEquals(Available, GitblitUnitTest.filestore().uploadBlob(FilestoreManagerTest.blob_zero.hash, FilestoreManagerTest.blob_zero.length, u, r, new ByteArrayInputStream(FilestoreManagerTest.blob_zero.blob)));
        streamOut.reset();
        Assert.assertEquals(Available, GitblitUnitTest.filestore().downloadBlob(FilestoreManagerTest.blob_zero.hash, u, r, streamOut));
        Assert.assertArrayEquals(FilestoreManagerTest.blob_zero.blob, streamOut.toByteArray());
        // Pre-informed upload identifies identical errors as immediate upload
        Assert.assertEquals(Upload_Pending, GitblitUnitTest.filestore().addObject(FilestoreManagerTest.blob_6MB.hash, FilestoreManagerTest.blob_6MB.length, u, r));
        streamOut.reset();
        Assert.assertEquals(Unavailable, GitblitUnitTest.filestore().downloadBlob(FilestoreManagerTest.blob_6MB.hash, u, r, streamOut));
        Assert.assertEquals(Error_Invalid_Oid, GitblitUnitTest.filestore().uploadBlob(FilestoreManagerTest.invalid_hash_empty, FilestoreManagerTest.blob_6MB.length, u, r, new ByteArrayInputStream(FilestoreManagerTest.blob_6MB.blob)));
        Assert.assertEquals(Error_Invalid_Oid, GitblitUnitTest.filestore().uploadBlob(FilestoreManagerTest.invalid_hash_major, FilestoreManagerTest.blob_6MB.length, u, r, new ByteArrayInputStream(FilestoreManagerTest.blob_6MB.blob)));
        Assert.assertEquals(Error_Invalid_Oid, GitblitUnitTest.filestore().uploadBlob(FilestoreManagerTest.invalid_hash_regex_attack, FilestoreManagerTest.blob_6MB.length, u, r, new ByteArrayInputStream(FilestoreManagerTest.blob_6MB.blob)));
        Assert.assertEquals(Error_Invalid_Oid, GitblitUnitTest.filestore().uploadBlob(FilestoreManagerTest.invalid_hash_one_long, FilestoreManagerTest.blob_6MB.length, u, r, new ByteArrayInputStream(FilestoreManagerTest.blob_6MB.blob)));
        Assert.assertEquals(Error_Invalid_Oid, GitblitUnitTest.filestore().uploadBlob(FilestoreManagerTest.invalid_hash_one_short, FilestoreManagerTest.blob_6MB.length, u, r, new ByteArrayInputStream(FilestoreManagerTest.blob_6MB.blob)));
        Assert.assertEquals(Error_Size_Mismatch, GitblitUnitTest.filestore().uploadBlob(FilestoreManagerTest.blob_6MB.hash, 0, u, r, new ByteArrayInputStream(FilestoreManagerTest.blob_6MB.blob)));
        Assert.assertEquals(Error_Size_Mismatch, GitblitUnitTest.filestore().uploadBlob(FilestoreManagerTest.blob_6MB.hash, ((FilestoreManagerTest.blob_6MB.length) - 1), u, r, new ByteArrayInputStream(FilestoreManagerTest.blob_6MB.blob)));
        Assert.assertEquals(Error_Size_Mismatch, GitblitUnitTest.filestore().uploadBlob(FilestoreManagerTest.blob_6MB.hash, ((FilestoreManagerTest.blob_6MB.length) + 1), u, r, new ByteArrayInputStream(FilestoreManagerTest.blob_6MB.blob)));
        streamOut.reset();
        Assert.assertEquals(Unavailable, GitblitUnitTest.filestore().downloadBlob(FilestoreManagerTest.blob_6MB.hash, u, r, streamOut));
        // Good input will accept the upload
        Assert.assertEquals(Available, GitblitUnitTest.filestore().uploadBlob(FilestoreManagerTest.blob_6MB.hash, FilestoreManagerTest.blob_6MB.length, u, r, new ByteArrayInputStream(FilestoreManagerTest.blob_6MB.blob)));
        streamOut.reset();
        Assert.assertEquals(Available, GitblitUnitTest.filestore().downloadBlob(FilestoreManagerTest.blob_6MB.hash, u, r, streamOut));
        Assert.assertArrayEquals(FilestoreManagerTest.blob_6MB.blob, streamOut.toByteArray());
        // Confirm the relevant files exist
        Assert.assertTrue("Authenticated user did not save zero length file!", GitblitUnitTest.filestore().getStoragePath(FilestoreManagerTest.blob_zero.hash).exists());
        Assert.assertTrue("Authenticated user did not save 512KB file!", GitblitUnitTest.filestore().getStoragePath(FilestoreManagerTest.blob_512KB.hash).exists());
        Assert.assertTrue("Authenticated user did not save 6MB file!", GitblitUnitTest.filestore().getStoragePath(FilestoreManagerTest.blob_6MB.hash).exists());
        // Clear the files and cache to test upload limit property
        FileUtils.delete(GitblitUnitTest.filestore().getStorageFolder());
        GitblitUnitTest.filestore().clearFilestoreCache();
        GitblitUnitTest.settings().overrideSetting(maxUploadSize, FilestoreManagerTest.download_limit_test);
        Assert.assertEquals(Available, GitblitUnitTest.filestore().uploadBlob(FilestoreManagerTest.blob_512KB.hash, FilestoreManagerTest.blob_512KB.length, u, r, new ByteArrayInputStream(FilestoreManagerTest.blob_512KB.blob)));
        streamOut.reset();
        Assert.assertEquals(Available, GitblitUnitTest.filestore().downloadBlob(FilestoreManagerTest.blob_512KB.hash, u, r, streamOut));
        Assert.assertArrayEquals(FilestoreManagerTest.blob_512KB.blob, streamOut.toByteArray());
        Assert.assertEquals(Error_Exceeds_Size_Limit, GitblitUnitTest.filestore().uploadBlob(FilestoreManagerTest.blob_6MB.hash, FilestoreManagerTest.blob_6MB.length, u, r, new ByteArrayInputStream(FilestoreManagerTest.blob_6MB.blob)));
        streamOut.reset();
        Assert.assertEquals(Unavailable, GitblitUnitTest.filestore().downloadBlob(FilestoreManagerTest.blob_6MB.hash, u, r, streamOut));
        Assert.assertTrue("Authenticated user did not save 512KB file!", GitblitUnitTest.filestore().getStoragePath(FilestoreManagerTest.blob_512KB.hash).exists());
        Assert.assertFalse("Authenticated user saved 6MB file (over filesize limit)!", GitblitUnitTest.filestore().getStoragePath(FilestoreManagerTest.blob_6MB.hash).exists());
    }

    @Test
    public void testAnonymousAccess() throws Exception {
        FileUtils.delete(GitblitUnitTest.filestore().getStorageFolder());
        GitblitUnitTest.filestore().clearFilestoreCache();
        RepositoryModel r = new RepositoryModel("myrepo.git", null, null, new Date());
        r.authorizationControl = AuthorizationControl.NAMED;
        r.accessRestriction = AccessRestrictionType.CLONE;
        ByteArrayOutputStream streamOut = new ByteArrayOutputStream();
        UserModel u = UserModel.ANONYMOUS;
        // No upload limit
        GitblitUnitTest.settings().overrideSetting(maxUploadSize, FilestoreManagerTest.download_limit_default);
        // Invalid hash tests
        Assert.assertEquals(Error_Invalid_Oid, GitblitUnitTest.filestore().downloadBlob(FilestoreManagerTest.invalid_hash_major, u, r, streamOut));
        Assert.assertEquals(Error_Invalid_Oid, GitblitUnitTest.filestore().downloadBlob(FilestoreManagerTest.invalid_hash_regex_attack, u, r, streamOut));
        Assert.assertEquals(Error_Invalid_Oid, GitblitUnitTest.filestore().downloadBlob(FilestoreManagerTest.invalid_hash_one_long, u, r, streamOut));
        Assert.assertEquals(Error_Invalid_Oid, GitblitUnitTest.filestore().downloadBlob(FilestoreManagerTest.invalid_hash_one_short, u, r, streamOut));
        // Download prior to upload
        streamOut.reset();
        Assert.assertEquals(Unavailable, GitblitUnitTest.filestore().downloadBlob(FilestoreManagerTest.blob_512KB.hash, u, r, streamOut));
        streamOut.reset();
        Assert.assertEquals(Unavailable, GitblitUnitTest.filestore().downloadBlob(FilestoreManagerTest.blob_512KB.hash, u, r, streamOut));
        // Bad input is rejected with no upload taking place
        Assert.assertEquals(AuthenticationRequired, GitblitUnitTest.filestore().uploadBlob(FilestoreManagerTest.invalid_hash_empty, FilestoreManagerTest.blob_512KB.length, u, r, new ByteArrayInputStream(FilestoreManagerTest.blob_512KB.blob)));
        Assert.assertEquals(AuthenticationRequired, GitblitUnitTest.filestore().uploadBlob(FilestoreManagerTest.invalid_hash_major, FilestoreManagerTest.blob_512KB.length, u, r, new ByteArrayInputStream(FilestoreManagerTest.blob_512KB.blob)));
        Assert.assertEquals(AuthenticationRequired, GitblitUnitTest.filestore().uploadBlob(FilestoreManagerTest.invalid_hash_regex_attack, FilestoreManagerTest.blob_512KB.length, u, r, new ByteArrayInputStream(FilestoreManagerTest.blob_512KB.blob)));
        Assert.assertEquals(AuthenticationRequired, GitblitUnitTest.filestore().uploadBlob(FilestoreManagerTest.invalid_hash_one_long, FilestoreManagerTest.blob_512KB.length, u, r, new ByteArrayInputStream(FilestoreManagerTest.blob_512KB.blob)));
        Assert.assertEquals(AuthenticationRequired, GitblitUnitTest.filestore().uploadBlob(FilestoreManagerTest.invalid_hash_one_short, FilestoreManagerTest.blob_512KB.length, u, r, new ByteArrayInputStream(FilestoreManagerTest.blob_512KB.blob)));
        Assert.assertEquals(AuthenticationRequired, GitblitUnitTest.filestore().uploadBlob(FilestoreManagerTest.blob_512KB.hash, (-1), u, r, new ByteArrayInputStream(FilestoreManagerTest.blob_512KB.blob)));
        Assert.assertEquals(AuthenticationRequired, GitblitUnitTest.filestore().uploadBlob(FilestoreManagerTest.blob_512KB.hash, 0, u, r, new ByteArrayInputStream(FilestoreManagerTest.blob_512KB.blob)));
        Assert.assertEquals(AuthenticationRequired, GitblitUnitTest.filestore().uploadBlob(FilestoreManagerTest.blob_512KB.hash, ((FilestoreManagerTest.blob_512KB.length) - 1), u, r, new ByteArrayInputStream(FilestoreManagerTest.blob_512KB.blob)));
        Assert.assertEquals(AuthenticationRequired, GitblitUnitTest.filestore().uploadBlob(FilestoreManagerTest.blob_512KB.hash, ((FilestoreManagerTest.blob_512KB.length) + 1), u, r, new ByteArrayInputStream(FilestoreManagerTest.blob_512KB.blob)));
        Assert.assertEquals(AuthenticationRequired, GitblitUnitTest.filestore().uploadBlob(FilestoreManagerTest.blob_zero.hash, FilestoreManagerTest.blob_512KB.length, u, r, new ByteArrayInputStream(FilestoreManagerTest.blob_512KB.blob)));
        // Confirm no upload with bad input
        streamOut.reset();
        Assert.assertEquals(Unavailable, GitblitUnitTest.filestore().downloadBlob(FilestoreManagerTest.blob_512KB.hash, u, r, streamOut));
        // Good input will accept the upload
        Assert.assertEquals(AuthenticationRequired, GitblitUnitTest.filestore().uploadBlob(FilestoreManagerTest.blob_512KB.hash, FilestoreManagerTest.blob_512KB.length, u, r, new ByteArrayInputStream(FilestoreManagerTest.blob_512KB.blob)));
        streamOut.reset();
        Assert.assertEquals(Unavailable, GitblitUnitTest.filestore().downloadBlob(FilestoreManagerTest.blob_512KB.hash, u, r, streamOut));
        // Subsequent failed uploads do not affect file
        Assert.assertEquals(AuthenticationRequired, GitblitUnitTest.filestore().uploadBlob(FilestoreManagerTest.blob_512KB.hash, ((FilestoreManagerTest.blob_512KB.length) - 1), u, r, new ByteArrayInputStream(FilestoreManagerTest.blob_512KB.blob)));
        Assert.assertEquals(AuthenticationRequired, GitblitUnitTest.filestore().uploadBlob(FilestoreManagerTest.blob_512KB.hash, ((FilestoreManagerTest.blob_512KB.length) + 1), u, r, new ByteArrayInputStream(FilestoreManagerTest.blob_512KB.blob)));
        streamOut.reset();
        Assert.assertEquals(Unavailable, GitblitUnitTest.filestore().downloadBlob(FilestoreManagerTest.blob_512KB.hash, u, r, streamOut));
        // Zero length upload is valid
        Assert.assertEquals(AuthenticationRequired, GitblitUnitTest.filestore().uploadBlob(FilestoreManagerTest.blob_zero.hash, FilestoreManagerTest.blob_zero.length, u, r, new ByteArrayInputStream(FilestoreManagerTest.blob_zero.blob)));
        streamOut.reset();
        Assert.assertEquals(Unavailable, GitblitUnitTest.filestore().downloadBlob(FilestoreManagerTest.blob_zero.hash, u, r, streamOut));
        // Pre-informed upload identifies identical errors as immediate upload
        Assert.assertEquals(AuthenticationRequired, GitblitUnitTest.filestore().addObject(FilestoreManagerTest.blob_6MB.hash, FilestoreManagerTest.blob_6MB.length, u, r));
        streamOut.reset();
        Assert.assertEquals(Unavailable, GitblitUnitTest.filestore().downloadBlob(FilestoreManagerTest.blob_6MB.hash, u, r, streamOut));
        Assert.assertEquals(AuthenticationRequired, GitblitUnitTest.filestore().uploadBlob(FilestoreManagerTest.invalid_hash_empty, FilestoreManagerTest.blob_6MB.length, u, r, new ByteArrayInputStream(FilestoreManagerTest.blob_6MB.blob)));
        Assert.assertEquals(AuthenticationRequired, GitblitUnitTest.filestore().uploadBlob(FilestoreManagerTest.invalid_hash_major, FilestoreManagerTest.blob_6MB.length, u, r, new ByteArrayInputStream(FilestoreManagerTest.blob_6MB.blob)));
        Assert.assertEquals(AuthenticationRequired, GitblitUnitTest.filestore().uploadBlob(FilestoreManagerTest.invalid_hash_regex_attack, FilestoreManagerTest.blob_6MB.length, u, r, new ByteArrayInputStream(FilestoreManagerTest.blob_6MB.blob)));
        Assert.assertEquals(AuthenticationRequired, GitblitUnitTest.filestore().uploadBlob(FilestoreManagerTest.invalid_hash_one_long, FilestoreManagerTest.blob_6MB.length, u, r, new ByteArrayInputStream(FilestoreManagerTest.blob_6MB.blob)));
        Assert.assertEquals(AuthenticationRequired, GitblitUnitTest.filestore().uploadBlob(FilestoreManagerTest.invalid_hash_one_short, FilestoreManagerTest.blob_6MB.length, u, r, new ByteArrayInputStream(FilestoreManagerTest.blob_6MB.blob)));
        Assert.assertEquals(AuthenticationRequired, GitblitUnitTest.filestore().uploadBlob(FilestoreManagerTest.blob_6MB.hash, (-1), u, r, new ByteArrayInputStream(FilestoreManagerTest.blob_6MB.blob)));
        Assert.assertEquals(AuthenticationRequired, GitblitUnitTest.filestore().uploadBlob(FilestoreManagerTest.blob_6MB.hash, 0, u, r, new ByteArrayInputStream(FilestoreManagerTest.blob_6MB.blob)));
        Assert.assertEquals(AuthenticationRequired, GitblitUnitTest.filestore().uploadBlob(FilestoreManagerTest.blob_6MB.hash, ((FilestoreManagerTest.blob_6MB.length) - 1), u, r, new ByteArrayInputStream(FilestoreManagerTest.blob_6MB.blob)));
        Assert.assertEquals(AuthenticationRequired, GitblitUnitTest.filestore().uploadBlob(FilestoreManagerTest.blob_6MB.hash, ((FilestoreManagerTest.blob_6MB.length) + 1), u, r, new ByteArrayInputStream(FilestoreManagerTest.blob_6MB.blob)));
        streamOut.reset();
        Assert.assertEquals(Unavailable, GitblitUnitTest.filestore().downloadBlob(FilestoreManagerTest.blob_6MB.hash, u, r, streamOut));
        // Good input will accept the upload
        Assert.assertEquals(AuthenticationRequired, GitblitUnitTest.filestore().uploadBlob(FilestoreManagerTest.blob_6MB.hash, FilestoreManagerTest.blob_6MB.length, u, r, new ByteArrayInputStream(FilestoreManagerTest.blob_6MB.blob)));
        streamOut.reset();
        Assert.assertEquals(Unavailable, GitblitUnitTest.filestore().downloadBlob(FilestoreManagerTest.blob_6MB.hash, u, r, streamOut));
        // Confirm the relevant files do not exist
        Assert.assertFalse("Anonymous user saved zero length file!", GitblitUnitTest.filestore().getStoragePath(FilestoreManagerTest.blob_zero.hash).exists());
        Assert.assertFalse("Anonymous user 512KB file!", GitblitUnitTest.filestore().getStoragePath(FilestoreManagerTest.blob_512KB.hash).exists());
        Assert.assertFalse("Anonymous user 6MB file!", GitblitUnitTest.filestore().getStoragePath(FilestoreManagerTest.blob_6MB.hash).exists());
        // Clear the files and cache to test upload limit property
        FileUtils.delete(GitblitUnitTest.filestore().getStorageFolder());
        GitblitUnitTest.filestore().clearFilestoreCache();
        GitblitUnitTest.settings().overrideSetting(maxUploadSize, FilestoreManagerTest.download_limit_test);
        Assert.assertEquals(AuthenticationRequired, GitblitUnitTest.filestore().uploadBlob(FilestoreManagerTest.blob_512KB.hash, FilestoreManagerTest.blob_512KB.length, u, r, new ByteArrayInputStream(FilestoreManagerTest.blob_512KB.blob)));
        streamOut.reset();
        Assert.assertEquals(Unavailable, GitblitUnitTest.filestore().downloadBlob(FilestoreManagerTest.blob_512KB.hash, u, r, streamOut));
        Assert.assertEquals(AuthenticationRequired, GitblitUnitTest.filestore().uploadBlob(FilestoreManagerTest.blob_6MB.hash, FilestoreManagerTest.blob_6MB.length, u, r, new ByteArrayInputStream(FilestoreManagerTest.blob_6MB.blob)));
        streamOut.reset();
        Assert.assertEquals(Unavailable, GitblitUnitTest.filestore().downloadBlob(FilestoreManagerTest.blob_6MB.hash, u, r, streamOut));
        Assert.assertFalse("Anonymous user saved 512KB file!", GitblitUnitTest.filestore().getStoragePath(FilestoreManagerTest.blob_512KB.hash).exists());
        Assert.assertFalse("Anonymous user saved 6MB file (over filesize limit)!", GitblitUnitTest.filestore().getStoragePath(FilestoreManagerTest.blob_6MB.hash).exists());
    }

    @Test
    public void testUnauthorizedAccess() throws Exception {
        FileUtils.delete(GitblitUnitTest.filestore().getStorageFolder());
        GitblitUnitTest.filestore().clearFilestoreCache();
        RepositoryModel r = new RepositoryModel("myrepo.git", null, null, new Date());
        r.authorizationControl = AuthorizationControl.NAMED;
        r.accessRestriction = AccessRestrictionType.VIEW;
        ByteArrayOutputStream streamOut = new ByteArrayOutputStream();
        UserModel u = new UserModel("test");
        u.setRepositoryPermission(r.name, CLONE);
        // No upload limit
        GitblitUnitTest.settings().overrideSetting(maxUploadSize, FilestoreManagerTest.download_limit_default);
        // Invalid hash tests
        Assert.assertEquals(Error_Invalid_Oid, GitblitUnitTest.filestore().downloadBlob(FilestoreManagerTest.invalid_hash_major, u, r, streamOut));
        Assert.assertEquals(Error_Invalid_Oid, GitblitUnitTest.filestore().downloadBlob(FilestoreManagerTest.invalid_hash_regex_attack, u, r, streamOut));
        Assert.assertEquals(Error_Invalid_Oid, GitblitUnitTest.filestore().downloadBlob(FilestoreManagerTest.invalid_hash_one_long, u, r, streamOut));
        Assert.assertEquals(Error_Invalid_Oid, GitblitUnitTest.filestore().downloadBlob(FilestoreManagerTest.invalid_hash_one_short, u, r, streamOut));
        // Download prior to upload
        streamOut.reset();
        Assert.assertEquals(Unavailable, GitblitUnitTest.filestore().downloadBlob(FilestoreManagerTest.blob_512KB.hash, u, r, streamOut));
        streamOut.reset();
        Assert.assertEquals(Unavailable, GitblitUnitTest.filestore().downloadBlob(FilestoreManagerTest.blob_512KB.hash, u, r, streamOut));
        // Bad input is rejected with no upload taking place
        Assert.assertEquals(Error_Unauthorized, GitblitUnitTest.filestore().uploadBlob(FilestoreManagerTest.invalid_hash_major, FilestoreManagerTest.blob_512KB.length, u, r, new ByteArrayInputStream(FilestoreManagerTest.blob_512KB.blob)));
        Assert.assertEquals(Error_Unauthorized, GitblitUnitTest.filestore().uploadBlob(FilestoreManagerTest.invalid_hash_regex_attack, FilestoreManagerTest.blob_512KB.length, u, r, new ByteArrayInputStream(FilestoreManagerTest.blob_512KB.blob)));
        Assert.assertEquals(Error_Unauthorized, GitblitUnitTest.filestore().uploadBlob(FilestoreManagerTest.invalid_hash_one_long, FilestoreManagerTest.blob_512KB.length, u, r, new ByteArrayInputStream(FilestoreManagerTest.blob_512KB.blob)));
        Assert.assertEquals(Error_Unauthorized, GitblitUnitTest.filestore().uploadBlob(FilestoreManagerTest.invalid_hash_empty, FilestoreManagerTest.blob_512KB.length, u, r, new ByteArrayInputStream(FilestoreManagerTest.blob_512KB.blob)));
        Assert.assertEquals(Error_Unauthorized, GitblitUnitTest.filestore().uploadBlob(FilestoreManagerTest.invalid_hash_one_short, FilestoreManagerTest.blob_512KB.length, u, r, new ByteArrayInputStream(FilestoreManagerTest.blob_512KB.blob)));
        Assert.assertEquals(Error_Unauthorized, GitblitUnitTest.filestore().uploadBlob(FilestoreManagerTest.blob_512KB.hash, (-1), u, r, new ByteArrayInputStream(FilestoreManagerTest.blob_512KB.blob)));
        Assert.assertEquals(Error_Unauthorized, GitblitUnitTest.filestore().uploadBlob(FilestoreManagerTest.blob_512KB.hash, 0, u, r, new ByteArrayInputStream(FilestoreManagerTest.blob_512KB.blob)));
        Assert.assertEquals(Error_Unauthorized, GitblitUnitTest.filestore().uploadBlob(FilestoreManagerTest.blob_512KB.hash, ((FilestoreManagerTest.blob_512KB.length) - 1), u, r, new ByteArrayInputStream(FilestoreManagerTest.blob_512KB.blob)));
        Assert.assertEquals(Error_Unauthorized, GitblitUnitTest.filestore().uploadBlob(FilestoreManagerTest.blob_512KB.hash, ((FilestoreManagerTest.blob_512KB.length) + 1), u, r, new ByteArrayInputStream(FilestoreManagerTest.blob_512KB.blob)));
        Assert.assertEquals(Error_Unauthorized, GitblitUnitTest.filestore().uploadBlob(FilestoreManagerTest.blob_zero.hash, FilestoreManagerTest.blob_512KB.length, u, r, new ByteArrayInputStream(FilestoreManagerTest.blob_512KB.blob)));
        // Confirm no upload with bad input
        streamOut.reset();
        Assert.assertEquals(Unavailable, GitblitUnitTest.filestore().downloadBlob(FilestoreManagerTest.blob_512KB.hash, u, r, streamOut));
        // Good input will accept the upload
        Assert.assertEquals(Error_Unauthorized, GitblitUnitTest.filestore().uploadBlob(FilestoreManagerTest.blob_512KB.hash, FilestoreManagerTest.blob_512KB.length, u, r, new ByteArrayInputStream(FilestoreManagerTest.blob_512KB.blob)));
        streamOut.reset();
        Assert.assertEquals(Unavailable, GitblitUnitTest.filestore().downloadBlob(FilestoreManagerTest.blob_512KB.hash, u, r, streamOut));
        // Subsequent failed uploads do not affect file
        Assert.assertEquals(Error_Unauthorized, GitblitUnitTest.filestore().uploadBlob(FilestoreManagerTest.blob_512KB.hash, ((FilestoreManagerTest.blob_512KB.length) - 1), u, r, new ByteArrayInputStream(FilestoreManagerTest.blob_512KB.blob)));
        Assert.assertEquals(Error_Unauthorized, GitblitUnitTest.filestore().uploadBlob(FilestoreManagerTest.blob_512KB.hash, ((FilestoreManagerTest.blob_512KB.length) + 1), u, r, new ByteArrayInputStream(FilestoreManagerTest.blob_512KB.blob)));
        streamOut.reset();
        Assert.assertEquals(Unavailable, GitblitUnitTest.filestore().downloadBlob(FilestoreManagerTest.blob_512KB.hash, u, r, streamOut));
        // Zero length upload is valid
        Assert.assertEquals(Error_Unauthorized, GitblitUnitTest.filestore().uploadBlob(FilestoreManagerTest.blob_zero.hash, FilestoreManagerTest.blob_zero.length, u, r, new ByteArrayInputStream(FilestoreManagerTest.blob_zero.blob)));
        streamOut.reset();
        Assert.assertEquals(Unavailable, GitblitUnitTest.filestore().downloadBlob(FilestoreManagerTest.blob_zero.hash, u, r, streamOut));
        // Pre-informed upload identifies identical errors as immediate upload
        Assert.assertEquals(Error_Unauthorized, GitblitUnitTest.filestore().addObject(FilestoreManagerTest.blob_6MB.hash, FilestoreManagerTest.blob_6MB.length, u, r));
        streamOut.reset();
        Assert.assertEquals(Unavailable, GitblitUnitTest.filestore().downloadBlob(FilestoreManagerTest.blob_6MB.hash, u, r, streamOut));
        Assert.assertEquals(Error_Unauthorized, GitblitUnitTest.filestore().uploadBlob(FilestoreManagerTest.invalid_hash_empty, FilestoreManagerTest.blob_6MB.length, u, r, new ByteArrayInputStream(FilestoreManagerTest.blob_6MB.blob)));
        Assert.assertEquals(Error_Unauthorized, GitblitUnitTest.filestore().uploadBlob(FilestoreManagerTest.invalid_hash_major, FilestoreManagerTest.blob_6MB.length, u, r, new ByteArrayInputStream(FilestoreManagerTest.blob_6MB.blob)));
        Assert.assertEquals(Error_Unauthorized, GitblitUnitTest.filestore().uploadBlob(FilestoreManagerTest.invalid_hash_regex_attack, FilestoreManagerTest.blob_6MB.length, u, r, new ByteArrayInputStream(FilestoreManagerTest.blob_6MB.blob)));
        Assert.assertEquals(Error_Unauthorized, GitblitUnitTest.filestore().uploadBlob(FilestoreManagerTest.invalid_hash_one_long, FilestoreManagerTest.blob_6MB.length, u, r, new ByteArrayInputStream(FilestoreManagerTest.blob_6MB.blob)));
        Assert.assertEquals(Error_Unauthorized, GitblitUnitTest.filestore().uploadBlob(FilestoreManagerTest.invalid_hash_one_short, FilestoreManagerTest.blob_6MB.length, u, r, new ByteArrayInputStream(FilestoreManagerTest.blob_6MB.blob)));
        Assert.assertEquals(Error_Unauthorized, GitblitUnitTest.filestore().uploadBlob(FilestoreManagerTest.blob_6MB.hash, (-1), u, r, new ByteArrayInputStream(FilestoreManagerTest.blob_6MB.blob)));
        Assert.assertEquals(Error_Unauthorized, GitblitUnitTest.filestore().uploadBlob(FilestoreManagerTest.blob_6MB.hash, 0, u, r, new ByteArrayInputStream(FilestoreManagerTest.blob_6MB.blob)));
        Assert.assertEquals(Error_Unauthorized, GitblitUnitTest.filestore().uploadBlob(FilestoreManagerTest.blob_6MB.hash, ((FilestoreManagerTest.blob_6MB.length) - 1), u, r, new ByteArrayInputStream(FilestoreManagerTest.blob_6MB.blob)));
        Assert.assertEquals(Error_Unauthorized, GitblitUnitTest.filestore().uploadBlob(FilestoreManagerTest.blob_6MB.hash, ((FilestoreManagerTest.blob_6MB.length) + 1), u, r, new ByteArrayInputStream(FilestoreManagerTest.blob_6MB.blob)));
        streamOut.reset();
        Assert.assertEquals(Unavailable, GitblitUnitTest.filestore().downloadBlob(FilestoreManagerTest.blob_6MB.hash, u, r, streamOut));
        // Good input will accept the upload
        Assert.assertEquals(Error_Unauthorized, GitblitUnitTest.filestore().uploadBlob(FilestoreManagerTest.blob_6MB.hash, FilestoreManagerTest.blob_6MB.length, u, r, new ByteArrayInputStream(FilestoreManagerTest.blob_6MB.blob)));
        streamOut.reset();
        Assert.assertEquals(Unavailable, GitblitUnitTest.filestore().downloadBlob(FilestoreManagerTest.blob_6MB.hash, u, r, streamOut));
        // Confirm the relevant files exist
        Assert.assertFalse("Unauthorized user saved zero length file!", GitblitUnitTest.filestore().getStoragePath(FilestoreManagerTest.blob_zero.hash).exists());
        Assert.assertFalse("Unauthorized user saved 512KB file!", GitblitUnitTest.filestore().getStoragePath(FilestoreManagerTest.blob_512KB.hash).exists());
        Assert.assertFalse("Unauthorized user saved 6MB file!", GitblitUnitTest.filestore().getStoragePath(FilestoreManagerTest.blob_6MB.hash).exists());
        // Clear the files and cache to test upload limit property
        FileUtils.delete(GitblitUnitTest.filestore().getStorageFolder());
        GitblitUnitTest.filestore().clearFilestoreCache();
        GitblitUnitTest.settings().overrideSetting(maxUploadSize, FilestoreManagerTest.download_limit_test);
        Assert.assertEquals(Error_Unauthorized, GitblitUnitTest.filestore().uploadBlob(FilestoreManagerTest.blob_512KB.hash, FilestoreManagerTest.blob_512KB.length, u, r, new ByteArrayInputStream(FilestoreManagerTest.blob_512KB.blob)));
        streamOut.reset();
        Assert.assertEquals(Unavailable, GitblitUnitTest.filestore().downloadBlob(FilestoreManagerTest.blob_512KB.hash, u, r, streamOut));
        Assert.assertEquals(Error_Unauthorized, GitblitUnitTest.filestore().uploadBlob(FilestoreManagerTest.blob_6MB.hash, FilestoreManagerTest.blob_6MB.length, u, r, new ByteArrayInputStream(FilestoreManagerTest.blob_6MB.blob)));
        streamOut.reset();
        Assert.assertEquals(Unavailable, GitblitUnitTest.filestore().downloadBlob(FilestoreManagerTest.blob_6MB.hash, u, r, streamOut));
        Assert.assertFalse("Unauthorized user saved 512KB file!", GitblitUnitTest.filestore().getStoragePath(FilestoreManagerTest.blob_512KB.hash).exists());
        Assert.assertFalse("Unauthorized user saved 6MB file (over filesize limit)!", GitblitUnitTest.filestore().getStoragePath(FilestoreManagerTest.blob_6MB.hash).exists());
    }
}

