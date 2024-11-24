package com.materna.ips.connect.install.lib.kiosk;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.testng.annotations.Test;

import com.materna.buc.commons.os.OsType;
import com.materna.buc.commons.os.OsUtils;
import com.materna.ips.connect.install.lib.for_kiosk_installer.app.KioskInstallerApp;
import com.materna.ips.connect.install.lib.for_kiosk_installer.cmdlets.CopyAndAdaptFileCmdlet.CopyAndAdaptFileCfg;
import com.materna.ips.connect.install.lib.for_kiosk_installer.cmdlets.CopyAndAdaptFileCmdlet.CopyAndAdaptFileCmdletFactory;
import com.materna.ips.connect.install.lib.util.CmdletContextForTests;
import com.materna.phi.install.lib.app.impl.IpsAdminAppBase2;

import lombok.SneakyThrows;

/**
 * @author mbrinkma
 * @since 19.10.2023
 *
 */
public class ONCE_5739_adapt_files_Tests {
    private static final String inputDir = "src/test/resources/kiosk/";
    private static final String className = ONCE_5739_adapt_files_Tests.class.getSimpleName();
    private static final String BASE_INPUT_PATH = inputDir + className;
    public static final String CONFIG_FILE_PATH = BASE_INPUT_PATH + ".config.yaml";

    @Test
    public void test_adapt_kioskApp_yaml() {
        try (CmdletContextForTests ctx = new CmdletContextForTests(false, true)) {
            replacePlaceholders(ctx, "kioskApp");
            final String output = ctx.getMessages().get(0);
            // @formatter:off
            assertThat(output)
                .startsWith("server:\n" + "    port : 1234")
                .doesNotContain("${-")
                .contains("key-store: file:c:\\temp\\kiosk-tests\\localhost-keystore.pkcs12")
                ;
            // @formatter:on
        }
    }

    @Test
    public void test_adapt_actionProvider_yaml() {
        try (CmdletContextForTests ctx = new CmdletContextForTests(false, true)) {
            replacePlaceholders(ctx, "actionProvider");
            final String output = ctx.getMessages().get(0);
            assertThat(output).startsWith("server:\n" + "    port: 12345").doesNotContain("${-");
        }
    }

    @Test
    @SneakyThrows
    public void test_adapt_actionProvider_yaml__createNonExistingDestDir() {
        if (OsUtils.getOsType() == OsType.Windows) {
            try (CmdletContextForTests ctx = new CmdletContextForTests(false, true)) {
                final String targetDir = "target\\non-existing-dir";
                FileUtils.deleteDirectory(new File(targetDir));
                final String targetFile = targetDir + "\\" + "application.yaml";
                //@formatter:off
                KioskInstallerApp.main(ctx,
                        IpsAdminAppBase2.OPT_CFG_FILE, CONFIG_FILE_PATH
                        , CopyAndAdaptFileCmdletFactory.CMD_NAME
                        , CopyAndAdaptFileCfg.ARG_SRC_FILE, BASE_INPUT_PATH + ".kioskApp.yaml"
                        , CopyAndAdaptFileCfg.ARG_DST_FILE, targetFile
                        );
                //@formatter:on
                assertTrue(new File(targetFile).isFile());
            }
        }
    }

    @Test
    public void test_adapt_start_app_cmd() {
        try (CmdletContextForTests ctx = new CmdletContextForTests(false, true)) {
            replacePlaceholders(ctx, "start-app", "cmd");
            final String output = ctx.getMessages().get(0);
            assertThat(output).contains("set PROXY=some-host:123").doesNotContain("${-");
        }
    }

    private void replacePlaceholders(CmdletContextForTests ctx, String fileNameInfix) {
        replacePlaceholders(ctx, fileNameInfix, "yaml");
    }

    private void replacePlaceholders(CmdletContextForTests ctx, String fileNameInfix, String fileNamePostfix) {
        //@formatter:off
        KioskInstallerApp.main(ctx,
                IpsAdminAppBase2.OPT_CFG_FILE, CONFIG_FILE_PATH,
                CopyAndAdaptFileCmdletFactory.CMD_NAME,
                CopyAndAdaptFileCfg.ARG_SRC_FILE, BASE_INPUT_PATH + "." + fileNameInfix + "." + fileNamePostfix);
        //@formatter:on
    }
}
