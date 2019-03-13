/**
 * Copyright 2017 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.navercorp.pinpoint.web.applicationmap.appender.server;


import ServiceType.TEST_STAND_ALONE;
import com.navercorp.pinpoint.common.trace.ServiceType;
import com.navercorp.pinpoint.common.trace.ServiceTypeFactory;
import com.navercorp.pinpoint.web.applicationmap.appender.server.datasource.ServerInstanceListDataSource;
import com.navercorp.pinpoint.web.applicationmap.nodes.Node;
import com.navercorp.pinpoint.web.applicationmap.nodes.NodeList;
import com.navercorp.pinpoint.web.applicationmap.nodes.ServerInstanceList;
import com.navercorp.pinpoint.web.applicationmap.rawdata.LinkData;
import com.navercorp.pinpoint.web.applicationmap.rawdata.LinkDataDuplexMap;
import com.navercorp.pinpoint.web.vo.Application;
import com.navercorp.pinpoint.web.vo.Range;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 *
 *
 * @author HyunGil Jeong
 */
public class ServerInfoAppenderTest {
    private final ExecutorService executor = Executors.newFixedThreadPool(4);

    private final ServerInfoAppenderFactory serverInfoAppenderFactory = new ServerInfoAppenderFactory(executor);

    private ServerInstanceListDataSource serverInstanceListDataSource;

    private ServerInfoAppender serverInfoAppender;

    @Test
    public void nullNodeList() {
        // Given
        Range range = new Range(0, (60 * 1000));
        NodeList nodeList = null;
        LinkDataDuplexMap linkDataDuplexMap = Mockito.mock(LinkDataDuplexMap.class);
        // When
        serverInfoAppender.appendServerInfo(range, nodeList, linkDataDuplexMap);
        // Then
        Assert.assertNull(nodeList);
        Mockito.verifyZeroInteractions(serverInstanceListDataSource);
        Mockito.verifyZeroInteractions(linkDataDuplexMap);
    }

    @Test
    public void emptyNodeList() {
        // Given
        Range range = new Range(0, (60 * 1000));
        NodeList nodeList = new NodeList();
        LinkDataDuplexMap linkDataDuplexMap = Mockito.mock(LinkDataDuplexMap.class);
        // When
        serverInfoAppender.appendServerInfo(range, nodeList, linkDataDuplexMap);
        // Then
        Assert.assertTrue(nodeList.getNodeList().isEmpty());
        Mockito.verifyZeroInteractions(serverInstanceListDataSource);
        Mockito.verifyZeroInteractions(linkDataDuplexMap);
    }

    @Test
    public void wasNode() {
        // Given
        Range range = new Range(0, (60 * 1000));
        NodeList nodeList = new NodeList();
        LinkDataDuplexMap linkDataDuplexMap = Mockito.mock(LinkDataDuplexMap.class);
        Node wasNode = new Node(new Application("Was", ServiceType.TEST_STAND_ALONE));
        nodeList.addNode(wasNode);
        ServerInstanceList serverInstanceList = new ServerInstanceList();
        Mockito.when(serverInstanceListDataSource.createServerInstanceList(wasNode, range.getTo())).thenReturn(serverInstanceList);
        // When
        serverInfoAppender.appendServerInfo(range, nodeList, linkDataDuplexMap);
        // Then
        Assert.assertSame(serverInstanceList, wasNode.getServerInstanceList());
        Mockito.verifyZeroInteractions(linkDataDuplexMap);
    }

    @Test
    public void wasNodes() {
        // Given
        Range range = new Range(0, (60 * 1000));
        NodeList nodeList = new NodeList();
        LinkDataDuplexMap linkDataDuplexMap = Mockito.mock(LinkDataDuplexMap.class);
        Node wasNode1 = new Node(new Application("Was1", ServiceType.TEST_STAND_ALONE));
        nodeList.addNode(wasNode1);
        Node wasNode2 = new Node(new Application("Was2", ServiceType.TEST_STAND_ALONE));
        nodeList.addNode(wasNode2);
        ServerInstanceList serverInstanceList1 = new ServerInstanceList();
        Mockito.when(serverInstanceListDataSource.createServerInstanceList(wasNode1, range.getTo())).thenReturn(serverInstanceList1);
        ServerInstanceList serverInstanceList2 = new ServerInstanceList();
        Mockito.when(serverInstanceListDataSource.createServerInstanceList(wasNode2, range.getTo())).thenReturn(serverInstanceList2);
        // When
        serverInfoAppender.appendServerInfo(range, nodeList, linkDataDuplexMap);
        // Then
        Assert.assertSame(serverInstanceList1, wasNode1.getServerInstanceList());
        Assert.assertSame(serverInstanceList2, wasNode2.getServerInstanceList());
        Mockito.verifyZeroInteractions(linkDataDuplexMap);
    }

