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


import Code.SUCCESS;
import FormType.NONE;
import Status.READY;
import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.zeppelin.display.AngularObject;
import org.apache.zeppelin.display.AngularObjectBuilder;
import org.apache.zeppelin.display.AngularObjectRegistry;
import org.apache.zeppelin.display.Input;
import org.apache.zeppelin.interpreter.AbstractInterpreterTest;
import org.apache.zeppelin.interpreter.InterpreterContext;
import org.apache.zeppelin.interpreter.InterpreterOption;
import org.apache.zeppelin.interpreter.InterpreterResult;
import org.apache.zeppelin.interpreter.InterpreterResult.Code;
import org.apache.zeppelin.interpreter.InterpreterResult.Type;
import org.apache.zeppelin.resource.ResourcePool;
import org.apache.zeppelin.user.AuthenticationInfo;
import org.apache.zeppelin.user.Credentials;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class ParagraphTest extends AbstractInterpreterTest {
    @Test
    public void scriptBodyWithReplName() {
        Note note = createNote();
        Paragraph paragraph = new Paragraph(note, null);
        paragraph.setText("%test(1234567");
        Assert.assertEquals("test", paragraph.getIntpText());
        Assert.assertEquals("(1234567", paragraph.getScriptText());
        paragraph.setText("%test 1234567");
        Assert.assertEquals("test", paragraph.getIntpText());
        Assert.assertEquals("1234567", paragraph.getScriptText());
    }

    @Test
    public void scriptBodyWithoutReplName() {
        Note note = createNote();
        Paragraph paragraph = new Paragraph(note, null);
        paragraph.setText("1234567");
        Assert.assertEquals("", paragraph.getIntpText());
        Assert.assertEquals("1234567", paragraph.getScriptText());
    }

    @Test
    public void replNameAndNoBody() {
        Note note = createNote();
        Paragraph paragraph = new Paragraph(note, null);
        paragraph.setText("%test");
        Assert.assertEquals("test", paragraph.getIntpText());
        Assert.assertEquals("", paragraph.getScriptText());
    }

    @Test
    public void replSingleCharName() {
        Note note = createNote();
        Paragraph paragraph = new Paragraph(note, null);
        paragraph.setText("%r a");
        Assert.assertEquals("r", paragraph.getIntpText());
        Assert.assertEquals("a", paragraph.getScriptText());
    }

    @Test
    public void testParagraphProperties() {
        Note note = createNote();
        Paragraph paragraph = new Paragraph(note, null);
        paragraph.setText("%test(p1=v1,p2=v2) a");
        Assert.assertEquals("test", paragraph.getIntpText());
        Assert.assertEquals("a", paragraph.getScriptText());
        Assert.assertEquals(2, paragraph.getLocalProperties().size());
        Assert.assertEquals("v1", paragraph.getLocalProperties().get("p1"));
        Assert.assertEquals("v2", paragraph.getLocalProperties().get("p2"));
        // properties with space
        paragraph.setText("%test(p1=v1,  p2=v2) a");
        Assert.assertEquals("test", paragraph.getIntpText());
        Assert.assertEquals("a", paragraph.getScriptText());
        Assert.assertEquals(2, paragraph.getLocalProperties().size());
        Assert.assertEquals("v1", paragraph.getLocalProperties().get("p1"));
        Assert.assertEquals("v2", paragraph.getLocalProperties().get("p2"));
        // empty properties
        paragraph.setText("%test() a");
        Assert.assertEquals("test", paragraph.getIntpText());
        Assert.assertEquals("a", paragraph.getScriptText());
        Assert.assertEquals(0, paragraph.getLocalProperties().size());
    }

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Test
    public void testInvalidProperties() {
        expectedEx.expect(RuntimeException.class);
        expectedEx.expectMessage("Invalid paragraph properties format");
        Note note = createNote();
        Paragraph paragraph = new Paragraph(note, null);
        paragraph.setText("%test(p1=v1=v2) a");
    }

    @Test
    public void replInvalid() {
        Note note = createNote();
        Paragraph paragraph = new Paragraph(note, null);
        paragraph.setText("foo %r");
        Assert.assertEquals("", paragraph.getIntpText());
        Assert.assertEquals("foo %r", paragraph.getScriptText());
        paragraph.setText("foo%r");
        Assert.assertEquals("", paragraph.getIntpText());
        Assert.assertEquals("foo%r", paragraph.getScriptText());
        paragraph.setText("% foo");
        Assert.assertEquals("", paragraph.getIntpText());
        Assert.assertEquals("% foo", paragraph.getScriptText());
    }

    @Test
    public void replNameEndsWithWhitespace() {
        Note note = createNote();
        Paragraph paragraph = new Paragraph(note, null);
        paragraph.setText("%test\r\n###Hello");
        Assert.assertEquals("test", paragraph.getIntpText());
        Assert.assertEquals("###Hello", paragraph.getScriptText());
        paragraph.setText("%test\t###Hello");
        Assert.assertEquals("test", paragraph.getIntpText());
        Assert.assertEquals("###Hello", paragraph.getScriptText());
        paragraph.setText("%test\u000b###Hello");
        Assert.assertEquals("test", paragraph.getIntpText());
        Assert.assertEquals("###Hello", paragraph.getScriptText());
        paragraph.setText("%test\f###Hello");
        Assert.assertEquals("test", paragraph.getIntpText());
        Assert.assertEquals("###Hello", paragraph.getScriptText());
        paragraph.setText("%test\n###Hello");
        Assert.assertEquals("test", paragraph.getIntpText());
        Assert.assertEquals("###Hello", paragraph.getScriptText());
        paragraph.setText("%test ###Hello");
        Assert.assertEquals("test", paragraph.getIntpText());
        Assert.assertEquals("###Hello", paragraph.getScriptText());
        paragraph.setText(" %test ###Hello");
        Assert.assertEquals("test", paragraph.getIntpText());
        Assert.assertEquals("###Hello", paragraph.getScriptText());
        paragraph.setText("\n\r%test ###Hello");
        Assert.assertEquals("test", paragraph.getIntpText());
        Assert.assertEquals("###Hello", paragraph.getScriptText());
        paragraph.setText("%\r\n###Hello");
        Assert.assertEquals("", paragraph.getIntpText());
        Assert.assertEquals("%\r\n###Hello", paragraph.getScriptText());
    }

    @Test
    public void should_extract_variable_from_angular_object_registry() throws Exception {
        // Given
        final String noteId = "noteId";
        final AngularObjectRegistry registry = Mockito.mock(AngularObjectRegistry.class);
        final Note note = Mockito.mock(org.apache.zeppelin.interpreter.Note.class);
        final Map<String, Input> inputs = new HashMap<>();
        inputs.put("name", null);
        inputs.put("age", null);
        inputs.put("job", null);
        final String scriptBody = "My name is ${name} and I am ${age=20} years old. " + "My occupation is ${ job = engineer | developer | artists}";
        final Paragraph paragraph = new Paragraph(note, null);
        final String paragraphId = paragraph.getId();
        final AngularObject nameAO = AngularObjectBuilder.build("name", "DuyHai DOAN", noteId, paragraphId);
        final AngularObject ageAO = AngularObjectBuilder.build("age", 34, noteId, null);
        Mockito.when(note.getId()).thenReturn(noteId);
        Mockito.when(registry.get("name", noteId, paragraphId)).thenReturn(nameAO);
        Mockito.when(registry.get("age", noteId, null)).thenReturn(ageAO);
        final String expected = "My name is DuyHai DOAN and I am 34 years old. " + "My occupation is ${ job = engineer | developer | artists}";
        // When
        final String actual = paragraph.extractVariablesFromAngularRegistry(scriptBody, inputs, registry);
        // Then
        Mockito.verify(registry).get("name", noteId, paragraphId);
        Mockito.verify(registry).get("age", noteId, null);
        Assert.assertEquals(actual, expected);
    }

    @Test
    public void returnDefaultParagraphWithNewUser() {
        Paragraph p = new Paragraph("para_1", null, null);
        String defaultValue = "Default Value";
        p.setResult(new InterpreterResult(Code.SUCCESS, defaultValue));
        Paragraph newUserParagraph = p.getUserParagraph("new_user");
        Assert.assertNotNull(newUserParagraph);
        Assert.assertEquals(defaultValue, newUserParagraph.getReturn().message().get(0).getData());
    }

    @Test
    public void returnUnchangedResultsWithDifferentUser() throws Throwable {
        Note mockNote = Mockito.mock(org.apache.zeppelin.interpreter.Note.class);
        Mockito.when(mockNote.getCredentials()).thenReturn(Mockito.mock(Credentials.class));
        Paragraph spyParagraph = Mockito.spy(new Paragraph("para_1", mockNote, null));
        Interpreter mockInterpreter = Mockito.mock(org.apache.zeppelin.interpreter.Interpreter.class);
        spyParagraph.setInterpreter(mockInterpreter);
        Mockito.doReturn(mockInterpreter).when(spyParagraph).getBindedInterpreter();
        ManagedInterpreterGroup mockInterpreterGroup = Mockito.mock(org.apache.zeppelin.interpreter.ManagedInterpreterGroup.class);
        Mockito.when(mockInterpreter.getInterpreterGroup()).thenReturn(mockInterpreterGroup);
        Mockito.when(mockInterpreterGroup.getId()).thenReturn("mock_id_1");
        Mockito.when(mockInterpreterGroup.getAngularObjectRegistry()).thenReturn(Mockito.mock(AngularObjectRegistry.class));
        Mockito.when(mockInterpreterGroup.getResourcePool()).thenReturn(Mockito.mock(ResourcePool.class));
        List<InterpreterSetting> spyInterpreterSettingList = Mockito.spy(Lists.<InterpreterSetting>newArrayList());
        InterpreterSetting mockInterpreterSetting = Mockito.mock(org.apache.zeppelin.interpreter.InterpreterSetting.class);
        Mockito.when(mockInterpreterGroup.getInterpreterSetting()).thenReturn(mockInterpreterSetting);
        InterpreterOption mockInterpreterOption = Mockito.mock(InterpreterOption.class);
        Mockito.when(mockInterpreterSetting.getOption()).thenReturn(mockInterpreterOption);
        Mockito.when(mockInterpreterOption.permissionIsSet()).thenReturn(false);
        Mockito.when(mockInterpreterSetting.getStatus()).thenReturn(READY);
        Mockito.when(mockInterpreterSetting.getId()).thenReturn("mock_id_1");
        Mockito.when(mockInterpreterSetting.getOrCreateInterpreterGroup(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(mockInterpreterGroup);
        Mockito.when(mockInterpreterSetting.isUserAuthorized(ArgumentMatchers.any(List.class))).thenReturn(true);
        spyInterpreterSettingList.add(mockInterpreterSetting);
        Mockito.when(mockNote.getId()).thenReturn("any_id");
        Mockito.when(mockInterpreter.getFormType()).thenReturn(NONE);
        ParagraphJobListener mockJobListener = Mockito.mock(ParagraphJobListener.class);
        Mockito.doReturn(mockJobListener).when(spyParagraph).getListener();
        Mockito.doNothing().when(mockJobListener).onOutputUpdateAll(Mockito.<Paragraph>any(), Mockito.anyList());
        InterpreterResult mockInterpreterResult = Mockito.mock(InterpreterResult.class);
        Mockito.when(mockInterpreter.interpret(ArgumentMatchers.anyString(), Mockito.<InterpreterContext>any())).thenReturn(mockInterpreterResult);
        Mockito.when(mockInterpreterResult.code()).thenReturn(SUCCESS);
        // Actual test
        List<InterpreterResultMessage> result1 = Lists.newArrayList();
        result1.add(new InterpreterResultMessage(Type.TEXT, "result1"));
        Mockito.when(mockInterpreterResult.message()).thenReturn(result1);
        AuthenticationInfo user1 = new AuthenticationInfo("user1");
        spyParagraph.setAuthenticationInfo(user1);
        spyParagraph.jobRun();
        Paragraph p1 = spyParagraph.getUserParagraph(user1.getUser());
        List<InterpreterResultMessage> result2 = Lists.newArrayList();
        result2.add(new InterpreterResultMessage(Type.TEXT, "result2"));
        Mockito.when(mockInterpreterResult.message()).thenReturn(result2);
        AuthenticationInfo user2 = new AuthenticationInfo("user2");
        spyParagraph.setAuthenticationInfo(user2);
        spyParagraph.jobRun();
        Paragraph p2 = spyParagraph.getUserParagraph(user2.getUser());
        Assert.assertNotEquals(p1.getReturn().toString(), p2.getReturn().toString());
        Assert.assertEquals(p1, spyParagraph.getUserParagraph(user1.getUser()));
    }

    @Test
    public void testCursorPosition() {
        Paragraph paragraph = Mockito.spy(new Paragraph());
        // left = buffer, middle = cursor position into source code, right = cursor position after parse
        List<Triple<String, Integer, Integer>> dataSet = Arrays.asList(Triple.of("%jdbc schema.", 13, 7), Triple.of("   %jdbc schema.", 16, 7), Triple.of(" \n%jdbc schema.", 15, 7), Triple.of("%jdbc schema.table.  ", 19, 13), Triple.of("%jdbc schema.\n\n", 13, 7), Triple.of("  %jdbc schema.tab\n\n", 18, 10), Triple.of("  \n%jdbc schema.\n \n", 16, 7), Triple.of("  \n%jdbc schema.\n \n", 16, 7), Triple.of("  \n%jdbc\n\n schema\n \n", 17, 6), Triple.of("%another\n\n schema.", 18, 7), Triple.of("\n\n schema.", 10, 7), Triple.of("schema.", 7, 7), Triple.of("schema. \n", 7, 7), Triple.of("  \n   %jdbc", 11, 0), Triple.of("\n   %jdbc", 9, 0), Triple.of("%jdbc  \n  schema", 16, 6), Triple.of("%jdbc  \n  \n   schema", 20, 6));
        for (Triple<String, Integer, Integer> data : dataSet) {
            paragraph.setText(data.getLeft());
            Integer actual = paragraph.calculateCursorPosition(data.getLeft(), data.getMiddle());
            Assert.assertEquals(data.getRight(), actual);
        }
    }
}

