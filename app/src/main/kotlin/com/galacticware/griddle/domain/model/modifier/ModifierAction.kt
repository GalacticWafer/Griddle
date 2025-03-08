package com.galacticware.griddle.domain.model.modifier

enum class ModifierAction(val description: (ModifierKeyKind, ModifierCycleDirection?) -> String) {
    ONE_SHOT({m, _ -> "Apply a one-shot ${m.prettyName}"}),
    RELEASE({m, _  -> "Release ${m.prettyName}"}),
    CYCLE({m, c  -> "Cycle through ${m.prettyName} $c "}),
    TOGGLE({m, _ -> "Toggle ${m.prettyName}"}),
}