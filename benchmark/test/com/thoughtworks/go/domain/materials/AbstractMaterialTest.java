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
package com.thoughtworks.go.domain.materials;


import com.thoughtworks.go.config.PipelineConfig;
import com.thoughtworks.go.config.materials.AbstractMaterial;
import com.thoughtworks.go.config.materials.SubprocessExecutionContext;
import com.thoughtworks.go.domain.MaterialInstance;
import com.thoughtworks.go.domain.MaterialRevision;
import com.thoughtworks.go.util.command.ConsoleOutputStreamConsumer;
import com.thoughtworks.go.util.command.EnvironmentVariableContext;
import java.io.File;
import java.util.List;
import java.util.Map;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class AbstractMaterialTest {
    public static class TestMaterial extends AbstractMaterial {
        private final String displayName;

        private String bar = "bar";

        private String quux = "quux";

        public static int PIPELINE_UNIQUE_ATTRIBUTE_ADDED = 0;

        public TestMaterial(String displayName) {
            super(displayName);
            this.displayName = displayName;
        }

        protected void appendPipelineUniqueCriteria(Map<String, Object> basicCriteria) {
            basicCriteria.put("pipeline-unique", ("unique-" + ((AbstractMaterialTest.TestMaterial.PIPELINE_UNIQUE_ATTRIBUTE_ADDED)++)));
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

        public void updateTo(ConsoleOutputStreamConsumer outputStreamConsumer, File baseDir, RevisionContext revisionContext, final SubprocessExecutionContext execCtx) {
            throw new UnsupportedOperationException();
        }

        public ValidationBean checkConnection(final SubprocessExecutionContext execCtx) {
            throw new UnsupportedOperationException();
        }

        public List<Modification> latestModification(File baseDir, final SubprocessExecutionContext execCtx) {
            throw new UnsupportedOperationException();
        }

        public void toJson(Map jsonMap, Revision revision) {
            throw new UnsupportedOperationException();
        }

        public boolean matches(String name, String regex) {
            throw new UnsupportedOperationException();
        }

        public void emailContent(StringBuilder content, Modification modification) {
            throw new UnsupportedOperationException();
        }

        public List<Modification> modificationsSince(File baseDir, Revision revision, final SubprocessExecutionContext execCtx) {
            throw new UnsupportedOperationException();
        }

        public MaterialInstance createMaterialInstance() {
            throw new UnsupportedOperationException();
        }

        public String getDescription() {
            throw new UnsupportedOperationException();
        }

        public String getTypeForDisplay() {
            throw new UnsupportedOperationException();
        }

        public void populateEnvironmentContext(EnvironmentVariableContext context, MaterialRevision materialRevision, File workingDir) {
            throw new UnsupportedOperationException();
        }

        public String getDisplayName() {
            return displayName;
        }

        public boolean isAutoUpdate() {
            throw new UnsupportedOperationException();
        }

        public MatchedRevision createMatchedRevision(Modification modifications, String searchString) {
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

        public Revision oldestRevision(Modifications modifications) {
            throw new UnsupportedOperationException("Not yet implemented");
        }

        @Override
        public String getLongDescription() {
            throw new UnsupportedOperationException();
        }
    }

    @Test
    public void shouldCacheCriteriaAndAttributeMap() {
        AbstractMaterialTest.TestMaterial testMaterial = new AbstractMaterialTest.TestMaterial("foo");
        Map<String, Object> sqlCriteria = getSqlCriteria();
        Assert.assertThat(testMaterial.getSqlCriteria(), Matchers.sameInstance(sqlCriteria));
        Assert.assertThat(testMaterial.getSqlCriteria().get("foo"), Matchers.is("bar"));
        Assert.assertThat(testMaterial.getSqlCriteria().getClass().getCanonicalName(), Matchers.is("java.util.Collections.UnmodifiableMap"));
        Map<String, Object> attributesForXml = getAttributesForXml();
        Assert.assertThat(testMaterial.getAttributesForXml(), Matchers.sameInstance(attributesForXml));
        Assert.assertThat(testMaterial.getAttributesForXml().get("baz"), Matchers.is("quux"));
        Assert.assertThat(testMaterial.getAttributesForXml().getClass().getCanonicalName(), Matchers.is("java.util.Collections.UnmodifiableMap"));
    }

    @Test
    public void shouldCachePipelineUniqueFingerprint() {
        AbstractMaterialTest.TestMaterial testMaterial = new AbstractMaterialTest.TestMaterial("foo");
        String pipelineUniqueFingerprint = getPipelineUniqueFingerprint();
        int appendPipelineUniqueAttrsCallCount = AbstractMaterialTest.TestMaterial.PIPELINE_UNIQUE_ATTRIBUTE_ADDED;
        Assert.assertThat(testMaterial.getPipelineUniqueFingerprint(), Matchers.sameInstance(pipelineUniqueFingerprint));
        Assert.assertThat(appendPipelineUniqueAttrsCallCount, Matchers.is(AbstractMaterialTest.TestMaterial.PIPELINE_UNIQUE_ATTRIBUTE_ADDED));
    }

    @Test
    public void shouldReturnFullNameIfTheLengthIsLessThanGivenThreshold() throws Exception {
        AbstractMaterial material = new AbstractMaterialTest.TestMaterial("foo_bar_baz_quuz_ban");
        Assert.assertThat(material.getTruncatedDisplayName(), Matchers.is("foo_bar_baz_quuz_ban"));
    }

    @Test
    public void shouldReturnTruncatedNameIfTheLengthIsGreaterThanGivenThreshold() throws Exception {
        AbstractMaterial material = new AbstractMaterialTest.TestMaterial("foo_bar_baz_quuz_ban_pavan");
        Assert.assertThat(material.getTruncatedDisplayName(), Matchers.is("foo_bar_ba..._ban_pavan"));
    }
}

