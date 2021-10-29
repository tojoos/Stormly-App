package application.stormlyapp.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "records")
public class Record {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @PastOrPresent
    @Column(name = "date")
    private LocalDateTime date;

    @Min(-50)
    @Max(100)
    @Column(name = "temperature")
    private double temperature;

    @DecimalMax("1.0")
    @DecimalMin("0.0")
    @Column(name = "humidity")
    private double humidity;

    @DecimalMax("1100")
    @DecimalMin("800")
    @Column(name = "pressure")
    private double pressure;

    public String getFormattedDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        return date.format(formatter);
    }
}
