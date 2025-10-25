package ie.setu.scouting.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class EventModel(
    var id: Long = 0,
    var title: String = "",
    var description: String = "",
    var leadersNeeded: Int = 0,
    var parentVolunteersAllowed: Boolean = false
) : Parcelable
