package com.example.aa_example.auto

import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.ItemList
import androidx.car.app.model.PlaceListMapTemplate
import androidx.car.app.model.Template

class AAScreen(carContext: CarContext) : Screen(carContext) {
    override fun onGetTemplate(): Template {
        return PlaceListMapTemplate.Builder()
            .setTitle("Hello World")
            .setHeaderAction(Action.APP_ICON)
            .setItemList(ItemList.Builder().build())
            .build()
    }
}
