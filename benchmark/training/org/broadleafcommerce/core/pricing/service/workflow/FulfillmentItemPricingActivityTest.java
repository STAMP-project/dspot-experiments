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
package org.broadleafcommerce.core.pricing.service.workflow;


import ProductBundlePricingModelType.BUNDLE;
import java.math.BigDecimal;
import junit.framework.TestCase;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.core.offer.domain.OrderAdjustment;
import org.broadleafcommerce.core.offer.domain.OrderAdjustmentImpl;
import org.broadleafcommerce.core.offer.service.OfferDataItemProvider;
import org.broadleafcommerce.core.order.domain.BundleOrderItem;
import org.broadleafcommerce.core.order.domain.FulfillmentGroup;
import org.broadleafcommerce.core.order.domain.FulfillmentGroupItem;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.core.workflow.ProcessContext;


public class FulfillmentItemPricingActivityTest extends TestCase {
    private OfferDataItemProvider dataProvider = new OfferDataItemProvider();

    private FulfillmentItemPricingActivity fulfillmentItemPricingActivity = new FulfillmentItemPricingActivity();

    public void testZeroOrderSavings() throws Exception {
        Order order = dataProvider.createBasicOrder();
        ProcessContext<Order> context = new org.broadleafcommerce.core.workflow.DefaultProcessContextImpl<Order>();
        context.setSeedData(order);
        fulfillmentItemPricingActivity.execute(context);
        TestCase.assertTrue(((sumProratedOfferAdjustments(order).getAmount().compareTo(BigDecimal.ZERO)) == 0));
    }

    public void testDistributeOneDollarAcrossFiveEqualItems() throws Exception {
        Order order = dataProvider.createBasicOrder();
        Money subTotal = new Money(order.getCurrency());
        for (OrderItem orderItem : order.getOrderItems()) {
            orderItem.setSalePrice(new Money(10.0));
            orderItem.getOrderItemPriceDetails().clear();
            subTotal = subTotal.add(orderItem.getTotalPrice());
        }
        OrderAdjustment adjustment = new OrderAdjustmentImpl();
        adjustment.setValue(new Money(new BigDecimal("1"), order.getCurrency()));
        order.getOrderAdjustments().add(adjustment);
        adjustment.setOrder(order);
        order.setSubTotal(subTotal);
        ProcessContext<Order> context = new org.broadleafcommerce.core.workflow.DefaultProcessContextImpl<Order>();
        context.setSeedData(order);
        fulfillmentItemPricingActivity.execute(context);
        // Each item is equally priced, so the adjustment should be .20 per item.
        Money proratedAdjustment = new Money(".20");
        for (FulfillmentGroup fulfillmentGroup : order.getFulfillmentGroups()) {
            for (FulfillmentGroupItem fulfillmentGroupItem : fulfillmentGroup.getFulfillmentGroupItems()) {
                TestCase.assertTrue(((fulfillmentGroupItem.getProratedOrderAdjustmentAmount().compareTo(proratedAdjustment.multiply(fulfillmentGroupItem.getQuantity()))) == 0));
            }
        }
    }

    public void testDistributeOneDollarAcrossFiveItems() throws Exception {
        Order order = dataProvider.createBasicOrder();
        OrderAdjustment adjustment = new OrderAdjustmentImpl();
        adjustment.setValue(new Money(new BigDecimal("1"), order.getCurrency()));
        adjustment.setOrder(order);
        order.getOrderAdjustments().add(adjustment);
        ProcessContext<Order> context = new org.broadleafcommerce.core.workflow.DefaultProcessContextImpl<Order>();
        context.setSeedData(order);
        fulfillmentItemPricingActivity.execute(context);
        Money adj1 = new Money(".31");
        Money adj2 = new Money(".69");
        for (FulfillmentGroup fulfillmentGroup : order.getFulfillmentGroups()) {
            for (FulfillmentGroupItem fulfillmentGroupItem : fulfillmentGroup.getFulfillmentGroupItems()) {
                if (fulfillmentGroupItem.getSalePrice().equals(new Money("19.99"))) {
                    TestCase.assertTrue(fulfillmentGroupItem.getProratedOrderAdjustmentAmount().equals(adj1));
                } else {
                    TestCase.assertTrue(fulfillmentGroupItem.getProratedOrderAdjustmentAmount().equals(adj2));
                }
            }
        }
    }

    public void testRoundingRequired() throws Exception {
        Order order = dataProvider.createBasicOrder();
        Money subTotal = new Money(order.getCurrency());
        for (OrderItem orderItem : order.getOrderItems()) {
            orderItem.getOrderItemPriceDetails().clear();
            orderItem.setQuantity(2);
            orderItem.setSalePrice(new Money(10.0));
            subTotal = subTotal.add(orderItem.getTotalPrice());
        }
        order.setSubTotal(subTotal);
        OrderAdjustment adjustment = new OrderAdjustmentImpl();
        adjustment.setValue(new Money(new BigDecimal(".05"), order.getCurrency()));
        adjustment.setOrder(order);
        order.getOrderAdjustments().add(adjustment);
        ProcessContext<Order> context = new org.broadleafcommerce.core.workflow.DefaultProcessContextImpl<Order>();
        context.setSeedData(order);
        fulfillmentItemPricingActivity.execute(context);
        TestCase.assertTrue(sumProratedOfferAdjustments(order).equals(new Money(new BigDecimal(".05"), order.getCurrency())));
    }

    public void testBundleDistribution() throws Exception {
        Order order = dataProvider.createOrderWithBundle();
        Money subTotal = new Money(order.getCurrency());
        for (OrderItem orderItem : order.getOrderItems()) {
            subTotal = subTotal.add(orderItem.getTotalPrice());
        }
        order.setSubTotal(subTotal);
        OrderAdjustment adjustment = new OrderAdjustmentImpl();
        adjustment.setValue(new Money(new BigDecimal("1"), order.getCurrency()));
        adjustment.setOrder(order);
        order.getOrderAdjustments().add(adjustment);
        ProcessContext<Order> context = new org.broadleafcommerce.core.workflow.DefaultProcessContextImpl<Order>();
        context.setSeedData(order);
        fulfillmentItemPricingActivity.execute(context);
        TestCase.assertTrue(sumProratedOfferAdjustments(order).equals(new Money(new BigDecimal("1"), order.getCurrency())));
    }

    public void testBundleDistributionWithoutItemSum() throws Exception {
        Order order = dataProvider.createOrderWithBundle();
        Money subTotal = new Money(order.getCurrency());
        for (OrderItem orderItem : order.getOrderItems()) {
            if (orderItem instanceof BundleOrderItem) {
                BundleOrderItem bItem = ((BundleOrderItem) (orderItem));
                bItem.getProductBundle().setPricingModel(BUNDLE);
            }
            subTotal = subTotal.add(orderItem.getTotalPrice());
        }
        order.setSubTotal(subTotal);
        OrderAdjustment adjustment = new OrderAdjustmentImpl();
        adjustment.setValue(new Money(new BigDecimal("1"), order.getCurrency()));
        adjustment.setOrder(order);
        order.getOrderAdjustments().add(adjustment);
        ProcessContext<Order> context = new org.broadleafcommerce.core.workflow.DefaultProcessContextImpl<Order>();
        context.setSeedData(order);
        fulfillmentItemPricingActivity.execute(context);
        TestCase.assertTrue(sumProratedOfferAdjustments(order).equals(new Money(new BigDecimal("1"), order.getCurrency())));
    }
}

