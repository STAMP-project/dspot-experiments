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


import NinjaConstant.MODE_KEY_NAME;
import NinjaConstant.applicationSecret;
import NinjaProperties.NINJA_EXTERNAL_CONF;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import javax.inject.Named;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import static NinjaConstant.applicationSecret;
import static NinjaMode.dev;
import static NinjaMode.prod;
import static NinjaMode.test;


public class NinjaPropertiesImplTest {
    @Test
    public void testSkippingThroughModesWorks() {
        // check that mode tests works:
        NinjaPropertiesImpl ninjaPropertiesImpl = new NinjaPropertiesImpl(test);
        Assert.assertEquals("test_testproperty", ninjaPropertiesImpl.get("testproperty"));
        // check that mode dev works:
        ninjaPropertiesImpl = new NinjaPropertiesImpl(dev);
        Assert.assertEquals("dev_testproperty", ninjaPropertiesImpl.get("testproperty"));
        Assert.assertEquals("secret", ninjaPropertiesImpl.get(applicationSecret));
        // and in a completely different mode with no "%"-prefixed key the
        // default value use used
        ninjaPropertiesImpl = new NinjaPropertiesImpl(prod);
        Assert.assertEquals("testproperty_without_prefix", ninjaPropertiesImpl.get("testproperty"));
        Assert.assertEquals("secret", ninjaPropertiesImpl.get(applicationSecret));
        // tear down
        System.clearProperty(MODE_KEY_NAME);
    }

    @Test(expected = RuntimeException.class)
    public void testGetOrDie() {
        NinjaPropertiesImpl ninjaPropertiesImpl = new NinjaPropertiesImpl(dev);
        Assert.assertEquals("dev_testproperty", ninjaPropertiesImpl.getOrDie("testproperty"));
        ninjaPropertiesImpl.getOrDie("a_propert_that_is_not_in_the_file");
    }

    @Test
    public void testGetBooleanParsing() {
        NinjaPropertiesImpl ninjaPropertiesImpl = new NinjaPropertiesImpl(dev);
        Assert.assertEquals(true, ninjaPropertiesImpl.getBoolean("booleanTestTrue"));
        Assert.assertEquals(false, ninjaPropertiesImpl.getBoolean("booleanTestFalse"));
        Assert.assertEquals(null, ninjaPropertiesImpl.getBoolean("booleanNotValid"));
    }

    @Test(expected = RuntimeException.class)
    public void testGetBooleanOrDie() {
        NinjaPropertiesImpl ninjaPropertiesImpl = new NinjaPropertiesImpl(dev);
        Assert.assertEquals(true, ninjaPropertiesImpl.getBooleanOrDie("booleanTestTrue"));
        ninjaPropertiesImpl.getBooleanOrDie("booleanNotValid");
    }

    @Test
    public void testGetIntegerParsing() {
        NinjaPropertiesImpl ninjaPropertiesImpl = new NinjaPropertiesImpl(dev);
        Assert.assertEquals(new Integer(123456789), ninjaPropertiesImpl.getInteger("integerTest"));
        Assert.assertEquals(null, ninjaPropertiesImpl.getInteger("integerNotValid"));
    }

    @Test(expected = RuntimeException.class)
    public void testGetIntegerDie() {
        NinjaPropertiesImpl ninjaPropertiesImpl = new NinjaPropertiesImpl(dev);
        Assert.assertEquals(new Integer(123456789), ninjaPropertiesImpl.getIntegerOrDie("integerTest"));
        ninjaPropertiesImpl.getIntegerOrDie("integerNotValid");
    }

