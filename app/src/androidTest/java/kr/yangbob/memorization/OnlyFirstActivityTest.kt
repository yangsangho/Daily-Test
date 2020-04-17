package kr.yangbob.memorization

import android.app.Activity
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry
import androidx.test.runner.lifecycle.Stage
import kr.yangbob.memorization.view.MainActivity
import kr.yangbob.memorization.view.OnlyFirstActivity
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.test.KoinTest
import org.koin.test.get

@RunWith(AndroidJUnit4::class)
@LargeTest
class OnlyFirstActivityTest : KoinTest {
    @get:Rule
    var activityRule: ActivityTestRule<MainActivity>
            = ActivityTestRule(MainActivity::class.java, true, false)

    @Before
    fun before(){
        val settings: SharedPreferences = get()
        settings.edit().also {
            it.putBoolean(SETTING_IS_FIRST_MAIN, true)
        }.apply()
        activityRule.launchActivity(null)
        val curActivity = getCurrentActivity()
        if(isLandScape) {
            curActivity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            activityRule.activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }
    }

    @Test
    fun basicTest() {
        var curActivity = getCurrentActivity()
        assertThat(curActivity, instanceOf(OnlyFirstActivity::class.java))

        val tvSkip = onView(withId(R.id.tv_only_first_skip))
        val tvNextOrStart = onView(withId(R.id.tv_only_first_next_or_start))
        val ivPager = onView((withId(R.id.iv_only_first_pager)))
        val tvPagerMain = onView(withId(R.id.tv_only_first_pager_main))
        val tvPagerSub = onView(withId(R.id.tv_only_first_pager_sub))
        val tvPagerAdditional = onView(withId(R.id.tv_only_first_pager_additional))
        val ivIndex1 = onView(withId(R.id.iv_only_first_index_1))
        val ivIndex2 = onView(withId(R.id.iv_only_first_index_2))
        val ivIndex3 = onView(withId(R.id.iv_only_first_index_3))
        val ivIndex4 = onView(withId(R.id.iv_only_first_index_4))

        // 1 페이지
        tvSkip.check(matches(allOf(isDisplayed(), withText(R.string.skip))))
        tvNextOrStart.check(matches(allOf(isDisplayed(), withText(R.string.next))))
        ivPager.check(matches(allOf(isDisplayed(), withTagValue(`is`(R.drawable.ic_start_test)))))
        tvPagerMain.check(matches(withText(R.string.only_first_pager_main1)))
        tvPagerSub.check(matches(withText(R.string.only_first_pager_sub1)))
        tvPagerAdditional.check(matches(isDisplayed()))
        ivIndex1.check(matches(withTagValue(`is`(true))))
        ivIndex2.check(matches(withTagValue(`is`(false))))
        ivIndex3.check(matches(withTagValue(`is`(false))))
        ivIndex4.check(matches(withTagValue(`is`(false))))

        // 2 페이지
        tvNextOrStart.perform(click())
        tvSkip.check(matches(allOf(isDisplayed(), withText(R.string.skip))))
        tvNextOrStart.check(matches(allOf(isDisplayed(), withText(R.string.next))))
        ivPager.check(matches(allOf(isDisplayed(), withTagValue(`is`(R.drawable.ic_start_meta)))))
        tvPagerMain.check(matches(withText(R.string.only_first_pager_main2)))
        tvPagerSub.check(matches(withText(R.string.only_first_pager_sub2)))
        tvPagerAdditional.check(matches(withEffectiveVisibility(Visibility.GONE)))
        ivIndex1.check(matches(withTagValue(`is`(false))))
        ivIndex2.check(matches(withTagValue(`is`(true))))
        ivIndex3.check(matches(withTagValue(`is`(false))))
        ivIndex4.check(matches(withTagValue(`is`(false))))

        // 3 페이지
        tvNextOrStart.perform(click())
        tvSkip.check(matches(allOf(isDisplayed(), withText(R.string.skip))))
        tvNextOrStart.check(matches(allOf(isDisplayed(), withText(R.string.next))))
        ivPager.check(matches(allOf(isDisplayed(), withTagValue(`is`(R.drawable.ic_start_output)))))
        tvPagerMain.check(matches(withText(R.string.only_first_pager_main3)))
        tvPagerSub.check(matches(withText(R.string.only_first_pager_sub3)))
        tvPagerAdditional.check(matches(withEffectiveVisibility(Visibility.GONE)))
        ivIndex1.check(matches(withTagValue(`is`(false))))
        ivIndex2.check(matches(withTagValue(`is`(false))))
        ivIndex3.check(matches(withTagValue(`is`(true))))
        ivIndex4.check(matches(withTagValue(`is`(false))))

        // 4 페이지
        tvNextOrStart.perform(click())
        tvSkip.check(matches(withEffectiveVisibility(Visibility.GONE)))
        tvNextOrStart.check(matches(allOf(isDisplayed(), withText(R.string.start))))
        ivPager.check(matches(allOf(isDisplayed(), withTagValue(`is`(R.drawable.ic_start_memory)))))
        tvPagerMain.check(matches(withText(R.string.only_first_pager_main4)))
        tvPagerSub.check(matches(withText(R.string.only_first_pager_sub4)))
        tvPagerAdditional.check(matches(withEffectiveVisibility(Visibility.GONE)))
        ivIndex1.check(matches(withTagValue(`is`(false))))
        ivIndex2.check(matches(withTagValue(`is`(false))))
        ivIndex3.check(matches(withTagValue(`is`(false))))
        ivIndex4.check(matches(withTagValue(`is`(true))))

        // 종료
        tvNextOrStart.perform(click())
        curActivity = getCurrentActivity()
        assertThat(curActivity, instanceOf(MainActivity::class.java))
    }

    @Test
    fun skipTest1(){
        onView(withId(R.id.tv_only_first_skip)).perform(click())
        val curActivity = getCurrentActivity()
        assertThat(curActivity, instanceOf(MainActivity::class.java))
    }

    @Test
    fun skipTest2(){
        onView(withId(R.id.tv_only_first_next_or_start)).perform(click())

        onView(withId(R.id.tv_only_first_skip)).perform(click())
        val curActivity = getCurrentActivity()
        assertThat(curActivity, instanceOf(MainActivity::class.java))
    }

    @Test
    fun skipTest3(){
        val tvNext = onView(withId(R.id.tv_only_first_next_or_start))
        tvNext.perform(click())
        tvNext.perform(click())

        onView(withId(R.id.tv_only_first_skip)).perform(click())
        val curActivity = getCurrentActivity()
        assertThat(curActivity, instanceOf(MainActivity::class.java))
    }

    private fun getCurrentActivity(): Activity? {
        var currentActivity: Activity? = null
        getInstrumentation().runOnMainSync { run {
            currentActivity = ActivityLifecycleMonitorRegistry.getInstance()
                    .getActivitiesInStage(Stage.RESUMED).elementAtOrNull(0) }
        }
        return currentActivity
    }
}
