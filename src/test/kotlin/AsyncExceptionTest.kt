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

    @Test
    fun withSupervisorJob(){

        val supervisorJob = SupervisorJob()

        val scope = CoroutineScope(Dispatchers.Default + supervisorJob)

        val job1 = scope.launch {
            try {
                // Kode yang mungkin menghasilkan exception
                throw IllegalStateException("Exception 1")
            } catch (e: Exception) {
                println("Exception 1 ditangani: ${e.message}")
            }
        }

        val job2 = scope.launch {
            try {
                // Kode yang mungkin menghasilkan exception
                throw IllegalArgumentException("Exception 2")
            } catch (e: Exception) {
                println("Exception 2 ditangani: ${e.message}")
            }
        }

        runBlocking {
            job1.join()
            job2.join()
        }
    }
}