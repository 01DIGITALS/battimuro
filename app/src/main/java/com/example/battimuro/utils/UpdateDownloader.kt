package com.example.battimuro.utils

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File

object UpdateDownloader {

    fun downloadAndInstall(context: Context, url: String, fileName: String = "update.apk") {
        val request = DownloadManager.Request(Uri.parse(url))
            .setTitle("Battimuro Update")
            .setDescription("Downloading new version...")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, fileName)
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(true)

        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val downloadId = downloadManager.enqueue(request)

        Toast.makeText(context, "Download avviato...", Toast.LENGTH_SHORT).show()

        // Register Receiver for completion
        val onComplete = object : BroadcastReceiver() {
            override fun onReceive(ctxt: Context, intent: Intent) {
                val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                if (id == downloadId) {
                    installApk(ctxt, fileName)
                    try {
                        ctxt.unregisterReceiver(this)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
        context.registerReceiver(onComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE), Context.RECEIVER_EXPORTED)
    }

    private fun installApk(context: Context, fileName: String) {
        try {
            val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName)
            
            if (file.exists()) {
                val uri = FileProvider.getUriForFile(
                    context,
                    context.packageName + ".provider",
                    file
                )
                
                val intent = Intent(Intent.ACTION_VIEW)
                intent.setDataAndType(uri, "application/vnd.android.package-archive")
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
                context.startActivity(intent)
            } else {
                Toast.makeText(context, "Errore: File non trovato", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Errore installazione: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}
