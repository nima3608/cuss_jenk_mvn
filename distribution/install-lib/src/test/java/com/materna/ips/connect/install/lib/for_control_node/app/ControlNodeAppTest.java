/**
 * (c) Materna GmbH 2022
 */

package com.materna.ips.connect.install.lib.for_control_node.app;

import org.testng.annotations.Test;

import com.materna.ips.connect.install.lib.for_control_node.app.impl.MainCfg;
import com.materna.ips.connect.install.lib.for_control_node.cmdlets.GetCfgCmdlet.GetCfgCfg;
import com.materna.ips.connect.install.lib.for_control_node.cmdlets.GetCfgCmdlet.GetCfgCmdletFactory;
import com.materna.ips.connect.install.lib.util.CmdletContextForTests;
import com.materna.ips.connect.install.lib.util.ConstantsForTesting;
import com.materna.ips.connect.install.lib.util.UtilForTesting;

/**
 * @author mbrinkma
 * @since 23.07.2022
 *
 */
public class ControlNodeAppTest extends ControlNodeAppTestBase {

    @Test
    public void testGetListOfBlueAppNodes() {
        final CmdletContextForTests ctx = UtilForTesting.createContext();
        callCmd(ctx, GetCfgCmdletFactory.CMD_NAME, GetCfgCfg.ARG_ITEM_KEY, "cluster.blue.ips-services.instances.list");
        assertOneResult(ctx, "app1");
    }

    @Test
    public void testLoopUpdateableIpsSvcNodes() {
        final CmdletContextForTests ctx = UtilForTesting.createContext();
        callCmd(ctx, GetCfgCmdletFactory.CMD_NAME, GetCfgCfg.ARG_UPDATEABLE_CLUSTERS);
        assertOneResult(ctx, ConstantsForTesting.CLUSTER_BLUE);
        final String updateableCluster = consumeResult(ctx);
        callCmd(ctx, GetCfgCmdletFactory.CMD_NAME, GetCfgCfg.ARG_CLUSTER_NAME, updateableCluster, GetCfgCfg.ARG_IPS_SERVICES,
                GetCfgCfg.ARG_ITEM_KEY, "instances.list");
        assertOneResult(ctx, "app1");
        final String svc = consumeResult(ctx);
        callCmd(ctx, GetCfgCmdletFactory.CMD_NAME, GetCfgCfg.ARG_CLUSTER_NAME, updateableCluster, GetCfgCfg.ARG_IPS_SERVICE, svc,
                GetCfgCfg.ARG_ITEM_KEY, "user");
        assertOneResult(ctx, "massai");
    }

    @Test
    public void testGetDefaultValue() {
        try (CmdletContextForTests ctx = UtilForTesting.createContext()) {
            callCmd(ctx, GetCfgCmdletFactory.CMD_NAME, GetCfgCfg.ARG_ITEM_KEY, "clustergroup.db.reset-required",
                    GetCfgCfg.ARG_DEFAULT_VALUE, "false");
            assertOneResult(ctx, "false");
        }

        try (CmdletContextForTests ctx = UtilForTesting.createContext()) {
            callCmd(ctx, GetCfgCmdletFactory.CMD_NAME, GetCfgCfg.ARG_ITEM_KEY, "cluster.blue.updateable", GetCfgCfg.ARG_DEFAULT_VALUE,
                    "false");
            assertOneResult(ctx, "true");
        }

        try (CmdletContextForTests ctx = UtilForTesting.createContext()) {
            callCmd(ctx, GetCfgCmdletFactory.CMD_NAME, GetCfgCfg.ARG_ITEM_KEY, "cluster.blue.xxx");
            assertOneResult(ctx, "");
        }
    }

    @Test
    public void testOverwriteCfgFromCmdLine() {
        try (CmdletContextForTests ctx = UtilForTesting.createContext()) {
            callCmd(ctx, MainCfg.ARG_CFG_VALUE, "cluster.blue.updateable=false" // is true
                                                                      // in
                                                                      // cfg-file
                    , GetCfgCmdletFactory.CMD_NAME, GetCfgCfg.ARG_ITEM_KEY, "cluster.blue.updateable");
            assertOneResult(ctx, "false");
        }
    }
}
