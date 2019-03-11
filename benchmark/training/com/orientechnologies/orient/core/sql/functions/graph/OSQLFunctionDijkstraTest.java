package com.orientechnologies.orient.core.sql.functions.graph;


import com.orientechnologies.orient.core.command.OBasicCommandContext;
import com.orientechnologies.orient.core.db.OrientDB;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.record.OVertex;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class OSQLFunctionDijkstraTest {
    private OrientDB orientDB;

    private ODatabaseDocument graph;

    private OVertex v1;

    private OVertex v2;

    private OVertex v3;

    private OVertex v4;

    private OSQLFunctionDijkstra functionDijkstra;

    @Test
    public void testExecute() throws Exception {
        final List<OVertex> result = functionDijkstra.execute(null, null, null, new Object[]{ v1, v4, "'weight'" }, new OBasicCommandContext());
        Assert.assertEquals(4, result.size());
        Assert.assertEquals(v1, result.get(0));
        Assert.assertEquals(v2, result.get(1));
        Assert.assertEquals(v3, result.get(2));
        Assert.assertEquals(v4, result.get(3));
    }
}

