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
package org.apache.zeppelin.notebook;


import AuthenticationInfo.ANONYMOUS;
import InterpreterResult.Code;
import InterpreterResult.Type;
import java.util.ArrayList;
import java.util.List;
import org.apache.zeppelin.interpreter.Interpreter;
import org.apache.zeppelin.interpreter.InterpreterFactory;
import org.apache.zeppelin.interpreter.InterpreterNotFoundException;
import org.apache.zeppelin.interpreter.InterpreterResult;
import org.apache.zeppelin.interpreter.InterpreterSettingManager;
import org.apache.zeppelin.notebook.repo.NotebookRepo;
import org.apache.zeppelin.scheduler.Scheduler;
import org.apache.zeppelin.user.AuthenticationInfo;
import org.apache.zeppelin.user.Credentials;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class NoteTest {
    @Mock
    NotebookRepo repo;

    @Mock
    ParagraphJobListener paragraphJobListener;

    @Mock
    Credentials credentials;

    @Mock
    Interpreter interpreter;

    @Mock
    Scheduler scheduler;

    List<NoteEventListener> noteEventListener = new ArrayList<>();

    @Mock
    InterpreterFactory interpreterFactory;

    @Mock
    InterpreterSettingManager interpreterSettingManager;

    private AuthenticationInfo anonymous = new AuthenticationInfo("anonymous");

    @Test
    public void runNormalTest() throws InterpreterNotFoundException {
        Mockito.when(interpreterFactory.getInterpreter(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.eq("spark"), ArgumentMatchers.anyString())).thenReturn(interpreter);
        Mockito.when(interpreter.getScheduler()).thenReturn(scheduler);
        String pText = "%spark sc.version";
        Note note = new Note("test", "test", interpreterFactory, interpreterSettingManager, paragraphJobListener, credentials, noteEventListener);
        Paragraph p = note.addNewParagraph(ANONYMOUS);
        p.setText(pText);
        p.setAuthenticationInfo(anonymous);
        note.run(p.getId());
        ArgumentCaptor<Paragraph> pCaptor = ArgumentCaptor.forClass(Paragraph.class);
        Mockito.verify(scheduler, Mockito.only()).submit(pCaptor.capture());
        Mockito.verify(interpreterFactory, Mockito.times(1)).getInterpreter(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.eq("spark"), ArgumentMatchers.anyString());
        Assert.assertEquals("Paragraph text", pText, pCaptor.getValue().getText());
    }

    @Test
    public void addParagraphWithEmptyReplNameTest() {
        Note note = new Note("test", "", interpreterFactory, interpreterSettingManager, paragraphJobListener, credentials, noteEventListener);
        Paragraph p = note.addNewParagraph(ANONYMOUS);
        Assert.assertNull(p.getText());
    }

    @Test
    public void addParagraphWithLastReplNameTest() throws InterpreterNotFoundException {
        Mockito.when(interpreterFactory.getInterpreter(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.eq("spark"), ArgumentMatchers.anyString())).thenReturn(interpreter);
        Note note = new Note("test", "", interpreterFactory, interpreterSettingManager, paragraphJobListener, credentials, noteEventListener);
        Paragraph p1 = note.addNewParagraph(ANONYMOUS);
        p1.setText("%spark ");
        Paragraph p2 = note.addNewParagraph(ANONYMOUS);
        Assert.assertEquals("%spark\n", p2.getText());
    }

    @Test
    public void insertParagraphWithLastReplNameTest() throws InterpreterNotFoundException {
        Mockito.when(interpreterFactory.getInterpreter(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.eq("spark"), ArgumentMatchers.anyString())).thenReturn(interpreter);
        Note note = new Note("test", "", interpreterFactory, interpreterSettingManager, paragraphJobListener, credentials, noteEventListener);
        Paragraph p1 = note.addNewParagraph(ANONYMOUS);
        p1.setText("%spark ");
        Paragraph p2 = note.insertNewParagraph(note.getParagraphs().size(), ANONYMOUS);
        Assert.assertEquals("%spark\n", p2.getText());
    }

    @Test
    public void insertParagraphWithInvalidReplNameTest() throws InterpreterNotFoundException {
        Mockito.when(interpreterFactory.getInterpreter(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.eq("invalid"), ArgumentMatchers.anyString())).thenReturn(null);
        Note note = new Note("test", "", interpreterFactory, interpreterSettingManager, paragraphJobListener, credentials, noteEventListener);
        Paragraph p1 = note.addNewParagraph(ANONYMOUS);
        p1.setText("%invalid ");
        Paragraph p2 = note.insertNewParagraph(note.getParagraphs().size(), ANONYMOUS);
        Assert.assertNull(p2.getText());
    }

    @Test
    public void insertParagraphwithUser() {
        Note note = new Note("test", "", interpreterFactory, interpreterSettingManager, paragraphJobListener, credentials, noteEventListener);
        Paragraph p = note.insertNewParagraph(note.getParagraphs().size(), ANONYMOUS);
        Assert.assertEquals("anonymous", p.getUser());
    }

    @Test
    public void clearAllParagraphOutputTest() throws InterpreterNotFoundException {
        Mockito.when(interpreterFactory.getInterpreter(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.eq("md"), ArgumentMatchers.anyString())).thenReturn(interpreter);
        Mockito.when(interpreter.getScheduler()).thenReturn(scheduler);
        Note note = new Note("test", "", interpreterFactory, interpreterSettingManager, paragraphJobListener, credentials, noteEventListener);
        Paragraph p1 = note.addNewParagraph(ANONYMOUS);
        InterpreterResult result = new InterpreterResult(Code.SUCCESS, Type.TEXT, "result");
        p1.setResult(result);
        Paragraph p2 = note.addNewParagraph(ANONYMOUS);
        p2.setReturn(result, new Throwable());
        note.clearAllParagraphOutput();
        Assert.assertNull(p1.getReturn());
        Assert.assertNull(p2.getReturn());
    }

    @Test
    public void personalizedModeReturnDifferentParagraphInstancePerUser() {
        Note note = new Note("test", "", interpreterFactory, interpreterSettingManager, paragraphJobListener, credentials, noteEventListener);
        String user1 = "user1";
        String user2 = "user2";
        note.setPersonalizedMode(true);
        note.addNewParagraph(new AuthenticationInfo(user1));
        Paragraph baseParagraph = note.getParagraphs().get(0);
        Paragraph user1Paragraph = baseParagraph.getUserParagraph(user1);
        Paragraph user2Paragraph = baseParagraph.getUserParagraph(user2);
        Assert.assertNotEquals(System.identityHashCode(baseParagraph), System.identityHashCode(user1Paragraph));
        Assert.assertNotEquals(System.identityHashCode(baseParagraph), System.identityHashCode(user2Paragraph));
        Assert.assertNotEquals(System.identityHashCode(user1Paragraph), System.identityHashCode(user2Paragraph));
    }
}

