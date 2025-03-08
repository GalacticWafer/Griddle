package com.galacticware.applicationsd

import com.galacticware.griddle.domain.model.geometry.AxialParams
import com.galacticware.griddle.domain.model.geometry.CartesianAxis
import com.galacticware.griddle.domain.model.geometry.GridPosition
import com.galacticware.griddle.domain.model.geometry.StartAndSpan
import org.junit.Test
import org.junit.Assert.*

class GridPositionTest {

    @Test
    fun withPosition() {
        // Arrange
        val initialRowStart = 1
        val initialColStart = 2
        val rowSpan = 3
        val colSpan = 4
        val gridPosition = GridPosition(
            AxialParams(CartesianAxis.Y, StartAndSpan(initialRowStart, rowSpan)),
            AxialParams(CartesianAxis.X, StartAndSpan(initialColStart, colSpan))
        )

        // Act
        val newRowStart = 5
        val newColStart = 6
        val newGridPosition = gridPosition.withPosition(newRowStart, newColStart)

        // Assert
        assertEquals(newRowStart, newGridPosition.rowStart)
        assertEquals(newColStart, newGridPosition.colStart)
        assertEquals(rowSpan, newGridPosition.rowSpan)
        assertEquals(colSpan, newGridPosition.colSpan)
    }
}
