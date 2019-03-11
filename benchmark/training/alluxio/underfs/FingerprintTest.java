/**
 * The Alluxio Open Foundation licenses this work under the Apache License, version 2.0
 * (the "License"). You may not use this work except in compliance with the License, which is
 * available at www.apache.org/licenses/LICENSE-2.0
 *
 * This software is distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied, as more fully set forth in the License.
 *
 * See the NOTICE file distributed with this work for information regarding copyright ownership.
 */
package alluxio.underfs;


import Fingerprint.Tag.ACL;
import Fingerprint.Tag.GROUP;
import Fingerprint.Tag.MODE;
import Fingerprint.Tag.OWNER;
import alluxio.security.authorization.AccessControlList;
import alluxio.util.CommonUtils;
import java.util.Arrays;
import java.util.Random;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for the {@link Fingerprint} class.
 */
public final class FingerprintTest {
    private Random mRandom = new Random();

    @Test
    public void parseFileFingerprint() {
        UfsStatus status = new UfsFileStatus(CommonUtils.randomAlphaNumString(10), CommonUtils.randomAlphaNumString(10), mRandom.nextLong(), mRandom.nextLong(), CommonUtils.randomAlphaNumString(10), CommonUtils.randomAlphaNumString(10), ((short) (mRandom.nextInt())));
        Fingerprint fp = Fingerprint.create(CommonUtils.randomAlphaNumString(10), status);
        String expected = fp.serialize();
        Assert.assertNotNull(expected);
        Assert.assertEquals(expected, Fingerprint.parse(expected).serialize());
    }

    @Test
    public void parseDirectoryFingerprint() {
        UfsStatus status = new UfsDirectoryStatus(CommonUtils.randomAlphaNumString(10), CommonUtils.randomAlphaNumString(10), CommonUtils.randomAlphaNumString(10), ((short) (mRandom.nextInt())));
        Fingerprint fp = Fingerprint.create(CommonUtils.randomAlphaNumString(10), status);
        String expected = fp.serialize();
        Assert.assertNotNull(expected);
        Assert.assertEquals(expected, Fingerprint.parse(expected).serialize());
    }

    @Test
    public void parseInvalidFingerprint() {
        Fingerprint fp = Fingerprint.create(CommonUtils.randomAlphaNumString(10), null);
        String expected = fp.serialize();
        Assert.assertNotNull(expected);
        Assert.assertEquals(expected, Fingerprint.parse(expected).serialize());
    }

    @Test
    public void matchMetadataOrContent() {
        String name = CommonUtils.randomAlphaNumString(10);
        String contentHash = CommonUtils.randomAlphaNumString(10);
        String contentHash2 = CommonUtils.randomAlphaNumString(11);
        Long contentLength = mRandom.nextLong();
        Long lastModifiedTimeMs = mRandom.nextLong();
        String owner = CommonUtils.randomAlphaNumString(10);
        String group = CommonUtils.randomAlphaNumString(10);
        short mode = ((short) (mRandom.nextInt()));
        String ufsName = CommonUtils.randomAlphaNumString(10);
        UfsFileStatus status = new UfsFileStatus(name, contentHash, contentLength, lastModifiedTimeMs, owner, group, mode);
        UfsFileStatus metadataChangedStatus = new UfsFileStatus(name, contentHash, contentLength, lastModifiedTimeMs, CommonUtils.randomAlphaNumString(10), CommonUtils.randomAlphaNumString(10), mode);
        UfsFileStatus dataChangedStatus = new UfsFileStatus(name, contentHash2, contentLength, lastModifiedTimeMs, owner, group, mode);
        Fingerprint fp = Fingerprint.create(ufsName, status);
        Fingerprint fpMetadataChanged = Fingerprint.create(ufsName, metadataChangedStatus);
        Fingerprint fpDataChanged = Fingerprint.create(ufsName, dataChangedStatus);
        Assert.assertTrue(fp.matchMetadata(fp));
        Assert.assertFalse(fp.matchMetadata(fpMetadataChanged));
        Assert.assertTrue(fp.matchContent(fpMetadataChanged));
        Assert.assertFalse(fp.matchContent(fpDataChanged));
        Assert.assertTrue(fp.matchMetadata(fpDataChanged));
    }

    @Test
    public void createFingerprintFromUfsStatus() {
        String name = CommonUtils.randomAlphaNumString(10);
        String owner = CommonUtils.randomAlphaNumString(10);
        String group = CommonUtils.randomAlphaNumString(10);
        short mode = ((short) (mRandom.nextInt()));
        String ufsName = CommonUtils.randomAlphaNumString(10);
        UfsDirectoryStatus dirStatus = new UfsDirectoryStatus(name, owner, group, mode);
        Fingerprint fp = Fingerprint.create(ufsName, dirStatus);
        Assert.assertEquals(owner, fp.getTag(OWNER));
        Assert.assertEquals(group, fp.getTag(GROUP));
        Assert.assertEquals(String.valueOf(mode), fp.getTag(MODE));
        String contentHash = CommonUtils.randomAlphaNumString(10);
        Long contentLength = mRandom.nextLong();
        Long lastModifiedTimeMs = mRandom.nextLong();
        UfsFileStatus fileStatus = new UfsFileStatus(name, contentHash, contentLength, lastModifiedTimeMs, owner, group, mode);
        fp = Fingerprint.create(ufsName, fileStatus);
        Assert.assertEquals(owner, fp.getTag(OWNER));
        Assert.assertEquals(group, fp.getTag(GROUP));
        Assert.assertEquals(String.valueOf(mode), fp.getTag(MODE));
    }

    @Test
    public void createACLFingeprint() {
        UfsStatus status = new UfsFileStatus(CommonUtils.randomAlphaNumString(10), CommonUtils.randomAlphaNumString(10), mRandom.nextLong(), mRandom.nextLong(), CommonUtils.randomAlphaNumString(10), CommonUtils.randomAlphaNumString(10), ((short) (mRandom.nextInt())));
        AccessControlList acl = AccessControlList.fromStringEntries(CommonUtils.randomAlphaNumString(10), CommonUtils.randomAlphaNumString(10), Arrays.asList("user::rw-", "group::r--", "other::rwx"));
        Fingerprint fp = Fingerprint.create(CommonUtils.randomAlphaNumString(10), status, acl);
        String expected = fp.serialize();
        Assert.assertNotNull(expected);
        Assert.assertEquals("user::rw-,group::r--,other::rwx", Fingerprint.parse(expected).getTag(ACL));
        Assert.assertEquals(expected, Fingerprint.parse(expected).serialize());
    }
}

