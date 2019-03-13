/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.processor.lucene;


import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class LuceneQueryProcessorTest extends CamelTestSupport {
    private static final Logger LOG = LoggerFactory.getLogger(LuceneQueryProcessorTest.class);

    @Test
    public void testPhraseSearcher() throws Exception {
        final StandardAnalyzer analyzer = new StandardAnalyzer();
        MockEndpoint mockSearchEndpoint = getMockEndpoint("mock:searchResult");
        context.stop();
        context.addRoutes(new RouteBuilder() {
            public void configure() {
                try {
                    from("direct:start").setHeader("QUERY", constant("Rodney Dangerfield")).process(new LuceneQueryProcessor("target/stdindexDir", analyzer, null, 20)).to("direct:next");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                from("direct:next").process(new Processor() {
                    public void process(org.apache.camel.Exchange exchange) throws Exception {
                        org.apache.camel.processor.lucene.support.Hits hits = exchange.getIn().getBody(org.apache.camel.processor.lucene.support.Hits.class);
                        printResults(hits);
                    }

                    private void printResults(org.apache.camel.processor.lucene.support.Hits hits) {
                        LuceneQueryProcessorTest.LOG.debug(("Number of hits: " + (hits.getNumberOfHits())));
                        for (int i = 0; i < (hits.getNumberOfHits()); i++) {
                            LuceneQueryProcessorTest.LOG.debug(((("Hit " + i) + " Index Location:") + (hits.getHit().get(i).getHitLocation())));
                            LuceneQueryProcessorTest.LOG.debug(((("Hit " + i) + " Score:") + (hits.getHit().get(i).getScore())));
                            LuceneQueryProcessorTest.LOG.debug(((("Hit " + i) + " Data:") + (hits.getHit().get(i).getData())));
                        }
                    }
                }).to("mock:searchResult");
            }
        });
        context.start();
        LuceneQueryProcessorTest.LOG.debug("------------Beginning Phrase + Standard Analyzer Search Test---------------");
        sendRequest();
        mockSearchEndpoint.assertIsSatisfied();
        LuceneQueryProcessorTest.LOG.debug("------------Completed Phrase + Standard Analyzer Search Test---------------");
        context.stop();
    }

    @Test
    public void testWildcardSearcher() throws Exception {
        final WhitespaceAnalyzer analyzer = new WhitespaceAnalyzer();
        MockEndpoint mockSearchEndpoint = getMockEndpoint("mock:searchResult");
        context.stop();
        context.addRoutes(new RouteBuilder() {
            public void configure() {
                try {
                    from("direct:start").setHeader("QUERY", constant("Carl*")).process(new LuceneQueryProcessor("target/simpleindexDir", analyzer, null, 20)).to("direct:next");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                from("direct:next").process(new Processor() {
                    public void process(org.apache.camel.Exchange exchange) throws Exception {
                        org.apache.camel.processor.lucene.support.Hits hits = exchange.getIn().getBody(org.apache.camel.processor.lucene.support.Hits.class);
                        printResults(hits);
                    }

                    private void printResults(org.apache.camel.processor.lucene.support.Hits hits) {
                        LuceneQueryProcessorTest.LOG.debug(("Number of hits: " + (hits.getNumberOfHits())));
                        for (int i = 0; i < (hits.getNumberOfHits()); i++) {
                            LuceneQueryProcessorTest.LOG.debug(((("Hit " + i) + " Index Location:") + (hits.getHit().get(i).getHitLocation())));
                            LuceneQueryProcessorTest.LOG.debug(((("Hit " + i) + " Score:") + (hits.getHit().get(i).getScore())));
                            LuceneQueryProcessorTest.LOG.debug(((("Hit " + i) + " Data:") + (hits.getHit().get(i).getData())));
                        }
                    }
                }).to("mock:searchResult");
            }
        });
        context.start();
        LuceneQueryProcessorTest.LOG.debug("------------Beginning Wildcard + Simple Analyzer Phrase Searcher Test---------------");
        sendRequest();
        mockSearchEndpoint.assertIsSatisfied();
        LuceneQueryProcessorTest.LOG.debug("------------Completed Wildcard + Simple Analyzer Phrase Searcher Test---------------");
        context.stop();
    }
}

