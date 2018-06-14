package ru.gpsbox.natmob.web.rest;

import ru.gpsbox.natmob.PondApp;

import ru.gpsbox.natmob.domain.LiveStock;
import ru.gpsbox.natmob.repository.LiveStockRepository;
import ru.gpsbox.natmob.repository.search.LiveStockSearchRepository;
import ru.gpsbox.natmob.service.dto.LiveStockDTO;
import ru.gpsbox.natmob.service.mapper.LiveStockMapper;
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

import ru.gpsbox.natmob.domain.enumeration.StockCase;
/**
 * Test class for the LiveStockResource REST controller.
 *
 * @see LiveStockResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = PondApp.class)
public class LiveStockResourceIntTest {

    private static final ZonedDateTime DEFAULT_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final StockCase DEFAULT_REASON = StockCase.ADDED;
    private static final StockCase UPDATED_REASON = StockCase.REMOVED;

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Integer DEFAULT_QTY = 1;
    private static final Integer UPDATED_QTY = 2;

    private static final Double DEFAULT_TEMP_VAL = 1D;
    private static final Double UPDATED_TEMP_VAL = 2D;

    private static final Integer DEFAULT_TIMESTAMP = 1;
    private static final Integer UPDATED_TIMESTAMP = 2;

    @Autowired
    private LiveStockRepository liveStockRepository;

    @Autowired
    private LiveStockMapper liveStockMapper;

    @Autowired
    private LiveStockSearchRepository liveStockSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restLiveStockMockMvc;

    private LiveStock liveStock;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final LiveStockResource liveStockResource = new LiveStockResource(liveStockRepository, liveStockMapper, liveStockSearchRepository);
        this.restLiveStockMockMvc = MockMvcBuilders.standaloneSetup(liveStockResource)
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
    public static LiveStock createEntity(EntityManager em) {
        LiveStock liveStock = new LiveStock()
            .date(DEFAULT_DATE)
            .reason(DEFAULT_REASON)
            .description(DEFAULT_DESCRIPTION)
            .qty(DEFAULT_QTY)
            .tempVal(DEFAULT_TEMP_VAL)
            .timestamp(DEFAULT_TIMESTAMP);
        return liveStock;
    }

    @Before
    public void initTest() {
        liveStockSearchRepository.deleteAll();
        liveStock = createEntity(em);
    }

    @Test
    @Transactional
    public void createLiveStock() throws Exception {
        int databaseSizeBeforeCreate = liveStockRepository.findAll().size();

        // Create the LiveStock
        LiveStockDTO liveStockDTO = liveStockMapper.toDto(liveStock);
        restLiveStockMockMvc.perform(post("/api/live-stocks")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(liveStockDTO)))
            .andExpect(status().isCreated());

        // Validate the LiveStock in the database
        List<LiveStock> liveStockList = liveStockRepository.findAll();
        assertThat(liveStockList).hasSize(databaseSizeBeforeCreate + 1);
        LiveStock testLiveStock = liveStockList.get(liveStockList.size() - 1);
        assertThat(testLiveStock.getDate()).isEqualTo(DEFAULT_DATE);
        assertThat(testLiveStock.getReason()).isEqualTo(DEFAULT_REASON);
        assertThat(testLiveStock.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testLiveStock.getQty()).isEqualTo(DEFAULT_QTY);
        assertThat(testLiveStock.getTempVal()).isEqualTo(DEFAULT_TEMP_VAL);
        assertThat(testLiveStock.getTimestamp()).isEqualTo(DEFAULT_TIMESTAMP);

        // Validate the LiveStock in Elasticsearch
        LiveStock liveStockEs = liveStockSearchRepository.findOne(testLiveStock.getId());
        assertThat(testLiveStock.getDate()).isEqualTo(testLiveStock.getDate());
        assertThat(liveStockEs).isEqualToIgnoringGivenFields(testLiveStock, "date");
    }

    @Test
    @Transactional
    public void createLiveStockWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = liveStockRepository.findAll().size();

        // Create the LiveStock with an existing ID
        liveStock.setId(1L);
        LiveStockDTO liveStockDTO = liveStockMapper.toDto(liveStock);

        // An entity with an existing ID cannot be created, so this API call must fail
        restLiveStockMockMvc.perform(post("/api/live-stocks")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(liveStockDTO)))
            .andExpect(status().isBadRequest());

        // Validate the LiveStock in the database
        List<LiveStock> liveStockList = liveStockRepository.findAll();
        assertThat(liveStockList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = liveStockRepository.findAll().size();
        // set the field null
        liveStock.setDate(null);

        // Create the LiveStock, which fails.
        LiveStockDTO liveStockDTO = liveStockMapper.toDto(liveStock);

        restLiveStockMockMvc.perform(post("/api/live-stocks")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(liveStockDTO)))
            .andExpect(status().isBadRequest());

        List<LiveStock> liveStockList = liveStockRepository.findAll();
        assertThat(liveStockList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkQtyIsRequired() throws Exception {
        int databaseSizeBeforeTest = liveStockRepository.findAll().size();
        // set the field null
        liveStock.setQty(null);

        // Create the LiveStock, which fails.
        LiveStockDTO liveStockDTO = liveStockMapper.toDto(liveStock);

        restLiveStockMockMvc.perform(post("/api/live-stocks")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(liveStockDTO)))
            .andExpect(status().isBadRequest());

        List<LiveStock> liveStockList = liveStockRepository.findAll();
        assertThat(liveStockList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkTempValIsRequired() throws Exception {
        int databaseSizeBeforeTest = liveStockRepository.findAll().size();
        // set the field null
        liveStock.setTempVal(null);

        // Create the LiveStock, which fails.
        LiveStockDTO liveStockDTO = liveStockMapper.toDto(liveStock);

        restLiveStockMockMvc.perform(post("/api/live-stocks")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(liveStockDTO)))
            .andExpect(status().isBadRequest());

        List<LiveStock> liveStockList = liveStockRepository.findAll();
        assertThat(liveStockList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllLiveStocks() throws Exception {
        // Initialize the database
        liveStockRepository.saveAndFlush(liveStock);

        // Get all the liveStockList
        restLiveStockMockMvc.perform(get("/api/live-stocks?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(liveStock.getId().intValue())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(sameInstant(DEFAULT_DATE))))
            .andExpect(jsonPath("$.[*].reason").value(hasItem(DEFAULT_REASON.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].qty").value(hasItem(DEFAULT_QTY)))
            .andExpect(jsonPath("$.[*].tempVal").value(hasItem(DEFAULT_TEMP_VAL.doubleValue())))
            .andExpect(jsonPath("$.[*].timestamp").value(hasItem(DEFAULT_TIMESTAMP)));
    }

    @Test
    @Transactional
    public void getLiveStock() throws Exception {
        // Initialize the database
        liveStockRepository.saveAndFlush(liveStock);

        // Get the liveStock
        restLiveStockMockMvc.perform(get("/api/live-stocks/{id}", liveStock.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(liveStock.getId().intValue()))
            .andExpect(jsonPath("$.date").value(sameInstant(DEFAULT_DATE)))
            .andExpect(jsonPath("$.reason").value(DEFAULT_REASON.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.qty").value(DEFAULT_QTY))
            .andExpect(jsonPath("$.tempVal").value(DEFAULT_TEMP_VAL.doubleValue()))
            .andExpect(jsonPath("$.timestamp").value(DEFAULT_TIMESTAMP));
    }

    @Test
    @Transactional
    public void getNonExistingLiveStock() throws Exception {
        // Get the liveStock
        restLiveStockMockMvc.perform(get("/api/live-stocks/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateLiveStock() throws Exception {
        // Initialize the database
        liveStockRepository.saveAndFlush(liveStock);
        liveStockSearchRepository.save(liveStock);
        int databaseSizeBeforeUpdate = liveStockRepository.findAll().size();

        // Update the liveStock
        LiveStock updatedLiveStock = liveStockRepository.findOne(liveStock.getId());
        // Disconnect from session so that the updates on updatedLiveStock are not directly saved in db
        em.detach(updatedLiveStock);
        updatedLiveStock
            .date(UPDATED_DATE)
            .reason(UPDATED_REASON)
            .description(UPDATED_DESCRIPTION)
            .qty(UPDATED_QTY)
            .tempVal(UPDATED_TEMP_VAL)
            .timestamp(UPDATED_TIMESTAMP);
        LiveStockDTO liveStockDTO = liveStockMapper.toDto(updatedLiveStock);

        restLiveStockMockMvc.perform(put("/api/live-stocks")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(liveStockDTO)))
            .andExpect(status().isOk());

        // Validate the LiveStock in the database
        List<LiveStock> liveStockList = liveStockRepository.findAll();
        assertThat(liveStockList).hasSize(databaseSizeBeforeUpdate);
        LiveStock testLiveStock = liveStockList.get(liveStockList.size() - 1);
        assertThat(testLiveStock.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testLiveStock.getReason()).isEqualTo(UPDATED_REASON);
        assertThat(testLiveStock.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testLiveStock.getQty()).isEqualTo(UPDATED_QTY);
        assertThat(testLiveStock.getTempVal()).isEqualTo(UPDATED_TEMP_VAL);
        assertThat(testLiveStock.getTimestamp()).isEqualTo(UPDATED_TIMESTAMP);

        // Validate the LiveStock in Elasticsearch
        LiveStock liveStockEs = liveStockSearchRepository.findOne(testLiveStock.getId());
        assertThat(testLiveStock.getDate()).isEqualTo(testLiveStock.getDate());
        assertThat(liveStockEs).isEqualToIgnoringGivenFields(testLiveStock, "date");
    }

    @Test
    @Transactional
    public void updateNonExistingLiveStock() throws Exception {
        int databaseSizeBeforeUpdate = liveStockRepository.findAll().size();

        // Create the LiveStock
        LiveStockDTO liveStockDTO = liveStockMapper.toDto(liveStock);

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restLiveStockMockMvc.perform(put("/api/live-stocks")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(liveStockDTO)))
            .andExpect(status().isCreated());

        // Validate the LiveStock in the database
        List<LiveStock> liveStockList = liveStockRepository.findAll();
        assertThat(liveStockList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteLiveStock() throws Exception {
        // Initialize the database
        liveStockRepository.saveAndFlush(liveStock);
        liveStockSearchRepository.save(liveStock);
        int databaseSizeBeforeDelete = liveStockRepository.findAll().size();

        // Get the liveStock
        restLiveStockMockMvc.perform(delete("/api/live-stocks/{id}", liveStock.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate Elasticsearch is empty
        boolean liveStockExistsInEs = liveStockSearchRepository.exists(liveStock.getId());
        assertThat(liveStockExistsInEs).isFalse();

        // Validate the database is empty
        List<LiveStock> liveStockList = liveStockRepository.findAll();
        assertThat(liveStockList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchLiveStock() throws Exception {
        // Initialize the database
        liveStockRepository.saveAndFlush(liveStock);
        liveStockSearchRepository.save(liveStock);

        // Search the liveStock
        restLiveStockMockMvc.perform(get("/api/_search/live-stocks?query=id:" + liveStock.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(liveStock.getId().intValue())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(sameInstant(DEFAULT_DATE))))
            .andExpect(jsonPath("$.[*].reason").value(hasItem(DEFAULT_REASON.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].qty").value(hasItem(DEFAULT_QTY)))
            .andExpect(jsonPath("$.[*].tempVal").value(hasItem(DEFAULT_TEMP_VAL.doubleValue())))
            .andExpect(jsonPath("$.[*].timestamp").value(hasItem(DEFAULT_TIMESTAMP)));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(LiveStock.class);
        LiveStock liveStock1 = new LiveStock();
        liveStock1.setId(1L);
        LiveStock liveStock2 = new LiveStock();
        liveStock2.setId(liveStock1.getId());
        assertThat(liveStock1).isEqualTo(liveStock2);
        liveStock2.setId(2L);
        assertThat(liveStock1).isNotEqualTo(liveStock2);
        liveStock1.setId(null);
        assertThat(liveStock1).isNotEqualTo(liveStock2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(LiveStockDTO.class);
        LiveStockDTO liveStockDTO1 = new LiveStockDTO();
        liveStockDTO1.setId(1L);
        LiveStockDTO liveStockDTO2 = new LiveStockDTO();
        assertThat(liveStockDTO1).isNotEqualTo(liveStockDTO2);
        liveStockDTO2.setId(liveStockDTO1.getId());
        assertThat(liveStockDTO1).isEqualTo(liveStockDTO2);
        liveStockDTO2.setId(2L);
        assertThat(liveStockDTO1).isNotEqualTo(liveStockDTO2);
        liveStockDTO1.setId(null);
        assertThat(liveStockDTO1).isNotEqualTo(liveStockDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(liveStockMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(liveStockMapper.fromId(null)).isNull();
    }
}
