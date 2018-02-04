package io.github.moesama.moecomponent.library

import android.content.Context
import android.os.Handler
import android.view.View

/**
 * Created by SuperVC on 2018/2/2.
 */
@Suppress("UNCHECKED_CAST")
open class Component(private val context: Context) {

    private val handler: Handler = Handler(context.mainLooper)
    private val componentHandler: Handler = Handler((context as ComponentActivity).componentLooper)
    protected var baseComponent: BaseComponent? = null

    @get:Synchronized
    var isAlive = false

    inline fun <reified T: Component> getComponent(): T? = getComponent(T::class.java)

    inline fun <reified T: Component> addComponent(): T? = addComponent(T::class.java)

    inline fun <reified T: Component> removeComponent() {
        removeComponent(T::class.java)
    }

    // for java
    fun <T: Component> getComponent(clazz: Class<T>): T? =
            baseComponent?.components?.get(clazz.name) as T?

    // for java
    fun <T: Component> addComponent(clazz: Class<T>): T? {
        baseComponent?.let {
            if (it.components.contains(clazz.name)) {
                return it.components[clazz.name] as T
            } else {
                try {
                    val t = clazz.getConstructor(Context::class.java).newInstance(context)
                    it.components[clazz.name] = t
                    t.baseComponent = it
                    if (it.isAlive) {
                        run { t.onStart() }
                    }
                    return t
                } catch (e: NoSuchMethodException) {
                    e.printStackTrace()
                } catch (e: SecurityException) {
                    e.printStackTrace()
                }
            }
        }
        return null
    }

    // for java
    fun <T: Component> removeComponent(clazz: Class<T>) {
        baseComponent?.let {
            if (it.components.contains(clazz.name)) {
                val t = it.components[clazz.name]
                it.components.remove(clazz.name)
                t?.let {
                    if (it.isAlive) {
                        run { it.onDestroy() }
                    }
                }
            }
        }
    }

    open fun onStart() {
        isAlive = true
    }
    open fun onUpdate() {}
    open fun onDestroy() {
        isAlive = false
    }

    fun runOnUiThread(block: () -> Unit) {
        handler.post(block)
    }

    fun run(block: () -> Unit) {
        componentHandler.post(block)
    }
}

// for java
fun <T: Component> View.getComponent(clazz: Class<T>): T? = getBaseComponent()?.getComponent(clazz)
fun <T: Component> View.addComponent(clazz: Class<T>): T? = getBaseComponent()?.addComponent(clazz)
fun <T: Component> View.removeComponent(clazz: Class<T>) {
    getBaseComponent()?.removeComponent(clazz)
}

inline fun <reified T: Component> View.getComponent(): T? = getBaseComponent()?.getComponent()
inline fun <reified T: Component> View.addComponent(): T? = getBaseComponent()?.addComponent()
inline fun <reified T: Component> View.removeComponent() {
    getBaseComponent()?.removeComponent<T>()
}
