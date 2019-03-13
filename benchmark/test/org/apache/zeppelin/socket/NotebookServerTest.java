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
package org.apache.zeppelin.socket;


import AuthenticationInfo.ANONYMOUS;
import Job.Status;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.thrift.TException;
import org.apache.zeppelin.conf.ZeppelinConfiguration;
import org.apache.zeppelin.display.AngularObject;
import org.apache.zeppelin.display.AngularObjectBuilder;
import org.apache.zeppelin.interpreter.InterpreterGroup;
import org.apache.zeppelin.interpreter.InterpreterSetting;
import org.apache.zeppelin.interpreter.remote.RemoteAngularObjectRegistry;
import org.apache.zeppelin.interpreter.thrift.ParagraphInfo;
import org.apache.zeppelin.interpreter.thrift.ServiceException;
import org.apache.zeppelin.notebook.Note;
import org.apache.zeppelin.notebook.Notebook;
import org.apache.zeppelin.notebook.NotebookAuthorization;
import org.apache.zeppelin.notebook.Paragraph;
import org.apache.zeppelin.notebook.socket.Message;
import org.apache.zeppelin.notebook.socket.Message.OP;
import org.apache.zeppelin.rest.AbstractTestRestApi;
import org.apache.zeppelin.service.NotebookService;
import org.apache.zeppelin.user.AuthenticationInfo;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Basic REST API tests for notebookServer.
 */
public class NotebookServerTest extends AbstractTestRestApi {
    private static Notebook notebook;

    private static NotebookServer notebookServer;

    private static NotebookService notebookService;

    private HttpServletRequest mockRequest;

    private AuthenticationInfo anonymous;

    @Test
    public void checkOrigin() throws UnknownHostException {
        NotebookServer server = new NotebookServer();
        server.setNotebook(() -> NotebookServerTest.notebook);
        server.setNotebookService(() -> NotebookServerTest.notebookService);
        String origin = ("http://" + (InetAddress.getLocalHost().getHostName())) + ":8080";
        Assert.assertTrue((("Origin " + origin) + " is not allowed. Please check your hostname."), server.checkOrigin(mockRequest, origin));
    }

    @Test
    public void checkInvalidOrigin() {
        NotebookServer server = new NotebookServer();
        server.setNotebook(() -> NotebookServerTest.notebook);
        server.setNotebookService(() -> NotebookServerTest.notebookService);
        Assert.assertFalse(server.checkOrigin(mockRequest, "http://evillocalhost:8080"));
    }

