/**
 * (c) Materna GmbH 2022
 */

package com.materna.ips.connect.install.lib.for_control_node.app;

import java.util.Collection;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.materna.ips.connect.install.lib.for_control_node.app.impl.MainCfg;
import com.materna.ips.connect.install.lib.for_control_node.cmdlets.GetCfgCmdlet.GetCfgCmdletFactory;
import com.materna.ips.connect.install.lib.for_control_node.cmdlets.PrintIpsSvcNodesCmdlet.PrintIpsSvcNodesCmdletFactory;
import com.materna.ips.connect.install.lib.for_control_node.cmdlets.PrintTimestampCmdlet.PrintTimestampCmdletFactory;
import com.materna.ips.connect.install.lib.for_control_node.cmdlets.WriteCfgEntriesCmdlet.WriteCfgEntriesCmdletFactory;
import com.materna.ips.connect.system.cfg.types.ClusterGroupCfg;
import com.materna.phi.install.lib.app.cmdlets.types.Cmdlet.CmdletContext;
import com.materna.phi.install.lib.app.cmdlets.types.Cmdlet.CmdletFactory;
import com.materna.phi.install.lib.app.impl.IpsAdminAppBase2;
import com.materna.phi.install.lib.app.impl.IpsAdminAppCmdletContext;

/**
 * @author mbrinkma
 * @since 22.07.2022
 *
 */
public class ControlNodeApp extends IpsAdminAppBase2 {
    // @formatter:off
    @SuppressWarnings("rawtypes")
    public static final ImmutableList<CmdletFactory> CMD_FACTORIES = ImmutableList.<CmdletFactory> builder()
            .add(GetCfgCmdletFactory.of())
            .add(WriteCfgEntriesCmdletFactory.of())
            .add(PrintTimestampCmdletFactory.<ClusterGroupCfg>of())
            .add(PrintIpsSvcNodesCmdletFactory.of())
            .build();
    // @formatter:on

    public static void main(String... args) {
        final IpsAdminAppCmdletContext ctx = IpsAdminAppCmdletContext.of("IPS Connect Control Node Admin Tool",
                "Tool for e.g. reading the config of a connect cluster-group.");
        main(ctx, args);
    }

    @VisibleForTesting
    public static void main(CmdletContext ctx, String... args) {
        final Collection<String> args2 = Lists.newArrayList(args);
        main(ctx, args2);
    }

    @VisibleForTesting
    public static void main(CmdletContext ctx, final Collection<String> args2) {
        final MainCfg mainCfg = MainCfg.of(ctx);
        parseArgumentsAndThenExecuteParsedCommandsIntern(ctx, CMD_FACTORIES, args2, mainCfg, mainCfg2 -> mainCfg2.onAfterParsing(), true);
    }
}
