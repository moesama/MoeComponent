package io.github.moesama.moecomponent.library

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.HandlerThread
import android.os.Looper
import android.os.SystemClock
import android.support.v7.app.AppCompatActivity
import android.view.ViewGroup

/**
 * Created by SuperVC on 2018/2/2.
 */
@SuppressLint("Registered")
open class ComponentActivity: AppCompatActivity() {
    companion object {
        const val FPS = 60
    }

    private val componentThread = ComponentThread()
    lateinit var componentLooper: Looper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        componentThread.start()
        componentLooper = componentThread.looper
    }

    override fun onDestroy() {
        super.onDestroy()
        componentThread.quitSafely()
    }

    inner class ComponentThread : HandlerThread("component") {
        private var lastTime = SystemClock.currentThreadTimeMillis()
        private val delta = 1000 / FPS

        override fun onLooperPrepared() {
            super.onLooperPrepared()
            val d = SystemClock.currentThreadTimeMillis() - lastTime;
            if (d < delta) {
                Thread.sleep(delta - d)
            }
            findViewById<ViewGroup>(android.R.id.content).getChildAt(0)?.getBaseComponent()?.let {
                if (it.isAlive) {
                    it.onUpdate()
                }
            }
            lastTime = SystemClock.currentThreadTimeMillis()
        }
    }
}