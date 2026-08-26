package com.debttracker.app.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.debttracker.app.ui.theme.LocalExtraColors
import kotlinx.coroutines.launch

/**
 * A row that can be dragged to reveal quick actions (edit / delete).
 * Works with both LTR and RTL layouts using Animatable.
 */
@Composable
fun SwipeToRevealRow(
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    editLabel: String,
    deleteLabel: String,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(20.dp),
    content: @Composable () -> Unit
) {
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    val directionSign = if (isRtl) 1f else -1f
    val scope = rememberCoroutineScope()
    val extraColors = LocalExtraColors.current

    var actionsWidthPx by remember { mutableFloatStateOf(0f) }
    val offsetX = remember { Animatable(0f) }

    fun collapse() {
        scope.launch {
            offsetX.animateTo(0f, animationSpec = tween(200))
        }
    }

    fun runAction(action: () -> Unit) {
        collapse()
        action()
    }

    Box(
        modifier = modifier
            .clip(shape)
            .draggable(
                state = rememberDraggableState { delta ->
                    if (actionsWidthPx > 0f) {
                        val targetOffset = actionsWidthPx * directionSign
                        val current = offsetX.value
                        val newOffset = current + delta
                        val clamped = if (isRtl) {
                            newOffset.coerceIn(0f, targetOffset)
                        } else {
                            newOffset.coerceIn(targetOffset, 0f)
                        }
                        scope.launch { offsetX.snapTo(clamped) }
                    }
                },
                orientation = Orientation.Horizontal,
                onDragStopped = { velocity ->
                    if (actionsWidthPx > 0f) {
                        val targetOffset = actionsWidthPx * directionSign
                        val current = offsetX.value
                        val threshold = targetOffset / 2f
                        val shouldOpen = if (isRtl) {
                            current > threshold || velocity > 300f
                        } else {
                            current < threshold || velocity < -300f
                        }
                        val finalTarget = if (shouldOpen) targetOffset else 0f
                        scope.launch {
                            offsetX.animateTo(finalTarget, animationSpec = tween(250))
                        }
                    }
                }
            )
    ) {
        Row(
            modifier = Modifier
                .align(if (isRtl) Alignment.CenterStart else Alignment.CenterEnd)
                .onSizeChanged { size -> actionsWidthPx = size.width.toFloat() }
                .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ActionButton(
                icon = Icons.Default.Edit,
                contentDescription = editLabel,
                containerColor = extraColors.positiveContainer,
                contentColor = extraColors.onPositiveContainer,
                onClick = { runAction(onEdit) }
            )
            ActionButton(
                icon = Icons.Default.Delete,
                contentDescription = deleteLabel,
                containerColor = extraColors.negativeContainer,
                contentColor = extraColors.onNegativeContainer,
                onClick = { runAction(onDelete) }
            )
        }
        Box(
            modifier = Modifier.graphicsLayer {
                translationX = offsetX.value
            }
        ) {
            content()
        }
    }
}

@Composable
private fun ActionButton(
    icon: ImageVector,
    contentDescription: String,
    containerColor: Color,
    contentColor: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(containerColor)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = contentColor,
            modifier = Modifier.size(22.dp)
        )
    }
}
