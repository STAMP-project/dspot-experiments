/**
 * Copyright 2010-2012 VMware and contributors
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
package org.springsource.loaded.test;


import java.lang.reflect.Modifier;
import org.junit.Assert;
import org.junit.Test;
import org.springsource.loaded.TypeDelta;
import org.springsource.loaded.TypeDiffComputer;
import org.springsource.loaded.Utils;


/**
 * Tests for TypeDeltas which tell us about the differences between two class objects.
 *
 * @author Andy Clement
 * @since 1.0
 */
public class TypeDeltaTests extends SpringLoadedTests {
    @Test
    public void typesAreTheSame() {
        byte[] bytes = loadBytesForClass("differs.DiffOne");
        TypeDelta td = TypeDiffComputer.computeDifferences(bytes, bytes);
        Assert.assertFalse(td.hasAnythingChanged());
    }

    @Test
    public void basicTypeLevelChanges() {
        byte[] bytes = loadBytesForClass("differs.DiffOne");
        byte[] bytes2 = retrieveRename("differs.DiffOne", "differs.DiffOneX");
        TypeDelta td = TypeDiffComputer.computeDifferences(bytes, bytes2);
        Assert.assertTrue(td.hasAnythingChanged());
        Assert.assertTrue(td.hasTypeDeclarationChanged());
        Assert.assertTrue(td.hasTypeAccessChanged());
        Assert.assertTrue(Modifier.isPublic(td.oAccess));
        Assert.assertTrue((!(Modifier.isPublic(td.nAccess))));
    }

    @Test
    public void basicTypeLevelChanges2() {
        byte[] bytes = loadBytesForClass("differs.DiffOne");
        byte[] bytes2 = retrieveRename("differs.DiffOne", "differs.DiffOneY");
        TypeDelta td = TypeDiffComputer.computeDifferences(bytes, bytes2);
        Assert.assertTrue(td.hasAnythingChanged());
        Assert.assertTrue(td.hasTypeDeclarationChanged());
        Assert.assertTrue(td.hasTypeInterfacesChanged());
        Assert.assertEquals(0, td.oInterfaces.size());
        Assert.assertEquals("java/io/Serializable", td.nInterfaces.get(0));
    }

    @Test
    public void basicTypeLevelChanges3() {
        byte[] bytes = loadBytesForClass("differs.DiffThree");
        byte[] bytes2 = retrieveRename("differs.DiffThreeZ", "differs.DiffThreeX");
        TypeDelta td = TypeDiffComputer.computeDifferences(bytes, bytes2);
        Assert.assertTrue(td.hasAnythingChanged());
        Assert.assertTrue(td.hasTypeNameChanged());
        Assert.assertTrue(td.hasTypeSupertypeChanged());
        Assert.assertTrue(td.hasTypeInterfacesChanged());
        Assert.assertEquals(1, td.nInterfaces.size());
        Assert.assertFalse(td.hasTypeAccessChanged());
        Assert.assertFalse(td.haveFieldsChanged());
        Assert.assertFalse(td.haveFieldsChangedOrBeenAddedOrRemoved());
        Assert.assertFalse(td.hasTypeSignatureChanged());
        // As of May-2011 generic signature change is not a change
        // byte[] bytes3 = retrieveRename("differs.DiffThreeYY", "differs.DiffThreeY");
        // td = TypeDiffComputer.computeDifferences(bytes, bytes3);
        // assertTrue(td.hasTypeSignatureChanged());
    }

    @Test
    public void addedAField() {
        byte[] bytes = loadBytesForClass("differs.DiffOne");
        byte[] bytes2 = retrieveRename("differs.DiffOne", "differs.DiffOneZ");
        TypeDelta td = TypeDiffComputer.computeDifferences(bytes, bytes2);
        Assert.assertTrue(td.hasAnythingChanged());
        Assert.assertFalse(td.hasTypeNameChanged());
        Assert.assertFalse(td.hasTypeSupertypeChanged());
        Assert.assertFalse(td.hasTypeInterfacesChanged());
        Assert.assertFalse(td.hasTypeDeclarationChanged());
        Assert.assertTrue(td.haveFieldsChangedOrBeenAddedOrRemoved());
        Assert.assertTrue(td.hasNewFields());
        Assert.assertEquals(1, td.getNewFields().size());
        Assert.assertEquals("public I newIntField", Utils.fieldNodeFormat(td.getNewFields().get("newIntField")));
    }

