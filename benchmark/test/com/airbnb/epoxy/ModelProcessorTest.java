package com.airbnb.epoxy;


import com.google.testing.compile.JavaFileObjects;
import javax.tools.JavaFileObject;
import junit.framework.Assert;
import org.junit.Test;


/**
 * These processor tests are in their own module since the processor module can't depend on the
 * android EpoxyAdapter library that contains the EpoxyModel.
 */
@SuppressWarnings("ResultOfMethodCallIgnored")
public class ModelProcessorTest {
    @Test
    public void testSimpleModel() {
        ProcessorTestUtils.assertGeneration("BasicModelWithAttribute.java", "BasicModelWithAttribute_.java");
    }

    @Test
    public void testModelWithAllFieldTypes() {
        ProcessorTestUtils.assertGeneration("ModelWithAllFieldTypes.java", "ModelWithAllFieldTypes_.java");
    }

    @Test
    public void testModelWithConstructors() {
        ProcessorTestUtils.assertGeneration("ModelWithConstructors.java", "ModelWithConstructors_.java");
    }

    @Test
    public void testModelWithSuper() {
        ProcessorTestUtils.assertGeneration("ModelWithSuper.java", "ModelWithSuper_.java");
    }

    @Test
    public void testModelWithFieldAnnotation() {
        ProcessorTestUtils.assertGeneration("ModelWithFieldAnnotation.java", "ModelWithFieldAnnotation_.java");
    }

    @Test
    public void testModelWithSuperClass() {
        JavaFileObject model = JavaFileObjects.forResource("ModelWithSuperAttributes.java");
        JavaFileObject generatedModel = JavaFileObjects.forResource("ModelWithSuperAttributes_.java");
        JavaFileObject generatedSubClassModel = JavaFileObjects.forResource("ModelWithSuperAttributes$SubModelWithSuperAttributes_.java");
        assert_().about(javaSource()).that(model).processedWith(new EpoxyProcessor()).compilesWithoutError().and().generatesSources(generatedModel, generatedSubClassModel);
    }

    @Test
    public void testModelWithType() {
        ProcessorTestUtils.assertGeneration("ModelWithType.java", "ModelWithType_.java");
    }

    @Test
    public void testModelWithoutHash() {
        ProcessorTestUtils.assertGeneration("ModelWithoutHash.java", "ModelWithoutHash_.java");
    }

    @Test
    public void testDoNotHashModel() {
        ProcessorTestUtils.assertGeneration("ModelDoNotHash.java", "ModelDoNotHash_.java");
    }

    @Test
    public void testModelWithFinalAttribute() {
        ProcessorTestUtils.assertGeneration("ModelWithFinalField.java", "ModelWithFinalField_.java");
    }

    @Test
    public void testModelWithStaticAttributeFails() {
        ProcessorTestUtils.assertGenerationError("ModelWithStaticField.java", "static");
    }

    @Test
    public void testModelWithPrivateClassFails() {
        ProcessorTestUtils.assertGenerationError("ModelWithPrivateInnerClass.java", "private classes");
    }

    @Test
    public void testModelWithFinalClassFails() {
        ProcessorTestUtils.assertGenerationError("ModelWithFinalClass.java", "");
    }

    @Test
    public void testModelThatDoesNotExtendEpoxyModelFails() {
        ProcessorTestUtils.assertGenerationError("ModelWithoutEpoxyExtension.java", "must extend");
    }

    @Test
    public void testModelAsInnerClassFails() {
        ProcessorTestUtils.assertGenerationError("ModelAsInnerClass.java", "Nested classes");
    }

    @Test
    public void testModelWithIntDefAnnotation() {
        ProcessorTestUtils.assertGeneration("ModelWithIntDef.java", "ModelWithIntDef_.java");
    }

    @Test
    public void testModelWithAnnotatedClass() {
        ProcessorTestUtils.assertGeneration("ModelWithAnnotatedClass.java", "ModelWithAnnotatedClass_.java");
    }

    @Test
    public void testModelWithAbstractClass() {
        JavaFileObject model = JavaFileObjects.forResource("ModelWithAbstractClass.java");
        assert_().about(javaSource()).that(model).processedWith(new EpoxyProcessor()).compilesWithoutError();
        // We don't generate subclasses if the model is abstract unless it has a class annotation.
        boolean modelNotGenerated;
        try {
            JavaFileObjects.forResource("ModelWithAbstractClass_.java");
            modelNotGenerated = false;
        } catch (IllegalArgumentException e) {
            modelNotGenerated = true;
        }
        Assert.assertTrue(modelNotGenerated);
    }

