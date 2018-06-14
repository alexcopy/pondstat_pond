package ru.gpsbox.natmob.repository.search;

import ru.gpsbox.natmob.domain.Location;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the Location entity.
 */
public interface LocationSearchRepository extends ElasticsearchRepository<Location, Long> {
}