    @Test
    public void testCollaborativeEditing() throws IOException {
        if (!(ZeppelinConfiguration.create().isZeppelinNotebookCollaborativeModeEnable())) {
            return;
        }
        NotebookSocket sock1 = createWebSocket();
        NotebookSocket sock2 = createWebSocket();
        String noteName = "Note with millis " + (System.currentTimeMillis());
        NotebookServerTest.notebookServer.onMessage(sock1, put("name", noteName).toJson());
        Note createdNote = null;
        for (Note note : NotebookServerTest.notebook.getAllNotes()) {
            if (note.getName().equals(noteName)) {
                createdNote = note;
                break;
            }
        }
        Message message = new Message(OP.GET_NOTE).put("id", createdNote.getId());
        NotebookServerTest.notebookServer.onMessage(sock1, message.toJson());
        NotebookServerTest.notebookServer.onMessage(sock2, message.toJson());
        Paragraph paragraph = createdNote.getParagraphs().get(0);
        String paragraphId = paragraph.getId();
        String[] patches = new String[]{ "@@ -0,0 +1,3 @@\n+ABC\n", // ABC
        "@@ -1,3 +1,4 @@\n ABC\n+%0A\n"// press Enter
        , "@@ -1,4 +1,7 @@\n ABC%0A\n+abc\n"// abc
        , "@@ -1,7 +1,45 @@\n ABC\n-%0Aabc\n+ ssss%0Aabc ssss\n"// add text in two string
         };
        int sock1SendCount = 0;
        int sock2SendCount = 0;
        Mockito.reset(sock1);
        Mockito.reset(sock2);
        patchParagraph(sock1, paragraphId, patches[0]);
        Assert.assertEquals("ABC", paragraph.getText());
        Mockito.verify(sock1, Mockito.times(sock1SendCount)).send(ArgumentMatchers.anyString());
        Mockito.verify(sock2, Mockito.times((++sock2SendCount))).send(ArgumentMatchers.anyString());
        patchParagraph(sock2, paragraphId, patches[1]);
        Assert.assertEquals("ABC\n", paragraph.getText());
        Mockito.verify(sock1, Mockito.times((++sock1SendCount))).send(ArgumentMatchers.anyString());
        Mockito.verify(sock2, Mockito.times(sock2SendCount)).send(ArgumentMatchers.anyString());
        patchParagraph(sock1, paragraphId, patches[2]);
        Assert.assertEquals("ABC\nabc", paragraph.getText());
        Mockito.verify(sock1, Mockito.times(sock1SendCount)).send(ArgumentMatchers.anyString());
        Mockito.verify(sock2, Mockito.times((++sock2SendCount))).send(ArgumentMatchers.anyString());
        patchParagraph(sock2, paragraphId, patches[3]);
        Assert.assertEquals("ABC ssss\nabc ssss", paragraph.getText());
        Mockito.verify(sock1, Mockito.times((++sock1SendCount))).send(ArgumentMatchers.anyString());
        Mockito.verify(sock2, Mockito.times(sock2SendCount)).send(ArgumentMatchers.anyString());
        NotebookServerTest.notebook.removeNote(createdNote.getId(), anonymous);
    }

    @Test
    public void testMakeSureNoAngularObjectBroadcastToWebsocketWhoFireTheEvent() throws IOException, InterruptedException {
        // create a notebook
        Note note1 = NotebookServerTest.notebook.createNote("note1", anonymous);
        // get reference to interpreterGroup
        InterpreterGroup interpreterGroup = null;
        List<InterpreterSetting> settings = NotebookServerTest.notebook.getInterpreterSettingManager().getInterpreterSettings(note1.getId());
        for (InterpreterSetting setting : settings) {
            if (setting.getName().equals("md")) {
                interpreterGroup = setting.getOrCreateInterpreterGroup("anonymous", "sharedProcess");
                break;
            }
        }
        // start interpreter process
        Paragraph p1 = note1.addNewParagraph(ANONYMOUS);
        p1.setText("%md start remote interpreter process");
        p1.setAuthenticationInfo(anonymous);
        note1.run(p1.getId());
        // wait for paragraph finished
        while (true) {
            if ((p1.getStatus()) == (Status.FINISHED)) {
                break;
            }
            Thread.sleep(100);
        } 
        // sleep for 1 second to make sure job running thread finish to fire event. See ZEPPELIN-3277
        Thread.sleep(1000);
        // add angularObject
        interpreterGroup.getAngularObjectRegistry().add("object1", "value1", note1.getId(), null);
        // create two sockets and open it
        NotebookSocket sock1 = createWebSocket();
        NotebookSocket sock2 = createWebSocket();
        Assert.assertEquals(sock1, sock1);
        Assert.assertNotEquals(sock1, sock2);
        NotebookServerTest.notebookServer.onOpen(sock1);
        NotebookServerTest.notebookServer.onOpen(sock2);
        Mockito.verify(sock1, Mockito.times(0)).send(ArgumentMatchers.anyString());// getNote, getAngularObject

        // open the same notebook from sockets
        NotebookServerTest.notebookServer.onMessage(sock1, new Message(OP.GET_NOTE).put("id", note1.getId()).toJson());
        NotebookServerTest.notebookServer.onMessage(sock2, new Message(OP.GET_NOTE).put("id", note1.getId()).toJson());
        Mockito.reset(sock1);
        Mockito.reset(sock2);
        // update object from sock1
        NotebookServerTest.notebookServer.onMessage(sock1, put("value", "value1").put("interpreterGroupId", interpreterGroup.getId()).toJson());
        // expect object is broadcasted except for where the update is created
        Mockito.verify(sock1, Mockito.times(0)).send(ArgumentMatchers.anyString());
        Mockito.verify(sock2, Mockito.times(1)).send(ArgumentMatchers.anyString());
        NotebookServerTest.notebook.removeNote(note1.getId(), anonymous);
    }

