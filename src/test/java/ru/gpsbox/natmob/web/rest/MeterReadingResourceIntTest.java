package ru.gpsbox.natmob.web.rest;

import ru.gpsbox.natmob.PondApp;

import ru.gpsbox.natmob.domain.MeterReading;
import ru.gpsbox.natmob.repository.MeterReadingRepository;
import ru.gpsbox.natmob.repository.search.MeterReadingSearchRepository;
import ru.gpsbox.natmob.service.dto.MeterReadingDTO;
import ru.gpsbox.natmob.service.mapper.MeterReadingMapper;
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
 * Test class for the MeterReadingResource REST controller.
 *
 * @see MeterReadingResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = PondApp.class)
public class MeterReadingResourceIntTest {

    private static final ZonedDateTime DEFAULT_READING_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_READING_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Double DEFAULT_READING = 1D;
    private static final Double UPDATED_READING = 2D;

    private static final Double DEFAULT_TEMP_VAL = 1D;
    private static final Double UPDATED_TEMP_VAL = 2D;

    @Autowired
    private MeterReadingRepository meterReadingRepository;

    @Autowired
    private MeterReadingMapper meterReadingMapper;

    @Autowired
    private MeterReadingSearchRepository meterReadingSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restMeterReadingMockMvc;

    private MeterReading meterReading;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final MeterReadingResource meterReadingResource = new MeterReadingResource(meterReadingRepository, meterReadingMapper, meterReadingSearchRepository);
        this.restMeterReadingMockMvc = MockMvcBuilders.standaloneSetup(meterReadingResource)
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
    public static MeterReading createEntity(EntityManager em) {
        MeterReading meterReading = new MeterReading()
            .readingDate(DEFAULT_READING_DATE)
            .description(DEFAULT_DESCRIPTION)
            .reading(DEFAULT_READING)
            .tempVal(DEFAULT_TEMP_VAL);
        return meterReading;
    }

    @Before
    public void initTest() {
        meterReadingSearchRepository.deleteAll();
        meterReading = createEntity(em);
    }

    @Test
    @Transactional
    public void createMeterReading() throws Exception {
        int databaseSizeBeforeCreate = meterReadingRepository.findAll().size();

        // Create the MeterReading
        MeterReadingDTO meterReadingDTO = meterReadingMapper.toDto(meterReading);
        restMeterReadingMockMvc.perform(post("/api/meter-readings")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(meterReadingDTO)))
            .andExpect(status().isCreated());

        // Validate the MeterReading in the database
        List<MeterReading> meterReadingList = meterReadingRepository.findAll();
        assertThat(meterReadingList).hasSize(databaseSizeBeforeCreate + 1);
        MeterReading testMeterReading = meterReadingList.get(meterReadingList.size() - 1);
        assertThat(testMeterReading.getReadingDate()).isEqualTo(DEFAULT_READING_DATE);
        assertThat(testMeterReading.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testMeterReading.getReading()).isEqualTo(DEFAULT_READING);
        assertThat(testMeterReading.getTempVal()).isEqualTo(DEFAULT_TEMP_VAL);

