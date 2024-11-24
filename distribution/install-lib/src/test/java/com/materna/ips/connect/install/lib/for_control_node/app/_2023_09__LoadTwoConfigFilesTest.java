package com.materna.ips.connect.install.lib.for_control_node.app;

import org.testng.annotations.Test;

import com.google.common.collect.Lists;
import com.materna.ips.connect.install.lib.for_control_node.cmdlets.GetCfgCmdlet.GetCfgCfg;
import com.materna.ips.connect.install.lib.for_control_node.cmdlets.GetCfgCmdlet.GetCfgCmdletFactory;
import com.materna.ips.connect.install.lib.util.CmdletContextForTests;
import com.materna.ips.connect.install.lib.util.UtilForTesting;

/**
 * Test that parameters can be found not only in e.g. "cluster.blue", but in
 * "cluster" as well.
 *
 * Requirements: file src/test/resources/ci-system/ci-system.yaml
 *
 * @author mbrinkma
 * @since 21.09.2023
 *
 */
public class _2023_09__LoadTwoConfigFilesTest extends ControlNodeAppTestBase {
    @Test
    public void testGetValuePos() {
        try (CmdletContextForTests ctx = UtilForTesting.createContext()) {
            callCmdEx(ctx, GetCfgCmdletFactory.CMD_NAME, GetCfgCfg.ARG_ITEM_KEY, "cluster.blue.ips-services.instances.list");
            assertOneResult(ctx, "app1 adm1");
        }
        try (CmdletContextForTests ctx = UtilForTesting.createContext()) {
            callCmdEx(ctx, GetCfgCmdletFactory.CMD_NAME, GetCfgCfg.ARG_ITEM_KEY, "test.omitIpsServices", GetCfgCfg.ARG_DEFAULT_VALUE,
                    "false");
            assertOneResult(ctx, "false");
        }
    }

    private void callCmdEx(CmdletContextForTests ctx, String... args) {
        super.callCmd(Lists.newArrayList(UtilForTesting.AWS_INSTALLHOST_YAML_PATH, UtilForTesting.CI_SYSTEM_YAML_PATH), ctx, args);
    }
}
