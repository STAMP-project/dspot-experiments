/**
 * Copyright 2019 ThoughtWorks, Inc.
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
package com.thoughtworks.go.plugin.access.elastic.v5;


import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.Mockito;


public class CapabilitiesConverterV5Test {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Mock
    private CapabilitiesDTO capabilitiesDTO;

    private CapabilitiesConverterV5 capabilitiesConverter;

    @Test
    public void fromDTO_shouldConvertToCapabilitiesFromCapabilitiesDTO() {
        Mockito.when(capabilitiesDTO.supportsStatusReport()).thenReturn(false);
        Mockito.when(capabilitiesDTO.supportsAgentStatusReport()).thenReturn(false);
        Assert.assertFalse(capabilitiesConverter.fromDTO(capabilitiesDTO).supportsStatusReport());
        Assert.assertFalse(capabilitiesConverter.fromDTO(capabilitiesDTO).supportsAgentStatusReport());
        Mockito.when(capabilitiesDTO.supportsStatusReport()).thenReturn(true);
        Mockito.when(capabilitiesDTO.supportsAgentStatusReport()).thenReturn(true);
        Assert.assertTrue(capabilitiesConverter.fromDTO(capabilitiesDTO).supportsStatusReport());
        Assert.assertTrue(capabilitiesConverter.fromDTO(capabilitiesDTO).supportsAgentStatusReport());
        Mockito.when(capabilitiesDTO.supportsStatusReport()).thenReturn(false);
        Mockito.when(capabilitiesDTO.supportsAgentStatusReport()).thenReturn(true);
        Assert.assertFalse(capabilitiesConverter.fromDTO(capabilitiesDTO).supportsStatusReport());
        Assert.assertTrue(capabilitiesConverter.fromDTO(capabilitiesDTO).supportsAgentStatusReport());
        Mockito.when(capabilitiesDTO.supportsStatusReport()).thenReturn(true);
        Mockito.when(capabilitiesDTO.supportsAgentStatusReport()).thenReturn(false);
        Assert.assertTrue(capabilitiesConverter.fromDTO(capabilitiesDTO).supportsStatusReport());
        Assert.assertFalse(capabilitiesConverter.fromDTO(capabilitiesDTO).supportsAgentStatusReport());
    }
}

