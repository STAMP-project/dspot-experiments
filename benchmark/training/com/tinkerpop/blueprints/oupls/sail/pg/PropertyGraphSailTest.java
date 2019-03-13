package com.tinkerpop.blueprints.oupls.sail.pg;


import RDF.TYPE;
import RDFFormat.TURTLE;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.impls.tg.TinkerGraph;
import com.tinkerpop.blueprints.util.io.graphml.GraphMLReader;
import info.aduna.iteration.CloseableIteration;
import junit.framework.Assert;
import org.junit.Test;
import org.openrdf.model.Literal;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.impl.EmptyBindingSet;
import org.openrdf.query.parser.ParsedQuery;
import org.openrdf.query.parser.sparql.SPARQLParser;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.Rio;
import org.openrdf.sail.SailConnection;

import static PropertyGraphSail.EDGE_NS;
import static PropertyGraphSail.ONTOLOGY_NS;
import static PropertyGraphSail.PROPERTY_NS;
import static PropertyGraphSail.RELATION_NS;
import static PropertyGraphSail.VERTEX_NS;
import static org.junit.Assert.assertNotNull;


/**
 *
 *
 * @author Joshua Shinavier (http://fortytwo.net)
 */
public class PropertyGraphSailTest {
    private final PropertyGraphSail sail;

    private final ValueFactory vf;

    private final URI edge;

    private final URI head;

    private final URI id;

    private final URI label;

    private final URI tail;

    private final URI vertex;

    private final URI age;

    private final URI josh;

    private final URI lang;

    private final URI lop;

    private final URI marko;

    private final URI name;

    private final URI peter;

    private final URI ripple;

    private final URI vadas;

    private final URI weight;

    private final URI created;

    private final URI knows;

    private final URI markoKnowsVadas;

    private final URI markoKnowsJosh;

    private final URI markoCreatedLop;

    private final URI joshCreatedRipple;

    private final URI joshCreatedLop;

    private final URI peterCreatedLop;

    private SailConnection sc;

    public PropertyGraphSailTest() throws Exception {
        Graph g = new TinkerGraph();
        GraphMLReader r = new GraphMLReader(g);
        r.inputGraph(GraphMLReader.class.getResourceAsStream("graph-example-1.xml"));
        sail = new PropertyGraphSail(g);
        sail.initialize();
        vf = sail.getValueFactory();
        edge = vf.createURI(((ONTOLOGY_NS) + "Edge"));
        head = vf.createURI(((ONTOLOGY_NS) + "head"));
        id = vf.createURI(((ONTOLOGY_NS) + "id"));
        label = vf.createURI(((ONTOLOGY_NS) + "label"));
        tail = vf.createURI(((ONTOLOGY_NS) + "tail"));
        vertex = vf.createURI(((ONTOLOGY_NS) + "Vertex"));
        age = vf.createURI(((PROPERTY_NS) + "age"));
        lang = vf.createURI(((PROPERTY_NS) + "lang"));
        name = vf.createURI(((PROPERTY_NS) + "name"));
        weight = vf.createURI(((PROPERTY_NS) + "weight"));
        josh = vf.createURI(((VERTEX_NS) + "4"));
        lop = vf.createURI(((VERTEX_NS) + "3"));
        marko = vf.createURI(((VERTEX_NS) + "1"));
        peter = vf.createURI(((VERTEX_NS) + "6"));
        ripple = vf.createURI(((VERTEX_NS) + "5"));
        vadas = vf.createURI(((VERTEX_NS) + "2"));
        markoKnowsVadas = vf.createURI(((EDGE_NS) + "7"));
        markoKnowsJosh = vf.createURI(((EDGE_NS) + "8"));
        markoCreatedLop = vf.createURI(((EDGE_NS) + "9"));
        joshCreatedRipple = vf.createURI(((EDGE_NS) + "10"));
        joshCreatedLop = vf.createURI(((EDGE_NS) + "11"));
        peterCreatedLop = vf.createURI(((EDGE_NS) + "12"));
        created = vf.createURI(((RELATION_NS) + "created"));
        knows = vf.createURI(((RELATION_NS) + "knows"));
    }

