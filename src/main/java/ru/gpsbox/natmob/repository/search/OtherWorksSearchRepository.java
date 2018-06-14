package ru.gpsbox.natmob.repository.search;

import ru.gpsbox.natmob.domain.OtherWorks;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the OtherWorks entity.
 */
public interface OtherWorksSearchRepository extends ElasticsearchRepository<OtherWorks, Long> {
}
