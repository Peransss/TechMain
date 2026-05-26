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
import com.example.techmain.ui.theme.CyberPrimary
import com.example.techmain.ui.theme.CyberSecondary
import com.example.techmain.ui.theme.CyberAccent
import com.example.techmain.ui.theme.CyberSurfaceBorder
import com.example.techmain.ui.theme.CyberBackground
import com.example.techmain.ui.theme.CyberTextSecondary

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    border: BorderStroke? = BorderStroke(1.dp, CyberSurfaceBorder),
    containerColor: Color = CyberBackground,
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
            modifier = modifier.shadow(
                elevation = 4.dp,
                shape = shape,
                ambientColor = CyberPrimary.copy(alpha = 0.2f),
                spotColor = CyberPrimary.copy(alpha = 0.2f)
            ),
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

@Composable
fun NeonButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isPrimary: Boolean = true,
    isOutlined: Boolean = false,
    containerColor: Color? = null,
    content: @Composable RowScope.() -> Unit
) {
    val accentColor = if (isPrimary) CyberAccent else CyberPrimary
    
    val resolvedContainerColor = containerColor ?: when {
        !enabled -> CyberSurfaceBorder.copy(alpha = 0.5f)
        isOutlined -> Color.Transparent
        else -> accentColor
    }
    
    val contentColor = when {
        !enabled -> CyberTextSecondary
        isOutlined -> accentColor
        else -> Color.White
    }
    
    val border = if (isOutlined && enabled) {
        BorderStroke(1.dp, accentColor)
    } else if (isOutlined && !enabled) {
        BorderStroke(1.dp, CyberSurfaceBorder)
    } else null

    Button(
        onClick = onClick,
        modifier = if (enabled && !isOutlined && containerColor == null) {
            modifier.shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(12.dp),
                ambientColor = accentColor,
                spotColor = accentColor
            )
        } else modifier,
        enabled = enabled,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = resolvedContainerColor,
            contentColor = contentColor,
            disabledContainerColor = CyberSurfaceBorder.copy(alpha = 0.5f),
            disabledContentColor = CyberTextSecondary
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
    val bgColor = when {
        isCorrect -> Color(0xFF34D399)
        isWrong -> CyberAccent
        isSelected -> CyberPrimary
        else -> CyberSurfaceBorder
    }

    NeonButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        isPrimary = isSelected,
        containerColor = bgColor
    ) {
        Text(
            text = text,
            modifier = Modifier.fillMaxWidth(),
            textAlign = androidx.compose.ui.text.style.TextAlign.Start
        )
    }
}
