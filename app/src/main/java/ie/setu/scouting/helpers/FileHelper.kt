package ie.setu.scouting.helpers

import android.content.Context
import java.io.*

object FileHelper {

    fun write(context: Context, fileName: String, data: String) {
        try {
            context.openFileOutput(fileName, Context.MODE_PRIVATE).use {
                it.write(data.toByteArray())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun read(context: Context, fileName: String): String? {
        return try {
            context.openFileInput(fileName).bufferedReader().use { it.readText() }
        } catch (e: FileNotFoundException) {
            null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
