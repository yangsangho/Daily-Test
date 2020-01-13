package kr.yangbob.memorization.db

import android.app.Application

enum class Stage(val num: Int)
{
    BEGIN_ONE(1), BEGIN_TWO(2), BEGIN_THREE(3), AFTER_THREE(4),
    AFTER_WEEK(5), AFTER_HALF(6), AFTER_MONTH(7)
}

class MemRepository(application: Application)
{
    private val memDB = MemDatabase.getInstance(application)!!
    private val qna: QnaDao = memDB.getQnaDao()
    private val test: TestDao = memDB.getTestDao()
    private val track: TrackDao = memDB.getTrackDao()



}