package io.github.digitals01.battimuro.billing

import android.content.Context

object BillingManagerFactory {
    fun create(context: Context, purchaseRepository: PurchaseRepository): BillingManager {
        return HuaweiBillingManager(purchaseRepository)
    }
}
