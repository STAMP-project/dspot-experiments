/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2017 by Hitachi Vantara : http://www.pentaho.com
 *
 * ******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ****************************************************************************
 */
package org.pentaho.di.trans.steps.mailinput;


import java.util.Date;
import javax.mail.Message;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.RowDataUtil;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.steps.mailinput.MailInput.MessageParser;
import org.pentaho.di.trans.steps.mock.StepMockHelper;

import static MailInputField.COLUMN_ATTACHED_FILES_COUNT;
import static MailInputField.COLUMN_BODY;
import static MailInputField.COLUMN_BODY_CONTENT_TYPE;
import static MailInputField.COLUMN_CONTENT_TYPE;
import static MailInputField.COLUMN_DESCRIPTION;
import static MailInputField.COLUMN_FOLDER_NAME;
import static MailInputField.COLUMN_HEADER;
import static MailInputField.COLUMN_MESSAGE_NR;
import static MailInputField.COLUMN_RECEIVED_DATE;
import static MailInputField.COLUMN_RECIPIENTS;
import static MailInputField.COLUMN_REPLY_TO;
import static MailInputField.COLUMN_SENDER;
import static MailInputField.COLUMN_SENT_DATE;
import static MailInputField.COLUMN_SIZE;
import static MailInputField.COLUMN_SUBJECT;


public class ParseMailInputTest {
    // mock is existed per-class instance loaded by junit loader
    private static StepMockHelper<MailInputMeta, StepDataInterface> stepMockHelper;

    // test data
    public static final int MSG_NUMB = 3;

    public static final String MSG_BODY = "msg_body";

    public static final String FLD_NAME = "junit_folder";

    public static final int ATTCH_COUNT = 3;

    public static final String CNTNT_TYPE = "text/html";

    public static final String FROM1 = "localhost_1";

    public static final String FROM2 = "localhost_2";

    public static final String REP1 = "127.0.0.1";

    public static final String REP2 = "127.0.0.2";

    public static final String REC1 = "Vasily";

    public static final String REC2 = "Pupkin";

    public static final String SUBJ = "mocktest";

    public static final String DESC = "desc";

    public static final Date DATE1 = new Date(0);

    public static final Date DATE2 = new Date(60000);

    public static final String CNTNT_TYPE_EMAIL = "application/acad";

    public static int CNTNT_SIZE = 23;

    public static String HDR_EX1 = "header_ex1";

    public static String HDR_EX1V = "header_ex1_value";

    public static String HDR_EX2 = "header_ex2";

    public static String HDR_EX2V = "header_ex2_value";

    // this objects re-created for every test method
    private Message message;

    private MailInputData data;

    private MailInputMeta meta;

    private MailInput mailInput;

    /**
     * [PDI-6532] When mail header is found returns his actual value.
     *
     * @throws Exception
     * 		
     * @throws KettleException
     * 		
     */
    @Test
    public void testHeadersParsedPositive() throws Exception {
        // add expected fields:
        int[] fields = new int[]{ COLUMN_HEADER };
        MailInputField[] farr = this.getDefaultInputFields(fields);
        // points to existed header
        farr[0].setName(ParseMailInputTest.HDR_EX1);
        this.mockMailInputMeta(farr);
        try {
            mailInput.processRow(meta, data);
        } catch (KettleException e) {
            // don't worry about it
        }
        MessageParser underTest = mailInput.new MessageParser();
        Object[] r = RowDataUtil.allocateRowData(data.nrFields);
        underTest.parseToArray(r, message);
        Assert.assertEquals("Header is correct", ParseMailInputTest.HDR_EX1V, String.class.cast(r[0]));
    }

