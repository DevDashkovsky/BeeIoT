package com.app.mobile.domain.repository

import com.app.mobile.domain.models.hives.HiveDomain
import com.app.mobile.domain.models.hives.HiveDomainPreview
import com.app.mobile.domain.models.hives.HiveEditorDomain
import com.app.mobile.domain.models.sensors.NoiseSensor
import com.app.mobile.domain.models.sensors.TempSensor
import com.app.mobile.domain.models.sensors.WeightSensor

interface HivesLocalRepository {

	suspend fun getHives(): List<HiveDomainPreview>

	suspend fun getHive(
		hiveId: String,
		tempSensor: TempSensor,
		noiseSensor: NoiseSensor,
		weightSensor: WeightSensor
	): HiveDomain?

	suspend fun getHivePreview(hiveId: String): HiveDomainPreview?

	suspend fun saveHive(hive: HiveEditorDomain)
}