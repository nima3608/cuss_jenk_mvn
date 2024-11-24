package com.materna.ips.connect.install.lib.kiosk;

import static org.assertj.core.api.Assertions.assertThat;

import org.testng.annotations.Test;

import com.materna.ips.connect.install.lib.for_kiosk_installer.app.KioskInstallerApp;
import com.materna.ips.connect.install.lib.for_kiosk_installer.cmdlets.GetCfgCmdlet.GetCfgCfg;
import com.materna.ips.connect.install.lib.for_kiosk_installer.cmdlets.GetCfgCmdlet.GetCfgCmdletFactory;
import com.materna.ips.connect.install.lib.util.CmdletContextForTests;
import com.materna.phi.install.lib.app.impl.IpsAdminAppBase2;

/**
 * @author mbrinkma
 * @since 20.02.2024
 *
 */
public class GetCfgCmdletTests {
    @Test
    public void test_print_a_config_entry() {
        try (CmdletContextForTests ctx = new CmdletContextForTests(false, true)) {
            //@formatter:off
            KioskInstallerApp.main(ctx,
                    IpsAdminAppBase2.OPT_CFG_FILE, ONCE_5739_adapt_files_Tests.CONFIG_FILE_PATH,
                    GetCfgCmdletFactory.CMD_NAME,
                    GetCfgCfg.ARG_ITEM_KEY, "application.path");
            //@formatter:on
            final String output = ctx.getMessages().get(0);
            assertThat(output).isEqualTo("c:\\temp\\kiosk-tests");
        }
    }

}
