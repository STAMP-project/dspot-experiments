/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2018 by Hitachi Vantara : http://www.pentaho.com
 *
 * ******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ****************************************************************************
 */
package org.pentaho.di.core.util;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.pentaho.di.core.variables.Variables;
import org.pentaho.di.junit.rules.RestorePDIEnvironment;


public class UtilsTest {
    @ClassRule
    public static RestorePDIEnvironment env = new RestorePDIEnvironment();

    @Test
    public void testIsEmpty() {
        Assert.assertTrue(Utils.isEmpty(((String) (null))));
        Assert.assertTrue(Utils.isEmpty(""));
        Assert.assertFalse(Utils.isEmpty("test"));
    }

    @Test
    public void testIsEmptyStringArray() {
        Assert.assertTrue(Utils.isEmpty(((String[]) (null))));
        Assert.assertTrue(Utils.isEmpty(new String[]{  }));
        Assert.assertFalse(Utils.isEmpty(new String[]{ "test" }));
    }

    @Test
    public void testIsEmptyObjectArray() {
        Assert.assertTrue(Utils.isEmpty(((Object[]) (null))));
        Assert.assertTrue(Utils.isEmpty(new Object[]{  }));
        Assert.assertFalse(Utils.isEmpty(new Object[]{ "test" }));
    }

    @Test
    public void testIsEmptyList() {
        Assert.assertTrue(Utils.isEmpty(((List<String>) (null))));
        Assert.assertTrue(Utils.isEmpty(new ArrayList<String>()));
        Assert.assertFalse(Utils.isEmpty(Arrays.asList("test", 1)));
    }

    @Test
    public void testIsEmptyStringBuffer() {
        Assert.assertTrue(Utils.isEmpty(((StringBuffer) (null))));
        Assert.assertTrue(Utils.isEmpty(new StringBuffer("")));
        Assert.assertFalse(Utils.isEmpty(new StringBuffer("test")));
    }

    @Test
    public void testIsEmptyStringBuilder() {
        Assert.assertTrue(Utils.isEmpty(((StringBuilder) (null))));
        Assert.assertTrue(Utils.isEmpty(new StringBuilder("")));
        Assert.assertFalse(Utils.isEmpty(new StringBuilder("test")));
    }

    @Test
    public void testResolvePassword() {
        String password = "password";
        // is supposed the password stays the same
        Assert.assertSame(password, Utils.resolvePassword(Variables.getADefaultVariableSpace(), password).intern());
    }

    @Test
    public void testResolvePasswordEncrypted() {
        String decPassword = "password";
        // is supposed encrypted with Encr.bat util
        String encPassword = "Encrypted 2be98afc86aa7f2e4bb18bd63c99dbdde";
        Assert.assertSame(decPassword, Utils.resolvePassword(Variables.getADefaultVariableSpace(), encPassword).intern());
    }

    @Test
    public void testResolvePasswordNull() {
        String password = null;
        // null is valid input parameter
        Assert.assertSame(password, Utils.resolvePassword(Variables.getADefaultVariableSpace(), password));
    }

    @Test
    public void testResolvePasswordVariable() {
        String passwordKey = "PASS_VAR";
        String passwordVar = ("${" + passwordKey) + "}";
        String passwordValue = "password";
        Variables vars = new Variables();
        vars.setVariable(passwordKey, passwordValue);
        // resolvePassword gets variable
        Assert.assertSame(passwordValue, Utils.resolvePassword(vars, passwordVar).intern());
    }