        // Validate the MeterReading in Elasticsearch
        MeterReading meterReadingEs = meterReadingSearchRepository.findOne(testMeterReading.getId());
        assertThat(testMeterReading.getReadingDate()).isEqualTo(testMeterReading.getReadingDate());
        assertThat(meterReadingEs).isEqualToIgnoringGivenFields(testMeterReading, "readingDate");
    }

    @Test
    @Transactional
    public void createMeterReadingWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = meterReadingRepository.findAll().size();

        // Create the MeterReading with an existing ID
        meterReading.setId(1L);
        MeterReadingDTO meterReadingDTO = meterReadingMapper.toDto(meterReading);

        // An entity with an existing ID cannot be created, so this API call must fail
        restMeterReadingMockMvc.perform(post("/api/meter-readings")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(meterReadingDTO)))
            .andExpect(status().isBadRequest());

        // Validate the MeterReading in the database
        List<MeterReading> meterReadingList = meterReadingRepository.findAll();
        assertThat(meterReadingList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkReadingDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = meterReadingRepository.findAll().size();
        // set the field null
        meterReading.setReadingDate(null);

        // Create the MeterReading, which fails.
        MeterReadingDTO meterReadingDTO = meterReadingMapper.toDto(meterReading);

        restMeterReadingMockMvc.perform(post("/api/meter-readings")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(meterReadingDTO)))
            .andExpect(status().isBadRequest());

        List<MeterReading> meterReadingList = meterReadingRepository.findAll();
        assertThat(meterReadingList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkReadingIsRequired() throws Exception {
        int databaseSizeBeforeTest = meterReadingRepository.findAll().size();
        // set the field null
        meterReading.setReading(null);

        // Create the MeterReading, which fails.
        MeterReadingDTO meterReadingDTO = meterReadingMapper.toDto(meterReading);

        restMeterReadingMockMvc.perform(post("/api/meter-readings")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(meterReadingDTO)))
            .andExpect(status().isBadRequest());

        List<MeterReading> meterReadingList = meterReadingRepository.findAll();
        assertThat(meterReadingList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkTempValIsRequired() throws Exception {
        int databaseSizeBeforeTest = meterReadingRepository.findAll().size();
        // set the field null
        meterReading.setTempVal(null);

        // Create the MeterReading, which fails.
        MeterReadingDTO meterReadingDTO = meterReadingMapper.toDto(meterReading);

        restMeterReadingMockMvc.perform(post("/api/meter-readings")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(meterReadingDTO)))
            .andExpect(status().isBadRequest());

        List<MeterReading> meterReadingList = meterReadingRepository.findAll();
        assertThat(meterReadingList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllMeterReadings() throws Exception {
        // Initialize the database
        meterReadingRepository.saveAndFlush(meterReading);

        // Get all the meterReadingList
        restMeterReadingMockMvc.perform(get("/api/meter-readings?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(meterReading.getId().intValue())))
            .andExpect(jsonPath("$.[*].readingDate").value(hasItem(sameInstant(DEFAULT_READING_DATE))))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].reading").value(hasItem(DEFAULT_READING.doubleValue())))
            .andExpect(jsonPath("$.[*].tempVal").value(hasItem(DEFAULT_TEMP_VAL.doubleValue())));
    }

    @Test
    @Transactional
    public void getMeterReading() throws Exception {
        // Initialize the database
        meterReadingRepository.saveAndFlush(meterReading);

        // Get the meterReading
        restMeterReadingMockMvc.perform(get("/api/meter-readings/{id}", meterReading.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(meterReading.getId().intValue()))
            .andExpect(jsonPath("$.readingDate").value(sameInstant(DEFAULT_READING_DATE)))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.reading").value(DEFAULT_READING.doubleValue()))
            .andExpect(jsonPath("$.tempVal").value(DEFAULT_TEMP_VAL.doubleValue()));
    }

    @Test
    @Transactional
    public void getNonExistingMeterReading() throws Exception {
        // Get the meterReading
        restMeterReadingMockMvc.perform(get("/api/meter-readings/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateMeterReading() throws Exception {
        // Initialize the database
        meterReadingRepository.saveAndFlush(meterReading);
        meterReadingSearchRepository.save(meterReading);
        int databaseSizeBeforeUpdate = meterReadingRepository.findAll().size();

        // Update the meterReading
        MeterReading updatedMeterReading = meterReadingRepository.findOne(meterReading.getId());
        // Disconnect from session so that the updates on updatedMeterReading are not directly saved in db
        em.detach(updatedMeterReading);
        updatedMeterReading
            .readingDate(UPDATED_READING_DATE)
            .description(UPDATED_DESCRIPTION)
            .reading(UPDATED_READING)
            .tempVal(UPDATED_TEMP_VAL);
        MeterReadingDTO meterReadingDTO = meterReadingMapper.toDto(updatedMeterReading);

        restMeterReadingMockMvc.perform(put("/api/meter-readings")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(meterReadingDTO)))
            .andExpect(status().isOk());

        // Validate the MeterReading in the database
        List<MeterReading> meterReadingList = meterReadingRepository.findAll();
        assertThat(meterReadingList).hasSize(databaseSizeBeforeUpdate);
        MeterReading testMeterReading = meterReadingList.get(meterReadingList.size() - 1);
        assertThat(testMeterReading.getReadingDate()).isEqualTo(UPDATED_READING_DATE);
        assertThat(testMeterReading.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testMeterReading.getReading()).isEqualTo(UPDATED_READING);
        assertThat(testMeterReading.getTempVal()).isEqualTo(UPDATED_TEMP_VAL);

        // Validate the MeterReading in Elasticsearch
        MeterReading meterReadingEs = meterReadingSearchRepository.findOne(testMeterReading.getId());
        assertThat(testMeterReading.getReadingDate()).isEqualTo(testMeterReading.getReadingDate());
        assertThat(meterReadingEs).isEqualToIgnoringGivenFields(testMeterReading, "readingDate");
    }

    @Test
    @Transactional
    public void updateNonExistingMeterReading() throws Exception {
        int databaseSizeBeforeUpdate = meterReadingRepository.findAll().size();

        // Create the MeterReading
        MeterReadingDTO meterReadingDTO = meterReadingMapper.toDto(meterReading);

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restMeterReadingMockMvc.perform(put("/api/meter-readings")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(meterReadingDTO)))
            .andExpect(status().isCreated());

        // Validate the MeterReading in the database
        List<MeterReading> meterReadingList = meterReadingRepository.findAll();
        assertThat(meterReadingList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteMeterReading() throws Exception {
        // Initialize the database
        meterReadingRepository.saveAndFlush(meterReading);
        meterReadingSearchRepository.save(meterReading);
        int databaseSizeBeforeDelete = meterReadingRepository.findAll().size();

        // Get the meterReading
        restMeterReadingMockMvc.perform(delete("/api/meter-readings/{id}", meterReading.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate Elasticsearch is empty
        boolean meterReadingExistsInEs = meterReadingSearchRepository.exists(meterReading.getId());
        assertThat(meterReadingExistsInEs).isFalse();

        // Validate the database is empty
        List<MeterReading> meterReadingList = meterReadingRepository.findAll();
        assertThat(meterReadingList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchMeterReading() throws Exception {
        // Initialize the database
        meterReadingRepository.saveAndFlush(meterReading);
        meterReadingSearchRepository.save(meterReading);

        // Search the meterReading
        restMeterReadingMockMvc.perform(get("/api/_search/meter-readings?query=id:" + meterReading.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(meterReading.getId().intValue())))
            .andExpect(jsonPath("$.[*].readingDate").value(hasItem(sameInstant(DEFAULT_READING_DATE))))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].reading").value(hasItem(DEFAULT_READING.doubleValue())))
            .andExpect(jsonPath("$.[*].tempVal").value(hasItem(DEFAULT_TEMP_VAL.doubleValue())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(MeterReading.class);
        MeterReading meterReading1 = new MeterReading();
        meterReading1.setId(1L);
        MeterReading meterReading2 = new MeterReading();
        meterReading2.setId(meterReading1.getId());
        assertThat(meterReading1).isEqualTo(meterReading2);
        meterReading2.setId(2L);
        assertThat(meterReading1).isNotEqualTo(meterReading2);
        meterReading1.setId(null);
        assertThat(meterReading1).isNotEqualTo(meterReading2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(MeterReadingDTO.class);
        MeterReadingDTO meterReadingDTO1 = new MeterReadingDTO();
        meterReadingDTO1.setId(1L);
        MeterReadingDTO meterReadingDTO2 = new MeterReadingDTO();
        assertThat(meterReadingDTO1).isNotEqualTo(meterReadingDTO2);
        meterReadingDTO2.setId(meterReadingDTO1.getId());
        assertThat(meterReadingDTO1).isEqualTo(meterReadingDTO2);
        meterReadingDTO2.setId(2L);
        assertThat(meterReadingDTO1).isNotEqualTo(meterReadingDTO2);
        meterReadingDTO1.setId(null);
        assertThat(meterReadingDTO1).isNotEqualTo(meterReadingDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(meterReadingMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(meterReadingMapper.fromId(null)).isNull();
    }
}