    @Test
    public void testPropertiesBoundInGuice() {
        final NinjaPropertiesImpl props = new NinjaPropertiesImpl(dev);
        NinjaPropertiesImplTest.MockService service = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                props.bindProperties(binder());
            }
        }).getInstance(NinjaPropertiesImplTest.MockService.class);
        Assert.assertNotNull("Application secret not set by Guice", service.applicationSecret);
        Assert.assertEquals("secret", service.applicationSecret);
    }

    public static class MockService {
        @Inject
        @Named(applicationSecret)
        public String applicationSecret;
    }

    @Test
    public void testReferenciningOfPropertiesWorks() {
        // instantiate the properties:
        NinjaProperties ninjaProperties = new NinjaPropertiesImpl(dev);
        // this is testing if referencing of properties works with external
        // configurations
        // and application.conf: (fullServerName=${serverName}:${serverPort})
        Assert.assertEquals("http://myserver.com:80", ninjaProperties.get("fullServerName"));
    }

    @Test
    public void testLoadingOfExternalConfFile() {
        // we can set an external conf file by setting the following system
        // property:
        System.setProperty(NINJA_EXTERNAL_CONF, "conf/heroku.conf");
        // instantiate the properties:
        NinjaProperties ninjaProperties = new NinjaPropertiesImpl(dev);
        // we expect that the original application secret gets overwritten:
        Assert.assertEquals("secretForHeroku", ninjaProperties.get(applicationSecret));
        // and make sure other properties of heroku.conf get loaded as well:
        Assert.assertEquals("some special parameter", ninjaProperties.get("heroku.special.property"));
        // this is testing if referencing of properties works with external
        // configurations
        // and application.conf (fullServerName=${serverName}:${serverPort})
        Assert.assertEquals("http://myapp.herokuapp.com:80", ninjaProperties.get("fullServerName"));
    }

    @Test
    public void testLoadingOfExternalConfFileOverridesSystemProperty() {
        // we can set an external conf file by setting the following system property
        System.setProperty(NINJA_EXTERNAL_CONF, "conf/filedoesnotexist.conf");
        // instantiate the properties, but provide a different one
        NinjaProperties ninjaProperties = new NinjaPropertiesImpl(dev, "conf/heroku.conf");
        // we expect that the original application secret gets overwritten:
        Assert.assertEquals("secretForHeroku", ninjaProperties.get(applicationSecret));
        // and make sure other properties of heroku.conf get loaded as well:
        Assert.assertEquals("some special parameter", ninjaProperties.get("heroku.special.property"));
        // this is testing if referencing of properties works with external
        // configurations
        // and application.conf (fullServerName=${serverName}:${serverPort})
        Assert.assertEquals("http://myapp.herokuapp.com:80", ninjaProperties.get("fullServerName"));
    }

    @Test
    public void testLoadingOfExternalConfigurationFileWorksAndPrefixedConfigurationsSetRead() {
        // we can set an external conf file by setting the following system
        // property:
        System.setProperty(NINJA_EXTERNAL_CONF, "conf/heroku.conf");
        // instantiate the properties:
        NinjaProperties ninjaProperties = new NinjaPropertiesImpl(test);
        // this is testing if referencing of properties works with external
        // configurations
        // and application.conf (fullServerName=${serverName}:${serverPort})
        // It also will be different as we are in "test" mode:
        // "myapp-test" is the important thing here.
        Assert.assertEquals("http://myapp-test.herokuapp.com:80", ninjaProperties.get("fullServerName"));
    }

    @Test
    public void testUft8Works() {
        NinjaProperties ninjaProperties = new NinjaPropertiesImpl(dev);
        // We test this: utf8Test=this is utf8: ???
        Assert.assertEquals("this is utf8: ???", ninjaProperties.get("utf8Test"));
    }

    @Test(expected = RuntimeException.class)
    public void testExernalConfigLoadingBreaksWhenFileDoesNotExist() {
        // we can set an external conf file by setting the following system
        // property:
        System.setProperty(NINJA_EXTERNAL_CONF, "conf/non_existing.conf");
        // instantiate the properties:
        NinjaProperties ninjaProperties = new NinjaPropertiesImpl(dev);
        // now a runtime exception must be thrown.
    }

    @Test
    public void testGetWithDefault() {
        // instantiate the properties:
        NinjaProperties ninjaProperties = new NinjaPropertiesImpl(dev);
        // test default works when property not there:
        Assert.assertEquals("default", ninjaProperties.getWithDefault("non_existsing_property_to_check_defaults", "default"));
        // test default works when property is there: => we are int dev mode...
        Assert.assertEquals("dev_testproperty", ninjaProperties.getWithDefault("testproperty", "default"));
    }

    @Test
    public void testGetIntegerWithDefault() {
        // instantiate the properties:
        NinjaProperties ninjaProperties = new NinjaPropertiesImpl(dev);
        // test default works when property not there:
        Assert.assertEquals(Integer.valueOf(1), ninjaProperties.getIntegerWithDefault("non_existsing_property_to_check_defaults", 1));
        // test default works when property is there:
        Assert.assertEquals(Integer.valueOf(123456789), ninjaProperties.getIntegerWithDefault("integerTest", 1));
    }

    @Test
    public void testGetBooleanWithDefault() {
        // instantiate the properties:
        NinjaProperties ninjaProperties = new NinjaPropertiesImpl(dev);
        // test default works when property not there:
        Assert.assertEquals(Boolean.valueOf(true), ninjaProperties.getBooleanWithDefault("non_existsing_property_to_check_defaults", true));
        // test default works when property is there:
        Assert.assertEquals(Boolean.valueOf(true), ninjaProperties.getBooleanWithDefault("booleanTestTrue", false));
    }

    @Test
    public void testGetStringArrayWorks() {
        // instantiate the properties:
        NinjaProperties ninjaProperties = new NinjaPropertiesImpl(dev);
        // test default works when property not there:
        Assert.assertEquals("one", ninjaProperties.get("getOneElementStringArray"));
        Assert.assertEquals("one", ninjaProperties.getStringArray("getOneElementStringArray")[0]);
        Assert.assertEquals("one , me", ninjaProperties.get("getMultipleElementStringArrayWithSpaces"));
        Assert.assertEquals("one", ninjaProperties.getStringArray("getMultipleElementStringArrayWithSpaces")[0]);
        Assert.assertEquals("me", ninjaProperties.getStringArray("getMultipleElementStringArrayWithSpaces")[1]);
        Assert.assertEquals("one,me", ninjaProperties.get("getMultipleElementStringArrayWithoutSpaces"));
        Assert.assertEquals("one", ninjaProperties.getStringArray("getMultipleElementStringArrayWithoutSpaces")[0]);
        Assert.assertEquals("me", ninjaProperties.getStringArray("getMultipleElementStringArrayWithoutSpaces")[1]);
    }

    @Test
    public void systemProperties() {
        // verify property in external conf is set
        NinjaProperties ninjaProperties = new NinjaPropertiesImpl(dev, "conf/system_property.conf");
        Assert.assertThat(ninjaProperties.get("unit.test.123"), CoreMatchers.is("123-value-via-external-conf"));
        // verify system property overrides it
        System.setProperty("unit.test.123", "123-value-via-system-property");
        try {
            ninjaProperties = new NinjaPropertiesImpl(dev, "conf/system_property.conf");
            Assert.assertThat(ninjaProperties.get("unit.test.123"), CoreMatchers.is("123-value-via-system-property"));
        } finally {
            System.clearProperty("unit.test.123");
        }
        // verify prefixed system property overrides both
        System.setProperty("unit.test.123", "123-value-via-system-property");
        System.setProperty("%dev.unit.test.123", "123-value-via-prefixed-system-property");
        try {
            ninjaProperties = new NinjaPropertiesImpl(dev, "conf/system_property.conf");
            Assert.assertThat(ninjaProperties.get("unit.test.123"), CoreMatchers.is("123-value-via-prefixed-system-property"));
        } finally {
            System.clearProperty("unit.test.123");
        }
    }
}

