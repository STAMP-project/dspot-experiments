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
package org.sonar.db.ce;


import CeQueueDto.Status;
import UpdateIf.NewProperties;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import java.util.Random;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;


@RunWith(DataProviderRunner.class)
public class UpdateIfTest {
    private static final String STR_40_CHARS = "0123456789012345678901234567890123456789";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void newProperties_constructor_accepts_null_workerUuid() {
        UpdateIf.NewProperties newProperties = new UpdateIf.NewProperties(Status.PENDING, null, 123, 456);
        assertThat(newProperties.getWorkerUuid()).isNull();
    }

    @Test
    public void newProperties_constructor_fails_with_NPE_if_status_is_null() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("status can't be null");
        new UpdateIf.NewProperties(null, "foo", 123, 456);
    }

    @Test
    public void newProperties_constructor_fails_with_IAE_if_workerUuid_is_41_or_more() {
        String workerUuid = RandomStringUtils.randomAlphanumeric((41 + (new Random().nextInt(5))));
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(("worker uuid is too long: " + workerUuid));
        new UpdateIf.NewProperties(Status.PENDING, workerUuid, 123, 456);
    }

    @Test
    public void newProperties_constructor_IAE_if_workerUuid_is_41_chars() {
        String str_41_chars = (UpdateIfTest.STR_40_CHARS) + "a";
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(("worker uuid is too long: " + str_41_chars));
        new UpdateIf.NewProperties(Status.PENDING, str_41_chars, 123, 345);
    }
}