    @Test
    public void testAngularObjectSaveToNote() throws IOException, InterruptedException {
        // create a notebook
        Note note1 = NotebookServerTest.notebook.createNote("note1", "angular", anonymous);
        // get reference to interpreterGroup
        InterpreterGroup interpreterGroup = null;
        List<InterpreterSetting> settings = NotebookServerTest.notebook.getInterpreterSettingManager().getInterpreterSettings(note1.getId());
        for (InterpreterSetting setting : settings) {
            if (setting.getName().equals("angular")) {
                interpreterGroup = setting.getOrCreateInterpreterGroup("anonymous", "sharedProcess");
                break;
            }
        }
        // start interpreter process
        Paragraph p1 = note1.addNewParagraph(ANONYMOUS);
        p1.setText("%angular <h2>Bind here : {{COMMAND_TYPE}}</h2>");
        p1.setAuthenticationInfo(anonymous);
        note1.run(p1.getId());
        // wait for paragraph finished
        while (true) {
            if ((p1.getStatus()) == (Status.FINISHED)) {
                break;
            }
            Thread.sleep(100);
        } 
        // sleep for 1 second to make sure job running thread finish to fire event. See ZEPPELIN-3277
        Thread.sleep(1000);
        // create two sockets and open it
        NotebookSocket sock1 = createWebSocket();
        NotebookServerTest.notebookServer.onOpen(sock1);
        Mockito.verify(sock1, Mockito.times(0)).send(ArgumentMatchers.anyString());// getNote, getAngularObject

        // open the same notebook from sockets
        NotebookServerTest.notebookServer.onMessage(sock1, new Message(OP.GET_NOTE).put("id", note1.getId()).toJson());
        Mockito.reset(sock1);
        // bind object from sock1
        NotebookServerTest.notebookServer.onMessage(sock1, put("value", "COMMAND_TYPE_VALUE").put("interpreterGroupId", interpreterGroup.getId()).toJson());
        List<AngularObject> list = note1.getAngularObjects("angular-shared_process");
        Assert.assertEquals(list.size(), 1);
        Assert.assertEquals(list.get(0).getNoteId(), note1.getId());
        Assert.assertEquals(list.get(0).getParagraphId(), p1.getId());
        Assert.assertEquals(list.get(0).getName(), "COMMAND_TYPE");
        Assert.assertEquals(list.get(0).get(), "COMMAND_TYPE_VALUE");
        // Check if the interpreterGroup AngularObjectRegistry is updated
        Map<String, Map<String, AngularObject>> mapRegistry = interpreterGroup.getAngularObjectRegistry().getRegistry();
        AngularObject ao = mapRegistry.get((((note1.getId()) + "_") + (p1.getId()))).get("COMMAND_TYPE");
        Assert.assertEquals(ao.getName(), "COMMAND_TYPE");
        Assert.assertEquals(ao.get(), "COMMAND_TYPE_VALUE");
        // update bind object from sock1
        NotebookServerTest.notebookServer.onMessage(sock1, put("value", "COMMAND_TYPE_VALUE_UPDATE").put("interpreterGroupId", interpreterGroup.getId()).toJson());
        list = note1.getAngularObjects("angular-shared_process");
        Assert.assertEquals(list.size(), 1);
        Assert.assertEquals(list.get(0).getNoteId(), note1.getId());
        Assert.assertEquals(list.get(0).getParagraphId(), p1.getId());
        Assert.assertEquals(list.get(0).getName(), "COMMAND_TYPE");
        Assert.assertEquals(list.get(0).get(), "COMMAND_TYPE_VALUE_UPDATE");
        // Check if the interpreterGroup AngularObjectRegistry is updated
        mapRegistry = interpreterGroup.getAngularObjectRegistry().getRegistry();
        AngularObject ao1 = mapRegistry.get((((note1.getId()) + "_") + (p1.getId()))).get("COMMAND_TYPE");
        Assert.assertEquals(ao1.getName(), "COMMAND_TYPE");
        Assert.assertEquals(ao1.get(), "COMMAND_TYPE_VALUE_UPDATE");
        // unbind object from sock1
        NotebookServerTest.notebookServer.onMessage(sock1, put("value", "COMMAND_TYPE_VALUE").put("interpreterGroupId", interpreterGroup.getId()).toJson());
        list = note1.getAngularObjects("angular-shared_process");
        Assert.assertEquals(list.size(), 0);
        // Check if the interpreterGroup AngularObjectRegistry is delete
        mapRegistry = interpreterGroup.getAngularObjectRegistry().getRegistry();
        AngularObject ao2 = mapRegistry.get((((note1.getId()) + "_") + (p1.getId()))).get("COMMAND_TYPE");
        Assert.assertNull(ao2);
        NotebookServerTest.notebook.removeNote(note1.getId(), anonymous);
    }

