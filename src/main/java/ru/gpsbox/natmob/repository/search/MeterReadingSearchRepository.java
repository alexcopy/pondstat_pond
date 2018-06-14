package ru.gpsbox.natmob.repository.search;

import ru.gpsbox.natmob.domain.MeterReading;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the MeterReading entity.
 */
public interface MeterReadingSearchRepository extends ElasticsearchRepository<MeterReading, Long> {
}