    @Test
    public void removedAField() {
        byte[] bytes = loadBytesForClass("differs.DiffTwo");
        byte[] bytes2 = retrieveRename("differs.DiffTwo", "differs.DiffTwoX");
        TypeDelta td = TypeDiffComputer.computeDifferences(bytes, bytes2);
        Assert.assertTrue(td.hasAnythingChanged());
        Assert.assertFalse(td.hasTypeDeclarationChanged());
        Assert.assertTrue(td.haveFieldsChangedOrBeenAddedOrRemoved());
        Assert.assertTrue(td.hasLostFields());
        Assert.assertEquals(1, td.getLostFields().size());
        Assert.assertEquals("public I anIntField", Utils.fieldNodeFormat(td.getLostFields().get("anIntField")));
    }

    @Test
    public void changedAFieldType() {
        byte[] bytes = loadBytesForClass("differs.DiffTwo");
        byte[] bytes2 = retrieveRename("differs.DiffTwo", "differs.DiffTwoY");
        TypeDelta td = TypeDiffComputer.computeDifferences(bytes, bytes2);
        Assert.assertTrue(td.hasAnythingChanged());
        Assert.assertFalse(td.hasTypeDeclarationChanged());
        Assert.assertTrue(td.haveFieldsChangedOrBeenAddedOrRemoved());
        Assert.assertFalse(td.hasLostFields());
        Assert.assertFalse(td.hasNewFields());
        Assert.assertTrue(td.haveFieldsChanged());
        Assert.assertEquals(1, td.getChangedFields().size());
        Assert.assertEquals("FieldDelta[field:anIntField type:I>Ljava/lang/String;]", td.getChangedFields().get("anIntField").toString());
    }

    @Test
    public void changedAFieldAccess() {
        byte[] bytes = loadBytesForClass("differs.DiffTwo");
        byte[] bytes2 = retrieveRename("differs.DiffTwo", "differs.DiffTwoZ");
        TypeDelta td = TypeDiffComputer.computeDifferences(bytes, bytes2);
        Assert.assertTrue(td.hasAnythingChanged());
        Assert.assertFalse(td.hasTypeDeclarationChanged());
        Assert.assertTrue(td.haveFieldsChangedOrBeenAddedOrRemoved());
        Assert.assertFalse(td.hasLostFields());
        Assert.assertFalse(td.hasNewFields());
        Assert.assertTrue(td.haveFieldsChanged());
        Assert.assertEquals(1, td.getChangedFields().size());
        Assert.assertEquals("FieldDelta[field:anIntField access:1>2]", td.getChangedFields().get("anIntField").toString());
    }

    @Test
    public void changedFieldAnnotations() {
        byte[] bytes = loadBytesForClass("differs.AnnotFields");
        byte[] bytes2 = retrieveRename("differs.AnnotFields", "differs.AnnotFields2");
        TypeDelta td = TypeDiffComputer.computeDifferences(bytes, bytes2);
        Assert.assertTrue(td.hasAnythingChanged());
        Assert.assertFalse(td.hasTypeDeclarationChanged());
        Assert.assertTrue(td.haveFieldsChangedOrBeenAddedOrRemoved());
        Assert.assertFalse(td.hasLostFields());
        Assert.assertFalse(td.hasNewFields());
        Assert.assertTrue(td.haveFieldsChanged());
        Assert.assertEquals(1, td.getChangedFields().size());
        Assert.assertEquals("FieldDelta[field:i annotations:-differs/Annot]", td.getChangedFields().get("i").toString());
    }

    @Test
    public void changedFieldAnnotationValues() {
        byte[] bytes = loadBytesForClass("differs.AnnotFieldsTwo");
        byte[] bytes2 = retrieveRename("differs.AnnotFieldsTwo", "differs.AnnotFieldsTwo2");
        TypeDelta td = TypeDiffComputer.computeDifferences(bytes, bytes2);
        Assert.assertTrue(td.hasAnythingChanged());
        Assert.assertFalse(td.hasTypeDeclarationChanged());
        Assert.assertTrue(td.haveFieldsChangedOrBeenAddedOrRemoved());
        Assert.assertFalse(td.hasLostFields());
        Assert.assertFalse(td.hasNewFields());
        Assert.assertTrue(td.haveFieldsChanged());
        Assert.assertEquals(1, td.getChangedFields().size());
        Assert.assertEquals("FieldDelta[field:i annotations:-differs/Annot2(id=xyz)+differs/Annot2(id=xyz,value=24)]", td.getChangedFields().get("i").toString());
    }
}

