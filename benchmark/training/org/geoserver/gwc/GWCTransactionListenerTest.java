/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.gwc;


import GWCTransactionListener.GWC_TRANSACTION_INFO_PLACEHOLDER;
import TransactionEventType.POST_INSERT;
import TransactionEventType.PRE_INSERT;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;
import junit.framework.TestCase;
import net.opengis.wfs.InsertElementType;
import org.geoserver.wfs.TransactionEvent;
import org.geoserver.wfs.request.TransactionRequest;
import org.geoserver.wfs.request.TransactionResponse;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.geometry.jts.ReferencedEnvelope3D;
import org.geotools.referencing.CRS;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.opengis.referencing.crs.CoordinateReferenceSystem;


public class GWCTransactionListenerTest {
    private GWC mediator;

    private GWCTransactionListener listener;

    @Test
    public void testNoInteractionsInUnusedMethods() {
        TransactionRequest request = Mockito.mock(TransactionRequest.class);
        TransactionRequest returned = listener.beforeTransaction(request);
        Assert.assertSame(request, returned);
        Mockito.verifyNoMoreInteractions(request, mediator);
        listener.beforeCommit(request);
        Mockito.verifyNoMoreInteractions(request, mediator);
    }

    @Test
    public void testAfterTransactionUncommitted() {
        TransactionRequest request = Mockito.mock(TransactionRequest.class);
        TransactionResponse result = Mockito.mock(TransactionResponse.class);
        boolean committed = false;
        listener.afterTransaction(request, result, committed);
        Mockito.verifyNoMoreInteractions(request, result, mediator);
    }

    @Test
    public void testDataStoreChangeDoesNotPropagateExceptions() {
        TransactionEvent event = Mockito.mock(TransactionEvent.class);
        Mockito.when(event.getSource()).thenThrow(new RuntimeException("fake"));
        try {
            listener.dataStoreChange(event);
        } catch (RuntimeException e) {
            Assert.fail("Exception should have been eaten to prevent the transaction from failing due to a gwc integration error");
        }
    }

    @Test
    public void testDataStoreChangeOfNoInterest() {
        TransactionEvent event = Mockito.mock(TransactionEvent.class);
        Mockito.when(event.getSource()).thenReturn(new Object());
        listener.dataStoreChange(event);
        Mockito.verify(event, Mockito.times(1)).getLayerName();
        Mockito.verify(event, Mockito.times(1)).getType();
        Mockito.verify(event, Mockito.times(1)).getSource();
        Mockito.verifyNoMoreInteractions(event, mediator);
    }

    @Test
    public void testDataStoreChangePostInsert() {
        InsertElementType insert = Mockito.mock(InsertElementType.class);
        TransactionEvent event = Mockito.mock(TransactionEvent.class);
        QName layerName = new QName("testType");
        Mockito.when(event.getLayerName()).thenReturn(layerName);
        Mockito.when(event.getSource()).thenReturn(insert);
        Mockito.when(event.getType()).thenReturn(POST_INSERT);
        listener.dataStoreChange(event);
        // no need to do anything at post insert, bounds computed at pre_insert
        Mockito.verifyNoMoreInteractions(mediator);
    }

    @Test
    public void testDataStoreChangeDoesNotAffectTileLayer() {
        InsertElementType insert = Mockito.mock(InsertElementType.class);
        TransactionEvent event = Mockito.mock(TransactionEvent.class);
        QName layerName = new QName("testType");
        Mockito.when(event.getLayerName()).thenReturn(layerName);
        Mockito.when(event.getSource()).thenReturn(insert);
        Mockito.when(event.getType()).thenReturn(PRE_INSERT);
        Mockito.when(mediator.getTileLayersByFeatureType(ArgumentMatchers.eq(layerName.getNamespaceURI()), ArgumentMatchers.eq(layerName.getLocalPart()))).thenReturn(Collections.EMPTY_SET);
        listener.dataStoreChange(event);
        // nothing else to do
        Mockito.verify(mediator, Mockito.times(1)).getTileLayersByFeatureType(ArgumentMatchers.eq(layerName.getNamespaceURI()), ArgumentMatchers.eq(layerName.getLocalPart()));
        Mockito.verifyNoMoreInteractions(mediator);
    }

