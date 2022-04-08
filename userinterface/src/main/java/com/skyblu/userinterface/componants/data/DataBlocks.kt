package com.skyblu.userinterface.componants.data

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.skyblu.configuration.MEDIUM_PADDING
import com.skyblu.configuration.USERNAME_STRING
import com.skyblu.models.jump.User

@Preview(showBackground = true)
@Composable
fun ProfileHeader(user: User = User()) {
    Column() {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(
                modifier = Modifier.height(100.dp),
                verticalArrangement = Arrangement.Center
            ) {
                LabelledText(
                    user.username,
                    USERNAME_STRING
                )
            }

            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(user.photoUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "",
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .size(100.dp)
                    .size(100.dp)
                    .clip(CircleShape)
            )
            Column(
                modifier = Modifier.height(100.dp),
                verticalArrangement = Arrangement.Center
            ) {
                LabelledText(
                    user.licence.toString(),
                    USERNAME_STRING
                )
            }
        }

        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(MEDIUM_PADDING)
        ) {
            Text(text = user.bio)
        }
    }
}