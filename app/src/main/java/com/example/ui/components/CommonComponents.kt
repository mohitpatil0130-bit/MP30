package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.StatusAbsentBg
import com.example.ui.theme.StatusAbsentText
import com.example.ui.theme.StatusLateBg
import com.example.ui.theme.StatusLateText
import com.example.ui.theme.StatusLeaveBg
import com.example.ui.theme.StatusLeaveText
import com.example.ui.theme.StatusPassActiveBg
import com.example.ui.theme.StatusPassActiveText
import com.example.ui.theme.StatusPresentBg
import com.example.ui.theme.StatusPresentText

fun dialPhoneNumber(context: Context, phoneNumber: String) {
    try {
        val cleanNumber = phoneNumber.replace(" ", "")
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$cleanNumber")
        }
        context.startActivity(intent)
    } catch (_: Exception) {
    }
}

@Composable
fun StudentAvatar(
    name: String,
    hexColor: String,
    modifier: Modifier = Modifier,
    sizeDp: Int = 42
) {
    val initials = name.split(" ")
        .mapNotNull { it.firstOrNull()?.toString() }
        .take(2)
        .joinToString("")
        .uppercase()

    val parsedColor = try {
        Color(android.graphics.Color.parseColor(hexColor))
    } catch (_: Exception) {
        MaterialTheme.colorScheme.primary
    }

    Box(
        modifier = modifier
            .size(sizeDp.dp)
            .clip(CircleShape)
            .background(parsedColor),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (initials.isNotEmpty()) initials else "G",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = (sizeDp * 0.38).sp
        )
    }
}

@Composable
fun AttendanceStatusBadge(
    status: String,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, label) = when (status) {
        "PRESENT" -> Triple(StatusPresentBg, StatusPresentText, "Present")
        "LATE" -> Triple(StatusLateBg, StatusLateText, "Late Entry")
        "ABSENT" -> Triple(StatusAbsentBg, StatusAbsentText, "Absent")
        "ON_LEAVE" -> Triple(StatusLeaveBg, StatusLeaveText, "On Leave")
        "ACTIVE_OUT" -> Triple(StatusPassActiveBg, StatusPassActiveText, "Out on Pass")
        else -> Triple(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.onSurfaceVariant, "Unmarked")
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val icon = when (status) {
                "PRESENT" -> Icons.Default.CheckCircle
                "LATE" -> Icons.Default.Schedule
                "ABSENT" -> Icons.Default.Warning
                "ON_LEAVE", "ACTIVE_OUT" -> Icons.Default.ExitToApp
                else -> null
            }
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = textColor,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
            Text(
                text = label,
                color = textColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun MetricSummaryCard(
    title: String,
    count: String,
    subtitle: String,
    accentColor: Color,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Card(
        modifier = modifier
            .then(
                if (onClick != null) Modifier.clickable { onClick() } else Modifier
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = accentColor.copy(alpha = 0.12f)
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = count,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = accentColor
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
fun QuickCallAction(
    phoneNumber: String,
    label: String = "Call",
    testTagKey: String = "call_button"
) {
    val context = LocalContext.current
    IconButton(
        onClick = { dialPhoneNumber(context, phoneNumber) },
        modifier = Modifier.testTag(testTagKey)
    ) {
        Icon(
            imageVector = Icons.Default.Call,
            contentDescription = label,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
    }
}
