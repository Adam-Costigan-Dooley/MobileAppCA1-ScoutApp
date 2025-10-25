package ie.setu.scouting.models

data class EventModel(
    var title: String = "",
    var description: String = "",
    var leadersNeeded: Int = 0,
    var parentVolunteersAllowed: Boolean = false
)
