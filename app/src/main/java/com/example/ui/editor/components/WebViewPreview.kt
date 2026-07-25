package com.example.ui.editor.components

import android.content.Context
import android.graphics.Bitmap
import android.webkit.ConsoleMessage
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.ui.editor.DevConsoleLog
import com.example.ui.editor.DeviceViewport

@Composable
fun WebViewPreview(
    codeContent: String,
    deviceViewport: DeviceViewport,
    autoInjectTailwind: Boolean,
    onConsoleLog: (DevConsoleLog) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val webView = remember {
        createCustomWebView(context, onConsoleLog)
    }

    DisposableEffect(Unit) {
        onDispose {
            webView.destroy()
        }
    }

    val preparedHtml = remember(codeContent, autoInjectTailwind) {
        prepareHtmlWithTailwind(codeContent, autoInjectTailwind)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        contentAlignment = Alignment.TopCenter
    ) {
        val viewportModifier = when (deviceViewport) {
            DeviceViewport.MOBILE -> Modifier
                .width(375.dp)
                .padding(vertical = 12.dp)
                .clip(RoundedCornerShape(16.dp))
                .border(2.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
            DeviceViewport.TABLET -> Modifier
                .width(600.dp)
                .padding(vertical = 8.dp)
                .clip(RoundedCornerShape(12.dp))
                .border(2.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            DeviceViewport.FULL -> Modifier.fillMaxSize()
        }

        AndroidView(
            factory = { webView },
            update = { view ->
                view.loadDataWithBaseURL(
                    "https://offline.local/",
                    preparedHtml,
                    "text/html",
                    "UTF-8",
                    null
                )
            },
            modifier = viewportModifier
                .testTag("webview_preview")
        )
    }
}

private fun createCustomWebView(
    context: Context,
    onConsoleLog: (DevConsoleLog) -> Unit
): WebView {
    return WebView(context).apply {
        settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            allowFileAccess = true
            allowContentAccess = true
            loadWithOverviewMode = true
            useWideViewPort = true
            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }

        webChromeClient = object : WebChromeClient() {
            override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
                consoleMessage?.let { msg ->
                    val level = when (msg.messageLevel()) {
                        ConsoleMessage.MessageLevel.ERROR -> DevConsoleLog.LogLevel.ERROR
                        ConsoleMessage.MessageLevel.WARNING -> DevConsoleLog.LogLevel.WARN
                        ConsoleMessage.MessageLevel.TIP -> DevConsoleLog.LogLevel.INFO
                        else -> DevConsoleLog.LogLevel.LOG
                    }
                    val logText = "${msg.message()} (line ${msg.lineNumber()})"
                    onConsoleLog(DevConsoleLog(level = level, message = logText))
                }
                return true
            }
        }

        webViewClient = object : WebViewClient() {
            override fun shouldInterceptRequest(
                view: WebView?,
                request: WebResourceRequest?
            ): WebResourceResponse? {
                val url = request?.url?.toString() ?: ""
                
                // Intercept Tailwind CDN and load offline bundled script!
                if (url.contains("cdn.tailwindcss.com")) {
                    return try {
                        val inputStream = context.assets.open("tailwind.cdn.js")
                        WebResourceResponse("application/javascript", "UTF-8", inputStream)
                    } catch (e: Exception) {
                        super.shouldInterceptRequest(view, request)
                    }
                }

                // Intercept tailwind default CSS
                if (url.contains("tailwind_default.css")) {
                    return try {
                        val inputStream = context.assets.open("tailwind_default.css")
                        WebResourceResponse("text/css", "UTF-8", inputStream)
                    } catch (e: Exception) {
                        super.shouldInterceptRequest(view, request)
                    }
                }

                return super.shouldInterceptRequest(view, request)
            }
        }
    }
}

private fun prepareHtmlWithTailwind(rawHtml: String, autoInject: Boolean): String {
    if (!autoInject) return rawHtml

    val hasTailwindScript = rawHtml.contains("cdn.tailwindcss.com")
    val hasTailwindCSS = rawHtml.contains("tailwind_default.css")

    if (hasTailwindScript || hasTailwindCSS) return rawHtml

    val injectScript = """
        <script src="https://cdn.tailwindcss.com"></script>
        <link rel="stylesheet" href="https://offline.local/tailwind_default.css">
    """.trimIndent()

    return if (rawHtml.contains("<head>")) {
        rawHtml.replace("<head>", "<head>\n$injectScript")
    } else if (rawHtml.contains("<html>")) {
        rawHtml.replace("<html>", "<html>\n<head>\n$injectScript\n</head>")
    } else {
        "$injectScript\n$rawHtml"
    }
}
