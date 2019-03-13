/**
 * Copyright 2016-2019 Crown Copyright
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
package uk.gov.gchq.gaffer.rest.factory;


import SystemProperty.USER_FACTORY_CLASS;
import org.junit.Assert;
import org.junit.Test;


public class UserFactoryTest {
    @Test
    public void shouldCreateDefaultUserFactoryWhenNoSystemProperty() {
        // Given
        System.clearProperty(USER_FACTORY_CLASS);
        // When
        final UserFactory userFactory = UserFactory.createUserFactory();
        // Then
        Assert.assertEquals(UnknownUserFactory.class, userFactory.getClass());
    }

    @Test
    public void shouldCreateUserFactoryFromSystemPropertyClassName() {
        // Given
        System.setProperty(USER_FACTORY_CLASS, UserFactoryForTest.class.getName());
        // When
        final UserFactory userFactory = UserFactory.createUserFactory();
        // Then
        Assert.assertEquals(UserFactoryForTest.class, userFactory.getClass());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionFromInvalidSystemPropertyClassName() {
        // Given
        System.setProperty(USER_FACTORY_CLASS, "InvalidClassName");
        // When
        final UserFactory userFactory = UserFactory.createUserFactory();
        // Then
        Assert.fail();
    }
}

