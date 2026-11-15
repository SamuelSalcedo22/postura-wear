package com.posturawear.core.ui.pages

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.posturawear.core.R
import com.posturawear.core.domain.model.AppLanguage
import com.posturawear.core.domain.model.Sensitivity
import com.posturawear.core.domain.model.Settings
import com.posturawear.core.domain.model.SimScenario
import com.posturawear.core.tokens.semantic.SemanticSpacing
import com.posturawear.core.tokens.theme.PosturaTheme
import com.posturawear.core.ui.atoms.BodyText
import com.posturawear.core.ui.atoms.ButtonVariant
import com.posturawear.core.ui.atoms.PosturaButton
import com.posturawear.core.ui.atoms.SectionLabel
import com.posturawear.core.ui.molecules.ChoiceGroup
import com.posturawear.core.ui.molecules.ToggleRow
import com.posturawear.core.ui.templates.ListTemplate

private enum class ClearStep { Idle, Confirm, Done }

@Composable
private fun FieldLabel(text: String) {
    BodyText(
        text,
        Modifier.fillMaxWidth().padding(top = SemanticSpacing.stack, start = SemanticSpacing.stack),
        textAlign = TextAlign.Start,
    )
}

/**
 * Página: ajustes. Agrupados por lo que el usuario quiere lograr (avisos,
 * patrones, análisis, datos), no por cómo está hecha la app.
 * Cada cambio se guarda al instante; no hay botón de "guardar".
 */
@Composable
fun SettingsPage(
    settings: Settings,
    versionName: String,
    onChange: ((Settings) -> Settings) -> Unit,
    onClearHistory: () -> Unit,
) {
    var clearStep by remember { mutableStateOf(ClearStep.Idle) }

    ListTemplate {
        // --- Avisos ---
        item { SectionLabel(stringResource(R.string.settings_section_alerts)) }
        item {
            ToggleRow(
                label = stringResource(R.string.settings_vibration),
                secondary = stringResource(R.string.settings_vibration_hint),
                checked = settings.vibrationEnabled,
                onCheckedChange = { on -> onChange { it.copy(vibrationEnabled = on) } },
            )
        }
        item { FieldLabel(stringResource(R.string.settings_sensitivity)) }
        item {
            ChoiceGroup(
                options = listOf(
                    Sensitivity.LOW to stringResource(R.string.settings_sensitivity_low),
                    Sensitivity.MEDIUM to stringResource(R.string.settings_sensitivity_medium),
                    Sensitivity.HIGH to stringResource(R.string.settings_sensitivity_high),
                ),
                selected = settings.sensitivity,
                onSelect = { value -> onChange { it.copy(sensitivity = value) } },
            )
        }
        item { FieldLabel(stringResource(R.string.settings_alert_delay)) }
        item {
            // Dos filas de dos: cuatro opciones en una fila no caben en pantallas pequeñas.
            val options = Settings.AlertDelayOptions.map { it to stringResource(R.string.settings_alert_delay_value, it) }
            ChoiceGroup(
                options = options.take(2),
                selected = settings.alertDelaySeconds,
                onSelect = { value -> onChange { it.copy(alertDelaySeconds = value) } },
            )
        }
        item {
            val options = Settings.AlertDelayOptions.map { it to stringResource(R.string.settings_alert_delay_value, it) }
            ChoiceGroup(
                options = options.drop(2),
                selected = settings.alertDelaySeconds,
                onSelect = { value -> onChange { it.copy(alertDelaySeconds = value) } },
            )
        }

        // --- Patrones que avisan ---
        item { SectionLabel(stringResource(R.string.settings_section_patterns)) }
        item {
            ToggleRow(
                label = stringResource(R.string.settings_pattern_forward),
                checked = settings.alertOnForward,
                onCheckedChange = { on -> onChange { it.copy(alertOnForward = on) } },
            )
        }
        item {
            ToggleRow(
                label = stringResource(R.string.settings_pattern_side),
                checked = settings.alertOnSide,
                onCheckedChange = { on -> onChange { it.copy(alertOnSide = on) } },
            )
        }
        item {
            ToggleRow(
                label = stringResource(R.string.settings_pattern_unusual),
                checked = settings.alertOnUnusual,
                onCheckedChange = { on -> onChange { it.copy(alertOnUnusual = on) } },
            )
        }

        // --- Análisis ---
        item { SectionLabel(stringResource(R.string.settings_section_analysis)) }
        item {
            ToggleRow(
                label = stringResource(R.string.settings_cloud),
                secondary = stringResource(R.string.settings_cloud_hint),
                checked = settings.cloudEnabled,
                onCheckedChange = { on -> onChange { it.copy(cloudEnabled = on) } },
            )
        }
        item {
            ToggleRow(
                label = stringResource(R.string.settings_simulator),
                secondary = stringResource(R.string.settings_simulator_hint),
                checked = settings.simulatorEnabled,
                onCheckedChange = { on -> onChange { it.copy(simulatorEnabled = on) } },
            )
        }
        if (settings.simulatorEnabled) {
            item { FieldLabel(stringResource(R.string.settings_scenario)) }
            item {
                ChoiceGroup(
                    options = listOf(
                        SimScenario.GOOD to stringResource(R.string.settings_scenario_good),
                        SimScenario.BAD to stringResource(R.string.settings_scenario_bad),
                        SimScenario.MIXED to stringResource(R.string.settings_scenario_mixed),
                    ),
                    selected = settings.scenario,
                    onSelect = { value -> onChange { it.copy(scenario = value) } },
                    stacked = true,
                )
            }
        }

        // --- Idioma ---
        item { SectionLabel(stringResource(R.string.settings_section_language)) }
        item {
            ChoiceGroup(
                options = listOf(
                    AppLanguage.SYSTEM to stringResource(R.string.settings_language_system),
                    AppLanguage.ES to stringResource(R.string.language_es),
                    AppLanguage.EN to stringResource(R.string.language_en),
                ),
                selected = settings.language,
                onSelect = { value -> onChange { it.copy(language = value) } },
                stacked = true,
            )
        }

        // --- Datos ---
        item { SectionLabel(stringResource(R.string.settings_section_data)) }
        item {
            PosturaButton(
                label = stringResource(
                    when (clearStep) {
                        ClearStep.Idle -> R.string.settings_clear_history
                        ClearStep.Confirm -> R.string.settings_clear_history_confirm
                        ClearStep.Done -> R.string.settings_clear_history_done
                    },
                ),
                onClick = {
                    clearStep = when (clearStep) {
                        ClearStep.Idle -> ClearStep.Confirm
                        else -> {
                            onClearHistory()
                            ClearStep.Done
                        }
                    }
                },
                variant = if (clearStep == ClearStep.Confirm) ButtonVariant.Danger else ButtonVariant.Secondary,
                enabled = clearStep != ClearStep.Done,
            )
        }

        // --- Acerca de ---
        item { SectionLabel(stringResource(R.string.settings_section_about)) }
        item { BodyText(stringResource(R.string.disclaimer_long)) }
        item {
            BodyText(
                stringResource(R.string.settings_version, versionName),
                color = PosturaTheme.colors.contentMuted,
            )
        }
    }
}
