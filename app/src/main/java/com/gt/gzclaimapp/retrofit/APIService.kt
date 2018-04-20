package com.gt.gzclaimapp.retrofit

import com.gt.gzclaimapp.gson.*
import io.reactivex.Observable
import okhttp3.MultipartBody
import retrofit2.http.*

interface APIService {

    //checkIn
    @GET("checkIn/otrecord")
    fun checkInOTRecord(@Query("sessionId") sessionId: String): Observable<BaseGsonArray<OTMonthlyRecord>>

    @GET("checkIn/editOtInfo")
    fun checkInEditOtInfo(@Query("sessionId") sessionId: String): Observable<BaseGsonObject<OtEditInfo>>

    @GET("checkIn/getOTInfo")
    fun checkInGetOtInfo(@Query("sessionId") sessionId: String, @Query("checkInId") checkInId: Int): Observable<BaseGsonObject<OTDetailInfo>>

    @GET("checkIn/hrToDolist")
    fun checkInHrToDolist(@Query("sessionId") sessionId: String, @Query("searchDate") searchDate: Long): Observable<BaseGsonArray<ToDoList>>

    @FormUrlEncoded
    @POST("checkIn/checkIn")
    fun checkInCheckIn(@Field("projectName") projectName: String, @Field("checkInTime") checkInTime: String,
                       @Field("sessionId") sessionId: String): Observable<BaseGsonObject<CheckInCheckIn>>

    @GET("checkIn/otrecordlist")
    fun checkInOTRecordList(@Query("sessionId") sessionId: String, @Query("otDate") otDate: Long): Observable<BaseGsonArray<OTDailyRecord>>

    @Multipart
    @POST("checkIn/addOTInfo")
    fun checkInAddOTInfo(@Part("sessionId") sessionId: String, @Part("checkInId") checkInId: Int,
                         @Part("trafficSubsidies") trafficSubsidies: Double, @Part("imageIds") imageIds: String,
                         @Part() multipartFiles: List<MultipartBody.Part>?): Observable<BaseGsonObject<OTApplyResult>>

    //user
    @FormUrlEncoded
    @POST("user/login")
    fun userLogin(@Field("email") email: String, @Field("deviceId") deviceId: String, @Field("deviceType") deviceType: Int,
                  @Field("token") token: String): Observable<BaseGsonObject<UserInfo>>

    //claim
    @GET("claim/monthRecord")
    fun claimMonthRecord(@Query("sessionId") sessionId: String, @Query("type") type: Int): Observable<BaseGsonArray<ClaimMonthRecord>>

    @GET("claim/record")
    fun claimDailyRecord(@Query("sessionId") sessionId: String, @Query("date") date: Long,
                         @Query("type") type: Int): Observable<BaseGsonArray<ClaimDailyRecord>>

    @GET("claim/recordDetail")
    fun getRecordDetail(@Query("sessionId") sessionId: String, @Query("recordId") recordId: Int?):Observable<BaseGsonObject<ClaimRecordDetail>>

    @FormUrlEncoded
    @POST("claim/recordDetail")
    fun addClaimRecord(@Field("sessionId") sessionId: String, @Field("recordId") recordId: Int?,
                           @Field("type") type:Int , @Field("claimType") claimType:Int,
                           @Field("totalPrice") totalPrice:Double, @Field("remark") remark:String):Observable<BaseGsonObject<AddClaimResult>>

    //push
    @POST("push/updateToken?deviceType=1")
    fun pushUpdateToken(@Query("sessionId") sessionId: String, @Query("deviceId") deviceId: String,
                        @Query("token") token: String): Observable<CommonResult>

}