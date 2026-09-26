package com.uct.base

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.officialwebsite.R
import com.uct.App
import com.uct.utlis.LanguageUtil
import com.uct.utlis.SpUtil

abstract class BaseActivity : AppCompatActivity() {
    @JvmField
    protected var img_menu: ImageView? = null

    @JvmField
    protected var tv_right: TextView? = null

    @JvmField
    protected var tv_title: TextView? = null

    @JvmField
    protected var self: Context? = null

    protected val menuImage: ImageView
        get() = requireNotNull(img_menu)

    protected val rightText: TextView
        get() = requireNotNull(tv_right)

    protected val titleText: TextView
        get() = requireNotNull(tv_title)

    protected val selfContext: Context
        get() = requireNotNull(self)

    protected abstract fun getRootLayout(): Int

    protected open fun initView() = Unit

    protected open fun initViewListener() = Unit

    protected open fun menu() = Unit

    protected open fun process(bundle: Bundle?) = Unit

    protected open fun right() = Unit

    override fun attachBaseContext(newBase: Context) {
        val language = SpUtil.getInstance(newBase.applicationContext).getString(SpUtil.LANGUAGE)
        Log.e(LOG_TAG, "attachBaseContext: $language")
        super.attachBaseContext(LanguageUtil.attachBaseContext(newBase, language))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getRootLayout())
        supportActionBar?.hide()
        window.setFlags(TRANSLUCENT_FLAG, TRANSLUCENT_FLAG)
        self = this
        tv_title = findViewById(R.id.tv_title)
        img_menu = findViewById(R.id.img_menu)
        tv_right = findViewById(R.id.tv_nearby_bike_order)
        img_menu?.setOnClickListener { menu() }
        tv_right?.setOnClickListener { right() }
        process(savedInstanceState)
        initView()
        initViewListener()
    }

    protected fun <T : View> findView(viewId: Int): T = findViewById(viewId)

    protected fun <T : View> fv(viewId: Int): T = findViewById(viewId)

    fun changeLanguage(language: String, activity: AppCompatActivity) {
        Log.e(LOG_TAG, "changeLanguage: $language")
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            LanguageUtil.changeAppLanguage(App.getContext(), language)
        }
        SpUtil.getInstance(this).putString(SpUtil.LANGUAGE, language)
        startActivity(
            Intent(this, activity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            },
        )
        finish()
    }

    private companion object {
        const val LOG_TAG = "ble"
        const val TRANSLUCENT_FLAG = 67108864
    }
}
