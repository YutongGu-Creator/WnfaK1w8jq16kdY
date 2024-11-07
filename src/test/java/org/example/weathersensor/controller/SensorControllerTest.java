package org.example.weathersensor.controller;

import org.example.weathersensor.data.SensorData;
import org.example.weathersensor.service.SensorDataService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SensorControllerTest {

    @Mock
    private SensorDataService sensorDataService;

    @InjectMocks
    private SensorController sensorController;

    AutoCloseable openMocks;

    @BeforeEach
    void setUp() {
        openMocks = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        openMocks.close();
    }

    @Test
    void shouldSuccessfullyAddData() {
        var sensorData = new SensorData();
        when(sensorDataService.saveSensorData(sensorData)).thenReturn(sensorData);

        var response = sensorController.addSensorData(sensorData);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(sensorData, response.getBody());
        verify(sensorDataService, times(1)).saveSensorData(sensorData);
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldSuccessfullyGetData() {
        var sensorId = "sensor1";
        var metrics = Arrays.asList("temperature", "humidity", "windspeed");
        var statistic = "average";
        var startDate = new Date();
        var endDate = new Date();

        var expectedResult = new HashMap<>();
        expectedResult.put("temperature", 10.7);

        when(sensorDataService.getMetrics(eq(sensorId), eq(metrics), eq(statistic),
                any(Optional.class), any(Optional.class)))
                .thenReturn(expectedResult);

        var response = sensorController.getMetrics(sensorId, metrics, statistic, startDate, endDate);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResult, response.getBody());
        verify(sensorDataService, times(1)).getMetrics(eq(sensorId), eq(metrics),
                eq(statistic), any(Optional.class), any(Optional.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldSuccessfullyGetDataWithoutDates() {
        var sensorId = "sensor1";
        var metrics = Collections.singletonList("temperature");
        var statistic = "average";

        var expectedResult = new HashMap<>();
        expectedResult.put("temperature", 10.7);

        when(sensorDataService.getMetrics(eq(sensorId), eq(metrics), eq(statistic),
                any(Optional.class), any(Optional.class)))
                .thenReturn(expectedResult);

        var response = sensorController.getMetrics(
                sensorId, metrics, statistic, null, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResult, response.getBody());
        verify(sensorDataService, times(1)).getMetrics(eq(sensorId), eq(metrics),
                eq(statistic), any(Optional.class), any(Optional.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldSuccessfullyGetDataWithoutEndDate() {
        var sensorId = "sensor1";
        var metrics = Collections.singletonList("temperature");
        var statistic = "average";
        var startDate = new Date(new Date().getTime() - 48 * 60 * 60 * 1000);

        var expectedResult = new HashMap<>();
        expectedResult.put("temperature", 10.7);

        when(sensorDataService.getMetrics(eq(sensorId), eq(metrics), eq(statistic),
                any(Optional.class), any(Optional.class)))
                .thenReturn(expectedResult);

        var response = sensorController.getMetrics(
                sensorId, metrics, statistic, startDate, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResult, response.getBody());
        verify(sensorDataService, times(1)).getMetrics(eq(sensorId), eq(metrics),
                eq(statistic), any(Optional.class), any(Optional.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldThrowExceptionWithoutStartDate() {
        var sensorId = "sensor1";
        var metrics = Collections.singletonList("temperature");
        var statistic = "average";
        var endDate = new Date();

        var expectedResult = new HashMap<>();
        expectedResult.put("temperature", 10.7);

        when(sensorDataService.getMetrics(eq(sensorId), eq(metrics), eq(statistic),
                any(Optional.class), any(Optional.class)))
                .thenReturn(expectedResult);

        assertThrows(IllegalArgumentException.class, () -> sensorController.getMetrics(
                sensorId, metrics, statistic, null, endDate));
    }


    @Test
    @SuppressWarnings("unchecked")
    void shouldThrowIllegalArgumentException() {
        var sensorId = "sensor1";
        var metrics = Collections.singletonList("invalidMetric");
        var statistic = "average";
        var startDate = new Date();
        var endDate = new Date();

        var errorMessage = "Invalid metric: invalidMetric";
        when(sensorDataService.getMetrics(eq(sensorId), eq(metrics), eq(statistic),
                any(Optional.class), any(Optional.class)))
                .thenThrow(new IllegalArgumentException(errorMessage));

        var response = sensorController.getMetrics(
                sensorId, metrics, statistic, startDate, endDate);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsKey(errorMessage));
        assertEquals(Double.NaN, response.getBody().get(errorMessage));
    }
}
