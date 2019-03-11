package com.baeldung.gridfs;


import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.gridfs.model.GridFSFile;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.bson.types.ObjectId;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 * This test requires:
 * * mongodb instance running on the environment
 */
@ContextConfiguration("file:src/main/resources/mongoConfig.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class GridFSLiveTest {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Test
    public void whenStoringFileWithMetadata_thenFileAndMetadataAreStored() {
        DBObject metaData = new BasicDBObject();
        metaData.put("user", "alex");
        InputStream inputStream = null;
        String id = "";
        try {
            inputStream = new FileInputStream("src/main/resources/test.png");
            id = gridFsTemplate.store(inputStream, "test.png", "image/png", metaData).toString();
        } catch (FileNotFoundException ex) {
            logger.error("File not found", ex);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ex) {
                    logger.error("Failed to close", ex);
                }
            }
        }
        Assert.assertNotNull(id);
    }

    @Test
    public void givenFileWithMetadataExist_whenFindingFileById_thenFileWithMetadataIsFound() {
        DBObject metaData = new BasicDBObject();
        metaData.put("user", "alex");
        InputStream inputStream = null;
        ObjectId id = null;
        try {
            inputStream = new FileInputStream("src/main/resources/test.png");
            id = gridFsTemplate.store(inputStream, "test.png", "image/png", metaData);
        } catch (FileNotFoundException ex) {
            logger.error("File not found", ex);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ex) {
                    logger.error("Failed to close", ex);
                }
            }
        }
        GridFSFile gridFSFile = gridFsTemplate.findOne(new Query(Criteria.where("_id").is(id)));
        Assert.assertNotNull(gridFSFile);
        // assertNotNull(gridFSFile.getInputStream());
        // assertThat(gridFSFile.numChunks(), is(1));
        // assertThat(gridFSFile.containsField("filename"), is(true));
        Assert.assertThat(gridFSFile.getFilename(), CoreMatchers.is("test.png"));
        Assert.assertThat(gridFSFile.getObjectId(), CoreMatchers.is(id));
        // assertThat(gridFSFile.keySet().size(), is(9));
        // assertNotNull(gridFSFile.getMD5());
        Assert.assertNotNull(gridFSFile.getUploadDate());
        // assertNull(gridFSFile.getAliases());
        Assert.assertNotNull(gridFSFile.getChunkSize());
        Assert.assertThat(gridFSFile.getMetadata().get("_contentType"), CoreMatchers.is("image/png"));
        Assert.assertThat(gridFSFile.getFilename(), CoreMatchers.is("test.png"));
        Assert.assertThat(gridFSFile.getMetadata().get("user"), CoreMatchers.is("alex"));
    }

    @Test
    public void givenMetadataAndFilesExist_whenFindingAllFiles_thenFilesWithMetadataAreFound() {
        DBObject metaDataUser1 = new BasicDBObject();
        metaDataUser1.put("user", "alex");
        DBObject metaDataUser2 = new BasicDBObject();
        metaDataUser2.put("user", "david");
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream("src/main/resources/test.png");
            gridFsTemplate.store(inputStream, "test.png", "image/png", metaDataUser1);
            gridFsTemplate.store(inputStream, "test.png", "image/png", metaDataUser2);
        } catch (FileNotFoundException ex) {
            logger.error("File not found", ex);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ex) {
                    logger.error("Failed to close", ex);
                }
            }
        }
        List<GridFSFile> gridFSFiles = new ArrayList<GridFSFile>();
        gridFsTemplate.find(new Query()).into(gridFSFiles);
        Assert.assertNotNull(gridFSFiles);
        Assert.assertThat(gridFSFiles.size(), CoreMatchers.is(2));
    }

    @Test
    public void givenMetadataAndFilesExist_whenFindingAllFilesOnQuery_thenFilesWithMetadataAreFoundOnQuery() {
        DBObject metaDataUser1 = new BasicDBObject();
        metaDataUser1.put("user", "alex");
        DBObject metaDataUser2 = new BasicDBObject();
        metaDataUser2.put("user", "david");
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream("src/main/resources/test.png");
            gridFsTemplate.store(inputStream, "test.png", "image/png", metaDataUser1);
            gridFsTemplate.store(inputStream, "test.png", "image/png", metaDataUser2);
        } catch (FileNotFoundException ex) {
            logger.error("File not found", ex);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ex) {
                    logger.error("Failed to close", ex);
                }
            }
        }
        List<GridFSFile> gridFSFiles = new ArrayList<GridFSFile>();
        gridFsTemplate.find(new Query(Criteria.where("metadata.user").is("alex"))).into(gridFSFiles);
        Assert.assertNotNull(gridFSFiles);
        Assert.assertThat(gridFSFiles.size(), CoreMatchers.is(1));
    }

    @Test
    public void givenFileWithMetadataExist_whenDeletingFileById_thenFileWithMetadataIsDeleted() {
        DBObject metaData = new BasicDBObject();
        metaData.put("user", "alex");
        InputStream inputStream = null;
        String id = "";
        try {
            inputStream = new FileInputStream("src/main/resources/test.png");
            id = gridFsTemplate.store(inputStream, "test.png", "image/png", metaData).toString();
        } catch (FileNotFoundException ex) {
            logger.error("File not found", ex);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ex) {
                    logger.error("Failed to close", ex);
                }
            }
        }
        gridFsTemplate.delete(new Query(Criteria.where("_id").is(id)));
        Assert.assertThat(gridFsTemplate.findOne(new Query(Criteria.where("_id").is(id))), CoreMatchers.is(Matchers.nullValue()));
    }

    @Test
    public void givenFileWithMetadataExist_whenGettingFileByResource_thenFileWithMetadataIsGotten() {
        DBObject metaData = new BasicDBObject();
        metaData.put("user", "alex");
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream("src/main/resources/test.png");
            gridFsTemplate.store(inputStream, "test.png", "image/png", metaData).toString();
        } catch (FileNotFoundException ex) {
            logger.error("File not found", ex);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ex) {
                    logger.error("Failed to close", ex);
                }
            }
        }
        GridFsResource[] gridFsResource = gridFsTemplate.getResources("test*");
        Assert.assertNotNull(gridFsResource);
        Assert.assertEquals(gridFsResource.length, 1);
        Assert.assertThat(gridFsResource[0].getFilename(), CoreMatchers.is("test.png"));
    }
}

