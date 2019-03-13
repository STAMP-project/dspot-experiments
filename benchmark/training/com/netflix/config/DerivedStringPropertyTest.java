/**
 * Copyright 2014 Netflix, Inc.
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
package com.netflix.config;


import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Assert;
import org.junit.Test;


public class DerivedStringPropertyTest {
    @Test
    public void testPropertyChanged() {
        final AtomicBoolean derived = new AtomicBoolean(false);
        final String defaultVal = "hi";
        DerivedStringProperty p = new DerivedStringProperty("com.netflix.hello", defaultVal) {
            @Override
            protected Object derive(String value) {
                derived.set(true);
                return String.format("%s/derived", value);
            }
        };
        // p.propertyChanged();
        p.propertyChangedInternal();
        Assert.assertTrue("derive() was not called", derived.get());
        String derivedDefault = String.format("%s/derived", defaultVal);
        Assert.assertEquals(derivedDefault, p.get());
        Assert.assertEquals(derivedDefault, p.getDefaultValue());
    }

    @Test
    public void testPropertyChangedWhenDeriveThrowsException() {
        final String defaultVal = "hi";
        DerivedStringProperty p = new DerivedStringProperty("com.netflix.hello", defaultVal) {
            @Override
            protected Object derive(String value) {
                throw new RuntimeException("oops");
            }
        };
        p.propertyChangedInternal();
        Assert.assertEquals(null, p.get());
    }
}

