/**
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.ap.test.accessibility;


import java.lang.reflect.Modifier;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mapstruct.ap.testutil.IssueKey;
import org.mapstruct.ap.testutil.WithClasses;
import org.mapstruct.ap.testutil.runner.AnnotationProcessorTestRunner;


/**
 * Test for different accessibility modifiers
 *
 * @author Andreas Gudian
 */
@WithClasses({ Source.class, Target.class, DefaultSourceTargetMapperAbstr.class, DefaultSourceTargetMapperIfc.class })
@RunWith(AnnotationProcessorTestRunner.class)
public class AccessibilityTest {
    @Test
    @IssueKey("103")
    public void testGeneratedModifiersFromAbstractClassAreCorrect() throws Exception {
        Class<?> defaultFromAbstract = AccessibilityTest.loadForMapper(DefaultSourceTargetMapperAbstr.class);
        Assert.assertTrue(AccessibilityTest.isDefault(defaultFromAbstract.getModifiers()));
        Assert.assertTrue(Modifier.isPublic(modifiersFor(defaultFromAbstract, "publicSourceToTarget")));
        Assert.assertTrue(Modifier.isProtected(modifiersFor(defaultFromAbstract, "protectedSourceToTarget")));
        Assert.assertTrue(AccessibilityTest.isDefault(modifiersFor(defaultFromAbstract, "defaultSourceToTarget")));
    }

    @Test
    @IssueKey("103")
    public void testGeneratedModifiersFromInterfaceAreCorrect() throws Exception {
        Class<?> defaultFromIfc = AccessibilityTest.loadForMapper(DefaultSourceTargetMapperIfc.class);
        Assert.assertTrue(AccessibilityTest.isDefault(defaultFromIfc.getModifiers()));
        Assert.assertTrue(Modifier.isPublic(modifiersFor(defaultFromIfc, "implicitlyPublicSoureToTarget")));
    }
}

