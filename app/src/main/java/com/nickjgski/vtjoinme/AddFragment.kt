package com.nickjgski.vtjoinme


import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add, container, false)

        initSpinners(view)
        initEditText(view)

        val activityGroup: RadioGroup = view.findViewById(R.id.activtiy_group)
        activityGroup.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { radioGroup, id ->
            when(id) {
                R.id.study_button -> study = true
                R.id.dining_button -> study = false
            }
        })

        val envGroup: RadioGroup = view.findViewById(R.id.env_group)
        envGroup.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { radioGroup, id ->
            when(id) {
                R.id.quiet_button -> quiet = true
                R.id.casual_button -> quiet = false
            }
        })

        return view
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


    fun onClickEnvButton(view: View) {
        if (view is RadioButton) {
            // Is the button now checked?
            val checked = view.isChecked

            // Check which radio button was clicked
            when (view.getId()) {
                R.id.quiet_button ->
                    if (checked) {
                        quiet = true
                    }
                R.id.casual_button ->
                    if (checked) {
                        quiet = false
                    }
            }
        }
    }

}
