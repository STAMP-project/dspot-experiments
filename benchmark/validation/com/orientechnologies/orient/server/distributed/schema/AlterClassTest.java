package com.orientechnologies.orient.server.distributed.schema;


import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.server.distributed.AbstractServerClusterTest;
import org.junit.Ignore;
import org.junit.Test;


@Ignore
public class AlterClassTest extends AbstractServerClusterTest {
    private OClass oClass;

    @Test
    public void test() throws Exception {
        init(2);
        prepare(true);
        execute();
    }
}

