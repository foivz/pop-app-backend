package hr.foi.pop.backend.utils

import org.slf4j.LoggerFactory
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import kotlin.reflect.KClass

class MockMvcBuilderManager {
    companion object {
        fun getMockMvc(context: WebApplicationContext, parentClassnameForLogger: KClass<*>): MockMvc {
            return MockMvcBuilders
                .webAppContextSetup(context)
                .apply<DefaultMockMvcBuilder>(SecurityMockMvcConfigurers.springSecurity())
                .alwaysDo<DefaultMockMvcBuilder> {
                    LoggerFactory.getLogger(parentClassnameForLogger.java).info(it.response.contentAsString)
                }
                .build()
        }
    }
}
