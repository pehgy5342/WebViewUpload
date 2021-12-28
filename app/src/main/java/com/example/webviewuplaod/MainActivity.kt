package com.example.webviewuplaod

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.ProgressBar
import androidx.activity.result.contract.ActivityResultContracts

class MainActivity : AppCompatActivity() {

    private var filePathValueCallback: ValueCallback<Array<Uri>>? = null
    private val toChooseFileLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = if (result.data == null) null else result.data
                intent?.data?.let { uri ->
                    //回撥openFileChooser方法，onReceiveValue傳入一個Uri物件
                    filePathValueCallback?.onReceiveValue(arrayOf(uri))
                }
                filePathValueCallback = null
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val Url = "https://uflight.liontravel.com/refundticket?sYear=2021&sOrdr=25006&memberUID=null&platform=android"

        val webView = findViewById<WebView>(R.id.webView)
        webView.settings.domStorageEnabled = true //支援DOM Storage
        webView.settings.javaScriptEnabled = true //設置webView可以調用javascript代碼
        webView.settings.javaScriptCanOpenWindowsAutomatically = true //設置javascript可以自動彈彈窗
        webView.settings.useWideViewPort = true //自動調整螢幕大小
        webView.settings.databaseEnabled = true //支援database儲存
        webView.settings.loadsImagesAutomatically = true //自動加載圖片
        webView.settings.cacheMode = WebSettings.LOAD_DEFAULT //設定網頁是否啟用cache緩存，default:根據cache-control決定是否從網絡上取數據
        webView.settings.setSupportZoom(false) //支持屏幕缩放
        webView.webChromeClient = ChromeClient()
        webView.loadUrl(Url)
    }

    inner class ChromeClient : WebChromeClient() {
        //載入進度條
        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            super.onProgressChanged(view, newProgress)

            val progress = findViewById<ProgressBar>(R.id.progress_bar)
            progress.progress = newProgress

            if (newProgress == 100){
                progress.visibility = View.GONE
            }
        }
        //開啟文件選擇器
        override fun onShowFileChooser(webView: WebView?, filePathCallback: ValueCallback<Array<Uri>>, fileChooserParams: FileChooserParams?): Boolean {
            openFileChooseImplement(filePathCallback)
            return true
        }
    }
    //開啟相簿選擇圖片上傳方法
    private fun openFileChooseImplement(filePathCallback: ValueCallback<Array<Uri>>) {
        filePathValueCallback = filePathCallback
        val intent = Intent()
            .setType("*/*")
            .setAction(Intent.ACTION_GET_CONTENT)
            .addCategory(Intent.CATEGORY_OPENABLE)
        toChooseFileLauncher.launch(intent)
    }
}