package ie.setu.scouting.models

import timber.log.Timber.i

var lastId = 0L
internal fun getId(): Long = lastId++

class EventMemStore : EventStore {

    private val events = ArrayList<EventModel>()

    override fun findAll(): List<EventModel> = events

    override fun create(event: EventModel) {
        event.id = getId()
        events.add(event)
        logAll()
    }

    override fun update(event: EventModel) {
        val found = events.find { it.id == event.id }
        if (found != null) {
            found.title = event.title
            found.description = event.description
            found.leadersNeeded = event.leadersNeeded
            found.parentVolunteersAllowed = event.parentVolunteersAllowed
            found.date = event.date
            logAll()
        }
    }
    override fun delete(event: EventModel) {
        events.removeIf { it.id == event.id }
        logAll()
    }
    private fun logAll() {
        events.forEach { i("$it") }
    }
}
