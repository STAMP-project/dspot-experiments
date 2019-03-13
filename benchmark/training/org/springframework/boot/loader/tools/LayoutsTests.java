/**
 * Copyright 2012-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.boot.loader.tools;


import Layouts.Jar;
import Layouts.War;
import LibraryScope.COMPILE;
import LibraryScope.CUSTOM;
import LibraryScope.PROVIDED;
import LibraryScope.RUNTIME;
import java.io.File;
import org.junit.Test;


/**
 * Tests for {@link Layouts}.
 *
 * @author Phillip Webb
 * @author Andy Wilkinson
 */
public class LayoutsTests {
    @Test
    public void jarFile() {
        assertThat(Layouts.forFile(new File("test.jar"))).isInstanceOf(Jar.class);
        assertThat(Layouts.forFile(new File("test.JAR"))).isInstanceOf(Jar.class);
        assertThat(Layouts.forFile(new File("test.jAr"))).isInstanceOf(Jar.class);
        assertThat(Layouts.forFile(new File("te.st.jar"))).isInstanceOf(Jar.class);
    }

    @Test
    public void warFile() {
        assertThat(Layouts.forFile(new File("test.war"))).isInstanceOf(War.class);
        assertThat(Layouts.forFile(new File("test.WAR"))).isInstanceOf(War.class);
        assertThat(Layouts.forFile(new File("test.wAr"))).isInstanceOf(War.class);
        assertThat(Layouts.forFile(new File("te.st.war"))).isInstanceOf(War.class);
    }

    @Test
    public void unknownFile() {
        assertThatIllegalStateException().isThrownBy(() -> Layouts.forFile(new File("test.txt"))).withMessageContaining("Unable to deduce layout for 'test.txt'");
    }

    @Test
    public void jarLayout() {
        Layout layout = new Layouts.Jar();
        assertThat(layout.getLibraryDestination("lib.jar", COMPILE)).isEqualTo("BOOT-INF/lib/");
        assertThat(layout.getLibraryDestination("lib.jar", CUSTOM)).isEqualTo("BOOT-INF/lib/");
        assertThat(layout.getLibraryDestination("lib.jar", PROVIDED)).isEqualTo("BOOT-INF/lib/");
        assertThat(layout.getLibraryDestination("lib.jar", RUNTIME)).isEqualTo("BOOT-INF/lib/");
    }

    @Test
    public void warLayout() {
        Layout layout = new Layouts.War();
        assertThat(layout.getLibraryDestination("lib.jar", COMPILE)).isEqualTo("WEB-INF/lib/");
        assertThat(layout.getLibraryDestination("lib.jar", CUSTOM)).isEqualTo("WEB-INF/lib/");
        assertThat(layout.getLibraryDestination("lib.jar", PROVIDED)).isEqualTo("WEB-INF/lib-provided/");
        assertThat(layout.getLibraryDestination("lib.jar", RUNTIME)).isEqualTo("WEB-INF/lib/");
    }
}

