/**
 * Copyright 2018 The Data Transfer Project Authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.datatransferproject.datatransfer.flickr.photos;


import FlickrPhotosImporter.ImageStreamProvider;
import com.flickr4java.flickr.Flickr;
import com.flickr4java.flickr.FlickrException;
import com.flickr4java.flickr.auth.Auth;
import com.flickr4java.flickr.auth.AuthInterface;
import com.flickr4java.flickr.auth.Permission;
import com.flickr4java.flickr.people.User;
import com.flickr4java.flickr.photosets.Photoset;
import com.flickr4java.flickr.photosets.PhotosetsInterface;
import com.flickr4java.flickr.uploader.UploadMetaData;
import com.flickr4java.flickr.uploader.Uploader;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.UUID;
import org.datatransferproject.api.launcher.Monitor;
import org.datatransferproject.cloud.local.LocalJobStore;
import org.datatransferproject.spi.cloud.storage.JobStore;
import org.datatransferproject.spi.transfer.provider.ImportResult;
import org.datatransferproject.spi.transfer.types.TempPhotosData;
import org.datatransferproject.types.common.models.photos.PhotoAlbum;
import org.datatransferproject.types.common.models.photos.PhotoModel;
import org.datatransferproject.types.common.models.photos.PhotosContainerResource;
import org.datatransferproject.types.transfer.auth.TokenSecretAuthData;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.scribe.model.Token;

import static FlickrPhotosImporter.COPY_PREFIX;


public class FlickrPhotosImporterTest {
    private static final String ALBUM_ID = "Album ID";

    private static final String ALBUM_NAME = "Album name";

    private static final String ALBUM_DESCRIPTION = "Album description";

    private static final String PHOTO_TITLE = "Title";

    private static final String FETCHABLE_URL = "fetchable_url";

    private static final String PHOTO_DESCRIPTION = "Description";

    private static final String MEDIA_TYPE = "jpeg";

    private static final String FLICKR_PHOTO_ID = "flickrPhotoId";

    private static final String FLICKR_ALBUM_ID = "flickrAlbumId";

    private static final PhotoAlbum PHOTO_ALBUM = new PhotoAlbum(FlickrPhotosImporterTest.ALBUM_ID, FlickrPhotosImporterTest.ALBUM_NAME, FlickrPhotosImporterTest.ALBUM_DESCRIPTION);

    private static final PhotoModel PHOTO_MODEL = new PhotoModel(FlickrPhotosImporterTest.PHOTO_TITLE, FlickrPhotosImporterTest.FETCHABLE_URL, FlickrPhotosImporterTest.PHOTO_DESCRIPTION, FlickrPhotosImporterTest.MEDIA_TYPE, null, FlickrPhotosImporterTest.ALBUM_ID, false);

    private Flickr flickr = Mockito.mock(Flickr.class);

    private PhotosetsInterface photosetsInterface = Mockito.mock(PhotosetsInterface.class);

    private Uploader uploader = Mockito.mock(Uploader.class);

    private JobStore jobStore = new LocalJobStore();

    private ImageStreamProvider imageStreamProvider = Mockito.mock(ImageStreamProvider.class);

    private User user = Mockito.mock(User.class);

    private Auth auth = new Auth(Permission.WRITE, user);

    private BufferedInputStream bufferedInputStream = Mockito.mock(BufferedInputStream.class);

    private AuthInterface authInterface = Mockito.mock(AuthInterface.class);

    private Monitor monitor = Mockito.mock(Monitor.class);

    @Test
    public void importStoresAlbumInJobStore() throws FlickrException, IOException {
        UUID jobId = UUID.randomUUID();
        PhotosContainerResource photosContainerResource = new PhotosContainerResource(Collections.singletonList(FlickrPhotosImporterTest.PHOTO_ALBUM), Collections.singletonList(FlickrPhotosImporterTest.PHOTO_MODEL));
        // Setup Mock
        Mockito.when(user.getId()).thenReturn("userId");
        Mockito.when(authInterface.checkToken(ArgumentMatchers.any(Token.class))).thenReturn(auth);
        Mockito.when(flickr.getPhotosetsInterface()).thenReturn(photosetsInterface);
        Mockito.when(flickr.getUploader()).thenReturn(uploader);
        Mockito.when(flickr.getAuthInterface()).thenReturn(authInterface);
        Mockito.when(imageStreamProvider.get(FlickrPhotosImporterTest.FETCHABLE_URL)).thenReturn(bufferedInputStream);
        Mockito.when(uploader.upload(ArgumentMatchers.any(BufferedInputStream.class), ArgumentMatchers.any(UploadMetaData.class))).thenReturn(FlickrPhotosImporterTest.FLICKR_PHOTO_ID);
        String flickrAlbumTitle = (COPY_PREFIX) + (FlickrPhotosImporterTest.ALBUM_NAME);
        Photoset photoset = FlickrTestUtils.initializePhotoset(FlickrPhotosImporterTest.FLICKR_ALBUM_ID, FlickrPhotosImporterTest.ALBUM_DESCRIPTION, FlickrPhotosImporterTest.FLICKR_PHOTO_ID);
        Mockito.when(photosetsInterface.create(flickrAlbumTitle, FlickrPhotosImporterTest.ALBUM_DESCRIPTION, FlickrPhotosImporterTest.FLICKR_PHOTO_ID)).thenReturn(photoset);
        // Run test
        FlickrPhotosImporter importer = new FlickrPhotosImporter(flickr, jobStore, imageStreamProvider, monitor);
        ImportResult result = importer.importItem(jobId, new TokenSecretAuthData("token", "secret"), photosContainerResource);
        // Verify that the image stream provider got the correct URL and that the correct info was uploaded
        Mockito.verify(imageStreamProvider).get(FlickrPhotosImporterTest.FETCHABLE_URL);
        ArgumentCaptor<UploadMetaData> uploadMetaDataArgumentCaptor = ArgumentCaptor.forClass(UploadMetaData.class);
        Mockito.verify(uploader).upload(ArgumentMatchers.eq(bufferedInputStream), uploadMetaDataArgumentCaptor.capture());
        UploadMetaData actualUploadMetaData = uploadMetaDataArgumentCaptor.getValue();
        assertThat(actualUploadMetaData.getTitle()).isEqualTo(((COPY_PREFIX) + (FlickrPhotosImporterTest.PHOTO_TITLE)));
        assertThat(actualUploadMetaData.getDescription()).isEqualTo(FlickrPhotosImporterTest.PHOTO_DESCRIPTION);
        // Verify the photosets interface got the command to create the correct album
        Mockito.verify(photosetsInterface).create(flickrAlbumTitle, FlickrPhotosImporterTest.ALBUM_DESCRIPTION, FlickrPhotosImporterTest.FLICKR_PHOTO_ID);
        // Check contents of JobStore
        TempPhotosData tempPhotosData = jobStore.findData(jobId, "tempPhotosData", TempPhotosData.class);
        assertThat(tempPhotosData).isNotNull();
        String expectedAlbumKey = FlickrPhotosImporterTest.ALBUM_ID;
        assertThat(tempPhotosData.lookupTempAlbum(expectedAlbumKey)).isNull();
        assertThat(tempPhotosData.lookupNewAlbumId(FlickrPhotosImporterTest.ALBUM_ID)).isEqualTo(FlickrPhotosImporterTest.FLICKR_ALBUM_ID);
    }
}

