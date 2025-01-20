import android.content.Context
import android.net.Uri
import android.util.Base64
import com.example.projeto_cm_24_25.utils.ManifestUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ImgBBApi {

    private const val BASE_URL = "https://api.imgbb.com/1/"

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val apiService: ImgBBService by lazy {
        retrofit.create(ImgBBService::class.java)
    }

    fun getApiKey(context: Context): String {
        return ManifestUtils.getApiKeyFromManifest(context, "com.example.IMGBB_API_KEY")
            ?: throw IllegalStateException("API Key not found in Manifest!")
    }

    fun uploadImage(
        context: Context,
        base64Image: String,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        val apiKey = getApiKey(context)
        val apiService = ImgBBApi.apiService

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.uploadImage(
                    key = apiKey,
                    image = base64Image
                )
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    responseBody?.data?.url?.let { url ->
                        withContext(Dispatchers.Main) {
                            onSuccess(url)
                        }
                    } ?: withContext(Dispatchers.Main) {
                        onError("Failed to get image URL from response.")
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        onError("Upload failed: ${response.message()}")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    onError("An error occurred: ${e.localizedMessage}")
                }
            }
        }
    }

    fun convertImageToBase64(context: Context, uri: Uri): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val bytes = inputStream?.readBytes()
            inputStream?.close()
            Base64.encodeToString(bytes, Base64.DEFAULT)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
