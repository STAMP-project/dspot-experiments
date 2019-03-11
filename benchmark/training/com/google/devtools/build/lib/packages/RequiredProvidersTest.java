/**
 * Copyright 2016 The Bazel Authors. All rights reserved.
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
package com.google.devtools.build.lib.packages;


import AdvertisedProviderSet.ANY;
import AdvertisedProviderSet.EMPTY;
import Location.BUILTIN;
import com.google.common.collect.ImmutableSet;
import com.google.devtools.build.lib.cmdline.Label;
import com.google.devtools.build.lib.cmdline.LabelSyntaxException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Test for {@link com.google.devtools.build.lib.packages.RequiredProviders} class
 */
@RunWith(JUnit4.class)
public class RequiredProvidersTest {
    private static final String NO_PROVIDERS_REQUIRED = "no providers required";

    private static final class P1 {}

    private static final class P2 {}

    private static final class P3 {}

    private static final Provider P_NATIVE = new NativeProvider<StructImpl>(StructImpl.class, "p_native") {};

    private static final SkylarkProvider P_SKYLARK = SkylarkProvider.createUnexportedSchemaless(BUILTIN);

    static {
        try {
            RequiredProvidersTest.P_SKYLARK.export(Label.create("foo/bar", "x.bzl"), "p_skylark");
        } catch (LabelSyntaxException e) {
            throw new AssertionError(e);
        }
    }

    private static final SkylarkProviderIdentifier ID_NATIVE = SkylarkProviderIdentifier.forKey(RequiredProvidersTest.P_NATIVE.getKey());

    private static final SkylarkProviderIdentifier ID_SKYLARK = SkylarkProviderIdentifier.forKey(RequiredProvidersTest.P_SKYLARK.getKey());

    private static final SkylarkProviderIdentifier ID_LEGACY = SkylarkProviderIdentifier.forLegacy("p_legacy");

    @Test
    public void any() {
        assertThat(RequiredProvidersTest.satisfies(EMPTY, RequiredProviders.acceptAnyBuilder().build())).isTrue();
        assertThat(RequiredProvidersTest.satisfies(ANY, RequiredProviders.acceptAnyBuilder().build())).isTrue();
        assertThat(RequiredProvidersTest.satisfies(AdvertisedProviderSet.builder().addNative(RequiredProvidersTest.P1.class).build(), RequiredProviders.acceptAnyBuilder().build())).isTrue();
        assertThat(RequiredProvidersTest.satisfies(AdvertisedProviderSet.builder().addSkylark("p1").build(), RequiredProviders.acceptAnyBuilder().build())).isTrue();
    }

    @Test
    public void none() {
        assertThat(RequiredProvidersTest.satisfies(EMPTY, RequiredProviders.acceptNoneBuilder().build())).isFalse();
        assertThat(RequiredProvidersTest.satisfies(ANY, RequiredProviders.acceptNoneBuilder().build())).isFalse();
        assertThat(RequiredProvidersTest.satisfies(AdvertisedProviderSet.builder().addNative(RequiredProvidersTest.P1.class).build(), RequiredProviders.acceptNoneBuilder().build())).isFalse();
        assertThat(RequiredProvidersTest.satisfies(AdvertisedProviderSet.builder().addSkylark("p1").build(), RequiredProviders.acceptNoneBuilder().build())).isFalse();
    }

    @Test
    public void nativeProvidersAllMatch() {
        AdvertisedProviderSet providerSet = AdvertisedProviderSet.builder().addNative(RequiredProvidersTest.P1.class).addNative(RequiredProvidersTest.P2.class).build();
        assertThat(RequiredProvidersTest.validateNative(providerSet, RequiredProvidersTest.NO_PROVIDERS_REQUIRED, ImmutableSet.of(RequiredProvidersTest.P1.class, RequiredProvidersTest.P2.class))).isTrue();
    }

    @Test
    public void nativeProvidersBranchMatch() {
        assertThat(RequiredProvidersTest.validateNative(AdvertisedProviderSet.builder().addNative(RequiredProvidersTest.P1.class).build(), RequiredProvidersTest.NO_PROVIDERS_REQUIRED, ImmutableSet.<Class<?>>of(RequiredProvidersTest.P1.class), ImmutableSet.<Class<?>>of(RequiredProvidersTest.P2.class))).isTrue();
    }

    @Test
    public void nativeProvidersNoMatch() {
        assertThat(RequiredProvidersTest.validateNative(AdvertisedProviderSet.builder().addNative(RequiredProvidersTest.P3.class).build(), "P1 or P2", ImmutableSet.<Class<?>>of(RequiredProvidersTest.P1.class), ImmutableSet.<Class<?>>of(RequiredProvidersTest.P2.class))).isFalse();
    }

    @Test
    public void skylarkProvidersAllMatch() {
        AdvertisedProviderSet providerSet = AdvertisedProviderSet.builder().addSkylark(RequiredProvidersTest.ID_LEGACY).addSkylark(RequiredProvidersTest.ID_NATIVE).addSkylark(RequiredProvidersTest.ID_SKYLARK).build();
        assertThat(RequiredProvidersTest.validateSkylark(providerSet, RequiredProvidersTest.NO_PROVIDERS_REQUIRED, ImmutableSet.of(RequiredProvidersTest.ID_LEGACY, RequiredProvidersTest.ID_SKYLARK, RequiredProvidersTest.ID_NATIVE))).isTrue();
    }

    @Test
    public void skylarkProvidersBranchMatch() {
        assertThat(RequiredProvidersTest.validateSkylark(AdvertisedProviderSet.builder().addSkylark(RequiredProvidersTest.ID_LEGACY).build(), RequiredProvidersTest.NO_PROVIDERS_REQUIRED, ImmutableSet.of(RequiredProvidersTest.ID_LEGACY), ImmutableSet.of(RequiredProvidersTest.ID_NATIVE))).isTrue();
    }

    @Test
    public void skylarkProvidersNoMatch() {
        assertThat(RequiredProvidersTest.validateSkylark(AdvertisedProviderSet.builder().addSkylark(RequiredProvidersTest.ID_SKYLARK).build(), "'p_legacy' or 'p_native'", ImmutableSet.of(RequiredProvidersTest.ID_LEGACY), ImmutableSet.of(RequiredProvidersTest.ID_NATIVE))).isFalse();
    }

    @Test
    public void checkDescriptions() {
        assertThat(RequiredProviders.acceptAnyBuilder().build().getDescription()).isEqualTo("no providers required");
        assertThat(RequiredProviders.acceptNoneBuilder().build().getDescription()).isEqualTo("no providers accepted");
        assertThat(RequiredProviders.acceptAnyBuilder().addSkylarkSet(ImmutableSet.of(RequiredProvidersTest.ID_LEGACY, RequiredProvidersTest.ID_SKYLARK)).addSkylarkSet(ImmutableSet.of(RequiredProvidersTest.ID_SKYLARK)).addNativeSet(ImmutableSet.of(RequiredProvidersTest.P1.class, RequiredProvidersTest.P2.class)).build().getDescription()).isEqualTo("[P1, P2] or ['p_legacy', 'p_skylark'] or 'p_skylark'");
    }
}

