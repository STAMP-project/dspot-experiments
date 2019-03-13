/**
 * Copyright 1999-2015 dangdang.com.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * </p>
 */
package io.elasticjob.lite.lifecycle.internal.statistics;


import JobBriefInfo.JobStatus.CRASHED;
import JobBriefInfo.JobStatus.DISABLED;
import JobBriefInfo.JobStatus.OK;
import JobBriefInfo.JobStatus.SHARDING_FLAG;
import com.google.common.collect.Lists;
import io.elasticjob.lite.lifecycle.api.JobStatisticsAPI;
import io.elasticjob.lite.lifecycle.domain.JobBriefInfo;
import io.elasticjob.lite.lifecycle.fixture.LifecycleJsonConstants;
import io.elasticjob.lite.reg.base.CoordinatorRegistryCenter;
import java.util.Arrays;
import java.util.Collections;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


public final class JobStatisticsAPIImplTest {
    private JobStatisticsAPI jobStatisticsAPI;

    @Mock
    private CoordinatorRegistryCenter regCenter;

    @Test
    public void assertGetJobsTotalCount() {
        Mockito.when(regCenter.getChildrenKeys("/")).thenReturn(Arrays.asList("test_job_1", "test_job_2"));
        Assert.assertThat(jobStatisticsAPI.getJobsTotalCount(), Is.is(2));
    }

    @Test
    public void assertGetOKJobBriefInfo() {
        Mockito.when(regCenter.getChildrenKeys("/")).thenReturn(Lists.newArrayList("test_job"));
        Mockito.when(regCenter.get("/test_job/config")).thenReturn(LifecycleJsonConstants.getSimpleJobJson("test_job", "desc"));
        Mockito.when(regCenter.getChildrenKeys("/test_job/servers")).thenReturn(Arrays.asList("ip1", "ip2"));
        Mockito.when(regCenter.getChildrenKeys("/test_job/instances")).thenReturn(Arrays.asList("ip1@-@defaultInstance", "ip2@-@defaultInstance"));
        Mockito.when(regCenter.getChildrenKeys("/test_job/sharding")).thenReturn(Arrays.asList("0", "1", "2"));
        Mockito.when(regCenter.get("/test_job/sharding/0/instance")).thenReturn("ip1@-@defaultInstance");
        Mockito.when(regCenter.get("/test_job/sharding/1/instance")).thenReturn("ip1@-@defaultInstance");
        Mockito.when(regCenter.get("/test_job/sharding/2/instance")).thenReturn("ip2@-@defaultInstance");
        Mockito.when(regCenter.getChildrenKeys("/test_job/instances")).thenReturn(Arrays.asList("ip1@-@defaultInstance", "ip2@-@defaultInstance"));
        JobBriefInfo jobBrief = jobStatisticsAPI.getJobBriefInfo("test_job");
        Assert.assertThat(jobBrief.getJobName(), Is.is("test_job"));
        Assert.assertThat(jobBrief.getDescription(), Is.is("desc"));
        Assert.assertThat(jobBrief.getCron(), Is.is("0/1 * * * * ?"));
        Assert.assertThat(jobBrief.getInstanceCount(), Is.is(2));
        Assert.assertThat(jobBrief.getShardingTotalCount(), Is.is(3));
        Assert.assertThat(jobBrief.getStatus(), Is.is(OK));
    }

    @Test
    public void assertGetOKJobBriefInfoWithPartialDisabledServer() {
        Mockito.when(regCenter.getChildrenKeys("/")).thenReturn(Lists.newArrayList("test_job"));
        Mockito.when(regCenter.get("/test_job/config")).thenReturn(LifecycleJsonConstants.getSimpleJobJson("test_job", "desc"));
        Mockito.when(regCenter.getChildrenKeys("/test_job/servers")).thenReturn(Arrays.asList("ip1", "ip2"));
        Mockito.when(regCenter.get("/test_job/servers/ip1")).thenReturn("DISABLED");
        Mockito.when(regCenter.getChildrenKeys("/test_job/instances")).thenReturn(Arrays.asList("ip1@-@defaultInstance", "ip2@-@defaultInstance"));
        Mockito.when(regCenter.getChildrenKeys("/test_job/sharding")).thenReturn(Arrays.asList("0", "1"));
        Mockito.when(regCenter.get("/test_job/sharding/0/instance")).thenReturn("ip1@-@defaultInstance");
        Mockito.when(regCenter.get("/test_job/sharding/1/instance")).thenReturn("ip2@-@defaultInstance");
        JobBriefInfo jobBrief = jobStatisticsAPI.getJobBriefInfo("test_job");
        Assert.assertThat(jobBrief.getStatus(), Is.is(OK));
    }

