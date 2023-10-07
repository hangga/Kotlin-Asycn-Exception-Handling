import kotlinx.coroutines.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.File
import java.io.IOException
import kotlin.random.Random


// https://stackoverflow.com/questions/53303358/kotlin-async-exception-handling

class AsyncExceptionTest {

    fun fetchData(id: Int): String {
        if (Random.nextBoolean()) {
            throw IllegalArgumentException("Exception for id $id")
        }
        return "Data for id $id"
    }

    @Test
    fun withTryCatch(){
        runBlocking {
            val deferredResults = List(5) {
                async {
                    try {
                        fetchData(it)
                    } catch (e: Exception) {
                        println("Exception for id $it: ${e.message}")
                        "Error"
                    }
                }
            }

            val results = deferredResults.awaitAll()
            results.forEachIndexed { index, result ->
                println("Hasil untuk id $index: $result")
            }
        }
    }

    @Test
    fun withAwait(){
        runBlocking {
            val deferredResults = List(5) { async { fetchData(it) } }

            try {
                val results = awaitAll(*deferredResults.toTypedArray())
                results.forEachIndexed { index, result ->
                    println("Hasil untuk id $index: $result")
                }
            } catch (e: Exception) {
                println("Exception ditangani: ${e.message}")
            }
        }
    }


}