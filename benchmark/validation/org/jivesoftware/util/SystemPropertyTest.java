package org.jivesoftware.util;


import SystemProperty.Builder;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.jivesoftware.openfire.auth.AuthProvider;
import org.jivesoftware.openfire.auth.DefaultAuthProvider;
import org.jivesoftware.openfire.auth.HybridAuthProvider;
import org.jivesoftware.openfire.ldap.LdapAuthProvider;
import org.junit.Assert;
import org.junit.Test;
import org.xmpp.packet.JID;


public class SystemPropertyTest {
    @Test
    public void willBuildAStringProperty() {
        final SystemProperty<String> stringProperty = Builder.ofType(String.class).setKey("a-test-string-property").setDefaultValue("this-is-a-default").setDynamic(true).build();
        Assert.assertThat(stringProperty.getValue(), CoreMatchers.is("this-is-a-default"));
        Assert.assertThat(stringProperty.getValueAsSaved(), CoreMatchers.is("this-is-a-default"));
        stringProperty.setValue("this-is-not-a-default");
        Assert.assertThat(stringProperty.getValue(), CoreMatchers.is("this-is-not-a-default"));
        Assert.assertThat(stringProperty.getValueAsSaved(), CoreMatchers.is("this-is-not-a-default"));
    }

    @Test
    public void willBuildALongProperty() {
        final SystemProperty<Long> longProperty = Builder.ofType(Long.class).setKey("a-test-long-property").setDefaultValue(42L).setDynamic(true).build();
        Assert.assertThat(longProperty.getValue(), CoreMatchers.is(42L));
        Assert.assertThat(longProperty.getValueAsSaved(), CoreMatchers.is("42"));
        longProperty.setValue(84L);
        Assert.assertThat(longProperty.getValue(), CoreMatchers.is(84L));
        Assert.assertThat(longProperty.getValueAsSaved(), CoreMatchers.is("84"));
    }

    @Test
    public void willBuildADurationProperty() {
        final SystemProperty<Duration> longProperty = Builder.ofType(Duration.class).setKey("a-test-duration-property").setDefaultValue(Duration.ofHours(1)).setChronoUnit(ChronoUnit.MINUTES).setDynamic(true).build();
        Assert.assertThat(longProperty.getValue(), CoreMatchers.is(Duration.ofHours(1)));
        Assert.assertThat(longProperty.getValueAsSaved(), CoreMatchers.is("60"));
        longProperty.setValue(Duration.ofDays(1));
        Assert.assertThat(longProperty.getValue(), CoreMatchers.is(Duration.ofDays(1)));
        Assert.assertThat(JiveGlobals.getProperty("a-test-duration-property"), CoreMatchers.is("1440"));
        Assert.assertThat(longProperty.getValueAsSaved(), CoreMatchers.is("1440"));
    }

