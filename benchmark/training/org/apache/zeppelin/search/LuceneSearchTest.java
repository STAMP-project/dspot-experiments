/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.zeppelin.search;


import AuthenticationInfo.ANONYMOUS;
import com.google.common.base.Splitter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.apache.zeppelin.interpreter.InterpreterSettingManager;
import org.apache.zeppelin.notebook.Note;
import org.apache.zeppelin.notebook.Notebook;
import org.apache.zeppelin.notebook.Paragraph;
import org.junit.Test;


public class LuceneSearchTest {
    private Notebook notebook;

    private InterpreterSettingManager interpreterSettingManager;

    private SearchService noteSearchService;

    @Test
    public void canIndexAndQueryByNotebookName() throws IOException, InterruptedException {
        // given
        Note note1 = newNoteWithParagraph("Notebook1", "test");
        Note note2 = newNoteWithParagraphs("Notebook2", "not test", "not test at all");
        noteSearchService.drainEvents();
        // when
        List<Map<String, String>> results = noteSearchService.query("Notebook1");
        // then
        assertThat(results).isNotEmpty();
        assertThat(results.size()).isEqualTo(1);
        assertThat(results.get(0)).containsEntry("id", note1.getId());
    }

    @Test
    public void canIndexAndQueryByParagraphTitle() throws IOException, InterruptedException {
        // given
        Note note1 = newNoteWithParagraph("Notebook1", "test", "testingTitleSearch");
        Note note2 = newNoteWithParagraph("Notebook2", "not test", "notTestingTitleSearch");
        noteSearchService.drainEvents();
        // when
        List<Map<String, String>> results = noteSearchService.query("testingTitleSearch");
        // then
        assertThat(results).isNotEmpty();
        assertThat(results.size()).isAtLeast(1);
        int TitleHits = 0;
        for (Map<String, String> res : results) {
            if (res.get("header").contains("testingTitleSearch")) {
                TitleHits++;
            }
        }
        assertThat(TitleHits).isAtLeast(1);
    }

    @Test
    public void indexKeyContract() throws IOException {
        // give
        Note note1 = newNoteWithParagraph("Notebook1", "test");
        // when
        noteSearchService.addIndexDoc(note1);
        // then
        String id = resultForQuery("test").get(0).get("id");// LuceneSearch.ID_FIELD

        // key structure <noteId>/paragraph/<paragraphId>
        assertThat(Splitter.on("/").split(id)).containsAllOf(note1.getId(), "paragraph", note1.getLastParagraph().getId());// LuceneSearch.PARAGRAPH

    }

    // (expected=IllegalStateException.class)
    @Test
    public void canNotSearchBeforeIndexing() {
        // given NO noteSearchService.index() was called
        // when
        List<Map<String, String>> result = noteSearchService.query("anything");
        // then
        assertThat(result).isEmpty();
        // assert logs were printed
        // "ERROR org.apache.zeppelin.search.SearchService:97 - Failed to open index dir RAMDirectory"
    }

    @Test
    public void canIndexAndReIndex() throws IOException, InterruptedException {
        // given
        Note note1 = newNoteWithParagraph("Notebook1", "test");
        Note note2 = newNoteWithParagraphs("Notebook2", "not test", "not test at all");
        noteSearchService.drainEvents();
        // when
        Paragraph p2 = note2.getLastParagraph();
        p2.setText("test indeed");
        noteSearchService.updateIndexDoc(note2);
        // then
        List<Map<String, String>> results = noteSearchService.query("all");
        assertThat(results).isEmpty();
        results = noteSearchService.query("indeed");
        assertThat(results).isNotEmpty();
    }

    @Test
    public void canDeleteNull() throws IOException {
        // give
        // looks like a bug in web UI: it tries to delete a note twice (after it has just been deleted)
        // when
        noteSearchService.deleteIndexDocs(null);
    }

    @Test
    public void canDeleteFromIndex() throws IOException, InterruptedException {
        // given
        Note note1 = newNoteWithParagraph("Notebook1", "test");
        Note note2 = newNoteWithParagraphs("Notebook2", "not test", "not test at all");
        noteSearchService.drainEvents();
        assertThat(resultForQuery("Notebook2")).isNotEmpty();
        // when
        noteSearchService.deleteIndexDocs(note2.getId());
        // then
        assertThat(noteSearchService.query("all")).isEmpty();
        assertThat(resultForQuery("Notebook2")).isEmpty();
        List<Map<String, String>> results = resultForQuery("test");
        assertThat(results).isNotEmpty();
        assertThat(results.size()).isEqualTo(1);
    }

    @Test
    public void indexParagraphUpdatedOnNoteSave() throws IOException, InterruptedException {
        // given: total 2 notebooks, 3 paragraphs
        Note note1 = newNoteWithParagraph("Notebook1", "test");
        Note note2 = newNoteWithParagraphs("Notebook2", "not test", "not test at all");
        noteSearchService.drainEvents();
        assertThat(resultForQuery("test").size()).isEqualTo(3);
        // when
        Paragraph p1 = note1.getLastParagraph();
        p1.setText("no no no");
        notebook.saveNote(note1, ANONYMOUS);
        noteSearchService.drainEvents();
        // then
        assertThat(resultForQuery("Notebook1").size()).isEqualTo(1);
        List<Map<String, String>> results = resultForQuery("test");
        assertThat(results).isNotEmpty();
        assertThat(results.size()).isEqualTo(2);
        // does not include Notebook1's paragraph any more
        for (Map<String, String> result : results) {
            assertThat(result.get("id").startsWith(note1.getId())).isFalse();
        }
    }

    @Test
    public void indexNoteNameUpdatedOnNoteSave() throws IOException, InterruptedException {
        // given: total 2 notebooks, 3 paragraphs
        Note note1 = newNoteWithParagraph("Notebook1", "test");
        Note note2 = newNoteWithParagraphs("Notebook2", "not test", "not test at all");
        noteSearchService.drainEvents();
        assertThat(resultForQuery("test").size()).isEqualTo(3);
        // when
        note1.setName("NotebookN");
        notebook.saveNote(note1, ANONYMOUS);
        noteSearchService.drainEvents();
        Thread.sleep(1000);
        // then
        assertThat(resultForQuery("Notebook1")).isEmpty();
        assertThat(resultForQuery("NotebookN")).isNotEmpty();
        assertThat(resultForQuery("NotebookN").size()).isEqualTo(1);
    }
}

