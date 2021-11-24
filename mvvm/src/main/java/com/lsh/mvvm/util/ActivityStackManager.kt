package com.lsh.mvvm.util

import androidx.appcompat.app.AppCompatActivity

/**
 *
 * @Description:
 * @CreateDate:     2021/11/23 20:53
 * @Version:        1.0
 */
object ActivityStackManager {

    private var activityList = mutableListOf<AppCompatActivity>()

    /**
     * 添加一个 AppCompatActivity 实例
     */
    fun addActivity(activity: AppCompatActivity) {
        activityList.add(activity)
    }

    /**
     * 移除指定[移除指定[AppCompatActivity]实例]实例
     */
    fun removeActivity(activity: AppCompatActivity) {
        activityList.remove(activity)
    }

    /**
     * finish 移除所有AppCompatActivity
     */
    fun finishAll() {
        activityList.takeIf {
            it.isEmpty()
        }?.let {
            showInfo(msg = "finishAll activity List is empty")
        }
        activityList.forEach {
            if (!it.isFinishing && it.isDestroyed) {
                it.finish()
            }
        }
    }


}