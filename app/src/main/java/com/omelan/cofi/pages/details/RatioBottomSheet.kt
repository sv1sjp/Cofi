@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)

package com.omelan.cofi.pages.details

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.omelan.cofi.R
import com.omelan.cofi.components.Material3BottomSheet
import com.omelan.cofi.components.OutlinedNumbersField
import com.omelan.cofi.share.components.slideLeftRight
import com.omelan.cofi.share.model.Step
import com.omelan.cofi.share.model.StepType
import com.omelan.cofi.share.utils.roundToDecimals
import com.omelan.cofi.share.utils.toStringShort
import com.omelan.cofi.ui.Spacing
import kotlin.math.roundToInt

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RatioBottomSheet(
    timeMultiplier: Float,
    setTimeMultiplier: (Float) -> Unit,
    weightMultiplier: Float,
    setWeightMultiplier: (Float) -> Unit,
    onDismissRequest: () -> Unit,
    allSteps: List<Step>,
) {
    Material3BottomSheet(onDismissRequest = onDismissRequest) {
        Column(
            modifier = Modifier
                .waterfallPadding()
                .navigationBarsPadding()
                .padding(horizontal = Spacing.big)
                .padding(top = Spacing.big),
        ) {
            ManualContent(
                timeMultiplier,
                setTimeMultiplier,
                weightMultiplier,
                setWeightMultiplier,
                allSteps,
            )
        }
    }
}

val predefinedMultipliers = arrayOf(0.5f, 1f, 2f, 3f)

@Composable
private fun ColumnScope.ManualContent(
    timeMultiplier: Float,
    setTimeMultiplier: (Float) -> Unit,
    weightMultiplier: Float,
    setWeightMultiplier: (Float) -> Unit,
    allSteps: List<Step>,
) {
    val focusRequester = remember { FocusRequester() }
    val combinedWaterWeight by remember(allSteps) {
        derivedStateOf {
            allSteps.sumOf {
                if (it.type == StepType.WATER) {
                    it.value?.toDouble() ?: 0.0
                } else {
                    0.0
                }
            }.toFloat()
        }
    }
    val combinedCoffeeWeight by remember(allSteps) {
        derivedStateOf {
            allSteps.sumOf {
                if (it.type == StepType.ADD_COFFEE) {
                    it.value?.toDouble() ?: 0.0
                } else {
                    0.0
                }
            }.toFloat()
        }
    }

    LaunchedEffect(true) {
        focusRequester.requestFocus()
    }
    Title(stringResource(id = R.string.recipe_details_multiply_weight))
    Spacer(modifier = Modifier.height(Spacing.normal))
    Subtitle(
        text = stringResource(
            id = R.string.recipe_details_recipeRatio,
            (combinedWaterWeight / combinedCoffeeWeight).toStringShort(),
        ),
    )

    Row(
        modifier = Modifier.padding(top = Spacing.normal),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.normal),
    ) {
        OutlinedNumbersField(
            modifier = Modifier
                .weight(1f, true)
                .focusRequester(focusRequester),
            value = (combinedCoffeeWeight * weightMultiplier).toString(),
            onValueChange = {
                val newWeightMultiplier =
                    ((it.toFloatOrNull() ?: combinedCoffeeWeight) / combinedCoffeeWeight)
                setWeightMultiplier(if (newWeightMultiplier.isNaN()) 0f else newWeightMultiplier)
            },
            suffix = {
                Text(text = "g")
            },
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.recipe_icon_coffee_grinder),
                    contentDescription = null,
                )
            },
        )
        Text(text = ":")
        OutlinedNumbersField(
            modifier = Modifier.weight(1f, true),
            value = (combinedWaterWeight * weightMultiplier).toString(),
            onValueChange = {
                val newWeightMultiplier =
                    ((it.toFloatOrNull() ?: combinedWaterWeight) / combinedWaterWeight)
                setWeightMultiplier(if (newWeightMultiplier.isNaN()) 0f else newWeightMultiplier)
            },
            suffix = {
                Text(text = "g")
            },
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_water),
                    contentDescription = null,
                )
            },
        )
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = Spacing.big),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        predefinedMultipliers.forEach {
            com.omelan.cofi.components.Chip(
                value = "${it.toStringShort()}x",
                onCheck = { checked ->
                    if (checked) {
                        setWeightMultiplier(it)
                    }
                },
                isChecked = weightMultiplier == it,
            )
        }
    }
    Title(stringResource(id = R.string.recipe_details_multiply_time))
    SliderWithValue(timeMultiplier, setTimeMultiplier)
}

@Composable
fun Title(text: String) {
    Text(
        text = text,
        textAlign = TextAlign.Start,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.onSurface,
    )
}

@Composable
fun Subtitle(text: String) {
    Text(
        text = text,
        textAlign = TextAlign.Start,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Light,
        color = MaterialTheme.colorScheme.secondary,
    )
}

const val step = 0.1f
val range = 0f..3f
val steps = (range.endInclusive / step).roundToInt() + 1

@Composable
private fun SliderWithValue(value: Float, setValue: (Float) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Slider(
            valueRange = range,
            steps = steps,
            value = value,
            onValueChange = { setValue(it.roundToDecimals()) },
            modifier = Modifier.weight(1f, true),
        )
        AnimatedContent(
            targetState = value,
            transitionSpec = slideLeftRight { target, initial -> target > initial },
            label = "slider value",
        ) {
            Text(
                color = MaterialTheme.colorScheme.onSurface,
                text = it.toString(),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(horizontal = Spacing.normal),
            )
        }
    }
}
