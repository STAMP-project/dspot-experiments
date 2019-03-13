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
package com.thoughtworks.go.util.command;


import EnvironmentVariableContext.EnvironmentVariable;
import EnvironmentVariableContext.EnvironmentVariable.MASK_VALUE;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class EnvironmentVariableContextTest {
    private static final String PROPERTY_NAME = "PROPERTY_NAME";

    private static final String PROPERTY_VALUE = "property value";

    private static final String NEW_VALUE = "new value";

    @Test
    public void shouldBeAbleToAddProperties() {
        EnvironmentVariableContext context = new EnvironmentVariableContext();
        context.setProperty(EnvironmentVariableContextTest.PROPERTY_NAME, EnvironmentVariableContextTest.PROPERTY_VALUE, false);
        Assert.assertThat(context.getProperty(EnvironmentVariableContextTest.PROPERTY_NAME), Matchers.is(EnvironmentVariableContextTest.PROPERTY_VALUE));
    }

    @Test
    public void shouldReportLastAddedAsPropertyValue() {
        EnvironmentVariableContext context = new EnvironmentVariableContext();
        context.setProperty(EnvironmentVariableContextTest.PROPERTY_NAME, EnvironmentVariableContextTest.PROPERTY_VALUE, false);
        context.setProperty(EnvironmentVariableContextTest.PROPERTY_NAME, EnvironmentVariableContextTest.NEW_VALUE, false);
        Assert.assertThat(context.getProperty(EnvironmentVariableContextTest.PROPERTY_NAME), Matchers.is(EnvironmentVariableContextTest.NEW_VALUE));
    }

    @Test
    public void shouldReportWhenAVariableIsSet() {
        EnvironmentVariableContext context = new EnvironmentVariableContext();
        context.setProperty(EnvironmentVariableContextTest.PROPERTY_NAME, EnvironmentVariableContextTest.PROPERTY_VALUE, false);
        List<String> repo = context.report(Collections.<String>emptyList());
        Assert.assertThat(repo.size(), Matchers.is(1));
        Assert.assertThat(repo.get(0), Matchers.is("[go] setting environment variable 'PROPERTY_NAME' to value 'property value'"));
    }

    @Test
    public void shouldReportWhenAVariableIsOverridden() {
        EnvironmentVariableContext context = new EnvironmentVariableContext();
        context.setProperty(EnvironmentVariableContextTest.PROPERTY_NAME, EnvironmentVariableContextTest.PROPERTY_VALUE, false);
        context.setProperty(EnvironmentVariableContextTest.PROPERTY_NAME, EnvironmentVariableContextTest.NEW_VALUE, false);
        List<String> report = context.report(Collections.<String>emptyList());
        Assert.assertThat(report.size(), Matchers.is(2));
        Assert.assertThat(report.get(0), Matchers.is("[go] setting environment variable 'PROPERTY_NAME' to value 'property value'"));
        Assert.assertThat(report.get(1), Matchers.is("[go] overriding environment variable 'PROPERTY_NAME' with value 'new value'"));
    }

    @Test
    public void shouldMaskOverRiddenSecureVariable() {
        EnvironmentVariableContext context = new EnvironmentVariableContext();
        context.setProperty(EnvironmentVariableContextTest.PROPERTY_NAME, EnvironmentVariableContextTest.PROPERTY_VALUE, true);
        context.setProperty(EnvironmentVariableContextTest.PROPERTY_NAME, EnvironmentVariableContextTest.NEW_VALUE, true);
        List<String> report = context.report(Collections.<String>emptyList());
        Assert.assertThat(report.size(), Matchers.is(2));
        Assert.assertThat(report.get(0), Matchers.is(String.format("[go] setting environment variable 'PROPERTY_NAME' to value '%s'", MASK_VALUE)));
        Assert.assertThat(report.get(1), Matchers.is(String.format("[go] overriding environment variable 'PROPERTY_NAME' with value '%s'", MASK_VALUE)));
    }

    @Test
    public void shouldReportSecureVariableAsMaskedValue() {
        EnvironmentVariableContext context = new EnvironmentVariableContext();
        context.setProperty(EnvironmentVariableContextTest.PROPERTY_NAME, EnvironmentVariableContextTest.PROPERTY_VALUE, true);
        List<String> repot = context.report(Collections.<String>emptyList());
        Assert.assertThat(repot.size(), Matchers.is(1));
        Assert.assertThat(repot.get(0), Matchers.is(String.format("[go] setting environment variable 'PROPERTY_NAME' to value '%s'", MASK_VALUE)));
    }

    @Test
    public void testReportOverrideForProcessEnvironmentVariables() {
        EnvironmentVariableContext context = new EnvironmentVariableContext();
        context.setProperty("PATH", "/foo", false);
        List<String> report = context.report(Collections.singleton("PATH"));
        Assert.assertThat(report.size(), Matchers.is(1));
        Assert.assertThat(report.get(0), Matchers.is("[go] overriding environment variable 'PATH' with value '/foo'"));
    }

    @Test
    public void shouldBeAbleToSerialize() throws IOException, ClassNotFoundException {
        EnvironmentVariableContext original = new EnvironmentVariableContext("blahKey", "blahValue");
        EnvironmentVariableContext clone = ((EnvironmentVariableContext) (SerializationTester.serializeAndDeserialize(original)));
        Assert.assertThat(clone, Matchers.is(original));
    }

    @Test
    public void shouldSaveSecureStateAboutAEnvironmentVariable() {
        EnvironmentVariableContext environmentVariableContext = new EnvironmentVariableContext();
        environmentVariableContext.setProperty(EnvironmentVariableContextTest.PROPERTY_NAME, EnvironmentVariableContextTest.PROPERTY_VALUE, false);
        Assert.assertThat(environmentVariableContext.getProperty(EnvironmentVariableContextTest.PROPERTY_NAME), Matchers.is(EnvironmentVariableContextTest.PROPERTY_VALUE));
        Assert.assertThat(environmentVariableContext.getPropertyForDisplay(EnvironmentVariableContextTest.PROPERTY_NAME), Matchers.is(EnvironmentVariableContextTest.PROPERTY_VALUE));
        Assert.assertThat(environmentVariableContext.isPropertySecure(EnvironmentVariableContextTest.PROPERTY_NAME), Matchers.is(false));
    }

    @Test
    public void shouldSaveSecureStateForASecureEnvironmentVariable() {
        EnvironmentVariableContext environmentVariableContext = new EnvironmentVariableContext();
        environmentVariableContext.setProperty(EnvironmentVariableContextTest.PROPERTY_NAME, EnvironmentVariableContextTest.PROPERTY_VALUE, true);
        Assert.assertThat(environmentVariableContext.getProperty(EnvironmentVariableContextTest.PROPERTY_NAME), Matchers.is(EnvironmentVariableContextTest.PROPERTY_VALUE));
        Assert.assertThat(environmentVariableContext.getPropertyForDisplay(EnvironmentVariableContextTest.PROPERTY_NAME), Matchers.is(MASK_VALUE));
        Assert.assertThat(environmentVariableContext.isPropertySecure(EnvironmentVariableContextTest.PROPERTY_NAME), Matchers.is(true));
    }

    @Test
    public void shouldGetSecureEnvironmentVariables() {
        EnvironmentVariableContext environmentVariableContext = new EnvironmentVariableContext();
        environmentVariableContext.setProperty("secure_foo", "secure_foo_value", true);
        environmentVariableContext.setProperty("plain_foo", "plain_foo_value", false);
        List<EnvironmentVariableContext.EnvironmentVariable> secureEnvironmentVariables = environmentVariableContext.getSecureEnvironmentVariables();
        Assert.assertThat(secureEnvironmentVariables.size(), Matchers.is(1));
        Assert.assertThat(secureEnvironmentVariables, Matchers.hasItem(new EnvironmentVariableContext.EnvironmentVariable("secure_foo", "secure_foo_value", true)));
    }
}

