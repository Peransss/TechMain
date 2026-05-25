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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.techmain.ui.theme.GlassWhite
import com.example.techmain.ui.theme.GlassWhiteHigh
import com.example.techmain.ui.theme.NeonSlatePrimary
import com.example.techmain.ui.theme.NeonSlateSecondary
import com.example.techmain.ui.theme.NeonSlateSurfaceBorder
import com.example.techmain.ui.theme.NeonSlateTextSecondary

/**
 * A glassmorphism-styled card with semi-transparent background and optional click handling.
 * Note: backdrop-blur (via Modifier.blur) is currently omitted from the main container
 * as it blurs card content. Future updates will explore API 31+ RenderEffect for true backdrop blur.
 */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    border: BorderStroke? = BorderStroke(1.dp, GlassWhiteHigh),
    containerColor: Color = GlassWhite,
    shape: RoundedCornerShape = RoundedCornerShape(24.dp),
    content: @Composable ColumnScope.() -> Unit
) {
    val cardContent: @Composable ColumnScope.() -> Unit = {
        Column(
            modifier = Modifier.padding(contentPadding),
            content = content
        )
    }

    if (onClick != null) {
        Card(
            onClick = onClick,
            modifier = modifier,
            shape = shape,
            border = border,
            colors = CardDefaults.cardColors(
                containerColor = containerColor,
                contentColor = Color.Unspecified
            ),
            content = cardContent
        )
    } else {
        Card(
            modifier = modifier,
            shape = shape,
            border = border,
            colors = CardDefaults.cardColors(
                containerColor = containerColor,
                contentColor = Color.Unspecified
            ),
            content = cardContent
        )
    }
}

/**
 * A neon-styled button supporting solid primary and outlined secondary (glass) variations.
 */
@Composable
fun NeonButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isPrimary: Boolean = true,
    isOutlined: Boolean = false,
    content: @Composable RowScope.() -> Unit
) {
    val accentColor = if (isPrimary) NeonSlatePrimary else NeonSlateSecondary
    
    val containerColor = when {
        !enabled -> NeonSlateSurfaceBorder.copy(alpha = 0.5f)
        isOutlined -> GlassWhite
        else -> accentColor
    }
    
    val contentColor = when {
        !enabled -> NeonSlateTextSecondary
        isOutlined -> accentColor
        else -> Color.White
    }
    
    val border = if (isOutlined && enabled) {
        BorderStroke(1.dp, accentColor)
    } else if (isOutlined && !enabled) {
        BorderStroke(1.dp, NeonSlateSurfaceBorder)
    } else null

    Button(
        onClick = onClick,
        modifier = modifier.shadow(
            elevation = if (enabled && !isOutlined) 8.dp else 0.dp,
            shape = RoundedCornerShape(12.dp),
            ambientColor = accentColor,
            spotColor = accentColor
        ),
        enabled = enabled,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = NeonSlateSurfaceBorder.copy(alpha = 0.5f),
            disabledContentColor = NeonSlateTextSecondary
        ),
        border = border,
        content = content
    )
}

@Composable
fun AnswerButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isSelected: Boolean = false,
    isCorrect: Boolean = false,
    isWrong: Boolean = false
) {
    val containerColor = when {
        isCorrect -> Color(0xFF4CAF50)
        isWrong -> Color(0xFFE53935)
        isSelected -> NeonSlatePrimary
        else -> NeonSlateSurfaceBorder
    }

    NeonButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        isPrimary = isSelected
    ) {
        Text(
            text = text,
            modifier = Modifier.fillMaxWidth(),
            textAlign = androidx.compose.ui.text.style.TextAlign.Start
        )
    }
}
