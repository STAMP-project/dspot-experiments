/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.internal.yaml;


import YamlNode.UNNAMED_NODE;
import com.google.common.io.CharStreams;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class YamlTest {
    private static final int NOT_EXISTING = 42;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test(expected = YamlException.class)
    public void testLoadOnJdk6Throws() {
        HazelcastTestSupport.assumeThatJDK6();
        YamlLoader.load("irrelevant");
    }

    @Test
    public void testYamlFromInputStream() {
        HazelcastTestSupport.assumeThatJDK8OrHigher();
        InputStream inputStream = YamlTest.class.getClassLoader().getResourceAsStream("yaml-test-root-map.yaml");
        YamlNode root = YamlLoader.load(inputStream, "root-map");
        verify(root);
    }

    @Test
    public void testYamlFromInputStreamWithoutRootName() {
        HazelcastTestSupport.assumeThatJDK8OrHigher();
        InputStream inputStream = YamlTest.class.getClassLoader().getResourceAsStream("yaml-test-root-map.yaml");
        YamlNode root = YamlLoader.load(inputStream);
        verify(YamlUtil.asMapping(root).childAsMapping("root-map"));
    }

    @Test
    public void testYamlExtendedTestFromInputStream() {
        HazelcastTestSupport.assumeThatJDK8OrHigher();
        InputStream inputStream = YamlTest.class.getClassLoader().getResourceAsStream("yaml-test-root-map-extended.yaml");
        YamlNode root = YamlLoader.load(inputStream, "root-map");
        verify(root);
        verifyExtendedYaml(root);
    }

    @Test
    public void testJsonFromInputStream() {
        HazelcastTestSupport.assumeThatJDK8OrHigher();
        InputStream inputStream = YamlTest.class.getClassLoader().getResourceAsStream("yaml-test-root-map.json");
        YamlNode root = YamlLoader.load(inputStream, "root-map");
        verify(root);
    }

    @Test
    public void testYamlFromReader() {
        HazelcastTestSupport.assumeThatJDK8OrHigher();
        InputStream inputStream = YamlTest.class.getClassLoader().getResourceAsStream("yaml-test-root-map.yaml");
        InputStreamReader reader = new InputStreamReader(inputStream);
        YamlNode root = YamlLoader.load(reader, "root-map");
        verify(root);
    }

    @Test
    public void testYamlFromReaderWithoutRootName() {
        HazelcastTestSupport.assumeThatJDK8OrHigher();
        InputStream inputStream = YamlTest.class.getClassLoader().getResourceAsStream("yaml-test-root-map.yaml");
        InputStreamReader reader = new InputStreamReader(inputStream);
        YamlNode root = YamlLoader.load(reader);
        verify(YamlUtil.asMapping(root).childAsMapping("root-map"));
    }

    @Test
    public void testYamlFromString() throws IOException {
        HazelcastTestSupport.assumeThatJDK8OrHigher();
        InputStream inputStream = YamlTest.class.getClassLoader().getResourceAsStream("yaml-test-root-map.yaml");
        InputStreamReader reader = new InputStreamReader(inputStream);
        String yamlString = CharStreams.toString(reader);
        YamlNode root = YamlLoader.load(yamlString, "root-map");
        verify(root);
    }

    @Test
    public void testYamlFromStringWithoutRootMap() throws IOException {
        HazelcastTestSupport.assumeThatJDK8OrHigher();
        InputStream inputStream = YamlTest.class.getClassLoader().getResourceAsStream("yaml-test-root-map.yaml");
        InputStreamReader reader = new InputStreamReader(inputStream);
        String yamlString = CharStreams.toString(reader);
        YamlNode root = YamlLoader.load(yamlString);
        verify(YamlUtil.asMapping(root).childAsMapping("root-map"));
    }

    @Test
    public void testLoadingInvalidYamlFromInputStream() {
        HazelcastTestSupport.assumeThatJDK8OrHigher();
        InputStream inputStream = YamlTest.class.getClassLoader().getResourceAsStream("yaml-test-invalid.yaml");
        expectedException.expect(YamlException.class);
        YamlLoader.load(inputStream);
    }

    @Test
    public void testLoadingInvalidYamlFromInputStreamWithRootName() {
        HazelcastTestSupport.assumeThatJDK8OrHigher();
        InputStream inputStream = YamlTest.class.getClassLoader().getResourceAsStream("yaml-test-invalid.yaml");
        expectedException.expect(YamlException.class);
        YamlLoader.load(inputStream, "root-map");
    }

    @Test
    public void testLoadingInvalidYamlFromReader() {
        HazelcastTestSupport.assumeThatJDK8OrHigher();
        InputStream inputStream = YamlTest.class.getClassLoader().getResourceAsStream("yaml-test-invalid.yaml");
        InputStreamReader reader = new InputStreamReader(inputStream);
        expectedException.expect(YamlException.class);
        YamlLoader.load(reader);
    }

    @Test
    public void testLoadingInvalidYamlFromReaderWithRootName() {
        HazelcastTestSupport.assumeThatJDK8OrHigher();
        InputStream inputStream = YamlTest.class.getClassLoader().getResourceAsStream("yaml-test-invalid.yaml");
        InputStreamReader reader = new InputStreamReader(inputStream);
        expectedException.expect(YamlException.class);
        YamlLoader.load(reader, "root-map");
    }

    @Test
    public void testLoadingInvalidYamlFromString() throws IOException {
        HazelcastTestSupport.assumeThatJDK8OrHigher();
        InputStream inputStream = YamlTest.class.getClassLoader().getResourceAsStream("yaml-test-invalid.yaml");
        InputStreamReader reader = new InputStreamReader(inputStream);
        String yamlString = CharStreams.toString(reader);
        expectedException.expect(YamlException.class);
        YamlLoader.load(yamlString);
    }

    @Test
    public void testLoadingInvalidYamlFromStringWithRootName() throws IOException {
        HazelcastTestSupport.assumeThatJDK8OrHigher();
        InputStream inputStream = YamlTest.class.getClassLoader().getResourceAsStream("yaml-test-invalid.yaml");
        InputStreamReader reader = new InputStreamReader(inputStream);
        String yamlString = CharStreams.toString(reader);
        expectedException.expect(YamlException.class);
        YamlLoader.load(yamlString, "root-map");
    }

    @Test
    public void testInvalidScalarValueTypeMap() {
        HazelcastTestSupport.assumeThatJDK8OrHigher();
        YamlMapping rootMap = getYamlRoot();
        YamlMapping embeddedMap = rootMap.childAsMapping("embedded-map");
        expectedException.expect(ClassCastException.class);
        int notAnInt = ((Integer) (embeddedMap.childAsScalarValue("scalar-str")));
    }

    @Test
    public void testInvalidScalarValueTypeSeq() {
        HazelcastTestSupport.assumeThatJDK8OrHigher();
        YamlMapping rootMap = getYamlRoot();
        YamlSequence embeddedList = rootMap.childAsMapping("embedded-map").childAsSequence("embedded-list");
        expectedException.expect(ClassCastException.class);
        int notAnInt = ((Integer) (embeddedList.childAsScalarValue(0)));
    }

    @Test
    public void testInvalidScalarValueTypeHintedMap() {
        HazelcastTestSupport.assumeThatJDK8OrHigher();
        YamlMapping rootMap = getYamlRoot();
        YamlMapping embeddedMap = rootMap.childAsMapping("embedded-map");
        embeddedMap.childAsScalarValue("scalar-str", String.class);
        expectedException.expect(YamlException.class);
        embeddedMap.childAsScalarValue("scalar-str", Integer.class);
    }

    @Test
    public void testInvalidScalarValueTypeHintedSeq() {
        HazelcastTestSupport.assumeThatJDK8OrHigher();
        YamlMapping rootMap = getYamlRoot();
        YamlSequence embeddedList = rootMap.childAsMapping("embedded-map").childAsSequence("embedded-list");
        embeddedList.childAsScalarValue(0, String.class);
        expectedException.expect(YamlException.class);
        embeddedList.childAsScalarValue(0, Integer.class);
    }

    @Test
    public void testNotExistingMappingFromMap() {
        HazelcastTestSupport.assumeThatJDK8OrHigher();
        Assert.assertNull(getYamlRoot().childAsMapping("not-existing"));
    }

    @Test
    public void testNotExistingSequenceFromMap() {
        HazelcastTestSupport.assumeThatJDK8OrHigher();
        Assert.assertNull(getYamlRoot().childAsSequence("not-existing"));
    }

    @Test
    public void testNotExistingScalarFromMap() {
        HazelcastTestSupport.assumeThatJDK8OrHigher();
        Assert.assertNull(getYamlRoot().childAsScalar("not-existing"));
    }

    @Test
    public void testNotExistingMappingFromSeq() {
        HazelcastTestSupport.assumeThatJDK8OrHigher();
        YamlSequence seq = getYamlRoot().childAsMapping("embedded-map").childAsSequence("embedded-list");
        Assert.assertNull(seq.childAsMapping(YamlTest.NOT_EXISTING));
    }

    @Test
    public void testNotExistingSequenceFromSeq() {
        HazelcastTestSupport.assumeThatJDK8OrHigher();
        YamlSequence seq = getYamlRoot().childAsMapping("embedded-map").childAsSequence("embedded-list");
        Assert.assertNull(seq.childAsSequence(YamlTest.NOT_EXISTING));
    }

    @Test
    public void testNotExistingScalarFromSeq() {
        HazelcastTestSupport.assumeThatJDK8OrHigher();
        YamlSequence seq = getYamlRoot().childAsMapping("embedded-map").childAsSequence("embedded-list");
        Assert.assertNull(seq.childAsScalar(YamlTest.NOT_EXISTING));
    }

    @Test
    public void testInvalidNodeTypeNotAMapping() {
        HazelcastTestSupport.assumeThatJDK8OrHigher();
        InputStream inputStream = YamlTest.class.getClassLoader().getResourceAsStream("yaml-test-root-map.yaml");
        YamlNode root = YamlLoader.load(inputStream, "root-map");
        YamlMapping embeddedMap = childAsMapping("embedded-map");
        expectedException.expect(YamlException.class);
        embeddedMap.childAsMapping("embedded-list");
    }

    @Test
    public void testInvalidNodeTypeNotASeq() {
        HazelcastTestSupport.assumeThatJDK8OrHigher();
        YamlMapping rootMap = getYamlRoot();
        expectedException.expect(YamlException.class);
        rootMap.childAsSequence("embedded-map");
    }

    @Test
    public void testInvalidNodeTypeNotAScalar() {
        HazelcastTestSupport.assumeThatJDK8OrHigher();
        YamlMapping rootMap = getYamlRoot();
        expectedException.expect(YamlException.class);
        rootMap.childAsScalar("embedded-map");
    }

    @Test
    public void testIterateChildrenMap() {
        HazelcastTestSupport.assumeThatJDK8OrHigher();
        YamlMapping embeddedMap = getYamlRoot().childAsMapping("embedded-map");
        int childCount = 0;
        for (YamlNode node : embeddedMap.children()) {
            Assert.assertNotNull(node);
            childCount++;
        }
        Assert.assertEquals(6, childCount);
    }

    @Test
    public void testIterateChildrenSeq() {
        HazelcastTestSupport.assumeThatJDK8OrHigher();
        YamlSequence embeddedList = getYamlRoot().childAsMapping("embedded-map").childAsSequence("embedded-list");
        int childCount = 0;
        for (YamlNode node : embeddedList.children()) {
            Assert.assertNotNull(node);
            childCount++;
        }
        Assert.assertEquals(4, childCount);
    }

    @Test
    public void testParentOfRootIsNull() {
        HazelcastTestSupport.assumeThatJDK8OrHigher();
        Assert.assertNull(getYamlRoot().parent());
    }

    @Test
    public void testParentOfEmbeddedMapIsRoot() {
        HazelcastTestSupport.assumeThatJDK8OrHigher();
        YamlMapping root = getYamlRoot();
        Assert.assertSame(root, root.childAsMapping("embedded-map").parent());
    }

    @Test
    public void testParentOfScalarIntIsEmbeddedMap() {
        HazelcastTestSupport.assumeThatJDK8OrHigher();
        YamlMapping embeddedMap = getYamlRoot().childAsMapping("embedded-map");
        Assert.assertSame(embeddedMap, embeddedMap.childAsScalar("scalar-int").parent());
    }

    @Test
    public void testNameOfMap() {
        HazelcastTestSupport.assumeThatJDK8OrHigher();
        Assert.assertEquals("embedded-map", getYamlRoot().childAsMapping("embedded-map").nodeName());
    }

    @Test
    public void testNameOfSeq() {
        HazelcastTestSupport.assumeThatJDK8OrHigher();
        Assert.assertEquals("embedded-list", getYamlRoot().childAsMapping("embedded-map").childAsSequence("embedded-list").nodeName());
    }

    @Test
    public void testNameOfNamedScalar() {
        HazelcastTestSupport.assumeThatJDK8OrHigher();
        Assert.assertEquals("scalar-int", getYamlRoot().childAsMapping("embedded-map").childAsScalar("scalar-int").nodeName());
    }

    @Test
    public void testNameOfUnnamedScalar() {
        HazelcastTestSupport.assumeThatJDK8OrHigher();
        Assert.assertSame(UNNAMED_NODE, getYamlRoot().childAsMapping("embedded-map").childAsSequence("embedded-list").childAsScalar(0).nodeName());
    }

    @Test
    public void testYamlListInRoot() throws IOException {
        HazelcastTestSupport.assumeThatJDK8OrHigher();
        InputStream inputStream = YamlTest.class.getClassLoader().getResourceAsStream("yaml-test-root-seq.yaml");
        InputStreamReader reader = new InputStreamReader(inputStream);
        String yamlString = CharStreams.toString(reader);
        YamlNode root = YamlLoader.load(yamlString);
        Assert.assertTrue((root instanceof YamlSequence));
        YamlSequence rootSeq = YamlUtil.asSequence(root);
        Assert.assertEquals(42, ((Integer) (rootSeq.childAsScalarValue(0))).intValue());
        YamlMapping map = rootSeq.childAsMapping(1);
        Assert.assertEquals(UNNAMED_NODE, map.nodeName());
        Assert.assertEquals("embedded-map", map.childAsMapping("embedded-map").nodeName());
    }
}

