/**
 * Copyright 2013 Square Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package mortar;


import org.junit.Test;


public class MortarScopeDevHelperTest {
    private static final char BLANK = '\u00a0';

    @Test
    public void nestedScopeHierarchyToString() {
        MortarScope root = MortarScope.buildRootScope().build("Root");
        root.buildChild().build("Cadet");
        MortarScope colonel = root.buildChild().build("Colonel");
        colonel.buildChild().build("ElderColonel");
        colonel.buildChild().build("ZeElderColonel");
        MortarScope elder = root.buildChild().build("Elder");
        elder.buildChild().build("ElderCadet");
        elder.buildChild().build("ZeElderCadet");
        elder.buildChild().build("ElderElder");
        elder.buildChild().build("AnElderCadet");
        String hierarchy = MortarScopeDevHelper.scopeHierarchyToString(root);
        // 
        assertThat(hierarchy).isEqualTo((((((((((((((((((((((""// 
         + "Mortar Hierarchy:\n")// 
         + (MortarScopeDevHelperTest.BLANK)) + "SCOPE Root\n")// 
         + (MortarScopeDevHelperTest.BLANK)) + "+-SCOPE Cadet\n")// 
         + (MortarScopeDevHelperTest.BLANK)) + "+-SCOPE Colonel\n")// 
         + (MortarScopeDevHelperTest.BLANK)) + "| +-SCOPE ElderColonel\n")// 
         + (MortarScopeDevHelperTest.BLANK)) + "| `-SCOPE ZeElderColonel\n")// 
         + (MortarScopeDevHelperTest.BLANK)) + "`-SCOPE Elder\n")// 
         + (MortarScopeDevHelperTest.BLANK)) + "  +-SCOPE AnElderCadet\n")// 
         + (MortarScopeDevHelperTest.BLANK)) + "  +-SCOPE ElderCadet\n")// 
         + (MortarScopeDevHelperTest.BLANK)) + "  +-SCOPE ElderElder\n")// 
         + (MortarScopeDevHelperTest.BLANK)) + "  `-SCOPE ZeElderCadet\n"));
    }

    @Test
    public void startsFromMortarScope() {
        MortarScope root = MortarScope.buildRootScope().build("Root");
        MortarScope child = root.buildChild().build("Child");
        String hierarchy = MortarScopeDevHelper.scopeHierarchyToString(child);
        // 
        assertThat(hierarchy).isEqualTo((((((""// 
         + "Mortar Hierarchy:\n")// 
         + (MortarScopeDevHelperTest.BLANK)) + "SCOPE Root\n")// 
         + (MortarScopeDevHelperTest.BLANK)) + "`-SCOPE Child\n"));
    }

    @Test
    public void noSpaceAtLineBeginnings() {
        MortarScope root = MortarScope.buildRootScope().build("Root");
        MortarScope child = root.buildChild().build("Child");
        child.buildChild().build("Grand Child");
        String hierarchy = MortarScopeDevHelper.scopeHierarchyToString(root);
        // 
        assertThat(hierarchy).isEqualTo((((((((""// 
         + "Mortar Hierarchy:\n")// 
         + (MortarScopeDevHelperTest.BLANK)) + "SCOPE Root\n")// 
         + (MortarScopeDevHelperTest.BLANK)) + "`-SCOPE Child\n")// 
         + (MortarScopeDevHelperTest.BLANK)) + "  `-SCOPE Grand Child\n"));
    }
}

