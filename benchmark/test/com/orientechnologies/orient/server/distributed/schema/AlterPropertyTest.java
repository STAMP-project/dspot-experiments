package com.orientechnologies.orient.server.distributed.schema;


import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.server.distributed.AbstractServerClusterTest;
import org.junit.Test;


public class AlterPropertyTest extends AbstractServerClusterTest {
    private OProperty property;

    @Test
    public void test() throws Exception {
        init(2);
        prepare(true);
        execute();
    }
}

