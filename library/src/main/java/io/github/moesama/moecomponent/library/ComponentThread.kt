package io.github.moesama.moecomponent.library

import android.app.Activity
import android.os.HandlerThread
import android.os.Looper
import android.os.SystemClock
import android.view.ViewGroup

/**
 * Created by SuperVC on 2018/2/4.
 */
class ComponentThread(activity: Activity) : HandlerThread("component") {
    companion object {
        const val FPS = 60

        fun setup(activity: Activity) {
            if (activity.getComponentThread() == null) {
                ComponentThread(activity).start()
            }
        }

        fun destroy(activity: Activity) {
            val componentThread = activity.getComponentThread()
            componentThread?.quitSafely()
            activity.clearComponentThread()
        }
    }

    private val content: ViewGroup = activity.findViewById(android.R.id.content)
    private var lastTime = SystemClock.currentThreadTimeMillis()
    private val delta = 1000 / FPS

    init {
        content.setTag(R.id.tag_component_thread, this)
    }

    override fun onLooperPrepared() {
        super.onLooperPrepared()
        val d = SystemClock.currentThreadTimeMillis() - lastTime
        if (d < delta) {
            Thread.sleep(delta - d)
        }
        content.getChildAt(0)?.getBaseComponent()?.let {
            if (it.isAlive) {
                it.onUpdate()
            }
        }
        lastTime = SystemClock.currentThreadTimeMillis()
    }
}

fun Activity.getComponentThread(): ComponentThread? = findViewById<ViewGroup>(android.R.id.content)?.getTag(R.id.tag_component_thread) as ComponentThread?

fun Activity.clearComponentThread() {
    findViewById<ViewGroup>(android.R.id.content)?.setTag(R.id.tag_component_thread, null)
}

fun Activity.getComponentLooper(): Looper? = getComponentThread()?.looper