package com.nickjgski.vtjoinme


import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.navigation.fragment.findNavController
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import java.util.*
import kotlin.collections.HashMap

/**
 * A simple [Fragment] subclass.
 */
class AddFragment : Fragment() {
    
    private lateinit var buildingSpinner: Spinner
    private lateinit var availSeatsText: EditText
    private lateinit var totalSeatsText: EditText
    private lateinit var hourSpinner: Spinner
    private lateinit var minuteSpinner: Spinner
    private lateinit var descText: EditText
    private var selectedBuilding: String = ""
    private var availSeats: Int = 0
    private var totalSeats: Int = 0
    private var hours: Int = 0
    private var minutes: Int = 0
    private var study: Boolean = false
    private var quiet: Boolean = false
    private var desc: String = ""

    private val mFirestore = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add, container, false)

        initSpinners(view)
        initEditText(view)

        val activityGroup: RadioGroup = view.findViewById(R.id.activtiy_group)
        activityGroup.setOnCheckedChangeListener { _, id ->
            when(id) {
                R.id.study_button -> study = true
                R.id.dining_button -> study = false
            }
        }

        val envGroup: RadioGroup = view.findViewById(R.id.env_group)
        envGroup.setOnCheckedChangeListener { _, id ->
            when(id) {
                R.id.quiet_button -> quiet = true
                R.id.casual_button -> quiet = false
            }
        }

        view.findViewById<Button>(R.id.table_add_button).setOnClickListener {
            addPin()
        }

        return view
    }

    private fun addPin() {
        var time: Date = Date()
        var cal: Calendar = Calendar.getInstance()
        cal.time = time
        cal.set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY) + hours)
        cal.set(Calendar.MINUTE, cal.get(Calendar.MINUTE) + minutes)
        time = cal.time
        var location = GeoPoint((activity as MainActivity).latlong!!.latitude, (activity as MainActivity).latlong!!.longitude)
        val data = hashMapOf<String, Any>(
            "building" to selectedBuilding,
            "totalSeats" to totalSeats,
            "availSeats" to availSeats,
            "desc" to desc,
            "expTime" to Timestamp(time),
            "quiet" to quiet,
            "study" to study,
            "latitutde" to location.latitude,
            "longitude" to location.longitude
        )

        mFirestore.collection("pins")
            .add(data)
            .addOnSuccessListener { documentReference ->
                Log.d("Doc added", "DocumentSnapshot written with ID: ${documentReference.id}")
                Toast.makeText(context, "Table added", Toast.LENGTH_LONG).show()
                findNavController().navigate(R.id.nav_map)
            }
            .addOnFailureListener { e ->
                Log.w("Doc added", "Error adding document", e)
                Toast.makeText(context, "Failed to add table", Toast.LENGTH_LONG).show()
            }


    }

    private fun initSpinners(view: View) {

        buildingSpinner = view.findViewById(R.id.building_spinner)
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
            }
        }

        hourSpinner = view.findViewById(R.id.hour_spinner)
        ArrayAdapter.createFromResource(
            view.context,
            R.array.hour_dropdown,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            hourSpinner.adapter = adapter
        }
        hourSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                hours = parent?.getItemAtPosition(pos).toString().toInt()
            }
        }

        minuteSpinner = view.findViewById(R.id.minute_spinner)
        ArrayAdapter.createFromResource(
            view.context,
            R.array.minute_dropdown,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            minuteSpinner.adapter = adapter
        }
        minuteSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                minutes = parent?.getItemAtPosition(pos).toString().toInt()
            }
        }

    }

    private fun initEditText(view: View) {

        availSeatsText = view.findViewById(R.id.avail_seats_enter)
        availSeatsText.addTextChangedListener(object: TextWatcher {
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                availSeats = if(s?.toString() == "") {
                    0
                } else {
                    s?.toString()?.toInt() ?: availSeats
                }
            }
        })

        totalSeatsText = view.findViewById(R.id.total_seats_enter)
        totalSeatsText.addTextChangedListener(object: TextWatcher {
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                totalSeats = if(s?.toString() == "") {
                    0
                } else {
                    s?.toString()?.toInt() ?: totalSeats
                }
            }
        })

        descText = view.findViewById(R.id.desc_enter)
        descText.addTextChangedListener(object: TextWatcher {
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                desc = if(s?.toString() == null) {
                    ""
                } else {
                    s.toString()
                }
            }
        })

    }

}
