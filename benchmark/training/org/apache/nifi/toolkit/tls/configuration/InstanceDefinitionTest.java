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
package org.apache.nifi.toolkit.tls.configuration;


import java.util.Arrays;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.junit.Test;


public class InstanceDefinitionTest {
    @Test
    public void testCreateDefinitionKeyPassword() {
        testCreateDefinition("testHostname", 4, "keyStorePassword", "keyPassword", "trustStorePassword");
    }

    @Test
    public void testCreateDefinitionNoKeyPassword() {
        testCreateDefinition("testHostname", 5, "keyStorePassword", null, "trustStorePassword");
    }

    @Test
    public void testCreateDefinitionsSingleHostSingleName() {
        testCreateDefinitions(Arrays.asList("hostname"), Arrays.asList("hostname"), Arrays.asList(1), false);
    }

    @Test
    public void testCreateDefinitionsSingleHostnameOneNumberInParens() {
        testCreateDefinitions(Arrays.asList("hostname(20)"), IntStream.range(1, 21).mapToObj(( operand) -> "hostname").collect(Collectors.toList()), integerRange(1, 20).collect(Collectors.toList()), false);
    }

    @Test
    public void testCreateDefinitionsSingleHostnameTwoNumbersInParens() {
        testCreateDefinitions(Arrays.asList("hostname(5-20)"), IntStream.range(5, 21).mapToObj(( operand) -> "hostname").collect(Collectors.toList()), integerRange(5, 20).collect(Collectors.toList()), false);
    }

    @Test
    public void testCreateDefinitionsMultipleHostnamesWithMultipleNumbers() {
        testCreateDefinitions(Arrays.asList("host[10]name[02-5](20)"), integerRange(1, 10).flatMap(( v) -> integerRange(2, 5).flatMap(( v2) -> integerRange(1, 20).map(( v3) -> (("host" + v) + "name") + (String.format("%02d", v2))))).collect(Collectors.toList()), integerRange(1, 10).flatMap(( val) -> integerRange(2, 5).flatMap(( val2) -> integerRange(1, 20))).collect(Collectors.toList()), false);
    }

    @Test
    public void testCreateDefinitionsStream() {
        testCreateDefinitions(Arrays.asList("host", "name"), Arrays.asList("host", "name"), Arrays.asList(1, 1), true);
    }

    @Test
    public void testCreateDefinitionsStreamNonNullKeyPasswords() {
        testCreateDefinitions(Arrays.asList("host", "name"), Arrays.asList("host", "name"), Arrays.asList(1, 1), false);
    }
}

