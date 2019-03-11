/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.beam.sdk.options;


import Default.Boolean;
import PipelineOptionType.Enum.ARRAY;
import PipelineOptionType.Enum.BOOLEAN;
import PipelineOptionType.Enum.INTEGER;
import PipelineOptionType.Enum.NUMBER;
import PipelineOptionType.Enum.STRING;
import PipelineOptionsFactory.CACHE;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.auto.service.AutoService;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import org.apache.beam.model.jobmanagement.v1.JobApi.PipelineOptionDescriptor;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.PipelineResult;
import org.apache.beam.sdk.PipelineRunner;
import org.apache.beam.sdk.runners.PipelineRunnerRegistrar;
import org.apache.beam.sdk.testing.CrashingRunner;
import org.apache.beam.sdk.testing.ExpectedLogs;
import org.apache.beam.sdk.testing.InterceptingUrlClassLoader;
import org.apache.beam.sdk.testing.RestoreSystemProperties;
import org.apache.beam.sdk.util.common.ReflectHelpers;
import org.apache.beam.vendor.guava.v20_0.com.google.common.base.Charsets;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ArrayListMultimap;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.Collections2;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableList;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableMap;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ListMultimap;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.Sets;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link PipelineOptionsFactory}.
 */
@RunWith(JUnit4.class)
public class PipelineOptionsFactoryTest {
    private static final String DEFAULT_RUNNER_NAME = "DirectRunner";

    private static final Class<? extends PipelineRunner<?>> REGISTERED_RUNNER = PipelineOptionsFactoryTest.RegisteredTestRunner.class;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public TestRule restoreSystemProperties = new RestoreSystemProperties();

    @Rule
    public ExpectedLogs expectedLogs = ExpectedLogs.none(PipelineOptionsFactory.class);

    @Test
    public void testAutomaticRegistrationOfPipelineOptions() {
        Assert.assertTrue(PipelineOptionsFactory.getRegisteredOptions().contains(PipelineOptionsFactoryTest.RegisteredTestOptions.class));
    }

    @Test
    public void testAutomaticRegistrationOfRunners() {
        Assert.assertEquals(PipelineOptionsFactoryTest.REGISTERED_RUNNER, PipelineOptionsFactory.getRegisteredRunners().get(PipelineOptionsFactoryTest.REGISTERED_RUNNER.getSimpleName().toLowerCase()));
    }

    @Test
    public void testAutomaticRegistrationInculdesWithoutRunnerSuffix() {
        // Sanity check to make sure the substring works appropriately
        Assert.assertEquals("RegisteredTest", PipelineOptionsFactoryTest.REGISTERED_RUNNER.getSimpleName().substring(0, ((PipelineOptionsFactoryTest.REGISTERED_RUNNER.getSimpleName().length()) - ("Runner".length()))));
        Map<String, Class<? extends PipelineRunner<?>>> registered = CACHE.get().getSupportedPipelineRunners();
        Assert.assertEquals(PipelineOptionsFactoryTest.REGISTERED_RUNNER, registered.get(PipelineOptionsFactoryTest.REGISTERED_RUNNER.getSimpleName().toLowerCase().substring(0, ((PipelineOptionsFactoryTest.REGISTERED_RUNNER.getSimpleName().length()) - ("Runner".length())))));
    }

    @Test
    public void testAppNameIsSet() {
        ApplicationNameOptions options = PipelineOptionsFactory.as(ApplicationNameOptions.class);
        Assert.assertEquals(PipelineOptionsFactoryTest.class.getSimpleName(), options.getAppName());
    }

    /**
     * A simple test interface.
     */
    public interface TestPipelineOptions extends PipelineOptions {
        String getTestPipelineOption();

        void setTestPipelineOption(String value);
    }

    @Test
    public void testAppNameIsSetWhenUsingAs() {
        PipelineOptionsFactoryTest.TestPipelineOptions options = PipelineOptionsFactory.as(PipelineOptionsFactoryTest.TestPipelineOptions.class);
        Assert.assertEquals(PipelineOptionsFactoryTest.class.getSimpleName(), as(ApplicationNameOptions.class).getAppName());
    }

    @Test
    public void testOptionsIdIsSet() throws Exception {
        ObjectMapper mapper = new ObjectMapper().registerModules(ObjectMapper.findModules(ReflectHelpers.findClassLoader()));
        PipelineOptions options = PipelineOptionsFactory.create();
        // We purposely serialize/deserialize to get another instance. This allows to test if the
        // default has been set or not.
        PipelineOptions clone = mapper.readValue(mapper.writeValueAsString(options), PipelineOptions.class);
        // It is important that we don't call getOptionsId() before we have created the clone.
        Assert.assertEquals(options.getOptionsId(), clone.getOptionsId());
    }

    @Test
    public void testManualRegistration() {
        Assert.assertFalse(PipelineOptionsFactory.getRegisteredOptions().contains(PipelineOptionsFactoryTest.TestPipelineOptions.class));
        PipelineOptionsFactory.register(PipelineOptionsFactoryTest.TestPipelineOptions.class);
        Assert.assertTrue(PipelineOptionsFactory.getRegisteredOptions().contains(PipelineOptionsFactoryTest.TestPipelineOptions.class));
    }

    @Test
    public void testDefaultRegistration() {
        Assert.assertTrue(PipelineOptionsFactory.getRegisteredOptions().contains(PipelineOptions.class));
    }

    /**
     * A test interface missing a getter.
     */
    public interface MissingGetter extends PipelineOptions {
        void setObject(Object value);
    }

