package com.orientechnologies.orient.core.exception;


import com.orientechnologies.orient.core.id.ORecordId;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by tglman on 22/02/17.
 */
public class RecordNotFoundExceptionTest {
    @Test
    public void simpleExceptionCopyTest() {
        ORecordNotFoundException ex = new ORecordNotFoundException(new ORecordId(1, 2));
        ORecordNotFoundException ex1 = new ORecordNotFoundException(ex);
        Assert.assertNotNull(ex1.getRid());
        Assert.assertEquals(ex1.getRid().getClusterId(), 1);
        Assert.assertEquals(ex1.getRid().getClusterPosition(), 2);
    }
}

