/**
 * Copyright 2017 The Bazel Authors. All rights reserved.
 */
/**
 *
 */
/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
/**
 * you may not use this file except in compliance with the License.
 */
/**
 * You may obtain a copy of the License at
 */
/**
 *
 */
/**
 * http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 *
 */
/**
 * Unless required by applicable law or agreed to in writing, software
 */
/**
 * distributed under the License is distributed on an "AS IS" BASIS,
 */
/**
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
/**
 * See the License for the specific language governing permissions and
 */
/**
 * limitations under the License.
 */
package com.google.devtools.common.options.processor;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Unit tests for the compile-time checks in {@link OptionProcessor}.
 */
@RunWith(JUnit4.class)
public class OptionProcessorTest {
    @Test
    public void optionsInNonOptionBasesAreRejected() {
        assertAbout(javaSource()).that(OptionProcessorTest.getFile("OptionInNonOptionBase.java")).processedWith(new OptionProcessor()).failsToCompile().withErrorContaining("@Option annotated fields can only be in classes that inherit from OptionsBase.");
    }

    @Test
    public void privatelyDeclaredOptionsAreRejected() {
        assertAbout(javaSource()).that(OptionProcessorTest.getFile("PrivateOptionField.java")).processedWith(new OptionProcessor()).failsToCompile().withErrorContaining("@Option annotated fields should be public.");
    }

    @Test
    public void protectedOptionsAreRejected() {
        assertAbout(javaSource()).that(OptionProcessorTest.getFile("ProtectedOptionField.java")).processedWith(new OptionProcessor()).failsToCompile().withErrorContaining("@Option annotated fields should be public.");
    }

    @Test
    public void staticOptionsAreRejected() {
        assertAbout(javaSource()).that(OptionProcessorTest.getFile("StaticOptionField.java")).processedWith(new OptionProcessor()).failsToCompile().withErrorContaining("@Option annotated fields should not be static.");
    }

    @Test
    public void finalOptionsAreRejected() {
        assertAbout(javaSource()).that(OptionProcessorTest.getFile("FinalOptionField.java")).processedWith(new OptionProcessor()).failsToCompile().withErrorContaining("@Option annotated fields should not be final.");
    }

    @Test
    public void namelessOptionsAreRejected() {
        assertAbout(javaSource()).that(OptionProcessorTest.getFile("NamelessOption.java")).processedWith(new OptionProcessor()).failsToCompile().withErrorContaining("Option must have an actual name.");
    }

    @Test
    public void badNamesAreRejected() {
        assertAbout(javaSource()).that(OptionProcessorTest.getFile("BadNameForDocumentedOption.java")).processedWith(new OptionProcessor()).failsToCompile().withErrorContaining(("Options that are used on the command line as flags must have names made from word " + "characters only."));
        assertAbout(javaSource()).that(OptionProcessorTest.getFile("BadNameWithEqualsSign.java")).processedWith(new OptionProcessor()).failsToCompile().withErrorContaining(("Options that are used on the command line as flags must have names made from word " + "characters only."));
    }

    @Test
    public void badNamesForHiddenOptionsPass() {
        assertAbout(javaSource()).that(OptionProcessorTest.getFile("BadNameForInternalOption.java")).processedWith(new OptionProcessor()).compilesWithoutError();
    }

    @Test
    public void deprecatedCategorySaysUndocumented() {
        assertAbout(javaSource()).that(OptionProcessorTest.getFile("DeprecatedUndocumentedCategory.java")).processedWith(new OptionProcessor()).failsToCompile().withErrorContaining(("Documentation level is no longer read from the option category. Category " + "\"undocumented\" is disallowed, see OptionMetadataTags for the relevant tags."));
    }

    @Test
    public void deprecatedCategorySaysHidden() {
        assertAbout(javaSource()).that(OptionProcessorTest.getFile("DeprecatedHiddenCategory.java")).processedWith(new OptionProcessor()).failsToCompile().withErrorContaining(("Documentation level is no longer read from the option category. Category " + "\"hidden\" is disallowed, see OptionMetadataTags for the relevant tags."));
    }

    @Test
    public void deprecatedCategorySaysInternal() {
        assertAbout(javaSource()).that(OptionProcessorTest.getFile("DeprecatedInternalCategory.java")).processedWith(new OptionProcessor()).failsToCompile().withErrorContaining(("Documentation level is no longer read from the option category. Category " + "\"internal\" is disallowed, see OptionMetadataTags for the relevant tags."));
    }

    @Test
    public void optionMustHaveEffectExplicitlyStated() {
        assertAbout(javaSource()).that(OptionProcessorTest.getFile("EffectlessOption.java")).processedWith(new OptionProcessor()).failsToCompile().withErrorContaining(("Option does not list at least one OptionEffectTag. " + ("If the option has no effect, please be explicit and add NO_OP. " + "Otherwise, add a tag representing its effect.")));
    }

    @Test
    public void contradictingEffectTagsAreRejected() {
        assertAbout(javaSource()).that(OptionProcessorTest.getFile("OptionWithContradictingNoopEffects.java")).processedWith(new OptionProcessor()).failsToCompile().withErrorContaining(("Option includes NO_OP with other effects. This doesn't make much sense. " + "Please remove NO_OP or the actual effects from the list, whichever is correct."));
        assertAbout(javaSource()).that(OptionProcessorTest.getFile("OptionWithContradictingUnknownEffects.java")).processedWith(new OptionProcessor()).failsToCompile().withErrorContaining(("Option includes UNKNOWN with other, known, effects. " + "Please remove UNKNOWN from the list."));
    }

