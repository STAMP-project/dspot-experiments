/**
 * Copyright (c) 2012-2018 Red Hat, Inc.
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Red Hat, Inc. - initial API and implementation
 */
package org.eclipse.che.ide.ext.java.client.search;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.eclipse.che.api.languageserver.shared.model.SnippetResult;
import org.eclipse.che.jdt.ls.extension.api.dto.LinearRange;
import org.eclipse.che.jdt.ls.extension.api.dto.SearchResult;
import org.eclipse.che.plugin.languageserver.ide.service.TextDocumentServiceClient;
import org.junit.Test;
import org.mockito.Mockito;


public class SnippetLoadingTest {
    private TextDocumentServiceClient service;

    @Test
    public void testSnippetMatching() {
        ElementNode[] node = new ElementNode[1];
        SnippetLoadingTest.result("bar").children(SnippetLoadingTest.result("foo").children(SnippetLoadingTest.result("foo").process(( n) -> {
            node[0] = n;
        }).match(200, 37))).build(null);
        FindUsagesPresenter presenter = new FindUsagesPresenter(null, null, Mockito.mock(FindUsagesView.class), null, service, null, null, null, null, null);
        presenter.computeMatches(node[0]).then(( nodes) -> {
            assertEquals(1, nodes.size());
            MatchNode match = nodes.get(0);
            assertEquals(new SnippetResult(new LinearRange(200, 37), "foofoo", 37, new LinearRange(70, 18)), match.getSnippet());
        });
    }

    private static class SearchResultBuilder {
        private String uri;

        private List<SnippetLoadingTest.SearchResultBuilder> children = new ArrayList<>();

        private List<LinearRange> matches = new ArrayList<>();

        private Consumer<ElementNode> processor;

        public SearchResultBuilder(String uri) {
            this.uri = uri;
        }

        public SnippetLoadingTest.SearchResultBuilder children(SnippetLoadingTest.SearchResultBuilder... builders) {
            children.addAll(Arrays.asList(builders));
            return this;
        }

        public SnippetLoadingTest.SearchResultBuilder match(int offset, int length) {
            matches.add(new LinearRange(offset, length));
            return this;
        }

        public SnippetLoadingTest.SearchResultBuilder process(Consumer<ElementNode> processor) {
            this.processor = processor;
            return this;
        }

        private ElementNode build(ElementNode parent) {
            SearchResult result = new SearchResult();
            final ElementNode node = Mockito.mock(ElementNode.class);
            Mockito.when(node.getParent()).thenReturn(parent);
            Mockito.when(node.getElement()).thenReturn(result);
            result.setUri(uri);
            result.setMatches(matches);
            result.setChildren(children.stream().map(( builder) -> {
                ElementNode childNode = builder.build(node);
                return childNode.getElement();
            }).collect(Collectors.toList()));
            if ((processor) != null) {
                processor.accept(node);
            }
            return node;
        }
    }
}

