package com.app.mobile.domain.mappers

import com.app.mobile.data.database.entity.HubEntity
import com.app.mobile.domain.models.hives.HubDomain
import com.app.mobile.domain.models.sensors.NoiseSensor
import com.app.mobile.domain.models.sensors.TempSensor
import com.app.mobile.domain.models.sensors.WeightSensor
import com.app.mobile.presentation.models.hive.HubPreviewModel
import com.app.mobile.presentation.models.hive.HubUi
import com.app.mobile.presentation.models.hive.NoiseSensorPreview
import com.app.mobile.presentation.models.hive.TempSensorPreview
import com.app.mobile.presentation.models.hive.WeightSensorPreview

fun HubEntity.toDomain(tempSensor: TempSensor, noiseSensor: NoiseSensor, weightSensor: WeightSensor) = HubDomain(
    id = this.id,
    hiveId = this.hiveId,
    name = this.name,
    tempSensor = tempSensor,
    noiseSensor = noiseSensor,
    weightSensor = weightSensor
)

fun HubDomain?.toUiModel(): HubUi {
    return this?.let { hub ->
        HubUi.Present(
            id = hub.id,
            name = hub.name,
            tempSensor = tempSensor.toUi(),
            noiseSensor = noiseSensor.toUi(),
            weightSensor = weightSensor.toUi()
        )
    } ?: HubUi.Absent
}

fun HubDomain.toPreviewModel() = HubPreviewModel(
    id = this.id,
    name = this.name
)

fun HubDomain.toEntity() = HubEntity(
    id = this.id,
    hiveId = this.hiveId,
    name = this.name
)

fun TempSensor.toUi() = TempSensorPreview(
    temp = lastTemp
)

fun NoiseSensor.toUi() = NoiseSensorPreview(
    noise = lastNoise
)

fun WeightSensor.toUi() = WeightSensorPreview(
    weight = weight
)