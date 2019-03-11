/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.provenance;


import ProvenanceEventType.RECEIVE;
import SearchableFields.ComponentID;
import SearchableFields.Filename;
import SearchableFields.FlowFileUUID;
import SearchableFields.TransitURI;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.apache.nifi.provenance.search.Query;
import org.apache.nifi.provenance.search.QuerySubmission;
import org.apache.nifi.provenance.search.SearchTerms;
import org.apache.nifi.util.NiFiProperties;
import org.junit.Assert;
import org.junit.Test;


public class TestVolatileProvenanceRepository {
    private VolatileProvenanceRepository repo;

    @Test
    public void testAddAndGet() throws IOException, InterruptedException {
        repo = new VolatileProvenanceRepository(NiFiProperties.createBasicNiFiProperties(null, null));
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("abc", "xyz");
        attributes.put("xyz", "abc");
        attributes.put("uuid", UUID.randomUUID().toString());
        final ProvenanceEventBuilder builder = new StandardProvenanceEventRecord.Builder();
        builder.setEventTime(System.currentTimeMillis());
        builder.setEventType(RECEIVE);
        builder.setTransitUri("nifi://unit-test");
        builder.fromFlowFile(createFlowFile(3L, 3000L, attributes));
        builder.setComponentId("1234");
        builder.setComponentType("dummy processor");
        for (int i = 0; i < 10; i++) {
            repo.registerEvent(builder.build());
        }
        final List<ProvenanceEventRecord> retrieved = repo.getEvents(0L, 12);
        Assert.assertEquals(10, retrieved.size());
        for (int i = 0; i < 10; i++) {
            final ProvenanceEventRecord recovered = retrieved.get(i);
            Assert.assertEquals(i, recovered.getEventId());
            Assert.assertEquals("nifi://unit-test", recovered.getTransitUri());
            Assert.assertEquals(RECEIVE, recovered.getEventType());
            Assert.assertEquals(attributes, recovered.getAttributes());
        }
    }

    @Test
    public void testIndexAndCompressOnRolloverAndSubsequentSearchAsync() throws InterruptedException {
        repo = new VolatileProvenanceRepository(NiFiProperties.createBasicNiFiProperties(null, null));
        final String uuid = "00000000-0000-0000-0000-000000000000";
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("abc", "xyz");
        attributes.put("xyz", "abc");
        attributes.put("filename", ("file-" + uuid));
        final ProvenanceEventBuilder builder = new StandardProvenanceEventRecord.Builder();
        builder.setEventTime(System.currentTimeMillis());
        builder.setEventType(RECEIVE);
        builder.setTransitUri("nifi://unit-test");
        builder.fromFlowFile(createFlowFile(3L, 3000L, attributes));
        builder.setComponentId("1234");
        builder.setComponentType("dummy processor");
        for (int i = 0; i < 10; i++) {
            attributes.put("uuid", ("00000000-0000-0000-0000-00000000000" + i));
            builder.fromFlowFile(createFlowFile(i, 3000L, attributes));
            repo.registerEvent(builder.build());
        }
        final Query query = new Query(UUID.randomUUID().toString());
        query.addSearchTerm(SearchTerms.newSearchTerm(FlowFileUUID, "00000*"));
        query.addSearchTerm(SearchTerms.newSearchTerm(Filename, "file-*"));
        query.addSearchTerm(SearchTerms.newSearchTerm(ComponentID, "12?4"));
        query.addSearchTerm(SearchTerms.newSearchTerm(TransitURI, "nifi://*"));
        query.setMaxResults(100);
        final QuerySubmission submission = repo.submitQuery(query, createUser());
        while (!(submission.getResult().isFinished())) {
            Thread.sleep(100L);
        } 
        Assert.assertEquals(10, submission.getResult().getMatchingEvents().size());
        for (final ProvenanceEventRecord match : submission.getResult().getMatchingEvents()) {
            System.out.println(match);
        }
    }
}

