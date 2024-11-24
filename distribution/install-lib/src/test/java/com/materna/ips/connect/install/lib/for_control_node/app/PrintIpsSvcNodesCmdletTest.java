package com.materna.ips.connect.install.lib.for_control_node.app;

import org.assertj.core.api.Assertions;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.materna.ips.connect.install.lib.for_control_node.cmdlets.CommonArgNames;
import com.materna.ips.connect.install.lib.for_control_node.cmdlets.IpsSvcNodeSorter;
import com.materna.ips.connect.install.lib.for_control_node.cmdlets.PrintIpsSvcNodesCmdlet.PrintIpsSvcNodesCfg;
import com.materna.ips.connect.install.lib.for_control_node.cmdlets.PrintIpsSvcNodesCmdlet.PrintIpsSvcNodesCmdletFactory;
import com.materna.ips.connect.install.lib.util.CmdletContextForTests;
import com.materna.ips.connect.install.lib.util.ConstantsForTesting;
import com.materna.ips.connect.install.lib.util.UtilForTesting;
import com.materna.ips.connect.system.cfg.types.IpsSvcNodeType;

/**
 * @author mbrinkma
 * @since 20.07.2023
 *
 */
public class PrintIpsSvcNodesCmdletTest {
    @Test
    public void testPrintIpsSvcNodesAsList() {
        try (CmdletContextForTests ctx = UtilForTesting.createContext()) {
            callCmd(ctx, CommonArgNames.ARG_CLUSTER_NAME, ConstantsForTesting.CLUSTER_BLUE);
            final String rc = ctx.getMessages().get(0);
            Assertions.assertThat(rc).isEqualTo("app1 adm1");
        }
    }

    @Test
    public void testPrintIpsSvcNodesAsListAdminNodesFirst() {
        try (CmdletContextForTests ctx = UtilForTesting.createContext()) {
            callCmd(ctx, CommonArgNames.ARG_CLUSTER_NAME, ConstantsForTesting.CLUSTER_BLUE, PrintIpsSvcNodesCfg.ARG_SORT_BY,
                    IpsSvcNodeSorter.NAME_ADMIN_NODES_FIRST);
            final String rc = ctx.getMessages().get(0);
            Assertions.assertThat(rc).isEqualTo("adm1 app1");
        }
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testPrintIpsSvcNodesAsListErrorInvalidClusterName() {
        try (CmdletContextForTests ctx = UtilForTesting.createContext()) {
            callCmd(ctx, CommonArgNames.ARG_CLUSTER_NAME, ConstantsForTesting.CLUSTER_BLUE + "xx");
            Assert.fail();
        }
    }

    @Test
    public void testPrintIpsSvcAppserverNodesAsList() {
        try (CmdletContextForTests ctx = UtilForTesting.createContext()) {
            callCmd(ctx, CommonArgNames.ARG_CLUSTER_NAME, ConstantsForTesting.CLUSTER_BLUE, PrintIpsSvcNodesCfg.ARG_NODE_TYPES,
                    IpsSvcNodeType.APP.name());
            final String rc = ctx.getMessages().get(0);
            Assertions.assertThat(rc).isEqualTo("app1");
        }
    }

    private void callCmd(CmdletContextForTests ctx, String... args) {
        UtilForTesting.callCmd(ctx, PrintIpsSvcNodesCmdletFactory.CMD_NAME, args);
    }
}
