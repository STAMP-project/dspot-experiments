/**
 * (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wps.jdbc;


import Ows11Factory.eINSTANCE;
import net.opengis.ows11.CodeType;
import net.opengis.wps10.ComplexDataType;
import net.opengis.wps10.DataInputsType1;
import net.opengis.wps10.DataType;
import net.opengis.wps10.ExecuteType;
import net.opengis.wps10.InputType;
import net.opengis.wps10.Wps10Factory;
import org.geoserver.wps.AbstractProcessStoreTest;
import org.geoserver.wps.executor.ExecutionStatus;
import org.geotools.data.DataStore;
import org.geotools.feature.NameImpl;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests the JDBC based process status store with a single instance
 *
 * @author Ian Turton
 */
public abstract class AbstractJDBCStatusStoreTest extends AbstractProcessStoreTest {
    private DataStore datastore;

    JDBCStatusStore statusStore;

    String fixtureId;

    @Test
    public void testStackTrace() {
        ExecutionStatus s = new ExecutionStatus(new NameImpl("tracetest"), "ian", false);
        s.setException(new IllegalArgumentException("a test exception"));
        store.save(s);
        ExecutionStatus status = store.get(s.getExecutionId());
        Assert.assertEquals(s, status);
        Assert.assertEquals(s.getException().getMessage(), status.getException().getMessage());
        StackTraceElement[] expStackTrace = s.getException().getStackTrace();
        StackTraceElement[] obsStackTrace = status.getException().getStackTrace();
        Assert.assertEquals(expStackTrace.length, obsStackTrace.length);
        for (int i = 0; i < (obsStackTrace.length); i++) {
            Assert.assertEquals(expStackTrace[i], obsStackTrace[i]);
        }
        store.remove(s.getExecutionId());
    }

    @Test
    public void testRequest() {
        Wps10Factory f = Wps10Factory.eINSTANCE;
        ExecuteType ex = f.createExecuteType();
        CodeType id = eINSTANCE.createCodeType();
        ex.setIdentifier(id);
        id.setValue("foo");
        DataInputsType1 inputs = f.createDataInputsType1();
        ex.setDataInputs(inputs);
        InputType in = f.createInputType();
        inputs.getInput().add(in);
        DataType data = f.createDataType();
        in.setData(data);
        ComplexDataType cd = f.createComplexDataType();
        data.setComplexData(cd);
        ExecutionStatus s = new ExecutionStatus(new NameImpl("requesttest"), "ian", false);
        s.setRequest(ex);
        store.save(s);
        ExecutionStatus status = store.get(s.getExecutionId());
        Assert.assertEquals(s, status);
        ExecuteType obs = status.getRequest();
        ExecuteType expected = s.getRequest();
        Assert.assertEquals(expected.getBaseUrl(), obs.getBaseUrl());
        Assert.assertEquals(expected.getIdentifier().getValue(), obs.getIdentifier().getValue());
    }
}

