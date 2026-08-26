package com.debttracker.app.model

data class AppSettings(
    val language: AppLanguage = AppLanguage.ARABIC,
    /** null = follow the system dark-mode setting. */
    val darkModeOverride: Boolean? = null
)
