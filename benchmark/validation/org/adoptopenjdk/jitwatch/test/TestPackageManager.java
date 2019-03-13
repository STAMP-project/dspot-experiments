/**
 * Copyright (c) 2013-2015 Chris Newland.
 * Licensed under https://github.com/AdoptOpenJDK/jitwatch/blob/master/LICENSE-BSD
 * Instructions: https://github.com/AdoptOpenJDK/jitwatch/wiki
 */
package org.adoptopenjdk.jitwatch.test;


import java.util.Arrays;
import java.util.List;
import org.adoptopenjdk.jitwatch.model.MetaPackage;
import org.adoptopenjdk.jitwatch.model.PackageManager;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestPackageManager {
    /* Scenario: No valid package name is passed
    Given an unknown or empty package name is passed to the PackageManager
    When the package is built
    Then an empty meta package is returned
     */
    /* Deadcode detected via this test
    {
    if (mp == null)
    {
    // default package ""
    mp = new MetaPackage(S_EMPTY);
    metaPackages.put(S_EMPTY, mp);
    }
    }
     */
    @Test
    public void givenUnknownPackageNameIsUsedWithPackageManager_WhenPackageIsBuilt_ThenEmptyMetaPackage() {
        // Given
        MetaPackage expectedPackage = new MetaPackage(S_EMPTY);
        // When
        PackageManager packageManager = new PackageManager();
        MetaPackage actualPackage = packageManager.buildPackage(S_EMPTY);
        // Then
        Assert.assertThat(actualPackage, CoreMatchers.is(CoreMatchers.equalTo(expectedPackage)));
    }

    /* Scenario: Valid package name is passed
    Given an valid package name is passed to the PackageManager
    When the package is built
    And the root packages are requested
    Then the appropriate root packages are returned
     */
    @Test
    public void givenKnownPackageNameIsUsedWithPackageManager_WhenPackageIsBuiltAndRootPackagesAreRequested_ThenRootPackagesAreReturned() {
        // Given
        List<MetaPackage> expectedRootPackages = Arrays.asList(new MetaPackage("com"));
        // When
        PackageManager packageManager = new PackageManager();
        packageManager.buildPackage("com.sun.java");
        List<MetaPackage> actualRootPackages = packageManager.getRootPackages();
        // Then
        Assert.assertThat(actualRootPackages, CoreMatchers.is(CoreMatchers.equalTo(expectedRootPackages)));
    }
}

