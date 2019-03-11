/**
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 *
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.core.offer.service.processor;


import OfferDiscountType.PERCENT_OFF;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import junit.framework.TestCase;
import org.broadleafcommerce.core.offer.dao.OfferDao;
import org.broadleafcommerce.core.offer.domain.Offer;
import org.broadleafcommerce.core.offer.domain.OfferItemCriteria;
import org.broadleafcommerce.core.offer.domain.OfferItemCriteriaImpl;
import org.broadleafcommerce.core.offer.domain.OfferQualifyingCriteriaXref;
import org.broadleafcommerce.core.offer.domain.OfferQualifyingCriteriaXrefImpl;
import org.broadleafcommerce.core.offer.service.OfferDataItemProvider;
import org.broadleafcommerce.core.offer.service.discount.CandidatePromotionItems;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableCandidateOrderOffer;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableOrder;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableOrderItem;


/**
 *
 *
 * @author jfischer
 */
public class OrderOfferProcessorTest extends TestCase {
    protected OfferDao offerDaoMock;

    protected OrderOfferProcessorImpl orderProcessor;

    protected OfferDataItemProvider dataProvider = new OfferDataItemProvider();

    protected OfferTimeZoneProcessor offerTimeZoneProcessorMock;

    public void testFilterOffers() throws Exception {
        replay();
        PromotableOrder order = dataProvider.createBasicPromotableOrder();
        List<Offer> offers = dataProvider.createCustomerBasedOffer("customer.registered==true", dataProvider.yesterday(), dataProvider.yesterday(), PERCENT_OFF);
        orderProcessor.filterOffers(offers, order.getOrder().getCustomer());
        // confirm out-of-date orders are filtered out
        TestCase.assertTrue(((offers.size()) == 0));
        offers = dataProvider.createCustomerBasedOffer("customer.registered==true", dataProvider.yesterday(), dataProvider.tomorrow(), PERCENT_OFF);
        orderProcessor.filterOffers(offers, order.getOrder().getCustomer());
        // confirm valid customer offer is retained
        TestCase.assertTrue(((offers.size()) == 1));
        offers = dataProvider.createCustomerBasedOffer("customer.registered==false", dataProvider.yesterday(), dataProvider.tomorrow(), PERCENT_OFF);
        orderProcessor.filterOffers(offers, order.getOrder().getCustomer());
        // confirm invalid customer offer is culled
        TestCase.assertTrue(((offers.size()) == 0));
        verify();
    }

    public void testFilterOrderLevelOffer() throws Exception {
        replay();
        PromotableOrder order = dataProvider.createBasicPromotableOrder();
        List<PromotableCandidateOrderOffer> qualifiedOffers = new ArrayList<PromotableCandidateOrderOffer>();
        List<Offer> offers = dataProvider.createOrderBasedOffer("order.subTotal.getAmount()>20", PERCENT_OFF);
        orderProcessor.filterOrderLevelOffer(order, qualifiedOffers, offers.get(0));
        // test that the valid order offer is included
        TestCase.assertTrue((((qualifiedOffers.size()) == 1) && (qualifiedOffers.get(0).getOffer().equals(offers.get(0)))));
        qualifiedOffers = new ArrayList<PromotableCandidateOrderOffer>();
        offers = dataProvider.createOrderBasedOfferWithItemCriteria("order.subTotal.getAmount()>20", PERCENT_OFF, "([MVEL.eval(\"toUpperCase()\",\"test1\"), MVEL.eval(\"toUpperCase()\",\"test2\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))");
        orderProcessor.filterOrderLevelOffer(order, qualifiedOffers, offers.get(0));
        // test that the valid order offer is included
        TestCase.assertTrue((((qualifiedOffers.size()) == 1) && (qualifiedOffers.get(0).getOffer().equals(offers.get(0)))));
        qualifiedOffers = new ArrayList<PromotableCandidateOrderOffer>();
        offers = dataProvider.createOrderBasedOfferWithItemCriteria("order.subTotal.getAmount()>20", PERCENT_OFF, "([5,6] contains discreteOrderItem.category.id.intValue())");
        orderProcessor.filterOrderLevelOffer(order, qualifiedOffers, offers.get(0));
        // test that the invalid order offer is excluded
        TestCase.assertTrue(((qualifiedOffers.size()) == 0));
        verify();
    }

    public void testCouldOfferApplyToOrder() throws Exception {
        replay();
        PromotableOrder order = dataProvider.createBasicPromotableOrder();
        List<Offer> offers = dataProvider.createOrderBasedOffer("order.subTotal.getAmount()>20", PERCENT_OFF);
        boolean couldApply = orderProcessor.couldOfferApplyToOrder(offers.get(0), order, order.getDiscountableOrderItems().get(0), order.getFulfillmentGroups().get(0));
        // test that the valid order offer is included
        TestCase.assertTrue(couldApply);
        offers = dataProvider.createOrderBasedOffer("order.subTotal.getAmount()==0", PERCENT_OFF);
        couldApply = orderProcessor.couldOfferApplyToOrder(offers.get(0), order, order.getDiscountableOrderItems().get(0), order.getFulfillmentGroups().get(0));
        // test that the invalid order offer is excluded
        TestCase.assertFalse(couldApply);
        verify();
    }

