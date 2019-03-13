/**
 * Copyright 2010 the original author or authors.
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
package powermock.classloading;


import java.net.URL;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.powermock.classloading.DeepCloner;


@Ignore("Test are failed on JDK more that 1.6. On Travis we can run only on JDK8 and JDK9")
public class ObjenesisDeepClonerTest {
    @Test
    public void clonesJavaInstances() throws Exception {
        final URL original = new URL("http://www.powermock.org");
        URL clone = new DeepCloner().clone(original);
        Assert.assertEquals(clone, original);
        Assert.assertNotSame(clone, original);
    }

    @Test
    public void clonesUnmodifiableLists() throws Exception {
        final UnmodifiableListExample original = new UnmodifiableListExample();
        UnmodifiableListExample clone = new DeepCloner().clone(original);
        Assert.assertEquals(clone, original);
        Assert.assertNotSame(clone, original);
    }

    @Test
    public void clonesArraysWithNullValues() throws Exception {
        Object[] original = new Object[]{ "Test", null };
        Object[] clone = new DeepCloner().clone(original);
        Assert.assertArrayEquals(clone, original);
        Assert.assertNotSame(clone, original);
    }
}