    @Test
    public void testLoadAngularObjectFromNote() throws IOException, InterruptedException {
        // create a notebook
        Note note1 = NotebookServerTest.notebook.createNote("note1", anonymous);
        // get reference to interpreterGroup
        InterpreterGroup interpreterGroup = null;
        List<InterpreterSetting> settings = NotebookServerTest.notebook.getInterpreterSettingManager().getInterpreterSettings(note1.getId());
        for (InterpreterSetting setting : settings) {
            if (setting.getName().equals("angular")) {
                interpreterGroup = setting.getOrCreateInterpreterGroup("anonymous", "sharedProcess");
                break;
            }
        }
        // start interpreter process
        Paragraph p1 = note1.addNewParagraph(ANONYMOUS);
        p1.setText("%angular <h2>Bind here : {{COMMAND_TYPE}}</h2>");
        p1.setAuthenticationInfo(anonymous);
        note1.run(p1.getId());
        // wait for paragraph finished
        while (true) {
            if ((p1.getStatus()) == (Status.FINISHED)) {
                break;
            }
            Thread.sleep(100);
        } 
        // sleep for 1 second to make sure job running thread finish to fire event. See ZEPPELIN-3277
        Thread.sleep(1000);
        // set note AngularObject
        AngularObject ao = new AngularObject("COMMAND_TYPE", "COMMAND_TYPE_VALUE", note1.getId(), p1.getId(), null);
        note1.addOrUpdateAngularObject("angular-shared_process", ao);
        // create sockets and open it
        NotebookSocket sock1 = createWebSocket();
        NotebookServerTest.notebookServer.onOpen(sock1);
        // Check the AngularObjectRegistry of the interpreterGroup before executing GET_NOTE
        Map<String, Map<String, AngularObject>> mapRegistry1 = interpreterGroup.getAngularObjectRegistry().getRegistry();
        Assert.assertEquals(mapRegistry1.size(), 0);
        // open the notebook from sockets, AngularObjectRegistry that triggers the update of the interpreterGroup
        NotebookServerTest.notebookServer.onMessage(sock1, new Message(OP.GET_NOTE).put("id", note1.getId()).toJson());
        Thread.sleep(1000);
        // After executing GET_NOTE, check the AngularObjectRegistry of the interpreterGroup
        Map<String, Map<String, AngularObject>> mapRegistry2 = interpreterGroup.getAngularObjectRegistry().getRegistry();
        Assert.assertEquals(mapRegistry1.size(), 2);
        AngularObject ao1 = mapRegistry2.get((((note1.getId()) + "_") + (p1.getId()))).get("COMMAND_TYPE");
        Assert.assertEquals(ao1.getName(), "COMMAND_TYPE");
        Assert.assertEquals(ao1.get(), "COMMAND_TYPE_VALUE");
        NotebookServerTest.notebook.removeNote(note1.getId(), anonymous);
    }

