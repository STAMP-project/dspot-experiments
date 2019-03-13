package com.tinkerpop.blueprints.oupls.sail;


import GraphSail.VALUE;
import RDF.TYPE;
import RDFFormat.TRIG;
import RDFS.LABEL;
import RDFS.SUBCLASSOF;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.KeyIndexableGraph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.tg.TinkerGraph;
import info.aduna.iteration.CloseableIteration;
import junit.framework.Assert;
import org.junit.Test;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.impl.EmptyBindingSet;
import org.openrdf.query.parser.ParsedQuery;
import org.openrdf.query.parser.sparql.SPARQLParser;
import org.openrdf.sail.Sail;
import org.openrdf.sail.SailConnection;
import org.openrdf.sail.SailException;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;


/**
 *
 *
 * @author Joshua Shinavier (http://fortytwo.net)
 */
public abstract class GraphSailTest extends SailTest {
    protected KeyIndexableGraph graph;

    @Test
    public void testIsolatedVerticesAutomaticallyDeleted() throws Exception {
        String ex = "http://example.org/ns#";
        URI ref = new URIImpl((ex + "Ref"));
        clear();
        int edgesBefore;
        int verticesBefore;
        SailConnection sc = sail.getConnection();
        try {
            sc.begin();
            edgesBefore = countEdges();
            verticesBefore = countVertices();
        } finally {
            sc.commit();
            sc.close();
        }
        addFile(SailTest.class.getResourceAsStream("graph-example-bnodes.trig"), TRIG);
        sc = sail.getConnection();
        // showStatements(sc, null, null, null);
        try {
            sc.begin();
            Assert.assertEquals(14, countStatements(sc, null, null, null, false));
            Assert.assertEquals((edgesBefore + 14), countEdges());
            Assert.assertEquals((verticesBefore + 10), countVertices());
            sc.removeStatements(ref, null, null);
            sc.commit();
            sc.begin();
            Assert.assertEquals(13, countStatements(sc, null, null, null, false));
            Assert.assertEquals((edgesBefore + 13), countEdges());
            Assert.assertEquals((verticesBefore + 9), countVertices());
            sc.clear();
            sc.commit();
            sc.begin();
            Assert.assertEquals(0, countStatements(sc, null, null, null, false));
            Assert.assertEquals(0, countEdges());
            // Namespaces vertex is still present.
            Assert.assertEquals(verticesBefore, countVertices());
        } finally {
            sc.rollback();
            sc.close();
        }
    }

    @Test
    public void testBlankNodesUnique() throws Exception {
        String ex = "http://example.org/ns#";
        URI class1 = new URIImpl((ex + "Class1"));
        clear();
        int edgesBefore;
        int verticesBefore;
        SailConnection sc = sail.getConnection();
        try {
            sc.begin();
            edgesBefore = countEdges();
            verticesBefore = countVertices();
        } finally {
            sc.rollback();
            sc.close();
        }
        // Load a file once.
        addFile(SailTest.class.getResourceAsStream("graph-example-bnodes.trig"), TRIG);
        sc = sail.getConnection();
        try {
            Assert.assertEquals(3, countStatements(sc, class1, SUBCLASSOF, null, false));
            Assert.assertEquals((edgesBefore + 14), countEdges());
            Assert.assertEquals((verticesBefore + 10), countVertices());
        } finally {
            sc.rollback();
            sc.close();
        }
        // Load the file again.
        // Loading the same file twice results in extra vertices and edges,
        // since blank nodes assume different identities on each load.
        addFile(SailTest.class.getResourceAsStream("graph-example-bnodes.trig"), TRIG);
        sc = sail.getConnection();
        try {
            Assert.assertEquals(5, countStatements(sc, class1, SUBCLASSOF, null, false));
            Assert.assertEquals((edgesBefore + 23), countEdges());
            Assert.assertEquals((verticesBefore + 12), countVertices());
        } finally {
            sc.rollback();
            sc.close();
        }
    }

    @Test
    public void testIndexPatterns() throws Exception {
        assertTriplePattern("spoc", true);
        assertTriplePattern("poc", true);
        assertTriplePattern("p", true);
        assertTriplePattern("", true);
        assertTriplePattern("xpoc", false);
        assertTriplePattern("sspo", false);
    }

