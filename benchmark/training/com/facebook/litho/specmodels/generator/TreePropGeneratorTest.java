/**
 * Copyright 2014-present Facebook, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.facebook.litho.specmodels.generator;


import com.facebook.litho.specmodels.model.DelegateMethod;
import com.facebook.litho.specmodels.model.SpecMethodModel;
import com.facebook.litho.specmodels.model.SpecModel;
import com.facebook.litho.specmodels.model.TreePropModel;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * Tests {@link TreePropGenerator}
 */
public class TreePropGeneratorTest {
    private final SpecModel mSpecModel = Mockito.mock(SpecModel.class);

    private final SpecModel mGenericSpecModel = Mockito.mock(SpecModel.class);

    private final TreePropModel mTreeProp = Mockito.mock(TreePropModel.class);

    private final TreePropModel mGenericTreeProp = Mockito.mock(TreePropModel.class);

    private SpecMethodModel<DelegateMethod, Void> mOnCreateTreePropMethodModel;

    private SpecMethodModel<DelegateMethod, Void> mGenericOnCreateTreePropMethodModel;

    @Test
    public void testGenerate() {
        TypeSpecDataHolder typeSpecDataHolder = TreePropGenerator.generate(mSpecModel);
        assertThat(typeSpecDataHolder.getFieldSpecs()).isEmpty();
        assertThat(typeSpecDataHolder.getMethodSpecs()).hasSize(2);
        assertThat(typeSpecDataHolder.getTypeSpecs()).isEmpty();
        assertThat(typeSpecDataHolder.getMethodSpecs().get(0).toString()).isEqualTo(("@java.lang.Override\n" + ((((("protected void populateTreeProps(com.facebook.litho.TreeProps treeProps) {\n" + "  if (treeProps == null) {\n") + "    return;\n") + "  }\n") + "  treeProp = treeProps.get(int.class);\n") + "}\n")));
        assertThat(typeSpecDataHolder.getMethodSpecs().get(1).toString()).isEqualTo(("@java.lang.Override\n" + (((((((("protected com.facebook.litho.TreeProps getTreePropsForChildren(com.facebook.litho.ComponentContext c,\n" + "    com.facebook.litho.TreeProps parentTreeProps) {\n") + "  final com.facebook.litho.TreeProps childTreeProps = com.facebook.litho.TreeProps.acquire(parentTreeProps);\n") + "  childTreeProps.put(boolean.class, TestSpec.onCreateTreeProp(\n") + "      (com.facebook.litho.ComponentContext) c,\n") + "      prop,\n") + "      mStateContainer.state));\n") + "  return childTreeProps;\n") + "}\n")));
    }

    @Test
    public void testGenericGenerate() {
        TypeSpecDataHolder typeSpecDataHolder = TreePropGenerator.generate(mGenericSpecModel);
        assertThat(typeSpecDataHolder.getFieldSpecs()).isEmpty();
        assertThat(typeSpecDataHolder.getMethodSpecs()).hasSize(2);
        assertThat(typeSpecDataHolder.getTypeSpecs()).isEmpty();
        assertThat(typeSpecDataHolder.getMethodSpecs().get(0).toString()).isEqualTo(("@java.lang.Override\n" + (((((("protected void populateTreeProps(com.facebook.litho.TreeProps treeProps) {\n" + "  if (treeProps == null) {\n") + "    return;\n") + "  }\n") + "  genericTreeProp = treeProps.get(com.facebook.litho.specmodels.generator.TreePropGeneratorTest.GenericObject.class);\n") + "  treeProp = treeProps.get(int.class);\n") + "}\n")));
        assertThat(typeSpecDataHolder.getMethodSpecs().get(1).toString()).isEqualTo(("@java.lang.Override\n" + ((((((("protected com.facebook.litho.TreeProps getTreePropsForChildren(com.facebook.litho.ComponentContext c,\n" + "    com.facebook.litho.TreeProps parentTreeProps) {\n") + "  final com.facebook.litho.TreeProps childTreeProps = com.facebook.litho.TreeProps.acquire(parentTreeProps);\n") + "  childTreeProps.put(com.facebook.litho.specmodels.generator.TreePropGeneratorTest.GenericObject.class, TestSpec.onCreateTreeProp(\n") + "      (com.facebook.litho.ComponentContext) c,\n") + "      prop));\n") + "  return childTreeProps;\n") + "}\n")));
    }

    private static class GenericObject<T> {
        T value;
    }
}

