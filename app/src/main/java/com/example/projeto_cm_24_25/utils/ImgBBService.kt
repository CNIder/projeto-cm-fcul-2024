import com.example.projeto_cm_24_25.utils.ImgBBResponse
import retrofit2.Response
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.Field

interface ImgBBService {
    @FormUrlEncoded
    @POST("upload")
    suspend fun uploadImage(
        @Field("key") key: String,
        @Field("image") image: String
    ): Response<ImgBBResponse>
}
