package ie.setu.scouting.models

import android.content.Context
import android.net.Uri
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import ie.setu.scouting.helpers.FileHelper
import timber.log.Timber.i
import com.google.gson.*
import java.lang.reflect.Type

private const val JSON_FILE = "events.json"

class EventJSONStore(private val context: Context) : EventStore {

    private val events = ArrayList<EventModel>()
    private val gson: Gson

    init {
        // Custom Gson with Uri adapter
        val gsonBuilder = GsonBuilder()
        gsonBuilder.registerTypeAdapter(Uri::class.java, UriAdapter())
        gson = gsonBuilder.setPrettyPrinting().create()

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
            found.image = event.image
            found.lat = event.lat
            found.lng = event.lng
            found.zoom = event.zoom
            serialize()
        }
    }

    override fun delete(event: EventModel) {
        if (events.removeIf { it.id == event.id }) {
            serialize()
        }
    }

    private fun serialize() {
        val json = gson.toJson(events)
        FileHelper.write(context, JSON_FILE, json)
        i("Saved ${events.size} events to $JSON_FILE")
    }

    private fun deserialize() {
        val json = FileHelper.read(context, JSON_FILE) ?: return
        val listType = object : com.google.gson.reflect.TypeToken<ArrayList<EventModel>>() {}.type
        val loaded = gson.fromJson<ArrayList<EventModel>>(json, listType) ?: arrayListOf()
        events.clear()
        events.addAll(loaded)
        i("Loaded ${events.size} events from $JSON_FILE")
    }
}

// Custom Gson adapter for Uri
class UriAdapter : JsonSerializer<Uri>, JsonDeserializer<Uri> {
    override fun serialize(src: Uri?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        return JsonPrimitive(src.toString())
    }

    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Uri {
        return Uri.parse(json?.asString)
    }
}
