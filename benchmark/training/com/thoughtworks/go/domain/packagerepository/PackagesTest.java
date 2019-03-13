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
package com.thoughtworks.go.domain.packagerepository;


import PackageRepository.NAME;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class PackagesTest {
    @Test
    public void shouldCheckForEqualityOfPackages() {
        PackageDefinition packageDefinition = new PackageDefinition();
        Packages packages = new Packages(packageDefinition);
        Assert.assertThat(packages, Matchers.is(new Packages(packageDefinition)));
    }

    @Test
    public void shouldFindPackageGivenThePkgId() throws Exception {
        PackageRepository repository = PackageRepositoryMother.create("repo-id2", "repo2", "plugin-id", "1.0", null);
        PackageDefinition p1 = PackageDefinitionMother.create("id1", "pkg1", null, repository);
        PackageDefinition p2 = PackageDefinitionMother.create("id2", "pkg2", null, repository);
        Packages packages = new Packages(p1, p2);
        Assert.assertThat(packages.find("id2"), Matchers.is(p2));
    }

    @Test
    public void shouldReturnNullIfNoMatchingPkgFound() throws Exception {
        Packages packages = new Packages();
        Assert.assertThat(packages.find("id2"), Matchers.is(Matchers.nullValue()));
    }

    @Test
    public void shouldValidateForCaseInsensitiveNameUniqueness() {
        PackageDefinition p1 = PackageDefinitionMother.create("id1", "pkg1", null, null);
        PackageDefinition p2 = PackageDefinitionMother.create("id2", "pkg1", null, null);
        PackageDefinition p3 = PackageDefinitionMother.create("id3", "pkg3", null, null);
        Packages packages = new Packages(p1, p2, p3);
        packages.validate(null);
        Assert.assertThat(p1.errors().isEmpty(), Matchers.is(true));
        String nameError = String.format("You have defined multiple packages called '%s'. Package names are case-insensitive and must be unique within a repository.", p2.getName());
        Assert.assertThat(p2.errors().isEmpty(), Matchers.is(false));
        Assert.assertThat(p2.errors().getAllOn(NAME).contains(nameError), Matchers.is(true));
        Assert.assertThat(p3.errors().isEmpty(), Matchers.is(true));
    }
}

