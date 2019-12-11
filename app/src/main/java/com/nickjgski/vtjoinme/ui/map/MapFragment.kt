package com.nickjgski.vtjoinme.ui.map

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import com.nickjgski.vtjoinme.Pin
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import com.nickjgski.vtjoinme.R


class MapFragment : Fragment(), OnMapReadyCallback, EventListener<QuerySnapshot>, GoogleMap.OnMarkerClickListener {

    private lateinit var mapView: MapView
    private var gmap: GoogleMap? = null

    private var pins: MutableList<Pin> = mutableListOf()
    private var markers: MutableList<Marker> = mutableListOf()

    private val mFirestore = FirebaseFirestore.getInstance()
    lateinit var mQuery: Query
    private var mRegistration: ListenerRegistration? = null

    private val MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey"

    companion object {
        val format: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_map, container, false)
        var mapViewBundle: Bundle? = null
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY)
        }

        initFirestore()

        mapView = root.findViewById(R.id.mapView)
        mapView.onCreate(mapViewBundle)
        mapView.getMapAsync(this)
        return root
    }

    private fun initFirestore() {
        mQuery = mFirestore.collection("pins")
        mQuery.addSnapshotListener { snapshots, e ->
            if (e != null) {
                return@addSnapshotListener
            }

            for(doc in snapshots!!.documents) {
                pins.add(0, doc.toObject(Pin::class.java)!!)
                pins[0].uid = doc.id
            }
            addPinsToMap()
        }
    }

    override fun onEvent(snapshots: QuerySnapshot?, e: FirebaseFirestoreException?) {
        if(e != null) {
            Log.w("MapFragment", "listen:error", e)
        }

        /*
        for(doc in snapshots!!.documents) {
            pins.add(0, doc.toObject(Pin::class.java)!!)
            pins[0].uid = doc.id

            pins.add(Pin(doc.id, map?.get("building").toString(),
                map?.get("totalSeats").toString().toInt(),
                map?.get("availSeats").toString().toInt(),
                map?.get("expTime") as Date,
                map?.get("study").toString().toBoolean(),
                map?.get("quiet").toString().toBoolean(),
                map?.get("desc").toString(),
                map?.get("public").toString().toBoolean(),
                map?.get("latlong") as GeoPoint
            ))

        }
        */

        for (dc in snapshots!!.documentChanges) {
            when (dc.type) {
                DocumentChange.Type.ADDED -> onDocumentAdded(dc)
                DocumentChange.Type.MODIFIED -> onDocumentModified(dc)
                DocumentChange.Type.REMOVED -> onDocumentDeleted(dc)
            }
        }
    }

    private fun addPinsToMap() {
        deleteExpired()
        Log.d("Add Markers", "Pins: $pins")
        pins.forEach {
            var use: String = if(it.study) {
                "Study"
            } else {
                "Eat"
            }
            var marker = gmap?.addMarker(MarkerOptions()
                    .position(LatLng(it.latitude, it.longitude))
                    .title("${it.building}, $use"))
            if(marker != null) {
                markers.add(0, marker)
                markers[0].tag = it.uid
            }

        }
    }

    private fun deleteExpired() {
        var removedPins = mutableListOf<Pin>()
        Log.d("Remove Markers", "Markers: $markers")
        pins.forEach {
            if(it.expTime < Date()) {
                val currPin = it
                val currMarker = markers.firstOrNull { tag == currPin.uid }
                Log.d("Remove Markers", "Found matching marker")
                markers.removeIf { tag == currPin.uid }
                currMarker?.remove()
                mFirestore.document("pins/${it.uid}").delete()
                removedPins.add(currPin)
            }
        }
        pins.removeAll(removedPins)
    }

    private fun deletePin(id: String) {
        var removedPins = mutableListOf<Pin>()
        Log.d("Remove Markers", "Markers: ${markers}")
        pins.forEach {
            if(it.uid == id) {
                val currPin = it
                val currMarker = markers.firstOrNull { tag == currPin.uid }
                Log.d("Remove Marker", "Found matching marker")
                Log.d("Remove Marker", currMarker?.remove().toString())
                Log.d("Remove Marker", "Marker list removal: ${markers.removeIf { tag == currPin.uid }}")
                mFirestore.document("pins/${it.uid}").delete()
                removedPins.add(currPin)
            }
        }
        pins.removeAll(removedPins)
    }

    private fun onDocumentAdded(change: DocumentChange) {
        Log.d("Doc added", change.document.id)
        pins.add(0, change.document.toObject(Pin::class.java)!!)
        pins[0].uid = change.document.id
        Log.d("Doc added", pins.toString())
        addPinsToMap()
    }

    private fun onDocumentModified(change: DocumentChange) {
        var uid = change.document.id
        pins[pins.indexOf(pins.first { it.uid == uid })] = change.document.toObject(Pin::class.java)
        addPinsToMap()
    }

    private fun onDocumentDeleted(change: DocumentChange) {
        deletePin(change.document.id)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        var mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY)
        if (mapViewBundle == null) {
            mapViewBundle = Bundle()
            outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle)
        }
        mapView.onSaveInstanceState(mapViewBundle)
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        Log.d("Detail", "Map Tag: ${marker.tag}")
        findNavController().navigate(R.id.nav_detail, bundleOf("id" to marker.tag))
        return true
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onPause() {
        mapView.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        mapView.onDestroy()
        super.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        gmap = googleMap
        gmap?.setMinZoomPreference(14f)
        val vt = LatLng(37.227705, -80.421854)
        gmap?.moveCamera(CameraUpdateFactory.newLatLng(vt))
        gmap?.setOnMarkerClickListener(this)
        addPinsToMap()
    }

    private  fun getDateFromString(datetoSaved: String?): Date? {
        return try {
            format.parse(datetoSaved)
        } catch (e: ParseException) {
            null
        }
    }

}