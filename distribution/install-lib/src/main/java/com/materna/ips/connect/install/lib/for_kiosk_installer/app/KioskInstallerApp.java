package com.materna.ips.connect.install.lib.for_kiosk_installer.app;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;

import com.beust.jcommander.Parameter;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.materna.ips.connect.install.lib.for_kiosk_installer.app.impl.KioskInstallerCfg;
import com.materna.ips.connect.install.lib.for_kiosk_installer.cmdlets.CopyAndAdaptFileCmdlet.CopyAndAdaptFileCmdletFactory;
import com.materna.ips.connect.install.lib.for_kiosk_installer.cmdlets.GetCfgCmdlet.GetCfgCmdletFactory;
import com.materna.ips.connect.system.util.YamlUtil;
import com.materna.ips.connect.util.cfg.YamlPropertyCvt;
import com.materna.phi.install.lib.app.cmdlets.types.Cmdlet.CmdletContext;
import com.materna.phi.install.lib.app.cmdlets.types.Cmdlet.CmdletFactory;
import com.materna.phi.install.lib.app.impl.IpsAdminAppBase.CfgWithHelp;
import com.materna.phi.install.lib.app.impl.IpsAdminAppBase2;
import com.materna.phi.install.lib.app.impl.IpsAdminAppCmdletContext;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

/**
 * @author mbrinkma
 * @since 15.02.2024
 *
 */
public class KioskInstallerApp extends IpsAdminAppBase2 {

    @Getter
    @RequiredArgsConstructor(staticName = "of")
    public static class AppMainCfg implements CfgWithHelp {
        private static final String INSTALL_CONFIG_FILE_NAME = "kiosk-install.config";
        public static final String ARG_CFG_VALUE = "-cfgValue";
        @Parameter(names = IpsAdminAppBase2.OPT_CFG_FILE, description = "path(s) of the kiosk-installer-configuration-file(s).")
        public List<String> cfgFilePaths = Lists.newLinkedList();

        private final CmdletContext ctx;

        @Parameter(names = ARG_CFG_VALUE, description = "A cfg-value for overwriting cfgs in the config-file (form is key=value).")
        public List<String> cfgValues = Lists.newLinkedList();
        @Parameter(names = "-h", description = "Print help.")
        public boolean printHelp = false;

        public KioskInstallerCfg onAfterParsing() {
            Properties rc1 = new Properties();
            if (cfgFilePaths.isEmpty()) {
                readCfgFile(rc1, INSTALL_CONFIG_FILE_NAME);
            } else {
                for (final String cfgFileName : cfgFilePaths) {
                    readCfgFile(rc1, cfgFileName);
                }
            }
            // fill in special variables
            return KioskInstallerCfg.of(rc1);
        }

        @SneakyThrows
        void readCfgFile(Properties targetProps, String fileName) {
            try (Reader fr = new InputStreamReader(new FileInputStream(fileName), Charsets.UTF_8)) {
                final Map<String, Object> yamlAsMap = YamlUtil.yamlToMap(fr);
                final Properties props = YamlPropertyCvt.yamlToProperties(yamlAsMap);
                targetProps.putAll(props);
            }
        }
    }

    @SuppressWarnings("rawtypes")
    public static final ImmutableList<CmdletFactory> CMD_FACTORIES = ImmutableList.<CmdletFactory> builder()
    // @formatter:off
            .add(CopyAndAdaptFileCmdletFactory.of())
            .add(GetCfgCmdletFactory.of())
            .build();
    // @formatter:on

    public static void main(String... args) {
        final IpsAdminAppCmdletContext ctx = IpsAdminAppCmdletContext.of("Kiosk Installer", "Tool for installing a kiosk.");
        main(ctx, args);
    }

    @VisibleForTesting
    public static void main(CmdletContext ctx, String... args) {
        initLogging();
        final Collection<String> args2 = Lists.newArrayList(args);
        final AppMainCfg mainCfg = AppMainCfg.of(ctx);
        parseArgumentsAndThenExecuteParsedCommandsIntern(ctx, CMD_FACTORIES, args2, mainCfg, mainCfg2 -> mainCfg2.onAfterParsing(), true);
    }

    private static void initLogging() {
        Logger.getRootLogger().getLoggerRepository().resetConfiguration();
        RollingFileAppender fa = new RollingFileAppender();
        fa.setName("FileLogger");
        fa.setFile("work/logs/installer.log");
        fa.setLayout(new PatternLayout("%d %-5p [%c{1}] %m%n"));
        fa.setThreshold(Level.DEBUG);
        fa.setAppend(true);
        fa.setMaxBackupIndex(5);
        fa.setMaxFileSize("10MB");
        fa.activateOptions();

        // add appender to any Logger (here is root)
        Logger.getRootLogger().addAppender(fa);
        Logger.getRootLogger().setLevel(Level.DEBUG);
        Logger.getLogger(KioskInstallerApp.class).info("----------------------------start---------------------------");
    }
}
