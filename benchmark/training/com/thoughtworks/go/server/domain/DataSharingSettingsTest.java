/**
 * Copyright 2018 ThoughtWorks, Inc.
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
package com.thoughtworks.go.server.domain;


import java.util.Date;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.jupiter.api.Test;


class DataSharingSettingsTest {
    @Test
    public void shouldCopyDataSharingAttributesFromProvidedObject() {
        Date currentUpdatedOn = new Date();
        currentUpdatedOn.setTime(((currentUpdatedOn.getTime()) - 10000));
        DataSharingSettings current = new DataSharingSettings(false, "default", currentUpdatedOn);
        DataSharingSettings latest = new DataSharingSettings(true, "Bob", new Date());
        Assert.assertThat(current.allowSharing(), Matchers.is(Matchers.not(latest.allowSharing())));
        Assert.assertThat(current.updatedBy(), Matchers.is(Matchers.not(latest.updatedBy())));
        Assert.assertThat(current.updatedOn().getTime(), Matchers.is(Matchers.not(latest.updatedOn().getTime())));
        current.copyFrom(latest);
        Assert.assertThat(current.allowSharing(), Matchers.is(latest.allowSharing()));
        Assert.assertThat(current.updatedBy(), Matchers.is(latest.updatedBy()));
        Assert.assertThat(current.updatedOn().getTime(), Matchers.is(latest.updatedOn().getTime()));
    }

    @Test
    public void shouldNotCopyPersistentObjectIdWhileCopying() {
        DataSharingSettings current = new DataSharingSettings();
        current.setId(1);
        DataSharingSettings latest = new DataSharingSettings();
        latest.setId(2);
        Assert.assertThat(current.getId(), Matchers.is(1L));
        current.copyFrom(latest);
        Assert.assertThat(current.getId(), Matchers.is(1L));
    }
}

