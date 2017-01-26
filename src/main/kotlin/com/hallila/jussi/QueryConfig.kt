package com.hallila.jussi

import io.prometheus.client.Counter
import io.prometheus.client.Gauge
import io.prometheus.client.Histogram
import io.prometheus.client.Summary
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class QueryConfig @Autowired constructor(val monitoringQueries: List<MonitoringQuery>) : WithLogging() {

    @Bean
    open fun gauges(): List<GaugeQuery> =
        monitoringQueries
            .filter({ it -> it.type === MonitoringType.GAUGE })
            .map({ it ->
                GaugeQuery(Gauge.build()
                    .name(it.name)
                    .help(it.description)
                    .labelNames(*it.labels.toTypedArray())
                    .register(), it)
            })

    @Bean
    open fun counters(): List<CounterQuery> =
        monitoringQueries
            .filter({ it -> it.type === MonitoringType.COUNTER })
            .map({ it ->
                CounterQuery(Counter.build()
                    .name(it.name)
                    .help(it.description)
                    .labelNames(*it.labels.toTypedArray())
                    .register(), it)
            })


    @Bean
    open fun summaries(): List<SummaryQuery> =
        monitoringQueries
            .filter({ it -> it.type === MonitoringType.SUMMARY })
            .map({ it ->
                SummaryQuery(Summary.build()
                    .name(it.name)
                    .help(it.description)
                    .labelNames(*it.labels.toTypedArray())
                    .register(), it)
            })


    @Bean
    open fun histograms(): List<HistogramQuery> =
        monitoringQueries
            .filter({ it -> it.type === MonitoringType.HISTOGRAM })
            .map({ it ->
                HistogramQuery(Histogram.build()
                    .name(it.name)
                    .help(it.description)
                    .labelNames(*it.labels.toTypedArray())
                    .register(), it)
            })
}
