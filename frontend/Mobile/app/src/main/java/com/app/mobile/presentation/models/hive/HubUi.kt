package com.app.mobile.presentation.models.hive

sealed interface HubUi {
	data class Present(
		val id: String, val name: String, val tempSensor: TempSensorPreview, val noiseSensor: NoiseSensorPreview, val weightSensor: WeightSensorPreview
	) : HubUi

	data object Absent : HubUi
}
