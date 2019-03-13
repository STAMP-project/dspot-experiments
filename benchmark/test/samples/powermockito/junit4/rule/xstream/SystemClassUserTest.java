/**
 * Copyright 2008 the original author or authors.
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
package samples.powermockito.junit4.rule.xstream;


import java.net.URLEncoder;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import samples.system.SystemClassUser;


/**
 * Demonstrates PowerMockito's ability to mock non-final and final system
 * classes. To mock a system class you need to prepare the calling class for
 * testing. I.e. let's say you're testing class A which interacts with
 * URLEncoder then you would do:
 *
 * <pre>
 *
 * &#064;PrepareForTest({A.class})
 *
 * </pre>
 */
@PrepareForTest({ SystemClassUser.class })
@Ignore
public class SystemClassUserTest {
    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    @Test
    public void assertThatMockingOfNonFinalSystemClassesWorks() throws Exception {
        mockStatic(URLEncoder.class);
        when(URLEncoder.encode("string", "enc")).thenReturn("something");
        Assert.assertEquals("something", new SystemClassUser().performEncode());
    }

    @Test
    public void assertThatMockingOfTheRuntimeSystemClassWorks() throws Exception {
        mockStatic(Runtime.class);
        Runtime runtimeMock = mock(Runtime.class);
        Process processMock = mock(Process.class);
        when(Runtime.getRuntime()).thenReturn(runtimeMock);
        when(runtimeMock.exec("command")).thenReturn(processMock);
        Assert.assertSame(processMock, new SystemClassUser().executeCommand());
    }

    @Test
    public void assertThatMockingOfFinalSystemClassesWorks() throws Exception {
        mockStatic(System.class);
        when(System.getProperty("property")).thenReturn("my property");
        Assert.assertEquals("my property", new SystemClassUser().getSystemProperty());
    }

    @Test
    public void assertThatPartialMockingOfFinalSystemClassesWorks() throws Exception {
        spy(System.class);
        when(System.nanoTime()).thenReturn(2L);
        new SystemClassUser().doMoreComplicatedStuff();
        Assert.assertEquals("2", System.getProperty("nanoTime"));
    }

    @Test
    public void assertThatMockingOfCollectionsWork() throws Exception {
        List<?> list = new LinkedList<Object>();
        mockStatic(Collections.class);
        Collections.shuffle(list);
        new SystemClassUser().shuffleCollection(list);
        verifyStatic(Collections.class, Mockito.times(2));
        Collections.shuffle(list);
    }

    @Test
    public void assertThatPartialMockingOfFinalSystemClassesWorksForNonVoidMethods() throws Exception {
        spy(System.class);
        when(System.getProperty("property")).thenReturn("my property");
        final SystemClassUser systemClassUser = new SystemClassUser();
        systemClassUser.copyProperty("to", "property");
    }

    @Test
    public void assertThatMockingStringWorks() throws Exception {
        mockStatic(String.class);
        final String string = "string";
        final String args = "args";
        final String returnValue = "returnValue";
        when(String.format(string, args)).thenReturn(returnValue);
        final SystemClassUser systemClassUser = new SystemClassUser();
        Assert.assertEquals(systemClassUser.format(string, args), returnValue);
    }
}

