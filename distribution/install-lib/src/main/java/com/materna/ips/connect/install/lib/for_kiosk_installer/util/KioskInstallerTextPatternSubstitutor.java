package com.materna.ips.connect.install.lib.for_kiosk_installer.util;

import java.util.Properties;

import com.materna.buc.commons.text.substitution.ITextPatternSubstitutor;
import com.materna.buc.commons.text.substitution.JexlTextPatternSubstitutor;
import com.materna.buc.commons.text.substitution.TextPatternSubstitutorBase2;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

/**
 * @author mbrinkma
 * @since 15.02.2024
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class KioskInstallerTextPatternSubstitutor {

    @SneakyThrows
    public static TextPatternSubstitutorBase2 of(Properties config) {
        final TextPatternSubstitutorBase2 rc = new JexlTextPatternSubstitutor(config, ITextPatternSubstitutor.STD_LEADIN, ITextPatternSubstitutor.STD_LEADOUT);
        return rc;
    }
}
