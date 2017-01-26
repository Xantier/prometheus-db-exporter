package com.hallila.jussi

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.prometheus.client.Collector
import io.prometheus.client.exporter.MetricsServlet
import io.prometheus.client.hotspot.DefaultExports
import io.prometheus.client.spring.boot.SpringBootMetricsCollector
import org.springframework.boot.SpringApplication
import org.springframework.boot.actuate.endpoint.PublicMetrics
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.web.servlet.ServletRegistrationBean
import org.springframework.context.annotation.Bean
import java.io.File
import java.util.*

@SpringBootApplication open class App : WithLogging() {
    companion object {
        @JvmStatic fun main(args: Array<String>) {
            SpringApplication.run(App::class.java, *args)
        }
    }

    @Bean
    open fun springBootMetricsCollector(publicMetrics: Collection<PublicMetrics>): SpringBootMetricsCollector {
        val springBootMetricsCollector = SpringBootMetricsCollector(publicMetrics)
        springBootMetricsCollector.register<Collector>()
        return springBootMetricsCollector
    }

    @Bean
    open fun servletRegistrationBean(): ServletRegistrationBean {
        DefaultExports.initialize()
        return ServletRegistrationBean(MetricsServlet(), "/metrics")
    }

    @Bean
    open fun monitoringQueries(): List<MonitoringQuery> {
        val returnable = ArrayList<MonitoringQuery>()
        val mapper = ObjectMapper(YAMLFactory()).registerKotlinModule()
        try {
            val pathname = "./queries/"
            val files = File(pathname).listFiles()
            files.filter { it.isFile }
                .mapTo(returnable) { mapper.readValue<MonitoringQuery>(File(pathname + it.name)) }
        } catch (e: Exception) {
            log.error("Failed to parse query files.", e)
        }
        return returnable
    }

}

