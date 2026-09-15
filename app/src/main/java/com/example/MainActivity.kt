package com.example

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
  private var webView: WebView? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(androidx.compose.ui.graphics.Color.Black),
          contentAlignment = Alignment.Center
        ) {
          ArcadeGameScreen(
            onWebViewCreated = { webView = it }
          )
        }
      }
    }
  }

  override fun onPause() {
    super.onPause()
    webView?.onPause()
    webView?.evaluateJavascript("if (window.game && window.game.state === 'PLAYING') window.game.togglePause();", null)
  }

  override fun onResume() {
    super.onResume()
    webView?.onResume()
  }

  override fun onDestroy() {
    super.onDestroy()
    webView?.destroy()
    webView = null
  }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun ArcadeGameScreen(
  modifier: Modifier = Modifier,
  onWebViewCreated: (WebView) -> Unit = {}
) {
  val context = LocalContext.current
  val webView = remember {
    WebView(context).apply {
      layoutParams = ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT
      )
      setBackgroundColor(Color.BLACK)
      setLayerType(View.LAYER_TYPE_HARDWARE, null)
      isVerticalScrollBarEnabled = false
      isHorizontalScrollBarEnabled = false
      overScrollMode = View.OVER_SCROLL_NEVER

      settings.apply {
        javaScriptEnabled = true
        domStorageEnabled = true
        databaseEnabled = true
        mediaPlaybackRequiresUserGesture = false
        useWideViewPort = true
        loadWithOverviewMode = true
        cacheMode = WebSettings.LOAD_DEFAULT
        setSupportZoom(false)
        builtInZoomControls = false
        displayZoomControls = false
        allowFileAccess = true
        mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
      }

      webViewClient = object : WebViewClient() {
        override fun onRenderProcessGone(view: WebView?, detail: android.webkit.RenderProcessGoneDetail?): Boolean {
          view?.post {
            view.loadUrl("file:///android_asset/index.html")
          }
          return true
        }
      }
      webChromeClient = object : WebChromeClient() {}

      loadUrl("file:///android_asset/index.html")
    }
  }

  DisposableEffect(webView) {
    onWebViewCreated(webView)
    onDispose {
      // Cleaned up in Activity onDestroy
    }
  }

  AndroidView(
    factory = { webView },
    modifier = modifier.fillMaxSize()
  )
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
  Text(text = "Hello $name!", modifier = modifier)
}

