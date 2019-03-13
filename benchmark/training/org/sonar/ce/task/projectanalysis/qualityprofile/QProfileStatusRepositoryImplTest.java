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
package org.sonar.ce.task.projectanalysis.qualityprofile;


import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import java.util.Optional;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;


@RunWith(DataProviderRunner.class)
public class QProfileStatusRepositoryImplTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private QProfileStatusRepositoryImpl underTest;

    @Test
    public void get_return_empty_for_null_qp_key() {
        assertThat(underTest.get(null)).isEqualTo(Optional.empty());
    }

    @Test
    public void register_fails_with_NPE_if_status_is_null() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("status can't be null");
        underTest.register("key", null);
    }
}

