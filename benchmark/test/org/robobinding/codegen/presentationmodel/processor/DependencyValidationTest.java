package org.robobinding.codegen.presentationmodel.processor;


import com.google.common.collect.Sets;
import java.util.Set;
import org.junit.Test;


/**
 *
 *
 * @since 1.0
 * @version $Revision: 1.0 $
 * @author Cheng Wei
 */
public class DependencyValidationTest {
    private static final String PROPERTY_NAME = "property0";

    private Set<String> existingPropertyNames;

    private DependencyValidation validation;

    @Test
    public void givenValidDependentProperties_thenSuccessful() {
        validation.validate(DependencyValidationTest.PROPERTY_NAME, Sets.newHashSet("property1", "property2"));
    }

    @Test(expected = RuntimeException.class)
    public void givenDependingOnSelf_thenThrowException() {
        validation.validate(DependencyValidationTest.PROPERTY_NAME, Sets.newHashSet("property1", DependencyValidationTest.PROPERTY_NAME, "property2"));
    }

    @Test(expected = RuntimeException.class)
    public void whenCreateWithSomeNonExistingDependentProperties_thenThrowException() {
        validation.validate(DependencyValidationTest.PROPERTY_NAME, Sets.newHashSet("property1", "nonExistingProperty", "property2"));
    }
}

