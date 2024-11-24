/**
 * (c) Materna GmbH 2022
 */

package com.materna.ips.connect.install.lib.util;

import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

import com.materna.ips.connect.install.lib.for_control_node.app.ControlNodeApp;

/**
 * @author mbrinkma
 * @since 24.07.2022
 *
 */
public class UtilForTesting {
    public static final String CI_SYSTEM_PROPERTIES_PATH = "src/test/resources/config/ci-system/ci-system.properties";
    public static final String CI_SYSTEM_YAML_PATH = "src/test/resources/config/ci-system/ci-system.yaml";
    public static final String AWS_INSTALLHOST_YAML_PATH = "src/test/resources/config/common/aws-install-host.yaml";

    public static CmdletContextForTests createContext() {
        return new CmdletContextForTests(false, true);
    }

    public static void callCmd(CmdletContextForTests ctx, String cmdName, String... args) {
        final List<String> args2 = newArrayList(ControlNodeApp.OPT_CFG_FILE, CI_SYSTEM_YAML_PATH,
                cmdName);
        for (final String arg : args) {
            args2.add(arg);
        }
        ControlNodeApp.main(ctx, args2);
    }
}
