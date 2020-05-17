package kr.entree.spigradle.internal

import com.google.common.base.CaseFormat
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties

/**
 * Created by JunHyung Lim on 2020-05-11
 */
@OptIn(ExperimentalStdlibApi::class)
internal inline fun <reified V> Any.toFieldEntries(
        keyMapper: (String) -> String = { CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, it) }
): List<Pair<String, V>> {
    return this::class.memberProperties.map { property ->
        val renameAnnotation = property.findAnnotation<SerialName>()
        val name = renameAnnotation?.value ?: property.name
        val value = if (property.isConst) property.call() else property.call(this)
        keyMapper(name) to value as V
    }
}