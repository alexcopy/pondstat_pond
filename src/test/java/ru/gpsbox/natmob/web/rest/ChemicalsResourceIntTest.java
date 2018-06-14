package ru.gpsbox.natmob.web.rest;

import ru.gpsbox.natmob.PondApp;

import ru.gpsbox.natmob.domain.Chemicals;
import ru.gpsbox.natmob.repository.ChemicalsRepository;
import ru.gpsbox.natmob.repository.search.ChemicalsSearchRepository;
import ru.gpsbox.natmob.service.dto.ChemicalsDTO;
import ru.gpsbox.natmob.service.mapper.ChemicalsMapper;
import ru.gpsbox.natmob.web.rest.errors.ExceptionTranslator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.ZoneOffset;
import java.time.ZoneId;
import java.util.List;

import static ru.gpsbox.natmob.web.rest.TestUtil.sameInstant;
import static ru.gpsbox.natmob.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the ChemicalsResource REST controller.
 *
 * @see ChemicalsResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = PondApp.class)
public class ChemicalsResourceIntTest {

    private static final ZonedDateTime DEFAULT_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final Integer DEFAULT_QTY = 1;
    private static final Integer UPDATED_QTY = 2;

    private static final String DEFAULT_REASON = "AAAAAAAAAA";
    private static final String UPDATED_REASON = "BBBBBBBBBB";

    private static final Double DEFAULT_TEMP_VAL = 1D;
    private static final Double UPDATED_TEMP_VAL = 2D;

    private static final Integer DEFAULT_TIMESTAMP = 1;
    private static final Integer UPDATED_TIMESTAMP = 2;

    @Autowired
    private ChemicalsRepository chemicalsRepository;

    @Autowired
    private ChemicalsMapper chemicalsMapper;

    @Autowired
    private ChemicalsSearchRepository chemicalsSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restChemicalsMockMvc;

    private Chemicals chemicals;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final ChemicalsResource chemicalsResource = new ChemicalsResource(chemicalsRepository, chemicalsMapper, chemicalsSearchRepository);
        this.restChemicalsMockMvc = MockMvcBuilders.standaloneSetup(chemicalsResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Chemicals createEntity(EntityManager em) {
        Chemicals chemicals = new Chemicals()
            .date(DEFAULT_DATE)
            .qty(DEFAULT_QTY)
            .reason(DEFAULT_REASON)
            .tempVal(DEFAULT_TEMP_VAL)
            .timestamp(DEFAULT_TIMESTAMP);
        return chemicals;
    }

    @Before
    public void initTest() {
        chemicalsSearchRepository.deleteAll();
        chemicals = createEntity(em);
    }

    @Test
    @Transactional
    public void createChemicals() throws Exception {
        int databaseSizeBeforeCreate = chemicalsRepository.findAll().size();

        // Create the Chemicals
        ChemicalsDTO chemicalsDTO = chemicalsMapper.toDto(chemicals);
        restChemicalsMockMvc.perform(post("/api/chemicals")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(chemicalsDTO)))
            .andExpect(status().isCreated());

        // Validate the Chemicals in the database
        List<Chemicals> chemicalsList = chemicalsRepository.findAll();
        assertThat(chemicalsList).hasSize(databaseSizeBeforeCreate + 1);
        Chemicals testChemicals = chemicalsList.get(chemicalsList.size() - 1);
        assertThat(testChemicals.getDate()).isEqualTo(DEFAULT_DATE);
        assertThat(testChemicals.getQty()).isEqualTo(DEFAULT_QTY);
        assertThat(testChemicals.getReason()).isEqualTo(DEFAULT_REASON);
        assertThat(testChemicals.getTempVal()).isEqualTo(DEFAULT_TEMP_VAL);
        assertThat(testChemicals.getTimestamp()).isEqualTo(DEFAULT_TIMESTAMP);

        // Validate the Chemicals in Elasticsearch
        Chemicals chemicalsEs = chemicalsSearchRepository.findOne(testChemicals.getId());
        assertThat(testChemicals.getDate()).isEqualTo(testChemicals.getDate());
        assertThat(chemicalsEs).isEqualToIgnoringGivenFields(testChemicals, "date");
    }

    @Test
    @Transactional
    public void createChemicalsWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = chemicalsRepository.findAll().size();