    @Test
    public void testAddVertex() throws Exception {
        GraphSail gSail = ((GraphSail) (sail));
        Value toAdd = new URIImpl("http://example.org/thelarch");
        assertNull(gSail.getVertex(toAdd));
        int count = countVertices();
        Vertex added = gSail.addVertex(toAdd);
        assertNotNull(added);
        Assert.assertEquals((1 + count), countVertices());
        Assert.assertEquals("http://example.org/thelarch", added.getProperty(VALUE));
        // also test that we get the vertex through getVertex
        added = gSail.getVertex(toAdd);
        assertNotNull(added);
        Assert.assertEquals("http://example.org/thelarch", added.getProperty(VALUE));
    }

    @Test
    public void getGetVertex() throws Exception {
        GraphSail gSail = ((GraphSail) (sail));
        SailConnection sc = gSail.getConnection();
        try {
            sc.begin();
            Vertex type = gSail.getVertex(TYPE);
            assertNull(type);
            sc.addStatement(TYPE, LABEL, new LiteralImpl("type"));
            type = gSail.getVertex(TYPE);
            assertNotNull(type);
            Assert.assertEquals(TYPE.stringValue(), type.getProperty(VALUE));
        } finally {
            sc.rollback();
            sc.close();
        }
    }

    @Test
    public void testCodePlay() throws Exception {
        Sail sail = new GraphSail(new TinkerGraph());
        sail.initialize();
        try {
            SailConnection sc = sail.getConnection();
            try {
                sc.begin();
                ValueFactory vf = sail.getValueFactory();
                sc.addStatement(vf.createURI("http://tinkerpop.com#1"), vf.createURI("http://tinkerpop.com#knows"), vf.createURI("http://tinkerpop.com#3"), vf.createURI("http://tinkerpop.com"));
                sc.addStatement(vf.createURI("http://tinkerpop.com#1"), vf.createURI("http://tinkerpop.com#name"), vf.createLiteral("marko"), vf.createURI("http://tinkerpop.com"));
                sc.addStatement(vf.createURI("http://tinkerpop.com#3"), vf.createURI("http://tinkerpop.com#name"), vf.createLiteral("josh"), vf.createURI("http://tinkerpop.com"));
                CloseableIteration<? extends Statement, SailException> results = sc.getStatements(null, null, null, false);
                try {
                    System.out.println("get statements: ?s ?p ?o ?g");
                    while (results.hasNext()) {
                        System.out.println(results.next());
                    } 
                } finally {
                    results.close();
                }
                System.out.println("\nget statements: http://tinkerpop.com#3 ?p ?o ?g");
                results = sc.getStatements(vf.createURI("http://tinkerpop.com#3"), null, null, false);
                try {
                    while (results.hasNext()) {
                        System.out.println(results.next());
                    } 
                } finally {
                    results.close();
                }
                SPARQLParser parser = new SPARQLParser();
                CloseableIteration<? extends BindingSet, QueryEvaluationException> sparqlResults;
                String queryString = "SELECT ?x ?y WHERE { ?x <http://tinkerpop.com#knows> ?y }";
                ParsedQuery query = parser.parseQuery(queryString, "http://tinkerPop.com");
                System.out.println(("\nSPARQL: " + queryString));
                sparqlResults = sc.evaluate(query.getTupleExpr(), query.getDataset(), new EmptyBindingSet(), false);
                try {
                    while (sparqlResults.hasNext()) {
                        System.out.println(sparqlResults.next());
                    } 
                } finally {
                    sparqlResults.close();
                }
                Graph graph = getBaseGraph();
                System.out.println();
                for (Vertex v : graph.getVertices()) {
                    System.out.println("------");
                    System.out.println(v);
                    for (String key : v.getPropertyKeys()) {
                        System.out.println(((key + "=") + (v.getProperty(key))));
                    }
                }
                for (Edge e : graph.getEdges()) {
                    System.out.println("------");
                    System.out.println(e);
                    for (String key : e.getPropertyKeys()) {
                        System.out.println(((key + "=") + (e.getProperty(key))));
                    }
                }
            } finally {
                sc.rollback();
                sc.close();
            }
        } finally {
            sail.shutDown();
        }
    }
}

