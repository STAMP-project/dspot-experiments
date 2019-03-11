package com.github.dockerjava.api.command;


import InspectExecResponse.ProcessConfig;
import RemoteApiVersion.VERSION_1_22;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dockerjava.test.serdes.JSONSamples;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsNull;
import org.junit.Test;


/**
 *
 *
 * @author Kanstantsin Shautsou
 */
public class InspectExecResponseTest {
    @Test
    public void test_1_22_SerDer1() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JavaType type = mapper.getTypeFactory().constructType(InspectExecResponse.class);
        final InspectExecResponse execResponse = JSONSamples.testRoundTrip(VERSION_1_22, "/exec/ID/1.json", type);
        MatcherAssert.assertThat(execResponse, IsNull.notNullValue());
        MatcherAssert.assertThat(execResponse.getId(), Matchers.is("1ca2ca598fab202f86dd9281196c405456069013958a475396b707e85c56473b"));
        MatcherAssert.assertThat(execResponse.isRunning(), Matchers.is(false));
        MatcherAssert.assertThat(execResponse.getExitCode(), Matchers.is(IsNull.nullValue()));
        final InspectExecResponse.ProcessConfig processConfig = execResponse.getProcessConfig();
        MatcherAssert.assertThat(processConfig, IsNull.notNullValue());
        MatcherAssert.assertThat(processConfig.isTty(), Matchers.is(false));
        MatcherAssert.assertThat(processConfig.getEntryPoint(), Matchers.is("/bin/bash"));
        MatcherAssert.assertThat(processConfig.getArguments(), Matchers.hasSize(0));
        MatcherAssert.assertThat(processConfig.isPrivileged(), Matchers.is(false));
        MatcherAssert.assertThat(processConfig.getUser(), Matchers.isEmptyString());
        MatcherAssert.assertThat(execResponse.isOpenStdin(), Matchers.is(false));
        MatcherAssert.assertThat(execResponse.isOpenStderr(), Matchers.is(true));
        MatcherAssert.assertThat(execResponse.isOpenStdout(), Matchers.is(true));
        MatcherAssert.assertThat(execResponse.getCanRemove(), Matchers.is(false));
        MatcherAssert.assertThat(execResponse.getContainerID(), Matchers.is("ffa39805f089af3099e36452a985481f96170a9dff40be69d34d1722c7660d38"));
        MatcherAssert.assertThat(execResponse.getDetachKeys(), Matchers.isEmptyString());
    }
}

