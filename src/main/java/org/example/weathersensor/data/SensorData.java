package org.example.weathersensor.data;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Data
public class SensorData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Incremental id generation
    private Long id;
    private String sensorId;
    private Double temperature;
    private Double humidity;
    private Double windSpeed;
    private Date timestamp;
}
