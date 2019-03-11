/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
package com.liferay.portal.search.internal.facet;


import com.liferay.portal.kernel.search.facet.Facet;
import com.liferay.portal.kernel.search.facet.MultiValueFacet;
import com.liferay.portal.kernel.search.facet.RangeFacet;
import com.liferay.portal.kernel.search.facet.SimpleFacet;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.search.internal.test.util.SearchMapUtil;
import org.junit.Test;


/**
 *
 *
 * @author Andr? de Oliveira
 */
public class FacetDiscounterTest {
    @Test
    public void testMultiValueFacet() {
        Facet facet = new MultiValueFacet(null);
        _populate(facet, FacetDiscounterTest._toTerm("a", 10), FacetDiscounterTest._toTerm("b", 5), FacetDiscounterTest._toTerm("c", 2));
        FacetDiscounter facetDiscounter = new FacetDiscounter(facet);
        _discount(facetDiscounter, _createDocument(new String[]{ "a", "c" }));
        _assertTermCollectors(facet, SearchMapUtil.join(FacetDiscounterTest._toMap("a", 9), FacetDiscounterTest._toMap("b", 5), FacetDiscounterTest._toMap("c", 1)));
    }

    @Test
    public void testRangeFacet() {
        Facet facet = new RangeFacet(null);
        _populate(facet, FacetDiscounterTest._toTerm("[0 TO 5]", 3), FacetDiscounterTest._toTerm("[0 TO 9]", 3));
        FacetDiscounter facetDiscounter = new FacetDiscounter(facet);
        _discount(facetDiscounter, "2", "7");
        _assertTermCollectors(facet, SearchMapUtil.join(FacetDiscounterTest._toMap("[0 TO 5]", 2), FacetDiscounterTest._toMap("[0 TO 9]", 1)));
    }

    @Test
    public void testSimpleFacet() {
        Facet facet = new SimpleFacet(null);
        _populate(facet, FacetDiscounterTest._toTerm("a", 10), FacetDiscounterTest._toTerm("b", 5), FacetDiscounterTest._toTerm("c", 2));
        FacetDiscounter facetDiscounter = new FacetDiscounter(facet);
        _discount(facetDiscounter, "a", "b", "c");
        _assertTermCollectors(facet, SearchMapUtil.join(FacetDiscounterTest._toMap("a", 9), FacetDiscounterTest._toMap("b", 4), FacetDiscounterTest._toMap("c", 1)));
    }

    @Test
    public void testZeroedTermIsRemoved() {
        SimpleFacet facet = new SimpleFacet(null);
        _populate(facet, FacetDiscounterTest._toTerm("public", 1000), FacetDiscounterTest._toTerm("secret", 1));
        FacetDiscounter facetDiscounter = new FacetDiscounter(facet);
        _discount(facetDiscounter, "secret");
        _assertTermCollectors(facet, FacetDiscounterTest._toMap("public", 1000));
    }

    private static final String _FIELD_NAME = RandomTestUtil.randomString();
}

