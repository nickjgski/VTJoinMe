package com.nickjgski.vtjoinme.ui.list

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.firebase.ui.firestore.SnapshotParser
import com.google.firebase.FirebaseOptions
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.nickjgski.vtjoinme.Pin
import com.nickjgski.vtjoinme.R


class ListFragment : Fragment() {

    val mFirestore = FirebaseFirestore.getInstance()
    var mQuery: Query = mFirestore.collection("pins").orderBy("expTime", Query.Direction.ASCENDING)

    private var defaultOpt: FirestoreRecyclerOptions<Pin> = FirestoreRecyclerOptions.Builder<Pin>()
        .setQuery(mQuery) { snapshot: DocumentSnapshot ->
            var pin = snapshot.toObject(Pin::class.java)
            pin!!.uid = snapshot.id
            pin
        }
        .build()

    var adapter: PinAdapter = PinAdapter(defaultOpt)

    private lateinit var pinList: RecyclerView
    private lateinit var buildingSpinner: Spinner
    private lateinit var actSpinner: Spinner
    private lateinit var envSpinner: Spinner

    private var selectedBuilding = "Building"
    private var selectedAct = "Activity"
    private var selectedEnv = "Environment"

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_list, container, false)
        pinList = view.findViewById(R.id.pinlist)
        pinList.layoutManager = LinearLayoutManager(context)
        pinList.adapter = adapter

        initSpinners(view)
        filterOptions()

        return view
    }

    private fun initSpinners(view: View) {

        buildingSpinner = view.findViewById(R.id.building_filter)
        ArrayAdapter.createFromResource(
            view.context,
            R.array.buildings,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            buildingSpinner.adapter = adapter
        }
        buildingSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                selectedBuilding = parent?.getItemAtPosition(pos).toString()
                filterOptions()
            }
        }

        actSpinner = view.findViewById(R.id.activity_filter)
        ArrayAdapter.createFromResource(
            view.context,
            R.array.activities,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            actSpinner.adapter = adapter
        }
        actSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                selectedAct = parent?.getItemAtPosition(pos).toString()
                filterOptions()
            }
        }

        envSpinner = view.findViewById(R.id.env_filter)
        ArrayAdapter.createFromResource(
            view.context,
            R.array.envs,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            envSpinner.adapter = adapter
        }
        envSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                selectedEnv = parent?.getItemAtPosition(pos).toString()
                filterOptions()
            }
        }

    }

    private fun filterOptions() {
        Log.d("Selections", "$selectedBuilding $selectedAct $selectedEnv")
        var query = mFirestore.collection("pins").orderBy("expTime", Query.Direction.ASCENDING)
        if(selectedBuilding != "Building" && selectedAct != "Activity" && selectedEnv != "Environment") {
            Log.d("Selections", "Path 1")
            val act = selectedAct == "Study"
            val env = selectedEnv == "Quiet"
            query = mFirestore.collection("pins")
                .whereEqualTo("building", selectedBuilding)
                .whereEqualTo("study", act)
                .whereEqualTo("quiet", env)
                .orderBy("expTime", Query.Direction.ASCENDING)
        } else if(selectedBuilding != "Building" && selectedAct != "Activity") {
            Log.d("Selections", "Path 2")
            val act = selectedAct == "Study"
            query = mFirestore.collection("pins")
                .whereEqualTo("building", selectedBuilding)
                .whereEqualTo("study", act)
                .orderBy("expTime", Query.Direction.ASCENDING)
        } else if(selectedBuilding != "Building" && selectedEnv != "Environment") {
            Log.d("Selections", "Path 3")
            val env = selectedAct == "Study"
            query = mFirestore.collection("pins")
                .whereEqualTo("building", selectedBuilding)
                .whereEqualTo("quiet", env)
                .orderBy("expTime", Query.Direction.ASCENDING)
        } else if(selectedAct != "Activity" && selectedEnv != "Environment") {
            Log.d("Selections", "Path 4")
            val act = selectedAct == "Study"
            val env = selectedEnv == "Quiet"
            query = mFirestore.collection("pins")
                .whereEqualTo("study", act)
                .whereEqualTo("quiet", env)
                .orderBy("expTime", Query.Direction.ASCENDING)
        } else if(selectedBuilding != "Building") {
            Log.d("Selections", "Path 5")
            query = mFirestore.collection("pins")
                .whereEqualTo("building", selectedBuilding)
                .orderBy("expTime", Query.Direction.ASCENDING)
        } else if(selectedAct != "Activity") {
            Log.d("Selections", "Path 6")
            val act = selectedAct == "Study"
            query = mFirestore.collection("pins")
                .whereEqualTo("study", act)
                .orderBy("expTime", Query.Direction.ASCENDING)
        } else if(selectedEnv != "Environment") {
            Log.d("Selections", "Path 7")
            val env = selectedEnv == "Quiet"
            query = mFirestore.collection("pins")
                .whereEqualTo("quiet", env)
                .orderBy("expTime", Query.Direction.ASCENDING)
        }
        var options: FirestoreRecyclerOptions<Pin> = FirestoreRecyclerOptions.Builder<Pin>()
            .setQuery(query) { snapshot: DocumentSnapshot ->
                var pin = snapshot.toObject(Pin::class.java)
                pin!!.uid = snapshot.id
                pin
            }
            .build()
        adapter = PinAdapter(options)
        adapter.startListening()
        pinList.adapter = adapter
        adapter.notifyDataSetChanged()
    }

    inner class PinAdapter(options: FirestoreRecyclerOptions<Pin>): FirestoreRecyclerAdapter<Pin, PinAdapter.PinViewHolder>(options) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PinViewHolder {
            val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.card_view, parent, false)
            return PinViewHolder(v)
        }

        override fun onBindViewHolder(holder: PinViewHolder, position: Int, pin: Pin) {
            var use = if(pin.study) {
                "Study"
            } else {
                "Eat"
            }
            holder.view.findViewById<TextView>(R.id.title).text = "${pin.building}, $use"
            holder.view.findViewById<TextView>(R.id.seats).text = "${pin.availSeats}/${pin.totalSeats}"
            holder.view.findViewById<TextView>(R.id.extra_info).text = pin.desc
            holder.view.setOnClickListener {
                findNavController().navigate(R.id.nav_detail, bundleOf("id" to pin.uid))
            }
        }

        inner class PinViewHolder(val view: View): RecyclerView.ViewHolder(view),View.OnClickListener {
            override fun onClick(view: View?) {

            }

        }

    }
}