/**
 * (c) Materna GmbH 2022
 */

package com.materna.ips.connect.install.lib.cfg;

import static org.assertj.core.api.Assertions.assertThat;

import org.testng.annotations.Test;

import com.materna.ips.connect.system.cfg.types.ClusterGroupCfg;

/**
 * @author mbrinkma
 * @since 22.07.2022
 *
 */
public class SystemsCfgTest {
    public static final String PROPERTIES_PATH = "src/test/resources/config/";

    @Test
    public void testReadCfgFromYaml() {
        for (final String fileName : new String[] { "ci-system/ci-system.yaml", "autotest-system/autotest-docker-system.yaml" }) {
            System.out.println(fileName);
            final ClusterGroupCfg clusterGroupCfg = ClusterGroupCfg.builder().configFilePath(PROPERTIES_PATH + fileName).build();
            assertThat(clusterGroupCfg).isNotNull();
        }
    }
}
