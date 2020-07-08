package com.example.traficontime

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

interface ApiService {
    @GET("querystation.asp")
    fun stationQuery(@Query("inpPointfr") stationName: String): Single<ResponseBody>

    @GET("stationresults.asp")
    fun stationRecords(@Query("selPointFrKey") stationId: String): Single<ResponseBody>
}


private var retrofit: Retrofit = Retrofit.Builder()
    .baseUrl("http://www.labs.skanetrafiken.se/v2.2/")
    .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
    .build()

private var apiService = retrofit.create(ApiService::class.java)


//
//<Id>80000</Id>
//<Name>Malmö C</Name>
//<Type>STOP_AREA</Type>
//<X>6167946</X>
//<Y>1323245</Y>
data class Station(
    @SerializedName("Id") val id: Int,
    @SerializedName("Name") val name: String
)

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
    val name: String,
    val time: String,
    val isRealTime: Boolean,
    val stopPoint: String,
    val toward: String
)

fun getBussTimeTable(stationId: String) =
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
            StationRecord(
                jsonObject.getString("Name"),
                jsonObject.getString("JourneyDateTime"),
                jsonObject.getBoolean("IsTimingPoint"),
                jsonObject.getString("StopPoint"),
                jsonObject.getString("Towards")
            )
        }
    }