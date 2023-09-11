package hr.foi.pop.backend.controllers

import hr.foi.pop.backend.utils.AccessTokenBeanGenerator
import hr.foi.pop.backend.utils.MockMvcBuilderManager
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.web.context.WebApplicationContext

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EventControllerCreationTest {

    @Autowired
    lateinit var context: WebApplicationContext

    @Autowired
    @Qualifier(AccessTokenBeanGenerator.ADMIN)
    lateinit var mockAccessToken: String

    private lateinit var mvc: MockMvc

    @BeforeAll
    fun setup() {
        mvc = MockMvcBuilderManager.getMockMvc(context, EventControllerCreationTest::class)
    }

    @Test
    fun givenAdminUser_whenPostRequestWithProperEventName_status200() {

    }

    @Test
    fun givenNonAdminUser_whenPostRequestWithProperEventName_status403() {

    }

//    @Test
//    fun givenAdminUser_when

}
