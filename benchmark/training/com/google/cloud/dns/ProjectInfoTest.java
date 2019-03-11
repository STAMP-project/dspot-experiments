/**
 * Copyright 2016 Google LLC
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
package com.google.cloud.dns;


import ProjectInfo.Quota;
import java.math.BigInteger;
import org.junit.Assert;
import org.junit.Test;


public class ProjectInfoTest {
    private static final String ID = "project-id-123";

    private static final BigInteger NUMBER = new BigInteger("123");

    private static final Quota QUOTA = new ProjectInfo.Quota(1, 2, 3, 4, 5, 6);

    private static final ProjectInfo PROJECT_INFO = ProjectInfo.newBuilder().setId(ProjectInfoTest.ID).setNumber(ProjectInfoTest.NUMBER).setQuota(ProjectInfoTest.QUOTA).build();

    @Test
    public void testBuilder() {
        ProjectInfo withId = ProjectInfo.newBuilder().setId(ProjectInfoTest.ID).build();
        Assert.assertEquals(ProjectInfoTest.ID, withId.getId());
        Assert.assertNull(withId.getNumber());
        Assert.assertNull(withId.getQuota());
        ProjectInfo withNumber = ProjectInfo.newBuilder().setNumber(ProjectInfoTest.NUMBER).build();
        Assert.assertEquals(ProjectInfoTest.NUMBER, withNumber.getNumber());
        Assert.assertNull(withNumber.getQuota());
        Assert.assertNull(withNumber.getId());
        ProjectInfo withQuota = ProjectInfo.newBuilder().setQuota(ProjectInfoTest.QUOTA).build();
        Assert.assertEquals(ProjectInfoTest.QUOTA, withQuota.getQuota());
        Assert.assertNull(withQuota.getId());
        Assert.assertNull(withQuota.getNumber());
        Assert.assertEquals(ProjectInfoTest.QUOTA, ProjectInfoTest.PROJECT_INFO.getQuota());
        Assert.assertEquals(ProjectInfoTest.NUMBER, ProjectInfoTest.PROJECT_INFO.getNumber());
        Assert.assertEquals(ProjectInfoTest.ID, ProjectInfoTest.PROJECT_INFO.getId());
    }

    @Test
    public void testQuotaConstructor() {
        Assert.assertEquals(1, ProjectInfoTest.QUOTA.getZones());
        Assert.assertEquals(2, ProjectInfoTest.QUOTA.getResourceRecordsPerRrset());
        Assert.assertEquals(3, ProjectInfoTest.QUOTA.getRrsetAdditionsPerChange());
        Assert.assertEquals(4, ProjectInfoTest.QUOTA.getRrsetDeletionsPerChange());
        Assert.assertEquals(5, ProjectInfoTest.QUOTA.getRrsetsPerZone());
        Assert.assertEquals(6, ProjectInfoTest.QUOTA.getTotalRrdataSizePerChange());
    }

    @Test
    public void testEqualsAndNotEqualsQuota() {
        ProjectInfo.Quota clone = new ProjectInfo.Quota(6, 5, 4, 3, 2, 1);
        Assert.assertNotEquals(ProjectInfoTest.QUOTA, clone);
        clone = Quota.fromPb(ProjectInfoTest.QUOTA.toPb());
        Assert.assertEquals(ProjectInfoTest.QUOTA, clone);
    }

    @Test
    public void testSameHashCodeOnEqualsQuota() {
        ProjectInfo.Quota clone = Quota.fromPb(ProjectInfoTest.QUOTA.toPb());
        Assert.assertEquals(ProjectInfoTest.QUOTA, clone);
        Assert.assertEquals(ProjectInfoTest.QUOTA.hashCode(), clone.hashCode());
    }

    @Test
    public void testEqualsAndNotEquals() {
        ProjectInfo clone = ProjectInfo.newBuilder().build();
        Assert.assertNotEquals(ProjectInfoTest.PROJECT_INFO, clone);
        clone = ProjectInfo.newBuilder().setId(ProjectInfoTest.PROJECT_INFO.getId()).setNumber(ProjectInfoTest.PROJECT_INFO.getNumber()).build();
        Assert.assertNotEquals(ProjectInfoTest.PROJECT_INFO, clone);
        clone = ProjectInfo.newBuilder().setId(ProjectInfoTest.PROJECT_INFO.getId()).setQuota(ProjectInfoTest.PROJECT_INFO.getQuota()).build();
        Assert.assertNotEquals(ProjectInfoTest.PROJECT_INFO, clone);
        clone = ProjectInfo.newBuilder().setNumber(ProjectInfoTest.PROJECT_INFO.getNumber()).setQuota(ProjectInfoTest.PROJECT_INFO.getQuota()).build();
        Assert.assertNotEquals(ProjectInfoTest.PROJECT_INFO, clone);
        clone = ProjectInfo.fromPb(ProjectInfoTest.PROJECT_INFO.toPb());
        Assert.assertEquals(ProjectInfoTest.PROJECT_INFO, clone);
    }

    @Test
    public void testSameHashCodeOnEquals() {
        ProjectInfo clone = ProjectInfo.fromPb(ProjectInfoTest.PROJECT_INFO.toPb());
        Assert.assertEquals(ProjectInfoTest.PROJECT_INFO, clone);
        Assert.assertEquals(ProjectInfoTest.PROJECT_INFO.hashCode(), clone.hashCode());
    }

    @Test
    public void testToAndFromPb() {
        Assert.assertEquals(ProjectInfoTest.PROJECT_INFO, ProjectInfo.fromPb(ProjectInfoTest.PROJECT_INFO.toPb()));
        ProjectInfo partial = ProjectInfo.newBuilder().setId(ProjectInfoTest.ID).build();
        Assert.assertEquals(partial, ProjectInfo.fromPb(partial.toPb()));
        partial = ProjectInfo.newBuilder().setNumber(ProjectInfoTest.NUMBER).build();
        Assert.assertEquals(partial, ProjectInfo.fromPb(partial.toPb()));
        partial = ProjectInfo.newBuilder().setQuota(ProjectInfoTest.QUOTA).build();
        Assert.assertEquals(partial, ProjectInfo.fromPb(partial.toPb()));
        Assert.assertNotEquals(ProjectInfoTest.PROJECT_INFO, partial);
    }

    @Test
    public void testToAndFromPbQuota() {
        Assert.assertEquals(ProjectInfoTest.QUOTA, Quota.fromPb(ProjectInfoTest.QUOTA.toPb()));
        ProjectInfo.Quota wrong = new ProjectInfo.Quota(5, 6, 3, 6, 2, 1);
        Assert.assertNotEquals(ProjectInfoTest.QUOTA, Quota.fromPb(wrong.toPb()));
    }
}

