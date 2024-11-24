/**
 * (c) Materna GmbH 2022
 */

package com.materna.ips.connect.install.lib.for_control_node.app.impl;

import java.util.List;

import com.beust.jcommander.Parameter;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.materna.ips.connect.system.cfg.types.ClusterGroupCfg;
import com.materna.phi.install.lib.app.cmdlets.types.Cmdlet.CmdletContext;
import com.materna.phi.install.lib.app.impl.IpsAdminAppBase.CfgWithHelp;
import com.materna.phi.install.lib.app.impl.IpsAdminAppBase2;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author mbrinkma
 * @since 23.07.2022
 *
 */
@Getter
@RequiredArgsConstructor(staticName = "of")
public class MainCfg implements CfgWithHelp {
    public static final String ARG_CFG_VALUE = "-cfgValue";
    private static final Splitter KEY_VALUE_SPLITTER = Splitter.on("=").limit(2);

    private final CmdletContext ctx;

    @Parameter(names = IpsAdminAppBase2.OPT_CFG_FILE, description = "path(s) of the clustergroup-configuration-file(s).")
    public List<String> cfgFilePaths = Lists.newLinkedList();
    @Parameter(names = ARG_CFG_VALUE, description = "A cfg-value for overwriting cfgs in the config-file (form is key=value).")
    public List<String> cfgValues = Lists.newLinkedList();
    @Parameter(names = "-h", description = "Print help.")
    public boolean printHelp = false;

    public ClusterGroupCfg onAfterParsing() {
        if (cfgFilePaths.isEmpty()) {
            IpsAdminAppBase2.exitWithError(ctx, "please specify " + IpsAdminAppBase2.OPT_CFG_FILE + " <path-to-file>");
        }
        final ClusterGroupCfg cfg = ClusterGroupCfg.ofConfigFilePaths(cfgFilePaths);
        final ClusterGroupCfg.Builder cfgB = ClusterGroupCfg.builder().configFilePaths(cfgFilePaths);
        for (final String keyValuePair : cfgValues) {
            final List<String> l = KEY_VALUE_SPLITTER.splitToList(keyValuePair);
            if (l.size() != 2) {
                IpsAdminAppBase2.exitWithError(ctx,
                        "do not understand " + keyValuePair);
            }
            final String key = l.get(0);
            final String value = l.get(1);
            final boolean overwriteForbidden = Boolean.parseBoolean(cfg.getProperty(key + "-overwrite-forbidden", "false"));
            if (overwriteForbidden) {
                IpsAdminAppBase2.exitWithError(ctx,
                        "you are not allowed to overwrite cfg-entry \"" + key + "\"!");
            } else {
                cfgB.setCfgKeyValue(key, value);
            }
        }
        return cfgB.build();
    }
}
