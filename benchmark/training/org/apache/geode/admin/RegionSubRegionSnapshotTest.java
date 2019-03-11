/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.admin;


import java.util.Collections;
import org.junit.Test;
import org.mockito.Mockito;


public class RegionSubRegionSnapshotTest {
    @Test
    public void shouldBeMockable() throws Exception {
        RegionSubRegionSnapshot mockRegionSubRegionSnapshot = Mockito.mock(RegionSubRegionSnapshot.class);
        RegionSubRegionSnapshot mockRegionSubRegionSnapshotParent = Mockito.mock(RegionSubRegionSnapshot.class);
        Mockito.when(mockRegionSubRegionSnapshot.getEntryCount()).thenReturn(0);
        Mockito.when(mockRegionSubRegionSnapshot.getName()).thenReturn("name");
        Mockito.when(mockRegionSubRegionSnapshot.getSubRegionSnapshots()).thenReturn(Collections.emptySet());
        Mockito.when(mockRegionSubRegionSnapshot.getParent()).thenReturn(mockRegionSubRegionSnapshotParent);
        mockRegionSubRegionSnapshot.setEntryCount(1);
        mockRegionSubRegionSnapshot.setName("NAME");
        mockRegionSubRegionSnapshot.setSubRegionSnapshots(null);
        mockRegionSubRegionSnapshot.setParent(null);
        Mockito.verify(mockRegionSubRegionSnapshot, Mockito.times(1)).setEntryCount(1);
        Mockito.verify(mockRegionSubRegionSnapshot, Mockito.times(1)).setName("NAME");
        Mockito.verify(mockRegionSubRegionSnapshot, Mockito.times(1)).setSubRegionSnapshots(null);
        Mockito.verify(mockRegionSubRegionSnapshot, Mockito.times(1)).setParent(null);
        assertThat(mockRegionSubRegionSnapshot.getEntryCount()).isEqualTo(0);
        assertThat(mockRegionSubRegionSnapshot.getName()).isEqualTo("name");
        assertThat(mockRegionSubRegionSnapshot.getSubRegionSnapshots()).isEqualTo(Collections.emptySet());
        assertThat(mockRegionSubRegionSnapshot.getParent()).isSameAs(mockRegionSubRegionSnapshotParent);
    }
}

