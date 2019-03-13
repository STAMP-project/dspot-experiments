/**
 * Copyright 2018 ThoughtWorks, Inc.
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
package com.thoughtworks.go.plugin.access.configrepo.v1;


import JsonMessageHandler1_0.CURRENT_CONTRACT_VERSION;
import com.thoughtworks.go.plugin.access.configrepo.ConfigRepoMigrator;
import com.thoughtworks.go.plugin.configrepo.codec.GsonCodec;
import com.thoughtworks.go.plugin.configrepo.contract.CRParseResult;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import static JsonMessageHandler1_0.CURRENT_CONTRACT_VERSION;


public class JsonMessageHandler1_0Test {
    private final JsonMessageHandler1_0 handler;

    private final ConfigRepoMigrator configRepoMigrator;

    public JsonMessageHandler1_0Test() {
        configRepoMigrator = Mockito.mock(ConfigRepoMigrator.class);
        handler = new JsonMessageHandler1_0(new GsonCodec(), configRepoMigrator);
    }

    @Test
    public void shouldErrorWhenMissingTargetVersionInResponse() {
        String json = "{\n" + ((("  \"environments\" : [],\n" + "  \"pipelines\" : [],\n") + "  \"errors\" : []\n") + "}");
        CRParseResult result = handler.responseMessageForParseDirectory(json);
        Assert.assertThat(result.getErrors().getErrorsAsText(), ArgumentMatchers.contains("missing 'target_version' field"));
    }

    @Test
    public void shouldNotErrorWhenTargetVersionInResponse() {
        String json = "{\n" + ((("  \"target_version\" : 1,\n" + "  \"pipelines\" : [],\n") + "  \"errors\" : []\n") + "}");
        makeMigratorReturnSameJSON();
        CRParseResult result = handler.responseMessageForParseDirectory(json);
        TestCase.assertFalse(result.hasErrors());
    }

    @Test
    public void shouldAppendPluginErrorsToAllErrors() {
        String json = "{\n" + ((("  \"target_version\" : 1,\n" + "  \"pipelines\" : [],\n") + "  \"errors\" : [{\"location\" : \"somewhere\", \"message\" : \"failed to parse pipeline.json\"}]\n") + "}");
        CRParseResult result = handler.responseMessageForParseDirectory(json);
        TestCase.assertTrue(result.hasErrors());
    }

    @Test
    public void shouldCallMigratorForEveryVersionFromTheProvidedOneToTheLatest() throws Exception {
        handler.responseMessageForParseDirectory("{ \"target_version\": \"0\", \"something\": \"value\" }");
        Mockito.verify(configRepoMigrator).migrate(ArgumentMatchers.anyString(), ArgumentMatchers.eq(1));
        Mockito.verify(configRepoMigrator).migrate(ArgumentMatchers.nullable(String.class), ArgumentMatchers.eq(2));
        Mockito.verify(configRepoMigrator, Mockito.times(CURRENT_CONTRACT_VERSION)).migrate(ArgumentMatchers.nullable(String.class), ArgumentMatchers.anyInt());
    }

    @Test
    public void shouldErrorWhenTargetVersionOfPluginIsHigher() {
        int targetVersion = (CURRENT_CONTRACT_VERSION) + 1;
        String json = ((((("{\n" + "  \"target_version\" : ") + targetVersion) + ",\n") + "  \"pipelines\" : [],\n") + "  \"errors\" : []\n") + "}";
        CRParseResult result = handler.responseMessageForParseDirectory(json);
        String errorMessage = String.format("'target_version' is %s but the GoCD Server supports %s", targetVersion, CURRENT_CONTRACT_VERSION);
        Assert.assertThat(result.getErrors().getErrorsAsText(), ArgumentMatchers.contains(errorMessage));
    }
}

