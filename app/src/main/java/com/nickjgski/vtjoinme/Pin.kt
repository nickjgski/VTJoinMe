package com.nickjgski.vtjoinme

import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint
import java.util.*

data class Pin(
    var uid: String = "",
    var building: String = "",
    var totalSeats: Int = -1,
    var availSeats: Int = -1,
    var expTime: Date = Date(),
    var study: Boolean = false,
    var quiet: Boolean = false,
    var desc: String = "",
    var latitude: Double = 0.0,
    var longitude: Double = 0.0
)