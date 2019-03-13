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
package org.apache.nifi.atlas.reporting;


import SiteToSiteAttributes.S2S_PORT_ID;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.function.BiConsumer;
import org.apache.nifi.atlas.emulator.AtlasAPIV2ServerEmulator;
import org.apache.nifi.atlas.emulator.Lineage;
import org.apache.nifi.atlas.emulator.Node;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.controller.status.ConnectionStatus;
import org.apache.nifi.controller.status.PortStatus;
import org.apache.nifi.controller.status.ProcessGroupStatus;
import org.apache.nifi.controller.status.ProcessorStatus;
import org.apache.nifi.provenance.ProvenanceEventRecord;
import org.apache.nifi.provenance.lineage.ComputeLineageResult;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;


public class ITReportLineageToAtlas {
    private static final Logger logger = LoggerFactory.getLogger(ITReportLineageToAtlas.class);

    private static class TemplateContentHander implements ContentHandler {
        private static class Context {
            private boolean isConnectionSource;

            private boolean isConnectionDestination;

            private boolean isRemoteProcessGroup;

            private Stack<String> stack = new Stack<>();
        }

        private final Map<Class, BiConsumer<Object, String>> nameSetters = new HashMap<>();

        private final Map<Class, BiConsumer<Object, String>> idSetters = new HashMap<>();

        private final Map<String, Map<Class, BiConsumer<Object, String>>> setters = new HashMap<>();

        private final ITReportLineageToAtlas.TemplateContentHander.Context context = new ITReportLineageToAtlas.TemplateContentHander.Context();

        private BiConsumer<Object, String> s(String tag, BiConsumer<Object, String> setter) {
            return ( o, s) -> {
                // Only apply the function when the element is the first level child.
                // In order to avoid different 'name', 'id' or other common tags overwriting values.
                if (tag.equals(context.stack.get(((context.stack.size()) - 2)))) {
                    setter.accept(o, s);
                }
            };
        }

        public TemplateContentHander(String name) {
            rootPgStatus = new ProcessGroupStatus();
            rootPgStatus.setId(name);
            rootPgStatus.setName(name);
            pgStatus = rootPgStatus;
            current = rootPgStatus;
            pgStack.push(rootPgStatus);
            setters.put("id", idSetters);
            setters.put("name", nameSetters);
            idSetters.put(ProcessGroupStatus.class, s("processGroups", ( o, id) -> setId(id)));
            idSetters.put(ProcessorStatus.class, s("processors", ( o, id) -> setId(id)));
            idSetters.put(PortStatus.class, ( o, id) -> setId(id));
            idSetters.put(ConnectionStatus.class, ( o, id) -> {
                if (context.isConnectionSource) {
                    setSourceId(id);
                } else
                    if (context.isConnectionDestination) {
                        setDestinationId(id);
                    } else {
                        ((ConnectionStatus) (o)).setId(id);
                    }

            });
            nameSetters.put(ProcessGroupStatus.class, s("processGroups", ( o, n) -> setName(n)));
            nameSetters.put(ProcessorStatus.class, s("processors", ( o, n) -> setName(n)));
            nameSetters.put(PortStatus.class, ( o, n) -> setName(n));
            nameSetters.put(ConnectionStatus.class, s("connections", ( o, n) -> setName(n)));
        }

        private ProcessGroupStatus rootPgStatus;

        private ProcessGroupStatus parentPgStatus;

        private Stack<ProcessGroupStatus> pgStack = new Stack<>();

        private ProcessGroupStatus pgStatus;

        private ProcessorStatus processorStatus;

        private PortStatus portStatus;

        private ConnectionStatus connectionStatus;

        private Object current;

        private StringBuffer stringBuffer;

        private Map<String, String> componentNames = new HashMap<>();

        public ProcessGroupStatus getRootProcessGroupStatus() {
            return rootPgStatus;
        }

        @Override
        public void setDocumentLocator(Locator locator) {
        }

        @Override
        public void startDocument() throws SAXException {
        }

        private void setConnectionNames(ProcessGroupStatus pg) {
            pg.getConnectionStatus().forEach(( c) -> setConnectionName(c));
            pg.getProcessGroupStatus().forEach(( child) -> setConnectionNames(child));
        }

        private void setConnectionName(ConnectionStatus c) {
            if (((c.getSourceName()) == null) || (c.getSourceName().isEmpty())) {
                c.setSourceName(componentNames.get(c.getSourceId()));
            }
            if (((c.getDestinationName()) == null) || (c.getDestinationName().isEmpty())) {
                c.setDestinationName(componentNames.get(c.getDestinationId()));
            }
        }

        @Override
        public void endDocument() throws SAXException {
            setConnectionNames(rootPgStatus);
            System.out.println(("rootPgStatus=" + (rootPgStatus)));
        }

        @Override
        public void startPrefixMapping(String prefix, String uri) throws SAXException {
        }

        @Override
        public void endPrefixMapping(String prefix) throws SAXException {
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
            // Clear flags.
            stringBuffer = new StringBuffer();
            switch (qName) {
                case "processGroups" :
                    if ((pgStatus) != null) {
                        pgStack.push(pgStatus);
                    }
                    parentPgStatus = pgStatus;
                    pgStatus = new ProcessGroupStatus();
                    current = pgStatus;
                    if ((parentPgStatus) != null) {
                        parentPgStatus.getProcessGroupStatus().add(pgStatus);
                    }
                    break;
                case "processors" :
                    processorStatus = new ProcessorStatus();
                    current = processorStatus;
                    pgStatus.getProcessorStatus().add(processorStatus);
                    break;
                case "inputPorts" :
                case "outputPorts" :
                    portStatus = new PortStatus();
                    current = portStatus;
                    if (!(context.isRemoteProcessGroup)) {
                        ("inputPorts".equals(qName) ? pgStatus.getInputPortStatus() : pgStatus.getOutputPortStatus()).add(portStatus);
                    }
                    break;
                case "connections" :
                    connectionStatus = new ConnectionStatus();
                    current = connectionStatus;
                    pgStatus.getConnectionStatus().add(connectionStatus);
                    context.isConnectionSource = false;
                    context.isConnectionDestination = false;
                    break;
                case "source" :
                    if ((current) instanceof ConnectionStatus) {
                        context.isConnectionSource = true;
                    }
                    break;
                case "destination" :
                    if ((current) instanceof ConnectionStatus) {
                        context.isConnectionDestination = true;
                    }
                    break;
                case "remoteProcessGroups" :
                    context.isRemoteProcessGroup = true;
                    break;
            }
            context.stack.push(qName);
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            switch (qName) {
                case "processGroups" :
                    // At this point pgStatus has id assigned. Set group id of each component within this pg.
                    pgStatus.getProcessorStatus().forEach(( s) -> s.setGroupId(pgStatus.getId()));
                    pgStatus.getInputPortStatus().forEach(( s) -> s.setGroupId(pgStatus.getId()));
                    pgStatus.getOutputPortStatus().forEach(( s) -> s.setGroupId(pgStatus.getId()));
                    pgStatus.getConnectionStatus().forEach(( s) -> s.setGroupId(pgStatus.getId()));
                    // Put the previous ProcessGroup back to current.
                    pgStatus = (pgStack.isEmpty()) ? null : pgStack.pop();
                    current = pgStatus;
                    break;
                case "processors" :
                case "connections" :
                    current = pgStatus;
                    break;
                case "inputPorts" :
                case "outputPorts" :
                    current = pgStatus;
                    if (context.isRemoteProcessGroup) {
                        componentNames.put(portStatus.getId(), portStatus.getName());
                    }
                    break;
                case "id" :
                case "name" :
                    if ((current) != null) {
                        final BiConsumer<Object, String> setter = setters.get(qName).get(current.getClass());
                        if (setter == null) {
                            throw new RuntimeException(((qName + " setter was not found: ") + (current.getClass())));
                        }
                        setter.accept(current, stringBuffer.toString());
                    }
                    break;
                case "remoteProcessGroups" :
                    context.isRemoteProcessGroup = false;
                    break;
            }
            context.stack.pop();
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            stringBuffer.append(ch, start, length);
        }

