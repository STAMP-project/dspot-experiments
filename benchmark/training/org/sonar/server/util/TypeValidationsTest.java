/**
 * SonarQube
 * Copyright (C) 2009-2019 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.server.util;


import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.sonar.server.exceptions.BadRequestException;


public class TypeValidationsTest {
    @Test
    public void validate() {
        TypeValidation fakeTypeValidation = Mockito.mock(TypeValidation.class);
        Mockito.when(fakeTypeValidation.key()).thenReturn("Fake");
        TypeValidations typeValidations = new TypeValidations(Lists.newArrayList(fakeTypeValidation));
        typeValidations.validate("10", "Fake", Lists.newArrayList("a"));
        Mockito.verify(fakeTypeValidation).validate("10", Lists.newArrayList("a"));
    }

    @Test
    public void validate__multiple_values() {
        TypeValidation fakeTypeValidation = Mockito.mock(TypeValidation.class);
        Mockito.when(fakeTypeValidation.key()).thenReturn("Fake");
        TypeValidations typeValidations = new TypeValidations(Lists.newArrayList(fakeTypeValidation));
        typeValidations.validate(Lists.newArrayList("10", "11", "12"), "Fake", Lists.newArrayList("11"));
        Mockito.verify(fakeTypeValidation).validate("10", Lists.newArrayList("11"));
    }

    @Test
    public void fail_on_unknown_type() {
        TypeValidation fakeTypeValidation = Mockito.mock(TypeValidation.class);
        Mockito.when(fakeTypeValidation.key()).thenReturn("Fake");
        try {
            TypeValidations typeValidations = new TypeValidations(Lists.newArrayList(fakeTypeValidation));
            typeValidations.validate("10", "Unknown", null);
            Assert.fail();
        } catch (Exception e) {
            assertThat(e).isInstanceOf(BadRequestException.class);
            BadRequestException badRequestException = ((BadRequestException) (e));
            assertThat(badRequestException.getMessage()).isEqualTo("Type 'Unknown' is not valid.");
        }
    }
}

