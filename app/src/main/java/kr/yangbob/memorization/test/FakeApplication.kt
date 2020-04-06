package kr.yangbob.memorization.test

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.google.android.gms.ads.MobileAds
import kr.yangbob.memorization.NOTIFICATION_CHANNEL_ID
import kr.yangbob.memorization.NOTIFICATION_CHANNEL_NAME
import kr.yangbob.memorization.testDbModule
import kr.yangbob.memorization.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class FakeApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@FakeApplication)
            androidLogger(Level.INFO)
            modules( listOf(viewModelModule, testDbModule) )
        }

        MobileAds.initialize(this) {}

        // O 버전부터 NotificationChannel이 필요하다네
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }
}