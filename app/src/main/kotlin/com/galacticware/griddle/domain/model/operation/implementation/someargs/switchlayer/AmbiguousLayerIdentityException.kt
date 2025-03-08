package com.galacticware.griddle.domain.model.operation.implementation.someargs.switchlayer

import com.galacticware.griddle.domain.model.layer.LayerKind

class AmbiguousLayerIdentityException(layerName: String, layerKind: LayerKind)
    : Exception("Found layerName '$layerName' and layerKind '$layerKind,' but exactly one of them should be null.")