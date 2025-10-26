package ie.setu.scouting.models

import timber.log.Timber.i
import ie.setu.scouting.helpers.FileHelper
import ie.setu.scouting.main.MainApp
import android.content.Context
var lastId = 0L
internal fun getId(): Long = lastId++

class EventMemStore(private val context: Context) : EventStore {

    private val fileName = "events.txt"
    private val events = ArrayList<EventModel>()

    override fun findAll(): List<EventModel> = events

    override fun create(event: EventModel) {
        event.id = getId()
        events.add(event)
        serialize()
    }

    override fun update(event: EventModel) {
        val found = events.find { it.id == event.id }
        if (found != null) {
            found.title = event.title
            found.description = event.description
            found.leadersNeeded = event.leadersNeeded
            found.parentVolunteersAllowed = event.parentVolunteersAllowed
            found.date = event.date
            serialize()
        }
    }
    override fun delete(event: EventModel) {
        events.removeIf { it.id == event.id }
        serialize()
    }
    private fun serialize() {
        val builder = StringBuilder()
        events.forEach {
            builder.append("${it.id}|${it.title}|${it.description}|${it.leadersNeeded}|${it.parentVolunteersAllowed}|${it.date}\n")
        }
        FileHelper.write(context, fileName, builder.toString())
        i("Saved ${events.size} events to file")
    }

    private fun deserialize() {
        val data = FileHelper.read(context, fileName) ?: return
        val lines = data.split("\n").filter { it.isNotEmpty() }
        events.clear()
        for (line in lines) {
            val parts = line.split("|")
            if (parts.size >= 6) {
                events.add(
                    EventModel(
                        id = parts[0].toLong(),
                        title = parts[1],
                        description = parts[2],
                        leadersNeeded = parts[3].toIntOrNull() ?: 0,
                        parentVolunteersAllowed = parts[4].toBoolean(),
                        date = parts[5]
                    )
                )
            }
        }
        i("Loaded ${events.size} events from file")
    }

    init {
        deserialize()
    }
}