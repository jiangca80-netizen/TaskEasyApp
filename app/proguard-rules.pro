# Keep line numbers for crash stack traces
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Room persistence classes
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**