    @Test
    public void willBuildABooleanProperty() {
        final SystemProperty<Boolean> booleanProperty = Builder.ofType(Boolean.class).setKey("a-test-boolean-property").setDefaultValue(false).setDynamic(true).build();
        Assert.assertThat(booleanProperty.getValue(), CoreMatchers.is(false));
        Assert.assertThat(booleanProperty.getValueAsSaved(), CoreMatchers.is("false"));
        booleanProperty.setValue(true);
        Assert.assertThat(booleanProperty.getValue(), CoreMatchers.is(true));
        Assert.assertThat(booleanProperty.getValueAsSaved(), CoreMatchers.is("true"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void willNotBuildAPropertyWithoutAKey() {
        Builder.ofType(String.class).build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void willNotBuildAPropertyWithoutADefaultValue() {
        Builder.ofType(String.class).setKey("a-test-property-without-a-default-value").build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void willNotBuildAPropertyWithoutADynamicIndicator() {
        Builder.ofType(String.class).setKey("a-test-property-without-dynamic-set").setDefaultValue("default value").build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void willNotBuildAPropertyForAnUnsupportedClass() {
        Builder.ofType(JavaSpecVersion.class).setKey("a-property-for-an-unsupported-class").setDefaultValue(new JavaSpecVersion("1.8")).setDynamic(true).build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void willNotBuildTheSamePropertyTwice() {
        Builder.ofType(String.class).setKey("a-duplicate-property").setDefaultValue("").setDynamic(true).build();
        Builder.ofType(Boolean.class).setKey("a-duplicate-property").setDefaultValue(false).setDynamic(true).build();
    }

    @Test
    public void willPreventAValueBeingTooLow() {
        final SystemProperty<Long> property = Builder.ofType(Long.class).setKey("this-is-a-constrained-long-key").setDefaultValue(42L).setMinValue(0L).setMaxValue(42L).setDynamic(true).build();
        property.setValue((-1L));
        Assert.assertThat(property.getValue(), CoreMatchers.is(42L));
        Assert.assertThat(property.getValueAsSaved(), CoreMatchers.is("42"));
    }

    @Test
    public void willPreventAValueBeingTooHigh() {
        final SystemProperty<Long> property = Builder.ofType(Long.class).setKey("this-is-another-constrained-long-key").setDefaultValue(42L).setMinValue(0L).setMaxValue(42L).setDynamic(true).build();
        property.setValue(500L);
        Assert.assertThat(property.getValue(), CoreMatchers.is(42L));
        Assert.assertThat(property.getValueAsSaved(), CoreMatchers.is("42"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void willNotBuildADurationPropertyWithoutAChronoUnit() {
        Builder.ofType(Duration.class).setKey("this-will-not-work").setDefaultValue(Duration.ZERO).setDynamic(true).build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void willNotBuildADurationPropertyWithAnInvalidChronoUnit() {
        Builder.ofType(Duration.class).setKey("this-will-not-work").setDefaultValue(Duration.ZERO).setChronoUnit(ChronoUnit.CENTURIES).setDynamic(true).build();
    }

    @Test
    public void willNotifyListenersOfChanges() {
        final AtomicReference<Duration> notifiedValue = new AtomicReference<>();
        final SystemProperty<Duration> property = Builder.ofType(Duration.class).setKey("property-notifier").setDefaultValue(Duration.ZERO).setChronoUnit(ChronoUnit.SECONDS).setDynamic(true).addListener(notifiedValue::set).build();
        property.setValue(Duration.ofMinutes(60));
        Assert.assertThat(notifiedValue.get(), CoreMatchers.is(Duration.ofHours(1)));
    }

    @Test
    public void willIndicateDynamicPropertyDoesNotNeedRestarting() {
        final SystemProperty<Long> longProperty = Builder.ofType(Long.class).setKey("a-test-dynamic-property").setDefaultValue(42L).setDynamic(true).build();
        Assert.assertThat(longProperty.isDynamic(), CoreMatchers.is(true));
        Assert.assertThat(longProperty.isRestartRequired(), CoreMatchers.is(false));
        longProperty.setValue(84L);
        Assert.assertThat(longProperty.isRestartRequired(), CoreMatchers.is(false));
    }

    @Test
    public void willIndicateNonDynamicPropertyNeedsRestarting() {
        final SystemProperty<Long> longProperty = Builder.ofType(Long.class).setKey("a-test-non-dynamic-property").setDefaultValue(42L).setDynamic(false).build();
        Assert.assertThat(longProperty.isDynamic(), CoreMatchers.is(false));
        Assert.assertThat(longProperty.isRestartRequired(), CoreMatchers.is(false));
        longProperty.setValue(84L);
        Assert.assertThat(longProperty.isRestartRequired(), CoreMatchers.is(true));
    }

    @Test
    public void theDefaultPluginIsOpenfire() {
        final SystemProperty<Long> longProperty = Builder.ofType(Long.class).setKey("an-openfire-property").setDefaultValue(42L).setDynamic(false).build();
        Assert.assertThat(longProperty.getPlugin(), CoreMatchers.is("Openfire"));
    }

    @Test
    public void thePluginCanBeChanged() {
        final SystemProperty<Long> longProperty = Builder.ofType(Long.class).setKey("a-plugin-property").setDefaultValue(42L).setPlugin("TestPluginName").setDynamic(false).build();
        Assert.assertThat(longProperty.getPlugin(), CoreMatchers.is("TestPluginName"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void aPluginIsRequired() {
        Builder.ofType(Long.class).setKey("a-null-plugin-property").setDefaultValue(42L).setPlugin(null).setDynamic(false).build();
    }

    @Test
    public void willReturnAClass() {
        final SystemProperty<Class> classProperty = Builder.ofType(Class.class).setKey("a-class-property").setDefaultValue(DefaultAuthProvider.class).setBaseClass(AuthProvider.class).setDynamic(false).build();
        Assert.assertThat(classProperty.getValue(), CoreMatchers.is(Matchers.equalTo(DefaultAuthProvider.class)));
        Assert.assertThat(classProperty.getValueAsSaved(), CoreMatchers.is("org.jivesoftware.openfire.auth.DefaultAuthProvider"));
        JiveGlobals.setProperty("a-class-property", "org.jivesoftware.openfire.auth.HybridAuthProvider");
        Assert.assertThat(classProperty.getValue(), CoreMatchers.is(Matchers.equalTo(HybridAuthProvider.class)));
        Assert.assertThat(classProperty.getValueAsSaved(), CoreMatchers.is("org.jivesoftware.openfire.auth.HybridAuthProvider"));
        classProperty.setValue(LdapAuthProvider.class);
        Assert.assertThat(classProperty.getValue(), CoreMatchers.is(Matchers.equalTo(LdapAuthProvider.class)));
        Assert.assertThat(classProperty.getValueAsSaved(), CoreMatchers.is("org.jivesoftware.openfire.ldap.LdapAuthProvider"));
    }

    @Test
    public void willNotReturnAnotherClass() {
        final SystemProperty<Class> classProperty = Builder.ofType(Class.class).setKey("another-subclass-property").setDefaultValue(DefaultAuthProvider.class).setBaseClass(AuthProvider.class).setDynamic(false).build();
        JiveGlobals.setProperty("another-subclass-property", "java.lang.Object");
        Assert.assertThat(classProperty.getValue(), CoreMatchers.is(Matchers.equalTo(DefaultAuthProvider.class)));
    }

    @Test
    public void willNotReturnAMissingClass() {
        final SystemProperty<Class> classProperty = Builder.ofType(Class.class).setKey("a-missing-subclass-property").setDefaultValue(DefaultAuthProvider.class).setBaseClass(AuthProvider.class).setDynamic(false).build();
        JiveGlobals.setProperty("a-missing-subclass-property", "this.class.is.missing");
        Assert.assertThat(classProperty.getValue(), CoreMatchers.is(Matchers.equalTo(DefaultAuthProvider.class)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void willNotBuildAClassPropertyWithoutABaseClass() {
        Builder.ofType(Class.class).setKey("a-broken-class-property").setDefaultValue(DefaultAuthProvider.class).setDynamic(false).build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void willNotBuildARegularPropertyWithABaseClass() {
        Builder.ofType(Long.class).setKey("a-broken-long-property").setDefaultValue(42L).setBaseClass(Long.class).setDynamic(false).build();
    }

    @Test
    public void shouldEncryptAProperty() {
        final SystemProperty<Long> longProperty = Builder.ofType(Long.class).setKey("an-encrypted-property").setDefaultValue(42L).setDynamic(false).setEncrypted(true).build();
        longProperty.setValue(84L);
        Assert.assertThat(JiveGlobals.isPropertyEncrypted("an-encrypted-property"), CoreMatchers.is(true));
    }

    @Test
    public void willAllowNullDefaultsForAStringProperty() {
        final SystemProperty<String> stringProperty = Builder.ofType(String.class).setKey("a-null-string-property").setDynamic(true).build();
        Assert.assertThat(stringProperty.getDefaultValue(), CoreMatchers.is(CoreMatchers.nullValue()));
    }

    @Test
    public void willAllowNullDefaultsForAClassProperty() {
        final SystemProperty<Class> classProperty = Builder.ofType(Class.class).setKey("a-null-class-property").setBaseClass(AuthProvider.class).setDynamic(false).build();
        Assert.assertThat(classProperty.getDefaultValue(), CoreMatchers.is(CoreMatchers.nullValue()));
    }

    @Test
    public void willRemovePluginSpecificProperties() {
        final String key = "a-class-property-to-remove";
        final SystemProperty<Class> property = Builder.ofType(Class.class).setKey(key).setBaseClass(AuthProvider.class).setPlugin("TestPluginName").setDynamic(false).build();
        Assert.assertThat(SystemProperty.getProperty(key), CoreMatchers.is(Optional.of(property)));
        SystemProperty.removePropertiesForPlugin("TestPluginName");
        Assert.assertThat(SystemProperty.getProperty(key), CoreMatchers.is(Optional.empty()));
    }

    @Test
    public void willCreateAnInstantProperty() {
        final String key = "test.instant.property";
        final SystemProperty<Instant> property = Builder.ofType(Instant.class).setKey(key).setDynamic(true).build();
        Assert.assertThat(property.getValue(), CoreMatchers.is(CoreMatchers.nullValue()));
        final Instant value = Instant.now();
        property.setValue(value);
        Assert.assertThat(property.getValue(), CoreMatchers.is(value));
    }

    @Test
    public void willCreateAnInstantPropertyWithADefaultValue() {
        final String key = "test.instant.property.with.default";
        final Instant defaultValue = Instant.now();
        final SystemProperty<Instant> property = Builder.ofType(Instant.class).setKey(key).setDefaultValue(defaultValue).setDynamic(true).build();
        Assert.assertThat(property.getValue(), CoreMatchers.is(defaultValue));
    }

    @Test
    public void willCreateAListOfStringProperties() {
        final String key = "a list property";
        final SystemProperty<List<String>> property = Builder.ofType(List.class).setKey(key).setDefaultValue(Collections.emptyList()).setDynamic(true).buildList(String.class);
        Assert.assertThat(property.getValue(), CoreMatchers.is(Collections.emptyList()));
        property.setValue(Arrays.asList("3", "2", "1"));
        Assert.assertThat(property.getValue(), CoreMatchers.is(Arrays.asList("3", "2", "1")));
        Assert.assertThat(property.getDisplayValue(), CoreMatchers.is("3,2,1"));
        Assert.assertThat(JiveGlobals.getProperty(key), CoreMatchers.is("3,2,1"));
    }

    @Test
    public void willCreateAListOfLongProperties() {
        final String key = "a list of longs property";
        final SystemProperty<List<Long>> property = Builder.ofType(List.class).setKey(key).setSorted(true).setDefaultValue(Collections.emptyList()).setDynamic(true).buildList(Long.class);
        Assert.assertThat(property.getValue(), CoreMatchers.is(Collections.emptyList()));
        property.setValue(Arrays.asList(3L, 2L, 1L));
        Assert.assertThat(property.getValue(), CoreMatchers.is(Arrays.asList(1L, 2L, 3L)));
        Assert.assertThat(property.getDisplayValue(), CoreMatchers.is("1,2,3"));
        Assert.assertThat(JiveGlobals.getProperty(key), CoreMatchers.is("1,2,3"));
        JiveGlobals.setProperty(key, "3,2,1");
        Assert.assertThat(property.getValue(), CoreMatchers.is(Arrays.asList(1L, 2L, 3L)));
        Assert.assertThat(property.getDisplayValue(), CoreMatchers.is("1,2,3"));
    }

    @Test
    public void willCreateAListOfDurationProperties() {
        final String key = "a list of durations property";
        final SystemProperty<List<Duration>> property = Builder.ofType(List.class).setChronoUnit(ChronoUnit.HOURS).setKey(key).setDefaultValue(Collections.singletonList(Duration.ZERO)).setDynamic(true).buildList(Duration.class);
        Assert.assertThat(property.getValue(), CoreMatchers.is(Collections.singletonList(Duration.ZERO)));
        property.setValue(Arrays.asList(Duration.ofHours(1), Duration.ZERO));
        Assert.assertThat(property.getValue(), CoreMatchers.is(Arrays.asList(Duration.ofHours(1), Duration.ZERO)));
        Assert.assertThat(property.getDisplayValue(), CoreMatchers.is("1 hour,0 ms"));
        Assert.assertThat(JiveGlobals.getProperty(key), CoreMatchers.is("1,0"));
    }

    @Test
    public void willCreateASetOfStringProperties() {
        final String key = "a set property";
        final SystemProperty<Set<String>> property = Builder.ofType(Set.class).setKey(key).setSorted(true).setDefaultValue(Collections.emptySet()).setDynamic(true).buildSet(String.class);
        Assert.assertThat(property.getValue(), CoreMatchers.is(Collections.emptySet()));
        property.setValue(new HashSet(Arrays.asList("3", "2", "1", "2")));
        Assert.assertThat(property.getValue(), CoreMatchers.is(new LinkedHashSet(Arrays.asList("1", "2", "3"))));
        Assert.assertThat(property.getDisplayValue(), CoreMatchers.is("1,2,3"));
        Assert.assertThat(JiveGlobals.getProperty(key), CoreMatchers.is("1,2,3"));
        JiveGlobals.setProperty(key, "3,2,1,3");
        Assert.assertThat(property.getValue(), CoreMatchers.is(new LinkedHashSet(Arrays.asList("1", "2", "3"))));
        Assert.assertThat(property.getDisplayValue(), CoreMatchers.is("1,2,3"));
    }

    @Test
    public void willBuildAJIDProperty() {
        final String jidString = "test-user@test-domain/test-resource";
        final JID jid = new JID(jidString);
        final String key = "a jid property";
        final SystemProperty<JID> property = Builder.ofType(JID.class).setKey(key).setDynamic(true).build();
        Assert.assertThat(property.getValue(), CoreMatchers.is(CoreMatchers.nullValue()));
        property.setValue(jid);
        Assert.assertThat(property.getValue(), CoreMatchers.is(jid));
        Assert.assertThat(property.getDisplayValue(), CoreMatchers.is(jidString));
        Assert.assertThat(JiveGlobals.getProperty(key), CoreMatchers.is(jidString));
    }

    @Test
    public void willReturnNullForInvalidJID() {
        final String jidString = "@test-domain";
        final String key = "an invalid jid property";
        final SystemProperty<JID> property = Builder.ofType(JID.class).setKey(key).setDynamic(true).build();
        Assert.assertThat(property.getValue(), CoreMatchers.is(CoreMatchers.nullValue()));
        JiveGlobals.setProperty(key, jidString);
        Assert.assertThat(property.getValue(), CoreMatchers.is(CoreMatchers.nullValue()));
    }

    private enum TestEnum {

        TEST_1,
        TEST_2;}

    @Test
    public void willBuildAnEnumProperty() {
        final String key = "an enum property";
        final SystemProperty<SystemPropertyTest.TestEnum> property = Builder.ofType(SystemPropertyTest.TestEnum.class).setKey(key).setDefaultValue(SystemPropertyTest.TestEnum.TEST_1).setDynamic(true).build();
        Assert.assertThat(property.getValue(), CoreMatchers.is(SystemPropertyTest.TestEnum.TEST_1));
        property.setValue(SystemPropertyTest.TestEnum.TEST_2);
        Assert.assertThat(property.getValue(), CoreMatchers.is(SystemPropertyTest.TestEnum.TEST_2));
        Assert.assertThat(property.getDisplayValue(), CoreMatchers.is("TEST_2"));
        Assert.assertThat(JiveGlobals.getProperty(key), CoreMatchers.is("TEST_2"));
    }
}

