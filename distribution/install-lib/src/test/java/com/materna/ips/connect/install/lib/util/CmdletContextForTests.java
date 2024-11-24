/**
 * (c) Materna GmbH 2022
 */

package com.materna.ips.connect.install.lib.util;

/**
 * @author mbrinkma
 * @since 01.08.2022
 *
 */
public class CmdletContextForTests extends com.materna.phi.install.lib.app.for_testing.CmdletTestUtil.CmdletContextForTests
        implements AutoCloseable {

    public CmdletContextForTests(boolean useDB, boolean ignoreExit0Call) {
        super(useDB, ignoreExit0Call);
    }

    @Override
    public void close() {
    }
}
