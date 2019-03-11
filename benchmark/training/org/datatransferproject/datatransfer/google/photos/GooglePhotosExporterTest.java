/**
 * Copyright 2018 The Data Transfer Project Authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.datatransferproject.datatransfer.google.photos;


import GooglePhotosExporter.PHOTO_TOKEN_PREFIX;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.datatransferproject.datatransfer.google.photos.model.AlbumListResponse;
import org.datatransferproject.datatransfer.google.photos.model.GoogleMediaItem;
import org.datatransferproject.datatransfer.google.photos.model.MediaItemSearchResponse;
import org.datatransferproject.spi.cloud.storage.JobStore;
import org.datatransferproject.spi.transfer.provider.ExportResult;
import org.datatransferproject.spi.transfer.types.ContinuationData;
import org.datatransferproject.spi.transfer.types.TempPhotosData;
import org.datatransferproject.types.common.PaginationData;
import org.datatransferproject.types.common.StringPaginationToken;
import org.datatransferproject.types.common.models.ContainerResource;
import org.datatransferproject.types.common.models.IdOnlyContainerResource;
import org.datatransferproject.types.common.models.photos.PhotoAlbum;
import org.datatransferproject.types.common.models.photos.PhotoModel;
import org.datatransferproject.types.common.models.photos.PhotosContainerResource;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Matchers;
import org.mockito.Mockito;


public class GooglePhotosExporterTest {
    private String IMG_URI = "image uri";

    private String PHOTO_ID = "photo id";

    private String ALBUM_ID = "GoogleAlbum id";

    private String ALBUM_TOKEN = "album_token";

    private String PHOTO_TOKEN = "photo_token";

    private UUID uuid = UUID.randomUUID();

    private GooglePhotosExporter googlePhotosExporter;

    private JobStore jobStore;

    private GooglePhotosInterface photosInterface;

    private MediaItemSearchResponse mediaItemSearchResponse;

    private AlbumListResponse albumListResponse;

    @Test
    public void exportAlbumFirstSet() throws IOException {
        setUpSingleAlbum();
        Mockito.when(albumListResponse.getNextPageToken()).thenReturn(ALBUM_TOKEN);
        // Run test
        ExportResult<PhotosContainerResource> result = googlePhotosExporter.exportAlbums(null, Optional.empty(), uuid);
        // Check results
        // Verify correct methods were called
        Mockito.verify(photosInterface).listAlbums(Optional.empty());
        Mockito.verify(albumListResponse).getAlbums();
        // Check pagination token
        ContinuationData continuationData = result.getContinuationData();
        StringPaginationToken paginationToken = ((StringPaginationToken) (continuationData.getPaginationData()));
        assertThat(paginationToken.getToken()).isEqualTo(((GooglePhotosExporter.ALBUM_TOKEN_PREFIX) + (ALBUM_TOKEN)));
        // Check albums field of container
        Collection<PhotoAlbum> actualAlbums = result.getExportedData().getAlbums();
        assertThat(actualAlbums.stream().map(PhotoAlbum::getId).collect(Collectors.toList())).containsExactly(ALBUM_ID);
        // Check photos field of container (should be empty, even though there is a photo in the
        // original album)
        Collection<PhotoModel> actualPhotos = result.getExportedData().getPhotos();
        assertThat(actualPhotos).isEmpty();
        // Should be one container in the resource list
        List<ContainerResource> actualResources = continuationData.getContainerResources();
        assertThat(actualResources.stream().map(( a) -> ((IdOnlyContainerResource) (a)).getId()).collect(Collectors.toList())).containsExactly(ALBUM_ID);
    }

    @Test
    public void exportAlbumSubsequentSet() throws IOException {
        setUpSingleAlbum();
        Mockito.when(albumListResponse.getNextPageToken()).thenReturn(null);
        StringPaginationToken inputPaginationToken = new StringPaginationToken(((GooglePhotosExporter.ALBUM_TOKEN_PREFIX) + (ALBUM_TOKEN)));
        // Run test
        ExportResult<PhotosContainerResource> result = googlePhotosExporter.exportAlbums(null, Optional.of(inputPaginationToken), uuid);
        // Check results
        // Verify correct methods were called
        Mockito.verify(photosInterface).listAlbums(Optional.of(ALBUM_TOKEN));
        Mockito.verify(albumListResponse).getAlbums();
        // Check pagination token - should be absent
        ContinuationData continuationData = result.getContinuationData();
        StringPaginationToken paginationData = ((StringPaginationToken) (continuationData.getPaginationData()));
        assertThat(paginationData.getToken()).isEqualTo(PHOTO_TOKEN_PREFIX);
    }

    @Test
    public void exportPhotoFirstSet() throws IOException {
        setUpSingleAlbum();
        Mockito.when(albumListResponse.getNextPageToken()).thenReturn(null);
        GoogleMediaItem mediaItem = setUpSinglePhoto(IMG_URI, PHOTO_ID);
        Mockito.when(mediaItemSearchResponse.getMediaItems()).thenReturn(new GoogleMediaItem[]{ mediaItem });
        Mockito.when(mediaItemSearchResponse.getNextPageToken()).thenReturn(PHOTO_TOKEN);
        IdOnlyContainerResource idOnlyContainerResource = new IdOnlyContainerResource(ALBUM_ID);
        ExportResult<PhotosContainerResource> result = googlePhotosExporter.exportPhotos(null, Optional.of(idOnlyContainerResource), Optional.empty(), uuid);
        // Check results
        // Verify correct methods were called
        Mockito.verify(photosInterface).listMediaItems(Optional.of(ALBUM_ID), Optional.empty());
        Mockito.verify(mediaItemSearchResponse).getMediaItems();
        // Check pagination
        ContinuationData continuationData = result.getContinuationData();
        StringPaginationToken paginationToken = ((StringPaginationToken) (continuationData.getPaginationData()));
        assertThat(paginationToken.getToken()).isEqualTo(((GooglePhotosExporter.PHOTO_TOKEN_PREFIX) + (PHOTO_TOKEN)));
        // Check albums field of container (should be empty)
        Collection<PhotoAlbum> actualAlbums = result.getExportedData().getAlbums();
        assertThat(actualAlbums).isEmpty();
        // Check photos field of container
        Collection<PhotoModel> actualPhotos = result.getExportedData().getPhotos();
        assertThat(actualPhotos.stream().map(PhotoModel::getFetchableUrl).collect(Collectors.toList())).containsExactly(((IMG_URI) + "=d"));// for download

        assertThat(actualPhotos.stream().map(PhotoModel::getAlbumId).collect(Collectors.toList())).containsExactly(ALBUM_ID);
    }

    @Test
    public void exportPhotoSubsequentSet() throws IOException {
        setUpSingleAlbum();
        Mockito.when(albumListResponse.getNextPageToken()).thenReturn(null);
        GoogleMediaItem mediaItem = setUpSinglePhoto(IMG_URI, PHOTO_ID);
        Mockito.when(mediaItemSearchResponse.getMediaItems()).thenReturn(new GoogleMediaItem[]{ mediaItem });
        Mockito.when(mediaItemSearchResponse.getNextPageToken()).thenReturn(null);
        StringPaginationToken inputPaginationToken = new StringPaginationToken(((GooglePhotosExporter.PHOTO_TOKEN_PREFIX) + (PHOTO_TOKEN)));
        IdOnlyContainerResource idOnlyContainerResource = new IdOnlyContainerResource(ALBUM_ID);
        // Run test
        ExportResult<PhotosContainerResource> result = googlePhotosExporter.exportPhotos(null, Optional.of(idOnlyContainerResource), Optional.of(inputPaginationToken), uuid);
        // Check results
        // Verify correct methods were called
        Mockito.verify(photosInterface).listMediaItems(Optional.of(ALBUM_ID), Optional.of(PHOTO_TOKEN));
        Mockito.verify(mediaItemSearchResponse).getMediaItems();
        // Check pagination token
        ContinuationData continuationData = result.getContinuationData();
        PaginationData paginationToken = continuationData.getPaginationData();
        Assert.assertNull(paginationToken);
    }

    @Test
    public void populateContainedPhotosList() throws IOException {
        // Set up an album with two photos
        setUpSingleAlbum();
        Mockito.when(albumListResponse.getNextPageToken()).thenReturn(null);
        MediaItemSearchResponse albumMediaResponse = Mockito.mock(MediaItemSearchResponse.class);
        GoogleMediaItem firstPhoto = setUpSinglePhoto(IMG_URI, PHOTO_ID);
        String secondUri = "second uri";
        String secondId = "second id";
        GoogleMediaItem secondPhoto = setUpSinglePhoto(secondUri, secondId);
        Mockito.when(photosInterface.listMediaItems(Matchers.eq(Optional.of(ALBUM_ID)), Matchers.any(Optional.class))).thenReturn(albumMediaResponse);
        Mockito.when(albumMediaResponse.getMediaItems()).thenReturn(new GoogleMediaItem[]{ firstPhoto, secondPhoto });
        Mockito.when(albumMediaResponse.getNextPageToken()).thenReturn(null);
        // Run test
        googlePhotosExporter.populateContainedPhotosList(uuid, null);
        // Check contents of job store
        ArgumentCaptor<TempPhotosData> tempPhotosDataArgumentCaptor = ArgumentCaptor.forClass(TempPhotosData.class);
        Mockito.verify(jobStore).create(Matchers.eq(uuid), Matchers.eq("tempPhotosData"), tempPhotosDataArgumentCaptor.capture());
        assertThat(tempPhotosDataArgumentCaptor.getValue().lookupContainedPhotoIds()).containsExactly(PHOTO_ID, secondId);
    }

    /* Tests that when there is no album information passed along to exportPhotos, only albumless
    photos are exported.
     */
    @Test
    public void onlyExportAlbumlessPhoto() throws IOException {
        // Set up - two photos will be returned by a media item search without an album id, but one of
        // them will have already been put into the list of contained photos
        String containedPhotoUri = "contained photo uri";
        String containedPhotoId = "contained photo id";
        GoogleMediaItem containedPhoto = setUpSinglePhoto(containedPhotoUri, containedPhotoId);
        String albumlessPhotoUri = "albumless photo uri";
        String albumlessPhotoId = "albumless photo id";
        GoogleMediaItem albumlessPhoto = setUpSinglePhoto(albumlessPhotoUri, albumlessPhotoId);
        MediaItemSearchResponse mediaItemSearchResponse = Mockito.mock(MediaItemSearchResponse.class);
        Mockito.when(photosInterface.listMediaItems(Matchers.eq(Optional.empty()), Matchers.eq(Optional.empty()))).thenReturn(mediaItemSearchResponse);
        Mockito.when(mediaItemSearchResponse.getMediaItems()).thenReturn(new GoogleMediaItem[]{ containedPhoto, albumlessPhoto });
        Mockito.when(mediaItemSearchResponse.getNextPageToken()).thenReturn(null);
        TempPhotosData tempPhotosData = new TempPhotosData(uuid);
        tempPhotosData.addContainedPhotoId(containedPhotoId);
        InputStream stream = GooglePhotosExporter.convertJsonToInputStream(tempPhotosData);
        Mockito.when(jobStore.getStream(uuid, "tempPhotosData")).thenReturn(stream);
        // Run test
        ExportResult<PhotosContainerResource> result = googlePhotosExporter.exportPhotos(null, Optional.empty(), Optional.empty(), uuid);
        // Check results
        assertThat(result.getExportedData().getPhotos().stream().map(PhotoModel::getFetchableUrl).collect(Collectors.toList())).containsExactly((albumlessPhotoUri + "=d"));// download

    }
}

