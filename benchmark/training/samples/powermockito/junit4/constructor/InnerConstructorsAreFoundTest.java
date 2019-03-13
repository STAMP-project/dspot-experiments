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
package samples.powermockito.junit4.constructor;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


/**
 * Asserts that <a
 * href="http://code.google.com/p/powermock/issues/detail?id=267">issue 267</a>
 * is resolved.
 */
@PrepareForTest({ InnerConstructorsAreFoundTest.WorkingObjectUnderTesting.class, InnerConstructorsAreFoundTest.BuggyObjectUnderTesting.class, InnerConstructorsAreFoundTest.StillBuggyObjectUnderTesting.class })
@RunWith(PowerMockRunner.class)
public class InnerConstructorsAreFoundTest {
    interface AnyInterface {}

    class AnyImplementation implements InnerConstructorsAreFoundTest.AnyInterface {
        @Override
        public boolean equals(Object obj) {
            return true;
        }
    }

    class SomeOtherImplementationOfSomethingElse {
        @Override
        public boolean equals(Object obj) {
            return true;
        }
    }

    // <ObjectsUnderTesting>
    class WorkingObjectUnderTesting {
        void methodToTest() {
            new InnerConstructorsAreFoundTest.WorkingObjectToMock(new InnerConstructorsAreFoundTest.AnyImplementation());
        }
    }

    class BuggyObjectUnderTesting {
        void methodToTest() {
            new InnerConstructorsAreFoundTest.BuggyObjectToMock(new InnerConstructorsAreFoundTest.SomeOtherImplementationOfSomethingElse(), new InnerConstructorsAreFoundTest.AnyImplementation());
        }
    }

    class StillBuggyObjectUnderTesting {
        void methodToTest() {
            new InnerConstructorsAreFoundTest.StillBuggyObjectToMock(new InnerConstructorsAreFoundTest.SomeOtherImplementationOfSomethingElse(), new InnerConstructorsAreFoundTest.AnyInterface[]{ new InnerConstructorsAreFoundTest.AnyImplementation() });
        }
    }

    // </ObjectsUnderTesting>
    // <ObjectsToMock>
    class WorkingObjectToMock {
        public WorkingObjectToMock(InnerConstructorsAreFoundTest.AnyInterface... anys) {
        }
    }

    class BuggyObjectToMock {
        @SuppressWarnings("unused")
        private final InnerConstructorsAreFoundTest.AnyInterface[] anys;

        public BuggyObjectToMock(InnerConstructorsAreFoundTest.SomeOtherImplementationOfSomethingElse other, InnerConstructorsAreFoundTest.AnyInterface... anys) {
            this.anys = anys;
        }
    }

    class StillBuggyObjectToMock {
        @SuppressWarnings("unused")
        private final InnerConstructorsAreFoundTest.AnyInterface[] anys;

        public StillBuggyObjectToMock(InnerConstructorsAreFoundTest.SomeOtherImplementationOfSomethingElse other, InnerConstructorsAreFoundTest.AnyInterface[] anys) {
            this.anys = anys;
        }
    }

    @Mock
    InnerConstructorsAreFoundTest.WorkingObjectToMock mockedWorkingObject;

    @Mock
    InnerConstructorsAreFoundTest.BuggyObjectToMock mockedBuggyObject;

    @Mock
    InnerConstructorsAreFoundTest.StillBuggyObjectToMock mockedStillBuggyObject;

    @Test
    public void shouldFindMemberVarArgsCtorWhenPassingArrayToWithArguments() throws Exception {
        whenNew(InnerConstructorsAreFoundTest.BuggyObjectToMock.class).withArguments(new InnerConstructorsAreFoundTest.SomeOtherImplementationOfSomethingElse(), ((Object[]) (new InnerConstructorsAreFoundTest.AnyInterface[]{ new InnerConstructorsAreFoundTest.AnyImplementation() }))).thenReturn(mockedBuggyObject);
        new InnerConstructorsAreFoundTest.BuggyObjectUnderTesting().methodToTest();
    }

    @Test
    public void shouldFindMemberArrayCtorWhenPassingArrayToWithArguments() throws Exception {
        whenNew(InnerConstructorsAreFoundTest.StillBuggyObjectToMock.class).withArguments(new InnerConstructorsAreFoundTest.SomeOtherImplementationOfSomethingElse(), ((Object) (new InnerConstructorsAreFoundTest.AnyInterface[]{ new InnerConstructorsAreFoundTest.AnyImplementation() }))).thenReturn(mockedStillBuggyObject);
        new InnerConstructorsAreFoundTest.StillBuggyObjectUnderTesting().methodToTest();
    }
}

