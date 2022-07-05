import java.text.SimpleDateFormat
import java.util.*

/**
 *    Created by Administrator on 2021/11/10.
 *    Desc :
 */
object Deploy {
    fun getSystem(): String {
        val simpleDateFormat = SimpleDateFormat("yyyyMMdd", Locale.CHINA);
        return simpleDateFormat.format(System.currentTimeMillis())
    }
}