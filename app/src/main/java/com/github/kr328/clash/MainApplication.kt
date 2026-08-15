package com.github.kr328.clash

import android.app.Application
import android.content.Context
import com.github.kr328.clash.common.Global
import com.github.kr328.clash.common.compat.currentProcessName
import com.github.kr328.clash.common.log.Log
import com.github.kr328.clash.remote.Remote
import com.github.kr328.clash.service.util.sendServiceRecreated
import com.github.kr328.clash.util.clashDir
import java.io.File
import java.io.FileOutputStream

@Suppress("unused")
class MainApplication : Application() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)

        Global.init(this)
    }

    override fun onCreate() {
        super.onCreate()

        val processName = currentProcessName
        extractGeoFiles()

        Log.d("Process $processName started")

        if (processName == packageName) {
            Remote.launch()
        } else {
            sendServiceRecreated()
        }
    }

    private fun extractGeoFiles() {
        clashDir.mkdirs()

        // getPackageInfo 可能抛异常（如包未安装），做安全处理
        val updateDate = try {
            packageManager.getPackageInfo(packageName, 0).lastUpdateTime
        } catch (e: Exception) {
            Log.w("Failed to get package update time: ${e.message}")
            0L
        }

        // 逐个提取 geo 文件，单个文件缺失不影响其他文件
        listOf(
            "geoip.metadb" to "geoip.metadb",
            "geosite.dat" to "geosite.dat",
            "ASN.mmdb" to "ASN.mmdb",
            "BundleMRS.7z" to "BundleMRS.7z",
        ).forEach { (assetName, fileName) ->
            val target = File(clashDir, fileName)
            if (target.exists() && updateDate > 0L && target.lastModified() < updateDate) {
                target.delete()
            }
            if (!target.exists()) {
                try {
                    FileOutputStream(target).use {
                        assets.open(assetName).copyTo(it)
                    }
                } catch (e: Exception) {
                    Log.w("Failed to extract $assetName: ${e.message}")
                    target.delete()
                }
            }
        }
    }

    fun finalize() {
        Global.destroy()
    }
}
