/**
 * -\-\-
 * Spotify Apollo API Implementations
 * --
 * Copyright (C) 2013 - 2015 Spotify AB
 * --
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * -/-/-
 */
package com.spotify.apollo.meta.model;


import Model.MetaInfo;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class DefaultMetaGathererTest {
    private static final String buildVersion = "1.2.3.4";

    private static final String containerVersion = "freight-container 3.2.1";

    MetaGatherer gatherer;

    @Test
    public void testInfo() throws InterruptedException {
        Thread.sleep(10);
        Model.MetaInfo metaInfo = gatherer.info();
        Assert.assertThat(metaInfo.componentId(), CoreMatchers.is("freight-container"));
        Assert.assertThat(metaInfo.buildVersion(), CoreMatchers.is(DefaultMetaGathererTest.buildVersion));
        Assert.assertThat(metaInfo.containerVersion(), CoreMatchers.is(DefaultMetaGathererTest.containerVersion));
        Assert.assertThat(metaInfo.serviceUptime(), CoreMatchers.is(Matchers.greaterThan(0.0)));
        Assert.assertThat(metaInfo.systemVersion(), CoreMatchers.is(CoreMatchers.notNullValue()));
    }
}

