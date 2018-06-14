package ru.gpsbox.natmob.repository.search;

import ru.gpsbox.natmob.domain.Device;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the Device entity.
 */
public interface DeviceSearchRepository extends ElasticsearchRepository<Device, Long> {
}
