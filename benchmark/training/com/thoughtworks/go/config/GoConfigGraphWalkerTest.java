/**
 * ***********************GO-LICENSE-START*********************************
 * Copyright 2014 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ************************GO-LICENSE-END**********************************
 */
package com.thoughtworks.go.config;


import com.thoughtworks.go.config.materials.PackageMaterialConfig;
import com.thoughtworks.go.config.materials.PluggableSCMMaterialConfig;
import com.thoughtworks.go.config.merge.MergePipelineConfigs;
import com.thoughtworks.go.domain.packagerepository.PackageDefinition;
import com.thoughtworks.go.domain.packagerepository.PackageRepository;
import com.thoughtworks.go.domain.scm.SCM;
import com.thoughtworks.go.util.ReflectionUtil;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class GoConfigGraphWalkerTest {
    @Test
    public void walkedObject_shouldOnlyAcceptObjectsInThoughtworksPackage() {
        Assert.assertThat(new GoConfigGraphWalker.WalkedObject("non-tw object").shouldWalk(), Matchers.is(false));
        Assert.assertThat(shouldWalk(), Matchers.is(true));
    }

    @Test
    public void walkedObject_shouldWalkMergePipelineConfigs() {
        Assert.assertThat(new GoConfigGraphWalker.WalkedObject(new MergePipelineConfigs(new BasicPipelineConfigs())).shouldWalk(), Matchers.is(true));
    }

    @Test
    public void walkedObject_shouldNotWalkNull() {
        Assert.assertThat(new GoConfigGraphWalker.WalkedObject(null).shouldWalk(), Matchers.is(false));
    }

    @Test
    public void shouldWalkPipelineConfigsInBasicPipelineConfigs() {
        PipelineConfig pipe = mockPipelineConfig();
        BasicPipelineConfigs basicPipelines = new BasicPipelineConfigs(pipe);
        new GoConfigGraphWalker(basicPipelines).walk(new GoConfigGraphWalker.Handler() {
            @Override
            public void handle(Validatable validatable, ValidationContext ctx) {
                validatable.validate(ctx);
            }
        });
        Mockito.verify(pipe, Mockito.atLeastOnce()).validate(ArgumentMatchers.any(ValidationContext.class));
    }

    @Test
    public void shouldWalkPipelineConfigsInMergePipelineConfigs() {
        PipelineConfig pipe = mockPipelineConfig();
        MergePipelineConfigs mergePipelines = new MergePipelineConfigs(new BasicPipelineConfigs(pipe));
        new GoConfigGraphWalker(mergePipelines).walk(new GoConfigGraphWalker.Handler() {
            @Override
            public void handle(Validatable validatable, ValidationContext ctx) {
                validatable.validate(ctx);
            }
        });
        Mockito.verify(pipe, Mockito.atLeastOnce()).validate(ArgumentMatchers.any(ValidationContext.class));
    }

    @Test
    public void shouldNotWalkFieldsWhichAreTaggedWithIgnoreTraversal() {
        PackageRepository repository = Mockito.mock(PackageRepository.class);
        PackageDefinition packageDefinition = new PackageDefinition();
        packageDefinition.setRepository(repository);
        new GoConfigGraphWalker(packageDefinition).walk(new GoConfigGraphWalker.Handler() {
            @Override
            public void handle(Validatable validatable, ValidationContext ctx) {
                validatable.validate(ctx);
            }
        });
        Mockito.verify(repository, Mockito.never()).validate(ArgumentMatchers.any(ValidationContext.class));
    }

    @Test
    public void shouldNotWalkPackageDefinitionWhileTraversingPackageMaterial() {
        PackageDefinition packageDefinition = Mockito.mock(PackageDefinition.class);
        PackageMaterialConfig packageMaterialConfig = new PackageMaterialConfig("package-id");
        ReflectionUtil.setField(packageMaterialConfig, "packageDefinition", packageDefinition);
        BasicCruiseConfig config = new BasicCruiseConfig();
        PackageRepository packageRepository = Mockito.mock(PackageRepository.class);
        Mockito.when(packageRepository.getPackages()).thenReturn(new com.thoughtworks.go.domain.packagerepository.Packages(packageDefinition));
        Mockito.when(packageDefinition.getRepository()).thenReturn(packageRepository);
        Mockito.when(packageRepository.doesPluginExist()).thenReturn(true);
        Mockito.when(packageDefinition.getId()).thenReturn("package-id");
        config.getPackageRepositories().add(packageRepository);
        final ConfigSaveValidationContext context = new ConfigSaveValidationContext(config);
        new GoConfigGraphWalker(packageMaterialConfig).walk(new GoConfigGraphWalker.Handler() {
            @Override
            public void handle(Validatable validatable, ValidationContext ctx) {
                validatable.validate(context);
            }
        });
        Mockito.verify(packageDefinition, Mockito.never()).validate(ArgumentMatchers.any(ValidationContext.class));
    }

    @Test
    public void shouldNotWalkSCMMaterialWhileTraversingPluggableSCMMaterial() {
        SCM scmConfig = Mockito.mock(SCM.class);
        Mockito.when(scmConfig.getName()).thenReturn("scm");
        Mockito.when(scmConfig.getId()).thenReturn("scm-id");
        PluggableSCMMaterialConfig pluggableSCMMaterialConfig = new PluggableSCMMaterialConfig("scm-id");
        ReflectionUtil.setField(pluggableSCMMaterialConfig, "scmConfig", scmConfig);
        BasicCruiseConfig config = new BasicCruiseConfig();
        config.getSCMs().add(scmConfig);
        final ConfigSaveValidationContext context = new ConfigSaveValidationContext(config);
        walk(new GoConfigGraphWalker.Handler() {
            @Override
            public void handle(Validatable validatable, ValidationContext ctx) {
                validatable.validate(context);
            }
        });
        Mockito.verify(scmConfig, Mockito.never()).validate(ArgumentMatchers.any(ValidationContext.class));
    }
}

