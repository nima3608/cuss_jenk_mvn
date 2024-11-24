package com.materna.ips.connect.install.lib.file_patching;

import java.io.StringReader;
import java.io.StringWriter;

import org.assertj.core.api.Assertions;
import org.testng.annotations.Test;

import com.materna.buc.commons.file.patching.FilePatcher;

import lombok.SneakyThrows;

/**
 * @author mbrinkma
 * @since 19.10.2023
 *
 */
public class ONCE_4359_Advance_installation_in_order_to_support_OS_GUI_Tests {
    // see .connect-svc-install-base.sh, function
    // prepareFrontendControllerPatchFile():
    private static final String PATCH_DESCRIPTION = """
            diff -  matchOptions=regex
            .1,3c3
            <     mainTemplateTenant:
            <       LH: .*
            <       LX: .*
            ---
            >     mainTemplateTenant:
            >       LH: "$UI_PATH_PREFIX/$UI_VERSION/lh/index.html"
            >       LX: "$UI_PATH_PREFIX/$UI_VERSION/lx/index.html"
            .1,4c4
            <     mainTemplateTenant:
            <       LH: .*
            <       LX: .*
            <       OS: .*
            ---
            >     mainTemplateTenant:
            >       LH: "$UI_PATH_PREFIX/$UI_VERSION/lh/index.html"
            >       LX: "$UI_PATH_PREFIX/$UI_VERSION/lx/index.html"
            >       OS: "$UI_PATH_PREFIX/$UI_VERSION/os/index.html"
            """;

    @Test
    public void test_only_LH_and_LX() {
        final String input = """
                mainTemplateTenant:
                  LH: "/home/fargate/ui/static/7852/lh/index.html"
                  LX: "/home/fargate/ui/static/7852/lx/index.html"
                """;
        // @formatter:off
        final String expectedOutput =
               """
                   mainTemplateTenant:
                     LH: "$UI_PATH_PREFIX/$UI_VERSION/lh/index.html"
                     LX: "$UI_PATH_PREFIX/$UI_VERSION/lx/index.html"
               """;
        // @formatter:on
        assertPatchWorked(input, expectedOutput);
    }

    @Test
    public void test_LH_LX_and_OS() {
        final String input = """
                mainTemplateTenant:
                  LH: "/home/fargate/ui/static/7852/lh/index.html"
                  LX: "/home/fargate/ui/static/7852/lx/index.html"
                  OS: "/home/fargate/ui/static/7852/os/index.html"
                """;
        // @formatter:off
        final String expectedOutput =
            """
                mainTemplateTenant:
                  LH: "$UI_PATH_PREFIX/$UI_VERSION/lh/index.html"
                  LX: "$UI_PATH_PREFIX/$UI_VERSION/lx/index.html"
                  OS: "$UI_PATH_PREFIX/$UI_VERSION/os/index.html"
            """;
        // @formatter:on
        assertPatchWorked(input, expectedOutput);
    }

    @SneakyThrows
    private void assertPatchWorked(final String input, final String expectedOutput) {
        final StringWriter outputWriter = new StringWriter();
        final FilePatcher patcher = new FilePatcher(new StringReader(PATCH_DESCRIPTION));
        patcher.doPatches(new StringReader(input), outputWriter);
        final String output = outputWriter.toString();
        Assertions.assertThat(output).isEqualTo(expectedOutput);
    }
}
