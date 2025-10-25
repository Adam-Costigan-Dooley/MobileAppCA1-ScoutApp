package ie.setu.scouting.event
import ie.setu.scouting.adapters.EventAdapter

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ie.setu.scouting.R
import ie.setu.scouting.databinding.ActivityEventListBinding
import ie.setu.scouting.databinding.CardEventBinding
import ie.setu.scouting.main.MainApp
import ie.setu.scouting.models.EventModel
import ie.setu.scouting.adapters.EventListener

class EventListActivity : AppCompatActivity(), EventListener  {

    lateinit var app: MainApp
    private lateinit var binding: ActivityEventListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEventListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.title = title
        setSupportActionBar(binding.toolbar)

        app = application as MainApp

        val layoutManager = LinearLayoutManager(this)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = EventAdapter(app.events.findAll(), this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_add -> {
                val launcherIntent = Intent(this, EventActivity::class.java)
                getResult.launch(launcherIntent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private val getResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                binding.recyclerView.adapter?.notifyDataSetChanged()
            } ///Explanation beside editresult
        }

    override fun onEventClick(event: EventModel) {
        val intent = Intent(this, EventActivity::class.java)
        intent.putExtra("event_edit", event)
        editResult.launch(intent)
    }
    private val editResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                binding.recyclerView.adapter?.notifyDataSetChanged()
            }
        ///The solution to the delete button not working was given by chatgpt after troubleshooting /
            ///Refreshs the recylerview as the the event list was not updated visibly,
            /// notifyDataSetChanged() makes the adapter rebind all visible list items to fix it
        }
}
