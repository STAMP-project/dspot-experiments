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
package samples.junit4.replayall;


import java.io.IOException;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import samples.Service;
import samples.expectnew.ExpectNewDemo;
import samples.expectnew.ExpectNewServiceUser;
import samples.newmocking.MyClass;


/**
 * The purpose of this test is to try-out the replay all functionality in
 * PowerMock in combination with expectNew.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ MyClass.class, ExpectNewDemo.class })
public class ReplayAllForExpectNewTest {
    @Test
    public void testNewWithCheckedException() throws Exception {
        ExpectNewDemo tested = new ExpectNewDemo();
        final String expectedFailMessage = "testing checked exception";
        expectNew(MyClass.class).andThrow(new IOException(expectedFailMessage));
        replayAll();
        try {
            tested.throwExceptionAndWrapInRunTimeWhenInvoction();
            Assert.fail("Should throw a checked Exception!");
        } catch (RuntimeException e) {
            Assert.assertTrue(((e.getCause()) instanceof IOException));
            Assert.assertEquals(expectedFailMessage, e.getMessage());
        }
        verifyAll();
    }

    @Test
    public void testGetMessage() throws Exception {
        ExpectNewDemo tested = new ExpectNewDemo();
        MyClass myClassMock = createMock(MyClass.class);
        expectNew(MyClass.class).andReturn(myClassMock);
        String expected = "Hello altered World";
        expect(myClassMock.getMessage()).andReturn("Hello altered World");
        replayAll();
        String actual = tested.getMessage();
        verifyAll();
        Assert.assertEquals("Expected and actual did not match", expected, actual);
    }

    @Test
    public void testReplayAllWithExpectNewWhenTheClassBeingConstructedIsNotPreparedForTest() throws Exception {
        final int numberOfTimes = 2;
        final String expected = "used";
        ExpectNewDemo tested = new ExpectNewDemo();
        ExpectNewServiceUser expectNewServiceImplMock = createMock(ExpectNewServiceUser.class);
        Service serviceMock = createMock(Service.class);
        expectNew(ExpectNewServiceUser.class, serviceMock, numberOfTimes).andReturn(expectNewServiceImplMock);
        expect(expectNewServiceImplMock.useService()).andReturn(expected);
        replayAll();
        Assert.assertEquals(expected, tested.newWithArguments(serviceMock, numberOfTimes));
        verifyAll();
    }

    @Test
    public void testReplayAllWithAdditionalMocks() throws Exception {
        final int numberOfTimes = 2;
        final String expected = "used";
        ExpectNewDemo tested = new ExpectNewDemo();
        ExpectNewServiceUser expectNewServiceImplMock = EasyMock.createMock(ExpectNewServiceUser.class);
        Service serviceMock = createMock(Service.class);
        expectNew(ExpectNewServiceUser.class, serviceMock, numberOfTimes).andReturn(expectNewServiceImplMock);
        expect(expectNewServiceImplMock.useService()).andReturn(expected);
        replayAll(expectNewServiceImplMock);
        Assert.assertEquals(expected, tested.newWithArguments(serviceMock, numberOfTimes));
        verifyAll();
    }
}

