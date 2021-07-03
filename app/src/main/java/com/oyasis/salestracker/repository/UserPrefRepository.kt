import android.content.Context
import androidx.core.content.edit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

private const val USER_PREFERENCES_NAME = "user_preferences"
private const val SORT_ORDER_KEY = "sort_order"

/**
 * Class that handles saving and retrieving user preferences
 */
public class UserPreferencesRepository private constructor(context: Context) {

    private val sharedPreferences =
        context.applicationContext.getSharedPreferences(USER_PREFERENCES_NAME, Context.MODE_PRIVATE)

    private val KEY_ACCESS_CODE = "access_key"
    private val KEY_IS_LOGGEDIN = "logged_in"
    private val KEY_USER_CONFIRMED = "user_confirmed"

    fun updateKey(key : String) {
        sharedPreferences.edit{
            putString(KEY_ACCESS_CODE, key)
            putBoolean(KEY_IS_LOGGEDIN, true)
            apply()
        }
    }

    fun hasLoggedIn(): Boolean =
        sharedPreferences.getBoolean(KEY_IS_LOGGEDIN, false)

    fun getKey() : String? =
        sharedPreferences.getString(KEY_ACCESS_CODE, null)

    fun isUserConfirmed() : Boolean =
        sharedPreferences.getBoolean(KEY_USER_CONFIRMED, false)


    companion object {
        @Volatile
        private var INSTANCE: UserPreferencesRepository? = null

        fun getInstance(context: Context): UserPreferencesRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE?.let {
                    return it
                }

                val instance = UserPreferencesRepository(context)
                INSTANCE = instance
                instance
            }
        }
    }
}