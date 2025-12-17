package ie.setu.scouting.views.eventlist

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import ie.setu.scouting.R
import ie.setu.scouting.adapters.EventAdapter
import ie.setu.scouting.adapters.EventListener
import ie.setu.scouting.databinding.ActivityEventListBinding
import ie.setu.scouting.models.EventModel
import timber.log.Timber

class EventListView : AppCompatActivity(), EventListener {

    private lateinit var binding: ActivityEventListBinding
    private lateinit var presenter: EventListPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEventListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.title = title
        setSupportActionBar(binding.toolbar)

        presenter = EventListPresenter(this)

        setupRecyclerView()
        
        Timber.i("EventListView started")
    }

    private fun setupRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = EventAdapter(presenter.getEvents(), this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_add -> {
                presenter.doAddEvent()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onEventClick(event: EventModel) {
        presenter.doEditEvent(event)
    }

    fun refreshEventList() {
        binding.recyclerView.adapter?.notifyDataSetChanged()
        Timber.i("Event list refreshed: ${presenter.getEvents().size} events")
    }
}
