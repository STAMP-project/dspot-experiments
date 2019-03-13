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
package samples.powermockito.junit4.whennew;


import java.io.File;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import samples.expectnew.NewFileExample;


@RunWith(PowerMockRunner.class)
@PrepareForTest(NewFileExample.class)
public class VerifyNewMultipleTimesTest {
    private static final String DIRECTORY_PATH = "mocked path";

    @Mock
    private File directoryMock;

    @Test(expected = AssertionError.class)
    public void verifyNewTooManyTimesCausesAssertionError() throws Exception {
        Assert.assertTrue(new NewFileExample().createDirectoryStructure(VerifyNewMultipleTimesTest.DIRECTORY_PATH));
        Mockito.verify(directoryMock).mkdirs();
        // Correct usage
        verifyNew(File.class, Mockito.times(1)).withArguments(VerifyNewMultipleTimesTest.DIRECTORY_PATH);
        // Should throw
        verifyNew(File.class, Mockito.times(100000)).withArguments(VerifyNewMultipleTimesTest.DIRECTORY_PATH);
    }
}

