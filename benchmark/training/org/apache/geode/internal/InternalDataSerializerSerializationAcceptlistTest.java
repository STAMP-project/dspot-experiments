package org.apache.geode.internal;


import DistributionConfig.SERIALIZABLE_OBJECT_FILTER_NAME;
import DistributionConfig.VALIDATE_SERIALIZABLE_OBJECTS_NAME;
import java.io.InvalidClassException;
import java.io.Serializable;
import java.util.Properties;
import org.apache.geode.distributed.internal.DistributionConfigImpl;
import org.apache.geode.test.junit.categories.SerializationTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/* Licensed to the Apache Software Foundation (ASF) under one or more contributor license
agreements. See the NOTICE file distributed with this work for additional information regarding
copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance with the License. You may obtain a
copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License
is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
or implied. See the License for the specific language governing permissions and limitations under
the License.
 */
@Category({ SerializationTest.class })
public class InternalDataSerializerSerializationAcceptlistTest {
    private HeapDataOutputStream outputStream;

    private Object testSerializable;

    private Properties properties;

    @Test
    public void distributionConfigDefaults() {
        DistributionConfigImpl distributionConfig = new DistributionConfigImpl(new Properties());
        Assert.assertFalse(distributionConfig.getValidateSerializableObjects());
        Assert.assertEquals("!*", distributionConfig.getSerializableObjectFilter());
    }

    @Test
    public void canSerializeWhenFilterIsDisabled() throws Exception {
        trySerializingTestObject(new Properties());
    }

    @Test(expected = InvalidClassException.class)
    public void notAcceptlistedWithFilterCannotSerialize() throws Exception {
        properties.setProperty(VALIDATE_SERIALIZABLE_OBJECTS_NAME, "true");
        trySerializingTestObject(properties);
    }

    @Test
    public void acceptlistedWithFilterCanSerialize() throws Exception {
        properties.setProperty(VALIDATE_SERIALIZABLE_OBJECTS_NAME, "true");
        properties.setProperty(SERIALIZABLE_OBJECT_FILTER_NAME, InternalDataSerializerSerializationAcceptlistTest.TestSerializable.class.getName());
        trySerializingTestObject(properties);
    }

    @Test(expected = InvalidClassException.class)
    public void acceptlistedWithNonMatchingFilterCannotSerialize() throws Exception {
        trySerializingWithFilter("RabidMonkeyTurnip");
    }

    @Test(expected = InvalidClassException.class)
    public void acceptlistedWithPartialMatchingFilterCannotSerialize() throws Exception {
        trySerializingWithFilter("TestSerializable");// Not fully qualified class name

    }

    @Test(expected = InvalidClassException.class)
    public void acceptlistedWithEmptyFilterCannotSerialize() throws Exception {
        trySerializingWithFilter("");
    }

    @Test(expected = InvalidClassException.class)
    public void acceptlistedWithIncorrectPathFilterCannotSerialize() throws Exception {
        trySerializingWithFilter("org.apache.commons.InternalDataSerializerSerializationAcceptlistTest$TestSerializable");
    }

    @Test(expected = InvalidClassException.class)
    public void acceptlistedWithWildcardPathFilterCannotSerialize() throws Exception {
        trySerializingWithFilter("org.apache.*");
    }

    @Test
    public void acceptlistedWithWildcardSubpathFilterCanSerialize() throws Exception {
        trySerializingWithFilter("org.apache.**");
    }

    private static class TestSerializable implements Serializable {}
}

