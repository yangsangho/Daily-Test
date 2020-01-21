package kr.yangbob.memorization.testmanager

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kr.yangbob.memorization.model.MemRepository
import org.koin.core.context.GlobalContext.get

const val TEST_CHK_RECV_ID = 10

class TestChkReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // 명시적 인텐트로 실행해서 action 체크 없이
        val memRepo = get().koin.get<MemRepository>()

    }
}
