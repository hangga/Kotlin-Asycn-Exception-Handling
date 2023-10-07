import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.File
import java.io.IOException


// https://stackoverflow.com/questions/53303358/kotlin-async-exception-handling

class AsyncExceptionTest {
    @Throws(IOException::class)
    suspend fun readFile(fileName: String): String {
        val file = File(fileName)
        if (!file.exists()) {
            throw IOException("File $fileName tidak ditemukan.")
        }

        return file.readText()
    }

    @Test
    fun testReadFile() = runBlocking {
        val job1 = async { readFile("sample.txt") }
        val job2 = async { readFile("another.txt") }

        try {
            val content1 = job1.await()
            val content2 = job2.await()

            assertEquals("Sample file content", content1.trim())
            assertEquals("Another file content", content2.trim())
        } catch (e: IOException) {
            println("Terjadi kesalahan: ${e.message}")
        }
    }
}