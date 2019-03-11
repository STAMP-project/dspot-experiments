package com.orientechnologies.orient.core.metadata.function;


import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by tglman on 10/02/16.
 */
public class OFunctionLibraryTest {
    private ODatabaseDocument db;

    @Test
    public void testSimpleFunctionCreate() {
        OFunction func = db.getMetadata().getFunctionLibrary().createFunction("TestFunc");
        Assert.assertNotNull(func);
        func = db.getMetadata().getFunctionLibrary().getFunction("TestFunc");
        Assert.assertNotNull(func);
    }

    @Test(expected = OFunctionDuplicatedException.class)
    public void testDuplicateFunctionCreate() {
        OFunction func = db.getMetadata().getFunctionLibrary().createFunction("TestFunc");
        Assert.assertNotNull(func);
        db.getMetadata().getFunctionLibrary().createFunction("TestFunc");
    }

    @Test
    public void testFunctionCreateDrop() {
        OFunction func = db.getMetadata().getFunctionLibrary().createFunction("TestFunc");
        Assert.assertNotNull(func);
        func = db.getMetadata().getFunctionLibrary().getFunction("TestFunc");
        Assert.assertNotNull(func);
        db.getMetadata().getFunctionLibrary().dropFunction("TestFunc");
        func = db.getMetadata().getFunctionLibrary().getFunction("TestFunc");
        Assert.assertNull(func);
        func = db.getMetadata().getFunctionLibrary().createFunction("TestFunc1");
        db.getMetadata().getFunctionLibrary().dropFunction(func);
        func = db.getMetadata().getFunctionLibrary().getFunction("TestFunc");
        Assert.assertNull(func);
    }
}

