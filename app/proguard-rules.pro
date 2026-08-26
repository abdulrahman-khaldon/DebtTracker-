# DebtTracker ProGuard rules.
# Room, Hilt and Compose ship with consumer rules; nothing extra is required.
# Keep enum values used by Room TypeConverters.
-keepclassmembers enum com.debttracker.app.data.local.** {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Kotlin coroutines debug metadata (safe to keep, stripped in release anyway).
-dontwarn kotlinx.coroutines.**