    @Test
    public void testDataStoreChangeInsert() {
        Map<Object, Object> extendedProperties = new HashMap<Object, Object>();
        ReferencedEnvelope affectedBounds = new ReferencedEnvelope((-180), 0, 0, 90, WGS84);
        issueInsert(extendedProperties, affectedBounds);
        TestCase.assertTrue(extendedProperties.containsKey(GWC_TRANSACTION_INFO_PLACEHOLDER));
        @SuppressWarnings("unchecked")
        Map<String, List<ReferencedEnvelope>> placeHolder = ((Map<String, List<ReferencedEnvelope>>) (extendedProperties.get(GWC_TRANSACTION_INFO_PLACEHOLDER)));
        Assert.assertNotNull(placeHolder.get("theLayer"));
        Assert.assertSame(affectedBounds, placeHolder.get("theLayer").get(0));
        Assert.assertSame(affectedBounds, placeHolder.get("theGroup").get(0));
    }

    @Test
    public void testAfterTransactionCompoundCRS() throws Exception {
        Map<Object, Object> extendedProperties = new HashMap<Object, Object>();
        final CoordinateReferenceSystem compoundCrs = CRS.decode("EPSG:7415");
        ReferencedEnvelope3D transactionBounds = new ReferencedEnvelope3D(142892, 470783, 142900, 470790, 16, 20, compoundCrs);
        issueInsert(extendedProperties, transactionBounds);
        TransactionRequest request = Mockito.mock(TransactionRequest.class);
        TransactionResponse result = Mockito.mock(TransactionResponse.class);
        Mockito.when(request.getExtendedProperties()).thenReturn(extendedProperties);
        Mockito.when(mediator.getDeclaredCrs(ArgumentMatchers.anyString())).thenReturn(compoundCrs);
        listener.afterTransaction(request, result, true);
        ReferencedEnvelope expectedBounds = new ReferencedEnvelope(transactionBounds, CRS.getHorizontalCRS(compoundCrs));
        Mockito.verify(mediator, Mockito.times(1)).truncate(ArgumentMatchers.eq("theLayer"), ArgumentMatchers.eq(expectedBounds));
        Mockito.verify(mediator, Mockito.times(1)).truncate(ArgumentMatchers.eq("theGroup"), ArgumentMatchers.eq(expectedBounds));
    }

    @Test
    public void testAfterTransaction() throws Exception {
        Map<Object, Object> extendedProperties = new HashMap<Object, Object>();
        ReferencedEnvelope affectedBounds1 = new ReferencedEnvelope((-180), 0, 0, 90, WGS84);
        ReferencedEnvelope affectedBounds2 = new ReferencedEnvelope(0, 180, 0, 90, WGS84);
        issueInsert(extendedProperties, affectedBounds1);
        issueInsert(extendedProperties, affectedBounds2);
        TransactionRequest request = Mockito.mock(TransactionRequest.class);
        TransactionResponse result = Mockito.mock(TransactionResponse.class);
        Mockito.when(request.getExtendedProperties()).thenReturn(extendedProperties);
        Mockito.when(mediator.getDeclaredCrs(ArgumentMatchers.anyString())).thenReturn(WGS84);
        listener.afterTransaction(request, result, true);
        ReferencedEnvelope expectedEnv = new ReferencedEnvelope(affectedBounds1);
        expectedEnv.expandToInclude(affectedBounds2);
        Mockito.verify(mediator, Mockito.times(1)).truncate(ArgumentMatchers.eq("theLayer"), ArgumentMatchers.eq(expectedEnv));
        Mockito.verify(mediator, Mockito.times(1)).truncate(ArgumentMatchers.eq("theGroup"), ArgumentMatchers.eq(expectedEnv));
    }
}

