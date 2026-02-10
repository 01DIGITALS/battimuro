package io.github.digitals01.battimuro.billing

import android.app.Activity
import android.widget.Toast

class HuaweiBillingManager(
    private val purchaseRepository: PurchaseRepository
) : BillingManager {

    private var listener: BillingListener? = null

    override fun setListener(listener: BillingListener) {
        this.listener = listener
    }

    override fun connect() {
        listener?.onConnectionReady()
    }

    override fun disconnect() {}

    override fun purchaseStylePack(activity: Activity) {
        Toast.makeText(activity, "Acquisti non ancora disponibili su questo store", Toast.LENGTH_SHORT).show()
        listener?.onPurchaseError("Huawei IAP not yet implemented")
    }

    override fun purchaseLevelsPack(activity: Activity) {
        Toast.makeText(activity, "Acquisti non ancora disponibili su questo store", Toast.LENGTH_SHORT).show()
        listener?.onPurchaseError("Huawei IAP not yet implemented")
    }

    override fun purchaseDonation(activity: Activity) {
        Toast.makeText(activity, "Acquisti non ancora disponibili su questo store", Toast.LENGTH_SHORT).show()
        listener?.onPurchaseError("Huawei IAP not yet implemented")
    }

    override fun purchaseBonusPack(activity: Activity) {
        Toast.makeText(activity, "Acquisti non ancora disponibili su questo store", Toast.LENGTH_SHORT).show()
        listener?.onPurchaseError("Huawei IAP not yet implemented")
    }

    override fun queryPurchases() {}
}
