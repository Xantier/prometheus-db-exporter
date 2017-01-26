package com.hallila.jussi

import com.fasterxml.jackson.annotation.JsonProperty
import io.prometheus.client.Counter
import io.prometheus.client.Gauge
import io.prometheus.client.Histogram
import io.prometheus.client.Summary

data class MonitoringQuery(
    val type: MonitoringType,
    val name: String,
    val labels: List<String>,
    val query: String,
    val description: String
)

interface PrometheusQuery {
    val monitoringQuery: MonitoringQuery
}

data class GaugeQuery(val gauge: Gauge, override val monitoringQuery: MonitoringQuery) : PrometheusQuery
data class CounterQuery(val counter: Counter, override val monitoringQuery: MonitoringQuery) : PrometheusQuery
data class SummaryQuery(val summary: Summary, override val monitoringQuery: MonitoringQuery) : PrometheusQuery
data class HistogramQuery(val histogram: Histogram, override val monitoringQuery: MonitoringQuery) : PrometheusQuery

data class QueryResult(val labelValues: List<String>, val value: Long)

enum class MonitoringType {
    @JsonProperty("gauge")
    GAUGE,
    @JsonProperty("counter")
    COUNTER,
    @JsonProperty("summary")
    SUMMARY,
    @JsonProperty("histogram")
    HISTOGRAM
}