    @Test
    public void assertGetDisabledJobBriefInfo() {
        Mockito.when(regCenter.getChildrenKeys("/")).thenReturn(Lists.newArrayList("test_job"));
        Mockito.when(regCenter.get("/test_job/config")).thenReturn(LifecycleJsonConstants.getSimpleJobJson("test_job", "desc"));
        Mockito.when(regCenter.getChildrenKeys("/test_job/servers")).thenReturn(Arrays.asList("ip1", "ip2"));
        Mockito.when(regCenter.get("/test_job/servers/ip1")).thenReturn("DISABLED");
        Mockito.when(regCenter.get("/test_job/servers/ip2")).thenReturn("DISABLED");
        Mockito.when(regCenter.getChildrenKeys("/test_job/instances")).thenReturn(Arrays.asList("ip1@-@defaultInstance", "ip2@-@defaultInstance"));
        Mockito.when(regCenter.getChildrenKeys("/test_job/sharding")).thenReturn(Arrays.asList("0", "1"));
        Mockito.when(regCenter.get("/test_job/sharding/0/instance")).thenReturn("ip1@-@defaultInstance");
        Mockito.when(regCenter.get("/test_job/sharding/1/instance")).thenReturn("ip2@-@defaultInstance");
        JobBriefInfo jobBrief = jobStatisticsAPI.getJobBriefInfo("test_job");
        Assert.assertThat(jobBrief.getStatus(), Is.is(DISABLED));
    }

    @Test
    public void assertGetShardingErrorJobBriefInfo() {
        Mockito.when(regCenter.getChildrenKeys("/")).thenReturn(Lists.newArrayList("test_job"));
        Mockito.when(regCenter.get("/test_job/config")).thenReturn(LifecycleJsonConstants.getSimpleJobJson("test_job", "desc"));
        Mockito.when(regCenter.getChildrenKeys("/test_job/servers")).thenReturn(Arrays.asList("ip1", "ip2"));
        Mockito.when(regCenter.getChildrenKeys("/test_job/instances")).thenReturn(Arrays.asList("ip1@-@defaultInstance", "ip2@-@defaultInstance"));
        Mockito.when(regCenter.getChildrenKeys("/test_job/sharding")).thenReturn(Arrays.asList("0", "1", "2"));
        Mockito.when(regCenter.get("/test_job/sharding/0/instance")).thenReturn("ip1@-@defaultInstance");
        Mockito.when(regCenter.get("/test_job/sharding/1/instance")).thenReturn("ip2@-@defaultInstance");
        Mockito.when(regCenter.get("/test_job/sharding/2/instance")).thenReturn("ip3@-@defaultInstance");
        JobBriefInfo jobBrief = jobStatisticsAPI.getJobBriefInfo("test_job");
        Assert.assertThat(jobBrief.getStatus(), Is.is(SHARDING_FLAG));
    }

    @Test
    public void assertGetCrashedJobBriefInfo() {
        Mockito.when(regCenter.getChildrenKeys("/")).thenReturn(Lists.newArrayList("test_job"));
        Mockito.when(regCenter.get("/test_job/config")).thenReturn(LifecycleJsonConstants.getSimpleJobJson("test_job", "desc"));
        Mockito.when(regCenter.getChildrenKeys("/test_job/servers")).thenReturn(Arrays.asList("ip1", "ip2"));
        JobBriefInfo jobBrief = jobStatisticsAPI.getJobBriefInfo("test_job");
        Assert.assertThat(jobBrief.getStatus(), Is.is(CRASHED));
    }

    @Test
    public void assertGetAllJobsBriefInfoWithoutNamespace() {
        Mockito.when(regCenter.getChildrenKeys("/")).thenReturn(Arrays.asList("test_job_1", "test_job_2"));
        Assert.assertThat(jobStatisticsAPI.getAllJobsBriefInfo().size(), Is.is(0));
    }

