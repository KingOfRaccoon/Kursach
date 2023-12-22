package data.local

import data.network.TimetableItem
import data.time.DataTime

data class EmptyTime(val startTime: Int, var paddingCoefficient: Double = 1.0) :
    TimetableItem(-1, startTime.toString()) {

    override fun getDataTimeStart(): DataTime {
        return DataTime.now()
    }

    override fun getDataTimeEnd(): DataTime {
        return DataTime.now()
    }
}