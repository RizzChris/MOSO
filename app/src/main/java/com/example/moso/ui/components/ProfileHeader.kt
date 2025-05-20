
// app/src/main/java/com/example/moso/ui/components/ProfileComponents.kt
package com.example.moso.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.moso.ui.screens.profile.MosoBlue
import com.example.moso.ui.screens.profile.MosoBlueDark
import com.example.moso.ui.screens.profile.MosoWhite
import com.example.moso.ui.theme.QuicksandFontFamily

@Composable
fun ProfileHeader(
    name: String,
    email: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(MosoBlue),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Avatar",
                    tint = MosoWhite,
                    modifier = Modifier.size(48.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = name,
                style = MaterialTheme.typography.headlineSmall,
                fontFamily = QuicksandFontFamily,
                fontWeight = FontWeight.Bold,
                color = MosoBlueDark
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = email,
                style = MaterialTheme.typography.bodyMedium,
                fontFamily = QuicksandFontFamily,
                color = Color.Gray
            )
        }
    }
}
