package ru.gpsbox.natmob.web.rest;

import ru.gpsbox.natmob.PondApp;

import ru.gpsbox.natmob.domain.WaterChange;
import ru.gpsbox.natmob.repository.WaterChangeRepository;
import ru.gpsbox.natmob.repository.search.WaterChangeSearchRepository;
import ru.gpsbox.natmob.service.dto.WaterChangeDTO;
import ru.gpsbox.natmob.service.mapper.WaterChangeMapper;
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
 * Test class for the WaterChangeResource REST controller.
 *
 * @see WaterChangeResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = PondApp.class)
public class WaterChangeResourceIntTest {

    private static final ZonedDateTime DEFAULT_CHANGE_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_CHANGE_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Double DEFAULT_READING_BEFORE = 1D;
    private static final Double UPDATED_READING_BEFORE = 2D;

    private static final Double DEFAULT_READING_AFTER = 1D;
    private static final Double UPDATED_READING_AFTER = 2D;

    private static final Double DEFAULT_TEMP_VAL = 1D;
    private static final Double UPDATED_TEMP_VAL = 2D;

    private static final Integer DEFAULT_TIMESTAMP = 1;
    private static final Integer UPDATED_TIMESTAMP = 2;

    @Autowired
    private WaterChangeRepository waterChangeRepository;

    @Autowired
    private WaterChangeMapper waterChangeMapper;

    @Autowired
    private WaterChangeSearchRepository waterChangeSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restWaterChangeMockMvc;

    private WaterChange waterChange;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final WaterChangeResource waterChangeResource = new WaterChangeResource(waterChangeRepository, waterChangeMapper, waterChangeSearchRepository);
        this.restWaterChangeMockMvc = MockMvcBuilders.standaloneSetup(waterChangeResource)
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
    public static WaterChange createEntity(EntityManager em) {
        WaterChange waterChange = new WaterChange()
            .changeDate(DEFAULT_CHANGE_DATE)
            .description(DEFAULT_DESCRIPTION)
            .readingBefore(DEFAULT_READING_BEFORE)
            .readingAfter(DEFAULT_READING_AFTER)
            .tempVal(DEFAULT_TEMP_VAL)
            .timestamp(DEFAULT_TIMESTAMP);
        return waterChange;
    }

    @Before
    public void initTest() {
        waterChangeSearchRepository.deleteAll();
        waterChange = createEntity(em);
    }

