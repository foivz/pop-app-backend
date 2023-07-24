package hr.foi.pop.backend

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.Statement

@SpringBootTest
class BackendApplicationTests {
    val url = "jdbc:h2:mem:pop-dev-db"
    val username = "db"
    val password = "admin"

    @Test
    fun insertMockDataInH2_20ProductsInserted_True() {
        val connection: Connection = DriverManager.getConnection(url, username, password)
        val statement: Statement = connection.createStatement()
        val query = "SELECT COUNT(*) as products_count FROM POPAPP_DB.PRODUCTS "

        val result: ResultSet = statement.executeQuery(query)
        result.next()
        val count = result.getInt("products_count")

        Assertions.assertEquals(20, count)
    }

}
