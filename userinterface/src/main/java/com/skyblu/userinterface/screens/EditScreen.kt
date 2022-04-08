package com.skyblu.userinterface.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.skyblu.configuration.*
import com.skyblu.userinterface.R
import com.skyblu.userinterface.componants.ActionConcept
import com.skyblu.userinterface.componants.input.ActionConceptList
import com.skyblu.userinterface.componants.input.AppTextField
import com.skyblu.userinterface.componants.input.StyledNumberPicker
import com.skyblu.userinterface.componants.scaffold.AppTopAppBar
import com.skyblu.userinterface.viewmodels.EditViewModel

@Composable
fun EditScreen(
    viewModel: EditViewModel = hiltViewModel(),
    navController: NavController
) {
    val state = viewModel.state
    val p = state.aircraft
    val focusManager = LocalFocusManager.current

    Scaffold(
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(SMALL_PADDING),
                verticalArrangement = Arrangement.spacedBy(SMALL_PADDING),

                ) {
                AppTextField(
                    value = state.title.value,
                    onValueChanged = { state.title.value = it },
                    imeAction = ImeAction.Next,
                    onIme = { focusManager.moveFocus(FocusDirection.Down) },
                    placeholder = TITLE_STRING,
                    leadingIcon = R.drawable.number
                )
                AppTextField(
                    value = state.dropzone.value,
                    onValueChanged = { state.dropzone.value = it },
                    imeAction = ImeAction.Next,
                    onIme = { focusManager.moveFocus(FocusDirection.Down) },
                    placeholder = DROPZONE_STRING,
                    leadingIcon = R.drawable.location
                )
                AppTextField(
                    value = state.aircraft.value,
                    onValueChanged = { state.aircraft.value = it },
                    imeAction = ImeAction.Next,
                    onIme = { focusManager.moveFocus(FocusDirection.Down) },
                    placeholder = AIRCRAFT_STRING,
                    leadingIcon = R.drawable.aircraft
                )
                AppTextField(
                    value = state.equipment.value,
                    onValueChanged = { state.equipment.value = it },
                    imeAction = ImeAction.Next,
                    onIme = { focusManager.moveFocus(FocusDirection.Down) },
                    placeholder = EQUIPMENT_STRING,
                    leadingIcon = R.drawable.parachute
                )
                AppTextField(
                    value = state.description.value,
                    onValueChanged = { state.description.value = it },
                    imeAction = ImeAction.Next,
                    onIme = { focusManager.moveFocus(FocusDirection.Down) },
                    placeholder = DESCRIPTION_STRING,
                    leadingIcon = R.drawable.edit
                )
                StyledNumberPicker(
                    value = state.jumpNumber.value,
                    onValueChanged = { state.jumpNumber.value = it },
                    range = 1 .. 50000
                )
            }
        },
        topBar = {
            AppTopAppBar(
                title = EDIT_JUMP_STRING,
                navigationIcon = {
                    ActionConceptList(
                        menuActions = listOf(
                            ActionConcept(
                                action = {
                                    navController.popBackStack()
                                },
                                concept = Concept.Previous
                            )
                        )
                    )
                },
                actionIcons = {
                    ActionConceptList(
                        menuActions = listOf(
                            ActionConcept(
                                action = { viewModel.updateSkydive() },
                                concept = Concept.Save
                            ),
                        )
                    )
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    viewModel.writeServer.deleteJump(
                        state.jumpID,
                        viewModel.applicationContext
                    )
                },
                content = {
                    Icon(
                        painter = painterResource(id = R.drawable.delete),
                        contentDescription = Concept.Delete.title
                    )
                },
                backgroundColor = MaterialTheme.colors.error
            )
        }
    )
}