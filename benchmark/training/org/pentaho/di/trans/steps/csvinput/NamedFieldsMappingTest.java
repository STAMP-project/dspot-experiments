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
package org.pentaho.di.trans.steps.csvinput;


import FieldsMapping.FIELD_DOES_NOT_EXIST;
import org.junit.Assert;
import org.junit.Test;


public class NamedFieldsMappingTest {
    private NamedFieldsMapping fieldsMapping;

    @Test
    public void fieldMetaIndex() {
        Assert.assertEquals(3, fieldsMapping.fieldMetaIndex(0));
    }

    @Test
    public void fieldMetaIndexWithUnexistingField() {
        Assert.assertEquals(FIELD_DOES_NOT_EXIST, fieldsMapping.fieldMetaIndex(4));
    }

    @Test
    public void size() {
        Assert.assertEquals(2, fieldsMapping.size());
    }

    @Test
    public void mapping() {
        NamedFieldsMapping mapping = NamedFieldsMapping.mapping(new String[]{ "FIRST", "SECOND", "THIRD" }, new String[]{ "SECOND", "THIRD" });
        Assert.assertEquals(0, mapping.fieldMetaIndex(1));
    }

    @Test
    public void mappingWithNonUniqueColumnNames() {
        NamedFieldsMapping mapping = NamedFieldsMapping.mapping(new String[]{ "Object", "Test", "Object" }, new String[]{ "Object", "Test", "Object" });
        Assert.assertEquals(0, mapping.fieldMetaIndex(0));
        Assert.assertEquals(2, mapping.fieldMetaIndex(2));
    }

    @Test
    public void fieldMetaIndexWithUnexistingField_nonUniqueColumnNames() {
        NamedFieldsMapping mapping = NamedFieldsMapping.mapping(new String[]{ "Object", "Test", "Object" }, new String[]{ "Object", "Test" });
        Assert.assertEquals(FIELD_DOES_NOT_EXIST, mapping.fieldMetaIndex(2));
    }

    @Test
    public void mappingWithNonMatchingColumnNames() {
        NamedFieldsMapping mapping = NamedFieldsMapping.mapping(new String[]{ "One", "Two", "Three" }, new String[]{ "A", "B", "C" });
        Assert.assertEquals(0, mapping.fieldMetaIndex(0));// One -> A

        Assert.assertEquals(1, mapping.fieldMetaIndex(1));// Two -> B

        Assert.assertEquals(2, mapping.fieldMetaIndex(2));// Three -> C

        mapping = NamedFieldsMapping.mapping(new String[]{ "A", "B", "A" }, new String[]{ "A", "A" });
        Assert.assertEquals(0, mapping.fieldMetaIndex(0));// A -> A

        Assert.assertEquals(FIELD_DOES_NOT_EXIST, mapping.fieldMetaIndex(1));// B -> undefined

        Assert.assertEquals(1, mapping.fieldMetaIndex(2));// A -> A

        mapping = NamedFieldsMapping.mapping(new String[]{ "A", "B", "A" }, new String[]{ "A", "X", "B", "Z" });
        Assert.assertEquals(0, mapping.fieldMetaIndex(0));// A -> A

        Assert.assertEquals(2, mapping.fieldMetaIndex(1));// B -> B

        Assert.assertEquals(1, mapping.fieldMetaIndex(2));// A -> X

        Assert.assertEquals(FIELD_DOES_NOT_EXIST, mapping.fieldMetaIndex(3));// undefined -> undefined

        mapping = NamedFieldsMapping.mapping(new String[]{ "A", "B", "A" }, new String[]{ "A", "A", "C" });
        Assert.assertEquals(0, mapping.fieldMetaIndex(0));// A -> A

        Assert.assertEquals(2, mapping.fieldMetaIndex(1));// B -> C

        Assert.assertEquals(1, mapping.fieldMetaIndex(2));// A -> A

        mapping = NamedFieldsMapping.mapping(new String[]{ "A", "B", "C", "D", "E" }, new String[]{ "X", "C", "Y", "B" });
        Assert.assertEquals(0, mapping.fieldMetaIndex(0));// A -> X

        Assert.assertEquals(3, mapping.fieldMetaIndex(1));// B -> B

        Assert.assertEquals(1, mapping.fieldMetaIndex(2));// C -> C

        Assert.assertEquals(2, mapping.fieldMetaIndex(3));// D -> Y

        Assert.assertEquals(FIELD_DOES_NOT_EXIST, mapping.fieldMetaIndex(4));// E -> undefined

    }
}

