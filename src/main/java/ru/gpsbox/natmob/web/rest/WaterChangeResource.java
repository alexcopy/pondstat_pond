package ru.gpsbox.natmob.web.rest;

import com.codahale.metrics.annotation.Timed;
import ru.gpsbox.natmob.domain.WaterChange;

import ru.gpsbox.natmob.repository.WaterChangeRepository;
import ru.gpsbox.natmob.repository.search.WaterChangeSearchRepository;
import ru.gpsbox.natmob.web.rest.errors.BadRequestAlertException;
import ru.gpsbox.natmob.web.rest.util.HeaderUtil;
import ru.gpsbox.natmob.service.dto.WaterChangeDTO;
import ru.gpsbox.natmob.service.mapper.WaterChangeMapper;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing WaterChange.
 */
@RestController
@RequestMapping("/api")
public class WaterChangeResource {

    private final Logger log = LoggerFactory.getLogger(WaterChangeResource.class);

    private static final String ENTITY_NAME = "waterChange";

    private final WaterChangeRepository waterChangeRepository;

    private final WaterChangeMapper waterChangeMapper;

    private final WaterChangeSearchRepository waterChangeSearchRepository;

    public WaterChangeResource(WaterChangeRepository waterChangeRepository, WaterChangeMapper waterChangeMapper, WaterChangeSearchRepository waterChangeSearchRepository) {
        this.waterChangeRepository = waterChangeRepository;
        this.waterChangeMapper = waterChangeMapper;
        this.waterChangeSearchRepository = waterChangeSearchRepository;
    }

    /**
     * POST  /water-changes : Create a new waterChange.
     *
     * @param waterChangeDTO the waterChangeDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new waterChangeDTO, or with status 400 (Bad Request) if the waterChange has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/water-changes")
    @Timed
    public ResponseEntity<WaterChangeDTO> createWaterChange(@Valid @RequestBody WaterChangeDTO waterChangeDTO) throws URISyntaxException {
        log.debug("REST request to save WaterChange : {}", waterChangeDTO);
        if (waterChangeDTO.getId() != null) {
            throw new BadRequestAlertException("A new waterChange cannot already have an ID", ENTITY_NAME, "idexists");
        }
        WaterChange waterChange = waterChangeMapper.toEntity(waterChangeDTO);
        waterChange = waterChangeRepository.save(waterChange);
        WaterChangeDTO result = waterChangeMapper.toDto(waterChange);
        waterChangeSearchRepository.save(waterChange);
        return ResponseEntity.created(new URI("/api/water-changes/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /water-changes : Updates an existing waterChange.
     *
     * @param waterChangeDTO the waterChangeDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated waterChangeDTO,
     * or with status 400 (Bad Request) if the waterChangeDTO is not valid,
     * or with status 500 (Internal Server Error) if the waterChangeDTO couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/water-changes")
    @Timed
    public ResponseEntity<WaterChangeDTO> updateWaterChange(@Valid @RequestBody WaterChangeDTO waterChangeDTO) throws URISyntaxException {
        log.debug("REST request to update WaterChange : {}", waterChangeDTO);
        if (waterChangeDTO.getId() == null) {
            return createWaterChange(waterChangeDTO);
        }
        WaterChange waterChange = waterChangeMapper.toEntity(waterChangeDTO);
        waterChange = waterChangeRepository.save(waterChange);
        WaterChangeDTO result = waterChangeMapper.toDto(waterChange);
        waterChangeSearchRepository.save(waterChange);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, waterChangeDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /water-changes : get all the waterChanges.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of waterChanges in body
     */
    @GetMapping("/water-changes")
    @Timed
    public List<WaterChangeDTO> getAllWaterChanges() {
        log.debug("REST request to get all WaterChanges");
        List<WaterChange> waterChanges = waterChangeRepository.findAll();
        return waterChangeMapper.toDto(waterChanges);
        }

    /**
     * GET  /water-changes/:id : get the "id" waterChange.
     *
     * @param id the id of the waterChangeDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the waterChangeDTO, or with status 404 (Not Found)
     */
    @GetMapping("/water-changes/{id}")
    @Timed
    public ResponseEntity<WaterChangeDTO> getWaterChange(@PathVariable Long id) {
        log.debug("REST request to get WaterChange : {}", id);
        WaterChange waterChange = waterChangeRepository.findOne(id);
        WaterChangeDTO waterChangeDTO = waterChangeMapper.toDto(waterChange);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(waterChangeDTO));
    }

    /**
     * DELETE  /water-changes/:id : delete the "id" waterChange.
     *
     * @param id the id of the waterChangeDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/water-changes/{id}")
    @Timed
    public ResponseEntity<Void> deleteWaterChange(@PathVariable Long id) {
        log.debug("REST request to delete WaterChange : {}", id);
        waterChangeRepository.delete(id);
        waterChangeSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/water-changes?query=:query : search for the waterChange corresponding
     * to the query.
     *
     * @param query the query of the waterChange search
     * @return the result of the search
     */
    @GetMapping("/_search/water-changes")
    @Timed
    public List<WaterChangeDTO> searchWaterChanges(@RequestParam String query) {
        log.debug("REST request to search WaterChanges for query {}", query);
        return StreamSupport
            .stream(waterChangeSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .map(waterChangeMapper::toDto)
            .collect(Collectors.toList());
    }

}
