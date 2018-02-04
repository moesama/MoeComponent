package io.github.moesama.moecomponent.library

import android.view.View

/**
 * Created by SuperVC on 2018/2/2.
 * for java use
 */
object ComponentHelper {
    fun <T: Component> getComponentFromView(view: View, clazz: Class<T>): T? = view.getComponent(clazz)
    fun <T: Component> addComponentToView(view: View, clazz: Class<T>): T? = view.addComponent(clazz)
    fun <T: Component> removeComponentFromView(view: View, clazz: Class<T>) {
        view.removeComponent(clazz)
    }
}