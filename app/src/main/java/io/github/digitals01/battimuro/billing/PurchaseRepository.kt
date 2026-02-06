package io.github.digitals01.battimuro.billing

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PurchaseRepository(context: Context) {
    companion object {
        private const val PREFS_NAME = "battimuro_purchases"
        private const val KEY_STYLE_PACK = "style_pack_purchased"
        private const val KEY_LEVELS_PACK = "levels_pack_purchased"
    }

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _stylePackOwned = MutableStateFlow(prefs.getBoolean(KEY_STYLE_PACK, false))
    val stylePackOwned: StateFlow<Boolean> = _stylePackOwned.asStateFlow()

    fun setStylePackPurchased(purchased: Boolean) {
        prefs.edit().putBoolean(KEY_STYLE_PACK, purchased).apply()
        _stylePackOwned.value = purchased
    }

    fun isStylePackPurchased(): Boolean = _stylePackOwned.value

    private val _levelsPackOwned = MutableStateFlow(prefs.getBoolean(KEY_LEVELS_PACK, false))
    val levelsPackOwned: StateFlow<Boolean> = _levelsPackOwned.asStateFlow()

    fun setLevelsPackPurchased(purchased: Boolean) {
        prefs.edit().putBoolean(KEY_LEVELS_PACK, purchased).apply()
        _levelsPackOwned.value = purchased
    }

    fun isLevelsPackPurchased(): Boolean = _levelsPackOwned.value
}
