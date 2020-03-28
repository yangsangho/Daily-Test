package kr.yangbob.memorization.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        if(checkInternet()){
//        } else {
//        }
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

//    private fun checkInternet(): Boolean{
//        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            val nw = cm.activeNetwork ?: return false
//            val actNw = cm.getNetworkCapabilities(nw) ?: return false
//            return when {
//                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
//                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
//                else -> false
//            }
//        } else {
//            val nwInfo = cm.activeNetworkInfo ?: return false
//            return nwInfo.isConnected
//        }
//    }
}
