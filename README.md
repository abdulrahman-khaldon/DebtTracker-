# DebtTracker — تتبع الديون

تطبيق أندرويد كامل لتتبع الديون بينك وبين أصدقائك ومعارفك. يعمل بدون إنترنت بالكامل،
وبياناتك محفوظة على جهازك فقط.

A complete offline Android app for tracking debts between you and your contacts.
Kotlin + Jetpack Compose + Room + Hilt.

## المميزات | Features

- **الشاشة الرئيسية**: بطاقة ملخص (لي عند الناس / عليّ للناس / الرصيد الصافي)، قائمة أشخاص
  مرتبة حسب الرصيد، سحب أي شخص يكشف زرَّي تعديل وحذف.
- **تفاصيل الشخص**: ترويسة كبيرة مع الرصيد ورقم الهاتف (اتصال مباشر)، سجل المعاملات
  (لمس للتعديل، ضغط مطوّل للحذف)، تبويب إضافة معاملة (مبدّل «لي عليه / له عليّ»، لوحة أرقام،
  منتقي تاريخ، ملاحظات)، وزر «تسوية الكل».
- **الإعدادات**: عربي/English، الوضع الداكن، تصدير البيانات إلى CSV، مسح جميع البيانات.
- **عربي أولاً**: اتجاه RTL تلقائي مع اللغة، أرقام عربية-هندية (٠١٢٣) في الوضع العربي
  وأرقام غربية في الإنجليزي، عملة الريال اليمني (ر.ي / YER).
- **منطق الأرصدة**: «لي عليه» (CREDIT) يزيد رصيدَه لديك، «له عليّ» (DEBT) ينقصه.
  زر «تسوية الكل» يعلّم كل معاملات الشخص كمُسوّاة (يبقى السجل ويصبح الرصيد صفراً).

## التقنيات | Tech stack

| المكوّن | التقنية |
|---|---|
| اللغة | Kotlin 2.0.21 |
| الواجهة | Jetpack Compose (BOM 2024.12.01) + Material 3 |
| قاعدة البيانات | Room 2.6.1 |
| الحقن | Hilt 2.52 (KSP) |
| التنقل | Navigation Compose 2.8.5 |
| البنية | MVVM + StateFlow + Repository |
| الحد الأدنى | Android 8.0 (API 26) |

## البناء | Building

**من Android Studio:** افتح مجلد المشروع واضغط Run.

**من سطر الأوامر:**

```bash
# Windows
gradlew.bat assembleDebug
# APK الناتج: app/build/outputs/apk/debug/app-debug.apk
```

يتطلب JDK 17 و Android SDK (API 35). إذا فتحت المشروع في Android Studio سيتم ضبط كل شيء تلقائياً.

## بنية المشروع | Structure

```
app/src/main/java/com/debttracker/app/
├── DebtTrackerApp.kt, MainActivity.kt, MainViewModel.kt
├── di/            # Hilt modules (Room + DataStore)
├── data/
│   ├── local/     # Room entities, DAOs, database, converters
│   ├── repository/# DebtRepository + SettingsRepository
│   └── csv/       # CsvExporter (UTF-8 BOM)
├── model/         # AppLanguage, AppSettings
├── ui/
│   ├── theme/     # Material 3 color schemes (light/dark) + typography
│   ├── navigation/# Routes + NavHost
│   ├── components/# Avatar, EmptyState, SwipeToRevealRow, DatePickerField...
│   ├── home/      # HomeScreen + HomeViewModel
│   ├── contact/   # AddEdit + ContactDetail (screens + view models)
│   └── settings/  # SettingsScreen + SettingsViewModel
└── util/          # Formatters (amounts, dates, Arabic numerals)
```

## ملاحظات | Notes

- تبديل اللغة من داخل الإعدادات يطبَّق فوراً بدون إعادة تشغيل التطبيق.
- اسم التطبيق تحت أيقونة المشغّل يتبع لغة النظام (سلوك أندرويد القياسي).
- ملف CSV يبدأ بـ BOM حتى تظهر النصوص العربية بشكل صحيح في Excel.
