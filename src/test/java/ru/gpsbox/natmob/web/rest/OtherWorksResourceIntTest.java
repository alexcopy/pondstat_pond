package ru.gpsbox.natmob.web.rest;

import ru.gpsbox.natmob.PondApp;

import ru.gpsbox.natmob.domain.OtherWorks;
import ru.gpsbox.natmob.repository.OtherWorksRepository;
import ru.gpsbox.natmob.repository.search.OtherWorksSearchRepository;
import ru.gpsbox.natmob.service.dto.OtherWorksDTO;
import ru.gpsbox.natmob.service.mapper.OtherWorksMapper;
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
 * Test class for the OtherWorksResource REST controller.
 *
 * @see OtherWorksResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = PondApp.class)
public class OtherWorksResourceIntTest {

    private static final ZonedDateTime DEFAULT_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final String DEFAULT_REASON = "AAAAAAAAAA";
    private static final String UPDATED_REASON = "BBBBBBBBBB";

    private static final Integer DEFAULT_QTY = 1;
    private static final Integer UPDATED_QTY = 2;

    private static final String DEFAULT_DESCRIPTON = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTON = "BBBBBBBBBB";

    private static final Double DEFAULT_TEMP_VAL = 1D;
    private static final Double UPDATED_TEMP_VAL = 2D;

    private static final Integer DEFAULT_TIMESTAMP = 1;
    private static final Integer UPDATED_TIMESTAMP = 2;

    @Autowired
    private OtherWorksRepository otherWorksRepository;

    @Autowired
    private OtherWorksMapper otherWorksMapper;

    @Autowired
    private OtherWorksSearchRepository otherWorksSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restOtherWorksMockMvc;

    private OtherWorks otherWorks;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final OtherWorksResource otherWorksResource = new OtherWorksResource(otherWorksRepository, otherWorksMapper, otherWorksSearchRepository);
        this.restOtherWorksMockMvc = MockMvcBuilders.standaloneSetup(otherWorksResource)
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
    public static OtherWorks createEntity(EntityManager em) {
        OtherWorks otherWorks = new OtherWorks()
            .date(DEFAULT_DATE)
            .reason(DEFAULT_REASON)
            .qty(DEFAULT_QTY)
            .descripton(DEFAULT_DESCRIPTON)
            .tempVal(DEFAULT_TEMP_VAL)
            .timestamp(DEFAULT_TIMESTAMP);
        return otherWorks;
    }

    @Before
    public void initTest() {
        otherWorksSearchRepository.deleteAll();
        otherWorks = createEntity(em);
    }

    @Test
    @Transactional
    public void createOtherWorks() throws Exception {
        int databaseSizeBeforeCreate = otherWorksRepository.findAll().size();

        // Create the OtherWorks
        OtherWorksDTO otherWorksDTO = otherWorksMapper.toDto(otherWorks);
        restOtherWorksMockMvc.perform(post("/api/other-works")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(otherWorksDTO)))
            .andExpect(status().isCreated());

        // Validate the OtherWorks in the database
        List<OtherWorks> otherWorksList = otherWorksRepository.findAll();
        assertThat(otherWorksList).hasSize(databaseSizeBeforeCreate + 1);
        OtherWorks testOtherWorks = otherWorksList.get(otherWorksList.size() - 1);
        assertThat(testOtherWorks.getDate()).isEqualTo(DEFAULT_DATE);
        assertThat(testOtherWorks.getReason()).isEqualTo(DEFAULT_REASON);
        assertThat(testOtherWorks.getQty()).isEqualTo(DEFAULT_QTY);
        assertThat(testOtherWorks.getDescripton()).isEqualTo(DEFAULT_DESCRIPTON);
        assertThat(testOtherWorks.getTempVal()).isEqualTo(DEFAULT_TEMP_VAL);
        assertThat(testOtherWorks.getTimestamp()).isEqualTo(DEFAULT_TIMESTAMP);

