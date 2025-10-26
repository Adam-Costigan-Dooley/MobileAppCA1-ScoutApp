package ie.setu.scouting.models

import android.content.Context
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import ie.setu.scouting.helpers.FileHelper
import timber.log.Timber.i

private const val JSON_FILE = "events.json"

class EventJSONStore(private val context: Context) : EventStore {

    private val events = ArrayList<EventModel>()

    // Moshi setup
    private val moshi: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val listType = Types.newParameterizedType(List::class.java, EventModel::class.java)
    private val listAdapter = moshi.adapter<List<EventModel>>(listType)

    init {
        deserialize()
        lastId = (events.maxOfOrNull { it.id } ?: -1L) + 1L
    }

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
        if (events.removeIf { it.id == event.id }) {
            serialize()
        }
    }
    private fun serialize() {
        val json = listAdapter.indent("  ").toJson(events)
        FileHelper.write(context, JSON_FILE, json)
        i("Saved ${events.size} events to $JSON_FILE")
    }

    private fun deserialize() {
        val json = FileHelper.read(context, JSON_FILE) ?: return
        val loaded = listAdapter.fromJson(json) ?: emptyList()
        events.clear()
        events.addAll(loaded)
        i("Loaded ${events.size} events from $JSON_FILE")
    }
}
