/**
 * Copyright (C) 2008 The Android Open Source Project
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


/**
 * Tests basic behavior of enums.
 */
public class OldAndroidEnumTest extends TestCase {
    enum MyEnum {

        ZERO,
        ONE,
        TWO,
        THREE,
        FOUR() {
            boolean isFour() {
                return true;
            }
        };
        boolean isFour() {
            return false;
        }
    }

    enum MyEnumTwo {

        FIVE,
        SIX;}

    public void testEnum() throws Exception {
        TestCase.assertTrue(((OldAndroidEnumTest.MyEnum.ZERO.compareTo(OldAndroidEnumTest.MyEnum.ONE)) < 0));
        TestCase.assertEquals(OldAndroidEnumTest.MyEnum.ZERO, OldAndroidEnumTest.MyEnum.ZERO);
        TestCase.assertTrue(((OldAndroidEnumTest.MyEnum.TWO.compareTo(OldAndroidEnumTest.MyEnum.ONE)) > 0));
        TestCase.assertTrue(((OldAndroidEnumTest.MyEnum.FOUR.compareTo(OldAndroidEnumTest.MyEnum.ONE)) > 0));
        TestCase.assertEquals("ONE", OldAndroidEnumTest.MyEnum.ONE.name());
        TestCase.assertSame(OldAndroidEnumTest.MyEnum.ONE.getDeclaringClass(), OldAndroidEnumTest.MyEnum.class);
        TestCase.assertSame(OldAndroidEnumTest.MyEnum.FOUR.getDeclaringClass(), OldAndroidEnumTest.MyEnum.class);
        TestCase.assertTrue(OldAndroidEnumTest.MyEnum.FOUR.isFour());
        OldAndroidEnumTest.MyEnum e;
        e = OldAndroidEnumTest.MyEnum.ZERO;
        switch (e) {
            case ZERO :
                break;
            default :
                TestCase.fail("wrong switch");
        }
    }
}

