package ie.setu.scouting.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ie.setu.scouting.R
import ie.setu.scouting.databinding.CardEventBinding
import ie.setu.scouting.models.EventModel

interface EventListener {
    fun onEventClick(event: EventModel)
}

class EventAdapter(private var events: List<EventModel>,
                   private val listener: EventListener) :
    RecyclerView.Adapter<EventAdapter.MainHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        val binding = CardEventBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MainHolder(binding)
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val event = events[holder.adapterPosition]
        holder.bind(event, listener)
    }

    override fun getItemCount(): Int = events.size

    class MainHolder(private val binding: CardEventBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(event: EventModel, listener: EventListener) {
            binding.eventTitle.text = event.title
            binding.description.text = event.description
            binding.leadersNeeded.text = binding.root.context.getString(
                R.string.label_leaders_needed, event.leadersNeeded
            )
            val parentRequested = if (event.parentVolunteersAllowed) "Yes" else "No"
            binding.parentAllowed.text = binding.root.context.getString(
                R.string.label_parent_volunteers, parentRequested
            )
            binding.eventDate.text = binding.root.context.getString(
                R.string.label_event_date,
                event.date.ifEmpty { "—" }
            )
            binding.root.setOnClickListener { listener.onEventClick(event) }
        } /// The Parentallowed binding was generated with ChatGPT to provide a number and yes/no feature to the app, as i ran into an unexpected issue with implementing it originally.
    }     /// While ChatGPT was not used for the eventDate, it the previous chatgpt parent did serve as the basis.
}
