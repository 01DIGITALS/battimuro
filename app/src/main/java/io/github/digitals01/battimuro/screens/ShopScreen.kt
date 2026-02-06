package io.github.digitals01.battimuro.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.digitals01.battimuro.ui.theme.GoldBase
import io.github.digitals01.battimuro.ui.theme.GoldLight
import io.github.digitals01.battimuro.ui.theme.NeonCyan
import io.github.digitals01.battimuro.ui.theme.NeonGreen
import io.github.digitals01.battimuro.ui.theme.NeonGray
import io.github.digitals01.battimuro.ui.theme.NeonMagenta

@Composable
fun ShopScreen(
    isStylePackOwned: Boolean,
    isLevelsPackOwned: Boolean,
    onPurchaseStylePack: () -> Unit,
    onPurchaseLevelsPack: () -> Unit,
    onPurchaseDonation: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "NEGOZIO",
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            color = GoldBase,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // --- STYLE PACK ---
        ShopCard(
            title = "PACCHETTO STILI",
            description = "Sblocca 7 stili aggiuntivi per la pallina e 7 per i respingenti: Pulsante, Prisma, Fantasma, Pixel, Plasma, Ghiaccio e Oro.",
            price = "0,99 \u20AC",
            isPurchased = isStylePackOwned,
            onPurchase = onPurchaseStylePack,
            accentColor = GoldBase
        )

        // --- LEVELS PACK ---
        ShopCard(
            title = "PACCHETTO LIVELLI",
            description = "5 livelli a difficolt\u00e0 crescente con ostacoli nel campo di gioco che deflettono la pallina.",
            price = "0,99 \u20AC",
            isPurchased = isLevelsPackOwned,
            onPurchase = onPurchaseLevelsPack,
            accentColor = NeonCyan
        )

        // --- BONUS PACK ---
        ShopCard(
            title = "PACCHETTO BONUS",
            description = "Power-up durante la partita: respingente allargato, multiball e penalizzazione riduzione barra.",
            price = "1,99 \u20AC",
            isPurchased = false,
            isComingSoon = true,
            onPurchase = {},
            accentColor = NeonMagenta
        )

        // --- DONATION ---
        Card(
            modifier = Modifier.fillMaxWidth(0.9f),
            colors = CardDefaults.cardColors(containerColor = NeonGray)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "SUPPORTA LO SVILUPPATORE",
                    color = NeonGreen,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "Se ti piace Battimuro, puoi offrirmi un caff\u00e8 per supportare lo sviluppo futuro del gioco!",
                    color = Color.Gray,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = onPurchaseDonation,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NeonGreen,
                        contentColor = Color.Black
                    )
                ) {
                    Text("DONA - 1,99 \u20AC", fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = onBack,
            colors = ButtonDefaults.outlinedButtonColors(contentColor = NeonMagenta)
        ) {
            Text("INDIETRO", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun ShopCard(
    title: String,
    description: String,
    price: String,
    isPurchased: Boolean,
    isComingSoon: Boolean = false,
    onPurchase: () -> Unit,
    accentColor: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(0.9f),
        colors = CardDefaults.cardColors(containerColor = NeonGray)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                title,
                color = accentColor,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Spacer(Modifier.height(4.dp))
            Text(
                description,
                color = Color.Gray,
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(12.dp))
            when {
                isPurchased -> {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Filled.Check,
                            contentDescription = null,
                            tint = NeonGreen,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text("ACQUISTATO", color = NeonGreen, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
                isComingSoon -> {
                    OutlinedButton(
                        onClick = {},
                        enabled = false
                    ) {
                        Text("IN ARRIVO - $price", fontWeight = FontWeight.Bold)
                    }
                }
                else -> {
                    Button(
                        onClick = onPurchase,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = accentColor,
                            contentColor = Color.Black
                        )
                    ) {
                        Text("SBLOCCA - $price", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
