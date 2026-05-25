package com.example.techmain.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.techmain.ui.theme.GlassWhite
import com.example.techmain.ui.theme.GlassWhiteHigh
import com.example.techmain.ui.theme.NeonSlatePrimary
import com.example.techmain.ui.theme.NeonSlateSecondary
import com.example.techmain.ui.theme.NeonSlateSurfaceBorder

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    border: BorderStroke? = BorderStroke(1.dp, GlassWhiteHigh),
    content: @Composable ColumnScope.() -> Unit
) {
    val cardContent: @Composable ColumnScope.() -> Unit = {
        Column(
            modifier = Modifier.padding(contentPadding),
            content = content
        )
    }

    val glassModifier = modifier.blur(radius = 12.dp)

    if (onClick != null) {
        Card(
            onClick = onClick,
            modifier = glassModifier,
            shape = RoundedCornerShape(24.dp),
            border = border,
            colors = CardDefaults.cardColors(containerColor = GlassWhite),
            content = cardContent
        )
    } else {
        Card(
            modifier = glassModifier,
            shape = RoundedCornerShape(24.dp),
            border = border,
            colors = CardDefaults.cardColors(containerColor = GlassWhite),
            content = cardContent
        )
    }
}

@Composable
fun NeonButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isPrimary: Boolean = true,
    content: @Composable RowScope.() -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier.shadow(
            elevation = if (enabled) 8.dp else 0.dp,
            shape = RoundedCornerShape(12.dp),
            ambientColor = if (isPrimary) NeonSlatePrimary else NeonSlateSecondary,
            spotColor = if (isPrimary) NeonSlatePrimary else NeonSlateSecondary
        ),
        enabled = enabled,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isPrimary) NeonSlatePrimary else NeonSlateSecondary,
            disabledContainerColor = NeonSlateSurfaceBorder
        ),
        content = content
    )
}
