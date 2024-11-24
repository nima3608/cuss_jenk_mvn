/**
 * (c) Materna GmbH 2022
 */

package com.materna.ips.connect.install.lib.file_patching;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.StringReader;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.io.Files;
import com.materna.buc.commons.file.patching.FilePatcher;
import com.materna.buc.ma.util.FileUtils;

import lombok.SneakyThrows;

/**
 * @author mbrinkma
 * @since 10.11.2022
 *
 */
public class FrontendControllerConfigPatchTest {

    @Test
    @SneakyThrows
    public void testPatchConfigYaml() {
        // see .connect-svc-install-base.sh, function
        // prepareFrontendControllerPatchFile():
        final String patchDescription = """
                diff $YAML_FILE  matchOptions=regex
                .1,1c1
                <     mainTemplate: .*
                ---
                >     mainTemplate: \"$UI_PATH_PREFIX/$UI_VERSION/index.html\"
                .1,1c1
                <     uiBase: .*
                ---
                >     uiBase: \"/$UI_VERSION/\"
                .1,1c1
                <         - /[0-9]+/\\*\\*$
                ---
                >         - /$UI_VERSION/**
                """;
        final String fecFileName = "frontend-controller.yaml";
        final String patchedFilePathAsString = "target/" + fecFileName;
        final File patchedFileAsFile = new File(patchedFilePathAsString);
        Files.copy(new File("src/test/resources/file_patching/lh-once-int/" + fecFileName), patchedFileAsFile);
        final String oldUiVersion = "4061";
        final String newUiVersion = "4063";
        // @formatter:off
        final String patchDescription2 = replaceStrings(
                patchDescription,
                ImmutableMap.<String,String>builder()
                    .put("$YAML_FILE", patchedFilePathAsString)
                    .put("$UI_VERSION", newUiVersion)
                    .put("$UI_PATH_PREFIX", "uiPath")
                    .build()
                );
        // @formatter:on
        final FilePatcher patcher = new FilePatcher(new StringReader(patchDescription2));
        patcher.doPatches();
        final String patchedFileAsString = FileUtils.readFileAsString(patchedFileAsFile);
        assertThat(patchedFileAsString).contains(newUiVersion).doesNotContain(oldUiVersion);
        assertThat(patchedFileAsString).contains("- /" + newUiVersion + "/**");
        assertThat(StringUtils.countMatches(patchedFileAsString, newUiVersion)).isEqualTo(3);
    }

    private String replaceStrings(String sourceString, Map<String, String> replacements) {
        String rc = sourceString;
        for (final Map.Entry<String, String> entry : replacements.entrySet()) {
            rc = replaceAllOccurrences(rc, entry.getKey(), entry.getValue());
        }
        return rc;
    }

    public String replaceAllOccurrences(String theBigString, CharSequence toBeReplaced, CharSequence replacement) {
        return Pattern.compile(toBeReplaced.toString(), Pattern.LITERAL).matcher(theBigString)
                .replaceAll(Matcher.quoteReplacement(replacement.toString()));
    }
}
