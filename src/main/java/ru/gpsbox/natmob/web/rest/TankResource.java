package ru.gpsbox.natmob.web.rest;

import com.codahale.metrics.annotation.Timed;
import ru.gpsbox.natmob.domain.Tank;

import ru.gpsbox.natmob.repository.TankRepository;
import ru.gpsbox.natmob.repository.search.TankSearchRepository;
import ru.gpsbox.natmob.web.rest.errors.BadRequestAlertException;
import ru.gpsbox.natmob.web.rest.util.HeaderUtil;
import ru.gpsbox.natmob.service.dto.TankDTO;
import ru.gpsbox.natmob.service.mapper.TankMapper;
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
 * REST controller for managing Tank.
 */
@RestController
@RequestMapping("/api")
public class TankResource {

    private final Logger log = LoggerFactory.getLogger(TankResource.class);

    private static final String ENTITY_NAME = "tank";

    private final TankRepository tankRepository;

    private final TankMapper tankMapper;

    private final TankSearchRepository tankSearchRepository;

    public TankResource(TankRepository tankRepository, TankMapper tankMapper, TankSearchRepository tankSearchRepository) {
        this.tankRepository = tankRepository;
        this.tankMapper = tankMapper;
        this.tankSearchRepository = tankSearchRepository;
    }

    /**
     * POST  /tanks : Create a new tank.
     *
     * @param tankDTO the tankDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new tankDTO, or with status 400 (Bad Request) if the tank has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/tanks")
    @Timed
    public ResponseEntity<TankDTO> createTank(@Valid @RequestBody TankDTO tankDTO) throws URISyntaxException {
        log.debug("REST request to save Tank : {}", tankDTO);
        if (tankDTO.getId() != null) {
            throw new BadRequestAlertException("A new tank cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Tank tank = tankMapper.toEntity(tankDTO);
        tank = tankRepository.save(tank);
        TankDTO result = tankMapper.toDto(tank);
        tankSearchRepository.save(tank);
        return ResponseEntity.created(new URI("/api/tanks/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /tanks : Updates an existing tank.
     *
     * @param tankDTO the tankDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated tankDTO,
     * or with status 400 (Bad Request) if the tankDTO is not valid,
     * or with status 500 (Internal Server Error) if the tankDTO couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/tanks")
    @Timed
    public ResponseEntity<TankDTO> updateTank(@Valid @RequestBody TankDTO tankDTO) throws URISyntaxException {
        log.debug("REST request to update Tank : {}", tankDTO);
        if (tankDTO.getId() == null) {
            return createTank(tankDTO);
        }
        Tank tank = tankMapper.toEntity(tankDTO);
        tank = tankRepository.save(tank);
        TankDTO result = tankMapper.toDto(tank);
        tankSearchRepository.save(tank);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, tankDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /tanks : get all the tanks.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of tanks in body
     */
    @GetMapping("/tanks")
    @Timed
    public List<TankDTO> getAllTanks() {
        log.debug("REST request to get all Tanks");
        List<Tank> tanks = tankRepository.findAll();
        return tankMapper.toDto(tanks);
        }

    /**
     * GET  /tanks/:id : get the "id" tank.
     *
     * @param id the id of the tankDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the tankDTO, or with status 404 (Not Found)
     */
    @GetMapping("/tanks/{id}")
    @Timed
    public ResponseEntity<TankDTO> getTank(@PathVariable Long id) {
        log.debug("REST request to get Tank : {}", id);
        Tank tank = tankRepository.findOne(id);
        TankDTO tankDTO = tankMapper.toDto(tank);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(tankDTO));
    }

    /**
     * DELETE  /tanks/:id : delete the "id" tank.
     *
     * @param id the id of the tankDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/tanks/{id}")
    @Timed
    public ResponseEntity<Void> deleteTank(@PathVariable Long id) {
        log.debug("REST request to delete Tank : {}", id);
        tankRepository.delete(id);
        tankSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/tanks?query=:query : search for the tank corresponding
     * to the query.
     *
     * @param query the query of the tank search
     * @return the result of the search
     */
    @GetMapping("/_search/tanks")
    @Timed
    public List<TankDTO> searchTanks(@RequestParam String query) {
        log.debug("REST request to search Tanks for query {}", query);
        return StreamSupport
            .stream(tankSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .map(tankMapper::toDto)
            .collect(Collectors.toList());
    }

}
