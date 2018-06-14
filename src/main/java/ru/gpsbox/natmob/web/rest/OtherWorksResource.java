package ru.gpsbox.natmob.web.rest;

import com.codahale.metrics.annotation.Timed;
import ru.gpsbox.natmob.domain.OtherWorks;

import ru.gpsbox.natmob.repository.OtherWorksRepository;
import ru.gpsbox.natmob.repository.search.OtherWorksSearchRepository;
import ru.gpsbox.natmob.web.rest.errors.BadRequestAlertException;
import ru.gpsbox.natmob.web.rest.util.HeaderUtil;
import ru.gpsbox.natmob.service.dto.OtherWorksDTO;
import ru.gpsbox.natmob.service.mapper.OtherWorksMapper;
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
 * REST controller for managing OtherWorks.
 */
@RestController
@RequestMapping("/api")
public class OtherWorksResource {

    private final Logger log = LoggerFactory.getLogger(OtherWorksResource.class);

    private static final String ENTITY_NAME = "otherWorks";

    private final OtherWorksRepository otherWorksRepository;

    private final OtherWorksMapper otherWorksMapper;

    private final OtherWorksSearchRepository otherWorksSearchRepository;

    public OtherWorksResource(OtherWorksRepository otherWorksRepository, OtherWorksMapper otherWorksMapper, OtherWorksSearchRepository otherWorksSearchRepository) {
        this.otherWorksRepository = otherWorksRepository;
        this.otherWorksMapper = otherWorksMapper;
        this.otherWorksSearchRepository = otherWorksSearchRepository;
    }

    /**
     * POST  /other-works : Create a new otherWorks.
     *
     * @param otherWorksDTO the otherWorksDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new otherWorksDTO, or with status 400 (Bad Request) if the otherWorks has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/other-works")
    @Timed
    public ResponseEntity<OtherWorksDTO> createOtherWorks(@Valid @RequestBody OtherWorksDTO otherWorksDTO) throws URISyntaxException {
        log.debug("REST request to save OtherWorks : {}", otherWorksDTO);
        if (otherWorksDTO.getId() != null) {
            throw new BadRequestAlertException("A new otherWorks cannot already have an ID", ENTITY_NAME, "idexists");
        }
        OtherWorks otherWorks = otherWorksMapper.toEntity(otherWorksDTO);
        otherWorks = otherWorksRepository.save(otherWorks);
        OtherWorksDTO result = otherWorksMapper.toDto(otherWorks);
        otherWorksSearchRepository.save(otherWorks);
        return ResponseEntity.created(new URI("/api/other-works/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /other-works : Updates an existing otherWorks.
     *
     * @param otherWorksDTO the otherWorksDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated otherWorksDTO,
     * or with status 400 (Bad Request) if the otherWorksDTO is not valid,
     * or with status 500 (Internal Server Error) if the otherWorksDTO couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/other-works")
    @Timed
    public ResponseEntity<OtherWorksDTO> updateOtherWorks(@Valid @RequestBody OtherWorksDTO otherWorksDTO) throws URISyntaxException {
        log.debug("REST request to update OtherWorks : {}", otherWorksDTO);
        if (otherWorksDTO.getId() == null) {
            return createOtherWorks(otherWorksDTO);
        }
        OtherWorks otherWorks = otherWorksMapper.toEntity(otherWorksDTO);
        otherWorks = otherWorksRepository.save(otherWorks);
        OtherWorksDTO result = otherWorksMapper.toDto(otherWorks);
        otherWorksSearchRepository.save(otherWorks);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, otherWorksDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /other-works : get all the otherWorks.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of otherWorks in body
     */
    @GetMapping("/other-works")
    @Timed
    public List<OtherWorksDTO> getAllOtherWorks() {
        log.debug("REST request to get all OtherWorks");
        List<OtherWorks> otherWorks = otherWorksRepository.findAll();
        return otherWorksMapper.toDto(otherWorks);
        }

    /**
     * GET  /other-works/:id : get the "id" otherWorks.
     *
     * @param id the id of the otherWorksDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the otherWorksDTO, or with status 404 (Not Found)
     */
    @GetMapping("/other-works/{id}")
    @Timed
    public ResponseEntity<OtherWorksDTO> getOtherWorks(@PathVariable Long id) {
        log.debug("REST request to get OtherWorks : {}", id);
        OtherWorks otherWorks = otherWorksRepository.findOne(id);
        OtherWorksDTO otherWorksDTO = otherWorksMapper.toDto(otherWorks);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(otherWorksDTO));
    }

    /**
     * DELETE  /other-works/:id : delete the "id" otherWorks.
     *
     * @param id the id of the otherWorksDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/other-works/{id}")
    @Timed
    public ResponseEntity<Void> deleteOtherWorks(@PathVariable Long id) {
        log.debug("REST request to delete OtherWorks : {}", id);
        otherWorksRepository.delete(id);
        otherWorksSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/other-works?query=:query : search for the otherWorks corresponding
     * to the query.
     *
     * @param query the query of the otherWorks search
     * @return the result of the search
     */
    @GetMapping("/_search/other-works")
    @Timed
    public List<OtherWorksDTO> searchOtherWorks(@RequestParam String query) {
        log.debug("REST request to search OtherWorks for query {}", query);
        return StreamSupport
            .stream(otherWorksSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .map(otherWorksMapper::toDto)
            .collect(Collectors.toList());
    }

}
