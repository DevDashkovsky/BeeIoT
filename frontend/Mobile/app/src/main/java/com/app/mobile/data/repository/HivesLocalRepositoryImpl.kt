package com.app.mobile.data.repository

import com.app.mobile.data.database.dao.HiveDao
import com.app.mobile.domain.mappers.toDomain
import com.app.mobile.domain.mappers.toEntity
import com.app.mobile.domain.models.hives.HiveDomain
import com.app.mobile.domain.models.hives.HiveDomainPreview
import com.app.mobile.domain.models.hives.HiveEditorDomain
import com.app.mobile.domain.models.sensors.NoiseSensor
import com.app.mobile.domain.models.sensors.TempSensor
import com.app.mobile.domain.models.sensors.WeightSensor
import com.app.mobile.domain.repository.HivesLocalRepository

class HivesLocalRepositoryImpl(private val hiveDao: HiveDao) : HivesLocalRepository {

	override suspend fun getHives(): List<HiveDomainPreview> =
		hiveDao.getHives().map { it.toDomain() }

	override suspend fun getHive(
		hiveId: String,
		tempSensor: TempSensor,
		noiseSensor: NoiseSensor,
		weightSensor: WeightSensor
	): HiveDomain? =
		hiveDao.getHive(hiveId)?.toDomain(tempSensor, noiseSensor, weightSensor)

	override suspend fun getHivePreview(hiveId: String) =
		hiveDao.getHivePreview(hiveId)?.toDomain()

	override suspend fun saveHive(hive: HiveEditorDomain) {
		hiveDao.saveHive(hive.toEntity())
	}
}