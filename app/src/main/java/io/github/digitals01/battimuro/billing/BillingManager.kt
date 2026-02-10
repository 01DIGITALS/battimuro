package io.github.digitals01.battimuro.billing

import android.app.Activity

interface BillingManager {
    fun connect()
    fun disconnect()
    fun purchaseStylePack(activity: Activity)
    fun purchaseLevelsPack(activity: Activity)
    fun purchaseDonation(activity: Activity)
    fun purchaseBonusPack(activity: Activity)
    fun queryPurchases()
    fun setListener(listener: BillingListener)
}

interface BillingListener {
    fun onPurchaseComplete(productId: String, success: Boolean)
    fun onPurchaseError(message: String)
    fun onConnectionReady()
}
