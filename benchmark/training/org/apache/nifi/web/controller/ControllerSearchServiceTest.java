/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.web.controller;


import java.util.HashSet;
import org.apache.nifi.groups.ProcessGroup;
import org.apache.nifi.registry.flow.VersionControlInformation;
import org.apache.nifi.registry.variable.MutableVariableRegistry;
import org.apache.nifi.web.api.dto.search.SearchResultsDTO;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class ControllerSearchServiceTest {
    private MutableVariableRegistry variableRegistry;

    private ControllerSearchService service;

    private SearchResultsDTO searchResultsDTO;

    @Test
    public void testSearchInRootLevelAllAuthorizedNoVersionControl() {
        // root level PG
        final ProcessGroup rootProcessGroup = ControllerSearchServiceTest.setupMockedProcessGroup("root", null, true, variableRegistry, null);
        // first level PGs
        final ProcessGroup firstLevelAProcessGroup = ControllerSearchServiceTest.setupMockedProcessGroup("firstLevelA", rootProcessGroup, true, variableRegistry, null);
        final ProcessGroup firstLevelBProcessGroup = ControllerSearchServiceTest.setupMockedProcessGroup("firstLevelB", rootProcessGroup, true, variableRegistry, null);
        // second level PGs
        final ProcessGroup secondLevelAProcessGroup = ControllerSearchServiceTest.setupMockedProcessGroup("secondLevelA", firstLevelAProcessGroup, true, variableRegistry, null);
        final ProcessGroup secondLevelBProcessGroup = ControllerSearchServiceTest.setupMockedProcessGroup("secondLevelB", firstLevelBProcessGroup, true, variableRegistry, null);
        // third level PGs
        final ProcessGroup thirdLevelAProcessGroup = ControllerSearchServiceTest.setupMockedProcessGroup("thirdLevelA", secondLevelAProcessGroup, true, variableRegistry, null);
        final ProcessGroup thirdLevelBProcessGroup = ControllerSearchServiceTest.setupMockedProcessGroup("thirdLevelB", secondLevelAProcessGroup, true, variableRegistry, null);
        // link PGs together
        Mockito.doReturn(new HashSet<ProcessGroup>() {
            {
                add(firstLevelAProcessGroup);
                add(firstLevelBProcessGroup);
            }
        }).when(rootProcessGroup).getProcessGroups();
        Mockito.doReturn(new HashSet<ProcessGroup>() {
            {
                add(secondLevelAProcessGroup);
            }
        }).when(firstLevelAProcessGroup).getProcessGroups();
        Mockito.doReturn(new HashSet<ProcessGroup>() {
            {
                add(secondLevelBProcessGroup);
            }
        }).when(firstLevelBProcessGroup).getProcessGroups();
        Mockito.doReturn(new HashSet<ProcessGroup>() {
            {
                add(thirdLevelAProcessGroup);
                add(thirdLevelBProcessGroup);
            }
        }).when(secondLevelAProcessGroup).getProcessGroups();
        // setup processor
        ControllerSearchServiceTest.setupMockedProcessor("foobar", rootProcessGroup, true, variableRegistry);
        // perform search
        service.search(searchResultsDTO, "foo", rootProcessGroup);
        Assert.assertTrue(((searchResultsDTO.getProcessorResults().size()) == 1));
        Assert.assertTrue(searchResultsDTO.getProcessorResults().get(0).getId().equals("foobarId"));
        Assert.assertTrue(searchResultsDTO.getProcessorResults().get(0).getParentGroup().getId().equals("rootId"));
        Assert.assertTrue(searchResultsDTO.getProcessorResults().get(0).getParentGroup().getName().equals("root"));
        Assert.assertTrue(((searchResultsDTO.getProcessorResults().get(0).getVersionedGroup()) == null));
    }

    @Test
    public void testSearchInThirdLevelAllAuthorizedNoVersionControl() {
        // root level PG
        final ProcessGroup rootProcessGroup = ControllerSearchServiceTest.setupMockedProcessGroup("root", null, true, variableRegistry, null);
        // first level PGs
        final ProcessGroup firstLevelAProcessGroup = ControllerSearchServiceTest.setupMockedProcessGroup("firstLevelA", rootProcessGroup, true, variableRegistry, null);
        final ProcessGroup firstLevelBProcessGroup = ControllerSearchServiceTest.setupMockedProcessGroup("firstLevelB", rootProcessGroup, true, variableRegistry, null);
        // second level PGs
        final ProcessGroup secondLevelAProcessGroup = ControllerSearchServiceTest.setupMockedProcessGroup("secondLevelA", firstLevelAProcessGroup, true, variableRegistry, null);
        final ProcessGroup secondLevelBProcessGroup = ControllerSearchServiceTest.setupMockedProcessGroup("secondLevelB", firstLevelBProcessGroup, true, variableRegistry, null);
        // third level PGs
        final ProcessGroup thirdLevelAProcessGroup = ControllerSearchServiceTest.setupMockedProcessGroup("thirdLevelA", secondLevelAProcessGroup, true, variableRegistry, null);
        final ProcessGroup thirdLevelBProcessGroup = ControllerSearchServiceTest.setupMockedProcessGroup("thirdLevelB", secondLevelAProcessGroup, true, variableRegistry, null);
        // link PGs together
        Mockito.doReturn(new HashSet<ProcessGroup>() {
            {
                add(firstLevelAProcessGroup);
                add(firstLevelBProcessGroup);
            }
        }).when(rootProcessGroup).getProcessGroups();
        Mockito.doReturn(new HashSet<ProcessGroup>() {
            {
                add(secondLevelAProcessGroup);
            }
        }).when(firstLevelAProcessGroup).getProcessGroups();
        Mockito.doReturn(new HashSet<ProcessGroup>() {
            {
                add(secondLevelBProcessGroup);
            }
        }).when(firstLevelBProcessGroup).getProcessGroups();
        Mockito.doReturn(new HashSet<ProcessGroup>() {
            {
                add(thirdLevelAProcessGroup);
                add(thirdLevelBProcessGroup);
            }
        }).when(secondLevelAProcessGroup).getProcessGroups();
        // setup processor
        ControllerSearchServiceTest.setupMockedProcessor("foobar", thirdLevelAProcessGroup, true, variableRegistry);
        // perform search
        service.search(searchResultsDTO, "foo", rootProcessGroup);
        Assert.assertTrue(((searchResultsDTO.getProcessorResults().size()) == 1));
        Assert.assertTrue(searchResultsDTO.getProcessorResults().get(0).getId().equals("foobarId"));
        Assert.assertTrue(searchResultsDTO.getProcessorResults().get(0).getParentGroup().getId().equals("thirdLevelAId"));
        Assert.assertTrue(searchResultsDTO.getProcessorResults().get(0).getParentGroup().getName().equals("thirdLevelA"));
        Assert.assertTrue(((searchResultsDTO.getProcessorResults().get(0).getVersionedGroup()) == null));
    }

    @Test
    public void testSearchInThirdLevelParentNotAuthorizedNoVersionControl() {
        // root level PG
        final ProcessGroup rootProcessGroup = ControllerSearchServiceTest.setupMockedProcessGroup("root", null, true, variableRegistry, null);
        // first level PGs
        final ProcessGroup firstLevelAProcessGroup = ControllerSearchServiceTest.setupMockedProcessGroup("firstLevelA", rootProcessGroup, true, variableRegistry, null);
        final ProcessGroup firstLevelBProcessGroup = ControllerSearchServiceTest.setupMockedProcessGroup("firstLevelB", rootProcessGroup, true, variableRegistry, null);
        // second level PGs
        final ProcessGroup secondLevelAProcessGroup = ControllerSearchServiceTest.setupMockedProcessGroup("secondLevelA", firstLevelAProcessGroup, true, variableRegistry, null);
        final ProcessGroup secondLevelBProcessGroup = ControllerSearchServiceTest.setupMockedProcessGroup("secondLevelB", firstLevelBProcessGroup, true, variableRegistry, null);
        // third level PGs - not authorized
        final ProcessGroup thirdLevelAProcessGroup = ControllerSearchServiceTest.setupMockedProcessGroup("thirdLevelA", secondLevelAProcessGroup, false, variableRegistry, null);
        final ProcessGroup thirdLevelBProcessGroup = ControllerSearchServiceTest.setupMockedProcessGroup("thirdLevelB", secondLevelAProcessGroup, false, variableRegistry, null);
        // link PGs together
        Mockito.doReturn(new HashSet<ProcessGroup>() {
            {
                add(firstLevelAProcessGroup);
                add(firstLevelBProcessGroup);
            }
        }).when(rootProcessGroup).getProcessGroups();
        Mockito.doReturn(new HashSet<ProcessGroup>() {
            {
                add(secondLevelAProcessGroup);
            }
        }).when(firstLevelAProcessGroup).getProcessGroups();
        Mockito.doReturn(new HashSet<ProcessGroup>() {
            {
                add(secondLevelBProcessGroup);
            }
        }).when(firstLevelBProcessGroup).getProcessGroups();
        Mockito.doReturn(new HashSet<ProcessGroup>() {
            {
                add(thirdLevelAProcessGroup);
                add(thirdLevelBProcessGroup);
            }
        }).when(secondLevelAProcessGroup).getProcessGroups();
        // setup processor
        ControllerSearchServiceTest.setupMockedProcessor("foobar", thirdLevelAProcessGroup, true, variableRegistry);
        // perform search
        service.search(searchResultsDTO, "foo", rootProcessGroup);
        Assert.assertTrue(((searchResultsDTO.getProcessorResults().size()) == 1));
        Assert.assertTrue(searchResultsDTO.getProcessorResults().get(0).getId().equals("foobarId"));
        Assert.assertTrue(searchResultsDTO.getProcessorResults().get(0).getParentGroup().getId().equals("thirdLevelAId"));
        Assert.assertTrue(((searchResultsDTO.getProcessorResults().get(0).getParentGroup().getName()) == null));
        Assert.assertTrue(((searchResultsDTO.getProcessorResults().get(0).getVersionedGroup()) == null));
    }

    @Test
    public void testSearchInThirdLevelParentNotAuthorizedWithVersionControl() {
        // root level PG
        final ProcessGroup rootProcessGroup = ControllerSearchServiceTest.setupMockedProcessGroup("root", null, true, variableRegistry, null);
        // first level PGs
        final VersionControlInformation versionControlInformation = ControllerSearchServiceTest.setupVC();
        final ProcessGroup firstLevelAProcessGroup = ControllerSearchServiceTest.setupMockedProcessGroup("firstLevelA", rootProcessGroup, true, variableRegistry, versionControlInformation);
        final ProcessGroup firstLevelBProcessGroup = ControllerSearchServiceTest.setupMockedProcessGroup("firstLevelB", rootProcessGroup, true, variableRegistry, null);
        // second level PGs
        final ProcessGroup secondLevelAProcessGroup = ControllerSearchServiceTest.setupMockedProcessGroup("secondLevelA", firstLevelAProcessGroup, true, variableRegistry, null);
        final ProcessGroup secondLevelBProcessGroup = ControllerSearchServiceTest.setupMockedProcessGroup("secondLevelB", firstLevelBProcessGroup, true, variableRegistry, null);
        // third level PGs - not authorized
        final ProcessGroup thirdLevelAProcessGroup = ControllerSearchServiceTest.setupMockedProcessGroup("thirdLevelA", secondLevelAProcessGroup, false, variableRegistry, null);
        final ProcessGroup thirdLevelBProcessGroup = ControllerSearchServiceTest.setupMockedProcessGroup("thirdLevelB", secondLevelAProcessGroup, false, variableRegistry, null);
        // link PGs together
        Mockito.doReturn(new HashSet<ProcessGroup>() {
            {
                add(firstLevelAProcessGroup);
                add(firstLevelBProcessGroup);
            }
        }).when(rootProcessGroup).getProcessGroups();
        Mockito.doReturn(new HashSet<ProcessGroup>() {
            {
                add(secondLevelAProcessGroup);
            }
        }).when(firstLevelAProcessGroup).getProcessGroups();
        Mockito.doReturn(new HashSet<ProcessGroup>() {
            {
                add(secondLevelBProcessGroup);
            }
        }).when(firstLevelBProcessGroup).getProcessGroups();
        Mockito.doReturn(new HashSet<ProcessGroup>() {
            {
                add(thirdLevelAProcessGroup);
                add(thirdLevelBProcessGroup);
            }
        }).when(secondLevelAProcessGroup).getProcessGroups();
        // setup processor
        ControllerSearchServiceTest.setupMockedProcessor("foobar", thirdLevelAProcessGroup, true, variableRegistry);
        // perform search
        service.search(searchResultsDTO, "foo", rootProcessGroup);
        Assert.assertTrue(((searchResultsDTO.getProcessorResults().size()) == 1));
        Assert.assertTrue(searchResultsDTO.getProcessorResults().get(0).getId().equals("foobarId"));
        Assert.assertTrue(searchResultsDTO.getProcessorResults().get(0).getParentGroup().getId().equals("thirdLevelAId"));
        Assert.assertTrue(((searchResultsDTO.getProcessorResults().get(0).getParentGroup().getName()) == null));
        Assert.assertTrue(((searchResultsDTO.getProcessorResults().get(0).getVersionedGroup()) != null));
        Assert.assertTrue(searchResultsDTO.getProcessorResults().get(0).getVersionedGroup().getId().equals("firstLevelAId"));
        Assert.assertTrue(searchResultsDTO.getProcessorResults().get(0).getVersionedGroup().getName().equals("firstLevelA"));
    }

    @Test
    public void testSearchInThirdLevelParentNotAuthorizedWithVersionControlInTheGroup() {
        // root level PG
        final ProcessGroup rootProcessGroup = ControllerSearchServiceTest.setupMockedProcessGroup("root", null, true, variableRegistry, null);
        // first level PGs
        final ProcessGroup firstLevelAProcessGroup = ControllerSearchServiceTest.setupMockedProcessGroup("firstLevelA", rootProcessGroup, true, variableRegistry, null);
        final ProcessGroup firstLevelBProcessGroup = ControllerSearchServiceTest.setupMockedProcessGroup("firstLevelB", rootProcessGroup, true, variableRegistry, null);
        // second level PGs
        final ProcessGroup secondLevelAProcessGroup = ControllerSearchServiceTest.setupMockedProcessGroup("secondLevelA", firstLevelAProcessGroup, true, variableRegistry, null);
        final ProcessGroup secondLevelBProcessGroup = ControllerSearchServiceTest.setupMockedProcessGroup("secondLevelB", firstLevelBProcessGroup, true, variableRegistry, null);
        // third level PGs - not authorized
        final VersionControlInformation versionControlInformation = ControllerSearchServiceTest.setupVC();
        final ProcessGroup thirdLevelAProcessGroup = ControllerSearchServiceTest.setupMockedProcessGroup("thirdLevelA", secondLevelAProcessGroup, false, variableRegistry, versionControlInformation);
        final ProcessGroup thirdLevelBProcessGroup = ControllerSearchServiceTest.setupMockedProcessGroup("thirdLevelB", secondLevelAProcessGroup, false, variableRegistry, null);
        // link PGs together
        Mockito.doReturn(new HashSet<ProcessGroup>() {
            {
                add(firstLevelAProcessGroup);
                add(firstLevelBProcessGroup);
            }
        }).when(rootProcessGroup).getProcessGroups();
        Mockito.doReturn(new HashSet<ProcessGroup>() {
            {
                add(secondLevelAProcessGroup);
            }
        }).when(firstLevelAProcessGroup).getProcessGroups();
        Mockito.doReturn(new HashSet<ProcessGroup>() {
            {
                add(secondLevelBProcessGroup);
            }
        }).when(firstLevelBProcessGroup).getProcessGroups();
        Mockito.doReturn(new HashSet<ProcessGroup>() {
            {
                add(thirdLevelAProcessGroup);
                add(thirdLevelBProcessGroup);
            }
        }).when(secondLevelAProcessGroup).getProcessGroups();
        // setup processor
        ControllerSearchServiceTest.setupMockedProcessor("foobar", thirdLevelAProcessGroup, true, variableRegistry);
        // perform search
        service.search(searchResultsDTO, "foo", rootProcessGroup);
        Assert.assertTrue(((searchResultsDTO.getProcessorResults().size()) == 1));
        Assert.assertTrue(searchResultsDTO.getProcessorResults().get(0).getId().equals("foobarId"));
        Assert.assertTrue(searchResultsDTO.getProcessorResults().get(0).getParentGroup().getId().equals("thirdLevelAId"));
        Assert.assertTrue(((searchResultsDTO.getProcessorResults().get(0).getParentGroup().getName()) == null));
        Assert.assertTrue(((searchResultsDTO.getProcessorResults().get(0).getVersionedGroup()) != null));
        Assert.assertTrue(searchResultsDTO.getProcessorResults().get(0).getVersionedGroup().getId().equals("thirdLevelAId"));
        Assert.assertTrue(((searchResultsDTO.getProcessorResults().get(0).getVersionedGroup().getName()) == null));
    }
}

