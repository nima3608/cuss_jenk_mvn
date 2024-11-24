package com.materna.ips.connect.install.lib.for_control_node.util;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

/**
 * @author mbrinkma
 * @since 21.09.2023
 *
 */
public class CfgUtil {
    public static final String ITEM_KEY_SEP = ".";
    private static final Splitter ITEM_KEY_SPLITTER = Splitter.on(ITEM_KEY_SEP);
    private static final Joiner ITEM_KEY_JOINER = Joiner.on(ITEM_KEY_SEP);

    public static String appendItemKeySegments(@Nonnull String prefix, @Nonnull String suffix) {
        return prefix + ITEM_KEY_SEP + suffix;
    }

    public static @Nullable String removeItemKeyTail(@Nonnull String itemKey) {
        final List<String> segments = Lists.newLinkedList(ITEM_KEY_SPLITTER.splitToList(itemKey));
        final String rc;
        if (segments.size() <= 1) {
            rc = null;
        } else {
            segments.remove(segments.size() - 1);
            rc = ITEM_KEY_JOINER.join(segments);
        }
        return rc;
    }
}
