package com.evolved.automata.app.dynamicnback.model.types

data class Stimulus (val isInactive:Boolean = true, val stimulusId:Int = 0, val cellType: CellType = CellType.COLORS,
                     val inactiveCellValue:String = cellType.inactiveCell,
                     val positionIndex:Int = stimulusId % 9,
                     val col:Int = positionIndex % 3,
                     val row:Int = positionIndex / 3,
                     val cellTypeIndex:Int = stimulusId/(cellType.cellItems.size),
                     val cellValueKey:String = cellType.cellItems[cellTypeIndex]
)