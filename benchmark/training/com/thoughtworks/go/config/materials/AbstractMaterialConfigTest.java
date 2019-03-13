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
package com.thoughtworks.go.config.materials;


import AbstractMaterialConfig.MATERIAL_NAME;
import com.thoughtworks.go.config.materials.dependency.DependencyMaterialConfig;
import com.thoughtworks.go.config.materials.mercurial.HgMaterialConfig;
import com.thoughtworks.go.domain.materials.MaterialConfig;
import java.util.HashMap;
import java.util.Map;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class AbstractMaterialConfigTest {
    @Test
    public void shouldRecomputePipelineUniqueFingerprint_whenAttributesChanged() {
        AbstractMaterialConfigTest.TestMaterialConfig testMaterialConfig = new AbstractMaterialConfigTest.TestMaterialConfig("foo");
        String pipelineUniqueFingerprint = getPipelineUniqueFingerprint();
        testMaterialConfig.setConfigAttributes(m("bar", "baz"));
        Assert.assertThat(testMaterialConfig.getPipelineUniqueFingerprint(), Matchers.not(pipelineUniqueFingerprint));
    }

    @Test
    public void shouldNotSetMaterialNameIfItIsSetToEmptyAsItsAnOptionalField() {
        AbstractMaterialConfig materialConfig = new AbstractMaterialConfigTest.TestMaterialConfig("");
        Map<String, String> map = new HashMap<>();
        map.put(MATERIAL_NAME, "");
        materialConfig.setConfigAttributes(map);
        Assert.assertThat(materialConfig.getName(), Matchers.is(Matchers.nullValue()));
    }

    @Test
    public void shouldRecomputeSqlCriteriaAndXmlAttributeMap_whenAttributesChanged() {
        AbstractMaterialConfig testMaterialConfig = new AbstractMaterialConfigTest.TestMaterialConfig("foo");
        Map<String, Object> sqlCriteria = testMaterialConfig.getSqlCriteria();
        testMaterialConfig.setConfigAttributes(m("bar", "baz"));
        Assert.assertThat(testMaterialConfig.getSqlCriteria(), Matchers.not(Matchers.sameInstance(sqlCriteria)));
        Assert.assertThat(testMaterialConfig.getSqlCriteria().get("foo"), Matchers.is("baz"));
    }

    @Test
    public void shouldReturnTrueIfMaterialNameIsUsedInPipelineTemplate() {
        AbstractMaterialConfig material = new AbstractMaterialConfigTest.TestMaterialConfig("");
        material.setName(new CaseInsensitiveString("funky_name"));
        PipelineConfig pipelineConfig = new PipelineConfig(new CaseInsensitiveString("blah"), "${COUNT}-${funky_name}", "", false, null, new com.thoughtworks.go.domain.BaseCollection());
        Assert.assertThat(material.isUsedInLabelTemplate(pipelineConfig), Matchers.is(true));
    }

    @Test
    public void shouldReturnTrueIfMaterialNameIsUsedInPipelineTemplate_caseInsensitive() {
        AbstractMaterialConfig material = new AbstractMaterialConfigTest.TestMaterialConfig("");
        material.setName(new CaseInsensitiveString("funky_name"));
        PipelineConfig pipelineConfig = new PipelineConfig(new CaseInsensitiveString("blah"), "${COUNT}-${funky_Name}", "", false, null, new com.thoughtworks.go.domain.BaseCollection());
        Assert.assertThat(material.isUsedInLabelTemplate(pipelineConfig), Matchers.is(true));
    }

    @Test
    public void shouldReturnFalseIfMaterialNameIsNotUsedInPipelineTemplate() {
        AbstractMaterialConfig material = new AbstractMaterialConfigTest.TestMaterialConfig("");
        material.setName(new CaseInsensitiveString("funky_name"));
        Assert.assertThat(material.isUsedInLabelTemplate(new PipelineConfig(new CaseInsensitiveString("blah"), "${COUNT}-${test1}-test", "", false, null, new com.thoughtworks.go.domain.BaseCollection())), Matchers.is(false));
    }

    @Test
    public void shouldReturnFalseIfMaterialNameIsNotDefined() {
        AbstractMaterialConfig material = new AbstractMaterialConfigTest.TestMaterialConfig("test");
        PipelineConfig pipelineConfig = new PipelineConfig(new CaseInsensitiveString("blah"), "${COUNT}-${test}-test", "", false, null, new com.thoughtworks.go.domain.BaseCollection());
        Assert.assertThat(material.isUsedInLabelTemplate(pipelineConfig), Matchers.is(false));
    }

    @Test
    public void shouldNotUseNameFieldButInsteadUseTheNameMethodToCheckIfTheMaterialNameIsUsedInThePipelineLabel() throws Exception {
        PipelineConfig pipelineConfig = Mockito.mock(PipelineConfig.class);
        Mockito.when(pipelineConfig.getLabelTemplate()).thenReturn("${COUNT}-${hg}-${dep}-${pkg}-${scm}");
        MaterialConfig hg = Mockito.mock(HgMaterialConfig.class);
        Mockito.when(hg.getName()).thenReturn(new CaseInsensitiveString("hg"));
        Mockito.when(hg.isUsedInLabelTemplate(pipelineConfig)).thenCallRealMethod();
        MaterialConfig dependency = Mockito.mock(DependencyMaterialConfig.class);
        Mockito.when(dependency.getName()).thenReturn(new CaseInsensitiveString("dep"));
        Mockito.when(dependency.isUsedInLabelTemplate(pipelineConfig)).thenCallRealMethod();
        MaterialConfig aPackage = Mockito.mock(PackageMaterialConfig.class);
        Mockito.when(aPackage.getName()).thenReturn(new CaseInsensitiveString("pkg"));
        Mockito.when(aPackage.isUsedInLabelTemplate(pipelineConfig)).thenCallRealMethod();
        MaterialConfig aPluggableSCM = Mockito.mock(PluggableSCMMaterialConfig.class);
        Mockito.when(aPluggableSCM.getName()).thenReturn(new CaseInsensitiveString("scm"));
        Mockito.when(aPluggableSCM.isUsedInLabelTemplate(pipelineConfig)).thenCallRealMethod();
        Assert.assertThat(hg.isUsedInLabelTemplate(pipelineConfig), Matchers.is(true));
        Assert.assertThat(dependency.isUsedInLabelTemplate(pipelineConfig), Matchers.is(true));
        Assert.assertThat(aPackage.isUsedInLabelTemplate(pipelineConfig), Matchers.is(true));
        Assert.assertThat(aPluggableSCM.isUsedInLabelTemplate(pipelineConfig), Matchers.is(true));
        Mockito.verify(hg).getName();
        Mockito.verify(dependency).getName();
        Mockito.verify(aPackage).getName();
        Mockito.verify(aPluggableSCM).getName();
    }

    @Test
    public void shouldHandleBlankMaterialName() {
        AbstractMaterialConfigTest.TestMaterialConfig materialConfig = new AbstractMaterialConfigTest.TestMaterialConfig("");
        setName(((CaseInsensitiveString) (null)));
        materialConfig.validate(PipelineConfigSaveValidationContext.forChain(true, "group", new PipelineConfig()));
        Assert.assertThat(errors().getAllOn(MATERIAL_NAME), Matchers.is(Matchers.nullValue()));
        materialConfig.setName(new CaseInsensitiveString(null));
        materialConfig.validate(PipelineConfigSaveValidationContext.forChain(true, "group", new PipelineConfig()));
        Assert.assertThat(errors().getAllOn(MATERIAL_NAME), Matchers.is(Matchers.nullValue()));
        materialConfig.setName(new CaseInsensitiveString(""));
        materialConfig.validate(PipelineConfigSaveValidationContext.forChain(true, "group", new PipelineConfig()));
        Assert.assertThat(errors().getAllOn(MATERIAL_NAME), Matchers.is(Matchers.nullValue()));
    }

    public static class TestMaterialConfig extends AbstractMaterialConfig {
        private final String displayName;

        private String bar = "bar";

        private String quux = "quux";

        public static int PIPELINE_UNIQUE_ATTRIBUTE_ADDED = 0;

        public TestMaterialConfig(String displayName) {
            super(displayName);
            this.displayName = displayName;
        }

        protected void appendPipelineUniqueCriteria(Map<String, Object> basicCriteria) {
            basicCriteria.put("pipeline-unique", ("unique-" + ((AbstractMaterialConfigTest.TestMaterialConfig.PIPELINE_UNIQUE_ATTRIBUTE_ADDED)++)));
        }

        @Override
        protected void validateConcreteMaterial(ValidationContext validationContext) {
        }

        protected void appendCriteria(Map<String, Object> parameters) {
            parameters.put("foo", bar);
        }

        protected void appendAttributes(Map<String, Object> parameters) {
            parameters.put("baz", quux);
        }

        public String getFolder() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Filter filter() {
            return null;
        }

        @Override
        public boolean isInvertFilter() {
            return false;
        }

        @Override
        public void setConfigAttributes(Object attributes) {
            super.setConfigAttributes(attributes);
            Map map = ((Map) (attributes));
            if (map.containsKey("bar")) {
                bar = ((String) (map.get("bar")));
            }
            if (map.containsKey("quux")) {
                quux = ((String) (map.get("quux")));
            }
        }

        public boolean matches(String name, String regex) {
            throw new UnsupportedOperationException();
        }

        public String getDescription() {
            throw new UnsupportedOperationException();
        }

        public String getTypeForDisplay() {
            throw new UnsupportedOperationException();
        }

        public String getDisplayName() {
            return displayName;
        }

        public boolean isAutoUpdate() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setAutoUpdate(boolean autoUpdate) {
            throw new UnsupportedOperationException();
        }

        public String getUriForDisplay() {
            throw new UnsupportedOperationException();
        }

        public Boolean isUsedInFetchArtifact(PipelineConfig pipelineConfig) {
            return false;
        }

        public Class getInstanceType() {
            throw new UnsupportedOperationException("instance not available for test material");
        }

        @Override
        public String getLongDescription() {
            throw new UnsupportedOperationException();
        }
    }
}

