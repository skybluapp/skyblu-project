package com.skyblu.userinterface.componants.alerts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.skyblu.configuration.Concept
import com.skyblu.configuration.MEDIUM_PADDING
import com.skyblu.userinterface.componants.ActionConcept

@Composable
fun StyledBanner(
    text: String,
    actionConcept: ActionConcept,
    color: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = color),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = text,
            Modifier
                .weight(9F)
                .padding(start = MEDIUM_PADDING),
            fontWeight = FontWeight.Bold
        )
        IconButton(
            onClick = { actionConcept.action() },
            modifier = Modifier.weight(1F)
        ) {
            Icon(
                painter = painterResource(id = actionConcept.concept.icon),
                contentDescription = actionConcept.concept.title
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun AppBannerPreview() {
    StyledBanner(
        text = "This is a Banner",
        actionConcept = ActionConcept(
            action = {},
            concept = Concept.Close
        ),
        color = MaterialTheme.colors.background
    )
}
