/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership.  The ASF
 * licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.hadoop.ozone.security.acl;


import OzoneObj.StoreType;
import OzoneObjInfo.Builder;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test class for {@link OzoneObjInfo}.
 */
public class TestOzoneObjInfo {
    private OzoneObjInfo objInfo;

    private Builder builder;

    private String volume = "vol1";

    private String bucket = "bucket1";

    private String key = "key1";

    private static final StoreType STORE = StoreType.OZONE;

    @Test
    public void testGetVolumeName() {
        builder = getBuilder(volume, bucket, key);
        objInfo = builder.build();
        Assert.assertEquals(objInfo.getVolumeName(), volume);
        objInfo = getBuilder(null, null, null).build();
        Assert.assertEquals(objInfo.getVolumeName(), null);
        objInfo = getBuilder(volume, null, null).build();
        Assert.assertEquals(objInfo.getVolumeName(), volume);
    }

    @Test
    public void testGetBucketName() {
        objInfo = getBuilder(volume, bucket, key).build();
        Assert.assertEquals(objInfo.getBucketName(), bucket);
        objInfo = getBuilder(volume, null, null).build();
        Assert.assertEquals(objInfo.getBucketName(), null);
        objInfo = getBuilder(null, bucket, null).build();
        Assert.assertEquals(objInfo.getBucketName(), bucket);
    }

    @Test
    public void testGetKeyName() {
        objInfo = getBuilder(volume, bucket, key).build();
        Assert.assertEquals(objInfo.getKeyName(), key);
        objInfo = getBuilder(volume, null, null).build();
        Assert.assertEquals(objInfo.getKeyName(), null);
        objInfo = getBuilder(null, bucket, null).build();
        Assert.assertEquals(objInfo.getKeyName(), null);
        objInfo = getBuilder(null, null, key).build();
        Assert.assertEquals(objInfo.getKeyName(), key);
    }
}

