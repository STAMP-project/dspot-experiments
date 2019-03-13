/**
 * Copyright 2014 Google Inc. All rights reserved.
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under
 * the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF
 * ANY KIND, either express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.google.maps;


import AddressComponentType.ADMINISTRATIVE_AREA_LEVEL_1;
import AddressComponentType.COUNTRY;
import AddressComponentType.LOCALITY;
import AddressComponentType.POLITICAL;
import AddressComponentType.POSTAL_CODE;
import AddressComponentType.ROUTE;
import AddressComponentType.STREET_NUMBER;
import AddressType.ESTABLISHMENT;
import AutocompletePrediction.Term;
import DayOfWeek.FRIDAY;
import DayOfWeek.MONDAY;
import DayOfWeek.THURSDAY;
import DayOfWeek.TUESDAY;
import DayOfWeek.WEDNESDAY;
import FindPlaceFromTextRequest.FieldMask.FORMATTED_ADDRESS;
import FindPlaceFromTextRequest.FieldMask.GEOMETRY;
import FindPlaceFromTextRequest.FieldMask.OPENING_HOURS;
import FindPlaceFromTextRequest.FieldMask.PHOTOS;
import FindPlaceFromTextRequest.FieldMask.RATING;
import InputType.TEXT_QUERY;
import PlaceAutocompleteType.REGIONS;
import PlaceDetails.Review;
import PlaceDetails.Review.AspectRating;
import PlaceDetailsRequest.FieldMask.NAME;
import PlaceDetailsRequest.FieldMask.PLACE_ID;
import PlaceDetailsRequest.FieldMask.TYPES;
import PlaceIdScope.GOOGLE;
import PlaceType.AIRPORT;
import PlaceType.BANK;
import PlaceType.BAR;
import PriceLevel.EXPENSIVE;
import PriceLevel.INEXPENSIVE;
import PriceLevel.VERY_EXPENSIVE;
import RankBy.DISTANCE;
import RankBy.PROMINENCE;
import RatingType.OVERALL;
import com.google.maps.FindPlaceFromTextRequest.LocationBiasIP;
import com.google.maps.PlaceAutocompleteRequest.SessionToken;
import com.google.maps.model.AutocompletePrediction;
import com.google.maps.model.AutocompletePrediction.MatchedSubstring;
import com.google.maps.model.AutocompleteStructuredFormatting;
import com.google.maps.model.ComponentFilter;
import com.google.maps.model.FindPlaceFromText;
import com.google.maps.model.LatLng;
import com.google.maps.model.OpeningHours.Period;
import com.google.maps.model.Photo;
import com.google.maps.model.PlaceDetails;
import com.google.maps.model.PlacesSearchResponse;
import com.google.maps.model.PlacesSearchResult;
import java.net.URI;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;


public class PlacesApiTest {
    private static final String GOOGLE_SYDNEY = "ChIJN1t_tDeuEmsRUsoyG83frY4";

    private static final String QUAY_PLACE_ID = "ChIJ02qnq0KuEmsRHUJF4zo1x4I";

    private static final String PERMANENTLY_CLOSED_PLACE_ID = "ChIJZQvy3jAbdkgR9avxegjoCe0";

    private static final String QUERY_AUTOCOMPLETE_INPUT = "pizza near par";

    private static final LatLng SYDNEY = new LatLng((-33.865), 151.2094);

    private final String autocompletePredictionStructuredFormatting;

    private final String placeDetailResponseBody;

    private final String placeDetailResponseBodyForPermanentlyClosedPlace;

    private final String quayResponseBody;

    private final String queryAutocompleteResponseBody;

    private final String queryAutocompleteWithPlaceIdResponseBody;

    private final String textSearchResponseBody;

    private final String textSearchPizzaInNYCbody;

    private final String placesApiTextSearch;

    private final String placesApiPhoto;

    private final String placesApiPizzaInNewYork;

    private final String placesApiDetailsInFrench;

    private final String placesApiNearbySearchRequestByKeyword;

    private final String placesApiNearbySearchRequestByName;

    private final String placesApiNearbySearchRequestByType;

    private final String placesApiPlaceAutocomplete;

    private final String placesApiPlaceAutocompleteWithType;

    private final String placesApiKitaWard;

    private final String findPlaceFromTextMuseumOfContemporaryArt;

    public PlacesApiTest() {
        autocompletePredictionStructuredFormatting = TestUtils.retrieveBody("AutocompletePredictionStructuredFormatting.json");
        placeDetailResponseBody = TestUtils.retrieveBody("PlaceDetailsResponse.json");
        placeDetailResponseBodyForPermanentlyClosedPlace = TestUtils.retrieveBody("PlaceDetailsResponseForPermanentlyClosedPlace.json");
        quayResponseBody = TestUtils.retrieveBody("PlaceDetailsQuay.json");
        queryAutocompleteResponseBody = TestUtils.retrieveBody("QueryAutocompleteResponse.json");
        queryAutocompleteWithPlaceIdResponseBody = TestUtils.retrieveBody("QueryAutocompleteResponseWithPlaceID.json");
        textSearchResponseBody = TestUtils.retrieveBody("TextSearchResponse.json");
        textSearchPizzaInNYCbody = TestUtils.retrieveBody("TextSearchPizzaInNYC.json");
        placesApiTextSearch = TestUtils.retrieveBody("PlacesApiTextSearchResponse.json");
        placesApiPhoto = TestUtils.retrieveBody("PlacesApiPhotoResponse.json");
        placesApiPizzaInNewYork = TestUtils.retrieveBody("PlacesApiPizzaInNewYorkResponse.json");
        placesApiDetailsInFrench = TestUtils.retrieveBody("PlacesApiDetailsInFrenchResponse.json");
        placesApiNearbySearchRequestByKeyword = TestUtils.retrieveBody("PlacesApiNearbySearchRequestByKeywordResponse.json");
        placesApiNearbySearchRequestByName = TestUtils.retrieveBody("PlacesApiNearbySearchRequestByNameResponse.json");
        placesApiNearbySearchRequestByType = TestUtils.retrieveBody("PlacesApiNearbySearchRequestByTypeResponse.json");
        placesApiPlaceAutocomplete = TestUtils.retrieveBody("PlacesApiPlaceAutocompleteResponse.json");
        placesApiPlaceAutocompleteWithType = TestUtils.retrieveBody("PlacesApiPlaceAutocompleteWithTypeResponse.json");
        placesApiKitaWard = TestUtils.retrieveBody("placesApiKitaWardResponse.json");
        findPlaceFromTextMuseumOfContemporaryArt = TestUtils.retrieveBody("FindPlaceFromTextMuseumOfContemporaryArt.json");
    }

    @Test
    public void testPlaceDetailsRequest() throws Exception {
        try (LocalTestServerContext sc = new LocalTestServerContext("{\"status\" : \"OK\"}")) {
            PlacesApi.placeDetails(sc.context, PlacesApiTest.GOOGLE_SYDNEY).await();
            sc.assertParamValue(PlacesApiTest.GOOGLE_SYDNEY, "placeid");
        }
    }

    @Test
    public void testAutocompletePredictionStructuredFormatting() throws Exception {
        try (LocalTestServerContext sc = new LocalTestServerContext(autocompletePredictionStructuredFormatting)) {
            SessionToken session = new SessionToken();
            final AutocompletePrediction[] predictions = PlacesApi.placeAutocomplete(sc.context, "1", session).await();
            Assert.assertNotNull(predictions);
            Assert.assertNotNull(Arrays.toString(predictions));
            Assert.assertEquals(1, predictions.length);
            final AutocompletePrediction prediction = predictions[0];
            Assert.assertNotNull(prediction);
            Assert.assertEquals("1033 Princes Highway, Heathmere, Victoria, Australia", prediction.description);
            final AutocompleteStructuredFormatting structuredFormatting = prediction.structuredFormatting;
            Assert.assertNotNull(structuredFormatting);
            Assert.assertEquals("1033 Princes Highway", structuredFormatting.mainText);
            Assert.assertEquals("Heathmere, Victoria, Australia", structuredFormatting.secondaryText);
            Assert.assertEquals(1, structuredFormatting.mainTextMatchedSubstrings.length);
            final MatchedSubstring matchedSubstring = structuredFormatting.mainTextMatchedSubstrings[0];
            Assert.assertNotNull(matchedSubstring);
            Assert.assertEquals(1, matchedSubstring.length);
            Assert.assertEquals(0, matchedSubstring.offset);
        }
    }

    @Test
    public void testPlaceDetailsLookupGoogleSydney() throws Exception {
        try (LocalTestServerContext sc = new LocalTestServerContext(placeDetailResponseBody)) {
            PlaceDetails placeDetails = PlacesApi.placeDetails(sc.context, PlacesApiTest.GOOGLE_SYDNEY).fields(PLACE_ID, NAME, TYPES).await();
            sc.assertParamValue(PlacesApiTest.GOOGLE_SYDNEY, "placeid");
            sc.assertParamValue("place_id,name,types", "fields");
            Assert.assertNotNull(placeDetails);
            Assert.assertNotNull(placeDetails.toString());
            // Address
            Assert.assertNotNull(placeDetails.addressComponents);
            Assert.assertEquals(placeDetails.addressComponents[0].longName, "5");
            Assert.assertEquals(placeDetails.addressComponents[0].types.length, 0);
            Assert.assertEquals(placeDetails.addressComponents[1].longName, "48");
            Assert.assertEquals(placeDetails.addressComponents[1].types[0], STREET_NUMBER);
            Assert.assertEquals(placeDetails.addressComponents[2].longName, "Pirrama Road");
            Assert.assertEquals(placeDetails.addressComponents[2].shortName, "Pirrama Rd");
            Assert.assertEquals(placeDetails.addressComponents[2].types[0], ROUTE);
            Assert.assertEquals(placeDetails.addressComponents[3].shortName, "Pyrmont");
            Assert.assertEquals(placeDetails.addressComponents[3].types[0], LOCALITY);
            Assert.assertEquals(placeDetails.addressComponents[3].types[1], POLITICAL);
            Assert.assertEquals(placeDetails.addressComponents[4].longName, "New South Wales");
            Assert.assertEquals(placeDetails.addressComponents[4].shortName, "NSW");
            Assert.assertEquals(placeDetails.addressComponents[4].types[0], ADMINISTRATIVE_AREA_LEVEL_1);
            Assert.assertEquals(placeDetails.addressComponents[4].types[1], POLITICAL);
            Assert.assertEquals(placeDetails.addressComponents[5].longName, "Australia");
            Assert.assertEquals(placeDetails.addressComponents[5].shortName, "AU");
            Assert.assertEquals(placeDetails.addressComponents[5].types[0], COUNTRY);
            Assert.assertEquals(placeDetails.addressComponents[5].types[1], POLITICAL);
            Assert.assertEquals(placeDetails.addressComponents[6].shortName, "2009");
            Assert.assertEquals(placeDetails.addressComponents[6].types[0], POSTAL_CODE);
            Assert.assertNotNull(placeDetails.formattedAddress);
            Assert.assertEquals(placeDetails.formattedAddress, "5, 48 Pirrama Rd, Pyrmont NSW 2009, Australia");
            Assert.assertNotNull(placeDetails.vicinity);
            Assert.assertEquals(placeDetails.vicinity, "5 48 Pirrama Road, Pyrmont");
            // Phone numbers
            Assert.assertNotNull(placeDetails.formattedPhoneNumber);
            Assert.assertEquals(placeDetails.formattedPhoneNumber, "(02) 9374 4000");
            Assert.assertNotNull(placeDetails.internationalPhoneNumber);
            Assert.assertEquals(placeDetails.internationalPhoneNumber, "+61 2 9374 4000");
            // Geometry
            Assert.assertNotNull(placeDetails.geometry);
            Assert.assertNotNull(placeDetails.geometry.location);
            Assert.assertEquals(placeDetails.geometry.location.lat, (-33.866611), 0.001);
            Assert.assertEquals(placeDetails.geometry.location.lng, 151.195832, 0.001);
            // URLs
            Assert.assertNotNull(placeDetails.icon);
            Assert.assertEquals(placeDetails.icon.toURI(), new URI("https://maps.gstatic.com/mapfiles/place_api/icons/generic_business-71.png"));
            Assert.assertNotNull(placeDetails.url);
            Assert.assertEquals(placeDetails.url.toURI(), new URI("https://plus.google.com/111337342022929067349/about?hl=en-US"));
            Assert.assertNotNull(placeDetails.website);
            Assert.assertEquals(placeDetails.website.toURI(), new URI("https://www.google.com.au/about/careers/locations/sydney/"));
            // Name
            Assert.assertNotNull(placeDetails.name);
            Assert.assertEquals(placeDetails.name, "Google");
            // Opening Hours
            Assert.assertNotNull(placeDetails.openingHours);
            Assert.assertNotNull(placeDetails.openingHours.openNow);
            Assert.assertTrue(placeDetails.openingHours.openNow);
            Assert.assertNotNull(placeDetails.openingHours.periods);
            Assert.assertEquals(placeDetails.openingHours.periods.length, 5);
            {
                Period monday = placeDetails.openingHours.periods[0];
                Period tuesday = placeDetails.openingHours.periods[1];
                Period wednesday = placeDetails.openingHours.periods[2];
                Period thursday = placeDetails.openingHours.periods[3];
                Period friday = placeDetails.openingHours.periods[4];
                Assert.assertEquals(MONDAY, monday.open.day);
                LocalTime opening = LocalTime.of(8, 30);
                LocalTime closing5pm = LocalTime.of(17, 0);
                LocalTime closing530pm = LocalTime.of(17, 30);
                Assert.assertEquals(opening, monday.open.time);
                Assert.assertEquals(MONDAY, monday.close.day);
                Assert.assertEquals(closing530pm, monday.close.time);
                Assert.assertEquals(TUESDAY, tuesday.open.day);
                Assert.assertEquals(opening, tuesday.open.time);
                Assert.assertEquals(TUESDAY, tuesday.close.day);
                Assert.assertEquals(closing530pm, tuesday.close.time);
                Assert.assertEquals(WEDNESDAY, wednesday.open.day);
                Assert.assertEquals(opening, wednesday.open.time);
                Assert.assertEquals(WEDNESDAY, wednesday.close.day);
                Assert.assertEquals(closing530pm, wednesday.close.time);
                Assert.assertEquals(THURSDAY, thursday.open.day);
                Assert.assertEquals(opening, thursday.open.time);
                Assert.assertEquals(THURSDAY, thursday.close.day);
                Assert.assertEquals(closing530pm, thursday.close.time);
                Assert.assertEquals(FRIDAY, friday.open.day);
                Assert.assertEquals(opening, friday.open.time);
                Assert.assertEquals(FRIDAY, friday.close.day);
                Assert.assertEquals(closing5pm, friday.close.time);
            }
            Assert.assertNotNull(placeDetails.openingHours.weekdayText);
            Assert.assertEquals(placeDetails.openingHours.weekdayText[0], "Monday: 8:30 am ? 5:30 pm");
            Assert.assertEquals(placeDetails.openingHours.weekdayText[1], "Tuesday: 8:30 am ? 5:30 pm");
            Assert.assertEquals(placeDetails.openingHours.weekdayText[2], "Wednesday: 8:30 am ? 5:30 pm");
            Assert.assertEquals(placeDetails.openingHours.weekdayText[3], "Thursday: 8:30 am ? 5:30 pm");
            Assert.assertEquals(placeDetails.openingHours.weekdayText[4], "Friday: 8:30 am ? 5:00 pm");
            Assert.assertEquals(placeDetails.openingHours.weekdayText[5], "Saturday: Closed");
            Assert.assertEquals(placeDetails.openingHours.weekdayText[6], "Sunday: Closed");
            Assert.assertEquals(placeDetails.utcOffset, 600);
            // Photos
            Assert.assertNotNull(placeDetails.photos);
            Photo photo = placeDetails.photos[0];
            Assert.assertNotNull(photo);
            Assert.assertNotNull(photo.photoReference);
            Assert.assertNotNull(photo.htmlAttributions);
            Assert.assertNotNull(photo.htmlAttributions[0]);
            // Reviews
            Assert.assertNotNull(placeDetails.reviews);
            PlaceDetails.Review review = placeDetails.reviews[0];
            Assert.assertNotNull(review);
            Assert.assertNotNull(review.authorName);
            Assert.assertEquals("Danielle Lonnon", review.authorName);
            Assert.assertNotNull(review.authorUrl);
            Assert.assertEquals(new URI("https://plus.google.com/118257578392162991040"), review.authorUrl.toURI());
            Assert.assertNotNull(review.profilePhotoUrl);
            Assert.assertEquals("https://lh5.googleusercontent.com/photo.jpg", review.profilePhotoUrl);
            Assert.assertNotNull(review.language);
            Assert.assertEquals("en", review.language);
            Assert.assertNotNull(review.relativeTimeDescription);
            Assert.assertEquals("a month ago", review.relativeTimeDescription);
            Assert.assertEquals(5, review.rating);
            Assert.assertNotNull(review.text);
            Assert.assertTrue(review.text.startsWith("As someone who works in the theatre,"));
            Assert.assertNotNull(review.aspects);
            PlaceDetails.Review.AspectRating aspect = review.aspects[0];
            Assert.assertNotNull(aspect);
            Assert.assertEquals(3, aspect.rating);
            Assert.assertNotNull(aspect.type);
            Assert.assertEquals(OVERALL, aspect.type);
            Assert.assertEquals(1425790392, ((review.time.toEpochMilli()) / 1000));
            Assert.assertEquals("2015-03-08 04:53 am", DateTimeFormatter.ofPattern("YYYY-MM-dd HH:mm a").withZone(ZoneOffset.UTC).format(review.time).toLowerCase());
            // Place ID
            Assert.assertNotNull(placeDetails.placeId);
            Assert.assertEquals(placeDetails.placeId, PlacesApiTest.GOOGLE_SYDNEY);
            Assert.assertNotNull(placeDetails.scope);
            Assert.assertEquals(placeDetails.scope, GOOGLE);
            Assert.assertNotNull(placeDetails.types);
            Assert.assertEquals(placeDetails.types[0], ESTABLISHMENT);
            Assert.assertEquals(placeDetails.rating, 4.4, 0.1);
            // Permanently closed:
            Assert.assertFalse(placeDetails.permanentlyClosed);
        }
    }

    @Test
    public void testPlaceDetailsLookupPermanentlyClosedPlace() throws Exception {
        try (LocalTestServerContext sc = new LocalTestServerContext(placeDetailResponseBodyForPermanentlyClosedPlace)) {
            PlaceDetails placeDetails = PlacesApi.placeDetails(sc.context, PlacesApiTest.PERMANENTLY_CLOSED_PLACE_ID).await();
            Assert.assertNotNull(placeDetails);
            Assert.assertNotNull(placeDetails.toString());
            Assert.assertTrue(placeDetails.permanentlyClosed);
        }
    }

    @Test
    public void testPlaceDetailsLookupQuay() throws Exception {
        try (LocalTestServerContext sc = new LocalTestServerContext(quayResponseBody)) {
            PlaceDetails placeDetails = PlacesApi.placeDetails(sc.context, PlacesApiTest.QUAY_PLACE_ID).await();
            Assert.assertNotNull(placeDetails);
            Assert.assertNotNull(placeDetails.toString());
            Assert.assertNotNull(placeDetails.priceLevel);
            Assert.assertEquals(VERY_EXPENSIVE, placeDetails.priceLevel);
            Assert.assertNotNull(placeDetails.photos);
            Photo photo = placeDetails.photos[0];
            Assert.assertEquals(1944, photo.height);
            Assert.assertEquals(2592, photo.width);
            Assert.assertEquals("<a href=\"https://maps.google.com/maps/contrib/101719343658521132777\">James Prendergast</a>", photo.htmlAttributions[0]);
            Assert.assertEquals("CmRdAAAATDVdhv0RdMEZlvO2jNE_EXXZZnCWvenfvLmWCsYqVtCFxZiasbcv1X0CNDTkpaCtrurGzVxTVt8Fqc7egdA7VyFeq1VFaq1GiFatWrFAUm_H0CN9u2wbfjb1Zf0NL9QiEhCj6I5O2h6eFH_2sa5hyVaEGhTdn8b7RWD-2W64OrT3mFGjzzLWlQ", photo.photoReference);
        }
    }

    @Test
    public void testQueryAutocompleteRequest() throws Exception {
        try (LocalTestServerContext sc = new LocalTestServerContext("{\"status\" : \"OK\"}")) {
            LatLng location = new LatLng(10, 20);
            PlacesApi.queryAutocomplete(sc.context, PlacesApiTest.QUERY_AUTOCOMPLETE_INPUT).offset(10).location(location).radius(5000).language("en").await();
            sc.assertParamValue(PlacesApiTest.QUERY_AUTOCOMPLETE_INPUT, "input");
            sc.assertParamValue("10", "offset");
            sc.assertParamValue(location.toUrlValue(), "location");
            sc.assertParamValue("5000", "radius");
            sc.assertParamValue("en", "language");
        }
    }

    @Test
    public void testQueryAutocompletePizzaNearPar() throws Exception {
        try (LocalTestServerContext sc = new LocalTestServerContext(queryAutocompleteResponseBody)) {
            AutocompletePrediction[] predictions = PlacesApi.queryAutocomplete(sc.context, PlacesApiTest.QUERY_AUTOCOMPLETE_INPUT).await();
            Assert.assertNotNull(predictions);
            Assert.assertEquals(predictions.length, 5);
            Assert.assertNotNull(Arrays.toString(predictions));
            AutocompletePrediction prediction = predictions[0];
            Assert.assertNotNull(prediction);
            Assert.assertNotNull(prediction.description);
            Assert.assertEquals("pizza near Paris, France", prediction.description);
            Assert.assertEquals(3, prediction.matchedSubstrings.length);
            AutocompletePrediction.MatchedSubstring matchedSubstring = prediction.matchedSubstrings[0];
            Assert.assertEquals(5, matchedSubstring.length);
            Assert.assertEquals(0, matchedSubstring.offset);
            Assert.assertEquals(4, prediction.terms.length);
            AutocompletePrediction.Term term = prediction.terms[0];
            Assert.assertEquals(0, term.offset);
            Assert.assertEquals("pizza", term.value);
        }
    }

    @Test
    public void testQueryAutocompleteWithPlaceId() throws Exception {
        try (LocalTestServerContext sc = new LocalTestServerContext(queryAutocompleteWithPlaceIdResponseBody)) {
            AutocompletePrediction[] predictions = PlacesApi.queryAutocomplete(sc.context, PlacesApiTest.QUERY_AUTOCOMPLETE_INPUT).await();
            Assert.assertNotNull(predictions);
            Assert.assertEquals(predictions.length, 1);
            Assert.assertNotNull(Arrays.toString(predictions));
            AutocompletePrediction prediction = predictions[0];
            Assert.assertNotNull(prediction);
            Assert.assertNotNull(prediction.description);
            Assert.assertEquals("Bondi Pizza, Campbell Parade, Sydney, New South Wales, Australia", prediction.description);
            Assert.assertEquals(2, prediction.matchedSubstrings.length);
            AutocompletePrediction.MatchedSubstring matchedSubstring = prediction.matchedSubstrings[0];
            Assert.assertEquals(5, matchedSubstring.length);
            Assert.assertEquals(6, matchedSubstring.offset);
            Assert.assertEquals(5, prediction.terms.length);
            AutocompletePrediction.Term term = prediction.terms[0];
            Assert.assertEquals(0, term.offset);
            Assert.assertEquals("Bondi Pizza", term.value);
            Assert.assertEquals("ChIJv0wpwp6tEmsR0Glcf5tugrk", prediction.placeId);
        }
    }

    @Test
    public void testTextSearchRequest() throws Exception {
        try (LocalTestServerContext sc = new LocalTestServerContext("{\"status\" : \"OK\"}")) {
            LatLng location = new LatLng(10, 20);
            PlacesApi.textSearchQuery(sc.context, "Google Sydney").location(location).region("AU").radius(3000).minPrice(INEXPENSIVE).maxPrice(VERY_EXPENSIVE).name("name").openNow(true).rankby(DISTANCE).type(AIRPORT).await();
            sc.assertParamValue("Google Sydney", "query");
            sc.assertParamValue(location.toUrlValue(), "location");
            sc.assertParamValue("AU", "region");
            sc.assertParamValue(String.valueOf(3000), "radius");
            sc.assertParamValue(String.valueOf(1), "minprice");
            sc.assertParamValue(String.valueOf(4), "maxprice");
            sc.assertParamValue("name", "name");
            sc.assertParamValue("true", "opennow");
            sc.assertParamValue(DISTANCE.toString(), "rankby");
            sc.assertParamValue(AIRPORT.toString(), "type");
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTextSearchLocationWithoutRadius() throws Exception {
        try (LocalTestServerContext sc = new LocalTestServerContext("{\"status\" : \"OK\"}")) {
            LatLng location = new LatLng(10, 20);
            PlacesApi.textSearchQuery(sc.context, "query").location(location).await();
        }
    }

    @Test
    public void testTextSearchResponse() throws Exception {
        try (LocalTestServerContext sc = new LocalTestServerContext(textSearchResponseBody)) {
            PlacesSearchResponse results = PlacesApi.textSearchQuery(sc.context, "Google Sydney").await();
            Assert.assertNotNull(results);
            Assert.assertNotNull(results.results);
            Assert.assertEquals(1, results.results.length);
            Assert.assertNotNull(results.toString());
            PlacesSearchResult result = results.results[0];
            Assert.assertNotNull(result.formattedAddress);
            Assert.assertEquals("5, 48 Pirrama Rd, Pyrmont NSW 2009, Australia", result.formattedAddress);
            Assert.assertNotNull(result.geometry);
            Assert.assertNotNull(result.geometry.location);
            Assert.assertEquals((-33.866611), result.geometry.location.lat, 1.0E-4);
            Assert.assertEquals(151.195832, result.geometry.location.lng, 1.0E-4);
            Assert.assertNotNull(result.icon);
            Assert.assertEquals(new URI("https://maps.gstatic.com/mapfiles/place_api/icons/generic_business-71.png"), result.icon.toURI());
            Assert.assertNotNull(result.name);
            Assert.assertEquals("Google", result.name);
            Assert.assertNotNull(result.openingHours);
            Assert.assertFalse(result.openingHours.openNow);
            Assert.assertNotNull(result.photos);
            Assert.assertEquals(1, result.photos.length);
            Photo photo = result.photos[0];
            Assert.assertNotNull(photo);
            Assert.assertEquals(2322, photo.height);
            Assert.assertEquals(4128, photo.width);
            Assert.assertNotNull(photo.htmlAttributions);
            Assert.assertEquals(1, photo.htmlAttributions.length);
            Assert.assertEquals("<a href=\"https://maps.google.com/maps/contrib/107252953636064841537\">William Stewart</a>", photo.htmlAttributions[0]);
            Assert.assertEquals(("CmRdAAAAa43ZeiQvF4n-Yv5UnEGcIe0KjdTzzTH4g-g1GuKgWas0g8W7793eFDGxkrG4Z5i_Jua0Z-" + ("Ib88IuYe2iVAZ0W3Q7wUrp4A2mux4BjZmakLFkTkPj_OZ7ek3vSGnrzqExEhBqB3AIn82lmf38RnVSFH1CGhSWrvzN30A_" + "ABGNScuiYEU70wau3w")), photo.photoReference);
            Assert.assertNotNull(result.placeId);
            Assert.assertEquals("ChIJN1t_tDeuEmsRUsoyG83frY4", result.placeId);
            Assert.assertEquals(4.4, result.rating, 1.0E-4);
            Assert.assertNotNull(result.types);
            Assert.assertNotNull(result.types[0]);
            Assert.assertEquals("establishment", result.types[0]);
        }
    }

    @Test
    public void testTextSearchNYC() throws Exception {
        try (LocalTestServerContext sc = new LocalTestServerContext(textSearchPizzaInNYCbody)) {
            PlacesSearchResponse results = PlacesApi.textSearchQuery(sc.context, "Pizza in New York").await();
            Assert.assertNotNull(results.toString());
            Assert.assertNotNull(results.nextPageToken);
            Assert.assertEquals(("CuQB1wAAANI17eHXt1HpqbLjkj7T5Ti69DEAClo02Qampg7Q6W_O_krFbge7hnTtDR7oVF3asex" + ((("HcGnUtR1ZKjroYd4BTCXxSGPi9LEkjJ0P_zVE7byjEBcHvkdxB6nCHKHAgVNGqe0ZHuwSYKlr3C1-" + "kuellMYwMlg3WSe69bJr1Ck35uToNZkUGvo4yjoYxNFRn1lABEnjPskbMdyHAjUDwvBDxzgGxpd8t") + "0EzA9UOM8Y1jqWnZGJM7u8gacNFcI4prr0Doh9etjY1yHrgGYI4F7lKPbfLQKiks_wYzoHbcAcdbB") + "jkEhAxDHC0XXQ16thDAlwVbEYaGhSaGDw5sHbaZkG9LZIqbcas0IJU8w")), results.nextPageToken);
        }
    }

    @Test
    public void testPhotoRequest() throws Exception {
        try (LocalTestServerContext sc = new LocalTestServerContext("")) {
            final String photoReference = "Photo Reference";
            final int width = 200;
            final int height = 100;
            PlacesApi.photo(sc.context, photoReference).maxWidth(width).maxHeight(height).awaitIgnoreError();
            sc.assertParamValue(photoReference, "photoreference");
            sc.assertParamValue(String.valueOf(width), "maxwidth");
            sc.assertParamValue(String.valueOf(height), "maxheight");
        }
    }

    @Test
    public void testNearbySearchRequest() throws Exception {
        try (LocalTestServerContext sc = new LocalTestServerContext("{\"status\" : \"OK\"}")) {
            LatLng location = new LatLng(10, 20);
            PlacesApi.nearbySearchQuery(sc.context, location).radius(5000).rankby(PROMINENCE).keyword("keyword").language("en").minPrice(INEXPENSIVE).maxPrice(EXPENSIVE).name("name").openNow(true).type(AIRPORT).pageToken("next-page-token").await();
            sc.assertParamValue(location.toUrlValue(), "location");
            sc.assertParamValue("5000", "radius");
            sc.assertParamValue(PROMINENCE.toString(), "rankby");
            sc.assertParamValue("keyword", "keyword");
            sc.assertParamValue("en", "language");
            sc.assertParamValue(INEXPENSIVE.toString(), "minprice");
            sc.assertParamValue(EXPENSIVE.toString(), "maxprice");
            sc.assertParamValue("name", "name");
            sc.assertParamValue("true", "opennow");
            sc.assertParamValue(AIRPORT.toString(), "type");
            sc.assertParamValue("next-page-token", "pagetoken");
        }
    }

    // Testing a deprecated method
    @Test
    @SuppressWarnings("deprecation")
    public void testNearbySearchRequestWithMultipleType() throws Exception {
        try (LocalTestServerContext sc = new LocalTestServerContext("{\"status\" : \"OK\"}")) {
            LatLng location = new LatLng(10, 20);
            PlacesApi.nearbySearchQuery(sc.context, location).type(AIRPORT, BANK).await();
            sc.assertParamValue(location.toUrlValue(), "location");
            sc.assertParamValue((((AIRPORT.toString()) + "|") + (BANK.toString())), "type");
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNearbySearchRadiusAndRankbyDistance() throws Exception {
        try (LocalTestServerContext sc = new LocalTestServerContext("")) {
            LatLng location = new LatLng(10, 20);
            PlacesApi.nearbySearchQuery(sc.context, location).radius(5000).rankby(DISTANCE).await();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNearbySearchRankbyDistanceWithoutKeywordNameOrType() throws Exception {
        try (LocalTestServerContext sc = new LocalTestServerContext("")) {
            LatLng location = new LatLng(10, 20);
            PlacesApi.nearbySearchQuery(sc.context, location).rankby(DISTANCE).await();
        }
    }

    @Test
    public void testPlaceAutocompleteRequest() throws Exception {
        try (LocalTestServerContext sc = new LocalTestServerContext("{\"status\" : \"OK\"}")) {
            SessionToken session = new SessionToken();
            LatLng location = new LatLng(10, 20);
            PlacesApi.placeAutocomplete(sc.context, "Sydney Town Hall", session).offset(4).location(location).radius(5000).types(PlaceAutocompleteType.ESTABLISHMENT).components(ComponentFilter.country("AU")).await();
            sc.assertParamValue("Sydney Town Hall", "input");
            sc.assertParamValue(Integer.toString(4), "offset");
            sc.assertParamValue(location.toUrlValue(), "location");
            sc.assertParamValue("5000", "radius");
            sc.assertParamValue(PlaceAutocompleteType.ESTABLISHMENT.toString(), "types");
            sc.assertParamValue(ComponentFilter.country("AU").toString(), "components");
            sc.assertParamValue(session.toUrlValue(), "sessiontoken");
        }
    }

    @Test
    public void testTextSearch() throws Exception {
        try (LocalTestServerContext sc = new LocalTestServerContext(placesApiTextSearch)) {
            PlacesSearchResponse response = PlacesApi.textSearchQuery(sc.context, "Google Sydney").await();
            sc.assertParamValue("Google Sydney", "query");
            Assert.assertNotNull(response.toString());
            Assert.assertEquals(1, response.results.length);
            PlacesSearchResult result = response.results[0];
            Assert.assertEquals("5, 48 Pirrama Rd, Pyrmont NSW 2009, Australia", result.formattedAddress);
            Assert.assertEquals("ChIJN1t_tDeuEmsRUsoyG83frY4", result.placeId);
        }
    }

    @Test
    public void testPhoto() throws Exception {
        try (LocalTestServerContext sc = new LocalTestServerContext(placesApiPhoto)) {
            PlaceDetails placeDetails = PlacesApi.placeDetails(sc.context, PlacesApiTest.GOOGLE_SYDNEY).await();
            sc.assertParamValue("ChIJN1t_tDeuEmsRUsoyG83frY4", "placeid");
            Assert.assertNotNull(placeDetails.toString());
            Assert.assertEquals(10, placeDetails.photos.length);
            Assert.assertEquals("CmRaAAAA-N3w5YTMXWautuDW7IZgX9knz_2fNyyUpCWpvYdVEVb8RurBiisMKvr7AFxMW8dsu2yakYoqjW-IYSFk2cylXVM_c50cCxfm7MlgjPErFxumlcW1bLNOe--SwLYmWlvkEhDxjz75xRqim-CkVlwFyp7sGhTs1fE02MZ6GQcc-TugrepSaeWapA", placeDetails.photos[0].photoReference);
            Assert.assertEquals(1365, placeDetails.photos[0].height);
            Assert.assertEquals(2048, placeDetails.photos[0].width);
        }
    }

    @Test
    public void testPizzaInNewYorkPagination() throws Exception {
        try (LocalTestServerContext sc = new LocalTestServerContext(placesApiPizzaInNewYork)) {
            PlacesSearchResponse response = PlacesApi.textSearchQuery(sc.context, "Pizza in New York").await();
            sc.assertParamValue("Pizza in New York", "query");
            Assert.assertNotNull(response.toString());
            Assert.assertEquals(20, response.results.length);
            Assert.assertEquals("CvQB6AAAAPQLwX6KjvGbOw81Y7aYVhXRlHR8M60aCRXFDM9eyflac4BjE5MaNxTj_1T429x3H2kzBd-ztTFXCSu1CPh3kY44Gu0gmL-xfnArnPE9-BgfqXTpgzGPZNeCltB7m341y4LnU-NE2omFPoDWIrOPIyHnyi05Qol9eP2wKW7XPUhMlHvyl9MeVgZ8COBZKvCdENHbhBD1MN1lWlada6A9GPFj06cCp1aqRGW6v98-IHcIcM9RcfMcS4dLAFm6TsgLq4tpeU6E1kSzhrvDiLMBXdJYFlI0qJmytd2wS3vD0t3zKgU6Im_mY-IJL7AwAqhugBIQ8k0X_n6TnacL9BExELBaixoUo8nPOwWm0Nx02haufF2dY0VL-tg", response.nextPageToken);
        }
    }

    @Test
    public void testPlaceDetailsInFrench() throws Exception {
        try (LocalTestServerContext sc = new LocalTestServerContext(placesApiDetailsInFrench)) {
            PlaceDetails details = PlacesApi.placeDetails(sc.context, "ChIJ442GNENu5kcRGYUrvgqHw88").language("fr").await();
            sc.assertParamValue("ChIJ442GNENu5kcRGYUrvgqHw88", "placeid");
            sc.assertParamValue("fr", "language");
            Assert.assertNotNull(details.toString());
            Assert.assertEquals("ChIJ442GNENu5kcRGYUrvgqHw88", details.placeId);
            Assert.assertEquals("35 Rue du Chevalier de la Barre, 75018 Paris, France", details.formattedAddress);
            Assert.assertEquals("Sacr?-C?ur", details.name);
        }
    }

    @Test
    public void testNearbySearchRequestByKeyword() throws Exception {
        try (LocalTestServerContext sc = new LocalTestServerContext(placesApiNearbySearchRequestByKeyword)) {
            PlacesSearchResponse response = PlacesApi.nearbySearchQuery(sc.context, PlacesApiTest.SYDNEY).radius(10000).keyword("pub").await();
            sc.assertParamValue("10000", "radius");
            sc.assertParamValue("pub", "keyword");
            sc.assertParamValue(PlacesApiTest.SYDNEY.toUrlValue(), "location");
            Assert.assertEquals(20, response.results.length);
        }
    }

    @Test
    public void testNearbySearchRequestByName() throws Exception {
        try (LocalTestServerContext sc = new LocalTestServerContext(placesApiNearbySearchRequestByName)) {
            PlacesSearchResponse response = PlacesApi.nearbySearchQuery(sc.context, PlacesApiTest.SYDNEY).radius(10000).name("Sydney Town Hall").await();
            sc.assertParamValue("Sydney Town Hall", "name");
            sc.assertParamValue(PlacesApiTest.SYDNEY.toUrlValue(), "location");
            sc.assertParamValue("10000", "radius");
            Assert.assertEquals("Sydney Town Hall", response.results[0].name);
        }
    }

    @Test
    public void testNearbySearchRequestByType() throws Exception {
        try (LocalTestServerContext sc = new LocalTestServerContext(placesApiNearbySearchRequestByType)) {
            PlacesSearchResponse response = PlacesApi.nearbySearchQuery(sc.context, PlacesApiTest.SYDNEY).radius(10000).type(BAR).await();
            sc.assertParamValue(PlacesApiTest.SYDNEY.toUrlValue(), "location");
            sc.assertParamValue("10000", "radius");
            sc.assertParamValue(BAR.toUrlValue(), "type");
            Assert.assertEquals(20, response.results.length);
        }
    }

    @Test
    public void testPlaceAutocomplete() throws Exception {
        try (LocalTestServerContext sc = new LocalTestServerContext(placesApiPlaceAutocomplete)) {
            SessionToken session = new SessionToken();
            AutocompletePrediction[] predictions = PlacesApi.placeAutocomplete(sc.context, "Sydney Town Ha", session).await();
            sc.assertParamValue("Sydney Town Ha", "input");
            sc.assertParamValue(session.toUrlValue(), "sessiontoken");
            Assert.assertEquals(5, predictions.length);
            Assert.assertTrue(predictions[0].description.contains("Town Hall"));
        }
    }

    @Test
    public void testPlaceAutocompleteWithType() throws Exception {
        try (LocalTestServerContext sc = new LocalTestServerContext(placesApiPlaceAutocompleteWithType)) {
            SessionToken session = new SessionToken();
            AutocompletePrediction[] predictions = PlacesApi.placeAutocomplete(sc.context, "po", session).components(ComponentFilter.country("nz")).types(REGIONS).await();
            sc.assertParamValue("po", "input");
            sc.assertParamValue("country:nz", "components");
            sc.assertParamValue("(regions)", "types");
            sc.assertParamValue(session.toUrlValue(), "sessiontoken");
            Assert.assertNotNull(Arrays.toString(predictions));
            Assert.assertEquals(5, predictions.length);
            for (AutocompletePrediction prediction : predictions) {
                for (int j = 0; j < (prediction.types.length); j++) {
                    Assert.assertFalse(prediction.types[j].equals("route"));
                    Assert.assertFalse(prediction.types[j].equals("establishment"));
                }
            }
        }
    }

    @Test
    public void testPlaceAutocompleteWithStrictBounds() throws Exception {
        try (LocalTestServerContext sc = new LocalTestServerContext(placesApiPlaceAutocomplete)) {
            SessionToken session = new SessionToken();
            PlacesApi.placeAutocomplete(sc.context, "Amoeba", session).types(PlaceAutocompleteType.ESTABLISHMENT).location(new LatLng(37.76999, (-122.44696))).radius(500).strictBounds(true).await();
            sc.assertParamValue("Amoeba", "input");
            sc.assertParamValue("establishment", "types");
            sc.assertParamValue("37.76999000,-122.44696000", "location");
            sc.assertParamValue("500", "radius");
            sc.assertParamValue("true", "strictbounds");
            sc.assertParamValue(session.toUrlValue(), "sessiontoken");
        }
    }

    @Test
    public void testKitaWard() throws Exception {
        try (LocalTestServerContext sc = new LocalTestServerContext(placesApiKitaWard)) {
            String query = "Kita Ward, Kyoto, Kyoto Prefecture, Japan";
            PlacesSearchResponse response = PlacesApi.textSearchQuery(sc.context, query).await();
            sc.assertParamValue(query, "query");
            Assert.assertEquals("Kita Ward, Kyoto, Kyoto Prefecture, Japan", response.results[0].formattedAddress);
            Assert.assertTrue(Arrays.asList(response.results[0].types).contains("ward"));
        }
    }

    @Test
    public void testFindPlaceFromText() throws Exception {
        try (LocalTestServerContext sc = new LocalTestServerContext(findPlaceFromTextMuseumOfContemporaryArt)) {
            String input = "Museum of Contemporary Art Australia";
            FindPlaceFromText response = PlacesApi.findPlaceFromText(sc.context, input, TEXT_QUERY).fields(PHOTOS, FORMATTED_ADDRESS, FindPlaceFromTextRequest.FieldMask.NAME, RATING, OPENING_HOURS, GEOMETRY).locationBias(new LocationBiasIP()).await();
            sc.assertParamValue(input, "input");
            sc.assertParamValue("textquery", "inputtype");
            sc.assertParamValue("photos,formatted_address,name,rating,opening_hours,geometry", "fields");
            sc.assertParamValue("ipbias", "locationbias");
            Assert.assertNotNull(response);
            PlacesSearchResult candidate = response.candidates[0];
            Assert.assertNotNull(candidate);
            Assert.assertEquals("140 George St, The Rocks NSW 2000, Australia", candidate.formattedAddress);
            LatLng location = candidate.geometry.location;
            Assert.assertEquals((-33.8599358), location.lat, 1.0E-5);
            Assert.assertEquals(151.2090295, location.lng, 1.0E-5);
            Assert.assertEquals("Museum of Contemporary Art Australia", candidate.name);
            Assert.assertEquals(true, candidate.openingHours.openNow);
            Photo photo = candidate.photos[0];
            Assert.assertEquals("CmRaAAAAXBZe3QrziBst5oTCPUzL4LSgSuWYMctBNRu8bOP4TfwD0aU80YemnnbhjWdFfMX-kkh5h9NhFJky6fW5Ivk_G9fc11GekI0HOCDASZH3qRJmUBsdw0MWoCDZmwQAg-dVEhBb0aLoJXzoZ8cXWEceB9omGhRrX24jI3VnSEQUmInfYoAwSX4OPw", photo.photoReference);
            Assert.assertEquals(2268, photo.height);
            Assert.assertEquals(4032, photo.width);
            Assert.assertEquals(4.4, candidate.rating, 0.01);
        }
    }

    @Test
    public void testFindPlaceFromTextPoint() throws Exception {
        try (LocalTestServerContext sc = new LocalTestServerContext(findPlaceFromTextMuseumOfContemporaryArt)) {
            String input = "Museum of Contemporary Art Australia";
            PlacesApi.findPlaceFromText(sc.context, input, TEXT_QUERY).fields(PHOTOS, FORMATTED_ADDRESS, FindPlaceFromTextRequest.FieldMask.NAME, RATING, OPENING_HOURS, GEOMETRY).locationBias(new com.google.maps.FindPlaceFromTextRequest.LocationBiasPoint(new LatLng(1, 2))).await();
            sc.assertParamValue(input, "input");
            sc.assertParamValue("textquery", "inputtype");
            sc.assertParamValue("photos,formatted_address,name,rating,opening_hours,geometry", "fields");
            sc.assertParamValue("point:1.00000000,2.00000000", "locationbias");
        }
    }

    @Test
    public void testFindPlaceFromTextCircular() throws Exception {
        try (LocalTestServerContext sc = new LocalTestServerContext(findPlaceFromTextMuseumOfContemporaryArt)) {
            String input = "Museum of Contemporary Art Australia";
            PlacesApi.findPlaceFromText(sc.context, input, TEXT_QUERY).fields(PHOTOS, FORMATTED_ADDRESS, FindPlaceFromTextRequest.FieldMask.NAME, RATING, OPENING_HOURS, GEOMETRY).locationBias(new com.google.maps.FindPlaceFromTextRequest.LocationBiasCircular(new LatLng(1, 2), 3000)).await();
            sc.assertParamValue(input, "input");
            sc.assertParamValue("textquery", "inputtype");
            sc.assertParamValue("photos,formatted_address,name,rating,opening_hours,geometry", "fields");
            sc.assertParamValue("circle:3000@1.00000000,2.00000000", "locationbias");
        }
    }

    @Test
    public void testFindPlaceFromTextRectangular() throws Exception {
        try (LocalTestServerContext sc = new LocalTestServerContext(findPlaceFromTextMuseumOfContemporaryArt)) {
            String input = "Museum of Contemporary Art Australia";
            PlacesApi.findPlaceFromText(sc.context, input, TEXT_QUERY).fields(PHOTOS, FORMATTED_ADDRESS, FindPlaceFromTextRequest.FieldMask.NAME, RATING, OPENING_HOURS, GEOMETRY).locationBias(new com.google.maps.FindPlaceFromTextRequest.LocationBiasRectangular(new LatLng(1, 2), new LatLng(3, 4))).await();
            sc.assertParamValue(input, "input");
            sc.assertParamValue("textquery", "inputtype");
            sc.assertParamValue("photos,formatted_address,name,rating,opening_hours,geometry", "fields");
            sc.assertParamValue("rectangle:1.00000000,2.00000000|3.00000000,4.00000000", "locationbias");
        }
    }
}

