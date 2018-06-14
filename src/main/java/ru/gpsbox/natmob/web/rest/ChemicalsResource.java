package ru.gpsbox.natmob.web.rest;

import com.codahale.metrics.annotation.Timed;
import ru.gpsbox.natmob.domain.Chemicals;

import ru.gpsbox.natmob.repository.ChemicalsRepository;
import ru.gpsbox.natmob.repository.search.ChemicalsSearchRepository;
import ru.gpsbox.natmob.web.rest.errors.BadRequestAlertException;
import ru.gpsbox.natmob.web.rest.util.HeaderUtil;
import ru.gpsbox.natmob.web.rest.util.PaginationUtil;
import ru.gpsbox.natmob.service.dto.ChemicalsDTO;
import ru.gpsbox.natmob.service.mapper.ChemicalsMapper;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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
 * REST controller for managing Chemicals.
 */
@RestController
@RequestMapping("/api")
public class ChemicalsResource {

    private final Logger log = LoggerFactory.getLogger(ChemicalsResource.class);

    private static final String ENTITY_NAME = "chemicals";

    private final ChemicalsRepository chemicalsRepository;

    private final ChemicalsMapper chemicalsMapper;

    private final ChemicalsSearchRepository chemicalsSearchRepository;

    public ChemicalsResource(ChemicalsRepository chemicalsRepository, ChemicalsMapper chemicalsMapper, ChemicalsSearchRepository chemicalsSearchRepository) {
        this.chemicalsRepository = chemicalsRepository;
        this.chemicalsMapper = chemicalsMapper;
        this.chemicalsSearchRepository = chemicalsSearchRepository;
    }

    /**
     * POST  /chemicals : Create a new chemicals.
     *
     * @param chemicalsDTO the chemicalsDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new chemicalsDTO, or with status 400 (Bad Request) if the chemicals has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/chemicals")
    @Timed
    public ResponseEntity<ChemicalsDTO> createChemicals(@Valid @RequestBody ChemicalsDTO chemicalsDTO) throws URISyntaxException {
        log.debug("REST request to save Chemicals : {}", chemicalsDTO);
        if (chemicalsDTO.getId() != null) {
            throw new BadRequestAlertException("A new chemicals cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Chemicals chemicals = chemicalsMapper.toEntity(chemicalsDTO);
        chemicals = chemicalsRepository.save(chemicals);
        ChemicalsDTO result = chemicalsMapper.toDto(chemicals);
        chemicalsSearchRepository.save(chemicals);
        return ResponseEntity.created(new URI("/api/chemicals/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /chemicals : Updates an existing chemicals.
     *
     * @param chemicalsDTO the chemicalsDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated chemicalsDTO,
     * or with status 400 (Bad Request) if the chemicalsDTO is not valid,
     * or with status 500 (Internal Server Error) if the chemicalsDTO couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/chemicals")
    @Timed
    public ResponseEntity<ChemicalsDTO> updateChemicals(@Valid @RequestBody ChemicalsDTO chemicalsDTO) throws URISyntaxException {
        log.debug("REST request to update Chemicals : {}", chemicalsDTO);
        if (chemicalsDTO.getId() == null) {
            return createChemicals(chemicalsDTO);
        }
        Chemicals chemicals = chemicalsMapper.toEntity(chemicalsDTO);
        chemicals = chemicalsRepository.save(chemicals);
        ChemicalsDTO result = chemicalsMapper.toDto(chemicals);
        chemicalsSearchRepository.save(chemicals);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, chemicalsDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /chemicals : get all the chemicals.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of chemicals in body
     */
    @GetMapping("/chemicals")
    @Timed
    public ResponseEntity<List<ChemicalsDTO>> getAllChemicals(Pageable pageable) {
        log.debug("REST request to get a page of Chemicals");
        Page<Chemicals> page = chemicalsRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/chemicals");
        return new ResponseEntity<>(chemicalsMapper.toDto(page.getContent()), headers, HttpStatus.OK);
    }

    /**
     * GET  /chemicals/:id : get the "id" chemicals.
     *
     * @param id the id of the chemicalsDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the chemicalsDTO, or with status 404 (Not Found)
     */
    @GetMapping("/chemicals/{id}")
    @Timed
    public ResponseEntity<ChemicalsDTO> getChemicals(@PathVariable Long id) {
        log.debug("REST request to get Chemicals : {}", id);
        Chemicals chemicals = chemicalsRepository.findOne(id);
        ChemicalsDTO chemicalsDTO = chemicalsMapper.toDto(chemicals);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(chemicalsDTO));
    }

    /**
     * DELETE  /chemicals/:id : delete the "id" chemicals.
     *
     * @param id the id of the chemicalsDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/chemicals/{id}")
    @Timed
    public ResponseEntity<Void> deleteChemicals(@PathVariable Long id) {
        log.debug("REST request to delete Chemicals : {}", id);
        chemicalsRepository.delete(id);
        chemicalsSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/chemicals?query=:query : search for the chemicals corresponding
     * to the query.
     *
     * @param query the query of the chemicals search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/chemicals")
    @Timed
    public ResponseEntity<List<ChemicalsDTO>> searchChemicals(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of Chemicals for query {}", query);
        Page<Chemicals> page = chemicalsSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/chemicals");
        return new ResponseEntity<>(chemicalsMapper.toDto(page.getContent()), headers, HttpStatus.OK);
    }

}