    @Test
    public void testSize() throws Exception {
        Assert.assertEquals(60, sc.size());
    }

    @Test
    public void testNamespaces() throws Exception {
        Assert.assertEquals(5, count(sc.getNamespaces()));
        Assert.assertEquals("http://tinkerpop.com/pgm/property/", sc.getNamespace("prop"));
        Assert.assertEquals("http://tinkerpop.com/pgm/ontology#", sc.getNamespace("pgm"));
        Assert.assertEquals("http://tinkerpop.com/pgm/vertex/", sc.getNamespace("vertex"));
        Assert.assertEquals("http://tinkerpop.com/pgm/edge/", sc.getNamespace("edge"));
        Assert.assertEquals("http://www.w3.org/1999/02/22-rdf-syntax-ns#", sc.getNamespace("rdf"));
    }

    @Test
    public void testVertexProperties() throws Exception {
        // s p o
        assertExpected(get(marko, name, vf.createLiteral("marko")), vf.createStatement(marko, name, vf.createLiteral("marko")));
        // s p ?
        assertExpected(get(marko, name, null), vf.createStatement(marko, name, vf.createLiteral("marko")));
        // ? p o
        assertExpected(get(null, name, vf.createLiteral("marko")), vf.createStatement(marko, name, vf.createLiteral("marko")));
        // ? ? o
        assertExpected(get(null, null, vf.createLiteral("marko")), vf.createStatement(marko, name, vf.createLiteral("marko")));
        // ? p ?
        assertExpected(get(null, name, null), vf.createStatement(josh, name, vf.createLiteral("josh")), vf.createStatement(lop, name, vf.createLiteral("lop")), vf.createStatement(marko, name, vf.createLiteral("marko")), vf.createStatement(peter, name, vf.createLiteral("peter")), vf.createStatement(ripple, name, vf.createLiteral("ripple")), vf.createStatement(vadas, name, vf.createLiteral("vadas")));
        // s ? o
        assertExpected(get(marko, null, vf.createLiteral("marko")), vf.createStatement(marko, name, vf.createLiteral("marko")));
    }

    @Test
    public void testEdgeProperties() throws Exception {
        // s p o
        assertExpected(get(joshCreatedRipple, weight, vf.createLiteral(1.0F)), vf.createStatement(joshCreatedRipple, weight, vf.createLiteral(1.0F)));
        // s p ?
        assertExpected(get(joshCreatedRipple, weight, null), vf.createStatement(joshCreatedRipple, weight, vf.createLiteral(1.0F)));
        // ? p o
        assertExpected(get(null, weight, vf.createLiteral(1.0F)), vf.createStatement(joshCreatedRipple, weight, vf.createLiteral(1.0F)), vf.createStatement(markoKnowsJosh, weight, vf.createLiteral(1.0F)));
        // ? ? o
        assertExpected(get(null, null, vf.createLiteral(1.0F)), vf.createStatement(joshCreatedRipple, weight, vf.createLiteral(1.0F)), vf.createStatement(markoKnowsJosh, weight, vf.createLiteral(1.0F)));
        // ? p ?
        assertExpected(get(null, weight, null), vf.createStatement(joshCreatedRipple, weight, vf.createLiteral(1.0F)), vf.createStatement(markoKnowsVadas, weight, vf.createLiteral(0.5F)), vf.createStatement(markoCreatedLop, weight, vf.createLiteral(0.4F)), vf.createStatement(joshCreatedLop, weight, vf.createLiteral(0.4F)), vf.createStatement(peterCreatedLop, weight, vf.createLiteral(0.2F)), vf.createStatement(markoKnowsJosh, weight, vf.createLiteral(1.0F)));
        // s ? o
        assertExpected(get(joshCreatedRipple, null, vf.createLiteral(1.0F)), vf.createStatement(joshCreatedRipple, weight, vf.createLiteral(1.0F)));
    }

