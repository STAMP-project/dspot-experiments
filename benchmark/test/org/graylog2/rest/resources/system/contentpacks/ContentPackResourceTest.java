/**
 * This file is part of Graylog.
 *
 * Graylog is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Graylog is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Graylog.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.graylog2.rest.resources.system.contentpacks;


import Response.Status.CREATED;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import org.graylog2.contentpacks.ContentPackInstallationPersistenceService;
import org.graylog2.contentpacks.ContentPackPersistenceService;
import org.graylog2.contentpacks.ContentPackService;
import org.graylog2.contentpacks.model.ContentPack;
import org.graylog2.contentpacks.model.ContentPackInstallation;
import org.graylog2.contentpacks.model.ModelId;
import org.graylog2.contentpacks.model.constraints.ConstraintCheckResult;
import org.graylog2.rest.models.system.contenpacks.responses.ContentPackList;
import org.graylog2.rest.models.system.contenpacks.responses.ContentPackMetadata;
import org.graylog2.rest.models.system.contenpacks.responses.ContentPackResponse;
import org.graylog2.rest.models.system.contenpacks.responses.ContentPackRevisions;
import org.graylog2.shared.bindings.GuiceInjectorHolder;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;


public class ContentPackResourceTest {
    private static final String CONTENT_PACK = "" + ((((((((((("{\n" + "    \"v\": \"1\",\n") + "    \"id\": \"78547c87-af21-4392-FAAT-614da5baf6c3\",\n") + "    \"rev\": 1,\n") + "    \"name\": \"The most smallest content pack\",\n") + "    \"summary\": \"This is the smallest and most useless content pack\",\n") + "    \"description\": \"### We do not saw!\\n But we might kill!\",\n") + "    \"vendor\": \"Graylog, Inc. <egwene@graylog.com>\",\n") + "    \"url\": \"https://github.com/graylog-labs/small-content-pack.git\",\n") + "    \"parameters\": [ ],\n") + "    \"entities\": [ ]\n") + "}");

    @Rule
    public final MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private ContentPackService contentPackService;

    @Mock
    private ContentPackPersistenceService contentPackPersistenceService;

    @Mock
    private ContentPackInstallationPersistenceService contentPackInstallationPersistenceService;

    @Mock
    private Set<ContentPackInstallation> contentPackInstallations;

    private ContentPackResource contentPackResource;

    private ObjectMapper objectMapper;

    public ContentPackResourceTest() {
        GuiceInjectorHolder.createInjector(Collections.emptyList());
    }

    @Test
    public void uploadContentPack() throws Exception {
        final ContentPack contentPack = objectMapper.readValue(ContentPackResourceTest.CONTENT_PACK, ContentPack.class);
        Mockito.when(contentPackPersistenceService.insert(contentPack)).thenReturn(Optional.ofNullable(contentPack));
        final Response response = contentPackResource.createContentPack(contentPack);
        Mockito.verify(contentPackPersistenceService, Mockito.times(1)).insert(contentPack);
        assertThat(response.getStatusInfo()).isEqualTo(CREATED);
    }

    @Test
    public void listAndLatest() throws Exception {
        final ContentPack contentPack = objectMapper.readValue(ContentPackResourceTest.CONTENT_PACK, ContentPack.class);
        final Set<ContentPack> contentPacks = Collections.singleton(contentPack);
        final Map<ModelId, Map<Integer, ContentPackMetadata>> metaDataMap = Collections.emptyMap();
        final ContentPackList expectedList = ContentPackList.create(contentPacks.size(), contentPacks, metaDataMap);
        Mockito.when(contentPackPersistenceService.loadAll()).thenReturn(Collections.singleton(contentPack));
        final ContentPackList contentPackList = contentPackResource.listContentPacks();
        Mockito.verify(contentPackPersistenceService, Mockito.times(1)).loadAll();
        assertThat(contentPackList).isEqualTo(expectedList);
        Mockito.when(contentPackPersistenceService.loadAllLatest()).thenReturn(Collections.singleton(contentPack));
        final ContentPackList contentPackLatest = contentPackResource.listLatestContentPacks();
        Mockito.verify(contentPackPersistenceService, Mockito.times(1)).loadAll();
        assertThat(contentPackLatest).isEqualTo(expectedList);
    }

    @Test
    public void getContentPack() throws Exception {
        final ContentPack contentPack = objectMapper.readValue(ContentPackResourceTest.CONTENT_PACK, ContentPack.class);
        final Set<ContentPack> contentPackSet = Collections.singleton(contentPack);
        final Set<ConstraintCheckResult> constraints = Collections.emptySet();
        final Map<Integer, ContentPack> contentPacks = Collections.singletonMap(1, contentPack);
        final Map<Integer, Set<ConstraintCheckResult>> constraintMap = Collections.singletonMap(1, constraints);
        final ContentPackRevisions expectedRevisions = ContentPackRevisions.create(contentPacks, constraintMap);
        final ModelId id = ModelId.of("1");
        Mockito.when(contentPackPersistenceService.findAllById(id)).thenReturn(contentPackSet);
        final ContentPackRevisions contentPackRevisions = contentPackResource.listContentPackRevisions(id);
        Mockito.verify(contentPackPersistenceService, Mockito.times(1)).findAllById(id);
        assertThat(contentPackRevisions).isEqualTo(expectedRevisions);
        Mockito.when(contentPackPersistenceService.findByIdAndRevision(id, 1)).thenReturn(Optional.ofNullable(contentPack));
        final ContentPackResponse contentPackResponse = contentPackResource.getContentPackRevisions(id, 1);
        Mockito.verify(contentPackPersistenceService, Mockito.times(1)).findByIdAndRevision(id, 1);
        assertThat(contentPackResponse.contentPack()).isEqualTo(contentPack);
    }

    @Test
    public void deleteContentPack() throws Exception {
        final ModelId id = ModelId.of("1");
        Mockito.when(contentPackPersistenceService.deleteById(id)).thenReturn(1);
        contentPackResource.deleteContentPack(id);
        Mockito.verify(contentPackPersistenceService, Mockito.times(1)).deleteById(id);
        Mockito.when(contentPackPersistenceService.deleteByIdAndRevision(id, 1)).thenReturn(1);
        contentPackResource.deleteContentPack(id, 1);
        Mockito.verify(contentPackPersistenceService, Mockito.times(1)).deleteByIdAndRevision(id, 1);
    }

    @Test
    public void notDeleteContentPack() throws Exception {
        final ModelId id = ModelId.of("1");
        Mockito.when(contentPackInstallations.size()).thenReturn(1);
        Mockito.when(contentPackInstallationPersistenceService.findByContentPackId(id)).thenReturn(contentPackInstallations);
        boolean exceptionCalled = false;
        try {
            contentPackResource.deleteContentPack(id);
        } catch (BadRequestException e) {
            exceptionCalled = true;
        }
        assertThat(exceptionCalled).isEqualTo(true);
        Mockito.verify(contentPackInstallationPersistenceService, Mockito.times(1)).findByContentPackId(id);
        Mockito.verify(contentPackPersistenceService, Mockito.times(0)).deleteById(id);
        Mockito.when(contentPackInstallations.size()).thenReturn(1);
        Mockito.when(contentPackInstallationPersistenceService.findByContentPackIdAndRevision(id, 1)).thenReturn(contentPackInstallations);
        exceptionCalled = false;
        try {
            contentPackResource.deleteContentPack(id, 1);
        } catch (BadRequestException e) {
            exceptionCalled = true;
        }
        assertThat(exceptionCalled).isEqualTo(true);
        Mockito.verify(contentPackInstallationPersistenceService, Mockito.times(1)).findByContentPackIdAndRevision(id, 1);
        Mockito.verify(contentPackPersistenceService, Mockito.times(0)).deleteByIdAndRevision(id, 1);
    }

    static class PermittedTestResource extends ContentPackResource {
        PermittedTestResource(ContentPackService contentPackService, ContentPackPersistenceService contentPackPersistenceService, ContentPackInstallationPersistenceService contentPackInstallationPersistenceService) {
            super(contentPackService, contentPackPersistenceService, contentPackInstallationPersistenceService);
        }

        @Override
        protected boolean isPermitted(String permission) {
            return true;
        }

        @Override
        protected boolean isPermitted(String permission, String id) {
            return true;
        }

        @Override
        protected UriBuilder getUriBuilderToSelf() {
            return UriBuilder.fromUri("http://testserver/api");
        }
    }
}

