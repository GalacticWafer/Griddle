package com.galacticware.griddle.domain.model.layer

/**
 * This object helps keep track of any layers being edited by the user.
 */
data class LayerDesignMetadata(
    val designerLayer: LayerDefinable,
    val currentlyEditedLayer: LayerDefinable,
)