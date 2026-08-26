package com.debttracker.app.model

import java.util.Locale

enum class AppLanguage(val code: String, val locale: Locale) {
    ARABIC("ar", Locale("ar")),
    ENGLISH("en", Locale.ENGLISH);

    companion object {
        fun fromCode(code: String?): AppLanguage =
            entries.firstOrNull { it.code == code } ?: ARABIC
    }
}
