/**
 * (c) Materna GmbH 2023
 */

package com.materna.ips.connect.install.lib.for_control_node.app;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.materna.ips.connect.install.lib.for_control_node.cmdlets.PrintTimestampCmdlet.PrintTimestampCfg;
import com.materna.ips.connect.install.lib.for_control_node.cmdlets.PrintTimestampCmdlet.PrintTimestampCmdletFactory;
import com.materna.ips.connect.install.lib.util.CmdletContextForTests;
import com.materna.ips.connect.install.lib.util.UtilForTesting;

/**
 * @author mbrinkma
 * @since 14.03.2023
 *
 */
public class PrintTimestampCmdletTest {
    private static final String OUTPUT_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final DateTimeFormatter DT_FORMATTER = DateTimeFormatter.ofPattern(OUTPUT_FORMAT);

    @Test
    public void testPrintTimestamp() {
        String currentTime;
        String output1;
        try (CmdletContextForTests ctx = UtilForTesting.createContext()) {
            callCmd(ctx, PrintTimestampCfg.ARG_CURRENT_TIME, PrintTimestampCfg.ARG_TIME_OFFSET_SEC, "-" + (15 * 60));
            currentTime = ctx.getMessages().get(0);
            // assertThat(ctx.getMessages()).contains("cluster.current.ips-services.instance.current.host=10.23.206.106");
            // assertThat(ctx.getMessages().get(0)).isEqualTo(expected);
        }
        try (CmdletContextForTests ctx = UtilForTesting.createContext()) {
            callCmd(ctx, PrintTimestampCfg.ARG_TIME, currentTime, PrintTimestampCfg.ARG_ROUND_TO, "15m",
                    PrintTimestampCfg.ARG_OUTPUT_FORMAT, OUTPUT_FORMAT);
            output1 = ctx.getMessages().get(0);
            System.out.println(output1);
        }
        try (CmdletContextForTests ctx = UtilForTesting.createContext()) {
            callCmd(ctx, PrintTimestampCfg.ARG_TIME, currentTime, PrintTimestampCfg.ARG_ROUND_TO, "15m", PrintTimestampCfg.ARG_TIMESPAN_END,
                    PrintTimestampCfg.ARG_OUTPUT_FORMAT, OUTPUT_FORMAT);
            final String output2 = ctx.getMessages().get(0);
            System.out.println(output2);
            final Temporal time1 = DT_FORMATTER.parse(output1, LocalDateTime::from);
            final Temporal time2 = DT_FORMATTER.parse(output2, LocalDateTime::from);
            final long secondsBetween = ChronoUnit.SECONDS.between(time1, time2);
            Assert.assertEquals(secondsBetween, 15 * 60 - 1);
        }
    }

    private void callCmd(CmdletContextForTests ctx, String... args) {
        UtilForTesting.callCmd(ctx, PrintTimestampCmdletFactory.CMD_NAME, args);
    }
}
