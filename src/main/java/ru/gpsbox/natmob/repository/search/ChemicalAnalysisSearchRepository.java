package ru.gpsbox.natmob.repository.search;

import ru.gpsbox.natmob.domain.ChemicalAnalysis;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the ChemicalAnalysis entity.
 */
public interface ChemicalAnalysisSearchRepository extends ElasticsearchRepository<ChemicalAnalysis, Long> {
}