    /**
     * [PDI-6532] When mail header is not found returns empty String
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testHeadersParsedNegative() throws Exception {
        int[] fields = new int[]{ COLUMN_HEADER };
        MailInputField[] farr = this.getDefaultInputFields(fields);
        farr[0].setName(((ParseMailInputTest.HDR_EX1) + "salt"));
        this.mockMailInputMeta(farr);
        try {
            mailInput.processRow(meta, data);
        } catch (KettleException e) {
            // don't worry about it
        }
        MessageParser underTest = mailInput.new MessageParser();
        Object[] r = RowDataUtil.allocateRowData(data.nrFields);
        underTest.parseToArray(r, message);
        Assert.assertEquals("Header is correct", "", String.class.cast(r[0]));
    }

    /**
     * Test, message number can be parsed correctly
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testMessageNumberIsParsed() throws Exception {
        int[] fields = new int[]{ COLUMN_MESSAGE_NR };
        MailInputField[] farr = this.getDefaultInputFields(fields);
        this.mockMailInputMeta(farr);
        try {
            mailInput.processRow(meta, data);
        } catch (KettleException e) {
            // don't worry about it
        }
        MessageParser underTest = mailInput.new MessageParser();
        Object[] r = RowDataUtil.allocateRowData(data.nrFields);
        underTest.parseToArray(r, message);
        Assert.assertEquals("Message number is correct", new Long(ParseMailInputTest.MSG_NUMB), Long.class.cast(r[0]));
    }

    /**
     * Test message subject can be parsed
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testMessageSubjectIsParsed() throws Exception {
        int[] fields = new int[]{ COLUMN_SUBJECT };
        MailInputField[] farr = this.getDefaultInputFields(fields);
        this.mockMailInputMeta(farr);
        try {
            mailInput.processRow(meta, data);
        } catch (KettleException e) {
            // don't worry about it
        }
        MessageParser underTest = mailInput.new MessageParser();
        Object[] r = RowDataUtil.allocateRowData(data.nrFields);
        underTest.parseToArray(r, message);
        Assert.assertEquals("Message subject is correct", ParseMailInputTest.SUBJ, String.class.cast(r[0]));
    }

    /**
     * Test message From can be parsed correctly
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testMessageFromIsParsed() throws Exception {
        int[] fields = new int[]{ COLUMN_SENDER };
        MailInputField[] farr = this.getDefaultInputFields(fields);
        this.mockMailInputMeta(farr);
        try {
            mailInput.processRow(meta, data);
        } catch (KettleException e) {
            // don't worry about it
        }
        MessageParser underTest = mailInput.new MessageParser();
        Object[] r = RowDataUtil.allocateRowData(data.nrFields);
        underTest.parseToArray(r, message);
        // expect, that from is concatenated with ';'
        String expected = StringUtils.join(new String[]{ ParseMailInputTest.FROM1, ParseMailInputTest.FROM2 }, ";");
        Assert.assertEquals("Message From is correct", expected, String.class.cast(r[0]));
    }

    /**
     * Test message ReplayTo can be parsed correctly
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testMessageReplayToIsParsed() throws Exception {
        int[] fields = new int[]{ COLUMN_REPLY_TO };
        MailInputField[] farr = this.getDefaultInputFields(fields);
        this.mockMailInputMeta(farr);
        try {
            mailInput.processRow(meta, data);
        } catch (KettleException e) {
            // don't worry about it
        }
        MessageParser underTest = mailInput.new MessageParser();
        Object[] r = RowDataUtil.allocateRowData(data.nrFields);
        underTest.parseToArray(r, message);
        // is concatenated with ';'
        String expected = StringUtils.join(new String[]{ ParseMailInputTest.REP1, ParseMailInputTest.REP2 }, ";");
        Assert.assertEquals("Message ReplayTo is correct", expected, String.class.cast(r[0]));
    }

    /**
     * Test message recipients can be parsed
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testMessageRecipientsIsParsed() throws Exception {
        int[] fields = new int[]{ COLUMN_RECIPIENTS };
        MailInputField[] farr = this.getDefaultInputFields(fields);
        this.mockMailInputMeta(farr);
        try {
            mailInput.processRow(meta, data);
        } catch (KettleException e) {
            // don't worry about it
        }
        MessageParser underTest = mailInput.new MessageParser();
        Object[] r = RowDataUtil.allocateRowData(data.nrFields);
        underTest.parseToArray(r, message);
        // is concatenated with ';'
        String expected = StringUtils.join(new String[]{ ParseMailInputTest.REC1, ParseMailInputTest.REC2 }, ";");
        Assert.assertEquals("Message Recipients is correct", expected, String.class.cast(r[0]));
    }

    /**
     * Test message description is correct
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testMessageDescriptionIsParsed() throws Exception {
        int[] fields = new int[]{ COLUMN_DESCRIPTION };
        MailInputField[] farr = this.getDefaultInputFields(fields);
        this.mockMailInputMeta(farr);
        try {
            mailInput.processRow(meta, data);
        } catch (KettleException e) {
            // don't worry about it
        }
        MessageParser underTest = mailInput.new MessageParser();
        Object[] r = RowDataUtil.allocateRowData(data.nrFields);
        underTest.parseToArray(r, message);
        Assert.assertEquals("Message Description is correct", ParseMailInputTest.DESC, String.class.cast(r[0]));
    }

    /**
     * Test message received date is correct
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testMessageRecivedDateIsParsed() throws Exception {
        int[] fields = new int[]{ COLUMN_RECEIVED_DATE };
        MailInputField[] farr = this.getDefaultInputFields(fields);
        this.mockMailInputMeta(farr);
        try {
            mailInput.processRow(meta, data);
        } catch (KettleException e) {
            // don't worry about it
        }
        MessageParser underTest = mailInput.new MessageParser();
        Object[] r = RowDataUtil.allocateRowData(data.nrFields);
        underTest.parseToArray(r, message);
        Assert.assertEquals("Message Recived date is correct", ParseMailInputTest.DATE1, Date.class.cast(r[0]));
    }

    /**
     * Test message sent date is correct
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testMessageSentDateIsParsed() throws Exception {
        int[] fields = new int[]{ COLUMN_SENT_DATE };
        MailInputField[] farr = this.getDefaultInputFields(fields);
        this.mockMailInputMeta(farr);
        try {
            mailInput.processRow(meta, data);
        } catch (KettleException e) {
            // don't worry about it
        }
        MessageParser underTest = mailInput.new MessageParser();
        Object[] r = RowDataUtil.allocateRowData(data.nrFields);
        underTest.parseToArray(r, message);
        Assert.assertEquals("Message Sent date is correct", ParseMailInputTest.DATE2, Date.class.cast(r[0]));
    }

    /**
     * Message content type is correct
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testMessageContentTypeIsParsed() throws Exception {
        int[] fields = new int[]{ COLUMN_CONTENT_TYPE };
        MailInputField[] farr = this.getDefaultInputFields(fields);
        this.mockMailInputMeta(farr);
        try {
            mailInput.processRow(meta, data);
        } catch (KettleException e) {
            // don't worry about it
        }
        MessageParser underTest = mailInput.new MessageParser();
        Object[] r = RowDataUtil.allocateRowData(data.nrFields);
        underTest.parseToArray(r, message);
        Assert.assertEquals("Message Content type is correct", ParseMailInputTest.CNTNT_TYPE_EMAIL, String.class.cast(r[0]));
    }

    /**
     * Test message size is correct
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testMessageSizeIsParsed() throws Exception {
        int[] fields = new int[]{ COLUMN_SIZE };
        MailInputField[] farr = this.getDefaultInputFields(fields);
        this.mockMailInputMeta(farr);
        try {
            mailInput.processRow(meta, data);
        } catch (KettleException e) {
            // don't worry about it
        }
        MessageParser underTest = mailInput.new MessageParser();
        Object[] r = RowDataUtil.allocateRowData(data.nrFields);
        underTest.parseToArray(r, message);
        Assert.assertEquals("Message Size is correct", new Long(ParseMailInputTest.CNTNT_SIZE), Long.class.cast(r[0]));
    }

    /**
     * Test that message body can be parsed correctly
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testMessageBodyIsParsed() throws Exception {
        int[] fields = new int[]{ COLUMN_BODY };
        MailInputField[] farr = this.getDefaultInputFields(fields);
        this.mockMailInputMeta(farr);
        try {
            mailInput.processRow(meta, data);
        } catch (KettleException e) {
            // don't worry about it
        }
        MessageParser underTest = mailInput.new MessageParser();
        Object[] r = RowDataUtil.allocateRowData(data.nrFields);
        underTest.parseToArray(r, message);
        Assert.assertEquals("Message Body is correct", ParseMailInputTest.MSG_BODY, String.class.cast(r[0]));
    }

    /**
     * Test that message folder name can be parsed correctly
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testMessageFolderNameIsParsed() throws Exception {
        int[] fields = new int[]{ COLUMN_FOLDER_NAME };
        MailInputField[] farr = this.getDefaultInputFields(fields);
        this.mockMailInputMeta(farr);
        try {
            mailInput.processRow(meta, data);
        } catch (KettleException e) {
            // don't worry about it
        }
        MessageParser underTest = mailInput.new MessageParser();
        Object[] r = RowDataUtil.allocateRowData(data.nrFields);
        underTest.parseToArray(r, message);
        Assert.assertEquals("Message Folder Name is correct", ParseMailInputTest.FLD_NAME, String.class.cast(r[0]));
    }

    /**
     * Test that message folder name can be parsed correctly
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testMessageAttachedFilesCountNameIsParsed() throws Exception {
        int[] fields = new int[]{ COLUMN_ATTACHED_FILES_COUNT };
        MailInputField[] farr = this.getDefaultInputFields(fields);
        this.mockMailInputMeta(farr);
        try {
            mailInput.processRow(meta, data);
        } catch (KettleException e) {
            // don't worry about it
        }
        MessageParser underTest = mailInput.new MessageParser();
        Object[] r = RowDataUtil.allocateRowData(data.nrFields);
        underTest.parseToArray(r, message);
        Assert.assertEquals("Message Attached files count is correct", new Long(ParseMailInputTest.ATTCH_COUNT), Long.class.cast(r[0]));
    }

    /**
     * Test that message body content type can be parsed correctly
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testMessageBodyContentTypeIsParsed() throws Exception {
        int[] fields = new int[]{ COLUMN_BODY_CONTENT_TYPE };
        MailInputField[] farr = this.getDefaultInputFields(fields);
        this.mockMailInputMeta(farr);
        try {
            mailInput.processRow(meta, data);
        } catch (KettleException e) {
            // don't worry about it
        }
        MessageParser underTest = mailInput.new MessageParser();
        Object[] r = RowDataUtil.allocateRowData(data.nrFields);
        underTest.parseToArray(r, message);
        Assert.assertEquals("Message body content type is correct", ParseMailInputTest.CNTNT_TYPE, String.class.cast(r[0]));
    }
}

