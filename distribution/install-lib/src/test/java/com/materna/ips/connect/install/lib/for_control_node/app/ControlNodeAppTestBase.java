package com.materna.ips.connect.install.lib.for_control_node.app;

import static com.materna.ips.connect.install.lib.util.UtilForTesting.CI_SYSTEM_PROPERTIES_PATH;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.materna.ips.connect.install.lib.util.CmdletContextForTests;

/**
 * @author mbrinkma
 * @since 21.09.2023
 *
 */
public class ControlNodeAppTestBase {

    protected String consumeResult(CmdletContextForTests ctx) {
        final String rc = ctx.getMessages().get(0);
        ctx.cleanup();
        return rc;
    }

    protected void assertOneResult(final CmdletContextForTests ctx, String expected) {
        assertThat(ctx.getMessages()).hasSize(1);
        assertThat(ctx.getMessages().get(0)).isEqualTo(expected);
    }

    protected void callCmd(CmdletContextForTests ctx, String... args) {
        callCmd(ImmutableList.of(CI_SYSTEM_PROPERTIES_PATH), ctx, args);
    }

    protected void callCmd(Collection<String> configFilePaths, CmdletContextForTests ctx, String... args) {
        final List<String> args2 = Lists.newArrayList();
        for (final String configFilePath : configFilePaths) {
            args2.add(ControlNodeApp.OPT_CFG_FILE);
            args2.add(configFilePath);
        }
        for (final String arg : args) {
            args2.add(arg);
        }
        ControlNodeApp.main(ctx, args2);
    }

}
