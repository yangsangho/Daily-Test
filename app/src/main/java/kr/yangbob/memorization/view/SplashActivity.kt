package kr.yangbob.memorization.view

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability

class SplashActivity : AppCompatActivity() {

    private val appUpdateManager: AppUpdateManager by lazy {
        AppUpdateManagerFactory.create(this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(checkInternet()){
            Log.d("TEST", "internet open")
            appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
                Log.i("TEST", "Spalsh onCreate : updateAvailability = ${appUpdateInfo.updateAvailability()}")
                if(appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)){
                    appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                        AppUpdateType.IMMEDIATE,
                        this,
                        1234
                    )
                } else {
                    startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                    finish()
                }
            }
        } else {
            Log.d("TEST", "internet close")
            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            finish()
        }
    }

//    UNKNOWN = 0
//    UPDATE_NOT_AVAILABLE = 1
//    UPDATE_AVAILABLE = 2
//    DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS = 3

    override fun onResume() {
        super.onResume()

        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            Log.i("TEST", "Spalsh OnResume : updateAvailability = ${appUpdateInfo.updateAvailability()}")
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    AppUpdateType.IMMEDIATE,
                    this,
                    1234
                )
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 1234){
            if(resultCode != RESULT_OK){
                Log.i("TEST", "Update flow failed! Result code: $resultCode")
//                (-1)RESULT_OK: 사용자가 업데이트를 수락했습니다. 즉시 업데이트인 경우 앱에 업데이트 제어 권한이 주어졌을 때는 이미 Google Play가 업데이트를 완료한 상태여야 하기 때문에 개발자는 이 콜백을 수신하지 못할 수 있습니다.
//                (0)RESULT_CANCELED: 사용자가 업데이트를 거부하거나 취소했습니다.
//                (1)ActivityResult.RESULT_IN_APP_UPDATE_FAILED: 기타 오류로 인해 사용자가 동의하지 못했거나 업데이트가 진행되지 못했습니다.
            } else {
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                finish()
            }
        }
    }
    private fun checkInternet(): Boolean{
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val nw = cm.activeNetwork ?: return false
            val actNw = cm.getNetworkCapabilities(nw) ?: return false
            return when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                else -> false
            }
        } else {
            val nwInfo = cm.activeNetworkInfo ?: return false
            return nwInfo.isConnected
        }
    }
}