        // Validate the OtherWorks in Elasticsearch
        OtherWorks otherWorksEs = otherWorksSearchRepository.findOne(testOtherWorks.getId());
        assertThat(testOtherWorks.getDate()).isEqualTo(testOtherWorks.getDate());
        assertThat(otherWorksEs).isEqualToIgnoringGivenFields(testOtherWorks, "date");
    }

    @Test
    @Transactional
    public void createOtherWorksWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = otherWorksRepository.findAll().size();

        // Create the OtherWorks with an existing ID
        otherWorks.setId(1L);
        OtherWorksDTO otherWorksDTO = otherWorksMapper.toDto(otherWorks);

        // An entity with an existing ID cannot be created, so this API call must fail
        restOtherWorksMockMvc.perform(post("/api/other-works")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(otherWorksDTO)))
            .andExpect(status().isBadRequest());

        // Validate the OtherWorks in the database
        List<OtherWorks> otherWorksList = otherWorksRepository.findAll();
        assertThat(otherWorksList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = otherWorksRepository.findAll().size();
        // set the field null
        otherWorks.setDate(null);

        // Create the OtherWorks, which fails.
        OtherWorksDTO otherWorksDTO = otherWorksMapper.toDto(otherWorks);

        restOtherWorksMockMvc.perform(post("/api/other-works")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(otherWorksDTO)))
            .andExpect(status().isBadRequest());

        List<OtherWorks> otherWorksList = otherWorksRepository.findAll();
        assertThat(otherWorksList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkTempValIsRequired() throws Exception {
        int databaseSizeBeforeTest = otherWorksRepository.findAll().size();
        // set the field null
        otherWorks.setTempVal(null);

        // Create the OtherWorks, which fails.
        OtherWorksDTO otherWorksDTO = otherWorksMapper.toDto(otherWorks);

        restOtherWorksMockMvc.perform(post("/api/other-works")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(otherWorksDTO)))
            .andExpect(status().isBadRequest());

        List<OtherWorks> otherWorksList = otherWorksRepository.findAll();
        assertThat(otherWorksList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllOtherWorks() throws Exception {
        // Initialize the database
        otherWorksRepository.saveAndFlush(otherWorks);

        // Get all the otherWorksList
        restOtherWorksMockMvc.perform(get("/api/other-works?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(otherWorks.getId().intValue())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(sameInstant(DEFAULT_DATE))))
            .andExpect(jsonPath("$.[*].reason").value(hasItem(DEFAULT_REASON.toString())))
            .andExpect(jsonPath("$.[*].qty").value(hasItem(DEFAULT_QTY)))
            .andExpect(jsonPath("$.[*].descripton").value(hasItem(DEFAULT_DESCRIPTON.toString())))
            .andExpect(jsonPath("$.[*].tempVal").value(hasItem(DEFAULT_TEMP_VAL.doubleValue())))
            .andExpect(jsonPath("$.[*].timestamp").value(hasItem(DEFAULT_TIMESTAMP)));
    }

    @Test
    @Transactional
    public void getOtherWorks() throws Exception {
        // Initialize the database
        otherWorksRepository.saveAndFlush(otherWorks);

        // Get the otherWorks
        restOtherWorksMockMvc.perform(get("/api/other-works/{id}", otherWorks.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(otherWorks.getId().intValue()))
            .andExpect(jsonPath("$.date").value(sameInstant(DEFAULT_DATE)))
            .andExpect(jsonPath("$.reason").value(DEFAULT_REASON.toString()))
            .andExpect(jsonPath("$.qty").value(DEFAULT_QTY))
            .andExpect(jsonPath("$.descripton").value(DEFAULT_DESCRIPTON.toString()))
            .andExpect(jsonPath("$.tempVal").value(DEFAULT_TEMP_VAL.doubleValue()))
            .andExpect(jsonPath("$.timestamp").value(DEFAULT_TIMESTAMP));
    }

    @Test
    @Transactional
    public void getNonExistingOtherWorks() throws Exception {
        // Get the otherWorks
        restOtherWorksMockMvc.perform(get("/api/other-works/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateOtherWorks() throws Exception {
        // Initialize the database
        otherWorksRepository.saveAndFlush(otherWorks);
        otherWorksSearchRepository.save(otherWorks);
        int databaseSizeBeforeUpdate = otherWorksRepository.findAll().size();

        // Update the otherWorks
        OtherWorks updatedOtherWorks = otherWorksRepository.findOne(otherWorks.getId());
        // Disconnect from session so that the updates on updatedOtherWorks are not directly saved in db
        em.detach(updatedOtherWorks);
        updatedOtherWorks
            .date(UPDATED_DATE)
            .reason(UPDATED_REASON)
            .qty(UPDATED_QTY)
            .descripton(UPDATED_DESCRIPTON)
            .tempVal(UPDATED_TEMP_VAL)
            .timestamp(UPDATED_TIMESTAMP);
        OtherWorksDTO otherWorksDTO = otherWorksMapper.toDto(updatedOtherWorks);

        restOtherWorksMockMvc.perform(put("/api/other-works")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(otherWorksDTO)))
            .andExpect(status().isOk());

        // Validate the OtherWorks in the database
        List<OtherWorks> otherWorksList = otherWorksRepository.findAll();
        assertThat(otherWorksList).hasSize(databaseSizeBeforeUpdate);
        OtherWorks testOtherWorks = otherWorksList.get(otherWorksList.size() - 1);
        assertThat(testOtherWorks.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testOtherWorks.getReason()).isEqualTo(UPDATED_REASON);
        assertThat(testOtherWorks.getQty()).isEqualTo(UPDATED_QTY);
        assertThat(testOtherWorks.getDescripton()).isEqualTo(UPDATED_DESCRIPTON);
        assertThat(testOtherWorks.getTempVal()).isEqualTo(UPDATED_TEMP_VAL);
        assertThat(testOtherWorks.getTimestamp()).isEqualTo(UPDATED_TIMESTAMP);

        // Validate the OtherWorks in Elasticsearch
        OtherWorks otherWorksEs = otherWorksSearchRepository.findOne(testOtherWorks.getId());
        assertThat(testOtherWorks.getDate()).isEqualTo(testOtherWorks.getDate());
        assertThat(otherWorksEs).isEqualToIgnoringGivenFields(testOtherWorks, "date");
    }

    @Test
    @Transactional
    public void updateNonExistingOtherWorks() throws Exception {
        int databaseSizeBeforeUpdate = otherWorksRepository.findAll().size();

        // Create the OtherWorks
        OtherWorksDTO otherWorksDTO = otherWorksMapper.toDto(otherWorks);

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restOtherWorksMockMvc.perform(put("/api/other-works")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(otherWorksDTO)))
            .andExpect(status().isCreated());

        // Validate the OtherWorks in the database
        List<OtherWorks> otherWorksList = otherWorksRepository.findAll();
        assertThat(otherWorksList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteOtherWorks() throws Exception {
        // Initialize the database
        otherWorksRepository.saveAndFlush(otherWorks);
        otherWorksSearchRepository.save(otherWorks);
        int databaseSizeBeforeDelete = otherWorksRepository.findAll().size();

        // Get the otherWorks
        restOtherWorksMockMvc.perform(delete("/api/other-works/{id}", otherWorks.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate Elasticsearch is empty
        boolean otherWorksExistsInEs = otherWorksSearchRepository.exists(otherWorks.getId());
        assertThat(otherWorksExistsInEs).isFalse();

        // Validate the database is empty
        List<OtherWorks> otherWorksList = otherWorksRepository.findAll();
        assertThat(otherWorksList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchOtherWorks() throws Exception {
        // Initialize the database
        otherWorksRepository.saveAndFlush(otherWorks);
        otherWorksSearchRepository.save(otherWorks);

        // Search the otherWorks
        restOtherWorksMockMvc.perform(get("/api/_search/other-works?query=id:" + otherWorks.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(otherWorks.getId().intValue())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(sameInstant(DEFAULT_DATE))))
            .andExpect(jsonPath("$.[*].reason").value(hasItem(DEFAULT_REASON.toString())))
            .andExpect(jsonPath("$.[*].qty").value(hasItem(DEFAULT_QTY)))
            .andExpect(jsonPath("$.[*].descripton").value(hasItem(DEFAULT_DESCRIPTON.toString())))
            .andExpect(jsonPath("$.[*].tempVal").value(hasItem(DEFAULT_TEMP_VAL.doubleValue())))
            .andExpect(jsonPath("$.[*].timestamp").value(hasItem(DEFAULT_TIMESTAMP)));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(OtherWorks.class);
        OtherWorks otherWorks1 = new OtherWorks();
        otherWorks1.setId(1L);
        OtherWorks otherWorks2 = new OtherWorks();
        otherWorks2.setId(otherWorks1.getId());
        assertThat(otherWorks1).isEqualTo(otherWorks2);
        otherWorks2.setId(2L);
        assertThat(otherWorks1).isNotEqualTo(otherWorks2);
        otherWorks1.setId(null);
        assertThat(otherWorks1).isNotEqualTo(otherWorks2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(OtherWorksDTO.class);
        OtherWorksDTO otherWorksDTO1 = new OtherWorksDTO();
        otherWorksDTO1.setId(1L);
        OtherWorksDTO otherWorksDTO2 = new OtherWorksDTO();
        assertThat(otherWorksDTO1).isNotEqualTo(otherWorksDTO2);
        otherWorksDTO2.setId(otherWorksDTO1.getId());
        assertThat(otherWorksDTO1).isEqualTo(otherWorksDTO2);
        otherWorksDTO2.setId(2L);
        assertThat(otherWorksDTO1).isNotEqualTo(otherWorksDTO2);
        otherWorksDTO1.setId(null);
        assertThat(otherWorksDTO1).isNotEqualTo(otherWorksDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(otherWorksMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(otherWorksMapper.fromId(null)).isNull();
    }
}
