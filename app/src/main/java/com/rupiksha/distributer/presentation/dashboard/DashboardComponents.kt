package com.rupiksha.distributer.presentation.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rupiksha.distributer.ui.theme.*

data class StatData(
    val title: String,
    val value: String,
    val label: String,
    val icon: ImageVector,
    val iconColor: Color,
    val iconBgColor: Color
)

data class SummaryItem(
    val title: String,
    val subtitle: String,
    val valueStr: String,
    val isPositive: Boolean? = null,
    val isNew: Boolean = false
)

@Composable
fun StatCard(stat: StatData) {
    Card(
        modifier = Modifier
            .width(150.dp)
            .padding(horizontal = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stat.title,
                fontSize = 12.sp,
                color = TextSecondary,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(stat.iconBgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = stat.icon,
                    contentDescription = null,
                    tint = stat.iconColor,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stat.value,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = stat.label,
                fontSize = 10.sp,
                color = TextSecondary
            )
        }
    }
}

@Composable
fun SummaryItemCard(
    item: SummaryItem,
    isExpanded: Boolean = false,
    onExpandClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon Placeholder (matching the colored circles in the image)
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(InteractionBg), // Use a light background
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when(item.title) {
                        "SMAs" -> Icons.Default.Person
                        "Transacting" -> Icons.Default.SwapVert
                        "Gross Adds" -> Icons.AutoMirrored.Filled.TrendingUp
                        "Non-transacting" -> Icons.Default.RemoveCircleOutline
                        else -> Icons.Default.Star
                    },
                    contentDescription = null,
                    tint = BrandPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = item.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = TextPrimary
                    )
                    if (item.isNew) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            color = Color(0xFF34A853),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "NEW",
                                color = Color.White,
                                fontSize = 8.sp,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                Text(
                    text = item.subtitle,
                    color = TextSecondary,
                    fontSize = 10.sp,
                    lineHeight = 14.sp
                )
            }
            
            if (item.valueStr.isNotEmpty()) {
                val isPositive = item.isPositive == true
                val bgColor = if (isPositive) TrendUpBg else TrendDownBg
                val textColor = if (isPositive) TrendUp else TrendDown
                val arrow = if (isPositive) "↑" else "↓"
                
                Surface(
                    color = bgColor,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "$arrow ${item.valueStr}",
                        color = textColor,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            IconButton(onClick = onExpandClick) {
                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = TextSecondary
                )
            }
        }
        HorizontalDivider(thickness = 0.5.dp, color = BorderLight)
    }
}

@Composable
fun DashboardToggle(
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = Color(0xFFF1F3F4),
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        Row {
            listOf("Monthly", "Daily").forEach { option ->
                val isSelected = selectedOption == option
                Surface(
                    onClick = { onOptionSelected(option) },
                    shape = RoundedCornerShape(24.dp),
                    color = if (isSelected) BrandPrimary else Color.Transparent
                ) {
                    Text(
                        text = option,
                        color = if (isSelected) Color.White else TextSecondary,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}
