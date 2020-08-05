package com.firebaseapp.traffic_425b3

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import fr.arnaudguyon.xmltojsonlib.XmlToJson
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.text.SimpleDateFormat
import java.util.*

interface ApiService {
    @GET("querystation.asp")
    fun stationQuery(@Query("inpPointfr") stationName: String): Single<ResponseBody>

    @GET("stationresults.asp")
    fun stationRecords(@Query("selPointFrKey") stationId: Int): Single<ResponseBody>

    @GET("neareststation.asp")
    fun nearByStation(
        @Query("x") x: String,
        @Query("y") y: String,
        @Query("Radius") radius: Int
    ): Single<ResponseBody>
}


private var retrofit: Retrofit = Retrofit.Builder()
    .baseUrl("http://www.labs.skanetrafiken.se/v2.2/")
    .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
    .build()

private var apiService = retrofit.create(ApiService::class.java)

data class SavedStation(
    val id: Int,
    val name: String,
    val stopPointSet: Set<String>,
    val busNameSet: Set<String>
)

//
//<Id>80000</Id>
//<Name>Malmö C</Name>
//<Type>STOP_AREA</Type>
//<X>6167946</X>
//<Y>1323245</Y>
data class Station(
    @SerializedName("Id") val id: Int,
    @SerializedName("Name") val name: String
) {
    fun toSavedStation() = SavedStation(id, name, setOf(), setOf())
}

fun getStations(station: String) =
    apiService.stationQuery(station).map {
        XmlToJson.Builder(it.string()).build().toJson()!!
    }.map {
        it.getJSONObject("soap:Envelope")
            .getJSONObject("soap:Body")
            .getJSONObject("GetStartEndPointResponse")
            .getJSONObject("GetStartEndPointResult")
            .getJSONObject("StartPoints")
            .getJSONArray("Point")
    }.map {
        Gson().fromJson(it.toString(), Array<Station>::class.java).toList()
    }

//
//<Line>
//<Name>SkåneExpressen 1</Name>
//<No>401</No>
//<JourneyDateTime>2020-07-08T06:49:00</JourneyDateTime>
//<IsTimingPoint>true</IsTimingPoint>
//<StopPoint>G</StopPoint>
//<LineTypeId>2</LineTypeId>
//<LineTypeName>SkåneExpressen</LineTypeName>
//<Towards>SkåneExpressen Kristianstad</Towards>
//<RealTime>
//<RealTimeInfo>
//<NewDepPoint> G</NewDepPoint>
//<DepTimeDeviation>0</DepTimeDeviation>
//<DepDeviationAffect>NON_CRITICAL</DepDeviationAffect>
//</RealTimeInfo>
//</RealTime>
//<TrainNo>0</TrainNo>
//<Deviations/>
//<RunNo>18</RunNo>
//</Line>

data class StationRecord(
    val stationName: String,
    val busName: String,
    val time: Date,
    val isRealTime: Boolean,
    val stopPoint: String,
    val toward: String
)


fun getBussTimeTable(stationId: Int, stationName: String) =
    apiService.stationRecords(stationId).map {
        XmlToJson.Builder(it.string()).build().toJson()!!
    }.map {
        it.getJSONObject("soap:Envelope")
            .getJSONObject("soap:Body")
            .getJSONObject("GetDepartureArrivalResponse")
            .getJSONObject("GetDepartureArrivalResult")
            .getJSONObject("Lines")
            .getJSONArray("Line")
    }.map { jsonArray ->
        (0 until jsonArray.length()).map {
            val jsonObject = jsonArray.getJSONObject(it)

            val div =
                if (jsonObject.getString("RealTime").isNullOrEmpty().not()
                ) {
                    jsonObject.getJSONObject("RealTime").getJSONObject("RealTimeInfo")
                        .getInt("DepTimeDeviation")
                } else 0


            var stopPoint = jsonObject.getString("StopPoint")
            if (stopPoint.isNullOrEmpty())
                stopPoint = "X"

            StationRecord(
                stationName,
                jsonObject.getString("Name"),
                getTime(jsonObject.getString("JourneyDateTime"), div),
                jsonObject.getString("RealTime").isNullOrEmpty().not(),
                //jsonObject.getBoolean("IsTimingPoint"),
                stopPoint,
                jsonObject.getString("Towards")
            )
        }
    }
//<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema">
//<soap:Body>
//<GetNearestStopAreaResponse xmlns="http://www.etis.fskab.se/v1.0/ETISws">
//<GetNearestStopAreaResult>
//<Code>0</Code>
//<Message/>
//<NearestStopAreas>
//<NearestStopArea>

fun getNearBy(lat: String, lon: String, radius: Int = 500) =
    apiService.nearByStation(lat, lon, radius).map {
        XmlToJson.Builder(it.string()).build().toJson()!!
    }.map {
        it.getJSONObject("soap:Envelope")
            .getJSONObject("soap:Body")
            .getJSONObject("GetNearestStopAreaResponse")
            .getJSONObject("GetNearestStopAreaResult")
            .getJSONObject("NearestStopAreas")
            .getJSONArray("NearestStopArea")
    }.map {
        Gson().fromJson(it.toString(), Array<Station>::class.java).toList()
    }

private fun getTime(dateString: String, div: Int): Date {
    val date = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(dateString)
    val calendar = Calendar.getInstance()
    calendar.time = date
    calendar.add(Calendar.MINUTE, div);
    return calendar.time
}