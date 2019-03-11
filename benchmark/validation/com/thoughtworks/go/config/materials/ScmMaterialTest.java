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


import ScmMaterial.GO_FROM_REVISION;
import ScmMaterial.GO_REVISION;
import ScmMaterial.GO_TO_REVISION;
import com.thoughtworks.go.config.CaseInsensitiveString;
import com.thoughtworks.go.config.PipelineConfig;
import com.thoughtworks.go.domain.MaterialRevision;
import com.thoughtworks.go.domain.materials.DummyMaterial;
import com.thoughtworks.go.domain.materials.Modification;
import com.thoughtworks.go.util.command.EnvironmentVariableContext;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class ScmMaterialTest {
    private DummyMaterial material;

    @Test
    public void shouldSmudgePasswordForDescription() throws Exception {
        material.setUrl("http://user:password@localhost:8000/foo");
        Assert.assertThat(getDescription(), Matchers.is("http://user:******@localhost:8000/foo"));
    }

    @Test
    public void displayNameShouldReturnUrlWhenNameNotSet() throws Exception {
        material.setUrl("http://user:password@localhost:8000/foo");
        Assert.assertThat(getDisplayName(), Matchers.is("http://user:******@localhost:8000/foo"));
    }

    @Test
    public void displayNameShouldReturnNameWhenSet() throws Exception {
        material.setName(new CaseInsensitiveString("blah-name"));
        Assert.assertThat(getDisplayName(), Matchers.is("blah-name"));
    }

    @Test
    public void willNeverBeUsedInAFetchArtifact() {
        Assert.assertThat(material.isUsedInFetchArtifact(new PipelineConfig()), Matchers.is(false));
    }

    @Test
    public void populateEnvironmentContextShouldSetFromAndToRevisionEnvironmentVariables() {
        EnvironmentVariableContext ctx = new EnvironmentVariableContext();
        final ArrayList<Modification> modifications = new ArrayList<>();
        modifications.add(new Modification("user2", "comment2", "email2", new Date(), "24"));
        modifications.add(new Modification("user1", "comment1", "email1", new Date(), "23"));
        MaterialRevision materialRevision = new MaterialRevision(material, modifications);
        Assert.assertThat(ctx.getProperty(GO_FROM_REVISION), Matchers.is(Matchers.nullValue()));
        Assert.assertThat(ctx.getProperty(GO_TO_REVISION), Matchers.is(Matchers.nullValue()));
        Assert.assertThat(ctx.getProperty(GO_REVISION), Matchers.is(Matchers.nullValue()));
        material.populateEnvironmentContext(ctx, materialRevision, new File("."));
        Assert.assertThat(ctx.getProperty(GO_FROM_REVISION), Matchers.is("23"));
        Assert.assertThat(ctx.getProperty(GO_TO_REVISION), Matchers.is("24"));
        Assert.assertThat(ctx.getProperty(GO_REVISION), Matchers.is("24"));
    }

    @Test
    public void shouldIncludeMaterialNameInEnvVariableNameIfAvailable() {
        EnvironmentVariableContext context = new EnvironmentVariableContext();
        material.setVariableWithName(context, "value", "GO_PROPERTY");
        Assert.assertThat(context.getProperty("GO_PROPERTY"), Matchers.is("value"));
        context = new EnvironmentVariableContext();
        material.setName(new CaseInsensitiveString("dummy"));
        material.setVariableWithName(context, "value", "GO_PROPERTY");
        Assert.assertThat(context.getProperty("GO_PROPERTY_DUMMY"), Matchers.is("value"));
        Assert.assertThat(context.getProperty("GO_PROPERTY"), Matchers.is(Matchers.nullValue()));
    }

    @Test
    public void shouldIncludeDestFolderInEnvVariableNameIfMaterialNameNotAvailable() {
        EnvironmentVariableContext context = new EnvironmentVariableContext();
        material.setVariableWithName(context, "value", "GO_PROPERTY");
        Assert.assertThat(context.getProperty("GO_PROPERTY"), Matchers.is("value"));
        context = new EnvironmentVariableContext();
        setFolder("foo_dir");
        material.setVariableWithName(context, "value", "GO_PROPERTY");
        Assert.assertThat(context.getProperty("GO_PROPERTY_FOO_DIR"), Matchers.is("value"));
        Assert.assertThat(context.getProperty("GO_PROPERTY"), Matchers.is(Matchers.nullValue()));
    }

    @Test
    public void shouldEscapeHyphenFromMaterialNameWhenUsedInEnvVariable() {
        EnvironmentVariableContext context = new EnvironmentVariableContext();
        material.setName(new CaseInsensitiveString("material-name"));
        material.setVariableWithName(context, "value", "GO_PROPERTY");
        Assert.assertThat(context.getProperty("GO_PROPERTY_MATERIAL_NAME"), Matchers.is("value"));
        Assert.assertThat(context.getProperty("GO_PROPERTY"), Matchers.is(Matchers.nullValue()));
    }

    @Test
    public void shouldEscapeHyphenFromFolderNameWhenUsedInEnvVariable() {
        EnvironmentVariableContext context = new EnvironmentVariableContext();
        setFolder("folder-name");
        material.setVariableWithName(context, "value", "GO_PROPERTY");
        Assert.assertThat(context.getProperty("GO_PROPERTY_FOLDER_NAME"), Matchers.is("value"));
        Assert.assertThat(context.getProperty("GO_PROPERTY"), Matchers.is(Matchers.nullValue()));
    }

    @Test
    public void shouldReturnTrueForAnScmMaterial_supportsDestinationFolder() throws Exception {
        Assert.assertThat(supportsDestinationFolder(), Matchers.is(true));
    }

    @Test
    public void shouldGetMaterialNameForEnvironmentMaterial() {
        Assert.assertThat(getMaterialNameForEnvironmentVariable(), Matchers.is(""));
        setFolder("dest-folder");
        Assert.assertThat(getMaterialNameForEnvironmentVariable(), Matchers.is("DEST_FOLDER"));
        material.setName(new CaseInsensitiveString("some-material"));
        Assert.assertThat(getMaterialNameForEnvironmentVariable(), Matchers.is("SOME_MATERIAL"));
    }
}

