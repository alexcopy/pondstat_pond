package ru.gpsbox.natmob.repository.search;

import ru.gpsbox.natmob.domain.Tank;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the Tank entity.
 */
public interface TankSearchRepository extends ElasticsearchRepository<Tank, Long> {
}
