package io.github.moesama.moecomponent.library

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import java.util.concurrent.ConcurrentHashMap

/**
 * Created by SuperVC on 2018/2/2.
 */
class BaseComponent(val view: View) : Component(view.context as Activity), View.OnAttachStateChangeListener {
    companion object {
        fun obtain(view: View): BaseComponent? {
            var baseComponent = view.getTag(R.id.tag_base_component) as BaseComponent?
            if (baseComponent == null && view.context is Activity) {
                baseComponent = BaseComponent(view)
                view.setTag(R.id.tag_base_component, baseComponent)
            }
            return baseComponent
        }
    }

    val components: ConcurrentHashMap<String, Component> = ConcurrentHashMap()

    init {
        baseComponent = this
        view.addOnAttachStateChangeListener(this)
        if (view.isAttachedToWindow) {
            run {
                onStart()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        components.asSequence()
                .filter {
                    it.value != this
                }
                .forEach {
                    it.value.onStart()
                }
    }

    override fun onUpdate() {
        if (view is ViewGroup) {
            view.run {
                val count = childCount
                (0 until count)
                        .map { getChildAt(it) }
                        .forEach { it.getBaseComponent()?.onUpdate() }
            }
        }
        components.asSequence()
                .filter {
                    it.value != this
                }
                .forEach {
                    it.value.onUpdate()
                }
        super.onUpdate()
    }

    override fun onDestroy() {
        components.asSequence()
                .filter {
                    it.value != this
                }
                .forEach {
                    it.value.onDestroy()
                }
        super.onDestroy()
    }

    override fun onViewAttachedToWindow(v: View?) {
        run {
            onStart()
        }
    }

    override fun onViewDetachedFromWindow(v: View?) {
        run {
            onDestroy()
        }
    }
}

fun View.getBaseComponent() = BaseComponent.obtain(this)