        @Override
        public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
        }

        @Override
        public void processingInstruction(String target, String data) throws SAXException {
        }

        @Override
        public void skippedEntity(String name) throws SAXException {
        }
    }

    private static String TARGET_ATLAS_URL = "http://localhost:21000";

    private Stack<Long> requestedLineageComputationIds = new Stack<>();

    private Stack<Long> requestedExpandParentsIds = new Stack<>();

    private class TestConfiguration {
        private final ProcessGroupStatus rootPgStatus;

        private final Map<PropertyDescriptor, String> properties = new HashMap<>();

        private final ITReportLineageToAtlas.ProvenanceRecords provenanceRecords = new ITReportLineageToAtlas.ProvenanceRecords();

        private final Map<Long, ComputeLineageResult> lineageResults = new HashMap<>();

        private final Map<Long, ComputeLineageResult> parentLineageResults = new HashMap<>();

        private TestConfiguration(String templateName) {
            this.rootPgStatus = loadTemplate(templateName);
        }

        private void addLineage(ComputeLineageResult lineage) {
            lineage.getNodes().forEach(( n) -> lineageResults.put(Long.parseLong(n.getIdentifier()), lineage));
        }
    }

    private boolean useEmbeddedEmulator;

    private AtlasAPIV2ServerEmulator atlasAPIServer;

    public ITReportLineageToAtlas() {
        useEmbeddedEmulator = Boolean.valueOf(System.getenv("useEmbeddedEmulator"));
        if (useEmbeddedEmulator) {
            atlasAPIServer = new AtlasAPIV2ServerEmulator();
        }
    }

    private static class ProvenanceRecords extends ArrayList<ProvenanceEventRecord> {
        @Override
        public boolean add(ProvenanceEventRecord record) {
            ((SimpleProvenanceRecord) (record)).setEventId(size());
            return super.add(record);
        }
    }

    @Test
    public void testSimplestPath() throws Exception {
        final ITReportLineageToAtlas.TestConfiguration tc = new ITReportLineageToAtlas.TestConfiguration("SimplestFlowPath");
        test(tc);
        final Lineage lineage = getLineage();
        lineage.assertLink("nifi_flow", "SimplestFlowPath", "SimplestFlowPath@example", "nifi_flow_path", "GenerateFlowFile, LogAttribute", "d270e6f0-c5e0-38b9");
    }

    @Test
    public void testSingleFlowPath() throws Exception {
        final ITReportLineageToAtlas.TestConfiguration tc = new ITReportLineageToAtlas.TestConfiguration("SingleFlowPath");
        final ITReportLineageToAtlas.ProvenanceRecords prs = tc.provenanceRecords;
        prs.add(SimpleProvenanceRecord.pr("2e9a2852-228f-379b", "ConsumeKafka_0_11", RECEIVE, "PLAINTEXT://0.kafka.example.com:6667/topic-a"));
        prs.add(SimpleProvenanceRecord.pr("5a56149a-d82a-3242", "PublishKafka_0_11", SEND, "PLAINTEXT://0.kafka.example.com:6667/topic-b"));
        test(tc);
        waitNotificationsGetDelivered();
        final Lineage lineage = getLineage();
        final Node flow = lineage.findNode("nifi_flow", "SingleFlowPath", "SingleFlowPath@example");
        final Node path = lineage.findNode("nifi_flow_path", "ConsumeKafka_0_11, UpdateAttribute, ConvertJSONToSQL, PutSQL, PublishKafka_0_11", "2e9a2852-228f-379b");
        final Node topicA = lineage.findNode("kafka_topic", "topic-a@example");
        final Node topicB = lineage.findNode("kafka_topic", "topic-b@example");
        lineage.assertLink(flow, path);
        lineage.assertLink(topicA, path);
        lineage.assertLink(path, topicB);
    }

    @Test
    public void testMultipleProcessGroups() throws Exception {
        final ITReportLineageToAtlas.TestConfiguration tc = new ITReportLineageToAtlas.TestConfiguration("MultipleProcessGroups");
        final ITReportLineageToAtlas.ProvenanceRecords prs = tc.provenanceRecords;
        prs.add(SimpleProvenanceRecord.pr("989dabb7-54b9-3c78", "ConsumeKafka_0_11", RECEIVE, "PLAINTEXT://0.kafka.example.com:6667/nifi-test"));
        prs.add(SimpleProvenanceRecord.pr("767c7bd6-75e3-3f32", "PutHDFS", SEND, "hdfs://nn1.example.com:8020/user/nifi/5262553828219"));
        test(tc);
        waitNotificationsGetDelivered();
        final Lineage lineage = getLineage();
        final Node flow = lineage.findNode("nifi_flow", "MultipleProcessGroups", "MultipleProcessGroups@example");
        final Node path = lineage.findNode("nifi_flow_path", "ConsumeKafka_0_11, UpdateAttribute, PutHDFS", "989dabb7-54b9-3c78");
        final Node kafkaTopic = lineage.findNode("kafka_topic", "nifi-test@example");
        final Node hdfsPath = lineage.findNode("hdfs_path", "/user/nifi/5262553828219@example");
        lineage.assertLink(flow, path);
        lineage.assertLink(kafkaTopic, path);
        lineage.assertLink(path, hdfsPath);
    }

    @Test
    public void testS2SSendSimple() throws Exception {
        final ITReportLineageToAtlas.TestConfiguration tc = new ITReportLineageToAtlas.TestConfiguration("S2SSend");
        testS2SSend(tc);
        final Lineage lineage = getLineage();
        // The FlowFile created by Generate A has not been finished (by DROP event, but SIMPLE_PATH strategy can report it.
        final Node pathA = lineage.findNode("nifi_flow_path", "Generate A", "ca71e4d9-2a4f-3970");
        final Node genA = lineage.findNode("nifi_data", "Generate A", "ca71e4d9-2a4f-3970");
        lineage.assertLink(genA, pathA);
        final Node pathC = lineage.findNode("nifi_flow_path", "Generate C", "c439cdca-e989-3491");
        final Node pathT = lineage.findNode("nifi_flow_path", "GetTwitter", "b775b657-5a5b-3708");
        // Generate C and GetTwitter have reported proper SEND lineage to the input port.
        final Node remoteInputPortD = lineage.findNode("nifi_input_port", "input", "77919f59-533e-35a3");
        final Node remoteInputPortP = lineage.findNode("nifi_flow_path", "Remote Input Port", "f31a6b53-3077-4c59");
        final Node remoteInputPortQ = lineage.findNode("nifi_queue", "queue", "f31a6b53-3077-4c59");
        lineage.assertLink(pathC, remoteInputPortQ);
        lineage.assertLink(pathT, remoteInputPortQ);
        lineage.assertLink(remoteInputPortQ, remoteInputPortP);
        lineage.assertLink(remoteInputPortP, remoteInputPortD);
        // nifi_data is created for each obscure input processor.
        final Node genC = lineage.findNode("nifi_data", "Generate C", "c439cdca-e989-3491");
        final Node genT = lineage.findNode("nifi_data", "GetTwitter", "b775b657-5a5b-3708");
        lineage.assertLink(genC, pathC);
        lineage.assertLink(genT, pathT);
    }

    @Test
    public void testS2SSendComplete() throws Exception {
        final ITReportLineageToAtlas.TestConfiguration tc = new ITReportLineageToAtlas.TestConfiguration("S2SSend");
        tc.properties.put(ReportLineageToAtlas.NIFI_LINEAGE_STRATEGY, ReportLineageToAtlas.LINEAGE_STRATEGY_COMPLETE_PATH.getValue());
        testS2SSend(tc);
        final Lineage lineage = getLineage();
        // Complete path has hash.
        final Node pathC = lineage.findNode("nifi_flow_path", "Generate C, Remote Input Port", "c439cdca-e989-3491-0000-000000000000::1605753423@example");
        final Node pathT = lineage.findNode("nifi_flow_path", "GetTwitter, Remote Input Port", "b775b657-5a5b-3708-0000-000000000000::3843156947@example");
        // Generate C and GetTwitter have reported proper SEND lineage to the input port.
        final Node remoteInputPort = lineage.findNode("nifi_input_port", "input", "77919f59-533e-35a3");
        lineage.assertLink(pathC, remoteInputPort);
        lineage.assertLink(pathT, remoteInputPort);
        // nifi_data is created for each obscure input processor.
        final Node genC = lineage.findNode("nifi_data", "Generate C", "c439cdca-e989-3491");
        final Node genT = lineage.findNode("nifi_data", "GetTwitter", "b775b657-5a5b-3708");
        lineage.assertLink(genC, pathC);
        lineage.assertLink(genT, pathT);
    }

    @Test
    public void testS2SSendSimpleRAW() throws Exception {
        final ITReportLineageToAtlas.TestConfiguration tc = new ITReportLineageToAtlas.TestConfiguration("S2SSendRAW");
        testS2SSendRAW(tc);
        final Lineage lineage = getLineage();
        // The FlowFile created by Generate A has not been finished (by DROP event, but SIMPLE_PATH strategy can report it.
        final Node pathA = lineage.findNode("nifi_flow_path", "Generate A", "ca71e4d9-2a4f-3970");
        final Node genA = lineage.findNode("nifi_data", "Generate A", "ca71e4d9-2a4f-3970");
        lineage.assertLink(genA, pathA);
        final Node pathC = lineage.findNode("nifi_flow_path", "Generate C", "c439cdca-e989-3491");
        final Node pathT = lineage.findNode("nifi_flow_path", "GetTwitter", "b775b657-5a5b-3708");
        // Generate C and GetTwitter have reported proper SEND lineage to the input port.
        final Node remoteInputPortD = lineage.findNode("nifi_input_port", "input", "77919f59-533e-35a3");
        final Node remoteInputPortP = lineage.findNode("nifi_flow_path", "Remote Input Port", "f31a6b53-3077-4c59");
        final Node remoteInputPortQ = lineage.findNode("nifi_queue", "queue", "f31a6b53-3077-4c59");
        lineage.assertLink(pathC, remoteInputPortQ);
        lineage.assertLink(pathT, remoteInputPortQ);
        lineage.assertLink(remoteInputPortQ, remoteInputPortP);
        lineage.assertLink(remoteInputPortP, remoteInputPortD);
        // nifi_data is created for each obscure input processor.
        final Node genC = lineage.findNode("nifi_data", "Generate C", "c439cdca-e989-3491");
        final Node genT = lineage.findNode("nifi_data", "GetTwitter", "b775b657-5a5b-3708");
        lineage.assertLink(genC, pathC);
        lineage.assertLink(genT, pathT);
    }

    @Test
    public void testS2SSendCompleteRAW() throws Exception {
        final ITReportLineageToAtlas.TestConfiguration tc = new ITReportLineageToAtlas.TestConfiguration("S2SSendRAW");
        tc.properties.put(ReportLineageToAtlas.NIFI_LINEAGE_STRATEGY, ReportLineageToAtlas.LINEAGE_STRATEGY_COMPLETE_PATH.getValue());
        testS2SSendRAW(tc);
        final Lineage lineage = getLineage();
        // Complete path has hash.
        final Node pathC = lineage.findNode("nifi_flow_path", "Generate C, Remote Input Port", "c439cdca-e989-3491-0000-000000000000::1605753423@example");
        final Node pathT = lineage.findNode("nifi_flow_path", "GetTwitter, Remote Input Port", "b775b657-5a5b-3708-0000-000000000000::3843156947@example");
        // Generate C and GetTwitter have reported proper SEND lineage to the input port.
        final Node remoteInputPort = lineage.findNode("nifi_input_port", "input", "77919f59-533e-35a3");
        lineage.assertLink(pathC, remoteInputPort);
        lineage.assertLink(pathT, remoteInputPort);
        // nifi_data is created for each obscure input processor.
        final Node genC = lineage.findNode("nifi_data", "Generate C", "c439cdca-e989-3491");
        final Node genT = lineage.findNode("nifi_data", "GetTwitter", "b775b657-5a5b-3708");
        lineage.assertLink(genC, pathC);
        lineage.assertLink(genT, pathT);
    }

    /**
     * A client NiFi gets FlowFiles from a remote NiFi.
     */
    @Test
    public void testS2SGet() throws Exception {
        final ITReportLineageToAtlas.TestConfiguration tc = new ITReportLineageToAtlas.TestConfiguration("S2SGet");
        final ITReportLineageToAtlas.ProvenanceRecords prs = tc.provenanceRecords;
        // The remote port GUID is different than the Remote Output Ports.
        prs.add(SimpleProvenanceRecord.pr("7375f8f6-4604-468d", "Remote Output Port", RECEIVE, ("http://nifi.example.com:8080/nifi-api/data-transfer/output-ports" + "/392e7343-3950-329b-0000-000000000000/transactions/tx-1/flow-files")));
        test(tc);
        waitNotificationsGetDelivered();
        final Lineage lineage = getLineage();
        final Node flow = lineage.findNode("nifi_flow", "S2SGet", "S2SGet@example");
        final Node pathL = lineage.findNode("nifi_flow_path", "LogAttribute", "97cc5b27-22f3-3c3b");
        final Node pathP = lineage.findNode("nifi_flow_path", "PutFile", "4f3bfa4c-6427-3aac");
        final Node pathU = lineage.findNode("nifi_flow_path", "UpdateAttribute", "bb530e58-ee14-3cac");
        // These entities should be created by notification.
        final Node remoteOutputPortDataSet = lineage.findNode("nifi_output_port", "output", "392e7343-3950-329b");
        final Node remoteOutputPortProcess = lineage.findNode("nifi_flow_path", "Remote Output Port", "7375f8f6-4604-468d");
        final Node queueL = lineage.findNode("nifi_queue", "queue", "97cc5b27-22f3-3c3b");
        final Node queueP = lineage.findNode("nifi_queue", "queue", "4f3bfa4c-6427-3aac");
        final Node queueU = lineage.findNode("nifi_queue", "queue", "bb530e58-ee14-3cac");
        lineage.assertLink(remoteOutputPortDataSet, remoteOutputPortProcess);
        lineage.assertLink(flow, remoteOutputPortProcess);
        lineage.assertLink(remoteOutputPortProcess, queueL);
        lineage.assertLink(remoteOutputPortProcess, queueP);
        lineage.assertLink(remoteOutputPortProcess, queueU);
        lineage.assertLink(queueL, pathL);
        lineage.assertLink(queueP, pathP);
        lineage.assertLink(queueU, pathU);
    }

    /**
     * A client NiFi gets FlowFiles from a remote NiFi using RAW protocol.
     */
    @Test
    public void testS2SGetRAW() throws Exception {
        final ITReportLineageToAtlas.TestConfiguration tc = new ITReportLineageToAtlas.TestConfiguration("S2SGetRAW");
        final ITReportLineageToAtlas.ProvenanceRecords prs = tc.provenanceRecords;
        // The remote port GUID is different than the Remote Output Ports.
        final SimpleProvenanceRecord receiveEvent = SimpleProvenanceRecord.pr("7375f8f6-4604-468d", "Remote Output Port", RECEIVE, "nifi://nifi.example.com:8081/7f1a5d65-65bb-4473-b1a4-4a742d9af4a7");
        receiveEvent.getAttributes().put(S2S_PORT_ID.key(), "392e7343-3950-329b-0000-000000000000");
        prs.add(receiveEvent);
        test(tc);
        waitNotificationsGetDelivered();
        final Lineage lineage = getLineage();
        final Node flow = lineage.findNode("nifi_flow", "S2SGetRAW", "S2SGetRAW@example");
        final Node pathL = lineage.findNode("nifi_flow_path", "LogAttribute", "97cc5b27-22f3-3c3b");
        final Node pathP = lineage.findNode("nifi_flow_path", "PutFile", "4f3bfa4c-6427-3aac");
        final Node pathU = lineage.findNode("nifi_flow_path", "UpdateAttribute", "bb530e58-ee14-3cac");
        // These entities should be created by notification.
        final Node remoteOutputPortDataSet = lineage.findNode("nifi_output_port", "output", "392e7343-3950-329b");
        final Node remoteOutputPortProcess = lineage.findNode("nifi_flow_path", "Remote Output Port", "7375f8f6-4604-468d");
        final Node queueL = lineage.findNode("nifi_queue", "queue", "97cc5b27-22f3-3c3b");
        final Node queueP = lineage.findNode("nifi_queue", "queue", "4f3bfa4c-6427-3aac");
        final Node queueU = lineage.findNode("nifi_queue", "queue", "bb530e58-ee14-3cac");
        lineage.assertLink(remoteOutputPortDataSet, remoteOutputPortProcess);
        lineage.assertLink(flow, remoteOutputPortProcess);
        lineage.assertLink(remoteOutputPortProcess, queueL);
        lineage.assertLink(remoteOutputPortProcess, queueP);
        lineage.assertLink(remoteOutputPortProcess, queueU);
        lineage.assertLink(queueL, pathL);
        lineage.assertLink(queueP, pathP);
        lineage.assertLink(queueU, pathU);
    }

    /**
     * A remote NiFi transfers FlowFiles to remote client NiFis.
     * This NiFi instance owns RootProcessGroup output port.
     */
    @Test
    public void testS2STransfer() throws Exception {
        final ITReportLineageToAtlas.TestConfiguration tc = new ITReportLineageToAtlas.TestConfiguration("S2STransfer");
        final ITReportLineageToAtlas.ProvenanceRecords prs = tc.provenanceRecords;
        prs.add(SimpleProvenanceRecord.pr("392e7343-3950-329b", "Output Port", SEND, ("http://nifi.example.com:8080/nifi-api/data-transfer/output-ports" + "/392e7343-3950-329b-0000-000000000000/transactions/tx-1/flow-files")));
        test(tc);
        waitNotificationsGetDelivered();
        final Lineage lineage = getLineage();
        final Node flow = lineage.findNode("nifi_flow", "S2STransfer", "S2STransfer@example");
        final Node path = lineage.findNode("nifi_flow_path", "GenerateFlowFile, output", "1b9f81db-a0fd-389a");
        final Node outputPort = lineage.findNode("nifi_output_port", "output", "392e7343-3950-329b");
        lineage.assertLink(flow, path);
        lineage.assertLink(path, outputPort);
    }

    /**
     * A remote NiFi receives FlowFiles from remote client NiFis.
     * This NiFi instance owns RootProcessGroup input port.
     */
    @Test
    public void testS2SReceive() throws Exception {
        final ITReportLineageToAtlas.TestConfiguration tc = new ITReportLineageToAtlas.TestConfiguration("S2SReceive");
        final ITReportLineageToAtlas.ProvenanceRecords prs = tc.provenanceRecords;
        prs.add(SimpleProvenanceRecord.pr("77919f59-533e-35a3", "Input Port", RECEIVE, ("http://nifi.example.com:8080/nifi-api/data-transfer/output-ports" + "/77919f59-533e-35a3-0000-000000000000/transactions/tx-1/flow-files")));
        test(tc);
        waitNotificationsGetDelivered();
        final Lineage lineage = getLineage();
        final Node flow = lineage.findNode("nifi_flow", "S2SReceive", "S2SReceive@example");
        final Node path = lineage.findNode("nifi_flow_path", "input, UpdateAttribute", "77919f59-533e-35a3");
        final Node inputPort = lineage.findNode("nifi_input_port", "input", "77919f59-533e-35a3");
        lineage.assertLink(flow, path);
        lineage.assertLink(flow, inputPort);
        lineage.assertLink(inputPort, path);
    }

    /**
     * A remote NiFi transfers FlowFiles to remote client NiFis using RAW protocol.
     * This NiFi instance owns RootProcessGroup output port.
     */
    @Test
    public void testS2STransferRAW() throws Exception {
        final ITReportLineageToAtlas.TestConfiguration tc = new ITReportLineageToAtlas.TestConfiguration("S2STransfer");
        final ITReportLineageToAtlas.ProvenanceRecords prs = tc.provenanceRecords;
        prs.add(SimpleProvenanceRecord.pr("392e7343-3950-329b", "Output Port", SEND, "nifi://nifi.example.com:8081/580b7989-a80b-4089-b25b-3f5e0103af82"));
        test(tc);
        waitNotificationsGetDelivered();
        final Lineage lineage = getLineage();
        final Node flow = lineage.findNode("nifi_flow", "S2STransfer", "S2STransfer@example");
        final Node path = lineage.findNode("nifi_flow_path", "GenerateFlowFile, output", "1b9f81db-a0fd-389a");
        final Node outputPort = lineage.findNode("nifi_output_port", "output", "392e7343-3950-329b");
        lineage.assertLink(flow, path);
        lineage.assertLink(path, outputPort);
    }

    /**
     * A remote NiFi receives FlowFiles from remote client NiFis using RAW protocol.
     * This NiFi instance owns RootProcessGroup input port.
     */
    @Test
    public void testS2SReceiveRAW() throws Exception {
        final ITReportLineageToAtlas.TestConfiguration tc = new ITReportLineageToAtlas.TestConfiguration("S2SReceive");
        final ITReportLineageToAtlas.ProvenanceRecords prs = tc.provenanceRecords;
        prs.add(SimpleProvenanceRecord.pr("77919f59-533e-35a3", "Input Port", RECEIVE, "nifi://nifi.example.com:8081/232018cc-a147-40c6-b148-21f9f814e93c"));
        test(tc);
        waitNotificationsGetDelivered();
        final Lineage lineage = getLineage();
        final Node flow = lineage.findNode("nifi_flow", "S2SReceive", "S2SReceive@example");
        final Node path = lineage.findNode("nifi_flow_path", "input, UpdateAttribute", "77919f59-533e-35a3");
        final Node inputPort = lineage.findNode("nifi_input_port", "input", "77919f59-533e-35a3");
        lineage.assertLink(flow, path);
        lineage.assertLink(flow, inputPort);
        lineage.assertLink(inputPort, path);
    }

    @Test
    public void testS2SReceiveAndSendCombination() throws Exception {
        testS2SReceive();
        testS2SSendSimple();
        final Lineage lineage = getLineage();
        final Node remoteFlow = lineage.findNode("nifi_flow", "S2SReceive", "S2SReceive@example");
        final Node localFlow = lineage.findNode("nifi_flow", "S2SSend", "S2SSend@example");
        final Node remoteInputPortQ = lineage.findNode("nifi_queue", "queue", "f31a6b53-3077-4c59");
        final Node remoteInputPortP = lineage.findNode("nifi_flow_path", "Remote Input Port", "f31a6b53-3077-4c59");
        final Node inputPort = lineage.findNode("nifi_input_port", "input", "77919f59-533e-35a3");
        final Node pathC = lineage.findNode("nifi_flow_path", "Generate C", "c439cdca-e989-3491");
        final Node pathT = lineage.findNode("nifi_flow_path", "GetTwitter", "b775b657-5a5b-3708");
        // Remote flow owns the inputPort.
        lineage.assertLink(remoteFlow, inputPort);
        // These paths within local flow sends data to the remote flow through the remote input port.
        lineage.assertLink(localFlow, pathC);
        lineage.assertLink(localFlow, pathT);
        lineage.assertLink(pathC, remoteInputPortQ);
        lineage.assertLink(pathT, remoteInputPortQ);
        lineage.assertLink(remoteInputPortQ, remoteInputPortP);
        lineage.assertLink(remoteInputPortP, inputPort);
    }

    @Test
    public void testS2STransferAndGetCombination() throws Exception {
        testS2STransfer();
        testS2SGet();
        final Lineage lineage = getLineage();
        final Node remoteFlow = lineage.findNode("nifi_flow", "S2STransfer", "S2STransfer@example");
        final Node localFlow = lineage.findNode("nifi_flow", "S2SGet", "S2SGet@example");
        final Node remoteGen = lineage.findNode("nifi_flow_path", "GenerateFlowFile, output", "1b9f81db-a0fd-389a");
        final Node outputPort = lineage.findNode("nifi_output_port", "output", "392e7343-3950-329b");
        final Node remoteOutputPortP = lineage.findNode("nifi_flow_path", "Remote Output Port", "7375f8f6-4604-468d");
        final Node queueL = lineage.findNode("nifi_queue", "queue", "97cc5b27-22f3-3c3b");
        final Node queueP = lineage.findNode("nifi_queue", "queue", "4f3bfa4c-6427-3aac");
        final Node queueU = lineage.findNode("nifi_queue", "queue", "bb530e58-ee14-3cac");
        final Node pathL = lineage.findNode("nifi_flow_path", "LogAttribute", "97cc5b27-22f3-3c3b");
        final Node pathP = lineage.findNode("nifi_flow_path", "PutFile", "4f3bfa4c-6427-3aac");
        final Node pathU = lineage.findNode("nifi_flow_path", "UpdateAttribute", "bb530e58-ee14-3cac");
        // Remote flow owns the outputPort and transfer data generated by GenerateFlowFile.
        lineage.assertLink(remoteFlow, remoteGen);
        lineage.assertLink(remoteGen, outputPort);
        // The Remote Output Port path in local flow gets data from the remote.
        lineage.assertLink(localFlow, remoteOutputPortP);
        lineage.assertLink(outputPort, remoteOutputPortP);
        lineage.assertLink(remoteOutputPortP, queueL);
        lineage.assertLink(remoteOutputPortP, queueP);
        lineage.assertLink(remoteOutputPortP, queueU);
        lineage.assertLink(queueL, pathL);
        lineage.assertLink(queueP, pathP);
        lineage.assertLink(queueU, pathU);
    }

    @Test
    public void testS2SReceiveAndSendCombinationRAW() throws Exception {
        testS2SReceiveRAW();
        testS2SSendSimpleRAW();
        final Lineage lineage = getLineage();
        final Node remoteFlow = lineage.findNode("nifi_flow", "S2SReceive", "S2SReceive@example");
        final Node localFlow = lineage.findNode("nifi_flow", "S2SSendRAW", "S2SSendRAW@example");
        final Node remoteInputPortQ = lineage.findNode("nifi_queue", "queue", "f31a6b53-3077-4c59");
        final Node remoteInputPortP = lineage.findNode("nifi_flow_path", "Remote Input Port", "f31a6b53-3077-4c59");
        final Node inputPort = lineage.findNode("nifi_input_port", "input", "77919f59-533e-35a3");
        final Node pathC = lineage.findNode("nifi_flow_path", "Generate C", "c439cdca-e989-3491");
        final Node pathT = lineage.findNode("nifi_flow_path", "GetTwitter", "b775b657-5a5b-3708");
        // Remote flow owns the inputPort.
        lineage.assertLink(remoteFlow, inputPort);
        // These paths within local flow sends data to the remote flow through the remote input port.
        lineage.assertLink(localFlow, pathC);
        lineage.assertLink(localFlow, pathT);
        lineage.assertLink(pathC, remoteInputPortQ);
        lineage.assertLink(pathT, remoteInputPortQ);
        lineage.assertLink(remoteInputPortQ, remoteInputPortP);
        lineage.assertLink(remoteInputPortP, inputPort);
    }

    @Test
    public void testS2STransferAndGetCombinationRAW() throws Exception {
        testS2STransferRAW();
        testS2SGetRAW();
        final Lineage lineage = getLineage();
        final Node remoteFlow = lineage.findNode("nifi_flow", "S2STransfer", "S2STransfer@example");
        final Node localFlow = lineage.findNode("nifi_flow", "S2SGetRAW", "S2SGetRAW@example");
        final Node remoteGen = lineage.findNode("nifi_flow_path", "GenerateFlowFile, output", "1b9f81db-a0fd-389a");
        final Node outputPort = lineage.findNode("nifi_output_port", "output", "392e7343-3950-329b");
        final Node remoteOutputPortP = lineage.findNode("nifi_flow_path", "Remote Output Port", "7375f8f6-4604-468d");
        final Node queueL = lineage.findNode("nifi_queue", "queue", "97cc5b27-22f3-3c3b");
        final Node queueP = lineage.findNode("nifi_queue", "queue", "4f3bfa4c-6427-3aac");
        final Node queueU = lineage.findNode("nifi_queue", "queue", "bb530e58-ee14-3cac");
        final Node pathL = lineage.findNode("nifi_flow_path", "LogAttribute", "97cc5b27-22f3-3c3b");
        final Node pathP = lineage.findNode("nifi_flow_path", "PutFile", "4f3bfa4c-6427-3aac");
        final Node pathU = lineage.findNode("nifi_flow_path", "UpdateAttribute", "bb530e58-ee14-3cac");
        // Remote flow owns the outputPort and transfer data generated by GenerateFlowFile.
        lineage.assertLink(remoteFlow, remoteGen);
        lineage.assertLink(remoteGen, outputPort);
        // The Remote Output Port path in local flow gets data from the remote.
        lineage.assertLink(localFlow, remoteOutputPortP);
        lineage.assertLink(outputPort, remoteOutputPortP);
        lineage.assertLink(remoteOutputPortP, queueL);
        lineage.assertLink(remoteOutputPortP, queueP);
        lineage.assertLink(remoteOutputPortP, queueU);
        lineage.assertLink(queueL, pathL);
        lineage.assertLink(queueP, pathP);
        lineage.assertLink(queueU, pathU);
    }

    /**
     * A client NiFi gets FlowFiles from a remote output port and sends it to a remote input port without doing anything.
     */
    @Test
    public void testS2SDirect() throws Exception {
        final ITReportLineageToAtlas.TestConfiguration tc = new ITReportLineageToAtlas.TestConfiguration("S2SDirect");
        final ITReportLineageToAtlas.ProvenanceRecords prs = tc.provenanceRecords;
        prs.add(SimpleProvenanceRecord.pr("d73d9115-b987-4ffc", "Remote Output Port", RECEIVE, ("http://nifi.example.com:8080/nifi-api/data-transfer/output-ports" + "/015f1040-dcd7-17bd-5c1f-e31afe0a09a4/transactions/tx-1/flow-files")));
        prs.add(SimpleProvenanceRecord.pr("a4f14247-89aa-4e6c", "Remote Input Port", SEND, ("http://nifi.example.com:8080/nifi-api/data-transfer/input-ports" + "/015f101e-dcd7-17bd-8899-1a723733521a/transactions/tx-2/flow-files")));
        Map<Long, ComputeLineageResult> lineages = tc.lineageResults;
        // Received from remote output port, then sent it via remote input port
        lineages.put(1L, createLineage(prs, 0, 1));
        test(tc);
        waitNotificationsGetDelivered();
        final Lineage lineage = getLineage();
        final Node flow = lineage.findNode("nifi_flow", "S2SDirect", "S2SDirect@example");
        final Node remoteOutputPort = lineage.findNode("nifi_output_port", "output", "015f1040-dcd7-17bd-5c1f-e31afe0a09a4@example");
        final Node remoteOutputPortP = lineage.findNode("nifi_flow_path", "Remote Output Port", "d73d9115-b987-4ffc");
        final Node remoteInputPortQ = lineage.findNode("nifi_queue", "queue", "a4f14247-89aa-4e6c");
        final Node remoteInputPortP = lineage.findNode("nifi_flow_path", "Remote Input Port", "a4f14247-89aa-4e6c");
        final Node remoteInputPort = lineage.findNode("nifi_input_port", "input", "015f101e-dcd7-17bd-8899-1a723733521a@example");
        // Even if there is no Processor, lineage can be reported using root flow_path.
        lineage.assertLink(flow, remoteOutputPortP);
        lineage.assertLink(remoteOutputPort, remoteOutputPortP);
        lineage.assertLink(remoteOutputPortP, remoteInputPortQ);
        lineage.assertLink(remoteInputPortQ, remoteInputPortP);
        lineage.assertLink(remoteInputPortP, remoteInputPort);
    }

    @Test
    public void testRemoteInvocation() throws Exception {
        final ITReportLineageToAtlas.TestConfiguration tc = new ITReportLineageToAtlas.TestConfiguration("RemoteInvocation");
        final ITReportLineageToAtlas.ProvenanceRecords prs = tc.provenanceRecords;
        prs.add(SimpleProvenanceRecord.pr("2607ed95-c6ef-3636", "DeleteHDFS", REMOTE_INVOCATION, "hdfs://nn1.example.com:8020/test/2017-10-23"));
        prs.add(SimpleProvenanceRecord.pr("2607ed95-c6ef-3636", "DeleteHDFS", REMOTE_INVOCATION, "hdfs://nn1.example.com:8020/test/2017-10-24"));
        prs.add(SimpleProvenanceRecord.pr("2607ed95-c6ef-3636", "DeleteHDFS", REMOTE_INVOCATION, "hdfs://nn1.example.com:8020/test/2017-10-25"));
        test(tc);
        waitNotificationsGetDelivered();
        final Lineage lineage = getLineage();
        final Node flow = lineage.findNode("nifi_flow", "RemoteInvocation", "RemoteInvocation@example");
        final Node path = lineage.findNode("nifi_flow_path", "DeleteHDFS", "2607ed95-c6ef-3636");
        final Node hdfsPath23 = lineage.findNode("hdfs_path", "/test/2017-10-23@example");
        final Node hdfsPath24 = lineage.findNode("hdfs_path", "/test/2017-10-24@example");
        final Node hdfsPath25 = lineage.findNode("hdfs_path", "/test/2017-10-25@example");
        lineage.assertLink(flow, path);
        lineage.assertLink(path, hdfsPath23);
        lineage.assertLink(path, hdfsPath24);
        lineage.assertLink(path, hdfsPath25);
    }

    @Test
    public void testSimpleEventLevelSimplePath() throws Exception {
        final ITReportLineageToAtlas.TestConfiguration tc = new ITReportLineageToAtlas.TestConfiguration("SimpleEventLevel");
        final ITReportLineageToAtlas.ProvenanceRecords prs = tc.provenanceRecords;
        prs.add(SimpleProvenanceRecord.pr("d9257f7e-b78c-349a", "Generate A", CREATE));
        prs.add(SimpleProvenanceRecord.pr("d84b9bdc-5e42-3b3b", "Generate B", CREATE));
        prs.add(SimpleProvenanceRecord.pr("eaf013c1-aec5-39b0", "PutFile", SEND, "file:/tmp/nifi/a.txt"));
        prs.add(SimpleProvenanceRecord.pr("eaf013c1-aec5-39b0", "PutFile", SEND, "file:/tmp/nifi/b.txt"));
        test(tc);
        waitNotificationsGetDelivered();
        final Lineage lineage = getLineage();
        final Node genA = lineage.findNode("nifi_data", "Generate A", "d9257f7e-b78c-349a");
        final Node genB = lineage.findNode("nifi_data", "Generate B", "d84b9bdc-5e42-3b3b");
        final Node genAPath = lineage.findNode("nifi_flow_path", "Generate A", "d9257f7e-b78c-349a");
        final Node genBPath = lineage.findNode("nifi_flow_path", "Generate B", "d84b9bdc-5e42-3b3b");
        final Node queue = lineage.findNode("nifi_queue", "queue", "eaf013c1-aec5-39b0");
        final Node putFile = lineage.findNode("nifi_flow_path", "PutFile, LogAttribute", "eaf013c1-aec5-39b0");
        final Node outA = lineage.findNode("fs_path", "/tmp/nifi/a.txt@example");
        final Node outB = lineage.findNode("fs_path", "/tmp/nifi/b.txt@example");
        lineage.assertLink(genA, genAPath);
        lineage.assertLink(genAPath, queue);
        lineage.assertLink(genB, genBPath);
        lineage.assertLink(genBPath, queue);
        lineage.assertLink(queue, putFile);
        lineage.assertLink(putFile, outA);
        lineage.assertLink(putFile, outB);
    }

    @Test
    public void testSimpleEventLevelCompletePath() throws Exception {
        final ITReportLineageToAtlas.TestConfiguration tc = new ITReportLineageToAtlas.TestConfiguration("SimpleEventLevel");
        tc.properties.put(ReportLineageToAtlas.NIFI_LINEAGE_STRATEGY, ReportLineageToAtlas.LINEAGE_STRATEGY_COMPLETE_PATH.getValue());
        final ITReportLineageToAtlas.ProvenanceRecords prs = tc.provenanceRecords;
        String flowFileUUIDA = "A0000000-0000-0000";
        String flowFileUUIDB = "B0000000-0000-0000";
        prs.add(SimpleProvenanceRecord.pr("d9257f7e-b78c-349a", "Generate A", CREATE, flowFileUUIDA));
        prs.add(SimpleProvenanceRecord.pr("d84b9bdc-5e42-3b3b", "Generate B", CREATE, flowFileUUIDB));
        prs.add(SimpleProvenanceRecord.pr("eaf013c1-aec5-39b0", "PutFile", SEND, "file:/tmp/nifi/a.txt", flowFileUUIDA));
        prs.add(SimpleProvenanceRecord.pr("eaf013c1-aec5-39b0", "PutFile", SEND, "file:/tmp/nifi/b.txt", flowFileUUIDB));
        prs.add(SimpleProvenanceRecord.pr("bfc30bc3-48cf-332a", "LogAttribute", DROP, flowFileUUIDA));
        prs.add(SimpleProvenanceRecord.pr("bfc30bc3-48cf-332a", "LogAttribute", DROP, flowFileUUIDB));
        Map<Long, ComputeLineageResult> lineages = tc.lineageResults;
        lineages.put(4L, createLineage(prs, 0, 2, 4));
        lineages.put(5L, createLineage(prs, 1, 3, 5));
        test(tc);
        waitNotificationsGetDelivered();
        final Lineage lineage = getLineage();
        final Node genA = lineage.findNode("nifi_data", "Generate A", "d9257f7e-b78c-349a");
        final Node genB = lineage.findNode("nifi_data", "Generate B", "d84b9bdc-5e42-3b3b");
        final Node genAPath = lineage.findNode("nifi_flow_path", "Generate A, PutFile, LogAttribute", "d9257f7e-b78c-349a-0000-000000000000::980416504@example");
        final Node genBPath = lineage.findNode("nifi_flow_path", "Generate B, PutFile, LogAttribute", "d84b9bdc-5e42-3b3b-0000-000000000000::442259660@example");
        final Node outA = lineage.findNode("fs_path", "/tmp/nifi/a.txt@example");
        final Node outB = lineage.findNode("fs_path", "/tmp/nifi/b.txt@example");
        lineage.assertLink(genA, genAPath);
        lineage.assertLink(genB, genBPath);
        lineage.assertLink(genAPath, outA);
        lineage.assertLink(genBPath, outB);
    }

    @Test
    public void testMergedEvents() throws Exception {
        final ITReportLineageToAtlas.TestConfiguration tc = new ITReportLineageToAtlas.TestConfiguration("MergedEvents");
        tc.properties.put(ReportLineageToAtlas.NIFI_LINEAGE_STRATEGY, ReportLineageToAtlas.LINEAGE_STRATEGY_COMPLETE_PATH.getValue());
        final ITReportLineageToAtlas.ProvenanceRecords prs = tc.provenanceRecords;
        final String flowFileUUIDA = "A0000000-0000-0000";
        final String flowFileUUIDB = "B0000000-0000-0000";
        final String flowFileUUIDC = "C0000000-0000-0000";
        final String flowFileUUIDD = "D0000000-0000-0000";
        // Merged B and C.
        final String flowFileUUIDBC = "BC000000-0000-0000";
        prs.add(SimpleProvenanceRecord.pr("f585d83b-2a03-37cf", "Generate A", CREATE, flowFileUUIDA));// 0

        prs.add(SimpleProvenanceRecord.pr("59a7c1f9-9a73-3cc6", "Generate B", CREATE, flowFileUUIDB));// 1

        prs.add(SimpleProvenanceRecord.pr("d6c3f282-e03d-316c", "Generate C", CREATE, flowFileUUIDC));// 2

        prs.add(SimpleProvenanceRecord.pr("f9593a5a-f0d5-3e87", "Generate D", CREATE, flowFileUUIDD));// 3

        // Original files are dropped.
        prs.add(SimpleProvenanceRecord.pr("c77dd033-bb9e-39ea", "MergeContent", JOIN, flowFileUUIDBC));// 4

        prs.add(SimpleProvenanceRecord.pr("c77dd033-bb9e-39ea", "MergeContent", DROP, flowFileUUIDB));// 5

        prs.add(SimpleProvenanceRecord.pr("c77dd033-bb9e-39ea", "MergeContent", DROP, flowFileUUIDC));// 6

        prs.add(SimpleProvenanceRecord.pr("93f8ad14-6ee6-34c1", "PutFile", SEND, "file:/tmp/nifi/a.txt", flowFileUUIDA));// 7

        prs.add(SimpleProvenanceRecord.pr("93f8ad14-6ee6-34c1", "PutFile", SEND, "file:/tmp/nifi/bc.txt", flowFileUUIDBC));// 8

        prs.add(SimpleProvenanceRecord.pr("93f8ad14-6ee6-34c1", "PutFile", SEND, "file:/tmp/nifi/d.txt", flowFileUUIDD));// 9

        prs.add(SimpleProvenanceRecord.pr("bfc30bc3-48cf-332a", "LogAttribute", DROP, flowFileUUIDA));// 10

        prs.add(SimpleProvenanceRecord.pr("bfc30bc3-48cf-332a", "LogAttribute", DROP, flowFileUUIDBC));// 11

        prs.add(SimpleProvenanceRecord.pr("bfc30bc3-48cf-332a", "LogAttribute", DROP, flowFileUUIDD));// 12

        Map<Long, ComputeLineageResult> lineages = tc.lineageResults;
        final ComputeLineageResult lineageB = createLineage(prs, 1, 4, 5);
        final ComputeLineageResult lineageC = createLineage(prs, 2, 4, 6);
        lineages.put(5L, lineageB);// B

        lineages.put(6L, lineageC);// C

        lineages.put(10L, createLineage(prs, 0, 7, 10));// A

        lineages.put(11L, createLineage(prs, 4, 8, 11));// BC

        lineages.put(12L, createLineage(prs, 3, 9, 12));// D

        Map<Long, ComputeLineageResult> parents = tc.parentLineageResults;
        parents.put(4L, compositeLineages(lineageB, lineageC));
        test(tc);
        waitNotificationsGetDelivered();
        final Lineage lineage = getLineage();
        final Node genA = lineage.findNode("nifi_data", "Generate A", "f585d83b-2a03-37cf");
        final Node genB = lineage.findNode("nifi_data", "Generate B", "59a7c1f9-9a73-3cc6");
        final Node genC = lineage.findNode("nifi_data", "Generate C", "d6c3f282-e03d-316c");
        final Node genD = lineage.findNode("nifi_data", "Generate D", "f9593a5a-f0d5-3e87");
        final Node genAPath = lineage.findNode("nifi_flow_path", "Generate A, PutFile, LogAttribute", "f585d83b-2a03-37cf-0000-000000000000::1003499964@example");
        final Node genBPath = lineage.findNode("nifi_flow_path", "Generate B", "59a7c1f9-9a73-3cc6-0000-000000000000::45412830@example");
        final Node genCPath = lineage.findNode("nifi_flow_path", "Generate C", "d6c3f282-e03d-316c-0000-000000000000::1968410985@example");
        final Node genDPath = lineage.findNode("nifi_flow_path", "Generate D, PutFile, LogAttribute", "f9593a5a-f0d5-3e87-0000-000000000000::4257576567@example");
        lineage.assertLink(genA, genAPath);
        lineage.assertLink(genB, genBPath);
        lineage.assertLink(genC, genCPath);
        lineage.assertLink(genD, genDPath);
        // B and C were merged together, while A and D were processed individually.
        final Node joinBC = lineage.findNode("nifi_queue", "JOIN", "c77dd033-bb9e-39ea-0000-000000000000::2370367315@example");
        final Node bcPath = lineage.findNode("nifi_flow_path", "MergeContent, PutFile, LogAttribute", "c77dd033-bb9e-39ea-0000-000000000000::2370367315@example");
        lineage.assertLink(genBPath, joinBC);
        lineage.assertLink(genCPath, joinBC);
        lineage.assertLink(joinBC, bcPath);
        final Node outA = lineage.findNode("fs_path", "/tmp/nifi/a.txt@example");
        final Node outBC = lineage.findNode("fs_path", "/tmp/nifi/bc.txt@example");
        final Node outD = lineage.findNode("fs_path", "/tmp/nifi/d.txt@example");
        lineage.assertLink(genAPath, outA);
        lineage.assertLink(bcPath, outBC);
        lineage.assertLink(genDPath, outD);
    }

    @Test
    public void testRecordAndDataSetLevel() throws Exception {
        final ITReportLineageToAtlas.TestConfiguration tc = new ITReportLineageToAtlas.TestConfiguration("RecordAndDataSetLevel");
        tc.properties.put(ReportLineageToAtlas.NIFI_LINEAGE_STRATEGY, ReportLineageToAtlas.LINEAGE_STRATEGY_COMPLETE_PATH.getValue());
        final ITReportLineageToAtlas.ProvenanceRecords prs = tc.provenanceRecords;
        // Publish part
        final String ffIdA1 = "A1000000";
        final String ffIdB1 = "B1000000";
        prs.add(SimpleProvenanceRecord.pr("22be62d9-c4a1-3056", "GetFile", RECEIVE, "file:/tmp/input/A1.csv", ffIdA1));// 0

        prs.add(SimpleProvenanceRecord.pr("22be62d9-c4a1-3056", "GetFile", RECEIVE, "file:/tmp/input/B1.csv", ffIdB1));// 1

        prs.add(SimpleProvenanceRecord.pr("eaf013c1-aec5-39b0", "PutFile", SEND, "file:/tmp/output/A1.csv", ffIdA1));// 2

        prs.add(SimpleProvenanceRecord.pr("eaf013c1-aec5-39b0", "PutFile", SEND, "file:/tmp/output/B1.csv", ffIdB1));// 3

        prs.add(SimpleProvenanceRecord.pr("97641de3-fb76-3d95", "PublishKafkaRecord_0_10", SEND, "PLAINTEXT://localhost:9092/nifi-test", ffIdA1));// 4

        prs.add(SimpleProvenanceRecord.pr("97641de3-fb76-3d95", "PublishKafkaRecord_0_10", SEND, "PLAINTEXT://localhost:9092/nifi-test", ffIdB1));// 5

        prs.add(SimpleProvenanceRecord.pr("97641de3-fb76-3d95", "PublishKafkaRecord_0_10", DROP, ffIdA1));// 6

        prs.add(SimpleProvenanceRecord.pr("97641de3-fb76-3d95", "PublishKafkaRecord_0_10", DROP, ffIdB1));// 7

        // Consume part
        final String ffIdK1 = "K1000000";
        final String ffIdA2 = "A2000000";// Forked children

        final String ffIdB2 = "B2000000";// Forked children

        prs.add(SimpleProvenanceRecord.pr("529e6722-9b49-3b66", "ConsumeKafkaRecord_0_10", RECEIVE, "PLAINTEXT://localhost:9092/nifi-test", ffIdK1));// 8

        prs.add(SimpleProvenanceRecord.pr("3f6d405e-6e3d-38c9", "PartitionRecord", FORK, ffIdK1));// 9

        prs.add(SimpleProvenanceRecord.pr("db8bb12c-5cd3-3011", "UpdateAttribute", ATTRIBUTES_MODIFIED, ffIdA2));// 10

        prs.add(SimpleProvenanceRecord.pr("db8bb12c-5cd3-3011", "UpdateAttribute", ATTRIBUTES_MODIFIED, ffIdB2));// 11

        prs.add(SimpleProvenanceRecord.pr("062caf95-da40-3a57", "PutFile", SEND, "file:/tmp/consumed/A_20171101_100701.csv", ffIdA2));// 12

        prs.add(SimpleProvenanceRecord.pr("062caf95-da40-3a57", "PutFile", SEND, "file:/tmp/consumed/B_20171101_100701.csv", ffIdB2));// 13

        prs.add(SimpleProvenanceRecord.pr("062caf95-da40-3a57", "PutFile", DROP, ffIdA2));// 14

        prs.add(SimpleProvenanceRecord.pr("062caf95-da40-3a57", "PutFile", DROP, ffIdB2));// 15

        Map<Long, ComputeLineageResult> lineages = tc.lineageResults;
        Map<Long, ComputeLineageResult> parents = tc.parentLineageResults;
        lineages.put(6L, createLineage(prs, 0, 2, 4, 6));// Publish A1

        lineages.put(7L, createLineage(prs, 1, 3, 5, 7));// Publish B1

        parents.put(9L, createLineage(prs, 8, 9));// Consumed and Forked K1

        lineages.put(14L, createLineage(prs, 9, 10, 12, 14));// Processed A2

        lineages.put(15L, createLineage(prs, 9, 11, 13, 15));// Processed B2

        test(tc);
        waitNotificationsGetDelivered();
        final Lineage lineage = getLineage();
        // Publish part
        final Node inputFileA1 = lineage.findNode("fs_path", "/tmp/input/A1.csv@example");
        final Node inputFileB1 = lineage.findNode("fs_path", "/tmp/input/B1.csv@example");
        // These two flow paths are derived from the same set of Processors, but with different input files, and resulted different hashes.
        final Node getFileToPublishKafkaA = lineage.findNode("nifi_flow_path", "GetFile, PutFile, PublishKafkaRecord_0_10", "22be62d9-c4a1-3056-0000-000000000000::2823953997@example");
        final Node getFileToPublishKafkaB = lineage.findNode("nifi_flow_path", "GetFile, PutFile, PublishKafkaRecord_0_10", "22be62d9-c4a1-3056-0000-000000000000::568010061@example");
        lineage.assertLink(inputFileA1, getFileToPublishKafkaA);
        lineage.assertLink(inputFileB1, getFileToPublishKafkaB);
        final Node nifiTestTopic = lineage.findNode("kafka_topic", "nifi-test@example");
        final Node outputFileA = lineage.findNode("fs_path", "/tmp/output/A1.csv@example");
        final Node outputFileB = lineage.findNode("fs_path", "/tmp/output/B1.csv@example");
        lineage.assertLink(getFileToPublishKafkaA, nifiTestTopic);
        lineage.assertLink(getFileToPublishKafkaB, nifiTestTopic);
        lineage.assertLink(getFileToPublishKafkaA, outputFileA);
        lineage.assertLink(getFileToPublishKafkaB, outputFileB);
        // Consume part
        final Node consumeNifiTestTopic = lineage.findNode("nifi_flow_path", "ConsumeKafkaRecord_0_10", "529e6722-9b49-3b66-0000-000000000000::3649132843@example");
        final Node forkedA = lineage.findNode("nifi_queue", "FORK", "3f6d405e-6e3d-38c9-0000-000000000000::234149075@example");
        final Node forkedB = lineage.findNode("nifi_queue", "FORK", "3f6d405e-6e3d-38c9-0000-000000000000::2377021542@example");
        lineage.assertLink(consumeNifiTestTopic, forkedA);
        lineage.assertLink(consumeNifiTestTopic, forkedB);
        final Node partitionToPutA = lineage.findNode("nifi_flow_path", "PartitionRecord, UpdateAttribute, PutFile", "3f6d405e-6e3d-38c9-0000-000000000000::234149075@example");
        final Node partitionToPutB = lineage.findNode("nifi_flow_path", "PartitionRecord, UpdateAttribute, PutFile", "3f6d405e-6e3d-38c9-0000-000000000000::2377021542@example");
        final Node consumedFileA = lineage.findNode("fs_path", "/tmp/consumed/A_20171101_100701.csv@example");
        final Node consumedFileB = lineage.findNode("fs_path", "/tmp/consumed/B_20171101_100701.csv@example");
        lineage.assertLink(forkedA, partitionToPutA);
        lineage.assertLink(forkedB, partitionToPutB);
        lineage.assertLink(partitionToPutA, consumedFileA);
        lineage.assertLink(partitionToPutB, consumedFileB);
    }

    @Test
    public void testMultiInAndOuts() throws Exception {
        final ITReportLineageToAtlas.TestConfiguration tc = new ITReportLineageToAtlas.TestConfiguration("MultiInAndOuts");
        final ITReportLineageToAtlas.ProvenanceRecords prs = tc.provenanceRecords;
        test(tc);
        waitNotificationsGetDelivered();
        final Lineage lineage = getLineage();
        final Node gen1 = lineage.findNode("nifi_flow_path", "Gen1", "a4bfe4ec-570b-3126");
        final Node gen2 = lineage.findNode("nifi_flow_path", "Gen2", "894218d5-dfe9-3ee5");
        final Node ua1 = lineage.findNode("nifi_flow_path", "UA1", "5609cb4f-8a95-3b7a");
        final Node ua2 = lineage.findNode("nifi_flow_path", "UA2", "6f88b3d9-5723-356a");
        final Node ua3 = lineage.findNode("nifi_flow_path", "UA3, UA4, LogAttribute", "3250aeb6-4026-3969");
        final Node ua1Q = lineage.findNode("nifi_queue", "queue", "5609cb4f-8a95-3b7a");
        final Node ua2Q = lineage.findNode("nifi_queue", "queue", "6f88b3d9-5723-356a");
        final Node ua3Q = lineage.findNode("nifi_queue", "queue", "3250aeb6-4026-3969");
        lineage.assertLink(gen1, ua1Q);
        lineage.assertLink(gen1, ua2Q);
        lineage.assertLink(gen2, ua2Q);
        lineage.assertLink(ua1Q, ua1);
        lineage.assertLink(ua2Q, ua2);
        lineage.assertLink(ua1, ua3Q);
        lineage.assertLink(ua2, ua3Q);
        lineage.assertLink(ua3Q, ua3);
    }
}

