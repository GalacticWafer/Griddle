package com.galacticware.griddle.domain.model.layer

import com.galacticware.griddle.domain.model.keyboard.system.layerkind.AbstractKeyboardLayer

/**
 * Use a provider pattern so that we can potentially change which class is associated with each layer.
 */
abstract class LayerClassProvider(private val f: () -> Class<out AbstractKeyboardLayer>)
    : () -> Class<out AbstractKeyboardLayer>{
    override fun invoke() = f()
    companion object {
        fun create(clazz: Class<out AbstractKeyboardLayer>) = object : LayerClassProvider({ clazz }){}
    }
}