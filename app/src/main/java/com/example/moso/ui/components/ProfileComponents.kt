// app/src/main/java/com/example/moso/ui/components/ProfileComponents.kt
package com.example.moso.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.moso.ui.screens.profile.RecentOrder
import com.example.moso.ui.theme.MosoBlue
import com.example.moso.ui.theme.MosoBlueDark
import com.example.moso.ui.theme.MosoBrown
import com.example.moso.ui.theme.MosoGray
import com.example.moso.ui.theme.QuicksandFontFamily


@Composable
fun StatCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MosoBlue.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = MosoBlue)
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontFamily = QuicksandFontFamily,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                fontFamily = QuicksandFontFamily,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun AboutCard() {
    Card(
        Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(Modifier.fillMaxWidth().padding(16.dp)) {
            Text(
                "Sobre MOSO.app",
                style = MaterialTheme.typography.titleMedium,
                fontFamily = QuicksandFontFamily,
                fontWeight = FontWeight.Bold,
                color = MosoBlue
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "En MOSO.app manejamos componentes electrónicos accesibles, de buena calidad, ideales para tus proyectos de ingeniería.",
                style = MaterialTheme.typography.bodyMedium,
                fontFamily = QuicksandFontFamily,
                color = Color.DarkGray
            )
        }
    }
}

@Composable
fun RecentOrdersCard(
    orders: List<RecentOrder>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(Modifier.fillMaxWidth().padding(16.dp)) {
            Text(
                "Compras recientes",
                style = MaterialTheme.typography.titleMedium,
                fontFamily = QuicksandFontFamily,
                fontWeight = FontWeight.Bold,
                color = MosoBlue
            )
            Spacer(Modifier.height(16.dp))
            if (orders.isEmpty()) {
                Text(
                    "No tienes compras recientes",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium,
                    fontFamily = QuicksandFontFamily,
                    color = Color.Gray
                )
            } else {
                orders.forEachIndexed { index, order ->
                    OrderItem(order)
                    if (index < orders.lastIndex) {
                        Divider(color = MosoGray, modifier = Modifier.padding(vertical = 12.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun OrderItem(order: RecentOrder) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MosoBlue.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Receipt,
                contentDescription = null,
                tint = MosoBlue
            )
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(
                "Pedido #${order.id}",
                style = MaterialTheme.typography.bodyMedium,
                fontFamily = QuicksandFontFamily,
                fontWeight = FontWeight.SemiBold,
                color = MosoBlueDark,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.DateRange,
                    contentDescription = null,
                    tint = MosoBrown
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    order.date,
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = QuicksandFontFamily,
                    color = Color.Gray
                )
            }
        }
    }
}
