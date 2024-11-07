package org.example.weathersensor.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface SensorDataRepository extends JpaRepository<SensorData, Long> {

    @Query("SELECT s FROM SensorData s WHERE s.sensorId = :sensorId " +
            "AND s.timestamp BETWEEN :startDate AND :endDate")
    List<SensorData> findBySensorIdAndTimestampBetween(
            String sensorId, Date startDate, Date endDate);
}
