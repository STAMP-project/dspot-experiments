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
package org.sonar.ce.task;


import CeTask.Component;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;


@RunWith(DataProviderRunner.class)
public class CeTaskComponentTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void equals_is_based_on_all_fields() {
        String uuid = randomAlphabetic(2);
        String key = randomAlphabetic(3);
        String name = randomAlphabetic(4);
        String somethingElse = randomAlphabetic(5);
        CeTask.Component underTest = new CeTask.Component(uuid, key, name);
        assertThat(underTest).isEqualTo(underTest);
        assertThat(underTest).isEqualTo(new CeTask.Component(uuid, key, name));
        assertThat(underTest).isNotEqualTo(null);
        assertThat(underTest).isNotEqualTo(new Object());
        assertThat(underTest).isNotEqualTo(new CeTask.Component(somethingElse, key, name));
        assertThat(underTest).isNotEqualTo(new CeTask.Component(uuid, somethingElse, name));
        assertThat(underTest).isNotEqualTo(new CeTask.Component(uuid, key, somethingElse));
        assertThat(underTest).isNotEqualTo(new CeTask.Component(uuid, key, null));
    }

    @Test
    public void hashcode_is_based_on_all_fields() {
        String uuid = randomAlphabetic(2);
        String key = randomAlphabetic(3);
        String name = randomAlphabetic(4);
        String somethingElse = randomAlphabetic(5);
        CeTask.Component underTest = new CeTask.Component(uuid, key, name);
        assertThat(underTest.hashCode()).isEqualTo(underTest.hashCode());
        assertThat(underTest.hashCode()).isEqualTo(new CeTask.Component(uuid, key, name).hashCode());
        assertThat(underTest.hashCode()).isNotEqualTo(new Object().hashCode());
        assertThat(underTest.hashCode()).isNotEqualTo(new CeTask.Component(somethingElse, key, name).hashCode());
        assertThat(underTest.hashCode()).isNotEqualTo(new CeTask.Component(uuid, somethingElse, name).hashCode());
        assertThat(underTest.hashCode()).isNotEqualTo(new CeTask.Component(uuid, key, somethingElse).hashCode());
        assertThat(underTest.hashCode()).isNotEqualTo(new CeTask.Component(uuid, key, null).hashCode());
    }
}

