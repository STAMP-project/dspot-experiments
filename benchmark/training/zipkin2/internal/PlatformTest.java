/**
 * Copyright 2015-2018 The OpenZipkin Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package zipkin2.internal;


import Platform.Jre6;
import Platform.Jre7;
import Platform.Jre8;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


// Added to declutter console: tells power mock not to mess with implicit classes we aren't testing
@RunWith(PowerMockRunner.class)
@PowerMockIgnore({ "org.apache.logging.*", "javax.script.*" })
@PrepareForTest(Platform.class)
public class PlatformTest {
    @Test
    public void findPlatform_jre8Wins() throws ClassNotFoundException {
        mockStatic(Class.class);
        when(Class.forName("java.io.UncheckedIOException")).thenReturn(null);
        assertThat(Platform.findPlatform()).isInstanceOf(Jre8.class);
    }

    @Test
    public void findPlatform_fallsBackToJre7() throws ClassNotFoundException {
        mockStatic(Class.class);
        when(Class.forName("java.io.UncheckedIOException")).thenThrow(new ClassNotFoundException());
        when(Class.forName("java.util.concurrent.ThreadLocalRandom")).thenReturn(null);
        assertThat(Platform.findPlatform()).isInstanceOf(Jre7.class);
    }

    @Test
    public void findPlatform_fallsBackToJre6() throws ClassNotFoundException {
        mockStatic(Class.class);
        when(Class.forName("java.io.UncheckedIOException")).thenThrow(new ClassNotFoundException());
        when(Class.forName("java.util.concurrent.ThreadLocalRandom")).thenThrow(new ClassNotFoundException());
        assertThat(Platform.findPlatform()).isInstanceOf(Jre6.class);
    }
}

