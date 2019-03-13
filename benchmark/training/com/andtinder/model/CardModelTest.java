/**
 * AndTinder v0.1 for Android
 *
 * @unknown Enrique L?pez Ma?as <eenriquelopez@gmail.com>
http://www.lopez-manas.com

TAndTinder is a native library for Android that provide a
Tinder card like effect. A card can be constructed using an
image and displayed with animation effects, dismiss-to-like
and dismiss-to-unlike, and use different sorting mechanisms.

AndTinder is compatible with API Level 13 and upwards
 * @unknown Enrique L?pez Ma?as
 * @unknown Apache License 2.0
 */
package com.andtinder.model;


import CardModel.OnCardDismissedListener;
import CardModel.OnClickListener;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test for the CarModel
 */
public class CardModelTest {
    @Test
    public void testConstructorDefaultValueCheck() {
        // when
        CardModel cardModel = new CardModel();
        // then
        Assert.assertNull("title property value must be null for zero argument constructor", cardModel.getTitle());
        Assert.assertNull("description property value must be null for zero argument constructor", cardModel.getDescription());
        Assert.assertNull("cardImageDrawable property value must be null for zero argument constructor", cardModel.getCardImageDrawable());
        Assert.assertNull("cardLikeImageDrawable property value must be null for zero argument constructor", cardModel.getCardLikeImageDrawable());
        Assert.assertNull("cardDislikeImageDrawable property value must be null for zero argument constructor", cardModel.getCardDislikeImageDrawable());
        Assert.assertNull("mOnCardDismissedListener property value must be null for zero argument constructor", cardModel.getOnCardDismissedListener());
        Assert.assertNull("mOnClickListener property value must be null for zero argument constructor", cardModel.getOnClickListener());
    }

    @Test
    public void testConstructorValueCheck() {
        // given
        String title = "New Year Greeting Card";
        String description = "Happy New Year";
        Drawable drawable = mock(Drawable.class);
        // when
        CardModel cardModel = new CardModel(title, description, drawable);
        // then
        Assert.assertEquals("title property value must be equal to 'New Year Greeting Card' ", title, cardModel.getTitle());
        Assert.assertEquals("description property value must be equal to 'Happy New Year'", description, cardModel.getDescription());
        Assert.assertEquals("cardLikeImageDrawable property instances must be equal", drawable, cardModel.getCardImageDrawable());
    }

    @Test
    public void testConstructorBitmapValueCheck() {
        // given
        String title = "New Year Greeting Card";
        String description = "Happy New Year";
        Bitmap bitMap = mock(Bitmap.class);
        // when
        CardModel cardModel = new CardModel(title, description, bitMap);
        // then
        Assert.assertEquals("title property value must be equal to 'New Year Greeting Card' ", title, cardModel.getTitle());
        Assert.assertEquals("description property value must be equal to 'Happy New Year'", description, cardModel.getDescription());
        Assert.assertNotNull("cardImageDrawable property value must be not null", cardModel.getCardImageDrawable());
    }

    @Test
    public void testSetTitleAndDescriptionProperties() {
        // given
        CardModel cardModel = new CardModel();
        // when
        cardModel.setTitle("New Year Greeting Card");
        // then
        Assert.assertEquals("title property value must be equal to 'New Year Greeting Card' ", "New Year Greeting Card", cardModel.getTitle());
        // when
        cardModel.setDescription("Happy New Year");
        // then
        Assert.assertEquals("description property value must be equal to 'Happy New Year'", "Happy New Year", cardModel.getDescription());
    }

    @Test
    public void testSetCardProperties() {
        // given
        CardModel cardModel = new CardModel();
        Drawable cardImageDrawable = mock(Drawable.class);
        // when
        cardModel.setCardImageDrawable(cardImageDrawable);
        // then
        Assert.assertEquals("cardImageDrawable property instances must be equal", cardImageDrawable, cardModel.getCardImageDrawable());
        // given
        Drawable cardLikeImageDrawable = mock(Drawable.class);
        // when
        cardModel.setCardLikeImageDrawable(cardLikeImageDrawable);
        // then
        Assert.assertEquals("cardLikeImageDrawable property instances must be equal", cardLikeImageDrawable, cardModel.getCardLikeImageDrawable());
        // given
        Drawable cardDislikeImageDrawable = mock(Drawable.class);
        // when
        cardModel.setCardDislikeImageDrawable(cardDislikeImageDrawable);
        // then
        Assert.assertEquals("cardDislikeImageDrawable property instances must be equal", cardDislikeImageDrawable, cardModel.getCardDislikeImageDrawable());
    }

    @Test
    public void testSetPropertyListeners() {
        // given
        CardModel cardModel = new CardModel();
        CardModel.OnCardDismissedListener onCardDismissedListener = mock(OnCardDismissedListener.class);
        // when
        cardModel.setOnCardDismissedListener(onCardDismissedListener);
        // then
        Assert.assertEquals("mOnCardDismissedListener property instances must be equal", onCardDismissedListener, cardModel.getOnCardDismissedListener());
        // given
        CardModel.OnClickListener onClickListener = mock(OnClickListener.class);
        // when
        cardModel.setOnClickListener(onClickListener);
        // then
        Assert.assertEquals("mOnClickListener property instances must be equal", onClickListener, cardModel.getOnClickListener());
    }
}

