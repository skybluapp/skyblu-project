package com.skyblu.userinterface.screens.settingsScreens

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.skyblu.configuration.Concept
import com.skyblu.configuration.LARGE_PADDING
import com.skyblu.configuration.USERNAME_STRING
import com.skyblu.userinterface.R
import com.skyblu.userinterface.componants.*
import com.skyblu.userinterface.componants.input.ActionConceptList
import com.skyblu.userinterface.componants.input.AppTextField
import com.skyblu.userinterface.componants.photos.AppDisplayPhoto
import com.skyblu.userinterface.componants.scaffold.AppTopAppBar
import com.skyblu.userinterface.viewmodels.AccountSettingsViewModel

@Composable()
fun AccountSettingsScreen(
    navController: NavController,
    viewModel: AccountSettingsViewModel = hiltViewModel(),

) {

    val state = viewModel.state

    val navIcon = Concept.Previous
    fun save() {
        viewModel.save()
    }


    val context = LocalContext.current
    val bitmap =  remember {
        mutableStateOf<Bitmap?>(null)
    }
    val imageBitmap =  remember {
        mutableStateOf<ImageBitmap?>(null)
    }

    val launcher = rememberLauncherForActivityResult(contract =
    ActivityResultContracts.GetContent()) { uri: Uri? ->
        viewModel.setPhotoUri(uri)
    }

    state.photoUri?.let {
        if (Build.VERSION.SDK_INT < 28) {
            bitmap.value = MediaStore.Images
                .Media.getBitmap(context.contentResolver,it)

        } else {
            val source = ImageDecoder
                .createSource(context.contentResolver,it)
            bitmap.value = ImageDecoder.decodeBitmap(source)
        }


    }

    bitmap.value?.let {  btm ->
        Image(bitmap = btm.asImageBitmap(),
            contentDescription =null,
            modifier = Modifier.size(400.dp))
        imageBitmap.value = btm.asImageBitmap()
    }





    Scaffold(
        content = {
            Column(
                Modifier.fillMaxSize().padding(LARGE_PADDING),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(LARGE_PADDING)

            ) {


                if(viewModel.state.photoUri != null){
                    AppDisplayPhoto(
                        size = 150.dp,
                        image = imageBitmap.value,
                        onClick = {
                            launcher.launch("image/*")
                        },
                    )
                } else {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(state.photoUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = "",
                        contentScale = ContentScale.FillBounds,
                        modifier = Modifier
                            .size(150.dp).clip(CircleShape)
                            .clickable { launcher.launch("image/*") },
                    )
                }

                AppTextField(
                    value = state.username.value,
                    onValueChanged = {state.username.value = it},
                    placeholder = USERNAME_STRING,
                    leadingIcon = R.drawable.person
                )
                AppTextField(
                    value = state.bio.value,
                    onValueChanged = {state.bio.value = it},
                    placeholder = "Bio",
                    leadingIcon = R.drawable.edit
                )

            }
        },
        topBar = {
            AppTopAppBar(
                title = "Account Settings",
                navigationIcon = {
                    ActionConceptList(
                        menuActions = listOf(
                            ActionConcept(
                                action = {
                                    navController.navigate(Concept.Settings.route) {
                                        popUpTo(Concept.Settings.route) {
                                            inclusive = true
                                        }
                                    }
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
                                action = { save() },
                                concept = Concept.Save
                            )
                        )
                    )
                },
            )
        }
    )
}

