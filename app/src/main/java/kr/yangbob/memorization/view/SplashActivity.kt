package kr.yangbob.memorization.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import kr.yangbob.memorization.BuildConfig
import kr.yangbob.memorization.IN_APP_UPDATE_RECV_ID
import kr.yangbob.memorization.R

class SplashActivity : AppCompatActivity() {
    private val appUpdateManager: AppUpdateManager by lazy {
        AppUpdateManagerFactory.create(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(BuildConfig.DEBUG){
            Toast.makeText(this, "DEBUG MODE", Toast.LENGTH_LONG).show()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        } else {
            appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
                if(appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                        && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)){
                    appUpdateManager.startUpdateFlowForResult(
                            appUpdateInfo,
                            AppUpdateType.IMMEDIATE,
                            this,
                            IN_APP_UPDATE_RECV_ID
                    )
                } else {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == IN_APP_UPDATE_RECV_ID){
            if(resultCode != RESULT_OK){
                Toast.makeText(this, R.string.update_cancel, Toast.LENGTH_SHORT).show()
//                (-1)RESULT_OK: 사용자가 업데이트를 수락했습니다. 즉시 업데이트인 경우 앱에 업데이트 제어 권한이 주어졌을 때는 이미 Google Play가 업데이트를 완료한 상태여야 하기 때문에 개발자는 이 콜백을 수신하지 못할 수 있습니다.
//                (0)RESULT_CANCELED: 사용자가 업데이트를 거부하거나 취소했습니다.
//                (1)ActivityResult.RESULT_IN_APP_UPDATE_FAILED: 기타 오류로 인해 사용자가 동의하지 못했거나 업데이트가 진행되지 못했습니다.
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                        AppUpdateType.IMMEDIATE,
                        this,
                        IN_APP_UPDATE_RECV_ID
                )
            }
        }
    }
}
