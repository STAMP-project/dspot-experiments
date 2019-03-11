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
package alluxio.underfs.gcs;


import Permission.PERMISSION_FULL_CONTROL;
import Permission.PERMISSION_READ;
import Permission.PERMISSION_READ_ACP;
import Permission.PERMISSION_WRITE;
import org.jets3t.service.acl.CanonicalGrantee;
import org.jets3t.service.acl.GroupGrantee;
import org.jets3t.service.acl.gs.GSAccessControlList;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link GCSUtils} methods.
 */
public final class GCSUtilsTest {
    private static final String NAME = "foo";

    private static final String ID = "123456789012";

    private static final String OTHER_ID = "987654321098";

    private CanonicalGrantee mUserGrantee;

    private GSAccessControlList mAcl;

    @Test
    public void translateUserReadPermission() {
        mAcl.grantPermission(mUserGrantee, PERMISSION_READ);
        Assert.assertEquals(((short) (320)), GCSUtils.translateBucketAcl(mAcl, GCSUtilsTest.ID));
        Assert.assertEquals(((short) (0)), GCSUtils.translateBucketAcl(mAcl, GCSUtilsTest.OTHER_ID));
        mAcl.grantPermission(mUserGrantee, PERMISSION_READ_ACP);
        Assert.assertEquals(((short) (320)), GCSUtils.translateBucketAcl(mAcl, GCSUtilsTest.ID));
        Assert.assertEquals(((short) (0)), GCSUtils.translateBucketAcl(mAcl, GCSUtilsTest.OTHER_ID));
    }

    @Test
    public void translateUserWritePermission() {
        mAcl.grantPermission(mUserGrantee, PERMISSION_WRITE);
        Assert.assertEquals(((short) (128)), GCSUtils.translateBucketAcl(mAcl, GCSUtilsTest.ID));
        mAcl.grantPermission(mUserGrantee, PERMISSION_READ);
        Assert.assertEquals(((short) (448)), GCSUtils.translateBucketAcl(mAcl, GCSUtilsTest.ID));
    }

    @Test
    public void translateUserFullPermission() {
        mAcl.grantPermission(mUserGrantee, PERMISSION_FULL_CONTROL);
        Assert.assertEquals(((short) (448)), GCSUtils.translateBucketAcl(mAcl, GCSUtilsTest.ID));
        Assert.assertEquals(((short) (0)), GCSUtils.translateBucketAcl(mAcl, GCSUtilsTest.OTHER_ID));
    }

    @Test
    public void translateEveryoneReadPermission() {
        GroupGrantee allUsersGrantee = GroupGrantee.ALL_USERS;
        mAcl.grantPermission(allUsersGrantee, PERMISSION_READ);
        Assert.assertEquals(((short) (320)), GCSUtils.translateBucketAcl(mAcl, GCSUtilsTest.ID));
        Assert.assertEquals(((short) (320)), GCSUtils.translateBucketAcl(mAcl, GCSUtilsTest.OTHER_ID));
    }

    @Test
    public void translateEveryoneWritePermission() {
        GroupGrantee allUsersGrantee = GroupGrantee.ALL_USERS;
        mAcl.grantPermission(allUsersGrantee, PERMISSION_WRITE);
        Assert.assertEquals(((short) (128)), GCSUtils.translateBucketAcl(mAcl, GCSUtilsTest.ID));
        Assert.assertEquals(((short) (128)), GCSUtils.translateBucketAcl(mAcl, GCSUtilsTest.OTHER_ID));
    }

    @Test
    public void translateEveryoneFullPermission() {
        GroupGrantee allUsersGrantee = GroupGrantee.ALL_USERS;
        mAcl.grantPermission(allUsersGrantee, PERMISSION_FULL_CONTROL);
        Assert.assertEquals(((short) (448)), GCSUtils.translateBucketAcl(mAcl, GCSUtilsTest.ID));
        Assert.assertEquals(((short) (448)), GCSUtils.translateBucketAcl(mAcl, GCSUtilsTest.OTHER_ID));
    }

    @Test
    public void translateAuthenticatedUserReadPermission() {
        GroupGrantee authenticatedUsersGrantee = GroupGrantee.AUTHENTICATED_USERS;
        mAcl.grantPermission(authenticatedUsersGrantee, PERMISSION_READ);
        Assert.assertEquals(((short) (320)), GCSUtils.translateBucketAcl(mAcl, GCSUtilsTest.ID));
        Assert.assertEquals(((short) (320)), GCSUtils.translateBucketAcl(mAcl, GCSUtilsTest.OTHER_ID));
    }

    @Test
    public void translateAuthenticatedUserWritePermission() {
        GroupGrantee authenticatedUsersGrantee = GroupGrantee.AUTHENTICATED_USERS;
        mAcl.grantPermission(authenticatedUsersGrantee, PERMISSION_WRITE);
        Assert.assertEquals(((short) (128)), GCSUtils.translateBucketAcl(mAcl, GCSUtilsTest.ID));
        Assert.assertEquals(((short) (128)), GCSUtils.translateBucketAcl(mAcl, GCSUtilsTest.OTHER_ID));
    }

    @Test
    public void translateAuthenticatedUserFullPermission() {
        GroupGrantee authenticatedUsersGrantee = GroupGrantee.AUTHENTICATED_USERS;
        mAcl.grantPermission(authenticatedUsersGrantee, PERMISSION_FULL_CONTROL);
        Assert.assertEquals(((short) (448)), GCSUtils.translateBucketAcl(mAcl, GCSUtilsTest.ID));
        Assert.assertEquals(((short) (448)), GCSUtils.translateBucketAcl(mAcl, GCSUtilsTest.OTHER_ID));
    }
}

