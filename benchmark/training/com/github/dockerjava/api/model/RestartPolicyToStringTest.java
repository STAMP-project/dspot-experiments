package com.github.dockerjava.api.model;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class RestartPolicyToStringTest {
    @Parameterized.Parameter
    public String policy;

    @Test
    public void serializationWithoutCount() throws Exception {
        Assert.assertEquals(RestartPolicy.parse(policy).toString(), policy);
    }
}

