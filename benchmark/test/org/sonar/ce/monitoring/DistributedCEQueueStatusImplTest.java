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
package org.sonar.ce.monitoring;


import org.junit.Test;
import org.mockito.Mockito;
import org.sonar.db.DbClient;


public class DistributedCEQueueStatusImplTest extends CommonCEQueueStatusImplTest {
    private DistributedCEQueueStatusImpl underTest = new DistributedCEQueueStatusImpl(getDbClient());

    public DistributedCEQueueStatusImplTest() {
        super(Mockito.mock(DbClient.class, Mockito.RETURNS_DEEP_STUBS));
    }

    @Test
    public void getPendingCount_returns_0_without_querying_database() {
        assertThat(underTest.getPendingCount()).isZero();
        Mockito.verifyZeroInteractions(getDbClient());
    }
}

