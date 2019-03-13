/**
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
package org.flowable.content.engine.test;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.io.IOUtils;
import org.flowable.common.engine.api.FlowableObjectNotFoundException;
import org.flowable.content.api.ContentItem;
import org.junit.Assert;
import org.junit.Test;


public class ContentItemTest extends AbstractFlowableContentTest {
    @Test
    public void createSimpleProcessContentItemNoData() throws Exception {
        ContentItem contentItem = contentService.newContentItem();
        contentItem.setName("testItem");
        contentItem.setMimeType("application/pdf");
        contentItem.setProcessInstanceId("123456");
        contentService.saveContentItem(contentItem);
        Assert.assertNotNull(contentItem.getId());
        ContentItem dbContentItem = contentService.createContentItemQuery().id(contentItem.getId()).singleResult();
        Assert.assertNotNull(dbContentItem);
        Assert.assertEquals(contentItem.getId(), dbContentItem.getId());
        contentService.deleteContentItem(contentItem.getId());
    }

    @Test
    public void createSimpleProcessContentItemWithData() throws Exception {
        ContentItem contentItem = contentService.newContentItem();
        contentItem.setProcessInstanceId("123456");
        assertCreateContentWithData(contentItem, "process-instance-content");
    }

    @Test
    public void createSimpleTaskContentItemWithData() throws Exception {
        ContentItem contentItem = contentService.newContentItem();
        contentItem.setTaskId("123456");
        assertCreateContentWithData(contentItem, "task-content");
    }

    @Test
    public void createSimpleCaseContentItemWithData() throws Exception {
        ContentItem contentItem = contentService.newContentItem();
        contentItem.setScopeId("123456");
        contentItem.setScopeType("cmmn");
        assertCreateContentWithData(contentItem, "cmmn");
    }

    @Test
    public void createSimpleNewTypeContentItemWithData() throws Exception {
        ContentItem contentItem = contentService.newContentItem();
        contentItem.setScopeId("123456");
        contentItem.setScopeType("newType");
        assertCreateContentWithData(contentItem, "newType");
    }

    @Test
    public void createSimpleUncategorizedTypeContentItemWithData() throws Exception {
        ContentItem contentItem = contentService.newContentItem();
        contentItem.setScopeId("123456");
        contentItem.setScopeType("uncategorizedNewType");
        assertCreateContentWithData(contentItem, "uncategorizedNewType");
    }

    @Test
    public void createSimpleUncategorizedContentItemWithData() throws Exception {
        ContentItem contentItem = contentService.newContentItem();
        contentItem.setScopeId("123456");
        contentItem.setScopeType(null);
        contentItem.setName("testItem");
        contentItem.setMimeType("application/pdf");
        try (InputStream in = this.getClass().getClassLoader().getResourceAsStream("test.txt")) {
            contentService.saveContentItem(contentItem, in);
        }
        Assert.assertNotNull(contentItem.getId());
        Assert.assertTrue(new File((((((contentEngineConfiguration.getContentRootFolder()) + (File.separator)) + "uncategorized") + (File.separator)) + (contentItem.getContentStoreId().substring(((contentItem.getContentStoreId().lastIndexOf('.')) + 1))))).exists());
        ContentItem dbContentItem = contentService.createContentItemQuery().id(contentItem.getId()).singleResult();
        Assert.assertNotNull(dbContentItem);
        Assert.assertEquals(contentItem.getId(), dbContentItem.getId());
        try (InputStream contentStream = contentService.getContentItemData(contentItem.getId())) {
            String contentValue = IOUtils.toString(contentStream, "utf-8");
            Assert.assertEquals("hello", contentValue);
        }
        contentService.deleteContentItem(contentItem.getId());
        Assert.assertFalse(new File((((((contentEngineConfiguration.getContentRootFolder()) + (File.separator)) + "uncategorized") + (File.separator)) + (contentItem.getContentStoreId().substring(((contentItem.getContentStoreId().lastIndexOf('.')) + 1))))).exists());
        try {
            contentService.getContentItemData(contentItem.getId());
            Assert.fail("Expected not found exception");
        } catch (FlowableObjectNotFoundException e) {
            // expected
        } catch (Exception e) {
            Assert.fail(("Expected not found exception, not " + e));
        }
    }

    @Test
    public void createSimpleUncategorizedContentItemWithoutIdWithData() throws Exception {
        ContentItem contentItem = contentService.newContentItem();
        contentItem.setScopeId(null);
        contentItem.setScopeType(null);
        contentItem.setName("testItem");
        contentItem.setMimeType("application/pdf");
        try (InputStream in = this.getClass().getClassLoader().getResourceAsStream("test.txt")) {
            contentService.saveContentItem(contentItem, in);
        }
        Assert.assertNotNull(contentItem.getId());
        Assert.assertTrue(new File((((((contentEngineConfiguration.getContentRootFolder()) + (File.separator)) + "uncategorized") + (File.separator)) + (contentItem.getContentStoreId().substring(((contentItem.getContentStoreId().lastIndexOf('.')) + 1))))).exists());
        ContentItem dbContentItem = contentService.createContentItemQuery().id(contentItem.getId()).singleResult();
        Assert.assertNotNull(dbContentItem);
        Assert.assertEquals(contentItem.getId(), dbContentItem.getId());
        try (InputStream contentStream = contentService.getContentItemData(contentItem.getId())) {
            String contentValue = IOUtils.toString(contentStream, "utf-8");
            Assert.assertEquals("hello", contentValue);
        }
        contentService.deleteContentItem(contentItem.getId());
        Assert.assertFalse(new File((((((contentEngineConfiguration.getContentRootFolder()) + (File.separator)) + "uncategorized") + (File.separator)) + (contentItem.getContentStoreId().substring(((contentItem.getContentStoreId().lastIndexOf('.')) + 1))))).exists());
        try {
            contentService.getContentItemData(contentItem.getId());
            Assert.fail("Expected not found exception");
        } catch (FlowableObjectNotFoundException e) {
            // expected
        } catch (Exception e) {
            Assert.fail(("Expected not found exception, not " + e));
        }
    }

    @Test
    public void createAndDeleteUncategorizedContentTwice() throws Exception {
        createSimpleUncategorizedContentItemWithoutIdWithData();
        createSimpleUncategorizedContentItemWithoutIdWithData();
    }

    @Test
    public void queryContentItemWithScopeId() {
        createContentItem();
        Assert.assertEquals("testScopeItem", contentService.createContentItemQuery().scopeId("testScopeId").singleResult().getName());
        Assert.assertEquals("testScopeItem", contentService.createContentItemQuery().scopeIdLike("testScope%").singleResult().getName());
        contentService.deleteContentItemsByScopeIdAndScopeType("testScopeId", "testScopeType");
    }

    @Test
    public void queryContentItemWithScopeType() {
        createContentItem();
        Assert.assertEquals("testScopeItem", contentService.createContentItemQuery().scopeType("testScopeType").singleResult().getName());
        Assert.assertEquals("testScopeItem", contentService.createContentItemQuery().scopeTypeLike("testScope%").singleResult().getName());
        contentService.deleteContentItemsByScopeIdAndScopeType("testScopeId", "testScopeType");
    }

    @Test
    public void insertAll() {
        ContentItem contentItem = contentService.newContentItem();
        contentItem.setName("testScopeItem1");
        contentItem.setMimeType("application/pdf");
        contentItem.setScopeType("testScopeType");
        contentItem.setScopeId("testScopeId");
        contentService.saveContentItem(contentItem);
        ContentItem contentItem2 = contentService.newContentItem();
        contentItem2.setName("testScopeItem1");
        contentItem2.setMimeType("application/pdf");
        contentItem2.setScopeType("testScopeType");
        contentItem2.setScopeId("testScopeId");
        contentService.saveContentItem(contentItem2);
        Assert.assertEquals(2, contentService.createContentItemQuery().scopeTypeLike("testScope%").list().size());
        contentService.deleteContentItemsByScopeIdAndScopeType("testScopeId", "testScopeType");
        Assert.assertEquals(0, contentService.createContentItemQuery().scopeTypeLike("testScope%").list().size());
    }

    @Test
    public void lastModifiedTimestampUpdateOnContentChange() throws IOException {
        ContentItem initialContentItem = contentService.newContentItem();
        initialContentItem.setName("testItem");
        initialContentItem.setMimeType("text/plain");
        contentService.saveContentItem(initialContentItem);
        long initialTS = System.currentTimeMillis();
        Assert.assertNotNull(initialContentItem.getId());
        Assert.assertNotNull(initialContentItem.getLastModified());
        Assert.assertTrue(((initialContentItem.getLastModified().getTime()) <= (System.currentTimeMillis())));
        ContentItem storedContentItem = contentService.createContentItemQuery().id(initialContentItem.getId()).singleResult();
        Assert.assertNotNull(storedContentItem);
        Assert.assertEquals(initialContentItem.getId(), storedContentItem.getId());
        Assert.assertEquals(initialContentItem.getLastModified().getTime(), storedContentItem.getLastModified().getTime());
        long storeTS = System.currentTimeMillis();
        contentService.saveContentItem(storedContentItem, this.getClass().getClassLoader().getResourceAsStream("test.txt"));
        storedContentItem = contentService.createContentItemQuery().id(initialContentItem.getId()).singleResult();
        Assert.assertNotNull(storedContentItem);
        Assert.assertEquals(initialContentItem.getId(), storedContentItem.getId());
        InputStream contentStream = contentService.getContentItemData(initialContentItem.getId());
        String contentValue = IOUtils.toString(contentStream, "utf-8");
        Assert.assertEquals("hello", contentValue);
        Assert.assertTrue(((initialContentItem.getLastModified().getTime()) < (storedContentItem.getLastModified().getTime())));
        contentService.deleteContentItem(initialContentItem.getId());
    }
}