        // Create the Chemicals with an existing ID
        chemicals.setId(1L);
        ChemicalsDTO chemicalsDTO = chemicalsMapper.toDto(chemicals);

        // An entity with an existing ID cannot be created, so this API call must fail
        restChemicalsMockMvc.perform(post("/api/chemicals")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(chemicalsDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Chemicals in the database
        List<Chemicals> chemicalsList = chemicalsRepository.findAll();
        assertThat(chemicalsList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = chemicalsRepository.findAll().size();
        // set the field null
        chemicals.setDate(null);

        // Create the Chemicals, which fails.
        ChemicalsDTO chemicalsDTO = chemicalsMapper.toDto(chemicals);

        restChemicalsMockMvc.perform(post("/api/chemicals")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(chemicalsDTO)))
            .andExpect(status().isBadRequest());

        List<Chemicals> chemicalsList = chemicalsRepository.findAll();
        assertThat(chemicalsList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkTempValIsRequired() throws Exception {
        int databaseSizeBeforeTest = chemicalsRepository.findAll().size();
        // set the field null
        chemicals.setTempVal(null);

        // Create the Chemicals, which fails.
        ChemicalsDTO chemicalsDTO = chemicalsMapper.toDto(chemicals);

        restChemicalsMockMvc.perform(post("/api/chemicals")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(chemicalsDTO)))
            .andExpect(status().isBadRequest());

        List<Chemicals> chemicalsList = chemicalsRepository.findAll();
        assertThat(chemicalsList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllChemicals() throws Exception {
        // Initialize the database
        chemicalsRepository.saveAndFlush(chemicals);

        // Get all the chemicalsList
        restChemicalsMockMvc.perform(get("/api/chemicals?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(chemicals.getId().intValue())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(sameInstant(DEFAULT_DATE))))
            .andExpect(jsonPath("$.[*].qty").value(hasItem(DEFAULT_QTY)))
            .andExpect(jsonPath("$.[*].reason").value(hasItem(DEFAULT_REASON.toString())))
            .andExpect(jsonPath("$.[*].tempVal").value(hasItem(DEFAULT_TEMP_VAL.doubleValue())))
            .andExpect(jsonPath("$.[*].timestamp").value(hasItem(DEFAULT_TIMESTAMP)));
    }

    @Test
    @Transactional
    public void getChemicals() throws Exception {
        // Initialize the database
        chemicalsRepository.saveAndFlush(chemicals);

        // Get the chemicals
        restChemicalsMockMvc.perform(get("/api/chemicals/{id}", chemicals.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(chemicals.getId().intValue()))
            .andExpect(jsonPath("$.date").value(sameInstant(DEFAULT_DATE)))
            .andExpect(jsonPath("$.qty").value(DEFAULT_QTY))
            .andExpect(jsonPath("$.reason").value(DEFAULT_REASON.toString()))
            .andExpect(jsonPath("$.tempVal").value(DEFAULT_TEMP_VAL.doubleValue()))
            .andExpect(jsonPath("$.timestamp").value(DEFAULT_TIMESTAMP));
    }

    @Test
    @Transactional
    public void getNonExistingChemicals() throws Exception {
        // Get the chemicals
        restChemicalsMockMvc.perform(get("/api/chemicals/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateChemicals() throws Exception {
        // Initialize the database
        chemicalsRepository.saveAndFlush(chemicals);
        chemicalsSearchRepository.save(chemicals);
        int databaseSizeBeforeUpdate = chemicalsRepository.findAll().size();

        // Update the chemicals
        Chemicals updatedChemicals = chemicalsRepository.findOne(chemicals.getId());
        // Disconnect from session so that the updates on updatedChemicals are not directly saved in db
        em.detach(updatedChemicals);
        updatedChemicals
            .date(UPDATED_DATE)
            .qty(UPDATED_QTY)
            .reason(UPDATED_REASON)
            .tempVal(UPDATED_TEMP_VAL)
            .timestamp(UPDATED_TIMESTAMP);
        ChemicalsDTO chemicalsDTO = chemicalsMapper.toDto(updatedChemicals);

        restChemicalsMockMvc.perform(put("/api/chemicals")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(chemicalsDTO)))
            .andExpect(status().isOk());

        // Validate the Chemicals in the database
        List<Chemicals> chemicalsList = chemicalsRepository.findAll();
        assertThat(chemicalsList).hasSize(databaseSizeBeforeUpdate);
        Chemicals testChemicals = chemicalsList.get(chemicalsList.size() - 1);
        assertThat(testChemicals.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testChemicals.getQty()).isEqualTo(UPDATED_QTY);
        assertThat(testChemicals.getReason()).isEqualTo(UPDATED_REASON);
        assertThat(testChemicals.getTempVal()).isEqualTo(UPDATED_TEMP_VAL);
        assertThat(testChemicals.getTimestamp()).isEqualTo(UPDATED_TIMESTAMP);

        // Validate the Chemicals in Elasticsearch
        Chemicals chemicalsEs = chemicalsSearchRepository.findOne(testChemicals.getId());
        assertThat(testChemicals.getDate()).isEqualTo(testChemicals.getDate());
        assertThat(chemicalsEs).isEqualToIgnoringGivenFields(testChemicals, "date");
    }

    @Test
    @Transactional
    public void updateNonExistingChemicals() throws Exception {
        int databaseSizeBeforeUpdate = chemicalsRepository.findAll().size();

        // Create the Chemicals
        ChemicalsDTO chemicalsDTO = chemicalsMapper.toDto(chemicals);

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restChemicalsMockMvc.perform(put("/api/chemicals")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(chemicalsDTO)))
            .andExpect(status().isCreated());

        // Validate the Chemicals in the database
        List<Chemicals> chemicalsList = chemicalsRepository.findAll();
        assertThat(chemicalsList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteChemicals() throws Exception {
        // Initialize the database
        chemicalsRepository.saveAndFlush(chemicals);
        chemicalsSearchRepository.save(chemicals);
        int databaseSizeBeforeDelete = chemicalsRepository.findAll().size();

        // Get the chemicals
        restChemicalsMockMvc.perform(delete("/api/chemicals/{id}", chemicals.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate Elasticsearch is empty
        boolean chemicalsExistsInEs = chemicalsSearchRepository.exists(chemicals.getId());
        assertThat(chemicalsExistsInEs).isFalse();

        // Validate the database is empty
        List<Chemicals> chemicalsList = chemicalsRepository.findAll();
        assertThat(chemicalsList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchChemicals() throws Exception {
        // Initialize the database
        chemicalsRepository.saveAndFlush(chemicals);
        chemicalsSearchRepository.save(chemicals);

        // Search the chemicals
        restChemicalsMockMvc.perform(get("/api/_search/chemicals?query=id:" + chemicals.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(chemicals.getId().intValue())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(sameInstant(DEFAULT_DATE))))
            .andExpect(jsonPath("$.[*].qty").value(hasItem(DEFAULT_QTY)))
            .andExpect(jsonPath("$.[*].reason").value(hasItem(DEFAULT_REASON.toString())))
            .andExpect(jsonPath("$.[*].tempVal").value(hasItem(DEFAULT_TEMP_VAL.doubleValue())))
            .andExpect(jsonPath("$.[*].timestamp").value(hasItem(DEFAULT_TIMESTAMP)));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Chemicals.class);
        Chemicals chemicals1 = new Chemicals();
        chemicals1.setId(1L);
        Chemicals chemicals2 = new Chemicals();
        chemicals2.setId(chemicals1.getId());
        assertThat(chemicals1).isEqualTo(chemicals2);
        chemicals2.setId(2L);
        assertThat(chemicals1).isNotEqualTo(chemicals2);
        chemicals1.setId(null);
        assertThat(chemicals1).isNotEqualTo(chemicals2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ChemicalsDTO.class);
        ChemicalsDTO chemicalsDTO1 = new ChemicalsDTO();
        chemicalsDTO1.setId(1L);
        ChemicalsDTO chemicalsDTO2 = new ChemicalsDTO();
        assertThat(chemicalsDTO1).isNotEqualTo(chemicalsDTO2);
        chemicalsDTO2.setId(chemicalsDTO1.getId());
        assertThat(chemicalsDTO1).isEqualTo(chemicalsDTO2);
        chemicalsDTO2.setId(2L);
        assertThat(chemicalsDTO1).isNotEqualTo(chemicalsDTO2);
        chemicalsDTO1.setId(null);
        assertThat(chemicalsDTO1).isNotEqualTo(chemicalsDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(chemicalsMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(chemicalsMapper.fromId(null)).isNull();
    }
}
