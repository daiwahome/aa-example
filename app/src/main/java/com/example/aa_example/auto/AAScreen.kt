package com.example.aa_example.auto

import android.text.SpannableString
import android.text.Spanned
import androidx.car.app.CarContext
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
import com.example.aa_example.db.places

class AAScreen(carContext: CarContext) : Screen(carContext) {

    // フォールバック用の位置情報
    private val fallbackLocation = 35.6762 to 139.6503

    private fun getCurrentLocation(): Pair<Double, Double> {
        return fallbackLocation
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