    @Test
    public void testImportNotebook() throws IOException {
        String msg = "{\"op\":\"IMPORT_NOTE\",\"data\":" + ((((("{\"note\":{\"paragraphs\": [{\"text\": \"Test " + "paragraphs import\",") + "\"progressUpdateIntervalMs\":500,") + "\"config\":{},\"settings\":{}}],") + "\"name\": \"Test Zeppelin notebook import\",\"config\": ") + "{}}}}");
        Message messageReceived = NotebookServerTest.notebookServer.deserializeMessage(msg);
        Note note = null;
        try {
            note = NotebookServerTest.notebookServer.importNote(null, messageReceived);
        } catch (NullPointerException e) {
            // broadcastNoteList(); failed nothing to worry.
            AbstractTestRestApi.LOG.error(("Exception in NotebookServerTest while testImportNotebook, failed nothing to " + "worry "), e);
        }
        Assert.assertNotEquals(null, NotebookServerTest.notebook.getNote(note.getId()));
        Assert.assertEquals("Test Zeppelin notebook import", NotebookServerTest.notebook.getNote(note.getId()).getName());
        Assert.assertEquals("Test paragraphs import", NotebookServerTest.notebook.getNote(note.getId()).getParagraphs().get(0).getText());
        NotebookServerTest.notebook.removeNote(note.getId(), anonymous);
    }

    @Test
    public void bindAngularObjectToRemoteForParagraphs() throws Exception {
        // Given
        final String varName = "name";
        final String value = "DuyHai DOAN";
        final Message messageReceived = put("value", value).put("paragraphId", "paragraphId");
        final Notebook notebook = Mockito.mock(Notebook.class);
        final NotebookServer server = new NotebookServer();
        server.setNotebook(() -> notebook);
        server.setNotebookService(() -> NotebookServerTest.notebookService);
        final Note note = Mockito.mock(Note.class, Mockito.RETURNS_DEEP_STUBS);
        Mockito.when(notebook.getNote("noteId")).thenReturn(note);
        final Paragraph paragraph = Mockito.mock(Paragraph.class, Mockito.RETURNS_DEEP_STUBS);
        Mockito.when(note.getParagraph("paragraphId")).thenReturn(paragraph);
        final RemoteAngularObjectRegistry mdRegistry = Mockito.mock(RemoteAngularObjectRegistry.class);
        final InterpreterGroup mdGroup = new InterpreterGroup("mdGroup");
        mdGroup.setAngularObjectRegistry(mdRegistry);
        Mockito.when(paragraph.getBindedInterpreter().getInterpreterGroup()).thenReturn(mdGroup);
        final AngularObject<String> ao1 = AngularObjectBuilder.build(varName, value, "noteId", "paragraphId");
        Mockito.when(mdRegistry.addAndNotifyRemoteProcess(varName, value, "noteId", "paragraphId")).thenReturn(ao1);
        NotebookSocket conn = Mockito.mock(NotebookSocket.class);
        NotebookSocket otherConn = Mockito.mock(NotebookSocket.class);
        final String mdMsg1 = server.serializeMessage(put("paragraphId", "paragraphId"));
        server.getConnectionManager().noteSocketMap.put("noteId", Arrays.asList(conn, otherConn));
        // When
        server.angularObjectClientBind(conn, messageReceived);
        // Then
        Mockito.verify(mdRegistry, Mockito.never()).addAndNotifyRemoteProcess(varName, value, "noteId", null);
        Mockito.verify(otherConn).send(mdMsg1);
    }

