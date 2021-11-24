package com.lsh.mvvm.base

import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import androidx.viewbinding.ViewBinding
import com.lsh.mvvm.util.ActivityStackManager
import com.lsh.mvvm.util.showDebug
import com.lsh.mvvm.util.showInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel

/**
 *
 * @Description:
 * @CreateDate:     2021/11/23 20:29
 * @Version:        1.0
 */
abstract class BaseActivity<VB : ViewBinding> : AppCompatActivity(), CoroutineScope by MainScope() {

    companion object {
        private const val PERMISSION_REQUEST_CODE = 0x01
    }

    /**
     * ViewBinding
     */
    protected val mViewBinding: VB by lazy {
        getViewBinding()
    }


    /**
     * RootView
     */
    protected lateinit var mRootView: View

    /**
     * 是否显示toolbar
     */
    private var isShowToolbar = true

    /**
     * 是否显示StatusBar
     */
    private var isShowStatusBar = true


    /**
     * 申请权限时成功和失败的回调
     */
    private var mPermissionRequestSuccess: (() -> Unit)? = null

    private var mPermissionRequestFailed: ((permissions: Array<String>) -> Unit)? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        showDebug("activity create start")
        preCreate()
        super.onCreate(savedInstanceState)
        ActivityStackManager.addActivity(this)
        showOrHindToolBar()
        mRootView = mViewBinding.root
        setContentView(mRootView)
        work(savedInstanceState)
        showDebug(msg = "activity create end")

    }

    abstract fun getViewBinding(): VB

    abstract fun work(savedInstanceState: Bundle?)

    abstract fun preCreate()


    /**
     * 获取VM，只能获取不带参数的VM
     */
    fun <T : ViewModel> getViewModel(clazz: Class<T>): T = ViewModelProviders.of(this).get(clazz)


    fun isShowToolBar(isShow: Boolean) {
        isShowToolbar = isShow
    }


    fun setTransparent() {
        if (!isShowStatusBar) {
            showInfo(msg = "is not show StatusBar ,not Transparent")
            return
        }
        transparentStatusBar()
    }


    private fun showOrHindToolBar() {
        if (!isShowStatusBar) {
            supportActionBar?.hide()
        } else {
            supportActionBar?.show()
        }
    }


    fun showOrHideStatusBar(isShowStatusBar: Boolean) {
        val attributes = window.attributes
        if (!isShowStatusBar) {
            attributes.flags = attributes.flags or WindowManager.LayoutParams.FLAG_FULLSCREEN
            window.attributes = attributes
            window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        } else {
            attributes.flags = attributes.flags and WindowManager.LayoutParams.FLAG_FULLSCREEN.inv()
            window.attributes = attributes
            window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        }
    }

    /**
     * 透明状态栏
     */
    private fun transparentStatusBar() {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        window.navigationBarColor = Color.TRANSPARENT
        window.statusBarColor = Color.TRANSPARENT
        supportActionBar?.hide()
    }

    fun requestPermissions(
        permissions: Array<String>,
        success: () -> Unit,
        failed: (permissions: Array<String>) -> Unit
    ) {
        mPermissionRequestSuccess = success
        mPermissionRequestFailed = failed
        val shouldRequestPermssions = mutableListOf<String>()
        shouldRequestPermssions.addAll(permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        })

        if (shouldRequestPermssions.isEmpty()) (mPermissionRequestSuccess!!)() else ActivityCompat.requestPermissions(
            this,
            shouldRequestPermssions.toTypedArray(),
            PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode== PERMISSION_REQUEST_CODE){
            val deniedPermissions= mutableListOf<String>()
            if (grantResults.isNotEmpty()){
                for (grant in grantResults){
                    if (grant!=PackageManager.PERMISSION_GRANTED){
                        deniedPermissions.add(permissions[grant])
                    }
                }

                if (deniedPermissions.isEmpty()) (mPermissionRequestSuccess!!)() else (mPermissionRequestFailed!!)(
                    deniedPermissions.toTypedArray()
                )
            }
        }
    }


    override fun onDestroy() {
        showDebug(msg = "activity is destroy")
        super.onDestroy()
        // 取消MainScope下的所有的协程
        cancel()
        ActivityStackManager.removeActivity(this)

    }

}