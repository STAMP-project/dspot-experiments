/**
 * Copyright 2016 LinkedIn Corp. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
package com.github.ambry.messageformat;


import BlobType.DataBlob;
import BlobType.MetadataBlob;
import com.codahale.metrics.MetricRegistry;
import com.github.ambry.store.MockIdFactory;
import com.github.ambry.store.StoreKeyFactory;
import com.github.ambry.store.Transformer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class MessageSievingInputStreamTest {
    private enum TransformerOptions {

        Validate,
        KeyConvert;}

    private static short messageFormatHeaderVersionSaved;

    private final List<Transformer> transformers = new ArrayList<>();

    private final EnumSet<MessageSievingInputStreamTest.TransformerOptions> options;

    private final StoreKeyFactory storeKeyFactory;

    private final RandomKeyConverter randomKeyConverter;

    public MessageSievingInputStreamTest(EnumSet<MessageSievingInputStreamTest.TransformerOptions> options) throws Exception {
        this.options = options;
        this.storeKeyFactory = new MockIdFactory();
        this.randomKeyConverter = new RandomKeyConverter();
        if (options.contains(MessageSievingInputStreamTest.TransformerOptions.Validate)) {
            transformers.add(new ValidatingTransformer(storeKeyFactory, randomKeyConverter));
        }
        if (options.contains(MessageSievingInputStreamTest.TransformerOptions.KeyConvert)) {
            transformers.add(new ValidatingKeyConvertingTransformer(storeKeyFactory, randomKeyConverter));
        }
    }

    /**
     * If there are no messages in the Message info list, the returned stream should be empty.
     */
    @Test
    public void testEmptyMsgInfoList() throws Exception {
        MessageSievingInputStream sievedStream = new MessageSievingInputStream(null, Collections.emptyList(), Collections.emptyList(), new MetricRegistry());
        Assert.assertFalse(sievedStream.hasInvalidMessages());
        Assert.assertEquals(0, sievedStream.getSize());
        Assert.assertEquals(0, sievedStream.getValidMessageInfoList().size());
    }

    /**
     * Test the case where all messages are valid.
     */
    @Test
    public void testValidBlobsAgainstCorruption() throws Exception {
        testValidBlobs(Blob_Version_V1, DataBlob, Message_Header_Version_V1);
        testValidBlobs(Blob_Version_V1, DataBlob, Message_Header_Version_V2);
        testValidBlobs(Blob_Version_V2, DataBlob, Message_Header_Version_V2);
        testValidBlobs(Blob_Version_V2, MetadataBlob, Message_Header_Version_V2);
    }

    /**
     * Test the case where there are corrupt messages, messages that are deleted and messages that are expired.
     */
    @Test
    public void testInValidDeletedAndExpiredBlobsAgainstCorruption() throws Exception {
        testInValidDeletedAndExpiredBlobs(Blob_Version_V1, DataBlob, MessageFormatRecord.Message_Header_Version_V1);
        testInValidDeletedAndExpiredBlobs(Blob_Version_V2, DataBlob, MessageFormatRecord.Message_Header_Version_V2);
        testInValidDeletedAndExpiredBlobs(Blob_Version_V2, MetadataBlob, MessageFormatRecord.Message_Header_Version_V2);
    }

    /**
     * Test the case where there are delete records in messages.
     */
    @Test
    public void testDeletedRecordsAgainstCorruption() throws Exception {
        testDeletedRecords(Blob_Version_V1, DataBlob);
        testDeletedRecords(Blob_Version_V2, DataBlob);
        testDeletedRecords(Blob_Version_V2, MetadataBlob);
    }

    /**
     * Test the case where there are deprecated messages.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testDeprecatedMsgTransformation() throws Exception {
        testDeprecatedMsg(Blob_Version_V1, DataBlob, MessageFormatRecord.Message_Header_Version_V1);
        testDeprecatedMsg(Blob_Version_V2, DataBlob, MessageFormatRecord.Message_Header_Version_V2);
        testDeprecatedMsg(Blob_Version_V2, MetadataBlob, MessageFormatRecord.Message_Header_Version_V2);
    }
}

