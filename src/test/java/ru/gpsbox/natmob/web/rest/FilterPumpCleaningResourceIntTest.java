package ru.gpsbox.natmob.web.rest;

import ru.gpsbox.natmob.PondApp;

import ru.gpsbox.natmob.domain.FilterPumpCleaning;
import ru.gpsbox.natmob.repository.FilterPumpCleaningRepository;
import ru.gpsbox.natmob.repository.search.FilterPumpCleaningSearchRepository;
import ru.gpsbox.natmob.service.dto.FilterPumpCleaningDTO;
import ru.gpsbox.natmob.service.mapper.FilterPumpCleaningMapper;
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
 * Test class for the FilterPumpCleaningResource REST controller.
 *
 * @see FilterPumpCleaningResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = PondApp.class)
public class FilterPumpCleaningResourceIntTest {

    private static final ZonedDateTime DEFAULT_CLEANING_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_CLEANING_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Double DEFAULT_TEMP_VAL = 1D;
    private static final Double UPDATED_TEMP_VAL = 2D;

    private static final Integer DEFAULT_TIMESTAMP = 1;
    private static final Integer UPDATED_TIMESTAMP = 2;

    @Autowired
    private FilterPumpCleaningRepository filterPumpCleaningRepository;

    @Autowired
    private FilterPumpCleaningMapper filterPumpCleaningMapper;

    @Autowired
    private FilterPumpCleaningSearchRepository filterPumpCleaningSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restFilterPumpCleaningMockMvc;

    private FilterPumpCleaning filterPumpCleaning;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final FilterPumpCleaningResource filterPumpCleaningResource = new FilterPumpCleaningResource(filterPumpCleaningRepository, filterPumpCleaningMapper, filterPumpCleaningSearchRepository);
        this.restFilterPumpCleaningMockMvc = MockMvcBuilders.standaloneSetup(filterPumpCleaningResource)
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
    public static FilterPumpCleaning createEntity(EntityManager em) {
        FilterPumpCleaning filterPumpCleaning = new FilterPumpCleaning()
            .cleaningDate(DEFAULT_CLEANING_DATE)
            .description(DEFAULT_DESCRIPTION)
            .tempVal(DEFAULT_TEMP_VAL)
            .timestamp(DEFAULT_TIMESTAMP);
        return filterPumpCleaning;
    }

    @Before
    public void initTest() {
        filterPumpCleaningSearchRepository.deleteAll();
        filterPumpCleaning = createEntity(em);
    }

    @Test
    @Transactional
    public void createFilterPumpCleaning() throws Exception {
        int databaseSizeBeforeCreate = filterPumpCleaningRepository.findAll().size();

        // Create the FilterPumpCleaning
        FilterPumpCleaningDTO filterPumpCleaningDTO = filterPumpCleaningMapper.toDto(filterPumpCleaning);
        restFilterPumpCleaningMockMvc.perform(post("/api/filter-pump-cleanings")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(filterPumpCleaningDTO)))
            .andExpect(status().isCreated());

        // Validate the FilterPumpCleaning in the database
        List<FilterPumpCleaning> filterPumpCleaningList = filterPumpCleaningRepository.findAll();
        assertThat(filterPumpCleaningList).hasSize(databaseSizeBeforeCreate + 1);
        FilterPumpCleaning testFilterPumpCleaning = filterPumpCleaningList.get(filterPumpCleaningList.size() - 1);
        assertThat(testFilterPumpCleaning.getCleaningDate()).isEqualTo(DEFAULT_CLEANING_DATE);
        assertThat(testFilterPumpCleaning.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testFilterPumpCleaning.getTempVal()).isEqualTo(DEFAULT_TEMP_VAL);
        assertThat(testFilterPumpCleaning.getTimestamp()).isEqualTo(DEFAULT_TIMESTAMP);

