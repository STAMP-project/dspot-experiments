/**
 * Copyright 2015 The Bazel Authors. All rights reserved.
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
package com.google.devtools.build.lib.skyframe;


import ErrorReason.DELETED_PACKAGE;
import com.google.common.collect.ImmutableSet;
import com.google.common.testing.EqualsTester;
import com.google.devtools.build.lib.cmdline.PackageIdentifier;
import com.google.devtools.build.lib.cmdline.RepositoryName;
import com.google.devtools.build.lib.skyframe.ContainingPackageLookupValue.ContainingPackage;
import com.google.devtools.build.lib.skyframe.ContainingPackageLookupValue.NoContainingPackage;
import com.google.devtools.build.lib.testutil.FoundationTestCase;
import com.google.devtools.build.lib.vfs.PathFragment;
import com.google.devtools.build.lib.vfs.Root;
import com.google.devtools.build.skyframe.MemoizingEvaluator;
import com.google.devtools.build.skyframe.RecordingDifferencer;
import com.google.devtools.build.skyframe.SequentialBuildDriver;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static ContainingPackageLookupValue.NONE;


/**
 * Tests for {@link ContainingPackageLookupFunction}.
 */
@RunWith(JUnit4.class)
public class ContainingPackageLookupFunctionTest extends FoundationTestCase {
    private AtomicReference<ImmutableSet<PackageIdentifier>> deletedPackages;

    private MemoizingEvaluator evaluator;

    private SequentialBuildDriver driver;

    private RecordingDifferencer differencer;

    @Test
    public void testNoContainingPackage() throws Exception {
        ContainingPackageLookupValue value = lookupContainingPackage("a/b");
        assertThat(value.hasContainingPackage()).isFalse();
    }

    @Test
    public void testContainingPackageIsParent() throws Exception {
        scratch.file("a/BUILD");
        ContainingPackageLookupValue value = lookupContainingPackage("a/b");
        assertThat(value.hasContainingPackage()).isTrue();
        assertThat(value.getContainingPackageName()).isEqualTo(PackageIdentifier.createInMainRepo("a"));
        assertThat(value.getContainingPackageRoot()).isEqualTo(Root.fromPath(rootDirectory));
    }

    @Test
    public void testContainingPackageIsSelf() throws Exception {
        scratch.file("a/b/BUILD");
        ContainingPackageLookupValue value = lookupContainingPackage("a/b");
        assertThat(value.hasContainingPackage()).isTrue();
        assertThat(value.getContainingPackageName()).isEqualTo(PackageIdentifier.createInMainRepo("a/b"));
        assertThat(value.getContainingPackageRoot()).isEqualTo(Root.fromPath(rootDirectory));
    }

    @Test
    public void testContainingPackageIsExternalRepositoryViaExternalRepository() throws Exception {
        scratch.overwriteFile("WORKSPACE", "local_repository(name='a', path='a')");
        scratch.file("a/WORKSPACE");
        scratch.file("a/BUILD");
        scratch.file("a/b/BUILD");
        ContainingPackageLookupValue value = lookupContainingPackage(PackageIdentifier.create(RepositoryName.create("@a"), PathFragment.create("b")));
        assertThat(value.hasContainingPackage()).isTrue();
        assertThat(value.getContainingPackageName()).isEqualTo(PackageIdentifier.create(RepositoryName.create("@a"), PathFragment.create("b")));
    }

    @Test
    public void testContainingPackageIsExternalRepositoryViaLocalPath() throws Exception {
        scratch.overwriteFile("WORKSPACE", "local_repository(name='a', path='a')");
        scratch.file("a/WORKSPACE");
        scratch.file("a/BUILD");
        scratch.file("a/b/BUILD");
        ContainingPackageLookupValue value = lookupContainingPackage("a/b");
        assertThat(value.hasContainingPackage()).isTrue();
        assertThat(value.getContainingPackageName()).isEqualTo(PackageIdentifier.create(RepositoryName.create("@a"), PathFragment.create("b")));
    }

    @Test
    public void testEqualsAndHashCodeContract() throws Exception {
        ContainingPackageLookupValue valueA1 = NONE;
        ContainingPackageLookupValue valueA2 = NONE;
        ContainingPackageLookupValue valueB1 = ContainingPackageLookupValue.withContainingPackage(PackageIdentifier.createInMainRepo("b"), Root.fromPath(rootDirectory));
        ContainingPackageLookupValue valueB2 = ContainingPackageLookupValue.withContainingPackage(PackageIdentifier.createInMainRepo("b"), Root.fromPath(rootDirectory));
        PackageIdentifier cFrag = PackageIdentifier.createInMainRepo("c");
        ContainingPackageLookupValue valueC1 = ContainingPackageLookupValue.withContainingPackage(cFrag, Root.fromPath(rootDirectory));
        ContainingPackageLookupValue valueC2 = ContainingPackageLookupValue.withContainingPackage(cFrag, Root.fromPath(rootDirectory));
        ContainingPackageLookupValue valueCOther = ContainingPackageLookupValue.withContainingPackage(cFrag, Root.fromPath(rootDirectory.getRelative("other_root")));
        new EqualsTester().addEqualityGroup(valueA1, valueA2).addEqualityGroup(valueB1, valueB2).addEqualityGroup(valueC1, valueC2).addEqualityGroup(valueCOther).testEquals();
    }

    @Test
    public void testNonExistentExternalRepositoryErrorReason() throws Exception {
        PackageIdentifier identifier = PackageIdentifier.create("@some_repo", PathFragment.create(":atarget"));
        ContainingPackageLookupValue value = lookupContainingPackage(identifier);
        assertThat(value.hasContainingPackage()).isFalse();
        assertThat(value.getClass()).isEqualTo(NoContainingPackage.class);
        assertThat(value.getReasonForNoContainingPackage()).isEqualTo("The repository '@some_repo' could not be resolved");
    }

    @Test
    public void testInvalidPackageLabelErrorReason() throws Exception {
        ContainingPackageLookupValue value = lookupContainingPackage("invalidpackagename:42/BUILD");
        assertThat(value.hasContainingPackage()).isFalse();
        assertThat(value.getClass()).isEqualTo(NoContainingPackage.class);
        // As for invalid package name we continue to climb up the parent packages,
        // we will find the top-level package with the path "" - empty string.
        assertThat(value.getReasonForNoContainingPackage()).isNull();
    }

    @Test
    public void testDeletedPackageErrorReason() throws Exception {
        PackageIdentifier identifier = PackageIdentifier.createInMainRepo("deletedpackage");
        deletedPackages.set(ImmutableSet.of(identifier));
        scratch.file("BUILD");
        PackageLookupValue packageLookupValue = lookupPackage(identifier);
        assertThat(packageLookupValue.packageExists()).isFalse();
        assertThat(packageLookupValue.getErrorReason()).isEqualTo(DELETED_PACKAGE);
        assertThat(packageLookupValue.getErrorMsg()).isEqualTo("Package is considered deleted due to --deleted_packages");
        ContainingPackageLookupValue value = lookupContainingPackage(identifier);
        assertThat(value.hasContainingPackage()).isTrue();
        assertThat(value.getContainingPackageName().toString()).isEmpty();
        assertThat(value.getClass()).isEqualTo(ContainingPackage.class);
    }

    @Test
    public void testNoBuildFileErrorReason() throws Exception {
        ContainingPackageLookupValue value = lookupContainingPackage("abc");
        assertThat(value.hasContainingPackage()).isFalse();
        assertThat(value.getClass()).isEqualTo(NoContainingPackage.class);
        assertThat(value.getReasonForNoContainingPackage()).isNull();
    }
}

