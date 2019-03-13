package io.elasticjob.lite.api.strategy;


import io.elasticjob.lite.util.env.IpUtils;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;


public final class JobInstanceTest {
    @Test
    public void assertGetJobInstanceId() {
        Assert.assertThat(new JobInstance("127.0.0.1@-@0").getJobInstanceId(), Is.is("127.0.0.1@-@0"));
    }

    @Test
    public void assertGetIp() {
        Assert.assertThat(new JobInstance().getIp(), Is.is(IpUtils.getIp()));
    }
}

