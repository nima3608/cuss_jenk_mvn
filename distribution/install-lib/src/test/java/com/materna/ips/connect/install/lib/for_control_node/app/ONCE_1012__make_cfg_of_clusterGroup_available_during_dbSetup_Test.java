/**
 * (c) Materna GmbH 2022
 */

package com.materna.ips.connect.install.lib.for_control_node.app;

import static org.assertj.core.api.Assertions.assertThat;

import org.testng.annotations.Test;

import com.materna.ips.connect.install.lib.for_control_node.cmdlets.WriteCfgEntriesCmdlet.OutputFormat;
import com.materna.ips.connect.install.lib.for_control_node.cmdlets.WriteCfgEntriesCmdlet.WriteCfgEntiresCfg;
import com.materna.ips.connect.install.lib.for_control_node.cmdlets.WriteCfgEntriesCmdlet.WriteCfgEntriesCmdletFactory;
import com.materna.ips.connect.install.lib.util.CmdletContextForTests;
import com.materna.ips.connect.install.lib.util.ConstantsForTesting;
import com.materna.ips.connect.install.lib.util.UtilForTesting;

/**
 * @author mbrinkma
 * @since 27.09.2022
 *
 */
public class ONCE_1012__make_cfg_of_clusterGroup_available_during_dbSetup_Test {

    @Test
    public void testExportShellVariablesForIpsSvcNode1() {
        try (CmdletContextForTests ctx = UtilForTesting.createContext()) {
            callCmd(ctx, WriteCfgEntiresCfg.ARG_CLUSTER_NAME, ConstantsForTesting.CLUSTER_BLUE, WriteCfgEntiresCfg.ARG_IPS_SERVICE, "app1");
            // @formatter:off
            assertThat(ctx.getMessages())
                .contains("cluster.current.ips-services.instance.current.host=10.23.206.106")
                .contains("cluster.current.name=" + ConstantsForTesting.CLUSTER_BLUE)
                ;
            // @formatter:on
            //assertThat(ctx.getMessages().get(0)).isEqualTo(expected);
        }
    }

    @Test
    public void testExportShellVariablesForIpsSvcNode2() {
        try (CmdletContextForTests ctx = UtilForTesting.createContext()) {
            callCmd(ctx, WriteCfgEntiresCfg.ARG_CLUSTER_NAME, ConstantsForTesting.CLUSTER_BLUE, WriteCfgEntiresCfg.ARG_IPS_SERVICE, "app1", WriteCfgEntiresCfg.ARG_OUTPUT_FORMAT, OutputFormat.SHELL.name());
            // @formatter:off
            assertThat(ctx.getMessages())
                .contains("CLUSTER_CURRENT_IPS_SERVICES_INSTANCE_CURRENT_HOST=10.23.206.106")
                .contains("CLUSTER_CURRENT_NAME=" + ConstantsForTesting.CLUSTER_BLUE)
                ;
            // @formatter:on
            //assertThat(ctx.getMessages().get(0)).isEqualTo(expected);
        }
    }

    private void callCmd(CmdletContextForTests ctx, String... args) {
        UtilForTesting.callCmd(ctx, WriteCfgEntriesCmdletFactory.CMD_NAME, args);
    }
}
