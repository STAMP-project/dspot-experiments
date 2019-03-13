/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * 	 http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flink.mesos.runtime.clusterframework;


import MesosTaskManagerParameters.MESOS_RM_CONTAINER_DOCKER_FORCE_PULL_IMAGE;
import MesosTaskManagerParameters.MESOS_RM_CONTAINER_DOCKER_PARAMETERS;
import MesosTaskManagerParameters.MESOS_RM_CONTAINER_VOLUMES;
import MesosTaskManagerParameters.MESOS_TM_URIS;
import Protos.Volume;
import Protos.Volume.Mode.RO;
import Protos.Volume.Mode.RW;
import com.netflix.fenzo.ConstraintEvaluator;
import com.netflix.fenzo.plugins.HostAttrValueConstraint;
import java.util.List;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.IllegalConfigurationException;
import org.apache.flink.util.TestLogger;
import org.apache.mesos.Protos;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import scala.Option;


/**
 * Tests for the {@link MesosTaskManagerParameters}.
 */
public class MesosTaskManagerParametersTest extends TestLogger {
    @Test
    public void testBuildVolumes() throws Exception {
        List<Protos.Volume> vols;
        Assert.assertEquals(MesosTaskManagerParameters.buildVolumes(Option.<String>apply(null)).size(), 0);
        String spec1 = "/host/path:/container/path:RO,/container/path:ro,/host/path:/container/path,/container/path";
        vols = MesosTaskManagerParameters.buildVolumes(Option.<String>apply(spec1));
        Assert.assertEquals(vols.size(), 4);
        Assert.assertEquals("/container/path", vols.get(0).getContainerPath());
        Assert.assertEquals("/host/path", vols.get(0).getHostPath());
        Assert.assertEquals(RO, vols.get(0).getMode());
        Assert.assertEquals("/container/path", vols.get(1).getContainerPath());
        Assert.assertEquals(RO, vols.get(1).getMode());
        Assert.assertEquals("/container/path", vols.get(2).getContainerPath());
        Assert.assertEquals("/host/path", vols.get(2).getHostPath());
        Assert.assertEquals(RW, vols.get(2).getMode());
        Assert.assertEquals("/container/path", vols.get(3).getContainerPath());
        Assert.assertEquals(RW, vols.get(3).getMode());
        // should handle empty strings, but not error
        Assert.assertEquals(0, MesosTaskManagerParameters.buildVolumes(Option.<String>apply("")).size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBuildVolumesBadMode() throws Exception {
        MesosTaskManagerParameters.buildVolumes(Option.<String>apply("/hp:/cp:RF"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBuildVolumesMalformed() throws Exception {
        MesosTaskManagerParameters.buildVolumes(Option.<String>apply("/hp:/cp:ro:extra"));
    }

    @Test
    public void testContainerVolumes() throws Exception {
        Configuration config = new Configuration();
        config.setString(MESOS_RM_CONTAINER_VOLUMES, "/host/path:/container/path:ro");
        MesosTaskManagerParameters params = MesosTaskManagerParameters.create(config);
        Assert.assertEquals(1, params.containerVolumes().size());
        Assert.assertEquals("/container/path", params.containerVolumes().get(0).getContainerPath());
        Assert.assertEquals("/host/path", params.containerVolumes().get(0).getHostPath());
        Assert.assertEquals(RO, params.containerVolumes().get(0).getMode());
    }

    @Test
    public void testContainerDockerParameter() throws Exception {
        Configuration config = new Configuration();
        config.setString(MESOS_RM_CONTAINER_DOCKER_PARAMETERS, "testKey=testValue");
        MesosTaskManagerParameters params = MesosTaskManagerParameters.create(config);
        Assert.assertEquals(params.dockerParameters().size(), 1);
        Assert.assertEquals(params.dockerParameters().get(0).getKey(), "testKey");
        Assert.assertEquals(params.dockerParameters().get(0).getValue(), "testValue");
    }

    @Test
    public void testContainerDockerParameters() throws Exception {
        Configuration config = new Configuration();
        config.setString(MESOS_RM_CONTAINER_DOCKER_PARAMETERS, "testKey1=testValue1,testKey2=testValue2,testParam3=key3=value3,testParam4=\"key4=value4\"");
        MesosTaskManagerParameters params = MesosTaskManagerParameters.create(config);
        Assert.assertEquals(params.dockerParameters().size(), 4);
        Assert.assertEquals(params.dockerParameters().get(0).getKey(), "testKey1");
        Assert.assertEquals(params.dockerParameters().get(0).getValue(), "testValue1");
        Assert.assertEquals(params.dockerParameters().get(1).getKey(), "testKey2");
        Assert.assertEquals(params.dockerParameters().get(1).getValue(), "testValue2");
        Assert.assertEquals(params.dockerParameters().get(2).getKey(), "testParam3");
        Assert.assertEquals(params.dockerParameters().get(2).getValue(), "key3=value3");
        Assert.assertEquals(params.dockerParameters().get(3).getKey(), "testParam4");
        Assert.assertEquals(params.dockerParameters().get(3).getValue(), "\"key4=value4\"");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testContainerDockerParametersMalformed() throws Exception {
        Configuration config = new Configuration();
        config.setString(MESOS_RM_CONTAINER_DOCKER_PARAMETERS, "badParam");
        MesosTaskManagerParameters params = MesosTaskManagerParameters.create(config);
    }

    @Test
    public void testUriParameters() throws Exception {
        Configuration config = new Configuration();
        config.setString(MESOS_TM_URIS, "file:///dev/null,http://localhost/test,  test_url ");
        MesosTaskManagerParameters params = MesosTaskManagerParameters.create(config);
        Assert.assertEquals(params.uris().size(), 3);
        Assert.assertEquals(params.uris().get(0), "file:///dev/null");
        Assert.assertEquals(params.uris().get(1), "http://localhost/test");
        Assert.assertEquals(params.uris().get(2), "test_url");
    }

    @Test
    public void testUriParametersDefault() throws Exception {
        Configuration config = new Configuration();
        MesosTaskManagerParameters params = MesosTaskManagerParameters.create(config);
        Assert.assertEquals(params.uris().size(), 0);
    }

    @Test
    public void testForcePullImageFalse() {
        Configuration config = new Configuration();
        config.setBoolean(MESOS_RM_CONTAINER_DOCKER_FORCE_PULL_IMAGE, false);
        MesosTaskManagerParameters params = MesosTaskManagerParameters.create(config);
        Assert.assertEquals(params.dockerForcePullImage(), false);
    }

    @Test
    public void testForcePullImageDefault() {
        Configuration config = new Configuration();
        MesosTaskManagerParameters params = MesosTaskManagerParameters.create(config);
        Assert.assertEquals(params.dockerForcePullImage(), false);
    }

    @Test
    public void givenTwoConstraintsInConfigShouldBeParsed() throws Exception {
        MesosTaskManagerParameters mesosTaskManagerParameters = MesosTaskManagerParameters.create(MesosTaskManagerParametersTest.withHardHostAttrConstraintConfiguration("cluster:foo,az:eu-west-1"));
        Assert.assertThat(mesosTaskManagerParameters.constraints().size(), Is.is(2));
        ConstraintEvaluator firstConstraintEvaluator = new HostAttrValueConstraint("cluster", new com.netflix.fenzo.functions.Func1<String, String>() {
            @Override
            public String call(String s) {
                return "foo";
            }
        });
        ConstraintEvaluator secondConstraintEvaluator = new HostAttrValueConstraint("az", new com.netflix.fenzo.functions.Func1<String, String>() {
            @Override
            public String call(String s) {
                return "foo";
            }
        });
        Assert.assertThat(mesosTaskManagerParameters.constraints().get(0).getName(), Is.is(firstConstraintEvaluator.getName()));
        Assert.assertThat(mesosTaskManagerParameters.constraints().get(1).getName(), Is.is(secondConstraintEvaluator.getName()));
    }

    @Test
    public void givenOneConstraintInConfigShouldBeParsed() throws Exception {
        MesosTaskManagerParameters mesosTaskManagerParameters = MesosTaskManagerParameters.create(MesosTaskManagerParametersTest.withHardHostAttrConstraintConfiguration("cluster:foo"));
        Assert.assertThat(mesosTaskManagerParameters.constraints().size(), Is.is(1));
        ConstraintEvaluator firstConstraintEvaluator = new HostAttrValueConstraint("cluster", new com.netflix.fenzo.functions.Func1<String, String>() {
            @Override
            public String call(String s) {
                return "foo";
            }
        });
        Assert.assertThat(mesosTaskManagerParameters.constraints().get(0).getName(), Is.is(firstConstraintEvaluator.getName()));
    }

    @Test
    public void givenEmptyConstraintInConfigShouldBeParsed() throws Exception {
        MesosTaskManagerParameters mesosTaskManagerParameters = MesosTaskManagerParameters.create(MesosTaskManagerParametersTest.withHardHostAttrConstraintConfiguration(""));
        Assert.assertThat(mesosTaskManagerParameters.constraints().size(), Is.is(0));
    }

    @Test
    public void givenInvalidConstraintInConfigShouldBeParsed() throws Exception {
        MesosTaskManagerParameters mesosTaskManagerParameters = MesosTaskManagerParameters.create(MesosTaskManagerParametersTest.withHardHostAttrConstraintConfiguration(",:,"));
        Assert.assertThat(mesosTaskManagerParameters.constraints().size(), Is.is(0));
    }

    @Test(expected = IllegalConfigurationException.class)
    public void testNegativeNumberOfGPUs() throws Exception {
        MesosTaskManagerParameters.create(MesosTaskManagerParametersTest.withGPUConfiguration((-1)));
    }
}

