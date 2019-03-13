/**
 * Copyright (c) 2014, Yahoo!, Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License. You
 * may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License. See accompanying
 * LICENSE file.
 */
package com.yahoo.ycsb.db;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * OptionsSupportTest provides tests for the OptionsSupport class.
 *
 * @author rjm
 */
public class OptionsSupportTest {
    /**
     * Test method for {@link OptionsSupport#updateUrl(String, Properties)} for
     * {@code mongodb.maxconnections}.
     */
    @Test
    public void testUpdateUrlMaxConnections() {
        Assert.assertThat(OptionsSupport.updateUrl("mongodb://locahost:27017/", props("mongodb.maxconnections", "1234")), CoreMatchers.is("mongodb://locahost:27017/?maxPoolSize=1234"));
        Assert.assertThat(OptionsSupport.updateUrl("mongodb://locahost:27017/?foo=bar", props("mongodb.maxconnections", "1234")), CoreMatchers.is("mongodb://locahost:27017/?foo=bar&maxPoolSize=1234"));
        Assert.assertThat(OptionsSupport.updateUrl("mongodb://locahost:27017/?maxPoolSize=1", props("mongodb.maxconnections", "1234")), CoreMatchers.is("mongodb://locahost:27017/?maxPoolSize=1"));
        Assert.assertThat(OptionsSupport.updateUrl("mongodb://locahost:27017/?foo=bar", props("foo", "1234")), CoreMatchers.is("mongodb://locahost:27017/?foo=bar"));
    }

    /**
     * Test method for {@link OptionsSupport#updateUrl(String, Properties)} for
     * {@code mongodb.threadsAllowedToBlockForConnectionMultiplier}.
     */
    @Test
    public void testUpdateUrlWaitQueueMultiple() {
        Assert.assertThat(OptionsSupport.updateUrl("mongodb://locahost:27017/", props("mongodb.threadsAllowedToBlockForConnectionMultiplier", "1234")), CoreMatchers.is("mongodb://locahost:27017/?waitQueueMultiple=1234"));
        Assert.assertThat(OptionsSupport.updateUrl("mongodb://locahost:27017/?foo=bar", props("mongodb.threadsAllowedToBlockForConnectionMultiplier", "1234")), CoreMatchers.is("mongodb://locahost:27017/?foo=bar&waitQueueMultiple=1234"));
        Assert.assertThat(OptionsSupport.updateUrl("mongodb://locahost:27017/?waitQueueMultiple=1", props("mongodb.threadsAllowedToBlockForConnectionMultiplier", "1234")), CoreMatchers.is("mongodb://locahost:27017/?waitQueueMultiple=1"));
        Assert.assertThat(OptionsSupport.updateUrl("mongodb://locahost:27017/?foo=bar", props("foo", "1234")), CoreMatchers.is("mongodb://locahost:27017/?foo=bar"));
    }

    /**
     * Test method for {@link OptionsSupport#updateUrl(String, Properties)} for
     * {@code mongodb.threadsAllowedToBlockForConnectionMultiplier}.
     */
    @Test
    public void testUpdateUrlWriteConcern() {
        Assert.assertThat(OptionsSupport.updateUrl("mongodb://locahost:27017/", props("mongodb.writeConcern", "errors_ignored")), CoreMatchers.is("mongodb://locahost:27017/?w=0"));
        Assert.assertThat(OptionsSupport.updateUrl("mongodb://locahost:27017/?foo=bar", props("mongodb.writeConcern", "unacknowledged")), CoreMatchers.is("mongodb://locahost:27017/?foo=bar&w=0"));
        Assert.assertThat(OptionsSupport.updateUrl("mongodb://locahost:27017/?foo=bar", props("mongodb.writeConcern", "acknowledged")), CoreMatchers.is("mongodb://locahost:27017/?foo=bar&w=1"));
        Assert.assertThat(OptionsSupport.updateUrl("mongodb://locahost:27017/?foo=bar", props("mongodb.writeConcern", "journaled")), CoreMatchers.is("mongodb://locahost:27017/?foo=bar&journal=true&j=true"));
        Assert.assertThat(OptionsSupport.updateUrl("mongodb://locahost:27017/?foo=bar", props("mongodb.writeConcern", "replica_acknowledged")), CoreMatchers.is("mongodb://locahost:27017/?foo=bar&w=2"));
        Assert.assertThat(OptionsSupport.updateUrl("mongodb://locahost:27017/?foo=bar", props("mongodb.writeConcern", "majority")), CoreMatchers.is("mongodb://locahost:27017/?foo=bar&w=majority"));
        // w already exists.
        Assert.assertThat(OptionsSupport.updateUrl("mongodb://locahost:27017/?w=1", props("mongodb.writeConcern", "acknowledged")), CoreMatchers.is("mongodb://locahost:27017/?w=1"));
        // Unknown options
        Assert.assertThat(OptionsSupport.updateUrl("mongodb://locahost:27017/?foo=bar", props("foo", "1234")), CoreMatchers.is("mongodb://locahost:27017/?foo=bar"));
    }

    /**
     * Test method for {@link OptionsSupport#updateUrl(String, Properties)} for
     * {@code mongodb.threadsAllowedToBlockForConnectionMultiplier}.
     */
    @Test
    public void testUpdateUrlReadPreference() {
        Assert.assertThat(OptionsSupport.updateUrl("mongodb://locahost:27017/", props("mongodb.readPreference", "primary")), CoreMatchers.is("mongodb://locahost:27017/?readPreference=primary"));
        Assert.assertThat(OptionsSupport.updateUrl("mongodb://locahost:27017/?foo=bar", props("mongodb.readPreference", "primary_preferred")), CoreMatchers.is("mongodb://locahost:27017/?foo=bar&readPreference=primaryPreferred"));
        Assert.assertThat(OptionsSupport.updateUrl("mongodb://locahost:27017/?foo=bar", props("mongodb.readPreference", "secondary")), CoreMatchers.is("mongodb://locahost:27017/?foo=bar&readPreference=secondary"));
        Assert.assertThat(OptionsSupport.updateUrl("mongodb://locahost:27017/?foo=bar", props("mongodb.readPreference", "secondary_preferred")), CoreMatchers.is("mongodb://locahost:27017/?foo=bar&readPreference=secondaryPreferred"));
        Assert.assertThat(OptionsSupport.updateUrl("mongodb://locahost:27017/?foo=bar", props("mongodb.readPreference", "nearest")), CoreMatchers.is("mongodb://locahost:27017/?foo=bar&readPreference=nearest"));
        // readPreference already exists.
        Assert.assertThat(OptionsSupport.updateUrl("mongodb://locahost:27017/?readPreference=primary", props("mongodb.readPreference", "secondary")), CoreMatchers.is("mongodb://locahost:27017/?readPreference=primary"));
        // Unknown options
        Assert.assertThat(OptionsSupport.updateUrl("mongodb://locahost:27017/?foo=bar", props("foo", "1234")), CoreMatchers.is("mongodb://locahost:27017/?foo=bar"));
    }
}

