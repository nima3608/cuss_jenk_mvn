/**
 * (c) Materna GmbH 2017
 */

package com.materna.ips.connect.install.lib.for_control_node.cmdlets;

import java.util.List;
import java.util.Optional;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.google.common.base.Joiner;
import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;
import com.materna.ips.connect.system.cfg.types.ClusterCfg;
import com.materna.ips.connect.system.cfg.types.ClusterGroupCfg;
import com.materna.ips.connect.system.cfg.types.IpsSvcNodesCfg;
import com.materna.phi.install.lib.app.cmdlets.types.Cmdlet;
import com.materna.phi.install.lib.app.impl.IpsAdminAppBase.CfgWithHelp;
import com.materna.phi.install.lib.properties.IpsPropertyProvider;

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
        public static final String ARG_ITEM_KEY_OPTIONAL_PREFIX = "-optionalItemKeyPrefix";
        public static final String ARG_ITEM_KEY = "-itemKey";
        public static final String ARG_DEFAULT_VALUE = "-defaultValue";
        // for getting "blue" or "green"
        public static final String ARG_UPDATEABLE_CLUSTERS = "-updateableClusters";
        public static final String ARG_CLUSTER_NAME = CommonArgNames.ARG_CLUSTER_NAME;
        public static final String ARG_IPS_SERVICES = "-ipsServices";
        public static final String ARG_IPS_SERVICE = "-ipsService";
        @Parameter(names = { "-h", "--help" }, description = "Print help.", help = true)
        private boolean printHelp;
        @Parameter(names = ARG_ITEM_KEY, description = "key of the searched item(s)", required = false)
        public String itemKey;
        @Parameter(names = ARG_ITEM_KEY_OPTIONAL_PREFIX, description = "optional prefix for key of the searched item(s)", required = false)
        public String itemKeyOptionalPrefix;
        @Parameter(names = ARG_UPDATEABLE_CLUSTERS, description = "get e.g. blue or green", required = false)
        private boolean getUpdateableClusters = false;
        @Parameter(names = ARG_CLUSTER_NAME, description = "refer only to e.g. blue", required = false)
        private String scopeClusterName;
        @Parameter(names = ARG_IPS_SERVICES, description = "scope is the ips-services only", required = false)
        private boolean scopeIpsServices = false;
        @Parameter(names = ARG_IPS_SERVICE, description = "refer only to e.g. app1", required = false)
        private String scopeIpsService;
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
            if (itemKey == null && !getUpdateableClusters) {
                rc = "please specify either an item-key or " + ARG_UPDATEABLE_CLUSTERS;
            }
            return Optional.ofNullable(rc);
        }
    }

    @Getter
    @RequiredArgsConstructor(staticName = "of")
    public static class GetCfgCmdletFactory implements CmdletFactory<ClusterGroupCfg, GetCfgCmdlet, GetCfgCfg> {
        public static final String CMD_NAME = "getCfgEntry";
        @SuppressFBWarnings("SS_SHOULD_BE_STATIC")
        private final String optionName = CMD_NAME;

        @Override
        public GetCfgCfg getJCommanderParamObject() {
            return GetCfgCfg.of();
        }

        @Override
        public GetCfgCmdlet createCmd(ClusterGroupCfg clusterGroupCfg, GetCfgCfg params, CmdletContext ctx) {
            return new GetCfgCmdlet(params, ctx, clusterGroupCfg);
        }
    }

    private final GetCfgCfg getCfgCfg;
    private final CmdletContext ctx;
    private final ClusterGroupCfg clusterGroupCfg;

    @Override
    @SneakyThrows
    public void run() {
        String rc1 = null;
        if (getCfgCfg.isGetUpdateableClusters()) {
            final List<String> clusterNames = Lists.newLinkedList();
            for (final ClusterCfg clusterCfg : clusterGroupCfg.getClusterCfgList()) {
                if (clusterCfg.isUpdateable()) {
                    clusterNames.add(clusterCfg.getId().getId());
                }
            }
            rc1 = Joiner.on(" ").join(clusterNames);
        } else {
            IpsPropertyProvider pp = clusterGroupCfg.getPropertyProvider();
            if (getCfgCfg.getScopeClusterName() != null) {
                pp = pp.getSubProvider("cluster." + getCfgCfg.getScopeClusterName());
            }
            if (getCfgCfg.isScopeIpsServices() || getCfgCfg.getScopeIpsService() != null) {
                pp = pp.getSubProvider(ClusterCfg.PROPKEY_IPS_SERVICES);
                if (getCfgCfg.getScopeIpsService() != null) {
                    pp = pp.getSubProvider(IpsSvcNodesCfg.PROPKEY_INSTANCE).getSubProvider(getCfgCfg.getScopeIpsService());
                }
            }
            if (getCfgCfg.getItemKeyOptionalPrefix() != null) {
                final IpsPropertyProvider pp1 = pp.getSubProvider(getCfgCfg.getItemKeyOptionalPrefix());
                if (pp1 != null) {
                    pp = pp1;
                }
            }
            rc1 = pp.getPropertyBySearchingUpwardsToRoot(getCfgCfg.getItemKey());
            if (rc1 == null) {
                rc1 = MoreObjects.firstNonNull(getCfgCfg.getDefaultValue(), "");
            }
        }
        ctx.println(rc1);
    }
}
