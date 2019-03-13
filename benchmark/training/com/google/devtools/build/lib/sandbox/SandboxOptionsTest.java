/**
 * Copyright 2016 The Bazel Authors. All rights reserved.
 */
/**
 *
 */
/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
/**
 * you may not use this file except in compliance with the License.
 */
/**
 * You may obtain a copy of the License at
 */
/**
 *
 */
/**
 * http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 *
 */
/**
 * Unless required by applicable law or agreed to in writing, software
 */
/**
 * distributed under the License is distributed on an "AS IS" BASIS,
 */
/**
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
/**
 * See the License for the specific language governing permissions and
 */
/**
 * limitations under the License.
 */
package com.google.devtools.build.lib.sandbox;


import com.google.devtools.common.options.OptionsParsingException;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@code SandboxOptions}.
 */
@RunWith(JUnit4.class)
public final class SandboxOptionsTest {
    private Map.Entry<String, String> pathPair;

    @Test
    public void testParsingAdditionalMounts_SinglePathWithoutColonSucess() throws Exception {
        String source = "/a/bc/def/gh";
        String target = source;
        String input = source;
        pathPair = new SandboxOptions.MountPairConverter().convert(input);
        SandboxOptionsTest.assertMountPair(pathPair, source, target);
    }

    @Test
    public void testParsingAdditionalMounts_SinglePathWithColonSucess() throws Exception {
        String source = "/a/b:c/def/gh";
        String target = source;
        String input = "/a/b\\:c/def/gh";
        pathPair = new SandboxOptions.MountPairConverter().convert(input);
        SandboxOptionsTest.assertMountPair(pathPair, source, target);
    }

    @Test
    public void testParsingAdditionalMounts_PathPairWithoutColonSucess() throws Exception {
        String source = "/a/bc/def/gh";
        String target = "/1/2/3/4/5";
        String input = (source + ":") + target;
        pathPair = new SandboxOptions.MountPairConverter().convert(input);
        SandboxOptionsTest.assertMountPair(pathPair, source, target);
    }

    @Test
    public void testParsingAdditionalMounts_PathPairWithColonSucess() throws Exception {
        String source = "/a:/bc:/d:ef/gh";
        String target = ":/1/2/3/4/5";
        String input = "/a\\:/bc\\:/d\\:ef/gh:\\:/1/2/3/4/5";
        pathPair = new SandboxOptions.MountPairConverter().convert(input);
        SandboxOptionsTest.assertMountPair(pathPair, source, target);
    }

    @Test
    public void testParsingAdditionalMounts_TooManyPaths() throws Exception {
        String input = "a/bc/def/gh:/1/2/3:x/y/z";
        try {
            pathPair = new SandboxOptions.MountPairConverter().convert(input);
            Assert.fail();
        } catch (OptionsParsingException e) {
            assertThat(e).hasMessageThat().isEqualTo(("Input must be a single path to mount inside the sandbox or " + "a mounting pair in the form of 'source:target'"));
        }
    }

    @Test
    public void testParsingAdditionalMounts_EmptyInput() throws Exception {
        String input = "";
        try {
            pathPair = new SandboxOptions.MountPairConverter().convert(input);
            Assert.fail();
        } catch (OptionsParsingException e) {
            assertThat((((("Input " + input) + " contains one or more empty paths. ") + "Input must be a single path to mount inside the sandbox or ") + "a mounting pair in the form of 'source:target'")).isEqualTo(e.getMessage());
        }
    }
}

