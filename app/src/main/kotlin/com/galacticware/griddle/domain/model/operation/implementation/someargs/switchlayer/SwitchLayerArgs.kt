package com.galacticware.griddle.domain.model.operation.implementation.someargs.switchlayer

import com.galacticware.griddle.domain.model.layer.LayerKind
import com.galacticware.griddle.domain.model.operation.base.ParameterizedOperation
import com.galacticware.griddle.domain.model.operation.base.OperationArgs
import kotlinx.serialization.Serializable

@Serializable
data class SwitchLayerArgs(
    val layerName: String? = null,
    val layerKind: LayerKind? = null,
): OperationArgs() {
    private val name : String by lazy { layerName?: layerKind!!.prettyName }
    override fun description(): String = "Switch to layer $name"
    init {
        if(layerName != null && layerKind != null) {
            throw AmbiguousLayerIdentityException(layerName, layerKind)
        }
    }
    override fun opInstance(): ParameterizedOperation<*> = SwitchLayer
}