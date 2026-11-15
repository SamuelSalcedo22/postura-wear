package com.posturawear.phone

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.posturawear.core.R
import com.posturawear.core.domain.model.SimScenario
import com.posturawear.core.state.BackendStatus
import com.posturawear.core.state.PosturaEngine
import com.posturawear.core.tokens.component.Tone
import com.posturawear.core.tokens.semantic.SemanticSpacing
import com.posturawear.core.ui.atoms.ButtonVariant
import com.posturawear.core.ui.atoms.PosturaButton
import com.posturawear.core.ui.atoms.SectionLabel
import com.posturawear.core.ui.molecules.Badge
import com.posturawear.core.ui.molecules.ChoiceGroup

/**
 * Controles de prueba, hechos con los mismos átomos y moléculas que el reloj.
 * Cambian la misma configuración que la pantalla de Ajustes, así que ambos
 * lados se mantienen sincronizados.
 */
@Composable
fun TestPanel(engine: PosturaEngine, modifier: Modifier = Modifier) {
    val data by engine.data.collectAsStateWithLifecycle()
    val backend by engine.backend.collectAsStateWithLifecycle()
    val settings = data?.settings ?: return

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(SemanticSpacing.stack),
    ) {
        SectionLabel(stringResource(R.string.phone_section_source))
        ChoiceGroup(
            options = listOf(
                false to stringResource(R.string.phone_source_device),
                true to stringResource(R.string.phone_source_simulator),
            ),
            selected = settings.simulatorEnabled,
            onSelect = { simulated -> engine.updateSettings { it.copy(simulatorEnabled = simulated) } },
        )

        if (settings.simulatorEnabled) {
            SectionLabel(stringResource(R.string.phone_section_scenario))
            ChoiceGroup(
                options = listOf(
                    SimScenario.GOOD to stringResource(R.string.settings_scenario_good),
                    SimScenario.BAD to stringResource(R.string.settings_scenario_bad),
                    SimScenario.MIXED to stringResource(R.string.settings_scenario_mixed),
                ),
                selected = settings.scenario,
                onSelect = { scenario -> engine.updateSettings { it.copy(scenario = scenario) } },
                stacked = true,
            )
        }

        SectionLabel(stringResource(R.string.phone_section_backend))
        val (statusText, statusTone) = when (backend) {
            BackendStatus.UNKNOWN, BackendStatus.CHECKING -> R.string.phone_backend_unknown to Tone.Neutral
            BackendStatus.OK -> R.string.phone_backend_ok to Tone.Positive
            BackendStatus.DOWN -> R.string.phone_backend_down to Tone.Attention
        }
        Badge(stringResource(statusText), statusTone)
        PosturaButton(
            label = stringResource(R.string.phone_backend_check),
            onClick = engine::checkBackend,
            variant = ButtonVariant.Secondary,
            enabled = backend != BackendStatus.CHECKING,
        )
        PosturaButton(
            label = stringResource(R.string.phone_test_vibration),
            onClick = engine::testVibration,
            variant = ButtonVariant.Secondary,
        )
    }
}
