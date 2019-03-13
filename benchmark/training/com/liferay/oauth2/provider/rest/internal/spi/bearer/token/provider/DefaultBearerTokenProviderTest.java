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
package com.liferay.oauth2.provider.rest.internal.spi.bearer.token.provider;


import BearerTokenProvider.AccessToken;
import BearerTokenProvider.RefreshToken;
import com.liferay.oauth2.provider.rest.spi.bearer.token.provider.BearerTokenProvider;
import com.liferay.portal.kernel.security.SecureRandomUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


/**
 *
 *
 * @author Tomas Polesovsky
 */
@PrepareForTest(SecureRandomUtil.class)
@RunWith(PowerMockRunner.class)
public class DefaultBearerTokenProviderTest extends PowerMockito {
    @Test
    public void testGenerateTokenKey() {
        Assert.assertEquals("", _defaultBearerTokenProvider.generateTokenKey(0));
        Assert.assertEquals(DefaultBearerTokenProviderTest._TOKEN_KEY_STRING_32_BYTES_HEX.substring(0, 4), _defaultBearerTokenProvider.generateTokenKey(2));
        Assert.assertEquals(DefaultBearerTokenProviderTest._TOKEN_KEY_STRING_32_BYTES_HEX.substring(0, 16), _defaultBearerTokenProvider.generateTokenKey(8));
        Assert.assertEquals(DefaultBearerTokenProviderTest._TOKEN_KEY_STRING_32_BYTES_HEX.substring(0, 20), _defaultBearerTokenProvider.generateTokenKey(10));
        Assert.assertEquals(((DefaultBearerTokenProviderTest._TOKEN_KEY_STRING_32_BYTES_HEX) + (DefaultBearerTokenProviderTest._TOKEN_KEY_STRING_32_BYTES_HEX)), _defaultBearerTokenProvider.generateTokenKey(64));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGenerateTokenKeyWithNegativeKeySize() {
        _defaultBearerTokenProvider.generateTokenKey((-1));
    }

    @Test
    public void testIsValidAccessToken() {
        long issuedAtNow = (System.currentTimeMillis()) / 1000;
        Assert.assertTrue(_defaultBearerTokenProvider.isValid(generateAccessToken(DefaultBearerTokenProviderTest._ACCESS_TOKEN_EXPIRES_IN, issuedAtNow)));
        Assert.assertFalse(_defaultBearerTokenProvider.isValid(generateAccessToken((-1), issuedAtNow)));
        Assert.assertFalse(_defaultBearerTokenProvider.isValid(generateAccessToken(DefaultBearerTokenProviderTest._ACCESS_TOKEN_EXPIRES_IN, (issuedAtNow - ((DefaultBearerTokenProviderTest._ACCESS_TOKEN_EXPIRES_IN) + 1)))));
        Assert.assertFalse(_defaultBearerTokenProvider.isValid(generateAccessToken(DefaultBearerTokenProviderTest._ACCESS_TOKEN_EXPIRES_IN, (issuedAtNow + (DefaultBearerTokenProviderTest._ACCESS_TOKEN_EXPIRES_IN)))));
    }

    @Test
    public void testIsValidRefreshToken() {
        long issuedAtNow = (System.currentTimeMillis()) / 1000;
        Assert.assertTrue(_defaultBearerTokenProvider.isValid(generateRefreshToken(DefaultBearerTokenProviderTest._REFRESH_TOKEN_EXPIRES_IN, issuedAtNow)));
        Assert.assertFalse(_defaultBearerTokenProvider.isValid(generateRefreshToken((-1), issuedAtNow)));
        Assert.assertFalse(_defaultBearerTokenProvider.isValid(generateRefreshToken(DefaultBearerTokenProviderTest._REFRESH_TOKEN_EXPIRES_IN, (issuedAtNow - ((DefaultBearerTokenProviderTest._REFRESH_TOKEN_EXPIRES_IN) + 1)))));
        Assert.assertFalse(_defaultBearerTokenProvider.isValid(generateRefreshToken(DefaultBearerTokenProviderTest._REFRESH_TOKEN_EXPIRES_IN, (issuedAtNow + (DefaultBearerTokenProviderTest._REFRESH_TOKEN_EXPIRES_IN)))));
    }

    @Test
    public void testOnBeforeCreateAccessToken() {
        BearerTokenProvider.AccessToken accessToken = generateAccessToken(0, 0);
        _defaultBearerTokenProvider.onBeforeCreate(accessToken);
        Assert.assertEquals(accessToken.getExpiresIn(), DefaultBearerTokenProviderTest._ACCESS_TOKEN_EXPIRES_IN);
        Assert.assertEquals(DefaultBearerTokenProviderTest._TOKEN_KEY_STRING_32_BYTES_HEX, accessToken.getTokenKey());
    }

    @Test
    public void testOnBeforeCreateRefreshToken() {
        BearerTokenProvider.RefreshToken refreshToken = generateRefreshToken(0, 0);
        _defaultBearerTokenProvider.onBeforeCreate(refreshToken);
        Assert.assertEquals(refreshToken.getExpiresIn(), DefaultBearerTokenProviderTest._REFRESH_TOKEN_EXPIRES_IN);
        Assert.assertEquals(DefaultBearerTokenProviderTest._TOKEN_KEY_STRING_32_BYTES_HEX, refreshToken.getTokenKey());
    }

    private static final int _ACCESS_TOKEN_EXPIRES_IN = 600;

    private static final int _ACCESS_TOKEN_KEY_BYTE_SIZE = 32;

    private static final int _REFRESH_TOKEN_EXPIRES_IN = 604800;

    private static final int _REFRESH_TOKEN_KEY_BYTE_SIZE = 32;

    private static final long _TOKEN_KEY_LONG = -2392855065499157826L;

    private static final String _TOKEN_KEY_STRING_32_BYTES_HEX = "decadefeededbabedecadefeededbabedecadefeededbabedecadefeededbabe";

    private DefaultBearerTokenProvider _defaultBearerTokenProvider;
}

