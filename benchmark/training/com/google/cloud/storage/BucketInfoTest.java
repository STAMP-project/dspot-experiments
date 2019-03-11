/**
 * Copyright 2015 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.cloud.storage;


import BucketInfo.IamConfiguration;
import LifecycleRule.DeleteLifecycleAction.TYPE;
import Role.READER;
import Role.WRITER;
import StorageClass.COLDLINE;
import Type.AGE;
import Type.CREATE_BEFORE;
import Type.IS_LIVE;
import Type.NUM_NEWER_VERSIONS;
import com.google.api.services.storage.model.Bucket;
import com.google.api.services.storage.model.Bucket.Lifecycle.Rule;
import com.google.cloud.storage.Acl.User;
import com.google.cloud.storage.BucketInfo.AgeDeleteRule;
import com.google.cloud.storage.BucketInfo.CreatedBeforeDeleteRule;
import com.google.cloud.storage.BucketInfo.DeleteRule;
import com.google.cloud.storage.BucketInfo.IsLiveDeleteRule;
import com.google.cloud.storage.BucketInfo.LifecycleRule.LifecycleAction;
import com.google.cloud.storage.BucketInfo.LifecycleRule.LifecycleCondition;
import com.google.cloud.storage.BucketInfo.NumNewerVersionsDeleteRule;
import com.google.cloud.storage.BucketInfo.RawDeleteRule;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;

import static ProjectRole.VIEWERS;
import static StorageClass.STANDARD;


public class BucketInfoTest {
    private static final List<Acl> ACL = ImmutableList.of(Acl.of(User.ofAllAuthenticatedUsers(), READER), Acl.of(new com.google.cloud.storage.Acl.Project(VIEWERS, "p1"), WRITER));

    private static final String ETAG = "0xFF00";

    private static final String GENERATED_ID = "B/N:1";

    private static final Long META_GENERATION = 10L;

    private static final User OWNER = new User("user@gmail.com");

    private static final String SELF_LINK = "http://storage/b/n";

    private static final Long CREATE_TIME = System.currentTimeMillis();

    private static final List<Cors> CORS = Collections.singletonList(Cors.newBuilder().build());

    private static final List<Acl> DEFAULT_ACL = Collections.singletonList(Acl.of(User.ofAllAuthenticatedUsers(), WRITER));

    private static final List<? extends DeleteRule> DELETE_RULES = Collections.singletonList(new AgeDeleteRule(5));

    private static final List<? extends BucketInfo.LifecycleRule> LIFECYCLE_RULES = Collections.singletonList(new BucketInfo.LifecycleRule(LifecycleAction.newDeleteAction(), LifecycleCondition.newBuilder().setAge(5).build()));

    private static final String INDEX_PAGE = "index.html";

    private static final IamConfiguration IAM_CONFIGURATION = IamConfiguration.newBuilder().setIsBucketPolicyOnlyEnabled(true).setBucketPolicyOnlyLockedTime(System.currentTimeMillis()).build();

    private static final String NOT_FOUND_PAGE = "error.html";

    private static final String LOCATION = "ASIA";

    private static final StorageClass STORAGE_CLASS = STANDARD;

    private static final String DEFAULT_KMS_KEY_NAME = "projects/p/locations/kr-loc/keyRings/kr/cryptoKeys/key";

    private static final Boolean VERSIONING_ENABLED = true;

    private static final Map<String, String> BUCKET_LABELS = ImmutableMap.of("label1", "value1");

    private static final Boolean REQUESTER_PAYS = true;

    private static final Boolean DEFAULT_EVENT_BASED_HOLD = true;

    private static final Long RETENTION_EFFECTIVE_TIME = 10L;

    private static final Long RETENTION_PERIOD = 10L;

    private static final Boolean RETENTION_POLICY_IS_LOCKED = false;

    private static final BucketInfo BUCKET_INFO = BucketInfo.newBuilder("b").setAcl(BucketInfoTest.ACL).setEtag(BucketInfoTest.ETAG).setGeneratedId(BucketInfoTest.GENERATED_ID).setMetageneration(BucketInfoTest.META_GENERATION).setOwner(BucketInfoTest.OWNER).setSelfLink(BucketInfoTest.SELF_LINK).setCors(BucketInfoTest.CORS).setCreateTime(BucketInfoTest.CREATE_TIME).setDefaultAcl(BucketInfoTest.DEFAULT_ACL).setDeleteRules(BucketInfoTest.DELETE_RULES).setLifecycleRules(BucketInfoTest.LIFECYCLE_RULES).setIndexPage(BucketInfoTest.INDEX_PAGE).setIamConfiguration(BucketInfoTest.IAM_CONFIGURATION).setNotFoundPage(BucketInfoTest.NOT_FOUND_PAGE).setLocation(BucketInfoTest.LOCATION).setStorageClass(BucketInfoTest.STORAGE_CLASS).setVersioningEnabled(BucketInfoTest.VERSIONING_ENABLED).setLabels(BucketInfoTest.BUCKET_LABELS).setRequesterPays(BucketInfoTest.REQUESTER_PAYS).setDefaultKmsKeyName(BucketInfoTest.DEFAULT_KMS_KEY_NAME).setDefaultEventBasedHold(BucketInfoTest.DEFAULT_EVENT_BASED_HOLD).setRetentionEffectiveTime(BucketInfoTest.RETENTION_EFFECTIVE_TIME).setRetentionPeriod(BucketInfoTest.RETENTION_PERIOD).setRetentionPolicyIsLocked(BucketInfoTest.RETENTION_POLICY_IS_LOCKED).build();

    @Test
    public void testToBuilder() {
        compareBuckets(BucketInfoTest.BUCKET_INFO, BucketInfoTest.BUCKET_INFO.toBuilder().build());
        BucketInfo bucketInfo = BucketInfoTest.BUCKET_INFO.toBuilder().setName("B").setGeneratedId("id").build();
        Assert.assertEquals("B", bucketInfo.getName());
        Assert.assertEquals("id", bucketInfo.getGeneratedId());
        bucketInfo = bucketInfo.toBuilder().setName("b").setGeneratedId(BucketInfoTest.GENERATED_ID).build();
        compareBuckets(BucketInfoTest.BUCKET_INFO, bucketInfo);
    }

    @Test
    public void testToBuilderIncomplete() {
        BucketInfo incompleteBucketInfo = BucketInfo.newBuilder("b").build();
        compareBuckets(incompleteBucketInfo, incompleteBucketInfo.toBuilder().build());
    }

    @Test
    public void testOf() {
        BucketInfo bucketInfo = BucketInfo.of("bucket");
        Assert.assertEquals("bucket", bucketInfo.getName());
    }

    @Test
    public void testBuilder() {
        Assert.assertEquals("b", BucketInfoTest.BUCKET_INFO.getName());
        Assert.assertEquals(BucketInfoTest.ACL, BucketInfoTest.BUCKET_INFO.getAcl());
        Assert.assertEquals(BucketInfoTest.ETAG, BucketInfoTest.BUCKET_INFO.getEtag());
        Assert.assertEquals(BucketInfoTest.GENERATED_ID, BucketInfoTest.BUCKET_INFO.getGeneratedId());
        Assert.assertEquals(BucketInfoTest.META_GENERATION, BucketInfoTest.BUCKET_INFO.getMetageneration());
        Assert.assertEquals(BucketInfoTest.OWNER, BucketInfoTest.BUCKET_INFO.getOwner());
        Assert.assertEquals(BucketInfoTest.SELF_LINK, BucketInfoTest.BUCKET_INFO.getSelfLink());
        Assert.assertEquals(BucketInfoTest.CREATE_TIME, BucketInfoTest.BUCKET_INFO.getCreateTime());
        Assert.assertEquals(BucketInfoTest.CORS, BucketInfoTest.BUCKET_INFO.getCors());
        Assert.assertEquals(BucketInfoTest.DEFAULT_ACL, BucketInfoTest.BUCKET_INFO.getDefaultAcl());
        Assert.assertEquals(BucketInfoTest.DELETE_RULES, BucketInfoTest.BUCKET_INFO.getDeleteRules());
        Assert.assertEquals(BucketInfoTest.INDEX_PAGE, BucketInfoTest.BUCKET_INFO.getIndexPage());
        Assert.assertEquals(BucketInfoTest.IAM_CONFIGURATION, BucketInfoTest.BUCKET_INFO.getIamConfiguration());
        Assert.assertEquals(BucketInfoTest.NOT_FOUND_PAGE, BucketInfoTest.BUCKET_INFO.getNotFoundPage());
        Assert.assertEquals(BucketInfoTest.LOCATION, BucketInfoTest.BUCKET_INFO.getLocation());
        Assert.assertEquals(BucketInfoTest.STORAGE_CLASS, BucketInfoTest.BUCKET_INFO.getStorageClass());
        Assert.assertEquals(BucketInfoTest.DEFAULT_KMS_KEY_NAME, BucketInfoTest.BUCKET_INFO.getDefaultKmsKeyName());
        Assert.assertEquals(BucketInfoTest.VERSIONING_ENABLED, BucketInfoTest.BUCKET_INFO.versioningEnabled());
        Assert.assertEquals(BucketInfoTest.BUCKET_LABELS, BucketInfoTest.BUCKET_INFO.getLabels());
        Assert.assertEquals(BucketInfoTest.REQUESTER_PAYS, BucketInfoTest.BUCKET_INFO.requesterPays());
        Assert.assertEquals(BucketInfoTest.DEFAULT_EVENT_BASED_HOLD, BucketInfoTest.BUCKET_INFO.getDefaultEventBasedHold());
        Assert.assertEquals(BucketInfoTest.RETENTION_EFFECTIVE_TIME, BucketInfoTest.BUCKET_INFO.getRetentionEffectiveTime());
        Assert.assertEquals(BucketInfoTest.RETENTION_PERIOD, BucketInfoTest.BUCKET_INFO.getRetentionPeriod());
        Assert.assertEquals(BucketInfoTest.RETENTION_POLICY_IS_LOCKED, BucketInfoTest.BUCKET_INFO.retentionPolicyIsLocked());
    }

    @Test
    public void testToPbAndFromPb() {
        compareBuckets(BucketInfoTest.BUCKET_INFO, BucketInfo.fromPb(BucketInfoTest.BUCKET_INFO.toPb()));
        BucketInfo bucketInfo = BucketInfo.of("b");
        compareBuckets(bucketInfo, BucketInfo.fromPb(bucketInfo.toPb()));
    }

    @Test
    public void testDeleteRules() {
        AgeDeleteRule ageRule = new AgeDeleteRule(10);
        Assert.assertEquals(10, ageRule.getDaysToLive());
        Assert.assertEquals(10, ageRule.getDaysToLive());
        Assert.assertEquals(AGE, ageRule.getType());
        Assert.assertEquals(AGE, ageRule.getType());
        CreatedBeforeDeleteRule createBeforeRule = new CreatedBeforeDeleteRule(1);
        Assert.assertEquals(1, createBeforeRule.getTimeMillis());
        Assert.assertEquals(1, createBeforeRule.getTimeMillis());
        Assert.assertEquals(CREATE_BEFORE, createBeforeRule.getType());
        NumNewerVersionsDeleteRule versionsRule = new NumNewerVersionsDeleteRule(2);
        Assert.assertEquals(2, versionsRule.getNumNewerVersions());
        Assert.assertEquals(2, versionsRule.getNumNewerVersions());
        Assert.assertEquals(NUM_NEWER_VERSIONS, versionsRule.getType());
        IsLiveDeleteRule isLiveRule = new IsLiveDeleteRule(true);
        Assert.assertTrue(isLiveRule.isLive());
        Assert.assertEquals(IS_LIVE, isLiveRule.getType());
        Assert.assertEquals(IS_LIVE, isLiveRule.getType());
        Rule rule = new Rule().set("a", "b");
        RawDeleteRule rawRule = new RawDeleteRule(rule);
        Assert.assertEquals(IS_LIVE, isLiveRule.getType());
        Assert.assertEquals(IS_LIVE, isLiveRule.getType());
        ImmutableList<DeleteRule> rules = ImmutableList.of(ageRule, createBeforeRule, versionsRule, isLiveRule, rawRule);
        for (DeleteRule delRule : rules) {
            Assert.assertEquals(delRule, DeleteRule.fromPb(delRule.toPb()));
        }
    }

    @Test
    public void testLifecycleRules() {
        Rule deleteLifecycleRule = toPb();
        Assert.assertEquals(TYPE, deleteLifecycleRule.getAction().getType());
        Assert.assertEquals(10, deleteLifecycleRule.getCondition().getAge().intValue());
        Rule setStorageClassLifecycleRule = toPb();
        Assert.assertEquals(COLDLINE.toString(), setStorageClassLifecycleRule.getAction().getStorageClass());
        Assert.assertTrue(setStorageClassLifecycleRule.getCondition().getIsLive());
        Assert.assertEquals(10, setStorageClassLifecycleRule.getCondition().getNumNewerVersions().intValue());
    }

    @Test
    public void testIamConfiguration() {
        Bucket.IamConfiguration iamConfiguration = IamConfiguration.newBuilder().setIsBucketPolicyOnlyEnabled(true).setBucketPolicyOnlyLockedTime(System.currentTimeMillis()).build().toPb();
        Assert.assertEquals(Boolean.TRUE, iamConfiguration.getBucketPolicyOnly().getEnabled());
        Assert.assertNotNull(iamConfiguration.getBucketPolicyOnly().getLockedTime());
    }
}

