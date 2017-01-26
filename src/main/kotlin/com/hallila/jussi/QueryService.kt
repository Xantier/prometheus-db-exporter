package com.hallila.jussi

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.sql.SQLException

@Service class QueryService @Autowired constructor(private val jdbcTemplate: JdbcTemplate): WithLogging() {

    lateinit var gauges: List<GaugeQuery>
        @Autowired set

    lateinit var counters: List<CounterQuery>
        @Autowired set

    lateinit var summaries: List<SummaryQuery>
        @Autowired set

    lateinit var histograms: List<HistogramQuery>
        @Autowired set

    @Scheduled(fixedRateString = "\${query.interval.gauge}")
    fun queryGauge() {
        gauges.forEach(runQuery<GaugeQuery> { query, res -> query.gauge.labels(*res.labelValues.toTypedArray()).inc(res.value.toDouble()) })
    }

    @Scheduled(fixedRateString = "\${query.interval.counter}")
    fun queryCounter() {
        counters.forEach(runQuery<CounterQuery> { query, res -> query.counter.labels(*res.labelValues.toTypedArray()).inc(res.value.toDouble()) })
    }

    @Scheduled(fixedRateString = "\${query.interval.summary}")
    fun querySummary() {
        summaries.forEach(runQuery<SummaryQuery> { query, res -> query.summary.labels(*res.labelValues.toTypedArray()).observe(res.value.toDouble()) })
    }

    @Scheduled(fixedRateString = "\${query.interval.histogram}")
    fun queryHistogram() {
        histograms.forEach(runQuery<HistogramQuery> { query, res -> query.histogram.labels(*res.labelValues.toTypedArray()).observe(res.value.toDouble()) })
    }

    private fun <T : PrometheusQuery> runQuery(populateMetric: (T, QueryResult) -> Unit): (T) -> Unit {
        return { it ->
            log.info("Running Query: {}", it.monitoringQuery.query)
            log.info("Creating data for {}: {} - {}", it.monitoringQuery.type, it.monitoringQuery.name, it.monitoringQuery.description)
            jdbcTemplate.query(it.monitoringQuery.query, mapRow(it)).forEach { res ->
                populateMetric(it, res)
            }
        }
    }

    private fun mapRow(it: PrometheusQuery): RowMapper<QueryResult> {
        return RowMapper { rs, i ->
            val labelValues = it.monitoringQuery.labels
                .map { s ->
                    try {
                        rs.getString(s)
                    } catch (e: SQLException) {
                        log.error("Failed to run query", e.errorCode)
                        ""
                    }
                }
            QueryResult(labelValues, rs.getLong(it.monitoringQuery.labels.size + 1))
        }
    }
}