    @Test
    public void testModelWithAbstractClassAndAnnotation() {
        ProcessorTestUtils.assertGeneration("ModelWithAbstractClassAndAnnotation.java", "ModelWithAbstractClassAndAnnotation_.java");
    }

    @Test
    public void testModelWithAnnotatedClassAndSuperClass() {
        JavaFileObject model = JavaFileObjects.forResource("ModelWithAnnotatedClassAndSuperAttributes.java");
        JavaFileObject generatedModel = JavaFileObjects.forResource("ModelWithAnnotatedClassAndSuperAttributes_.java");
        JavaFileObject generatedSubClassModel = JavaFileObjects.forResource(("ModelWithAnnotatedClassAndSuperAttributes$SubModel" + "WithAnnotatedClassAndSuperAttributes_.java"));
        assert_().about(javaSource()).that(model).processedWith(new EpoxyProcessor()).compilesWithoutError().and().generatesSources(generatedModel, generatedSubClassModel);
    }

    @Test
    public void testModelWithoutSetter() {
        ProcessorTestUtils.assertGeneration("ModelWithoutSetter.java", "ModelWithoutSetter_.java");
    }

    @Test
    public void testModelReturningClassType() {
        ProcessorTestUtils.assertGeneration("ModelReturningClassType.java", "ModelReturningClassType_.java");
    }

    @Test
    public void testModelReturningClassTypeWithVarargs() {
        ProcessorTestUtils.assertGeneration("ModelReturningClassTypeWithVarargs.java", "ModelReturningClassTypeWithVarargs_.java");
    }

    @Test
    public void testModelWithVarargsConstructors() {
        ProcessorTestUtils.assertGeneration("ModelWithVarargsConstructors.java", "ModelWithVarargsConstructors_.java");
    }

    @Test
    public void testModelWithHolderGeneratesNewHolderMethod() {
        ProcessorTestUtils.assertGeneration("AbstractModelWithHolder.java", "AbstractModelWithHolder_.java");
    }

    @Test
    public void testGenerateDefaultLayoutMethod() {
        ProcessorTestUtils.assertGeneration("GenerateDefaultLayoutMethod.java", "GenerateDefaultLayoutMethod_.java");
    }

    @Test
    public void testGenerateDefaultLayoutMethodFailsIfLayoutNotSpecified() {
        ProcessorTestUtils.assertGenerationError("GenerateDefaultLayoutMethodNoLayout.java", "Model must specify a valid layout resource");
    }

    @Test
    public void testGeneratedDefaultMethodWithLayoutSpecifiedInParent() {
        JavaFileObject model = JavaFileObjects.forResource("GenerateDefaultLayoutMethodParentLayout.java");
        JavaFileObject generatedNoLayoutModel = JavaFileObjects.forResource("GenerateDefaultLayoutMethodParentLayout$NoLayout_.java");
        JavaFileObject generatedWithLayoutModel = JavaFileObjects.forResource("GenerateDefaultLayoutMethodParentLayout$WithLayout_.java");
        assert_().about(javaSource()).that(model).processedWith(new EpoxyProcessor()).compilesWithoutError().and().generatesSources(generatedNoLayoutModel, generatedWithLayoutModel);
    }

    @Test
    public void testGeneratedDefaultMethodWithLayoutSpecifiedInNextParent() {
        JavaFileObject model = JavaFileObjects.forResource("GenerateDefaultLayoutMethodNextParentLayout.java");
        JavaFileObject generatedNoLayoutModel = JavaFileObjects.forResource("GenerateDefaultLayoutMethodNextParentLayout$NoLayout_.java");
        JavaFileObject generatedStillNoLayoutModel = JavaFileObjects.forResource("GenerateDefaultLayoutMethodNextParentLayout$StillNoLayout_.java");
        JavaFileObject generatedWithLayoutModel = JavaFileObjects.forResource("GenerateDefaultLayoutMethodNextParentLayout$WithLayout_.java");
        assert_().about(javaSource()).that(model).processedWith(new EpoxyProcessor()).compilesWithoutError().and().generatesSources(generatedNoLayoutModel, generatedStillNoLayoutModel, generatedWithLayoutModel);
    }

    @Test
    public void testGeneratedDefaultMethodWithLayoutFailsIfNotSpecifiedInParent() {
        ProcessorTestUtils.assertGenerationError("GenerateDefaultLayoutMethodParentStillNoLayout.java", "Model must specify a valid layout resource");
    }

