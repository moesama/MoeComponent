package io.github.moesama.moecomponent.library

import android.app.Activity
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.view.ViewGroup
import java.util.*

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
            componentThread?.let {
                it.quitSafely()
                it.onDestroy()
            }
            activity.clearComponentThread()
        }
    }

    private val content: ViewGroup = activity.findViewById(android.R.id.content)
    private val delta = 1000L / FPS

    private lateinit var handler: Handler

    private val timer = Timer()
    private val task: TimerTask = object : TimerTask() {
        override fun run() {
            handler.post {
                content.getChildAt(0)?.getBaseComponent()?.let {
                    if (it.isAlive) {
                        it.onUpdate()
                    }
                }
            }
        }
    }

    init {
        content.setTag(R.id.tag_component_thread, this)
    }

    override fun onLooperPrepared() {
        super.onLooperPrepared()
        handler = Handler(looper)
        timer.schedule(task, 0L, delta)
    }

    fun onDestroy() {
        timer.cancel()
    }
}

fun Activity.getComponentThread(): ComponentThread? = findViewById<ViewGroup>(android.R.id.content)?.getTag(R.id.tag_component_thread) as ComponentThread?

fun Activity.clearComponentThread() {
    findViewById<ViewGroup>(android.R.id.content)?.setTag(R.id.tag_component_thread, null)
}

fun Activity.getComponentLooper(): Looper? = getComponentThread()?.looper