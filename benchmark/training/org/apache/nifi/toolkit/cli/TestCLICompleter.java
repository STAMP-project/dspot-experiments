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
package org.apache.nifi.toolkit.cli;


import DefaultParser.ArgumentList;
import SessionVariable.NIFI_CLIENT_PROPS;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.nifi.toolkit.cli.impl.command.registry.NiFiRegistryCommandGroup;
import org.apache.nifi.toolkit.cli.impl.session.SessionVariable;
import org.jline.reader.Candidate;
import org.jline.reader.LineReader;
import org.jline.reader.impl.DefaultParser;
import org.junit.Assert;
import org.junit.Test;


public class TestCLICompleter {
    private static CLICompleter completer;

    private static LineReader lineReader;

    @Test
    public void testCompletionWithWordIndexNegative() {
        final DefaultParser.ArgumentList parsedLine = new DefaultParser.ArgumentList("", Collections.emptyList(), (-1), (-1), (-1));
        final List<Candidate> candidates = new ArrayList<>();
        TestCLICompleter.completer.complete(TestCLICompleter.lineReader, parsedLine, candidates);
        Assert.assertEquals(0, candidates.size());
    }

    @Test
    public void testCompletionWithWordIndexZero() {
        final DefaultParser.ArgumentList parsedLine = new DefaultParser.ArgumentList("", Collections.emptyList(), 0, (-1), (-1));
        final List<Candidate> candidates = new ArrayList<>();
        TestCLICompleter.completer.complete(TestCLICompleter.lineReader, parsedLine, candidates);
        Assert.assertEquals(TestCLICompleter.completer.getTopLevelCommands().size(), candidates.size());
    }

    @Test
    public void testCompletionWithWordIndexOneAndMatching() {
        final String topCommand = NiFiRegistryCommandGroup.REGISTRY_COMMAND_GROUP;
        final DefaultParser.ArgumentList parsedLine = new DefaultParser.ArgumentList("", Collections.singletonList(topCommand), 1, (-1), (-1));
        final List<Candidate> candidates = new ArrayList<>();
        TestCLICompleter.completer.complete(TestCLICompleter.lineReader, parsedLine, candidates);
        Assert.assertEquals(TestCLICompleter.completer.getSubCommands(topCommand).size(), candidates.size());
    }

    @Test
    public void testCompletionWithWordIndexOneAndNotMatching() {
        final String topCommand = "NOT-A-TOP-LEVEL-COMMAND";
        final DefaultParser.ArgumentList parsedLine = new DefaultParser.ArgumentList("", Collections.singletonList(topCommand), 1, (-1), (-1));
        final List<Candidate> candidates = new ArrayList<>();
        TestCLICompleter.completer.complete(TestCLICompleter.lineReader, parsedLine, candidates);
        Assert.assertEquals(0, candidates.size());
    }

    @Test
    public void testCompletionWithWordIndexTwoAndMatching() {
        final String topCommand = NiFiRegistryCommandGroup.REGISTRY_COMMAND_GROUP;
        final String subCommand = "list-buckets";
        final DefaultParser.ArgumentList parsedLine = new DefaultParser.ArgumentList("", Arrays.asList(topCommand, subCommand), 2, (-1), (-1));
        final List<Candidate> candidates = new ArrayList<>();
        TestCLICompleter.completer.complete(TestCLICompleter.lineReader, parsedLine, candidates);
        Assert.assertTrue(((candidates.size()) > 0));
        Assert.assertEquals(TestCLICompleter.completer.getOptions(subCommand).size(), candidates.size());
    }

    @Test
    public void testCompletionWithWordIndexTwoAndNotMatching() {
        final String topCommand = NiFiRegistryCommandGroup.REGISTRY_COMMAND_GROUP;
        final String subCommand = "NOT-A-TOP-LEVEL-COMMAND";
        final DefaultParser.ArgumentList parsedLine = new DefaultParser.ArgumentList("", Arrays.asList(topCommand, subCommand), 2, (-1), (-1));
        final List<Candidate> candidates = new ArrayList<>();
        TestCLICompleter.completer.complete(TestCLICompleter.lineReader, parsedLine, candidates);
        Assert.assertEquals(0, candidates.size());
    }

    @Test
    public void testCompletionWithMultipleArguments() {
        final String topCommand = NiFiRegistryCommandGroup.REGISTRY_COMMAND_GROUP;
        final String subCommand = "list-buckets";
        final DefaultParser.ArgumentList parsedLine = new DefaultParser.ArgumentList("", Arrays.asList(topCommand, subCommand, "-ks", "foo", "-kst", "JKS"), 6, (-1), (-1));
        final List<Candidate> candidates = new ArrayList<>();
        TestCLICompleter.completer.complete(TestCLICompleter.lineReader, parsedLine, candidates);
        Assert.assertTrue(((candidates.size()) > 0));
        Assert.assertEquals(TestCLICompleter.completer.getOptions(subCommand).size(), candidates.size());
    }

    @Test
    public void testCompletionWithFileArguments() {
        final String topCommand = NiFiRegistryCommandGroup.REGISTRY_COMMAND_GROUP;
        final String subCommand = "list-buckets";
        final DefaultParser.ArgumentList parsedLine = new DefaultParser.ArgumentList("", Arrays.asList(topCommand, subCommand, "-p", "src/test/resources/"), 3, (-1), (-1));
        final List<Candidate> candidates = new ArrayList<>();
        TestCLICompleter.completer.complete(TestCLICompleter.lineReader, parsedLine, candidates);
        Assert.assertTrue(((candidates.size()) > 0));
        boolean found = false;
        for (Candidate candidate : candidates) {
            if (candidate.value().equals("src/test/resources/test.properties")) {
                found = true;
                break;
            }
        }
        Assert.assertTrue(found);
    }

    @Test
    public void testCompletionForSessionVariableNames() {
        final String topCommand = "session";
        final String subCommand = "set";
        final DefaultParser.ArgumentList parsedLine = new DefaultParser.ArgumentList("", Arrays.asList(topCommand, subCommand), 2, (-1), (-1));
        final List<Candidate> candidates = new ArrayList<>();
        TestCLICompleter.completer.complete(TestCLICompleter.lineReader, parsedLine, candidates);
        Assert.assertTrue(((candidates.size()) > 0));
        Assert.assertEquals(SessionVariable.values().length, candidates.size());
    }

    @Test
    public void testCompletionForSessionVariableWithFiles() {
        final String topCommand = "session";
        final String subCommand = "set";
        final DefaultParser.ArgumentList parsedLine = new DefaultParser.ArgumentList("", Arrays.asList(topCommand, subCommand, NIFI_CLIENT_PROPS.getVariableName(), "src/test/resources/"), 3, (-1), (-1));
        final List<Candidate> candidates = new ArrayList<>();
        TestCLICompleter.completer.complete(TestCLICompleter.lineReader, parsedLine, candidates);
        Assert.assertTrue(((candidates.size()) > 0));
        boolean found = false;
        for (Candidate candidate : candidates) {
            if (candidate.value().equals("src/test/resources/test.properties")) {
                found = true;
                break;
            }
        }
        Assert.assertTrue(found);
    }
}

