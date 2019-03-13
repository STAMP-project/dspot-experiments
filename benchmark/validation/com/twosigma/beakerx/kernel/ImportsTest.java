/**
 * Copyright 2017 TWO SIGMA OPEN SOURCE, LLC
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.twosigma.beakerx.kernel;


import AddImportStatus.ADDED;
import AddImportStatus.EXISTS;
import com.twosigma.beakerx.jvm.classloader.BeakerXUrlClassLoader;
import org.junit.Test;


public class ImportsTest {
    private Imports imports;

    private BeakerXUrlClassLoader urlClassLoader;

    @Test
    public void shouldNotHaveDuplications() throws Exception {
        // given
        ImportPath anImport = new ImportPath("com.twosigma.beakerx.widget.IntSlider");
        // when
        assertThat(imports.add(anImport, urlClassLoader)).isEqualTo(ADDED);
        assertThat(imports.add(anImport, urlClassLoader)).isEqualTo(EXISTS);
        // then
        assertThat(imports.getImportPaths()).containsExactly(new ImportPath("com.twosigma.beakerx.widget.IntSlider"));
    }
}

