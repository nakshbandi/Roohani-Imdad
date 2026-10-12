package com.roohaniimdad.eraseed;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.print.PrintManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        webView = new WebView(this);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        webView.addJavascriptInterface(new AndroidBridge(), "AndroidPrint");

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return openExternalUrl(request.getUrl().toString());
            }

            @SuppressWarnings("deprecation")
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return openExternalUrl(url);
            }
        });

        setContentView(webView);
        webView.loadUrl("file:///android_asset/index.html");
    }

    private boolean openExternalUrl(String url) {
        if (url == null || url.startsWith("file:///android_asset/") || url.startsWith("about:")) {
            return false;
        }
        if (url.startsWith("https://") || url.startsWith("http://")
                || url.startsWith("whatsapp://") || url.startsWith("intent://")) {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            } catch (ActivityNotFoundException e) {
                Toast.makeText(this, "Link kholne ke liye munasib app install nahi hai.",
                        Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Toast.makeText(this, "Link nahi khul saka.", Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        return false;
    }

    private class AndroidBridge {
        @JavascriptInterface
        public void printReceipt() {
            runOnUiThread(() -> {
                if (webView == null) return;
                PrintManager manager = (PrintManager) getSystemService(Context.PRINT_SERVICE);
                if (manager == null) {
                    Toast.makeText(MainActivity.this, "Printing service available nahi hai.",
                            Toast.LENGTH_LONG).show();
                    return;
                }
                try {
                    String jobName = "Roohani Imdad E-Raseed";
                    manager.print(jobName, webView.createPrintDocumentAdapter(jobName),
                            new PrintAttributes.Builder().build());
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "Print start nahi ho saka.",
                            Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        if (webView != null && webView.canGoBack()) webView.goBack();
        else super.onBackPressed();
    }
        }