    @Test
    public void assertGetAllJobsBriefInfo() {
        Mockito.when(regCenter.getChildrenKeys("/")).thenReturn(Arrays.asList("test_job_1", "test_job_2"));
        Mockito.when(regCenter.get("/test_job_1/config")).thenReturn(LifecycleJsonConstants.getSimpleJobJson("test_job_1", "desc1"));
        Mockito.when(regCenter.get("/test_job_2/config")).thenReturn(LifecycleJsonConstants.getSimpleJobJson("test_job_2", "desc2"));
        Mockito.when(regCenter.getChildrenKeys("/test_job_1/servers")).thenReturn(Arrays.asList("ip1", "ip2"));
        Mockito.when(regCenter.getChildrenKeys("/test_job_2/servers")).thenReturn(Arrays.asList("ip3", "ip4"));
        Mockito.when(regCenter.getChildrenKeys("/test_job_1/sharding")).thenReturn(Arrays.asList("0", "1"));
        Mockito.when(regCenter.get("/test_job_1/sharding/0/instance")).thenReturn("ip1@-@defaultInstance");
        Mockito.when(regCenter.get("/test_job_1/sharding/1/instance")).thenReturn("ip2@-@defaultInstance");
        Mockito.when(regCenter.getChildrenKeys("/test_job_2/sharding")).thenReturn(Arrays.asList("0", "1"));
        Mockito.when(regCenter.get("/test_job_2/sharding/0/instance")).thenReturn("ip3@-@defaultInstance");
        Mockito.when(regCenter.get("/test_job_2/sharding/1/instance")).thenReturn("ip4@-@defaultInstance");
        Mockito.when(regCenter.getChildrenKeys("/test_job_1/instances")).thenReturn(Arrays.asList("ip1@-@defaultInstance", "ip2@-@defaultInstance"));
        Mockito.when(regCenter.getChildrenKeys("/test_job_2/instances")).thenReturn(Arrays.asList("ip3@-@defaultInstance", "ip4@-@defaultInstance"));
        int i = 0;
        for (JobBriefInfo each : jobStatisticsAPI.getAllJobsBriefInfo()) {
            i++;
            Assert.assertThat(each.getJobName(), Is.is(("test_job_" + i)));
            Assert.assertThat(each.getDescription(), Is.is(("desc" + i)));
            Assert.assertThat(each.getCron(), Is.is("0/1 * * * * ?"));
            Assert.assertThat(each.getInstanceCount(), Is.is(2));
            Assert.assertThat(each.getShardingTotalCount(), Is.is(3));
            Assert.assertThat(each.getStatus(), Is.is(OK));
        }
    }

    @Test
    public void assertGetJobsBriefInfoByIp() {
        Mockito.when(regCenter.getChildrenKeys("/")).thenReturn(Arrays.asList("test_job_1", "test_job_2", "test_job_3"));
        Mockito.when(regCenter.getChildrenKeys("/test_job_1/servers")).thenReturn(Collections.singletonList("ip1"));
        Mockito.when(regCenter.getChildrenKeys("/test_job_2/servers")).thenReturn(Collections.singletonList("ip1"));
        Mockito.when(regCenter.getChildrenKeys("/test_job_3/servers")).thenReturn(Collections.singletonList("ip1"));
        Mockito.when(regCenter.isExisted("/test_job_1/servers/ip1")).thenReturn(true);
        Mockito.when(regCenter.isExisted("/test_job_2/servers/ip1")).thenReturn(true);
        Mockito.when(regCenter.get("/test_job_2/servers/ip1")).thenReturn("DISABLED");
        Mockito.when(regCenter.getChildrenKeys("/test_job_1/instances")).thenReturn(Collections.singletonList("ip1@-@defaultInstance"));
        int i = 0;
        for (JobBriefInfo each : jobStatisticsAPI.getJobsBriefInfo("ip1")) {
            Assert.assertThat(each.getJobName(), Is.is(("test_job_" + (++i))));
            if (i == 1) {
                Assert.assertThat(each.getInstanceCount(), Is.is(1));
                Assert.assertThat(each.getStatus(), Is.is(OK));
            } else
                if (i == 2) {
                    Assert.assertThat(each.getInstanceCount(), Is.is(0));
                    Assert.assertThat(each.getStatus(), Is.is(DISABLED));
                }

        }
    }
}