    @Test
    public void testIds() throws Exception {
        // s p o
        assertExpected(get(marko, id, vf.createLiteral("1")), vf.createStatement(marko, id, vf.createLiteral("1")));
        // s p ?
        assertExpected(get(marko, id, null), vf.createStatement(marko, id, vf.createLiteral("1")));
        // ? p o
        assertExpected(get(null, id, vf.createLiteral("1")), vf.createStatement(marko, id, vf.createLiteral("1")));
        // ? ? o
        assertExpected(get(null, null, vf.createLiteral("1")), vf.createStatement(marko, id, vf.createLiteral("1")));
        // ? p ?
        assertExpected(get(null, id, null), vf.createStatement(marko, id, vf.createLiteral("1")), vf.createStatement(vadas, id, vf.createLiteral("2")), vf.createStatement(lop, id, vf.createLiteral("3")), vf.createStatement(josh, id, vf.createLiteral("4")), vf.createStatement(ripple, id, vf.createLiteral("5")), vf.createStatement(peter, id, vf.createLiteral("6")), vf.createStatement(markoKnowsVadas, id, vf.createLiteral("7")), vf.createStatement(markoKnowsJosh, id, vf.createLiteral("8")), vf.createStatement(markoCreatedLop, id, vf.createLiteral("9")), vf.createStatement(joshCreatedRipple, id, vf.createLiteral("10")), vf.createStatement(joshCreatedLop, id, vf.createLiteral("11")), vf.createStatement(peterCreatedLop, id, vf.createLiteral("12")));
        // s ? o
        assertExpected(get(marko, null, vf.createLiteral("1")), vf.createStatement(marko, id, vf.createLiteral("1")));
    }

    @Test
    public void testLabels() throws Exception {
        // s p o
        assertExpected(get(markoKnowsVadas, label, vf.createLiteral("knows")), vf.createStatement(markoKnowsVadas, label, vf.createLiteral("knows")));
        // s p ?
        assertExpected(get(markoKnowsVadas, label, null), vf.createStatement(markoKnowsVadas, label, vf.createLiteral("knows")));
        // ? p o
        assertExpected(get(null, label, vf.createLiteral("knows")), vf.createStatement(markoKnowsVadas, label, vf.createLiteral("knows")), vf.createStatement(markoKnowsJosh, label, vf.createLiteral("knows")));
        // ? ? o
        assertExpected(get(null, null, vf.createLiteral("knows")), vf.createStatement(markoKnowsVadas, label, vf.createLiteral("knows")), vf.createStatement(markoKnowsJosh, label, vf.createLiteral("knows")));
        // ? p ?
        assertExpected(get(null, label, null), vf.createStatement(markoCreatedLop, label, vf.createLiteral("created")), vf.createStatement(joshCreatedLop, label, vf.createLiteral("created")), vf.createStatement(joshCreatedRipple, label, vf.createLiteral("created")), vf.createStatement(peterCreatedLop, label, vf.createLiteral("created")), vf.createStatement(markoKnowsVadas, label, vf.createLiteral("knows")), vf.createStatement(markoKnowsJosh, label, vf.createLiteral("knows")));
        // s ? o
        assertExpected(get(markoKnowsVadas, null, vf.createLiteral("knows")), vf.createStatement(markoKnowsVadas, label, vf.createLiteral("knows")));
    }

    @Test
    public void testHeads() throws Exception {
        // s p o
        assertExpected(get(markoCreatedLop, head, lop), vf.createStatement(markoCreatedLop, head, lop));
        // s p ?
        assertExpected(get(markoCreatedLop, head, null), vf.createStatement(markoCreatedLop, head, lop));
        // ? p o
        assertExpected(get(null, head, lop), vf.createStatement(markoCreatedLop, head, lop), vf.createStatement(joshCreatedLop, head, lop), vf.createStatement(peterCreatedLop, head, lop));
        // ? ? o
        assertExpected(get(null, null, lop), vf.createStatement(markoCreatedLop, head, lop), vf.createStatement(joshCreatedLop, head, lop), vf.createStatement(peterCreatedLop, head, lop));
        // ? p ?
        assertExpected(get(null, head, null), vf.createStatement(markoKnowsJosh, head, josh), vf.createStatement(markoKnowsVadas, head, vadas), vf.createStatement(markoCreatedLop, head, lop), vf.createStatement(joshCreatedRipple, head, ripple), vf.createStatement(joshCreatedLop, head, lop), vf.createStatement(peterCreatedLop, head, lop));
        // s ? o
        assertExpected(get(markoCreatedLop, null, lop), vf.createStatement(markoCreatedLop, head, lop));
    }