    @Test
    public void unbindAngularObjectFromRemoteForParagraphs() throws Exception {
        // Given
        final String varName = "name";
        final String value = "val";
        final Message messageReceived = put("name", varName).put("paragraphId", "paragraphId");
        final Notebook notebook = Mockito.mock(Notebook.class);
        final NotebookServer server = new NotebookServer();
        server.setNotebook(() -> notebook);
        server.setNotebookService(() -> NotebookServerTest.notebookService);
        final Note note = Mockito.mock(Note.class, Mockito.RETURNS_DEEP_STUBS);
        Mockito.when(notebook.getNote("noteId")).thenReturn(note);
        final Paragraph paragraph = Mockito.mock(Paragraph.class, Mockito.RETURNS_DEEP_STUBS);
        Mockito.when(note.getParagraph("paragraphId")).thenReturn(paragraph);
        final RemoteAngularObjectRegistry mdRegistry = Mockito.mock(RemoteAngularObjectRegistry.class);
        final InterpreterGroup mdGroup = new InterpreterGroup("mdGroup");
        mdGroup.setAngularObjectRegistry(mdRegistry);
        Mockito.when(paragraph.getBindedInterpreter().getInterpreterGroup()).thenReturn(mdGroup);
        final AngularObject<String> ao1 = AngularObjectBuilder.build(varName, value, "noteId", "paragraphId");
        Mockito.when(mdRegistry.removeAndNotifyRemoteProcess(varName, "noteId", "paragraphId")).thenReturn(ao1);
        NotebookSocket conn = Mockito.mock(NotebookSocket.class);
        NotebookSocket otherConn = Mockito.mock(NotebookSocket.class);
        final String mdMsg1 = server.serializeMessage(put("paragraphId", "paragraphId"));
        server.getConnectionManager().noteSocketMap.put("noteId", Arrays.asList(conn, otherConn));
        // When
        server.angularObjectClientUnbind(conn, messageReceived);
        // Then
        Mockito.verify(mdRegistry, Mockito.never()).removeAndNotifyRemoteProcess(varName, "noteId", null);
        Mockito.verify(otherConn).send(mdMsg1);
    }

    @Test
    public void testCreateNoteWithDefaultInterpreterId() throws IOException {
        // create two sockets and open it
        NotebookSocket sock1 = createWebSocket();
        NotebookSocket sock2 = createWebSocket();
        Assert.assertEquals(sock1, sock1);
        Assert.assertNotEquals(sock1, sock2);
        NotebookServerTest.notebookServer.onOpen(sock1);
        NotebookServerTest.notebookServer.onOpen(sock2);
        String noteName = "Note with millis " + (System.currentTimeMillis());
        String defaultInterpreterId = "";
        List<InterpreterSetting> settings = NotebookServerTest.notebook.getInterpreterSettingManager().get();
        if ((settings.size()) > 1) {
            defaultInterpreterId = settings.get(0).getId();
        }
        // create note from sock1
        NotebookServerTest.notebookServer.onMessage(sock1, put("defaultInterpreterId", defaultInterpreterId).toJson());
        int sendCount = 2;
        if (ZeppelinConfiguration.create().isZeppelinNotebookCollaborativeModeEnable()) {
            sendCount++;
        }
        // expect the events are broadcasted properly
        Mockito.verify(sock1, Mockito.times(sendCount)).send(ArgumentMatchers.anyString());
        Note createdNote = null;
        for (Note note : NotebookServerTest.notebook.getAllNotes()) {
            if (note.getName().equals(noteName)) {
                createdNote = note;
                break;
            }
        }
        if ((settings.size()) > 1) {
            Assert.assertEquals(NotebookServerTest.notebook.getInterpreterSettingManager().getDefaultInterpreterSetting(createdNote.getId()).getId(), defaultInterpreterId);
        }
        NotebookServerTest.notebook.removeNote(createdNote.getId(), anonymous);
    }

