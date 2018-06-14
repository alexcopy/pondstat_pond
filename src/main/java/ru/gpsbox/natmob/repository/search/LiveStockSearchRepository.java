package ru.gpsbox.natmob.repository.search;

import ru.gpsbox.natmob.domain.LiveStock;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the LiveStock entity.
 */
public interface LiveStockSearchRepository extends ElasticsearchRepository<LiveStock, Long> {
}
