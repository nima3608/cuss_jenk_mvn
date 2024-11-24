/**
 * (c) Materna GmbH 2017
 */

package com.materna.ips.connect.install.lib.for_kiosk_installer.cmdlets;

import java.util.Optional;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.materna.ips.connect.install.lib.for_kiosk_installer.app.impl.KioskInstallerCfg;
import com.materna.phi.install.lib.app.cmdlets.types.Cmdlet;
import com.materna.phi.install.lib.app.impl.IpsAdminAppBase.CfgWithHelp;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

/**
 * Print out entries of the configuration-file.
 *
 * @author mbrinkma
 * @since 10.08.2017
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class GetCfgCmdlet implements Cmdlet {
    @Getter
    @NoArgsConstructor(staticName = "of")
    @Parameters(commandDescription = "Print out the value of one or more cfg-entries given by their keys.")
    public static class GetCfgCfg implements Validatable, CfgWithHelp {
        public static final String ARG_ITEM_KEY = "-itemKey";
        public static final String ARG_DEFAULT_VALUE = "-defaultValue";
        @Parameter(names = { "-h", "--help" }, description = "Print help.", help = true)
        private boolean printHelp;
        @Parameter(names = ARG_ITEM_KEY, description = "key of the searched item(s)", required = false)
        public String itemKey;
        @Parameter(names = ARG_DEFAULT_VALUE, description = "if value for key cannot be found, take this default", required = false)
        private String defaultValue;

        public static GetCfgCfg forTest(final String filterKey, final Integer filterFirstN, final String key) {
            GetCfgCfg rc = new GetCfgCfg();
            rc.itemKey = key;
            return rc;
        }

        @Override
        public Optional<String> validate() {
            String rc = null;
            return Optional.ofNullable(rc);
        }
    }

    @Getter
    @RequiredArgsConstructor(staticName = "of")
    public static class GetCfgCmdletFactory implements CmdletFactory<KioskInstallerCfg, GetCfgCmdlet, GetCfgCfg> {
        public static final String CMD_NAME = "getCfgEntry";
        @SuppressFBWarnings("SS_SHOULD_BE_STATIC")
        private final String optionName = CMD_NAME;

        @Override
        public GetCfgCfg getJCommanderParamObject() {
            return GetCfgCfg.of();
        }

        @Override
        public GetCfgCmdlet createCmd(KioskInstallerCfg appConfig, GetCfgCfg params, CmdletContext ctx) {
            return new GetCfgCmdlet(appConfig, params, ctx);
        }
    }

    private final KioskInstallerCfg appConfig;
    private final GetCfgCfg getCfgCfg;
    private final CmdletContext ctx;

    @Override
    @SneakyThrows
    public void run() {
        final String rc1 = appConfig.getProperties().getProperty(getCfgCfg.getItemKey(), getCfgCfg.getDefaultValue());
        ctx.println(rc1);
    }
}
