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
package alluxio.underfs.s3a;


import Permission.FullControl;
import Permission.Read;
import Permission.ReadAcp;
import Permission.Write;
import com.amazonaws.services.s3.model.AccessControlList;
import com.amazonaws.services.s3.model.CanonicalGrantee;
import com.amazonaws.services.s3.model.GroupGrantee;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link S3AUtils} methods.
 */
public final class S3AUtilsTest {
    private static final String NAME = "foo";

    private static final String ID = "123456789012";

    private static final String OTHER_ID = "987654321098";

    private CanonicalGrantee mUserGrantee;

    private AccessControlList mAcl;

    @Test
    public void translateUserReadPermission() {
        mAcl.grantPermission(mUserGrantee, Read);
        Assert.assertEquals(((short) (320)), S3AUtils.translateBucketAcl(mAcl, S3AUtilsTest.ID));
        Assert.assertEquals(((short) (0)), S3AUtils.translateBucketAcl(mAcl, S3AUtilsTest.OTHER_ID));
        mAcl.grantPermission(mUserGrantee, ReadAcp);
        Assert.assertEquals(((short) (320)), S3AUtils.translateBucketAcl(mAcl, S3AUtilsTest.ID));
        Assert.assertEquals(((short) (0)), S3AUtils.translateBucketAcl(mAcl, S3AUtilsTest.OTHER_ID));
    }

    @Test
    public void translateUserWritePermission() {
        mAcl.grantPermission(mUserGrantee, Write);
        Assert.assertEquals(((short) (128)), S3AUtils.translateBucketAcl(mAcl, S3AUtilsTest.ID));
        mAcl.grantPermission(mUserGrantee, Read);
        Assert.assertEquals(((short) (448)), S3AUtils.translateBucketAcl(mAcl, S3AUtilsTest.ID));
    }

    @Test
    public void translateUserFullPermission() {
        mAcl.grantPermission(mUserGrantee, FullControl);
        Assert.assertEquals(((short) (448)), S3AUtils.translateBucketAcl(mAcl, S3AUtilsTest.ID));
        Assert.assertEquals(((short) (0)), S3AUtils.translateBucketAcl(mAcl, S3AUtilsTest.OTHER_ID));
    }

    @Test
    public void translateEveryoneReadPermission() {
        GroupGrantee allUsersGrantee = GroupGrantee.AllUsers;
        mAcl.grantPermission(allUsersGrantee, Read);
        Assert.assertEquals(((short) (320)), S3AUtils.translateBucketAcl(mAcl, S3AUtilsTest.ID));
        Assert.assertEquals(((short) (320)), S3AUtils.translateBucketAcl(mAcl, S3AUtilsTest.OTHER_ID));
    }

    @Test
    public void translateEveryoneWritePermission() {
        GroupGrantee allUsersGrantee = GroupGrantee.AllUsers;
        mAcl.grantPermission(allUsersGrantee, Write);
        Assert.assertEquals(((short) (128)), S3AUtils.translateBucketAcl(mAcl, S3AUtilsTest.ID));
        Assert.assertEquals(((short) (128)), S3AUtils.translateBucketAcl(mAcl, S3AUtilsTest.OTHER_ID));
    }

    @Test
    public void translateEveryoneFullPermission() {
        GroupGrantee allUsersGrantee = GroupGrantee.AllUsers;
        mAcl.grantPermission(allUsersGrantee, FullControl);
        Assert.assertEquals(((short) (448)), S3AUtils.translateBucketAcl(mAcl, S3AUtilsTest.ID));
        Assert.assertEquals(((short) (448)), S3AUtils.translateBucketAcl(mAcl, S3AUtilsTest.OTHER_ID));
    }

    @Test
    public void translateAuthenticatedUserReadPermission() {
        GroupGrantee authenticatedUsersGrantee = GroupGrantee.AuthenticatedUsers;
        mAcl.grantPermission(authenticatedUsersGrantee, Read);
        Assert.assertEquals(((short) (320)), S3AUtils.translateBucketAcl(mAcl, S3AUtilsTest.ID));
        Assert.assertEquals(((short) (320)), S3AUtils.translateBucketAcl(mAcl, S3AUtilsTest.OTHER_ID));
    }

    @Test
    public void translateAuthenticatedUserWritePermission() {
        GroupGrantee authenticatedUsersGrantee = GroupGrantee.AuthenticatedUsers;
        mAcl.grantPermission(authenticatedUsersGrantee, Write);
        Assert.assertEquals(((short) (128)), S3AUtils.translateBucketAcl(mAcl, S3AUtilsTest.ID));
        Assert.assertEquals(((short) (128)), S3AUtils.translateBucketAcl(mAcl, S3AUtilsTest.OTHER_ID));
    }

    @Test
    public void translateAuthenticatedUserFullPermission() {
        GroupGrantee authenticatedUsersGrantee = GroupGrantee.AuthenticatedUsers;
        mAcl.grantPermission(authenticatedUsersGrantee, FullControl);
        Assert.assertEquals(((short) (448)), S3AUtils.translateBucketAcl(mAcl, S3AUtilsTest.ID));
        Assert.assertEquals(((short) (448)), S3AUtils.translateBucketAcl(mAcl, S3AUtilsTest.OTHER_ID));
    }
}