    @Test
    public void testRuntimeInfos() {
        // mock note
        String msg = "{\"op\":\"IMPORT_NOTE\",\"data\":" + ((((("{\"note\":{\"paragraphs\": [{\"text\": \"Test " + "paragraphs import\",") + "\"progressUpdateIntervalMs\":500,") + "\"config\":{},\"settings\":{}}],") + "\"name\": \"Test Zeppelin notebook import\",\"config\": ") + "{}}}}");
        Message messageReceived = NotebookServerTest.notebookServer.deserializeMessage(msg);
        Note note = null;
        try {
            note = NotebookServerTest.notebookServer.importNote(null, messageReceived);
        } catch (NullPointerException e) {
            // broadcastNoteList(); failed nothing to worry.
            AbstractTestRestApi.LOG.error(("Exception in NotebookServerTest while testImportNotebook, failed nothing to " + "worry "), e);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Assert.assertNotEquals(null, NotebookServerTest.notebook.getNote(note.getId()));
        Assert.assertNotEquals(null, note.getParagraph(0));
        String nodeId = note.getId();
        String paragraphId = note.getParagraph(0).getId();
        // update RuntimeInfos
        Map<String, String> infos = new HashMap<>();
        infos.put("jobUrl", "jobUrl_value");
        infos.put("jobLabel", "jobLabel_value");
        infos.put("label", "SPARK JOB");
        infos.put("tooltip", "View in Spark web UI");
        infos.put("noteId", nodeId);
        infos.put("paraId", paragraphId);
        NotebookServerTest.notebookServer.onParaInfosReceived(nodeId, paragraphId, "spark", infos);
        Paragraph paragraph = note.getParagraph(paragraphId);
        // check RuntimeInfos
        Assert.assertTrue(paragraph.getRuntimeInfos().containsKey("jobUrl"));
        List<Map<String, String>> list = paragraph.getRuntimeInfos().get("jobUrl").getValue();
        Assert.assertEquals(1, list.size());
        Assert.assertEquals(2, list.get(0).size());
        Assert.assertEquals(list.get(0).get("jobUrl"), "jobUrl_value");
        Assert.assertEquals(list.get(0).get("jobLabel"), "jobLabel_value");
    }

    @Test
    public void testGetParagraphList() {
        Note note = null;
        try {
            note = NotebookServerTest.notebook.createNote("note1", anonymous);
            Paragraph p1 = note.addNewParagraph(anonymous);
            p1.setText("%md start remote interpreter process");
            p1.setAuthenticationInfo(anonymous);
            NotebookServerTest.notebookServer.getNotebook().saveNote(note, anonymous);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String noteId = note.getId();
        String user1Id = "user1";
        String user2Id = "user2";
        NotebookAuthorization notebookAuthorization = NotebookAuthorization.getInstance();
        // test user1 can get anonymous's note
        List<ParagraphInfo> paragraphList0 = null;
        try {
            paragraphList0 = NotebookServerTest.notebookServer.getParagraphList(user1Id, noteId);
        } catch (ServiceException e) {
            e.printStackTrace();
        } catch (TException e) {
            e.printStackTrace();
        }
        Assert.assertNotNull((user1Id + " can get anonymous's note"), paragraphList0);
        // test user1 cannot get user2's note
        notebookAuthorization.setOwners(noteId, new HashSet(Arrays.asList(user2Id)));
        notebookAuthorization.setReaders(noteId, new HashSet(Arrays.asList(user2Id)));
        notebookAuthorization.setRunners(noteId, new HashSet(Arrays.asList(user2Id)));
        notebookAuthorization.setWriters(noteId, new HashSet(Arrays.asList(user2Id)));
        List<ParagraphInfo> paragraphList1 = null;
        try {
            paragraphList1 = NotebookServerTest.notebookServer.getParagraphList(user1Id, noteId);
        } catch (ServiceException e) {
            e.printStackTrace();
        } catch (TException e) {
            e.printStackTrace();
        }
        Assert.assertNull((((user1Id + " cannot get ") + user2Id) + "'s note"), paragraphList1);
        // test user1 can get user2's shared note
        notebookAuthorization.setOwners(noteId, new HashSet(Arrays.asList(user2Id)));
        notebookAuthorization.setReaders(noteId, new HashSet(Arrays.asList(user1Id, user2Id)));
        notebookAuthorization.setRunners(noteId, new HashSet(Arrays.asList(user2Id)));
        notebookAuthorization.setWriters(noteId, new HashSet(Arrays.asList(user2Id)));
        List<ParagraphInfo> paragraphList2 = null;
        try {
            paragraphList2 = NotebookServerTest.notebookServer.getParagraphList(user1Id, noteId);
        } catch (ServiceException e) {
            e.printStackTrace();
        } catch (TException e) {
            e.printStackTrace();
        }
        Assert.assertNotNull((((user1Id + " can get ") + user2Id) + "'s shared note"), paragraphList2);
    }
}