    @Test
    public void testMissingGetterThrows() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(("Expected getter for property [object] of type [java.lang.Object] on " + "[org.apache.beam.sdk.options.PipelineOptionsFactoryTest$MissingGetter]."));
        PipelineOptionsFactory.as(PipelineOptionsFactoryTest.MissingGetter.class);
    }

    /**
     * A test interface missing multiple getters.
     */
    public interface MissingMultipleGetters extends PipelineOptionsFactoryTest.MissingGetter {
        void setOtherObject(Object value);
    }

    @Test
    public void testMultipleMissingGettersThrows() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(("missing property methods on [org.apache.beam.sdk.options." + "PipelineOptionsFactoryTest$MissingMultipleGetters]"));
        expectedException.expectMessage("getter for property [object] of type [java.lang.Object]");
        expectedException.expectMessage("getter for property [otherObject] of type [java.lang.Object]");
        PipelineOptionsFactory.as(PipelineOptionsFactoryTest.MissingMultipleGetters.class);
    }

    /**
     * A test interface missing a setter.
     */
    public interface MissingSetter extends PipelineOptions {
        Object getObject();
    }

    @Test
    public void testMissingSetterThrows() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(("Expected setter for property [object] of type [java.lang.Object] on " + "[org.apache.beam.sdk.options.PipelineOptionsFactoryTest$MissingSetter]."));
        PipelineOptionsFactory.as(PipelineOptionsFactoryTest.MissingSetter.class);
    }

    /**
     * A test interface missing multiple setters.
     */
    public interface MissingMultipleSetters extends PipelineOptionsFactoryTest.MissingSetter {
        Object getOtherObject();
    }

    @Test
    public void testMissingMultipleSettersThrows() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(("missing property methods on [org.apache.beam.sdk.options." + "PipelineOptionsFactoryTest$MissingMultipleSetters]"));
        expectedException.expectMessage("setter for property [object] of type [java.lang.Object]");
        expectedException.expectMessage("setter for property [otherObject] of type [java.lang.Object]");
        PipelineOptionsFactory.as(PipelineOptionsFactoryTest.MissingMultipleSetters.class);
    }

    /**
     * A test interface missing a setter and a getter.
     */
    public interface MissingGettersAndSetters extends PipelineOptionsFactoryTest.MissingGetter {
        Object getOtherObject();
    }

    @Test
    public void testMissingGettersAndSettersThrows() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(("missing property methods on [org.apache.beam.sdk.options." + "PipelineOptionsFactoryTest$MissingGettersAndSetters]"));
        expectedException.expectMessage("getter for property [object] of type [java.lang.Object]");
        expectedException.expectMessage("setter for property [otherObject] of type [java.lang.Object]");
        PipelineOptionsFactory.as(PipelineOptionsFactoryTest.MissingGettersAndSetters.class);
    }

    /**
     * A test interface with a type mismatch between the getter and setter.
     */
    public interface GetterSetterTypeMismatch extends PipelineOptions {
        boolean getValue();

        void setValue(int value);
    }

    @Test
    public void testGetterSetterTypeMismatchThrows() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(("Type mismatch between getter and setter methods for property [value]. Getter is of type " + "[boolean] whereas setter is of type [int]."));
        PipelineOptionsFactory.as(PipelineOptionsFactoryTest.GetterSetterTypeMismatch.class);
    }

    /**
     * A test interface with multiple type mismatches between getters and setters.
     */
    public interface MultiGetterSetterTypeMismatch extends PipelineOptionsFactoryTest.GetterSetterTypeMismatch {
        long getOther();

        void setOther(String other);
    }

    @Test
    public void testMultiGetterSetterTypeMismatchThrows() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Type mismatches between getters and setters detected:");
        expectedException.expectMessage(("Property [value]: Getter is of type " + "[boolean] whereas setter is of type [int]."));
        expectedException.expectMessage(("Property [other]: Getter is of type [long] " + "whereas setter is of type [class java.lang.String]."));
        PipelineOptionsFactory.as(PipelineOptionsFactoryTest.MultiGetterSetterTypeMismatch.class);
    }

    /**
     * A test interface representing a composite interface.
     */
    public interface CombinedObject extends PipelineOptionsFactoryTest.MissingGetter , PipelineOptionsFactoryTest.MissingSetter {}

    @Test
    public void testHavingSettersGettersFromSeparateInterfacesIsValid() {
        PipelineOptionsFactory.as(PipelineOptionsFactoryTest.CombinedObject.class);
    }

    /**
     * A test interface that contains a non-bean style method.
     */
    public interface ExtraneousMethod extends PipelineOptions {
        String extraneousMethod(int value, String otherValue);
    }

    @Test
    public void testHavingExtraneousMethodThrows() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(("Methods [extraneousMethod(int, String)] on " + ("[org.apache.beam.sdk.options.PipelineOptionsFactoryTest$ExtraneousMethod] " + "do not conform to being bean properties.")));
        PipelineOptionsFactory.as(PipelineOptionsFactoryTest.ExtraneousMethod.class);
    }

    /**
     * A test interface that has a conflicting return type with its parent.
     */
    public interface ReturnTypeConflict extends PipelineOptionsFactoryTest.CombinedObject {
        @Override
        String getObject();

        void setObject(String value);
    }

    @Test
    public void testReturnTypeConflictThrows() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(("Method [getObject] has multiple definitions [public abstract java.lang.Object " + (((("org.apache.beam.sdk.options.PipelineOptionsFactoryTest$MissingSetter" + ".getObject(), public abstract java.lang.String ") + "org.apache.beam.sdk.options.PipelineOptionsFactoryTest$ReturnTypeConflict") + ".getObject()] with different return types for [") + "org.apache.beam.sdk.options.PipelineOptionsFactoryTest$ReturnTypeConflict].")));
        PipelineOptionsFactory.as(PipelineOptionsFactoryTest.ReturnTypeConflict.class);
    }

    /**
     * An interface to provide multiple methods with return type conflicts.
     */
    public interface MultiReturnTypeConflictBase extends PipelineOptionsFactoryTest.CombinedObject {
        Object getOther();

        void setOther(Object object);
    }

    /**
     * A test interface that has multiple conflicting return types with its parent.
     */
    public interface MultiReturnTypeConflict extends PipelineOptionsFactoryTest.MultiReturnTypeConflictBase {
        @Override
        String getObject();

        void setObject(String value);

        @Override
        Long getOther();

        void setOther(Long other);
    }

    @Test
    public void testMultipleReturnTypeConflictsThrows() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(("[org.apache.beam.sdk.options." + "PipelineOptionsFactoryTest$MultiReturnTypeConflict]"));
        expectedException.expectMessage("Methods with multiple definitions with different return types");
        expectedException.expectMessage("Method [getObject] has multiple definitions");
        expectedException.expectMessage(("public abstract java.lang.Object " + ("org.apache.beam.sdk.options.PipelineOptionsFactoryTest$" + "MissingSetter.getObject()")));
        expectedException.expectMessage(("public abstract java.lang.String org.apache.beam.sdk.options." + "PipelineOptionsFactoryTest$MultiReturnTypeConflict.getObject()"));
        expectedException.expectMessage("Method [getOther] has multiple definitions");
        expectedException.expectMessage(("public abstract java.lang.Object " + ("org.apache.beam.sdk.options.PipelineOptionsFactoryTest$" + "MultiReturnTypeConflictBase.getOther()")));
        expectedException.expectMessage(("public abstract java.lang.Long org.apache.beam.sdk.options." + "PipelineOptionsFactoryTest$MultiReturnTypeConflict.getOther()"));
        PipelineOptionsFactory.as(PipelineOptionsFactoryTest.MultiReturnTypeConflict.class);
    }

    /**
     * Test interface that has {@link JsonIgnore @JsonIgnore} on a setter for a property.
     */
    public interface SetterWithJsonIgnore extends PipelineOptions {
        String getValue();

        @JsonIgnore
        void setValue(String value);
    }

    @Test
    public void testSetterAnnotatedWithJsonIgnore() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(("Expected setter for property [value] to not be marked with @JsonIgnore on [" + "org.apache.beam.sdk.options.PipelineOptionsFactoryTest$SetterWithJsonIgnore]"));
        PipelineOptionsFactory.as(PipelineOptionsFactoryTest.SetterWithJsonIgnore.class);
    }

    /**
     * Test interface that has {@link JsonIgnore @JsonIgnore} on multiple setters.
     */
    public interface MultiSetterWithJsonIgnore extends PipelineOptionsFactoryTest.SetterWithJsonIgnore {
        Integer getOther();

        @JsonIgnore
        void setOther(Integer other);
    }

    @Test
    public void testMultipleSettersAnnotatedWithJsonIgnore() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Found setters marked with @JsonIgnore:");
        expectedException.expectMessage(("property [other] should not be marked with @JsonIgnore on [" + "org.apache.beam.sdk.options.PipelineOptionsFactoryTest$MultiSetterWithJsonIgnore]"));
        expectedException.expectMessage(("property [value] should not be marked with @JsonIgnore on [" + "org.apache.beam.sdk.options.PipelineOptionsFactoryTest$SetterWithJsonIgnore]"));
        PipelineOptionsFactory.as(PipelineOptionsFactoryTest.MultiSetterWithJsonIgnore.class);
    }

    /**
     * This class is has a conflicting field with {@link CombinedObject} that doesn't have {@link JsonIgnore @JsonIgnore}.
     */
    public interface GetterWithJsonIgnore extends PipelineOptions {
        @JsonIgnore
        Object getObject();

        void setObject(Object value);
    }

    /**
     * This class is has a conflicting {@link JsonIgnore @JsonIgnore} value with {@link GetterWithJsonIgnore}.
     */
    public interface GetterWithInconsistentJsonIgnoreValue extends PipelineOptions {
        @JsonIgnore(false)
        Object getObject();

        void setObject(Object value);
    }

    @Test
    public void testNotAllGettersAnnotatedWithJsonIgnore() throws Exception {
        // Initial construction is valid.
        PipelineOptionsFactoryTest.GetterWithJsonIgnore options = PipelineOptionsFactory.as(PipelineOptionsFactoryTest.GetterWithJsonIgnore.class);
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(("Expected getter for property [object] to be marked with @JsonIgnore on all [" + ((("org.apache.beam.sdk.options.PipelineOptionsFactoryTest$GetterWithJsonIgnore, " + "org.apache.beam.sdk.options.PipelineOptionsFactoryTest$MissingSetter], ") + "found only on [org.apache.beam.sdk.options.") + "PipelineOptionsFactoryTest$GetterWithJsonIgnore]")));
        // When we attempt to convert, we should error at this moment.
        as(PipelineOptionsFactoryTest.CombinedObject.class);
    }

    /**
     * Test interface.
     */
    public interface MultiGetters extends PipelineOptions {
        Object getObject();

        void setObject(Object value);

        @JsonIgnore
        Integer getOther();

        void setOther(Integer value);

        Void getConsistent();

        void setConsistent(Void consistent);
    }

    /**
     * Test interface.
     */
    public interface MultipleGettersWithInconsistentJsonIgnore extends PipelineOptions {
        @JsonIgnore
        Object getObject();

        void setObject(Object value);

        Integer getOther();

        void setOther(Integer value);

        Void getConsistent();

        void setConsistent(Void consistent);
    }

    @Test
    public void testMultipleGettersWithInconsistentJsonIgnore() {
        // Initial construction is valid.
        PipelineOptionsFactoryTest.MultiGetters options = PipelineOptionsFactory.as(PipelineOptionsFactoryTest.MultiGetters.class);
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Property getters are inconsistently marked with @JsonIgnore:");
        expectedException.expectMessage("property [object] to be marked on all");
        expectedException.expectMessage(("found only on [org.apache.beam.sdk.options." + "PipelineOptionsFactoryTest$MultiGetters]"));
        expectedException.expectMessage("property [other] to be marked on all");
        expectedException.expectMessage(("found only on [org.apache.beam.sdk.options." + "PipelineOptionsFactoryTest$MultipleGettersWithInconsistentJsonIgnore]"));
        expectedException.expectMessage(Matchers.anyOf(Matchers.containsString(java.util.Arrays.toString(new String[]{ "org.apache.beam.sdk.options." + "PipelineOptionsFactoryTest$MultipleGettersWithInconsistentJsonIgnore", "org.apache.beam.sdk.options.PipelineOptionsFactoryTest$MultiGetters" })), Matchers.containsString(java.util.Arrays.toString(new String[]{ "org.apache.beam.sdk.options.PipelineOptionsFactoryTest$MultiGetters", "org.apache.beam.sdk.options." + "PipelineOptionsFactoryTest$MultipleGettersWithInconsistentJsonIgnore" }))));
        expectedException.expectMessage(Matchers.not(Matchers.containsString("property [consistent]")));
        // When we attempt to convert, we should error immediately
        as(PipelineOptionsFactoryTest.MultipleGettersWithInconsistentJsonIgnore.class);
    }

    /**
     * Test interface that has {@link Default @Default} on a setter for a property.
     */
    public interface SetterWithDefault extends PipelineOptions {
        String getValue();

        @Default.String("abc")
        void setValue(String value);
    }

    @Test
    public void testSetterAnnotatedWithDefault() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(("Expected setter for property [value] to not be marked with @Default on [" + "org.apache.beam.sdk.options.PipelineOptionsFactoryTest$SetterWithDefault]"));
        PipelineOptionsFactory.as(PipelineOptionsFactoryTest.SetterWithDefault.class);
    }

    /**
     * Test interface that has {@link Default @Default} on multiple setters.
     */
    public interface MultiSetterWithDefault extends PipelineOptionsFactoryTest.SetterWithDefault {
        Integer getOther();

        @Default.String("abc")
        void setOther(Integer other);
    }

    @Test
    public void testMultipleSettersAnnotatedWithDefault() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Found setters marked with @Default:");
        expectedException.expectMessage(("property [other] should not be marked with @Default on [" + "org.apache.beam.sdk.options.PipelineOptionsFactoryTest$MultiSetterWithDefault]"));
        expectedException.expectMessage(("property [value] should not be marked with @Default on [" + "org.apache.beam.sdk.options.PipelineOptionsFactoryTest$SetterWithDefault]"));
        PipelineOptionsFactory.as(PipelineOptionsFactoryTest.MultiSetterWithDefault.class);
    }

    /**
     * This class is has a conflicting field with {@link CombinedObject} that doesn't have {@link Default @Default}.
     */
    public interface GetterWithDefault extends PipelineOptions {
        @Default.Integer(1)
        Object getObject();

        void setObject(Object value);
    }

    /**
     * This class is consistent with {@link GetterWithDefault} that has the same {@link Default @Default}.
     */
    public interface GetterWithConsistentDefault extends PipelineOptions {
        @Default.Integer(1)
        Object getObject();

        void setObject(Object value);
    }

    /**
     * This class is inconsistent with {@link GetterWithDefault} that has a different {@link Default @Default}.
     */
    public interface GetterWithInconsistentDefaultType extends PipelineOptions {
        @Default.String("abc")
        Object getObject();

        void setObject(Object value);
    }

    /**
     * This class is inconsistent with {@link GetterWithDefault} that has a different {@link Default @Default} value.
     */
    public interface GetterWithInconsistentDefaultValue extends PipelineOptions {
        @Default.Integer(0)
        Object getObject();

        void setObject(Object value);
    }

    @Test
    public void testNotAllGettersAnnotatedWithDefault() throws Exception {
        // Initial construction is valid.
        PipelineOptionsFactoryTest.GetterWithDefault options = PipelineOptionsFactory.as(PipelineOptionsFactoryTest.GetterWithDefault.class);
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(("Expected getter for property [object] to be marked with @Default on all [" + ((("org.apache.beam.sdk.options.PipelineOptionsFactoryTest$GetterWithDefault, " + "org.apache.beam.sdk.options.PipelineOptionsFactoryTest$MissingSetter], ") + "found only on [org.apache.beam.sdk.options.") + "PipelineOptionsFactoryTest$GetterWithDefault]")));
        // When we attempt to convert, we should error at this moment.
        as(PipelineOptionsFactoryTest.CombinedObject.class);
    }

    @Test
    public void testGettersAnnotatedWithConsistentDefault() throws Exception {
        PipelineOptionsFactoryTest.GetterWithConsistentDefault options = PipelineOptionsFactory.as(PipelineOptionsFactoryTest.GetterWithDefault.class).as(PipelineOptionsFactoryTest.GetterWithConsistentDefault.class);
        Assert.assertEquals(1, options.getObject());
    }

    @Test
    public void testGettersAnnotatedWithInconsistentDefault() throws Exception {
        // Initial construction is valid.
        PipelineOptionsFactoryTest.GetterWithDefault options = PipelineOptionsFactory.as(PipelineOptionsFactoryTest.GetterWithDefault.class);
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(("Property [object] is marked with contradictory annotations. Found [" + ((("[Default.Integer(value=1) on org.apache.beam.sdk.options.PipelineOptionsFactoryTest" + "$GetterWithDefault#getObject()], ") + "[Default.String(value=abc) on org.apache.beam.sdk.options.PipelineOptionsFactoryTest") + "$GetterWithInconsistentDefaultType#getObject()]].")));
        // When we attempt to convert, we should error at this moment.
        as(PipelineOptionsFactoryTest.GetterWithInconsistentDefaultType.class);
    }

    @Test
    public void testGettersAnnotatedWithInconsistentDefaultValue() throws Exception {
        // Initial construction is valid.
        PipelineOptionsFactoryTest.GetterWithDefault options = PipelineOptionsFactory.as(PipelineOptionsFactoryTest.GetterWithDefault.class);
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(("Property [object] is marked with contradictory annotations. Found [" + ((("[Default.Integer(value=1) on org.apache.beam.sdk.options.PipelineOptionsFactoryTest" + "$GetterWithDefault#getObject()], ") + "[Default.Integer(value=0) on org.apache.beam.sdk.options.PipelineOptionsFactoryTest") + "$GetterWithInconsistentDefaultValue#getObject()]].")));
        // When we attempt to convert, we should error at this moment.
        as(PipelineOptionsFactoryTest.GetterWithInconsistentDefaultValue.class);
    }

    @Test
    public void testGettersAnnotatedWithInconsistentJsonIgnoreValue() throws Exception {
        // Initial construction is valid.
        PipelineOptionsFactoryTest.GetterWithJsonIgnore options = PipelineOptionsFactory.as(PipelineOptionsFactoryTest.GetterWithJsonIgnore.class);
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(("Property [object] is marked with contradictory annotations. Found [" + ((("[JsonIgnore(value=false) on org.apache.beam.sdk.options.PipelineOptionsFactoryTest" + "$GetterWithInconsistentJsonIgnoreValue#getObject()], ") + "[JsonIgnore(value=true) on org.apache.beam.sdk.options.PipelineOptionsFactoryTest") + "$GetterWithJsonIgnore#getObject()]].")));
        // When we attempt to convert, we should error at this moment.
        as(PipelineOptionsFactoryTest.GetterWithInconsistentJsonIgnoreValue.class);
    }

    /**
     * Test interface.
     */
    public interface GettersWithMultipleDefault extends PipelineOptions {
        @Default.String("abc")
        @Default.Integer(0)
        Object getObject();

        void setObject(Object value);
    }

    @Test
    public void testGettersWithMultipleDefaults() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(("Property [object] is marked with contradictory annotations. Found [" + ((("[Default.String(value=abc) on org.apache.beam.sdk.options.PipelineOptionsFactoryTest" + "$GettersWithMultipleDefault#getObject()], ") + "[Default.Integer(value=0) on org.apache.beam.sdk.options.PipelineOptionsFactoryTest") + "$GettersWithMultipleDefault#getObject()]].")));
        // When we attempt to create, we should error at this moment.
        PipelineOptionsFactory.as(PipelineOptionsFactoryTest.GettersWithMultipleDefault.class);
    }

    /**
     * Test interface.
     */
    public interface MultiGettersWithDefault extends PipelineOptions {
        Object getObject();

        void setObject(Object value);

        @Default.Integer(1)
        Integer getOther();

        void setOther(Integer value);

        Void getConsistent();

        void setConsistent(Void consistent);
    }

    /**
     * Test interface.
     */
    public interface MultipleGettersWithInconsistentDefault extends PipelineOptions {
        @Default.Boolean(true)
        Object getObject();

        void setObject(Object value);

        Integer getOther();

        void setOther(Integer value);

        Void getConsistent();

        void setConsistent(Void consistent);
    }

    @Test
    public void testMultipleGettersWithInconsistentDefault() {
        // Initial construction is valid.
        PipelineOptionsFactoryTest.MultiGettersWithDefault options = PipelineOptionsFactory.as(PipelineOptionsFactoryTest.MultiGettersWithDefault.class);
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Property getters are inconsistently marked with @Default:");
        expectedException.expectMessage("property [object] to be marked on all");
        expectedException.expectMessage(("found only on [org.apache.beam.sdk.options." + "PipelineOptionsFactoryTest$MultiGettersWithDefault]"));
        expectedException.expectMessage("property [other] to be marked on all");
        expectedException.expectMessage(("found only on [org.apache.beam.sdk.options." + "PipelineOptionsFactoryTest$MultipleGettersWithInconsistentDefault]"));
        expectedException.expectMessage(Matchers.anyOf(Matchers.containsString(java.util.Arrays.toString(new String[]{ "org.apache.beam.sdk.options." + "PipelineOptionsFactoryTest$MultipleGettersWithInconsistentDefault", "org.apache.beam.sdk.options.PipelineOptionsFactoryTest$MultiGettersWithDefault" })), Matchers.containsString(java.util.Arrays.toString(new String[]{ "org.apache.beam.sdk.options.PipelineOptionsFactoryTest$MultiGettersWithDefault", "org.apache.beam.sdk.options." + "PipelineOptionsFactoryTest$MultipleGettersWithInconsistentDefault" }))));
        expectedException.expectMessage(Matchers.not(Matchers.containsString("property [consistent]")));
        // When we attempt to convert, we should error immediately
        as(PipelineOptionsFactoryTest.MultipleGettersWithInconsistentDefault.class);
    }

    @Test
    public void testAppNameIsNotOverriddenWhenPassedInViaCommandLine() {
        ApplicationNameOptions options = PipelineOptionsFactory.fromArgs("--appName=testAppName").as(ApplicationNameOptions.class);
        Assert.assertEquals("testAppName", options.getAppName());
    }

    @Test
    public void testPropertyIsSetOnRegisteredPipelineOptionNotPartOfOriginalInterface() {
        PipelineOptions options = PipelineOptionsFactory.fromArgs("--streaming").create();
        Assert.assertTrue(options.as(StreamingOptions.class).isStreaming());
    }

    /**
     * A test interface containing all the primitives.
     */
    public interface Primitives extends PipelineOptions {
        boolean getBoolean();

        void setBoolean(boolean value);

        char getChar();

        void setChar(char value);

        byte getByte();

        void setByte(byte value);

        short getShort();

        void setShort(short value);

        int getInt();

        void setInt(int value);

        long getLong();

        void setLong(long value);

        float getFloat();

        void setFloat(float value);

        double getDouble();

        void setDouble(double value);
    }

    @Test
    public void testPrimitives() {
        String[] args = new String[]{ "--boolean=true", "--char=d", "--byte=12", "--short=300", "--int=100000", "--long=123890123890", "--float=55.5", "--double=12.3" };
        PipelineOptionsFactoryTest.Primitives options = PipelineOptionsFactory.fromArgs(args).as(PipelineOptionsFactoryTest.Primitives.class);
        Assert.assertTrue(options.getBoolean());
        Assert.assertEquals('d', options.getChar());
        Assert.assertEquals(((byte) (12)), options.getByte());
        Assert.assertEquals(((short) (300)), options.getShort());
        Assert.assertEquals(100000, options.getInt());
        Assert.assertEquals(123890123890L, options.getLong());
        Assert.assertEquals(55.5F, options.getFloat(), 0.0F);
        Assert.assertEquals(12.3, options.getDouble(), 0.0);
    }

    @Test
    public void testBooleanShorthandArgument() {
        String[] args = new String[]{ "--boolean" };
        PipelineOptionsFactoryTest.Primitives options = PipelineOptionsFactory.fromArgs(args).as(PipelineOptionsFactoryTest.Primitives.class);
        Assert.assertTrue(options.getBoolean());
    }

    @Test
    public void testEmptyValueNotAllowed() {
        String[] args = new String[]{ "--byte=" };
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(emptyStringErrorMessage());
        as(PipelineOptionsFactoryTest.Primitives.class);
    }

    /**
     * Enum used for testing PipelineOptions CLI parsing.
     */
    public enum TestEnum {

        Value,
        Value2;}

    /**
     * A test interface containing all supported objects.
     */
    public interface Objects extends PipelineOptions {
        Boolean getBoolean();

        void setBoolean(Boolean value);

        Character getChar();

        void setChar(Character value);

        Byte getByte();

        void setByte(Byte value);

        Short getShort();

        void setShort(Short value);

        Integer getInt();

        void setInt(Integer value);

        Long getLong();

        void setLong(Long value);

        Float getFloat();

        void setFloat(Float value);

        Double getDouble();

        void setDouble(Double value);

        String getString();

        void setString(String value);

        String getEmptyString();

        void setEmptyString(String value);

        Class<?> getClassValue();

        void setClassValue(Class<?> value);

        PipelineOptionsFactoryTest.TestEnum getEnum();

        void setEnum(PipelineOptionsFactoryTest.TestEnum value);

        ValueProvider<String> getStringValue();

        void setStringValue(ValueProvider<String> value);

        ValueProvider<Long> getLongValue();

        void setLongValue(ValueProvider<Long> value);

        ValueProvider<PipelineOptionsFactoryTest.TestEnum> getEnumValue();

        void setEnumValue(ValueProvider<PipelineOptionsFactoryTest.TestEnum> value);
    }

    @Test
    public void testObjects() {
        String[] args = new String[]{ "--boolean=true", "--char=d", "--byte=12", "--short=300", "--int=100000", "--long=123890123890", "--float=55.5", "--double=12.3", "--string=stringValue", "--emptyString=", "--classValue=" + (PipelineOptionsFactoryTest.class.getName()), "--enum=" + (PipelineOptionsFactoryTest.TestEnum.Value), "--stringValue=beam", "--longValue=12389049585840", "--enumValue=" + (PipelineOptionsFactoryTest.TestEnum.Value) };
        PipelineOptionsFactoryTest.Objects options = PipelineOptionsFactory.fromArgs(args).as(PipelineOptionsFactoryTest.Objects.class);
        Assert.assertTrue(options.getBoolean());
        Assert.assertEquals(Character.valueOf('d'), options.getChar());
        Assert.assertEquals(Byte.valueOf(((byte) (12))), options.getByte());
        Assert.assertEquals(Short.valueOf(((short) (300))), options.getShort());
        Assert.assertEquals(Integer.valueOf(100000), options.getInt());
        Assert.assertEquals(Long.valueOf(123890123890L), options.getLong());
        Assert.assertEquals(55.5F, options.getFloat(), 0.0F);
        Assert.assertEquals(12.3, options.getDouble(), 0.0);
        Assert.assertEquals("stringValue", options.getString());
        Assert.assertTrue(options.getEmptyString().isEmpty());
        Assert.assertEquals(PipelineOptionsFactoryTest.class, options.getClassValue());
        Assert.assertEquals(PipelineOptionsFactoryTest.TestEnum.Value, options.getEnum());
        Assert.assertEquals("beam", options.getStringValue().get());
        Assert.assertEquals(Long.valueOf(12389049585840L), options.getLongValue().get());
        Assert.assertEquals(PipelineOptionsFactoryTest.TestEnum.Value, options.getEnumValue().get());
    }

    @Test
    public void testStringValueProvider() {
        String[] args = new String[]{ "--stringValue=beam" };
        String[] emptyArgs = new String[]{ "--stringValue=" };
        PipelineOptionsFactoryTest.Objects options = PipelineOptionsFactory.fromArgs(args).as(PipelineOptionsFactoryTest.Objects.class);
        Assert.assertEquals("beam", options.getStringValue().get());
        options = PipelineOptionsFactory.fromArgs(emptyArgs).as(PipelineOptionsFactoryTest.Objects.class);
        Assert.assertEquals("", options.getStringValue().get());
    }

    @Test
    public void testLongValueProvider() {
        String[] args = new String[]{ "--longValue=12345678762" };
        String[] emptyArgs = new String[]{ "--longValue=" };
        PipelineOptionsFactoryTest.Objects options = PipelineOptionsFactory.fromArgs(args).as(PipelineOptionsFactoryTest.Objects.class);
        Assert.assertEquals(Long.valueOf(12345678762L), options.getLongValue().get());
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(emptyStringErrorMessage());
        as(PipelineOptionsFactoryTest.Objects.class);
    }

    @Test
    public void testEnumValueProvider() {
        String[] args = new String[]{ "--enumValue=" + (PipelineOptionsFactoryTest.TestEnum.Value) };
        String[] emptyArgs = new String[]{ "--enumValue=" };
        PipelineOptionsFactoryTest.Objects options = PipelineOptionsFactory.fromArgs(args).as(PipelineOptionsFactoryTest.Objects.class);
        Assert.assertEquals(PipelineOptionsFactoryTest.TestEnum.Value, options.getEnumValue().get());
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(emptyStringErrorMessage());
        as(PipelineOptionsFactoryTest.Objects.class);
    }

    /**
     * A test class for verifying JSON -> Object conversion.
     */
    public static class ComplexType {
        String value;

        String value2;

        public ComplexType(@JsonProperty("key")
        String value, @JsonProperty("key2")
        String value2) {
            this.value = value;
            this.value2 = value2;
        }
    }

    /**
     * A test interface for verifying JSON -> complex type conversion.
     */
    public interface ComplexTypes extends PipelineOptions {
        Map<String, String> getMap();

        void setMap(Map<String, String> value);

        PipelineOptionsFactoryTest.ComplexType getObject();

        void setObject(PipelineOptionsFactoryTest.ComplexType value);

        ValueProvider<PipelineOptionsFactoryTest.ComplexType> getObjectValue();

        void setObjectValue(ValueProvider<PipelineOptionsFactoryTest.ComplexType> value);
    }

    @Test
    public void testComplexTypes() {
        String[] args = new String[]{ "--map={\"key\":\"value\",\"key2\":\"value2\"}", "--object={\"key\":\"value\",\"key2\":\"value2\"}", "--objectValue={\"key\":\"value\",\"key2\":\"value2\"}" };
        PipelineOptionsFactoryTest.ComplexTypes options = PipelineOptionsFactory.fromArgs(args).as(PipelineOptionsFactoryTest.ComplexTypes.class);
        Assert.assertEquals(ImmutableMap.of("key", "value", "key2", "value2"), options.getMap());
        Assert.assertEquals("value", options.getObject().value);
        Assert.assertEquals("value2", options.getObject().value2);
        Assert.assertEquals("value", options.getObjectValue().get().value);
        Assert.assertEquals("value2", options.getObjectValue().get().value2);
    }

    @Test
    public void testMissingArgument() {
        String[] args = new String[]{  };
        PipelineOptionsFactoryTest.Objects options = PipelineOptionsFactory.fromArgs(args).as(PipelineOptionsFactoryTest.Objects.class);
        Assert.assertNull(options.getString());
    }

    /**
     * A test interface containing all supported array return types.
     */
    public interface Arrays extends PipelineOptions {
        boolean[] getBoolean();

        void setBoolean(boolean[] value);

        char[] getChar();

        void setChar(char[] value);

        short[] getShort();

        void setShort(short[] value);

        int[] getInt();

        void setInt(int[] value);

        long[] getLong();

        void setLong(long[] value);

        float[] getFloat();

        void setFloat(float[] value);

        double[] getDouble();

        void setDouble(double[] value);

        String[] getString();

        void setString(String[] value);

        Class<?>[] getClassValue();

        void setClassValue(Class<?>[] value);

        PipelineOptionsFactoryTest.TestEnum[] getEnum();

        void setEnum(PipelineOptionsFactoryTest.TestEnum[] value);

        ValueProvider<String[]> getStringValue();

        void setStringValue(ValueProvider<String[]> value);

        ValueProvider<Long[]> getLongValue();

        void setLongValue(ValueProvider<Long[]> value);

        ValueProvider<PipelineOptionsFactoryTest.TestEnum[]> getEnumValue();

        void setEnumValue(ValueProvider<PipelineOptionsFactoryTest.TestEnum[]> value);
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void testArrays() {
        String[] args = new String[]{ "--boolean=true", "--boolean=true", "--boolean=false", "--char=d", "--char=e", "--char=f", "--short=300", "--short=301", "--short=302", "--int=100000", "--int=100001", "--int=100002", "--long=123890123890", "--long=123890123891", "--long=123890123892", "--float=55.5", "--float=55.6", "--float=55.7", "--double=12.3", "--double=12.4", "--double=12.5", "--string=stringValue1", "--string=stringValue2", "--string=stringValue3", "--classValue=" + (PipelineOptionsFactory.class.getName()), "--classValue=" + (PipelineOptionsFactoryTest.class.getName()), "--enum=" + (PipelineOptionsFactoryTest.TestEnum.Value), "--enum=" + (PipelineOptionsFactoryTest.TestEnum.Value2), "--stringValue=abc", "--stringValue=beam", "--longValue=123890123890", "--longValue=123890123891", "--enumValue=" + (PipelineOptionsFactoryTest.TestEnum.Value), "--enumValue=" + (PipelineOptionsFactoryTest.TestEnum.Value2) };
        PipelineOptionsFactoryTest.Arrays options = PipelineOptionsFactory.fromArgs(args).as(PipelineOptionsFactoryTest.Arrays.class);
        boolean[] bools = options.getBoolean();
        Assert.assertTrue((((bools[0]) && (bools[1])) && (!(bools[2]))));
        Assert.assertArrayEquals(new char[]{ 'd', 'e', 'f' }, options.getChar());
        Assert.assertArrayEquals(new short[]{ 300, 301, 302 }, options.getShort());
        Assert.assertArrayEquals(new int[]{ 100000, 100001, 100002 }, options.getInt());
        Assert.assertArrayEquals(new long[]{ 123890123890L, 123890123891L, 123890123892L }, options.getLong());
        Assert.assertArrayEquals(new float[]{ 55.5F, 55.6F, 55.7F }, options.getFloat(), 0.0F);
        Assert.assertArrayEquals(new double[]{ 12.3, 12.4, 12.5 }, options.getDouble(), 0.0);
        Assert.assertArrayEquals(new String[]{ "stringValue1", "stringValue2", "stringValue3" }, options.getString());
        Assert.assertArrayEquals(new Class[]{ PipelineOptionsFactory.class, PipelineOptionsFactoryTest.class }, options.getClassValue());
        Assert.assertArrayEquals(new PipelineOptionsFactoryTest.TestEnum[]{ PipelineOptionsFactoryTest.TestEnum.Value, PipelineOptionsFactoryTest.TestEnum.Value2 }, options.getEnum());
        Assert.assertArrayEquals(new String[]{ "abc", "beam" }, options.getStringValue().get());
        Assert.assertArrayEquals(new Long[]{ 123890123890L, 123890123891L }, options.getLongValue().get());
        Assert.assertArrayEquals(new PipelineOptionsFactoryTest.TestEnum[]{ PipelineOptionsFactoryTest.TestEnum.Value, PipelineOptionsFactoryTest.TestEnum.Value2 }, options.getEnumValue().get());
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void testEmptyInStringArrays() {
        String[] args = new String[]{ "--string=", "--string=", "--string=" };
        PipelineOptionsFactoryTest.Arrays options = PipelineOptionsFactory.fromArgs(args).as(PipelineOptionsFactoryTest.Arrays.class);
        Assert.assertArrayEquals(new String[]{ "", "", "" }, options.getString());
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void testEmptyInStringArraysWithCommaList() {
        String[] args = new String[]{ "--string=a,,b" };
        PipelineOptionsFactoryTest.Arrays options = PipelineOptionsFactory.fromArgs(args).as(PipelineOptionsFactoryTest.Arrays.class);
        Assert.assertArrayEquals(new String[]{ "a", "", "b" }, options.getString());
    }

    @Test
    public void testEmptyInNonStringArrays() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(emptyStringErrorMessage());
        String[] args = new String[]{ "--boolean=true", "--boolean=", "--boolean=false" };
        as(PipelineOptionsFactoryTest.Arrays.class);
    }

    @Test
    public void testEmptyInNonStringArraysWithCommaList() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(emptyStringErrorMessage());
        String[] args = new String[]{ "--int=1,,9" };
        as(PipelineOptionsFactoryTest.Arrays.class);
    }

    @Test
    public void testStringArrayValueProvider() {
        String[] args = new String[]{ "--stringValue=abc", "--stringValue=xyz" };
        String[] commaArgs = new String[]{ "--stringValue=abc,xyz" };
        String[] emptyArgs = new String[]{ "--stringValue=", "--stringValue=" };
        PipelineOptionsFactoryTest.Arrays options = PipelineOptionsFactory.fromArgs(args).as(PipelineOptionsFactoryTest.Arrays.class);
        Assert.assertArrayEquals(new String[]{ "abc", "xyz" }, options.getStringValue().get());
        options = PipelineOptionsFactory.fromArgs(commaArgs).as(PipelineOptionsFactoryTest.Arrays.class);
        Assert.assertArrayEquals(new String[]{ "abc", "xyz" }, options.getStringValue().get());
        options = PipelineOptionsFactory.fromArgs(emptyArgs).as(PipelineOptionsFactoryTest.Arrays.class);
        Assert.assertArrayEquals(new String[]{ "", "" }, options.getStringValue().get());
    }

    @Test
    public void testLongArrayValueProvider() {
        String[] args = new String[]{ "--longValue=12345678762", "--longValue=12345678763" };
        String[] commaArgs = new String[]{ "--longValue=12345678762,12345678763" };
        String[] emptyArgs = new String[]{ "--longValue=", "--longValue=" };
        PipelineOptionsFactoryTest.Arrays options = PipelineOptionsFactory.fromArgs(args).as(PipelineOptionsFactoryTest.Arrays.class);
        Assert.assertArrayEquals(new Long[]{ 12345678762L, 12345678763L }, options.getLongValue().get());
        options = PipelineOptionsFactory.fromArgs(commaArgs).as(PipelineOptionsFactoryTest.Arrays.class);
        Assert.assertArrayEquals(new Long[]{ 12345678762L, 12345678763L }, options.getLongValue().get());
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(emptyStringErrorMessage());
        as(PipelineOptionsFactoryTest.Arrays.class);
    }

    @Test
    public void testEnumArrayValueProvider() {
        String[] args = new String[]{ "--enumValue=" + (PipelineOptionsFactoryTest.TestEnum.Value), "--enumValue=" + (PipelineOptionsFactoryTest.TestEnum.Value2) };
        String[] commaArgs = new String[]{ (("--enumValue=" + (PipelineOptionsFactoryTest.TestEnum.Value)) + ",") + (PipelineOptionsFactoryTest.TestEnum.Value2) };
        String[] emptyArgs = new String[]{ "--enumValue=" };
        PipelineOptionsFactoryTest.Arrays options = PipelineOptionsFactory.fromArgs(args).as(PipelineOptionsFactoryTest.Arrays.class);
        Assert.assertArrayEquals(new PipelineOptionsFactoryTest.TestEnum[]{ PipelineOptionsFactoryTest.TestEnum.Value, PipelineOptionsFactoryTest.TestEnum.Value2 }, options.getEnumValue().get());
        options = PipelineOptionsFactory.fromArgs(commaArgs).as(PipelineOptionsFactoryTest.Arrays.class);
        Assert.assertArrayEquals(new PipelineOptionsFactoryTest.TestEnum[]{ PipelineOptionsFactoryTest.TestEnum.Value, PipelineOptionsFactoryTest.TestEnum.Value2 }, options.getEnumValue().get());
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(emptyStringErrorMessage());
        as(PipelineOptionsFactoryTest.Arrays.class);
    }

    @Test
    public void testOutOfOrderArrays() {
        String[] args = new String[]{ "--char=d", "--boolean=true", "--boolean=true", "--char=e", "--char=f", "--boolean=false" };
        PipelineOptionsFactoryTest.Arrays options = PipelineOptionsFactory.fromArgs(args).as(PipelineOptionsFactoryTest.Arrays.class);
        boolean[] bools = options.getBoolean();
        Assert.assertTrue((((bools[0]) && (bools[1])) && (!(bools[2]))));
        Assert.assertArrayEquals(new char[]{ 'd', 'e', 'f' }, options.getChar());
    }

    /**
     * A test interface containing all supported List return types.
     */
    public interface Lists extends PipelineOptions {
        List<String> getString();

        void setString(List<String> value);

        List<Integer> getInteger();

        void setInteger(List<Integer> value);

        @SuppressWarnings("rawtypes")
        List getList();

        @SuppressWarnings("rawtypes")
        void setList(List value);

        ValueProvider<List<String>> getStringValue();

        void setStringValue(ValueProvider<List<String>> value);

        ValueProvider<List<Long>> getLongValue();

        void setLongValue(ValueProvider<List<Long>> value);

        ValueProvider<List<PipelineOptionsFactoryTest.TestEnum>> getEnumValue();

        void setEnumValue(ValueProvider<List<PipelineOptionsFactoryTest.TestEnum>> value);
    }

    @Test
    public void testListRawDefaultsToString() {
        String[] manyArgs = new String[]{ "--list=stringValue1", "--list=stringValue2", "--list=stringValue3" };
        String[] manyArgsWithEmptyString = new String[]{ "--list=stringValue1", "--list=", "--list=stringValue3" };
        PipelineOptionsFactoryTest.Lists options = PipelineOptionsFactory.fromArgs(manyArgs).as(PipelineOptionsFactoryTest.Lists.class);
        Assert.assertEquals(ImmutableList.of("stringValue1", "stringValue2", "stringValue3"), options.getList());
        options = PipelineOptionsFactory.fromArgs(manyArgsWithEmptyString).as(PipelineOptionsFactoryTest.Lists.class);
        Assert.assertEquals(ImmutableList.of("stringValue1", "", "stringValue3"), options.getList());
    }

    @Test
    public void testListString() {
        String[] manyArgs = new String[]{ "--string=stringValue1", "--string=stringValue2", "--string=stringValue3" };
        String[] oneArg = new String[]{ "--string=stringValue1" };
        String[] emptyArg = new String[]{ "--string=" };
        PipelineOptionsFactoryTest.Lists options = PipelineOptionsFactory.fromArgs(manyArgs).as(PipelineOptionsFactoryTest.Lists.class);
        Assert.assertEquals(ImmutableList.of("stringValue1", "stringValue2", "stringValue3"), options.getString());
        options = PipelineOptionsFactory.fromArgs(oneArg).as(PipelineOptionsFactoryTest.Lists.class);
        Assert.assertEquals(ImmutableList.of("stringValue1"), options.getString());
        options = PipelineOptionsFactory.fromArgs(emptyArg).as(PipelineOptionsFactoryTest.Lists.class);
        Assert.assertEquals(ImmutableList.of(""), options.getString());
    }

    @Test
    public void testListInt() {
        String[] manyArgs = new String[]{ "--integer=1", "--integer=2", "--integer=3" };
        String[] manyArgsShort = new String[]{ "--integer=1,2,3" };
        String[] oneArg = new String[]{ "--integer=1" };
        String[] missingArg = new String[]{ "--integer=" };
        PipelineOptionsFactoryTest.Lists options = PipelineOptionsFactory.fromArgs(manyArgs).as(PipelineOptionsFactoryTest.Lists.class);
        Assert.assertEquals(ImmutableList.of(1, 2, 3), options.getInteger());
        options = PipelineOptionsFactory.fromArgs(manyArgsShort).as(PipelineOptionsFactoryTest.Lists.class);
        Assert.assertEquals(ImmutableList.of(1, 2, 3), options.getInteger());
        options = PipelineOptionsFactory.fromArgs(oneArg).as(PipelineOptionsFactoryTest.Lists.class);
        Assert.assertEquals(ImmutableList.of(1), options.getInteger());
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(emptyStringErrorMessage("java.util.List<java.lang.Integer>"));
        as(PipelineOptionsFactoryTest.Lists.class);
    }

    @Test
    public void testListShorthand() {
        String[] args = new String[]{ "--string=stringValue1,stringValue2,stringValue3" };
        PipelineOptionsFactoryTest.Lists options = PipelineOptionsFactory.fromArgs(args).as(PipelineOptionsFactoryTest.Lists.class);
        Assert.assertEquals(ImmutableList.of("stringValue1", "stringValue2", "stringValue3"), options.getString());
    }

    @Test
    public void testMixedShorthandAndLongStyleList() {
        String[] args = new String[]{ "--char=d", "--char=e", "--char=f", "--char=g,h,i", "--char=j", "--char=k", "--char=l", "--char=m,n,o" };
        PipelineOptionsFactoryTest.Arrays options = PipelineOptionsFactory.fromArgs(args).as(PipelineOptionsFactoryTest.Arrays.class);
        Assert.assertArrayEquals(new char[]{ 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o' }, options.getChar());
    }

    @Test
    public void testStringListValueProvider() {
        String[] args = new String[]{ "--stringValue=abc", "--stringValue=xyz" };
        String[] commaArgs = new String[]{ "--stringValue=abc,xyz" };
        String[] emptyArgs = new String[]{ "--stringValue=", "--stringValue=" };
        PipelineOptionsFactoryTest.Lists options = PipelineOptionsFactory.fromArgs(args).as(PipelineOptionsFactoryTest.Lists.class);
        Assert.assertEquals(ImmutableList.of("abc", "xyz"), options.getStringValue().get());
        options = PipelineOptionsFactory.fromArgs(commaArgs).as(PipelineOptionsFactoryTest.Lists.class);
        Assert.assertEquals(ImmutableList.of("abc", "xyz"), options.getStringValue().get());
        options = PipelineOptionsFactory.fromArgs(emptyArgs).as(PipelineOptionsFactoryTest.Lists.class);
        Assert.assertEquals(ImmutableList.of("", ""), options.getStringValue().get());
    }

    @Test
    public void testLongListValueProvider() {
        String[] args = new String[]{ "--longValue=12345678762", "--longValue=12345678763" };
        String[] commaArgs = new String[]{ "--longValue=12345678762,12345678763" };
        String[] emptyArgs = new String[]{ "--longValue=", "--longValue=" };
        PipelineOptionsFactoryTest.Lists options = PipelineOptionsFactory.fromArgs(args).as(PipelineOptionsFactoryTest.Lists.class);
        Assert.assertEquals(ImmutableList.of(12345678762L, 12345678763L), options.getLongValue().get());
        options = PipelineOptionsFactory.fromArgs(commaArgs).as(PipelineOptionsFactoryTest.Lists.class);
        Assert.assertEquals(ImmutableList.of(12345678762L, 12345678763L), options.getLongValue().get());
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(emptyStringErrorMessage());
        as(PipelineOptionsFactoryTest.Lists.class);
    }

    @Test
    public void testEnumListValueProvider() {
        String[] args = new String[]{ "--enumValue=" + (PipelineOptionsFactoryTest.TestEnum.Value), "--enumValue=" + (PipelineOptionsFactoryTest.TestEnum.Value2) };
        String[] commaArgs = new String[]{ (("--enumValue=" + (PipelineOptionsFactoryTest.TestEnum.Value)) + ",") + (PipelineOptionsFactoryTest.TestEnum.Value2) };
        String[] emptyArgs = new String[]{ "--enumValue=" };
        PipelineOptionsFactoryTest.Lists options = PipelineOptionsFactory.fromArgs(args).as(PipelineOptionsFactoryTest.Lists.class);
        Assert.assertEquals(ImmutableList.of(PipelineOptionsFactoryTest.TestEnum.Value, PipelineOptionsFactoryTest.TestEnum.Value2), options.getEnumValue().get());
        options = PipelineOptionsFactory.fromArgs(commaArgs).as(PipelineOptionsFactoryTest.Lists.class);
        Assert.assertEquals(ImmutableList.of(PipelineOptionsFactoryTest.TestEnum.Value, PipelineOptionsFactoryTest.TestEnum.Value2), options.getEnumValue().get());
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(emptyStringErrorMessage());
        as(PipelineOptionsFactoryTest.Lists.class);
    }

    @Test
    public void testSetASingularAttributeUsingAListThrowsAnError() {
        String[] args = new String[]{ "--string=100", "--string=200" };
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("expected one element but was");
        as(PipelineOptionsFactoryTest.Objects.class);
    }

    @Test
    public void testSetASingularAttributeUsingAListIsIgnoredWithoutStrictParsing() {
        String[] args = new String[]{ "--diskSizeGb=100", "--diskSizeGb=200" };
        PipelineOptionsFactory.fromArgs(args).withoutStrictParsing().create();
        expectedLogs.verifyWarn("Strict parsing is disabled, ignoring option");
    }

    private interface NonPublicPipelineOptions extends PipelineOptions {}

    @Test
    public void testNonPublicInterfaceLogsWarning() throws Exception {
        PipelineOptionsFactory.as(PipelineOptionsFactoryTest.NonPublicPipelineOptions.class);
        // Make sure we print the name of the class.
        expectedLogs.verifyWarn(PipelineOptionsFactoryTest.NonPublicPipelineOptions.class.getName());
        expectedLogs.verifyWarn("all non-public interfaces to be in the same package");
    }

    /**
     * A test interface containing all supported List return types.
     */
    public interface Maps extends PipelineOptions {
        Map<Integer, Integer> getMap();

        void setMap(Map<Integer, Integer> value);

        Map<Integer, Map<Integer, Integer>> getNestedMap();

        void setNestedMap(Map<Integer, Map<Integer, Integer>> value);
    }

    @Test
    public void testMapIntInt() {
        String[] manyArgsShort = new String[]{ "--map={\"1\":1,\"2\":2}" };
        String[] oneArg = new String[]{ "--map={\"1\":1}" };
        String[] missingArg = new String[]{ "--map=" };
        PipelineOptionsFactoryTest.Maps options = PipelineOptionsFactory.fromArgs(manyArgsShort).as(PipelineOptionsFactoryTest.Maps.class);
        Assert.assertEquals(ImmutableMap.of(1, 1, 2, 2), options.getMap());
        options = PipelineOptionsFactory.fromArgs(oneArg).as(PipelineOptionsFactoryTest.Maps.class);
        Assert.assertEquals(ImmutableMap.of(1, 1), options.getMap());
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(emptyStringErrorMessage("java.util.Map<java.lang.Integer, java.lang.Integer>"));
        as(PipelineOptionsFactoryTest.Maps.class);
    }

    @Test
    public void testNestedMap() {
        String[] manyArgsShort = new String[]{ "--nestedMap={\"1\":{\"1\":1},\"2\":{\"2\":2}}" };
        String[] oneArg = new String[]{ "--nestedMap={\"1\":{\"1\":1}}" };
        String[] missingArg = new String[]{ "--nestedMap=" };
        PipelineOptionsFactoryTest.Maps options = PipelineOptionsFactory.fromArgs(manyArgsShort).as(PipelineOptionsFactoryTest.Maps.class);
        Assert.assertEquals(ImmutableMap.of(1, ImmutableMap.of(1, 1), 2, ImmutableMap.of(2, 2)), options.getNestedMap());
        options = PipelineOptionsFactory.fromArgs(oneArg).as(PipelineOptionsFactoryTest.Maps.class);
        Assert.assertEquals(ImmutableMap.of(1, ImmutableMap.of(1, 1)), options.getNestedMap());
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(emptyStringErrorMessage("java.util.Map<java.lang.Integer, java.util.Map<java.lang.Integer, java.lang.Integer>>"));
        as(PipelineOptionsFactoryTest.Maps.class);
    }

    @Test
    public void testSettingRunner() {
        String[] args = new String[]{ "--runner=" + (PipelineOptionsFactoryTest.RegisteredTestRunner.class.getSimpleName()) };
        PipelineOptions options = PipelineOptionsFactory.fromArgs(args).create();
        Assert.assertEquals(PipelineOptionsFactoryTest.RegisteredTestRunner.class, options.getRunner());
    }

    @Test
    public void testSettingRunnerFullName() {
        String[] args = new String[]{ String.format("--runner=%s", CrashingRunner.class.getName()) };
        PipelineOptions opts = PipelineOptionsFactory.fromArgs(args).create();
        Assert.assertEquals(opts.getRunner(), CrashingRunner.class);
    }

    @Test
    public void testSettingUnknownRunner() {
        String[] args = new String[]{ "--runner=UnknownRunner" };
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(("Unknown 'runner' specified 'UnknownRunner', supported " + "pipeline runners"));
        Set<String> registeredRunners = PipelineOptionsFactory.getRegisteredRunners().keySet();
        Assert.assertThat(registeredRunners, Matchers.hasItem(PipelineOptionsFactoryTest.REGISTERED_RUNNER.getSimpleName().toLowerCase()));
        expectedException.expectMessage(CACHE.get().getSupportedRunners().toString());
        PipelineOptionsFactory.fromArgs(args).create();
    }

    private static class ExampleTestRunner extends PipelineRunner<PipelineResult> {
        @Override
        public PipelineResult run(Pipeline pipeline) {
            return null;
        }
    }

    @Test
    public void testSettingRunnerCanonicalClassNameNotInSupportedExists() {
        String[] args = new String[]{ String.format("--runner=%s", PipelineOptionsFactoryTest.ExampleTestRunner.class.getName()) };
        PipelineOptions opts = PipelineOptionsFactory.fromArgs(args).create();
        Assert.assertEquals(opts.getRunner(), PipelineOptionsFactoryTest.ExampleTestRunner.class);
    }

    @Test
    public void testSettingRunnerCanonicalClassNameNotInSupportedNotPipelineRunner() {
        String[] args = new String[]{ "--runner=java.lang.String" };
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("does not implement PipelineRunner");
        expectedException.expectMessage("java.lang.String");
        PipelineOptionsFactory.fromArgs(args).create();
    }

    @Test
    public void testUsingArgumentWithUnknownPropertyIsNotAllowed() {
        String[] args = new String[]{ "--unknownProperty=value" };
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("missing a property named 'unknownProperty'");
        PipelineOptionsFactory.fromArgs(args).create();
    }

    /**
     * Test interface.
     */
    public interface SuggestedOptions extends PipelineOptions {
        String getAbc();

        void setAbc(String value);

        String getAbcdefg();

        void setAbcdefg(String value);
    }

    @Test
    public void testUsingArgumentWithMisspelledPropertyGivesASuggestion() {
        String[] args = new String[]{ "--ab=value" };
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("missing a property named 'ab'. Did you mean 'abc'?");
        as(PipelineOptionsFactoryTest.SuggestedOptions.class);
    }

    @Test
    public void testUsingArgumentWithMisspelledPropertyGivesMultipleSuggestions() {
        String[] args = new String[]{ "--abcde=value" };
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("missing a property named 'abcde'. Did you mean one of [abc, abcdefg]?");
        as(PipelineOptionsFactoryTest.SuggestedOptions.class);
    }

    @Test
    public void testUsingArgumentWithUnknownPropertyIsIgnoredWithoutStrictParsing() {
        String[] args = new String[]{ "--unknownProperty=value" };
        PipelineOptionsFactory.fromArgs(args).withoutStrictParsing().create();
        expectedLogs.verifyWarn("missing a property named 'unknownProperty'");
    }

    @Test
    public void testUsingArgumentStartingWithIllegalCharacterIsNotAllowed() {
        String[] args = new String[]{ " --diskSizeGb=100" };
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Argument ' --diskSizeGb=100' does not begin with '--'");
        PipelineOptionsFactory.fromArgs(args).create();
    }

    @Test
    public void testUsingArgumentStartingWithIllegalCharacterIsIgnoredWithoutStrictParsing() {
        String[] args = new String[]{ " --diskSizeGb=100" };
        PipelineOptionsFactory.fromArgs(args).withoutStrictParsing().create();
        expectedLogs.verifyWarn("Strict parsing is disabled, ignoring option");
    }

    @Test
    public void testEmptyArgumentIsIgnored() {
        String[] args = new String[]{ "", "--string=100", "", "", "--runner=" + (PipelineOptionsFactoryTest.REGISTERED_RUNNER.getSimpleName()) };
        as(PipelineOptionsFactoryTest.Objects.class);
    }

    @Test
    public void testNullArgumentIsIgnored() {
        String[] args = new String[]{ "--string=100", null, null, "--runner=" + (PipelineOptionsFactoryTest.REGISTERED_RUNNER.getSimpleName()) };
        as(PipelineOptionsFactoryTest.Objects.class);
    }

    @Test
    public void testUsingArgumentWithInvalidNameIsNotAllowed() {
        String[] args = new String[]{ "--=100" };
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Argument '--=100' starts with '--='");
        PipelineOptionsFactory.fromArgs(args).create();
    }

    @Test
    public void testUsingArgumentWithInvalidNameIsIgnoredWithoutStrictParsing() {
        String[] args = new String[]{ "--=100" };
        PipelineOptionsFactory.fromArgs(args).withoutStrictParsing().create();
        expectedLogs.verifyWarn("Strict parsing is disabled, ignoring option");
    }

    @Test
    public void testWhenNoHelpIsRequested() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ListMultimap<String, String> arguments = ArrayListMultimap.create();
        Assert.assertFalse(/* exit */
        PipelineOptionsFactory.printHelpUsageAndExitIfNeeded(arguments, new PrintStream(baos), false));
        String output = new String(baos.toByteArray(), Charsets.UTF_8);
        Assert.assertEquals("", output);
    }

    @Test
    public void testDefaultHelpAsArgument() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ListMultimap<String, String> arguments = ArrayListMultimap.create();
        arguments.put("help", "true");
        Assert.assertTrue(/* exit */
        PipelineOptionsFactory.printHelpUsageAndExitIfNeeded(arguments, new PrintStream(baos), false));
        String output = new String(baos.toByteArray(), Charsets.UTF_8);
        Assert.assertThat(output, Matchers.containsString("The set of registered options are:"));
        Assert.assertThat(output, Matchers.containsString("org.apache.beam.sdk.options.PipelineOptions"));
        Assert.assertThat(output, Matchers.containsString("Use --help=<OptionsName> for detailed help."));
    }

    @Test
    public void testSpecificHelpAsArgument() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ListMultimap<String, String> arguments = ArrayListMultimap.create();
        arguments.put("help", "org.apache.beam.sdk.options.PipelineOptions");
        Assert.assertTrue(/* exit */
        PipelineOptionsFactory.printHelpUsageAndExitIfNeeded(arguments, new PrintStream(baos), false));
        String output = new String(baos.toByteArray(), Charsets.UTF_8);
        Assert.assertThat(output, Matchers.containsString("org.apache.beam.sdk.options.PipelineOptions"));
        Assert.assertThat(output, Matchers.containsString("--runner"));
        Assert.assertThat(output, Matchers.containsString(("Default: " + (PipelineOptionsFactoryTest.DEFAULT_RUNNER_NAME))));
        Assert.assertThat(output, Matchers.containsString("The pipeline runner that will be used to execute the pipeline."));
    }

    @Test
    public void testSpecificHelpAsArgumentWithSimpleClassName() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ListMultimap<String, String> arguments = ArrayListMultimap.create();
        arguments.put("help", "PipelineOptions");
        Assert.assertTrue(/* exit */
        PipelineOptionsFactory.printHelpUsageAndExitIfNeeded(arguments, new PrintStream(baos), false));
        String output = new String(baos.toByteArray(), Charsets.UTF_8);
        Assert.assertThat(output, Matchers.containsString("org.apache.beam.sdk.options.PipelineOptions"));
        Assert.assertThat(output, Matchers.containsString("--runner"));
        Assert.assertThat(output, Matchers.containsString(("Default: " + (PipelineOptionsFactoryTest.DEFAULT_RUNNER_NAME))));
        Assert.assertThat(output, Matchers.containsString("The pipeline runner that will be used to execute the pipeline."));
    }

    @Test
    public void testSpecificHelpAsArgumentWithClassNameSuffix() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ListMultimap<String, String> arguments = ArrayListMultimap.create();
        arguments.put("help", "options.PipelineOptions");
        Assert.assertTrue(/* exit */
        PipelineOptionsFactory.printHelpUsageAndExitIfNeeded(arguments, new PrintStream(baos), false));
        String output = new String(baos.toByteArray(), Charsets.UTF_8);
        Assert.assertThat(output, Matchers.containsString("org.apache.beam.sdk.options.PipelineOptions"));
        Assert.assertThat(output, Matchers.containsString("--runner"));
        Assert.assertThat(output, Matchers.containsString(("Default: " + (PipelineOptionsFactoryTest.DEFAULT_RUNNER_NAME))));
        Assert.assertThat(output, Matchers.containsString("The pipeline runner that will be used to execute the pipeline."));
    }

    /**
     * Used for a name collision test with the other NameConflict interfaces.
     */
    public static class NameConflictClassA {
        /**
         * Used for a name collision test with the other NameConflict interfaces.
         */
        public interface NameConflict extends PipelineOptions {}
    }

    /**
     * Used for a name collision test with the other NameConflict interfaces.
     */
    public static class NameConflictClassB {
        /**
         * Used for a name collision test with the other NameConflict interfaces.
         */
        public interface NameConflict extends PipelineOptions {}
    }

    @Test
    public void testShortnameSpecificHelpHasMultipleMatches() {
        PipelineOptionsFactory.register(PipelineOptionsFactoryTest.NameConflictClassA.NameConflict.class);
        PipelineOptionsFactory.register(PipelineOptionsFactoryTest.NameConflictClassB.NameConflict.class);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ListMultimap<String, String> arguments = ArrayListMultimap.create();
        arguments.put("help", "NameConflict");
        Assert.assertTrue(/* exit */
        PipelineOptionsFactory.printHelpUsageAndExitIfNeeded(arguments, new PrintStream(baos), false));
        String output = new String(baos.toByteArray(), Charsets.UTF_8);
        Assert.assertThat(output, Matchers.containsString("Multiple matches found for NameConflict"));
        Assert.assertThat(output, Matchers.containsString(("org.apache.beam.sdk.options." + "PipelineOptionsFactoryTest$NameConflictClassA$NameConflict")));
        Assert.assertThat(output, Matchers.containsString(("org.apache.beam.sdk.options." + "PipelineOptionsFactoryTest$NameConflictClassB$NameConflict")));
        Assert.assertThat(output, Matchers.containsString("The set of registered options are:"));
        Assert.assertThat(output, Matchers.containsString("org.apache.beam.sdk.options.PipelineOptions"));
    }

    @Test
    public void testHelpWithOptionThatOutputsValidEnumTypes() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ListMultimap<String, String> arguments = ArrayListMultimap.create();
        arguments.put("help", PipelineOptionsFactoryTest.Objects.class.getName());
        Assert.assertTrue(/* exit */
        PipelineOptionsFactory.printHelpUsageAndExitIfNeeded(arguments, new PrintStream(baos), false));
        String output = new String(baos.toByteArray(), Charsets.UTF_8);
        Assert.assertThat(output, Matchers.containsString("<Value | Value2>"));
    }

    @Test
    public void testHelpWithBadOptionNameAsArgument() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ListMultimap<String, String> arguments = ArrayListMultimap.create();
        arguments.put("help", "org.apache.beam.sdk.Pipeline");
        Assert.assertTrue(/* exit */
        PipelineOptionsFactory.printHelpUsageAndExitIfNeeded(arguments, new PrintStream(baos), false));
        String output = new String(baos.toByteArray(), Charsets.UTF_8);
        Assert.assertThat(output, Matchers.containsString("Unable to find option org.apache.beam.sdk.Pipeline"));
        Assert.assertThat(output, Matchers.containsString("The set of registered options are:"));
        Assert.assertThat(output, Matchers.containsString("org.apache.beam.sdk.options.PipelineOptions"));
    }

    @Test
    public void testHelpWithHiddenMethodAndInterface() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ListMultimap<String, String> arguments = ArrayListMultimap.create();
        arguments.put("help", "org.apache.beam.sdk.option.DataflowPipelineOptions");
        Assert.assertTrue(/* exit */
        PipelineOptionsFactory.printHelpUsageAndExitIfNeeded(arguments, new PrintStream(baos), false));
        String output = new String(baos.toByteArray(), Charsets.UTF_8);
        // A hidden interface.
        Assert.assertThat(output, Matchers.not(Matchers.containsString("org.apache.beam.sdk.options.DataflowPipelineDebugOptions")));
        // A hidden option.
        Assert.assertThat(output, Matchers.not(Matchers.containsString("--gcpCredential")));
    }

    @Test
    public void testProgrammaticPrintHelp() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PipelineOptionsFactory.printHelp(new PrintStream(baos));
        String output = new String(baos.toByteArray(), Charsets.UTF_8);
        Assert.assertThat(output, Matchers.containsString("The set of registered options are:"));
        Assert.assertThat(output, Matchers.containsString("org.apache.beam.sdk.options.PipelineOptions"));
    }

    @Test
    public void testProgrammaticPrintHelpForSpecificType() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PipelineOptionsFactory.printHelp(new PrintStream(baos), PipelineOptions.class);
        String output = new String(baos.toByteArray(), Charsets.UTF_8);
        Assert.assertThat(output, Matchers.containsString("org.apache.beam.sdk.options.PipelineOptions"));
        Assert.assertThat(output, Matchers.containsString("--runner"));
        Assert.assertThat(output, Matchers.containsString(("Default: " + (PipelineOptionsFactoryTest.DEFAULT_RUNNER_NAME))));
        Assert.assertThat(output, Matchers.containsString("The pipeline runner that will be used to execute the pipeline."));
    }

    /**
     * Test interface.
     */
    public interface PipelineOptionsInheritedInvalid extends PipelineOptions , PipelineOptionsFactoryTest.Invalid1 , PipelineOptionsFactoryTest.InvalidPipelineOptions2 {
        String getFoo();

        void setFoo(String value);
    }

    /**
     * Test interface.
     */
    public interface InvalidPipelineOptions1 {
        String getBar();

        void setBar(String value);
    }

    /**
     * Test interface.
     */
    public interface Invalid1 extends PipelineOptionsFactoryTest.InvalidPipelineOptions1 {
        @Override
        String getBar();

        @Override
        void setBar(String value);
    }

    /**
     * Test interface.
     */
    public interface InvalidPipelineOptions2 {
        String getBar();

        void setBar(String value);
    }

    @Test
    public void testAllFromPipelineOptions() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(("All inherited interfaces of [org.apache.beam.sdk.options.PipelineOptionsFactoryTest" + ((((("$PipelineOptionsInheritedInvalid] should inherit from the PipelineOptions interface. " + "The following inherited interfaces do not:\n") + " - org.apache.beam.sdk.options.PipelineOptionsFactoryTest") + "$InvalidPipelineOptions1\n") + " - org.apache.beam.sdk.options.PipelineOptionsFactoryTest") + "$InvalidPipelineOptions2")));
        PipelineOptionsFactory.as(PipelineOptionsFactoryTest.PipelineOptionsInheritedInvalid.class);
    }

    private static class RegisteredTestRunner extends PipelineRunner<PipelineResult> {
        public static PipelineRunner<PipelineResult> fromOptions(PipelineOptions options) {
            return new PipelineOptionsFactoryTest.RegisteredTestRunner();
        }

        @Override
        public PipelineResult run(Pipeline p) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * A {@link PipelineRunnerRegistrar} to demonstrate default {@link PipelineRunner} registration.
     */
    @AutoService(PipelineRunnerRegistrar.class)
    public static class RegisteredTestRunnerRegistrar implements PipelineRunnerRegistrar {
        @Override
        public Iterable<Class<? extends PipelineRunner<?>>> getPipelineRunners() {
            return ImmutableList.of(PipelineOptionsFactoryTest.RegisteredTestRunner.class);
        }
    }

    /**
     * Test interface.
     */
    public interface RegisteredTestOptions extends PipelineOptions {
        Object getRegisteredExampleFooBar();

        void setRegisteredExampleFooBar(Object registeredExampleFooBar);
    }

    /**
     * A {@link PipelineOptionsRegistrar} to demonstrate default {@link PipelineOptions} registration.
     */
    @AutoService(PipelineOptionsRegistrar.class)
    public static class RegisteredTestOptionsRegistrar implements PipelineOptionsRegistrar {
        @Override
        public Iterable<Class<? extends PipelineOptions>> getPipelineOptions() {
            return ImmutableList.of(PipelineOptionsFactoryTest.RegisteredTestOptions.class);
        }
    }

    @Test
    public void testRegistrationOfJacksonModulesForObjectMapper() throws Exception {
        PipelineOptionsFactoryTest.JacksonIncompatibleOptions options = PipelineOptionsFactory.fromArgs("--jacksonIncompatible=\"testValue\"").as(PipelineOptionsFactoryTest.JacksonIncompatibleOptions.class);
        Assert.assertEquals("testValue", options.getJacksonIncompatible().value);
    }

    /**
     * PipelineOptions used to test auto registration of Jackson modules.
     */
    public interface JacksonIncompatibleOptions extends PipelineOptions {
        PipelineOptionsFactoryTest.JacksonIncompatible getJacksonIncompatible();

        void setJacksonIncompatible(PipelineOptionsFactoryTest.JacksonIncompatible value);
    }

    /**
     * A Jackson {@link Module} to test auto-registration of modules.
     */
    @AutoService(Module.class)
    public static class RegisteredTestModule extends SimpleModule {
        public RegisteredTestModule() {
            super("RegisteredTestModule");
            setMixInAnnotation(PipelineOptionsFactoryTest.JacksonIncompatible.class, PipelineOptionsFactoryTest.JacksonIncompatibleMixin.class);
        }
    }

    /**
     * A class which Jackson does not know how to serialize/deserialize.
     */
    public static class JacksonIncompatible {
        private final String value;

        public JacksonIncompatible(String value) {
            this.value = value;
        }
    }

    /**
     * A Jackson mixin used to add annotations to other classes.
     */
    @JsonDeserialize(using = PipelineOptionsFactoryTest.JacksonIncompatibleDeserializer.class)
    @JsonSerialize(using = PipelineOptionsFactoryTest.JacksonIncompatibleSerializer.class)
    public static final class JacksonIncompatibleMixin {}

    /**
     * A Jackson deserializer for {@link JacksonIncompatible}.
     */
    public static class JacksonIncompatibleDeserializer extends JsonDeserializer<PipelineOptionsFactoryTest.JacksonIncompatible> {
        @Override
        public PipelineOptionsFactoryTest.JacksonIncompatible deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws JsonProcessingException, IOException {
            return new PipelineOptionsFactoryTest.JacksonIncompatible(jsonParser.readValueAs(String.class));
        }
    }

    /**
     * A Jackson serializer for {@link JacksonIncompatible}.
     */
    public static class JacksonIncompatibleSerializer extends JsonSerializer<PipelineOptionsFactoryTest.JacksonIncompatible> {
        @Override
        public void serialize(PipelineOptionsFactoryTest.JacksonIncompatible jacksonIncompatible, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws JsonProcessingException, IOException {
            jsonGenerator.writeString(jacksonIncompatible.value);
        }
    }

    /**
     * Used to test that the thread context class loader is used when creating proxies.
     */
    public interface ClassLoaderTestOptions extends PipelineOptions {
        @Default.Boolean(true)
        @Description("A test option.")
        boolean isOption();

        void setOption(boolean b);
    }

    @Test
    public void testPipelineOptionsFactoryUsesTccl() throws Exception {
        final Thread thread = Thread.currentThread();
        final ClassLoader testClassLoader = thread.getContextClassLoader();
        final ClassLoader caseLoader = new InterceptingUrlClassLoader(testClassLoader, ( name) -> name.toLowerCase(Locale.ROOT).contains("test"));
        thread.setContextClassLoader(caseLoader);
        PipelineOptionsFactory.resetCache();
        try {
            final PipelineOptions pipelineOptions = PipelineOptionsFactory.create();
            final Class optionType = caseLoader.loadClass("org.apache.beam.sdk.options.PipelineOptionsFactoryTest$ClassLoaderTestOptions");
            final Object options = pipelineOptions.as(optionType);
            Assert.assertSame(caseLoader, options.getClass().getClassLoader());
            Assert.assertSame(optionType.getClassLoader(), options.getClass().getClassLoader());
            Assert.assertSame(testClassLoader, optionType.getInterfaces()[0].getClassLoader());
            Assert.assertTrue(Boolean.class.cast(optionType.getMethod("isOption").invoke(options)));
        } finally {
            thread.setContextClassLoader(testClassLoader);
            PipelineOptionsFactory.resetCache();
        }
    }

    @Test
    public void testDefaultMethodIgnoresDefaultImplementation() {
        PipelineOptionsFactoryTest.OptionsWithDefaultMethod optsWithDefault = PipelineOptionsFactory.as(PipelineOptionsFactoryTest.OptionsWithDefaultMethod.class);
        Assert.assertThat(optsWithDefault.getValue(), Matchers.nullValue());
        optsWithDefault.setValue(12.25);
        Assert.assertThat(optsWithDefault.getValue(), Matchers.equalTo(12.25));
    }

    /**
     * Test interface.
     */
    public interface ExtendedOptionsWithDefault extends PipelineOptionsFactoryTest.OptionsWithDefaultMethod {}

    @Test
    public void testDefaultMethodInExtendedClassIgnoresDefaultImplementation() {
        PipelineOptionsFactoryTest.OptionsWithDefaultMethod extendedOptsWithDefault = PipelineOptionsFactory.as(PipelineOptionsFactoryTest.ExtendedOptionsWithDefault.class);
        Assert.assertThat(extendedOptsWithDefault.getValue(), Matchers.nullValue());
        extendedOptsWithDefault.setValue(Double.NEGATIVE_INFINITY);
        Assert.assertThat(extendedOptsWithDefault.getValue(), Matchers.equalTo(Double.NEGATIVE_INFINITY));
    }

    /**
     * Test interface.
     */
    public interface OptionsWithDefaultMethod extends PipelineOptions {
        default Number getValue() {
            return 1024;
        }

        void setValue(Number value);
    }

    @Test
    public void testStaticMethodsAreAllowed() {
        Assert.assertEquals("value", PipelineOptionsFactoryTest.OptionsWithStaticMethod.myStaticMethod(as(PipelineOptionsFactoryTest.OptionsWithStaticMethod.class)));
    }

    /**
     * Test interface.
     */
    public interface OptionsWithStaticMethod extends PipelineOptions {
        String getMyMethod();

        void setMyMethod(String value);

        static String myStaticMethod(PipelineOptionsFactoryTest.OptionsWithStaticMethod o) {
            return o.getMyMethod();
        }
    }

    /**
     * Test interface.
     */
    public interface TestDescribeOptions extends PipelineOptions {
        String getString();

        void setString(String value);

        @Description("integer property")
        Integer getInteger();

        void setInteger(Integer value);

        @Description("float number property")
        Float getFloat();

        void setFloat(Float value);

        @Description("simple boolean property")
        @Default.Boolean(true)
        boolean getBooleanSimple();

        void setBooleanSimple(boolean value);

        @Default.Boolean(false)
        Boolean getBooleanWrapper();

        void setBooleanWrapper(Boolean value);

        List<Integer> getList();

        void setList(List<Integer> value);
    }

    @Test
    public void testDescribe() {
        List<PipelineOptionDescriptor> described = PipelineOptionsFactory.describe(Sets.newHashSet(PipelineOptions.class, PipelineOptionsFactoryTest.TestDescribeOptions.class));
        Map<String, PipelineOptionDescriptor> mapped = uniqueIndex(described, ( input) -> input.getName());
        Assert.assertEquals("no duplicates", described.size(), mapped.size());
        Collection<PipelineOptionDescriptor> filtered = Collections2.filter(described, ( input) -> input.getGroup().equals(.class.getName()));
        Assert.assertEquals(6, filtered.size());
        mapped = uniqueIndex(filtered, ( input) -> input.getName());
        PipelineOptionDescriptor listDesc = mapped.get("list");
        Assert.assertThat(listDesc, Matchers.notNullValue());
        Assert.assertThat(listDesc.getDescription(), Matchers.isEmptyString());
        Assert.assertEquals(ARRAY, listDesc.getType());
        Assert.assertThat(listDesc.getDefaultValue(), Matchers.isEmptyString());
        PipelineOptionDescriptor stringDesc = mapped.get("string");
        Assert.assertThat(stringDesc, Matchers.notNullValue());
        Assert.assertThat(stringDesc.getDescription(), Matchers.isEmptyString());
        Assert.assertEquals(STRING, stringDesc.getType());
        Assert.assertThat(stringDesc.getDefaultValue(), Matchers.isEmptyString());
        PipelineOptionDescriptor integerDesc = mapped.get("integer");
        Assert.assertThat(integerDesc, Matchers.notNullValue());
        Assert.assertEquals("integer property", integerDesc.getDescription());
        Assert.assertEquals(INTEGER, integerDesc.getType());
        Assert.assertThat(integerDesc.getDefaultValue(), Matchers.isEmptyString());
        PipelineOptionDescriptor floatDesc = mapped.get("float");
        Assert.assertThat(integerDesc, Matchers.notNullValue());
        Assert.assertEquals("float number property", floatDesc.getDescription());
        Assert.assertEquals(NUMBER, floatDesc.getType());
        Assert.assertThat(floatDesc.getDefaultValue(), Matchers.isEmptyString());
        PipelineOptionDescriptor booleanSimpleDesc = mapped.get("boolean_simple");
        Assert.assertThat(booleanSimpleDesc, Matchers.notNullValue());
        Assert.assertEquals("simple boolean property", booleanSimpleDesc.getDescription());
        Assert.assertEquals(BOOLEAN, booleanSimpleDesc.getType());
        Assert.assertThat(booleanSimpleDesc.getDefaultValue(), Matchers.equalTo("true"));
        PipelineOptionDescriptor booleanWrapperDesc = mapped.get("boolean_wrapper");
        Assert.assertThat(booleanWrapperDesc, Matchers.notNullValue());
        Assert.assertThat(booleanWrapperDesc.getDescription(), Matchers.isEmptyString());
        Assert.assertEquals(BOOLEAN, booleanWrapperDesc.getType());
        Assert.assertThat(booleanWrapperDesc.getDefaultValue(), Matchers.equalTo("false"));
    }
}