    @Test
    public void terminalNode() {
        // Given
        Range range = new Range(0, (60 * 1000));
        NodeList nodeList = new NodeList();
        LinkDataDuplexMap linkDataDuplexMap = new LinkDataDuplexMap();
        ServiceType terminalType = ServiceTypeFactory.of(2000, "TERMINAL", TERMINAL, INCLUDE_DESTINATION_ID);
        Application terminalApplication = new Application("Terminal", terminalType);
        Node terminalNode = new Node(terminalApplication);
        nodeList.addNode(terminalNode);
        Application fromApplication = new Application("FromWas", ServiceType.TEST_STAND_ALONE);
        LinkData linkData = new LinkData(fromApplication, terminalApplication);
        linkData.addLinkData("wasAgent", TEST_STAND_ALONE, "terminalNodeAddress", terminalType, System.currentTimeMillis(), terminalType.getHistogramSchema().getNormalSlot().getSlotTime(), 1);
        linkDataDuplexMap.addSourceLinkData(linkData);
        // When
        serverInfoAppender.appendServerInfo(range, nodeList, linkDataDuplexMap);
        // Then
        Assert.assertEquals(1, terminalNode.getServerInstanceList().getInstanceCount());
    }

    @Test
    public void terminalNode_multipleInstances() {
        // Given
        Range range = new Range(0, (60 * 1000));
        NodeList nodeList = new NodeList();
        LinkDataDuplexMap linkDataDuplexMap = new LinkDataDuplexMap();
        ServiceType terminalType = ServiceTypeFactory.of(2000, "TERMINAL", TERMINAL, INCLUDE_DESTINATION_ID);
        Application terminalApplication = new Application("Terminal", terminalType);
        Node terminalNode = new Node(terminalApplication);
        nodeList.addNode(terminalNode);
        Application fromApplication = new Application("FromWas", ServiceType.TEST_STAND_ALONE);
        LinkData linkData = new LinkData(fromApplication, terminalApplication);
        linkData.addLinkData("wasAgent", TEST_STAND_ALONE, "terminalNodeAddress1", terminalType, System.currentTimeMillis(), terminalType.getHistogramSchema().getNormalSlot().getSlotTime(), 1);
        linkData.addLinkData("wasAgent", TEST_STAND_ALONE, "terminalNodeAddress2", terminalType, System.currentTimeMillis(), terminalType.getHistogramSchema().getNormalSlot().getSlotTime(), 1);
        linkDataDuplexMap.addSourceLinkData(linkData);
        // When
        serverInfoAppender.appendServerInfo(range, nodeList, linkDataDuplexMap);
        // Then
        Assert.assertEquals(2, terminalNode.getServerInstanceList().getInstanceCount());
    }

    @Test
    public void userNode() {
        // Given
        Range range = new Range(0, (60 * 1000));
        NodeList nodeList = new NodeList();
        LinkDataDuplexMap linkDataDuplexMap = Mockito.mock(LinkDataDuplexMap.class);
        Node userNode = new Node(new Application("User", ServiceType.USER));
        nodeList.addNode(userNode);
        // When
        serverInfoAppender.appendServerInfo(range, nodeList, linkDataDuplexMap);
        // Then
        Assert.assertEquals(0, userNode.getServerInstanceList().getInstanceCount());
        Mockito.verifyZeroInteractions(linkDataDuplexMap);
    }

    @Test
    public void unknownNode() {
        // Given
        Range range = new Range(0, (60 * 1000));
        NodeList nodeList = new NodeList();
        LinkDataDuplexMap linkDataDuplexMap = Mockito.mock(LinkDataDuplexMap.class);
        Node unknownNode = new Node(new Application("Unknown", ServiceType.UNKNOWN));
        nodeList.addNode(unknownNode);
        // When
        serverInfoAppender.appendServerInfo(range, nodeList, linkDataDuplexMap);
        // Then
        Assert.assertEquals(0, unknownNode.getServerInstanceList().getInstanceCount());
        Mockito.verifyZeroInteractions(linkDataDuplexMap);
    }
}

