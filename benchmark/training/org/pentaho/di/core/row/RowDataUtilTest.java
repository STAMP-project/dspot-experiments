/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2017 by Hitachi Vantara : http://www.pentaho.com
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
package org.pentaho.di.core.row;


import junit.framework.TestCase;
import org.pentaho.di.core.exception.KettleValueException;


public class RowDataUtilTest extends TestCase {
    public void testResizeArray() throws KettleValueException {
        Object[] arr1 = new Object[]{ "test", Boolean.TRUE, new Long(100), new Long(101), new String("test1") };
        Object[] nullArr = new Object[]{ null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null };
        // ------- Normal cases
        // Make array size bigger
        Object[] arr2 = RowDataUtil.resizeArray(arr1, 7);
        TestCase.assertTrue((arr1 != arr2));// Allocate new array

        TestCase.assertTrue(((arr2.length) >= 7));
        TestCase.assertTrue(arrayCompare(arr1, 0, arr2, 0, arr1.length));// Elements of arr1 are copied

        TestCase.assertTrue(arrayCompare(arr2, arr1.length, nullArr, 0, ((arr2.length) - (arr1.length))));// last are null

        // Make array size smaller
        Object[] arr3 = RowDataUtil.resizeArray(arr1, 3);
        TestCase.assertTrue((arr3 == arr1));
        // Make array size equal
        Object[] arr4 = RowDataUtil.resizeArray(arr1, arr1.length);
        TestCase.assertTrue((arr1 != arr2));// Allocatew new array

        TestCase.assertEquals(arr4.length, arr1.length);
        TestCase.assertTrue(arrayCompare(arr1, 0, arr4, 0, arr1.length));// Elements of arr1 are copied

    }

    public void testRemoveItem() throws KettleValueException {
        Object[] arr = new Object[]{ new Long(1L), new Long(2L), new Long(3L), new Long(4L), new Long(5L) };
        // Remove the first item
        Object[] comp1 = new Object[]{ new Long(2L), new Long(3L), new Long(4L), new Long(5L) };
        Object[] newArr1 = RowDataUtil.removeItem(arr, 0);
        TestCase.assertTrue(((newArr1.length) == (comp1.length)));
        TestCase.assertTrue(((newArr1.length) == ((arr.length) - 1)));
        TestCase.assertTrue(arrayCompare(newArr1, 0, comp1, 0, newArr1.length));
        // Remove the last item
        Object[] comp2 = new Object[]{ new Long(2L), new Long(3L), new Long(4L) };
        Object[] newArr2 = RowDataUtil.removeItem(newArr1, ((newArr1.length) - 1));
        TestCase.assertTrue(((newArr2.length) == ((arr.length) - 2)));
        TestCase.assertTrue(((newArr2.length) == (comp2.length)));
        TestCase.assertTrue(arrayCompare(newArr2, 0, comp2, 0, newArr2.length));
        // Remove in the middle
        Object[] comp3 = new Object[]{ new Long(2L), new Long(4L) };
        Object[] newArr3 = RowDataUtil.removeItem(newArr2, 1);
        TestCase.assertTrue(((newArr3.length) == ((arr.length) - 3)));
        TestCase.assertTrue(((newArr3.length) == (comp3.length)));
        TestCase.assertTrue(arrayCompare(newArr3, 0, comp3, 0, newArr3.length));
        // Remove until empty
        Object[] comp4 = new Object[]{  };
        Object[] newArr4 = RowDataUtil.removeItem(newArr3, 0);
        newArr4 = RowDataUtil.removeItem(newArr4, 0);
        TestCase.assertTrue(((newArr4.length) == ((arr.length) - 5)));
        TestCase.assertTrue(((newArr4.length) == (comp4.length)));
        TestCase.assertTrue(arrayCompare(newArr4, 0, comp4, 0, newArr4.length));
    }

    public void testAddRowData() throws KettleValueException {
        Object[] arr = new Object[]{ new Long(1L), new Long(2L), new Long(3L) };
        // Do all different combinations of adding rows to
        // each other
        Object[] newArr1 = RowDataUtil.addRowData(new Object[]{  }, 0, new Object[]{ new Long(1L), new Long(2L), new Long(3L) });
        TestCase.assertTrue(((newArr1.length) >= (arr.length)));
        TestCase.assertTrue(arrayCompare(newArr1, 0, arr, 0, arr.length));
        Object[] newArr2 = RowDataUtil.addRowData(new Object[]{ new Long(1L), new Long(2L), new Long(3L) }, 3, new Object[]{  });
        TestCase.assertTrue(((newArr2.length) >= (arr.length)));
        TestCase.assertTrue(arrayCompare(newArr2, 0, arr, 0, arr.length));
        Object[] newArr3 = RowDataUtil.addRowData(new Object[]{ new Long(1L) }, 1, new Object[]{ new Long(2L), new Long(3L) });
        TestCase.assertTrue(((newArr3.length) >= (arr.length)));
        TestCase.assertTrue(arrayCompare(newArr3, 0, arr, 0, arr.length));
        Object[] newArr4 = RowDataUtil.addRowData(new Object[]{ new Long(1L), new Long(2L) }, 2, new Object[]{ new Long(3L) });
        TestCase.assertTrue(((newArr4.length) >= (arr.length)));
        TestCase.assertTrue(arrayCompare(newArr4, 0, arr, 0, arr.length));
    }

    public void testAddValueData() throws KettleValueException {
        Object[] arr1 = new Object[]{ new Long(1L) };
        Object[] arr2 = new Object[]{ new Long(1L), new Long(2L) };
        Object[] newArr1 = RowDataUtil.addValueData(new Object[]{  }, 0, new Long(1L));
        TestCase.assertTrue(((newArr1.length) >= (arr1.length)));
        TestCase.assertTrue(arrayCompare(newArr1, 0, arr1, 0, arr1.length));
        Object[] newArr2 = RowDataUtil.addValueData(new Object[]{ new Long(1L) }, 1, new Long(2L));
        TestCase.assertTrue(((newArr2.length) >= (arr2.length)));
        TestCase.assertTrue(arrayCompare(newArr2, 0, arr2, 0, arr2.length));
    }

    public void testRemoveItems() throws KettleValueException {
        Object[] arr1 = new Object[]{ new Long(1L), new Long(2L), new Long(3L), new Long(4L), new Long(5L) };
        Object[] comp1 = new Object[]{ new Long(2L), new Long(4L) };
        Object[] newArr1 = RowDataUtil.removeItems(arr1, new int[]{  });
        TestCase.assertTrue(((newArr1.length) == (arr1.length)));
        TestCase.assertTrue(arrayCompare(newArr1, 0, arr1, 0, newArr1.length));
        Object[] newArr2 = RowDataUtil.removeItems(arr1, new int[]{ 0, 2, 4 });
        TestCase.assertTrue(((newArr2.length) == ((arr1.length) - 3)));
        TestCase.assertTrue(arrayCompare(newArr2, 0, comp1, 0, newArr2.length));
    }
}

