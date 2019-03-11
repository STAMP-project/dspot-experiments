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
package org.sonar.server.es;


import BulkIndexer.Size.LARGE;
import BulkIndexer.Size.REGULAR;
import org.junit.Test;


public class BulkIndexerConcurrentRequestCalculationTest {
    @Test
    public void should_not_parallelize_if_regular_size() {
        assertConcurrentRequests(REGULAR, BulkIndexerConcurrentRequestCalculationTest.cores(4)).isEqualTo(0);
    }

    @Test
    public void should_not_parallelize_if_large_indexing_but_few_cores() {
        assertConcurrentRequests(LARGE, BulkIndexerConcurrentRequestCalculationTest.cores(4)).isEqualTo(0);
    }

    /**
     * see https://jira.sonarsource.com/browse/SONAR-8075
     */
    @Test
    public void should_heavily_parallelize_on_96_cores_if_large_indexing() {
        assertConcurrentRequests(LARGE, BulkIndexerConcurrentRequestCalculationTest.cores(96)).isEqualTo(18);
    }
}

