package app.raihanlazuardi.whatif.data.storage

import android.content.Context
import app.raihanlazuardi.whatif.data.model.FastingSchedule
import app.raihanlazuardi.whatif.data.model.MokelRecord
import app.raihanlazuardi.whatif.data.model.UserProfile
import app.raihanlazuardi.whatif.data.model.WhatIFData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class WhatIFRepository(private val context: Context) {

    private val scope = CoroutineScope(Dispatchers.IO)
    private val dataFile = File(context.filesDir, "whatif_data.json")

    private val _dataFlow = MutableStateFlow(WhatIFData())
    val dataFlow: StateFlow<WhatIFData> = _dataFlow.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        if (!dataFile.exists()) {
            val initialData = WhatIFData()
            _dataFlow.value = initialData
            saveToFile(initialData)
            return
        }

        try {
            val jsonStr = dataFile.readText()
            val parsed = parseJson(jsonStr)
            _dataFlow.value = parsed
        } catch (e: Exception) {
            e.printStackTrace()
            val fallback = WhatIFData()
            _dataFlow.value = fallback
            saveToFile(fallback)
        }
    }

    @Synchronized
    private fun saveToFile(data: WhatIFData) {
        try {
            val jsonStr = serializeToJson(data)
            val tempFile = File(context.filesDir, "whatif_data.json.tmp")
            tempFile.writeText(jsonStr)
            if (tempFile.renameTo(dataFile) || tempFile.copyTo(dataFile, overwrite = true).exists()) {
                tempFile.delete()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun updateSchedule(schedule: FastingSchedule) {
        val updated = _dataFlow.value.copy(schedule = schedule)
        _dataFlow.value = updated
        scope.launch { saveToFile(updated) }
    }

    fun updateProfile(profile: UserProfile) {
        val updated = _dataFlow.value.copy(profile = profile)
        _dataFlow.value = updated
        scope.launch { saveToFile(updated) }
    }

    fun addMokel(record: MokelRecord) {
        val updatedList = listOf(record) + _dataFlow.value.mokelRecords
        val updated = _dataFlow.value.copy(mokelRecords = updatedList)
        _dataFlow.value = updated
        scope.launch { saveToFile(updated) }
    }

    fun deleteMokel(id: String) {
        val updatedList = _dataFlow.value.mokelRecords.filterNot { it.id == id }
        val updated = _dataFlow.value.copy(mokelRecords = updatedList)
        _dataFlow.value = updated
        scope.launch { saveToFile(updated) }
    }

    fun toggleFastSuccess(date: LocalDate) {
        val current = _dataFlow.value.completedFastDates
        val updated = if (current.contains(date)) {
            current.filterNot { it == date }
        } else {
            current + date
        }
        val newData = _dataFlow.value.copy(completedFastDates = updated)
        _dataFlow.value = newData
        scope.launch { saveToFile(newData) }
    }

    fun getMokelsForDate(date: LocalDate): List<MokelRecord> {
        return _dataFlow.value.mokelRecords.filter { it.localDate == date }
    }

    fun getTodayMokels(): List<MokelRecord> {
        return getMokelsForDate(LocalDate.now())
    }

    fun exportToJson(): String {
        return serializeToJson(_dataFlow.value)
    }

    fun importFromJson(jsonString: String): Result<Unit> {
        return try {
            val imported = parseJson(jsonString)
            _dataFlow.value = imported
            saveToFile(imported)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun resetData() {
        val reset = WhatIFData()
        _dataFlow.value = reset
        scope.launch { saveToFile(reset) }
    }

    private fun serializeToJson(data: WhatIFData): String {
        val root = JSONObject()

        // Profile
        val profileObj = JSONObject().apply {
            put("name", data.profile.name)
            put("targetFastingHours", data.profile.targetFastingHours)
            put("notificationsEnabled", data.profile.notificationsEnabled)
            put("useDynamicColor", data.profile.useDynamicColor)
        }
        root.put("profile", profileObj)

        // Schedule
        val scheduleObj = JSONObject().apply {
            put("startHour", data.schedule.startHour)
            put("startMinute", data.schedule.startMinute)
            put("endHour", data.schedule.endHour)
            put("endMinute", data.schedule.endMinute)
        }
        root.put("schedule", scheduleObj)

        // Mokel records
        val mokelArray = JSONArray()
        data.mokelRecords.forEach { record ->
            val item = JSONObject().apply {
                put("id", record.id)
                put("foodDescription", record.foodDescription)
                put("timestamp", record.timestamp.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                put("note", record.note)
            }
            mokelArray.put(item)
        }
        root.put("mokelRecords", mokelArray)

        // Completed fast dates
        val fastArray = JSONArray()
        data.completedFastDates.forEach { date ->
            fastArray.put(date.toString())
        }
        root.put("completedFastDates", fastArray)

        return root.toString(2)
    }

    private fun parseJson(jsonStr: String): WhatIFData {
        val root = JSONObject(jsonStr)

        val profile = if (root.has("profile")) {
            val p = root.getJSONObject("profile")
            UserProfile(
                name = p.optString("name", "Sobat IF"),
                targetFastingHours = p.optInt("targetFastingHours", 16),
                notificationsEnabled = p.optBoolean("notificationsEnabled", true),
                useDynamicColor = p.optBoolean("useDynamicColor", true)
            )
        } else UserProfile()

        val schedule = if (root.has("schedule")) {
            val s = root.getJSONObject("schedule")
            FastingSchedule(
                startHour = s.optInt("startHour", 20),
                startMinute = s.optInt("startMinute", 0),
                endHour = s.optInt("endHour", 12),
                endMinute = s.optInt("endMinute", 0)
            )
        } else FastingSchedule()

        val records = mutableListOf<MokelRecord>()
        if (root.has("mokelRecords")) {
            val array = root.getJSONArray("mokelRecords")
            for (i in 0 until array.length()) {
                val item = array.getJSONObject(i)
                val id = item.optString("id", "")
                val food = item.optString("foodDescription", "")
                val timeStr = item.optString("timestamp", "")
                val note = item.optString("note", "")

                val time = try {
                    LocalDateTime.parse(timeStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                } catch (e: Exception) {
                    LocalDateTime.now()
                }

                records.add(MokelRecord(id = id, foodDescription = food, timestamp = time, note = note))
            }
        }

        val completedDates = mutableListOf<LocalDate>()
        if (root.has("completedFastDates")) {
            val array = root.getJSONArray("completedFastDates")
            for (i in 0 until array.length()) {
                try {
                    completedDates.add(LocalDate.parse(array.getString(i)))
                } catch (_: Exception) {}
            }
        }

        return WhatIFData(
            profile = profile,
            schedule = schedule,
            mokelRecords = records,
            completedFastDates = completedDates
        )
    }

    companion object {
        @Volatile
        private var INSTANCE: WhatIFRepository? = null

        fun getInstance(context: Context): WhatIFRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: WhatIFRepository(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
}