    @Test
    public void testNormalizeArraysMethods() {
        String[] s1 = new String[]{ "one" };
        String[] s2 = new String[]{ "one", "two" };
        String[] s3 = new String[]{ "one", "two", "three" };
        long[] l1 = new long[]{ 1 };
        long[] l2 = new long[]{ 1, 2 };
        long[] l3 = new long[]{ 1, 2, 3 };
        short[] sh1 = new short[]{ 1 };
        short[] sh2 = new short[]{ 1, 2 };
        short[] sh3 = new short[]{ 1, 2, 3 };
        boolean[] b1 = new boolean[]{ true };
        boolean[] b2 = new boolean[]{ true, false };
        boolean[] b3 = new boolean[]{ true, false, true };
        int[] i1 = new int[]{ 1 };
        int[] i2 = new int[]{ 1, 2 };
        int[] i3 = new int[]{ 1, 3 };
        String[][] newS = Utils.normalizeArrays(3, s1, s2);
        Assert.assertEquals(2, newS.length);
        Assert.assertEquals(3, newS[0].length);
        Assert.assertEquals(3, newS[1].length);
        newS = Utils.normalizeArrays(3, s1, null);
        Assert.assertEquals(2, newS.length);
        Assert.assertEquals(3, newS[0].length);
        Assert.assertEquals(3, newS[1].length);
        newS = Utils.normalizeArrays(2, s2);
        Assert.assertEquals(1, newS.length);
        Assert.assertEquals(2, newS[0].length);
        Assert.assertArrayEquals(newS[0], s2);
        Assert.assertTrue(((newS[0]) == s2));// If arrays are equal sized, it should return original object

        long[][] newL = Utils.normalizeArrays(3, l1, l2);
        Assert.assertEquals(2, newL.length);
        Assert.assertEquals(3, newL[0].length);
        Assert.assertEquals(3, newL[1].length);
        newL = Utils.normalizeArrays(3, l1, null);
        Assert.assertEquals(2, newL.length);
        Assert.assertEquals(3, newL[0].length);
        Assert.assertEquals(3, newL[1].length);
        newL = Utils.normalizeArrays(2, l2);
        Assert.assertEquals(1, newL.length);
        Assert.assertEquals(2, newL[0].length);
        Assert.assertArrayEquals(newL[0], l2);
        Assert.assertTrue(((newL[0]) == l2));// If arrays are equal sized, it should return original object

        short[][] newSh = Utils.normalizeArrays(3, sh1, sh2);
        Assert.assertEquals(2, newSh.length);
        Assert.assertEquals(3, newSh[0].length);
        Assert.assertEquals(3, newSh[1].length);
        newSh = Utils.normalizeArrays(3, sh1, null);
        Assert.assertEquals(2, newSh.length);
        Assert.assertEquals(3, newSh[0].length);
        Assert.assertEquals(3, newSh[1].length);
        newSh = Utils.normalizeArrays(2, sh2);
        Assert.assertEquals(1, newSh.length);
        Assert.assertEquals(2, newSh[0].length);
        Assert.assertArrayEquals(newSh[0], sh2);
        Assert.assertTrue(((newSh[0]) == sh2));// If arrays are equal sized, it should return original object

        boolean[][] newB = Utils.normalizeArrays(3, b1, b2);
        Assert.assertEquals(2, newB.length);
        Assert.assertEquals(3, newB[0].length);
        Assert.assertEquals(3, newB[1].length);
        newB = Utils.normalizeArrays(3, b1, null);
        Assert.assertEquals(2, newB.length);
        Assert.assertEquals(3, newB[0].length);
        Assert.assertEquals(3, newB[1].length);
        newB = Utils.normalizeArrays(2, b2);
        Assert.assertEquals(1, newB.length);
        Assert.assertEquals(2, newB[0].length);
        Assert.assertTrue(((newB[0]) == b2));// If arrays are equal sized, it should return original object

        int[][] newI = Utils.normalizeArrays(3, i1, i2);
        Assert.assertEquals(2, newI.length);
        Assert.assertEquals(3, newI[0].length);
        Assert.assertEquals(3, newI[1].length);
        newI = Utils.normalizeArrays(3, i1, null);
        Assert.assertEquals(2, newI.length);
        Assert.assertEquals(3, newI[0].length);
        Assert.assertEquals(3, newI[1].length);
        newI = Utils.normalizeArrays(2, i2);
        Assert.assertEquals(1, newI.length);
        Assert.assertEquals(2, newI[0].length);
        Assert.assertArrayEquals(newI[0], i2);
        Assert.assertTrue(((newI[0]) == i2));// If arrays are equal sized, it should return original object

    }
}

