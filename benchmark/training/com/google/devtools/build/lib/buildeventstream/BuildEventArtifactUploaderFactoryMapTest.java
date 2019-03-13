/**
 * Copyright 2018 The Bazel Authors. All rights reserved.
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
package com.google.devtools.build.lib.buildeventstream;


import com.google.devtools.build.lib.runtime.BuildEventArtifactUploaderFactory;
import com.google.devtools.build.lib.runtime.BuildEventArtifactUploaderFactoryMap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link BuildEventArtifactUploaderFactoryMap}.
 */
@RunWith(JUnit4.class)
public final class BuildEventArtifactUploaderFactoryMapTest {
    private BuildEventArtifactUploaderFactoryMap uploaderFactories;

    private BuildEventArtifactUploaderFactory noConversionUploaderFactory;

    @Test
    public void testEmptyUploaders() throws Exception {
        BuildEventArtifactUploaderFactoryMap emptyUploader = new BuildEventArtifactUploaderFactoryMap.Builder().build();
        assertThat(emptyUploader.select(null).create(null).getClass()).isEqualTo(LocalFilesArtifactUploader.class);
    }

    @Test
    public void testAlphabeticalOrder() {
        assertThat(uploaderFactories.select(null).create(null).getClass()).isEqualTo(LocalFilesArtifactUploader.class);
    }

    @Test
    public void testSelectByName() throws Exception {
        assertThat(uploaderFactories.select("b")).isEqualTo(noConversionUploaderFactory);
    }
}

