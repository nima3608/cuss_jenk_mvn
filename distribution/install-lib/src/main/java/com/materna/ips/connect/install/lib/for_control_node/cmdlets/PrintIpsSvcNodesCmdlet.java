package com.materna.ips.connect.install.lib.for_control_node.cmdlets;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.stream.Collectors.toList;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.materna.ips.connect.system.cfg.types.ClusterCfg;
import com.materna.ips.connect.system.cfg.types.ClusterGroupCfg;
import com.materna.ips.connect.system.cfg.types.IpsSvcNodeCfg;
import com.materna.ips.connect.system.cfg.types.IpsSvcNodeType;
import com.materna.phi.install.lib.app.cmdlets.types.Cmdlet;
import com.materna.phi.install.lib.app.impl.IpsAdminAppBase.CfgWithHelp;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

/**
 * Print out information about the configured IPS-services-nodes.
 *
 * @author mbrinkma
 * @since 20.07.2023
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class PrintIpsSvcNodesCmdlet implements Cmdlet {
    @Getter
    @NoArgsConstructor(staticName = "of")
    @Parameters(commandDescription = "Print out information about the configured IPS-services-nodes.")
    public static class PrintIpsSvcNodesCfg implements CfgWithHelp {
        public static final String ARG_SORT_BY = "-sortBy";
        private static final List<String> NODE_TYPE_NAMES = Arrays.stream(IpsSvcNodeType.values()).map(sn -> sn.name()).collect(toList());
        // private static final String NODE_TYPE_NAMES_AS_STRING =
        // Joiner.on(',').join(NODE_TYPE_NAMES);
        public static final String ARG_NODE_TYPES = "-nodeTypes";
        @Parameter(names = { "-h", "--help" }, description = "Print help.", help = true)
        private boolean printHelp;
        @Parameter(names = CommonArgNames.ARG_CLUSTER_NAME, description = "The name of the cluster the nodes belong to.")
        private String clusterName = System.getenv("ACTIVE_CLUSTER_NAME");
        @Parameter(names = ARG_NODE_TYPES, description = "The type(s) of the nodes to print (separated by comma).")
        private List<String> nodeTypes = Lists.newArrayList(NODE_TYPE_NAMES);
        @Parameter(names = ARG_SORT_BY, description = "How the entries shall be sorted. Possible values: " + IpsSvcNodeSorter.NAME_ALL)
        private String sortBy;

        public boolean isJustPrintNodeList() {
            return true;
        }

        public List<IpsSvcNodeType> getNodeTypeEnums() {
            return nodeTypes.stream().map(s -> IpsSvcNodeType.parse(s)).collect(toList());
        }
    }

    @Getter
    @RequiredArgsConstructor(staticName = "of")
    public static class PrintIpsSvcNodesCmdletFactory
            implements CmdletFactory<ClusterGroupCfg, PrintIpsSvcNodesCmdlet, PrintIpsSvcNodesCfg> {
        public static final String CMD_NAME = "printIpsSvcNodes";
        @SuppressFBWarnings("SS_SHOULD_BE_STATIC")
        private final String optionName = CMD_NAME;

        @Override
        public PrintIpsSvcNodesCfg getJCommanderParamObject() {
            return PrintIpsSvcNodesCfg.of();
        }

        @Override
        public PrintIpsSvcNodesCmdlet createCmd(ClusterGroupCfg clusterGroupCfg, PrintIpsSvcNodesCfg params, CmdletContext ctx) {
            return new PrintIpsSvcNodesCmdlet(params, ctx, clusterGroupCfg);
        }
    }

    private final PrintIpsSvcNodesCfg myCfg;
    private final CmdletContext ctx;
    private final ClusterGroupCfg clusterGroupCfg;

    @Override
    @SneakyThrows
    public void run() {
        final ClusterCfg clusterCfg = clusterGroupCfg.getCluster(myCfg.getClusterName());
        checkNotNull(clusterCfg, "cluster '%s' is not configured", myCfg.getClusterName());
        final List<IpsSvcNodeCfg> selectedIpsSvcNodes = StreamSupport.stream(clusterCfg.getIpsSvcNodes().spliterator(), false)
                .filter(sn -> matches(sn)).collect(toList());
        if (myCfg.isJustPrintNodeList()) {
            Stream<IpsSvcNodeCfg> nodesStream1 = selectedIpsSvcNodes.stream();
            if (myCfg.getSortBy() != null) {
                nodesStream1 = nodesStream1.sorted(IpsSvcNodeSorter.parse(myCfg.getSortBy()));
            }
            final Stream<String> nodesStream2 = nodesStream1.map(sn -> sn.getNodeId());
            final String output = Joiner.on(' ').join(nodesStream2.collect(toList()));
            ctx.println(output);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    private boolean matches(IpsSvcNodeCfg sn) {
        return myCfg.getNodeTypeEnums().contains(sn.getIpsSvcType());
    }
}
