package ru.gpsbox.natmob.repository.search;

import ru.gpsbox.natmob.domain.Chemicals;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the Chemicals entity.
 */
public interface ChemicalsSearchRepository extends ElasticsearchRepository<Chemicals, Long> {
}
