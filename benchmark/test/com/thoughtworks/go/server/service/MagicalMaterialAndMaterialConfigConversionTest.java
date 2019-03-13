/**
 * Copyright 2017 ThoughtWorks, Inc.
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
package com.thoughtworks.go.server.service;


import com.thoughtworks.go.config.materials.PackageMaterialConfig;
import com.thoughtworks.go.config.materials.PluggableSCMMaterialConfig;
import com.thoughtworks.go.config.materials.dependency.DependencyMaterialConfig;
import com.thoughtworks.go.config.materials.git.GitMaterialConfig;
import com.thoughtworks.go.config.materials.mercurial.HgMaterialConfig;
import com.thoughtworks.go.config.materials.perforce.P4MaterialConfig;
import com.thoughtworks.go.config.materials.svn.SvnMaterialConfig;
import com.thoughtworks.go.config.materials.tfs.TfsMaterialConfig;
import com.thoughtworks.go.domain.materials.MaterialConfig;
import com.thoughtworks.go.domain.packagerepository.ConfigurationPropertyMother;
import com.thoughtworks.go.domain.packagerepository.PackageDefinition;
import com.thoughtworks.go.domain.packagerepository.PackageDefinitionMother;
import com.thoughtworks.go.domain.packagerepository.PackageRepository;
import com.thoughtworks.go.domain.packagerepository.PackageRepositoryMother;
import com.thoughtworks.go.domain.scm.SCM;
import com.thoughtworks.go.domain.scm.SCMMother;
import com.thoughtworks.go.helper.FilterMother;
import com.thoughtworks.go.security.GoCipher;
import com.thoughtworks.go.util.command.HgUrlArgument;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.junit.Test;
import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.Theories;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;


@RunWith(Theories.class)
public class MagicalMaterialAndMaterialConfigConversionTest {
    private static PackageRepository packageRepo = PackageRepositoryMother.create("repo-id", "repo-name", "pluginid", "version", new com.thoughtworks.go.domain.config.Configuration(ConfigurationPropertyMother.create("k1", false, "v1")));

    private static PackageDefinition packageDefinition = PackageDefinitionMother.create("id", "name1", new com.thoughtworks.go.domain.config.Configuration(ConfigurationPropertyMother.create("k2", false, "v2")), MagicalMaterialAndMaterialConfigConversionTest.packageRepo);

    public static SCM scmConfig = SCMMother.create("scm-id", "scm-name", "plugin-id", "1.0", new com.thoughtworks.go.domain.config.Configuration(ConfigurationPropertyMother.create("k1", false, "v1")));

    private static Map<Class, String[]> fieldsWhichShouldBeIgnoredWhenSavedInDbAndGotBack = new HashMap<>();

    private MaterialConfigConverter materialConfigConverter = new MaterialConfigConverter();

    @DataPoint
    public static MaterialConfig gitMaterialConfig = new GitMaterialConfig(MagicalMaterialAndMaterialConfigConversionTest.url("git-url"), "branch", "submodule", true, FilterMother.filterFor("*.doc"), false, "folder", MagicalMaterialAndMaterialConfigConversionTest.cis("gitMaterial"), false);

    @DataPoint
    public static MaterialConfig hgMaterialConfig = new HgMaterialConfig(new HgUrlArgument("hg-url"), true, FilterMother.filterFor("*.png"), false, "folder", MagicalMaterialAndMaterialConfigConversionTest.cis("hgMaterial"));

    @DataPoint
    public static MaterialConfig svnMaterialConfig = new SvnMaterialConfig(MagicalMaterialAndMaterialConfigConversionTest.url("svn-url"), "user", "pass", true, new GoCipher(), true, FilterMother.filterFor("*.txt"), false, "folder", MagicalMaterialAndMaterialConfigConversionTest.cis("name1"));

    @DataPoint
    public static MaterialConfig p4MaterialConfig = new P4MaterialConfig("localhost:9090", "user", "pass", true, "view", new GoCipher(), MagicalMaterialAndMaterialConfigConversionTest.cis("p4Material"), true, FilterMother.filterFor("*.jpg"), false, "folder");

    @DataPoint
    public static MaterialConfig tfsMaterialConfig = new TfsMaterialConfig(MagicalMaterialAndMaterialConfigConversionTest.url("tfs-url"), "user", "domain", "pass", "prj-path", new GoCipher(), true, FilterMother.filterFor("*.txt"), false, "folder", MagicalMaterialAndMaterialConfigConversionTest.cis("tfsMaterial"));

    @DataPoint
    public static MaterialConfig pkgMaterialConfig = new PackageMaterialConfig(MagicalMaterialAndMaterialConfigConversionTest.cis("name"), "pkg-id", MagicalMaterialAndMaterialConfigConversionTest.packageDefinition);

    @DataPoint
    public static MaterialConfig pluggableSCMMaterialConfig = new PluggableSCMMaterialConfig(MagicalMaterialAndMaterialConfigConversionTest.cis("name"), MagicalMaterialAndMaterialConfigConversionTest.scmConfig, "folder", FilterMother.filterFor("*.txt"));

    @DataPoint
    public static MaterialConfig dependencyMaterialConfig = new DependencyMaterialConfig(MagicalMaterialAndMaterialConfigConversionTest.cis("name1"), MagicalMaterialAndMaterialConfigConversionTest.cis("pipeline1"), MagicalMaterialAndMaterialConfigConversionTest.cis("stage1"));

    static {
        MagicalMaterialAndMaterialConfigConversionTest.fieldsWhichShouldBeIgnoredWhenSavedInDbAndGotBack.put(GitMaterialConfig.class, new String[]{ "filter" });
        MagicalMaterialAndMaterialConfigConversionTest.fieldsWhichShouldBeIgnoredWhenSavedInDbAndGotBack.put(HgMaterialConfig.class, new String[]{ "filter" });
        MagicalMaterialAndMaterialConfigConversionTest.fieldsWhichShouldBeIgnoredWhenSavedInDbAndGotBack.put(SvnMaterialConfig.class, new String[]{ "filter", "encryptedPassword", "goCipher" });
        MagicalMaterialAndMaterialConfigConversionTest.fieldsWhichShouldBeIgnoredWhenSavedInDbAndGotBack.put(P4MaterialConfig.class, new String[]{ "filter", "encryptedPassword", "goCipher" });
        MagicalMaterialAndMaterialConfigConversionTest.fieldsWhichShouldBeIgnoredWhenSavedInDbAndGotBack.put(TfsMaterialConfig.class, new String[]{ "filter", "encryptedPassword", "goCipher" });
        MagicalMaterialAndMaterialConfigConversionTest.fieldsWhichShouldBeIgnoredWhenSavedInDbAndGotBack.put(PackageMaterialConfig.class, new String[]{ "filter", "packageId", "packageDefinition", "fingerprint" });
        MagicalMaterialAndMaterialConfigConversionTest.fieldsWhichShouldBeIgnoredWhenSavedInDbAndGotBack.put(PluggableSCMMaterialConfig.class, new String[]{ "filter", "scmId", "scmConfig", "fingerprint" });
        MagicalMaterialAndMaterialConfigConversionTest.fieldsWhichShouldBeIgnoredWhenSavedInDbAndGotBack.put(DependencyMaterialConfig.class, new String[]{ "filter", "encryptedPassword", "goCipher" });
    }

    @Test
    public void failIfNewTypeOfMaterialIsNotAddedInTheAboveTest() throws Exception {
        ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
        provider.addIncludeFilter(new AssignableTypeFilter(MaterialConfig.class));
        Set<BeanDefinition> candidateComponents = provider.findCandidateComponents("com/thoughtworks");
        List<Class> reflectionsSubTypesOf = candidateComponents.stream().map(( beanDefinition) -> beanDefinition.getBeanClassName()).map(( s) -> {
            try {
                return Class.forName(s);
            } catch ( e) {
                throw new <e>RuntimeException();
            }
        }).collect(Collectors.toList());
        reflectionsSubTypesOf.removeIf(this::isNotAConcrete_NonTest_MaterialConfigImplementation);
        List<Class> allExpectedMaterialConfigImplementations = allMaterialConfigsWhichAreDataPointsInThisTest();
        assertThatAllMaterialConfigsInCodeAreTestedHere(reflectionsSubTypesOf, allExpectedMaterialConfigImplementations);
    }
}

