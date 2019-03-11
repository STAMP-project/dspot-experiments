/**
 * Copyright (C) 2006 The Android Open Source Project
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
package libcore.java.lang;


import junit.framework.TestCase;


public class OldAndroidInstanceofTest extends TestCase {
    protected OldAndroidInstanceofTest.A mA;

    protected OldAndroidInstanceofTest.ChildOfAOne mOne;

    protected OldAndroidInstanceofTest.ChildOfAOne mTwo;

    protected OldAndroidInstanceofTest.ChildOfAOne mThree;

    protected OldAndroidInstanceofTest.ChildOfAOne mFour;

    protected OldAndroidInstanceofTest.ChildOfAFive mFive;

    public void testNoInterface() throws Exception {
        OldAndroidInstanceofTest.A a = mA;
        for (int i = 0; i < 100000; i++) {
            TestCase.assertFalse("m_a should not be a ChildOfAFive", (a instanceof OldAndroidInstanceofTest.ChildOfAFive));
        }
    }

    public void testDerivedOne() throws Exception {
        OldAndroidInstanceofTest.InterfaceOne one = mOne;
        for (int i = 0; i < 100000; i++) {
            TestCase.assertFalse("m_one should not be a ChildOfAFive", (one instanceof OldAndroidInstanceofTest.ChildOfAFive));
        }
    }

    public void testDerivedTwo() throws Exception {
        OldAndroidInstanceofTest.InterfaceTwo two = mTwo;
        for (int i = 0; i < 100000; i++) {
            TestCase.assertFalse("m_two should not be a ChildOfAFive", (two instanceof OldAndroidInstanceofTest.ChildOfAFive));
        }
    }

    public void testDerivedThree() throws Exception {
        OldAndroidInstanceofTest.InterfaceThree three = mThree;
        for (int i = 0; i < 100000; i++) {
            TestCase.assertFalse("m_three should not be a ChildOfAFive", (three instanceof OldAndroidInstanceofTest.ChildOfAFive));
        }
    }

    public void testDerivedFour() throws Exception {
        OldAndroidInstanceofTest.InterfaceFour four = mFour;
        for (int i = 0; i < 100000; i++) {
            TestCase.assertFalse("m_four should not be a ChildOfAFive", (four instanceof OldAndroidInstanceofTest.ChildOfAFive));
        }
    }

    public void testSuccessClass() throws Exception {
        OldAndroidInstanceofTest.ChildOfAOne five = mFive;
        for (int i = 0; i < 100000; i++) {
            TestCase.assertTrue("m_five is suppose to be a ChildOfAFive", (five instanceof OldAndroidInstanceofTest.ChildOfAFive));
        }
    }

    public void testSuccessInterface() throws Exception {
        OldAndroidInstanceofTest.ChildOfAFive five = mFive;
        for (int i = 0; i < 100000; i++) {
            TestCase.assertTrue("m_five is suppose to be a InterfaceFour", (five instanceof OldAndroidInstanceofTest.InterfaceFour));
        }
    }

    public void testFailInterface() throws Exception {
        OldAndroidInstanceofTest.InterfaceOne one = mFive;
        for (int i = 0; i < 100000; i++) {
            TestCase.assertFalse("m_five does not implement InterfaceFive", (one instanceof OldAndroidInstanceofTest.InterfaceFive));
        }
    }

    private interface InterfaceOne {}

    private interface InterfaceTwo {}

    private interface InterfaceThree {}

    private interface InterfaceFour {}

    private interface InterfaceFive {}

    private static class A {}

    private static class ChildOfAOne extends OldAndroidInstanceofTest.A implements OldAndroidInstanceofTest.InterfaceFour , OldAndroidInstanceofTest.InterfaceOne , OldAndroidInstanceofTest.InterfaceThree , OldAndroidInstanceofTest.InterfaceTwo {}

    private static class ChildOfATwo extends OldAndroidInstanceofTest.ChildOfAOne {}

    private static class ChildOfAThree extends OldAndroidInstanceofTest.ChildOfATwo {}

    private static class ChildOfAFour extends OldAndroidInstanceofTest.ChildOfAThree {}

    private static class ChildOfAFive extends OldAndroidInstanceofTest.ChildOfAFour {}
}

