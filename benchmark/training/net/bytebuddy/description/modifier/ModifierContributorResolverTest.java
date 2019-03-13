package net.bytebuddy.description.modifier;


import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.objectweb.asm.Opcodes;

import static net.bytebuddy.description.modifier.ModifierContributor.Resolver.of;


public class ModifierContributorResolverTest {
    @Test
    public void testForType() throws Exception {
        MatcherAssert.assertThat(ModifierContributor.Resolver.of(Visibility.PUBLIC, TypeManifestation.FINAL).resolve(), CoreMatchers.is(((Opcodes.ACC_PUBLIC) | (Opcodes.ACC_FINAL))));
        MatcherAssert.assertThat(ModifierContributor.Resolver.of(Visibility.PUBLIC, TypeManifestation.INTERFACE, TypeManifestation.FINAL).resolve(), CoreMatchers.is(((Opcodes.ACC_PUBLIC) | (Opcodes.ACC_FINAL))));
        MatcherAssert.assertThat(ModifierContributor.Resolver.of(Visibility.PUBLIC, TypeManifestation.FINAL).resolve(1), CoreMatchers.is((((Opcodes.ACC_PUBLIC) | (Opcodes.ACC_FINAL)) | 1)));
    }

    @Test
    public void testForField() throws Exception {
        MatcherAssert.assertThat(ModifierContributor.Resolver.of(Visibility.PUBLIC, FieldManifestation.FINAL).resolve(), CoreMatchers.is(((Opcodes.ACC_PUBLIC) | (Opcodes.ACC_FINAL))));
        MatcherAssert.assertThat(ModifierContributor.Resolver.of(Visibility.PUBLIC, FieldManifestation.VOLATILE, FieldManifestation.FINAL).resolve(), CoreMatchers.is(((Opcodes.ACC_PUBLIC) | (Opcodes.ACC_FINAL))));
        MatcherAssert.assertThat(ModifierContributor.Resolver.of(Visibility.PUBLIC, FieldManifestation.FINAL).resolve(1), CoreMatchers.is((((Opcodes.ACC_PUBLIC) | (Opcodes.ACC_FINAL)) | 1)));
    }

    @Test
    public void testForMethod() throws Exception {
        MatcherAssert.assertThat(ModifierContributor.Resolver.of(Visibility.PUBLIC, MethodManifestation.FINAL).resolve(), CoreMatchers.is(((Opcodes.ACC_PUBLIC) | (Opcodes.ACC_FINAL))));
        MatcherAssert.assertThat(ModifierContributor.Resolver.of(Visibility.PUBLIC, MethodManifestation.ABSTRACT, MethodManifestation.FINAL).resolve(), CoreMatchers.is(((Opcodes.ACC_PUBLIC) | (Opcodes.ACC_FINAL))));
        MatcherAssert.assertThat(ModifierContributor.Resolver.of(Visibility.PUBLIC, MethodManifestation.FINAL).resolve(1), CoreMatchers.is((((Opcodes.ACC_PUBLIC) | (Opcodes.ACC_FINAL)) | 1)));
    }

    @Test
    public void testForParameter() throws Exception {
        MatcherAssert.assertThat(of(ProvisioningState.MANDATED, ParameterManifestation.FINAL).resolve(), CoreMatchers.is(((Opcodes.ACC_MANDATED) | (Opcodes.ACC_FINAL))));
        MatcherAssert.assertThat(of(ProvisioningState.MANDATED, ParameterManifestation.PLAIN, ParameterManifestation.FINAL).resolve(), CoreMatchers.is(((Opcodes.ACC_MANDATED) | (Opcodes.ACC_FINAL))));
        MatcherAssert.assertThat(of(ProvisioningState.MANDATED, ParameterManifestation.FINAL).resolve(1), CoreMatchers.is((((Opcodes.ACC_MANDATED) | (Opcodes.ACC_FINAL)) | 1)));
    }
}

