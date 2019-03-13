/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to you under the Apache License, Version 2.0 (the
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
package org.apache.zeppelin.sap.universe;


import StringUtils.EMPTY;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.zeppelin.completer.CachedCompleter;
import org.junit.Assert;
import org.junit.Test;


/**
 * Universe completer unit tests
 */
public class UniverseCompleterTest {
    private UniverseCompleter universeCompleter;

    private UniverseUtil universeUtil;

    private UniverseClient universeClient;

    @Test
    public void testCreateUniverseNameCompleter() {
        String buffer = "universe [";
        List<CharSequence> candidates = new ArrayList<>();
        universeCompleter.createOrUpdate(universeClient, null, buffer, 9);
        CachedCompleter completer = universeCompleter.getUniverseCompleter();
        Assert.assertNull(completer);
        universeCompleter.createOrUpdate(universeClient, null, buffer, 10);
        completer = universeCompleter.getUniverseCompleter();
        Assert.assertNotNull(completer);
        completer.getCompleter().complete(EMPTY, 0, candidates);
        Assert.assertEquals(3, candidates.size());
    }

    @Test
    public void testCreateUniverseNodesCompleter() {
        String buffer = "universe [testUniverse]; select [";
        List<CharSequence> candidates = new ArrayList<>();
        universeCompleter.createOrUpdate(universeClient, null, buffer, 32);
        Map<String, CachedCompleter> completerMap = universeCompleter.getUniverseInfoCompletersMap();
        Assert.assertFalse(completerMap.containsKey("testUniverse"));
        universeCompleter.createOrUpdate(universeClient, null, buffer, 33);
        completerMap = universeCompleter.getUniverseInfoCompletersMap();
        Assert.assertTrue(completerMap.containsKey("testUniverse"));
        CachedCompleter completer = completerMap.get("testUniverse");
        completer.getCompleter().complete(EMPTY, 0, candidates);
        Assert.assertEquals(3, candidates.size());
        List<String> candidatesStrings = new ArrayList<>();
        for (Object o : candidates) {
            UniverseNodeInfo info = ((UniverseNodeInfo) (o));
            candidatesStrings.add(info.getName());
        }
        List<String> expected = Arrays.asList("Filter", "Measure", "Dimension");
        Collections.sort(candidatesStrings);
        Collections.sort(expected);
        Assert.assertEquals(expected, candidatesStrings);
    }

    @Test
    public void testNestedUniverseNodes() {
        String buffer = "universe [testUniverse]; select [Dimension].[Test].[n";
        List<CharSequence> candidates = new ArrayList<>();
        universeCompleter.createOrUpdate(universeClient, null, buffer, 53);
        Map<String, CachedCompleter> completerMap = universeCompleter.getUniverseInfoCompletersMap();
        Assert.assertTrue(completerMap.containsKey("testUniverse"));
        CachedCompleter completer = completerMap.get("testUniverse");
        completer.getCompleter().complete("[Dimension].[Test].[n", 21, candidates);
        Assert.assertEquals(2, candidates.size());
        List<String> candidatesStrings = new ArrayList<>();
        for (Object o : candidates) {
            UniverseNodeInfo info = ((UniverseNodeInfo) (o));
            candidatesStrings.add(info.getName());
        }
        List<String> expected = Arrays.asList("name1", "name2");
        Collections.sort(candidatesStrings);
        Collections.sort(expected);
        Assert.assertEquals(expected, candidatesStrings);
    }
}

