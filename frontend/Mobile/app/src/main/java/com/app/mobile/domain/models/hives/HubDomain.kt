package com.app.mobile.domain.models.hives

import com.app.mobile.domain.models.sensors.NoiseSensor
import com.app.mobile.domain.models.sensors.TempSensor
import com.app.mobile.domain.models.sensors.WeightSensor
import java.util.UUID

data class HubDomain(
	val id: String = UUID.randomUUID().toString(),
	val hiveId: String?,
	val name: String,
	val tempSensor: TempSensor,
	val noiseSensor: NoiseSensor,
	val weightSensor: WeightSensor
)
