/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.query.impl.getters;


import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class FieldGetterTest {
    private Field limbArrayField;

    private Field limbCollectionField;

    private Field nailArrayField;

    private Field nailCollectionField;

    private FieldGetterTest.Body body;

    private FieldGetterTest.PrimitiveBloke bloke;

    private FieldGetterTest.Nail redNail;

    private FieldGetterTest.Nail greenNail;

    private FieldGetterTest.Limb leg;

    private FieldGetterTest.Nail whiteNail;

    private FieldGetterTest.Nail blackNail;

    private FieldGetterTest.Limb hand;

    private FieldGetterTest.Limb unnamedLimb;

    @Test(expected = IllegalArgumentException.class)
    public void constructor_whenModifierIsNotNullAndFieldTypeIsNotArrayOrCollection_thenThrowIllegalArgumentException() throws Exception {
        Field field = FieldGetterTest.Body.class.getDeclaredField("name");
        new FieldGetter(null, field, "[any]", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructor_whenModifierIsNegative_thenThrowIllegalArgumentException() throws Exception {
        Field field = FieldGetterTest.Body.class.getDeclaredField("name");
        new FieldGetter(null, field, "[-1]", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructor_whenModifierIsStarAndFieldTypeIsCollection_thenThrowIllegalArgumentException() {
        new FieldGetter(null, limbCollectionField, "[any]", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructor_whenModifierIsPositionAndFieldTypeIsCollection_thenThrowIllegalArgumentException() {
        new FieldGetter(null, limbCollectionField, "[0]", null);
    }

    @Test
    public void getValue_whenModifierOnArrayIsStar_thenReturnMultiValueResultWithAllItems() throws Exception {
        FieldGetter getter = new FieldGetter(null, limbArrayField, "[any]", null);
        MultiResult result = ((MultiResult) (getter.getValue(body)));
        assertContainsInAnyOrder(result, leg, hand, unnamedLimb);
    }

    @Test
    public void getValue_whenParentIsMultiValueAndModifierOnArrayIsStar_thenReturnMultiValueResultWithAllItems() throws Exception {
        FieldGetter limbGetter = new FieldGetter(null, limbArrayField, "[any]", null);
        FieldGetter nailGetter = new FieldGetter(limbGetter, nailArrayField, "[any]", null);
        MultiResult result = ((MultiResult) (nailGetter.getValue(body)));
        assertContainsInAnyOrder(result, whiteNail, blackNail, redNail, greenNail, null);
    }

    @Test
    public void getValue_whenParentIsMultiValueAndModifierOnArrayIsPosition_thenReturnMultiValueResultWithItemsAtPosition() throws Exception {
        FieldGetter limbGetter = new FieldGetter(null, limbArrayField, "[any]", null);
        FieldGetter nailGetter = new FieldGetter(limbGetter, nailArrayField, "[0]", null);
        MultiResult result = ((MultiResult) (nailGetter.getValue(body)));
        assertContainsInAnyOrder(result, redNail, whiteNail, null);
    }

    @Test
    public void getValue_whenParentIsMultiValueAndModifierOnCollectionIsStar_thenReturnMultiValueResultWithAllItems() throws Exception {
        FieldGetter limbGetter = new FieldGetter(null, limbArrayField, "[any]", null);
        FieldGetter nailGetter = new FieldGetter(limbGetter, nailCollectionField, "[any]", FieldGetterTest.Nail.class);
        MultiResult result = ((MultiResult) (nailGetter.getValue(body)));
        assertContainsInAnyOrder(result, whiteNail, blackNail, redNail, greenNail, null);
    }

    @Test
    public void getValue_whenParentIsMultiValueAndModifierOnCollectionIsPosition_thenReturnMultiValueResultWithItemsAtPosition() throws Exception {
        FieldGetter limbGetter = new FieldGetter(null, limbArrayField, "[any]", null);
        FieldGetter nailGetter = new FieldGetter(limbGetter, nailArrayField, "[0]", FieldGetterTest.Nail.class);
        MultiResult result = ((MultiResult) (nailGetter.getValue(body)));
        assertContainsInAnyOrder(result, redNail, whiteNail, null);
    }

    @Test
    public void getValue_whenModifierOnCollectionIsStar_thenReturnMultiValueResultWithAllItems() throws Exception {
        FieldGetter getter = new FieldGetter(null, limbCollectionField, "[any]", FieldGetterTest.Limb.class);
        MultiResult result = ((MultiResult) (getter.getValue(body)));
        assertContainsInAnyOrder(result, leg, hand, unnamedLimb);
    }

    @Test
    public void getValue_whenModifierOnCollectionIsStar_thenReturnMultiValueResultWithAllItems_bytes() throws Exception {
        FieldGetter getter = new FieldGetter(null, FieldGetterTest.PrimitiveBloke.class.getField("bytes"), "[any]", null);
        MultiResult result = ((MultiResult) (getter.getValue(bloke)));
        assertContainsInAnyOrder(result, bloke.bytes[0]);
    }

    @Test
    public void getValue_singleCell_bytes() throws Exception {
        FieldGetter getter = new FieldGetter(null, FieldGetterTest.PrimitiveBloke.class.getField("bytes"), "[0]", null);
        Assert.assertEquals(bloke.bytes[0], getter.getValue(bloke));
    }

    @Test
    public void getValue_whenModifierOnCollectionIsStar_thenReturnMultiValueResultWithAllItems_shorts() throws Exception {
        FieldGetter getter = new FieldGetter(null, FieldGetterTest.PrimitiveBloke.class.getField("shorts"), "[any]", null);
        MultiResult result = ((MultiResult) (getter.getValue(bloke)));
        assertContainsInAnyOrder(result, bloke.shorts[0]);
    }

    @Test
    public void getValue_singleCell_shorts() throws Exception {
        FieldGetter getter = new FieldGetter(null, FieldGetterTest.PrimitiveBloke.class.getField("shorts"), "[0]", null);
        Assert.assertEquals(bloke.shorts[0], getter.getValue(bloke));
    }

    @Test
    public void getValue_whenModifierOnCollectionIsStar_thenReturnMultiValueResultWithAllItems_ints() throws Exception {
        FieldGetter getter = new FieldGetter(null, FieldGetterTest.PrimitiveBloke.class.getField("ints"), "[any]", null);
        MultiResult result = ((MultiResult) (getter.getValue(bloke)));
        assertContainsInAnyOrder(result, bloke.ints[0]);
    }

    @Test
    public void getValue_singleCell_ints() throws Exception {
        FieldGetter getter = new FieldGetter(null, FieldGetterTest.PrimitiveBloke.class.getField("ints"), "[0]", null);
        Assert.assertEquals(bloke.ints[0], getter.getValue(bloke));
    }

    @Test
    public void getValue_whenModifierOnCollectionIsStar_thenReturnMultiValueResultWithAllItems_longs() throws Exception {
        FieldGetter getter = new FieldGetter(null, FieldGetterTest.PrimitiveBloke.class.getField("longs"), "[any]", null);
        MultiResult result = ((MultiResult) (getter.getValue(bloke)));
        assertContainsInAnyOrder(result, bloke.longs[0]);
    }

    @Test
    public void getValue_singleCell_longs() throws Exception {
        FieldGetter getter = new FieldGetter(null, FieldGetterTest.PrimitiveBloke.class.getField("longs"), "[0]", null);
        Assert.assertEquals(bloke.longs[0], getter.getValue(bloke));
    }

    @Test
    public void getValue_whenModifierOnCollectionIsStar_thenReturnMultiValueResultWithAllItems_floats() throws Exception {
        FieldGetter getter = new FieldGetter(null, FieldGetterTest.PrimitiveBloke.class.getField("floats"), "[any]", null);
        MultiResult result = ((MultiResult) (getter.getValue(bloke)));
        assertContainsInAnyOrder(result, bloke.floats[0]);
    }

    @Test
    public void getValue_singleCell_floats() throws Exception {
        FieldGetter getter = new FieldGetter(null, FieldGetterTest.PrimitiveBloke.class.getField("floats"), "[0]", null);
        Assert.assertEquals(bloke.floats[0], getter.getValue(bloke));
    }

    @Test
    public void getValue_whenModifierOnCollectionIsStar_thenReturnMultiValueResultWithAllItems_double() throws Exception {
        FieldGetter getter = new FieldGetter(null, FieldGetterTest.PrimitiveBloke.class.getField("doubles"), "[any]", null);
        MultiResult result = ((MultiResult) (getter.getValue(bloke)));
        assertContainsInAnyOrder(result, bloke.doubles[0]);
    }

    @Test
    public void getValue_singleCell_double() throws Exception {
        FieldGetter getter = new FieldGetter(null, FieldGetterTest.PrimitiveBloke.class.getField("doubles"), "[0]", null);
        Assert.assertEquals(bloke.doubles[0], getter.getValue(bloke));
    }

    @Test
    public void getValue_whenModifierOnCollectionIsStar_thenReturnMultiValueResultWithAllItems_chars() throws Exception {
        FieldGetter getter = new FieldGetter(null, FieldGetterTest.PrimitiveBloke.class.getField("chars"), "[any]", null);
        MultiResult result = ((MultiResult) (getter.getValue(bloke)));
        assertContainsInAnyOrder(result, bloke.chars[0]);
    }

    @Test
    public void getValue_singleCell_chars() throws Exception {
        FieldGetter getter = new FieldGetter(null, FieldGetterTest.PrimitiveBloke.class.getField("chars"), "[0]", null);
        Assert.assertEquals(bloke.chars[0], getter.getValue(bloke));
    }

    @Test
    public void getValue_whenModifierOnCollectionIsStar_thenReturnMultiValueResultWithAllItems_booleans() throws Exception {
        FieldGetter getter = new FieldGetter(null, FieldGetterTest.PrimitiveBloke.class.getField("booleans"), "[any]", null);
        MultiResult result = ((MultiResult) (getter.getValue(bloke)));
        assertContainsInAnyOrder(result, bloke.booleans[0]);
    }

    @Test
    public void getValue_singleCell_booleans() throws Exception {
        FieldGetter getter = new FieldGetter(null, FieldGetterTest.PrimitiveBloke.class.getField("booleans"), "[0]", null);
        Assert.assertEquals(bloke.booleans[0], getter.getValue(bloke));
    }

    @Test
    public void getValue_whenModifierOnArrayIsPositionAndElementAtGivenPositionExist_thenReturnTheItem() throws Exception {
        FieldGetter getter = new FieldGetter(null, limbArrayField, "[0]", null);
        FieldGetterTest.Limb result = ((FieldGetterTest.Limb) (getter.getValue(body)));
        Assert.assertSame(leg, result);
    }

    @Test
    public void getValue_whenModifierOnCollectionIsPositionAndElementAtGivenPositionExist_thenReturnTheItem() throws Exception {
        FieldGetter getter = new FieldGetter(null, limbCollectionField, "[0]", FieldGetterTest.Limb.class);
        FieldGetterTest.Limb result = ((FieldGetterTest.Limb) (getter.getValue(body)));
        Assert.assertSame(leg, result);
    }

    @Test
    public void getValue_whenModifierOnArrayIsPositionAndElementAtGivenPositionDoesNotExist_thenReturnNull() throws Exception {
        FieldGetter getter = new FieldGetter(null, limbArrayField, "[3]", null);
        FieldGetterTest.Limb result = ((FieldGetterTest.Limb) (getter.getValue(body)));
        Assert.assertNull(result);
    }

    @Test
    public void getValue_whenModifierOnCollectionIsPositionAndElementAtGivenPositionDoesNotExist_thenReturnNull() throws Exception {
        FieldGetter getter = new FieldGetter(null, limbCollectionField, "[3]", FieldGetterTest.Limb.class);
        FieldGetterTest.Limb result = ((FieldGetterTest.Limb) (getter.getValue(body)));
        Assert.assertNull(result);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void getValue_whenNoModifierOnCollection_thenReturnTheCollection() throws Exception {
        FieldGetter getter = new FieldGetter(null, limbCollectionField, null, null);
        Collection<FieldGetterTest.Limb> result = ((Collection<FieldGetterTest.Limb>) (getter.getValue(body)));
        Assert.assertSame(body.limbCollection, result);
    }

    @Test
    public void getValue_whenParentIsMultiResultAndNoModifier_thenReturnTheMultiResultContainingCurrentObjects() throws Exception {
        FieldGetter limbGetter = new FieldGetter(null, limbArrayField, "[any]", null);
        Field limbNameField = FieldGetterTest.Limb.class.getDeclaredField("name");
        FieldGetter nailNameGetter = new FieldGetter(limbGetter, limbNameField, null, null);
        MultiResult result = ((MultiResult) (nailNameGetter.getValue(body)));
        assertContainsInAnyOrder(result, "leg", "hand", null);
    }

    @Test
    public void getValue_whenNoModifierOnArray_thenReturnTheArray() throws Exception {
        FieldGetter getter = new FieldGetter(null, limbArrayField, null, null);
        FieldGetterTest.Limb[] result = ((FieldGetterTest.Limb[]) (getter.getValue(body)));
        Assert.assertSame(body.limbArray, result);
    }

    @Test
    public void getValue_whenInputIsNull_thenReturnNull() throws Exception {
        FieldGetter getter = new FieldGetter(null, limbArrayField, null, null);
        FieldGetterTest.Limb[] result = ((FieldGetterTest.Limb[]) (getter.getValue(null)));
        Assert.assertNull(result);
    }

    @Test
    public void getReturnType_whenSetExplicitly_thenReturnIt() {
        FieldGetter getter = new FieldGetter(null, limbCollectionField, "[any]", FieldGetterTest.Limb.class);
        Class returnType = getter.getReturnType();
        Assert.assertEquals(FieldGetterTest.Limb.class, returnType);
    }

    @Test
    public void getReturnType_whenModifierIsPositionAndFieldIsArray_thenInferReturnTypeFromTheArray() {
        FieldGetter getter = new FieldGetter(null, limbArrayField, "[0]", null);
        Class returnType = getter.getReturnType();
        Assert.assertEquals(FieldGetterTest.Limb.class, returnType);
    }

    @Test
    public void getReturnType_whenModifierIsStarAndFieldIsArray_thenInferReturnTypeFromTheArray() {
        FieldGetter getter = new FieldGetter(null, limbArrayField, "[any]", null);
        Class returnType = getter.getReturnType();
        Assert.assertEquals(FieldGetterTest.Limb.class, returnType);
    }

    @Test
    public void getReturnType_whenNoModifierAndFieldIsArray_thenReturnTheArrayType() {
        FieldGetter getter = new FieldGetter(null, limbArrayField, null, null);
        Class returnType = getter.getReturnType();
        Assert.assertEquals(FieldGetterTest.Limb[].class, returnType);
    }

    static final class Body {
        String name;

        FieldGetterTest.Limb[] limbArray;

        Collection<FieldGetterTest.Limb> limbCollection;

        Body(String name, FieldGetterTest.Limb... limbs) {
            this.name = name;
            this.limbCollection = Arrays.asList(limbs);
            this.limbArray = limbs;
        }
    }

    static final class Limb {
        String name;

        FieldGetterTest.Nail[] nailArray;

        Collection<FieldGetterTest.Nail> nailCollection;

        Limb(String name, FieldGetterTest.Nail... nails) {
            this.name = name;
            this.nailCollection = Arrays.asList(nails);
            this.nailArray = nails;
        }
    }

    static final class Nail {
        String colour;

        private Nail(String colour) {
            this.colour = colour;
        }
    }

    static final class PrimitiveBloke {
        public byte[] bytes = new byte[]{ 1 };

        public short[] shorts = new short[]{ 1 };

        public int[] ints = new int[]{ 1 };

        public long[] longs = new long[]{ 1 };

        public float[] floats = new float[]{ 1.0F };

        public double[] doubles = new double[]{ 1.0 };

        public char[] chars = new char[]{ 0 };

        public boolean[] booleans = new boolean[]{ false };
    }
}

