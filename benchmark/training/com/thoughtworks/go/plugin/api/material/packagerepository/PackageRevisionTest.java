/**
 * Copyright 2017 ThoughtWorks, Inc.
 *
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
 */
package com.thoughtworks.go.plugin.api.material.packagerepository;


import com.thoughtworks.go.plugin.api.material.packagerepository.exceptions.InvalidPackageRevisionDataException;
import java.util.Date;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class PackageRevisionTest {
    @Test
    public void shouldAcceptDataKeyMadeUpOfAlpahNumericAndUnderScoreCharacters() throws Exception {
        PackageRevision packageRevision = new PackageRevision("rev123", new Date(), "loser");
        packageRevision.addData("HELLO_WORLD123", "value");
        Assert.assertThat(packageRevision.getDataFor("HELLO_WORLD123"), Matchers.is("value"));
    }

    @Test
    public void shouldThrowExceptionWhenDataKeyIsNullOrEmpty() throws Exception {
        PackageRevision packageRevision = new PackageRevision("rev123", new Date(), "loser");
        try {
            packageRevision.addData(null, "value");
        } catch (InvalidPackageRevisionDataException e) {
            Assert.assertThat(e.getMessage(), Matchers.is("Key names cannot be null or empty."));
        }
        try {
            packageRevision.addData("", "value");
        } catch (InvalidPackageRevisionDataException e) {
            Assert.assertThat(e.getMessage(), Matchers.is("Key names cannot be null or empty."));
        }
    }

    @Test
    public void shouldThrowExceptionIfDataKeyContainsCharactersOtherThanAlphaNumericAndUnderScoreCharacters() throws Exception {
        PackageRevision packageRevision = new PackageRevision("rev123", new Date(), "loser");
        try {
            packageRevision.addData("HEL-LO-WORLD", "value");
            Assert.fail("should have thrown exception");
        } catch (InvalidPackageRevisionDataException e) {
            Assert.assertThat(e.getMessage(), Matchers.is("Key 'HEL-LO-WORLD' is invalid. Key names should consists of only alphanumeric characters and/or underscores."));
        }
    }

    @Test
    public void shouldNotAllowDataWhenKeyIsInvalid() throws Exception {
        assertForInvalidKey("", "Key names cannot be null or empty.");
        assertForInvalidKey("!key", "Key '!key' is invalid. Key names should consists of only alphanumeric characters and/or underscores.");
    }
}

