package ru.gpsbox.natmob.repository.search;

import ru.gpsbox.natmob.domain.TempMeter;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the TempMeter entity.
 */
public interface TempMeterSearchRepository extends ElasticsearchRepository<TempMeter, Long> {
}
