/**
 * ***********************GO-LICENSE-START*********************************
 * Copyright 2014 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ************************GO-LICENSE-END**********************************
 */
package com.thoughtworks.go.config.validation;


import com.thoughtworks.go.config.BasicCruiseConfig;
import com.thoughtworks.go.config.CruiseConfig;
import com.thoughtworks.go.config.ServerConfig;
import com.thoughtworks.go.helper.SecurityConfigMother;
import java.io.File;
import org.junit.Assert;
import org.junit.Test;


public class ArtifactDirValidatorTest {
    @Test
    public void shouldThrowExceptionWhenUserProvidesDot() throws Exception {
        CruiseConfig cruiseConfig = new BasicCruiseConfig();
        cruiseConfig.setServerConfig(new ServerConfig(".", null));
        ArtifactDirValidator dirValidator = new ArtifactDirValidator();
        try {
            dirValidator.validate(cruiseConfig);
            Assert.fail("should throw exception, see dot will make server check out the repository in the wrong place.");
        } catch (Exception e) {
        }
    }

    @Test
    public void shouldThrowExceptionWhenUserProvidesEmtpty() {
        CruiseConfig cruiseConfig = new BasicCruiseConfig();
        cruiseConfig.setServerConfig(new ServerConfig("", null));
        ArtifactDirValidator dirValidator = new ArtifactDirValidator();
        try {
            dirValidator.validate(cruiseConfig);
            Assert.fail("should throw exception");
        } catch (Exception e) {
        }
    }

    @Test
    public void shouldThrowExceptionWhenUserProvidesNull() {
        CruiseConfig cruiseConfig = new BasicCruiseConfig();
        cruiseConfig.setServerConfig(new ServerConfig(null, SecurityConfigMother.securityConfigWithRole("role", "user")));
        ArtifactDirValidator dirValidator = new ArtifactDirValidator();
        try {
            dirValidator.validate(cruiseConfig);
            Assert.fail("should throw exception");
        } catch (Exception e) {
        }
    }

    @Test
    public void shouldThrowExceptionWhenUserProvidesPathPointToServerSandBox() {
        File file = new File("");
        CruiseConfig cruiseConfig = new BasicCruiseConfig();
        cruiseConfig.setServerConfig(new ServerConfig(file.getAbsolutePath(), null));
        ArtifactDirValidator dirValidator = new ArtifactDirValidator();
        try {
            dirValidator.validate(cruiseConfig);
            Assert.fail("should throw exception, see dot will make server check out the repository in the wrong place.");
        } catch (Exception e) {
        }
    }

    @Test
    public void shouldNotThrowExceptionWhenUserProvidesValidPath() throws Exception {
        File file = new File("");
        CruiseConfig cruiseConfig = new BasicCruiseConfig();
        cruiseConfig.setServerConfig(new ServerConfig(((file.getAbsolutePath()) + "/logs"), null));
        ArtifactDirValidator dirValidator = new ArtifactDirValidator();
        dirValidator.validate(cruiseConfig);
    }
}

