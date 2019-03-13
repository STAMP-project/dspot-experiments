/**
 * Copyright ? 2010-2017 Nokia
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jsonschema2pojo;


import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JType;
import java.net.URI;
import java.net.URISyntaxException;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class SchemaStoreTest {
    @Test
    public void createWithAbsolutePath() throws URISyntaxException {
        URI schemaUri = getClass().getResource("/schema/address.json").toURI();
        Schema schema = new SchemaStore().create(schemaUri, "#/.");
        Assert.assertThat(schema, is(notNullValue()));
        Assert.assertThat(schema.getId(), is(equalTo(schemaUri)));
        Assert.assertThat(schema.getContent().has("description"), is(true));
        Assert.assertThat(schema.getContent().get("description").asText(), is(equalTo("An Address following the convention of http://microformats.org/wiki/hcard")));
    }

    @Test
    public void createWithRelativePath() throws URISyntaxException {
        URI addressSchemaUri = getClass().getResource("/schema/address.json").toURI();
        SchemaStore schemaStore = new SchemaStore();
        Schema addressSchema = schemaStore.create(addressSchemaUri, "#/.");
        Schema enumSchema = schemaStore.create(addressSchema, "enum.json", "#/.");
        String expectedUri = (StringUtils.removeEnd(addressSchemaUri.toString(), "address.json")) + "enum.json";
        Assert.assertThat(enumSchema, is(notNullValue()));
        Assert.assertThat(enumSchema.getId(), is(equalTo(URI.create(expectedUri))));
        Assert.assertThat(enumSchema.getContent().has("enum"), is(true));
    }

    @Test
    public void createWithSelfRef() throws URISyntaxException {
        URI schemaUri = getClass().getResource("/schema/address.json").toURI();
        SchemaStore schemaStore = new SchemaStore();
        Schema addressSchema = schemaStore.create(schemaUri, "#/.");
        Schema selfRefSchema = schemaStore.create(addressSchema, "#", "#/.");
        Assert.assertThat(addressSchema, is(sameInstance(selfRefSchema)));
    }

    @Test
    public void createWithEmbeddedSelfRef() throws URISyntaxException {
        URI schemaUri = getClass().getResource("/schema/embeddedRef.json").toURI();
        SchemaStore schemaStore = new SchemaStore();
        Schema topSchema = schemaStore.create(schemaUri, "#/.");
        Schema embeddedSchema = schemaStore.create(topSchema, "#/definitions/embedded", "#/.");
        Schema selfRefSchema = schemaStore.create(embeddedSchema, "#", "#/.");
        Assert.assertThat(topSchema, is(sameInstance(selfRefSchema)));
    }

    @Test
    public void createWithFragmentResolution() throws URISyntaxException {
        URI addressSchemaUri = getClass().getResource("/schema/address.json").toURI();
        SchemaStore schemaStore = new SchemaStore();
        Schema addressSchema = schemaStore.create(addressSchemaUri, "#/.");
        Schema innerSchema = schemaStore.create(addressSchema, "#/properties/post-office-box", "#/.");
        String expectedUri = (addressSchemaUri.toString()) + "#/properties/post-office-box";
        Assert.assertThat(innerSchema, is(notNullValue()));
        Assert.assertThat(innerSchema.getId(), is(equalTo(URI.create(expectedUri))));
        Assert.assertThat(innerSchema.getContent().has("type"), is(true));
        Assert.assertThat(innerSchema.getContent().get("type").asText(), is("string"));
    }

    @Test
    public void schemaAlreadyReadIsReused() throws URISyntaxException {
        URI schemaUri = getClass().getResource("/schema/address.json").toURI();
        SchemaStore schemaStore = new SchemaStore();
        Schema schema1 = schemaStore.create(schemaUri, "#/.");
        Schema schema2 = schemaStore.create(schemaUri, "#/.");
        Assert.assertThat(schema1, is(sameInstance(schema2)));
    }

    @Test
    public void setIfEmptyOnlySetsIfEmpty() throws URISyntaxException {
        JType firstClass = Mockito.mock(JDefinedClass.class);
        JType secondClass = Mockito.mock(JDefinedClass.class);
        URI schemaUri = getClass().getResource("/schema/address.json").toURI();
        Schema schema = new SchemaStore().create(schemaUri, "#/.");
        schema.setJavaTypeIfEmpty(firstClass);
        Assert.assertThat(schema.getJavaType(), is(equalTo(firstClass)));
        schema.setJavaTypeIfEmpty(secondClass);
        Assert.assertThat(schema.getJavaType(), is(not(equalTo(secondClass))));
    }
}

