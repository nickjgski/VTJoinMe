package com.nickjgski.vtjoinme.ui.map

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.firebase.firestore.*
import com.nickjgski.vtjoinme.Pin
import com.nickjgski.vtjoinme.PinViewModel
import com.nickjgski.vtjoinme.R


class MapFragment : Fragment(), OnMapReadyCallback, EventListener<QuerySnapshot> {

    private lateinit var pinViewModel: PinViewModel
    private lateinit var mapView: MapView
    private var gmap: GoogleMap? = null

    private var pins: List<Pin> = emptyList()

    val mFirestore = FirebaseFirestore.getInstance()
    lateinit var mQuery: Query

    private val MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        pinViewModel =
            ViewModelProviders.of(this).get(PinViewModel::class.java)
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
            if(e != null) {
                Log.w("MapFragment", "listen:error", e)
                return@addSnapshotListener
            }

            for (dc in snapshots!!.documentChanges) {
                when (dc.type) {
                    DocumentChange.Type.ADDED -> pins.add(dc.document)
                    DocumentChange.Type.MODIFIED -> Log.d("Doc Change", "${dc.document.data}")
                    DocumentChange.Type.REMOVED -> Log.d("Doc Change", "${dc.document.data}")
                }
            }

            for(doc in snapshots!!.documents) {

            }
        }
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

    }

    override fun onEvent(p0: QuerySnapshot?, p1: FirebaseFirestoreException?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}