    @Test
    public void contradictoryDocumentationCategoryIsRejected() {
        assertAbout(javaSource()).that(OptionProcessorTest.getFile("HiddenOptionWithCategory.java")).processedWith(new OptionProcessor()).failsToCompile().withErrorContaining(("Option has metadata tag HIDDEN but does not have category UNDOCUMENTED. " + "Please fix."));
        assertAbout(javaSource()).that(OptionProcessorTest.getFile("InternalOptionWithCategory.java")).processedWith(new OptionProcessor()).failsToCompile().withErrorContaining(("Option has metadata tag INTERNAL but does not have category UNDOCUMENTED. " + "Please fix."));
    }

    @Test
    public void defaultConvertersAreFound() {
        assertAbout(javaSource()).that(OptionProcessorTest.getFile("AllDefaultConverters.java")).processedWith(new OptionProcessor()).compilesWithoutError();
    }

    @Test
    public void defaultConvertersForAllowMultipleOptionsAreFound() {
        assertAbout(javaSource()).that(OptionProcessorTest.getFile("AllDefaultConvertersWithAllowMultiple.java")).processedWith(new OptionProcessor()).compilesWithoutError();
    }

    @Test
    public void converterReturnsListForAllowMultipleIsAllowed() {
        assertAbout(javaSource()).that(OptionProcessorTest.getFile("MultipleOptionWithListTypeConverter.java")).processedWith(new OptionProcessor()).compilesWithoutError();
    }

    @Test
    public void correctCustomConverterForPrimitiveTypePasses() {
        assertAbout(javaSource()).that(OptionProcessorTest.getFile("CorrectCustomConverterForPrimitiveType.java")).processedWith(new OptionProcessor()).compilesWithoutError();
    }

    @Test
    public void converterlessOptionIsRejected() {
        assertAbout(javaSource()).that(OptionProcessorTest.getFile("ConverterlessOption.java")).processedWith(new OptionProcessor()).failsToCompile().withErrorContaining(("Cannot find valid converter for option of type " + "java.util.Map<java.lang.String,java.lang.String>"));
    }

    @Test
    public void allowMultipleOptionWithCollectionTypeIsRejected() {
        assertAbout(javaSource()).that(OptionProcessorTest.getFile("CollectionTypeForAllowMultipleOption.java")).processedWith(new OptionProcessor()).failsToCompile().withErrorContaining(("Option that allows multiple occurrences must be of type java.util.List<E>, " + "but is of type java.util.Collection<java.lang.String>"));
    }

    @Test
    public void allowMultipleOptionWithNonListTypeIsRejected() {
        assertAbout(javaSource()).that(OptionProcessorTest.getFile("NonListTypeForAllowMultipleOption.java")).processedWith(new OptionProcessor()).failsToCompile().withErrorContaining(("Option that allows multiple occurrences must be of type java.util.List<E>, " + "but is of type java.lang.String"));
    }

    @Test
    public void optionWithIncorrectConverterIsRejected() {
        assertAbout(javaSource()).that(OptionProcessorTest.getFile("IncorrectConverterType.java")).processedWith(new OptionProcessor()).failsToCompile().withErrorContaining(("Type of field (java.lang.String) must be assignable from the converter's return type " + "(java.lang.Integer)"));
    }

    @Test
    public void allowMultipleOptionWithIncorrectConverterIsRejected() {
        assertAbout(javaSource()).that(OptionProcessorTest.getFile("IncorrectConverterTypeForAllowMultipleOption.java")).processedWith(new OptionProcessor()).failsToCompile().withErrorContaining(("Type of field (java.lang.String) must be assignable from the converter's return type " + "(java.lang.Integer)"));
    }

    @Test
    public void expansionOptionThatAllowsMultipleIsRejected() {
        assertAbout(javaSource()).that(OptionProcessorTest.getFile("ExpansionOptionWithAllowMultiple.java")).processedWith(new OptionProcessor()).failsToCompile().withErrorContaining("Can't set an option to accumulate multiple values and let it expand to other flags.");
    }

    @Test
    public void functionalExpansionOptionThatAllowsMultipleIsRejected() {
        assertAbout(javaSource()).that(OptionProcessorTest.getFile("FunctionalExpansionOptionWithAllowMultiple.java")).processedWith(new OptionProcessor()).failsToCompile().withErrorContaining("Can't set an option to accumulate multiple values and let it expand to other flags.");
    }

    @Test
    public void expansionOptionWithImplicitRequirementIsRejected() {
        assertAbout(javaSource()).that(OptionProcessorTest.getFile("ExpansionOptionWithImplicitRequirement.java")).processedWith(new OptionProcessor()).failsToCompile().withErrorContaining("Can't set an option to be both an expansion option and have implicit requirements.");
    }

    @Test
    public void expansionOptionThatExpandsInTwoWaysIsRejected() {
        assertAbout(javaSource()).that(OptionProcessorTest.getFile("DoubleExpansionOption.java")).processedWith(new OptionProcessor()).failsToCompile().withErrorContaining("Options cannot expand using both a static expansion list and an expansion function.");
    }
}