    @Test
    @Transactional
    public void createWaterChange() throws Exception {
        int databaseSizeBeforeCreate = waterChangeRepository.findAll().size();

        // Create the WaterChange
        WaterChangeDTO waterChangeDTO = waterChangeMapper.toDto(waterChange);
        restWaterChangeMockMvc.perform(post("/api/water-changes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(waterChangeDTO)))
            .andExpect(status().isCreated());

        // Validate the WaterChange in the database
        List<WaterChange> waterChangeList = waterChangeRepository.findAll();
        assertThat(waterChangeList).hasSize(databaseSizeBeforeCreate + 1);
        WaterChange testWaterChange = waterChangeList.get(waterChangeList.size() - 1);
        assertThat(testWaterChange.getChangeDate()).isEqualTo(DEFAULT_CHANGE_DATE);
        assertThat(testWaterChange.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testWaterChange.getReadingBefore()).isEqualTo(DEFAULT_READING_BEFORE);
        assertThat(testWaterChange.getReadingAfter()).isEqualTo(DEFAULT_READING_AFTER);
        assertThat(testWaterChange.getTempVal()).isEqualTo(DEFAULT_TEMP_VAL);
        assertThat(testWaterChange.getTimestamp()).isEqualTo(DEFAULT_TIMESTAMP);

        // Validate the WaterChange in Elasticsearch
        WaterChange waterChangeEs = waterChangeSearchRepository.findOne(testWaterChange.getId());
        assertThat(testWaterChange.getChangeDate()).isEqualTo(testWaterChange.getChangeDate());
        assertThat(waterChangeEs).isEqualToIgnoringGivenFields(testWaterChange, "changeDate");
    }

    @Test
    @Transactional
    public void createWaterChangeWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = waterChangeRepository.findAll().size();

        // Create the WaterChange with an existing ID
        waterChange.setId(1L);
        WaterChangeDTO waterChangeDTO = waterChangeMapper.toDto(waterChange);

        // An entity with an existing ID cannot be created, so this API call must fail
        restWaterChangeMockMvc.perform(post("/api/water-changes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(waterChangeDTO)))
            .andExpect(status().isBadRequest());

        // Validate the WaterChange in the database
        List<WaterChange> waterChangeList = waterChangeRepository.findAll();
        assertThat(waterChangeList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkChangeDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = waterChangeRepository.findAll().size();
        // set the field null
        waterChange.setChangeDate(null);

        // Create the WaterChange, which fails.
        WaterChangeDTO waterChangeDTO = waterChangeMapper.toDto(waterChange);

        restWaterChangeMockMvc.perform(post("/api/water-changes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(waterChangeDTO)))
            .andExpect(status().isBadRequest());

        List<WaterChange> waterChangeList = waterChangeRepository.findAll();
        assertThat(waterChangeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkReadingBeforeIsRequired() throws Exception {
        int databaseSizeBeforeTest = waterChangeRepository.findAll().size();
        // set the field null
        waterChange.setReadingBefore(null);

        // Create the WaterChange, which fails.
        WaterChangeDTO waterChangeDTO = waterChangeMapper.toDto(waterChange);

        restWaterChangeMockMvc.perform(post("/api/water-changes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(waterChangeDTO)))
            .andExpect(status().isBadRequest());

        List<WaterChange> waterChangeList = waterChangeRepository.findAll();
        assertThat(waterChangeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkReadingAfterIsRequired() throws Exception {
        int databaseSizeBeforeTest = waterChangeRepository.findAll().size();
        // set the field null
        waterChange.setReadingAfter(null);

        // Create the WaterChange, which fails.
        WaterChangeDTO waterChangeDTO = waterChangeMapper.toDto(waterChange);

        restWaterChangeMockMvc.perform(post("/api/water-changes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(waterChangeDTO)))
            .andExpect(status().isBadRequest());

        List<WaterChange> waterChangeList = waterChangeRepository.findAll();
        assertThat(waterChangeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkTempValIsRequired() throws Exception {
        int databaseSizeBeforeTest = waterChangeRepository.findAll().size();
        // set the field null
        waterChange.setTempVal(null);

        // Create the WaterChange, which fails.
        WaterChangeDTO waterChangeDTO = waterChangeMapper.toDto(waterChange);

        restWaterChangeMockMvc.perform(post("/api/water-changes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(waterChangeDTO)))
            .andExpect(status().isBadRequest());

        List<WaterChange> waterChangeList = waterChangeRepository.findAll();
        assertThat(waterChangeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllWaterChanges() throws Exception {
        // Initialize the database
        waterChangeRepository.saveAndFlush(waterChange);

        // Get all the waterChangeList
        restWaterChangeMockMvc.perform(get("/api/water-changes?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(waterChange.getId().intValue())))
            .andExpect(jsonPath("$.[*].changeDate").value(hasItem(sameInstant(DEFAULT_CHANGE_DATE))))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].readingBefore").value(hasItem(DEFAULT_READING_BEFORE.doubleValue())))
            .andExpect(jsonPath("$.[*].readingAfter").value(hasItem(DEFAULT_READING_AFTER.doubleValue())))
            .andExpect(jsonPath("$.[*].tempVal").value(hasItem(DEFAULT_TEMP_VAL.doubleValue())))
            .andExpect(jsonPath("$.[*].timestamp").value(hasItem(DEFAULT_TIMESTAMP)));
    }

    @Test
    @Transactional
    public void getWaterChange() throws Exception {
        // Initialize the database
        waterChangeRepository.saveAndFlush(waterChange);

        // Get the waterChange
        restWaterChangeMockMvc.perform(get("/api/water-changes/{id}", waterChange.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(waterChange.getId().intValue()))
            .andExpect(jsonPath("$.changeDate").value(sameInstant(DEFAULT_CHANGE_DATE)))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.readingBefore").value(DEFAULT_READING_BEFORE.doubleValue()))
            .andExpect(jsonPath("$.readingAfter").value(DEFAULT_READING_AFTER.doubleValue()))
            .andExpect(jsonPath("$.tempVal").value(DEFAULT_TEMP_VAL.doubleValue()))
            .andExpect(jsonPath("$.timestamp").value(DEFAULT_TIMESTAMP));
    }

    @Test
    @Transactional
    public void getNonExistingWaterChange() throws Exception {
        // Get the waterChange
        restWaterChangeMockMvc.perform(get("/api/water-changes/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateWaterChange() throws Exception {
        // Initialize the database
        waterChangeRepository.saveAndFlush(waterChange);
        waterChangeSearchRepository.save(waterChange);
        int databaseSizeBeforeUpdate = waterChangeRepository.findAll().size();

        // Update the waterChange
        WaterChange updatedWaterChange = waterChangeRepository.findOne(waterChange.getId());
        // Disconnect from session so that the updates on updatedWaterChange are not directly saved in db
        em.detach(updatedWaterChange);
        updatedWaterChange
            .changeDate(UPDATED_CHANGE_DATE)
            .description(UPDATED_DESCRIPTION)
            .readingBefore(UPDATED_READING_BEFORE)
            .readingAfter(UPDATED_READING_AFTER)
            .tempVal(UPDATED_TEMP_VAL)
            .timestamp(UPDATED_TIMESTAMP);
        WaterChangeDTO waterChangeDTO = waterChangeMapper.toDto(updatedWaterChange);

        restWaterChangeMockMvc.perform(put("/api/water-changes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(waterChangeDTO)))
            .andExpect(status().isOk());

        // Validate the WaterChange in the database
        List<WaterChange> waterChangeList = waterChangeRepository.findAll();
        assertThat(waterChangeList).hasSize(databaseSizeBeforeUpdate);
        WaterChange testWaterChange = waterChangeList.get(waterChangeList.size() - 1);
        assertThat(testWaterChange.getChangeDate()).isEqualTo(UPDATED_CHANGE_DATE);
        assertThat(testWaterChange.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testWaterChange.getReadingBefore()).isEqualTo(UPDATED_READING_BEFORE);
        assertThat(testWaterChange.getReadingAfter()).isEqualTo(UPDATED_READING_AFTER);
        assertThat(testWaterChange.getTempVal()).isEqualTo(UPDATED_TEMP_VAL);
        assertThat(testWaterChange.getTimestamp()).isEqualTo(UPDATED_TIMESTAMP);

        // Validate the WaterChange in Elasticsearch
        WaterChange waterChangeEs = waterChangeSearchRepository.findOne(testWaterChange.getId());
        assertThat(testWaterChange.getChangeDate()).isEqualTo(testWaterChange.getChangeDate());
        assertThat(waterChangeEs).isEqualToIgnoringGivenFields(testWaterChange, "changeDate");
    }

    @Test
    @Transactional
    public void updateNonExistingWaterChange() throws Exception {
        int databaseSizeBeforeUpdate = waterChangeRepository.findAll().size();

        // Create the WaterChange
        WaterChangeDTO waterChangeDTO = waterChangeMapper.toDto(waterChange);

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restWaterChangeMockMvc.perform(put("/api/water-changes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(waterChangeDTO)))
            .andExpect(status().isCreated());

        // Validate the WaterChange in the database
        List<WaterChange> waterChangeList = waterChangeRepository.findAll();
        assertThat(waterChangeList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteWaterChange() throws Exception {
        // Initialize the database
        waterChangeRepository.saveAndFlush(waterChange);
        waterChangeSearchRepository.save(waterChange);
        int databaseSizeBeforeDelete = waterChangeRepository.findAll().size();

        // Get the waterChange
        restWaterChangeMockMvc.perform(delete("/api/water-changes/{id}", waterChange.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate Elasticsearch is empty
        boolean waterChangeExistsInEs = waterChangeSearchRepository.exists(waterChange.getId());
        assertThat(waterChangeExistsInEs).isFalse();

        // Validate the database is empty
        List<WaterChange> waterChangeList = waterChangeRepository.findAll();
        assertThat(waterChangeList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchWaterChange() throws Exception {
        // Initialize the database
        waterChangeRepository.saveAndFlush(waterChange);
        waterChangeSearchRepository.save(waterChange);

        // Search the waterChange
        restWaterChangeMockMvc.perform(get("/api/_search/water-changes?query=id:" + waterChange.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(waterChange.getId().intValue())))
            .andExpect(jsonPath("$.[*].changeDate").value(hasItem(sameInstant(DEFAULT_CHANGE_DATE))))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].readingBefore").value(hasItem(DEFAULT_READING_BEFORE.doubleValue())))
            .andExpect(jsonPath("$.[*].readingAfter").value(hasItem(DEFAULT_READING_AFTER.doubleValue())))
            .andExpect(jsonPath("$.[*].tempVal").value(hasItem(DEFAULT_TEMP_VAL.doubleValue())))
            .andExpect(jsonPath("$.[*].timestamp").value(hasItem(DEFAULT_TIMESTAMP)));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(WaterChange.class);
        WaterChange waterChange1 = new WaterChange();
        waterChange1.setId(1L);
        WaterChange waterChange2 = new WaterChange();
        waterChange2.setId(waterChange1.getId());
        assertThat(waterChange1).isEqualTo(waterChange2);
        waterChange2.setId(2L);
        assertThat(waterChange1).isNotEqualTo(waterChange2);
        waterChange1.setId(null);
        assertThat(waterChange1).isNotEqualTo(waterChange2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(WaterChangeDTO.class);
        WaterChangeDTO waterChangeDTO1 = new WaterChangeDTO();
        waterChangeDTO1.setId(1L);
        WaterChangeDTO waterChangeDTO2 = new WaterChangeDTO();
        assertThat(waterChangeDTO1).isNotEqualTo(waterChangeDTO2);
        waterChangeDTO2.setId(waterChangeDTO1.getId());
        assertThat(waterChangeDTO1).isEqualTo(waterChangeDTO2);
        waterChangeDTO2.setId(2L);
        assertThat(waterChangeDTO1).isNotEqualTo(waterChangeDTO2);
        waterChangeDTO1.setId(null);
        assertThat(waterChangeDTO1).isNotEqualTo(waterChangeDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(waterChangeMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(waterChangeMapper.fromId(null)).isNull();
    }
}
