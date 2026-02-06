package io.github.digitals01.battimuro.billing

import android.app.Activity
import android.content.Context
import android.util.Log
import com.android.billingclient.api.*

class GooglePlayBillingManager(
    private val context: Context,
    private val purchaseRepository: PurchaseRepository
) : BillingManager, PurchasesUpdatedListener {

    companion object {
        private const val TAG = "GooglePlayBilling"
        const val PRODUCT_STYLE_PACK = "style_pack"
        const val PRODUCT_LEVELS_PACK = "levels_pack"
        const val PRODUCT_DONATION = "donation"
    }

    private var listener: BillingListener? = null
    private var productDetailsMap = mutableMapOf<String, ProductDetails>()

    private val billingClient = BillingClient.newBuilder(context)
        .setListener(this)
        .enablePendingPurchases()
        .build()

    override fun setListener(listener: BillingListener) {
        this.listener = listener
    }

    override fun connect() {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(result: BillingResult) {
                if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                    Log.d(TAG, "Billing connected")
                    queryProductDetails()
                    listener?.onConnectionReady()
                } else {
                    Log.e(TAG, "Billing connect failed: ${result.debugMessage}")
                }
            }

            override fun onBillingServiceDisconnected() {
                Log.w(TAG, "Billing disconnected, will retry on next action")
            }
        })
    }

    override fun disconnect() {
        billingClient.endConnection()
    }

    private fun queryProductDetails() {
        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(
                listOf(PRODUCT_STYLE_PACK, PRODUCT_LEVELS_PACK, PRODUCT_DONATION).map { productId ->
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(productId)
                        .setProductType(BillingClient.ProductType.INAPP)
                        .build()
                }
            )
            .build()

        billingClient.queryProductDetailsAsync(params) { result, detailsList ->
            if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                detailsList.forEach { details ->
                    productDetailsMap[details.productId] = details
                }
                Log.d(TAG, "Product details loaded: ${productDetailsMap.keys}")
            }
        }
    }

    override fun purchaseStylePack(activity: Activity) {
        launchPurchase(activity, PRODUCT_STYLE_PACK)
    }

    override fun purchaseLevelsPack(activity: Activity) {
        launchPurchase(activity, PRODUCT_LEVELS_PACK)
    }

    override fun purchaseDonation(activity: Activity) {
        launchPurchase(activity, PRODUCT_DONATION)
    }

    private fun launchPurchase(activity: Activity, productId: String) {
        val details = productDetailsMap[productId]
        if (details == null) {
            listener?.onPurchaseError("Prodotto non disponibile. Riprova tra poco.")
            return
        }

        val flowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(
                listOf(
                    BillingFlowParams.ProductDetailsParams.newBuilder()
                        .setProductDetails(details)
                        .build()
                )
            )
            .build()

        billingClient.launchBillingFlow(activity, flowParams)
    }

    override fun queryPurchases() {
        val params = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.INAPP)
            .build()

        billingClient.queryPurchasesAsync(params) { result, purchases ->
            if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                val hasStylePack = purchases.any {
                    it.products.contains(PRODUCT_STYLE_PACK) &&
                            it.purchaseState == Purchase.PurchaseState.PURCHASED
                }
                purchaseRepository.setStylePackPurchased(hasStylePack)

                val hasLevelsPack = purchases.any {
                    it.products.contains(PRODUCT_LEVELS_PACK) &&
                            it.purchaseState == Purchase.PurchaseState.PURCHASED
                }
                purchaseRepository.setLevelsPackPurchased(hasLevelsPack)

                purchases.filter {
                    it.purchaseState == Purchase.PurchaseState.PURCHASED && !it.isAcknowledged
                }.forEach { purchase ->
                    acknowledgePurchase(purchase)
                }
            }
        }
    }

    override fun onPurchasesUpdated(result: BillingResult, purchases: MutableList<Purchase>?) {
        when (result.responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                purchases?.forEach { purchase ->
                    if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                        if (purchase.products.contains(PRODUCT_STYLE_PACK)) {
                            purchaseRepository.setStylePackPurchased(true)
                            listener?.onPurchaseComplete(PRODUCT_STYLE_PACK, true)
                        }
                        if (purchase.products.contains(PRODUCT_LEVELS_PACK)) {
                            purchaseRepository.setLevelsPackPurchased(true)
                            listener?.onPurchaseComplete(PRODUCT_LEVELS_PACK, true)
                        }
                        if (!purchase.isAcknowledged) {
                            acknowledgePurchase(purchase)
                        }
                    }
                }
            }
            BillingClient.BillingResponseCode.USER_CANCELED -> {
                Log.d(TAG, "User canceled purchase")
            }
            else -> {
                listener?.onPurchaseError("Errore acquisto: ${result.debugMessage}")
            }
        }
    }

    private fun acknowledgePurchase(purchase: Purchase) {
        val params = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()

        billingClient.acknowledgePurchase(params) { result ->
            if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                Log.d(TAG, "Purchase acknowledged")
            }
        }
    }
}
