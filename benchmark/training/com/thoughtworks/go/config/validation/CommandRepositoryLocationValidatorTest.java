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


import com.thoughtworks.go.config.CruiseConfig;
import com.thoughtworks.go.config.ServerConfig;
import org.junit.Test;


public class CommandRepositoryLocationValidatorTest {
    private CommandRepositoryLocationValidator validator;

    private CruiseConfig cruiseConfig;

    private ServerConfig serverConfig;

    private String repoRootLocation;

    @Test
    public void shouldNotAllowEmptyValueForTaskRepositoryLocation() throws Exception {
        assertValidationFailedWith("", "Command Repository Location cannot be empty");
    }

    @Test
    public void shouldNotAllowSpacesForTaskRepositoryLocation() throws Exception {
        assertValidationFailedWith("          ", "Command Repository Location cannot be empty");
    }

    @Test
    public void shouldNotAllowToSpecifyPathOutsideTaskRepository() {
        String expectedMessage = String.format("Invalid  Repository Location, repository should be a subdirectory under %s", repoRootLocation);
        assertValidationFailedWith(".", expectedMessage);
        assertValidationFailedWith("../folder", expectedMessage);
    }

    @Test
    public void shouldAllowTaskRepositoryPath() throws Exception {
        assertValidationPassed("./test/sub");
        assertValidationPassed("../task_repository/test");
    }

    @Test
    public void shouldNotAllowTaskRepoPathThatContainingSpecialSysbols() {
        String message = "Invalid Repository Location";
        assertValidationFailedWith("/var/lib", message);
        assertValidationFailedWith("\\var/lib", message);
        assertValidationFailedWith("~/foo", message);
        assertValidationFailedWith("c:\\", message);
        assertValidationFailedWith("d:/", message);
    }
}

