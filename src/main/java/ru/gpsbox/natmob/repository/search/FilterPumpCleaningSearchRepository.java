package ru.gpsbox.natmob.repository.search;

import ru.gpsbox.natmob.domain.FilterPumpCleaning;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the FilterPumpCleaning entity.
 */
public interface FilterPumpCleaningSearchRepository extends ElasticsearchRepository<FilterPumpCleaning, Long> {
}