    @Test
    public void modelWithViewClickListener() {
        ProcessorTestUtils.assertGeneration("ModelWithViewClickListener.java", "ModelWithViewClickListener_.java");
    }

    @Test
    public void modelWithViewClickLongListener() {
        JavaFileObject model = JavaFileObjects.forResource("ModelWithViewLongClickListener.java");
        JavaFileObject generatedNoLayoutModel = JavaFileObjects.forResource("ModelWithViewLongClickListener_.java");
        assert_().about(javaSource()).that(model).processedWith(new EpoxyProcessor()).compilesWithoutError().and().generatesSources(generatedNoLayoutModel);
    }

    @Test
    public void testModelWithPrivateAttributeWithoutGetterAndSetterFails() {
        ProcessorTestUtils.assertGenerationError("ModelWithPrivateFieldWithoutGetterAndSetter.java", "private fields without proper getter and setter");
    }

    @Test
    public void testModelWithPrivateAttributeWithoutSetterFails() {
        ProcessorTestUtils.assertGenerationError("ModelWithPrivateFieldWithoutSetter.java", "private fields without proper getter and setter");
    }

    @Test
    public void testModelWithPrivateAttributeWithoutGetterFails() {
        ProcessorTestUtils.assertGenerationError("ModelWithPrivateFieldWithoutGetter.java", "private fields without proper getter and setter");
    }

    @Test
    public void testModelWithPrivateAttributeWithIsPrefixGetter() {
        ProcessorTestUtils.checkFileCompiles("ModelWithPrivateFieldWithIsPrefixGetter.java");
    }

    @Test
    public void testModelWithPrivateAttributeWithPrivateGetterFails() {
        ProcessorTestUtils.assertGenerationError("ModelWithPrivateFieldWithPrivateGetter.java", "private fields without proper getter and setter");
    }

    @Test
    public void testModelWithPrivateAttributeWithStaticGetterFails() {
        ProcessorTestUtils.assertGenerationError("ModelWithPrivateFieldWithStaticGetter.java", "private fields without proper getter and setter");
    }

    @Test
    public void testModelWithPrivateAttributeWithGetterWithParamsFails() {
        ProcessorTestUtils.assertGenerationError("ModelWithPrivateFieldWithGetterWithParams.java", "private fields without proper getter and setter");
    }

    @Test
    public void testModelWithPrivateAttributeWithPrivateSetterFails() {
        ProcessorTestUtils.assertGenerationError("ModelWithPrivateFieldWithPrivateSetter.java", "private fields without proper getter and setter");
    }

    @Test
    public void testModelWithPrivateAttributeWithStaticSetterFails() {
        ProcessorTestUtils.assertGenerationError("ModelWithPrivateFieldWithStaticSetter.java", "private fields without proper getter and setter");
    }

    @Test
    public void testModelWithPrivateAttributeWithGetterWithoutParamsFails() {
        ProcessorTestUtils.assertGenerationError("ModelWithPrivateFieldWithSettterWithoutParams.java", "private fields without proper getter and setter");
    }

    @Test
    public void testModelWithAllPrivateFieldTypes() {
        ProcessorTestUtils.assertGeneration("ModelWithAllPrivateFieldTypes.java", "ModelWithAllPrivateFieldTypes_.java");
    }

    @Test
    public void modelWithViewPrivateClickListener() {
        ProcessorTestUtils.assertGeneration("ModelWithPrivateViewClickListener.java", "ModelWithPrivateViewClickListener_.java");
    }

    @Test
    public void modelWithPrivateFieldWithSameAsFieldGetterAndSetterName() {
        ProcessorTestUtils.assertGeneration("ModelWithPrivateFieldWithSameAsFieldGetterAndSetterName.java", "ModelWithPrivateFieldWithSameAsFieldGetterAndSetterName_.java");
    }

    @Test
    public void testDoNotUseInToStringModel() {
        ProcessorTestUtils.assertGeneration("ModelDoNotUseInToString.java", "ModelDoNotUseInToString_.java");
    }

    @Test
    public void modelWithAnnotation() {
        ProcessorTestUtils.assertGeneration("ModelWithAnnotation.java", "ModelWithAnnotation_.java");
    }

    @Test
    public void testModelBuilderInterface() {
        ProcessorTestUtils.assertGeneration("ModelWithAllFieldTypes.java", "ModelWithAllFieldTypesBuilder.java");
    }

    @Test
    public void generatedEpoxyModelGroup() {
        ProcessorTestUtils.assertGeneration("EpoxyModelGroupWithAnnotations.java", "EpoxyModelGroupWithAnnotations_.java");
    }
}

