package com.materna.ips.connect.install.lib.for_control_node.app;

import org.testng.annotations.Test;

import com.materna.ips.connect.install.lib.for_control_node.app.impl.MainCfg;
import com.materna.ips.connect.install.lib.for_control_node.cmdlets.GetCfgCmdlet.GetCfgCfg;
import com.materna.ips.connect.install.lib.for_control_node.cmdlets.GetCfgCmdlet.GetCfgCmdletFactory;
import com.materna.ips.connect.install.lib.util.CmdletContextForTests;
import com.materna.ips.connect.install.lib.util.UtilForTesting;

/**
 * Test that parameters can be found not only in e.g. "cluster.blue", but in "cluster" as well.
 *
 * Requirements: file src/test/resources/ci-system/ci-system.yaml
 *
 * @author mbrinkma
 * @since 21.09.2023
 *
 */
public class _2023_09__ItemKeyWithOptionalPrefixTest extends ControlNodeAppTestBase {
    @Test
    public void testGetValuePos() {
        try (CmdletContextForTests ctx = UtilForTesting.createContext()) {
            final String expectedOutput = "1.2.3";
            // @formatter:off
            callCmd(ctx,
                    MainCfg.ARG_CFG_VALUE, "cluster.services.version=" + expectedOutput,
                    GetCfgCmdletFactory.CMD_NAME,
                    GetCfgCfg.ARG_CLUSTER_NAME, "blue",
                    GetCfgCfg.ARG_ITEM_KEY, "services.version"
                    );
            // @formatter:on
            assertOneResult(ctx, expectedOutput);
        }
    }

    @Test
    public void testGetValueExactPos() {
        try (CmdletContextForTests ctx = UtilForTesting.createContext()) {
            final String expectedOutput = "1.2.3";
            // @formatter:off
            callCmd(ctx,
                    MainCfg.ARG_CFG_VALUE, "cluster.blue.services.version=" + expectedOutput,
                    MainCfg.ARG_CFG_VALUE, "cluster.services.version=" + expectedOutput+"xx",
                    GetCfgCmdletFactory.CMD_NAME,
                    GetCfgCfg.ARG_CLUSTER_NAME, "blue",
                    GetCfgCfg.ARG_ITEM_KEY, "services.version"
                    );
            // @formatter:on
            assertOneResult(ctx, expectedOutput);
        }
    }

    @Test
    public void testGetValueNeg() {
        try (CmdletContextForTests ctx = UtilForTesting.createContext()) {
            final String expectedOutput = "1.2.3";
            // @formatter:off
            callCmd(ctx,
                    MainCfg.ARG_CFG_VALUE, "cluster.green.services.version=" + expectedOutput,
                    GetCfgCmdletFactory.CMD_NAME,
                    GetCfgCfg.ARG_CLUSTER_NAME, "blue",
                    GetCfgCfg.ARG_ITEM_KEY, "services.version"
                    );
            // @formatter:on
            assertOneResult(ctx, "");
        }
    }
}
