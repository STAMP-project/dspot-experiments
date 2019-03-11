/**
 * Copyright (C) 2012-2019 the original author or authors.
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
package ninja.utils;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import static NinjaMode.dev;
import static NinjaMode.prod;


public class OverlayedNinjaPropertiesTest {
    @Test
    public void precedence() {
        OverlayedNinjaProperties ninjaProperties = new OverlayedNinjaProperties(new NinjaPropertiesImpl(prod, "conf/overlayed.empty.conf"));
        // no value anywhere
        Assert.assertThat(ninjaProperties.get("key1", null, null), CoreMatchers.is(CoreMatchers.nullValue()));
        Assert.assertThat(ninjaProperties.getInteger("key2", null, null), CoreMatchers.is(CoreMatchers.nullValue()));
        Assert.assertThat(ninjaProperties.getBoolean("key3", null, null), CoreMatchers.is(CoreMatchers.nullValue()));
        // defaultValue > nothing
        Assert.assertThat(ninjaProperties.get("key1", null, "default"), CoreMatchers.is("default"));
        Assert.assertThat(ninjaProperties.getInteger("key2", null, 0), CoreMatchers.is(0));
        Assert.assertThat(ninjaProperties.getBoolean("key3", null, true), CoreMatchers.is(Boolean.TRUE));
        // configProperty > defaultValue
        ninjaProperties = new OverlayedNinjaProperties(new NinjaPropertiesImpl(prod, "conf/overlayed.conf"));
        Assert.assertThat(ninjaProperties.get("key1", null, "default"), CoreMatchers.is("test1"));
        Assert.assertThat(ninjaProperties.getInteger("key2", null, 0), CoreMatchers.is(1));
        Assert.assertThat(ninjaProperties.getBoolean("key3", null, true), CoreMatchers.is(Boolean.FALSE));
        try {
            // systemProperty > configProperty
            System.setProperty("key1", "system");
            System.setProperty("key2", "2");
            System.setProperty("key3", "true");
            Assert.assertThat(ninjaProperties.get("key1", null, "default"), CoreMatchers.is("system"));
            Assert.assertThat(ninjaProperties.getInteger("key2", null, 0), CoreMatchers.is(2));
            Assert.assertThat(ninjaProperties.getBoolean("key3", null, true), CoreMatchers.is(Boolean.TRUE));
            // currentValue > systemProperty
            Assert.assertThat(ninjaProperties.get("key1", "current", "default"), CoreMatchers.is("current"));
            Assert.assertThat(ninjaProperties.getInteger("key2", 3, 0), CoreMatchers.is(3));
            Assert.assertThat(ninjaProperties.getBoolean("key3", false, true), CoreMatchers.is(Boolean.FALSE));
        } finally {
            System.clearProperty("key1");
            System.clearProperty("key2");
            System.clearProperty("key3");
        }
    }

    @Test
    public void badValues() {
        OverlayedNinjaProperties ninjaProperties = new OverlayedNinjaProperties(new NinjaPropertiesImpl(dev, "conf/overlayed.bad.conf"));
        try {
            Integer i = ninjaProperties.getInteger("key2", null, null);
            Assert.fail("exception expected");
        } catch (IllegalArgumentException e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.containsString("Unable to convert property"));
        }
        try {
            Long l = ninjaProperties.getLong("key2", null, null);
            Assert.fail("exception expected");
        } catch (IllegalArgumentException e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.containsString("Unable to convert property"));
        }
        try {
            Boolean b = ninjaProperties.getBoolean("key3", null, null);
            Assert.fail("exception expected");
        } catch (IllegalArgumentException e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.containsString("Unable to convert property"));
        }
    }
}