        // Validate the FilterPumpCleaning in Elasticsearch
        FilterPumpCleaning filterPumpCleaningEs = filterPumpCleaningSearchRepository.findOne(testFilterPumpCleaning.getId());
        assertThat(testFilterPumpCleaning.getCleaningDate()).isEqualTo(testFilterPumpCleaning.getCleaningDate());
        assertThat(filterPumpCleaningEs).isEqualToIgnoringGivenFields(testFilterPumpCleaning, "cleaningDate");
    }

    @Test
    @Transactional
    public void createFilterPumpCleaningWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = filterPumpCleaningRepository.findAll().size();

        // Create the FilterPumpCleaning with an existing ID
        filterPumpCleaning.setId(1L);
        FilterPumpCleaningDTO filterPumpCleaningDTO = filterPumpCleaningMapper.toDto(filterPumpCleaning);

        // An entity with an existing ID cannot be created, so this API call must fail
        restFilterPumpCleaningMockMvc.perform(post("/api/filter-pump-cleanings")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(filterPumpCleaningDTO)))
            .andExpect(status().isBadRequest());

        // Validate the FilterPumpCleaning in the database
        List<FilterPumpCleaning> filterPumpCleaningList = filterPumpCleaningRepository.findAll();
        assertThat(filterPumpCleaningList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkCleaningDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = filterPumpCleaningRepository.findAll().size();
        // set the field null
        filterPumpCleaning.setCleaningDate(null);

        // Create the FilterPumpCleaning, which fails.
        FilterPumpCleaningDTO filterPumpCleaningDTO = filterPumpCleaningMapper.toDto(filterPumpCleaning);

        restFilterPumpCleaningMockMvc.perform(post("/api/filter-pump-cleanings")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(filterPumpCleaningDTO)))
            .andExpect(status().isBadRequest());

        List<FilterPumpCleaning> filterPumpCleaningList = filterPumpCleaningRepository.findAll();
        assertThat(filterPumpCleaningList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkTempValIsRequired() throws Exception {
        int databaseSizeBeforeTest = filterPumpCleaningRepository.findAll().size();
        // set the field null
        filterPumpCleaning.setTempVal(null);

        // Create the FilterPumpCleaning, which fails.
        FilterPumpCleaningDTO filterPumpCleaningDTO = filterPumpCleaningMapper.toDto(filterPumpCleaning);

        restFilterPumpCleaningMockMvc.perform(post("/api/filter-pump-cleanings")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(filterPumpCleaningDTO)))
            .andExpect(status().isBadRequest());

        List<FilterPumpCleaning> filterPumpCleaningList = filterPumpCleaningRepository.findAll();
        assertThat(filterPumpCleaningList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllFilterPumpCleanings() throws Exception {
        // Initialize the database
        filterPumpCleaningRepository.saveAndFlush(filterPumpCleaning);

        // Get all the filterPumpCleaningList
        restFilterPumpCleaningMockMvc.perform(get("/api/filter-pump-cleanings?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(filterPumpCleaning.getId().intValue())))
            .andExpect(jsonPath("$.[*].cleaningDate").value(hasItem(sameInstant(DEFAULT_CLEANING_DATE))))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].tempVal").value(hasItem(DEFAULT_TEMP_VAL.doubleValue())))
            .andExpect(jsonPath("$.[*].timestamp").value(hasItem(DEFAULT_TIMESTAMP)));
    }

    @Test
    @Transactional
    public void getFilterPumpCleaning() throws Exception {
        // Initialize the database
        filterPumpCleaningRepository.saveAndFlush(filterPumpCleaning);

        // Get the filterPumpCleaning
        restFilterPumpCleaningMockMvc.perform(get("/api/filter-pump-cleanings/{id}", filterPumpCleaning.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(filterPumpCleaning.getId().intValue()))
            .andExpect(jsonPath("$.cleaningDate").value(sameInstant(DEFAULT_CLEANING_DATE)))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.tempVal").value(DEFAULT_TEMP_VAL.doubleValue()))
            .andExpect(jsonPath("$.timestamp").value(DEFAULT_TIMESTAMP));
    }

    @Test
    @Transactional
    public void getNonExistingFilterPumpCleaning() throws Exception {
        // Get the filterPumpCleaning
        restFilterPumpCleaningMockMvc.perform(get("/api/filter-pump-cleanings/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateFilterPumpCleaning() throws Exception {
        // Initialize the database
        filterPumpCleaningRepository.saveAndFlush(filterPumpCleaning);
        filterPumpCleaningSearchRepository.save(filterPumpCleaning);
        int databaseSizeBeforeUpdate = filterPumpCleaningRepository.findAll().size();

        // Update the filterPumpCleaning
        FilterPumpCleaning updatedFilterPumpCleaning = filterPumpCleaningRepository.findOne(filterPumpCleaning.getId());
        // Disconnect from session so that the updates on updatedFilterPumpCleaning are not directly saved in db
        em.detach(updatedFilterPumpCleaning);
        updatedFilterPumpCleaning
            .cleaningDate(UPDATED_CLEANING_DATE)
            .description(UPDATED_DESCRIPTION)
            .tempVal(UPDATED_TEMP_VAL)
            .timestamp(UPDATED_TIMESTAMP);
        FilterPumpCleaningDTO filterPumpCleaningDTO = filterPumpCleaningMapper.toDto(updatedFilterPumpCleaning);

        restFilterPumpCleaningMockMvc.perform(put("/api/filter-pump-cleanings")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(filterPumpCleaningDTO)))
            .andExpect(status().isOk());

        // Validate the FilterPumpCleaning in the database
        List<FilterPumpCleaning> filterPumpCleaningList = filterPumpCleaningRepository.findAll();
        assertThat(filterPumpCleaningList).hasSize(databaseSizeBeforeUpdate);
        FilterPumpCleaning testFilterPumpCleaning = filterPumpCleaningList.get(filterPumpCleaningList.size() - 1);
        assertThat(testFilterPumpCleaning.getCleaningDate()).isEqualTo(UPDATED_CLEANING_DATE);
        assertThat(testFilterPumpCleaning.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testFilterPumpCleaning.getTempVal()).isEqualTo(UPDATED_TEMP_VAL);
        assertThat(testFilterPumpCleaning.getTimestamp()).isEqualTo(UPDATED_TIMESTAMP);

        // Validate the FilterPumpCleaning in Elasticsearch
        FilterPumpCleaning filterPumpCleaningEs = filterPumpCleaningSearchRepository.findOne(testFilterPumpCleaning.getId());
        assertThat(testFilterPumpCleaning.getCleaningDate()).isEqualTo(testFilterPumpCleaning.getCleaningDate());
        assertThat(filterPumpCleaningEs).isEqualToIgnoringGivenFields(testFilterPumpCleaning, "cleaningDate");
    }

    @Test
    @Transactional
    public void updateNonExistingFilterPumpCleaning() throws Exception {
        int databaseSizeBeforeUpdate = filterPumpCleaningRepository.findAll().size();

        // Create the FilterPumpCleaning
        FilterPumpCleaningDTO filterPumpCleaningDTO = filterPumpCleaningMapper.toDto(filterPumpCleaning);

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restFilterPumpCleaningMockMvc.perform(put("/api/filter-pump-cleanings")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(filterPumpCleaningDTO)))
            .andExpect(status().isCreated());

        // Validate the FilterPumpCleaning in the database
        List<FilterPumpCleaning> filterPumpCleaningList = filterPumpCleaningRepository.findAll();
        assertThat(filterPumpCleaningList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteFilterPumpCleaning() throws Exception {
        // Initialize the database
        filterPumpCleaningRepository.saveAndFlush(filterPumpCleaning);
        filterPumpCleaningSearchRepository.save(filterPumpCleaning);
        int databaseSizeBeforeDelete = filterPumpCleaningRepository.findAll().size();

        // Get the filterPumpCleaning
        restFilterPumpCleaningMockMvc.perform(delete("/api/filter-pump-cleanings/{id}", filterPumpCleaning.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate Elasticsearch is empty
        boolean filterPumpCleaningExistsInEs = filterPumpCleaningSearchRepository.exists(filterPumpCleaning.getId());
        assertThat(filterPumpCleaningExistsInEs).isFalse();

        // Validate the database is empty
        List<FilterPumpCleaning> filterPumpCleaningList = filterPumpCleaningRepository.findAll();
        assertThat(filterPumpCleaningList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchFilterPumpCleaning() throws Exception {
        // Initialize the database
        filterPumpCleaningRepository.saveAndFlush(filterPumpCleaning);
        filterPumpCleaningSearchRepository.save(filterPumpCleaning);

        // Search the filterPumpCleaning
        restFilterPumpCleaningMockMvc.perform(get("/api/_search/filter-pump-cleanings?query=id:" + filterPumpCleaning.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(filterPumpCleaning.getId().intValue())))
            .andExpect(jsonPath("$.[*].cleaningDate").value(hasItem(sameInstant(DEFAULT_CLEANING_DATE))))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].tempVal").value(hasItem(DEFAULT_TEMP_VAL.doubleValue())))
            .andExpect(jsonPath("$.[*].timestamp").value(hasItem(DEFAULT_TIMESTAMP)));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(FilterPumpCleaning.class);
        FilterPumpCleaning filterPumpCleaning1 = new FilterPumpCleaning();
        filterPumpCleaning1.setId(1L);
        FilterPumpCleaning filterPumpCleaning2 = new FilterPumpCleaning();
        filterPumpCleaning2.setId(filterPumpCleaning1.getId());
        assertThat(filterPumpCleaning1).isEqualTo(filterPumpCleaning2);
        filterPumpCleaning2.setId(2L);
        assertThat(filterPumpCleaning1).isNotEqualTo(filterPumpCleaning2);
        filterPumpCleaning1.setId(null);
        assertThat(filterPumpCleaning1).isNotEqualTo(filterPumpCleaning2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(FilterPumpCleaningDTO.class);
        FilterPumpCleaningDTO filterPumpCleaningDTO1 = new FilterPumpCleaningDTO();
        filterPumpCleaningDTO1.setId(1L);
        FilterPumpCleaningDTO filterPumpCleaningDTO2 = new FilterPumpCleaningDTO();
        assertThat(filterPumpCleaningDTO1).isNotEqualTo(filterPumpCleaningDTO2);
        filterPumpCleaningDTO2.setId(filterPumpCleaningDTO1.getId());
        assertThat(filterPumpCleaningDTO1).isEqualTo(filterPumpCleaningDTO2);
        filterPumpCleaningDTO2.setId(2L);
        assertThat(filterPumpCleaningDTO1).isNotEqualTo(filterPumpCleaningDTO2);
        filterPumpCleaningDTO1.setId(null);
        assertThat(filterPumpCleaningDTO1).isNotEqualTo(filterPumpCleaningDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(filterPumpCleaningMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(filterPumpCleaningMapper.fromId(null)).isNull();
    }
}
