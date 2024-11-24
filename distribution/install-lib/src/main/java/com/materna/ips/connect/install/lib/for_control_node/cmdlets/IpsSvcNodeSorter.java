package com.materna.ips.connect.install.lib.for_control_node.cmdlets;

import java.util.Comparator;

import com.materna.ips.connect.system.cfg.types.IpsSvcNodeCfg;
import com.materna.ips.connect.system.cfg.types.IpsSvcNodeType;

/**
 * @author mbrinkma
 * @since 20.07.2023
 *
 */
public class IpsSvcNodeSorter {
    public static final String NAME_ADMIN_NODES_FIRST = "admin-nodes-first";
    public static final String NAME_ALL = NAME_ADMIN_NODES_FIRST;

    public static Comparator<IpsSvcNodeCfg> parse(String name) {
        Comparator<IpsSvcNodeCfg> rc;
        switch (name) {
        case NAME_ADMIN_NODES_FIRST:
            rc = Comparator.comparing(IpsSvcNodeCfg::getIpsSvcType, IpsSvcNodeType.COMP_ADMIN_NODES_SMALLER);
            break;
        default:
            throw new UnsupportedOperationException(name);
        }
        return rc;
    }
}
