package com.example.aa_example.auto

import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.text.SpannableString
import android.text.Spanned
import androidx.car.app.CarContext
import androidx.car.app.CarToast
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.CarLocation
import androidx.car.app.model.Distance
import androidx.car.app.model.DistanceSpan
import androidx.car.app.model.ItemList
import androidx.car.app.model.Metadata
import androidx.car.app.model.Place
import androidx.car.app.model.PlaceListMapTemplate
import androidx.car.app.model.PlaceMarker
import androidx.car.app.model.Row
import androidx.car.app.model.Template
import androidx.core.net.toUri
import com.example.aa_example.db.places
import com.example.aa_example.model.PlaceInfo

class AAScreen(carContext: CarContext) : Screen(carContext) {

    private val locationManager: LocationManager = carContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    // フォールバック用の位置情報
    private val fallbackLocation = 35.6762 to 139.6503

    private fun getCurrentLocation(): Pair<Double, Double> {
        return try {
            val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (location == null) {
                CarToast.makeText(carContext, "位置情報の取得に失敗しました。", CarToast.LENGTH_SHORT).show()
                return fallbackLocation
            }

            location.latitude to location.longitude
        } catch (e: SecurityException) {
            CarToast.makeText(carContext, "位置情報の権限がありません。", CarToast.LENGTH_SHORT).show()
            fallbackLocation
        } catch (e: Exception) {
            CarToast.makeText(carContext, "位置情報の取得に失敗しました。", CarToast.LENGTH_SHORT).show()
            fallbackLocation
        }
    }

    private fun startNavigation(carContext: CarContext, place: PlaceInfo) {
        try {
            val uri = "geo:${place.latitude},${place.longitude}?q=${place.name}&mode=d".toUri()
            val intent = Intent(CarContext.ACTION_NAVIGATE, uri)

            carContext.startCarApp(intent)
            CarToast.makeText(carContext, "${place.name}への案内を開始します", CarToast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            CarToast.makeText(carContext, "地図アプリを開けませんでした", CarToast.LENGTH_SHORT).show()
        }
    }

    override fun onGetTemplate(): Template {
        val currentLocation = getCurrentLocation()

        val itemListBuilder = ItemList.Builder()
        places.forEach { place ->
            val text = SpannableString(" ").apply {
                val distance = Distance.create(
                    place.distance(currentLocation.first, currentLocation.second),
                    Distance.UNIT_KILOMETERS,
                )
                setSpan(
                    DistanceSpan.create(distance),
                    0,
                    1,
                    Spanned.SPAN_INCLUSIVE_INCLUSIVE,
                )
            }
            val row = Row.Builder()
                .setMetadata(
                    Metadata.Builder()
                        .setPlace(
                            Place.Builder(CarLocation.create(place.latitude, place.longitude))
                                .setMarker(PlaceMarker.Builder().setLabel(place.type.label).build())
                                .build()
                        )
                        .build()
                )
                .setTitle(place.name)
                .addText(text)
                .setOnClickListener { startNavigation(carContext = carContext, place = place) }
                .build()
            itemListBuilder.addItem(row)
        }

        return PlaceListMapTemplate.Builder()
            .setTitle("PA / SA")
            .setHeaderAction(Action.APP_ICON)
            .setItemList(itemListBuilder.build())
            .build()
    }
}
