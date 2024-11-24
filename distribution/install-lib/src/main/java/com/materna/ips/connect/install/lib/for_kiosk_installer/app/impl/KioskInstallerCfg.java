package com.materna.ips.connect.install.lib.for_kiosk_installer.app.impl;

import java.io.File;
import java.io.FilenameFilter;
import java.net.InetAddress;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

import com.materna.buc.commons.text.substitution.PropertyTableSubstitutor;
import com.materna.ips.connect.install.lib.for_kiosk_installer.util.KioskInstallerTextPatternSubstitutor;
import com.materna.phi.install.lib.util.HostUtil;

import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * @author mbrinkma
 * @since 16.02.2024
 *
 */
@Data
@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class KioskInstallerCfg {
    private static final String APPLICATION_PATH = "application.path";
    private static final String[] OPTIONAL_KEYS = new String[]{"proxy.host", "proxy.port", "browser.args.additional"};
    private final Properties properties;

    @SneakyThrows
    public static KioskInstallerCfg of(Properties properties) {
        Properties rc1 = new Properties();
        rc1.setProperty("host.name.short", HostUtil.getLocalHostNameShort());
        rc1.setProperty("host.ip.v4", InetAddress.getLocalHost().getHostAddress());
        for (final String key : OPTIONAL_KEYS) {
            rc1.setProperty(key, "");
        }
        rc1.putAll(properties);
        tryToSetPropertyForFile(rc1, "kiosk_app.jar", APPLICATION_PATH, "/ka", "kioskApp-service.*\\.jar");
        tryToSetPropertyForFile(rc1, "action_provider.jar", APPLICATION_PATH, "/ap", "actionProvider.*\\.jar");
        final PropertyTableSubstitutor subst = PropertyTableSubstitutor
                .builder().delegate(KioskInstallerTextPatternSubstitutor.of(rc1)).build();
        final Properties rc2 = subst.replace(rc1).getProperties();
        return new KioskInstallerCfg(rc2);
    }

    private static void tryToSetPropertyForFile(Properties props, String targetKey, String pathPropertyName, String pathSuffix,
            String fileNamePattern) {
        log.debug("> tryToSetPropertyForFile({}, {}, {}, {})", targetKey, pathPropertyName, pathSuffix, props);
        boolean found = false;
        if (props.getProperty(targetKey) == null) {
            final String path1 = props.getProperty(pathPropertyName);
            if (path1 != null) {
                final String path = path1 + pathSuffix;
                final File file = tryFindFileInPath(path, fileNamePattern);
                if (file != null) {
                    final String filePath = file.getAbsolutePath();
                    log.info("setting {}='{}'", targetKey, filePath);
                    props.setProperty(targetKey, filePath);
                    found = true;
                }
            } else {
                log.debug("{} is not set", pathPropertyName);
            }
        } else {
            log.debug("{} is already set", targetKey);
        }
        log.debug("< tryToSetPropertyForFile(), found={}", found);
    }

    private static @Nullable File tryFindFileInPath(String path, String fileNamePattern) {
        File rc = null;
        final File dir = new File(path);
        if (dir.isDirectory()) {
            final File[] filesFound = dir.listFiles(new FileFilterImpl(fileNamePattern));
            if (filesFound.length > 0) {
                rc = filesFound[0];
            }
        }
        log.debug("><tryFindFileInPath({}, {}), rc={}", path, fileNamePattern, rc);
        return rc;
    }

    static class FileFilterImpl implements FilenameFilter {
        private final Pattern pattern;

        public FileFilterImpl(String fileNamePattern) {
            pattern = Pattern.compile(fileNamePattern);
        }

        @Override
        public boolean accept(File dir, String name) {
            final Matcher m = pattern.matcher(name);
            boolean rc = m.matches();
            return rc;
        }
    }
}
