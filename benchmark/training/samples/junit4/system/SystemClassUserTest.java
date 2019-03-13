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
package samples.junit4.system;


import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import samples.system.SystemClassUser;


/**
 * Demonstrates PowerMock's ability to mock non-final and final system classes.
 * To mock a system class you need to prepare the calling class for testing.
 * I.e. let's say you're testing class A which interacts with URLEncoder then
 * you would do:
 *
 * <pre>
 *
 * &#064;PrepareForTest({A.class})
 *
 * </pre>
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ SystemClassUser.class })
public class SystemClassUserTest {
    @Test
    public void assertThatMockingOfNonFinalSystemClassesWorks() throws Exception {
        mockStatic(URLEncoder.class);
        expect(URLEncoder.encode("string", "enc")).andReturn("something");
        replayAll();
        Assert.assertEquals("something", new SystemClassUser().performEncode());
        verifyAll();
    }

    @Test
    public void assertThatMockingOfTheRuntimeSystemClassWorks() throws Exception {
        mockStatic(Runtime.class);
        Runtime runtimeMock = createMock(Runtime.class);
        Process processMock = createMock(Process.class);
        expect(Runtime.getRuntime()).andReturn(runtimeMock);
        expect(runtimeMock.exec("command")).andReturn(processMock);
        replayAll();
        Assert.assertSame(processMock, new SystemClassUser().executeCommand());
        verifyAll();
    }

    @Test
    public void assertThatMockingOfFinalSystemClassesWorks() throws Exception {
        mockStatic(System.class);
        expect(System.getProperty("property")).andReturn("my property");
        replayAll();
        Assert.assertEquals("my property", new SystemClassUser().getSystemProperty());
        verifyAll();
    }

    @Test
    public void assertThatPartialMockingOfFinalSystemClassesWorks() throws Exception {
        mockStaticPartial(System.class, "nanoTime");
        expect(System.nanoTime()).andReturn(2L);
        replayAll();
        new SystemClassUser().doMoreComplicatedStuff();
        Assert.assertEquals("2", System.getProperty("nanoTime"));
        verifyAll();
    }

    @Test
    public void assertThatMockingOfCollectionsWork() throws Exception {
        List<?> list = new LinkedList<Object>();
        mockStatic(Collections.class);
        Collections.shuffle(list);
        expectLastCall().once();
        replayAll();
        new SystemClassUser().shuffleCollection(list);
        verifyAll();
    }

    @Test
    public void assertThatPartialMockingOfFinalSystemClassesWorksForNonVoidMethods() throws Exception {
        mockStaticPartial(System.class, "getProperty");
        expect(System.getProperty("property")).andReturn("my property");
        replayAll();
        final SystemClassUser systemClassUser = new SystemClassUser();
        systemClassUser.copyProperty("to", "property");
        verifyAll();
    }

    @Test
    public void assertThatMockingStringWorks() throws Exception {
        mockStatic(String.class);
        final String string = "string";
        final String args = "args";
        final String returnValue = "returnValue";
        expect(String.format(string, args)).andReturn(returnValue);
        replayAll();
        final SystemClassUser systemClassUser = new SystemClassUser();
        Assert.assertEquals(systemClassUser.format(string, args), returnValue);
        verifyAll();
    }

    @Test
    public void mockingStaticVoidMethodWorks() throws Exception {
        mockStatic(Thread.class);
        Thread.sleep(anyLong());
        expectLastCall().once();
        replayAll();
        long startTime = System.currentTimeMillis();
        final SystemClassUser systemClassUser = new SystemClassUser();
        systemClassUser.threadSleep();
        long endTime = System.currentTimeMillis();
        Assert.assertTrue(((endTime - startTime) < 5000));
        verifyAll();
    }

    @Test
    public void mockingInstanceMethodOfFinalSystemClassWorks() throws Exception {
        URL url = createMock(URL.class);
        URLConnection urlConnection = createMock(URLConnection.class);
        expect(url.openConnection()).andStubReturn(urlConnection);
        replayAll();
        final SystemClassUser systemClassUser = new SystemClassUser();
        Assert.assertSame(urlConnection, systemClassUser.useURL(url));
        verifyAll();
    }

    @Test
    public void mockingURLWorks() throws Exception {
        URL url = createMock(URL.class);
        URLConnection urlConnectionMock = createMock(URLConnection.class);
        expect(url.openConnection()).andReturn(urlConnectionMock);
        replayAll();
        Assert.assertSame(url.openConnection(), urlConnectionMock);
        verifyAll();
    }

    @Test
    public void mockingFinalMethodsInSystemClassesWorks() throws Exception {
        ClassLoader cl1 = createMock(ClassLoader.class);
        ClassLoader cl2 = createMock(ClassLoader.class);
        expect(cl1.getParent()).andReturn(cl2);
        replayAll();
        Assert.assertSame(cl1.getParent(), cl2);
        verifyAll();
    }

    @Test
    public void mockingInetAddressWorks() throws Exception {
        final InetAddress mock = createMock(InetAddress.class);
        mockStatic(InetAddress.class);
        expect(InetAddress.getLocalHost()).andReturn(mock);
        replayAll();
        final SystemClassUser systemClassUser = new SystemClassUser();
        Assert.assertSame(mock, systemClassUser.getLocalHost());
        verifyAll();
    }
}