    @Test
    public void testTails() throws Exception {
        // s p o
        assertExpected(get(markoCreatedLop, tail, marko), vf.createStatement(markoCreatedLop, tail, marko));
        // s p ?
        assertExpected(get(markoCreatedLop, tail, null), vf.createStatement(markoCreatedLop, tail, marko));
        // ? p o
        assertExpected(get(null, tail, marko), vf.createStatement(markoCreatedLop, tail, marko), vf.createStatement(markoKnowsJosh, tail, marko), vf.createStatement(markoKnowsVadas, tail, marko));
        // ? ? o
        assertExpected(get(null, null, marko), vf.createStatement(markoCreatedLop, tail, marko), vf.createStatement(markoKnowsJosh, tail, marko), vf.createStatement(markoKnowsVadas, tail, marko));
        // ? p ?
        assertExpected(get(null, tail, null), vf.createStatement(markoKnowsJosh, tail, marko), vf.createStatement(markoKnowsVadas, tail, marko), vf.createStatement(markoCreatedLop, tail, marko), vf.createStatement(joshCreatedRipple, tail, josh), vf.createStatement(joshCreatedLop, tail, josh), vf.createStatement(peterCreatedLop, tail, peter));
        // s ? o
        assertExpected(get(markoCreatedLop, null, marko), vf.createStatement(markoCreatedLop, tail, marko));
    }

    @Test
    public void testTypes() throws Exception {
        // s p o
        assertExpected(get(marko, TYPE, vertex), vf.createStatement(marko, TYPE, vertex));
        // s p ?
        assertExpected(get(marko, TYPE, null), vf.createStatement(marko, TYPE, vertex));
        // ? p o
        assertExpected(get(null, TYPE, vertex), vf.createStatement(marko, TYPE, vertex), vf.createStatement(vadas, TYPE, vertex), vf.createStatement(lop, TYPE, vertex), vf.createStatement(josh, TYPE, vertex), vf.createStatement(ripple, TYPE, vertex), vf.createStatement(peter, TYPE, vertex));
        // ? ? o
        assertExpected(get(null, null, vertex), vf.createStatement(marko, TYPE, vertex), vf.createStatement(vadas, TYPE, vertex), vf.createStatement(lop, TYPE, vertex), vf.createStatement(josh, TYPE, vertex), vf.createStatement(ripple, TYPE, vertex), vf.createStatement(peter, TYPE, vertex));
        // ? p ?
        assertExpected(get(null, TYPE, null), vf.createStatement(markoKnowsJosh, TYPE, edge), vf.createStatement(markoKnowsVadas, TYPE, edge), vf.createStatement(markoCreatedLop, TYPE, edge), vf.createStatement(joshCreatedRipple, TYPE, edge), vf.createStatement(joshCreatedLop, TYPE, edge), vf.createStatement(peterCreatedLop, TYPE, edge), vf.createStatement(marko, TYPE, vertex), vf.createStatement(vadas, TYPE, vertex), vf.createStatement(lop, TYPE, vertex), vf.createStatement(josh, TYPE, vertex), vf.createStatement(ripple, TYPE, vertex), vf.createStatement(peter, TYPE, vertex));
        // s ? o
        assertExpected(get(marko, null, vertex), vf.createStatement(marko, TYPE, vertex));
    }

    @Test
    public void testxxx() throws Exception {
        Assert.assertEquals(60, get(null, null, null).size());
    }

    @Test
    public void testSxx() throws Exception {
        assertExpected(get(ripple, null, null), vf.createStatement(ripple, id, vf.createLiteral("5")), vf.createStatement(ripple, TYPE, vertex), vf.createStatement(ripple, lang, vf.createLiteral("java")), vf.createStatement(ripple, name, vf.createLiteral("ripple")));
    }

