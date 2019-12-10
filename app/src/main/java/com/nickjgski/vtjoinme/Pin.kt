package com.nickjgski.vtjoinme

import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint
import java.util.*

data class Pin(
    var uid: String = "",
    val building: String = "",
    val totalSeats: Int = -1,
    val availSeats: Int = -1,
    val expTime: Date = Date(),
    val study: Boolean = false,
    val quiet: Boolean = false,
    val desc: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)