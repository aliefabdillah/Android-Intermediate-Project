package com.dicoding.mywebview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.JsResult
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val webView =findViewById<WebView>(R.id.webView)
//        webView.loadUrl("https://www.dicoding.com")

        //kode untuk mengaktifkan javascript
        webView.settings.javaScriptEnabled = true

        //menampilkan toast ketika web berhasil dimuat
        /*webView.webViewClient = object : WebViewClient(){
            override fun onPageFinished(view: WebView, url: String) {
                Toast.makeText(this@MainActivity, "Web Dicoding berhasil dimuat", Toast.LENGTH_LONG).show()
            }
        }*/

        //menampilkan alert message menggunakan javascript
        /*
        * Webview dapat menerapkan tindakan seperti persiapan ketika webview dimuat atau ketika
        * selesai dimuat*/
        webView.webViewClient = object : WebViewClient(){
            override fun onPageFinished(view: WebView, url: String) {
                view.loadUrl("javascript:alert('Web Dicoding Berhasil Dibuat')")
            }
        }

        //menampilkan toas menggunakan js
        /*
        * webChromeClient merupakan webView yang mempunya fungsi untuk menampilkan loading, alert,
        * atau perintah" javascript lainnya*/
        webView.webChromeClient = object : WebChromeClient(){
            override fun onJsAlert(
                view: WebView,
                url: String,
                message: String,
                result: JsResult
            ): Boolean {
                Toast.makeText(this@MainActivity, message, Toast.LENGTH_LONG).show()
                result.confirm()
                return true
            }
        }

//        webView.webChromeClient = WebChromeClient()

        //kode ini digunakan untuk memuat sebuah url dari website
        //untuk nougat ke atas harus menggunakan https/http
        webView.loadUrl("https://www.dicoding.com")
    }
}