    @Test
    public void testPropertyTypeSensitivity() throws Exception {
        assertExpected(get(joshCreatedRipple, weight, vf.createLiteral(1.0F)), vf.createStatement(joshCreatedRipple, weight, vf.createLiteral(1.0F)));
        assertExpected(get(joshCreatedRipple, weight, vf.createLiteral(1.0)));
    }

    @Test
    public void testRDFDump() throws Exception {
        Repository repo = new org.openrdf.repository.sail.SailRepository(sail);
        RepositoryConnection rc = repo.getConnection();
        try {
            RDFWriter w = Rio.createWriter(TURTLE, System.out);
            rc.export(w);
        } finally {
            rc.close();
        }
    }

    @Test
    public void testSPARQL() throws Exception {
        int count;
        String queryStr = (((((((((((((("PREFIX pgm: <" + (ONTOLOGY_NS)) + ">\n") + "PREFIX prop: <") + (PROPERTY_NS)) + ">\n") + "SELECT ?project ?name WHERE {\n") + "   ?marko prop:name \"marko\".\n") + "   ?e1 pgm:label \"knows\".\n") + "   ?e1 pgm:tail ?marko.\n") + "   ?e1 pgm:head ?friend.\n") + "   ?e2 pgm:label \"created\".\n") + "   ?e2 pgm:tail ?friend.\n") + "   ?e2 pgm:head ?project.\n") + "   ?project prop:name ?name.\n") + "}";
        System.out.println(queryStr);
        ParsedQuery query = new SPARQLParser().parseQuery(queryStr, "http://example.org/bogus/");
        CloseableIteration<? extends BindingSet, QueryEvaluationException> results = sc.evaluate(query.getTupleExpr(), query.getDataset(), new EmptyBindingSet(), false);
        try {
            count = 0;
            while (results.hasNext()) {
                count++;
                BindingSet set = results.next();
                URI project = ((URI) (set.getValue("project")));
                Literal name = ((Literal) (set.getValue("name")));
                assertNotNull(project);
                assertNotNull(name);
                System.out.println(((("project = " + project) + ", name = ") + name));
            } 
        } finally {
            results.close();
        }
        Assert.assertEquals(2, count);
    }

    @Test
    public void testSimpleEdges() throws Exception {
        sail.setFirstClassEdges(false);
        sc.close();
        sc = sail.getConnection();
        for (Statement st : get(null, null, null)) {
            System.out.println(("st: " + st));
        }
        Assert.assertEquals(30, sc.size());
        Assert.assertEquals(30, count(null, null, null));
        Assert.assertEquals(6, count(null, TYPE, vertex));
        Assert.assertEquals(0, count(null, TYPE, edge));
        Assert.assertEquals(0, count(null, label, null));
        Assert.assertEquals(0, count(null, head, null));
        Assert.assertEquals(0, count(null, tail, null));
        // ... absence of other patterns could be tested, as well
        // s ? ?
        assertExpected(get(marko, null, null), vf.createStatement(marko, id, vf.createLiteral("1")), vf.createStatement(marko, TYPE, vertex), vf.createStatement(marko, age, vf.createLiteral(29)), vf.createStatement(marko, name, vf.createLiteral("marko")), vf.createStatement(marko, knows, vadas), vf.createStatement(marko, created, lop), vf.createStatement(marko, knows, josh));
        // s p ?
        assertExpected(get(marko, knows, null), vf.createStatement(marko, knows, vadas), vf.createStatement(marko, knows, josh));
        // s ? o
        assertExpected(get(marko, null, josh), vf.createStatement(marko, knows, josh));
        // s p o
        assertExpected(get(marko, knows, josh), vf.createStatement(marko, knows, josh));
        assertExpected(get(marko, name, vf.createLiteral("marko")), vf.createStatement(marko, name, vf.createLiteral("marko")));
        // ? ? o
        assertExpected(get(null, null, josh), vf.createStatement(marko, knows, josh));
        // ? p o
        assertExpected(get(null, knows, josh), vf.createStatement(marko, knows, josh));
        // ? p ?
        assertExpected(get(null, knows, null), vf.createStatement(marko, knows, vadas), vf.createStatement(marko, knows, josh));
    }
}

