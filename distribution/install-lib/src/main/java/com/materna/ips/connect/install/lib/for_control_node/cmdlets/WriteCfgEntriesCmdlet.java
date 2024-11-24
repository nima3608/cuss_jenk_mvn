/**
 * (c) Materna GmbH 2022
 */

package com.materna.ips.connect.install.lib.for_control_node.cmdlets;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.function.BiConsumer;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.materna.ips.connect.system.cfg.types.ClusterCfg;
import com.materna.ips.connect.system.cfg.types.ClusterGroupCfg;
import com.materna.ips.connect.system.cfg.types.IpsSvcNodeCfg;
import com.materna.ips.connect.system.cfg.types.IpsSvcNodesCfg;
import com.materna.phi.install.lib.app.cmdlets.types.Cmdlet;
import com.materna.phi.install.lib.app.impl.IpsAdminAppBase.CfgWithHelp;
import com.materna.phi.install.lib.properties.IpsPropertyProvider;
import com.materna.phi.install.lib.properties.IpsPropertyProviderBuilder;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

/**
 * Write the config-entries as key=value-pairs to a file.
 * <p>
 * For supporting the IPS-services-installation, optionally the cfg-entries only
 * regarding a ips-svc-node can be written out, too.
 *
 * @author mbrinkma
 * @since 05.08.2022
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class WriteCfgEntriesCmdlet implements Cmdlet {
    private static final String PROPKEY_CURRENT = "current";
    private static final String PROPKEY_NAME = "name";

    public enum OutputFormat {
        STD, SHELL
    }

    @Getter
    @NoArgsConstructor(staticName = "of")
    @Parameters(commandDescription = "Write out cfg-entries.")
    public static class WriteCfgEntiresCfg implements CfgWithHelp, Validatable {
        public static final String ARG_OUTPUT_FORMAT = "-outputFormat";
        public static final String ARG_CLUSTER_NAME = CommonArgNames.ARG_CLUSTER_NAME;
        public static final String ARG_IPS_SERVICE = "-currentIpsService";
        @Parameter(names = { "-h", "--help" }, description = "Print help.", help = true)
        private boolean printHelp;
        @Parameter(names = "-outputFile", description = "The file to write the cfg to.", required = false)
        private String outputFileName;
        @Parameter(names = ARG_CLUSTER_NAME, description = "Select the given cluster as \"current\".", required = false)
        private String currentClusterName;
        @Parameter(names = ARG_IPS_SERVICE, description = "Select the given IPS-services-node as \"current\".", required = false)
        private String currentIpsSvcNode;
        @Parameter(names = ARG_OUTPUT_FORMAT, description = "The output-format (default=STD, SHELL: Avoid \".\" and \"-\" in property-keys (replace them with \"_\")).")
        private OutputFormat outputFormat = OutputFormat.STD;

        @Override
        public Optional<String> validate() {
            final String rc;
            if (currentIpsSvcNode != null && currentClusterName == null) {
                rc = "when using " + ARG_IPS_SERVICE + ", " + ARG_CLUSTER_NAME + " must be given, too";
            } else {
                rc = null;
            }
            return Optional.ofNullable(rc);
        }
    }

    @Getter
    @RequiredArgsConstructor(staticName = "of")
    public static class WriteCfgEntriesCmdletFactory implements CmdletFactory<ClusterGroupCfg, WriteCfgEntriesCmdlet, WriteCfgEntiresCfg> {
        public static final String CMD_NAME = "writeCfgEntries";
        @SuppressFBWarnings("SS_SHOULD_BE_STATIC")
        private final String optionName = CMD_NAME;

        @Override
        public WriteCfgEntiresCfg getJCommanderParamObject() {
            return WriteCfgEntiresCfg.of();
        }

        @Override
        public WriteCfgEntriesCmdlet createCmd(ClusterGroupCfg clusterGroupCfg, WriteCfgEntiresCfg params, CmdletContext ctx) {
            return new WriteCfgEntriesCmdlet(params, clusterGroupCfg, ctx);
        }
    }

    private final WriteCfgEntiresCfg cmdletCfg;
    private final ClusterGroupCfg clusterGroupCfg;
    private final CmdletContext ctx;

    @Override
    @SneakyThrows
    @SuppressFBWarnings("PATH_TRAVERSAL_OUT")
    public void run() {
        final Properties properties1 = toProperties();
        final Properties properties;
        switch (cmdletCfg.getOutputFormat()) {
        case SHELL:
            properties = toShellFormat(properties1);
            break;
        case STD:
            properties = properties1;
            break;
        default:
            throw new UnsupportedOperationException();
        }
        if (cmdletCfg.getOutputFileName() != null) {
            try (OutputStream ow = new FileOutputStream(cmdletCfg.getOutputFileName())) {
                try (PrintStream ps = new PrintStream(ow, true, Charsets.UTF_8.name())) {
                    consume(properties, (key, value) -> ps.println(key + "=" + value));
                }
            }
        } else {
            consume(properties, (key, value) -> ctx.println(key + "=" + value));
        }
    }

    void consume(Properties properties, BiConsumer<String, String> fn) {
        for (final Map.Entry<Object, Object> entry : properties.entrySet()) {
            final String key = "" + entry.getKey();
            final String value = "" + entry.getValue();
            fn.accept(key, value);
        }
    }

    /**
     * Avoid "." and "-" in property-keys (replace them with "_").
     */
    private Properties toShellFormat(Properties other) {
        final Properties rc = new Properties();
        for (final Object key1 : other.keySet()) {
            if (key1 instanceof String) {
                final String key = (String) key1;
                final String key2 = keyToShellFormat(key);
                rc.setProperty(key2, other.getProperty(key));
            }
        }
        return rc;
    }

    private String keyToShellFormat(String key) {
        String key2 = key;
        key2 = key2.replace('-', '_');
        key2 = key2.replace('.', '_');
        key2 = key2.toUpperCase();
        return key2;
    }

    private Properties toProperties() {
        final IpsPropertyProviderBuilder ppB = IpsPropertyProvider.builder().add(clusterGroupCfg.getPropertyProvider());
        if (cmdletCfg.getCurrentClusterName() != null) {
            final ClusterCfg clusterCfg = clusterGroupCfg.getCluster(cmdletCfg.getCurrentClusterName());
            Preconditions.checkNotNull(clusterCfg, "could not find config for cluster " + cmdletCfg.getCurrentClusterName());
            final IpsPropertyProviderBuilder ppB2 = ppB.push(ClusterGroupCfg.PROPKEY_CLUSTER).push(PROPKEY_CURRENT)
                    .add(clusterCfg.getPropertyProvider());
            ppB2.addProperty(PROPKEY_NAME, cmdletCfg.getCurrentClusterName());
            if (cmdletCfg.getCurrentIpsSvcNode() != null) {
                final IpsSvcNodeCfg ipsSvcNodeCfg = clusterCfg.getIpsSvcNode(cmdletCfg.getCurrentIpsSvcNode());
                Preconditions.checkNotNull(ipsSvcNodeCfg, "could not find config for IPS-svc-node " + cmdletCfg.getCurrentIpsSvcNode());
                ppB2.push(ClusterCfg.PROPKEY_IPS_SERVICES).push(IpsSvcNodesCfg.PROPKEY_INSTANCE).push(PROPKEY_CURRENT)
                        .add(ipsSvcNodeCfg.getPropertyProvider()).addProperty(PROPKEY_NAME, cmdletCfg.getCurrentIpsSvcNode());
            }
        }
        return ppB.build().getProperties();
    }
}
