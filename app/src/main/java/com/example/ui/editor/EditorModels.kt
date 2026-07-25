package com.example.ui.editor

import androidx.compose.ui.graphics.Color

enum class SplitMode(val label: String) {
    SPLIT_VERTICAL("Split V"),
    SPLIT_HORIZONTAL("Split H"),
    EDITOR_ONLY("Code"),
    PREVIEW_ONLY("Preview")
}

enum class DeviceViewport(val label: String, val widthDp: Int?) {
    FULL("Full", null),
    TABLET("Tablet (768px)", 768),
    MOBILE("Mobile (375px)", 375)
}

data class DevConsoleLog(
    val id: Long = System.currentTimeMillis() + (0..999).random(),
    val level: LogLevel,
    val message: String,
    val timestamp: Long = System.currentTimeMillis()
) {
    enum class LogLevel { LOG, INFO, WARN, ERROR }
}

enum class CodeTheme(val title: String, val bg: Color, val textColor: Color, val lineNumColor: Color, val tagColor: Color, val attrColor: Color, val stringColor: Color) {
    VS_DARK("VS Dark", Color(0xFF1E1E1E), Color(0xFFD4D4D4), Color(0xFF858585), Color(0xFF569CD6), Color(0xFF9CDCFE), Color(0xFFCE9178)),
    MONOKAI("Monokai", Color(0xFF272822), Color(0xFFF8F8F2), Color(0xFF75715E), Color(0xFFF92672), Color(0xFFA6E22E), Color(0xFFE6DB74)),
    NORD("Nord", Color(0xFF2E3440), Color(0xFFD8DEE9), Color(0xFF4C566A), Color(0xFF81A1C1), Color(0xFF8FBCBB), Color(0xFFA3BE8C)),
    LIGHT_STUDIO("Light Studio", Color(0xFFF8FAFC), Color(0xFF0F172A), Color(0xFF94A3B8), Color(0xFF0284C7), Color(0xFF7C3AED), Color(0xFF16A34A))
}
