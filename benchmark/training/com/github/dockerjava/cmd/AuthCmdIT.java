package com.github.dockerjava.cmd;


import com.github.dockerjava.api.model.AuthResponse;
import com.github.dockerjava.core.RemoteApiVersion;
import com.github.dockerjava.junit.DockerMatchers;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


/**
 *
 *
 * @author Kanstantsin Shautsou
 */
public class AuthCmdIT extends CmdIT {
    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Test
    public void testAuth() throws Exception {
        Assume.assumeThat("Fails on 1.22. Temporary disabled.", dockerRule, DockerMatchers.apiVersionGreater(RemoteApiVersion.VERSION_1_22));
        AuthResponse response = dockerRule.getClient().authCmd().exec();
        Assert.assertThat(response.getStatus(), CoreMatchers.is("Login Succeeded"));
    }
}

