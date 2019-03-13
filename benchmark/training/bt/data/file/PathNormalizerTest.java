/**
 * Copyright (c) 2016?2017 Andrei Tomashpolskiy and individual contributors.
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
package bt.data.file;


import java.nio.file.FileSystem;
import java.util.Arrays;
import org.junit.Test;


public class PathNormalizerTest {
    private FileSystem fileSystem;

    private PathNormalizer normalizer;

    @Test
    public void testNormalizer_000() {
        verifyNormalization("_", "");
    }

    @Test
    public void testNormalizer_001() {
        verifyNormalization("_/_", "/");
    }

    @Test
    public void testNormalizer_002() {
        verifyNormalization("_/_/_", "//");
    }

    @Test
    public void testNormalizer_003() {
        verifyNormalization("_/a", "/a");
    }

    @Test
    public void testNormalizer_004() {
        verifyNormalization("a/_", "a/");
    }

    @Test
    public void testNormalizer_005() {
        verifyNormalization("_/_/a", "//a");
    }

    @Test
    public void testNormalizer_006() {
        verifyNormalization("a/_/_", "a//");
    }

    @Test
    public void testNormalizer_007() {
        verifyNormalization("_/a/_", "/a/");
    }

    @Test
    public void testNormalizer_008() {
        verifyNormalization("a/_/b", "a//b");
    }

    @Test
    public void testNormalizer_101() {
        verifyNormalization("_/_", " /");
    }

    @Test
    public void testNormalizer_102() {
        verifyNormalization("_/_", "/ ");
    }

    @Test
    public void testNormalizer_103() {
        verifyNormalization("_/_/_", " //");
    }

    @Test
    public void testNormalizer_104() {
        verifyNormalization("_/_/_", "// ");
    }

    @Test
    public void testNormalizer_105() {
        verifyNormalization("_/_/_", " // ");
    }

    @Test
    public void testNormalizer_106() {
        verifyNormalization("_/_/_", " / / ");
    }

    @Test
    public void testNormalizer_107() {
        verifyNormalization("_/a", "/ a");
    }

    @Test
    public void testNormalizer_108() {
        verifyNormalization("_/a b/_", "/a b/");
    }

    @Test
    public void testNormalizer_201() {
        verifyNormalization("_", ".");
    }

    @Test
    public void testNormalizer_202() {
        verifyNormalization("_", "..");
    }

    @Test
    public void testNormalizer_203() {
        verifyNormalization("_/_", "./");
    }

    @Test
    public void testNormalizer_204() {
        verifyNormalization("_/_", "/.");
    }

    @Test
    public void testNormalizer_205() {
        verifyNormalization("_/_", "../");
    }

    @Test
    public void testNormalizer_206() {
        verifyNormalization("_/_", "/..");
    }

    @Test
    public void testNormalizer_207() {
        verifyNormalization("_/_", "./.");
    }

    @Test
    public void testNormalizer_208() {
        verifyNormalization("_/_", "../..");
    }

    @Test
    public void testNormalizer_209() {
        verifyNormalization("_/_", ". /");
    }

    @Test
    public void testNormalizer_210() {
        verifyNormalization("_", ". .");
    }

    @Test
    public void testNormalizer_211() {
        verifyNormalization("_", ".. .");
    }

    @Test
    public void testNormalizer_212() {
        verifyNormalization("_", ". ..");
    }

    @Test
    public void testNormalizer_213() {
        verifyNormalization("_", ".. ..");
    }

    @Test
    public void testNormalizer_214() {
        verifyNormalization("_", ".. . ..");
    }

    @Test
    public void testNormalizer_215() {
        verifyNormalization("_/_/_/_/_", "./. / . //..");
    }

    @Test
    public void testNormalizer_301() {
        verifyNormalization("a. ..b", "a. ..b");
    }

    @Test
    public void testNormalizer_302() {
        verifyNormalization(".. .a", ".. .a ...");
    }

    @Test
    public void testNormalizer_303() {
        verifyNormalization(".a", ".a");
    }

    @Test
    public void testNormalizer_304() {
        verifyNormalization(".a/.b", ".a/.b");
    }

    @Test
    public void testNormalizer_305() {
        verifyNormalization(".a/b", ".a./b.");
    }

    @Test
    public void testNormalizer_401() {
        // '//'
        verifyNormalization("_/_/_", Arrays.asList("", "", ""));
    }

    @Test
    public void testNormalizer_402() {
        // '//..'
        verifyNormalization("_/_/_", Arrays.asList("/", ".."));
    }

    @Test
    public void testNormalizer_501() {
        verifyNormalization("a/b/c", Arrays.asList("a", "b", "c"));
    }

    @Test
    public void testNormalizer_601() {
        verifyNormalization("a/b/c", "a/b/c");
    }
}

