package com.example.data.local

import androidx.room.TypeConverter
import com.example.data.model.DetectionBox
import com.example.data.model.EvidenceSource
import org.json.JSONArray
import org.json.JSONObject

class AgriTypeConverters {

    @TypeConverter
    fun fromStringList(value: List<String>?): String {
        if (value == null) return "[]"
        val array = JSONArray()
        value.forEach { array.put(it) }
        return array.toString()
    }

    @TypeConverter
    fun toStringList(value: String?): List<String> {
        if (value.isNullOrBlank()) return emptyList()
        val list = mutableListOf<String>()
        val array = JSONArray(value)
        for (i in 0 until array.length()) {
            list.add(array.getString(i))
        }
        return list
    }

    @TypeConverter
    fun fromEvidenceList(value: List<EvidenceSource>?): String {
        if (value == null) return "[]"
        val array = JSONArray()
        value.forEach { src ->
            val obj = JSONObject().apply {
                put("id", src.id)
                put("title", src.title)
                put("organization", src.organization)
                put("url", src.url)
                put("publicationDate", src.publicationDate)
                put("retrievalDate", src.retrievalDate)
                put("summary", src.summary)
            }
            array.put(obj)
        }
        return array.toString()
    }

    @TypeConverter
    fun toEvidenceList(value: String?): List<EvidenceSource> {
        if (value.isNullOrBlank()) return emptyList()
        val list = mutableListOf<EvidenceSource>()
        val array = JSONArray(value)
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            list.add(
                EvidenceSource(
                    id = obj.optString("id", ""),
                    title = obj.optString("title", ""),
                    organization = obj.optString("organization", ""),
                    url = obj.optString("url", ""),
                    publicationDate = obj.optString("publicationDate", ""),
                    retrievalDate = obj.optString("retrievalDate", ""),
                    summary = obj.optString("summary", "")
                )
            )
        }
        return list
    }

    @TypeConverter
    fun fromDetectionBoxList(value: List<DetectionBox>?): String {
        if (value == null) return "[]"
        val array = JSONArray()
        value.forEach { box ->
            val obj = JSONObject().apply {
                put("label", box.label)
                put("confidence", box.confidence.toDouble())
                put("left", box.left.toDouble())
                put("top", box.top.toDouble())
                put("right", box.right.toDouble())
                put("bottom", box.bottom.toDouble())
                put("severity", box.severity)
            }
            array.put(obj)
        }
        return array.toString()
    }

    @TypeConverter
    fun toDetectionBoxList(value: String?): List<DetectionBox> {
        if (value.isNullOrBlank()) return emptyList()
        val list = mutableListOf<DetectionBox>()
        val array = JSONArray(value)
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            list.add(
                DetectionBox(
                    label = obj.optString("label", ""),
                    confidence = obj.optDouble("confidence", 0.9).toFloat(),
                    left = obj.optDouble("left", 0.0).toFloat(),
                    top = obj.optDouble("top", 0.0).toFloat(),
                    right = obj.optDouble("right", 1.0).toFloat(),
                    bottom = obj.optDouble("bottom", 1.0).toFloat(),
                    severity = obj.optString("severity", "Moderate")
                )
            )
        }
        return list
    }
}
