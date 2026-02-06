package io.github.digitals01.battimuro

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import io.github.digitals01.battimuro.billing.BillingListener
import io.github.digitals01.battimuro.billing.BillingManager
import io.github.digitals01.battimuro.billing.BillingManagerFactory
import io.github.digitals01.battimuro.billing.PurchaseRepository
import io.github.digitals01.battimuro.game.BallStyle
import io.github.digitals01.battimuro.game.PaddleStyle
import io.github.digitals01.battimuro.screens.GameScreen
import io.github.digitals01.battimuro.screens.HomeScreen
import io.github.digitals01.battimuro.screens.OptionsScreen
import io.github.digitals01.battimuro.screens.ShopScreen
import io.github.digitals01.battimuro.ui.theme.BattimuroTheme

class MainActivity : ComponentActivity() {

    private lateinit var purchaseRepository: PurchaseRepository
    private lateinit var billingManager: BillingManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        purchaseRepository = PurchaseRepository(applicationContext)
        billingManager = BillingManagerFactory.create(applicationContext, purchaseRepository)

        billingManager.setListener(object : BillingListener {
            override fun onPurchaseComplete(productId: String, success: Boolean) {
                if (success && productId == "style_pack") {
                    purchaseRepository.setStylePackPurchased(true)
                }
            }

            override fun onPurchaseError(message: String) {}

            override fun onConnectionReady() {
                billingManager.queryPurchases()
            }
        })

        billingManager.connect()

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        Handler(Looper.getMainLooper()).postDelayed({
            setTheme(R.style.Theme_Battimuro)

            setContent {
                BattimuroTheme {
                    Surface(modifier = Modifier.fillMaxSize()) {
                        BattimuroApp(
                            purchaseRepository = purchaseRepository,
                            onPurchaseStylePack = {
                                billingManager.purchaseStylePack(this@MainActivity)
                            },
                            onPurchaseDonation = {
                                billingManager.purchaseDonation(this@MainActivity)
                            }
                        )
                    }
                }
            }
        }, 2000)
    }

    override fun onDestroy() {
        super.onDestroy()
        billingManager.disconnect()
    }
}

enum class Screen {
    HOME,
    OPTIONS,
    SHOP,
    GAME
}

@Composable
fun BattimuroApp(
    purchaseRepository: PurchaseRepository,
    onPurchaseStylePack: () -> Unit,
    onPurchaseDonation: () -> Unit
) {
    val isStylePackOwned by purchaseRepository.stylePackOwned.collectAsState()

    var currentScreen by remember { mutableStateOf(Screen.HOME) }

    var gameMode by remember { mutableStateOf(io.github.digitals01.battimuro.game.GameMode.ONE_VS_CPU) }
    var difficulty by remember { mutableStateOf(io.github.digitals01.battimuro.game.Difficulty.MEDIUM) }
    var playerIsLeft by remember { mutableStateOf(true) }
    var ballStyle by remember { mutableStateOf(BallStyle.NEON) }
    var paddleStyle by remember { mutableStateOf(PaddleStyle.NEON) }

    when (currentScreen) {
        Screen.HOME -> {
            HomeScreen(
                onStartGame = { mode, diff, isLeft ->
                    gameMode = mode
                    difficulty = diff
                    playerIsLeft = isLeft
                    currentScreen = Screen.GAME
                },
                onOpenOptions = {
                    currentScreen = Screen.OPTIONS
                },
                onOpenShop = {
                    currentScreen = Screen.SHOP
                }
            )
        }
        Screen.OPTIONS -> {
            OptionsScreen(
                ballStyle = ballStyle,
                paddleStyle = paddleStyle,
                onBallStyleChange = { newStyle ->
                    if (!newStyle.isPremium || isStylePackOwned) {
                        ballStyle = newStyle
                    }
                },
                onPaddleStyleChange = { newStyle ->
                    if (!newStyle.isPremium || isStylePackOwned) {
                        paddleStyle = newStyle
                    }
                },
                isStylePackOwned = isStylePackOwned,
                onBack = { currentScreen = Screen.HOME }
            )
        }
        Screen.SHOP -> {
            ShopScreen(
                isStylePackOwned = isStylePackOwned,
                onPurchaseStylePack = onPurchaseStylePack,
                onPurchaseDonation = onPurchaseDonation,
                onBack = { currentScreen = Screen.HOME }
            )
        }
        Screen.GAME -> {
            GameScreen(
                gameMode = gameMode,
                difficulty = difficulty,
                playerIsLeft = playerIsLeft,
                ballStyle = ballStyle,
                paddleStyle = paddleStyle,
                onGameOver = {
                    currentScreen = Screen.HOME
                },
                onBack = {
                    currentScreen = Screen.HOME
                }
            )
        }
    }
}
