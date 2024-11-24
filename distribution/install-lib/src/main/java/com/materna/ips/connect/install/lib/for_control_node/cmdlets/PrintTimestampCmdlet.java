/**
 * (c) Materna GmbH 2023
 */

package com.materna.ips.connect.install.lib.for_control_node.cmdlets;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.materna.phi.install.lib.app.cmdlets.types.Cmdlet;
import com.materna.phi.install.lib.app.impl.IpsAdminAppBase.CfgWithHelp;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

/**
 * E.g. in shell-scripts, we want to call e.g. "awslogs" giving the exact start-time
 * for the last 15 minutes-range with the end-time being the end of this range.
 * <p>
 * This class enables such calls.
 *
 * @author mbrinkma
 * @since 14.03.2023
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class PrintTimestampCmdlet<CFG> implements Cmdlet {
    @Getter
    @NoArgsConstructor(staticName = "of")
    @Parameters(commandDescription = "Print timestamps as required.")
    public static class PrintTimestampCfg implements Validatable, CfgWithHelp {
        public static final String ARG_CURRENT_TIME = "-currentTime";
        public static final String ARG_TIME = "-time";
        public static final String ARG_TIME_OFFSET_SEC = "-timeOffsetSeconds";
        public static final String ARG_ROUND_TO = "-roundTo";
        public static final String ARG_TIMESPAN_END = "-endOfTimespan";
        public static final String ARG_OUTPUT_FORMAT = "-outputFormat";

        @Parameter(names = { "-h", "--help" }, description = "Print help.", help = true)
        private boolean printHelp;
        @Parameter(names = ARG_CURRENT_TIME, description = "take the current time", required = false)
        private boolean currentTime;
        @Parameter(names = ARG_TIME, description = "take the given time (ISO 8601)", required = false)
        private String timeAsString;
        @Parameter(names = ARG_TIME_OFFSET_SEC, description = "offset the time taken by given number of seconds", required = false)
        private Long timeOffsetSec;
        @Parameter(names = ARG_ROUND_TO, description = "round time to e.g. 15m (15 minutes)", required = false)
        private String roundTo;
        @Parameter(names = ARG_TIMESPAN_END, description = "print the end of the timespan", required = false)
        private boolean timespanEnd;
        @Parameter(names = ARG_OUTPUT_FORMAT, description = "output-format (default is ISO 8601)", required = false)
        private String outputFormatAsString;
        private ZonedDateTime time;
        private DateTimeFormatter outputFormatter;

        @Override
        public Optional<String> validate() {
            String rc = null;
            if (roundTo != null && !roundTo.equals("15m")) {
                rc = "can only round to 15m";
            }
            if (timeAsString != null && currentTime) {
                rc = "use either " + ARG_CURRENT_TIME + " or " + ARG_TIME + ", but not both";
            } else if (timeAsString != null) {
                time = ZonedDateTime.parse(timeAsString);
            } else if (currentTime) {
                time = ZonedDateTime.now();
            } else {
                rc = "use either " + ARG_CURRENT_TIME + " or " + ARG_TIME;
            }
            if (outputFormatAsString == null) {
                outputFormatter = DateTimeFormatter.ISO_DATE_TIME;
            } else {
                outputFormatter = DateTimeFormatter.ofPattern(outputFormatAsString);
            }
            return Optional.ofNullable(rc);
        }
    }

    @Getter
    @RequiredArgsConstructor(staticName = "of")
    public static class PrintTimestampCmdletFactory<CFG> implements CmdletFactory<CFG, PrintTimestampCmdlet<CFG>, PrintTimestampCfg> {
        public static final String CMD_NAME = "printTimestamp";
        @SuppressFBWarnings("SS_SHOULD_BE_STATIC")
        private final String optionName = CMD_NAME;

        @Override
        public PrintTimestampCfg getJCommanderParamObject() {
            return PrintTimestampCfg.of();
        }

        @Override
        public PrintTimestampCmdlet<CFG> createCmd(CFG baseCfg, PrintTimestampCfg params, CmdletContext ctx) {
            return new PrintTimestampCmdlet<CFG>(params, ctx);
        }
    }

    private final PrintTimestampCfg cfg;
    private final CmdletContext ctx;

    @Override
    public void run() {
        ZonedDateTime time = cfg.getTime();
        if (cfg.getTimeOffsetSec() != null) {
            time = time.plusSeconds(cfg.getTimeOffsetSec());
        }
        if (cfg.getRoundTo() != null) {
            final long minutes = 15;
            time = time.truncatedTo(ChronoUnit.HOURS).plusMinutes(minutes * (time.getMinute() / minutes));
            if (cfg.isTimespanEnd()) {
                time = time.plusMinutes(minutes).minusSeconds(1);
            }
        }

        ctx.println(cfg.getOutputFormatter().format(time));
    }
}
