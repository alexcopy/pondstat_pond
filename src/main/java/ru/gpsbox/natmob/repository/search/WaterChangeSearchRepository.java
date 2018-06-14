package ru.gpsbox.natmob.repository.search;

import ru.gpsbox.natmob.domain.WaterChange;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the WaterChange entity.
 */
public interface WaterChangeSearchRepository extends ElasticsearchRepository<WaterChange, Long> {
}
