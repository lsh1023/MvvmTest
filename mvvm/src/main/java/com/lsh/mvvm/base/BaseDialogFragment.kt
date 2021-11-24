package com.lsh.mvvm.base

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.DialogFragment.STYLE_NORMAL
import androidx.viewbinding.ViewBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel

/**
 *
 * @Description:
 * @CreateDate:     2021/11/24 10:45
 * @Version:        1.0
 */
abstract class BaseDialogFragment<VB : ViewBinding>(private val showGravity: Int = Gravity.BOTTOM) :
    DialogFragment(), CoroutineScope by MainScope() {

    protected val mViewBinding: VB by lazy {
        getViewBinding()
    }

    abstract fun getViewBinding(): VB

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setStyle(STYLE_NORMAL, android.R.style.Theme_Material_Dialog)
        return mViewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        work(view, savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        val window=dialog?.window
        window?.let {
            it.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val displayMetrics = DisplayMetrics()
            activity?.windowManager?.defaultDisplay?.getMetrics(displayMetrics)
            val params = it.attributes
            params.run {
                gravity = gravity
                width = ViewGroup.LayoutParams.MATCH_PARENT
                height = ViewGroup.LayoutParams.WRAP_CONTENT
            }
            it.attributes = params
        }
    }

    abstract fun work(view: View, savedInstanceState: Bundle?)


    override fun onDestroy() {
        super.onDestroy()
        cancel()
    }

}