    public void testCouldOrderItemMeetOfferRequirement() throws Exception {
        replay();
        PromotableOrder order = dataProvider.createBasicPromotableOrder();
        List<Offer> offers = dataProvider.createOrderBasedOfferWithItemCriteria("order.subTotal.getAmount()>20", PERCENT_OFF, "([MVEL.eval(\"toUpperCase()\",\"test1\"), MVEL.eval(\"toUpperCase()\",\"test2\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))");
        OfferQualifyingCriteriaXref xref = offers.get(0).getQualifyingItemCriteriaXref().iterator().next();
        boolean couldApply = orderProcessor.couldOrderItemMeetOfferRequirement(xref.getOfferItemCriteria(), order.getDiscountableOrderItems().get(0));
        // test that the valid order offer is included
        TestCase.assertTrue(couldApply);
        offers = dataProvider.createOrderBasedOfferWithItemCriteria("order.subTotal.getAmount()>20", PERCENT_OFF, "([MVEL.eval(\"toUpperCase()\",\"test5\"), MVEL.eval(\"toUpperCase()\",\"test6\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))");
        xref = offers.get(0).getQualifyingItemCriteriaXref().iterator().next();
        couldApply = orderProcessor.couldOrderItemMeetOfferRequirement(xref.getOfferItemCriteria(), order.getDiscountableOrderItems().get(0));
        // test that the invalid order offer is excluded
        TestCase.assertFalse(couldApply);
        verify();
    }

    public void testCouldOfferApplyToOrderItems() throws Exception {
        replay();
        PromotableOrder order = dataProvider.createBasicPromotableOrder();
        List<Offer> offers = dataProvider.createOrderBasedOfferWithItemCriteria("order.subTotal.getAmount()>20", PERCENT_OFF, "([MVEL.eval(\"toUpperCase()\",\"test1\"), MVEL.eval(\"toUpperCase()\",\"test2\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))");
        List<PromotableOrderItem> orderItems = new ArrayList<PromotableOrderItem>();
        for (PromotableOrderItem orderItem : order.getDiscountableOrderItems()) {
            orderItems.add(orderItem);
        }
        CandidatePromotionItems candidates = orderProcessor.couldOfferApplyToOrderItems(offers.get(0), orderItems);
        // test that the valid order offer is included
        TestCase.assertTrue(((candidates.isMatchedQualifier()) && ((candidates.getCandidateQualifiersMap().size()) == 1)));
        offers = dataProvider.createOrderBasedOfferWithItemCriteria("order.subTotal.getAmount()>20", PERCENT_OFF, "([MVEL.eval(\"toUpperCase()\",\"test5\"), MVEL.eval(\"toUpperCase()\",\"test6\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))");
        candidates = orderProcessor.couldOfferApplyToOrderItems(offers.get(0), orderItems);
        // test that the invalid order offer is excluded because there are no qualifying items
        TestCase.assertFalse(((candidates.isMatchedQualifier()) && ((candidates.getCandidateQualifiersMap().size()) == 1)));
        verify();
    }

    public void testQualifyingQuantity() throws Exception {
        replay();
        PromotableOrder order = dataProvider.createBasicPromotableOrder();
        List<Offer> offers = dataProvider.createOrderBasedOffer("order.subTotal.getAmount()>20", PERCENT_OFF);
        Offer firstOffer = offers.get(0);
        OfferItemCriteria qualCriteria = new OfferItemCriteriaImpl();
        int originalQuantityOnOrder = 5;
        qualCriteria.setQuantity((originalQuantityOnOrder + 1));
        qualCriteria.setMatchRule("([MVEL.eval(\"toUpperCase()\",\"test1\"), MVEL.eval(\"toUpperCase()\",\"test2\")] contains MVEL.eval(\"toUpperCase()\", discreteOrderItem.category.name))");
        Set<OfferQualifyingCriteriaXref> criterias = new HashSet<OfferQualifyingCriteriaXref>();
        OfferQualifyingCriteriaXref xref = new OfferQualifyingCriteriaXrefImpl();
        xref.setOffer(firstOffer);
        xref.setOfferItemCriteria(qualCriteria);
        criterias.add(xref);
        firstOffer.setQualifyingItemCriteriaXref(criterias);
        List<PromotableOrderItem> orderItems = new ArrayList<PromotableOrderItem>();
        for (PromotableOrderItem orderItem : order.getDiscountableOrderItems()) {
            orderItems.add(orderItem);
        }
        CandidatePromotionItems candidates = orderProcessor.couldOfferApplyToOrderItems(offers.get(0), orderItems);
        // test that the valid order offer is not included
        TestCase.assertTrue(((!(candidates.isMatchedQualifier())) && ((candidates.getCandidateQualifiersMap().size()) == 1)));
        int quantity = orderItems.get(0).getOrderItem().getQuantity();
        orderItems.get(0).getOrderItem().setQuantity((quantity + 1));
        candidates = orderProcessor.couldOfferApplyToOrderItems(offers.get(0), orderItems);
        // test that the valid order offer is included
        TestCase.assertTrue(((candidates.isMatchedQualifier()) && ((candidates.getCandidateQualifiersMap().size()) == 1)));
        verify();
    }
}

