/**
 * Copyright 2010 the original author or authors.
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
package samples.powermockito.junit4.annotationbased;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import samples.finalmocking.FinalDemo;
import samples.injectmocks.DependencyHolder;


/**
 * Asserts that {@link @InjectMocks} with PowerMock.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(FinalDemo.class)
public class InjectMocksAnnotationTest {
    @SuppressWarnings("unused")
    @Mock
    private FinalDemo finalDemo;

    @InjectMocks
    private DependencyHolder dependencyHolder = new DependencyHolder();

    @Test
    public void injectMocksWorks() {
        Assert.assertNotNull(dependencyHolder.getFinalDemo());
    }

    @Test
    public void testSay() throws Exception {
        FinalDemo tested = dependencyHolder.getFinalDemo();
        String expected = "Hello altered World";
        Mockito.when(tested.say("hello")).thenReturn("Hello altered World");
        String actual = tested.say("hello");
        Assert.assertEquals("Expected and actual did not match", expected, actual);
        // Should still be mocked by now.
        try {
            Mockito.verify(tested).say("world");
            Assert.fail("Should throw AssertionError!");
        } catch (AssertionError e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.is(CoreMatchers.containsString("Argument(s) are different! Wanted")));
        }
    }
}

