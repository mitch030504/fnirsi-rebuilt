package com.inuker.bluetooth.library.utils

import android.os.AsyncTask
import android.os.Handler
import android.os.Looper
import java.util.concurrent.Executor
import java.util.concurrent.FutureTask

abstract class Task : AsyncTask<Void, Void, Void>() {
    abstract fun doInBackground()

    override fun doInBackground(vararg params: Void?): Void? {
        doInBackground()
        return null
    }

    fun executeDelayed(executor: Executor?, delayMillis: Long) {
        getHandler().postDelayed({
            executeOnExecutor(executor ?: THREAD_POOL_EXECUTOR, *emptyArray())
        }, delayMillis)
    }

    fun execute(executor: Executor?) {
        getHandler().post {
            executeOnExecutor(executor ?: THREAD_POOL_EXECUTOR, *emptyArray())
        }
    }

    companion object {
        @Volatile
        private var handler: Handler? = null

        private fun getHandler(): Handler {
            return handler ?: synchronized(this) {
                handler ?: Handler(Looper.getMainLooper()).also { handler = it }
            }
        }

        @JvmStatic
        fun execute(task: Task?, executor: Executor?) {
            task?.execute(executor)
        }

        @JvmStatic
        fun executeDelayed(task: Task?, executor: Executor?, delayMillis: Long) {
            task?.executeDelayed(executor, delayMillis)
        }

        @JvmStatic
        fun executeDelayed(futureTask: FutureTask<*>?, executor: Executor?, delayMillis: Long) {
            if (futureTask == null || executor == null) {
                return
            }
            getHandler().postDelayed({ executor.execute(futureTask) }, delayMillis)
        }
    }
}
