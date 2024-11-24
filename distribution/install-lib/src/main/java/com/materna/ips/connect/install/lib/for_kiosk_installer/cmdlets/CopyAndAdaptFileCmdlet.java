/**
 * (c) Materna GmbH 2022
 */

package com.materna.ips.connect.install.lib.for_kiosk_installer.cmdlets;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collection;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.google.common.base.Charsets;
import com.materna.buc.commons.file.io.FileCopier;
import com.materna.buc.commons.text.substitution.ReaderSubstitutionInput;
import com.materna.buc.commons.text.substitution.ReplacementContext;
import com.materna.buc.commons.text.substitution.ReplacementIssue;
import com.materna.buc.commons.text.substitution.ReplacementResult;
import com.materna.buc.commons.text.substitution.TextPatternSubstitutorBase2;
import com.materna.ips.connect.install.lib.for_kiosk_installer.app.impl.KioskInstallerCfg;
import com.materna.ips.connect.install.lib.for_kiosk_installer.util.KioskInstallerTextPatternSubstitutor;
import com.materna.phi.install.lib.app.cmdlets.types.Cmdlet;
import com.materna.phi.install.lib.app.impl.IpsAdminAppBase.CfgWithHelp;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * @author mbrinkma
 * @since 15.02.2024
 *
 */
@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class CopyAndAdaptFileCmdlet implements Cmdlet {

    @Getter
    @NoArgsConstructor(staticName = "of")
    @Parameters(commandDescription = "Copy a file while adapting contained placeholdes.")
    public static class CopyAndAdaptFileCfg implements CfgWithHelp {
        public static final String ARG_SRC_FILE = "-sourceFile";
        public static final String ARG_DST_FILE = "-destFile";
        @Parameter(names = { "-h", "--help" }, description = "Print help.", help = true)
        private boolean printHelp;
        @Parameter(names = ARG_SRC_FILE, description = "The source file.", required = true)
        private String sourceFileName;
        @Parameter(names = ARG_DST_FILE, description = "The destination file.", required = false)
        private String destinationFileName;
        @Parameter(names = {
                "--dontFailOnMissingSubstitutions" }, description = "Do not fail when not all placeholders could be resolved.", help = true)
        private boolean dontFailOnMissingSubstitutions;
    }

    @Getter
    @RequiredArgsConstructor(staticName = "of")
    public static class CopyAndAdaptFileCmdletFactory
            implements CmdletFactory<KioskInstallerCfg, CopyAndAdaptFileCmdlet, CopyAndAdaptFileCfg> {
        public static final String CMD_NAME = "copyAndAdaptFile";
        @SuppressFBWarnings("SS_SHOULD_BE_STATIC")
        private final String optionName = CMD_NAME;

        @Override
        public CopyAndAdaptFileCfg getJCommanderParamObject() {
            return CopyAndAdaptFileCfg.of();
        }

        @Override
        public CopyAndAdaptFileCmdlet createCmd(KioskInstallerCfg appConfig, CopyAndAdaptFileCfg params, CmdletContext ctx) {
            return new CopyAndAdaptFileCmdlet(appConfig, params, ctx);
        }
    }

    private final KioskInstallerCfg appConfig;
    private final CopyAndAdaptFileCfg cmdletCfg;
    private final CmdletContext ctx;

    @Override
    @SneakyThrows
    @SuppressFBWarnings("PATH_TRAVERSAL_OUT")
    public void run() {
        final TextPatternSubstitutorBase2 substitutor = KioskInstallerTextPatternSubstitutor.of(appConfig.getProperties());
        final String destFileName = cmdletCfg.getDestinationFileName();
        if (destFileName != null) {
            final FileCopier fileCopier = new FileCopier(substitutor);
            final File destFile = new File(destFileName);
            final File destDir = destFile.getParentFile();
            if (destDir != null && !destDir.exists()) {
                destDir.mkdirs();
            }
            fileCopier.copy(cmdletCfg.getSourceFileName(), destFileName);
        } else {
            try (Reader fr = new InputStreamReader(new FileInputStream(cmdletCfg.getSourceFileName()), Charsets.UTF_8)) {
                final ReplacementContext rctx = ReplacementContext.none();
                final ReaderSubstitutionInput input2 = new ReaderSubstitutionInput(fr);
                final ReplacementResult output = (ReplacementResult) substitutor.replace(rctx, input2);
                if (output.isIssuesFound()) {
                    if (cmdletCfg.isDontFailOnMissingSubstitutions()) {
                        log.warn("found the following issues, ignoring them, but you should check:\n" + toString(output.getIssues()));
                        printIssues(output.getIssues());
                    } else {
                        ctx.exitWithError("found the following issues, exiting:\n" + toString(output.getIssues()));
                    }
                }
                final String output2 = output.getString();
                ctx.println(output2);
            }
        }
    }

    private void printIssues(Collection<ReplacementIssue> issues) {
        if (issues.size() > 0) {
            ctx.println("Detected the following issues, continuing, but please check whether actions are needed:");
            for (final ReplacementIssue issue : issues) {
                ctx.println(issue.getExplanation());
            }
        }
    }

    private String toString(Collection<ReplacementIssue> issues) {
        final StringBuilder rc = new StringBuilder();
        for (final ReplacementIssue issue : issues) {
            rc.append(issue.getExplanation()).append("\n");
        }
        return rc.toString();
    }
}
