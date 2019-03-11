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
package com.liferay.portal.lpkg.deployer.override;


import StringPool.BLANK;
import StringPool.COLON;
import StringPool.NEW_LINE;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.Validator;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Matthew Tambara
 */
public class LPKGOverrideTest {
    @Test
    public void testOverrideLPKG() throws IOException {
        String liferayHome = System.getProperty("liferay.home");
        Assert.assertFalse("Missing system property \"liferay.home\"", Validator.isNull(liferayHome));
        File file = new File(liferayHome, "/osgi/marketplace/override");
        for (File subfiles : file.listFiles()) {
            subfiles.delete();
        }
        Map<String, String> overrides = new HashMap<>();
        List<String> lpkgStaticFileNames = _getStaticLPKGFileNames();
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(liferayHome, "/osgi/marketplace"), "*.lpkg")) {
            for (Path lpkgPath : directoryStream) {
                try (ZipFile zipFile = new ZipFile(lpkgPath.toFile())) {
                    Enumeration<? extends ZipEntry> zipEntries = zipFile.entries();
                    while (zipEntries.hasMoreElements()) {
                        ZipEntry zipEntry = zipEntries.nextElement();
                        String name = zipEntry.getName();
                        if ((!((name.startsWith("com.liferay")) && (name.endsWith(".jar")))) && (!(name.endsWith(".war")))) {
                            continue;
                        }
                        Matcher matcher = LPKGOverrideTest._pattern.matcher(name);
                        Assert.assertTrue(((name + " does not match ") + (LPKGOverrideTest._pattern)), matcher.matches());
                        name = (matcher.group(1)) + (matcher.group(4));
                        Path lpkgPathName = lpkgPath.getFileName();
                        if (lpkgStaticFileNames.contains(lpkgPathName.toString())) {
                            Path staticOverridePath = Paths.get(liferayHome, "/osgi/static/", name);
                            Files.copy(zipFile.getInputStream(zipEntry), staticOverridePath, StandardCopyOption.REPLACE_EXISTING);
                            _upgradeModuleVersion(staticOverridePath, null);
                            overrides.put("static.".concat(matcher.group(1)), null);
                        } else {
                            Path overridePath = Paths.get(file.toString(), name);
                            Files.copy(zipFile.getInputStream(zipEntry), overridePath, StandardCopyOption.REPLACE_EXISTING);
                            if (name.endsWith(".war")) {
                                String fileName = matcher.group(1);
                                fileName = fileName.replace("-dxp", BLANK);
                                overrides.put("war.".concat(fileName), null);
                                continue;
                            }
                            _upgradeModuleVersion(overridePath, overrides);
                        }
                    } 
                }
            }
        }
        StringBundler sb = new StringBundler(((overrides.size()) * 4));
        for (Map.Entry<String, String> entry : overrides.entrySet()) {
            sb.append(entry.getKey());
            sb.append(COLON);
            sb.append(entry.getValue());
            sb.append(NEW_LINE);
        }
        sb.setIndex(((sb.index()) - 1));
        Files.write(Paths.get(liferayHome, "/overrides"), Arrays.asList(sb.toString()), StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
    }

    private static final Pattern _pattern = Pattern.compile("(.*?)(-\\d+\\.\\d+\\.\\d+)(\\..+)?(\\.[jw]ar)");
}

