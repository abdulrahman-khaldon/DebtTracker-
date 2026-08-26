# DebtTracker ProGuard rules.
-keep class com.debttracker.app.data.local.** { *; }
-keep class com.debttracker.app.model.** { *; }
-keepclassmembers class * extends androidx.room.RoomDatabase {
    public abstract <methods>;
}
-keepclassmembers enum com.debttracker.app.data.local.** {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-dontwarn kotlinx.coroutines.**

